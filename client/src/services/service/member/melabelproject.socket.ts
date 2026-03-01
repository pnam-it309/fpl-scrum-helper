import { websocketService } from "@/services/configsocket/websocket";

export type MBMeCreateLabelProjectRequest = {
    name: string;
    projectId: string;
    color: string;
  };

  export type MBMeUpdateLabelProjectRequest = {
    name: string;
    id: string;
    color: string;
  };
  
  export type MBMeDeleteLabelProjectRequest = {
    idLabelProject: string;
    projectId: string;
  };

export class WebSocketLabelProject {

   getCreateLabelProject(callback: (data: MBMeCreateLabelProjectRequest) => void) {
    const topic = `/topic/create-label-project`;
    websocketService.subscribe(topic, callback);
  }

  sendCreateLabelProject(payload: MBMeCreateLabelProjectRequest) {
    const destination = `/app/create-label-project`;
    websocketService.sendMessage(destination, payload);
  }

  getUpdateLabelProject(callback: (data: MBMeUpdateLabelProjectRequest) => void) {
    const topic = `/topic/update-label-project`;
    websocketService.subscribe(topic, callback);
  }

  sendUpdateLabelProject(payload: MBMeUpdateLabelProjectRequest) {
    const destination = `/app/update-label-project`;
    websocketService.sendMessage(destination, payload);
  }

  getDeleteLabelProject(callback: (data: MBMeDeleteLabelProjectRequest) => void) {
    const topic = `/topic/delete-label-project`;
    websocketService.subscribe(topic, callback);
  }

  sendDeleteLabelProject(payload: MBMeDeleteLabelProjectRequest) {
    const destination = `/app/delete-label-project`;
    websocketService.sendMessage(destination, payload);
  }

}

export const webSocketLabelProject = new WebSocketLabelProject();
