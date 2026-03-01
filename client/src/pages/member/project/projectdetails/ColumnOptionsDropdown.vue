<template>
  <div class="inline-block text-left">
    <button
      @click="toggleDropdown"
      ref="buttonRef"
      class="hover:bg-gray-200 p-1 flex items-center justify-center rounded-full h-auto leading-none"
    >
      <svg
        xmlns="http://www.w3.org/2000/svg"
        class="h-5 w-5 text-gray-600"
        viewBox="0 0 20 20"
        fill="currentColor"
      >
        <path
          fill-rule="evenodd"
          d="M6 10a2 2 0 114 0 2 2 0 01-4 0zm6 0a2 2 0 114 0 2 2 0 01-4 0zM4 10a2 2 0 11-4 0 2 2 0 014 0z"
          clip-rule="evenodd"
        />
      </svg>
    </button>

    <div
      v-if="dropdownOpen"
      class="fixed w-48 bg-white border border-gray-300 rounded-md shadow-lg z-50"
      :style="dropdownStyle"
    >
      <div class="px-4 py-2 border-b border-gray-200 font-medium text-sm text-gray-700 flex items-center gap-1">
        <DragOutlined class="text-gray-600 text-sm" />
        Hành động
      </div>
      <ul class="py-1">
        <li
          @click="handleDelete"
          class="cursor-pointer px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 flex items-center gap-2"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-4 w-4 text-red-500"
            viewBox="0 0 20 20"
            fill="currentColor"
          >
            <path
              fill-rule="evenodd"
              d="M6 8a1 1 0 011-1h6a1 1 0 011 1v8a1 1 0 01-1 1H7a1 1 0 01-1-1V8zm3-5a1 1 0 00-1 1v1H5a1 1 0 100 2h10a1 1 0 100-2h-3V4a1 1 0 00-1-1H9z"
              clip-rule="evenodd"
            />
          </svg>
          Xóa danh sách
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onBeforeUnmount, onMounted } from 'vue'
import type { MBMeBoardResponse } from '@/services/api/member/projectdetail/metodo.api'
import { DragOutlined } from '@ant-design/icons-vue'

const props = defineProps<{
  column: MBMeBoardResponse
}>()

const emit = defineEmits<{
  (e: 'delete', column: MBMeBoardResponse): void
}>()

const dropdownOpen = ref(false)
const dropdownStyle = ref<{ top: string; left: string }>({ top: '0px', left: '0px' })
const buttonRef = ref<HTMLElement | null>(null)

const toggleDropdown = (event: MouseEvent) => {
  dropdownOpen.value = !dropdownOpen.value
  if (dropdownOpen.value && buttonRef.value) {
    const rect = buttonRef.value.getBoundingClientRect()
    dropdownStyle.value = {
      top: `${rect.bottom + window.scrollY}px`,
      left: `${rect.left + window.scrollX}px`
    }
  }
}

const handleClickOutside = (event: MouseEvent) => {
  if (
    dropdownOpen.value &&
    buttonRef.value &&
    !buttonRef.value.contains(event.target as Node)
  ) {
    dropdownOpen.value = false
  }
}

const handleDelete = () => {
  emit('delete', props.column)
  dropdownOpen.value = false
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>
