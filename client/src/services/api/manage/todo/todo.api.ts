import {
  PREFIX_API_MANAGE_STATISTICS_TODO_STAFF_PROJECT,
  PREFIX_API_MANAGE_TODO,
  PREFIX_API_MANAGE_TODO_BY_PHASE,
  PREFIX_API_MANAGE_TODO_STAFF_PROJECT,
  PREFIX_API_MANAGE_USER_BY_TODO
} from '@/constants/url'
import request from '@/services/request'
import {
  DefaultResponse,
  PaginationParams,
  PaginationResponse,
  ResponseList
} from '@/types/api.common'
import { AxiosResponse } from 'axios'

export interface todoRequest extends PaginationParams {
  idProject?: string
  search?: string
}

export type todoResponse = ResponseList & {
  name: string
  code: string
  createDate: string
  progress: string
  priorityLevel: string
  type: string
  point: string
}

export type todoResponseStatistics = todoResponse & {
  status: string
  level: string
}

export type ResponseFetchDataToDoProject = todoResponseStatistics & {
  statusTodo: string
  idPhaseTodo: string
}

export interface dataCreateTodo {
  name: string
  code: string
}

export type statisticsTodoStaffProject = {
  nameStaff: string
  nameStudent: string
  countTodo: string
  imageStaff: string
  imageStudent: string
}

export type paramStatistic = {
  idProject: string
  idPhase: null
}

export type AmountTodoByStatusTodo = {
  todoStatus: string
  amount: number
}

export const getAllTodo = async (params: todoRequest) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_TODO}`,
    method: 'GET',
    params: params
  })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<todoResponse>>>>
  return res.data
}

export const fetchDataTodoByProject = async (id: string) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_TODO}/${id}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<ResponseFetchDataToDoProject>>>>
  return res.data
}

export const getAllTodoStatistics = async (params: paramStatistic) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_TODO}/statistics`,
    method: 'GET',
    params: params
  })) as AxiosResponse<DefaultResponse<Array<todoResponseStatistics>>>
  return res.data
}

export const getAllTodoStatisticsByStaffProject = async (params: paramStatistic) => {
  const res = (await request({
    url: `manage/todo/statistics/count-todo-staffproject`,
    method: 'GET',
    params: params
  })) as AxiosResponse<DefaultResponse<Array<statisticsTodoStaffProject>>>
  return res.data
}

export const getAllUserByTodo = async (params: todoRequest, id: string) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_USER_BY_TODO}/${id}`,
    method: 'GET',
    params: params
  })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<todoResponse>>>>
  return res.data
}

export const getAllTodoByPhase = async (params: todoRequest, id: string) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_TODO_BY_PHASE}/${id}`,
    method: 'GET',
    params: params
  })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<todoResponse>>>>
  return res.data
}

export const getAllStaffProject = async (id: string) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_TODO_STAFF_PROJECT}/${id}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<todoResponse>>>>
  return res.data
}

export const countTodoByTodoStatus = async (id: string) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_TODO}/count-todo-by-todo-status/${id}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<Array<AmountTodoByStatusTodo>>>

  return res.data
}
