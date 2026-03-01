import { websocketService } from "@/services/configsocket/websocket";

export type MBCreateTodoRequest = {
  name: string;
  idTodoList: string;
  indexTodo: number;
  idUser?: string;
  sessionId?: string;
};

export type MBMeUpdateIndexTodoRequest = {
  idTodo: string;
  idTodoListOld: string;
  nameTodoListOld: string;
  idTodoListNew: string;
  nameTodoListNew: string;
  indexBefore: number;
  indexAfter: number;
  phaseId: string;
  projectId: string;
  idUser?: string;
  sessionId?: string;
};

export type MBMeUpdateDescriptionsTodoRequest = {
  idTodoUpdate: string;
  descriptions: string;
  idTodo: string;
  idTodoList: string;
};

export type MBMeUpdateNameTodoRequest = {
  idTodo:string
  nameTodo:string
};

export type MBMeTodoAndTodoListRequest = {
  idTodo: string;
  idTodoList: string;
};

export type MBMeUpdateDeadlineTodoRequest = MBMeTodoAndTodoListRequest &  {
  idTodoUpdate:string
  deadline:string
  reminder:string
  projectId:string
  idUser:string
};

export type MBMeDeleteDeadlineTodoRequest = MBMeTodoAndTodoListRequest &  {
  idTodoDelete:string
  projectId:string
  idUser:string
};

export type MBMeUpdateCompleteTodoRequest = {
  id:string
  status:number
  idTodo:string
  idTodoList:string
  projectId:string
  periodId:string
  idUser:string
};

export type MBMeUpdateTodoRequest = MBMeTodoAndTodoListRequest & {
  idTodoChange:string
  priorityLevel:number
};


export class WebSocketTodo {
  connect(callback?: () => void) {
    websocketService.connect(callback);
  }

  subscribeUpdateIndexTodo(callback: (data: MBMeUpdateIndexTodoRequest) => void) {
    const topic = `/topic/update-index-todo`;
    websocketService.subscribe(topic, callback);
  }

  sendUpdateIndexTodo(projectId: string, periodId: string, payload: MBMeUpdateIndexTodoRequest) {
    const destination = `/app/update-index-todo/${projectId}/${periodId}`;
    websocketService.sendMessage(destination, payload);
  }

  sendCreateTodo(projectId: string, phaseId: string, payload: MBCreateTodoRequest) {
    const destination = `/app/create-todo/${projectId}/${phaseId}`;
    websocketService.sendMessage(destination, payload);
  }

  subscribeCreateTodo(callback: (data: MBMeCreateTaskRequest) => void) {
    const topic = `/topic/create-todo`;
    websocketService.subscribe(topic, callback);
  }

  sendUpdateDescriptionsTodo(payload: MBMeUpdateDescriptionsTodoRequest) {
    const destination = `/app/update-descriptions-todo`;
    websocketService.sendMessage(destination, payload);
  }

  getUpdateDescriptionsTodo(callback: (data: MBMeUpdateDescriptionsTodoRequest) => void) {
    const topic = `/topic/update-descriptions-todo`;
    websocketService.subscribe(topic, callback);
  }

  sendUpdateNameTodo(payload: MBMeUpdateNameTodoRequest) {
    const destination = `/app/update-name-todo`;
    websocketService.sendMessage(destination, payload);
  }

  getUpdateNameTodo(callback: (data: MBMeUpdateNameTodoRequest) => void) {
    const topic = `/topic/update-name-todo`;
    websocketService.subscribe(topic, callback);
  }

  sendUpdateDeadlineTodo(payload: MBMeUpdateDeadlineTodoRequest) {
    const destination = `/app/update-deadline-todo`;
    websocketService.sendMessage(destination, payload);
  }

  getUpdateDeadlineTodo(callback: (data: MBMeUpdateDeadlineTodoRequest) => void) {
    const topic = `/topic/update-deadline-todo`;
    websocketService.subscribe(topic, callback);
  }

  sendDeleteDeadlineTodo(payload: MBMeDeleteDeadlineTodoRequest) {
    const destination = `/app/delete-deadline-todo`;
    websocketService.sendMessage(destination, payload);
  }

  getDeleteDeadlineTodo(callback: (data: MBMeDeleteDeadlineTodoRequest) => void) {
    const topic = `/topic/delete-deadline-todo`;
    websocketService.subscribe(topic, callback);
  }

  sendupdateCompleteTodo(payload: MBMeUpdateCompleteTodoRequest) {
    const destination = `/app/update-complete-todo`;
    websocketService.sendMessage(destination, payload);
  }

  getupdateCompleteTodo(callback: (data: MBMeUpdateCompleteTodoRequest) => void) {
    const topic = `/topic/update-complete-todo`;
    websocketService.subscribe(topic, callback);
  }

  sendUpdatePriorityLevel(payload: MBMeUpdateTodoRequest) {
    const destination = `/app/update-priority-todo`;
    websocketService.sendMessage(destination, payload);
  }

  getUpdatePriorityLevel(callback: (data: MBMeUpdateTodoRequest) => void) {
    const topic = `/topic/todo`;
    websocketService.subscribe(topic, callback);
  }  
}

export const webSocketTodo = new WebSocketTodo();
