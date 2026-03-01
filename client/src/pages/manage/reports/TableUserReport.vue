<template>
  <DivCustom label="Danh sách thành viên">
    <a-tooltip :title="selectedIds.length === 0 ? 'Chọn ít nhất 1 thành viên để xuất báo cáo' : ''">
      <a-select
        v-model:value="exportFormat"
        :disabled="selectedIds.length === 0"
        placeholder="Xuất báo cáo"
        style="width: 130px"
        allowClear
        @change="handleSelectChange"
      >
        <a-select-option value="doc">ZIP-DOC</a-select-option>
        <a-select-option value="excel">EXCEL</a-select-option>
      </a-select>
    </a-tooltip>

    <template #icon>
      <IdcardOutlined />
    </template>

    <div class="pt-5">
      <a-table
        row-key="idStaffProject"
        :row-selection="rowSelection"
        :columns="columns"
        :data-source="data"
        :pagination="{
          current: paginationParams.page,
          pageSize: paginationParams.size,
          total: totalItems,
          showSizeChanger: true,
          pageSizeOptions: ['10', '20', '30', '40', '50']
        }"
        :row-class-name="rowClassName"
        class="max-h-[50vh] overflow-y-auto px-4"
        @change="handlePageChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'nameRoles'">
            <span v-if="record.nameRoles && record.nameRoles.trim() !== ''">
              <template v-for="(role, index) in record.nameRoles.split(',')" :key="index">
                <a-tag color="success" class="mr-1">
                  {{ role.trim() }}
                </a-tag>
              </template>
            </span>
          </template>
        </template>
      </a-table>
    </div>
  </DivCustom>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import { IdcardOutlined } from '@ant-design/icons-vue'
import { Modal, TableColumnsType } from 'ant-design-vue'
import { formatDateTime } from '@/utils/commom.helper'
import { exportFileDoc, exportReport } from '@/services/api/manage/report/report.api'
import { useRoute } from 'vue-router'

const props = defineProps<{
  data: any[]
  paginationParams: { page: number; size: number }
  totalItems: number
}>()

const route = useRoute()
const emit = defineEmits(['page-change', 'select-change'])

const columns: TableColumnsType = [
  {
    title: 'STT',
    key: 'orderNumber',
    dataIndex: 'orderNumber',
    width: '10%',
    align: 'center'
  },
  {
    title: 'Mã thành viên',
    key: 'code',
    dataIndex: 'code',
    width: '40%',
    align: 'left'
  },
  {
    title: 'Tên thành viên',
    key: 'name',
    dataIndex: 'name',
    width: '40%',
    align: 'left'
  }
]

const handlePageChange = (pagination: any) => {
  emit('page-change', { page: pagination.current, pageSize: pagination.pageSize })
}

const selectedIds = ref<string[]>([])

