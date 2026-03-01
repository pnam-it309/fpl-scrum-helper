import {
  PREFIX_API_MANAGE_PROJECT_BY_STAFF,
  PREFIX_API_MANAGE_PROJECT_BY_STUDENT,
  PREFIX_API_MANAGE_PROJECT_STUDENT,
  PREFIX_API_MANAGE_PROJECT_STUDENT_STAFF,
  PREFIX_API_MANAGE_USER_PROJECT
} from '@/constants/url'
import request from '@/services/request'
import { DefaultResponse } from '@/types/api.common'
import { AxiosResponse } from 'axios'

export type projectStudentResponse = {
  name: string
  code: string
  id: string
  image: string
  email: string
  status: string
  role: string
}

export const getAllProjectStudent = async (id: string) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_PROJECT_STUDENT}/${id}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<projectStudentResponse>>
  return res.data
}

export const getAllProjectStaff = async (id: string) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_PROJECT_STUDENT_STAFF}/${id}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<projectStudentResponse>>
  return res.data
}



export const getAllStaffByProject = async (id: string) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_PROJECT_BY_STAFF}/${id}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<projectStudentResponse>>
  return res.data
}

export const getAllStaffByStudent = async (id: string) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_PROJECT_BY_STUDENT}/${id}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<projectStudentResponse>>
  return res.data
}
