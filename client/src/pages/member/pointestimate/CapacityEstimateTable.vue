<template>
  <DivCustom label="Danh sách ước lượng">
    <template #icon>
      <IdcardOutlined />
    </template>
    <div class="p-8 min-h-[700px]">
      <a-table
        :columns="sprintColumns"
        :dataSource="sortedDataSprint"
        :rowKey="(record) => record.id"
        :pagination="false"
        :locale="{ emptyText: 'Không có dữ liệu' }"
        size="middle"
        bordered
        class="rounded-md"
        :expandedRowKeys="expandedRowKeys"
        @expandedRowsChange="handleExpandedRowsChange"
        :scroll="{ y: 600 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <span :class="getStatusClasses(record.statusPhase)">
              {{ getStatusText(record.statusPhase) }}
            </span>
          </template>
          <template v-if="column.key === 'timeRange'">
            <p class="text-sm text-gray-500">
              {{ formatDateNVV(Number(record.startTime)) }} -
              {{ formatDateNVV(Number(record.endTime)) }}
            </p>
          </template>
        </template>

        <template #expandedRowRender="{ record }">
          <a-table
            :columns="estimateColumns"
            :dataSource="groupedData[record.id] || []"
            :rowKey="(estimateRecord) => estimateRecord.id"
            :pagination="false"
            :locale="{ emptyText: 'Không có dữ liệu ước tính' }"
            size="small"
            bordered
            :scroll="{ x: 'max-content' }"
          />
        </template>
      </a-table>
    </div>
  </DivCustom>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { TableColumnsType } from 'ant-design-vue'
import BreadcrumbDefault from '@/components/custom/Div/BreadcrumbDefault.vue'
// Không cần LeftOutlined, RightOutlined nữa vì a-table có nút mở rộng riêng
import { formatDateNVV } from '@/utils/commom.helper'
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import { IdcardOutlined } from '@ant-design/icons-vue'
import DivCustomOld from '@/components/custom/Div/DivCustomOld.vue'
import BreadcrumbCustom from '@/components/custom/Div/BreadcrumbCustom.vue'

interface Sprint {
  id: string
  name: string
  statusPhase: string
  startTime: string
  endTime: string
}

interface Estimate {
  id: string
  idPhase: string
  name: string
  orderNumber?: number
  workday?: string
  // các trường khác nếu có
}

const props = defineProps<{
  data: Estimate[]
  dataSprint: Sprint[]
}>()

// State để quản lý các hàng sprint đang mở rộng
const expandedRowKeys = ref<string[]>([])

// Hàm xử lý khi các hàng mở rộng thay đổi
const handleExpandedRowsChange = (keys: string[]) => {
  expandedRowKeys.value = keys
}

// Hàm chuyển đổi trạng thái sang một giá trị số để sắp xếp
const getStatusOrder = (status: string): number => {
  switch (status) {
    case 'DANG_LAM':
      return 1 // Ưu tiên cao nhất
    case 'QUA_HAN':
      return 2 // Ưu tiên thứ hai
    case 'CHUA_HOAN_THANH':
      return 3 // Ưu tiên thứ ba
    case 'DA_HOAN_THANH':
      return 4 // Ưu tiên thấp nhất
    default:
      return 99 // Các trạng thái khác (nếu có)
  }
}

// Computed property để sắp xếp dataSprint
const sortedDataSprint = computed(() => {
  const sprints = [...props.dataSprint]
  return sprints.sort((a, b) => {
    const orderA = getStatusOrder(a.statusPhase)
    const orderB = getStatusOrder(b.statusPhase)
    return orderA - orderB
  })
})

// Nhóm dữ liệu ước lượng theo sprintId (idPhase)
const groupedData = computed(() => {
  if (!props.data || props.data.length === 0) {
    return {}
  }

  const map: Record<string, Estimate[]> = {}
  props.data.forEach((item) => {
    if (!map[item.idPhase]) {
      map[item.idPhase] = []
    }
    map[item.idPhase].push(item)
  })
  return map
})

// Định nghĩa cột cho bảng chính (bảng Sprint)
const sprintColumns: TableColumnsType<Sprint> = [
  {
    title: '#',
    key: 'index',
    customRender: ({ index }) => index + 1,
    width: '5%'
  },
  {
    title: 'Tên Sprint',
    dataIndex: 'name',
    key: 'name',
    width: '30%'
  },
  {
    title: 'Thời gian',
    key: 'timeRange',
    width: '40%'
  },
  {
    title: 'Trạng thái',
    key: 'status',
    width: '30%'
  }
]

// Định nghĩa cột cho bảng con (bảng Estimate)
const estimateColumns: TableColumnsType<Estimate> = [
  {
    title: '#',
    key: 'index',
    customRender: ({ index }) => index + 1,
    width: '5%'
  },
  {
    title: 'Tên thành viên',
    dataIndex: 'name',
    key: 'name',
    width: '25%'
  },
  {
    title: 'Ngày',
    dataIndex: 'workday',
    key: 'workday',
    width: '15%'
  },
  {
    title: 'Mô tả',
    dataIndex: 'descriptions',
    key: 'descriptions',
    width: '55%'
  }
]

// Màu status
const getStatusText = (status: string): string => {
  switch (status) {
    case 'CHUA_HOAN_THANH':
      return 'Chưa diễn ra'
    case 'DANG_LAM':
      return 'Đang làm'
    case 'DA_HOAN_THANH':
      return 'Đã hoàn thành'
    case 'QUA_HAN':
      return 'Quá hạn'
    default:
      return status // Trả về nguyên gốc nếu không khớp
  }
}

const getStatusClasses = (status: string): string => {
  switch (status) {
    case 'CHUA_HOAN_THANH':
      return 'px-3 py-1 text-sm font-medium text-gray-700 bg-gray-200 rounded-full'
    case 'DANG_LAM':
      return 'px-3 py-1 text-sm font-medium text-blue-700 bg-blue-100 rounded-full'
    case 'DA_HOAN_THANH':
      return 'px-3 py-1 text-sm font-medium text-green-700 bg-green-100 rounded-full'
    case 'QUA_HAN':
      return 'px-3 py-1 text-sm font-medium text-red-700 bg-red-100 rounded-full'
    default:
      return 'px-3 py-1 text-sm font-medium text-gray-600 bg-gray-100 rounded-full' // Mặc định
  }
}
</script>

<style scoped>
/* Các style hiện có vẫn có thể hữu ích, nhưng một số có thể không cần thiết nữa */
.table-container {
  /* Có thể điều chỉnh padding hoặc width cho container của bảng chính */
}

/* Các style cho fade transition không còn cần thiết nếu bạn không dùng div riêng cho từng sprint */
/* .fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
} */
</style>