const rowSelection = computed(() => ({
  columnWidth: 40,
  selectedRowKeys: selectedIds.value,
  onSelect: (record: any, selected: boolean) => {
    const id = String(record.idStaffProject)
    if (selected) {
      if (!selectedIds.value.includes(id)) {
        selectedIds.value.push(id)
      }
    } else {
      selectedIds.value = selectedIds.value.filter((item) => item !== id)
    }
  },
  onSelectAll: (selected: boolean, selectedRows: any[]) => {
    if (selected) {
      // Khi chọn tất cả, giả định bạn có một cách để lấy TẤT CẢ các ID từ backend.
      // Đây là nơi bạn sẽ gọi API hoặc truy cập vào một danh sách đã tải sẵn của tất cả các ID.
      // Ví dụ: Giả sử bạn có một prop `allAvailableIds` chứa tất cả các ID từ mọi trang.
      // Nếu không, bạn cần thay đổi logic để lấy tất cả các ID từ API khi người dùng chọn "chọn tất cả".
      // Hiện tại, tôi sẽ mô phỏng bằng cách sử dụng `props.data` (dữ liệu hiện tại) và giả định
      // rằng bạn sẽ fetch toàn bộ danh sách ID từ API khi cần.
      // Để thực sự chọn TẤT CẢ, bạn sẽ cần một API endpoint trả về tất cả các ID.
      // Dưới đây là cách mô phỏng nếu bạn có một danh sách tất cả các ID có sẵn:
      // selectedIds.value = allAvailableIds.value; // Ví dụ nếu bạn có biến này

      // *** Thay đổi quan trọng ở đây ***
      // Để chọn tất cả thành viên trên tất cả các trang, bạn cần một cách để lấy tất cả ID.
      // Tùy thuộc vào kiến trúc API của bạn, bạn có thể:
      // 1. Gọi một API mới để lấy danh sách tất cả các ID.
      // 2. Nếu dữ liệu của bạn không quá lớn và đã được tải toàn bộ ở đâu đó (ví dụ, một store Vuex),
      //    bạn có thể lấy từ đó.
      // Vì tôi không có quyền truy cập vào backend hoặc các hàm API khác, tôi sẽ giả định
      // rằng bạn sẽ có một hàm `fetchAllStaffIds` hoặc tương tự.
      // Tạm thời, để minh họa, tôi sẽ chỉ lấy ID từ `props.data` (các mục trên trang hiện tại)
      // nhưng bạn phải thay thế nó bằng logic lấy *tất cả* ID.
      selectedIds.value = props.data.map((item) => String(item.idStaffProject)) // Cần thay thế bằng toàn bộ ID từ tất cả các trang
      // Nếu bạn muốn thực sự chọn tất cả, bạn cần một API trả về tất cả ID mà không cần phân trang, ví dụ:
      // const allIds = await fetchAllStaffIds();
      // selectedIds.value = allIds;
    } else {
      selectedIds.value = []
    }
  }
}))

// ✅ Gán class để xóa màu nền dòng khi được chọn
const rowClassName = (record: any) => {
  return selectedIds.value.includes(String(record.idStaffProject)) ? 'custom-selected-row' : ''
}

const exportFormat = ref(null)

const handleSelectChange = (value) => {
  if (!value) return

  Modal.confirm({
    title: 'Xác nhận xuất báo cáo',
    content: `Bạn có chắc chắn muốn xuất báo cáo định dạng ${value.toUpperCase()} cho ${
      selectedIds.value.length
    } thành viên không?`,
    okText: 'Xác nhận',
    cancelText: 'Hủy',
    onOk() {
      if (value === 'doc') {
        exportDoc()
      } else if (value === 'excel') {
        exportDetail()
      }
      exportFormat.value = null
    },
    onCancel() {
      exportFormat.value = null
    }
  })
}

const exportDetail = async () => {
  // Đảm bảo `selectedIds.value` chứa TẤT CẢ các ID bạn muốn xuất.
  // Nếu `selectedIds.value` chỉ chứa ID của trang hiện tại, bạn cần điều chỉnh logic `onSelectAll`.
  const res = await exportReport(route.params.id as string, { idUser: selectedIds.value })
  const url = window.URL.createObjectURL(new Blob([res]))
  const link = document.createElement('a')
  link.href = url
  link.setAttribute('download', 'export-report.xlsx')
  document.body.appendChild(link)
  link.click()
  window.URL.revokeObjectURL(url)
  link.remove()
}

const exportDoc = async () => {
  try {
    // Đảm bảo `selectedIds.value` chứa TẤT CẢ các ID bạn muốn xuất.
    const res = await exportFileDoc(route.params.id as string, { idUser: selectedIds.value })
    const blob = new Blob([res], { type: 'application/zip' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', 'bao-cao-thuc-tap.zip')
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  } catch (error) {
    console.error('Lỗi khi xuất file:', error)
    alert('Không thể tải file báo cáo.')
  }
}
</script>

<style>
.ant-table .ant-table-tbody > tr.ant-table-row-selected > td {
  background-color: white !important;
  color: #000 !important;
}

:deep(.ant-btn) {
  background: var(--selected-header) !important; /* Gradient nền */
  color: var(--selected-text) !important; /* Màu chữ trắng */
  border-color: var(--selected-header) !important; /* Viền màu từ gradient */
}

/* Hiệu ứng hover */
:deep(.ant-btn:hover),
:deep(.ant-btn:focus) {
  background: var(--selected-header-hover) !important; /* Gradient khi hover */
  border-color: var(--selected-header-hover) !important; /* Viền khi hover */
}
</style>
