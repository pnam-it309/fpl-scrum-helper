<template>
  <div v-if="visible">
    <a-modal
      :zIndex="10000"
      v-model:open="visible"
      title="Chọn loại"
      :footer="null"
      width="220px"
    >
      <div class="space-y-2">
        <!-- <div class="text-sm text-gray-600">Loại công việc</div> -->
        <a-select
          :getPopupContainer="trigger => trigger.parentElement"
          v-model:value="selectedJobId"
          :options="jobTypes.map(item => ({
            value: item.id,
            label: h('div', { class: 'flex items-center gap-2' }, [
              h(item.icon, { class: 'text-base', style: { color: item.color } }),
              h('span', item.type)
            ])
          }))"
          placeholder="Chọn loại công việc"
          allow-clear
          show-search
          class="w-full"
          :filterOption="(input, option) =>
            option.label.children[1].children.toLowerCase().includes(input.toLowerCase())"
          @change="onSelectJob"
        />
      </div>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, computed, defineProps, defineEmits, watch, h } from 'vue'
import { BugOutlined, CheckCircleOutlined, CloseCircleOutlined, ProfileOutlined, QuestionCircleOutlined } from '@ant-design/icons-vue'
import { TODO_ID_STORAGE_KEY } from '@/constants/key'
import { getAllTypeTodo } from '@/services/api/member/projectdetail/typetodo.api'
import { webSocketTypeTodo } from '@/services/service/member/typetodo.socket'
import { localStorageAction } from '@/utils/storage'
import { getIconData } from '@/utils/commom.helper'

const props = defineProps({
  open: {
    type: Boolean,
    default: false
  },
  idType: String,
  type: String
})

const emit = defineEmits(['update:open', 'select'])

const visible = computed({
  get: () => props.open,
  set: (val) => emit('update:open', val)
})

const selectedJobId = ref(null)
const jobTypes = ref([])

const iconMap = {
  'Công việc': { icon: ProfileOutlined, color: '#1890ff' },
  'Lỗi': { icon: BugOutlined, color: '#ff4d4f' },
  'Khác': { icon: QuestionCircleOutlined, color: '#8c8c8c' }
}

const fetchTypeTodo = async () => {
  try {
    const response = await getAllTypeTodo()
    const data = response?.data || []
    jobTypes.value = data.map(item => {
      const iconData = getIconData(item.type)
      return {
        ...item,
        icon: iconData.icon,
        color: iconData.color
      }
    })
    console.log('kiểu công việc:', jobTypes.value)
  } catch (error) {
    console.error('Lỗi khi tải danh sách thể loại:', error)
  }
}


watch(() => props.open, async (val) => {
  visible.value = val
  if (val) {
    await fetchTypeTodo()
    const matchedJob = jobTypes.value.find(job => job.id === props.idType)
    selectedJobId.value = matchedJob ? matchedJob.id : null
  }
})

const onSelectJob = (value) => {
  const payload = {
    idType: value,
    idTodo: localStorageAction.get(TODO_ID_STORAGE_KEY)
  }
  webSocketTypeTodo.sendJoinTypeTodo(payload)
  webSocketTypeTodo.getJoinTypeTodo(fetchTypeTodo)
}
</script>

<style scoped>
.a-select-dropdown {
  z-index: 10001 !important;
}
</style>
