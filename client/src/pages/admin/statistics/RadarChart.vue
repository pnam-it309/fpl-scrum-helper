<template>
  <BaseChart type="radar" :chartData="chartData" :chartOptions="chartOptions" />
</template>

<script setup>
import BaseChart from './BaseChart.vue'

const props = defineProps({
  chartData: Object,
  chartOptions: Object
})
</script>
