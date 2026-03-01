import {
  PREFIX_API_ADMIN_PROJECT,
  PREFIX_API_ADMIN_PROJECT_DEPARTMENT_FACILITY,
  PREFIX_API_ADMIN_STUDENT_DOWNLOAD_TEMPLATE,
  PREFIX_API_ADMIN_PROJECT_STATISTICS,
  PREFIX_API_ADMIN_PROJECT_PARTICIPANTS_STATISTICS,
  PREFIX_API_ADMIN_PROJECT_TODO_COUNTS,
  PREFIX_API_ADMIN_PROJECT_TOTAL_PROJECTS,
  PREFIX_API_ADMIN_PROJECT_BY_FACILITY,
  PREFIX_API_ADMIN_PROJECT_BY_DEPARTMENT,
  PREFIX_API_ADMIN_PROJECT_BY_TODO_STATUS
} from '@/constants/url'
import request from '@/services/request'
import {
  DefaultResponse,
  PaginationParams,
  PaginationResponse,
  ResponseList
} from '@/types/api.common'
import { Axios, AxiosResponse } from 'axios'

export interface projectRequest extends PaginationParams {
  search: string
  status: string
  nameDepartment: string
}

export type projectResponse = ResponseList & {
  id?: string
  orderNumber: number
  nameProject: string
  codeProject: string
  nameDepartment: string
  status?: string
  idFacility: string
  startTime: string
  endTime: string
}

export type projectDetailResponse = {
  name: string
  code: string
  endTime: string
  startTime: string
  progress: number
  descriptions: string
  idCategory: string
  idMajorFacility: string
}

export interface createProjectRequest {
  nameProject: string
  codeProject: string
  idMajorFacility: string
  descriptions?: string
  startTime: string
  endTime: string
  idCategory: string
  listMembers: {
    name: string
    code: string
    email: string
    image?: string
    role: string
  }[],
  emailLogin: string
}

export type pjDepartmentFacilityResponse = {
  nameDepartment: string
  nameFacility: string
  id: string
  nameMajor: string
}

export type facilityResponse = {
  id: string
  name: string
}

export type ProjectStatisticsResponse = {
  completed: number
  in_progress: number
  not_started: number
}

export type ProjectParticipantsResponse = {
  projectName: string
  totalParticipants: number
}

export type ProjectTodoCounts = {
  projectName: string
  totalTodos: number
}

export type OverView = {
  totalProjects: 0
  inProgress: 0
  done: 0
  paused: 0
  overdue: 0
}

export type ProjectByFacility = {
  facilityName: string
  total: number
}

export type ProjectByDepartment = {
  departmentName: string
  total: number
}

export type ProjectByTodoStatus = {
  projectName: string
  projectStatus: number
  toTalTodo: number
}

export const getProjectByTodoStatus = async () => {
  const res = (await request({
    url: PREFIX_API_ADMIN_PROJECT_BY_TODO_STATUS,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<ProjectByTodoStatus[]>>

  return res.data
}

export const getProjectByDepartment = async () => {
  const res = (await request({
    url: PREFIX_API_ADMIN_PROJECT_BY_DEPARTMENT,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<ProjectByDepartment[]>>

  return res.data
}

export const getProjectByFacility = async () => {
  const res = (await request({
    url: PREFIX_API_ADMIN_PROJECT_BY_FACILITY,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<ProjectByFacility[]>>

  return res.data
}

export const getTotalProjects = async () => {
  const res = (await request({
    url: PREFIX_API_ADMIN_PROJECT_TOTAL_PROJECTS,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<OverView>>

  return res.data
}

export const getProjectTodoCounts = async () => {
  const res = (await request({
    url: PREFIX_API_ADMIN_PROJECT_TODO_COUNTS,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<ProjectTodoCounts[]>>

  return res.data
}

export const getProjectParticipantsStatistics = async () => {
  const res = (await request({
    url: PREFIX_API_ADMIN_PROJECT_PARTICIPANTS_STATISTICS,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<ProjectParticipantsResponse[]>>

  return res.data
}

export const getProjectStatistics = async () => {
  const res = (await request({
    url: PREFIX_API_ADMIN_PROJECT_STATISTICS,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<ProjectStatisticsResponse>>

  return res.data
}

export const getAllProject = async (params: projectRequest) => {
  const res = (await request({
    url: `${PREFIX_API_ADMIN_PROJECT}`,
    method: 'GET',
    params: params
  })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<projectResponse>>>>

  return res.data
}

export const getAllPJDepartFacility = async (idFacility: string) => {
  const res = (await request({
    url: `${PREFIX_API_ADMIN_PROJECT_DEPARTMENT_FACILITY}/${idFacility}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<pjDepartmentFacilityResponse>>
  return res.data
}

export const getAllFacilityProject = async () => {
  const res = (await request({
    url: `/admin/project/facility`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<facilityResponse>>
  return res.data
}

export const detailProject = async (id: string) => {
  const res = (await request({
    url: `${PREFIX_API_ADMIN_PROJECT}/${id}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<projectDetailResponse>>

  return res.data
}

export const createProject = async (data: createProjectRequest) => {
  const res = (await request({
    url: `${PREFIX_API_ADMIN_PROJECT}`,

    method: 'POST',
    data: data
  })) as AxiosResponse<DefaultResponse<projectResponse>>

  return res.data
}

export const updateProject = async (id: string, data: createProjectRequest) => {
  const res = (await request({
    url: `${PREFIX_API_ADMIN_PROJECT}/${id}`,
    method: 'PUT',
    data: data
  })) as AxiosResponse<DefaultResponse<projectResponse>>

  return res.data
}

export const deleteProject = async (id: string) => {
  const res = (await request({
    url: `${PREFIX_API_ADMIN_PROJECT}/${id}`,
    method: 'DELETE'
  })) as AxiosResponse<DefaultResponse<projectResponse>>

  return res.data
}

export const downloadExcelStudent = async () => {
  const res = (await request({
    url: `${PREFIX_API_ADMIN_STUDENT_DOWNLOAD_TEMPLATE}`,
    method: 'GET',
    responseType: 'blob'
  })) as AxiosResponse<Blob>

  return res.data
}

export const importStudent = async (data: FormData) => {
  const res = (await request({
    url: `/admin/student/file/upload`,
    method: 'POST',
    data: data
  })) as AxiosResponse<DefaultResponse<string>>
  return res.data
}
