<template>
  <a-modal :open="props.open" :title="props.title" footer @cancel="closeModal" :width="'60%'">
    <div class="w-full">
      <div v-if="isLoading" class="text-center">Đang tải biểu đồ Velocity...</div>

      <div v-else-if="chartData.length" class="max-w-full overflow-x-auto custom-scrollbar">
        <div
          id="velocityChart"
          class="m-5 overflow-hidden rounded-2xl border border-gray-200 bg-white px-5 pt-5 dark:border-gray-800 dark:bg-white/[0.03] sm:px-6 sm:pt-6"
        >
          <h3 class="text-lg font-semibold text-gray-800 dark:text-white/90">Velocity Chart</h3>
          <VueApexCharts
            :key="chartData.length"
            type="bar"
            height="400"
            :options="chartOptions"
            :series="series"
          />
        </div>
      </div>

      <div v-else class="text-center">Không có dữ liệu để hiển thị biểu đồ.</div>
    </div>
  </a-modal>

</template>

<script lang="ts" setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import VueApexCharts from 'vue3-apexcharts'
import { getVelocityChart, VelocityChartResponse } from '@/services/api/member/chart/chart.api'

const route = useRoute()
const projectId = ref<string | null>((route.params.id as string) || null)

const props = defineProps<{ open: boolean; title: string }>()
const emit = defineEmits(['close'])

const closeModal = () => {
  emit('close')
}

const isLoading = ref<boolean>(true)
const chartData = ref<VelocityChartResponse[]>([])

const series = ref<any[]>([])
const chartOptions = ref<any>({
  chart: {
    type: 'bar',
    toolbar: { show: false },
    fontFamily: 'Outfit, sans-serif'
  },
  plotOptions: {
    bar: {
      horizontal: false,
      columnWidth: '70%',
      borderRadius: 4
    }
  },
  dataLabels: { enabled: false },
  stroke: {
    show: true,
    width: 2,
    colors: ['transparent']
  },
  xaxis: {
    categories: [],
    title: {
      text: 'Giai đoạn'
    }
  },
  yaxis: {
    title: {
      text: 'Story Points'
    }
  },
  fill: {
    opacity: 1
  },
  tooltip: {
    y: {
      formatter: (val: number) => `${val} điểm`
    }
  },
  colors: ['#0c2258', '#d51826']
})

const fetchChartData = async () => {
  isLoading.value = true
  try {
    if (!projectId.value) return

    const response = await getVelocityChart(projectId.value)
    chartData.value = response?.data || []
    // Xử lý dữ liệu, nếu rỗng thì dùng dữ liệu mặc định
    if (chartData.value.length === 0) {
      // Dữ liệu mặc định với 1 giai đoạn mẫu
      chartOptions.value.xaxis.categories = ['Giai đoạn']
      series.value = [
        { name: 'Ước lượng', data: [0] },
        { name: 'Thực tế', data: [0] },
      ]
    } else {
      // Gán dữ liệu từ API
      series.value = [
        { name: 'Ước lượng', data: chartData.value.map((i) => i.estimated || 0) },
        { name: 'Thực tế', data: chartData.value.map((i) => i.actual || 0) },
      ]
      chartOptions.value.xaxis.categories = chartData.value.map((i) => i.phaseName || '')
    }
  } catch (err) {
    console.error('Lỗi khi lấy dữ liệu velocity chart:', err)
    // Nếu có lỗi, vẫn hiển thị biểu đồ với dữ liệu mặc định
    chartOptions.value.xaxis.categories = ['Giai đoạn mặc định']
    series.value = [
      { name: 'Ước lượng', data: [0] },
      { name: 'Thực tế', data: [0] },
    ]
  } finally {
    isLoading.value = false
  }
}

onMounted(fetchChartData)
</script>