<script setup lang="ts">
defineProps({
  customClasses: { type: String, default: '' }, // class thêm từ ngoài
  borderColor: { type: String, default: 'border-gray-200 dark:border-gray-700' }, // màu viền
  rounded: { type: String, default: 'rounded-md' }, // bo góc
  padding: { type: String, default: 'p-4' }, // padding mặc định
  shadow: { type: Boolean, default: true } // có shadow không
})
</script>

<template>
  <div
    :class="[
      'bg-white dark:bg-gray-800 transition-colors duration-200',
      borderColor,
      rounded,
      padding,
      shadow ? 'shadow-sm' : '',
      'border',             // viền mảnh
      'mb-4',               // khoảng cách dưới
      customClasses
    ]"
  >
    <slot />
  </div>
</template>
