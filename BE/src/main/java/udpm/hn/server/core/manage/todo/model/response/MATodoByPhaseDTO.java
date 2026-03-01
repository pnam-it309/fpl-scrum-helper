package udpm.hn.server.core.manage.todo.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MATodoByPhaseDTO {


    private String id;
    private String name;
    private String code;
    private String date;
    private String priorityLevel;
    private String progress;
    private String idStaffProject;
    private String idStaff;
    private String nameStaff;
    private String imageStaff;
    private String idStudent;
    private String deadline;
    private String type;
    private String nameStudent;
    private String imageStudent;
    private String statusTodo;
    private String point;

    private List<ParticipantDTO> participants = new ArrayList<>();

    public MATodoByPhaseDTO(MATodoResponse response) {
        this.id = response.getId();
        this.name = response.getName();
        this.code = response.getCode();
        this.deadline = response.getDeadline();
        this.idStaff = response.getIdStaff();
        this.idStaffProject = response.getIdStaffProject();
        this.idStudent = response.getIdStudent();
        this.imageStaff = response.getImageStaff();
        this.imageStudent = response.getImageStudent();
        this.nameStaff = response.getNameStaff();
        this.nameStudent =response.getNameStudent();
        this.date = response.getDate();
        this.statusTodo = response.getStatusTodo();
        this.priorityLevel = response.getPriorityLevel();
        this.progress = response.getProgress();
        this.type = response.getType();
        this.point = response.getPoint();
    }

    public void addParticipant(String name, String image) {
        if (name != null) {
            participants.add(new ParticipantDTO(name, image));
        }
    }



}
