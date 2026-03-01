import { PREFIX_API_TODO_PROJECT_DETAILS } from '@/constants/url'
import request from '@/services/request'
import { DefaultResponse, ResponseList } from '@/types/api.common'
import { AxiosResponse } from 'axios'

export interface ParamsGetMyFilterTodo {
  idPhase: string
  projectId: string
  idTodoList?: string
  name: string
}

export interface ParamsGetAllTodoDetailsRequest {
  idTodo: string
}

export interface ParamsdeleteTodoChecklistTestRequest {
  idTodoChild: string
}

export interface ParamseditTodoChecklistTestRequest {
  idTodoChild: string
  name: string
}

export interface ParamscreateTodoChecklistTestRequest {
  idTodo: string
  name: string
}

export interface MBMeConvertLabelResponse {
  id: string
  code: string
  name: string
  colorLabel: string
}

export interface MBMeConvertMemberTodoResponse {
  id: string
  email: string
  name: string
  image: string
}

export interface MBMeTodoChild_Response {
  id: string
  code: string | null
  name: string
  status: number
  statusTodoChild: number
}

export interface MBMemberTodo_Response {
  id: string
  name: string
  email: string
  image: string
}

export interface MBImage_Response {
  id: string
  urlImage: string
  nameImage: string
  status: string
  todoId: string
  createdDate: string
}

export interface MBMeConvertTodoResponse {
  id: string
  code: string
  name: string
  priorityLevel: string
  deadline: number
  completionTime: number
  indexTodo: number
  progress: number
  imageId: string
  urlImage: string
  numberTodoComplete: number
  numberTodo: number
  progressOfTodo: number
  deadlineString: string
  todoListId: string
  typeTodo: string 
  labels: MBMeConvertLabelResponse[]
  memberTodo: MBMeConvertMemberTodoResponse[]
}

export interface MBMeAllDetailTodo {
  id: string
  code: string
  name: string
  descriptions: string | null
  deadline: number
  reminderTime: string | null
  statusReminder: number | null
  completionTime: number
  priorityLevel: number
  progress: number
  imageId: string | null
  urlImage: string | null
  indexTodo: number
  todoId: string | null
  idType: string | null
  todoListId: string
  todoListName:string 
  statusTodo: number
  type: string | null
  members: MBMemberTodo_Response[]
  labels: MBMeConvertLabelResponse[]
  listTodoChild: MBMeTodoChild_Response[]
  images: MBImage_Response[]
}

export interface MBMeBoardResponse extends ResponseList {
  id: string
  code: string
  name: string
  indexTodoList: number
  tasks: MBMeConvertTodoResponse[]
}

export const getBoard = async (params: ParamsGetMyFilterTodo) => {
  const res = (await request({
    url: `${PREFIX_API_TODO_PROJECT_DETAILS}/board`,
    method: 'GET',
    params: params
  })) as AxiosResponse<DefaultResponse<MBMeBoardResponse[]>>

  return res.data
}

export const getAllDetailTodo = async (params: ParamsGetAllTodoDetailsRequest) => {
  const res = (await request({
    url: `${PREFIX_API_TODO_PROJECT_DETAILS}/get-all-detail`,
    method: 'GET',
    params: params
  })) as AxiosResponse<DefaultResponse<MBMeAllDetailTodo>>

  return res.data
}

export const getAllTodo = async (params: ParamsGetMyFilterTodo) => {
  const res = (await request({
    url: `${PREFIX_API_TODO_PROJECT_DETAILS}/all`,
    method: 'GET',
    params: params
  })) as AxiosResponse<DefaultResponse<MBMeConvertTodoResponse[]>>

  return res.data
}