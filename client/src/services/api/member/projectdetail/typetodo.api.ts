import { PREFIX_API_TYPE_TODO_PROJECT_DETAILS } from "@/constants/url";
import request from "@/services/request";
import {DefaultResponse, PaginationParams, PaginationResponse, ResponseList} from "@/types/api.common"
import { AxiosResponse } from "axios";

export interface MBTypeTodoRequest  {
    q?: string | '',
    typeName: string,
    typeStatus?: string
}

    export type TypeTodoResponse = ResponseList & {
        id: string,
        type: string,
        status: number,
    }

export interface ADCreateUpdateTypeTodoRequest{
    type: string
}

export const getAllTypeTodo = async () => {
    const res = (await request({
        url: `${PREFIX_API_TYPE_TODO_PROJECT_DETAILS}`,
        method: 'GET'
    })) as AxiosResponse<DefaultResponse<Array<TypeTodoResponse>>>
    return res.data
}

export const createTypeTodo = async (data: ADCreateUpdateTypeTodoRequest) => {
    const res = (await request({
      url: `${PREFIX_API_TYPE_TODO_PROJECT_DETAILS}/add`,
      method: 'POST',
      data: data
    })) as AxiosResponse<DefaultResponse<TypeTodoResponse>>
  
    return res.data
}

export const updateTypeTodo = async (data: MBTypeTodoRequest, id: string) => {
    const res = (await request({
        url: `${PREFIX_API_TYPE_TODO_PROJECT_DETAILS}/update/${id}`,
        method: 'PUT',
        data: data
    })) as AxiosResponse<DefaultResponse<TypeTodoResponse>>

    return res.data
}

export const changeStatusTypeTodo = async (id: string) => {
    const res = (await request({
        url: `${PREFIX_API_TYPE_TODO_PROJECT_DETAILS}/${id}/change-status`,
        method: 'PUT',
    })) as AxiosResponse<DefaultResponse<TypeTodoResponse>>

    return res.data
}

export const getByIdTypeTodo = async (id: string) => {
    const res = (await request({
        url: `${PREFIX_API_TYPE_TODO_PROJECT_DETAILS}/detail/${id}`,
        method: 'GET',
    })) as AxiosResponse<DefaultResponse<TypeTodoResponse>>

    return res.data
}

