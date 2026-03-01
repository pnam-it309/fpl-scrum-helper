<template>
  <DivCustom label="Bộ lọc" :enableToggle="true" :isOpened="false"  customClasses="mt-2">
    <template #icon>
      <FilterOutlined />
    </template>
    <div class="flex items-center space-x-2">
      <a-input
        v-model:value="localSearchQuery"
        placeholder="Nhập tên của công việc..."
        class="w-12/12"
      />
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

const props = defineProps<{ searchQuery: string }>()
const emit = defineEmits(['update:searchQuery'])

const localSearchQuery = ref(props.searchQuery)

watch(localSearchQuery, (newQuery) => {
  emit('update:searchQuery', newQuery)
})

const resetFilters = () => {
  localSearchQuery.value = ''
  emit('update:searchQuery', '')
}
</script>

<style scoped>
:deep(.ant-btn) {
  background: var(--selected-header) !important; /* Gradient nền */
  color: var(--selected-text) !important; /* Màu chữ trắng */
  border-color: var(--selected-header) !important; /* Viền màu từ gradient */
}

/* Hiệu ứng hover */
:deep(.ant-btn:hover),
:deep(.ant-btn:focus) {
  background: var(--selected-header-hover) !important; /* Gradient khi hover */
  border-color: var(--selected-header-hover) !important; /* Viền khi hover */
}
</style>
