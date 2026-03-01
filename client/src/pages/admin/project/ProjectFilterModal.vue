<template>
  <DivCustom label="Bộ lọc">
    <div class="flex items-center space-x-2">
      <a-input v-model:value="localSearchQuery" placeholder="Nhập mã / tên để tìm kiếm..." />
      <!-- <a-select v-model:value="" placeholder="Chọn bộ môn" name="facility">
        <a-select-option
          v-for="facility in staffDetailFacility"
          :key="facility.id"
          :value="facility.id"
        >
          {{ facility.nameFacility }}
        </a-select-option>
      </a-select> -->
      <a-tooltip title="Làm mới bộ lọc">
        <a-button @click="resetFilters" class="p-2 flex items-center justify-center"
          ><ReloadOutlined
        /></a-button>
      </a-tooltip>
    </div>
  </DivCustom>
</template>

<script setup lang="ts">
import { ref, watch, defineProps, defineEmits } from 'vue'
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import { ReloadOutlined } from '@ant-design/icons-vue'

const props = defineProps<{ searchQuery: string }>()
const emit = defineEmits(['update:searchQuery'])

const localSearchQuery = ref(props.searchQuery)

const options = [
  { label: 'Hoạt động', value: 0 },
  { label: 'Ngừng hoạt động', value: 1 }
]

watch([localSearchQuery], ([newQuery]) => {
  emit('update:searchQuery', newQuery)
})

const resetFilters = () => {
  localSearchQuery.value = ''
  emit('update:searchQuery', '')
}
</script>
