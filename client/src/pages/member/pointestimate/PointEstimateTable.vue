<template>
  <DivCustom label="Lịch sử ước lượng">
    <template #icon>
      <IdcardOutlined />
    </template>
    <template #extra>
      <a-tooltip>
        <a-button class="flex items-center justify-center px-4 mr-2" @click="handleAddClick">
          <PlusCircleOutlined /> Thêm ước lượng
        </a-button>
      </a-tooltip>
    </template>

    <div class="min-h-[680px] max-h-[680px]">
      <a-table
        :columns="columns"
        :data-source="data"
        size="middle"
        :pagination="{
          current: paginationParams.page,
          pageSize: paginationParams.size,
          total: totalItems,
          showSizeChanger: true,
          pageSizeOptions: ['10', '20', '30', '40', '50']
        }"
        :scroll="{ y: 600, x: 'max-content' }"
        @change="handlePageChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'operation'">
            <div class="flex gap-1 items-center justify-center text-center">
              <a-tooltip title="Cập nhật">
                <a-button
                  @click="handleViewClick(record.id, record.idPhase)"
                  class="flex items-center justify-center w-8 h-8"
                  :disabled="isSprintCompletedOrInProgress(record.idPhase)"
                >
                  <FormOutlined />
                </a-button>
              </a-tooltip>

              <a-tooltip title="Xóa ">
                <a-popconfirm
                  title="Bạn có chắc chắn muốn xóa ước lượng này?"
                  @confirm="handleDeleteClick(record.id, record.idPhase)"
                  ok-text="Đồng ý"
                  cancel-text="Huỷ"
                >
                  <a-button
                    class="flex items-center justify-center w-8 h-8"
                    :disabled="isSprintCompletedOrInProgress(record.idPhase)"
                  >
                    <DeleteOutlined />
                  </a-button>
                </a-popconfirm>
              </a-tooltip>
            </div>
          </template>
        </template>
      </a-table>
    </div>
  </DivCustom>
</template>

<script setup lang="ts">
import { defineProps, defineEmits, h } from 'vue'
import { TableColumnsType, Tag } from 'ant-design-vue'
import {
  DeleteOutlined,
  FormOutlined,
  IdcardOutlined,
  PlusCircleOutlined,
  UsergroupAddOutlined
} from '@ant-design/icons-vue'
import dayjs from 'dayjs'
import { localStorageAction } from '@/utils/storage'
import { USER_INFO_STORAGE_KEY } from '@/constants/storagekey'
import BreadcrumbDefault from '@/components/custom/Div/BreadcrumbDefault.vue'
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import BreadcrumbCustom from '@/components/custom/Div/BreadcrumbCustom.vue'

// Định nghĩa lại interface Sprint để sử dụng trong Component này
interface Sprint {
  id: string
  name: string
  statusPhase: string
  startTime: string
  endTime: string
}

const props = defineProps<{
  data: any[]
  paginationParams: { page: number; size: number }
  totalItems: number
  dataSprint: Sprint[] // THÊM PROP NÀY ĐỂ NHẬN DỮ LIỆU SPRINT TỪ COMPONENT CHA
}>()

const userLogin = localStorageAction.get(USER_INFO_STORAGE_KEY)

const columns: TableColumnsType = [
  {
    title: '#',
    key: 'orderNumber',
    dataIndex: 'orderNumber',
    width: '5%',
    align: 'center'
  },
  { title: 'Tên thành viên', key: 'name', dataIndex: 'name', width: '20%', align: 'left' },
  {
    title: 'Ngày làm',
    key: 'workday',
    dataIndex: 'workday',
    width: '15%',
    align: 'left'
  },
  {
    title: 'Điểm',
    key: 'adjustedStoryPoints',
    dataIndex: 'adjustedStoryPoints',
    width: '10%',
    align: 'left'
  },
  {
    title: 'Giai đoạn',
    key: 'sprint',
    dataIndex: 'sprint',
    width: '15%',
    align: 'left'
  },
  {
    title: 'Hành động',
    key: 'operation',
    width: '10%',
    align: 'center',
    fixed: 'right'
  }
]

const emit = defineEmits([
  'page-change',
  'add',
  'view', // Cập nhật emit 'view' để truyền cả id và idPhase
  'delete' // Cập nhật emit 'delete' để truyền cả id và idPhase
  // ... các emits khác nếu có
])

const handlePageChange = (Pagination: any) => {
  emit('page-change', { page: Pagination.current, pageSize: Pagination.pageSize })
}

const handleAddClick = () => {
  emit('add')
}

// Cập nhật hàm handleViewClick để nhận thêm idPhase
const handleViewClick = (id: string, idPhase: string) => {
  emit('view', id, idPhase) // Truyền cả id và idPhase
}

// Cập nhật hàm handleDeleteClick để nhận thêm idPhase
const handleDeleteClick = (id: string, idPhase: string) => {
  emit('delete', id, idPhase) // Truyền cả id và idPhase
}

// Hàm kiểm tra trạng thái sprint
const isSprintCompletedOrInProgress = (sprintId: string) => {
  const sprint = props.dataSprint.find((s) => s.id === sprintId)
  if (sprint) {
    // Vô hiệu hóa nút nếu trạng thái là DANG_LAM, DA_HOAN_THANH hoặc QUA_HAN
    return ['DANG_LAM', 'DA_HOAN_THANH', 'QUA_HAN'].includes(sprint.statusPhase)
  }
  return false // Mặc định không vô hiệu hóa nếu không tìm thấy sprint hoặc trạng thái không rõ
}
</script>

<style scoped>
/* CSS của bạn không cần thay đổi */

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
