import { PREFIX_API_MANAGE_TYPE_TODO } from '@/constants/url'
import request from '@/services/request'
import { DefaultResponse } from '@/types/api.common'
import { AxiosResponse } from 'axios'

export type TypeTodoResponse = {
  id: string
  type: string
  status: number
}

export const getListTypeTodo = async () => {
  const res = (await request({
    url: `manage/todo/type`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<Array<TypeTodoResponse>>>

  return res.data
}
