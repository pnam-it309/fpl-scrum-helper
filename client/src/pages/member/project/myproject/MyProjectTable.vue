<template>
  <DivCustom label="Danh sách dự án" customClasses="mt-5">
    <div class="min-h-[360px]">
      <a-table
        :columns="columns"
        :data-source="myProject"
        :pagination="{
          current: paginationParams.page,
          pageSize: paginationParams.size,
          total: totalItems,
          showSizeChanger: true,
          pageSizeOptions: ['10', '20', '30', '40', '50']
        }"
        @change="handlePageChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'statusProject'">
            <a-tag :color="getStatusColor(record.statusProject)">
              {{ convertStatus(record.statusProject) }}
            </a-tag>
          </template>
          <template v-if="column.key === 'operation'">
            <div class="flex flex gap-1 justify-center">
              <a-tooltip title="Chi tiết">
                <a-icon @click="handleViewClick(record.id)">
                  <font-awesome-icon
                    :icon="['fas', 'eye']"
                    class="cursor-pointer p-2"
                    style="font-size: 20px"
                  />
                </a-icon>
              </a-tooltip>

              <!-- <a-tooltip title="Báo cáo">
                <a-icon @click="handleNavigateToTodoList(record.id)">
                  <font-awesome-icon
                    :icon="['fas', 'circle-info']"
                    class="cursor-pointer p-2"
                    style="font-size: 18px"
                  />
                </a-icon>
              </a-tooltip> -->
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
import { faEye, faCircleInfo } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { library } from '@fortawesome/fontawesome-svg-core'
library.add(faEye, faCircleInfo)

import { getDateFormat } from '@/utils/commom.helper'
import { router } from '@/routes/router'
import { ROUTES_CONSTANTS } from '@/constants/path'
import { useRouter } from 'vue-router'
const props = defineProps<{
  searchQuery: string
  myProject: any[]
  paginationParams: { page: number; size: number }
  totalItems: number
}>()

const emit = defineEmits(['page-change', 'view'])

const columns: TableColumnsType = [
  {
    title: 'STT',
    key: 'orderNumber',
    dataIndex: 'orderNumber',
    width: 5,
    align: 'center',
    customRender: ({ index }) => index + 1
  },
  { title: 'Tên dự án', key: 'nameProject', dataIndex: 'nameProject', width: 15, align: 'center' },
  {
    title: 'Tiến độ',
    key: 'progress',
    dataIndex: 'progress',
    width: 10,
    align: 'center',
    customRender: ({ record }) => `${record.progress}%`
  },
  {
    title: 'Thời gian',
    key: 'timeRange',
    dataIndex: ['projectStartDate', 'projectEndDate'],
    width: 15,
    align: 'center',
    customRender: ({ record }) => {
      const start = getDateFormat(record.projectStartDate)
      const end = getDateFormat(record.projectEndDate)
      return `${start} - ${end}`
    }
  },
  {
    title: 'Trạng thái',
    key: 'statusProject',
    dataIndex: 'statusProject',
    width: 15,
    align: 'center'
  },
  { title: 'Hành động', key: 'operation', width: 15, align: 'center', fixed: 'right' }
]

const convertStatus = (status: string) => {
  switch (status) {
    case 'CHUA_DIEN_RA':
      return 'Chưa diễn ra'
    case 'DANG_DIEN_RA':
      return 'Đang diễn ra'
    case 'DA_DIEN_RA':
      return 'Đã diễn ra'
    default:
      return 'Không xác định'
  }
}

const getStatusColor = (status: string) => {
  switch (status) {
    case 'CHUA_DIEN_RA':
      return 'green'
    case 'DANG_DIEN_RA':
      return 'blue'
    case 'DA_DIEN_RA':
      return 'red'
    default:
      return 'gray'
  }
}

const handlePageChange = (pagination: any) => {
  emit('page-change', { page: pagination.current, pageSize: pagination.pageSize })
}

const handleViewClick = (id: string) => {
  router.push({
    name: `${ROUTES_CONSTANTS.PROJECT.children.VOTE_TODO.name}`,
    params: { id }
  })
  emit('view', id)
}

const router = useRouter()
const handleNavigateToTodoList = (idProject: string) => {
  router.push({
    path: ROUTES_CONSTANTS.MEMBER.children.REPORT.path,
    query: { idProject }
  })
}
</script>
