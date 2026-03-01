<script setup lang="ts">
import HeaderArea from '@/components/ui/Header/HeaderArea.vue'
import MemberSidebar from '@/components/custom/Sidebar/MemberSidebar.vue';
import TodoSidebar from '@/components/custom/Sidebar/TodoSidebar.vue';
import { useRoute } from 'vue-router';
import { computed } from 'vue';
import { webSocketNotificationMember } from '../services/service/member/notification-member.socket';
import { toast } from 'vue3-toastify';

const route = useRoute()

const isProjectDetail = computed(() => {
  return route.params.id !== undefined
})

// Kiểm tra route hiện tại có phải là các route liên quan đến project không
const isProjectRoute = computed(() => {
  const projectRoutes = ['MYPROJECT_REPORT', 'MYPROJECT_SPRINT']
  return projectRoutes.includes(route.name as string)
})

// Quyết định hiển thị sidebar nào
const showTodoSidebar = computed(() => {
  return isProjectDetail.value
})

webSocketNotificationMember.getCreateNotificationMember((data) => {
  if(data == true){
    toast.info('Bạn có thông báo mới!');
  }
});

</script>

<template>
  <!-- ===== Page Wrapper Start ===== -->
  <div class="flex h-screen overflow-hidden">
    <!-- ===== Sidebar Start ===== -->
    <component :is="showTodoSidebar ? TodoSidebar : MemberSidebar" />
    <!-- ===== Sidebar End ===== -->

    <!-- ===== Content Area Start ===== -->
    <div class="relative flex flex-1 flex-col overflow-y-auto overflow-x-hidden">
      <!-- ===== Header Start ===== -->
      <HeaderArea />
      <!-- ===== Header End ===== -->

      <!-- ===== Main Content Start ===== -->
      <!-- <main> -->
        <!-- <div class="p-4 md:p-6 2xl:p-10"> -->
          <router-view></router-view>
        <!-- </div> -->
      <!-- </main> -->
      <!-- ===== Main Content End ===== -->
    </div>
  </div>
  <!-- ===== Page Wrapper End ===== -->
</template>
<style scoped>
/* Add transition for smooth sidebar switching */
/* .flex {
  transition: all 0.3s ease;
} */
</style>