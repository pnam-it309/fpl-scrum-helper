import request from '@/services/request'
import {
  PREFIX_API_MANAGE_TODO,
  PREFIX_API_TODO_LIST_MANAGE,
  PREFIX_API_MEMBER_PROJECT_MANAGE,
  PREFIX_API_LABEL_MANAGE,
  PREFIX_API_MANAGE_TODO_BY_PHASE
} from '@/constants/url'

export class DashboardApi {
  // Lấy danh sách tất cả TodoList theo Project
  static fetchAllTodoList(idProject: string) {
    return request({
      method: 'GET',
      url: `${PREFIX_API_TODO_LIST_MANAGE}/${encodeURIComponent(idProject)}`
    })
  }

  // Thống kê TodoList của tất cả dự án
  static fetchAllDataDashboardTodoListAllProject(idProject: string) {
    return request({
      method: 'GET',
      url: `${PREFIX_API_MANAGE_TODO}/count-todo-by-todo-list-all-project?projectId=${encodeURIComponent(
        idProject
      )}`
    })
  }

  // Thống kê Todo có Due Date theo tất cả dự án
  static fetchAllDataDashboardDueDateAllProject(idProject: string, statusTodo: number) {
    return request({
      method: 'GET',
      url: `${PREFIX_API_MANAGE_TODO}/count-todo-by-due-date-all-project?projectId=${encodeURIComponent(
        idProject
      )}&statusTodo=${encodeURIComponent(statusTodo)}`
    })
  }

  // Thống kê Todo không có Due Date theo tất cả dự án
  static fetchAllDataDashboardNoDueDateAllProject(idProject: string) {
    return request({
      method: 'GET',
      url: `${PREFIX_API_MANAGE_TODO}/count-todo-by-no-due-date-all-project?projectId=${encodeURIComponent(
        idProject
      )}`
    })
  }

  // Thống kê Todo theo thành viên của tất cả dự án
  static fetchAllDataDashboardMemberAllProject(idProject: string) {
    return request({
      method: 'GET',
      url: `${PREFIX_API_MANAGE_TODO}/count-todo-by-member-all-project?projectId=${encodeURIComponent(
        idProject
      )}`
    })
  }

  // Thống kê Todo không có thành viên theo tất cả dự án
  static fetchAllDataDashboardNoMemberAllProject(idProject: string) {
    return request({
      method: 'GET',
      url: `${PREFIX_API_MANAGE_TODO}/count-todo-by-no-member-all-project?projectId=${encodeURIComponent(
        idProject
      )}`
    })
  }

  // Thống kê Todo theo nhãn của tất cả dự án
  static fetchAllDataDashboardLabelAllProject(idProject: string) {
    return request({
      method: 'GET',
      url: `${PREFIX_API_MANAGE_TODO}/count-todo-by-label-all-project?projectId=${encodeURIComponent(
        idProject
      )}`
    })
  }

  // Thống kê Todo không có nhãn theo tất cả dự án
  static fetchAllDataDashboardNoLabelAllProject(idProject: string) {
    return request({
      method: 'GET',
      url: `${PREFIX_API_MANAGE_TODO}/count-todo-by-no-label-all-project?projectId=${encodeURIComponent(
        idProject
      )}`
    })
  }

  // Thong ke Todo theo level
  static fetchAllDataDashboardLevelAllProject(idProject: string, priorityLevel: number)

  /////////////////////////////////////////////
  // API liên quan đến Phase

  // Thống kê TodoList theo từng Phase
  static fetchAllDataDashboardTodoListPhase(idProject: string, idPhase: string) {
    return request({
      method: 'GET',
      url: `${PREFIX_API_MANAGE_TODO_BY_PHASE}/count-todo-by-todo-list-phase?projectId=${encodeURIComponent(
        idProject
      )}&phaseId=${encodeURIComponent(idPhase)}`
    })
  }

  // Thống kê Todo có Due Date theo Phase
  static fetchAllDataDashboardDueDatePhase(idProject: string, statusTodo: string, idPhase: string) {
    return request({
      method: 'GET',
      url: `${PREFIX_API_MANAGE_TODO_BY_PHASE}/count-todo-by-due-date-phase?projectId=${encodeURIComponent(
        idProject
      )}&statusTodo=${encodeURIComponent(statusTodo)}&phaseId=${encodeURIComponent(idPhase)}`
    })
  }

  // Thống kê Todo không có Due Date theo Phase
  static fetchAllDataDashboardNoDueDatePhase(idProject: string, idPhase: string) {
    return request({
      method: 'GET',
      url: `${PREFIX_API_MANAGE_TODO_BY_PHASE}/count-todo-by-no-due-date-phase?projectId=${encodeURIComponent(
        idProject
      )}&phaseId=${encodeURIComponent(idPhase)}`
    })
  }

  // Thống kê Todo theo thành viên của từng Phase
  static fetchAllDataDashboardMemberPhase(idProject: string, idPhase: string) {
    return request({
      method: 'GET',
      url: `${PREFIX_API_MANAGE_TODO_BY_PHASE}/count-todo-by-member-phase?projectId=${encodeURIComponent(
        idProject
      )}&phaseId=${encodeURIComponent(idPhase)}`
    })
  }

  // Thống kê Todo không có thành viên theo Phase
  static fetchAllDataDashboardNoMemberPhase(idProject: string, idPhase: string) {
    return request({
      method: 'GET',
      url: `${PREFIX_API_MANAGE_TODO_BY_PHASE}/count-todo-by-no-member-phase?projectId=${encodeURIComponent(
        idProject
      )}&phaseId=${encodeURIComponent(idPhase)}`
    })
  }

  // Thống kê Todo theo nhãn của từng Phase
  static fetchAllDataDashboardLabelPhase(idProject: string, idPhase: string) {
    return request({
      method: 'GET',
      url: `${PREFIX_API_MANAGE_TODO_BY_PHASE}/count-todo-by-label-phase?projectId=${encodeURIComponent(
        idProject
      )}&phaseId=${encodeURIComponent(idPhase)}`
    })
  }

  // Thống kê Todo không có nhãn theo Phase
  static fetchAllDataDashboardNoLabelPhase(idProject: string, idPhase: string) {
    return request({
      method: 'GET',
      url: `${PREFIX_API_MANAGE_TODO_BY_PHASE}/count-todo-by-no-label-phase?projectId=${encodeURIComponent(
        idProject
      )}&phaseId=${encodeURIComponent(idPhase)}`
    })
  }

  // Lấy chi tiết của Phase
  static detailPhase(idPhase: string) {
    return request({
      method: 'GET',
      url: `${PREFIX_API_MANAGE_TODO_BY_PHASE}/detail/${encodeURIComponent(idPhase)}`
    })
  }
}
