<template>
  <Bar :key="chartKey" :data="dataStatisticsTodo" :options="barOptions" class="w-1/2" />
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, watch } from 'vue'
import { Bar } from 'vue-chartjs'
import { useRoute, useRouter } from 'vue-router'
import { getAllTodo, getAllTodoStatistics } from '@/services/api/manage/todo/todo.api'
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
} from 'chart.js'

const emit = defineEmits(['data-phase'])

// Đăng ký các thành phần cần thiết cho Chart.js
ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend)

const props = defineProps(['idPhase'])

const short = ref(0)

const medium = ref(0)

const important = ref(0)
const high = ref(0)
const barOptions = {
  responsive: true,
  // maintainAspectRatio: false,
  animation: {
    duration: 500
  },
  plugins: {
    legend: {
      display: false // Ẩn legend
    },
    title: {
      display: true,
      text: 'Thống kê mức độ công việc',
      font: {
        size: 14
      }
    }
  },
  scales: {
    y: {
      type: 'linear' // Đảm bảo rằng trục y sử dụng scale 'linear'
    }
  }
}

const chartKey = ref(0)

const dataStatisticsTodo = ref({
  labels: ['Mức độ thấp', 'Mức độ trung bình', 'Mức độ cao', 'Mức độ quan trọng'],
  datasets: [
    {
      label: ['Thống kê báo cáo'],
      data: [short.value, medium.value, high.value, important.value],
      backgroundColor: [
        'rgb(54, 162, 235)',
        'rgb(255, 99, 132)',
        'rgb(255, 200, 132)',
        'rgb(255,100,100)'
      ],
      hoverOffset: 4
    }
  ]
})

const route = useRoute()
const idProject = ref('')

const paramStatisticsTodo = reactive({
  idProject: '',
  idPhase: null
})

// Hàm gọi API và cập nhật dữ liệu
const fetchDataTodo = async () => {
  high.value = 0
  important.value = 0
  medium.value = 0
  short.value = 0
  try {
    const dataTodo = await getAllTodoStatistics(paramStatisticsTodo)

    for (const item of dataTodo.data) {
      console.log(item.level)
      switch (item.level) {
        case 'QUAN_TRONG':
          important.value++
          break
        case 'CAO':
          high.value++
          break
        case 'TRUNG_BINH':
          medium.value++
          break
        case 'THAP':
          short.value++
      }
    }

    console.log(dataTodo.data)

    dataStatisticsTodo.value.datasets[0].data = [
      short.value,
      medium.value,
      high.value,
      important.value
    ]

    chartKey.value++
  } catch (error) {
    console.error('Lỗi khi lấy dữ liệu todo:', error)
  }
}

watch(
  () => props.idPhase,
  (newvalue) => {
    paramStatisticsTodo.idPhase = newvalue
    fetchDataTodo()
  }
)

onMounted(() => {
  paramStatisticsTodo.idProject = route.params.id as string
  console.log(paramStatisticsTodo.idProject)
  fetchDataTodo()
  console.log('Hello')
})
</script>

<style scoped></style>
