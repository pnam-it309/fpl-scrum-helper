<template>
  <DivCustom label="Danh sách dự án" customClasses="mt-5">
    <template #icon>
      <ProjectOutlined />
    </template>
    <template #extra>
      <div class="flex gap-2">
        <a-button
          type="primary"
          @click="handleAddClick"
          class="flex items-center justify-center px-4"
        >
          <PlusCircleOutlined /> Thêm dự án
        </a-button>

        <a-tooltip :title="selectedRowKeys.length > 0 ? 'Tải xuống báo cáo' : 'Chọn ít nhất 1 dự án để xuất báo cáo'">
          <a-button
            type="primary"
            :disabled="selectedRowKeys.length === 0"
            @click="exportReportZip"
            class="flex items-center justify-center px-4"
          >
            <DownloadOutlined /> Tải xuống báo cáo (Doc + ZIP)
          </a-button>
        </a-tooltip>
        <!-- <a-tooltip
          :title="selectedRowKeys.length === 0 ? 'Chọn ít nhất 1 dự án để xuất báo cáo' : ''"
        >
          <a-select
            v-model:value="exportFormat"
            :disabled="selectedRowKeys.length === 0"
            placeholder="Xuất báo cáo"
            style="width: 130px"
            allowClear
            @change="handleSelectChange"
          >
            <a-select-option value="doc">DOC</a-select-option>
            <a-select-option value="zip">ZIP</a-select-option>
            <a-select-option value="excel">EXCEL</a-select-option>
          </a-select>
        </a-tooltip> -->

        <a-tooltip
          :title="selectedRowKeys.length === 0 ? 'Chọn ít nhất 1 dự án để xuất báo cáo' : ''"
        >
          <a-select
            v-model:value="exportFormat"
            placeholder="Xuất báo cáo Excel"
            style="width: 220px; color: white !important"
            :disabled="selectedRowKeys.length === 0"
            @change="handleSelectChange"
            :class="{ 'highlight-select': selectedRowKeys.length > 0 }"
          >
            <a-select-option value="1 file - 1 sheet">Gộp vào 1 file (1 sheet)</a-select-option>

            <a-select-option v-if="selectedRowKeys.length > 1" value="1 file - n sheet">
              Gộp vào 1 file (nhiều sheet)
            </a-select-option>

            <a-select-option value="n file - nén zip">Mỗi dự án 1 file (nén ZIP)</a-select-option>
          </a-select>
        </a-tooltip>
      </div>
    </template>
    <div class="min-h-[360px]">
      <a-table
        :columns="columns"
        :data-source="data"
        size="middle"
        :pagination="{
          current: page,
          pageSize: size,
          total: totalItems,
          showSizeChanger: true,
          pageSizeOptions: ['10', '20', '30', '40', '50']
        }"
        :scroll="{ x: 1300, y: 600 }"
        :row-selection="rowSelection"
        :row-key="(record) => record.id"
        @change="handlePageChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <a-tag :color="getStatusColor(record.status)">
              {{ getTextStatus(record.status) }}
            </a-tag>
          </template>

          <template v-if="column.key === 'operation'">
            <div class="flex items-center gap-1 justify-center">
              <a-tooltip title="Chỉnh sửa dự án">
                <a-button
                  @click="handleViewClick(record.id, record.idFacility)"
                  class="flex items-center justify-center w-8 h-8"
                >
                  <FormOutlined />
                </a-button>
              </a-tooltip>
              <a-tooltip title="Xóa dự án">
                <a-button
                  @click="handleDeleteClick(record.id)"
                  class="flex items-center justify-center w-8 h-8"
                >
                  <DeleteOutlined />
                </a-button>
              </a-tooltip>

              <a-tooltip title="Kết thúc dự án">
                <a-button
                  @click.stop="handleFinishEarlyProject(record.id)"
                  type="primary"
                  class="flex items-center justify-center w-8 h-8"
                >
                  <FieldTimeOutlined />
                  <!-- {{ record.status == 'DA_DIEN_RA' ? 'Chi tiết' : 'Kết thúc' }} -->
                </a-button>
              </a-tooltip>
            </div>
          </template>
        </template>
      </a-table>
    </div>
  </DivCustom>
</template>

