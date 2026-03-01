<template>
  <div class="">
    <TableUserReport
      :data="state.dataReport"
      :paginationParams="state.paginationParams"
      :totalItems="state.totalItems"
      @page-change="handlePageChange"
    />
  </div>
</template>

<script setup lang="ts">
import TableUserReport from './TableUserReport.vue'
import { exportReport, getReport, reportResponse } from '@/services/api/manage/report/report.api'
import { onMounted, reactive, ref } from 'vue'
import dayjs from 'dayjs'
import { useRoute } from 'vue-router'

const state = reactive({
  searchQuery: '',
  paginationParams: { page: 1, size: 10 },
  totalItemsHistory: 0,
  paginationParamsHistory: { page: 1, size: 10 },
  totalItems: 0,
  page: 1,
  size: 10,
  dataReport: [] as reportResponse[]
})

const route = useRoute()
const idProject = ref('')
const fetchUserReport = async () => {
  const todayReport = dayjs().format('YYYY-MM-DD')
  const res = await getReport({
    page: state.page,
    size: state.size,
    time: dayjs().valueOf(),
    idProject: idProject.value
  })

  state.dataReport = res.data.content.map((item) => ({
    ...item,
    name: item.nameStudent ?? item.nameStaff,
    code: item.codeStaff ?? item.codeStudent
  }))
  state.totalItems = res.data.totalElements
}

const handlePageChange = ({ page, pageSize }: { page: number; pageSize?: number }) => {
  state.paginationParams.page = page
  if (pageSize) {
    state.paginationParams.size = pageSize
  }

  // Cập nhật luôn giá trị page/size chính
  state.page = page
  if (pageSize) state.size = pageSize

  fetchUserReport()
}

onMounted(() => {
  idProject.value = route.params.id as string
  fetchUserReport()
})
</script>

<style scoped></style>
