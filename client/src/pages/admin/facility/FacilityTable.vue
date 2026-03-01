<template>
  <DivCustom label="Danh sách cơ sở" customClasses="mt-5">
    <template #icon>
      <EnvironmentOutlined />
    </template>
    <template #extra>

      <a-tooltip title="Thêm mới cơ sở">
        <a-button
          type="primary"
          @click="handleAddClick"
          class="flex items-center justify-center px-4"
        >
          <PlusCircleOutlined /> Thêm cơ sở
        </a-button>
      </a-tooltip>

    </template>
    <div class="min-h-[360px]">
      <a-table
        :columns="columns"
        :data-source="facility"
        size="middle"
        :pagination="{
          current: paginationParams.page,
          pageSize: paginationParams.size,
          total: totalItems,

          showSizeChanger: true,
          pageSizeOptions: ['10', '20', '30', '40', '50']
        }"
        :scroll="{ y: 500 }"
        @change="handlePageChange"
      >
        <template #bodyCell="{ column, record }">
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
import {
  EditOutlined,
  EnvironmentOutlined,
  FormOutlined,
  PlusCircleOutlined,
  ReloadOutlined
} from '@ant-design/icons-vue'
import { TableColumnsType, Tag } from 'ant-design-vue'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { library } from '@fortawesome/fontawesome-svg-core'
import { h } from 'vue'
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
  facility: any[]
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
    align: 'center'
  },
  { title: 'Mã cơ sở', key: 'facilityCode', dataIndex: 'facilityCode', width: 10, align: 'center' },
  {
    title: 'Tên cơ sở',
    key: 'facilityName',
    dataIndex: 'facilityName',
    width: 10,
    align: 'center'
  },
  {
    title: 'Trạng thái',
    key: 'facilityStatus',
    dataIndex: 'facilityStatus',
    width: 10,
    align: 'center',
    customRender: ({ text }) => {
      return h(
        Tag,
        {
          color: Number(text) === 0 ? 'green' : 'red'
        },
        () => (Number(text) === 0 ? 'Hoạt động' : 'Ngừng hoạt động')
      )
    }
    // customRender: ({ text }) => (Number(text) === 0 ? 'Hoạt động' : 'Ngừng hoạt động')
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

const handleViewClick = (id: string) => {
  emit('view', id)
}

const handlUpdateStatusClick = (id: string) => {
  emit('update-status', id)
}
</script>
