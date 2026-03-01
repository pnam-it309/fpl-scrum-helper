import {
  PREFIX_API_MANAGE_REPORT,
  PREFIX_API_MANAGE_STAGE,
  PREFIX_API_MANAGE_STAGE_TAKE_PLACE,
  PREFIX_API_MANAGE_COUNT_TODO_VOTED_BY_PRIORITY_LEVEL,
  PREFIX_API_MANAGE_FIND_ALL_VOTED_TODOS,
  PREFIX_API_MANAGE_STAGE_DETAIL
} from '@/constants/url'
import request from '@/services/request'
import { DefaultResponse, PaginationParams, PaginationResponse } from '@/types/api.common'
import { AxiosResponse } from 'axios'

export interface stageRequest {
  idStage?: string
  idProject: string
  startTime: number
  endTime: number
  idPhase: string
}

export interface stageVoteRequest extends PaginationParams {
  idProject: string
}

export type stageTakePlace = {
  startTime: number
  endTime: number
  idStage: string
  idPhase: string
  name: string
}

export type detailStage = {}

export type todoVoteStage = {
  name: string
  level: string
}

export type todoVoteLevelStatistics = {
  level: number
  total: number
}

export type voteTodo = {
  name: string
  memberName: string
  memberImage: string
  level: number
}

export const getVoteTodo = async (idProject: string) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_FIND_ALL_VOTED_TODOS}/${idProject}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<Array<voteTodo>>>

  return res.data
}

export const getTodoVoteLevelStatistics = async (idProject: string) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_COUNT_TODO_VOTED_BY_PRIORITY_LEVEL}/${idProject}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<Array<todoVoteLevelStatistics>>>

  return res.data
}

export const createUpdate = async (data: stageRequest) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_STAGE}`,
    method: 'POST',
    data: data
  })) as AxiosResponse<DefaultResponse<null>>

  return res.data
}

export const getAllStage = async (params: stageVoteRequest) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_STAGE}`,
    method: 'GET',
    params: params
  })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<stageTakePlace>>>>

  return res.data
}

export const detailStage = async (idStage: string) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_STAGE_DETAIL}/${idStage}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<stageTakePlace>>

  return res.data
}

export const deleteStage = async (idStage: string) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_STAGE}/${idStage}`,
    method: 'DELETE'
  })) as AxiosResponse<DefaultResponse<null>>

  return res.data
}

export const stageTakePlace = async (id: string) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_STAGE_TAKE_PLACE}/${id}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<Array<stageTakePlace>>>
  console.log(id)
  return res.data
}

export const findAllStageVote = async (id: string) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_STAGE}/${id}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<Array<todoVoteStage>>>

  return res.data
}

// export const createUpdate = async (data: any) => () => {}
