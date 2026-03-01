import { API_ADMIN_DEPARTMENT } from "@/constants/url";
import request from "@/services/request";
import {DefaultResponse, PaginationParams, PaginationResponse, ResponseList} from "@/types/api.common"
import { AxiosResponse } from "axios";

export interface ParamsGetDepartment extends PaginationParams {
    q?: string | '',
    departmentSearch: string | null,
    departmentStatus?: string | undefined
}

export type DepartmentResponse = ResponseList & {
    departmentCode: string,
    departmentName: string,
    departmentStatus: string
}

export interface ADDepartmentRequest {
    departmentId?: string,
    departmentCode: string,
    departmentName: string,
    departmentStatus: string,
    createdDate: number
}

export const getDepartments = async (params: ParamsGetDepartment) => {
    const res = (await request({
        url: `${API_ADMIN_DEPARTMENT}/get-all-department`,
        method: 'GET',
        params: params
    })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<DepartmentResponse>>>>
    return res.data
}

export const getDepartment = async (id: string) => {
    const res = (await request({
      url: `${API_ADMIN_DEPARTMENT}/detail-department/${id}`,
      method: 'GET'
    })) as AxiosResponse<DefaultResponse<DepartmentResponse>>
  
    return res.data
}

export const addDepartment = async (data: ADDepartmentRequest) => {
    const res = (await request({
      url: `${API_ADMIN_DEPARTMENT}/add-department`,
      method: 'POST',
      data: data
    })) as AxiosResponse<DefaultResponse<DepartmentResponse>>
  
    return res.data
}

export const updateDepartment = async (data: ADDepartmentRequest, id: string) => {
  const res = (await request({
    url: `${API_ADMIN_DEPARTMENT}/update-department/${id}`,
    method: 'PUT',
    data: data
  })) as AxiosResponse<DefaultResponse<DepartmentResponse>>

  return res.data
}

export const updateStatusDepartment = async (id: string) => {
    const res = (await request({
        url: `${API_ADMIN_DEPARTMENT}/status-department/${id}`,
        method: 'PUT',
    })) as AxiosResponse<DefaultResponse<DepartmentResponse>>

    return res.data
}