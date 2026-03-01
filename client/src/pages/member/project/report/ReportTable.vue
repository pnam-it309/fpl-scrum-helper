<template>
  <div class="calendar-wrapper">
    <div class="calendar-container">
      <FullCalendar :options="calendarOptions" />
    </div>

    <a-drawer
      v-model:open="isModalVisible"
      :title="reportId ? `Chỉnh sửa báo cáo ngày: ${selectedDate}` : `Báo cáo ngày: ${selectedDate}`"
      placement="right"
      :width="500"
      :closable="true"
      @close="closeModal"
      class="custom-drawer"
    >
      <template #extra>
        <a-button v-if="!isPastDate" type="primary" @click="submitReport">Lưu</a-button>
      </template>

      <div class="modal-content">
        <a-form layout="vertical" ref="reportForm" :model="report" :rules="rules">
          <a-card class="report-card" hoverable>
            <a-form-item
              label="Hôm nay bạn đã làm gì?"
              name="workDoneToday"
              class="form-item"
            >
              <a-textarea
                v-model:value="report.workDoneToday"
                rows="4"
                placeholder="Nhập nội dung công việc bạn đã hoàn thành hôm nay..."
                class="custom-textarea"
              />
            </a-form-item>
          </a-card>

          <a-card class="report-card" hoverable>
            <a-form-item
              label="Bạn gặp khó khăn gì?"
              name="obstaclesReport"
              class="form-item"
            >
              <a-textarea
                v-model:value="report.obstaclesReport"
                rows="4"
                placeholder="Mô tả các khó khăn hoặc vấn đề bạn gặp phải..."
                class="custom-textarea"
              />
            </a-form-item>

            <p style="display: inline-flex; align-items: center; gap: 6px; margin: 0; font-style: italic;">
              <input
                type="checkbox"
                v-model="check"
                style="width:15px; height:15px; cursor:pointer; accent-color: black; outline: none !important; box-shadow: none !important;"
              />
              Bạn có cần giúp đỡ không?
            </p>

            <!-- <p>report.help: {{ report.help }}</p>
            <p>check: {{ check }}</p> -->

          </a-card>

          <a-card class="report-card" hoverable>
            <a-form-item
              label="Kế hoạch ngày mai?"
              name="workPlanTomorrow"
              class="form-item"
            >
              <a-textarea
                v-model:value="report.workPlanTomorrow"
                rows="4"
                placeholder="Lập kế hoạch công việc cho ngày mai..."
                class="custom-textarea"
              />
            </a-form-item>
          </a-card>
        </a-form>
      </div>
    </a-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch, computed } from 'vue'
import FullCalendar from '@fullcalendar/vue3'
import dayGridPlugin from '@fullcalendar/daygrid'
import interactionPlugin from '@fullcalendar/interaction'
import {
  addReport,
  updateReport,
  detailReport,
  getReportIdByDate,
  getAllReport,
  getReportSetting
} from '@/services/api/member/report/report.api.ts'
import { toast } from 'vue3-toastify'
import { Modal } from 'ant-design-vue'
import { useRoute } from 'vue-router'

const isModalVisible = ref(false)
const isPastDate = ref(true)
const selectedDate = ref(null)
const reportId = ref(null)
const report = reactive({ workDoneToday: '', obstaclesReport: '', workPlanTomorrow: '' , help: ''})
const reportForm = ref()
const route = useRoute()


const props = defineProps<{ idReport: string | null }>()
const emit = defineEmits(['update-report-list'])

const projectId = ref<string | null>(route.params.id ? String(route.params.id) : null)

const rules = reactive({
  workDoneToday: [{ required: true, message: 'Nội dung không được để trống!', transform: value => value?.trim() }],
  obstaclesReport: [{ required: true, message: 'Nội dung không được để trống!', transform: value => value?.trim() }],
  workPlanTomorrow: [{ required: true, message: 'Nội dung không được để trống!', transform: value => value?.trim() }]
})

