import { PREFIX_API_MANAGE_FIND_ALL_REPORT, PREFIX_API_MANAGE_REPORT } from '@/constants/url'
import request from '@/services/request'
import {
  DefaultResponse,
  PaginationParams,
  PaginationResponse,
  ResponseList
} from '@/types/api.common'
import { AxiosResponse } from 'axios'

export interface reportRequest extends PaginationParams {
  time?: number
  idProject: string
}

export interface ExportRequest {
  idUser: String[]
}

export interface userRequest extends PaginationParams {
  idProject: string
}

export type reportResponse = ResponseList & {
  id?: string
  codeStaff: string
  codeStudent: string
  nameStaff: string
  nameStudent: string
  imageStudent: string
  idStaffProject: string
  imageStaff: string
  workDoneToday: string
  workPlanTomorrow: string
  obstacles: string
  reportTime: string
  orderNumber: number
}

export type userProjectResponse = {
  staffName: string
  codeStaff: string
  idStaff: string
  idStudent: string
  emailStaff: string
  codeStudent: string
  studentName: string
  emailStudent: string
}

export interface HolidayRequest{
    statusReport: string
    dates: number[]
}

export type HolidayResponse = ResponseList & {
    idHoliday: string
    createDate: number
    date: number
    statusReport: string
    idProject: string
}

export type DetailHolidayResponse = ResponseList & {
    date: number
}

export const getReport = async (params: reportRequest) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_REPORT}`,
    method: 'GET',
    params: params
  })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<reportResponse>>>>

  return res.data
}

export const findAllReport = async (params: reportRequest) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_FIND_ALL_REPORT}`,
    method: 'GET',
    params: params
  })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<reportResponse>>>>

  return res.data
}

export const exportReport = async (id: string, param: ExportRequest) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_REPORT}/export/${id}`,
    method: 'POST',
    responseType: 'blob',
    data: param
  })) as AxiosResponse<DefaultResponse<string>>

  return res.data
}

export const exportFileDoc = async (id: string, param: ExportRequest) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_REPORT}/docx/${id}`,
    method: 'POST',
    responseType: 'blob',
    data: param
  })) as AxiosResponse<DefaultResponse<string>>

  return res.data
}

export const userProject = async (data: userRequest) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_REPORT}/user-project`,
    method: 'GET',
    data: data
  })) as AxiosResponse<DefaultResponse<Array<userProjectResponse>>>

  return res.data
}

export const getAllReportCompensation = async () => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_REPORT}/report-compensation`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<number | null>>

  return res.data
}

export const addHoliday= async (data: HolidayRequest, idProject: string) => {
    const res = (await request({
        url: `${PREFIX_API_MANAGE_REPORT}/create-holiday/${idProject}`,
        method: 'POST',
        data: data
    })) as AxiosResponse<DefaultResponse<HolidayResponse>>

    return res.data
}

export const detailHoliday = async (idProject: string) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_REPORT}/detail-holiday/${idProject}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<DetailHolidayResponse>>

  return res.data
}

export const updateSatus= async (dates: number[], idProject: string) => {
    const res = (await request({
        url: `${PREFIX_API_MANAGE_REPORT}/update-holiday/${idProject}`,
        method: 'PUT',
        data: dates
    })) as AxiosResponse<DefaultResponse<HolidayResponse>>

    return res.data
}