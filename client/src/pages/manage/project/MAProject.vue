<template>
  <BreadcrumbDefaultV1 :routes="breadcrumbRoutes" label="Dự án mà bạn đang phụ trách">
    <MAProjectFilter
      :searchQuery="data.searchQuery"
      @update:searchQuery="dataSearch"
      :department="data.nameDepartmentSearch"
      :status="data.status"
      @update:search-department="dataSearchDepartment"
      @update:searchStatus="dataSearchStatus"
    ></MAProjectFilter>

    <MAProjectTable
      :data="data?.dataProject"
      :size="data.size"
      :total-items="data.totalItems"
      :page="data.page"
      @page-change="handlePageChange"
      @detail=""
      @view="openViewModal"
      @open-sumary-modal="openSumaryModal"
      @download-template-import="downloadTemplateExcel"
      @open-modal-import="handleOpenModalImport"
      @import-log=""
      @download-import-log="downloadImportLogExcel"
      
    >
    </MAProjectTable>
    
    <MAProjectModal
      :id-facility-project="idFacilityProject"
      :open="data.isModalOpen"
      :projectId="data.selectedProjectId"
      :title="modalTitle"
      @add="openAddModal"
      @close="closeModal"
    >
    </MAProjectModal>
  </BreadcrumbDefaultV1>

  <MAProjectSumaryModal
    :isOpen="isOpenSummaryModal"
    :idProject="idProjectSummary"
    @close="closeModalSumary"
    @success="getAllDataProject()"
  />

  <ReportCompensationImport
    :open="data.isOpenModalImport"
    :title="'Import Báo cáo bù'"
    :projectId="data.selectedProjectId "
    @close="handleCloseModalImport"
    @fetch-all=""
    @update-log="handleNewLog"
  />
</template>

<script setup lang="ts">
const breadcrumbRoutes = [
  { name: 'Quản lý xưởng', nameRoute: ROUTES_CONSTANTS.MANAGE.name },
    { name: 'Tổng quan dự án', nameRoute: ROUTES_CONSTANTS.MANAGE.children.PROJECT.name },

]
import { computed, onMounted, reactive, ref, watch } from 'vue'
import MAProjectTable from './MAProjectTable.vue'
import MAProjectFilter from './MAProjectFilter.vue'
import MAProjectModal from './MAProjectModal.vue'
import {
  dowloadExcel,
  downloadCSV,
  getAllProject,
  projectRequest,
  projectResponse
} from '@/services/api/manage/project/maproject.api'
import { debounce } from 'lodash'
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import { localStorageAction } from '@/utils/storage'
import { USER_INFO_STORAGE_KEY } from '@/constants/storagekey'
import MAProjectSumaryModal from './MAProjectSummaryModal.vue'
import { useRoute } from 'vue-router'
import BreadcrumbDefault from '@/components/custom/Div/BreadcrumbDefault.vue'
import ReportCompensationImport from './ReportCompensationImport.vue'
import { ROUTES_CONSTANTS } from '@/constants/path'
import BreadcrumbDefaultV1 from '@/components/custom/Div/BreadcrumbDefaultV1.vue'

const data = reactive({
  searchQuery: '',
  isModalOpen: false,
  selectedProjectId: null as string | null,
  dataProject: [] as projectResponse[],
  nameDepartmentSearch: '',
  isOpenModalImport: false,
  page: 1,
  size: 10,
  status: '',
  totalItems: 0
})

const user = localStorageAction.get(USER_INFO_STORAGE_KEY)
onMounted(() => {
  console.table(user)
})

const idFacilityProject = ref()

const openAddModal = () => {
  data.selectedProjectId = null
  data.isModalOpen = true
}

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
const userLogin = localStorageAction.get(USER_INFO_STORAGE_KEY)
const getAllDataProject = async () => {
  try {
    const params: projectRequest = {
      page: data.page,
      size: data.size,
      search: data.searchQuery,
      status: data.status,
      idUser: userLogin.userId,
      nameDepartment: data.nameDepartmentSearch
    }
    const response = await getAllProject(params)

    data.dataProject = response.data.content || []

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

const debouncedFetchProject = debounce(getAllDataProject, 300)

watch(
  () => [data.searchQuery, data.nameDepartmentSearch, data.status],

  () => {
    data.page = 1
    debouncedFetchProject()
  }
)

const downloadTemplateExcel = async () => {
  try {
    const response = await dowloadExcel()
    const blob = new Blob([response], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    })
    const url = URL.createObjectURL(blob)

    const link = document.createElement('a')
    link.href = url
    link.download = 'template_import_report_compensation.xlsx'
    link.click()

    URL.revokeObjectURL(url)
  } catch (error) {
    console.error('Failed to download template:', error)
  }
}

const downloadImportLogExcel = async () => {
  try {
    const blob = await downloadCSV()

    const url = window.URL.createObjectURL(new Blob([blob]))

    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', 'import-log.csv')
    document.body.appendChild(link)
    link.click()

    // Dọn dẹp URL sau khi tải xong
    window.URL.revokeObjectURL(url)
    link.remove()
  } catch (error) {
    console.error('Lỗi khi tải file:', error)
  }
}

const handleOpenModalImport = () => {
  data.isOpenModalImport = true
}

const handleCloseModalImport = () => {
  data.isOpenModalImport = false
}
const logRef = ref()


const handleNewLog = (newEntry) => {
  if (logRef.value) {
    logRef.value.addNewLogEntry(newEntry) // Thêm log mới vào bảng Import Log
  }
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
