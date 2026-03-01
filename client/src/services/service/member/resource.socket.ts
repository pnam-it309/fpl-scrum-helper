import { websocketService } from "@/services/configsocket/websocket";

export type MBMeCreateResourceRequest = {
    name: string;
    url: string;
    idTodo: string;
    idTodoList:string ; 
    projectId:string ; 
    idUser:string 
};

export type MBMeUpdateResourceRequest = {
    id: string;
    name: string;
    url: string;
    idTodo: string;
    idTodoList:string
    projectId:string
    idUser:string
};

export type MBMeDeleteResourceRequest = {
    id: string;
    url: string;
    name: string;
    idTodo: string;
    idTodoList:string ;
    projectId:string ;
    idUser:string 
};

export class WebSocketMeTodoResource {

  getAddResourceTodo(callback: (data: MBMeCreateResourceRequest) => void) {
    const topic = `/topic/create-resource`;
    websocketService.subscribe(topic, callback);
  }

  sendAddResourceTodo(payload: MBMeCreateResourceRequest) {
    const destination = `/app/create-resource`;
    websocketService.sendMessage(destination, payload);
  }

  getUpdateResourceTodo(callback: (data: MBMeUpdateResourceRequest) => void) {
    const topic = `/topic/update-resource`;
    websocketService.subscribe(topic, callback);
  }

  sendUpdateResourceTodo(payload: MBMeUpdateResourceRequest) {
    const destination = `/app/update-resource`;
    websocketService.sendMessage(destination, payload);
  }

  getDeleteResourceTodo(callback: (data: MBMeDeleteResourceRequest) => void) {
    const topic = `/topic/delete-resource`;
    websocketService.subscribe(topic, callback);
  }

  sendDeleteResourceTodo(payload: MBMeDeleteResourceRequest) {
    const destination = `/app/delete-resource`;
    websocketService.sendMessage(destination, payload);
  }

}

export const webSocketMeTodoResource = new WebSocketMeTodoResource();
