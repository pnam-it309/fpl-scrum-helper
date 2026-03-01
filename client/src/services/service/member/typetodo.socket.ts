import { websocketService } from "@/services/configsocket/websocket";

export type ADCreateUpdateTypeTodoRequest = {
    type: string;
};
export type ADChangeTypeTodoRequest = {
    typeId:string ; 
};
export type ADJoinTypeTodoRequest = {
    idTodo: string;
    idType: string ;
};
export class WebSocketTypeTodo {

    getUpdateTypeTodo(callback: (data: ADCreateUpdateTypeTodoRequest) => void) {
        const topic = `/topic/create-type-todo`;
        websocketService.subscribe(topic, callback);
    }

    sendUpdateTypeTodo(payload: ADCreateUpdateTypeTodoRequest) {
        const destination = `/app/create-type-todo`;
        websocketService.sendMessage(destination, payload);
    }

    getChangeStatusTypeTodo(callback: (data: ADChangeTypeTodoRequest) => void) {
        const topic = `/topic/change-type-todo`;
        websocketService.subscribe(topic, callback);
    }

    sendChangeTypeTodo(payload: ADChangeTypeTodoRequest) {
        const destination = `/app/change-type-todo`;
        websocketService.sendMessage(destination, payload);
    }

    getJoinTypeTodo(callback: (data: ADJoinTypeTodoRequest) => void) {
        const topic = `/topic/join-type-todo`;
        websocketService.subscribe(topic, callback);
    }

    sendJoinTypeTodo(payload: ADJoinTypeTodoRequest) {
        const destination = `/app/join-type-todo`;
        websocketService.sendMessage(destination, payload);
    }


}

export const webSocketTypeTodo = new WebSocketTypeTodo();
