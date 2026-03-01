import { websocketService } from '@/services/configsocket/websocket'

export interface dataCreateTodoVote {
  idUser: string
  idTodo: string
  idProject: string
}

export class TodoVoteWebSocket {
  connect(callback?: () => void) {
    websocketService.connect(callback)
  }

  subscribeCreateTodoVote(callback) {
    const topic = `/topic/create-todo-vote`
    websocketService.subscribe(topic, (data) => {
      callback(data)
    })
  }

  sendMessageCreateTodoVote(payload: dataCreateTodoVote) {
    const des = `/app/create-todo-vote`
    websocketService.sendMessage(des, payload)
  }

  subscribeDeleteTodoVote(callback) {
    const topic = `/topic/delete-todo-vote`
    websocketService.subscribe(topic, (data) => {
      callback(data)
    })
  }

  sendMessageDeleteTodoVote(payload: dataCreateTodoVote) {
    const des = `/app/delete-todo-vote`
    websocketService.sendMessage(des, payload)
  }
}

export const todoVoteWebSocket = new TodoVoteWebSocket()
