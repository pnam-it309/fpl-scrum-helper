<script setup lang="ts">
const props = defineProps(['label', 'customClasses'])
</script>

<template>
  <button
    class="inline-flex items-center justify-center rounded-md gap-2.5 py-3 px-10 text-center font-medium hover:bg-opacity-90 lg:px-8 xl:px-10"
    :class="props.customClasses"
  >
    <slot></slot>
    {{ props.label }}
  </button>
</template>
