import { AxiosResponse } from 'axios'
import request from '@/services/request'

import {
  DefaultResponse,
} from '@/types/api.common.ts'


export const getStaffADMIN = async () => {
  const res = (await request({
    url: `${"http://localhost:9999/api/v1/admin/ping"}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<null>>

  return res.data
}

export const getStaffMember = async () => {
  const res = (await request({
    url: `${"http://localhost:9999/api/v1/member/ping"}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<null>>

  return res.data
}

export const getStaffManage = async () => {
  const res = (await request({
    url: `${"http://localhost:9999/api/v1/manage/ping"}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<null>>

  return res.data
}