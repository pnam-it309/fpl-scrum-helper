import { PREFIX_API_TODO_PROJECT_DETAILS } from "@/constants/url";
import request from "@/services/request";
import { DefaultResponse,  ResponseList } from "@/types/api.common";
import { AxiosResponse } from "axios";

export interface ParamsGetMyFilterTodoDetailsModal  {
    idTodo: string ;
}

export interface ParamsGetCheckTodoChildRequest  {
    idTodo: string ;
}

export interface MBMeConvertLabelResponse {
    id: string;
    code: string;
    name: string;
    colorLabel:string
}

export interface MBMeConvertTodoModalResponse {
    id: string;
    code: string;
    name: string;
    priorityLevel: string;
    deadline: number;
    completionTime: number;
    indexTodo: number;
    progress: number;
    imageId: string;
    nameFile: string;
    numberTodoComplete: number;
    numberTodo: number;
    progressOfTodo: number;
    deadlineString: string;
    todoListId: string;
    labels: MBMeConvertLabelResponse[];
}

export interface MBMeTodoChildResponse {
    id: string;
    code: string;
    name: string;
    statusTodoChild: number;
}

export const getTodoDetailsModal = async (params: ParamsGetMyFilterTodoDetailsModal) => {
    const res = (await request({
        url: `${PREFIX_API_TODO_PROJECT_DETAILS}/find-todo`,
        method: "GET",
        params: params
    })) as AxiosResponse<DefaultResponse<MBMeConvertTodoModalResponse >>;

    return res.data;
};

export const getAllCheckTodoChild = async (params: ParamsGetCheckTodoChildRequest) => {
    const res = (await request({
        url: `${PREFIX_API_TODO_PROJECT_DETAILS}/check-todochild`,
        method: "GET",
        params: params
    })) as AxiosResponse<DefaultResponse<MBMeTodoChildResponse[] >>;

    return res.data;
};


