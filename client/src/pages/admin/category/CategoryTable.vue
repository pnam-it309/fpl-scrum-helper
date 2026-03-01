<template>
  <DivCustom label="Danh sách thể loại" customClasses="mt-5">
    <template #icon>
      <AppstoreOutlined />
    </template>
    <template #extra>
      <a-button
        type="primary"
        @click="handleAddClick"
        class="flex items-center justify-center px-4"
      >
        <PlusCircleOutlined /> Thêm Thể Loại
      </a-button>
    </template>
    <div class="min-h-[360px]">
      <a-table
        :columns="columns"
        :data-source="category"
        size="middle"
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
          <template v-if="column.key === 'categoryStatus'">
            <a-tag :color="record.categoryStatus === 0 ? 'green' : 'red'">
              {{ record.categoryStatus === 0 ? 'Hoạt động' : 'Ngừng hoạt động' }}
            </a-tag>
          </template>

          <template v-if="column.key === 'operation'">
            <div class="flex gap-1 justify-center">
              <a-tooltip title="Cập nhật">
                <a-button
                  @click="handleViewClick(record.id)"
                  class="flex items-center justify-center w-8 h-8"
                >
                  <FormOutlined />
                </a-button>
              </a-tooltip>

              <a-tooltip title="Thay đổi trạng thái">
                <a-button
                  @click="handlUpdateStatusClick(record.id)"
                  class="flex items-center justify-center w-8 h-8"
                >
                  <ReloadOutlined />
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
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import { TableColumnsType } from 'ant-design-vue'
import {
  faReceipt,
  faPenToSquare,
  faCircleInfo,
  faFilter,
  faRotateRight
} from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { library } from '@fortawesome/fontawesome-svg-core'
import { AppstoreOutlined, PlusCircleOutlined, ReloadOutlined } from '@ant-design/icons-vue'
library.add(faReceipt, faPenToSquare, faCircleInfo, faFilter, faRotateRight)
import { DeleteOutlined, EyeOutlined, FormOutlined, IdcardOutlined } from '@ant-design/icons-vue'
defineProps<{
  searchQuery: string
  category: any[]
  paginationParams: { page: number; size: number }
  totalItems: number
}>()

const emit = defineEmits(['page-change', 'add', 'view', 'update-status'])

const columns: TableColumnsType = [
  {
    title: 'STT',
    key: 'orderNumber',
    dataIndex: 'orderNumber',
    width: 5,
    align: 'center',
    customRender: ({ index }) => index + 1
  },
  { title: 'Mã', key: 'categoryCode', dataIndex: 'categoryCode', width: 15, align: 'center' },
  { title: 'Tên', key: 'categoryName', dataIndex: 'categoryName', width: 15, align: 'center' },
  {
    title: 'Trạng thái',
    key: 'categoryStatus',
    dataIndex: 'categoryStatus',
    width: 15,
    align: 'center',
    customRender: ({ text }) => (Number(text) === 0 ? 'Hoạt động' : 'Ngừng hoạt động')
  },
  {
    title: 'Hành động',
    key: 'operation',
    width: 15,
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

const handleViewClick = (id: string) => {
  emit('view', id)
}

const handlUpdateStatusClick = (id: string) => {
  emit('update-status', id)
}
</script>
