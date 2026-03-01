<template>
  <DivCustomOld label="Quản lý báo cáo dự án">
    <div class="flex justify-between">
      <div>
        <h1 class="font-semibold pl-2">Tên dự án : {{ valueProject?.name }}</h1>

        <div class="flex items-center">
          <h1 class="font-semibold pl-2">Thành viên :</h1>
          <a-avatar-group :max-count="3" :max-style="{ color: '#f56a00', backgroundColor: '#fde3cf' }" class="ml-6">
            <a-tooltip v-for="(user, index) in dataStaffProject" :key="index" :title="user.staff == null
                ? user.student.name + ' - Thành viên'
                : user.staff.name +
                ` ${user.role.substring(0, 1) + user.role.substring(1).toLowerCase()}`
              " placement="top">
              <a-avatar :src="user.staff == null ? user.student.image : user.staff.picture" />
            </a-tooltip>
          </a-avatar-group>
        </div>

        <div>

          <a-button @click="openModalOptions">
            <MenuOutlined /> Tùy chọn
          </a-button>

          <a-button type="primary" @click="isSetupTimeVisible = true" style="margin-left: 5px;">
            Set up thời gian dừng báo cáo
          </a-button>

          <a-button type="primary" style="margin-left: 5px;" @click="openHolidayModal">
            Tạo ngày nghỉ
          </a-button>

           <a-modal v-model:open="isSetupTimeVisible" title="Chọn giờ dừng báo cáo" @ok="confirmStopTime"
            @cancel="isSetupTimeVisible = false">
            <a-time-picker
              v-model:value="stopReportTime"
              format="HH:mm"
              :minute-step="1"
              :use12-hours="false"
              :hide-disabled-options="false"
              :default-value="dayjs().hour(23).minute(0)"
            />
          </a-modal>

          <a-modal v-model:visible="showModalOptions" title="Tùy chọn xuất/import" centered :width="400" :footer="null">
            <div class="modal-options">
              <a-typography-link @click="openModalExportReport">
                <DownloadOutlined /> Xuất báo cáo
              </a-typography-link>
              <a-typography-link @click="handleOpenModalImport">
                <UploadOutlined /> Import báo cáo bù
              </a-typography-link>
              <a-typography-link @click="downloadTemplateExcel">
                <FileOutlined /> Download template
              </a-typography-link>
              <a-typography-link @click="handleOpenModalImportHistory">
                <HistoryOutlined /> Lịch sử
              </a-typography-link>
              <a-typography-link @click="downloadImportLogExcel">
                <DownloadOutlined /> Download lỗi import
              </a-typography-link>
            </div>
          </a-modal>

        </div>

      </div>
      <div class="chart-container max-w-72.5 -mt-20">
        <div v-if="loading" class="flex justify-center items-center h-full">
          <a-spin />
        </div>
        <template v-else>
          <Pie :data="chartData" :options="pieOptions" />
        </template>
      </div>
    </div>
    <a-alert :message="`Ngày : ${selectedDate?.format('YYYY-MM-DD')}`" />
    <a-calendar v-model:value="value" @select="onSelect">
      <template #dateCellRender="{ current }">
        <ul class="events">
          <li v-for="item in getListData(current)" :key="item.content">
            <a-badge :status="item.type" :text="item.content" />
          </li>
        </ul>
      </template>
      <template #monthCellRender="{ current }">
        <div v-if="getMonthData(current)" class="notes-month">
          <section>{{ getMonthData(current) }}</section>
          <span>Backlog number</span>
        </div>
      </template>
    </a-calendar>
  </DivCustomOld>

  <MADetailReport :open="dataModalReport.isModalOpen" :title="dataModalReport.title" :data="dataModalReport.dataReport"
    :page="dataModalReport.page" :size="dataModalReport.size" :totalItems="dataModalReport.totalItems"
    :time="dataModalReport.time" @close="closeModalReport" />

  <ReportCompensationImport :open="data.isOpenModalImport" :title="'Import Báo cáo bù'" :projectId="projectId"
    @close="handleCloseModalImport" @fetch-all-history="fetchHistory" @fetch-all="" @update-log="handleNewLog" />

  <a-modal v-model:visible="showModal" title="Xuất báo cáo " centered :width="'50%'" :footer="null">
    <MAUserReport />
  </a-modal>

  <HistoryImportModalTable :open="data.isOpenModalImportHistory" :history="data.history" :paginationParams="{
    page: data.paginationParamsHistory.page,
    size: data.paginationParamsHistory.size
  }" :totalItems="data.totalItemsHistory" @close="handleCloseModalImportHistory"
    @page-change="handlePageChangeTodoLog" />


    <a-modal
  v-model:open="isHolidayModalVisible"
  title="Tạo ngày nghỉ"
  :footer="null"
  @cancel="closeHolidayModal"
  centered
  :width="600"
