<template>
  <hr>
  <div class="w-full max-w-lg mt-3">
    <div class="flex justify-between items-center">
      <h3 class="text-base font-semibold text-gray-800 flex items-center space-x-2">
        <ClockCircleOutlined /> <span>Hoạt động</span>
      </h3>
      <button @click="toggleShowAll" class="text-black-2 text-sm focus:outline-none">
        {{ showAll ? 'Ẩn bớt' : 'Xem đầy đủ' }}
        <UpOutlined v-if="showAll" />
        <DownOutlined v-else />
      </button>
    </div>
    <div>
      <div v-for="(activity, index) in activities" :key="index" class="p-2 border-b">
        <div class="flex items-start space-x-2">
          <img
            :src="activity.memberImage || 'https://cdn.sforum.vn/sforum/wp-content/uploads/2023/10/avatar-trang-4.jpg'"
            alt="Avatar" class="w-8 h-8 rounded-full" />

          <div class="flex-1">
            <p class="text-sm">
              <span class="font-semibold">{{ activity.memberName }}</span> {{ activity.contentAction }}
            </p>
            <p class="text-xs text-gray-500">{{ getDateFormat(activity.createdDate, true) }}</p>
            <!-- Hiển thị ảnh nếu có, click mở tab mới -->
            <div v-if="activity.urlImage" class="mt-2">
              <img :src="activity.urlImage" alt="Ảnh hoạt động"
                class="w-24 h-24 object-cover rounded-md shadow-sm cursor-pointer"
                @click="openImage(activity.urlImage)">
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { TODO_ID_STORAGE_KEY } from '@/constants/key';
import { MBMeActivityResponse } from '@/services/api/member/projectdetail/activity.api';
import { getAllActivityWhereIdTodo } from '@/services/api/member/projectdetail/activity.api';
import { webSocketActivity } from '@/services/service/member/activity.socket';
import { webSocketTodoChild } from '@/services/service/member/todochild.socket';
import { getDateFormat } from '@/utils/commom.helper';
import { localStorageAction } from '@/utils/storage';
import { ClockCircleOutlined, DownOutlined, UpOutlined } from '@ant-design/icons-vue';
import { ref, reactive, onMounted } from 'vue';

const activities = ref<MBMeActivityResponse[]>([]);
const showAll = ref(false);
const pageable = reactive({
  page: 1,
  size: 1,
});

const fetchTodoActivity = async () => {
  try {
    const param = {
      idTodo: localStorageAction.get(TODO_ID_STORAGE_KEY),
      page: pageable.page,
      size: pageable.size
    };
    const res = await getAllActivityWhereIdTodo(param);
    console.log("Dữ liệu hoạt động:", res.data.data);
    activities.value = res.data.data;
  } catch (err) {
    console.error('Lỗi khi lấy dữ liệu:', err);
  }
};

onMounted(() => {
  fetchTodoActivity();
});

const toggleShowAll = async () => {
  showAll.value = !showAll.value;
  pageable.size = showAll.value ? 50 : 1; // Hiển thị 50 hoặc chỉ 1
  await fetchTodoActivity();
};

// Mở ảnh trong tab mới
const openImage = (url: string) => {
  window.open(url, '_blank');
};
webSocketActivity.getAllActivityWhereIdTodo(fetchTodoActivity)
webSocketTodoChild.getUpdateProgress(fetchTodoActivity)
</script>
