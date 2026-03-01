<template>
  <div>
    <!-- Nút mở menu -->
    <button @click="toggleMenu" class="rounded-lg hover:bg-gray-200 transition z-[9999] w-10">
      <EllipsisOutlined class="text-2xl text-gray-600" />
    </button>

    <!-- Sidebar chung using Ant Design a-drawer -->
    <a-drawer
      v-model:visible="menuOpen"
      placement="right"
      :closable="false"
      :width="420"
      :destroy-on-close="true"
      class="z-[9999999]"
    >
      <div>
        <!-- Header  -->
        <div class="flex items-center justify-between border-b">
          <!-- Nút quay lại nếu ở chế độ "Hoạt động" -->
          <button v-if="currentMode" @click="closeMode" class="text-gray-500 mr-2">
   
            <LeftOutlined class="text-lg" />
          </button>

          <!-- Tiêu đề thay đổi theo chế độ -->
          <h2 class="text-lg font-semibold mx-auto">
            {{ currentMode ? currentMode.title : 'Menu' }}
          </h2>

          <!-- Nút đóng (X) luôn hiện ở góc phải -->
          <button @click="closeAll" class="p-2 text-gray-600">
          
            <CloseOutlined class="text-lg" />
          </button>
        </div>

        <!-- Menu -->
        <div v-if="!currentMode" class="p-4 space-y-4">
          <button @click="openMode('activity')" class="block w-full text-left px-4 py-2 hover:bg-gray-100">
            <ProfileOutlined /> Hoạt động
          </button>
          <button @click="openMode('change-background')" class="block w-full text-left px-4 py-2 hover:bg-gray-100">
            <BgColorsOutlined /> Thay đổi hình nền
          </button>
          <!-- <button @click="openMode('anotherFeature')" class="block w-full text-left px-4 py-2 hover:bg-gray-100">
            <ProfileOutlined /> Chức năng khác
          </button> -->
        </div>
      </div>

      <!-- Sidebar Hoạt động -->
      <div v-if="currentMode && currentMode.name === 'activity'">
        <ActivitySidebar @close="closeMode" @closeAll="closeAll" />
      </div>
      <div v-if="currentMode && currentMode.name === 'change-background'">
        <ChangeBackgroundSidebar @close="closeMode" @closeAll="closeAll" />
      </div>

      <!-- Sidebar Chức năng khác -->
      <div v-if="currentMode && currentMode.name === 'anotherFeature'">
        <p>Content for Another Feature</p>
      </div>
    </a-drawer>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import ActivitySidebar from './ActivitySidebar.vue';
import { EllipsisOutlined, LeftOutlined, CloseOutlined, ProfileOutlined, BgColorsOutlined } from '@ant-design/icons-vue';
import ChangeBackgroundSidebar from './ChangeBackgroundSidebar.vue';

const menuOpen = ref(false);
const currentMode = ref(null); // Để lưu trữ chế độ hiện tại

// Danh sách các chế độ
const modes = [
  { name: 'activity', title: 'Hoạt động' },
  { name: 'change-background', title: 'Thay đổi hình nền' },
  { name: 'anotherFeature', title: 'Chức năng khác' }
];

const toggleMenu = () => {
  menuOpen.value = !menuOpen.value;
};

const openMode = (modeName) => {
  const mode = modes.find(m => m.name === modeName);
  if (mode) {
    currentMode.value = mode;
  }
};

const closeMode = () => {
  currentMode.value = null;
};

const closeAll = () => {
  menuOpen.value = false;
  currentMode.value = null;
};
</script>

<style scoped>
/* Header nhỏ gọn hơn */
a-drawer .ant-drawer-header {
  padding: 8px 16px;
}

button {
  cursor: pointer;
}

h2 {
  font-size: 1.25rem;
  font-weight: 600;
  text-align: center;
  flex-grow: 1;
}

.ant-drawer-body {
  padding: 16px;
}
</style>
