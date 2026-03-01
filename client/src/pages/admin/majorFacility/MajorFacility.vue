<template>
  <BreadcrumbCustom label="Quản lý chuyên ngành theo cơ sở">
    <MajorFacilityFilter :searchQuery="state.searchQuery" @update:searchQuery="updateSearchQuery" />

    <MajorFacilityTable
      :searchQuery="state.searchQuery"
      :paginationParams="state.paginationParams"
      :majorFacility="state.majorFacility"
      :departmentFacilityId="props.departmentFacilityId || ''"
      :totalItems="state.totalItems"
      @view="openViewModal"
      @add="openAddModal"
      @page-change="handlePageChange"
      @update-status="handleUpdateStatusClick"
    />
  </BreadcrumbCustom>
  <MajorFacilityModal
    :open="state.isModalOpen"
    :majorFacilityId="state.majorFacilityId"
    :title="
      state.majorFacilityId
        ? 'Cập nhật chuyên ngành theo cơ sở'
        : 'Thêm mới chuyên ngành theo cơ sở'
    "
    :departmentFacilityId="props.departmentFacilityId || ''"
    @close="closeModal"
    @success="fetchMajorFacility"
  />
</template>

<script setup lang="ts">
import MajorFacilityTable from './MajorFacilityTable.vue'
import MajorFacilityModal from './MajorFacilityModal.vue'
import MajorFacilityFilter from './MajorFacilityFilter.vue'
import { onMounted, reactive, watch } from 'vue'
import {
  getMajorFacility,
  MajorFacilityResponse,
  ParamsGetMajorFacility,
  updateStatusMajorFacility
} from '@/services/api/majorFacility.api'
import { debounce } from 'lodash'
import BreadcrumbDefault from '@/components/custom/Div/BreadcrumbDefault.vue'
import { toast } from 'vue3-toastify'
import BreadcrumbCustom from '@/components/custom/Div/BreadcrumbCustom.vue'

const props = defineProps<{
  departmentFacilityId: string | null
  isMajorFacilityModalOpen: boolean
}>()

const state = reactive({
  searchQuery: '',
  isModalOpen: false,
  majorFacilityId: null as string | null,
  selectedMajorFacilityId: null as string | null,
  majorFacility: [] as MajorFacilityResponse[],
  paginationParams: { page: 1, size: 10 },
  totalItems: 0
})

const fetchMajorFacility = async () => {
  if (!props.departmentFacilityId) {
    return
  }

  try {
    const params: ParamsGetMajorFacility = {
      page: state.paginationParams.page,
      size: state.paginationParams.size,
      majorName: state.searchQuery || null
    }
    const response = await getMajorFacility(params, props.departmentFacilityId)
    state.majorFacility = response?.data?.data || []
    state.totalItems = response?.data?.totalElements || 0
  } catch (error) {
    console.error('Lỗi khi tải danh sách chuyên ngành theo cơ sở:', error)
  }
}

watch(
  () => props.departmentFacilityId,
  (newVal, oldVal) => {
    if (newVal) {
      fetchMajorFacility()
    }
  }
)

onMounted(fetchMajorFacility)

const debouncedFetchMajorFacility = debounce(fetchMajorFacility, 1000)

const updateSearchQuery = (newQuery: string) => {
  state.searchQuery = newQuery.trim()
}

watch(
  () => state.searchQuery,
  () => {
    state.paginationParams.page = 1
    debouncedFetchMajorFacility()
  }
)

const emit = defineEmits<{
  (event: 'update:isMajorFacilityModalOpen', value: boolean): void
}>()

const openAddModal = () => {
  state.majorFacilityId = null
  state.isModalOpen = true
  emit('update:isMajorFacilityModalOpen', false)
}

const openViewModal = (id: string) => {
  state.majorFacilityId = id
  state.isModalOpen = true
  emit('update:isMajorFacilityModalOpen', false)
}

const closeModal = () => {
  state.isModalOpen = false
  emit('update:isMajorFacilityModalOpen', true)
}

const handlePageChange = ({ page, pageSize }: { page: number; pageSize?: number }) => {
  state.paginationParams.page = page
  if (pageSize) {
    state.paginationParams.size = pageSize
  }
  fetchMajorFacility()
}

const handleUpdateStatus = async (id: string) => {
  try {
    await updateStatusMajorFacility(id)
    toast.success('Cập nhật trạng thái thành công!')
    fetchMajorFacility()
  } catch (error) {
    toast.error('Cập nhật trạng thái thất bại!')
  }
}

const handleUpdateStatusClick = (id: string) => {
  handleUpdateStatus(id)
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