<script setup lang="ts">
import { defineProps, defineEmits, h, ref, reactive } from 'vue'
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import { TableColumnsType, Tag } from 'ant-design-vue'
import { router } from '@/routes/router'
import { ROUTES_CONSTANTS } from '@/constants/path'
import { projectResponse } from '@/services/api/admin/project/project.api'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { library } from '@fortawesome/fontawesome-svg-core'
library.add(
  faReceipt,
  faPenToSquare,
  faCircleInfo,
  faFilter,
  faRotateRight,
  faTrash,
  faTrashAlt,
  faEye
)
import {
  faReceipt,
  faPenToSquare,
  faCircleInfo,
  faFilter,
  faRotateRight,
  faTrash,
  faTrashAlt,
  faEye
} from '@fortawesome/free-solid-svg-icons'
import {
  DeleteOutlined,
  DownloadOutlined,
  FieldTimeOutlined,
  FormOutlined,
  PlusCircleOutlined,
  ProjectOutlined,
  ScheduleOutlined
} from '@ant-design/icons-vue'
import {
  downloadReportExcelMultiFileSheetZip,
  downloadReportExcelOneFileMultiSheet,
  downloadReportExcelOneFileOneSheet,
  downloadReportZip,
  GenerateReportRequest
} from '@/services/api/admin/project/report.api'

import { API_ADMIN_REPORT } from '@/constants/url'
const props = defineProps<{
  data: projectResponse[]
  page: number
  size: number
  totalItems: number
}>()

const emit = defineEmits([
  'page-change',
  'add',
  'view',
  'delete',
  'idFacility',
  'selection-change',
  'openSumaryModal'
])

const getStatusColor = (status) => {
  switch (status) {
    case 'CHUA_DIEN_RA':
      return 'red'
    case 'DANG_DIEN_RA':
      return 'success'
    case 'DA_DIEN_RA':
      return 'blue'
  }
}

const getTextStatus = (status) => {
  switch (status) {
    case 'CHUA_DIEN_RA':
      return 'Chưa diễn ra'
    case 'DANG_DIEN_RA':
      return 'Đang diễn ra'
    case 'DA_DIEN_RA':
      return 'Đã kết thúc'
  }
}

const columns: TableColumnsType = [
  {
    title: 'STT',
    key: 'orderNumber',
    dataIndex: 'orderNumber',
    width: 40,
    align: 'left',
    fixed: 'left'
  },
  {
    title: 'Mã dự án',
    key: 'codeProject',
    dataIndex: 'codeProject',
    width: 100,
    align: 'left',
    fixed: 'left'
  },
  { title: 'Tên dự án', key: 'nameProject', dataIndex: 'nameProject', width: 100, align: 'left' },
  {
    title: 'Bộ môn',
    key: 'nameDepartment',
    dataIndex: 'nameDepartment',
    width: 100,
    align: 'left'
  },
  {
    title: 'Bắt đầu',
    key: 'startTime',
    dataIndex: 'startTime',
    width: 100,
    align: 'center'
  },
  {
    title: 'Kết thúc',
    key: 'endTime',
    dataIndex: 'endTime',
    width: 100,
    align: 'center'
  },
  {
    title: 'Trạng thái',
    key: 'status',
    dataIndex: 'status',
    width: 100,
    align: 'center',
    customRender: ({ text }) => {
      return h(
        Tag,
        { color: text === 0 ? 'red' : text === 2 ? 'blue' : 'yellow', class: 'ml-3.5' },
        {
          default: () => (text === 0 ? 'Chưa hoạt động' : text === 1 ? 'Hoạt động' : 'Đã kết thúc')
        }
      )
    }
  },
  {
    title: 'Hành động',
    key: 'operation',
    width: 80,
    align: 'center',
    fixed: 'right'
  }
]

const handlePageChange = (pagination: any) => {
  emit('page-change', { page: pagination.current, pageSize: pagination.pageSize })
}

const handleAddClick = () => {
  emit('add')
}

const handleViewClick = (id: string, idFacility: string) => {
  emit('view', id, idFacility)
}

const handleDeleteClick = (id: string) => {
  emit('delete', id)
}

const handleFinishEarlyProject = (idProject: string) => {
  emit('openSumaryModal', idProject)
}

