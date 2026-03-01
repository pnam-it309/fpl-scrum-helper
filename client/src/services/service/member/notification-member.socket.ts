import { USER_INFO_STORAGE_KEY } from "@/constants/storagekey";
import { websocketService } from "@/services/configsocket/websocket";
import { localStorageAction } from "@/utils/storage";

export type MBMessageNotificationMemberRequest = {
};

export class WebSocketNotificationMember {

    getNotificationMember(callback: (data: MBMessageNotificationMemberRequest) => void) {
        const topic = `/topic/subscribe-notification-member`;
        websocketService.subscribe(topic, callback);
    }

    sendNotificationMember(payload: MBMessageNotificationMemberRequest) {
        const destination = `/app/message-notification-member`;
        websocketService.sendMessage(destination, payload);
    }

    getCreateNotificationMember(callback: (data: MBMessageNotificationMemberRequest) => void) {
        const userLogin = localStorageAction.get(USER_INFO_STORAGE_KEY)
        const topic = `/topic/create-notification-member/${userLogin.userId}`;
        websocketService.subscribe(topic, callback);
    }

}

export const webSocketNotificationMember = new WebSocketNotificationMember();
