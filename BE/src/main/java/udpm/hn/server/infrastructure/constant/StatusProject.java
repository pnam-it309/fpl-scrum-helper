package udpm.hn.server.infrastructure.constant;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum StatusProject {

    CHUA_DIEN_RA,

    DANG_DIEN_RA,

    DA_DIEN_RA;

    public static List<String> getAllStatus() {
        return Arrays.stream(EntityStatus.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    public static String getAllStatusString() {
        return Arrays.stream(EntityStatus.values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }

}
