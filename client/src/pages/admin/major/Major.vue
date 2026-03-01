<template>
  <BreadcrumbCustom label="Quản lý chuyên ngành">
    <MajorFilter :searchQuery="state.searchQuery" @update:searchQuery="updateSearchQuery" />

    <MajorTable
      :searchQuery="state.searchQuery"
      :paginationParams="state.paginationParams"
      :major="state.major"
      :departmentId="props.departmentId || ''"
      :totalItems="state.totalItems"
      @view="openViewModal"
      @add="openAddModal"
      @page-change="handlePageChange"
      @update-status="handleUpdateStatusClick"
    />
  </BreadcrumbCustom>
  <MajorModal
    :open="state.isModalOpen"
    :majorId="state.majorId"
    :departmentId="props.departmentId || ''"
    :title="state.majorId ? 'Cập nhật chuyên ngành' : 'Thêm mới chuyên ngành'"
    @close="closeModal"
    @success="fetchMajor"
  />
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, watch } from 'vue'
import MajorTable from './MajorTable.vue'
import MajorModal from './MajorModal.vue'
import MajorFilter from './MajorFilter.vue'
import {
  getMajors,
  MajorResponse,
  ParamsGetMajor,
  updateStatusMajor
} from '@/services/api/major.api'
import { debounce } from 'lodash'
import { toast } from 'vue3-toastify'
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import BreadcrumbDefault from '@/components/custom/Div/BreadcrumbDefault.vue'
import BreadcrumbTable from '@/components/custom/Div/BreadcrumbCustom.vue'
import BreadcrumbCustom from '@/components/custom/Div/BreadcrumbCustom.vue'

const state = reactive({
  searchQuery: '',
  isModalOpen: false,
  majorId: null as string | null,
  selectedMajorId: null as string | null,
  major: [] as MajorResponse[],
  paginationParams: { page: 1, size: 10 },
  totalItems: 0
})

const props = defineProps<{ departmentId: string | null; isManageMajorModalOpen: boolean }>()

const fetchMajor = async () => {
  if (!props.departmentId) {
    console.warn(' departmentId đang null, không thể tải danh sách chuyên ngành.')
    return
  }

  try {
    const params: ParamsGetMajor = {
      page: state.paginationParams.page,
      size: state.paginationParams.size,
      majorName: state.searchQuery || null
    }
    const response = await getMajors(params, props.departmentId)
    state.major = response?.data?.data || []
    state.totalItems = response?.data?.totalElements || 0
  } catch (error) {
    console.error('Lỗi khi tải danh sách chuyên ngành:', error)
  }
}

const debouncedFetchMajor = debounce(fetchMajor, 1000)

const updateSearchQuery = (newQuery: string) => {
  state.searchQuery = newQuery.trim()
}

watch(
  () => state.searchQuery,
  () => {
    state.paginationParams.page = 1
    debouncedFetchMajor()
  }
)

watch(
  () => props.departmentId,
  (newId) => {
    fetchMajor()
  }
)

onMounted(fetchMajor)
const emit = defineEmits<{
  (event: 'update:isManageMajorModalOpen', value: boolean): void
}>()

const openAddModal = () => {
  state.majorId = null
  state.isModalOpen = true
  emit('update:isManageMajorModalOpen', false)
}

const openViewModal = (id: string) => {
  state.majorId = id
  state.isModalOpen = true
  emit('update:isManageMajorModalOpen', false)
}

const closeModal = () => {
  state.isModalOpen = false
  emit('update:isManageMajorModalOpen', true)
}

const handlePageChange = ({ page, pageSize }: { page: number; pageSize?: number }) => {
  state.paginationParams.page = page
  if (pageSize) {
    state.paginationParams.size = pageSize
  }
  fetchMajor()
}

const handleUpdateStatus = async (majorId: string) => {
  try {
    await updateStatusMajor(majorId)
    toast.success('Cập nhật trạng thái thành công!')
    fetchMajor()
  } catch (error) {
    toast.error('Cập nhật trạng thái thất bại!')
  }
}

const handleUpdateStatusClick = (majorId: string) => {
  handleUpdateStatus(majorId)
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
