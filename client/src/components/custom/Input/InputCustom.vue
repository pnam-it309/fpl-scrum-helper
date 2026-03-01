<script lang="ts">
import { defineComponent } from 'vue'
import { Input, DatePicker } from 'ant-design-vue'

export default defineComponent({
  name: 'InputCustom',
  components: {
    'a-input': Input,
    'a-date-picker': DatePicker
  },
  props: {
    label: String,
    type: {
      type: String,
      default: 'text'
    },
    placeholder: String,
    customClasses: {
      type: String,
      default: ''
    },
    customClassesInput: {
      type: String,
      default: ''
    },
    required: {
      type: Boolean,
      default: false
    },
    modelValue: [String, Date]
  },
  emits: ['update:modelValue'],
  methods: {
    handleInput(e: Event) {
      const target = e.target as HTMLInputElement
      this.$emit('update:modelValue', target.value)
    },
    handleDateChange(value: any) {
      this.$emit('update:modelValue', value)
    }
  }
})
</script>

<template>
  <div :class="['mb-4', customClasses]">
    <label class="block mb-1 text-sm font-medium text-gray-700 dark:text-white">
      {{ label }}
      <span v-if="required" class="text-red-500 ml-1">*</span>
    </label>

    <component
      :is="type === 'date' ? 'a-date-picker' : 'a-input'"
      v-bind="type === 'date'
        ? { value: modelValue, placeholder, size: 'large', onChange: handleDateChange }
        : { value: modelValue, type, placeholder, size: 'large', onInput: handleInput }"
      :class="[
        'w-full',
        customClassesInput
      ]"
      allow-clear
    />
  </div>
</template>

<style scoped>
/* Tuỳ chọn style thêm nếu cần */
</style>
