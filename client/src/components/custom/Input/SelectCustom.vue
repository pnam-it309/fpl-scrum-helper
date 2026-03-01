<script lang="ts" setup>
const props = defineProps<{
  label?: string
  placeholder?: string
  customClasses?: string
  customClassesInput: string
  required?: {
    type: Boolean
    default: false
  }
  modelValue: string[]
  dataOptions: Array<Object>
}>()

const emit = defineEmits<{
  (e: 'changeSelect', data: []): void
}>()

const handleChange = (value: []) => {
  emit('changeSelect', value)
}
</script>

<template>
  <div :class="customClasses">
    <label class="block text-black dark:text-white">
      {{ label }}
      <span v-if="required" class="text-meta-1">*</span>
    </label>
    <a-select
      :value="modelValue"
      mode="multiple"
      style="width: 100%"
      :placeholder="placeholder"
      :options="dataOptions"
      :class="[
        'w-full rounded-md border-[1.5px] text-black border-stroke bg-transparent py-3 px-5 font-normal outline-none transition hover:border-stroke focus:border-primary active:border-primary disabled:cursor-default disabled:bg-whiter dark:text-white dark:border-form-strokedark dark:bg-form-input dark:focus:border-primary',
        customClassesInput
      ]"
      @change="handleChange"
    ></a-select>
  </div>
</template>
