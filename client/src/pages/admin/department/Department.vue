<template>
  <BreadcrumbDefaultV1 label="Quản lý bộ môn" :routes="breadcrumbRoutes">
    <DepartmentFilter
      :searchQuery="state.searchQuery"
      @update:searchQuery="updateSearchQuery"
      :searchStatus="state.departmentStatus"
      @update:searchStatus="updateSearchStatus"
    />

    <DepartmentTable
      :searchQuery="state.searchQuery"
      :department="state.department"
      :paginationParams="state.paginationParams"
      :totalItems="state.totalItems"
      @add="openAddModal"
      @view="openViewModal"
      @page-change="handlePageChange"
      @manage-major="openManageMajorModal"
      @update-status="handleUpdateStatusClick"
    />
  </BreadcrumbDefaultV1>
  <DepartmentModal
    :open="state.isModalOpen"
    :departmentId="state.departmentId"
    :title="state.departmentId ? 'Cập nhật bộ môn' : 'Thêm mới bộ môn'"
    @close="closeModal"
    @success="fetchDepartment"
  />

  <a-modal
    v-model:visible="state.isManageMajorModalOpen"
    :width="1300"
    :footer="null"
    :z-index="9999"
    :wrap-style="{ display: 'flex', alignItems: 'center', justifyContent: 'center' }"
    :style="{ top: '10px', width: '60vw' }"
    @cancel="state.isManageMajorModalOpen = false"
  >
    <Major
      :departmentId="state.selectedDepartmentId"
      :isManageMajorModalOpen="state.isManageMajorModalOpen"
      @update:isManageMajorModalOpen="updateIsManageMajorModalOpen"
    />
  </a-modal>
</template>

<script setup lang="ts">
const breadcrumbRoutes = [
  { name: 'Quản trị hệ thống', nameRoute: ROUTES_CONSTANTS.ADMIN.name },
  { name: 'Bộ môn ', nameRoute: ROUTES_CONSTANTS.ADMIN.children.DEPARTMENT.name }
]
import { onMounted, reactive, watch } from 'vue'
import DepartmentTable from './DepartmentTable.vue'
import DepartmentModal from './DepartmentModal.vue'
import DepartmentFilter from './DepartmentFilter.vue'
import Major from '../major/Major.vue'
import { debounce } from 'lodash'
import {
  DepartmentResponse,
  getDepartments,
  ParamsGetDepartment,
  updateStatusDepartment
} from '@/services/api/department.api'
import { MajorResponse } from '@/services/api/major.api'
import BreadcrumbDefault from '@/components/custom/Div/BreadcrumbDefault.vue'
import { toast } from 'vue3-toastify'
import BreadcrumbDefaultV1 from '@/components/custom/Div/BreadcrumbDefaultV1.vue'
import { ROUTES_CONSTANTS } from '../../../constants/path'

const state = reactive({
  searchQuery: '',
  isModalOpen: false,
  departmentId: null as string | null,
  department: [] as DepartmentResponse[],
  departmentStatus: null as string | null,
  major: [] as MajorResponse[],
  paginationParams: { page: 1, size: 10 },
  totalItems: 0,
  isManageMajorModalOpen: false,
  selectedDepartmentId: null as string | null
})

const fetchDepartment = async () => {
  try {
    const params: ParamsGetDepartment = {
      page: state.paginationParams.page,
      size: state.paginationParams.size,
      departmentSearch: state.searchQuery || null,
      departmentStatus: state.departmentStatus ?? undefined
    }
    const response = await getDepartments(params)
    state.department = response?.data?.data || []
    state.totalItems = response?.data?.totalElements || 0
  } catch (error) {
    console.error('Lỗi khi tải danh sách bộ môn:', error)
  }
}

const debouncedFetchDepartment = debounce(fetchDepartment, 1000)

const updateSearchQuery = (newQuery: string) => {
  state.searchQuery = newQuery.trim()
}

// Theo dõi sự thay đổi của bộ lọc để gọi API
watch(
  () => state.searchQuery,
  () => {
    state.paginationParams.page = 1
    debouncedFetchDepartment()
  }
)

watch(
  () => state.departmentStatus,
  () => {
    state.paginationParams.page = 1
    debouncedFetchDepartment()
  }
)

onMounted(fetchDepartment)

const openAddModal = () => {
  state.departmentId = null
  state.isModalOpen = true
}

const openViewModal = (id: string) => {
  console.log('ID depaert main : ===> ', id)

  state.departmentId = id

  console.log('ID depaert main : ===> ', state.departmentId)
  state.isModalOpen = true
}

const openManageMajorModal = (departmentId: string) => {
  state.selectedDepartmentId = departmentId
  state.isManageMajorModalOpen = true
}

const closeModal = () => {
  state.isModalOpen = false
  state.departmentId = null // reset để đảm bảo modal mở lại sẽ gọi API
}

const handlePageChange = ({ page, pageSize }: { page: number; pageSize?: number }) => {
  state.paginationParams.page = page
  if (pageSize) {
    state.paginationParams.size = pageSize
  }
  fetchDepartment()
}

const updateIsManageMajorModalOpen = (value: boolean) => {
  state.isManageMajorModalOpen = value
}

const updateSearchStatus = (newStatus: string | null) => {
  state.departmentStatus = newStatus
  state.paginationParams.page = 1
  debouncedFetchDepartment() // Gọi API sau khi cập nhật trạng thái
}

const handleUpdateStatus = async (departmentId: string) => {
  try {
    await updateStatusDepartment(departmentId)
    toast.success('Cập nhật trạng thái thành công!')
    fetchDepartment()
  } catch (error) {
    toast.error('Cập nhật trạng thái thất bại!')
  }
}

const handleUpdateStatusClick = (departmentId: string) => {
  handleUpdateStatus(departmentId)
}
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
