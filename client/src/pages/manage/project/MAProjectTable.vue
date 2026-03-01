<template>
  <DivCustom label="Danh sách dự án" customClasses="mt-5">
    <template #icon>
      <ProjectOutlined />
    </template>
    <div class="space-y-4 p-4 min-h-[360px]">
      <a-card
        v-for="item in sortedData"
        :key="item.id"
        class="shadow-md rounded-lg border p-1 hover:shadow-xl hover:rounded-xl hover:border transition duration-300"
        :class="getCardColorClass(item.progressProject)"
        hoverable
        :style="{ minHeight: '200px' }"
        @click="handleDetailClick(item.id, item.status)"
      >
        <div class="flex items-center justify-between">
          <h3 class="font-semibold text-lg text-gray-800">
            {{ `${item.nameProject} - Bộ môn: ${item.nameDepartment}` }}
          </h3>
        </div>
        <hr class="my-2 border-gray-300" />

        <div class="flex justify-between items-start">
          <div class="flex-1 pr-6">
            <div class="text-gray-600 text-sm font-medium">
              <span class="mr-4">Mã dự án:
                <span class="text-gray-900 font-semibold">{{ item.codeProject }}</span>
              </span>
              <span>Ngày tạo:
                <span class="text-gray-900 font-semibold">
                  {{ formatDate(item.createdDateProject) }}
                </span>
              </span>
            </div>
            <div class="grid grid-cols-3 gap-4 mt-2">
              <div>
                <p class="text-gray-600 font-medium">Ngày bắt đầu</p>
                <a-tag color="blue">{{ formatDate(item.startTimeProject) }}</a-tag>
              </div>
              <div>
                <p class="text-gray-600 font-medium">Ngày kết thúc dự tính</p>
                <a-tag color="red">{{ formatDate(item.endTimeProject) }}</a-tag>
              </div>
              <div>
                <p class="text-gray-600 font-medium">Số ngày còn lại</p>
                <a-tag :color="getDaysLeftColor(item.endTimeProject)">
                  {{ getDaysLeft(item.endTimeProject) }}
                </a-tag>
              </div>
            </div>
          </div>

          <div class="w-1/4 flex flex-col items-center">
            <p class="text-gray-600 font-medium mt-7">Tiến độ</p>
            <a-progress
              :percent="Math.floor(item.progressProject)"
              :stroke-color="getProgressColor(item.progressProject)"
              show-info
              class="w-full"
            />
          </div>
        </div>
      </a-card>

      <div class="flex justify-center mt-4">
        <a-pagination
          :current="page"
          :page-size="pageSize"
          :total="totalItems"
          show-size-changer
          :page-size-options="['5', '10', '15', '20']"
          @change="handlePageChange"
        />
      </div>
    </div>
  </DivCustom>
</template>

<script setup lang="ts">
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import { ROUTES_CONSTANTS } from '@/constants/path'
import { router } from '@/routes/router'
import { ProjectOutlined } from '@ant-design/icons-vue'
import { library } from '@fortawesome/fontawesome-svg-core'
import {
  faCircleInfo,
  faEye,
  faFilter,
  faPenToSquare,
  faReceipt,
  faRotateRight,
  faTrash,
  faTrashAlt
} from '@fortawesome/free-solid-svg-icons'
import { computed, defineEmits, defineProps, ref } from 'vue'
import { toast } from 'vue3-toastify'
import { debounce } from 'lodash'
import { localStorageAction } from '@/utils/storage'
import { USER_INFO_STORAGE_KEY } from '@/constants/storagekey'

library.add(
  faPenToSquare,
  faReceipt,
  faCircleInfo,
  faFilter,
  faRotateRight,
  faTrash,
  faTrashAlt,
  faEye
)

const userLogin = localStorageAction.get(USER_INFO_STORAGE_KEY)
const emailLogin = userLogin.email

// Props
const props = defineProps<{
  data: Array<{
    id: string
    codeProject: string
    nameProject: string
    nameDepartment: string
    status: string
    idFacility: string
    backgroundColorProject: string
    bachbackgroundImageProject: string
    progressProject: number
    startTimeProject: number
    endTimeProject: number
    createdDateProject: number
  }>
  totalItems: number
}>()

const emit = defineEmits([
  'view',
  'page-change',
  'openSumaryModal',
  'downloadTemplateImport',
  'openModalImport',
  'delete',
  'importLog',
  'downloadImportLog'
])

const page = ref(1)
const pageSize = ref(5)

// Sắp xếp dự án theo tiến độ giảm dần hoặc số ngày còn lại nếu cần
const sortedData = computed(() => {
  return [...props.data].sort((a, b) => a.progressProject - b.progressProject)
})

// Màu thẻ dựa trên tiến độ dự án
const getCardColorClass = (progress: number) => {
  if (progress <= 33) return 'bg-red-100 border-red-500'
  if (progress > 33 && progress <= 66) return 'bg-orange-100 border-orange-400'
  return 'bg-green-100 border-green-400'
}

const getDaysLeft = (endTimestamp: number, raw = false) => {
  if (!endTimestamp) return raw ? 0 : 'N/A'
  const diffDays = Math.ceil((endTimestamp - Date.now()) / (1000 * 60 * 60 * 24))
  if (raw) return diffDays
  return diffDays > 0 ? `${diffDays} ngày` : 'Hết hạn'
}

const getDaysLeftColor = (endTimestamp: number) => {
  const daysLeft = getDaysLeft(endTimestamp, true)
  if (daysLeft <= 0) return 'red'
  if (daysLeft <= 7) return 'orange'
  return 'green'
}

const handlePageChange = (current: number, size: number) => {
  page.value = current
  pageSize.value = size
  emit('page-change', { page: current, pageSize: size })
}

const getProgressColor = (progress: number) => {
  if (progress < 30) return 'red'
  if (progress < 70) return 'orange'
  return 'green'
}

const formatDate = (timestamp: number) => {
  if (!timestamp) return 'N/A'
  const date = new Date(timestamp)
  return date.toLocaleDateString('vi-VN')
}

const debounceToastInfoProjectStatus = debounce(() => {
  toast.info('Dự án đã diễn ra hoặc đã hoàn thành. Không thể truy cập')
}, 300)

const handleDetailClick = (id: string, status: string) => {
  if (status == 'DA_DIEN_RA' || status == 'DA_HOAN_THANH') {
    debounceToastInfoProjectStatus()
    return
  }
  router.push({
    name: `${ROUTES_CONSTANTS.MANAGE.children.DESCRIBE.name}`,
    params: { id }
  })
}
</script>
