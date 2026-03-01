import {
  DefaultResponse,
  PaginationParams,
  PaginationResponse,
  ResponseList
} from '@/types/api.common'

import request from '@/services/request'
import { PREFIX_API_STAFF_ADMIN, PREFIX_API_STAFF_ADMIN_STAFF_NO_PROJECT } from '@/constants/url'
import { AxiosResponse } from 'axios'

export interface paramGetStaff extends PaginationParams {
  search?: string | ''
  searchStatus?: string
  searchFacility?: string
}

export type CodeRole = 'ADMIN' | 'QUAN_LY' | 'THANH_VIEN';

export type staffResponse = ResponseList & {
  codeStaff: string
  nameStaff: string
  emailFe: string
  emailFpt: string
  nameRoles: string
  idRole: string
  image: string
  nameFacility: string
  status: string
  orderNumber: number
}

export type Staff = {
  createdDate: number;
  lastModifiedDate: number;
  id: string;
  status: string;
  code: string;
  name: string;
  emailFe: string;
  emailFpt: string;
  picture: string;
};

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
  type: string;
  fileName: string;
  email: string
  staff: Staff | null;
  role: Role | null;
};


export interface staffRequest {
  name: string
  code: string
  emailFe: string
  emailFpt: string
  idRole: string
}

export type staffDetailResponse = {
  id?: string
  nameFacility?: string
  nameDepartment?: string
  nameMajor?: string
}

export type staffDetailIdResponse = staffDetailResponse & {
  idFacility?: string
  idDepartment?: string
  idMajor?: string
}

export type staffFacility = {
  id?: string
  name?: string
}

export interface staffDFMRequest {
  idStaffDetail: string
  idFacility: string
  idDepartment: string
  idMajor: string
}

export type roleStaffResponse = {
  id?: string
  name?: string
  code?: string
  idRole?: string
  nameFacility?: string
}

export type updateStaffFDM = {
  idFacility?: string
  idDepartment?: string
  idMajor?: string
  idUpdateFacility?: string
  idUpdateDepartment?: string
  idUpdateMajor?: string
  idStaffDetail?: string
}

export type roleByStaffResponse = {
  name: string
  code: string
  id: string
  idRole: string
  orderNumber?: number
}

export interface idRoleAndStaff {
  idRole: string
  idStaff: string
  codeRole?: CodeRole;
  emailLogin: string,
}

export interface idStaffRoleAndStaff {
  idStaffRole: string;
  idStaff: string;
}

