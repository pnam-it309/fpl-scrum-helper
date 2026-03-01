<template>
  <DivCustom label="Danh sách chuyên ngành theo cơ sở" customClasses="mt-5">
    <template #icon>
      <ClusterOutlined />
    </template>
    <template #extra>
      <a-button
        type="primary"
        @click="handleAddClick"
        class="flex items-center justify-center px-4"
      >
        <PlusCircleOutlined /> Thêm chuyên ngành theo cơ sở
      </a-button>
    </template>
    <div class="min-h-[360px]" ref="tableWrapper">
      <a-table
        :columns="columns"
        :data-source="majorFacility"
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
              <a-tooltip title="Cập nhật" :get-popup-container="() => tableWrapper">
                <a-button
                  @click="handleViewClick(record.majorFacilityId)"
                  class="flex items-center justify-center w-8 h-8"
                >
                  <FormOutlined />
                </a-button>
              </a-tooltip>

              <a-tooltip title="Thay đổi trạng thái" :get-popup-container="() => tableWrapper">
                <a-button
                  @click="handlUpdateStatusClick(record.majorFacilityId)"
                  class="flex items-center justify-center w-8 h-8"
                  style="
                    background-color: #7ff5f9 !important;
                    color: #ffffff !important;
                    border: none !important;
                  "
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
import { defineProps, defineEmits, ref, h } from 'vue'
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import {
  ClusterOutlined,
  EditOutlined,
  FormOutlined,
  PlusCircleOutlined,
  ReloadOutlined
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

const tableWrapper = ref<HTMLElement | null>(null)

defineProps<{
  searchQuery: string
  majorFacility: any[]
  paginationParams: { page: number; size: number }
  totalItems: number
  departmentFacilityId: string
}>()

const emit = defineEmits(['page-change', 'add', 'view', 'update-status'])

const columns: TableColumnsType = [
  {
    title: 'STT',
    key: 'orderNumber',
    dataIndex: 'orderNumber',
    width: 5,
    align: 'center'
  },
  { title: 'Mã', key: 'majorCode', dataIndex: 'majorCode', width: 10, align: 'center' },
  { title: 'Tên', key: 'majorName', dataIndex: 'majorName', width: 10, align: 'center' },
  {
    title: 'Trạng thái',
    key: 'majorFacilityStatus',
    dataIndex: 'majorFacilityStatus',
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

const handleViewClick = (majorFacilityId: string) => {
  emit('view', majorFacilityId)
}

const handlUpdateStatusClick = (majorFacilityId: string) => {
  emit('update-status', majorFacilityId)
}
</script>
