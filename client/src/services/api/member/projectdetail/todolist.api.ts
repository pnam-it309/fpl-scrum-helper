export interface MBTodoListResponse {
  id: string
  code: string
  name: string
  describe: string
  indexTodoList: number
  phaseProjectId: string
}

export interface ParamsGetTodoListByPhaseProject {
  idPhaseProject: string
}

export interface ProjectViewResponse {
  id: string
  code: string
  name: string
  startTime: number
  endTime: number
  actualEndDate: number
  descriptions: string
  progress: number
  backgroundColor: string
  backgroundImage: string
  statusProject: number
  phaseProjectName:string 
}


import { PREFIX_API_TODO_LIST_PROJECT_DETAILS } from '@/constants/url'
import request from '@/services/request'
import { AxiosResponse } from 'axios'
import { DefaultResponse } from '@/types/api.common'

export const getTodoListByPhaseProject = async (
  params: ParamsGetTodoListByPhaseProject
) => {
  const res = (await request({
    url: `${PREFIX_API_TODO_LIST_PROJECT_DETAILS}/all-todolist/by-id-phase-project`,
    method: 'GET',
    params: params
  })) as AxiosResponse<DefaultResponse<MBTodoListResponse[]>>

  return res.data
}

export const getProjectByPhaseProjectId = async (idPhaseProject: string) => {
  const res = (await request({
    url: `${PREFIX_API_TODO_LIST_PROJECT_DETAILS}/details-project/by-id-phase-project`,
    method: 'GET',
    params: { idPhaseProject }
  })) as AxiosResponse<DefaultResponse<ProjectViewResponse>>

  return res.data
}