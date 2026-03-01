<template>
  <DivCustom label="Bộ lọc" :enableToggle="true" :isOpened="true"  customClasses="mt-2">
    <template #icon>
      <FilterOutlined />
    </template>
    <div class="flex items-center space-x-2">
      <a-input v-model:value="localSearchQuery" placeholder="Nhập tên cơ sở..." class="w-8/12" />

      <a-select
        v-model:value="localSearchStatus"
        :options="options"
        placeholder="Trạng thái"
        class="w-4/12"
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
const emit = defineEmits(['update:searchQuery', 'update:searchStatus'])

const localSearchQuery = ref(props.searchQuery)

watch(localSearchQuery, (newQuery) => {
  emit('update:searchQuery', newQuery)
})

const options = [
  { label: 'Tất cả', value: null },
  { label: 'Hoạt động', value: 'ACTIVE' },
  { label: 'Ngừng hoạt động', value: 'INACTIVE' }
]

const localSearchStatus = ref<string | null>(null)

watch(localSearchStatus, (newStatus) => {
  emit('update:searchStatus', newStatus)
})

const resetFilters = () => {
  localSearchQuery.value = ''
  emit('update:searchQuery', '')
  localSearchStatus.value = null
  emit('update:searchStatus', null)
}
</script>
