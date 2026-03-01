<template>
  <a-modal
    :open="open"
    :title="title"
    @cancel="closeModal"
    :maskClosable="true"
    :centered="true"
    width="50%"
  >
    <template #footer>
      <a-button style="margin-right: 8px" @click="closeModal">Huỷ</a-button>
      <a-popconfirm
        title="Bạn có chắc chắn muốn lưu thay đổi?"
        @confirm="handleSubmit"
        ok-text="Đồng ý"
        cancel-text="Huỷ"
      >
        <a-button type="primary">Xác nhận</a-button>
      </a-popconfirm>
    </template>

    <a-form
      :model="estimate"
      ref="estimateForm"
      name="estimateForm"
      autocomplete="off"
      layout="vertical"
      :rules="rules"
    >
      <a-row :gutter="[16, 8]">
        <a-col :span="12">
          <a-form-item label="Sprint" name="idSprint" :rules="rules.idSprint">
            <a-select
              placeholder="Chọn Sprint"
              v-model:value="estimate.idSprint"
              show-search
              option-filter-prop="children"
              allowClear
            >
              <a-select-option v-for="item in filteredSprint" :key="item.id" :value="item.id">
                {{ item.name }}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label="Ngày ước tính" name="workday" :rules="rules.workday">
            <a-input-number
              v-model:value="estimate.workday"
              placeholder="Ngày"
              :min="1"
              style="width: 100%"
            />
          </a-form-item>
        </a-col>

        <a-col :span="24">
          <a-form-item label="Mô tả" name="descriptions" :rules="rules.descriptions">
            <a-textarea
              v-model:value="estimate.descriptions"
              placeholder="Nhập mô tả (tối đa 255 ký tự)"
              rows="4"
              allowClear
              show-count
              :maxLength="255"
            />
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, defineProps, defineEmits, watch, computed } from 'vue'
import {
  createEstimate,
  updateEstimate
} from '@/services/api/member/capacityestimate/capacityEstimate.api'
import { localStorageAction } from '@/utils/storage'
import { USER_INFO_STORAGE_KEY } from '@/constants/storagekey'
import { toast } from 'vue3-toastify'
import { useRoute } from 'vue-router'

const route = useRoute()
const props = defineProps<{
  open: boolean
  id: string | null
  title: string

  sprintHasBeen: any
  dataSprint: Array<{ id: string; name: string }>
  estimateDetails: {
    idEstimate: string
    sprint: string
    workday: string
    adjustedStoryPoints: string
    descriptions: string
  }
}>()
const emit = defineEmits(['close', 'success'])
const filteredSprint = computed<Array<{ id: string; name: string }>>(() => {
  return props.dataSprint.filter((sprint) => {
    if (!props.sprintHasBeen.includes(sprint.id)) return true
    if (sprint.id === props.estimateDetails.sprint) return true
    return false
  })
})

const estimate = ref({
  workday: '',
  point: '',
  idSprint: null,
  descriptions: ''
})

const estimateForm = ref(null)

const rules = {
  idSprint: [{ required: true, message: 'Vui lòng chọn Sprint', trigger: ['change'] }],
  workday: [
    { required: true, message: 'Vui lòng nhập thời gian ước tính', trigger: ['blur'] },
    {
      validator: (_: any, value: string) => {
        if (!value) return Promise.resolve()
        const num = Number(value)
        if (isNaN(num) || num <= 0) {
          return Promise.reject('Thời gian ước tính phải là số dương')
        }
        return Promise.resolve()
      },
      trigger: ['blur']
    }
  ],
  descriptions: [{ max: 500, message: 'Mô tả không được vượt quá 500 ký tự', trigger: ['blur'] }]
}

const closeModal = () => {
  emit('close')
  resetForm()
  props.estimateDetails.idEstimate = ''
}

const resetForm = () => {
  estimate.value = {
    workday: '',
    point: '',
    idSprint: null,
    descriptions: ''
  }

  // Reset validation trạng thái form
  estimateForm.value?.resetFields()
}

watch(
  () => props.open,
  (open) => {
    if (props.estimateDetails.sprint != null) {
      estimate.value.workday = props.estimateDetails.workday
      estimate.value.point = props.estimateDetails.adjustedStoryPoints
      estimate.value.idSprint = props.estimateDetails.sprint as any
      estimate.value.descriptions = props.estimateDetails.descriptions
    }
  }
)

const userLogin = localStorageAction.get(USER_INFO_STORAGE_KEY)

const handleSubmit = async () => {
  const formData = new FormData()
  formData.append('workday', estimate.value.workday)
  formData.append('adjustedStoryPoints', estimate.value.point)
  formData.append('idSprint', estimate.value.idSprint as any)
  formData.append('descriptions', estimate.value.descriptions)
  formData.append('idUser', userLogin.userId)
  formData.append('idProject', route.params.id as string)
  console.log('Dữ liệu truyền vào')

  console.log(formData)

  console.log(props.estimateDetails.idEstimate)

  if (!props.estimateDetails.idEstimate) {
    console.log('chạy vào create estimate')

    estimateForm.value
      .validate()
      .then(async () => {
        try {
          const res = await createEstimate(formData)
          resetForm()
          closeModal()
          emit('success')
          toast.success('Thêm thành công')
        } catch (error) {
          // Nếu server trả về lỗi thì nó sẽ vào đây
          console.error('Lỗi từ server:', error)
          toast.error(error?.response?.data?.message || 'Có lỗi xảy ra từ server')
        }
      })
      .catch((errorInfo) => {
        console.log('Validation Failed:', errorInfo)
      })
  } else {
    await updateEstimate(props.estimateDetails.idEstimate, formData)
    resetForm()
    props.estimateDetails.idEstimate = ''
    props.estimateDetails.sprint = ''
    toast.success('Cập nhật thành công')
    closeModal()
    emit('success')
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
