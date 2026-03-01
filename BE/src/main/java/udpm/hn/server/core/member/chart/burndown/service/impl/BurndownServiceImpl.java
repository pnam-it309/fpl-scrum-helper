package udpm.hn.server.core.member.chart.burndown.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.chart.burndown.model.request.BurndownDailyRequest;
import udpm.hn.server.core.member.chart.burndown.model.request.BurndownPoint;
import udpm.hn.server.core.member.chart.burndown.model.request.BurndownRequest;
import udpm.hn.server.core.member.chart.burndown.repository.PhaseBurndownRepository;
import udpm.hn.server.core.member.chart.burndown.repository.TodoCountRepository;
import udpm.hn.server.core.member.chart.burndown.service.BurndownService;
import udpm.hn.server.entity.PhaseProject;
import udpm.hn.server.entity.Project;
import udpm.hn.server.infrastructure.constant.StatusTodo;
import udpm.hn.server.repository.PhaseRepository;
import udpm.hn.server.repository.ProjectRepository;
import udpm.hn.server.repository.ToDoRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Validated
@RequiredArgsConstructor
public class BurndownServiceImpl implements BurndownService {

    private final PhaseBurndownRepository phaseBurndownRepository;

    private final TodoCountRepository todoCountRepository;

    private final ToDoRepository toDoRepository;

    private final ProjectRepository projectRepository;

    @Override
    public ResponseObject<?> burnDownChart( String idProject, String idPhase) {
        Long durationDays = phaseBurndownRepository.calculatePhaseDurationDays(idPhase, idProject);
        if (durationDays == null || durationDays == 0) {
            return ResponseObject.errorForward("Thời gian phase không hợp lệ hoặc bằng 0.", HttpStatus.BAD_REQUEST);
        }

        PhaseProject phase = phaseBurndownRepository
                .findByIdAndProjectId(idPhase, idProject)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phase hoặc project."));


        Long totalTasks = todoCountRepository.countTodosByPhaseProject(idPhase);

        List<StatusTodo> completedStatuses = List.of(StatusTodo.DA_HOAN_THANH, StatusTodo.HOAN_THANH_SOM,StatusTodo.QUA_HAN);
        Long totalCompleted = todoCountRepository.countCompletedTasksByPhaseAndProject(idProject, idPhase, completedStatuses);

        List<StatusTodo> remainingStatuses = List.of(StatusTodo.CHUA_HOAN_THANH);
        Long remainingTasks = todoCountRepository.countRemainingTasksByPhaseAndProject(idProject, idPhase, remainingStatuses);

        Double estimatedPerDay = BigDecimal.valueOf((double) totalTasks / durationDays)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();

        List<Long> completedPerDay = todoCountRepository.countCompletedTasksPerDay(idProject, idPhase, completedStatuses);

        List<BurndownDailyRequest> byDate = new ArrayList<>();
        LocalDate startDate = Instant.ofEpochMilli(phase.getStartTime())
                .atZone(ZoneId.systemDefault()).toLocalDate();

        for (int i = 0; i < durationDays; i++) {
            String currentDate = startDate.plusDays(i).toString();
            Long realityCount = (i < completedPerDay.size()) ? completedPerDay.get(i) : 0L;

            BurndownDailyRequest daily = new BurndownDailyRequest(
                    currentDate,
                    estimatedPerDay,
                    realityCount,
                    totalCompleted,
                    remainingTasks
            );
            byDate.add(daily);
        }

        BurndownRequest response = new BurndownRequest();
        response.setEstimatedNumberOfTasks(estimatedPerDay);
        response.setReality(completedPerDay);
        response.setTotalActual(totalCompleted);
        response.setRemaining(remainingTasks);
        response.setByDate(byDate);

        return new ResponseObject<>(response, HttpStatus.OK, "OK");
    }

