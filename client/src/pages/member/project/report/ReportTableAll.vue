<template>
  <div class="flex justify-end mb-4">
    <a-tooltip title="Xuất báo cáo">
      <a-button
        @click="showExportModal = true"
        style="background-color: #cc9708 !important"
        class="px-4"
      >
        Xuất báo cáo
      </a-button>
    </a-tooltip>

    <a-tooltip title="Xem biểu đồ báo cáo">
      <a-button
        @click="showChartModal = true"
        style="margin-left: 5px;   background-color: #cc9708 !important"
        class="px-4"
      >
        Biểu đồ
      </a-button>
    </a-tooltip>

  </div>

  <a-modal
    v-model:open="showExportModal"
    title="Chọn định dạng xuất báo cáo"
    ok-text="Xuất"
    cancel-text="Hủy"
    @ok="handleExportReport"
  >
    <a-select v-model:value="exportFormat" style="width: 100%">
      <a-select-option value="docx">Word (.docx)</a-select-option>
      <a-select-option value="xlsx">Excel (.xlsx)</a-select-option>
    </a-select>
  </a-modal>

<a-modal
  v-model:open="showChartModal"
  title="Biểu đồ báo cáo"
  width="50%"
  @cancel="showChartModal = false"
>
  <ReportChart />

  <template #footer>
    <a-button @click="showChartModal = false">Hủy</a-button>
  </template>
</a-modal>


</template>

<script setup lang="ts">
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import { TableColumnsType } from 'ant-design-vue'
import { getDateFormat } from '@/utils/commom.helper'
import { h, ref } from 'vue'
import {
  exportMemberReportDocx,
  exportMemberReportExcel
} from '@/services/api/member/report/report.api'
import { useRoute } from 'vue-router'
import ReportChart from './ReportChart.vue'

const route = useRoute()
const showExportModal = ref(false)
const exportFormat = ref('docx')

const handleExportReport = async () => {
  showExportModal.value = false
  const projectId = route.params.id as string

  if (exportFormat.value === 'docx') {
    const res = await exportMemberReportDocx(projectId)
    const url = window.URL.createObjectURL(new Blob([res]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', 'bao_cao.docx')
    document.body.appendChild(link)
    link.click()
    window.URL.revokeObjectURL(url)
    link.remove()
  } else if (exportFormat.value === 'xlsx') {
    const res = await exportMemberReportExcel(projectId)
    const url = window.URL.createObjectURL(new Blob([res]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', 'bao_cao.xlsx')
    document.body.appendChild(link)
    link.click()
    window.URL.revokeObjectURL(url)
    link.remove()
  }
}

const showChartModal = ref(false)

</script>

<style scoped>
:deep(.ant-btn) {
  background: var(--selected-header) !important;
  color: var(--selected-text) !important; 
  border-color: var(--selected-header) !important;
}

:deep(.ant-btn:hover),
:deep(.ant-btn:focus) {
  background: var(--selected-header-hover) !important;
  border-color: var(--selected-header-hover) !important; 
}
</style>
