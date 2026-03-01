<template>
  <BreadcrumbDefaultV1 label="Chi tiết sinh viên" :routes="breadcrumbRoutes">
    <FormCustom label="Thông tin sinh viên" class="mt-2">
      <a-form layout="vertical" class="mb-3 grid grid-cols-1 md:grid-cols-3 gap-3 p-5">
        <a-form-item label="Mã" class="">
          <a-input :value="dataDetailStudent?.code" readonly />
        </a-form-item>

        <a-form-item label="Tên sinh viên" class="">
          <a-input :value="dataDetailStudent?.name" readonly />
        </a-form-item>

        <a-form-item label="Email" class="">
          <a-input :value="dataDetailStudent?.email" readonly />
        </a-form-item>
      </a-form>
    </FormCustom>

    <FormCustom customClasses="mt-5" label="Chuyên ngành bộ môn cơ sở">
      <template class="flex flex-row-reverse">
        <div>
          <a-button
            type="primary"
            @click="openAddModal"
            class="flex items-center justify-center px-4"
            :disabled="dataStudentDepartmentMajor.length > 0"
          >
            <PlusCircleOutlined /> Thêm chuyên ngành
          </a-button>
        </div>
      </template>

      <a-table
        :dataSource="dataStudentDepartmentMajor"
        :columns="columns"
        class="mt-5"
        :pagination="false"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'action'">
            <div class="flex items-center gap-1 justify-center">
              <a-tooltip title="Sửa chuyên ngành bộ môn cơ sở">
                <a-button
                  @click="openViewModal(idStudent as string)"
                  class="flex items-center justify-center w-8 h-8"
                >
                  <FormOutlined />
                </a-button>
              </a-tooltip>

              <a-tooltip title="Xóa chuyên ngành bộ môn cơ sở">
                <a-button
                  @click="handleDelete(idStudent as string)"
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
        >* mỗi sinh viên chỉ có thể thuộc 1 chuyên ngành bộ môn cơ sở</label
      >
    </FormCustom>
  </BreadcrumbDefaultV1>
  <StudentDetailModal
    :open="data.isModalOpen"
    :studentId="data.selectedStudentId"
    :title="modalTitle"
    @close="closeModal"
    @success="handleSuccess"
  >
  </StudentDetailModal>
</template>

<script setup lang="ts">
const breadcrumbRoutes = [
  { name: 'Quản trị hệ thống', nameRoute: ROUTES_CONSTANTS.ADMIN.name },
  { name: 'Sinh viên ', nameRoute: ROUTES_CONSTANTS.ADMIN.children.STUDENT.name },
  { name: 'Chi tiết sinh viên ', nameRoute: ROUTES_CONSTANTS.ADMIN.children.STUDENT.name },
]
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import FormCustom from '@/components/custom/Form/FormCustom.vue'
import {
  deleteStudentFacility,
  detailStudent,
  studentByDepartmentMajor,
  studentDetailResponse,
  StudentResponse
} from '@/services/api/admin/student.api'
import {
  PlusCircleOutlined,
  DeleteOutlined,
  EditOutlined,
  FormOutlined
} from '@ant-design/icons-vue'
import { Modal, TableColumnsType } from 'ant-design-vue'
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { toast } from 'vue3-toastify'
import StudentDetailModal from './StudentDetailModal.vue'
const router = useRoute()
const dataDetailStudent = ref<StudentResponse>()
const idStudent = ref<String | null>(null)
const dataStudentDepartmentMajor = ref<studentDetailResponse[]>([])
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
import { localStorageAction } from '@/utils/storage'
import { USER_INFO_STORAGE_KEY } from '@/constants/storagekey'
import { ROUTES_CONSTANTS } from '@/constants/path'
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
// Khi có dữ liệu, thêm vào mảng
const data = reactive({
  searchQuery: '',
  isModalOpen: false,
  selectedStudentId: null as string | null,
  dataStaff: [] as StudentResponse[],
  page: 1,
  size: 10,
  totalItems: 0
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

const modalTitle = computed(() => {
  return data.selectedStudentId
    ? 'Cập nhật chuyên ngành bộ môn cơ sở'
    : 'Thêm chuyên ngành bộ môn cơ sở'
})

const closeModal = () => {
  data.isModalOpen = false
}

const handleSuccess = async () => {
  closeModal()
  await resDataTable()
}

const resDataTable = async () => {
  try {
    const res = await studentByDepartmentMajor(idStudent.value as string)
    dataStudentDepartmentMajor.value = [res.data]
  } catch (error) {
    if (error.response?.status === 404) {
      dataStudentDepartmentMajor.value = []
    } else {
      toast.error('Lỗi khi tải dữ liệu!')
    }
  }
}

const openViewModal = (id: string) => {
  data.selectedStudentId = id
  data.isModalOpen = true
}

const openAddModal = () => {
  data.selectedStudentId = null
  data.isModalOpen = true
}
const userLogin = localStorageAction.get(USER_INFO_STORAGE_KEY)
const handleDelete = async (id: string) => {
  const emailLogin = userLogin.email
  Modal.confirm({
    title: 'Xác nhận xóa khỏi chuyên ngành cơ sở',
    content: 'Bạn có chắc chắn muốn xóa không?',
    okText: 'Xóa',
    cancelText: 'Hủy',
    okType: 'danger',
    onOk: async () => {
      try {
        await deleteStudentFacility(id, emailLogin)
        await resDataTable()
        toast.success('Xóa thành công!')
      } catch (error) {
        toast.error('Xóa thất bại!')
      }
    }
  })
}

onMounted(() => {
  idStudent.value = router.params.id as string
})

onMounted(async () => {
  const res = await detailStudent(idStudent.value as string)
  dataDetailStudent.value = res.data
})

onMounted(async () => {
  resDataTable()
})
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
