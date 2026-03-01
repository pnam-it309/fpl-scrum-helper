import { PREFIX_API_TODO_VOTE_PROJECT_DETAILS,API_MEMBER_TODO_VOTE} from "@/constants/url";
import request from "@/services/request";
import { DefaultResponse, PaginationParams, PaginationResponse, ResponseList } from "@/types/api.common";
import { AxiosResponse } from "axios";

export interface todovoteRquest {
    idStaffProject: string,
}
export interface MBStageVoteRequest {
    idProject: string,
}
export type StaffStudentVoteResponse = ResponseList & {
    orderNumber: string,
    role: string,
    nameStaff: string,
    staffProjectId: string,
    voteStatus: string
}
export type MBStageVoteResponse =  {
    id: string,
    startName: string,
    endTime: string,
    phaseId: string,
    namePhaseProject: string
}
export const getTodoVoteByStaffProject = async (idProjectParam: string,idStaffParam: String) => {
    const res = (
        await request({
        url: `${PREFIX_API_TODO_VOTE_PROJECT_DETAILS}/todo-by-staffproject`,
        method: 'GET',
        params: {
            idStaff: idStaffParam,
            idProject: idProjectParam,
        }
    })) as AxiosResponse<DefaultResponse<Array<string>>>
    return res.data
}

export const getStaffStudentVote = async (id: string) => {
    const res = (await request({
        url: `${API_MEMBER_TODO_VOTE}/${id}`,
        method: 'GET'
    })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<StaffStudentVoteResponse>>>>
    return res.data
}
export const getVotingIsOnGoing = async (params: MBStageVoteRequest) => {
    const res = (await request({
        url: `${API_MEMBER_TODO_VOTE}/get-voting-is-on-going`,
        method: 'GET',
        params: params
    })) as AxiosResponse<DefaultResponse<MBStageVoteResponse>>
    return res.data
}

export const GetUpcomingVote = async (params: MBStageVoteRequest) => {
    const res = (await request({
        url: `${API_MEMBER_TODO_VOTE}/get-up-coming-vote`,
        method: 'GET',
        params: params
    })) as  AxiosResponse<DefaultResponse<Array<MBStageVoteResponse>>>
    return res.data
}