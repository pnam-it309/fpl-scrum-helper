import { PREFIX_API_MYPROJECT_MEMBER, API_MEMBER_REPORT } from "@/constants/url";
import request from "@/services/request";
import {DefaultResponse, PaginationParams, PaginationResponse, ResponseList} from "@/types/api.common"
import { AxiosResponse } from "axios";

export interface ParamsGetTodoProject extends PaginationParams {
    q?: string | '',
    name: string | null
}

export interface ParamsGetReport extends PaginationParams {
    q?: string | '',
    reportTime: number | null
}

export type TodoProjectResponse = ResponseList & {
    orderNumber: number,
    createdDate: number,
    nameTodo: string,
    projectId: string,
    priorityLevel: number,
    startTime: number,
    endTime: number
}

export type ReportResponse = ResponseList & {
    idReport: string
    createDate: number
    reportTime: number
    help: string
    statusReport: string
    obstaclesReport: string
    wordPlanTomorrowReport: string
    wordDoneTodayReport: string
    idProject: string
    sourceType: string
}

export type ReportRateResponse = ResponseList & {
  totalPhaseDays: number,
  holidayCount: number,
  reportCount: number,
  reportRate:number
}

export interface ReportRequest{
    workDoneToday: string
    obstacles: string
    workPlanTomorrow: string
    help: boolean
    statusReport: string
    reportTimem: number
    idTodo: string
}

export interface ExportReportMemberRequest{
    workDone: string
    obstacles: string
    workPlan: string
    reportTime: number
}

export interface UpdateReportSettingRequest {
  stopReportHour: number
}

export interface ReportSettingResponse {
  id: string
  stopReportHour: number
}


export const getAllReport = async ( params: ParamsGetReport,id: string) => {
    const res = (await request({
        url: `${PREFIX_API_MYPROJECT_MEMBER}/report/${id}`,
        method: 'GET',
        params: params
    })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<ReportResponse>>>>
    return res.data
}

export const getReportRate = async (idProject: string, idPhase: string) => {
    const res = (await request({
        url: `${PREFIX_API_MYPROJECT_MEMBER}/report-rate/${idPhase}/${idProject}`,
        method: 'GET'
    })) as AxiosResponse<DefaultResponse<Array<ReportRateResponse>>>
    return res.data
}


export const getTodoByProject = async (params: ParamsGetTodoProject, id: string) => {
    const res = (await request({
        url: `${PREFIX_API_MYPROJECT_MEMBER}/todo/${id}`,
        method: 'GET',
        params: params
    })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<TodoProjectResponse>>>>
    return res.data
}

export const addReport= async (data: ReportRequest, idProject: string) => {
    const res = (await request({
        url: `${PREFIX_API_MYPROJECT_MEMBER}/add-report/${idProject}`,
        method: 'POST',
        data: data
    })) as AxiosResponse<DefaultResponse<ReportResponse>>

    return res.data
}

export const updateReport = async (data: ReportRequest, id: string, idProject: string) => {
    const res = (await request({
      url: `${PREFIX_API_MYPROJECT_MEMBER}/update-report/${id}/${idProject}`,
      method: 'PUT',
      data: data
    })) as AxiosResponse<DefaultResponse<ReportResponse>>
  
    return res.data
}

export const detailReport = async (id: string, idProject: string) => {
    const res = (await request({
      url: `${PREFIX_API_MYPROJECT_MEMBER}/detail-report/${idProject}/${id}`,
      method: 'GET'
    })) as AxiosResponse<DefaultResponse<ReportResponse>>
  
    return res.data
}

export const getReportIdByDate = async (reportTime: number, idProject: string) => {
    const response = await request({
      url: `${PREFIX_API_MYPROJECT_MEMBER}/report-by-date/${reportTime}/${idProject}`,
      method: "GET",
    });
  
    return response.data;
};

export const exportMemberReportDocx = async (projectId: string) => {
  const res = (await request({
    url: `${API_MEMBER_REPORT}/docx/${projectId}`,
    method: 'GET',
    responseType: 'blob',
  })) as AxiosResponse<DefaultResponse<string>>

  return res.data
}

export const exportMemberReportExcel = async (projectId: string) => {
  const res = (await request({
    url: `${API_MEMBER_REPORT}/export-excel/${projectId}`,
    method: 'GET',
    responseType: 'blob',
  })) as AxiosResponse<DefaultResponse<string>>

  return res.data
}

export const updateReportSetting = async (
  id: string,
  data: UpdateReportSettingRequest
) => {
  const res = (await request({
    url: `${PREFIX_API_MYPROJECT_MEMBER}/update-report-setting/${id}`,
    method: 'PUT',
    data: data,
  })) as AxiosResponse<DefaultResponse<ReportSettingResponse>>

  return res.data
}

export const getReportSetting = async (idProject: string) => {
  const res = (await request({
    url: `${PREFIX_API_MYPROJECT_MEMBER}/report-setting/${idProject}`,
    method: 'GET',
  })) as AxiosResponse<DefaultResponse<ReportSettingResponse>>

  return res.data
}
