<template>
  <a-modal
    :open="props.isOpen"
 :title="modalTitle"
     @cancel="closeModal"
    :style="{ top: '20px' }"
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

    <div class="ml-5 my-5">
      <a-alert
        v-if="shouldShowWarning"
        type="warning"
        message="Giai đoạn này có chênh lệch lớn giữa điểm ước lượng và thực tế!"
        show-icon
        class="mb-4"
      />

      <a-form
        ref="formRef"
        layout="vertical"
        :model="formState"
        :rules="rules"
        :label-col="{ span: 7 }"
        :wrapper-col="{ span: 23 }"
      >
        <a-form-item label="Tên giai đoạn" name="name">
          <a-input
            v-model:value="formState.name"
            placeholder="Nhập tên giai đoạn"
            @blur="formState.name = formState.name.trim()"
          />        
        </a-form-item>

        <a-form-item label="Chọn thời gian">
          <a-select v-model:value="duration" :options="optionsSelectDuration" />
        </a-form-item>

        <a-form-item label="Ngày bắt đầu" name="startDate">
          <a-date-picker
            class="w-full"
            v-model:value="formState.startDate"
            placeholder="Chọn ngày bắt đầu"
            format="DD-MM-YYYY"
          />
        </a-form-item>

        <a-form-item label="Ngày kết thúc" name="endDate">
          <a-date-picker
            :disabled="duration !== 0"
            class="w-full"
            v-model:value="formState.endDate"
            placeholder="Chọn ngày kết thúc"
            format="DD-MM-YYYY"
          />
        </a-form-item>

        <a-form-item label="Mô tả" name="descriptions">
          <a-textarea placeholder="Nhập mô tả" v-model:value="formState.descriptions" :rows="4" />
        </a-form-item>
      </a-form>
    </div>
  </a-modal>
</template>

<script lang="ts" setup>
import { phaseWebSocket } from '@/services/api/manage/phase/phase.socket.api'
import { SelectProps } from 'ant-design-vue'
import { Rule } from 'ant-design-vue/es/form'
import dayjs, { Dayjs, ManipulateType } from 'dayjs'
import { debounce } from 'lodash'
import { defineProps, defineEmits, ref, reactive, watch, computed } from 'vue'
import { useRoute } from 'vue-router'

const props = defineProps<{
  isOpen: boolean
  title: Record<string, string>
  storyPoint: Record<string, number>
  countEstimateStoryPointSprint: Record<string, number>
  countUser: number

  check: number
  dataPhase:
    | {
        id: string
        name: string
        startDate: Dayjs | undefined
        endDate: Dayjs | undefined
        descriptions?: string
      }
    | undefined
}>()

const emits = defineEmits<{
  (e: 'close', idPhase: string): void
  (e: 'updateSprint', idPhase: string): void
}>()

// ======== Form State & Rules ========
type StartingPhaseRequest = {
  name: string
  startDate: Dayjs
  endDate: Dayjs
  id?: string
  descriptions: string
  statusPhase: string
}

const route = useRoute()

const durationWeek = 7 * 24 * 60 * 60 * 1000
const duration = ref<number>(1 * durationWeek)

const formRef = ref()
const formState = reactive<StartingPhaseRequest>({
  name: '',
  id: '',
  startDate: dayjs(),
  endDate: dayjs().add(duration.value, 'millisecond'),
  descriptions: '',
  statusPhase: ''
})

const rules: Record<string, Rule[]> = {
  name: [
    { required: true, message: 'Cần nhập tên giai đoạn', trigger: ['change', 'blur'] },
    {
      validator: (_rule, value: string) => {
        if (!value || !value.trim()) {
          return Promise.reject('Tên giai đoạn không được chỉ chứa khoảng trắng')
        }
        return Promise.resolve()
      },
      trigger: ['change', 'blur']
    }
  ],  
  startDate: [{ required: true, message: 'Cần chọn ngày bắt đầu', trigger: ['change'] }],
  endDate: [
    { required: true, message: 'Cần chọn ngày kết thúc', trigger: ['change'] },
    {
      validator: (_rule, value) => {
        if (!value || !formState.startDate) return Promise.resolve()
        if (value.isBefore(formState.startDate, 'day')) {
          return Promise.reject('Ngày kết thúc phải lớn hơn hoặc bằng ngày bắt đầu')
        }
        return Promise.resolve()
      },
      trigger: ['change']
    }
  ],
    descriptions: [
  {
    validator: (_rule, value: string) => {
      if (value == null || value === '') {
        return Promise.resolve();
      }
      if (!value.trim()) {
        return Promise.reject('Mô tả không được chỉ chứa khoảng trắng');
      }
      return Promise.resolve();
    },
    trigger: ['change', 'blur']
  }
]

}

