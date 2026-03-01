<template>
  <canvas ref="canvas"></canvas>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { Chart } from 'chart.js'

const props = defineProps({
  type: { type: String, required: true },
  chartData: { type: Object, required: true },
  chartOptions: { type: Object, default: () => ({}) }
})

const canvas = ref(null)
let chartInstance = null

const renderChart = () => {
  if (chartInstance) chartInstance.destroy()
  chartInstance = new Chart(canvas.value, {
    type: props.type,
    data: props.chartData,
    options: props.chartOptions
  })
}

onMounted(renderChart)

watch(() => props.chartData, renderChart, { deep: true })
watch(() => props.type, renderChart)
</script>
