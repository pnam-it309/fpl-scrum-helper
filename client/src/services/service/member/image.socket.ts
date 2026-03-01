import { websocketService } from "@/services/configsocket/websocket";

export type MBMeCreateImageRequest = {
    urlImage: string;
    nameFileOld: string;
    idTodo: string;
    idTodoList:string 
    projectId:string
    idUser:string
    urlPath:string 
};

export type MBMeUpdateNameImageRequest = {
    id: string;
    nameImage: string;
};

export type MBUpdateBackgroundProject = {
  idProject: string;
  backgroundColor: string;
  backgroundImage: string;
};

export type MBMeDeleteImageRequest = {
    id: string;
    urlImage: string;
    nameImage: string;
    statusImage:string 
    idTodo:string
    idTodoList:string
    projectId:string
    idUser:string
};

export type MBMeChangeCoverTodoRequest = {
    idTodo: string;
    idImage: string;
    nameFile: string;
    idTodoList:string 
    status:string
};

export class WebSocketImage {

  getCreateImage(callback: (data: MBMeCreateImageRequest) => void) {
    const topic = `/topic/create-image`;
    websocketService.subscribe(topic, callback);
  }

  sendCreateImage(payload: MBMeCreateImageRequest) {
    const destination = `/app/create-image`;
    websocketService.sendMessage(destination, payload);
  }

  getUpdateNameImage(callback: (data: MBMeUpdateNameImageRequest) => void) {
    const topic = `/topic/update-name-image`;
    websocketService.subscribe(topic, callback);
  }

  sendUpdateNameImage(payload: MBMeUpdateNameImageRequest) {
    const destination = `/app/update-name-image`;
    websocketService.sendMessage(destination, payload);
  }

  getDeleteImage(callback: (data: MBMeDeleteImageRequest) => void) {
    const topic = `/topic/delete-image`;
    websocketService.subscribe(topic, callback);
  }

  sendDeleteImage(payload: MBMeDeleteImageRequest) {
    const destination = `/app/delete-image`;
    websocketService.sendMessage(destination, payload);
  }

  getChangeCoverTodo(callback: (data: MBMeChangeCoverTodoRequest) => void) {
    const topic = `/topic/change-cover-image`;
    websocketService.subscribe(topic, callback);
  }

  sendChangeCoverTodo(payload: MBMeChangeCoverTodoRequest) {
    const destination = `/app/change-cover-image`;
    websocketService.sendMessage(destination, payload);
  }

  getUpdateBackgroundProject(callback: (data: MBUpdateBackgroundProject) => void) {
    const topic = `/topic/update-background-project`;
    websocketService.subscribe(topic, callback);
  }

  sendUpdateBackgroundProject(payload: MBUpdateBackgroundProject) {
    const destination = `/app/update-background-project`;
    websocketService.sendMessage(destination, payload);
  }
}

export const webSocketImage = new WebSocketImage();
