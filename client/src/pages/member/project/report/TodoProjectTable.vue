<template>
  <div class="table-wrapper">
    <a-table :columns="columns" :data-source="todoProject" :pagination="paginationOptions" :scroll="{ y: 300 }"
      @change="handlePageChange" class="custom-table">
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'operation'">
          <div class="flex gap-1 justify-center">
            <!-- Các nút thao tác nếu cần -->
          </div>
        </template>

        <template v-else-if="column.key === 'priorityLevel'">
          <a-tag :color="statusPriorityLevelColors[record.priorityLevel]">
            {{ statusPriorityLevelOptions[record.priorityLevel] || 'Không xác định' }}
          </a-tag>
        </template>
      </template>
    </a-table>
  </div>
</template>

<script setup lang="ts">
import { defineProps, defineEmits } from 'vue';
import DivCustom from '@/components/custom/Div/DivCustom.vue';

const props = defineProps<{
  searchQuery: string,
  todoProject: any[],
  paginationParams: { page: number; size: number },
  totalItems: number
}>();

const emit = defineEmits(['page-change']);

const columns = [
  { title: 'STT', key: 'orderNumber', dataIndex: 'orderNumber', width: 5, align: 'center' },
  { title: 'Tên', key: 'nameTodo', dataIndex: 'nameTodo', width: 20, align: 'center' },
  { title: 'Mức độ ưu tiên', key: 'priorityLevel', dataIndex: 'priorityLevel', width: 15, align: 'center' },
];

const statusPriorityLevelOptions = {
  QUAN_TRONG: "Quan trọng",
  CAO: "Cao",
  TRUNG_BINH: "Trung bình",
  THAP: "Thấp",
};

const statusPriorityLevelColors = {
  THAP: "blue",
  CAO: "orange",
  TRUNG_BINH: "green",
  QUAN_TRONG: "red",
};

const paginationOptions = {
  current: props.paginationParams.page,
  pageSize: props.paginationParams.size,
  total: props.totalItems,
  showSizeChanger: true,
  pageSizeOptions: ['10', '20', '30', '40', '50']
};

const handlePageChange = (pagination: any) => {
  emit('page-change', { page: pagination.current, pageSize: pagination.pageSize });
};
</script>

<style scoped>
/* .table-wrapper {
  min-height: 360px;
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0px 5px 15px rgba(0, 0, 0, 0.1);
} */

.custom-table :deep(.ant-table-thead > tr > th) {
  color: #060606;
  font-weight: bold;
  text-align: center;
}

.custom-table :deep(.ant-table-tbody > tr:hover) {
  background: #f0faff;
  transition: 0.3s;
}
</style>
