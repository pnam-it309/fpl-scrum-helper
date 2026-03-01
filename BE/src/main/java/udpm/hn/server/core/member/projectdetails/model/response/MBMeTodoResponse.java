package udpm.hn.server.core.member.projectdetails.model.response;

public interface MBMeTodoResponse {

    String getId();

    String getCode();

    String getName();

    Long getCompletionTime();

    String getPriorityLevel();

    Long getDeadline();

    Short getProgress();

    Short getIndexTodo();

    String getImageId();

    String getUrlImage();

    Short getNumberTodoComplete();

    Short getNumberTodo();

    String getTodoListId();

    String getTypeTodo();
}
