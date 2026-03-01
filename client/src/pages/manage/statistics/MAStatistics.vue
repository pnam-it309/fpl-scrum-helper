<template>
  <DivCustomOld label="Thống kê dự án">
    <!-- <label class="font-bold text-4xl text-black">Thống kê dự án</label> -->
    <hr />
    <div class="flex mt-10">
      <H1 class="font-semibold text-black text-xl mr-4">Bộ lọc :</H1>
      <a-select
        v-model:value="idPeriodChange"
        show-search
        placeholder="Tất cả dự án "
        style="width: 200px"
        :options="dataPhase"
        @change="handleChange"
        allowClear
      ></a-select>
    </div>
    <div></div>
    <DivCustomOld class="flex w-full mt-10">
      <div class="w-2/3">
        <MAStatisticsTodo :id-phase="idPeriodChange" />
      </div>
      <div class="w-1/3">
        <MAStatisticsPhase :data-phase="dataSprint" />
      </div>
    </DivCustomOld>
    <DivCustomOld>
      <div>
        <MAStatisticsTodoLevel :id-phase="idPeriodChange" />
      </div>
    </DivCustomOld>

    <DivCustomOld>
      <div>
        <MAStatisticsStaffProject :id-phase="idPeriodChange" />
      </div>
    </DivCustomOld>
  </DivCustomOld>
</template>

<script setup lang="ts">
import MAStatisticsTodo from './MAStatisticsTodo.vue'
import { h, onMounted, ref } from 'vue'
import { getAllPhaseStatistics, phaseResponse } from '@/services/api/manage/phase/phase.api'
import { useRoute, useRouter } from 'vue-router'
import MAStatisticsPhase from './MAStatisticsPhase.vue'
import MAStatisticsTodoLevel from './MAStatisticsTodoLevel.vue'
import MAStatisticsStaffProject from './MAStatisticsStaffProject.vue'
import { MenuProps } from 'ant-design-vue'
import { MailOutlined } from '@ant-design/icons-vue'
import DivCustomOld from '@/components/custom/Div/DivCustomOld.vue'
import BurnDownChart from '@/pages/member/project/chart/BurnDownChart.vue'

const route = useRoute()

const idProject = ref('')
const idPeriodChange = ref('')
const dataPhase = ref([]) // Khai báo dataPhase là một ref chứa mảng
const current = ref<string[]>(['mail'])

const dataSprint = ref<phaseResponse[]>()

const fetchDataPhase = async () => {
  try {
    const res = await getAllPhaseStatistics(idProject.value)
    dataSprint.value = res.data

    dataPhase.value = res.data.map((item: phaseResponse) => ({
      value: item.id as string,
      label: item.name as string
    }))
  } catch (error) {
    console.error('Lỗi khi lấy dữ liệu giai đoạn:', error)
  }
}

const handleChange = (value: string) => {
  idPeriodChange.value = value
}

onMounted(() => {
  idPeriodChange.value = null
  idProject.value = route.params.id as string
  fetchDataPhase()
})
</script>
