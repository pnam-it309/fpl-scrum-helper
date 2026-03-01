<template>
  <div class="charts-container">
    <div class="chart-card">
      <h3>Tổng dự án theo cơ sở</h3>
      <BarChart :chartData="projectByFacilityData" :chartOptions="barOptions" />
    </div>

    <div class="chart-card" style="width: 400px; height: 400px">
      <h3>Phân bố dự án theo bộ môn (Doughnut)</h3>
      <DoughnutChart :chartData="projectByDepartmentData" :chartOptions="pieOptions" />
    </div>

    <div class="chart-card">
      <h3>Tổng số công việc theo dự án</h3>
      <BarChart :chartData="workByFacilityData" :chartOptions="barOptions" />
    </div>

    <div class="chart-card">
      <h3>Số thành viên trong dự án</h3>
      <HorizontalBarChart :chartData="memberCountData" :chartOptions="barOptionsHorizontal" />
    </div>
  </div>
</template>

<script setup>
import BarChart from './BarChart.vue'
import PieChart from './PieChart.vue'
import DoughnutChart from './DoughnutChart.vue'
import HorizontalBarChart from './HorizontalBarChart.vue'
import LineChart from './LineChart.vue'
import RadarChart from './RadarChart.vue'
import {
  Chart,
  LinearScale,
  CategoryScale,
  BarController,
  BarElement,
  PointElement,
  LineController,
  LineElement,
  ArcElement,
  RadarController,
  RadialLinearScale,
  Tooltip,
  PieController,
  Legend
} from 'chart.js'
import axios from 'axios'
import {
  getProjectByFacility,
  getProjectByDepartment,
  getTotalProjects,
  getProjectTodoCounts,
  getProjectParticipantsStatistics,
  getProjectStatistics,
  getProjectByTodoStatus
} from '@/services/api/admin/project/project.api'
import { ref, onMounted } from 'vue'

const projectByFacilityData = ref({
  labels: [],
  datasets: []
})

const projectByDepartmentData = ref({
  labels: [],
  datasets: []
})

const workByFacilityData = ref({
  labels: [],
  datasets: []
})

const projectStatusData = ref({
  labels: [],
  datasets: []
})

const memberCountData = ref({
  labels: [],
  datasets: []
})

// Đăng ký các thành phần này
Chart.register(
  LinearScale,
  CategoryScale,
  BarController,
  BarElement,
  PointElement,
  LineController,
  LineElement,
  ArcElement,
  RadialLinearScale,
  Tooltip,
  RadarController,
  PieController,
  Legend
)

