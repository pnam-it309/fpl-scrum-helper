import { websocketService } from "@/services/configsocket/websocket";

// MBMeTodoAndTodoListRequest.ts
export type MBMeTodoAndTodoListRequest = {
    idTodo: string;
    idTodoList: string;
};

export type MBMeJoinOrOutLabelTodoRequest = MBMeTodoAndTodoListRequest & {
    idLabel: string;
    idTodoJoinOrOut: string;
};

export class WebSocketLabelProjectTodo {

    getjoinLabeProjectTodo(callback: (data: MBMeJoinOrOutLabelTodoRequest) => void) {
        const topic = `/topic/join-project-todo`;
        websocketService.subscribe(topic, callback);
    }

    sendjoinLabeProjectTodo(payload: MBMeJoinOrOutLabelTodoRequest) {
        const destination = `/app/join-label-project-todo`;
        websocketService.sendMessage(destination, payload);
    }

}

export const webSocketLabelProjectTodo = new WebSocketLabelProjectTodo();
