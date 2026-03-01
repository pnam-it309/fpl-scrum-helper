<template>
  <BreadcrumbDefaultV1

  :routes="breadcrumbRoutes"
    label="Chi tiết nhân viên"
    :link="{
      name: ROUTES_CONSTANTS.ADMIN.children.STAFF.name,
      path: ROUTES_CONSTANTS.ADMIN.children.STAFF.path
    }"
  >
    <FormCustom label="Thông tin nhân viên" class="mt-2">
      <a-form layout="vertical" class="mb-4 grid grid-cols-1 md:grid-cols-4 gap-4 p-5">
        <a-form-item label="Mã" class="">
          <a-input :value="dataDetailStaff?.codeStaff" readonly />
        </a-form-item>

        <a-form-item label="Tên nhân viên" class="">
          <a-input :value="dataDetailStaff?.nameStaff" readonly />
        </a-form-item>

        <a-form-item label="Mail FE" class="">
          <a-input :value="dataDetailStaff?.emailFe" readonly />
        </a-form-item>

        <a-form-item label="Mail FPT" class="">
          <a-input :value="dataDetailStaff?.emailFpt" readonly />
        </a-form-item>
      </a-form>
    </FormCustom>
    <StaffRoleTable
      :data="dataRoleByStaff.dataStaffRole"
      @delete="handleDeleteStaffRole"
      @add="openAddModalRoleByStaff"
      @close="closeModal"
    >
    </StaffRoleTable>

    <FormCustom customClasses="mt-5" label="Chuyên ngành bộ môn cơ sở">
      <template class="flex flex-row-reverse">
        <div>
          <a-button
            type="primary"
            @click="openAddModal"
            class="flex items-center justify-center px-4"
            :disabled="dataStaffDepartmentMajor.length > 0"
          >
            <PlusCircleOutlined />Thêm chuyên ngành
          </a-button>
        </div>
      </template>

      <a-table
        :dataSource="dataStaffDepartmentMajor"
        :columns="columns"
        :pagination="false"
        class="mt-5"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'action'">
            <div class="flex items-center gap-1 justify-center">
              <a-tooltip title="Sửa chuyên ngành bộ môn cơ sở">
                <a-button
                  @click="openViewModal(idStaff as string)"
                  class="flex items-center justify-center w-8 h-8"
                >
                  <FormOutlined />
                </a-button>
              </a-tooltip>

              <a-tooltip title="Xóa chuyên ngành bộ môn cơ sở">
                <a-button
                  @click="handleDelete(record.id)"
                  class="flex items-center justify-center w-8 h-8 cursor-pointer p-2"
                >
                  <DeleteOutlined />
                </a-button>
              </a-tooltip>
            </div>
          </template>
        </template>
      </a-table>

      <label class="text-red-500 text-x"
        >* Mỗi nhân viên chỉ có thể thuộc 1 chuyên ngành bộ môn cơ sở</label
      >
    </FormCustom>
  </BreadcrumbDefaultV1>

  <StaffDetailModal
    :open="data.isModalOpen"
    :staff-id="data.selectedStaffId"
    :title="modalTitle"
    @close="closeModal"
    @success="handleSuccess"
  >
  </StaffDetailModal>

  <StaffRoleModal
    :open="dataRoleByStaff.isModalOpen"
    :title="modalTitleRoleByStaff"
    :staff-id="dataRoleByStaff.selectetRoleByStaffId"
    @close="closeModalRoleByStaff"
    @success="handleSuccessRoleModal"
    @updateRoles="getAllRoleStaff"
  />
</template>

<script setup lang="ts">
const breadcrumbRoutes = [
  { name: 'Quản trị hệ thống', nameRoute: ROUTES_CONSTANTS.ADMIN.name },
  { name: 'Nhân viên ', nameRoute: ROUTES_CONSTANTS.ADMIN.children.STAFF.name },
  { name: 'Chi tiết nhân viên ', nameRoute: ROUTES_CONSTANTS.ADMIN.children.STAFF_DETAIL.name }
]
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import FormCustom from '@/components/custom/Form/FormCustom.vue'
import {
  deleteStaffByRole,
  deleteStaffFacility,
  detailStaff,
  getAllRoleByStaff,
  roleByStaffResponse,
  staffByDepartmentMajor,
  staffDetailResponse,
  staffResponse
} from '@/services/api/admin/staff.api'
import {
  ArrowLeftOutlined,
  DeleteOutlined,
  EyeOutlined,
  FormOutlined,
  PlusCircleOutlined
} from '@ant-design/icons-vue'
import { Modal, TableColumnsType } from 'ant-design-vue'
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import StaffDetailModal from './StaffDetailModal.vue'
import { toast } from 'vue3-toastify'
import StaffRoleTable from './StaffRoleTable.vue'
import StaffRoleModal from './StaffRoleModal.vue'
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
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { library } from '@fortawesome/fontawesome-svg-core'
import BreadcrumbDefault from '@/components/custom/Div/BreadcrumbDefault.vue'
import { ROUTES_CONSTANTS } from '@/constants/path'
import { localStorageAction } from '@/utils/storage'
import { USER_INFO_STORAGE_KEY } from '@/constants/storagekey'
import BreadcrumbDefaultV1 from '@/components/custom/Div/BreadcrumbDefaultV1.vue'
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
const router = useRoute()
const dataDetailStaff = ref<staffResponse>()
const idStaff = ref<String | null>(null)
const dataStaffDepartmentMajor = ref<staffDetailResponse[]>([]) // Khai báo nó là mảng

