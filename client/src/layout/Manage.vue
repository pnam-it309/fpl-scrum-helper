<script setup lang="ts">
import HeaderArea from '@/components/ui/Header/HeaderArea.vue'
import ManageSidebar from '@/components/custom/Sidebar/ManageSidebar.vue'
import ProjectSidebar from '@/components/custom/Sidebar/ProjectSidebar.vue'
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const isProjectDetail = computed(() => {
  return route.params.id !== undefined
})

const isProjectRoute = computed(() => {
  const projectRoutes = ['PROJECT_DETAIL', 'PROJECT_BACKLOG', 'PROJECT_BOARD', 'PROJECT_REPORT']
  return projectRoutes.includes(route.name as string)
})

const showProjectSidebar = computed(() => {
  return isProjectDetail.value
})
</script>

<template>
  <!-- ===== Page Wrapper Start ===== -->
  <div class="flex h-screen overflow-hidden">
    <!-- ===== Sidebar Start ===== -->
    <component :is="showProjectSidebar ? ProjectSidebar : ManageSidebar" />
    <!-- ===== Sidebar End ===== -->

    <!-- ===== Content Area Start ===== -->
    <div class="relative flex flex-1 flex-col overflow-y-auto overflow-x-hidden">
      <!-- ===== Header Start ===== -->
      <HeaderArea />
      <!-- ===== Header End ===== -->

      <!-- ===== Main Content Start ===== -->
      <!-- <main>
        <div class="p-4 md:p-6 2xl:p-10"> -->
      <router-view></router-view>
      <!-- </div>
      </main> -->
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
