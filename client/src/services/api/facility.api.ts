import { API_ADMIN_FACILITY } from "@/constants/url";
import request from "@/services/request";
import {DefaultResponse, PaginationParams, PaginationResponse, ResponseList} from "@/types/api.common"
import { AxiosResponse } from "axios";

export interface ParamsGetFacility extends PaginationParams {
    q?: string | '',
    facilityName: string,
    facilityStatus?: string | undefined
}

export type FacilityResponse = ResponseList & {
    facilityName: string,
    facilityCode: string,
    facilityStatus: number,
}

export interface ADFacilityRequest{
    facilityName: string
}

export const getAllFacility = async (params: ParamsGetFacility) => {
    const res = (await request({
        url: `${API_ADMIN_FACILITY}`,
        method: 'GET',
        params: params
    })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<FacilityResponse>>>>
    return res.data
}

export const addFacility = async (data: ADFacilityRequest) => {
    const res = (await request({
      url: `${API_ADMIN_FACILITY}/add`,
      method: 'POST',
      data: data
    })) as AxiosResponse<DefaultResponse<FacilityResponse>>
  
    return res.data
}

export const updateFacility = async (data: ADFacilityRequest, id: string) => {
    const res = (await request({
        url: `${API_ADMIN_FACILITY}/update/${id}`,
        method: 'PUT',
        data: data
    })) as AxiosResponse<DefaultResponse<FacilityResponse>>

    return res.data
}

export const changeStatusFacility = async (id: string) => {
    const res = (await request({
        url: `${API_ADMIN_FACILITY}/${id}/change-status`,
        method: 'PUT',
    })) as AxiosResponse<DefaultResponse<FacilityResponse>>

    return res.data
}

export const getFacilityById = async (id: string) => {
    const res = (await request({
        url: `${API_ADMIN_FACILITY}/detail/${id}`,
        method: 'GET',
    })) as AxiosResponse<DefaultResponse<FacilityResponse>>

    return res.data
}