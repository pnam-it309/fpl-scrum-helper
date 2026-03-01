<template>
  <BreadcrumbDefaultV1 :routes="breadcrumbRoutes" marginBottom="mb-2">
    <div class="flex justify-between">
      <div class="w-7/12">
        <CapacityEstimateTable :data-sprint="stageModal.dataSprint" :data="dataEstimateBySprint" />
      </div>
      <div class="w-5/12 pl-5">
        <PointEstimateTable
          :data="state.estimate"
          :pagination-params="state.paginationParams"
          :totalItems="state.totalItems"
          :data-sprint="stageModal.dataSprint"
          @add="handleAddClick"
          @delete="deleteEstimate"
          @view="estimateDetail"
          @page-change="handlePageChange"
        />
        <PointEstimateModal
          :title="'Ước lượng công việc'"
          :open="stageModal.openModal"
          :sprint-has-been="idSprint"
          @success="handleSuccess"
          @close="closeModal"
          :estimate-details="dataEstimateDetail"
          :data-sprint="stageModal.dataSprint"
        />
      </div>
    </div>
  </BreadcrumbDefaultV1>
</template>

<script setup lang="ts">
const breadcrumbRoutes = [
  { name: 'Quản lý xưởng', nameRoute: ROUTES_CONSTANTS.MEMBER.name },
  { name: 'Dự án', nameRoute: ROUTES_CONSTANTS.MEMBER.children.MYPROJECT.name },
  { name: 'Giai đoạn', nameRoute: ROUTES_CONSTANTS.MEMBER.children.TODO.name },
  { name: 'Ước lượng năng lực ', nameRoute: ROUTES_CONSTANTS.MEMBER.children.ESTIMATE_USER.name }
]
import { onMounted, reactive, ref } from 'vue'
import PointEstimateTable from './PointEstimateTable.vue'
import {
  capacityEstimateBySprint,
  detailEstimate,
  getAllEstimate,
  getAllPhaseInEstimate,
  ParamsGetEstimate,
  phaseByEstimateResponse,
  removeEstimate
} from '@/services/api/member/capacityestimate/capacityEstimate.api'
import { useRoute } from 'vue-router'
import PointEstimateModal from './PointEstimateModal.vue'
import { localStorageAction } from '@/utils/storage'
import { USER_INFO_STORAGE_KEY } from '@/constants/storagekey'
import { toast } from 'vue3-toastify'
import BreadcrumbDefault from '@/components/custom/Div/BreadcrumbDefault.vue'
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import CapacityEstimateTable from './CapacityEstimateTable.vue'
import { ROUTES_CONSTANTS } from '@/constants/path'
import BreadcrumbDefaultV1 from '@/components/custom/Div/BreadcrumbDefaultV1.vue'

const state = reactive({
  id: null as string | null,
  estimate: [] as ParamsGetEstimate[],
  paginationParams: { page: 1, size: 10 },
  totalItems: 0
})

const userLogin = localStorageAction.get(USER_INFO_STORAGE_KEY)

const stageModal = reactive({
  openModal: false,
  dataSprint: [] as phaseByEstimateResponse[]
})

const closeModal = () => {
  stageModal.openModal = false
  dataEstimateDetail.idEstimate = ''
  dataEstimateDetail.sprint = null as any
  dataEstimateDetail.availableHours = ''
  dataEstimateDetail.adjustedStoryPoints = ''
  dataEstimateDetail.descriptions = ''
}

const handleAddClick = () => {
  stageModal.openModal = true
}

const handlePageChange = (pagination: any) => {
  state.paginationParams.page = pagination.page
  state.paginationParams.size = pagination.pageSize
  fetchDataEstimate()
}

const handleSuccess = () => {
  console.log('12344')
  fetchDataEstimateBySprint()
  closeModal()
  fetchDataEstimate()
}

const route = useRoute()

const idSprint = ref<Array<string>>()
const fetchDataEstimate = async () => {
  const params: ParamsGetEstimate = {
    idUser: userLogin.userId,
    idProject: route.params.id as string,
    page: state.paginationParams.page,
    size: state.paginationParams.size
  }

  const res = await getAllEstimate(params)

  idSprint.value = res.data.content.map((item) => item.idPhase)

  state.estimate = res.data.content as any
  console.log(state.estimate)
  console.log(res.data.content)

  state.totalItems = res.data.totalElements
  dataSprint()
}

// Lấy dữ liệu sprint
const dataSprint = async () => {
  const res = await getAllPhaseInEstimate(route.params.id as string)
  console.log(res.data)

  stageModal.dataSprint = res.data
}

// Detail estimate

const dataEstimateDetail = reactive({
  idEstimate: '',
  sprint: '',
  workday: '',
  adjustedStoryPoints: '',
  descriptions: ''
})

const estimateDetail = async (id: string, sprintId: string) => {
  // THÊM sprintId VÀO ĐÂY
  console.log('Id của capacity estimate : ===> {}', id)

  const sprint = stageModal.dataSprint.find((s) => s.id === sprintId)

  // Kiểm tra nếu sprint đang DANG_LAM, DA_HOAN_THANH hoặc QUA_HAN
  if (sprint && ['DANG_LAM', 'DA_HOAN_THANH', 'QUA_HAN'].includes(sprint.statusPhase)) {
    toast.warn('Không thể cập nhật ước lượng cho sprint đã kết thúc hoặc đang diễn ra.')
    return // Ngăn không cho mở modal
  }

  // Nếu không, tiếp tục lấy chi tiết và mở modal
  try {
    const res = await detailEstimate(id)
    console.log('detail estimate')
    console.log(res.data)
    dataEstimateDetail.idEstimate = res.data.id as string
    dataEstimateDetail.sprint = res.data.idPhase
    dataEstimateDetail.workday = res.data.workday
    dataEstimateDetail.adjustedStoryPoints = res.data.adjustedStoryPoints
    dataEstimateDetail.descriptions = res.data.description
    stageModal.openModal = true
  } catch (error) {
    toast.error('Không thể lấy chi tiết ước lượng.')
  }
}

const deleteEstimate = async (id: string, sprintId: string) => {
  // THÊM sprintId VÀO ĐÂY
  const sprint = stageModal.dataSprint.find((s) => s.id === sprintId)

  // Kiểm tra nếu sprint đang DANG_LAM, DA_HOAN_THANH hoặc QUA_HAN
  if (sprint && ['DANG_LAM', 'DA_HOAN_THANH', 'QUA_HAN'].includes(sprint.statusPhase)) {
    toast.warn('Không thể xóa ước lượng cho sprint đã kết thúc hoặc đang diễn ra.')
    return // Ngăn không cho xóa
  }

  try {
    const res = await removeEstimate(id)
    console.log(res.data)
    fetchDataEstimate()
    toast.success('Xóa thành công')
  } catch (error) {
    toast.error('Xóa thất bại')
  }
}

const dataEstimateBySprint = ref<any[]>([])

const fetchDataEstimateBySprint = async () => {
  const res = await capacityEstimateBySprint(route.params.id as string)
  dataEstimateBySprint.value = res.data as any
}

onMounted(() => {
  fetchDataEstimate()
  dataSprint()
  fetchDataEstimateBySprint()
})
</script>

<style scoped>
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
