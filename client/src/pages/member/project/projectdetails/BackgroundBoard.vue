<template>
  <div class="h-screen w-screen flex flex-col overflow-hidden" :style="layoutStyle">
    <!-- Header chứa nút chuyển đổi -->
    <div class="flex-shrink-0">
      <ProjectHeaderArea
        :isTableView="isTableView"
        :idProject="route.params.id"
        @toggleView="$emit('toggleView')"
      />
    </div>
    
    <!-- Nội dung chính, cho phép cuộn -->

      <slot :isTableView="isTableView" />
  
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'

import ProjectHeaderArea from '@/components/ui/Header/ProjectHeaderArea.vue'
import { getBackgroundByIdProject } from '@/services/api/member/projectdetail/image.api'
import { useRoute } from 'vue-router'
import { webSocketImage } from '@/services/service/member/image.socket'
const route = useRoute()
const props = defineProps<{ isTableView: boolean }>()
const backgroundImage = ref(
  "https://hanoispiritofplace.com/wp-content/uploads/2017/11/hinh-nen-bien-dep-49.jpg" 
)

const backgroundColor = ref('#4CAF50') 

const fetchBackground = async () => {
  try {

    const response = await getBackgroundByIdProject(route.params.id);
    backgroundImage.value = response.data.backgroundImage;
    backgroundColor.value = response.data.backgroundColor;

  } catch (error) {
    console.error('Lỗi khi lấy danh sách cột:', error)
  }
}
webSocketImage.getUpdateBackgroundProject(fetchBackground)
onMounted(fetchBackground)

// Tạo inline style cho background
const layoutStyle = computed(() => {
  if (backgroundImage.value) {
    // Nếu có ảnh nền
    return {
      backgroundImage: `url(${backgroundImage.value})`,
      backgroundSize: 'cover',
      backgroundPosition: 'center center',
      backgroundRepeat: 'no-repeat',
      backgroundAttachment: 'fixed',
      width: '100%',
      height: '100%'
    }
  } else {
    // Nếu không có ảnh nền, sử dụng màu nền
    return {
      backgroundColor: backgroundColor.value,
      width: '100%',
      height: '100%'
    }
  }
})
</script>

<style scoped>
/* Đảm bảo ảnh nền được cố định */
.bg-fixed {
  background-attachment: fixed;
}

.bg-cover {
  background-size: cover;
}

.bg-center {
  background-position: center center;
}
</style>
