<template>
  <a-modal :open="props.open" :title="props.title" footer @cancel="closeModal" :width="'60%'">
    <div v-if="isLoading" class="text-center">Đang tải biểu đồ...</div>
    <div v-else-if="chartData.length" class="max-w-full overflow-x-auto custom-scrollbar">
      <div
        id="chartOne"
        class="m-5 overflow-hidden rounded-2xl border border-gray-200 bg-white px-5 pt-5 dark:border-gray-800 dark:bg-white/[0.03] sm:px-6 sm:pt-6"
      >
        <h3 class="text-lg font-semibold text-gray-800 dark:text-white/90">BurnDown Chart</h3>
        <VueApexCharts
          :key="chartData.length"
          type="line"
          height="500"
          :options="chartOptions"
          :series="series"
        />
      </div>
    </div>
    <div v-else class="text-center">
      <a-empty :description="'Chưa có giai đoạn nào diễn ra'" />
    </div>
  </a-modal>
</template>

<script lang="ts" setup>
import { BurndownPoint, getBurndownChartByProject } from '@/services/api/member/chart/chart.api'
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import VueApexCharts from 'vue3-apexcharts'

const props = defineProps<{ open: boolean; title: string }>()
const emit = defineEmits(['close'])

const closeModal = () => {
  emit('close')
}

const route = useRoute()
const projectId = ref<string | null>(route.params.id as string || null)
const chartData = ref<BurndownPoint[]>([])
const isLoading = ref<boolean>(true)
const error = ref<string | null>(null)

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
  },
  markers: {
    size: 5,
    colors: ['#008FFB', '#00e396'],
    strokeWidth: 2,
    strokeColors: '#fff',
    hover: { size: 7 },
  },
  xaxis: {
    categories: [],
    title: { text: 'Iteration' },
    axisBorder: { show: false },
    axisTicks: { show: false },
  },
  yaxis: {
    title: { text: 'Story Points' },
    min: 0,
  },
  legend: {
    show: true,
    position: 'top',
    horizontalAlign: 'center',
    fontFamily: 'Outfit',
    markers: { radius: 4 },
  },
  grid: { yaxis: { lines: { show: true } } },
  fill: { opacity: 1 },
  tooltip: {
    x: { formatter: (val: string) => val || '' },
    y: {
      formatter: (val: number | undefined, { series, seriesIndex, dataPointIndex }) => {
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
    if (!projectId.value) throw new Error('Thiếu projectId')

    const response = await getBurndownChartByProject(projectId.value)
    console.log('Phản hồi API:', response)

    if (response?.data && Array.isArray(response.data) && response.data.length) {
      const rawData = response.data.sort(
        (a, b) => new Date(a.date).getTime() - new Date(b.date).getTime()
      )

      chartData.value = rawData // Gán dữ liệu để v-if trong template hoạt động

      const minDate = new Date(rawData[0].date)
      const maxDate = new Date(rawData[rawData.length - 1].date)
      const today = new Date()

      // Build danh sách ngày đầy đủ
      const fullDates: string[] = []
      const current = new Date(minDate)
      while (current <= maxDate) {
        fullDates.push(current.toISOString().split('T')[0])
        current.setDate(current.getDate() + 1)
      }

      // Tạo Map để tra nhanh
      const dataMap = new Map<string, BurndownPoint>()
      rawData.forEach(item => dataMap.set(item.date, item))

const actualData: (number | null)[] = []
const expectedData: (number | null)[] = []

let lastActual: number | null = null
let lastExpected: number | null = null

for (const date of fullDates) {
  const dp = dataMap.get(date)

  // Expected
  if (dp?.expected !== null && dp?.expected !== undefined) {
    expectedData.push(dp.expected)
    lastExpected = dp.expected
  } else if (lastExpected !== null && new Date(date) <= today) {
    // Ngày đã qua và có giá trị trước đó → nối
    expectedData.push(lastExpected)
  } else {
    expectedData.push(null)
  }

  // Actual
  if (dp?.actual !== null && dp?.actual !== undefined) {
    actualData.push(dp.actual)
    lastActual = dp.actual
  } else if (lastActual !== null && new Date(date) <= today) {
    // Ngày đã qua và có giá trị trước đó → nối
    actualData.push(lastActual)
  } else {
    actualData.push(null)
  }
}



      chartOptions.value.xaxis.categories = fullDates
      series.value = [
  { name: 'Expected', type: 'line', data: expectedData },
  { name: 'Actual', type: 'line', data: actualData },
]

      const maxValue = Math.max(
        ...(expectedData.filter(v => v !== null) as number[]),
        ...(actualData.filter(v => v !== null) as number[])
      )
      chartOptions.value.yaxis.max = maxValue > 0 ? maxValue + 20 : 120
    } else {
      chartData.value = []
    }
  } catch (err) {
    console.error('Lỗi khi lấy dữ liệu biểu đồ:', err)
    error.value = 'Không thể tải dữ liệu biểu đồ.'
    chartData.value = []
  } finally {
    isLoading.value = false
  }
}

onMounted(fetchChartData)
</script>
