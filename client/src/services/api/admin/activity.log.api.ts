import { API_ADMIN_ACTIVITY_LOG } from "@/constants/url";
import request from "@/services/request";
import {DefaultResponse, PaginationParams, PaginationResponse, ResponseList} from "@/types/api.common"
import { AxiosResponse } from "axios";

export interface ParamsGetActivityLog extends PaginationParams {
    q?: string | '',
}

export interface ActivityLogResponse extends ResponseList {
    id: string;                 
    createDate: number;         
    executorEmail: string;     
    content: string;          
    role: string;               
}

export const getAllActivityLog = async (params: ParamsGetActivityLog) => {
    const res = (await request({
        url: `${API_ADMIN_ACTIVITY_LOG}`,
        method: 'GET',
        params: params
    })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<ActivityLogResponse>>>>
    return res.data
}



