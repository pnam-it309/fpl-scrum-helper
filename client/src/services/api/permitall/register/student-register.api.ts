import {PREFIX_API_AUTH } from '@/constants/url'
import request from '@/services/request'
import {
  DefaultResponse,
} from '@/types/api.common'
import { AxiosResponse } from 'axios'

export type studentDetailResponse = {
  id?: string
  nameFacility?: string
  nameDepartment?: string
  nameMajor?: string
}

export type studentRegisterResponse = studentDetailResponse & {
  idFacility?: string | null
  idDepartment?: string | null
  idMajor?: string | null
}

export interface studentRegisterRequest {
  idFacility: string
  idDepartment: string
  idMajor: string
  code: string 
  name : string 
  email: string 
}
export type facilityResponse  = {
  id: string
  nameFacility:string
  codeFacility:string
}

export const studentRegister = async (data: studentRegisterRequest) => {
  const res = (await request({
    url: `${PREFIX_API_AUTH}/register-student`,
    method: 'POST',
    data: data
  })) as AxiosResponse<DefaultResponse<null>>

  return res.data
}

export const getAllFacilityLogin = async () => {
  const res = (await request({
    url: `${PREFIX_API_AUTH}/facility-login`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<facilityResponse>>

  return res.data
}
