<template>
  <DivCustom label="Biểu đồ số lượng thành viên mỗi dự án">
    <v-chart class="w-full h-96" :option="barChartOptions" autoresize />
  </DivCustom>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { BarChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  GridComponent,
  LegendComponent
} from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import { getProjectParticipantsStatistics } from '@/services/api/admin/project/project.api'

use([BarChart, TitleComponent, TooltipComponent, GridComponent, LegendComponent, CanvasRenderer])

const barChartOptions = ref({})

const fetchProjectsWithMembers = async () => {
  try {
    const res = await getProjectParticipantsStatistics()
    const projects = res.data.map((project) => ({
      name: project.projectName,
      memberCount: project.totalParticipants
    }))

    barChartOptions.value = {
      title: {
        text: 'Số lượng thành viên theo dự án'
      },
      tooltip: {},
      xAxis: {
        type: 'category',
        data: projects.map((p) => p.name),
        axisLabel: { rotate: 30 }
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: 'Số thành viên',
          type: 'bar',
          data: projects.map((p) => p.memberCount),
          itemStyle: {
            color: '#4f46e5'
          }
        }
      ]
    }
  } catch (error) {
    console.error('Lỗi khi lấy dữ liệu biểu đồ:', error)
  }
}

onMounted(fetchProjectsWithMembers)
</script>
