import {
  PREFIX_API_ESTIMATE,
  PREFIX_API_ESTIMATE_PHASE,
  PREFIX_API_MYPROJECT_MEMBER
} from '@/constants/url'
import request from '@/services/request'
import {
  DefaultResponse,
  PaginationParams,
  PaginationResponse,
  ResponseList
} from '@/types/api.common'
import { AxiosResponse } from 'axios'

export interface ParamsGetEstimate extends PaginationParams {
  idProject: string
  idUser: string
}

export type estimateResponse = ResponseList & {
  name: string
  availableHours: string
  adjustedStoryPoints: string
  sprint: string
  startTime: string
  descriptions: string
  endTime: string
  idPhase: string
  idUser: string
  workday: string
}

export interface ParamsCreateEstimate {
  idProject: string
  idSprint: string
  idUser: string
  descriptions: string
  availableHours: number
  adjustedStoryPoints: number
}

export type phaseByEstimateResponse = {
  id: string
  name: string
  statusPhase: string
  startTime: string
  endTime: string
}

export const getAllEstimate = async (params: ParamsGetEstimate) => {
  const res = (await request({
    url: `${PREFIX_API_ESTIMATE}`,
    method: 'GET',
    params: params
  })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<estimateResponse>>>>
  return res.data
}

export const getAllPhaseInEstimate = async (id: string) => {
  const res = (await request({
    url: `${PREFIX_API_ESTIMATE_PHASE}/${id}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<Array<phaseByEstimateResponse>>>
  return res.data
}

export const createEstimate = async (data: ParamsCreateEstimate) => {
  const res = (await request({
    url: `${PREFIX_API_ESTIMATE}`,
    method: 'POST',
    data: data
  })) as AxiosResponse<DefaultResponse<Array<phaseByEstimateResponse>>>
  return res.data
}

export const detailEstimate = async (id: string) => {
  const res = (await request({
    url: `${PREFIX_API_ESTIMATE}/${id}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<estimateResponse>>
  return res.data
}

export const removeEstimate = async (id: string) => {
  const res = (await request({
    url: `${PREFIX_API_ESTIMATE}/${id}`,
    method: 'DELETE'
  })) as AxiosResponse<DefaultResponse<null>>
  return res.data
}

export const updateEstimate = async (id: string, data: ParamsCreateEstimate) => {
  const res = (await request({
    url: `${PREFIX_API_ESTIMATE}/${id}`,
    method: 'PUT',
    data: data
  })) as AxiosResponse<DefaultResponse<null>>
  return res.data
}

export const avgStoryPoint = async (id: string) => {
  const res = (await request({
    url: `${PREFIX_API_ESTIMATE}/velocity/${id}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<null>>
  return res.data
}

export const capacityEstimateBySprint = async (id: string) => {
  const res = (await request({
    url: `${PREFIX_API_ESTIMATE}/capacity-estimate/${id}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<null>>
  return res.data
}
