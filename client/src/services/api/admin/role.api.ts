import { API_ADMIN_ROLE } from '@/constants/url'
import request from '@/services/request'
import {
    DefaultResponse,
    PaginationParams,
    PaginationResponse,
    ResponseList
} from '@/types/api.common'
import { AxiosResponse } from 'axios'

// Định nghĩa interface cho bộ lọc danh sách chức vụ
export interface ParamsGetRole extends PaginationParams {
    q?: string
    department?: string
}

// Định nghĩa kiểu dữ liệu của phản hồi danh sách chức vụ
export type RoleResponse = ResponseList & {
    roleCode: string
    roleName: string
    department: string
}

// Lấy danh sách chức vụ với phân trang và bộ lọc
export const getAllRoles = async (params: ParamsGetRole) => {
    const queryParams = {
        page: params.page,
        size: params.size,
        q: params.q, // Sử dụng đúng key mà backend yêu cầu
        department: params.department
    }

    const res = (await request({
        url: API_ADMIN_ROLE,
        method: 'GET',
        params: queryParams
    })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<RoleResponse>>>>

    return res.data
}
