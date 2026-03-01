<template>
  <DivCustom>
    <h2 class="text-lg font-semibold mb-4">Chọn nhiều ngày nghỉ</h2>

    <!-- Chọn ngày -->
    <label class="block mb-2 text-sm font-medium">Ngày nghỉ</label>
    <Datepicker
      v-model="selectedDates"
      :multi-dates="true"
      :enable-time-picker="false"
      :format="'dd/MM/yyyy'"
      class="w-full"
      :disabled="isLoading"
    />

    <!-- Nút -->
    <div class="mt-4 flex justify-start gap-2">
      <a-modal
        v-model:visible="isConfirmModalVisible"
        title="Xác nhận"
        ok-text="Đồng ý"
        cancel-text="Huỷ"
        @ok="handleConfirm"
      >
        <p>Bạn có chắc chắn muốn tạo hoặc cập nhật ngày nghỉ?</p>
      </a-modal>

      <a-button
        @click="showConfirmModal"
        class="bg-blue-500 text-white px-4 py-1 rounded hover:bg-blue-600 text-sm"
        :disabled="isLoading"
      >
        Xác nhận
      </a-button>
    </div>

    <!-- Hiển thị ngày đã chọn -->
    <div v-if="selectedDates.length" class="mt-6">
      <h3 class="text-sm font-medium mb-2">Các ngày đã chọn:</h3>
      <ul class="list-disc list-inside text-sm text-gray-600 max-h-40 overflow-y-auto">
        <li v-for="(date, index) in selectedDates" :key="index">
          {{ formatDate(date) }}
        </li>
      </ul>
    </div>
  </DivCustom>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import Datepicker from '@vuepic/vue-datepicker'
import '@vuepic/vue-datepicker/dist/main.css'
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import { addHoliday, detailHoliday, updateSatus } from '@/services/api/manage/report/report.api'
import { useRoute } from 'vue-router'
import { toast } from 'vue3-toastify'

// State
const selectedDates = ref<Date[]>([])
const originalDates = ref<Date[]>([])
const note = ref('')
const route = useRoute()
const idProject = route.params.id as string
const isConfirmModalVisible = ref(false)
const isLoading = ref(true)

// Chuẩn hoá ngày về 0h local time
function normalizeDate(date: Date | number): number {
  const d = new Date(date)
  d.setHours(0, 0, 0, 0)
  return d.getTime()
}

// Load dữ liệu khi mount
onMounted(async () => {
  try {
    const response = await detailHoliday(idProject)
    if (response.success && Array.isArray(response.data)) {
      const normalized = response.data.map(item => new Date(item.date))
      selectedDates.value = [...normalized]
      originalDates.value = [...normalized]
    }
  } catch {
    toast.error('Không thể tải ngày nghỉ')
  } finally {
    isLoading.value = false
  }
})

const showConfirmModal = () => {
  if (!selectedDates.value.length) {
    toast.error('Vui lòng chọn ít nhất một ngày!')
    return
  }
  isConfirmModalVisible.value = true
}

const handleConfirm = async () => {
  isConfirmModalVisible.value = false

  const selectedTimestamps = selectedDates.value.map(d => normalizeDate(d))
  const originalTimestamps = originalDates.value.map(d => normalizeDate(d))

  const addedDates = selectedTimestamps.filter(ts => !originalTimestamps.includes(ts))
  const removedDates = originalTimestamps.filter(ts => !selectedTimestamps.includes(ts))

  console.log('🆕 Ngày thêm mới:', addedDates.map(ts => new Date(ts).toLocaleDateString('vi-VN')))
  console.log('🗑️ Ngày bị bỏ chọn:', removedDates)

  try {
    if (addedDates.length > 0) {
      const payload = {
        statusReport: note.value.trim(),
        dates: addedDates
      }
      await addHoliday(payload, idProject)
      toast.success('Thêm ngày nghỉ thành công!')
    }

    if (removedDates.length > 0) {
      await updateSatus(removedDates, idProject) // Gửi mảng Long
      toast.success('Cập nhật ngày nghỉ thành công!')
    }


    selectedDates.value = []
    originalDates.value = []
    note.value = ''

    const refreshed = await detailHoliday(idProject)
    if (refreshed.success && Array.isArray(refreshed.data)) {
      const reloaded = refreshed.data.map(item => new Date(item.date))
      selectedDates.value = [...reloaded]
      originalDates.value = [...reloaded]
    }
  } catch (error: any) {
    toast.error(error?.response?.data?.message || 'Đã xảy ra lỗi khi gửi yêu cầu.')
  }
}

const formatDate = (date: Date) =>
  new Intl.DateTimeFormat('vi-VN', {
    weekday: 'short',
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  }).format(date)
</script>