>
  <HolidayModal @close="closeHolidayModal" />
</a-modal>

</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { onMounted, reactive, watch } from 'vue'
import type { Dayjs } from 'dayjs'
import { getAllStaffProject } from '@/services/api/manage/todo/todo.api'
import { useRoute, useRouter } from 'vue-router'
import { detailProject, projectDetailResponse } from '@/services/api/admin/project/project.api'
import { Pie } from 'vue-chartjs'
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
  ArcElement
} from 'chart.js'
import dayjs from 'dayjs'
import MADetailReport from './MADetailReport.vue'
import DivCustomOld from '@/components/custom/Div/DivCustomOld.vue'
import {
  exportReport,
  findAllReport,
  getReport,
  reportResponse
} from '@/services/api/manage/report/report.api'
import MAUserReport from './MAUserReport.vue'
import {
  dowloadExcel,
  downloadCSV,
  getAllHistory,
  historyImprort
} from '@/services/api/manage/project/maproject.api'
import ReportCompensationImport from '../project/ReportCompensationImport.vue'
import HistoryImportTodoModalTable from '../todolist/HistoryImportTodoModalTable.vue'
import HistoryImportModalTable from './HistoryImportModalTable.vue'
import {
  DownloadOutlined,
  FileOutlined,
  HistoryOutlined,
  UploadOutlined
} from '@ant-design/icons-vue'
import { updateReportSetting } from '@/services/api/member/report/report.api'
import { toast } from 'vue3-toastify'
import HolidayModal from './HolidayModal.vue'

const showModalOptions = ref(false)

const openModalOptions = () => {
  showModalOptions.value = true
}

const isSetupTimeVisible = ref(false)

const isHolidayModalVisible = ref(false)

const openHolidayModal = () => {
  isHolidayModalVisible.value = true
}

const closeHolidayModal = () => {
  isHolidayModalVisible.value = false
}


const stopReportTime = ref<Dayjs>(dayjs().hour(23).minute(0))

const confirmStopTime = async () => {
  // Lấy ngày hiện tại
  const selectedDate = dayjs()

  // Lấy giờ và phút từ người dùng chọn
  const hour = stopReportTime.value.hour()
  const minute = stopReportTime.value.minute()

  // Tạo timestamp: same date, set hour + minute, convert to millis
  const stopTimestamp = selectedDate.hour(hour).minute(minute).second(0).millisecond(0).valueOf()

  isSetupTimeVisible.value = false

  try {
    const response = await updateReportSetting(route.params.id as string, {
      stopReportHour: stopTimestamp // 👈 timestamp dạng millis
    })

    if (response?.data) {
      toast.success('Cập nhật giờ dừng báo cáo thành công!')
    } else {
      toast.warning('⚠ Không thể cập nhật giờ dừng báo cáo')
    }
  } catch (error) {
    console.error('Lỗi khi cập nhật giờ dừng báo cáo:', error)
    toast.error('Lỗi khi cập nhật giờ dừng!')
  }
}




