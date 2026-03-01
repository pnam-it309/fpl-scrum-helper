<template>
  <div class="custom-form-item mb-4">
    <label class="block text-gray-700 text-sm font-semibold mb-1">
      <span v-if="props.required" class="text-red-500">*</span>
      {{ props.label }}
    </label>
    <a-form-item :name="props.name" :rules="props.rules">
      <slot></slot>
    </a-form-item>
  </div>
</template>

<script setup lang="ts">
import { defineProps } from 'vue'

const props = defineProps<{
  label: string
  name: string
  rules?: object[]
  required?: boolean
}>()
</script>