    @Override
    public ResponseObject<?> getBurndownDataStoryPoint(String projectId, String phaseId) {
        PhaseProject phase = phaseBurndownRepository.findById(String.valueOf(UUID.fromString(phaseId)))
                .orElseThrow(() -> new RuntimeException("Phase not found"));

        LocalDate start = Instant.ofEpochMilli(phase.getStartTime())
                .atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end = Instant.ofEpochMilli(phase.getEndTime())
                .atZone(ZoneId.systemDefault()).toLocalDate();
        int days = (int) ChronoUnit.DAYS.between(start, end);

        int totalPoint = Optional.ofNullable(
                toDoRepository.getTotalStoryPointByPhaseAndProject(projectId, phaseId)
        ).orElse(0);
        int pointPerDay = days > 0 ? totalPoint / days : totalPoint;

        // Lấy status hợp lệ
        List<StatusTodo> validStatuses = Arrays.asList(
                StatusTodo.DA_HOAN_THANH,
                StatusTodo.HOAN_THANH_SOM,
                StatusTodo.QUA_HAN
        );

        Map<LocalDate, Integer> donePerDay = new HashMap<>();
        List<Object[]> results = toDoRepository.getDonePointsByDateAndProject(
                projectId,
                phaseId,
                phase.getStartTime(),
                phase.getEndTime(),
                validStatuses
        );

        for (Object[] row : results) {
            LocalDate date = LocalDate.parse(row[0].toString());
            int done = ((Number) row[1]).intValue();
            donePerDay.put(date, done);
        }

        List<BurndownPoint> chartData = new ArrayList<>();
        int doneSoFar = 0;
        Integer lastActual = null;
        boolean hasStartedActual = false;

        LocalDate today = LocalDate.now();

        for (int i = 0; i <= days; i++) {
            LocalDate current = start.plusDays(i);
            int doneToday = donePerDay.getOrDefault(current, 0);

            if (!current.isAfter(today)) {
                if (doneToday > 0 || hasStartedActual) {
                    doneSoFar += doneToday;
                    lastActual = Math.max(totalPoint - doneSoFar, 0);
                    hasStartedActual = true;
                }
            }

            int expected = Math.max(totalPoint - pointPerDay * i, 0);

            chartData.add(new BurndownPoint(
                    current.toString(),
                    expected,
                    !current.isAfter(today) ? lastActual : null
            ));
        }


        return new ResponseObject<>(chartData, HttpStatus.OK, "OK");
    }

    @Override
    public ResponseObject<?> getBurndownDataStoryPointByProject(String projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        LocalDate start = Instant.ofEpochMilli(project.getStartTime())
                .atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end = Instant.ofEpochMilli(project.getEndTime())
                .atZone(ZoneId.systemDefault()).toLocalDate();

        // +1 để bao gồm cả ngày bắt đầu và kết thúc
        int days = (int) ChronoUnit.DAYS.between(start, end) + 1;

        int totalPoint = Optional.ofNullable(
                toDoRepository.getTotalStoryPointByProject(projectId)
        ).orElse(0);

        // Mỗi ngày giảm đều, ngày cuối = 0
        double pointPerDay = days > 1 ? (double) totalPoint / (days - 1) : totalPoint;

        // Status hợp lệ để tính actual
        List<StatusTodo> validStatuses = Arrays.asList(
                StatusTodo.DA_HOAN_THANH,
                StatusTodo.HOAN_THANH_SOM,
                StatusTodo.QUA_HAN
        );

        // Lấy dữ liệu điểm done theo ngày
        Map<LocalDate, Integer> donePerDay = new HashMap<>();
        List<Object[]> results = toDoRepository.getDonePointsByDateAndProjectOnly(
                projectId,
                project.getStartTime(),
                project.getEndTime(),
                validStatuses
        );

        for (Object[] row : results) {
            LocalDate date = LocalDate.parse(row[0].toString());
            int done = ((Number) row[1]).intValue();
            donePerDay.put(date, done);
        }

        List<BurndownPoint> chartData = new ArrayList<>();
        int doneSoFar = 0;
        LocalDate today = LocalDate.now();

        for (int i = 0; i < days; i++) {
            LocalDate current = start.plusDays(i);

            // Expected giảm đều
            int expected = (int) Math.max(totalPoint - Math.round(pointPerDay * i), 0);

            // Actual: chỉ set giá trị nếu hôm đó có done
            Integer actual = null;
            if (!current.isAfter(today)) {
                int doneToday = donePerDay.getOrDefault(current, 0);
                if (doneToday > 0) {
                    doneSoFar += doneToday;
                    actual = Math.max(totalPoint - doneSoFar, 0);
                }
            }

            chartData.add(new BurndownPoint(
                    current.toString(),
                    expected,
                    actual
            ));
        }

        return new ResponseObject<>(chartData, HttpStatus.OK, "OK");
    }


}
