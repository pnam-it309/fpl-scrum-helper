import { PREFIX_API_ACTIVITY_PROJECT_DETAILS } from "@/constants/url";
import request from "@/services/request";
import { DefaultResponse, PaginationParams, PaginationResponse } from "@/types/api.common"
import { AxiosResponse } from "axios";

export interface MBMeFindActivityRequest extends  PaginationParams{
    idTodo:string
    idProject:string 
}

export interface MBMeActivityResponse {
    id:string
    memberCreatedId:string 
    memberId:string 
    projectId :string 
    todoId :string 
    todoListId :string 
    contentAction:string 
    urlImage:string 
    createdDate: number
    urlPath:string 
    memberName:string 
    memberEmail : string 
    memberImage:string 
    contentActionProject:string 
    totalRecords:number
}

export const getAllActivityWhereIdTodo = async (param: MBMeFindActivityRequest) => {
    const res = (await request({
        url: `${PREFIX_API_ACTIVITY_PROJECT_DETAILS}`,
        method: "GET",
        params: param
    }))as AxiosResponse<DefaultResponse<PaginationResponse<Array<MBMeActivityResponse>>>>;
    return res.data;
};

export const getAllActivityWhereIdProject = async (param: MBMeFindActivityRequest) => {
    const res = (await request({
        url: `${PREFIX_API_ACTIVITY_PROJECT_DETAILS}/all-project`,
        method: "GET",
        params: param
    }))as AxiosResponse<DefaultResponse<PaginationResponse<Array<MBMeActivityResponse>>>>;
    return res.data;
};

export const getCountTotalActivitiesWhereIdProject = async (param: MBMeFindActivityRequest) => {
    const res = (await request({
        url: `${PREFIX_API_ACTIVITY_PROJECT_DETAILS}/count-activity-project`,
        method: "GET",
        params: param
    }))as AxiosResponse<DefaultResponse<Number>>;
    return res.data;
};