const calendarOptions = ref({
  plugins: [dayGridPlugin, interactionPlugin],
  initialView: 'dayGridMonth',
  locale: 'vi',
  headerToolbar: {
    left: 'prev,next today',
    center: 'title',
    right: ''
  },
  selectable: true,
  editable: false,
  events: [],
  eventDisplay: 'block',

  dateClick: async (info) => {
    await openReportModalForDate(info.dateStr)
  },

  eventClick: async (info) => {
    await openReportModalForDate(info.event.startStr)
  }
})

const openReportModalForDate = async (dateStr) => {
  const clickedDate = new Date(dateStr)
  clickedDate.setHours(0, 0, 0, 0)

  const today = new Date()
  today.setHours(0, 0, 0, 0)

  isPastDate.value = clickedDate < today

  if (clickedDate > today) {
    toast.error('Chưa đến thời gian báo cáo!')
    return
  }

  selectedDate.value = dateStr
  resetReport()

  try {
    const timestamp = new Date(selectedDate.value)
    timestamp.setHours(0, 0, 0, 0)
    const startOfDayTimestamp = timestamp.getTime()

    const response = await getReportIdByDate(startOfDayTimestamp, route.params.id as string)

    if (response?.data) {
      reportId.value = response.data
      await fetchReportDetails(reportId.value)
    } else {
      reportId.value = null
    }
  } catch (error) {
    console.error('❌ Lỗi khi lấy ID báo cáo:', error)
  }

  isModalVisible.value = true
}

const fetchReportDetails = async (idReport) => {

    if (!idReport) {
    resetReport()
    return
  }


  try {
    const response = await detailReport(idReport, route.params.id as string)
    // console.log("detail dâtta",response?.data)
    if (response?.data) {
      report.workDoneToday = response.data.wordDoneTodayReport
      report.obstaclesReport = response.data.obstaclesReport
      report.workPlanTomorrow = response.data.wordPlanTomorrowReport
      report.help = response.data.help
      // console.log('report.help sau fetch:', report.help)

      reportId.value = response.data.idReport || idReport
    } else {
      resetReport()
    }
  } catch (error) {
    console.error('❌ Lỗi khi lấy báo cáo:', error)
  }
}

const check = computed({
  get() {
    return report.help === 'CAN_GIUP_DO'
  },
  set(value) {
    report.help = value ? 'CAN_GIUP_DO' : ''
  }
})

const fetchReports = async () => {
  try {
    const response = await getAllReport({ page: 1, size: 100 }, route.params.id)
    if (response?.data) emit('update-report-list', response.data)
  } catch (error) {
    console.error('Lỗi khi tải danh sách báo cáo:', error)
  }
}

const loadReportsToCalendar = async () => {
  try {
    const res = await getAllReport({ page: 1, size: 1000 }, projectId.value!)
    const reports = res?.data?.data || []

    const formatDateLocal = (timestamp: number): string => {
      const date = new Date(timestamp)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    }

    const formatTime = (timestamp: number): string => {
      const date = new Date(timestamp)
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      return `${hours}:${minutes}`
    }

    // Tạo map các ngày đã báo cáo
    const reportedDays = new Set(
      reports.map((report) => {
        const date = new Date(report.reportTime)
        date.setHours(0, 0, 0, 0)
        return date.getTime()
      })
    )

  const events = reports.map((report) => {
    const date = formatDateLocal(report.reportTime)
    const timeText = formatTime(report.reportTime)
    let title = ''
    let className = ''

    if (report.statusReport === 'NGHI') {
      title = '📌 Nghỉ'
      className = 'event-off'
    } else {
      const statusText = report.statusReport === 'DA_BAO_CAO' ? '✅ Đã báo cáo' : '🕒 Báo cáo muộn'
      title = `${timeText}\n${statusText}`
      className = report.statusReport === 'DA_BAO_CAO' ? 'event-success' : 'event-pending'
    }

    return {
      title,
      date,
      className,
      display: 'auto'
    }
  })

    // Tính thêm các ngày không có báo cáo (trong vòng 30 ngày gần nhất)
    const today = new Date()
    today.setHours(0, 0, 0, 0)

    const start = new Date(today)
    start.setDate(start.getDate() - 30)

    for (let d = new Date(start); d <= today; d.setDate(d.getDate() + 1)) {
      const dayTime = d.getTime()
      if (!reportedDays.has(dayTime)) {
        const isToday = dayTime === today.getTime()
        events.push({
          title: isToday ? '⚠ Chưa báo cáo' : '⛔ Không báo cáo',
          date: formatDateLocal(dayTime),
          className: isToday ? 'event-warning' : 'event-error',
          display: 'auto'
        })
      }
    }

    calendarOptions.value.events = events
  } catch (error) {
    console.error('❌ Lỗi khi load sự kiện báo cáo vào lịch:', error)
  }
}


