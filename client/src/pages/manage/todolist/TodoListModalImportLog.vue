<template>
  <a-modal
    :open="open"
    :title="title"
    @cancel="closeModal"
    :maskClosable="true"
    :centered="true"
    :width="'60%'"
  >
    <template #footer>
      <a-button @click="closeModal">Huỷ</a-button>
    </template>
    <div class="min-h-[360px]">
      <a-table
        :columns="columns"
        :data-source="userData"
        row-key="orderNumber"
        :scroll="{ y: 300 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <a-tag :color="record.status ? 'success' : 'error'">
              {{ record.status ? 'Thành công' : 'Lỗi' }}
            </a-tag>
          </template>
        </template>
      </a-table>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, defineProps, defineEmits, onMounted, reactive, watch } from 'vue'
import { localStorageAction } from '@/utils/storage'
import { USER_INFO_STORAGE_KEY } from '@/constants/storagekey'
import { importLogTodoList, TodoListLogImport } from '@/services/api/manage/todolist/todolist.api'

const props = defineProps<{ open: boolean; title: string }>()
const emit = defineEmits(['close', 'success'])

const user = localStorageAction.get(USER_INFO_STORAGE_KEY)

const dataImportLog = ref<TodoListLogImport[]>([])
const userData = ref<TodoListLogImport[]>([])
const STORAGE_KEY = 'IMPORT_LOG_DATA'

// Lấy ngày giờ hiện tại theo định dạng 'dd/MM/yyyy, HH:mm:ss'
const formattedDate = new Date().toLocaleString('vi-VN', {
  year: 'numeric',
  month: '2-digit',
  day: '2-digit',
  hour: '2-digit',
  minute: '2-digit',
  second: '2-digit'
})

// Tạo đối tượng newLogEntry chứa thông tin người thực hiện
const newLogEntry = reactive({
  email: user?.email || 'Chưa xác định',
  date: formattedDate,
  message: '',
  rolesCodes: user?.rolesNames ? user.rolesNames.join(', ') : 'Chưa xác định'
})

// Định nghĩa các cột của bảng
const columns = [
  {
    title: 'STT',
    key: 'orderNumber',
    dataIndex: 'orderNumber',
    width: 50,
    align: 'center',
    customRender: ({ index }: { index: number }) => index + 1
  },
  { title: 'Email người thực hiện', key: 'email', dataIndex: 'email', width: 200, align: 'center' },
  {
    title: 'Ngày thực hiện',
    key: 'date',
    dataIndex: 'date',
    width: 150,
    align: 'center'
  },
  { title: 'Nội dung import', key: 'message', dataIndex: 'message', width: 150, align: 'center' },
  { title: 'Chức vụ', key: 'rolesCodes', dataIndex: 'rolesCodes', width: 150, align: 'center' }
]

// Đóng modal
const closeModal = () => emit('close')
const getFormattedDate = () => {
  return new Date().toLocaleString('vi-VN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

onMounted(() => {
  fecthDataImportLog()
})

const fecthDataImportLog = async () => {
  try {
    const res = await importLogTodoList()
    const logs = res.data as TodoListLogImport[]

    // 🟢 Lấy log từ localStorage (nếu có)
    const savedLogs = JSON.parse(localStorage.getItem('IMPORT_LOGS') || '[]')
    console.table(savedLogs)
    console.table(logs)

    // 🟢 Kết hợp date từ savedLogs và rolesCodes từ logs
    userData.value = logs.map((log, index) => ({
      orderNumber: index + 1, // 🏆 STT tự động tăng
      email: savedLogs[index]?.email || 'Chưa xác định',
      date: savedLogs[index]?.date || 'Không có ngày', // Lấy date từ savedLogs
      message: log.todolist + ' : ' + log.message,
      rolesCodes: savedLogs[index]?.rolesCodes || 'Chưa xác định' // Lấy rolesCodes từ logs
    }))
  } catch (error) {
    console.error('❌ Lỗi khi lấy dữ liệu import log:', error)
  }
}

watch(
  userData,
  (newVal) => {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(newVal))
  },
  { deep: true }
)

const addNewLogEntry = (newEntry) => {
  if (!newEntry) return

  // Nếu có message từ API, giữ nguyên, nếu không có thì đặt mặc định
  const entry = {
    ...newEntry
  }
  fecthDataImportLog()
  userData.value.unshift(entry)
}

defineExpose({ addNewLogEntry })
</script>

<style scoped>
.ant-btn {
  background-image: var(--selected-header) !important;
  color: var(--selected-text) !important;
  border: none !important;
}

.ant-btn:hover,
.ant-btn:focus {
  background-image: var(--selected-header-hover) !important;
  color: var(--selected-text) !important;
  border: none !important;
}
</style>
