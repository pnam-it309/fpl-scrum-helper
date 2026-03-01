<template>
  <a-drawer
    :title="title"
    :width="'30%'"
    :open="open"
    @close="onClose"
    class="phase-completion-drawer"
  >
    <template #extra>
      <a-button style="margin-right: 8px" @click="onClose">Hủy</a-button>
      <a-button type="primary" @click="handleComplete">Xác nhận hoàn thành</a-button>
    </template>
    <div class="completion-form">
      <p class="font-semibold text-lg mb-4">Danh sách công việc trong Sprint</p>

      <template v-if="formattedTodos && formattedTodos.length > 0">
        <a-table
          :dataSource="formattedTodos"
          :columns="columns"
          :pagination="false"
          :row-selection="{
            selectedRowKeys: selectedTodoIds,
            onChange: onSelectChange,
            getCheckboxProps
          }"
          class="mb-6"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'status'">
              <a-tag :color="getStatusColor(record?.status || '')">
                {{ getStatusText(record?.status || '') }}
              </a-tag>
            </template>
            <template v-if="column.key === 'progress'">
              <a-progress
                :percent="Number(record?.progress || 0)"
                size="small"
                :stroke-color="getProgressColor(Number(record?.progress || 0))"
              />
            </template>
          </template>
        </a-table>
      </template>
      <template v-else>
        <p>Không có công việc nào trong Sprint này</p>
      </template>

      <div v-if="selectedTodoIds.length > 0" class="mt-6">
        <p class="font-semibold mb-2">Chuyển công việc đã chọn sang Sprint:</p>
        <a-select
          v-model:value="targetSprintId"
          style="width: 100%"
          placeholder="Chọn Sprint để chuyển"
        >
          <a-select-option v-for="sprint in availableSprints" :key="sprint.id" :value="sprint.id">
            {{ sprint.name }}
          </a-select-option>
        </a-select>
      </div>

      <div class="mt-6">
        <p class="font-semibold mb-2">Ghi chú kết thúc Sprint:</p>
        <a-textarea
          v-model:value="completionNote"
          placeholder="Ghi chú về Sprint (không bắt buộc)"
          :rows="4"
        />
      </div>
    </div>
  </a-drawer>
</template>
<script lang="ts" setup>
import { Modal } from 'ant-design-vue'
import { ref, computed, reactive, watch } from 'vue'
import { toast } from 'vue3-toastify'
import { phaseWebSocket } from '@/services/api/manage/phase/phase.socket.api'
import { useRoute } from 'vue-router'

interface Todo {
  id?: string
  name?: string
  status?: string
  progress?: number
  type?: string
}
const route = useRoute()
const props = defineProps<{
  open: boolean
  title: string
  todos?: Todo[]
  sprints: any[]
  idProject: string
  currentSprintId: string
}>()

const emit = defineEmits(['close', 'success'])
const completionNote = ref('')
const hasIncompleteTodos = computed(() => {
  return props.todos?.some((todo) => todo.status !== 'DA_HOAN_THANH')
})

const selectedTodoIds = ref<string[]>([])
const targetSprintId = ref<string | null>(null)

// Cập nhật logic: Lọc sprint hiện tại và các sprint có trạng thái 'DANG_LAM' ra khỏi danh sách
const availableSprints = computed(() => {
  console.log('Danh sách spint')

  console.log(props.sprints)

  return props.sprints.filter(
    (sprint) => sprint.id !== props.currentSprintId && sprint.statusPhase !== 'DANG_LAM'
  )
})

const onSelectChange = (keys: string[]) => {
  selectedTodoIds.value = keys
}

const getCheckboxProps = (record: any) => ({
  disabled: record.status === 'DA_HOAN_THANH'
})

const formattedTodos = computed(() => {
  if (!Array.isArray(props.todos)) return []
  return props.todos.map((todo) => ({
    key: todo?.id || Math.random().toString(),
    ...todo
  }))
})

const columns = [
  {
    title: 'Tên công việc',
    dataIndex: 'name',
    key: 'name',
    width: '40%',
    render: (text: string) => text || 'Không có tên'
  },
  {
    title: 'Trạng thái',
    dataIndex: 'status',
    key: 'status',
    width: '25%'
  },
  {
    title: 'Tiến độ',
    dataIndex: 'progress',
    key: 'progress',
    width: '35%'
  }
]

const getStatusColor = (status: string) => {
  switch (status) {
    case 'CHUA_HOAN_THANH':
      return 'blue'
    case 'DANG_LAM':
      return 'orange'
    case 'DA_HOAN_THANH':
      return 'green'
    case 'QUA_HAN':
      return 'red'
    case 'HOAN_THANH_SOM':
      return 'orange'
    default:
      return 'default'
  }
}

const getStatusText = (status: string) => {
  if (!status) return 'Chưa hoàn thành'
  switch (status) {
    case 'CHUA_HOAN_THANH':
      return 'Chưa hoàn thành'
    case 'DANG_LAM':
      return 'Đang làm'
    case 'DA_HOAN_THANH':
      return 'Đã hoàn thành'
    case 'QUA_HAN':
      return 'Quá hạn'
    case 'HOAN_THANH_SOM':
      return 'Hoàn thành sớm'
    default:
      return status
  }
}

const getProgressColor = (progress: number) => {
  const safeProgress = Number(progress) || 0
  if (safeProgress >= 100) return '#52c41a'
  if (safeProgress >= 70) return '#1890ff'
  if (safeProgress >= 30) return '#faad14'
  return '#ff4d4f'
}

const onClose = () => {
  completionNote.value = ''
  selectedTodoIds.value = []
  targetSprintId.value = null
  emit('close')
}

const index = ref(0)
const handleComplete = async () => {
  const incompleteButNotSelected = props.todos?.filter(
    (todo) => todo.status !== 'DA_HOAN_THANH' && !selectedTodoIds.value.includes(todo.id as string)
  )

  const showConfirmationModal = async () => {
    return new Promise((resolve) => {
      Modal.confirm({
        title: 'Có công việc chưa hoàn thành',
        content:
          'Bạn có chắc chắn muốn kết thúc Sprint ngay cả khi còn công việc chưa hoàn thành chưa được chuyển không?',
        okText: 'Vẫn hoàn thành',
        cancelText: 'Hủy',
        onOk() {
          resolve(true)
        },
        onCancel() {
          resolve(false)
        }
      })
    })
  }

  if (selectedTodoIds.value.length > 0) {
    if (!targetSprintId.value) {
      toast.error('Vui lòng chọn Sprint để chuyển công việc!')
      return
    }
    index.value++

    selectedTodoIds.value.forEach((t) => {
      phaseWebSocket.sendMessageDeleteTodoByPhase(t)
    })

    try {
      await phaseWebSocket.sendMessageCreateTodoByPhase({
        idTodo: selectedTodoIds.value,
        idPhase: targetSprintId.value,
        index: index.value as unknown as string,
        idProject: route.params.id as string
      })
    } catch (error) {
      toast.error('Chuyển công việc thất bại!')
      console.error(error)
      return
    }
  }

  if (incompleteButNotSelected && incompleteButNotSelected.length > 0) {
    const confirmed = await showConfirmationModal()
    if (!confirmed) {
      return
    }
  }

  emit('success', {
    note: completionNote.value
  })
  onClose()
}
</script>

<style scoped></style>
