import { websocketService } from '@/services/configsocket/websocket'
export interface dataCreateTodo {
  name?: string
  code?: string
  priorityLevel?: string
  idType?: string
  idProject?: string
  point?: string
    idUser: string        // Người thao tác (assigner)
  nameUser: string      // Tên người thao tác
  urlPath: string       // Đường dẫn để lưu activity
}

export class TodoWebSocket {
  connect(callback?: () => void) {
    websocketService.connect(callback)
  }

  subscribeCreateTodo(callback) {
    const topic = `/topic/create-todo` // Kiểm tra kỹ đường dẫn này!
    console.log('📡 Đang lắng nghe tại:', topic)
    websocketService.subscribe(topic, (data) => {
      console.log('📬 Dữ liệu nhận được: ', data)
      callback(data)
    })
  }

  sendMessageCreateTodo(payload: dataCreateTodo) {
    const des = `/app/create-todo`
    websocketService.sendMessage(des, payload)
  }

  subscribeDeleteTodo(callback) {
    const topic = `/topic/delete-todo`
    websocketService.subscribe(topic, callback)
  }

  sendMessageDeleteTodo(id: string) {
    const des = `/app/delete-todo/${id}`
    websocketService.sendMessage(des, 'Xóa thành công')
  }

  subscribeUpdateTodo(callback) {
    const topic = `/topic/update-todo`
    websocketService.subscribe(topic, callback)
  }

  sendMessageUpdateTodo(id: string, payload: dataCreateTodo) {
    const des = `/app/update-todo/${id}`
    websocketService.sendMessage(des, payload)
  }
}

export const todoWebSocket = new TodoWebSocket()