const resetReport = () => {
  report.workDoneToday = ''
  report.obstaclesReport = ''
  report.workPlanTomorrow = ''
  reportId.value = null
  report.help = ''
  reportForm.value?.resetFields?.()
}

const closeModal = () => {
  isModalVisible.value = false
  resetReport()
}

const submitReport = async () => {
  const action = reportId.value ? 'cập nhật' : 'thêm mới'

  Modal.confirm({
    title: `Bạn muốn ${action} báo cáo không?`,
    content: `Hành động này sẽ ${action} báo cáo cho ngày ${selectedDate.value}.`,
    okText: 'Xác nhận',
    cancelText: 'Hủy',
    onOk: async () => {
      try {
        await reportForm.value?.validateFields()

        const formData = {
          workDoneToday: report.workDoneToday.trim(),
          obstacles: report.obstaclesReport.trim(),
          workPlanTomorrow: report.workPlanTomorrow.trim(),
          help: report.help === 'CAN_GIUP_DO',
          statusReport: 'DA_BAO_CAO',
          reportTime: new Date(selectedDate.value).getTime()
        }

        if (reportId.value != null) {
          await updateReport(formData, reportId.value, route.params.id)
          toast.success('Cập nhật báo cáo thành công!')
        } else {
          await addReport(formData, route.params.id)
          toast.success('Thêm báo cáo thành công!')
        }

        closeModal()
        fetchReports()
        loadReportsToCalendar()
      } catch (error) {
        // console.error('❌ Lỗi khi gửi báo cáo:', error)
        toast.error('Thất bại!')
      }
    }
  })
}

watch(
  () => props.idReport,
  (newId) => {
    if (newId) {
      // console.log('📥 Đang truyền idReport vào CalendarReport:', newId)
    }
  },
  { immediate: true }
)

onMounted(() => {
  loadReportsToCalendar()
})
</script>

<style scoped>
/* Giao diện tổng thể lịch */
::v-deep .fc {
  background-color: #fff;
  font-family: 'Inter', 'Segoe UI', Roboto, sans-serif;
  color: #2c3e50;
  font-size: 13px;
  border-radius: 8px;
  overflow: hidden;
}

/* Bố cục từng ô ngày */
::v-deep .fc-daygrid-day-frame {
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: center;
  padding-top: 6px;
  height: 85px;
  border: 0.5px solid #e8ecef !important;
  transition: background-color 0.2s ease-in-out;
}

/* Nền ngày hôm nay */
::v-deep .fc-day-today {
  background-color: #f0f7ff !important;
  border-radius: 6px;
}

/* Không viền ngày (giữ viền của frame) */
::v-deep .fc-daygrid-day {
  border: none !important;
}

/* Header tên thứ (T2, T3, ...) */
::v-deep .fc-col-header-cell-cushion {
  color: #34495e;
  font-weight: 500;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.3px;
}

/* Số ngày trong ô */
::v-deep .fc-daygrid-day-number {
  font-weight: 500;
  font-size: 12px;
  color: #2d3748;
  margin-bottom: 3px;
}

/* Hiệu ứng hover ô lịch */
::v-deep .fc-daygrid-day-frame:hover {
  background-color: #f9fafb;
  cursor: pointer;
  border-radius: 6px;
}

/* Header các thứ (T2, T3, ...) */
::v-deep .fc-col-header-cell {
  background-color: #f6f9ff;
  border-bottom: 0.5px solid #e8ecef;
  border-right: 0.5px solid #e8ecef;
  padding: 6px 0;
}

/* Chữ trong header */
::v-deep .fc-col-header-cell-cushion {
  color: #2c3e50;
  font-weight: 600;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.4px;
}

/* Viền ngang giữa các tuần */
::v-deep .fc-daygrid-day-top {
  border-bottom: 0.5px solid #e8ecef;
}

