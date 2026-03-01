import { websocketService } from '@/services/configsocket/websocket'

export type MBMeUpdateTodoListRequest = {
  idTodoList: string
  idProject: string
  indexBefore: string
  indexAfter: string
  sessionId: string
  idPhase:string
}

export type MBMeCreateTodoListRequest = {
  name: string
  idProject: string
  idPhase:string
}

export type TodoListResponse = {
  id: string
  code: string
  name: string
  describe: string
  indexTodoList: number
  project: {
    id: string
    name?: string
  }
}

export type MBMeDeleteTodoListRequest = {
  id: string
  projectId: string
  idPhase:string
}

export type MBMeUpdateNameTodoListRequest = {
  idTodoList: string
  name: string
}

export class WebSocketTodoList {
  connect(callback?: () => void) {
    websocketService.connect(callback)
  }

  getUpdateIndexColumn(callback: (data: MBMeUpdateTodoListRequest) => void) {
    const topic = `/topic/update-index-todo-list`
    websocketService.subscribe(topic, callback)
  }

  sendUpdateIndexColumn(projectId: string, payload: MBMeUpdateTodoListRequest) {
    const destination = `/app/update-index-todo-list/${projectId}`
    websocketService.sendMessage(destination, payload)
  }

  sendCreateTodoList(projectId: string, payload: MBMeCreateTodoListRequest) {
    const destination = `/app/create-todo-list/${projectId}`
    websocketService.sendMessage(destination, payload)
  }

  getCreateTodoList(callback: (data: TodoListResponse) => void) {
    const topic = `/topic/create-todo-list`
    websocketService.subscribe(topic, callback)
  }

  sendDeleteTodoList(projectId: string, payload: MBMeDeleteTodoListRequest) {
    const destination = `/app/delete-todo-list/${projectId}`
    websocketService.sendMessage(destination, payload)
  }

  getDeleteTodoList(callback: (data: MBMeDeleteTodoListRequest) => void) {
    const topic = `/topic/delete-todo-list`
    websocketService.subscribe(topic, callback)
  }

  sendUpdateNameTodoList(projectId: string, payload: MBMeUpdateNameTodoListRequest) {
    const destination = `/app/update-name-todo-list/{projectId}${projectId}`
    websocketService.sendMessage(destination, payload)
  }
  
  getUpdateNameTodoList(callback: (data: MBMeUpdateNameTodoListRequest) => void) {
    const topic = `/topic/update-name-todo-list`
    websocketService.subscribe(topic, callback)
  }
}

export const webSocketTodoList = new WebSocketTodoList()
