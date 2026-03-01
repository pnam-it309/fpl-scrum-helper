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

    <a-form :model="category" ref="categoryForm" name="categoryForm" autocomplete="off">
      <a-form-item label="Tên thể loại" name="categoryName" :rules="rules.categoryName">
        <a-input v-model:value="category.categoryName" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, watch, defineProps, defineEmits } from 'vue'
import {
  getByIdCategory,
  createCategory,
  updateCategory
} from '@/services/api/admin/category/category.api'
import { toast } from 'vue3-toastify'
import dayjs from 'dayjs'

const props = defineProps<{ open: boolean; categoryId: string | null; title: string }>()
const emit = defineEmits(['close', 'success'])

const category = ref({
  categoryCode: '',
  categoryName: '',
  categoryStatus: 1
})

const categoryForm = ref()

const rules = {
  categoryName: [
    // { required: true, message: 'Tên không được để trống!', trigger: 'blur' },
    // {
    //   min: 3,
    //   max: 50,
    //   message: 'Tên phải từ 3 đến 50 ký tự!',
    //   trigger: 'blur'
    // },
    // {
    //   pattern: /^[a-zA-ZÀ-Ỹà-ỹ0-9 _-]+$/,
    //   message: 'Tên chỉ được chứa chữ cái (có dấu), số, khoảng trắng, dấu _ hoặc -!',
    //   trigger: 'blur'
    // }
    { required: true, message: 'Tên không được để trống!' },
    {
      validator: (_, value) => {
        if (!value) {
          return Promise.resolve() // Bỏ qua nếu giá trị rỗng (đã có rule 'required' xử lý)
        }
        const trimmedValue = value.trim()
        if (trimmedValue.length < 3 || trimmedValue.length > 50) {
          return Promise.reject('Tên phải từ 3 đến 50 ký tự!')
        }

        // Kiểm tra ký tự đặc biệt (chỉ cho phép chữ, số)
        if (!/^[a-zA-ZÀ-Ỹà-ỹ0-9 _-]+$/.test(trimmedValue)) {
          return Promise.reject(
            'Tên chỉ được chứa chữ cái (có dấu), số, khoảng trắng, dấu _ hoặc -!'
          )
        }
        return Promise.resolve()
      }
    }
  ]
}

const fetchCategoryDetails = async (id: string) => {
  try {
    const response = await getByIdCategory(id)
    category.value = {
      categoryCode: response.data.categoryCode,
      categoryName: response.data.categoryName,
      categoryStatus: response.data.categoryStatus
    }
  } catch (error) {
    console.error('Lỗi khi lấy thông tin thể loại:', error)
  }
}

watch(
  () => props.categoryId,
  async (newId) => {
    if (newId) {
      await fetchCategoryDetails(newId)
    } else {
      category.value = { categoryCode: '', categoryName: '', categoryStatus: 1 }
    }
  },
  { immediate: true }
)

const closeModal = () => {
  resetForm()
  emit('close')
}

const resetForm = () => {
  category.value = { categoryCode: '', categoryName: '', categoryStatus: 1 }
  categoryForm.value?.resetFields()
}

const handleSubmit = async () => {
  try {
    await categoryForm.value.validate()
    const formData = {
      categoryCode: category.value.categoryCode,
      categoryName: category.value.categoryName,
      categoryStatus: category.value.categoryStatus,
      createdDate: dayjs().valueOf()
    }

    if (props.categoryId) {
      try {
        await updateCategory(formData, props.categoryId)
        toast.success('Cập nhật thành công!')
        closeModal()
        emit('success')
      } catch (error) {
        toast.error(error.response.data.message)
      }
    } else {
      try {
        await createCategory(formData)
        toast.success('Thêm thành công!')
        closeModal()
        emit('success')
      } catch (error) {
        toast.error(error.response.data.message)
      }
    }
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
