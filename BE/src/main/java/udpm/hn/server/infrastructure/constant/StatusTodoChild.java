package udpm.hn.server.infrastructure.constant;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum StatusTodoChild {
    UNCOMPLETE,
    COMPLETE;

    public static List<String> getAllTodoChild() {
        return Arrays.stream(StatusTodoChild.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    public static String getAllTodoChildString() {
        return Arrays.stream(StatusTodoChild.values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }
}
