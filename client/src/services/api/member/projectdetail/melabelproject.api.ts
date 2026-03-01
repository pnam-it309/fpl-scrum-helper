import { PREFIX_API_LABEL_PROJECT_DETAILS } from "@/constants/url";
import request from "@/services/request";
import { DefaultResponse } from "@/types/api.common";
import { AxiosResponse } from "axios";

export interface ParamsGetMyFilterTodoDetailsModal  {
    idTodo: string ;
}

export interface MBMeLabelResponse {
    id:string
    code :string
    name:string
    colorLabel:string
    isChecked:boolean
}

export const getAllLabelByIdTodo = async (idTodo: string) => {
    const res = (await request({
      url: `${PREFIX_API_LABEL_PROJECT_DETAILS}?idTodo=${idTodo}`, 
      method: "GET"
    })) as AxiosResponse<DefaultResponse<MBMeLabelResponse[]>>;
    return res.data;
  };
  export interface MBLabelProjectRequest {
    idProject: string;
    idTodo: string;

}
export interface MBLabelProjectSearchRequest {
  idProject: string;
}
export const getAllLabelByIdProject = async (params: MBLabelProjectRequest) => {
    const res = (await request({
      url: `${PREFIX_API_LABEL_PROJECT_DETAILS}/list`,
      method: "GET",
      params: params
    })) as AxiosResponse<DefaultResponse<MBMeLabelResponse[]>>;
    return res.data;
  };
  
  export const getAllLabelSearchByIdProject = async (params: MBLabelProjectSearchRequest) => {
    const res = (await request({
      url: `${PREFIX_API_LABEL_PROJECT_DETAILS}/list-search`,
      method: "GET",
      params: params
    })) as AxiosResponse<DefaultResponse<MBMeLabelResponse[]>>;
    return res.data;
  }; 



