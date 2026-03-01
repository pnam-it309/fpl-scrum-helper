import { PREFIX_API_RESOURCE_PROJECT_DETAILS } from "@/constants/url";
import request from "@/services/request";
import {DefaultResponse, PaginationParams, PaginationResponse} from "@/types/api.common"
import { AxiosResponse } from "axios";


export interface ParamsGetResource extends PaginationParams {
    idTodo: string;
  }

export interface MBMeResourceResponse {
    id: string;
    name: string;
    url: string;
    todoId: string;
    createdDate: number;
  }
  

  export const getAllResourceTodo = async (params: ParamsGetResource) => {
    const res = (await request({
      url: `${PREFIX_API_RESOURCE_PROJECT_DETAILS}`,
      method: "GET",
      params: params
    })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<MBMeResourceResponse>>>>;
    return res.data;
  };
