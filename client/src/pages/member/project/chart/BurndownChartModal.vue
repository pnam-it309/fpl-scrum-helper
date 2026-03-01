<!-- components/BurndownChartModal.vue -->
<template>
  <a-modal
  :visible="isOpen"
  @update:visible="handleUpdateVisible"
  @cancel="handleClose"
      title="Biểu đồ BurnDown"
    width="60%"
    :footer="null"
  >
    <BurnDownStoryPoint :isOpen="isOpen" />
  </a-modal>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import BurnDownStoryPoint from './BurnDownStoryPoint.vue';

const props = defineProps<{ isOpen: boolean }>()
const emit = defineEmits(['close'])

const handleClose = () => {
  emit('close')
}

const handleUpdateVisible = (val: boolean) => {
  if (!val) emit('close')
}

</script>
