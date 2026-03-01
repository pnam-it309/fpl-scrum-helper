<template>
  <a-modal :open="open" :title="title" @cancel="closeModal" :maskClosable="true" :centered="true">
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

    <a-form :model="student" ref="StudentForm" name="StudentForm" autocomplete="off">
      <a-form-item
        label="Mã sinh viên"
        name="code"
        :rules="rules.code"
        :labelCol="{ style: { width: '150px' } }"
      >
        <a-input v-model:value="student.code" />
      </a-form-item>
      <a-form-item
        label="Tên sinh viên"
        name="name"
        :rules="rules.name"
        :labelCol="{ style: { width: '150px' } }"
      >
        <a-input v-model:value="student.name" />
      </a-form-item>
      <a-form-item
        label="Email"
        name="email"
        :rules="rules.email"
        :labelCol="{ style: { width: '150px' } }"
      >
        <a-input v-model:value="student.email" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, watch, defineProps, defineEmits } from 'vue'
import { toast } from 'vue3-toastify'
import dayjs from 'dayjs'
import { addStudent, detailStudent, updateStudent } from '@/services/api/admin/student.api'
import { localStorageAction } from '@/utils/storage'
import { USER_INFO_STORAGE_KEY } from '@/constants/storagekey'

const props = defineProps<{ open: boolean; id: string | null; title: string }>()
const emit = defineEmits(['close', 'success'])

const student = ref({
  code: '',
  name: '',
  email: '',
  status: ''
})
const StudentForm = ref()

const rules = {
  code: [
    { required: true, message: 'Mã không được để trống!', trigger: 'blur' },
    {
      validator: (_, value, callback) => {
        const trimmed = value?.trim() || ''
        const regex = /^[a-zA-Z0-9_-]+$/

        if (trimmed.length === 0) {
          callback(new Error('Mã không được chỉ chứa khoảng trắng!'))
        } else if (trimmed.length < 3 || trimmed.length > 50) {
          callback(new Error('Mã phải từ 3 đến 50 ký tự sau khi loại bỏ khoảng trắng!'))
        } else if (!regex.test(trimmed)) {
          callback(
            new Error(
              'Mã chỉ được chứa chữ cái, số, dấu gạch dưới (_) hoặc dấu gạch ngang (-), không chứa khoảng trắng hoặc ký tự đặc biệt!'
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
    {
      validator: (_, value, callback) => {
        const trimmed = value?.trim() || ''
        const regex = /^[a-zA-ZÀ-Ỹà-ỹ\s\u00C0-\u1EF9]+$/

        if (trimmed.length === 0) {
          callback(new Error('Tên không được chỉ chứa khoảng trắng!'))
        } else if (trimmed.length < 3 || trimmed.length > 100) {
          callback(new Error('Tên phải từ 3 đến 100 ký tự sau khi loại bỏ khoảng trắng!'))
        } else if (!regex.test(trimmed)) {
          callback(new Error('Tên sinh viên không được chứa số hoặc ký tự đặc biệt!'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  email: [
    { required: true },
    {
      validator: (_, value, callback) => {
        const trimmed = value?.trim() || ''
        const regex = /^[A-Za-z0-9](\.?[A-Za-z0-9_%+-])*@(fpt\.edu\.vn|gmail\.com)$/

        if (!trimmed) {
          callback(new Error('Email không được để trống!'))
          return
        }

        if (trimmed.length > 100) {
          console.log('Email vượt quá 100 ký tự!')
          callback(new Error('Email FPT không được vượt quá 100 ký tự!'))
          return
        }

        if (!regex.test(trimmed)) {
          console.log('Email sai định dạng!')
          callback(new Error('Email sai định dạng @fpt.edu.vn hoặc @gmail.com!'))
          return
        }

        console.log('✅ Email hợp lệ:', trimmed)
        callback()
      },
      trigger: ['blur', 'change']
    }
  ],

  status: [{ required: true, message: 'Trạng thái không được để trống!' }]
}

const fetchStudentDetails = async (id: string) => {
  try {
    const response = await detailStudent(id)
    student.value = {
      code: response.data.code,
      name: response.data.name,
      email: response.data.email,
      status: response.data.status
    }
  } catch (error) {
    console.error('Lỗi khi lấy thông tin sinh viên:', error)
  }
}

watch(
  () => props.id,
  async (newId) => {
    if (newId) {
      await fetchStudentDetails(newId)
    } else {
      student.value = { code: '', name: '', status: '', email: '' }
    }
  },
  { immediate: true }
)

const closeModal = () => {
  resetForm()
  emit('close')
}

const resetForm = () => {
  student.value = {
    code: '',
    name: '',
    status: '',
    email: ''
  }
  StudentForm.value?.resetFields()
}
const userLogin = localStorageAction.get(USER_INFO_STORAGE_KEY)
const handleSubmit = async () => {
  try {
    const emailLogin = userLogin.email
    await StudentForm.value.validate()
    const formData = {
      id: props.id || '',
      code: student.value.code.trim(),
      name: student.value.name.trim(),
      stuaus: student.value.status,
      email: student.value.email,
      emailLogin: emailLogin,
      createdDate: dayjs().valueOf()
    }

    if (props.id) {
      try {
        const res = await updateStudent(formData, props.id)
        toast.success('Cập nhật thành công!')
      } catch (error) {
        toast.error(error.response?.data?.message || error.message || 'Cập nhật thất bại')
      }
    } else {
      try {
        await addStudent(formData)
        toast.success('Thêm thành công!')
      } catch (error) {
        toast.error(error.response?.data?.message || error.message || 'Thêm thất bại')
      }
    }
    resetForm()
    closeModal()
    emit('success')
  } catch (error) {
    toast.error('Lưu thất bại, vui lòng kiểm tra lại dữ liệu!')
  }
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
