<template>
  <div class="h-75 w-80 flex items-center justify-center">
    <Doughnut :data="chartData" :options="pieOptions" :key="chartKey" />
  </div>
</template>
<!-- <template>
  <div class="flex items-center justify-center h-64 w-full">
    <div class="w-80 h-80">
      <Doughnut :data="chartData" :options="pieOptions" :key="chartKey" />
    </div>
  </div>
</template> -->

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { Doughnut, Pie } from 'vue-chartjs'
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
  ArcElement
} from 'chart.js'
import { phaseResponse } from '@/services/api/manage/phase/phase.api'
// Đăng ký các thành phần cần thiết cho Chart.js
ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend, ArcElement)
const phaseDone = ref(0)
const phaseContinue = ref(0)
const phaseNoStart = ref(0)
const props = defineProps(['dataPhase'])
const chartData = ref({
  labels: ['Chưa diễn ra', 'Đang diễn ra', 'Đã hoàn thành'],
  datasets: [
    {
      label: 'Thống kê giai đoạn',
      data: [phaseNoStart.value, phaseContinue.value, phaseDone.value],
      backgroundColor: ['rgb(54, 162, 235)', 'rgb(255, 99, 132)', 'rgb(25, 200, 100'],
      hoverOffset: 4
    }
  ]
})
const chartKey = ref(0)

const fetchDataPhase = (data) => {
  phaseNoStart.value = 0
  phaseContinue.value = 0
  phaseDone.value = 0

  for (const item of props.dataPhase) {
    switch (item.statusPhase) {
      case 'CHUA_HOAN_THANH':
        phaseNoStart.value++
        break
      case 'DANG_LAM':
        phaseContinue.value++
        break
      case 'DA_HOAN_THANH':
        phaseDone.value++
    }
  }

  chartData.value.datasets[0].data = [phaseNoStart.value, phaseContinue.value, phaseDone.value]

  chartKey.value++
}

const dataPhase = ref<phaseResponse[]>()
watch(
  () => props.dataPhase,
  (newvalue) => {
    dataPhase.value = newvalue
    fetchDataPhase(newvalue)
  }
)

const pieOptions = {
  responsive: true,
  // maintainAspectRatio: false,
  animation: {
    duration: 300
  },
  plugins: {
    legend: {
      position: 'bottom' as const,
      labels: {
        boxWidth: 12,
        padding: 15,
        font: {
          size: 12
        }
      }
    },
    title: {
      display: true,
      text: 'Thống kê giai đoạn',
      font: {
        size: 14
      }
    }
  }
}
</script>

<style scoped></style>
