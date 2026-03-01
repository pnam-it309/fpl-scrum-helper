<template>
  <DivCustom label="Lịch Sử Import Excel" customClasses="mt-5">
    <div class="min-h-[360px]">
      <div class="min-h-[360px]">
        <a-table
          :columns="columns"
          :data-source="history"
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
        </a-table>
      </div>
    </div>
  </DivCustom>
</template>

<script setup lang="ts">
import { defineProps, defineEmits, h } from 'vue'
import { TableColumnsType, Tag } from 'ant-design-vue'
import dayjs from 'dayjs'
import DivCustom from '@/components/custom/Div/DivCustom.vue'

defineProps<{
  history: any[]
  paginationParams: { page: number; size: number }
  totalItems: number
}>()

const emit = defineEmits(['page-change', 'close', 'download'])

const columns: TableColumnsType = [
  {
    title: 'STT',
    key: 'stt',
    width: 10,
    align: 'center',
    customRender: ({ index }) => index + 1
  },

  {
    title: 'Email người thực hiện',
    key: 'email',
    dataIndex: 'email',
    width: 20,
    align: 'center',
  },

  {
    title: 'Ngày tạo',
    key: 'createdDate',
    dataIndex: 'createdDate',
    width: 20,
    align: 'center',
    customRender: ({ text }) =>
      text ? dayjs(text).format('DD/MM/YYYY HH:mm:ss') : 'Không có dữ liệu'
  },

  { title: 'Nội dung', key: 'message', dataIndex: 'message', width: 20, align: 'center' },
  {
    title: 'Chức vụ',
    key: 'name',
    width: 20,
    align: 'center',
    customRender: ({ record }) => {
      return record?.roles?.map((role) => role.name).join(', ') || 'Không có chức vụ'
    }
  }
]

const handlePageChange = (Pagination: any) => {
  emit('page-change', { page: Pagination.current, pageSize: Pagination.pageSize })
}
</script>
