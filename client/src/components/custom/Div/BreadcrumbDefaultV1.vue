<script setup lang="ts">
defineProps({
  routes: {
    type: Array,
    required: true,
  },
  label: {
    type: String,
    default: '',
  },
  marginTop: {
    type: String,
    default: ''
  },
  marginBottom: {
    type: String,
    default: ''
  },
  containerClass: {
    type: String,
    default: ''
  },
  barClass: {
    type: String,
    default: ''
  }
})
</script>

<template>
  <div :class="['min-h-screen w-full bg-gray-100', containerClass]">
    <div class="w-full px-4 md:px-8 max-w-[100vw] mx-auto">
      <!-- Breadcrumb bar -->
      <div
        class="flex flex-col md:flex-row justify-between md:items-center gap-3
               bg-white dark:bg-gray-800 rounded-md shadow-sm
               border border-gray-200 dark:border-gray-700
               px-4 py-3.5 transition"
        :class="[marginTop ? marginTop : 'mt-4', marginBottom ? marginBottom : '', barClass]"
      >
        <!-- Breadcrumb -->
        <nav aria-label="breadcrumb">
          <ol class="flex items-center flex-wrap text-sm">
            <li
              v-for="(item, index) in routes"
              :key="index"
              class="flex items-center"
              :class="index === routes.length - 1 ? 'text-gray-500 font-medium' : ''"
              aria-current="page"
            >
              <template v-if="index !== routes.length - 1">
                <RouterLink
                  :to="{ name: item.nameRoute }"
                  class="text-gray-500 hover:text-blue-600 hover:underline transition-colors"
                >
                  {{ item.name }}
                </RouterLink>
                <svg
                  class="w-4 h-4 mx-2 text-gray-400"
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
                </svg>
              </template>
              <template v-else>
                <span class="text-gray-700 dark:text-gray-200 font-semibold">
                  {{ item.name }}
                </span>
              </template>
            </li>
          </ol>
        </nav>
        <div
          v-if="label"
          class="text-xs md:text-sm text-gray-500 dark:text-gray-300 font-medium text-center md:text-right"
        >
          {{ label }}
        </div>
      </div>

      <!-- Main content slot -->
      <div class="mt-4">
        <slot />
      </div>
    </div>
  </div>
</template>
