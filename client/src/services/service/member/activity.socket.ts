import { websocketService } from "@/services/configsocket/websocket";

export class WebSocketActivity {

    getAllActivityWhereIdTodo(callback: (message: string) => void) {
    const topic = `/topic/subscribe-activity`;
    websocketService.subscribe(topic, callback);
  }

}

export const webSocketActivity = new WebSocketActivity();
