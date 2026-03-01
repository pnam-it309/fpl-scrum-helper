<script setup lang="ts">
defineProps({
  routes: {
    type: Array,
    required: true
  },
  label: {
    type: String,
    default: ''
  },
  marginTop: {
    type: String,
    default: '' // khoảng cách trên
  },
  marginBottom: {
    type: String,
    default: '' // khoảng cách dưới
  }
})
</script>

<template>
  <!-- ✅ Breadcrumb chỉ hiển thị bar, không bọc nội dung khác -->
  <div
    class="flex flex-col md:flex-row justify-between md:items-center gap-3
           bg-white dark:bg-gray-800 rounded-md shadow-sm
           border border-gray-200 dark:border-gray-700
           px-4 py-3.5 transition"
    :class="[
      marginTop ? marginTop : 'mt-2',
      marginBottom ? marginBottom : 'mb-4'
    ]"
  >
    <!-- Breadcrumb list -->
    <nav aria-label="breadcrumb">
      <ol class="flex items-center flex-wrap text-sm">
        <li
          v-for="(item, index) in routes"
          :key="index"
          class="flex items-center"
          :class="index === routes.length - 1 ? 'text-gray-500 font-medium' : ''"
          aria-current="page"
        >
          <!-- Nếu chưa phải phần cuối thì là link -->
          <template v-if="index !== routes.length - 1">
            <RouterLink
              :to="{ name: item.nameRoute }"
              class="text-gray-500 hover:text-blue-600 hover:underline transition-colors"
            >
              {{ item.name }}
            </RouterLink>

            <!-- icon phân cách -->
            <svg
              class="w-4 h-4 mx-2 text-gray-400"
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M9 5l7 7-7 7"
              />
            </svg>
          </template>

          <!-- Nếu là phần cuối -->
          <template v-else>
            <span class="text-gray-700 dark:text-gray-200 font-semibold">
              {{ item.name }}
            </span>
          </template>
        </li>
      </ol>
    </nav>

    <!-- Label bên phải -->
    <div
      v-if="label"
      class="text-xs md:text-sm text-gray-500 dark:text-gray-300 font-medium text-center md:text-right"
    >
      {{ label }}
    </div>
  </div>
</template>
