package udpm.hn.server.infrastructure.job.staff.commonio;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import udpm.hn.server.entity.Staff;
import udpm.hn.server.entity.StaffMajorFacility;
import udpm.hn.server.infrastructure.job.staff.model.dto.TransferStaffRole;
import udpm.hn.server.infrastructure.job.staff.model.request.StaffExcelRequest;
import udpm.hn.server.infrastructure.job.staff.repository.ConfigStaffCustomRepository;
import udpm.hn.server.repository.StaffMajorFacilityRepository;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@Transactional
public class StaffTasklet implements Tasklet {

    private final ConfigStaffCustomRepository staffRepository;
    private final StaffMajorFacilityRepository staffMajorFacilityRepository;
    private final ItemReader<StaffExcelRequest> reader;
    private final ItemProcessor<StaffExcelRequest, TransferStaffRole> processor;

    public StaffTasklet(ConfigStaffCustomRepository staffRepository,
                        StaffMajorFacilityRepository staffMajorFacilityRepository,
                        ItemReader<StaffExcelRequest> reader,
                        @Qualifier("staffExcelProcessor") ItemProcessor<StaffExcelRequest, TransferStaffRole> processor) {
        this.staffRepository = staffRepository;
        this.staffMajorFacilityRepository = staffMajorFacilityRepository;
        this.reader = reader;
        this.processor = processor;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("BẮT ĐẦU STAFF TASKLET");

        List<TransferStaffRole> validItems = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();
        StaffExcelRequest readItem;
        int readCount = 0;

        // Đọc và xử lý tất cả bản ghi
        while ((readItem = reader.read()) != null) {
            readCount++;
            log.info("ĐỌC BẢN GHI: {}", readItem);
            try {
                TransferStaffRole item = processor.process(readItem);
                if (item != null) {
                    validItems.add(item);
                } else {
                    // Lỗi đã được ghi trong processor, không cần thêm lỗi ở đây
                    errorMessages.add("Bản ghi có lỗi: " + readItem.toString());
                }
            } catch (Exception e) {
                log.error("LỖI XỬ LÝ BẢN GHI: {}", readItem, e);
                errorMessages.add("Lỗi xử lý bản ghi: " + readItem + " - " + e.getMessage());
            }
        }
        log.info("TỔNG SỐ BẢN GHI ĐỌC: {}", readCount);

        // Kiểm tra xem có lỗi nào không
        if (!errorMessages.isEmpty()) {
            log.warn("CÓ LỖI TRONG QUÁ TRÌNH XỬ LÝ, KHÔNG LƯU DỮ LIỆU. SỐ LỖI: {}", errorMessages.size());
            for (String error : errorMessages) {
                log.warn("LỖI: {}", error);
            }
            return RepeatStatus.FINISHED; // Kết thúc mà không lưu gì
        }

        // Nếu không có lỗi, lưu tất cả bản ghi
        if (!validItems.isEmpty()) {
            List<Staff> staffList = validItems.stream()
                    .map(TransferStaffRole::getStaff)
                    .toList();
            List<Staff> savedStaffList = staffRepository.saveAll(staffList);

            for (TransferStaffRole transfer : validItems) {
                Staff savedStaff = savedStaffList.stream()
                        .filter(s -> s.getCode().equals(transfer.getStaff().getCode()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Staff không tồn tại sau khi lưu"));
                StaffMajorFacility smf = transfer.getStaffMajorFacility();
                smf.setStaff(savedStaff);
                staffMajorFacilityRepository.save(smf);
            }
            log.info("Đã lưu {} Staff và StaffMajorFacility thành công.", validItems.size());
        } else {
            log.warn("KHÔNG CÓ BẢN GHI HỢP LỆ NÀO ĐƯỢC LƯU");
        }

        return RepeatStatus.FINISHED;
    }
}
