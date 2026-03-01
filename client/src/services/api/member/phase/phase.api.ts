import { PREFIX_API_PHASE_MEMBER } from "@/constants/url";
import request from "@/services/request";
import {DefaultResponse, PaginationParams, PaginationResponse, ResponseList} from "@/types/api.common"
import { AxiosResponse } from "axios";

export interface ParamsGetMBPhase extends PaginationParams {
    q?: string | '',
    search: string | null
}

export type MBPhaseResponse = ResponseList & {
    name: string,
    id: string,
    code: string,
    descriptions: string,
    orderNumber: string,
    statusPhase: string,
    createDate: string,
    startTime: string,
    endTime: string
}

export interface MBPhaseRequest{

}

export type TodoProjecMemBertResponse = ResponseList & {
    createdDate: string,  
    email:string,  
    idTodo: string,  
    priorityLevel: string,  
    statusTodo: string,  
    nameTodo: string,  
    picture: string,  
    lastModifiedDate: string,  
    orderNumber: string,  
    descriptionsTodo: string,  
    namePhaseProject: string,
    phaseId: string
}

export type TodoProjectResponseMember = ResponseList & {
    createdDate: string;
    email: string;
    idTodo: string;
    priorityLevel: string;
    deadline: string;
    statusTodo: string;
    nameTodo: string;
    picture: string;
    lastModifiedDate: string;
    orderNumber: string;
    descriptionsTodo: string;
    namePhaseProject: string;
    phaseId: string;
    projectId: string;
    students: Student[];
    staff: Staff[];
  };
  
  export type Student = {
    id: string;
    name: string;
    email: string;
    image: string;
    createdDate?: string | null;
    lastModifiedDate?: string | null;
    status?: string | null;
    code?: string | null;
    majorFacility?: string | null;
  };
  
  export type Staff = {
    id: string;
    name: string;
    emailFe?: string | null;
    emailFpt?: string | null;
    picture: string;
    createdDate?: string | null;
    lastModifiedDate?: string | null;
    status?: string | null;
    code?: string | null;
  };
  
  export type PhaseProjectResponseMember = ResponseList & {
    idPhase: string,
    namePhase: string
  };

export const getAllPhase = async (params: ParamsGetMBPhase,id: string) => {
    const res = (await request({
        url: `${PREFIX_API_PHASE_MEMBER}/${id}`,
        method: 'GET',
        params: params
    })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<MBPhaseResponse>>>>
    return res.data
}

export const detailPhase = async (idProject: string) => {
    const res = (await request({
      url: `${PREFIX_API_PHASE_MEMBER}/detail/${idProject}`,
      method: 'GET'
    })) as AxiosResponse<DefaultResponse<MBPhaseResponse>>
  
    return res.data
}

export const getAllTodoProjectMember = async (params: ParamsGetMBPhase, id: string) => {
    const res = (await request({
        url: `${PREFIX_API_PHASE_MEMBER}/get-all-todo/${id}`,
        method: 'GET',
        params: params
    })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<TodoProjecMemBertResponse>>>>
    return res.data
}

export const getAllTodoProject = async (params: ParamsGetMBPhase, id: string) => {
    const res = (await request({
        url: `${PREFIX_API_PHASE_MEMBER}/get-all-todo/${id}`,
        method: 'GET',
        params: params
    })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<TodoProjectResponseMember>>>>
    return res.data
  }
  
  export const getAllPhaseMa = async (id: string) => {
    const res = (await request({
        url: `${PREFIX_API_PHASE_MEMBER}/get-all-phase/${id}`,
        method: 'GET'
    })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<PhaseProjectResponseMember>>>>
    return res.data
  }
  
  export const updateTodoPhaseProject = async (idPhase:string, idTodo: string, idProject: string) => {
    const res = (await request({
      url: `${PREFIX_API_PHASE_MEMBER}/update-phase-todo/${idTodo}/${idPhase}/${idProject}`,
      method: 'PUT'
    })) as AxiosResponse<DefaultResponse<TodoProjectResponseMember>>
  
    return res.data
  }