<template>
  <a-modal :open="props.open" title="Thêm công việc mới" @cancel="closeTodoModal">
    <template #footer>
      <a-button @click="closeTodoModal">Hủy</a-button>
      <a-button type="primary" @click="submitForm">Xác nhận</a-button>
    </template>

    <!-- Begin Form -->
    <a-form ref="formRef" :model="formState" :rules="rules" layout="vertical">
      <!-- Tên công việc -->
      <a-form-item label="Tên công việc" name="name">
        <a-input v-model:value="formState.name" placeholder="Nhập tên công việc" />
      </a-form-item>

      <!-- Loại công việc -->
      <a-form-item label="Loại công việc" name="typeValue">
        <a-select v-model:value="formState.typeValue" placeholder="Chọn loại công việc" allow-clear>
          <a-select-option v-for="item in listTypeTodo" :key="item.id" :value="item.id">
            <component
              :is="iconMap[item.type]?.icon || QuestionCircleOutlined"
              :style="{ color: iconMap[item.type]?.color || '#8c8c8c', marginRight: '8px' }"
            />
            {{ item.type }}
          </a-select-option>
        </a-select>
      </a-form-item>
    </a-form>
    <!-- End Form -->
  </a-modal>
</template>

<script lang="ts" setup>
import { ref, onMounted, watch } from 'vue'
import { toast } from 'vue3-toastify'
import type { FormInstance } from 'ant-design-vue'
import { dataCreateTodo, todoWebSocket } from '@/services/api/manage/todo/todo.socket.api'
import { TypeTodoResponse } from '@/services/api/member/projectdetail/typetodo.api'
import { getListTypeTodo } from '@/services/api/manage/todo/typetodo.ap'
import { BugOutlined, ProfileOutlined, QuestionCircleOutlined } from '@ant-design/icons-vue'

const iconMap = {
  'Công việc': { icon: ProfileOutlined, color: '#1890ff' },
  Lỗi: { icon: BugOutlined, color: '#ff4d4f' },
  Khác: { icon: QuestionCircleOutlined, color: '#8c8c8c' }
}

// Props và emits
const props = defineProps<{
  open: boolean
  idProject: string | null
}>()

const emit = defineEmits(['close', 'success'])

// Form state
const formRef = ref<FormInstance>()
const formState = ref({
  name: ''
})

// Validate: không trống, không ký tự đặc biệt
const rules = {
  name: [
    { required: true, message: 'Vui lòng nhập tên công việc' },
    {
      validator: (_, value) => {
        if (!value) {
          return Promise.resolve() // Bỏ qua nếu giá trị rỗng (đã có rule 'required' xử lý)
        }
        const trimmedValue = value.trim()

        // 1. Kiểm tra giới hạn ký tự đặc biệt
        if (!/^[^<>[\]{} Máy chủ]*/.test(value)) {
          // Giữ nguyên check trên 'value' gốc để bắt các ký tự đặc biệt dù có khoảng trắng
          return Promise.reject('Tên công việc không được chứa các ký tự < > [ ] { }')
        }

        // 2. Kiểm tra độ dài sau khi trim
        if (trimmedValue.length === 0) {
          return Promise.reject('Vui lòng nhập tên công việc') // Tránh trường hợp chỉ có khoảng trắng
        }
        if (trimmedValue.length > 250) {
          return Promise.reject('Tên công việc không được vượt quá 250 ký tự!')
        }

        return Promise.resolve()
      }
    }
  ],
  typeValue: [{ required: true, message: 'Vui lòng chọn loại công việc' }]
}

const listTypeTodo = ref<TypeTodoResponse[]>()
const typeValue = ref('')
const itemDefault = ref<TypeTodoResponse[]>()

const fetchDataTypeTodo = async () => {
  const res = await getListTypeTodo()
  listTypeTodo.value = res.data
  const typeDefault = listTypeTodo.value.filter((item) => item.type === 'Công việc')
  if (typeDefault) {
    itemDefault.value = typeDefault
    typeValue.value = typeDefault[0].id
  }
}

// Gửi form
const submitForm = () => {
  formRef.value
    ?.validate()
    .then(() => {
      const payload: dataCreateTodo = {
        name: formState.value.name.trim(),
        code: formState.value.name.trim(),
        idProject: props.idProject as string,
        idType: typeValue.value
      }

      console.log('📤 Payload gửi đi:', payload)
      todoWebSocket.sendMessageCreateTodo(payload)
      toast.success('Thêm mới thành công')
      emit('success')
      closeTodoModal()
    })
    .catch(() => {
      console.log('❌ Validate thất bại')
    })
}

watch(
  () => props.open,
  async (val) => {
    if (val) {
      await fetchDataTypeTodo()
    }
  }
)

// Đóng modal & reset form
const closeTodoModal = () => {
  formRef.value?.resetFields()
  emit('close')
}

// Đăng ký socket
onMounted(() => {
  todoWebSocket.subscribeCreateTodo(() => emit('success'))
  todoWebSocket.subscribeDeleteTodo(() => emit('success'))
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
