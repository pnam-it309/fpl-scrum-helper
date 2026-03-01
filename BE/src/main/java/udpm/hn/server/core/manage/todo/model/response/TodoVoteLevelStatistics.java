package udpm.hn.server.core.manage.todo.model.response;

import udpm.hn.server.infrastructure.constant.PriorityLevel;

public interface TodoVoteLevelStatistics {
    Byte getLevel();  // Enum hoặc String tùy thiết kế
    Long getTotal();
}
