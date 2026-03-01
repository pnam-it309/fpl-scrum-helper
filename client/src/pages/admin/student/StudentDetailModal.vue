<template>
  <a-modal
    :open="open"
    :title="props.title"
    @cancel="closeModal"
    :maskClosable="true"
    :centered="true"
  >
    <template #footer>
      <a-popconfirm
        title="Bạn có chắc chắn muốn lưu thay đổi?"
        @confirm="handleSubmit"
        ok-text="Đồng ý"
        cancel-text="Huỷ"
      >
        <a-button type="primary">Xác nhận</a-button>
      </a-popconfirm>
      <a-button @click="closeModal">Huỷ</a-button>
    </template>
    <FormCustom>
      <a-form
        :model="studentDetail"
        ref="studentForm"
        name="studentForm"
        autocomplete="off"
        class="space-y-4 p-4"
      >
        <div class="flex flex-col">
          <label class="font-medium mb-1">Chọn cơ sở</label>
          <a-select
            v-model:value="idFacilitySelect"
            placeholder="Chọn cơ sở"
            name="facility"
            :rules="rules.facility"
          >
            <a-select-option
              v-for="facility in studentDetailFacility"
              :key="facility.id"
              :value="facility.id"
            >
              {{ facility.name }}
            </a-select-option>
          </a-select>
        </div>

        <!-- Chọn bộ môn -->
        <div class="flex flex-col">
          <label class="font-medium mb-1">Chọn bộ môn</label>
          <a-select
            v-model:value="idDepartmentSelect"
            placeholder="Chọn bộ môn"
            name="department"
            :rules="rules.department"
          >
            <a-select-option
              v-for="item in studentDetailDepartment"
              :key="item.idDepartment"
              :value="item.idDepartment"
            >
              {{ item.nameDepartment }}
            </a-select-option>
          </a-select>
        </div>

        <!-- Chọn chuyên ngành -->
        <div class="flex flex-col">
          <label class="font-medium mb-1">Chọn chuyên ngành</label>
          <a-select
            v-model:value="idMajorSelect"
            placeholder="Chọn chuyên ngành"
            name="major"
            :rules="rules.major"
          >
            <a-select-option
              v-for="dataMajor in studentDetailMajor"
              :key="dataMajor.idMajor"
              :value="dataMajor.idMajor"
            >
              {{ dataMajor.nameMajor }}
            </a-select-option>
          </a-select>
        </div>
      </a-form>
    </FormCustom>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, watch, defineProps, defineEmits, onMounted, reactive } from 'vue'
import { toast } from 'vue3-toastify'
import axios from 'axios'
import {
  createStaffFDM,
  getAllStaffDepartment,
  getAllStaffFacility,
  getAllStaffMajor,
  staffByDepartmentMajor,
  staffDetailIdResponse,
  updateStaffFDm
} from '@/services/api/admin/staff.api'
import FormCustom from '@/components/custom/Form/FormCustom.vue'
import { useRoute } from 'vue-router'
import {
  createStudentFDM,
  studentByDepartmentMajor,
  updateStudentFDm
} from '@/services/api/admin/student.api'
import { localStorageAction } from '@/utils/storage'
import { USER_INFO_STORAGE_KEY } from '@/constants/storagekey'

const props = defineProps<{
  open: boolean
  studentId: string | null
  title: string
}>()
const emit = defineEmits(['close', 'success'])

const studentDetailFacility = ref<staffDetailIdResponse[]>([])
const studentDetailDepartment = ref<staffDetailIdResponse[]>()
const studentDetailMajor = ref<staffDetailIdResponse[]>()
const studentDetail = ref<staffDetailIdResponse>()
const studentForm = ref()
const idFacilitySelect = ref<string>('')
const idDepartmentSelect = ref<string>('')
const idMajorSelect = ref<string>('')
const idStudent = ref()
const router = useRoute()
const idFacilitySelectUpdate = ref<string>('')
const idDepartmentSelectUpdate = ref<string>('')
const idMajorSelectUpdate = ref<string>('')
const rules = {
  facility: [
    { required: true, message: 'Mã không được để trống!', trigger: 'blur' },
    { max: 50, message: 'Mã không được vượt quá 50 ký tự!', trigger: 'blur' }
  ],
  major: [
    { required: true, message: 'Tên không được để trống!', trigger: 'blur' },
    { max: 100, message: 'Tên không được vượt quá 100 ký tự!', trigger: 'blur' }
  ],
  department: [
    { required: true, message: 'Tên không được để trống!', trigger: 'blur' },
    { max: 100, message: 'Tên không được vượt quá 100 ký tự!', trigger: 'blur' }
  ]
}