// ======== Dropdown Duration ========
const optionsSelectDuration = ref<SelectProps['options']>([
  { label: '1 tuần', value: 1 * durationWeek },
  { label: '2 tuần', value: 2 * durationWeek },
  { label: '4 tuần', value: 4 * durationWeek },
  { label: 'Tùy chỉnh', value: 0 }
])

// ======== Tính cảnh báo ========
const shouldShowWarning = computed(() => {
  if (!props.dataPhase?.id) return false

  const sprintId = props.dataPhase.id
  const estimatedPoints = props.countEstimateStoryPointSprint[sprintId] ?? 0
  const actualPoints = props.storyPoint[sprintId] ?? 0

  // Cảnh báo nếu chênh lệch tuyệt đối >= 3
  return actualPoints - estimatedPoints >= 3
})

// ======== Gửi dữ liệu ========
const debouncedEmitUpdateSprint = debounce(() => {
  emits('updateSprint', props.dataPhase?.id || '')
}, 100)

import { message } from 'ant-design-vue'

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
      formState.name = formState.name.trim()
      formState.descriptions = formState.descriptions.trim()

    await phaseWebSocket.sendMessageUpdatePhase(props.dataPhase?.id || '', {
      name: formState.name,
      code: formState.name,
      idProject: route.params.id as string,
      startTime: formState.startDate.valueOf() + '',
      endTime: formState.endDate.valueOf() + '',
      descriptions: formState.descriptions,
      statusPhase: formState.statusPhase

    })

    console.log("status: ", )
    if (props.check !== 1) {
      debouncedEmitUpdateSprint()
    }

    closeModal()
  } catch (error: any) {
    const serverMessage = error?.response?.data?.message || error.message || 'Đã xảy ra lỗi!'
    message.error(serverMessage)
  }
}

// ======== Đóng modal ========
const closeModal = () => {
  formRef.value?.resetFields()
  duration.value = 1 * durationWeek
  emits('close', props.dataPhase?.id || '')
}

// ======== Tính toán thời gian kết thúc ========
const caculateMiliseconds = (
  operator: 'add' | 'substract',
  target: Dayjs,
  timeAdd: number,
  unit: ManipulateType
) => {
  return operator === 'add' ? target.add(timeAdd, unit) : target.subtract(timeAdd, unit)
}

// ======== Theo dõi thay đổi thời gian ========
watch([duration, () => formState.startDate], ([newDuration, newStartDate]) => {
  const validStart = newStartDate ?? dayjs()
  if (newDuration !== 0) {
    formState.endDate = caculateMiliseconds('add', validStart, newDuration, 'millisecond')
  }
})

// ======== Khi mở modal, gán lại dữ liệu ========
watch(
  () => props.isOpen,
  (isOpen) => {
    if (isOpen && props.dataPhase) {
      formState.name = props.dataPhase.name || ''
      formState.startDate = props.dataPhase.startDate?.isValid?.()
        ? props.dataPhase.startDate
        : dayjs()
      formState.endDate = props.dataPhase.endDate?.isValid?.()
        ? props.dataPhase.endDate
        : dayjs().add(duration.value, 'millisecond')
formState.descriptions = props.dataPhase.descriptions || ''
formState.statusPhase = props.check === 1 ? 'DANG_LAM' : 'CHUA_HOAN_THANH'
    }
  },
  { immediate: true }
)

const modalTitle = computed(() => {
  const status = formState.statusPhase
  if (status === 'CHUA_HOAN_THANH') return 'Bắt đầu giai đoạn'
  if (status === 'DANG_LAM') return 'Cập nhật giai đoạn'
  return 'Thông tin giai đoạn'
})

watch(
  () => props.dataPhase,
  (val) => {
    console.log('New dataPhase:', val)
    console.log('Loaded description:', val?.descriptions)
  },
  { immediate: true }
)

</script>
