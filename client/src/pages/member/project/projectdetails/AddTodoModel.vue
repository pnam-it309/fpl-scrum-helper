<template>
  <div v-if="isOpen" class="fixed inset-0 flex items-center justify-center bg-gray-900 bg-opacity-50">
    <div class="bg-white px-6 py-5 rounded-lg shadow-lg w-full max-w-2xl min-w-[500px]">
      
      <!-- Header -->
      <div class="flex justify-between items-center mb-5">
        <h2 class="text-lg font-semibold">Thêm thẻ</h2>
        <button @click="closeModal" class="text-gray-500 hover:text-gray-700">✖</button>
      </div>

      <!-- Nhập tên thẻ -->
      <label class="block text-sm font-medium text-gray-700">Tên</label>
      <input v-model="taskName" type="text" placeholder="Nhập tên cho thẻ này"
        class="w-full p-2 border rounded mt-1 focus:outline-none focus:ring focus:border-blue-400" />

      <!-- Chọn danh sách -->
      <label class="block text-sm font-medium text-gray-700 mt-4">Danh sách</label>
      <select v-model="selectedList" 
        class="w-full p-2 border rounded mt-1 focus:outline-none focus:ring focus:border-blue-400">
        <option v-for="list in todoLists" :key="list.id" :value="list.id">{{ list.name }}</option>
      </select>

      <!-- Thời gian -->
      <div class="mt-4">
        <label class="block text-sm font-medium text-gray-700">Thời gian</label>
        <div class="grid grid-cols-2 gap-5 mt-2">
          
          <!-- Ngày bắt đầu -->
          <div class="flex items-center gap-3">
            <input type="checkbox" v-model="hasStartDate" class="h-4 w-4 text-blue-500 cursor-pointer" />
            <div class="flex-1">
              <label class="text-xs font-medium text-gray-600 block">Ngày bắt đầu</label>
              <input type="date" v-model="startDate" :disabled="!hasStartDate"
                class="w-full p-2 border rounded bg-gray-100 text-gray-500 focus:outline-none disabled:bg-gray-200" />
            </div>
          </div>

          <!-- Ngày hết hạn -->
          <div class="flex items-center gap-3">
            <input type="checkbox" v-model="hasDueDate" class="h-4 w-4 text-blue-500 cursor-pointer" />
            <div class="flex-1">
              <label class="text-xs font-medium text-gray-600 block">Ngày hết hạn</label>
              <div class="flex gap-3">
                <input type="date" v-model="dueDate" :disabled="!hasDueDate"
                  class="w-3/5 p-2 border rounded bg-gray-100 text-gray-500 focus:outline-none disabled:bg-gray-200" />
                <input type="time" v-model="dueTime" :disabled="!hasDueDate"
                  class="w-24 p-2 border rounded bg-gray-100 text-gray-500 focus:outline-none disabled:bg-gray-200" />
              </div>
            </div>
          </div>

        </div>
      </div>

      <!-- Nút thêm thẻ -->
      <button @click="addTask" :disabled="!taskName || !selectedList"
        class="w-full bg-blue-500 text-white p-2 rounded mt-5 disabled:bg-gray-300">
        Thêm thẻ
      </button>

    </div>
  </div>
</template>
 
<script setup>
import { ref, defineProps, defineEmits } from "vue";
import  { webSocketTodo }  from "@/services/service/member/metodo.socket";

const props = defineProps(["isOpen", "todoLists"]);
const emit = defineEmits(["close", "taskAdded"]);

const taskName = ref("");
const selectedList = ref(null);
const hasStartDate = ref(false);
const hasDueDate = ref(false);
const startDate = ref("");
const dueDate = ref("");
const dueTime = ref("");

const addTask = () => {
  if (!taskName.value.trim() || !selectedList.value) return;

  const newTask = {
    name: taskName.value,
    todoListId: selectedList.value,
    startDate: hasStartDate.value ? startDate.value : null,
    dueDate: hasDueDate.value ? `${dueDate.value}T${dueTime.value || "23:59"}` : null, // Format ISO
  };

  // 🔹 Gửi thẻ mới qua WebSocket
  webSocketTodo.sendCreateTodo("projectId", "phaseId", {
  name: taskName.value,
  idTodoList: selectedList.value, 
  indexTodo: 0, // Giá trị mặc định hoặc lấy từ danh sách hiện tại
});


  // 🔹 Emit sự kiện để cập nhật UI ngay lập tức
  emit("taskAdded", newTask);

  closeModal();
};

const closeModal = () => {
  emit("close");
};
</script>