onMounted(() => {
  fetchStudentDetailFacility()
})

const fetchStudentDetailFacility = async () => {
  try {
    const response = await getAllStaffFacility()
    studentDetailFacility.value = response.data as []
  } catch (error) {
    if (axios.isAxiosError(error)) {
      if (error?.response?.data?.message) {
        toast.error(error?.response?.data?.message)
      }
    }
  }
}

const fetchStudentDetailDepartment = async (id: string) => {
  idDepartmentSelect.value = ''
  try {
    const response = await getAllStaffDepartment(id)
    studentDetailDepartment.value = response.data as []
  } catch (error) {
    if (axios.isAxiosError(error)) {
      if (error?.response?.data?.message) {
        toast.error(error?.response?.data?.message)
      }
    }
  }
}

const fetchStudentDetailMajor = async (id: string) => {
  idMajorSelect.value = ''
  try {
    const response = await getAllStaffMajor(id)
    studentDetailMajor.value = response.data as []
  } catch (error) {
    if (axios.isAxiosError(error)) {
      if (error?.response?.data?.message) {
        toast.error(error?.response?.data?.message)
      }
    }
  }
}

watch(idFacilitySelect, (newValue, oldValue) => {
  console.log(newValue)
  fetchStudentDetailDepartment(newValue as any)
})

watch(idDepartmentSelect, (newValue, oldValue) => {
  fetchStudentDetailMajor(newValue as any)
  console.log(newValue)
})

watch(
  () => [props.studentId, props.open],
  async ([newId, isOpen]) => {
    if (isOpen) {
      if (studentForm.value) {
        studentForm.value.resetFields()
      }
      if (newId) {
        const data = await studentByDepartmentMajor(newId as string)
        idFacilitySelect.value = data.data.idFacility as string
        idFacilitySelectUpdate.value = data.data.idFacility as string
        await fetchStudentDetailDepartment(data.data.idFacility as string)
        idDepartmentSelect.value = data.data.idDepartment as string
        idDepartmentSelectUpdate.value = data.data.idDepartment as string
        await fetchStudentDetailMajor(data.data.idDepartment as string)
        idMajorSelect.value = data.data.idMajor as string
        idMajorSelectUpdate.value = data.data.idMajor as string
      } else {
        studentDetail.value = { idMajor: '', idDepartment: '', idFacility: '' }
      }
    }
  },
  { immediate: true }
)

const resertForm = () => {
  if (studentForm.value) {
    studentForm.value.resetFields()
  }
  idFacilitySelect.value = ''
  idDepartmentSelect.value = ''
  idMajorSelect.value = ''
}

const userLogin = localStorageAction.get(USER_INFO_STORAGE_KEY)
const handleSubmit = async () => {
  try {
    const emailLogin = userLogin.email
    const formData = new FormData()
    formData.append('idFacility', idFacilitySelect.value as string)
    formData.append('idDepartment', idDepartmentSelect.value as string)
    formData.append('idMajor', idMajorSelect.value as string)
    formData.append('idStudentDetail', router.params.id as string)
    formData.append('emailLogin', emailLogin)
    console.log(idFacilitySelectUpdate.value + ' co ưo')
    console.log(idDepartmentSelectUpdate.value + 'bo mon')
    console.log(idMajorSelectUpdate.value + 'chuyen naga ')
    const formDataUpdate = new FormData()
    formDataUpdate.append('idFacility', idFacilitySelectUpdate.value as string)
    formDataUpdate.append('idDepartment', idDepartmentSelectUpdate.value as string)
    formDataUpdate.append('idMajor', idMajorSelectUpdate.value as string)
    formDataUpdate.append('idStudentDetail', router.params.id as string)
    formDataUpdate.append('idUpdateFacility', idFacilitySelect.value as string)
    formDataUpdate.append('idUpdateDepartment', idDepartmentSelect.value as string)
    formDataUpdate.append('idUpdateMajor', idMajorSelect.value as string)
    formDataUpdate.append('emailLogin', emailLogin)
    if (props.studentId == null) {
      const res = await createStudentFDM(formData)
      toast.success(res.message)
    } else {
      const res = await updateStudentFDm(formDataUpdate)
      toast.success(res.message)
    }
    resertForm()
    closeModal()
    emit('success')
  } catch (error) {
    if (axios.isAxiosError(error)) {
      if (error?.response?.data?.message) {
        toast.error(error?.response?.data?.message)
      }
    }
  }
}

const closeModal = () => emit('close')
</script>
