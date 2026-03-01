<template>
  <div :class="['kpi-card', colorClass]">
    <div class="kpi-icon">
      <font-awesome-icon :icon="iconName" />
    </div>
    <div class="kpi-content">
      <div class="kpi-title">{{ title }}</div>
      <div class="kpi-value">{{ formattedValue }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, defineProps } from 'vue'
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import type { IconDefinition } from '@fortawesome/fontawesome-svg-core'
import {
  faProjectDiagram,
  faSpinner,
  faCheckCircle,
  faPauseCircle,
  faExclamationTriangle
} from '@fortawesome/free-solid-svg-icons'

const props = defineProps<{
  title: string
  value: number
  type?: 'total' | 'inProgress' | 'done' | 'paused' | 'overdue'
}>()

// Chọn icon theo type
const iconName = computed<IconDefinition>(() => {
  switch (props.type) {
    case 'inProgress':
      return faSpinner
    case 'done':
      return faCheckCircle
    case 'paused':
      return faPauseCircle
    case 'overdue':
      return faExclamationTriangle
    default:
      return faProjectDiagram
  }
})

// Chọn class màu theo type
const colorClass = computed(() => {
  switch (props.type) {
    case 'inProgress':
      return 'kpi-in-progress'
    case 'done':
      return 'kpi-done'
    case 'paused':
      return 'kpi-paused'
    case 'overdue':
      return 'kpi-overdue'
    default:
      return 'kpi-total'
  }
})

// Format số nếu số lớn (ví dụ 1200 thành 1.2K)
const formattedValue = computed(() => {
  const val = props.value
  if (val >= 1e6) return (val / 1e6).toFixed(1) + 'M'
  if (val >= 1e3) return (val / 1e3).toFixed(1) + 'K'
  return val.toString()
})
</script>

<style scoped>
.kpi-card {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  border-radius: 8px;
  color: white;
  box-shadow: 0 1px 5px rgb(0 0 0 / 0.1);
  min-width: 160px;
  gap: 16px;
}

.kpi-icon {
  font-size: 2.8rem;
}

.kpi-content {
  display: flex;
  flex-direction: column;
  justify-content: center;
  flex-grow: 1;
}

.kpi-title {
  font-size: 0.85rem;
  font-weight: 600;
  margin-bottom: 8px;
  opacity: 0.85;
  text-transform: uppercase;
}

.kpi-value {
  font-size: 2rem;
  font-weight: bold;
}

/* Màu sắc theo loại */
.kpi-total {
  background-color: #1890ff; /* xanh dương */
}

.kpi-in-progress {
  background-color: #faad14; /* vàng */
  color: #222;
}

.kpi-done {
  background-color: #52c41a; /* xanh lá */
}

.kpi-paused {
  background-color: #8c8c8c; /* xám */
}

.kpi-overdue {
  background-color: #f5222d; /* đỏ */
}
</style>
