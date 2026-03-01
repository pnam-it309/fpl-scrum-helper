<template>
  <DivCustomOld label="Danh sách role" customClasses="mt-5">
    <template #extra>
      <a-tooltip title="Cập nhật role nhân viên">
        <a-button
          type="primary"
          @click="handleAddClick"
          class="flex items-center justify-center px-4"
        >
          <FormOutlined />Cập nhật role
        </a-button>
      </a-tooltip>
    </template>
    <div class="min-h-[360px]">
      <a-table :columns="columns" :data-source="data" :scroll="{ y: 300 }">
        <template #bodyCell="{ column, record, index }">
          <template v-if="column.key === 'nameRole'">
            <span :class="column.value === 'ADMIN' ? 'text-red-500' : 'text-black'">
              <a-tag color="success">{{ record.nameRole }}</a-tag>
            </span>
          </template>

          <template v-if="column.key === 'orderNumber'">
            {{ index + 1 }}
          </template>

          <template v-if="column.key === 'operation'">
            <div class="flex items-center gap-1 justify-center">
              <a-tooltip title="Xóa nhân viên">
                <a-icon @click="handleDeleteClick(record.id)">
                  <font-awesome-icon
                    :icon="['fas', 'trash-alt']"
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
  </DivCustomOld>
</template>

<script setup lang="ts">
import { defineProps, defineEmits, h } from 'vue'
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import { PlusCircleOutlined, DeleteOutlined, FormOutlined } from '@ant-design/icons-vue'
import { TableColumnsType } from 'ant-design-vue'
import { roleByStaffResponse } from '@/services/api/admin/staff.api'
import { router } from '@/routes/router'
import { ROUTES_CONSTANTS } from '@/constants/path'
import {
  faReceipt,
  faPenToSquare,
  faCircleInfo,
  faFilter,
  faRotateRight,
  faTrash,
  faTrashAlt,
  faEye
} from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { library } from '@fortawesome/fontawesome-svg-core'
import DivCustomOld from '@/components/custom/Div/DivCustomOld.vue'
library.add(
  faReceipt,
  faPenToSquare,
  faCircleInfo,
  faFilter,
  faRotateRight,
  faTrash,
  faTrashAlt,
  faEye
)
const props = defineProps<{
  data: roleByStaffResponse[]
}>()

const emit = defineEmits(['page-change', 'add', 'view', 'delete'])

const columns: TableColumnsType = [
  {
    title: 'STT',
    key: 'orderNumber',
    dataIndex: 'orderNumber',
    width: 40,
    align: 'center'
  },
  {
    title: 'Mã role',
    key: 'code',
    dataIndex: 'code',
    width: 50,
    align: 'center'
  },
  {
    title: 'Tên role',
    key: 'name',
    dataIndex: 'name',
    width: 50,
    align: 'center'
  },
  {
    title: 'Cơ sở',
    key: 'nameFacility',
    dataIndex: 'nameFacility',
    width: 50,
    align: 'center'
  }
]

const handlePageChange = (pagination: any) => {
  emit('page-change', { page: pagination.current, pageSize: pagination.pageSize })
}

const handleAddClick = () => {
  emit('add')
}

const handleViewClick = (id: string) => {
  emit('view', id)
}

const handleDeleteClick = (id: string) => {
  emit('delete', id)
}

const handleDetailClick = (id: string) => {
  router.push({
    name: `${ROUTES_CONSTANTS.ADMIN.children.STAFF_DETAIL.name}`,
    params: { id }
  })
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
