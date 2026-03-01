import { PREFIX_API_MYPROJECT_MEMBER } from "@/constants/url";
import request from "@/services/request";
import {DefaultResponse, PaginationParams, PaginationResponse, ResponseList} from "@/types/api.common"
import { AxiosResponse } from "axios";

export interface ParamsGetMyProject extends PaginationParams {
    q?: string | '',
    projectName: string,
    ProjectStatus?: string
    idUser: string
}

export type myProjectResponse = ResponseList & {
    idProject : string ,
    nameProject: string , 
    progress: number,
    statusProject: string,
    projectStartDate:number,
    projectEndDate:number
}

export const getAllMyProject = async (params: ParamsGetMyProject) => {
    const res = (await request({
        url: `${PREFIX_API_MYPROJECT_MEMBER}`,
        method: 'GET',
        params: params
    })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<myProjectResponse>>>>
    return res.data
}