export const getAllStaffRole = async () => {
  const res = (await request({
    url: `${PREFIX_API_STAFF_ADMIN}/role-staff`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<roleStaffResponse>>

  return res.data
}

export const getAllStaff = async (params: paramGetStaff) => {
  const res = (await request({
    url: `${PREFIX_API_STAFF_ADMIN}`,
    method: 'GET',
    params: params
  })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<staffResponse>>>>
  return res.data
}

export const getAllStaffNoProject = async () => {
  const res = (await request({
    url: `${PREFIX_API_STAFF_ADMIN_STAFF_NO_PROJECT}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<staffResponse>>>>
  return res.data
}

export const getAllStaffFacility = async () => {
  const res = (await request({
    url: `${PREFIX_API_STAFF_ADMIN}/facility`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<staffDetailIdResponse>>

  return res.data
}

export const staffByDepartmentMajor = async (id: string) => {
  const res = (await request({
    url: `${PREFIX_API_STAFF_ADMIN}/detailStaff/department-major/${id}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<staffDetailIdResponse>>

  return res.data
}

export const getAllStaffDepartment = async (id: String) => {
  const res = (await request({
    url: `${PREFIX_API_STAFF_ADMIN}/department/${id}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<staffDetailIdResponse>>

  return res.data
}

export const getAllStaffMajor = async (id: String) => {
  const res = (await request({
    url: `${PREFIX_API_STAFF_ADMIN}/major-department/${id}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<staffDetailIdResponse>>

  return res.data
}

export const detailStaff = async (id: string) => {
  const res = (await request({
    url: `${PREFIX_API_STAFF_ADMIN}/detailStaff/${id}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<staffResponse>>

  return res.data
}

export const createStaff = async (data: staffRequest) => {
  const res = (await request({
    url: `${PREFIX_API_STAFF_ADMIN}`,
    method: 'POST',
    data: data
  })) as AxiosResponse<DefaultResponse<staffResponse>>

  return res.data
}

export const createStaffFDM = async (data: staffDFMRequest) => {
  const res = (await request({
    url: `${PREFIX_API_STAFF_ADMIN}/major-department-facility`,
    method: 'POST',
    data: data
  })) as AxiosResponse<DefaultResponse<staffDetailIdResponse>>

  return res.data
}

export const updateStaff = async (id: string, data: staffRequest) => {
  const res = (await request({
    url: `${PREFIX_API_STAFF_ADMIN}/${id}`,
    method: 'PUT',
    data: data
  })) as AxiosResponse<DefaultResponse<staffResponse>>

  return res.data
}

export const deleteStaff = async (id: string,emailLogin : string) => {
  const res = (await request({
    url: `${PREFIX_API_STAFF_ADMIN}/${id}/${emailLogin}`,
    method: 'DELETE'
  })) as AxiosResponse<DefaultResponse<staffResponse>>

  return res.data
}

export const updateStaffFDm = async (data: updateStaffFDM) => {
  const res = (await request({
    url: `${PREFIX_API_STAFF_ADMIN}/major-department-facility`,
    method: 'PUT',
    data: data
  })) as AxiosResponse<DefaultResponse<null>>

  return res.data
}

export const deleteStaffFacility = async (id: string,emailLogin : string) => {
  const res = (await request({
    url: `${PREFIX_API_STAFF_ADMIN}/staff-facility/${id}/${emailLogin}`,
    method: 'DELETE'
  })) as AxiosResponse<DefaultResponse<staffResponse>>

  return res.data
}

export const getAllRoleByStaff = async (id: string) => {
  const res = (await request({
    url: `${PREFIX_API_STAFF_ADMIN}/role-by-staff/${id}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<Array<roleByStaffResponse>>>

  return res.data
}

export const deleteStaffByRole = async (params: idRoleAndStaff) => {
  const res = (await request({
    url: `${PREFIX_API_STAFF_ADMIN}/delete-role-staff`,
    method: 'DELETE',
    data: params
  })) as AxiosResponse<DefaultResponse<roleByStaffResponse>>

  return res.data
}

export const createStaffRole = async (data: idRoleAndStaff) => {
  const res = (await request({
    url: `${PREFIX_API_STAFF_ADMIN}/create-staff-role`,
    method: 'POST',
    data: data
  })) as AxiosResponse<DefaultResponse<roleByStaffResponse>>

  return res.data
}

export const dowloadExcel = async () => {
  const res = (await request({
    url: `${PREFIX_API_STAFF_ADMIN}/template`,
    method: 'GET',
    responseType: 'blob'
  })) as AxiosResponse<Blob>

  return res.data
}

export const importStaff = async (data: FormData) => {
  const res = (await request({
    url: `${PREFIX_API_STAFF_ADMIN}/file/upload`,
    method: 'Post',
    data: data
  })) as AxiosResponse<DefaultResponse<String>>

  return res.data
}

export const getAllHistory = async () => {
  const res = (await request({
    url: `${PREFIX_API_STAFF_ADMIN}/history`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<historyImprort>>

  return res.data
}

export const downloadCSV = async () => {
  const res = (await request({
    url: `${PREFIX_API_STAFF_ADMIN}/download-csv`,
    method: 'GET',
    responseType: 'blob',
  })) as AxiosResponse<Blob>

  return res.data
}

export const getStaffRoleByStaff = async (idStaff: string) => {
  const res = (await request({
    url: `${PREFIX_API_STAFF_ADMIN}/staff-role-by-staff/${idStaff}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<Array<roleStaffResponse>>>

  return res.data
}

export const getAllCountStaff = async () => {
  const res = (await request({
    url: `${PREFIX_API_STAFF_ADMIN}/count-staff`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<number | null>>

  return res.data
}