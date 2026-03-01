<template>
  <div v-if="isLoading" class="text-center">Đang tải biểu đồ...</div>

  <div v-else-if="series.length > 0" class="flex justify-center">
    <div class="w-[300px]">
      <div
        id="chartOne"
        class="overflow-hidden rounded-2xl border border-none bg-white px-4 pt-4 dark:border-gray-800 dark:bg-white/[0.03]"
      >
        <h3 class="text-base font-semibold text-center mb-2 text-gray-800 dark:text-white/90">
          Tỉ lệ báo cáo công việc
        </h3>
        <VueApexCharts
          type="pie"
          height="300"
          :options="chartOptions"
          :series="series"
        />
      </div>
    </div>
  </div>

  <div v-else class="text-center">
    <a-empty description="Chưa có dữ liệu" />
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import VueApexCharts from 'vue3-apexcharts'
import { useRoute } from 'vue-router'
import { getReportRate } from '@/services/api/member/report/report.api'
import { detailPhase } from '@/services/api/member/phase/phase.api'

const isLoading = ref(true)
const series = ref<number[]>([])

const chartOptions = ref({
  colors: ['#315297', '#fa3b4f'],
  chart: {
    fontFamily: 'Outfit, sans-serif',
    type: 'pie',
    toolbar: { show: false }
  },
  labels: ['Đã báo cáo', 'Chưa báo cáo'],
  legend: {
    show: true,
    position: 'bottom',
    horizontalAlign: 'center',
    fontFamily: 'Outfit',
    markers: { radius: 99 }
  },
  responsive: [
    {
      breakpoint: 480,
      options: {
        chart: { width: '100%' },
        legend: { position: 'bottom' }
      }
    }
  ]
})

const route = useRoute()
const idProject = route.params.id as string
const idPhase = ref<string | null>(null)

onMounted(async () => {
  isLoading.value = true

  try {
    console.log('📦 Lấy phase cho project:', idProject)
    const resPhase = await detailPhase(idProject)
    idPhase.value = resPhase.data?.id

    if (!idPhase.value) {
      console.warn('⚠️ Không lấy được idPhase')
      return
    }

    console.log('📥 Lấy reportRate với:', { idProject, idPhase: idPhase.value })

    const resReport = await getReportRate(idProject, idPhase.value)
    const report = resReport?.data
    console.log('📊 Resport.data:', report)

    if (report && typeof report.reportRate === 'number') {
      const rate = Number(report.reportRate)
      console.log('✅ Report rate:', rate)

      series.value = [
        parseFloat(rate.toFixed(2)),
        parseFloat((100 - rate).toFixed(2))
      ]
    } else {
      console.warn('⚠️ Không có hoặc sai reportRate:', report)
      series.value = []
    }
  } catch (error) {
    console.error('❌ Lỗi khi tải dữ liệu biểu đồ:', error)
    series.value = []
  } finally {
    isLoading.value = false
  }
})

</script>

<style scoped>
canvas {
  max-width: 400px;
}
</style>
