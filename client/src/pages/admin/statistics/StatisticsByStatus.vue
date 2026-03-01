<template>
  <DivCustom label="Thống kê theo trạng thái dự án">
    <v-chart class="h-96" :option="chartOptions" />
  </DivCustom>
</template>

<script setup lang="ts">
// Import các hàm và module cần thiết
import { ref, onMounted } from 'vue'
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { PieChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import { getProjectStatistics } from '@/services/api/admin/project/project.api'

// Cấu hình ECharts
use([PieChart, TitleComponent, TooltipComponent, LegendComponent, CanvasRenderer])

const chartOptions = ref({})

// Lấy dữ liệu thống kê từ backend và cập nhật biểu đồ
const fetchProjectStats = async () => {
  try {
    const res = await getProjectStatistics() // Lấy dữ liệu từ API

    if (res.success) {
      const projectStats = res.data

      if (
        projectStats[0] === 0 && // Trạng thái 'Đã diễn ra'
        projectStats[1] === 0 && // Trạng thái 'Đang diễn ra'
        projectStats[2] === 0 // Trạng thái 'Chưa diễn ra'
      ) {
        alert('Không có dữ liệu thống kê dự án!')
      } else {
        // Ánh xạ lại trạng thái số thành chuỗi
        const statusMap = {
          0: 'Đã diễn ra',
          1: 'Đang diễn ra',
          2: 'Chưa diễn ra'
        }

        // Dữ liệu từ backend trả về dưới dạng: { 0: 1, 1: 4 }
        chartOptions.value = {
          title: {
            text: 'Tình trạng dự án',
            left: 'center'
          },
          tooltip: {
            trigger: 'item'
          },
          legend: {
            bottom: '0%',
            left: 'center'
          },
          series: [
            {
              name: 'Trạng thái',
              type: 'pie',
              radius: '50%',
              data: Object.entries(projectStats).map(([statusCode, count]) => ({
                value: count,
                name: statusMap[parseInt(statusCode)] || 'Không xác định'
              }))
            }
          ]
        }
      }
    } else {
      alert(res.message)
    }
  } catch (error) {
    console.error('Error fetching project statistics:', error)
  }
}

// Lấy dữ liệu khi component được mount
onMounted(fetchProjectStats)
</script>

<style scoped>
.h-96 {
  height: 24rem;
}
</style>
