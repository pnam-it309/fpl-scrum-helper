<script setup lang="ts">
import { ref, watch } from 'vue'

const props = defineProps({
  customClasses: String,
  customClassesLabel: String,
  label: String,
  enableToggle: { 
    type: Boolean, 
    default: false 
  },
  isOpened: { 
    type: Boolean,
    default: true,
  }
})

const isOpenedLocal = ref(props.isOpened)

watch(() => props.isOpened, (newVal) => {
  isOpenedLocal.value = newVal
})

const toggleContent = () => {
  if (props.enableToggle) {
    isOpenedLocal.value = !isOpenedLocal.value
  }
}
</script>

<template>
  <div
    class="col-span-12 shadow rounded-md border border-stroke bg-white shadow-default dark:border-strokedark dark:bg-boxdark xl:col-span-8"
    :class="props.customClasses"
  >
    <!-- 🔹 Thanh tiêu đề (bo góc tùy theo mở/đóng) -->
    <div
      v-if="props.label"
      @click="toggleContent"
      class="text-textSideBarIsNoSelected dark:bg-strokedark dark:text-white px-5 py-3 text-base font-semibold border-b border-stroke dark:border-strokedark flex justify-between items-center cursor-pointer select-none transition-colors"
      :class="[
        props.customClassesLabel,
        props.enableToggle ? 'hover:bg-selectedHeader/80' : '',
        // ✅ Nếu đóng -> bo cả 4 góc, nếu mở -> chỉ bo trên
        isOpenedLocal ? 'bg-selectedHeader rounded-t-md' : 'bg-selectedHeader rounded-md'
      ]"
    >
      <!-- Tiêu đề + icon slot -->
      <div class="flex items-center gap-2 leading-none">
        <div class="text-lg flex items-center">
          <slot name="icon" />
        </div>
        <span class="text-base font-semibold">
          {{ props.label }}
        </span>
      </div>

      <!-- Icon toggle nếu enableToggle -->
      <svg
        v-if="props.enableToggle"
        xmlns="http://www.w3.org/2000/svg"
        fill="none"
        viewBox="0 0 24 24"
        stroke="currentColor"
        class="w-5 h-5 transition-transform duration-300"
        :class="{ 'rotate-180': isOpenedLocal }"
      >
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
      </svg>
    </div>

    <!-- 🔳 Nội dung -->
    <transition name="fade-slide">
      <div 
        v-if="!props.enableToggle || isOpenedLocal" 
        class="px-10 pt-6 pb-10 sm:px-7.5 rounded-b-md"
      >
        <div class="flex justify-end mb-3">
          <slot name="extra" />
        </div>

        <slot />
      </div>
    </transition>
  </div>
</template>

<style scoped>
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s ease;
}
.fade-slide-enter-from,
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-5px);
}
</style>
