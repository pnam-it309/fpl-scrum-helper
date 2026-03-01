<template>
  <DivCustom label="Danh sách chức vụ" customClasses="mt-5">
    <template #icon>
      <SecurityScanOutlined />
    </template>
    <div class="min-h-[360px]">
      <a-table
        :columns="columns"
        :data-source="formattedData"
        size="middle"
        :pagination="{
          current: page,
          pageSize: size,
          total: totalItems,
          showSizeChanger: true,
          pageSizeOptions: ['10', '20', '30', '40', '50']
        }"
        :scroll="{ x: 800, y: 500 }"
        @change="handlePageChange"
      >
      </a-table>
    </div>
  </DivCustom>
</template>

<script setup lang="ts">
import { computed, defineProps, defineEmits } from 'vue'
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import { TableColumnsType } from 'ant-design-vue'
import { IdcardOutlined, SecurityScanOutlined } from '@ant-design/icons-vue'

const props = defineProps<{
  data: { id: string; roleCode: string; roleName: string; facilityName: string | null }[]
  page: number
  size: number
  totalItems: number
}>()

const emit = defineEmits(['page-change'])

const formattedData = computed(() =>
  props.data.map((item, index) => ({
    ...item,
    orderNumber: (props.page - 1) * props.size + index + 1
  }))
)

const columns: TableColumnsType = [
  {
    title: 'STT',
    key: 'orderNumber',
    dataIndex: 'orderNumber',
    width: 20,
    align: 'center',
    fixed: 'left'
  },
  {
    title: 'Mã chức vụ',
    key: 'roleCode',
    dataIndex: 'roleCode',
    width: 100,
    align: 'center',
    fixed: 'left'
  },
  {
    title: 'Tên chức vụ',
    key: 'roleName',
    dataIndex: 'roleName',
    width: 100,
    align: 'center'
  },
  {
    title: 'Cơ sở',
    key: 'facilityName',
    dataIndex: 'facilityName',
    width: 100,
    align: 'center',
    customRender: ({ text }) => (text ? text : 'Không có cơ sở') // Xử lý nếu null
  }
]

// ✅ Xử lý sự kiện thay đổi trang
const handlePageChange = (pagination: any) => {
  emit('page-change', { page: pagination.current, pageSize: pagination.pageSize })
}
</script>
