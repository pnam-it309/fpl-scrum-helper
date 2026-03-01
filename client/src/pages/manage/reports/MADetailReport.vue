<template>
  <a-modal :open="open" :title="props.title" @cancel="closeModal" :width="'50%'" centered :closable="false"
    :footer="null">
    <div class="max-h-[70vh] overflow-y-auto px-4">
        <h1 class="font-semibold">
          Báo cáo công việc ngày : {{ formatDateNVV(Number(props.time)) }}
        </h1>

      <table class="w-full border-collapse mt-4">
        <thead>
          <tr class="bg-gray-100">
            <th class="border p-2 text-left">Người báo cáo</th>
            <th class="border p-2 text-left">Báo cáo</th>
            <th class="border p-2 text-left">Ngày báo cáo</th>
            <th class="border p-2 text-left">Trạng thái</th>
          </tr>
        </thead>
        <tbody>
           <tr v-for="item in props.data" :key="item.id" class="hover:bg-gray-50">
            <td class="border p-2 w-1/5">
              {{ item.nameStaff == null ? item.nameStudent : item.nameStaff }} -
              {{ item.codeStaff == null ? item.codeStudent : item.codeStaff }}
            </td>
            <td class="border p-2 w-2/3">
              <p>
                Nay làm được gì :
                {{ item.workDoneToday == null ? 'Chưa báo cáo' : item.workDoneToday }}
              </p>
              <br />
              <p>
                Mai làm được gì:
                {{ item.workPlanTomorrow == null ? 'Chưa báo cáo' : item.workPlanTomorrow }}
              </p>
              <br />
              <p>Vướng mắc gì : {{ item.obstacles == null ? 'Chưa báo cáo' : item.obstacles }}</p>
            </td>
            <td class="border p-2">
              <a-tag
                :color="isReportTimeInvalid(Number(item.reportTime)) ? 'error' : 'success'"
                class="!px-3 !py-1 !rounded-full"
              >
                {{
                  isReportTimeInvalid(Number(item.reportTime))
                    ? 'Chưa báo cáo'
                    : formatReportTime(Number(item.reportTime))
                }}
              </a-tag>
            </td>
            <td class="border p-2">
              <a-tag
                :color="isReportTimeInvalid(Number(item.reportTime)) ? 'error' : 'success'"
                class="!px-3 !py-1 !rounded-full"
              >
                {{ isReportTimeInvalid(Number(item.reportTime)) ? 'Chưa báo cáo' : 'Đã báo cáo' }}
              </a-tag>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { defineProps, defineEmits, onMounted, ref } from 'vue'
import { reportResponse } from '@/services/api/manage/report/report.api'
import { updateReportSetting } from '@/services/api/member/report/report.api'
import { formatDateNVV } from '@/utils/commom.helper'
import { toast } from 'vue3-toastify'
import dayjs, { Dayjs } from 'dayjs'
import { router } from '@/routes/router'
import { useRoute } from 'vue-router'

const props = defineProps<{
  open: boolean
  title: string
  time?: number
  data: reportResponse[]
  page: number
  size: number
  totalItems: number
  projectId: string 
}>()

const route = useRoute()

const emit = defineEmits(['page-change', 'add', 'view', 'delete', 'idFacility', 'close', 'success'])

const closeModal = () => {
  emit('close')
}

const stopReportHour = ref<number>(23)

// ✅ Format timestamp => string giờ Việt Nam
const formatReportTime = (timestamp: number) => {
  if (!timestamp || timestamp < new Date('2000-01-01').getTime()) return 'N/A'
  const date = new Date(timestamp)
  const options: Intl.DateTimeFormatOptions = {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    hour12: false,
    timeZone: 'Asia/Ho_Chi_Minh'
  }
  return date.toLocaleString('en-GB', options).replace(',', '')
}

// ❌ Kiểm tra chưa báo cáo nếu không có thời gian hoặc vượt giới hạn giờ
const isReportTimeInvalid = (timestamp?: number | null): boolean => {
  if (!timestamp || timestamp < new Date('2000-01-01').getTime()) return true

  const reportDate = new Date(timestamp)
  const reportHour = reportDate.getHours()

  const selectedDate = new Date(props.time ?? 0)
  return reportDate.toDateString() !== selectedDate.toDateString() || reportHour > stopReportHour.value
}

onMounted(() => {
  console.log('Initial time:', props.time)
  console.log('Initial data:', props.data)
})
</script>
