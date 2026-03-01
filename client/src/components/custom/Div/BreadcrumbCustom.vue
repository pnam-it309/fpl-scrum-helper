<template>
  <div class="page-wrapper flex flex-col min-h-screen">
    <div
      class="content-wrapper bg-white dark:bg-boxdark p-6 shadow-lg border border-gray-200 dark:border-strokedark"
      :class="customClasses"
    >
      <div v-if="label" class="ml-2 text-base font-semibold mb-4 text-gray-800 dark:text-white">
        <router-link :to="link">
          {{ pageTitle }}
        </router-link>
      </div>
      <slot />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const props = defineProps({
  label: String,
  customClasses: String,
  link: String
})

const route = useRoute()

const pageTitle = computed(() => {
  return props.label 
})
</script>

<style scoped>
.page-wrapper {
  @apply flex flex-col min-h-screen;
}

.content-wrapper {
  @apply flex-grow;
}
</style>