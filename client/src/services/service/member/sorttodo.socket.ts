import { websocketService } from "@/services/configsocket/websocket";

export type MBMeSortTodoRequest = {
    idPhase: string;
    type: number;
};

export class WebSocketSortTodo {

  getSortTodoPriority(callback: (data: MBMeSortTodoRequest) => void) {
    const topic = `/topic/sort-todo-priority`;
    websocketService.subscribe(topic, callback);
  }

  sendSortTodoPriority(payload: MBMeSortTodoRequest) {
    const destination = `/app/sort-todo-priority`;
    websocketService.sendMessage(destination, payload);
  }

  getSortTodoDeadline(callback: (data: MBMeSortTodoRequest) => void) {
    const topic = `/topic/sort-todo-deadline`;
    websocketService.subscribe(topic, callback);
  }

  sendSortTodoDeadline(payload: MBMeSortTodoRequest) {
    const destination = `/app/sort-todo-deadline`;
    websocketService.sendMessage(destination, payload);
  }

  getSortTodoCreatedDate(callback: (data: MBMeSortTodoRequest) => void) {
    const topic = `/topic/sort-todo-created-date`;
    websocketService.subscribe(topic, callback);
  }

  sendSortTodoCreatedDate(payload: MBMeSortTodoRequest) {
    const destination = `/app/sort-todo-created-date`;
    websocketService.sendMessage(destination, payload);
  }

  
  getSortTodoProgress(callback: (data: MBMeSortTodoRequest) => void) {
    const topic = `/topic/sort-todo-progress`;
    websocketService.subscribe(topic, callback);
  }

  sendSortTodoProgress(payload: MBMeSortTodoRequest) {
    const destination = `/app/sort-todo-progress`;
    websocketService.sendMessage(destination, payload);
  }

  getSortTodoName(callback: (data: MBMeSortTodoRequest) => void) {
    const topic = `/topic/sort-todo-name`;
    websocketService.subscribe(topic, callback);
  }

  sendSortTodoName(payload: MBMeSortTodoRequest) {
    const destination = `/app/sort-todo-name`;
    websocketService.sendMessage(destination, payload);
  }

}

export const webSocketSortTodo = new WebSocketSortTodo();