// Đăng ký các components
ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend, ArcElement)
const useReportSuccess = ref<number>(0)
const useReportNotSuccess = ref<number>(0)
const chartData = ref({
  labels: ['Đã báo cáo', 'Chưa báo cáo'],
  datasets: [
    {
      label: 'Thống kê báo cáo',
      data: [useReportSuccess.value, useReportNotSuccess.value],
      backgroundColor: ['rgb(54, 162, 235)', 'rgb(255, 99, 132)'],
      hoverOffset: 4
    }
  ]
})

const pieOptions = {
  responsive: true,
  maintainAspectRatio: false,
  animation: {
    duration: 500
  },
  plugins: {
    legend: {
      position: 'bottom' as const,
      labels: {
        boxWidth: 12,
        padding: 15,
        font: {
          size: 12
        }
      }
    },
    title: {
      display: true,
      text: 'Thống kê báo cáo',
      font: {
        size: 14
      }
    }
  }
}

const dataModalReport = reactive({
  searchQuery: '',
  isModalOpen: false,
  dataReport: [] as reportResponse[],
  title: '',
  page: 1,
  size: 10000,
  status: '',
  totalItems: 0,
  time: 0
})

const showModal = ref(false)

// Hàm mở modal
const openModalExportReport = () => {
  showModal.value = true
}

const openModalReport = () => {
  dataModalReport.isModalOpen = true
}

const closeModalReport = () => {
  dataModalReport.isModalOpen = false
}

const getListData = (value: Dayjs) => {
  let listData: { type: string; content: string }[] = []

  const today = dayjs().format('YYYY-MM-DD') // Lấy ngày hôm nay
  const dateKey = value.format('YYYY-MM-DD')

  // Cập nhật dữ liệu cho ngày hôm nay
  if (dateKey === today) {
    const successReports = reportData.value[dateKey]?.success || 0
    const notSuccessReports = reportData.value[dateKey]?.notSuccess || 0

    listData.push({
      type: 'success',
      content: `Đã báo cáo: ${successReports}`
    })

    // Thêm trạng thái cho số người chưa báo cáo
    listData.push({
      type: 'warning',
      content: `Chưa báo cáo: ${notSuccessReports}`
    })
  }

  return listData
}

const loading = ref<boolean>(true)
onMounted(async () => {
  try {
    console.log(chartData.value.datasets[0].data)

    chartData.value = {
      ...chartData.value,
      datasets: [
        {
          ...chartData.value.datasets[0],
          data: [useReportSuccess.value, useReportNotSuccess.value]
        }
      ]
    }
  } catch (error) {
    console.error(' Lỗi khi tải dữ liệu:', error)
  } finally {
    loading.value = false
  }
})
const getMonthData = (value: Dayjs) => {
  // if (value.month() === 8) {
  //   return 1394
  // }
}

const value = ref<Dayjs>(dayjs())
const selectedDate = ref<Dayjs | null>(dayjs())
const emit = defineEmits(['data-report'])
const selectedTime = ref<number>()

const data = reactive({
  searchQuery: '',
  isModalOpen: false,
  selectedProjectId: null as string | null,
  nameDepartmentSearch: '',
  dataReport: [] as reportResponse[],
  isOpenModalImport: false,
  page: 1,
  size: 100,
  status: '',
  totalItems: 0,
  isOpenModalImportHistory: false,
  history: [] as historyImprort[],
  totalItems: 0,
  totalItemsHistory: 0,
  paginationParamsHistory: { page: 1, size: 10 }
})
const dataStaffProject = ref<[]>([])

const valueProject = ref<projectDetailResponse>()
const param = reactive({})
const dataReport = ref<reportResponse[]>([])
const route = useRoute()

