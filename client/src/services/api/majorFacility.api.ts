import { API_ADMIN_MAJOR_FACILITY } from "@/constants/url";
import request from "@/services/request";
import {DefaultResponse, PaginationParams, PaginationResponse, ResponseList} from "@/types/api.common"
import { AxiosResponse } from "axios";

export interface ParamsGetMajorFacility extends PaginationParams {
    q?: string | '',
    majorName: string | null,
}

export type MajorFacilityResponse = ResponseList & {
    majorFacilityId: string,
    majorName: string,
    majorId: string,
    majorCode: string,
    majorFacilityStatus: string
}

export interface ADMajorFacilityRequest{
    majorId: string,
    departmentId: string,
    departmentFacilityId: string
}

export const getMajorFacility = async (params: ParamsGetMajorFacility, id: string) => {
    const res = (await request({
        url: `${API_ADMIN_MAJOR_FACILITY}/get-all/${id}`,
        method: 'GET',
        params: params
    })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<MajorFacilityResponse>>>>
    return res.data
}

export const addMajorFacility = async (data: ADMajorFacilityRequest) => {
    const res = (await request({
      url: `${API_ADMIN_MAJOR_FACILITY}/add`,
      method: 'POST',
      data: data
    })) as AxiosResponse<DefaultResponse<MajorFacilityResponse>>
  
    return res.data
}

export const updateMajorFacility = async (data: ADMajorFacilityRequest, id: string) => {
    const res = (await request({
        url: `${API_ADMIN_MAJOR_FACILITY}/update/${id}`,
        method: 'PUT',
        data: data
    })) as AxiosResponse<DefaultResponse<MajorFacilityResponse>>

    return res.data
}

export const detailMajorFacility = async (id: string) => {
    const res = (await request({
        url: `${API_ADMIN_MAJOR_FACILITY}/${id}`,
        method: 'GET',
    })) as AxiosResponse<DefaultResponse<MajorFacilityResponse>>

    return res.data
}

export const getDFListMajorByDepartmentId = async (departmentId: string) => {
    const res = (await request({
        url: `${API_ADMIN_MAJOR_FACILITY}/major/${departmentId}`,
        method: 'GET',
    })) as AxiosResponse<DefaultResponse<Array<MajorFacilityResponse>>>
    return res.data
}

export const updateStatusMajorFacility = async (id: string) => {
    const res = (await request({
        url: `${API_ADMIN_MAJOR_FACILITY}/status-majorFacility/${id}`,
        method: 'PUT',
    })) as AxiosResponse<DefaultResponse<MajorFacilityResponse>>

    return res.data
}