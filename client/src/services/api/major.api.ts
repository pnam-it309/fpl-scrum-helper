import { API_ADMIN_MAJOR } from "@/constants/url";
import request from "@/services/request";
import {DefaultResponse, PaginationParams, PaginationResponse, ResponseList} from "@/types/api.common"
import { AxiosResponse } from "axios";

export interface ParamsGetMajor extends PaginationParams {
    q?: string | '',
    majorName: string | null,
}

export type MajorResponse = ResponseList & {
    majorCode: string,
    majorName: string,
    majorStatus: string,
}

export interface ADMajorRequest{
    majorId?: string,
    majorName: string,
    majorCode: string,
    majorStatus: string,
    createdDate: string,
    departmentId: string 
}

export const getMajors = async (params: ParamsGetMajor, departmentId: string) => {
    const res = (await request({
        url: `${API_ADMIN_MAJOR}/get-all-major/${departmentId}`,
        method: 'GET',
        params: params
    })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<MajorResponse>>>>
    return res.data
}

export const addMajor = async (data: ADMajorRequest) => {
    const res = (await request({
      url: `${API_ADMIN_MAJOR}/add-major`,
      method: 'POST',
      data: data
    })) as AxiosResponse<DefaultResponse<MajorResponse>>
  
    return res.data
}

export const updateMajor = async (data: ADMajorRequest, id: string) => {
    const res = (await request({
        url: `${API_ADMIN_MAJOR}/update-major/${id}`,
        method: 'PUT',
        data: data
    })) as AxiosResponse<DefaultResponse<MajorResponse>>

    return res.data
}

export const updateStatusMajor = async (id: string) => {
    const res = (await request({
        url: `${API_ADMIN_MAJOR}/delete-major/${id}`,
        method: 'PUT',
    })) as AxiosResponse<DefaultResponse<MajorResponse>>

    return res.data
}

export const getMajor = async (id: string) => {
    const res = (await request({
        url: `${API_ADMIN_MAJOR}/detail-major/${id}`,
        method: 'GET',
    })) as AxiosResponse<DefaultResponse<MajorResponse>>

    return res.data
}