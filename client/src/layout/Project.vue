<script setup lang="ts">
import { computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import BackgroundBoard from '@/pages/member/project/projectdetails/BackgroundBoard.vue'
import HeaderArea from '@/components/ui/Header/HeaderArea.vue'

const router = useRouter()
const route = useRoute()
watch(
  () => route.path,
  (newPath) => {
    console.log('Route path changed:', newPath)
  }
)

// Xác định trạng thái hiển thị dựa trên route
const isTableView = computed(() => route.path.includes('/table'))

const toggleView = () => {
  const projectId = route.params.id
  const phaseId = route.params.idPhase
  if (isTableView.value) {
    router.push(`/project/project-detail/${projectId}/${phaseId}`)
  } else {
    router.push(`/project/project-detail/${projectId}/${phaseId}/table`)
  }
  console.log('toggleView invoked')
  console.log(
    'toggleView invoked, projectId:',
    projectId,
    'new URL:',
    isTableView.value
      ? `/project/project-detail/${projectId}/${phaseId}`
      : `/project/project-detail/${projectId}/${phaseId}/table`
  )
}
</script>

<template>
  <div class="flex h-screen overflow-hidden">
    <div class="relative flex flex-1 flex-col overflow-y-auto overflow-x-hidden">
      <HeaderArea />
      <!-- Sử dụng BackgroundBoard truyền prop và event -->
      <!-- <BackgroundBoard :isTableView="isTableView" @toggleView="toggleView"> -->
      <!-- <main class="flex-1 h-full overflow-x-auto overflow-y-hidden"> -->
      <router-view />
      <!-- </main> -->
      <!-- </BackgroundBoard> -->
    </div>
  </div>
</template>
