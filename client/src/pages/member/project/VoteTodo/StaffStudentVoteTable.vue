<template>
  <div class="w-full">
    <div class="overflow-x-auto">
      <a-table
        :columns="columns"
        :data-source="staffstudent"
        :pagination="{
          current: paginationParams.page,
          pageSize: paginationParams.size,
          total: totalItems,
          showSizeChanger: true,
          pageSizeOptions: ['10', '20', '30', '40', '50']
        }"
        :scroll="{ x: '1000px' }"
        @change="handlePageChange"
      >
      </a-table>
    </div>
  </div>
</template>

  
  <script setup lang="ts">
  import { defineProps, defineEmits, h } from 'vue'
  import { TableColumnsType, Tag } from 'ant-design-vue'
  import { library } from '@fortawesome/fontawesome-svg-core'
  import { faReceipt, faPenToSquare, faCircleInfo, faFilter } from '@fortawesome/free-solid-svg-icons'
  
  library.add(faReceipt, faPenToSquare, faCircleInfo, faFilter)
  
  defineProps<{
    searchQuery: string
    staffstudent: any[]
    paginationParams: { page: number; size: number }
    totalItems: number
  }>()
  
  const emit = defineEmits(['page-change'])
  
  const columns: TableColumnsType = [
    {
      title: 'STT',
      key: 'orderNumber',
      dataIndex: 'orderNumber',
      width: 5,
      align: 'center'
    },
    { title: 'Thành viên', key: 'nameStaff', dataIndex: 'nameStaff', width: 200, align: 'center' },
    { title: 'Chức vụ', key: 'role', dataIndex: 'role', width: 10, align: 'center' },
    {
      title: 'Trạng thái',
      key: 'voteStatus',
      dataIndex: 'voteStatus',
      width: 10,
      align: 'center',
  
      customRender: ({ text }) => {
        return h(
          Tag,
          {
            color: text === 'true' ? 'green' : 'red',
          },
          () => (text === 'true' ? 'Đã bình chọn' : 'Chưa bình chọn')
        )
      }
  
    }
  ]
  
  const handlePageChange = (Pagination: any) => {
    emit('page-change', { page: Pagination.current, pageSize: Pagination.pageSize })
  }
  
  </script>
  