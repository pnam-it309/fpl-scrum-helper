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
      <a-button @click="closeModal">Huỷ</a-button>
    </template>

    <a-form :model="major" ref="majorForm" name="majorForm" autocomplete="off">
      <a-form-item label="Mã chuyên ngành" name="majorCode" :rules="rules.majorCode">
        <a-input v-model:value="major.majorCode" />
      </a-form-item>
      <a-form-item label="Tên chuyên ngành" name="majorName" :rules="rules.majorName">
        <a-input v-model:value="major.majorName" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, watch, defineProps, defineEmits } from 'vue'
import { getMajor, addMajor, updateMajor } from '@/services/api/major.api'
import { toast } from 'vue3-toastify'
import dayjs from 'dayjs'

const props = defineProps<{ open: boolean; majorId: string; departmentId: string; title: string }>()
const emit = defineEmits(['close', 'success'])

const major = ref({
  majorCode: '',
  majorName: '',
  majorStatus: '0',
  departmentId: props.departmentId
})

const majorForm = ref()

// const rules = {
//   majorCode: [
//     { required: true, message: 'Mã chuyên ngành không được để trống!' },
//     { min: 3, max: 50, message: 'Mã chuyên ngành phải từ 3 - 50 ký tự!' },
//     { pattern: /^[a-zA-Z0-9]+$/, message: 'Mã chuyên ngành không được chứa ký tự đặc biệt!' }
//   ],

//   majorName: [
//     { required: true, message: 'Tên chuyên ngành không được để trống!' },
//     { min: 3, max: 50, message: 'Tên chuyên ngành phải từ 3 - 50 ký tự!' },
//     {
//       pattern: /^[a-zA-ZÀ-Ỹà-ỹ\s]+$/, // chữ cái có dấu và khoảng trắng
//       message:
//         'Tên chuyên ngành chỉ được chứa chữ cái và khoảng trắng, không được chứa số hoặc ký tự đặc biệt!'
//     }
//   ]
// }

const rules = {
  majorCode: [
    { required: true, message: 'Mã chuyên ngành không được để trống!' },
    {
      // Sử dụng validator tùy chỉnh để kiểm tra độ dài sau khi trim
      validator: (_, value) => {
        if (!value) {
          return Promise.resolve() // Bỏ qua nếu giá trị rỗng (đã có rule 'required' xử lý)
        }
        const trimmedValue = value.trim()
        if (trimmedValue.length < 3 || trimmedValue.length > 50) {
          return Promise.reject('Mã chuyên ngành phải từ 3 - 50 ký tự!')
        }
        // Kiểm tra khoảng trắng ở giữa
        if (trimmedValue.includes(' ')) {
          return Promise.reject('Mã chuyên ngành không được chứa khoảng trắng ở giữa!')
        }
        // Kiểm tra ký tự đặc biệt (chỉ cho phép chữ, số)
        if (!/^[a-zA-Z0-9]+$/.test(trimmedValue)) {
          return Promise.reject('Mã chuyên ngành không được chứa ký tự đặc biệt!')
        }
        return Promise.resolve()
      }
    }
  ],
  majorName: [
    { required: true, message: 'Tên chuyên ngành không được để trống!' },
    {
      // Sử dụng validator tùy chỉnh để kiểm tra độ dài sau khi trim
      validator: (_, value) => {
        if (!value) {
          return Promise.resolve() // Bỏ qua nếu giá trị rỗng
        }
        const trimmedValue = value.trim()
        if (trimmedValue.length < 3 || trimmedValue.length > 50) {
          return Promise.reject('Tên chuyên ngành phải từ 3 - 50 ký tự!')
        }
        // Kiểm tra ký tự đặc biệt (cho phép chữ, số, tiếng Việt và khoảng trắng)
        if (!/^[a-zA-ZÀ-Ỹà-ỹ0-9\s\u00C0-\u1EF9]+$/.test(value)) {
          // dùng 'value' gốc để regex cho phép khoảng trắng ở giữa
          return Promise.reject('Tên chuyên ngành không được chứa ký tự đặc biệt!')
        }
        return Promise.resolve()
      }
    }
  ],
  departmentStatus: [{ required: true, message: 'Trạng thái không được để trống!' }]
}

const fetchMajorDetails = async (id: string) => {
  try {
    const response = await getMajor(id)
    major.value = {
      majorCode: response.data.majorCode,
      majorName: response.data.majorName,
      majorStatus: response.data.majorStatus,
      departmentId: props.departmentId
    }
  } catch (error) {
    console.error('Lỗi khi lấy thông tin chuyên ngành:', error)
  }
}

watch(
  () => props.majorId,
  async (newId) => {
    if (newId) {
      await fetchMajorDetails(newId)
    } else {
      resetForm() 
    }
  },
  { immediate: true }
)


const closeModal = () => {
  if (props.majorId) {
    fetchMajorDetails(props.majorId)
  } else {
    resetForm()
  }
  emit('close')
}


const resetForm = () => {
  major.value = {
    majorCode: '',
    majorName: '',
    majorStatus: '0',
    departmentId: props.departmentId
  }
  majorForm.value?.resetFields()
}

const handleSubmit = async () => {
  try {
    await majorForm.value.validate()
    const formData = {
      majorId: props.majorId || '',
      majorCode: major.value.majorCode,
      majorName: major.value.majorName,
      majorStatus: major.value.majorStatus,
      departmentId: props.departmentId,
      createdDate: dayjs().valueOf()
    }

    if (props.majorId) {
      await updateMajor(formData, props.majorId)
      toast.success('Cập nhật thành công!')
    } else {
      await addMajor(formData)
      toast.success('Thêm thành công!')
      resetForm()
    }
    closeModal()
    emit('success')
  } catch (error) {
    if (props.majorId) {
      toast.error(error.response?.data?.message || error.message || 'Cập nhật thất bại')
    } else {
      toast.error(error.response?.data?.message || error.message || 'Thêm thất bại')
    }
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
