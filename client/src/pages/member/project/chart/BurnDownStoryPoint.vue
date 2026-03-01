<template>
  <div :visible="isOpen" v-if="isLoading" class="text-center">Đang tải biểu đồ...</div>
  <div v-else class="max-w-full overflow-x-auto custom-scrollbar">
    <div id="chartOne"
      class="m-5 overflow-hidden rounded-2xl border border-gray-200 bg-white px-5 pt-5 dark:border-gray-800 dark:bg-white/[0.03] sm:px-6 sm:pt-6">
      <VueApexCharts :key="chartData.length" type="line" height="400" :options="chartOptions" :series="series" />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import VueApexCharts from 'vue3-apexcharts'
import { getBurndownChartTrello, BurndownPoint } from '@/services/api/member/chart/chart.api'

const route = useRoute()

const projectId = ref<string | null>(route.params.id as string || null)
const phaseId = ref<string | null>(route.params.idPhase as string || null)
const chartData = ref<BurndownPoint[]>([])
const isLoading = ref<boolean>(true)
const error = ref<string | null>(null)
defineProps<{ isOpen: boolean }>()

const series = ref<any[]>([])
const chartOptions = ref<any>({
  chart: {
    fontFamily: 'Outfit, sans-serif',
    type: 'line',
    toolbar: { show: false },
    stacked: false,
  },
  title: {
    text: 'BurnDown Chart',
    align: 'center',
    style: { fontSize: '18px', fontWeight: 'bold' },
  },
  dataLabels: { enabled: false },
  stroke: {
    show: true,
    width: 2,
    curve: 'straight',
    colors: ['#008FFB', '#00e396'],
    dashArray: [0, 0],
  },
  markers: {
    size: 5,
    colors: ['#008FFB', '#00e396'],
    strokeWidth: 2,
    strokeColors: '#fff',
    hover: {
      size: 7,
    },
  },
  xaxis: {
    categories: [],
    title: {
      text: 'Iteration',
    },
    axisBorder: { show: false },
    axisTicks: { show: false },
  },
  yaxis: {
    title: {
      text: 'Story Points',
    },
    min: 0,
  },
  legend: {
    show: true,
    position: 'top',
    horizontalAlign: 'center',
    fontFamily: 'Outfit',
    markers: { radius: 4 },
  },
  grid: {
    yaxis: { lines: { show: true } },
  },
  fill: { opacity: 1 },
  tooltip: {
    x: {
      formatter: (val: string | undefined) => (val ? `${val}` : ''),
    },
    y: {
      formatter: function (val: number | undefined, { series, seriesIndex, dataPointIndex }) {
        const point = series?.[seriesIndex]?.[dataPointIndex]
        return point === null || point === undefined ? '' : `${point}`
      },
    },
  },

})

const fetchChartData = async () => {
  isLoading.value = true
  error.value = null

  try {
    if (!projectId.value || !phaseId.value) {
      throw new Error('Thiếu projectId hoặc phaseId')
    }

    const response = await getBurndownChartTrello(projectId.value, phaseId.value)
    console.log('Phản hồi API:', response)

    if (response?.data && Array.isArray(response.data)) {
      // Sort dữ liệu gốc
      const rawData = response.data.sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime())

      // Tính ngày bắt đầu -> kết thúc
      const minDate = new Date(rawData[0].date)
      const maxDate = new Date(rawData[rawData.length - 1].date)

      const today = new Date()
      const fullDates: string[] = []

      const current = new Date(minDate)
      while (current <= maxDate) {
        const dateStr = current.toISOString().split('T')[0]
        fullDates.push(dateStr)
        current.setDate(current.getDate() + 1)
      }

      // Chuyển rawData thành Map để tra nhanh
      const dataMap = new Map<string, BurndownPoint>()
      rawData.forEach(item => dataMap.set(item.date, item))

      // Build dữ liệu với full ngày
      const expectedData: (number | null)[] = []
      const actualData: (number | null)[] = []

      let lastActual: number | null = null

      for (const date of fullDates) {
        const dataPoint = dataMap.get(date)
        const dateObj = new Date(date)

        // expected luôn lấy nếu có
        expectedData.push(dataPoint?.expected ?? null)

        if (dataPoint?.actual !== null && dataPoint?.actual !== undefined) {
          lastActual = dataPoint.actual
          actualData.push(lastActual)
        } else {
          // Nếu ngày chưa đến hôm nay thì vẫn để null để "đứt đoạn"
          if (dateObj <= today) {
            actualData.push(null)
          } else {
            // Sau hôm nay thì chưa tính tới
            actualData.push(null)
          }
        }
      }

      chartOptions.value.xaxis.categories = fullDates

      series.value = [
        {
          name: 'Expected',
          type: 'line',
          data: expectedData,
        },
        {
          name: 'Actual',
          type: 'line',
          data: actualData,
        },
      ]

      const maxValue = Math.max(
        ...(expectedData.filter(v => v !== null) as number[]),
        ...(actualData.filter(v => v !== null) as number[])
      )
      chartOptions.value.yaxis.max = maxValue > 0 ? maxValue + 20 : 120
    }

  } catch (err) {
    console.error('Lỗi khi lấy dữ liệu biểu đồ:', err)
    error.value = 'Không thể tải dữ liệu biểu đồ. Vui lòng thử lại.'
    const defaultDays = 22
    chartOptions.value.xaxis.categories = Array.from({ length: defaultDays + 1 }, (_, i) => i.toString())
    series.value = [
      {
        name: 'Expected',
        type: 'line',
        data: Array(defaultDays + 1).fill(null),
      },
      {
        name: 'Actual',
        type: 'line',
        data: Array(defaultDays + 1).fill(null),
      },
    ]
    chartOptions.value.yaxis.max = 120
  } finally {
    isLoading.value = false
  }
}

watch(chartData, () => {
  if (chartData.value.length) {
    chartData.value.sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime())

    chartOptions.value.xaxis.categories = chartData.value.map(item => item.date || '')
    const expectedData = chartData.value.map(item => item.expected ?? null)

    // Fill-forward actual
    const actualData: (number | null)[] = []
    let lastActual: number | null = null
    for (const item of chartData.value) {
      if (item.actual !== null && item.actual !== undefined) {
        lastActual = item.actual
      }
      actualData.push(lastActual)
    }

    series.value = [
      {
        name: 'Expected',
        type: 'line',
        data: expectedData,
      },
      {
        name: 'Actual',
        type: 'line',
        data: actualData,
      },
    ]

    const maxValue = Math.max(
      ...(expectedData.filter(v => v !== null) as number[]),
      ...(actualData.filter(v => v !== null) as number[])
    )
    chartOptions.value.yaxis.max = maxValue > 0 ? maxValue + 20 : 120
  } else {
    const defaultDays = 22
    chartOptions.value.xaxis.categories = Array.from({ length: defaultDays + 1 }, (_, i) => i.toString())
    series.value = [
      {
        name: 'Expected',
        type: 'line',
        data: Array(defaultDays + 1).fill(null),
      },
      {
        name: 'Actual',
        type: 'line',
        data: Array(defaultDays + 1).fill(null),
      },
    ]
    chartOptions.value.yaxis.max = 120
  }
})

onMounted(fetchChartData)
</script>
