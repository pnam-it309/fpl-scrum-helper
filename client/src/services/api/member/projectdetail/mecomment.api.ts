import { PREFIX_API_COMMENT_PROJECT_DETAILS } from "@/constants/url";
import request from "@/services/request";
import {DefaultResponse, PaginationParams, PaginationResponse} from "@/types/api.common"
import { AxiosResponse } from "axios";

export interface ParamsGetCategory extends PaginationParams {
    idTodo: string
}

export interface MBMeFindTagMemberRequest  {
    idProject: string
    email:string
}

export interface MBMeCommentResponse {
  id:string;
  content: string;
  todoId: string;
  projectId: string;
  staffProjectId: string;
  statusEdit: number;
  createdDate: number;

  userId: string;
  userCode: string;
  userName: string;
  userEmail: string;
  userImage: string;
}

export interface MBMeUserInProjectResponse {
    userId: string;
    userCode: string;
    userName: string;
    userEmail: string;
    userImage: string;
  }
  


export const getAllCommentByIdTodo = async (params: ParamsGetCategory) => {
    const res = (await request({
        url: `${PREFIX_API_COMMENT_PROJECT_DETAILS}`,
        method: 'GET',
        params: params
    })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<MBMeCommentResponse>>>>
    return res.data
}

export const getAllMemberInProject = async (params: MBMeFindTagMemberRequest) => {
    const res = (await request({
        url: `${PREFIX_API_COMMENT_PROJECT_DETAILS}/tag-member`,
        method: 'GET',
        params: params
    })) as AxiosResponse<DefaultResponse<Array<MBMeUserInProjectResponse>>>
    return res.data
}

