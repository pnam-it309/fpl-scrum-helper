import { API_ADMIN_DEPARTMENT_FACILITY } from "@/constants/url";
import request from "@/services/request";
import {DefaultResponse, PaginationParams, PaginationResponse, ResponseList} from "@/types/api.common"
import { AxiosResponse } from "axios";

export interface ParamsGetDepartmentFacility extends PaginationParams {
    q?: string | '',
    facilityName: string
}

export type DepartmentFacilityResponse = ResponseList & {
    departmentFacilityId: string,
    facilityId: string,
    facilityName: string,
    departmentFacilityStatus: string,
    createdDate: number,
    facilityCode: string,
}

export interface ADDepartmentFacilityRequest {
    departmentId: string,
    facilityId: string,
    departmentFacilityId?: string
}

export const getDepartmentFacilitys = async (params: ParamsGetDepartmentFacility, id: string) => {
    const res = (await request({
        url: `${API_ADMIN_DEPARTMENT_FACILITY}/get-all-department-facility/${id}`,
        method: 'GET',
        params: params
    })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<DepartmentFacilityResponse>>>>
    return res.data
}

export const addDepartmentFacility = async (data: ADDepartmentFacilityRequest) => {
    const res = (await request({
        url: `${API_ADMIN_DEPARTMENT_FACILITY}/add-department-facility`,
        method: 'POST',
        data: data
    })) as AxiosResponse<DefaultResponse<DepartmentFacilityResponse>>

    return res.data
}

export const updateDepartmentFacility = async (data: ADDepartmentFacilityRequest, id: string) => {
    const res = (await request({
      url: `${API_ADMIN_DEPARTMENT_FACILITY}/update-department-facility/${id}`,
      method: 'PUT',
      data: data
    })) as AxiosResponse<DefaultResponse<DepartmentFacilityResponse>>
  
    return res.data
}

export const getDFListFacility = async () => {
    const res = (await request({
        url: `${API_ADMIN_DEPARTMENT_FACILITY}/get-list-facility`,
        method: 'GET',
    })) as AxiosResponse<DefaultResponse<Array<DepartmentFacilityResponse>>>
    return res.data
}

export const getDFDepartmentName = async (id: string) => {
    const res = (await request({
        url: `${API_ADMIN_DEPARTMENT_FACILITY}/get-department-name/${id}`,
        method: 'GET',
    })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<DepartmentFacilityResponse>>>>
    return res.data
}

export const getDepartmentFacility = async (id: string) => {
    const res = (await request({
      url: `${API_ADMIN_DEPARTMENT_FACILITY}/detail-department-facility/${id}`,
      method: 'GET'
    })) as AxiosResponse<DefaultResponse<DepartmentFacilityResponse>>
  
    return res.data
}

export const updateStatusDepartmentFacility = async (id: string) => {
    const res = (await request({
        url: `${API_ADMIN_DEPARTMENT_FACILITY}/status-departmentFacility/${id}`,
        method: 'PUT',
    })) as AxiosResponse<DefaultResponse<DepartmentFacilityResponse>>

    return res.data
}