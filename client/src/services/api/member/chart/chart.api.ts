import { API_MEMBER_CHART } from "@/constants/url";
import request from "@/services/request";
import {DefaultResponse, PaginationParams, PaginationResponse, ResponseList} from "@/types/api.common"
import { AxiosResponse } from "axios";

export interface BurndownDailyData {
  date: string;         
  estimatedNumberOfTasks: number;    
  reality: number;     
  totalActual: number;
  remaining: number;
}

export interface BurndownChartResponse {
  estimatedNumberOfTasks: number;
  reality: number[]; 
  totalActual: number;
  remaining: number;
  byDate: BurndownDailyData[];
}

export interface VelocityChartResponse {
  phaseName: string;
  estimated: number;
  actual: number | null;
}


export interface BurndownPoint  {
  date: string;
  expected: number;
  actual: number | null;
}


export const getBurndownChart = async (projectId: string, phaseId: string) => {
    const res = await request({
        url: `${API_MEMBER_CHART}/burndown/${projectId}/${phaseId}`,
        method: 'GET',
    }) as AxiosResponse<DefaultResponse<BurndownChartResponse>>;
    return res.data;
}

export const getVelocityChart = async (projectId: string) => {
  const res = await request({
    url: `${API_MEMBER_CHART}/velocity/${projectId}`,
    method: "GET",
  }) as AxiosResponse<DefaultResponse<VelocityChartResponse[]>>;

  return res.data;
};

export const getBurndownChartTrello = async (projectId: string, phaseId: string) => {
    const res = await request({
        url: `${API_MEMBER_CHART}/storyPoint/${projectId}/${phaseId}`,
        method: 'GET',
    }) as AxiosResponse<DefaultResponse<BurndownPoint>>;
    return res.data;
}

export const getBurndownChartByProject = async (projectId: string) => {
    const res = await request({
        url: `${API_MEMBER_CHART}/storyPoint-project/${projectId}`,
        method: 'GET',
    }) as AxiosResponse<DefaultResponse<BurndownPoint>>;
    return res.data;
}