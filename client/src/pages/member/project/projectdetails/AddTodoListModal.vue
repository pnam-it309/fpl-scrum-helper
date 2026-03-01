<template>
    <div class="bg-white rounded-xl shadow-md p-4 w-full border border-gray-200 space-y-3">
      <input
        v-model="newColumnName"
        placeholder="Nhập tiêu đề danh sách..."
        class="w-full px-4 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-400 placeholder-gray-500 transition"
      />
  
      <select
        v-model="selectedPosition"
        class="w-full px-4 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-400 transition"
      >
        <option v-for="(list, index) in todoLists" :key="list.id" :value="index">
          {{ list.name }} (Vị trí {{ index + 1 }})
        </option>
        <option :value="todoLists.length">Cuối danh sách</option>
      </select>
  
      <div class="flex justify-between gap-2">
        <button
          class="flex-1 px-4 py-2 bg-blue-500 text-white text-sm font-semibold rounded-lg hover:bg-blue-600 transition"
          @click="addColumn"
        >
          Thêm danh sách
        </button>
        <button
          class="flex-shrink-0 px-4 py-2 bg-gray-200 text-sm text-gray-700 font-medium rounded-lg hover:bg-gray-300 transition"
          @click="$emit('cancel')"
        >
          Hủy
        </button>
      </div>
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref } from "vue";
  import { useRoute } from "vue-router";
  import { MBMeCreateTodoListRequest, webSocketTodoList } from "@/services/service/member/metodolist.socket";
  
  defineProps<{ todoLists: any[] }>();
  const emit = defineEmits(["added", "cancel"]);
  
  const route = useRoute();
  const projectId = route.params.idProject as string;
  
  const newColumnName = ref("");
  const selectedPosition = ref(0);
  
  const addColumn = () => {
    if (!newColumnName.value.trim()) return;
  
    const payload: MBMeCreateTodoListRequest = {
      name: newColumnName.value.trim(),
      idProject: projectId,
      position: selectedPosition.value,
    };
  
    webSocketTodoList.sendCreateTodoList(projectId, payload);
    newColumnName.value = "";
    emit("added");
  };
  </script>
  