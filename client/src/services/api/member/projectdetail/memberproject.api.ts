import { PREFIX_API_MEMBER_PROJECT_PROJECT_DETAILS } from "@/constants/url";
import request from "@/services/request";
import { DefaultResponse } from "@/types/api.common";
import { AxiosResponse } from "axios";

export interface ParamsGetMyFilterTodoDetailsModal  {
    idTodo: string ;
}

export interface MBMeMemberProjectResponse {
    id: string;
    name: string;
    email: string;
    image: string;
    isAdded:boolean;
  }

  export interface ParamscreateTodoChecklistTestRequest  {
    idTodo: string ;
    name:string;
}

export interface MBMeMemberProjectFilterResponse {
  id: string;
  name: string;
  email: string;
  image: string;
}

export interface ParamsjoinTodoVoteMemberProjectAPIRequest  {
  idMember: string ;
  projectId:string;
  nameMember:string
  email:string
  idTodoCreateOrDelete:string
  idUser:string
  idTodo:string
  idTodoList:string
}

export const getAllMemberProject = async (idProject: string, idTodo: string) => {
  const res = (await request({
    url: `${PREFIX_API_MEMBER_PROJECT_PROJECT_DETAILS}/${idProject}/${idTodo}`,
    method: "GET"
  })) as AxiosResponse<DefaultResponse<MBMeMemberProjectResponse[]>>;

  return res.data;
};

export const getAllFilterMemberProject = async (idProject: string) => {
  const res = (await request({
    url: `${PREFIX_API_MEMBER_PROJECT_PROJECT_DETAILS}/filter/${idProject}`,
    method: "GET"
  })) as AxiosResponse<DefaultResponse<MBMeMemberProjectFilterResponse[]>>;

  return res.data;
};