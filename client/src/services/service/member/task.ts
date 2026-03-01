import { websocketService } from "@/services/configsocket/websocket";

export type TaskResponse = {
  id: string;
  name: string;
  description?: string;
  status: string;
  indexTask: number;
  todoListId: string;
  projectId: string;
};

export type MBMeCreateTaskRequest = {
  name: string;
  todoListId: string;
  projectId: string;
};

export type MBMeUpdateTaskRequest = {
  idTask: string;
  name?: string;
  description?: string;
  status?: string;
  indexTask?: number;
  todoListId?: string;
  projectId: string;
};

export type MBMeDeleteTaskRequest = {
  idTask: string;
  projectId: string;
};

export class WebSocketTask {
  connect(callback?: () => void) {
    websocketService.connect(callback);
  }


  getTaskList(callback: (tasks: TaskResponse[]) => void) {
    websocketService.subscribe("/topic/task-list", (message) => {
      callback(message);
    });
  }

  /* 🔵 Gửi yêu cầu tạo task */
  sendCreateTask(projectId: string, payload: MBMeCreateTaskRequest) {
    websocketService.sendMessage(`/app/create-task/${projectId}`, payload);
  }

  sendUpdateTask(projectId: string, payload: MBMeUpdateTaskRequest) {
    websocketService.sendMessage(`/app/update-task/${projectId}`, payload);
  }

  sendDeleteTask(projectId: string, payload: MBMeDeleteTaskRequest) {
    websocketService.sendMessage(`/app/delete-task/${projectId}`, payload);
  }

  sendUpdateTaskPosition(projectId: string, payload: MBMeUpdateTaskRequest) {
    websocketService.sendMessage(`/app/update-task-position/${projectId}`, payload);
  }

  getUpdatedTask(callback: (task: TaskResponse) => void) {
    websocketService.subscribe("/topic/update-task", (message) => {
      callback(message);
    });
  }

  getDeletedTask(callback: (task: MBMeDeleteTaskRequest) => void) {
    websocketService.subscribe("/topic/delete-task", (message) => {
      callback(message);
    });
  }
}

export const webSocketTask = new WebSocketTask();
