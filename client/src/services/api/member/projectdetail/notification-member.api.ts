import { PREFIX_API_NOTIFICATION_MEMBER_PROJECT_DETAILS } from "@/constants/url";
import request from "@/services/request";
import { DefaultResponse, PaginationParams, PaginationResponse } from "@/types/api.common"
import { AxiosResponse } from "axios";

export interface MBMeFindNotificationMemberRequest extends  PaginationParams{
    idMember:string
}

export interface MBMeNotificationMemberResponse {
    id:string
    notificationId:string 
    content:string 
    url :string 
    status :number 
    createdDate :number 
    todoId:string 
}

export const getAllNotificationMember = async (param: MBMeFindNotificationMemberRequest) => {
    const res = (await request({
        url: `${PREFIX_API_NOTIFICATION_MEMBER_PROJECT_DETAILS}`,
        method: "GET",
        params: param
    }))as AxiosResponse<DefaultResponse<PaginationResponse<Array<MBMeNotificationMemberResponse>>>>;
    return res.data;
};


export const updateStatusNotificationNumber = async (idNotificationMember: string) => {
    const res = (await request({
        url: `${PREFIX_API_NOTIFICATION_MEMBER_PROJECT_DETAILS}/update-status`,
        method: "PUT",
        params: { idNotificationMember }
    }))as AxiosResponse<DefaultResponse<null>>;
    return res.data;
};