// Khi có dữ liệu, thêm vào mảng

const data = reactive({
  searchQuery: '',
  isModalOpen: false,
  selectedStaffId: null as string | null,
  dataStaff: [] as staffResponse[],
  page: 1,
  size: 10,
  totalItems: 0
})

const dataRoleByStaff = reactive({
  isModalOpen: false,
  selectetRoleByStaffId: null as string | null,
  dataStaffRole: [] as roleByStaffResponse[]
})

const closeModal = () => {
  data.isModalOpen = false
}

const modalTitle = computed(() => {
  return data.selectedStaffId
    ? 'Cập nhật chuyên ngành bộ môn cơ sở'
    : 'Thêm chuyên ngành bộ môn cơ sở'
})

const openAddModal = () => {
  data.selectedStaffId = null
  data.isModalOpen = true
}

const openViewModal = (id: string) => {
  data.selectedStaffId = id
  data.isModalOpen = true
}

const closeModalRoleByStaff = () => {
  dataRoleByStaff.isModalOpen = false
}

const modalTitleRoleByStaff = computed(() => {
  return dataRoleByStaff.selectetRoleByStaffId
    ? 'Cập nhật role nhân viên'
    : 'Cập nhật role nhân viên'
})

const openAddModalRoleByStaff = () => {
  dataRoleByStaff.selectetRoleByStaffId = idStaff.value as string
  dataRoleByStaff.isModalOpen = true
}

onMounted(() => {
  idStaff.value = router.params.id as string
})

onMounted(async () => {
  const res = await detailStaff(idStaff.value as string)
  dataDetailStaff.value = res.data
})

const handleSuccess = async () => {
  closeModal()
  await getAllRoleStaff()
  await resDataTable()
}

const handleSuccessRoleModal = async () => {
  await getAllRoleStaff() // Cập nhật danh sách vai trò
  // closeModalRoleByStaff()
}

onMounted(async () => {
  // const res = await getAllRoleByStaff(idStaff.value as string)
  // dataRoleByStaff.dataStaffRole = res.data
  getAllRoleStaff()
})

const getAllRoleStaff = async () => {
  const res = await getAllRoleByStaff(idStaff.value as string)
  dataRoleByStaff.dataStaffRole = res.data
}

const resDataTable = async () => {
  try {
    const res = await staffByDepartmentMajor(idStaff.value as string)
    dataStaffDepartmentMajor.value = [res.data]
  } catch (error) {
    if (error.response?.status === 404) {
      dataStaffDepartmentMajor.value = []
    } else {
      toast.error('Lỗi khi tải dữ liệu!')
    }
  }
}

onMounted(async () => {
  const res = await staffByDepartmentMajor(idStaff.value as string)
  dataStaffDepartmentMajor.value = [res.data]
})

const columns: TableColumnsType = [
  {
    title: 'Cơ sở',
    key: 'nameFacility',
    dataIndex: 'nameFacility',
    width: 100,
    align: 'center',
    fixed: 'left'
  },
  {
    title: 'Bộ môn',
    key: 'nameDepartment',
    dataIndex: 'nameDepartment',
    width: 100,
    align: 'center'
  },
  { title: 'Chuyên ngành', key: 'nameMajor', dataIndex: 'nameMajor', width: 100, align: 'center' },
  {
    title: 'Hành động',
    key: 'action',
    width: 60,
    align: 'center',
    fixed: 'right'
  }
]
const userLogin = localStorageAction.get(USER_INFO_STORAGE_KEY)
const handleDelete = async (id) => {
  const emailLogin = userLogin.email
  Modal.confirm({
    title: 'Xác nhận xóa khỏi chuyên ngành cơ sở',
    content: 'Bạn có chắc chắn muốn xóa không?',
    okText: 'Xóa',
    cancelText: 'Hủy',
    okType: 'danger',
    onOk: async () => {
      try {
        await deleteStaffFacility(id, emailLogin)
        toast.success('Xóa thành công!')
        await resDataTable() // Gọi lại bảng
        await getAllRoleStaff()
      } catch (error) {
        console.log(error)
        toast.error('Xóa thất bại!')
      }
    }
  })
}

const handleDeleteStaffRole = async (id) => {
  Modal.confirm({
    title: 'Xác nhận xóa quyền này không',
    content: 'Bạn có chắc chắn muốn xóa không?',
    okText: 'Xóa',
    cancelText: 'Hủy',
    okType: 'danger',
    onOk: async () => {
      try {
        await deleteStaffByRole(id)
        toast.success('Xóa thành công!')
        await getAllRoleStaff()
      } catch (error) {
        toast.error('Xóa thất bại!')
      }
    }
  })
}
</script>

<style scoped>
.ant-btn {
  background-image: var(--selected-header) !important;
  color: var(--selected-text) !important;
  border: none !important;
}

.ant-btn:hover,
.ant-btn:focus {
  background-image: var(--selected-header-hover) !important;
  color: var(--selected-text) !important;
  border: none !important;
}
</style>