const fetchDataReport = async () => {
  const resData = await getAllStaffProject(idProject.value)
  dataStaffProject.value = resData.data as []
  console.log(dataStaffProject.value)

  const resDetail = await detailProject(idProject.value)
  valueProject.value = resDetail.data
  const startDate = dayjs(resDetail.data.startTime)
  const endDate = dayjs()

  // Fetch toàn bộ dữ liệu báo cáo
  const resReports = await findAllReport({
    idProject: idProject.value
  })

  // Tạo Map chứa danh sách report theo ngày
  const reportsByDate: Record<string, reportResponse[]> = {}

  resReports.data.content.forEach((item: reportResponse) => {
    const dateKey = dayjs(item.createdAt).format('YYYY-MM-DD')
    if (!reportsByDate[dateKey]) {
      reportsByDate[dateKey] = []
    }
    reportsByDate[dateKey].push(item)
  })

  // Lặp qua từng ngày và khởi tạo reportData
  for (let date = startDate; date.isBefore(endDate.add(1, 'day')); date = date.add(1, 'day')) {
    const dateKey = date.format('YYYY-MM-DD')
    const reports = reportsByDate[dateKey] || []

    let success = 0
    let notSuccess = 0

    if (reports.length > 0) {
      reports.forEach((report) => {
        if (report.reportTime == null) {
          notSuccess++
        } else {
          success++
        }
      })
    } else {
      // Không có dữ liệu nghĩa là chưa báo cáo
      notSuccess = 1
    }

    reportData.value[dateKey] = {
      success,
      notSuccess
    }
  }

  // Cập nhật biểu đồ hôm nay
  const todayKey = dayjs().format('YYYY-MM-DD')
  chartData.value = {
    ...chartData.value,
    datasets: [
      {
        ...chartData.value.datasets[0],
        data: [
          reportData.value[todayKey]?.success || 0,
          reportData.value[todayKey]?.notSuccess || 0
        ]
      }
    ]
  }
}

const idProject = ref('')

onMounted(() => {
  idProject.value = route.params.id as string
  fetchDataReport()
  onSelect(dayjs()) // Cập nhật dữ liệu cho ngày hôm nay
})

const router = useRouter()

const reportData = ref<{ [key: string]: { success: number; notSuccess: number } }>({})

const onSelect = async (date: Dayjs) => {
  openModalReport()

  try {
    loading.value = true
    selectedDate.value = date
    selectedTime.value = date.valueOf()
    dataModalReport.time = selectedTime.value
    // Reset counter cho ngày được chọn
    const dateKey = date.format('YYYY-MM-DD')
    reportData.value[dateKey] = { success: 0, notSuccess: 0 }
    const res = await getReport({
      page: data.page,
      size: data.size,
      time: selectedTime.value,
      idProject: idProject.value
    })

    dataModalReport.dataReport = res.data.content

    // Đếm số lượng
    res.data.content.forEach((item) => {
      if (item.reportTime == null) {
        reportData.value[dateKey].notSuccess++
      } else {
        reportData.value[dateKey].success++
      }
    })

    // Cập nhật chart data
    chartData.value = {
      ...chartData.value,
      datasets: [
        {
          ...chartData.value.datasets[0],
          data: [reportData.value[dateKey].success, reportData.value[dateKey].notSuccess]
        }
      ]
    }

    console.log('Chart data updated:', chartData.value.datasets[0].data)
  } catch (error) {
    console.error('Lỗi:', error)
  } finally {
    loading.value = false
  }
}

watch(
  [useReportSuccess, useReportNotSuccess],
  ([success, notSuccess]) => {
    chartData.value = {
      ...chartData.value,
      datasets: [
        {
          ...chartData.value.datasets[0],
          data: [success, notSuccess]
        }
      ]
    }
  },
  { immediate: true }
)

const paramsTodo = reactive({
  page: 1,
  size: 10,
  totalItem: '',
  idProject: '',
  search: ''
})

// Hàm format ngày để hiển thị
const formatDate = (date: Dayjs) => {
  return date.format('DD/MM/YYYY')
}

//baos cao

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

const projectId = route.params.id as string
console.log('projectId:', projectId)

