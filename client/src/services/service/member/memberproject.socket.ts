import { websocketService } from "@/services/configsocket/websocket";

export type MBMeCreateOrDeleteTodoVoteRequest = {
    idTodo: string;
    projectId: string;
    idMember: string;       // Người nhận công việc
    nameMember?: string;    // Tên người nhận công việc (assignee)
    idUser?: string;        // Người thao tác/ phân công (assigner)
    nameAssigner?: string;  // Tên người phân công
    urlPath?: string;       // URL hiển thị khi click thông báo
};

export class WebSocketMemberProject {
  connect(callback?: () => void) {
    websocketService.connect(callback);
  }

  
   getJoinTodoVote(callback: (data: MBMeCreateOrDeleteTodoVoteRequest) => void) {
    const topic = `/topic/join-todoVote`;
    websocketService.subscribe(topic, callback);
  }

  sendJoinTodoVote(payload: MBMeCreateOrDeleteTodoVoteRequest) {
    const destination = `/app/join-todoVote`;
    websocketService.sendMessage(destination, payload);
  }


  getOutTodoVoteMemberProject(callback: (data: MBMeCreateOrDeleteTodoVoteRequest) => void) {
    const topic = `/topic/out-todoVote`;
    websocketService.subscribe(topic, callback);
  }

  sendOutTodoVoteMemberProject(payload: MBMeCreateOrDeleteTodoVoteRequest) {
    const destination = `/app/out-todoVote`;
    websocketService.sendMessage(destination, payload);
  }

}

export const webSocketMemberProject = new WebSocketMemberProject();
