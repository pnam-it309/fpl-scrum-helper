<template>
  <DivCustom label="Bộ lọc" :enableToggle="true" :isOpened="true"  customClasses="mt-2">
         <template #icon>
        <FilterOutlined />
      </template>
    <div class="flex items-center space-x-2">
      <a-input v-model:value="localSearchQuery" placeholder="Nhập mã / tên để tìm kiếm..." />

      <a-input v-model:value="searchDepartment" placeholder="Nhập tên bộ môn / cơ sở" />

      <a-select v-model:value="searchStatus" placeholder="Chọn trạng thái">
        <a-select-option value="0"> Chưa diễn ra </a-select-option>
        <a-select-option value="1"> Đang diễn ra </a-select-option>
        <a-select-option value="2"> Đã kết thúc </a-select-option>
      </a-select>

      <a-tooltip title="Làm mới bộ lọc">
        <a-button @click="resetFilters" class="p-2 flex items-center justify-center">
          <ReloadOutlined />
        </a-button>
      </a-tooltip>
    </div>
  </DivCustom>
</template>

<script setup lang="ts">
import { ref, watch, defineProps, defineEmits } from 'vue'
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import { FilterOutlined, ReloadOutlined } from '@ant-design/icons-vue'

// Định nghĩa props
const props = defineProps<{ searchQuery: string; department: string; status: string }>()

// Định nghĩa emit
const emit = defineEmits<{
  (e: 'update:searchQuery', value: string): void
  (e: 'update:searchDepartment', value: string): void
  (e: 'update:searchStatus', value: string): void
}>()

// Biến reactive để theo dõi trạng thái
const localSearchQuery = ref(props.searchQuery)
const searchDepartment = ref(props.department)
const searchStatus = ref<string | '' | null>(props.status || '' || null)

// Theo dõi thay đổi và phát sự kiện
watch(localSearchQuery, (newQuery) => {
  emit('update:searchQuery', newQuery)
})

watch(searchDepartment, (newDepartment) => {
  emit('update:searchDepartment', newDepartment)
})

watch(searchStatus, (newStatus) => {
  emit('update:searchStatus', newStatus as string)
})

// Hàm reset bộ lọc
const resetFilters = () => {
  localSearchQuery.value = ''
  searchDepartment.value = ''
  searchStatus.value = null

  emit('update:searchQuery', '')
  emit('update:searchDepartment', '')
  emit('update:searchStatus', '')
}
</script>
