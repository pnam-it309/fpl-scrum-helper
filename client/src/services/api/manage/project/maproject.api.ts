import {
  PREFIX_API_MANAGE_PROJECT,
  PREFIX_API_MANAGE_PROJECT_DEPARTMENT_FACILITY
} from '@/constants/url'
import request from '@/services/request'
import {
  DefaultResponse,
  PaginationParams,
  PaginationResponse,
  ResponseList
} from '@/types/api.common'
import { AxiosResponse } from 'axios'
import { Role } from '../todolist/todolist.api'

export interface projectRequest extends PaginationParams {
  search: string
  status: string
  nameDepartment: string
  idUser: string
}

export type projectResponse = ResponseList & {
  id?: string
  orderNumber: number
  nameProject: string
  codeProject: string
  nameDepartment: string
  status?: string
  idFacility: string
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
  emailLogin: string,
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

export type historyImprort = ResponseList & {
  message: string;
  email: string;
  id: string;
  createdDate: number;
  role: Role | null;
};

export type ProjectDetailSummaryResponse = {
  id: string
  name: string
  code: string
  nameDepartment: string
  startTime: number
  endTime: number
  status: string
  createdDateProject: number
  amountOfMembers: number,
  actualEndTime: number,
  category: string,
}

export type AmountPhaseByStatus = {
  status: string
  amount: number
}

export type AmountMembers = {
  member: 'STAFF' | 'STUDENT'
  amount: number
}

export type AmountTodoByPhase = {
  namePhase: string
  amount: number
}

export interface RestartProjectRequest {
  completionDate: number ,
      emailLogin: string,
}

export const getAllProject = async (params: projectRequest) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_PROJECT}`,
    method: 'GET',
    params: params
  })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<projectResponse>>>>

  return res.data
}

export const getAllPJDepartFacility = async (idFacility: string) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_PROJECT_DEPARTMENT_FACILITY}/${idFacility}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<pjDepartmentFacilityResponse>>
  return res.data
}

export const getAllFacilityProject = async () => {
  const res = (await request({
    url: `manage/project/facility`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<facilityResponse>>
  return res.data
}

export const detailProject = async (id: string) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_PROJECT}/${id}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<projectDetailResponse>>

  return res.data
}

export const updateProject = async (id: string, data: createProjectRequest) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_PROJECT}/${id}`,
    method: 'PUT',
    data: data
  })) as AxiosResponse<DefaultResponse<projectResponse>>

  return res.data
}

export const getDetailSummaryProject = async (id: string) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_PROJECT}/summary/${id}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<ProjectDetailSummaryResponse>>

  return res.data
}

export const finishEarlyProject = async (id: string,emailLogin:string) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_PROJECT}/finish-early/${id}/${emailLogin}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<projectResponse>>

  return res.data
}

export const getAmountOfPhasesByStatus = async (id: string) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_PROJECT}/amount-of-phase-by-status/${id}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<Array<AmountPhaseByStatus>>>

  return res.data
}

export const getAmountOfTodoByPhase = async (id: string) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_PROJECT}/amount-of-todo-by-phase/${id}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<Array<AmountTodoByPhase>>>

  return res.data
}

export const restartProject = async (idProject: string, data: RestartProjectRequest) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_PROJECT}/restart-project/${idProject}`,
    method: 'PUT',
    data: data
  })) as AxiosResponse<DefaultResponse<null>>

  return res.data;
}

export const dowloadExcel = async () => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_PROJECT}/template`,
    method: 'GET',
    responseType: 'blob'
  })) as AxiosResponse<Blob>

  return res.data
}

export const importReport = async (data: FormData, idProject: string) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_PROJECT}/file/upload/${idProject}`,
    method: 'POST',
    data: data
  })) as AxiosResponse<DefaultResponse<string>>

  return res.data
}



export const getAllHistory = async () => {
  const res = (await request({
    url: `/manage/report/history`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<historyImprort>>

  return res.data
}

export const downloadCSV = async () => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_PROJECT}/download-csv`,
    method: 'GET',
    responseType: 'blob',
  })) as AxiosResponse<Blob>

  return res.data
}