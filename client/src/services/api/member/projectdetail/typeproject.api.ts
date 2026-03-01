import { PREFIX_API_TYPE_PROJECT_DETAILS } from "@/constants/url";
import request from "@/services/request";
import { DefaultResponse } from "@/types/api.common";
import { AxiosResponse } from "axios";

export interface MBTypeProjectResponse {
    id:string
    type :string
}

export const getAllTypeProject = async () => {
    const res = (await request({
      url: `${PREFIX_API_TYPE_PROJECT_DETAILS}/list`,
      method: "GET"
    })) as AxiosResponse<DefaultResponse<MBTypeProjectResponse[]>>;
    return res.data;
  };


