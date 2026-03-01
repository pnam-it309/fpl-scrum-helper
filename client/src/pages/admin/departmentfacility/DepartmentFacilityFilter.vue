<template>
  <DivCustom label="Bộ lọc" :enableToggle="true" :isOpened="true"  customClasses="mt-2">
    <template #icon>
      <FilterOutlined />
    </template>
    <div class="flex items-center space-x-2">
      <a-input v-model:value="localSearchQuery" placeholder="Nhập tên cơ sở..." class="w-12/12" />
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