const handleDetailClick = (id: string) => {
  router.push({
    name: `${ROUTES_CONSTANTS.ADMIN.children.STAFF_DETAIL.name}`,
    params: { id }
  })
}

const selectedRowKeys = ref([])

const rowSelection = reactive({
  selectedRowKeys,
  onChange: (newSelectedKeys, selectedRows) => {
    selectedRowKeys.value = newSelectedKeys
    emit('selection-change', {
      keys: newSelectedKeys,
      rows: selectedRows
    })

    console.log('Selected Row Keys:', newSelectedKeys)
    console.log('Selected Rows:', selectedRows)
  }
})
const exportFormat = ref(null)

import { Modal } from 'ant-design-vue'

function handleSelectChange(value) {
  if (!value) {
    // nếu xóa chọn thì không làm gì
    return
  }

  Modal.confirm({
    title: 'Xác nhận xuất báo cáo',
    content: `Bạn có chắc chắn muốn xuất báo cáo định dạng ${value.toUpperCase()} cho ${
      selectedRowKeys.value.length
    } dự án không?`,
    okText: 'Xác nhận',
    cancelText: 'Hủy',
    onOk() {
      if (value === '1 file - 1 sheet') {
        download_Report_ExcelOneFileOneSheet()
      } else if (value === '1 file - n sheet') {
        download_Report_ExcelOneFileMultiSheet()
      } else if (value === 'n file - nén zip') {
        download_Report_ExcelMultiFileSheetZip()
      }
      exportFormat.value = null
    },
    onCancel() {
      exportFormat.value = null
    }
  })
}

const exportReportZip = async () => {
  try {
    const requestData: GenerateReportRequest = {
      projectIds: selectedRowKeys.value
    }
    const blob = await downloadReportZip(requestData)
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', 'bao_cao_du_an.zip')
    document.body.appendChild(link)
    link.click()
    link.remove()
    window.URL.revokeObjectURL(url)
  } catch (error) {
    console.error('Lỗi khi tải báo cáo zip:', error)
  }
}

const download_Report_ExcelOneFileOneSheet = async () => {
  try {
    const requestData: GenerateReportRequest = {
      projectIds: selectedRowKeys.value
    }
    const blob = await downloadReportExcelOneFileOneSheet(requestData)
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', 'BaoCao_TongHop.xlsx')
    document.body.appendChild(link)
    link.click()
    link.remove()
    window.URL.revokeObjectURL(url)
  } catch (error) {
    console.error('Lỗi khi tải báo cáo:', error)
  }
}

const download_Report_ExcelOneFileMultiSheet = async () => {
  try {
    const requestData: GenerateReportRequest = {
      projectIds: selectedRowKeys.value
    }
    const blob = await downloadReportExcelOneFileMultiSheet(requestData)
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', 'BaoCao_TongHop.xlsx') // ✅ Sửa đuôi zip thành xlsx
    document.body.appendChild(link)
    link.click()
    link.remove()
    window.URL.revokeObjectURL(url)
  } catch (error) {
    console.error('Lỗi khi tải báo cáo:', error)
  }
}

const download_Report_ExcelMultiFileSheetZip = async () => {
  try {
    const requestData: GenerateReportRequest = {
      projectIds: selectedRowKeys.value
    }
    const blob = await downloadReportExcelMultiFileSheetZip(requestData)

    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', 'BaoCao_TongHop.zip') // ✅ Sửa đuôi zip
    document.body.appendChild(link)
    link.click()
    link.remove()
    window.URL.revokeObjectURL(url)
  } catch (error) {
    console.error('Lỗi khi tải báo cáo:', error)
  }
}
</script>

<style>
.ant-table .ant-table-tbody > tr.ant-table-row-selected > td {
  background-color: white !important;
  color: #000 !important;
}
.highlight-select .ant-select-selector {
  transition: all 0.3s ease;
}

.highlight-select .ant-select-selector {
  background-color: #2c3e50 !important;
}
.highlight-select .ant-select-selection-item {
  color: #fff !important;
}

/* Ghi đè màu placeholder */
.highlight-select .ant-select-selection-placeholder {
  color: #fff !important; /* Màu cam nổi bật, bạn có thể đổi thành màu khác */
  font-weight: 500;
  font-size: small;
}

/* CSS có thể để trong file scoped hoặc global */
</style>
