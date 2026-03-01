<template>
  <Bar :key="chartKey" :data="dataStatisticsTodo" :options="barOptions" class="w-1/2 h-[400px]" />
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, watch } from 'vue'
import { Bar } from 'vue-chartjs'
import { useRoute } from 'vue-router'
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
} from 'chart.js'
import { getAllTodoStatisticsByStaffProject } from '@/services/api/manage/todo/todo.api'

// Đăng ký các thành phần của Chart.js
ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend)

// Props
const props = defineProps<{ idPhase: string | null }>()

// Biểu đồ options
const barOptions = {
  responsive: true,
  // maintainAspectRatio: false,
  animation: {
    duration: 500
  },
  plugins: {
    legend: {
      display: false
    },
    title: {
      display: true,
      text: 'Thống kê công việc của thành viên',
      font: {
        size: 14
      }
    }
  },
  scales: {
    y: {
      type: 'linear'
    }
  }
}

const chartKey = ref(0)

// Dữ liệu biểu đồ
const dataStatisticsTodo = ref({
  labels: [] as string[],
  datasets: [
    {
      label: ['Công việc của thành viên'],
      data: [] as number[],
      backgroundColor: ['rgb(54, 162, 235)', 'rgb(255, 99, 132)', 'rgb(255, 200, 132)'],
      hoverOffset: 4
    }
  ]
})

function generateRandomColors(count: number): string[] {
  const colors = [] as any
  for (let i = 0; i < count; i++) {
    const r = Math.floor(Math.random() * 256)
    const g = Math.floor(Math.random() * 256)
    const b = Math.floor(Math.random() * 256)
    colors.push(`rgb(${r}, ${g}, ${b})`)
  }
  return colors
}

// Lấy ID project từ route
const route = useRoute()

// Tham số gọi API
const paramStatisticsTodo = reactive({
  idProject: '',
  idPhase: null as string | null
})

// Hàm lấy dữ liệu
const fetchDataTodo = async () => {
  try {
    const dataTodo = await getAllTodoStatisticsByStaffProject(paramStatisticsTodo)
    const todoList = dataTodo.data || []

    // Gán dữ liệu vào biểu đồ
    dataStatisticsTodo.value.labels = todoList.map((item: any) =>
      item.nameStudent == null ? item.nameStaff : item.nameStudent
    )
    dataStatisticsTodo.value.datasets[0].data = todoList.map((item: any) => item.countTodo)
    dataStatisticsTodo.value.datasets[0].backgroundColor = generateRandomColors(todoList.length)

    // Cập nhật biểu đồ
    chartKey.value++
  } catch (error) {
    console.error('Lỗi khi lấy dữ liệu todo:', error)
  }
}

// Watch khi props thay đổi
watch(
  () => props.idPhase,
  (newValue) => {
    paramStatisticsTodo.idPhase = newValue
    fetchDataTodo()
  },
  { immediate: true } // Gọi luôn khi mounted
)

onMounted(() => {
  paramStatisticsTodo.idProject = route.params.id as string
  fetchDataTodo()
})
</script>

<style scoped></style>
