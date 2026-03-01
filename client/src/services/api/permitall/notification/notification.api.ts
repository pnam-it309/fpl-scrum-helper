import { PREFIX_API_AUTH, PREFIX_API_PERMITALL } from "@/constants/url";
import request from "@/services/request";
import { AxiosResponse } from "axios";

export interface ResponseObject<T> {
  data: T;
  status: string;      // "OK" | "NOT_FOUND" | ...
  message: string;
}

// Gọi API lấy thông tin dự án theo id
export const getProjectByIdWithNotification = async (idProject: string): Promise<any> => {
  try {
    const res = (await request({
      url: `${PREFIX_API_PERMITALL}/notification/project?idProject=${idProject}`, // <-- Thêm idProject vào URL
      method: "GET",
    })) as AxiosResponse<ResponseObject<any>>;
    return res.data.data;
  } catch (err) {
    return null;
  }
};

