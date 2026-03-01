import { websocketService } from "@/services/configsocket/websocket";

export type MBMeCreateCommentRequest = {
    idTodo: string;
    email: string;
    content: string;
    idStaffProject:string 
};

export type MBMeUpdateCommentRequest = {
    content: string;
    id: string;
    idTodo: string;
    idTodoList: string;
    idStaffProject:string
};

export type MBMeDeleteCommentRequest = {
    id: string;
    idStaffProject: string;
    idTodo: string;
    idTodoList: string;
};

export class WebSocketMeTodoComment {

  getAddComment(callback: (data: MBMeCreateCommentRequest) => void) {
    const topic = `/topic/create-comment`;
    websocketService.subscribe(topic, callback);
  }

  sendAddComment(payload: MBMeCreateCommentRequest) {
    const destination = `/app/create-comment`;
    websocketService.sendMessage(destination, payload);
  }

  getUpdateComment(callback: (data: MBMeUpdateCommentRequest) => void) {
    const topic = `/topic/update-comment`;
    websocketService.subscribe(topic, callback);
  }

  sendUpdateComment(payload: MBMeUpdateCommentRequest) {
    const destination = `/app/update-comment`;
    websocketService.sendMessage(destination, payload);
  }

  getDeleteComment(callback: (data: MBMeDeleteCommentRequest) => void) {
    const topic = `/topic/delete-comment`;
    websocketService.subscribe(topic, callback);
  }

  sendDeleteComment(payload: MBMeDeleteCommentRequest) {
    const destination = `/app/delete-comment`;
    websocketService.sendMessage(destination, payload);
  }

}

export const webSocketMeTodoComment = new WebSocketMeTodoComment();
