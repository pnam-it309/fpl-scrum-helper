<template>
  <a-modal :open="open" :title="title" @cancel="closeModal" style="top: 20px">
    <template #footer>
      <a-popconfirm
        title="Bạn có chắc chắn muốn lưu thay đổi?"
        @confirm="handleSubmit"
        ok-text="Đồng ý"
        cancel-text="Huỷ"
      >
        <a-button type="primary">Xác nhận</a-button>
      </a-popconfirm>
      <a-button @click="closeModal()">Huỷ</a-button>
    </template>

    <a-form :model="department" ref="departmentForm" name="departmentForm" autocomplete="off">
      <a-form-item label="Mã bộ môn" name="departmentCode" :rules="rules.departmentCode">
        <a-input v-model:value="department.departmentCode" />
      </a-form-item>
      <a-form-item label="Tên bộ môn" name="departmentName" :rules="rules.departmentName">
        <a-input v-model:value="department.departmentName" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, watch, defineProps, defineEmits } from 'vue'
import { getDepartment, addDepartment, updateDepartment } from '@/services/api/department.api'
import { toast } from 'vue3-toastify'
import dayjs from 'dayjs'

const props = defineProps<{ open: boolean; departmentId: string | null; title: string }>()
const emit = defineEmits(['close', 'success'])

const department = ref({
  departmentCode: '',
  departmentName: '',
  departmentStatus: 0
})
const departmentForm = ref()

const rules = {
  departmentCode: [
    { required: true, message: 'Mã bộ môn không được để trống!' },
    {
      validator: (_, value) => {
        if (!value) {
          return Promise.resolve() // Bỏ qua nếu giá trị rỗng (đã có rule 'required' xử lý)
        }
        const trimmedValue = value.trim()
        if (trimmedValue.length < 3 || trimmedValue.length > 50) {
          return Promise.reject('Mã bộ môn phải từ 3 - 50 ký tự!')
        }
        // Kiểm tra khoảng trắng ở giữa
        if (trimmedValue.includes(' ')) {
          return Promise.reject('Mã bộ môn không được chứa khoảng trắng ở giữa!')
        }
        // Kiểm tra ký tự đặc biệt (chỉ cho phép chữ, số)
        if (!/^[a-zA-Z0-9]+$/.test(trimmedValue)) {
          return Promise.reject('Mã bộ môn không được chứa ký tự đặc biệt!')
        }
        return Promise.resolve()
      }
    }
  ],
  departmentName: [
    { required: true, message: 'Tên bộ môn không được để trống!' },
    {
      // Sử dụng validator tùy chỉnh để kiểm tra độ dài sau khi trim
      validator: (_, value) => {
        if (!value) {
          return Promise.resolve() // Bỏ qua nếu giá trị rỗng
        }
        const trimmedValue = value.trim()
        if (trimmedValue.length < 3 || trimmedValue.length > 50) {
          return Promise.reject('Tên bộ môn phải từ 3 - 50 ký tự!')
        }
        // Kiểm tra ký tự đặc biệt (cho phép chữ, số, tiếng Việt và khoảng trắng)
        if (!/^[a-zA-ZÀ-Ỹà-ỹ0-9\s\u00C0-\u1EF9]+$/.test(value)) {
          // dùng 'value' gốc để regex cho phép khoảng trắng ở giữa
          return Promise.reject('Tên bộ môn không được chứa ký tự đặc biệt!')
        }
        return Promise.resolve()
      }
    }
  ],
  departmentStatus: [{ required: true, message: 'Trạng thái không được để trống!' }]
}

const fetchDepartmentDetails = async (id: string) => {
  try {
    const response = await getDepartment(id)
    department.value = {
      departmentCode: response.data.departmentCode,
      departmentName: response.data.departmentName,
      departmentStatus: response.data.departmentStatus
    }
  } catch (error) {
    console.error('Lỗi khi lấy thông tin bộ môn:', error)
  }
}

watch(
  () => props.departmentId,
  async (newId) => {
    if (props.open && newId) {
      await fetchDepartmentDetails(newId)
    } else {
      department.value = { departmentCode: '', departmentName: '', departmentStatus: 0 }
    }
  },
  { immediate: true }
)

watch(
  () => props.open,
  async (open) => {
    if (open && props.departmentId) {
      await fetchDepartmentDetails(props.departmentId)
    }
  }
)

const closeModal = () => {
  department.value = {
    departmentCode: '',
    departmentName: '',
    departmentStatus: 0
  }
  departmentForm.value?.resetFields()
  emit('close')
}

const handleSubmit = async () => {
  try {
    await departmentForm.value.validate()
    const formData = {
      departmentId: props.departmentId || '',
      departmentCode: department.value.departmentCode.trim(),
      departmentName: department.value.departmentName.trim(),
      departmentStatus: department.value.departmentStatus,
      createdDate: dayjs().valueOf()
    }

    if (props.departmentId) {
      await updateDepartment(formData, props.departmentId)
      toast.success('Cập nhật thành công!')
    } else {
      await addDepartment(formData)
      toast.success('Thêm thành công!')
    }
    closeModal()
    emit('success')
  } catch (error) {
    const message =
      error?.response?.data?.message || (props.departmentId ? 'Cập nhật thất bại' : 'Thêm thất bại')
    toast.error(message)
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
