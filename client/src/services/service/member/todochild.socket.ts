import { StatusTodoChild } from "@/constants/statusTodoChild";
import { websocketService } from "@/services/configsocket/websocket";

export type MBMeCreateTodoChildChecklistRequest = {
  name: string;
  idTodo: string;
  idUser?: string;
  sessionId?: string;
};

export type MBMeCreateTodoChildChecklistResponse = {
  id: string;
  name: string;
  idTodo: string;
  statusTodoChild: boolean;
};

export type MBMeDeleteTodoChecklistRequest = {
  idTodoChild: string;
  idTodo: string;
  periodId: string;
};

export type MBMeEditTodoChecklistRequest = {
  idTodoChild: string;
  name: string;
};

export type MBMeDeleteTodoChecklistResponse = {
  message?: string;
  success?: boolean;
};


export type MBHandleStatusTodoChildRequest = {
  idTodoChild: string;
  statusTodoChild: StatusTodoChild;
  sessionId?: string;
  idTodo: string
};

export type MBHandleStatusTodoChildResponse = {
  idTodoChild: string;
  statusTodoChild: boolean;
  message?: string;
  success?: boolean;
};

export type MBMeUpdateProgressTodoRequest = {
  idTodo: string;
  projectId:string 
  idUser?: string;
  progress: number
  urlPath : string
};

export class WebSocketTodoChild {
  connect(callback?: () => void) {
    websocketService.connect(callback);
  }

  getCreateTodoChildChecklist(callback: (data: MBMeCreateTodoChildChecklistResponse) => void) {
    const topic = `/topic/create-todochild-checklist`;
    websocketService.subscribe(topic, callback);
  }

  sendCreateTodoChildChecklist(payload: MBMeCreateTodoChildChecklistRequest) {
    const destination = `/app/create-todochild-checklist`;
    websocketService.sendMessage(destination, payload);
  }

  getDeleteTodoChecklist(callback: (data: MBMeDeleteTodoChecklistResponse) => void) {
    const topic = `/topic/delete-todo-checklist`;
    websocketService.subscribe(topic, callback);
  }

  sendDeleteTodoChecklist(payload: MBMeDeleteTodoChecklistRequest) {
    const destination = `/app/delete-todo-checklist`;
    websocketService.sendMessage(destination, payload);
  }

  getEditTodoChecklist(callback: (data: MBMeEditTodoChecklistRequest) => void) {
    const topic = `/topic/edit-todochild-checklist`;
    websocketService.subscribe(topic, callback);
  }

  sendEditTodoChecklist(payload: MBMeEditTodoChecklistRequest) {
    const destination = `/app/edit-todochild-checklist`;
    websocketService.sendMessage(destination, payload);
  }

  getUpdateStatusTodoChecklist(callback: (data: MBHandleStatusTodoChildResponse) => void) {
    const topic = `/topic/update-status-todochild-checklist`;
    websocketService.subscribe(topic, callback);
  }

  sendUpdateStatusTodoChecklist(payload: MBHandleStatusTodoChildRequest) {
    const destination = `/app/update-status-todochild-checklist`;
    websocketService.sendMessage(destination, payload);
  }

  getUpdateProgress(callback: (data: MBMeUpdateProgressTodoRequest) => void) {
    const topic = `/topic/update-progress-todo`;
    websocketService.subscribe(topic, callback);
  }

  sendUpdateProgress(payload: MBMeUpdateProgressTodoRequest) {
    const destination = `/app/update-progress-todo`;
    websocketService.sendMessage(destination, payload);
  }

}

export const webSocketTodoChild = new WebSocketTodoChild();