/* Viền dọc giữa các ngày */
::v-deep .fc-daygrid-day {
  border-right: 0.5px solid #e8ecef !important;
}

/* Đảm bảo ô cuối không có viền phải thừa */
::v-deep .fc-daygrid-day:last-child {
  border-right: none !important;
}

/* Đảm bảo hàng header cuối không có viền phải thừa */
::v-deep .fc-col-header-cell:last-child {
  border-right: none !important;
}

/* Sự kiện trong lịch */
::v-deep .fc-event {
  display: flex !important;
  flex-direction: column !important;
  justify-content: center !important;
  align-items: center !important;
  text-align: center !important;
  width: 100% !important;
  padding: 2px 3px;
  background-color: transparent !important;
  border: none !important;
  font-size: 11px;
  font-weight: 500;
  line-height: 1.3;
}

/* Đảm bảo title hiển thị đúng 2 dòng */
::v-deep .fc-event-title {
  white-space: pre-line !important;
  text-align: center !important;
  font-size: 13px;
  line-height: 1.3;
  width: 100%;
}

/* Màu cho trạng thái */
::v-deep .event-success {
  color: #28a745 !important;
}

::v-deep .event-pending {
  color: #f39c12 !important;
}

::v-deep .fc-event.event-success {
  color: #28a745 !important;
  background-color: transparent !important;
  border: none !important;
}

::v-deep .fc-event.event-pending {
  color: #f39c12 !important;
  background-color: transparent !important;
  border: none !important;
}

::v-deep .fc-event.event-success .fc-event-title {
  color: #28a745 !important;
}

::v-deep .fc-event.event-pending .fc-event-title {
  color: #f39c12 !important;
}

::v-deep .fc-event.event-warning .fc-event-title {
  color: #e67e22 !important;
}

::v-deep .fc-event.event-error .fc-event-title {
  color: #e74c3c !important;
}

::v-deep .fc-event.event-off .fc-event-title {
  color: #95a5a6 !important;
}

/* Modal content */
.modal-content {
  padding: 16px;
  background-color: #f9fafb;
  border-radius: 8px;
}

/* Report card */
.report-card {
  margin-bottom: 16px;
  border: none;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  background-color: #fff;
  transition: box-shadow 0.2s ease-in-out;
}

.report-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* Form item */
.form-item {
  margin-bottom: 12px;
}

.form-item .ant-form-item-label > label {
  font-family: 'Inter', 'Segoe UI', Roboto, sans-serif;
  font-size: 13px;
  font-weight: 500;
  color: #2d3748;
}

/* Textarea */
.custom-textarea {
  font-family: 'Inter', 'Segoe UI', Roboto, sans-serif;
  font-size: 13px;
  border: 1px solid #e8ecef;
  border-radius: 6px;
  padding: 8px 12px;
  transition: border-color 0.2s ease-in-out, box-shadow 0.2s ease-in-out;
}

.custom-textarea:hover {
  border-color: #d6e4ff;
}

.custom-textarea:focus {
  border-color: #40c4ff;
  box-shadow: 0 0 0 2px rgba(64, 196, 255, 0.2);
}

/* Placeholder */
.custom-textarea::placeholder {
  color: #a0aec0;
  font-style: italic;
}

/* Custom drawer */
.custom-drawer ::v-deep .ant-drawer-header {
  background-color: #f6f9ff;
  border-bottom: 1px solid #e8ecef;
}

.custom-drawer ::v-deep .ant-drawer-title {
  font-family: 'Inter', 'Segoe UI', Roboto, sans-serif;
  font-size: 16px;
  font-weight: 600;
  color: #2d3748;
}

.custom-drawer ::v-deep .ant-drawer-close {
  color: #2d3748;
}

.custom-drawer ::v-deep .ant-btn-primary {
  background-color: #40c4ff;
  border-color: #40c4ff;
  font-family: 'Inter', 'Segoe UI', Roboto, sans-serif;
  font-weight: 500;
  border-radius: 6px;
}

.custom-drawer ::v-deep .ant-btn-primary:hover {
  background-color: #0288d1;
  border-color: #0288d1;
}
</style>