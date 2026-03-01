package udpm.hn.server.infrastructure.listener;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import udpm.hn.server.entity.base.AuditEntity;

import java.util.Calendar;

public class AuditEntityListener {

    @PrePersist
    private void onCreate(AuditEntity entity) {
        entity.setCreatedDate(getCurrentTime());
        entity.setLastModifiedDate(getCurrentTime());
    }

    @PreUpdate
    private void onUpdate(AuditEntity entity) {
        entity.setLastModifiedDate(getCurrentTime());
    }

    private long getCurrentTime() {
        return Calendar.getInstance().getTimeInMillis();
    }

}
