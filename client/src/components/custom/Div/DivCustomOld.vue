<script setup lang="ts">
const props = defineProps({
  customClasses: String,
  customClassesLabel: String,
  label: String
})
</script>

<template>
  <div
    class="col-span-12 shadow p-4 rounded border border-stroke bg-white px-10 pt-10 pb-10 shadow-default dark:border-strokedark dark:bg-boxdark sm:px-7.5 xl:col-span-8"
    :class="props.customClasses"
  >
    <div class="flex items-center justify-between mb-3">
      <div class="flex items-center gap-2">
        <!-- 🖼️ Slot icon -->
        <span class="text-black text-xl">
          <slot name="icon" />
        </span>
        <!-- 🏷️ Label -->
        <h1
          v-if="props.label"
          class="text-xl font-bold text-graydark"
          :class="props.customClassesLabel"
        >
          {{ props.label }}
        </h1>
      </div>

      <!-- 👉 Slot extra content -->
      <div>
        <slot name="extra" />
      </div>
    </div>

    <!-- 📄 Main content -->
    <slot />
  </div>
</template>
