import { PREFIX_API_NOTIFICATION_PROJECT_DETAILS } from "@/constants/url";
import request from "@/services/request";
import { DefaultResponse } from "@/types/api.common"
import { AxiosResponse } from "axios";

export interface MBMeCreateNotificationCommentRequest {
    mentionedEmails?: string[];
    mentionedIds?:string[];
    idProject?: string;
    todoId: string;
    url: string;
    idUser: string;
    email?: string;
    username: string;
}

export const createNotificationTag = async (data: MBMeCreateNotificationCommentRequest) => {
    const res = (await request({
        url: `${PREFIX_API_NOTIFICATION_PROJECT_DETAILS}`,
        method: "POST",
        data: data
    })) as AxiosResponse<DefaultResponse<Array<null>>>;
    return res.data;
};
export interface MBMeCreate1NotificationCommentRequest {
  idProject: string;      // ✅ Bắt buộc - để backend lấy toàn bộ thành viên trong dự án
  idTodo?: string;        // ✅ Optional - giữ nếu sau này cần gắn Todo
  url: string;            // ✅ URL điều hướng khi click thông báo
  idUser: string;         // ✅ Ai tạo thông báo
  username: string;       // ✅ Tên người tạo
  content: string;        // ✅ Nội dung tùy biến (ví dụ "Sprint 1 đã hoàn thành")

}
export interface MBMeCreatev2NotificationCommentRequest {
  idProject: string;      // ✅ Bắt buộc - để backend lấy toàn bộ thành viên trong dự án
  idTodo?: string;        // ✅ Optional - giữ nếu sau này cần gắn Todo
  url: string;            // ✅ URL điều hướng khi click thông báo
  idUser: string;         // ✅ Ai tạo thông báo
  username: string;       // ✅ Tên người tạo
  content: string;        // ✅ Nội dung tùy biến (ví dụ "Sprint 1 đã hoàn thành")
  idReceiver:string // người nhận thông báo này 
}

export const createV1NotificationTag = async (
  data: MBMeCreate1NotificationCommentRequest
) => {
  const res = (await request({
    url: `${PREFIX_API_NOTIFICATION_PROJECT_DETAILS}/v1`, //  endpoint riêng
    method: "POST",
    data: data
  })) as AxiosResponse<DefaultResponse<null>>;
  return res.data;
};

// thông báo cho ai 
export const createV2NotificationTag = async (
  data: MBMeCreatev2NotificationCommentRequest
) => {
  const res = (await request({
    url: `${PREFIX_API_NOTIFICATION_PROJECT_DETAILS}/v2`, //  endpoint riêng
    method: "POST",
    data: data
  })) as AxiosResponse<DefaultResponse<null>>;
  return res.data;
};