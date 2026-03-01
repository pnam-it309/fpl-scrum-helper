import request from '@/services/request'

export class DashboardApi {
  static findProjectById = (idProject: string) => {
    return request({
      method: 'GET',
      url: `/member/project/detail-project/${idProject}`
    })
  }

  static findAllPhaseProject = (idProject: string) => {
    return request({
      method: 'GET',
      url: `/member/phase/${idProject}`
    })
  }

  static fetchAllBoard = (filter: Record<string, any>) => {
    return request({
      method: 'POST',
      url: '/member/todo',
      data: filter
    })
  }

  static fetchAllLabelProject = (idProject: string) => {
    return request({
      method: 'GET',
      url: `/member/label/list?idProject=${idProject}`
    })
  }

  static fetchAllNotification = (memberId: string, page: number) => {
    return request({
      method: 'GET',
      url: `/member/notification-member?memberId=${memberId}&page=${page}`
    })
  }

  static countNotification = (memberId: string) => {
    return request({
      method: 'GET',
      url: `/member/notification-member/count?memberId=${memberId}`
    })
  }

  static updateStatusNotification = (idNotificationMember: string) => {
    return request({
      method: 'PUT',
      url: `/member/notification-member/update-status?idNotificationMember=${idNotificationMember}`
    })
  }

  static updateAllStatusNotification = (memberId: string) => {
    return request({
      method: 'PUT',
      url: `/member/notification-member/update-all-status?memberId=${memberId}`
    })
  }

  static createNotification = (obj: Record<string, any>) => {
    return request({
      method: 'POST',
      url: `/member/notification`,
      data: obj
    })
  }
}
