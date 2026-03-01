<template>
  <BreadcrumbDefaultV1 label="Quản lý chức vụ" :routes="breadcrumbRoutes">
    <RoleFilter
        :q="data.searchQuery"
        @update:q="dataSearch"
        :department="data.branchSearch"
        @update:department="dataSearchBranch"
    />

    <RoleTable
        :data="data?.roleData"
        :size="data.size"
        :total-items="data.totalItems"
        :page="data.page"
        @page-change="handlePageChange"
    />
  </BreadcrumbDefaultV1>
</template>

<script setup lang="ts">
const breadcrumbRoutes = [
  { name: 'Quản trị hệ thống', nameRoute: ROUTES_CONSTANTS.ADMIN.name },
  { name: 'Quản lý chức vụ  ', nameRoute: ROUTES_CONSTANTS.ADMIN.children.ROLE.name },
]
import { onMounted, reactive, watch } from 'vue'
import RoleTable from './RoleTable.vue'
import RoleFilter from './RoleFilter.vue'
import { getAllRoles, ParamsGetRole, RoleResponse } from '@/services/api/admin/role.api'
import { debounce } from 'lodash'
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import BreadcrumbDefault from '@/components/custom/Div/BreadcrumbDefault.vue'
import BreadcrumbDefaultV1 from '@/components/custom/Div/BreadcrumbDefaultV1.vue'
import { ROUTES_CONSTANTS } from '@/constants/path'

const data = reactive({
  searchQuery: '',
  branchSearch: '',
  roleData: [] as RoleResponse[],
  page: 1,
  size: 10,
  totalItems: 0
})

// Nhận dữ liệu từ RoleFilter
const dataSearch = (search: string) => {
  data.searchQuery = search
}

const dataSearchBranch = (search: string) => {
  data.branchSearch = search
}

const getAllDataRoles = async () => {
  try {
    const params: ParamsGetRole = {
      page: data.page,
      size: data.size,
      q: data.searchQuery, // Đổi từ "search" → "q"
      department: data.branchSearch // Đổi từ "branch" → "department"
    }
    const response = await getAllRoles(params)
    data.roleData = response.data.content || []
    data.totalItems = response.data.totalElements
  } catch (error) {
    console.error('Failed to fetch roles:', error)
  }
}

onMounted(() => {
  getAllDataRoles()
})

// Phân trang
const handlePageChange = ({ page, pageSize }: { page: number; pageSize?: number }) => {
  data.page = page
  if (pageSize) {
    data.size = pageSize
  }
  getAllDataRoles()
}

// Gọi API khi thay đổi bộ lọc (debounced)
const debouncedFetchRoles = debounce(getAllDataRoles, 300)

watch(
    () => [data.searchQuery, data.branchSearch],
    () => {
      data.page = 1
      debouncedFetchRoles()
    }
)
</script>
<style scoped>
/* Áp dụng gradient cho nút */
:deep(.ant-btn) {
  background: var(--selected-header) !important; /* Gradient nền */
  color: var(--selected-text) !important; /* Màu chữ trắng */
  border-color: var(--selected-header) !important; /* Viền màu từ gradient */
}

/* Hiệu ứng hover */
:deep(.ant-btn:hover),
:deep(.ant-btn:focus) {
  background: var(--selected-header-hover) !important; /* Gradient khi hover */
  border-color: var(--selected-header-hover) !important; /* Viền khi hover */
}

</style>
