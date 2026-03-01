<template>
  <DivCustom label="Danh sách công việc theo dự án" customClasses="mt-5">
    <template #icon>
      <OrderedListOutlined />
    </template>
    <template #extra>
      <div class="flex w-full justify-between">
        <a-button
          type="primary"
          @click="emit('openModalImportTodoList')"
          class="flex items-center justify-center px-4 mr-2"
        >
          Import task
        </a-button>

        <a-button
          type="primary"
          @click="emit('downloadTemplateImport')"
          class="flex items-center justify-center px-4 mr-2"
        >
          Download template
        </a-button>

        <a-button
          type="primary"
          @click="handleAddClick"
          class="flex items-center justify-center px-4 mr-2"
        >
          Thêm Task
        </a-button>

        <a-button
          type="primary"
          @click="emit('importLog')"
          class="flex items-center justify-center px-4 mr-2"
        >
          Lịch sử
        </a-button>

        <a-button
          type="primary"
          @click="emit('downloadImportLog')"
          class="flex items-center justify-center px-4"
        >
          Download file import lỗi
        </a-button>
      </div>
    </template>

    <div class="min-h-[360px]">
      <a-table
        :columns="columns"
        :data-source="todoList"
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
                <a-icon @click="handleViewClick(record.todoListId)">
                  <font-awesome-icon
                    :icon="['fas', 'pen-to-square']"
                    class="cursor-pointer p-2"
                    style="font-size: 20px"
                  />
                </a-icon>
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
import { EditOutlined, ExclamationCircleOutlined, OrderedListOutlined } from '@ant-design/icons-vue'
import { TableColumnsType, Tag } from 'ant-design-vue'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
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
  todoList: any[]
  paginationParams: { page: number; size: number }
  totalItems: number
}>()
const emit = defineEmits([
  'page-change',
  'add',
  'view',
  'downloadTemplateImport',
  'openModalImportTodoList',
  'importLog',
  'downloadImportLog'
])

const columns: TableColumnsType = [
  {
    title: 'STT',
    key: 'orderNumber',
    dataIndex: 'orderNumber',
    width: 5,
    align: 'center'
  },
  { title: 'Mã', key: 'codeTodoList', dataIndex: 'codeTodoList', width: 10, align: 'center' },
  { title: 'Tên', key: 'nameTodoList', dataIndex: 'nameTodoList', width: 10, align: 'center' },
  {
    title: 'Mô tả',
    key: 'describeTodoList',
    dataIndex: 'describeTodoList',
    width: 30,
    align: 'center'
  },
  {
    title: 'Trạng thái',
    key: 'statusTodoList',
    dataIndex: 'statusTodoList',
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

const handleViewClick = (todoListId: string) => {
  emit('view', todoListId)
}
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
