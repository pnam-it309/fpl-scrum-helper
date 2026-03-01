<template>
  <DivCustom label="Bộ lọc" :enableToggle="true" :isOpened="false"  customClasses="mt-2">
    <template #icon>
      <FilterOutlined />
    </template>
    <div class="flex items-center space-x-2">
      <a-input v-model:value="localSearchQuery" placeholder="Nhập tên công việc " class="w-12/12" />

      <a-select
        placeholder="Độ ưu tiên"
        ref="select"
        v-model:value="searchLevelPriority"
        style="width: 300px"
        :options="optionLevelPriority"
      ></a-select>

      <a-select
        placeholder="Trạng thái công việc"
        ref="select"
        v-model:value="searchStatusPhase"
        style="width: 400px"
        :options="optionStatusPhase"
      ></a-select>

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
import { SelectProps } from 'ant-design-vue'

const props = defineProps<{ searchQuery: string }>()
const emit = defineEmits(['update:searchQuery'])

const localSearchQuery = ref(props.searchQuery)

const searchLevelPriority = ref()

const searchStatusPhase = ref()

const optionLevelPriority = ref<SelectProps['options']>([
  { value: 'QUAN_TRONG', label: 'Quan trọng' },
  { value: 'CAO', label: 'Cao' },
  { value: 'TRUNG_BINH', label: 'Trung bình' },
  { value: 'THAP', label: 'Thấp' }
])

const optionStatusPhase = ref<SelectProps['options']>([
  { value: 'CHUA_HOAN_THANH', label: 'Chưa hoàn thành' },
  { value: 'DA_HOAN_THANH', label: 'Đã hoàn thành' },
  { value: 'HOAN_THANH_SOM', label: 'Hoàn thành sớm' },
  { value: 'QUA_HAN', label: 'Quá hạn' }
])

watch(
  [localSearchQuery, searchLevelPriority, searchStatusPhase],
  ([newQuery, newLevel, newStatus]) => {
    console.log(newQuery, newLevel, newStatus)
    emit('update:searchQuery', {
      newQuery: newQuery,
      newLevel: newLevel,
      newStatus: newStatus
    })
  }
)

const resetFilters = () => {
  localSearchQuery.value = ''
  searchLevelPriority.value = undefined
  searchStatusPhase.value = undefined
  emit('update:searchQuery', '')
}
</script>
