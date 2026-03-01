<template>
  <a-modal :open="open" :title="props.title" @cancel="closeModal">
    <template #footer>
      <a-button @click="closeModal">Huỷ</a-button>

      <a-popconfirm
        title="Bạn có chắc chắn muốn lưu thay đổi?"
        @confirm="handleSubmit"
        ok-text="Đồng ý"
        cancel-text="Huỷ"
      >
        <a-button type="primary">Xác nhận</a-button>
      </a-popconfirm>
    </template>
    <a-form :model="staff" ref="staffForm" name="staffForm" autocomplete="off">
      <a-form-item
        label="Mã nhân viên"
        name="codeStaff"
        :label-col="{ span: 24 }"
        :rules="rules.code"
      >
        <a-input v-if="staff" v-model:value="staff.codeStaff" />
      </a-form-item>
      <a-form-item
        label="Tên nhân viên"
        name="nameStaff"
        :label-col="{ span: 24 }"
        :rules="rules.name"
      >
        <a-input v-if="staff" v-model:value="staff.nameStaff" />
      </a-form-item>
      <a-form-item label="Email FE" name="emailFe" :label-col="{ span: 24 }" :rules="rules.emailFe">
        <a-input v-if="staff" v-model:value="staff.emailFe" />
      </a-form-item>
      <a-form-item
        label="Email FPT"
        name="emailFpt"
        :label-col="{ span: 24 }"
        :rules="rules.emailFpt"
      >
        <a-input v-if="staff" v-model:value="staff.emailFpt" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, watch, defineProps, defineEmits, onMounted } from 'vue'
import { toast } from 'vue3-toastify'
import axios from 'axios'
import {
  createStaff,
  detailStaff,
  getAllStaffRole,
  roleStaffResponse,
  staffResponse,
  updateStaff
} from '@/services/api/admin/staff.api'
import { localStorageAction } from '@/utils/storage'
import { USER_INFO_STORAGE_KEY } from '@/constants/storagekey'

const props = defineProps<{ open: boolean; staffId: string | null; title: string }>()
const emit = defineEmits(['close', 'success'])

const staff = ref<staffResponse>()
const staffForm = ref()
const staffRole = ref<roleStaffResponse[]>()
const idRole = ref<string>('')

const rules = {
  code: [
    { required: true, message: 'Mã không được để trống!', trigger: 'blur' },
    {
      min: 3,
      max: 50,
      message: 'Mã phải từ 3 đến 50 ký tự!',
      trigger: 'blur'
    },
    {
      validator: (_, value, callback) => {
        const trimmed = value?.trim()
        const regex = /^[a-zA-Z0-9_-]+$/
        if (!regex.test(trimmed)) {
          callback(
            new Error(
              'Mã chỉ được chứa chữ cái, số, dấu gạch dưới (_) hoặc dấu gạch ngang (-), không chứa khoảng trắng!'
            )
          )
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  name: [
    { required: true, message: 'Tên không được để trống!', trigger: 'blur' },
    { min: 3, max: 100, message: 'Tên phải từ 3 đến 100 ký tự!', trigger: 'blur' },
    {
      pattern: /^[a-zA-ZÀ-Ỹà-ỹ\s\u00C0-\u1EF9]+$/,
      message: 'Tên nhân viên không được chứa số hoặc ký tự đặc biệt!',
      trigger: 'blur'
    }
  ],

  emailFpt: [
    { required: true, message: 'Email FPT không được để trống!', trigger: 'blur' },
    { max: 100, message: 'Email FPT không được vượt quá 100 ký tự!', trigger: 'blur' },
    {
      pattern: /^[A-Za-z0-9._%+-]+@(fpt\.edu\.vn|gmail\.com)$/,
      message: 'Email phải có định dạng @fpt.edu.vn hoặc @gmail.com!',
      trigger: ['blur', 'change']
    }
  ],
  emailFe: [
    { required: true, message: 'Email FE không được để trống!', trigger: 'blur' },
    { max: 100, message: 'Email FE không được vượt quá 100 ký tự!', trigger: 'blur' },
    {
      pattern: /^[A-Za-z0-9._%+-]+@(fe\.edu\.vn|gmail\.com)$/,
      message: 'Email phải có định dạng @fe.edu.vn hoặc @gmail.com!',
      trigger: ['blur', 'change']
    }
  ]
}

onMounted(() => {
  dataRole()
})

const fetchStaffDetails = async (id: string) => {
  try {
    const response = await detailStaff(id)
    staff.value = response.data
    idRole.value = response.data.idRole as string
    idRole.value = staff.value.idRole as string
  } catch (error) {
    if (axios.isAxiosError(error)) {
      if (error?.response?.data?.message) {
        toast.error(error?.response?.data?.message)
      }
    }
  }
}

const dataRole = async () => {
  idRole.value = ''
  try {
    const response = await getAllStaffRole()
    staffRole.value = response.data as []
  } catch (error) {
    if (axios.isAxiosError(error)) {
      if (error?.response?.data?.message) {
        toast.error(error?.response?.data?.message)
      }
    }
  }
}

watch(
  () => [props.staffId, props.open],
  async ([newId, isOpen]) => {
    if (isOpen) {
      if (staffForm.value) {
        staffForm.value.resetFields()
      }
      if (newId) {
        await fetchStaffDetails(newId as string)
      } else {
        staff.value = {
          codeStaff: '',
          nameStaff: '',
          emailFe: '',
          emailFpt: '',
          nameRole: '',
          idRole: ''
        }
        idRole.value = ''
      }
    }
  },
  { immediate: true }
)

const userLogin = localStorageAction.get(USER_INFO_STORAGE_KEY)
const handleSubmit = async () => {
  const emailLogin = userLogin.email
  try {
    await staffForm.value.validate()
    const formData = new FormData()
    formData.append('code', staff?.value?.codeStaff?.trim() || '')
    formData.append('name', staff?.value?.nameStaff?.trim() || '')
    formData.append('emailFe', staff?.value?.emailFe?.trim() || '')
    formData.append('emailFpt', staff?.value?.emailFpt?.trim() || '')
    formData.append('emailLogin', emailLogin)
    if (props.staffId == null) {
      // for (let i = 0; i < 100; i++) {
      //   const staffNew = {
      //     name: `Tuấn ${i + 1}`,
      //     code: `NV${i + 1}`,
      //     emailFe: `tuan${i + 1}@fe.edu.vn`,
      //     emailFpt: `tuan${i + 1}@fpt.edu.vn`,
      //     idRole: ``
      //   }
      //   const formData = new FormData()
      //   formData.append('code', staffNew.code)
      //   formData.append('name', staffNew.name)
      //   formData.append('emailFe', staffNew.emailFe)
      //   formData.append('emailFpt', staffNew.emailFpt)
      //   const res = await createStaff(formData)
      //   toast.success(res.message)
      // }
      const res = await createStaff(formData)
      toast.success(res.message)
    } else {
      const res = await updateStaff(props.staffId, formData)
      toast.success(res.message)
    }
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
