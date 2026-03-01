import { PREFIX_API_TODO_PROJECT_DETAILS } from "@/constants/url";
import request from "@/services/request";
import { DefaultResponse,  ResponseList } from "@/types/api.common";
import { AxiosResponse } from "axios";


export interface MBPhaseStatusResponse {
    
}

export const getStatusByIdPhase = async (idPhase: string) => {
    const res = (await request({
        url: `${PREFIX_API_TODO_PROJECT_DETAILS}/status-phase/${idPhase}`,
        method: "GET"
    })) as AxiosResponse<DefaultResponse<String>>;

    return res.data;
};


