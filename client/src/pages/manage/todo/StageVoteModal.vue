<template>
  <a-modal
    v-model:visible="visible"
    width="500px"
    :open="open"
    :title="props.title"
    @cancel="closeModal"
    @ok="createStage"
  >
    <div class="space-y-4">
      <!-- Chọn giai đoạn -->
      <div>
        <label class="block text-sm font-medium mb-1">Chọn giai đoạn</label>
        <a-select
          v-model:value="paramsStage.idPhase"
          placeholder="Chọn giai đoạn"
          :options="mappedPhases"
          class="w-full"
          size="large"
        />
      </div>

      <!-- Chọn ngày bắt đầu -->
      <div>
        <label class="block text-sm font-medium mb-1">Ngày bắt đầu</label>
        <a-date-picker
          show-time
          v-model:value="paramsStage.startTime"
          placeholder="Chọn ngày bắt đầu"
          class="w-full"
          size="large"
        />
      </div>

      <!-- Chọn ngày kết thúc cụ thể -->
      <div>
        <label class="block text-sm font-medium mb-1">Ngày kết thúc cụ thể</label>
        <a-date-picker
          show-time
          v-model:value="paramsStage.endTime"
          placeholder="Chọn ngày kết thúc"
          class="w-full"
          size="large"
        />
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, computed, reactive } from 'vue'
import { phaseResponse } from '@/services/api/manage/phase/phase.api'
import { createUpdate, stageRequest } from '@/services/api/manage/stage/stage.api'
import { toast } from 'vue3-toastify'
import dayjs from 'dayjs'
import { number } from 'echarts/core'
import { useRoute } from 'vue-router'

// Modal hiển thị
const visible = ref(false)
const route = useRoute()

// Props nhận từ cha
const props = defineProps<{
  open: boolean
  title: string
  dataPhase: phaseResponse[]
  idFacilityProject: string
}>()

// Tính toán options dựa trên props
const mappedPhases = computed(() =>
  props.dataPhase.map((item) => ({
    label: item.name,
    value: item.id
  }))
)

// Emit sự kiện
const emit = defineEmits(['close', 'success'])

// Đóng modal
const closeModal = () => {
  paramsStage.endTime = null
  paramsStage.startTime = null
  paramsStage.idPhase = ''
  paramsStage.idStage = ''

  visible.value = false
  emit('close')
}

const paramsStage = reactive({
  idPhase: '',
  idStage: '',
  startTime: null as any, // ban đầu là null, sau nhận từ date picker
  endTime: null as any
})
const createStage = async () => {
  try {
    const request: stageRequest = {
      idProject: route.params.id as string,
      idStage: paramsStage.idStage,
      idPhase: paramsStage.idPhase,
      startTime: dayjs(paramsStage.startTime).valueOf(),
      endTime: dayjs(paramsStage.endTime).valueOf()
    }
    const res = await createUpdate(request)
    toast.success(res.message)

    emit('success')
    closeModal()
  } catch (error) {
    toast.error(error.response.data.message)

    console.log(error)
  }
}
</script>

<style scoped>
.ant-modal-content {
  border-radius: 8px;
}
.ant-modal-body {
  padding: 24px;
}
</style>
