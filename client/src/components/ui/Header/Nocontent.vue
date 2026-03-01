<template>
  <DivCustom class="w-full h-full flex items-center justify-center">
    <a-empty :image="simpleImage">
      <template #description>
        {{ noDataText }}
      </template>
    </a-empty>
  </DivCustom>
</template>

<script lang="ts" setup>
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import { Empty } from 'ant-design-vue'

const simpleImage = Empty.PRESENTED_IMAGE_SIMPLE
const noDataText = 'Chưa có giai đoạn nào diễn ra'
</script>
