<template>
  <DivCustom label="Bộ lọc" :enableToggle="true" :isOpened="true"  customClasses="mt-2">
    <template #icon>
      <FilterOutlined />
    </template>
    <div class="flex items-center space-x-2">
      <a-input v-model:value="localQ" placeholder="Nhập mã / tên chức vụ..." />
      <a-input v-model:value="localDepartment" placeholder="Nhập tên cơ sở..." />
      <a-tooltip title="Làm mới bộ lọc">
        <a-button @click="resetFilters" class="items-center flex justify-center">
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
const props = defineProps<{ q: string; department: string }>()
const emit = defineEmits<{
  (e: 'update:q', value: string): void
  (e: 'update:department', value: string): void
}>()

const localQ = ref(props.q)
const localDepartment = ref(props.department)

watch(localQ, (newVal) => emit('update:q', newVal))
watch(localDepartment, (newVal) => emit('update:department', newVal))

const resetFilters = () => {
  localQ.value = ''
  localDepartment.value = ''
  emit('update:q', '')
  emit('update:department', '')
}
</script>