const fetchHistory = async () => {
  try {
    const response = await getAllHistory()

    console.log('Response data:', response)

    if (response?.data?.data) {
      data.history = response.data.data
      data.totalItemsHistory = response.data.totalElements
    } else {
      data.history = []
      data.totalItems = 0
    }
  } catch (error) {
    console.error('Lỗi khi tải lịch sử import:', error)
  }
}

onMounted(fetchHistory)

const handlePageChangeTodoLog = (pagination: any) => {
  data.paginationParamsHistory.page = pagination.page
  data.paginationParamsHistory.size = pagination.pageSize
  fetchHistory()
}

const handleOpenModalImportHistory = () => {
  data.isOpenModalImportHistory = true
}

const handleCloseModalImportHistory = () => {
  data.isOpenModalImportHistory = false
}
</script>

<style scoped>
.events {
  list-style: none;
  margin: 0;
  padding: 0;
}

.events .ant-badge-status {
  overflow: hidden;
  white-space: nowrap;
  width: 100%;
  text-overflow: ellipsis;
  font-size: 12px;
}

.notes-month {
  text-align: center;
  font-size: 28px;
}

.notes-month section {
  font-size: 28px;
}

.chart-container {
  width: 250px;
  height: 250px;
  padding: 10px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  justify-content: center;
}

/* Thêm style cho loading state */
.chart-container .ant-spin-spinning {
  margin: auto;
}

/* Áp dụng gradient cho nút */
:deep(.ant-btn) {
  background: var(--selected-header) !important;
  /* Gradient nền */
  color: var(--selected-text) !important;
  /* Màu chữ trắng */
  border-color: var(--selected-header) !important;
  /* Viền màu từ gradient */
}

/* Hiệu ứng hover */
:deep(.ant-btn:hover),
:deep(.ant-btn:focus) {
  background: var(--selected-header-hover) !important;
  /* Gradient khi hover */
  border-color: var(--selected-header-hover) !important;
  /* Viền khi hover */
}

.modal {
  background-color: rgba(0, 0, 0, 0.5);
  /* Nền mờ */
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-content {
  background-color: #fff;
  padding: 16px;
  border-radius: 8px;
  width: 300px;
  /* Điều chỉnh độ rộng phù hợp với ảnh */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  font-family: Arial, sans-serif;
}

.modal-content .ant-modal-title {
  font-size: 16px;
  font-weight: 500;
  color: #000;
  text-align: center;
}

.modal-content .ant-modal-close {
  top: 8px;
  right: 8px;
}

.modal-content .option-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.modal-content .option-list a {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #03070a;
  /* Màu xanh cho liên kết, giống ảnh */
  text-decoration: none;
  padding: 8px;
  border-radius: 4px;
  font-size: 14px;
}

.modal-content .option-list a:hover {
  background-color: #e6f7ff;
  color: #000000;
}

.modal-content .option-list a svg {
  font-size: 16px;
  color: #000000;
}

:deep(.ant-modal) {
  background-color: rgba(0, 0, 0, 0.5);
  /* Nền mờ */
}

:deep(.ant-modal-content) {
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  width: 400px !important;
  /* Đảm bảo độ rộng cố định */
}

:deep(.ant-modal-title) {
  text-align: center;
  font-size: 16px;
  font-weight: 500;
  color: #000;
}

:deep(.ant-modal-close) {
  top: 8px;
  right: 8px;
}

/* Nội dung bên trong modal */
.modal-options {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 16px;
}

.modal-options a {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #000;
  /* Thay đổi thành màu đen ban đầu */
  text-decoration: none;
  padding: 8px;
  border-radius: 4px;
  font-size: 14px;
}

.modal-options a:hover {
  background-color: #f0f0f0;
  /* Nền xám nhẹ khi hover */
  color: #000;
  /* Chữ vẫn màu đen khi hover (không thay đổi) */
}

.modal-options a svg {
  font-size: 16px;
  color: #1890ff;
  /* Giữ màu xanh cho biểu tượng nếu muốn */
}
</style>
