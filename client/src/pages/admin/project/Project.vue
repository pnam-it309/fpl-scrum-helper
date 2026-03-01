<template>
  <BreadcrumbDefaultV1 label="Quản lý dự án" :routes="breadcrumbRoutes">
    <ProjectFilter
      :searchQuery="data.searchQuery"
      @update:searchQuery="dataSearch"
      :department="data.nameDepartmentSearch"
      :status="data.status"
      @update:search-department="dataSearchDepartment"
      @update:searchStatus="dataSearchStatus"
    ></ProjectFilter>

    <ProjectTable
      :data="data?.dataProject"
      :size="data.size"
      :total-items="data.totalItems"
      :page="data.page"
      @page-change="handlePageChange"
      @delete="removeProject"
      @view="openViewModal"
      @add="openAddModal"
      @selection-change="handleSelectionChange"
      @open-sumary-modal="openSumaryModal"
    >
    </ProjectTable>
    <ProjectModal
      :id-facility-project="idFacilityProject"
      :open="data.isModalOpen"
      :project-id="data.selectedProjectId"
      :title="modalTitle"
      @add="openAddModal"
      @close="closeModal"
    >
    </ProjectModal>

  <MAProjectSummaryModal
    :isOpen="isOpenSummaryModal"
    :idProject="idProjectSummary"
    @close="closeModalSumary"
    @success="getAllDataProject()"
  />

  </BreadcrumbDefaultV1>
</template>

<script setup lang="ts">
const breadcrumbRoutes = [
  { name: 'Quản trị hệ thống', nameRoute: ROUTES_CONSTANTS.ADMIN.name },
  { name: 'Dự án ', nameRoute: ROUTES_CONSTANTS.ADMIN.children.PROJECT.name },
]
import { computed, onMounted, reactive, ref, watch } from 'vue'
import ProjectTable from './ProjectTable.vue'
import { useRouter } from 'vue-router'
import { LineChartOutlined } from '@ant-design/icons-vue'
import {
  deleteProject,
  getAllProject,
  projectRequest,
  projectResponse
} from '@/services/api/admin/project/project.api'
import ProjectFilter from './ProjectFilter.vue'
import { debounce } from 'lodash'
import { Modal } from 'ant-design-vue'
import { toast } from 'vue3-toastify'
import ProjectModal from './ProjectModal.vue'
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import { localStorageAction } from '@/utils/storage'
import { USER_INFO_STORAGE_KEY } from '@/constants/storagekey'
import { formatDate, formatDateNVV, getDateFormat } from '@/utils/commom.helper'
import BreadcrumbDefault from '@/components/custom/Div/BreadcrumbDefault.vue'
import MAProjectSummaryModal from '@/pages/manage/project/MAProjectSummaryModal.vue'
import BreadcrumbDefaultV1 from '@/components/custom/Div/BreadcrumbDefaultV1.vue'
import { ROUTES_CONSTANTS } from '@/constants/path'

const router = useRouter()

const data = reactive({
  searchQuery: '',
  isModalOpen: false,
  selectedProjectId: null as string | null,
  dataProject: [] as projectResponse[],
  nameDepartmentSearch: '',
  page: 1,
  size: 10,
  status: '',
  totalItems: 0
})

const idFacilityProject = ref()

const modalTitle = computed(() => {
  return data.selectedProjectId ? 'Cập nhật dự án' : 'Thêm dự án'
})

const dataSearch = (search: string) => {
  data.searchQuery = search
}

const dataSearchDepartment = (search: string) => {
  data.nameDepartmentSearch = search
}

const dataSearchStatus = (search: string) => {
  data.status = search
}

const openAddModal = () => {
  data.selectedProjectId = null
  data.isModalOpen = true
}

const openViewModal = (id: string, idFacility: string) => {
  data.selectedProjectId = id
  idFacilityProject.value = idFacility
  data.isModalOpen = true
}

const closeModal = () => {
  data.isModalOpen = false
  getAllDataProject()
}

onMounted(() => {
  getAllDataProject()
})

const getAllDataProject = async () => {
  try {
    const params: projectRequest = {
      page: data.page,
      size: data.size,
      search: data.searchQuery.trim(),
      status: data.status,
      nameDepartment: data.nameDepartmentSearch.trim()
    }
    const response = await getAllProject(params)

    data.dataProject = response.data.content || []

    for (const project of data.dataProject) {
      project.startTime = getDateFormat(+project.startTime, false)
      project.endTime = getDateFormat(+project.endTime, false)
    }

    console.log(data.dataProject)

    data.totalItems = response.data.totalElements
  } catch (error) {
    console.error('Failed to fetch project:', error)
  }
}

const handlePageChange = ({ page, pageSize }: { page: number; pageSize?: number }) => {
  data.page = page
  if (pageSize) {
    data.size = pageSize
  }
  getAllDataProject()
}

const goToStatistics = () => {
  router.push({ name: 'project-statistics' })
}

const debouncedFetchProject = debounce(getAllDataProject, 300)

watch(
  () => [data.searchQuery, data.nameDepartmentSearch, data.status],

  () => {
    data.page = 1
    debouncedFetchProject()
  }
)

const removeProject = (id) => {
  let res
  Modal.confirm({
    title: 'Xác nhận xóa dự án ',
    content: 'Bạn có chắc chắn muốn xóa dự án này không?',
    okText: 'Xóa  dự án',
    cancelText: 'Hủy',
    okType: 'danger',
    onOk: async () => {
      try {
        res = await deleteProject(id)
        toast.success(res.message)
        getAllDataProject()
      } catch (error) {
        toast.error(res.message)
      }
    }
  })
}
const handleSelectionChange = ({ keys, rows }) => {
  console.log('Selected keys:', keys)
  console.log('Selected rows:', rows)
}



const isOpenSummaryModal = ref<boolean>(false)
const idProjectSummary = ref<string>('')

const openSumaryModal = (idProject: string) => {
  isOpenSummaryModal.value = true
  idProjectSummary.value = idProject
}

const closeModalSumary = () => {
  isOpenSummaryModal.value = false
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
