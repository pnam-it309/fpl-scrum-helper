import { API_ADMIN_STUDENT } from '@/constants/url'
import request from '@/services/request'
import {
  DefaultResponse,
  PaginationParams,
  PaginationResponse,
  ResponseList
} from '@/types/api.common'
import { AxiosResponse } from 'axios'

export interface ParamsGetStudent extends PaginationParams {
  q?: string | ''
  search: string | null
  searchDF: string | null
}

export type StudentResponse = ResponseList & {
  code: string
  name: string
  status: string
  email: string
  majorFacility: string
}

export interface ADStudentRequest {
  id?: string
  code: string
  name: string
  status: string
  email: string
  createdDate: number
  image: string
}

export type studentDetailResponse = {
  id?: string
  nameFacility?: string
  nameDepartment?: string
  nameMajor?: string
}

export type studentDetailIdResponse = studentDetailResponse & {
  idFacility?: string | null
  idDepartment?: string | null
  idMajor?: string | null
}

export type studentResponse = ResponseList & {
  code: string
  name: string
  email: string
  image: string
  status: string
}

export interface studentDFMRequest {
  idStudentDetail: string
  idFacility: string
  idDepartment: string
  idMajor: string
}

export type updateStudentFDM = {
  idFacility?: string
  idDepartment?: string
  idMajor?: string
  idUpdateFacility?: string
  idUpdateDepartment?: string
  idUpdateMajor?: string
  idStudentDetail?: string
}

export type StudentLogImport = {
  id: string
  student: string
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

export type historyImprort = ResponseList & {
  message: string;
  email: string;
  id: string;
  createdDate: number;
  role: Role | null;
};

export const getStudent = async (params: ParamsGetStudent) => {
  const res = (await request({
    url: `${API_ADMIN_STUDENT}/get-all-student`,
    method: 'GET',
    params: params
  })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<StudentResponse>>>>
  return res.data
}

export const fillAllStudent = async () => {
  const res = (await request({
    url: `admin/student/fill-all-student`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<StudentResponse>>>>
  return res.data
}

export const detailStudent = async (id: string) => {
  const res = (await request({
    url: `${API_ADMIN_STUDENT}/detail-student/${id}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<StudentResponse>>

  return res.data
}

export const addStudent = async (data: ADStudentRequest) => {
  const res = (await request({
    url: `${API_ADMIN_STUDENT}/add-student`,
    method: 'POST',
    data: data
  })) as AxiosResponse<DefaultResponse<StudentResponse>>

  return res.data
}

export const updateStudent = async (data: ADStudentRequest, id: string) => {
  const res = (await request({
    url: `${API_ADMIN_STUDENT}/update-student/${id}`,
    method: 'PUT',
    data: data
  })) as AxiosResponse<DefaultResponse<StudentResponse>>

  return res.data
}

export const studentByDepartmentMajor = async (id: string) => {
  const res = (await request({
    url: `${API_ADMIN_STUDENT}/detailStudent/department-major/${id}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<studentDetailIdResponse>>

  return res.data
}

export const deleteStudentFacility = async (id: string,emailLogin : string) => {
  const res = (await request({
    url: `${API_ADMIN_STUDENT}/student-facility/${id}/${emailLogin}`,
    method: 'DELETE'
  })) as AxiosResponse<DefaultResponse<studentResponse>>
  
  return res.data
}

export const createStudentFDM = async (data: studentDFMRequest) => {
  const res = (await request({
    url: `${API_ADMIN_STUDENT}/major-department-facility`,
    method: 'POST',
    data: data
  })) as AxiosResponse<DefaultResponse<studentDetailIdResponse>>

  return res.data
}

export const updateStudentFDm = async (data: updateStudentFDM) => {
  const res = (await request({
    url: `${API_ADMIN_STUDENT}/major-department-facility`,
    method: 'PUT',
    data: data
  })) as AxiosResponse<DefaultResponse<null>>

  return res.data
}

export const deleteStudent = async (id: string, emailLogin : string) => {
  const res = (await request({
    url: `${API_ADMIN_STUDENT}/${id}/${emailLogin}`,
    method: 'DELETE'
  })) as AxiosResponse<DefaultResponse<studentResponse>>

  return res.data
}
export const importLog = async () => {
  const res = (await request({
    url: `/admin/student/read/fileLog`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<StudentLogImport>>
  return res.data
}

export const downloadImportLog = async () => {
  const res = (await request({
    url: `/admin/student/download-csv`,
    method: 'GET',
    responseType: 'blob'
  })) as AxiosResponse<Blob>

  return res.data
}

export const getAllHistoryStudent = async () => {
  const res = (await request({
    url: `${API_ADMIN_STUDENT}/history`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<historyImprort>>

  return res.data
}