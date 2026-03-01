import { PREFIX_API_MANAGE_TODO_LIST } from "@/constants/url";
import request from "@/services/request";
import { DefaultResponse, PaginationParams, PaginationResponse, ResponseList } from "@/types/api.common"
import { AxiosResponse } from "axios";

export interface ParamsGetTodoList extends PaginationParams {
  q?: string | '',
  search: string,
  level?: string | null,
  status?: string | null,
}

export type TodoListProjectResponse = ResponseList & {
  todoListId: string,
  statusTodoList: string,
  nameTodoList: string,
  codeTodoList: string,
  createdDate: number,
  nameProject: string,
  orderNumber: number,
  describeTodoList: string
}

export type TodoProjectResponse = ResponseList & {
  createdDate: string;
  email: string;
  idTodo: string;
  priorityLevel: string;
  deadline: string;
  statusTodo: string;
  nameTodo: string;
  picture: string;
  lastModifiedDate: string;
  orderNumber: string;
  descriptionsTodo: string;
  namePhaseProject: string;
  phaseId: string;
  projectId: string;
  students: Student[];
  staff: Staff[];
};

export interface TodoListBoardResponse extends ResponseList {
  id: string
  code: string
  name: string
  indexTodoList: number
  tasks: TodoPhaseProjectResponse[]
}

export type TodoPhaseProjectResponse = ResponseList & {
  orderNumber: string,
  todoId: string,
  indexTodo: number,
  todoName: string,
  completionTime: number,
  deadlineTodo: number,
  createdDate: string,
  todoListId: string,
  todoListName: string,
  priorityLevel: string;
  statusTodo: string;
  students: Student[];
  staff: Staff[];
  label: Label[];
};

export type Label = {
  id: string;
  name: string;
  color: string;
  createdDate?: string | null;
  lastModifiedDate?: string | null;
  status?: string | null;
  code?: string | null;
  descriptions?: string | null;
  project: string | null;
};

export type Student = {
  id: string;
  name: string;
  email: string;
  image: string;
  createdDate?: string | null;
  lastModifiedDate?: string | null;
  status?: string | null;
  code?: string | null;
  majorFacility?: string | null;
};

export type Staff = {
  id: string;
  name: string;
  emailFe?: string | null;
  emailFpt?: string | null;
  picture: string;
  createdDate?: string | null;
  lastModifiedDate?: string | null;
  status?: string | null;
  code?: string | null;
};

export type PhaseProjectResponseMa = ResponseList & {
  idPhase: string,
  namePhase: string
};

export type TodoListBYPhaseProjectResponseMa = ResponseList & {
  todoListId: string;
  todoListName: string;
};

export type IndexMaxResponse = ResponseList & {
  indexTodo: string
};

export interface TodoListRequest {
  todoListId: string,
  projectId: string,
  codeTodoList: string,
  nameTodoList: string,
  describeTodoList: string
}

export interface TodoProjectRequest {
  projectId: string
  nameTodo: string
  statusTodo: string
  idStaffProject: string
  priorityLevel: string
}

export type TodoListLogImport = {
  id: string
  todolist: string
  status: string
  message: string
}

export type Role = {
  createdDate: number,
  lastModifiedDate: number,
  id: string,
  status: string,
  name: string,
  code: string
}

export type historyImprortTodo = ResponseList & {
  message: string;
  email: string;
  id: string;
  createdDate: number;
  role: Role | null;
};

export const getAllTodoList = async (params: ParamsGetTodoList, id: string) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_TODO_LIST}/get-all-todolist/${id}`,
    method: 'GET',
    params: params
  })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<TodoListProjectResponse>>>>
  return res.data
}

export const getAllTodoProject = async (params: ParamsGetTodoList, id: string) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_TODO_LIST}/get-all-todo/${id}`,
    method: 'GET',
    params: params
  })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<TodoProjectResponse>>>>
  return res.data
}

// export const getAllTodoPhaseProject = async (idProject: string, idPhase: string) => {
//   const res = (await request({
//     url: `${PREFIX_API_MANAGE_TODO_LIST}/get-todo-phase/${idProject}/${idPhase}`,
//     method: 'GET'
//   })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<TodoPhaseProjectResponse>>>>
//   return res.data
// }

export const getAllTodoPhaseProject = async (
  idProject: string,
  idPhase: string,
  page = 0,
  size = 10
) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_TODO_LIST}/get-todo-phase/${idProject}/${idPhase}`,
    method: 'GET',
    params: { page, size }, // ✅ Gửi page và size qua query param
  })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<TodoPhaseProjectResponse>>>>
  return res.data
}


//
export const getAllTodoListByPhaseProject = async (idProject: string, idPhase: string) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_TODO_LIST}/get-todolist-phase/${idProject}/${idPhase}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<TodoListBYPhaseProjectResponseMa>>>>
  return res.data
}

export const getIndexMaxTodo = async (id: string) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_TODO_LIST}/index-todo/${id}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<IndexMaxResponse>>>>
  return res.data
}


//

export const getAllPhaseMa = async (id: string) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_TODO_LIST}/get-all-phase/${id}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<PhaseProjectResponseMa>>>>
  return res.data
}

export const updateTodoPhaseProject = async (idPhase: string, idTodo: string, idProject: string) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_TODO_LIST}/update-phase-todo/${idTodo}/${idPhase}/${idProject}`,
    method: 'PUT'
  })) as AxiosResponse<DefaultResponse<TodoProjectResponse>>

  return res.data
}

export const addTodoList = async (data: TodoListRequest) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_TODO_LIST}/add`,
    method: 'POST',
    data: data
  })) as AxiosResponse<DefaultResponse<TodoListProjectResponse>>

  return res.data
}

export const updateTodoList = async (data: TodoListRequest, id: string) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_TODO_LIST}/update/${id}`,
    method: 'PUT',
    data: data
  })) as AxiosResponse<DefaultResponse<TodoListProjectResponse>>

  return res.data
}

export const detailTodoList = async (id: string) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_TODO_LIST}/detail/${id}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<TodoListProjectResponse>>

  return res.data
}

export const downloadExcelTodoList = async () => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_TODO_LIST}/template`,
    method: 'GET',
    responseType: 'blob'
  })) as AxiosResponse<Blob>

  return res.data
}

export const importTodoList = async (data: FormData, idProject: string) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_TODO_LIST}/file/upload/${idProject}`,
    method: 'POST',
    data: data
  })) as AxiosResponse<DefaultResponse<string>>
  return res.data
}

export const importLogTodoList = async () => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_TODO_LIST}/read/fileLog`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<TodoListLogImport>>
  return res.data
}

export const downloadImportLogTodoList = async () => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_TODO_LIST}/download-csv`,
    method: 'GET',
    responseType: 'blob'
  })) as AxiosResponse<Blob>

  return res.data
}

export const updateTodoProject = async (data: TodoProjectRequest, id: string) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_TODO_LIST}/update-todo/${id}`,
    method: 'PUT',
    data: data
  })) as AxiosResponse<DefaultResponse<TodoProjectResponse>>

  return res.data
}

export const getAllHistoryTodo = async (idProject: string) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_TODO_LIST}/history/${idProject}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<historyImprortTodo>>

  return res.data
}

