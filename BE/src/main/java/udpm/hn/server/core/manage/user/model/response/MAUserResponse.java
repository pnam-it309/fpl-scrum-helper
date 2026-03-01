package udpm.hn.server.core.manage.user.model.response;

public interface MAUserResponse {

    String getName();

    String getCode();

    String getRole();

    String getEmail();

    Long getCountTodo();

    Long getCountTodoByPhase();

    String getCodeProject();

    String getIdStaffProject();

    Long getCountCompletedTodo();

    default double getProgressPercentage() {
        if (getCountTodoByPhase() == null || getCountTodoByPhase() == 0) {
            return 0.0;
        }
        long completed = getCountCompletedTodo() != null ? getCountCompletedTodo() : 0L;
        double result = (double) completed / getCountTodoByPhase();
        return Math.round(result * 10000.0) / 100.0;
    }

}
