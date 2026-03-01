<template>
  <DivCustom label="Danh sách bộ môn theo cơ sở" customClasses="mt-5">
    <template #icon>
      <GlobalOutlined />
    </template>
    <template #extra>
      <a-tooltip title="Thêm bộ môn theo cơ sở">
      <a-button
        type="primary"
        @click="handleAddClick"
        style="background-color: #cc9708 !important"
        class="flex items-center justify-center px-4 text-black-2"
      >
        <PlusCircleOutlined /> Thêm bộ môn theo cơ sở
      </a-button>

      </a-tooltip>
    </template>
    <div class="min-h-[360px]">
      <a-table
        :columns="columns"
        size="middle"
        :data-source="departmentFacility"
        :pagination="{
          current: paginationParams.page,
          pageSize: paginationParams.size,
          total: totalItems,
          showSizeChanger: true,
          pageSizeOptions: ['10', '20', '30', '40', '50']
        }"
        :scroll="{ y: 300 }"
        @change="handlePageChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'operation'">
            <div class="flex flex gap-1 justify-center">
              <a-tooltip title="Cập nhật">
                <a-button
                  @click="handleViewClick(record.departmentFacilityId)"
                  class="flex items-center justify-center w-8 h-8"
                >
                  <FormOutlined />
                </a-button>
              </a-tooltip>

              <a-tooltip title="Thay đổi trạng thái">
                <a-button
                  @click="handlUpdateStatusClick(record.departmentFacilityId)"
                  class="flex items-center justify-center w-8 h-8"
                >
                  <ReloadOutlined />
                </a-button>
              </a-tooltip>

              <a-tooltip title="Chuyên ngành theo cơ sở">
                <a-button
                  @click="handleMajorFacility(record.departmentFacilityId)"
                  class="flex items-center justify-center w-8 h-8 cursor-pointer p-2"
                >
                  <ExclamationCircleOutlined />
                </a-button>
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
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import {
  ReloadOutlined,
  ExclamationCircleOutlined,
  FormOutlined,
  GlobalOutlined,
  PlusCircleOutlined
} from '@ant-design/icons-vue'
import { TableColumnsType, Tag } from 'ant-design-vue'
import { library } from '@fortawesome/fontawesome-svg-core'
import {
  faReceipt,
  faPenToSquare,
  faCircleInfo,
  faFilter,
  faRotateRight
} from '@fortawesome/free-solid-svg-icons'

library.add(faReceipt, faPenToSquare, faCircleInfo, faFilter, faRotateRight)

defineProps<{
  searchQuery: string
  departmentFacility: any[]
  paginationParams: { page: number; size: number }
  totalItems: number
}>()
const emit = defineEmits(['page-change', 'add', 'view', 'major-facility', 'update-status'])

const columns: TableColumnsType = [
  {
    title: 'STT',
    key: 'orderNumber',
    dataIndex: 'orderNumber',
    width: 5,
    align: 'center'
  },
  {
    title: 'Mã bộ môn cơ sở',
    key: 'facilityCode',
    dataIndex: 'facilityCode',
    width: 10,
    align: 'center'
  },
  {
    title: 'Tên bộ môn cơ sở',
    key: 'facilityName',
    dataIndex: 'facilityName',
    width: 10,
    align: 'center'
  },
  {
    title: 'Trạng thái',
    key: 'departmentFacilityStatus',
    dataIndex: 'departmentFacilityStatus',
    width: 10,
    align: 'center',

    customRender: ({ text }) => {
      return h(
        Tag,
        {
          color: text === 'ACTIVE' ? 'green' : 'red'
        },
        () => (text === 'ACTIVE' ? 'Hoạt động' : 'Ngừng hoạt động')
      )
    }
  },
  {
    title: 'Hành động',
    key: 'operation',
    width: 10,
    align: 'center',
    fixed: 'right'
  }
]

const handlePageChange = (Pagination: any) => {
  emit('page-change', { page: Pagination.current, pageSize: Pagination.pageSize })
}

const handleAddClick = () => {
  emit('add')
}

const handleViewClick = (departmentFacilityId: string) => {
  emit('view', departmentFacilityId)
}

const handleMajorFacility = (departmentFacilityId: string) => {
  console.log('🆕 Gửi ID sang cha:', departmentFacilityId)
  emit('major-facility', departmentFacilityId)
}

const handlUpdateStatusClick = (departmentFacilityId: string) => {
  emit('update-status', departmentFacilityId)
}
</script>

<style scoped>
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
