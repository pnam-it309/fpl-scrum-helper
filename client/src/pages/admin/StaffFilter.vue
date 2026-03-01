<template>
  <DivCustom label="Bộ lọc" :enableToggle="true" :isOpened="true" customClasses="mt-2">
    <template #icon>
      <FilterOutlined />
    </template>
    <div class="flex flex-col md:flex-row items-center gap-4">
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4 w-full">
        <!-- Ô nhập liệu tìm kiếm -->
        <a-input
          v-model:value="localSearchQuery"
          placeholder="Nhập mã / tên để tìm kiếm..."
          allow-clear
        />

        <!-- Ô chọn bộ môn -->
        <a-select
          v-model:value="localSearchFacility"
          placeholder="Chọn cơ sở"
          name="department"
          allow-clear
        >
          <a-select-option v-for="item in dataFacility" :key="item.id" :value="item.id">
            {{ item.name }}
          </a-select-option>
        </a-select>

        <!-- Ô chọn trạng thái -->
        <a-select v-model:value="localSearchStatus" placeholder="Chọn trạng thái" allow-clear>
          <a-select-option value="0">Đang làm việc</a-select-option>
          <a-select-option value="1">Đã nghỉ việc</a-select-option>
        </a-select>
      </div>

      <!-- Nút làm mới -->
      <a-tooltip title="Làm mới bộ lọc">
        <a-button @click="resetFilters" class="p-3 flex items-center justify-center min-w-0">
          <ReloadOutlined />
        </a-button>
      </a-tooltip>
    </div>
  </DivCustom>
</template>

<script setup lang="ts">
import { ref, watch, defineProps, defineEmits, onMounted } from 'vue'
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import { FilterOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import { getAllStaffFacility, staffDetailIdResponse } from '@/services/api/admin/staff.api'

const props = defineProps<{ searchQuery: string; searchFacility: string; status: string }>()
const emit = defineEmits<{
  (e: 'update:searchQuery', value: string): void
  (e: 'update:searchFacility', value: string): void
  (e: 'update:status', value: string): void
}>()

const dataFacility = ref<staffDetailIdResponse[]>()

const fetchFacility = async () => {
  const res = await getAllStaffFacility()
  // Lọc dữ liệu ngay sau khi nhận được từ API
  dataFacility.value = res.data.filter((facility) => facility.status === 'ACTIVE')
}

onMounted(() => {
  fetchFacility()
})

const localSearchQuery = ref(props.searchQuery)
const localSearchFacility = ref<string | null>(props.searchFacility || null)
const localSearchStatus = ref<string | null>(props.status || null)

const options = [
  { label: 'Hoạt động', value: 0 },
  { label: 'Ngừng hoạt động', value: 1 }
]

watch([localSearchQuery], ([newQuery]) => {
  emit('update:searchQuery', newQuery)
})

watch([localSearchFacility], ([newQuery]) => {
  emit('update:searchFacility', newQuery as string)
})

watch([localSearchStatus], ([newQuery]) => {
  emit('update:status', newQuery as string)
})

const resetFilters = () => {
  localSearchQuery.value = ''
  localSearchFacility.value = null
  localSearchStatus.value = null
  emit('update:searchQuery', '')
  emit('update:searchFacility', '')
  emit('update:status', '')
}
</script>
