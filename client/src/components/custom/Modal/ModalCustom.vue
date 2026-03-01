<script setup lang="ts">
import { defineProps, defineEmits } from 'vue'
import ButtonDefault from '@/components/custom/Button/ButtonDefault.vue'

const props = defineProps<{ open: boolean; title: string }>()
const emit = defineEmits(['close', 'submit'])

const handleCancel = () => {
  emit('close')
}

const handleOk = () => {
  emit('submit')
}
</script>

<template>
  <a-modal :open="props.open" :title="props.title" @ok="handleOk" footer=""  >
    <slot></slot>
    <div class="flex justify-end gap-1 mt-3">
      <a-button type="" label="Huỷ" customClasses="bg-black text-white !px-3 !py-2" @click="handleCancel">
        Huỷ
      </a-button>
      <a-button type="" label="Xác nhận" customClasses="bg-black text-white !px-3 !py-2" @click="handleOk">
        Xác nhận
      </a-button>
    </div>
  </a-modal>
</template>

<style scoped>
.ant-btn {
  background-image: var(--selected-header) !important;
  color: var(--selected-text) !important;
  border: none !important;
}

.ant-btn:hover,
.ant-btn:focus {
  background-image: var(--selected-header-hover) !important;
  color: var(--selected-text) !important;
  border: none !important;
}
</style>
