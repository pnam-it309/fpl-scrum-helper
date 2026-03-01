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

// Đăng ký các thành phần cần thiết cho Chart.js
ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend)

const props = defineProps(['idPhase'])
const dataTodoDone = ref(0)

const dataTodoIncomplete = ref(0)

const dataTodoOverdue = ref(0)
const barOptions = {
  responsive: true,
  maintainAspectRatio: false,
  animation: {
    duration: 500
  },
  plugins: {
    legend: {
      display: false // Ẩn legend
    },
    // legend: {
    //   position: 'bottom' as const,
    //   labels: {
    //     boxWidth: 12,
    //     padding: 15,
    //     font: {
    //       size: 12
    //     }
    //   }
    // },
    title: {
      display: true,
      text: 'Thống kê công việc',
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
  labels: ['Chưa hoàn thành', 'Đã hoàn thành', 'Quá hạn'],
  datasets: [
    {
      label: ['Thống kê báo cáo'],
      data: [dataTodoIncomplete.value, dataTodoDone.value, dataTodoOverdue.value],
      backgroundColor: ['rgb(54, 162, 235)', 'rgb(255, 99, 132)', 'rgb(255, 200, 132)'],
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
  console.log(idProject.value)
  dataTodoIncomplete.value = 0
  dataTodoDone.value = 0
  dataTodoOverdue.value = 0
  try {
    const dataTodo = await getAllTodoStatistics(paramStatisticsTodo)

    for (const item of dataTodo.data) {
      console.log(item.status)

      switch (item.status) {
        case 'CHUA_HOAN_THANH':
          dataTodoIncomplete.value++
          break
        case 'HOAN_THANH_SOM':
        case 'DA_HOAN_THANH':
          dataTodoDone.value++
          break
        case 'QUA_HAN':
          dataTodoOverdue.value++
      }
    }

    dataStatisticsTodo.value.datasets[0].data = [
      dataTodoIncomplete.value,
      dataTodoDone.value,
      dataTodoOverdue.value
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
})
</script>

<style scoped></style>
