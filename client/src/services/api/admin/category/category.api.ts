import { API_ADMIN_CATEGORY } from "@/constants/url";
import request from "@/services/request";
import {DefaultResponse, PaginationParams, PaginationResponse, ResponseList} from "@/types/api.common"
import { AxiosResponse } from "axios";

export interface ParamsGetCategory extends PaginationParams {
    q?: string | '',
    categoryName: string,
    categoryStatus?: string
}

export type CategoryResponse = ResponseList & {
    categoryName: string,
    categoryCode: string,
    categoryStatus: number,
}

export interface ADCategoryRequest{
    categoryName: string
}

export const getAllCategory = async (params: ParamsGetCategory) => {
    const res = (await request({
        url: `${API_ADMIN_CATEGORY}`,
        method: 'GET',
        params: params
    })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<CategoryResponse>>>>
    return res.data
}

export const createCategory = async (data: ADCategoryRequest) => {
    const res = (await request({
      url: `${API_ADMIN_CATEGORY}/add`,
      method: 'POST',
      data: data
    })) as AxiosResponse<DefaultResponse<CategoryResponse>>
  
    return res.data
}

export const updateCategory = async (data: ADCategoryRequest, id: string) => {
    const res = (await request({
        url: `${API_ADMIN_CATEGORY}/update/${id}`,
        method: 'PUT',
        data: data
    })) as AxiosResponse<DefaultResponse<CategoryResponse>>

    return res.data
}

export const changeStatusCategory = async (id: string) => {
    const res = (await request({
        url: `${API_ADMIN_CATEGORY}/${id}/change-status`,
        method: 'PUT',
    })) as AxiosResponse<DefaultResponse<CategoryResponse>>

    return res.data
}

export const getByIdCategory = async (id: string) => {
    const res = (await request({
        url: `${API_ADMIN_CATEGORY}/detail/${id}`,
        method: 'GET',
    })) as AxiosResponse<DefaultResponse<CategoryResponse>>

    return res.data
}

