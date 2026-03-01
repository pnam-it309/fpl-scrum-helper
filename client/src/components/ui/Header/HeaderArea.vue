<script setup lang="ts">
import { useSidebarStore } from '@/stores/sidebar'
import { computed, defineProps, onMounted, ref } from 'vue'
import DropdownUser from './DropdownUser.vue'
import Notification from './Notification.vue'
import ActivityLogModal from './ActivityLogModal.vue'
import { HistoryOutlined, MenuOutlined } from '@ant-design/icons-vue'
import { localStorageAction } from '@/utils/storage'
import { USER_INFO_STORAGE_KEY } from '@/constants/storagekey'

const { toggleSidebar } = useSidebarStore()
const sidebarStore = useSidebarStore()

const props = defineProps({
  isNoSidebarPage: Boolean
})

const headerStyle = computed(() => {
  return props.isNoSidebarPage
    ? {
        backgroundImage:
          "url('https://tse1.mm.bing.net/th?id=OIP.vz3cycLecmyDwRm64tRxUgHaHa&pid=Api&P=0&h=180')"
      }
    : { backgroundColor: 'white' }
})

// Trạng thái hiển thị modal
const modalVisible = ref(false)
const showModal = () => {
  modalVisible.value = true
}
const closeModal = () => {
  modalVisible.value = false
}

const userLogin = localStorageAction.get(USER_INFO_STORAGE_KEY)
const isAdmin = ref(false)
onMounted(() => {
  isAdmin.value = userLogin.roleScreen === 'ADMIN'
})

import { useHeaderStore } from '@/stores/header'
const headerStore = useHeaderStore()
const toggleHeader = () => {
  headerStore.toggleHeader()
}
</script>

<template>
  <transition name="header-fade">
    <header
      v-show="headerStore.isHeaderOpen"
      :class="[
        // Header cao cố định, viền đen dưới, nền trắng
        'sticky top-0 z-20 flex w-full h-16 items-center bg-white drop-shadow-sm border-b border-black/10',
        props.isNoSidebarPage ? 'bg-[#00112f]' : 'bg-white dark:bg-boxdark dark:drop-shadow-none'
      ]"
      :style="headerStyle"
    >
      <div class="flex flex-grow items-center justify-between h-full px-4 md:px-4 2xl:px-8">
        <!-- Hamburger Toggle BTN -->
        <div class="flex items-center gap-2 sm:gap-4 lg:hidden">
          <button
            @click="toggleSidebar"
            class="z-99999 block rounded bg-white p-2 shadow-sm dark:bg-boxdark lg:hidden h-12 w-12 flex items-center justify-center text-xl"
          >
            <MenuOutlined />
          </button>
        </div>
        <div class="hidden sm:block"></div>

        <!-- Các action, avatar ... đều cùng line, căn giữa dọc -->
        <div class="flex items-center gap-2 2xsm:gap-4 h-full">
          <ul class="flex items-center gap-1 2xsm:gap-3"></ul>

          <!-- Lịch sử (admin) -->
          <a-button
            type="text"
            v-if="isAdmin"
            @click="showModal"
            href="javascript:void(0)"
            class="flex items-center justify-center notification-button"
          >
            <span class="text-[23px] leading-none"> 🗂️</span>
          </a-button>

          <Notification class="h-12 flex items-center" />

          <!-- User Area -->
          <DropdownUser :isNoSidebarPage="props.isNoSidebarPage" class="h-12 flex items-center" />
        </div>
      </div>
    </header>
  </transition>

  <ActivityLogModal :visible="modalVisible" @update:visible="modalVisible = $event" />
</template>

<style scoped>
.notification-button {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.3s;
}

/* Nếu muốn viền đậm hơn: tăng border-black/10 lên /20 hoặc để border-black hẳn */
/* Nếu muốn bóng rõ hơn, đổi drop-shadow-sm thành drop-shadow, hoặc thêm shadow-lg */
</style>