onMounted(async () => {
  try {
    const response = await getProjectByFacility()
    const data = response.data

    projectByFacilityData.value = {
      labels: data.map((item) => item.facilityName),
      datasets: [
        {
          label: 'Số lượng dự án',
          data: data.map((item) => item.total),
          backgroundColor: '#3b82f6'
        }
      ]
    }

    // Lấy dữ liệu dự án theo bộ môn
    const departmentResponse = await getProjectByDepartment()
    const departmentData = departmentResponse.data

    projectByDepartmentData.value = {
      labels: departmentData.map((item) => item.departmentName),
      datasets: [
        {
          label: 'Số lượng dự án',
          data: departmentData.map((item) => item.total),
          backgroundColor: ['#f87171', '#facc15', '#34d399', '#60a5fa', '#a78bfa', '#f472b6']
        }
      ]
    }

    // Lấy dữ liệu tổng công việc theo dự án
    const todoProjects = await getProjectByTodoStatus()
    const todoData = todoProjects.data

    // Lấy danh sách tên dự án duy nhất
    const projectNames = Array.from(new Set(todoData.map((item) => item.projectName)))

    // Nhóm công việc theo trạng thái (Chưa bắt đầu, Đang làm, Hoàn thành)
    const statusLabels = [0, 1, 2]
    const grouped = statusLabels.map((status) => {
      return {
        label: status === 0 ? 'Chưa bắt đầu' : status === 1 ? 'Đang làm' : 'Hoàn thành',
        backgroundColor: status === 0 ? '#f97316' : status === 1 ? '#3b82f6' : '#22c55e',
        data: projectNames.map((projectName) => {
          const item = todoData.find(
            (todo) => todo.projectName === projectName && todo.projectStatus === status
          )
          return item ? item.totalTodo : 0 // 👈 Sửa lại đúng key
        })
      }
    })

    // Cập nhật dữ liệu biểu đồ
    workByFacilityData.value = {
      labels: projectNames,
      datasets: grouped
    }

    // Lấy dữ liệu dự án theo trạng thái
    const statusProjects = await getProjectStatistics()
    const statusData = statusProjects.data

    const statusLabelMap = {
      0: 'Chưa diễn ra',
      1: 'Đang diễn ra',
      2: 'Đã kết thúc'
    }

    const colors = {
      0: '#fadb14',
      1: '#1890ff',
      2: '#52c41a'
    }

    const labels = []
    const chartData = []
    const backgroundColors = []

    Object.entries(statusData).forEach(([status, count]) => {
      labels.push(statusLabelMap[status])
      chartData.push(count)
      backgroundColors.push(colors[status])
    })

    projectStatusData.value = {
      labels,
      datasets: [
        {
          label: 'Số lượng dự án',
          backgroundColor: backgroundColors,
          data: chartData
        }
      ]
    }

    // Lấy dữ liệu số thành viên trong dự án
    const memberResponse = await getProjectParticipantsStatistics()
    const memberData = memberResponse.data

    const memberLabels = memberData.map((item) => item.projectName)
    const memberCounts = memberData.map((item) => item.totalParticipants)

    memberCountData.value = {
      labels: memberLabels,
      datasets: [
        {
          label: 'Số lượng thành viên',
          data: memberCounts,
          backgroundColor: '#a78bfa'
        }
      ]
    }
  } catch (error) {
    console.error('Lỗi khi lấy dữ liệu dự án theo cơ sở:', error)
  }
})

const projectProgressData = {
  labels: ['Tháng 1', 'Tháng 2', 'Tháng 3', 'Tháng 4', 'Tháng 5'],
  datasets: [
    {
      label: 'Tiến độ dự án (%)',
      borderColor: '#52c41a',
      backgroundColor: 'rgba(82,196,26,0.3)',
      fill: true,
      data: [10, 30, 55, 80, 95],
      tension: 0.2
    }
  ]
}

// Stacked bar: công việc theo trạng thái qua cơ sở
const stackedWorkStatusData = {
  labels: ['Cơ sở A', 'Cơ sở B', 'Cơ sở C'],
  datasets: [
    {
      label: 'Chưa bắt đầu',
      backgroundColor: '#ffa940',
      data: [5, 9, 6],
      stack: 'Stack 0'
    },
    {
      label: 'Đang làm',
      backgroundColor: '#1890ff',
      data: [10, 14, 11],
      stack: 'Stack 0'
    },
    {
      label: 'Hoàn thành',
      backgroundColor: '#52c41a',
      data: [12, 15, 9],
      stack: 'Stack 0'
    }
  ]
}

// Các options chuẩn
const barOptions = {
  responsive: true,
  plugins: { legend: { position: 'top' } },
  scales: { y: { beginAtZero: true } }
}

const barOptionsHorizontal = {
  ...barOptions,
  indexAxis: 'y'
}

const pieOptions = {
  responsive: true,
  plugins: { legend: { position: 'right' } }
}

const lineOptions = {
  responsive: true,
  plugins: { legend: { position: 'top' } },
  scales: { y: { min: 0, max: 100, ticks: { stepSize: 10 } } }
}

const stackedBarOptions = {
  responsive: true,
  plugins: { legend: { position: 'top' } },
  scales: {
    x: { stacked: true },
    y: { stacked: true, beginAtZero: true }
  }
}

const radarOptions = {
  responsive: true,
  scales: {
    r: { angleLines: { display: true }, suggestedMin: 0, suggestedMax: 100 }
  }
}
</script>

<style scoped>
.charts-container {
  display: flex;
  flex-wrap: wrap;
  gap: 32px;
  justify-content: space-between;
}

.chart-card {
  flex: 1 1 45%;
  min-width: 300px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgb(0 0 0 / 0.1);
  padding: 20px;
}

.chart-card h3 {
  margin-bottom: 20px;
}
</style>
