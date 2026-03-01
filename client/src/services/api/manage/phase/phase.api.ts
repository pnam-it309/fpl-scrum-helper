import {
  PREFIX_API_MANAGE_PHASE_BY_STATISTICS,
  PREFIX_API_MANAGE_PHASE_SUCCESS,
  PREFIX_API_MANAGE_SPRINT
} from '@/constants/url'
import request from '@/services/request'
import {
  DefaultResponse,
  PaginationParams,
  PaginationResponse,
  ResponseList
} from '@/types/api.common'
import { AxiosResponse } from 'axios'

export interface phaseRequest extends PaginationParams {}

export interface dataCreatePhase {
  name: string
  code: string
  startTime?: string
  endTime?: string
}

export type phaseResponse = ResponseList & {
  name: string
  code: string
  createDate: string
  endTime: string
  startTime: string
  descriptions: string
  expanded?: any
  statusPhase?: string
}

export const dataPhase = async (params: phaseRequest) => {
  const res = (await request({
    url: '/manage/phase-project',
    method: 'GET',
    params: params
  })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<phaseResponse>>>>

  return res.data
}

export const getPhaseSuccess = async (id: string) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_PHASE_SUCCESS}/${id}}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<phaseResponse>>

  return res.data
}

export const getAllPhaseStatistics = async (idProject: string) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_PHASE_BY_STATISTICS}/${idProject}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<Array<phaseResponse>>>

  return res.data
}

export const dataSprintProject = async (params: phaseRequest) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_SPRINT}`,
    method: 'GET',
    params: params
  })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<phaseResponse>>>>

  return res.data
}
