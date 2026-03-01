<template>
  <div class="border rounded-xl p-4 flex items-start space-x-3 shadow hover:shadow-md transition">
    <component :is="iconMap[icon]" :class="['text-xl mt-1', iconColor]" />
    <div>
      <div class="font-semibold">{{ title }}</div>
      <div class="text-sm text-gray-500">{{ subtitle }}</div>
    </div>
  </div>
</template>

<script setup>
import {
  CheckCircleOutlined,
  EditOutlined,
  FileAddOutlined,
  CalendarOutlined,
  BlockOutlined
} from '@ant-design/icons-vue'

const props = defineProps({
  icon: String,
  title: String,
  subtitle: String,
  iconColor: {
    type: String,
    default: 'text-gray-600' // 👈 màu mặc định
  }
})

const iconMap = {
  CheckCircleOutlined,
  EditOutlined,
  FileAddOutlined,
  CalendarOutlined,
  BlockOutlined
}
</script>
