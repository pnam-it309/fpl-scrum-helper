<template>
  <div class="bg-white rounded-2xl shadow-lg p-5 w-full border border-gray-300 space-y-2">

    <input v-model="newColumnName" type="text" placeholder="Nhập tiêu đề danh sách..."
      class="w-full text-sm border-gray-300 rounded-lg shadow-sm focus:border-blue-500 focus:ring-2 focus:ring-blue-500 p-2" />

    <div class="flex justify-between gap-2">

      <button type="button" class="flex-1 rounded-lg h-9 px-4 text-sm font-medium focus:ring-2 focus:ring-blue-500"
        :class="{
          'bg-blue-500 text-white hover:bg-blue-600': newColumnName.trim(),
          'bg-gray-300 text-gray-500 cursor-not-allowed': !newColumnName.trim()
        }" :disabled="!newColumnName.trim()" @click="addColumn">
        Thêm
      </button>

      <button type="button"
        class="rounded-lg h-9 px-4 text-sm font-medium bg-gray-200 hover:bg-gray-300 focus:ring-2 focus:ring-gray-300"
        @click="$emit('cancel')">
        Hủy
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import { MBMeCreateTodoListRequest, webSocketTodoList } from '@/services/service/member/metodolist.socket'
import { toast } from 'vue3-toastify'

const route = useRoute()
const projectId = route.params.id as string
const newColumnName = ref('')

const addColumn = () => {
  if (!newColumnName.value.trim()) {
    toast.warning("Tên danh sách không được để trống");
    return;
  }
  if (newColumnName.value.length > 100) {
    toast.error("Tên danh sách không được vượt quá 100 ký tự");
    return;
  }

  const payload: MBMeCreateTodoListRequest = {
    name: newColumnName.value.trim(),
    idProject: projectId,
    idPhase: route.params.idPhase
  }
  webSocketTodoList.sendCreateTodoList(projectId, payload)
  newColumnName.value = ''
}
</script>

<style scoped>
input:focus {
  outline: none;
}
</style>