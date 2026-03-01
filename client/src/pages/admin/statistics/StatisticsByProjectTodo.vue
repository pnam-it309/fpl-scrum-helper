<template>
  <DivCustom label="Biểu đồ số lượng công việc mỗi dự án">
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
import { getProjectTodoCounts } from '@/services/api/admin/project/project.api'

use([BarChart, TitleComponent, TooltipComponent, GridComponent, LegendComponent, CanvasRenderer])

const barChartOptions = ref({})

const fetchProjectsTodoCounts = async () => {
  try {
    const res = await getProjectTodoCounts()

    if (!res || !Array.isArray(res.data)) {
      console.warn('Dữ liệu không đúng định dạng:', res)
      return
    }

    const projects = res.data

    barChartOptions.value = {
      title: {
        text: 'Số lượng công việc theo dự án'
      },
      tooltip: {},
      xAxis: {
        type: 'category',
        data: projects.map((p) => p.projectName),
        axisLabel: { rotate: 30 }
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: 'Số lượng công việc',
          type: 'bar',
          data: projects.map((p) => p.totalTodo),
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

onMounted(fetchProjectsTodoCounts)
</script>
