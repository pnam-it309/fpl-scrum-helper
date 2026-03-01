package udpm.hn.server.core.member.projectdetails.model.response;

import udpm.hn.server.infrastructure.constant.EntityStatus;

public interface MBTypeTodoResponse {

    String getId();

    String getType();

    EntityStatus getStatus();

}
