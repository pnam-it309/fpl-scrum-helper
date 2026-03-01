import { PREFIX_API_MANAGE_USER } from "@/constants/url";
import request from "@/services/request";
import {DefaultResponse, PaginationParams, PaginationResponse, ResponseList} from "@/types/api.common"
import { AxiosResponse } from "axios";

export interface ParamsGetUser extends PaginationParams {
    q?: string | '',
    search: string | null,
}

export type UserResponse = ResponseList & {
    name: string,
    role: string,
    code: string,
    email: string,
    countTodo: number,
    codeProject: string,
    idStaffProject: string,
    countCompletedTodo: number,
    progressPercentage: number,
    countTodoByPhase: number
}

export interface createUpdateUsertRequest {
  listMembers: {
    name: string
    code: string
    email: string
    image?: string
    role: string
  }[],
}


export const getListUser = async (params: ParamsGetUser, idProject: string, idPhase: string) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_USER}/${idProject}/${idPhase}`,
    method: 'GET',
    params
  })) as AxiosResponse<DefaultResponse<PaginationResponse<UserResponse[]>>>
  return res.data
}


export const updateUserProject = async (id: string, data: createUpdateUsertRequest) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_USER}/${id}`,
    method: 'PUT',
    data: data
  })) as AxiosResponse<DefaultResponse<null>>

  return res.data
}

export const updateStatusUser = async (id: string) => {
    const res = (await request({
        url: `${PREFIX_API_MANAGE_USER}/delete-user/${id}`,
        method: 'PUT',
    })) as AxiosResponse<DefaultResponse<UserResponse[]>>

    return res.data
}

export const getFacilityByProjectId = async (idProject: string) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_USER}/facility/${idProject}`,
    method: 'GET',
  })) as AxiosResponse<DefaultResponse<string>>;

  return res.data;
}
