<template>
  <div class="flex flex-col sm:flex-row gap-4">
    <InputCustom class="w-full sm:w-8/12" placeholder="Nhập tên giai đoạn" v-model="nameSprint" />
    <InputCustom type="date" class="w-full sm:w-4/12" placeholder="Thời gian" v-model="time" />
  </div>
</template>

<script setup lang="ts">
import InputCustom from '@/components/custom/Input/InputCustom.vue'
import { ref, watch } from 'vue'
const emit = defineEmits(['searchName', 'time'])
const nameSprint = ref('')
const time = ref('')
watch(nameSprint, (newVal) => {
  emit('searchName', newVal.trim())
})
watch(time, (newVal) => {
  emit('time', newVal)
})
</script>
