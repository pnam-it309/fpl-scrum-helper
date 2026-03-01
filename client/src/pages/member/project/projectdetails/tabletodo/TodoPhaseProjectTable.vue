<template>
  <div class="min-h-[360px]" style="margin: 10px; height: 560px; background-color: white">
    <a-table
      :columns="columns"
      :data-source="todo"
      :rowKey="(record) => record.idTodo"
      :pagination="{
        current: paginationParams.page,
        pageSize: paginationParams.size,
        total: totalItems,
        showSizeChanger: true,
        pageSizeOptions: ['10', '20', '30', '40', '50']
      }"
      :scroll="{ x: 500, y: 400 }"
      @change="handlePageChange"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'todoName'">
          <div
            v-if="editingTodoId !== record.idTodo"
            @click="startEditingTitle(record)"
            class="cursor-pointer hover:bg-gray-100 p-1 rounded"
          >
            {{ record.todoName || 'Không có tiêu đề' }}
          </div>

          <a-input
            v-else
            v-model:value="editedTitle"
            size="large"
            @pressEnter="saveTitle(record)"
            @blur="saveTitle(record)"
            class="w-full"
            style="min-width: 120px; border: none; outline: none"
            placeholder="Nhập tiêu đề thẻ"
          />
        </template>

        <template v-if="column.key === 'assignee'">
          <div class="flex items-center flex-wrap gap-2" @click="openMemberModal(record)">
            <a-tooltip
              v-for="member in [...record.students, ...record.staff]"
              :key="member.id"
              :title="member.name"
            >
              <a-avatar :src="member.picture" :alt="member.name" />
            </a-tooltip>
            <!-- <a-button shape="circle" icon="+"  /> -->
            <div
              v-if="
                (!record.students || record.students.length === 0) &&
                (!record.staff || record.staff.length === 0)
              "
              class="text-gray-500 cursor-pointer"
            >
              Chọn thành viên
            </div>
          </div>
        </template>

        <template v-if="column.key === 'label'">
          <div class="flex items-center flex-wrap gap-2">
            <a-tag
              v-for="label in record?.label"
              :key="label.id"
              :style="{ backgroundColor: `${label.color}`, color: getTextColor(label.color) }"
              class="cursor-pointer"
              @click="openLabelModal('edit', record.idTodo)"
            >
              {{ label.name }}
            </a-tag>

            <a-button shape="circle" @click="openLabelModal('create', record.idTodo)">+</a-button>
          </div>
        </template>

        <template v-if="column.key === 'deadlineTodo'">
          <div class="flex items-center gap-2" v-if="record?.deadlineTodo">
            <a-checkbox :checked="record.completionTime" @change="handleCheckboxChange(record)" />
            <a-tag color="blue" class="text-xs">
              {{ getDateFormat(Number(record?.deadlineTodo), true) }}
            </a-tag>
          </div>
        </template>

        <template v-if="column.key === 'todoListName'">
          <div
            style="
              white-space: normal;
              word-break: break-word;
              text-align: center;
              min-width: 120px;
            "
          >
            <a-select
              v-model:value="record.todoListName"
              style="width: 100%; white-space: normal"
              :dropdownStyle="{ whiteSpace: 'normal', wordBreak: 'break-word', maxWidth: '300px' }"
              @change="(todoListId) => handleSelectChange(record, todoListId)"
            >
              <a-select-option
                v-for="todolist in todoList"
                :key="todolist.todoListId"
                :value="todolist.todoListId"
              >
                <div style="white-space: normal; word-break: break-word">
                  {{ todolist.todoListName }}
                </div>
              </a-select-option>
            </a-select>
          </div>
        </template>

        <template v-if="column.key === 'priorityLevel'">
          <a-select
            v-model:value="record.priorityLevel"
            style="min-width: 120px"
            @change="(value) => selectPriority(record, value)"
          >
            <a-select-option
              v-for="(label, key) in statusPriorityLevelOptions"
              :key="key"
              :value="key"
            >
              <a-tag :color="statusPriorityLevelColors[key]">{{ label }}</a-tag>
            </a-select-option>
          </a-select>
        </template>
      </template>
    </a-table>

    <MaTodoMemberModal
      v-model="showAddMemberModal"
      @close="showAddMemberModal = false"
      :todoId="selectedTodoId"
    />

    <TodoTableLabelModal
      :visible="isLabelModalVisible"
      :labelData="selectedLabel"
      :mode="modalMode"
      @close="handleCloseLabelModal"
      @save="handleSaveLabel"
      @delete="handleDeleteLabel"
      :id="selectedTodoId"
    />
  </div>
</template>

<script setup lang="ts">
import { defineProps, defineEmits, h, ref, watch, onMounted, computed } from 'vue'
import { message, Tag, Tooltip } from 'ant-design-vue'
import dayjs from 'dayjs'
import {
  MBMeUpdateIndexTodoRequest,
  MBMeUpdateTodoRequest,
  webSocketTodo
} from '@/services/service/member/metodo.socket'
import MaTodoMemberModal from '@/pages/manage/todolist/MaTodoMemberModal.vue'
import {
  getAllPhaseMa,
  getAllTodoListByPhaseProject,
  getIndexMaxTodo,
  TodoListBoardResponse,
  TodoPhaseProjectResponse,
  TodoProjectResponse,
  updateTodoPhaseProject
} from '@/services/api/manage/todolist/todolist.api'
import { useRoute } from 'vue-router'
import { toast } from 'vue3-toastify'
import { getDateFormat, getTextColor } from '@/utils/commom.helper'
import TodoTableLabelModal from './TodoTableLabelModal.vue'
import { localStorageAction } from '@/utils/storage'
import { USER_INFO_STORAGE_KEY } from '@/constants/storagekey'
import { CaretDownOutlined, DownCircleOutlined, PlusOutlined } from '@ant-design/icons-vue'
import { router } from '@/routes/router'
import { webSocketSortTodo } from '@/services/service/member/sorttodo.socket'

const props = defineProps<{
  todoId: string | null
  modelValue: boolean
  searchQuery: string
  todo: any[]
  paginationParams: { page: number; size: number }
  totalItems: number
  currentPriority?: number
}>()
const meAllDetailTodo = ref<TodoPhaseProjectResponse | null>(null)
const emit = defineEmits(['page-change', 'refresh-todo', 'updatePriority'])
const isEditingTitle = ref(false)
const showAddMemberModal = ref(false)
const selectedTodoId = ref<string | null>(null)

const openMemberModal = (record) => {
  selectedTodoId.value = record.idTodo
  showAddMemberModal.value = true
}
const editingTodoId = ref<string | null>(null)
const editedTitle = ref('')

const startEditingTitle = (record) => {
  editingTodoId.value = record.idTodo
  editedTitle.value = record.todoName || ''
}

const route = useRoute()

const index = route.fullPath.indexOf('/project-detail')
const userLogin = localStorageAction.get(USER_INFO_STORAGE_KEY)
const trimmedUrl = index !== -1 ? route.fullPath.substring(index) : null

const completed = ref(false)

const handleCheckboxChange = (record) => {
  if (!record.idTodo) return

  const isCompleted = !record.completionTime
  record.completionTime = isCompleted ? 1 : 0

  const payload = {
    id: record.idTodo,
    idTodo: record.idTodo,
    status: isCompleted ? 0 : 1,
    projectId: route.params.id,
    idUser: userLogin.userId,
    urlPath: `/project-detail/${route.params.id}/${route.params.idPhase}?idTodo=${record.idTodo}`
  }

  webSocketTodo.sendupdateCompleteTodo(payload)
  if (isCompleted) {
    toast.success('Bạn đã hoàn thành công việc.')
  }
  emit('refresh-todo')
}

watch(
  () => meAllDetailTodo?.value?.completionTime,
  (newVal) => {
    isCompletedTimeValue.value = !!newVal
  }
)

//todolist
const fetchTodoList = async (idProject: string, idPhase: string) => {
  try {
    const response = await getAllTodoListByPhaseProject(idProject, idPhase) // Gọi API `getAllPhaseMa`
    todoList.value = response.data // Lưu dữ liệu Phase vào phases
    console.log('ssss: ', response.data)
  } catch (error) {
    console.error('Lỗi khi tải danh sách Phase:', error)
  }
}
onMounted(async () => {
  await fetchTodoList(route.params.id as string, route.params.idPhase as string)
  console.log('Phases đã được tải:', phases.value)
})

let oldColumnsBeforeDrag: TodoListBoardResponse[] = []
const phaseId = route.params.idPhase
const projectId = route.params.id
const sessionId = 'SESSION_ID'
const idUser = userLogin.userId

const handleTaskMoved = async (payload: {
  idTodo: string
  idTodoListOld: string
  idTodoListNew: string
  indexBefore: number
  indexAfter: number
  nameTodoListOld: string
  nameTodoListNew: string
}) => {
  const columnOld = oldColumnsBeforeDrag.find((col) => col.id === payload.idTodoListOld)
  const columnNew = oldColumnsBeforeDrag.find((col) => col.id === payload.idTodoListNew)
  const movingTask = columnOld?.tasks.find((task) => task.id === payload.idTodo)
  const indexBefore = movingTask?.indexTodo ?? 0
  let indexAfter = 0

  try {
    const newTodoList = todoList.value.find((list) => list.todoListId === payload.idTodoListNew)
    const idIndex = newTodoList?.todoListId

    if (!idIndex) throw new Error('Không tìm thấy idIndex cho todoList được chọn')

    const res = await getIndexMaxTodo(idIndex)
    const data = res?.data?.data ?? []

    const maxIndex = Number(res?.data ?? 0)

    console.log('max: ', maxIndex)
    indexAfter = maxIndex + 1
    console.log('afteer: ', indexAfter)
  } catch (err) {
    console.error('Lỗi lấy index max:', err)
    indexAfter = indexBefore + 1
  }

  const request: MBMeUpdateIndexTodoRequest = {
    ...payload,
    indexBefore,
    indexAfter,
    phaseId,
    projectId,
    sessionId,
    idUser,
    urlPath: `/project-detail/${route.params.id}/${route.params.idPhase}?idTodo=${payload.idTodo}`
  }

  webSocketTodo.sendUpdateIndexTodo(projectId, phaseId, request)
}

const handleSelectChange = (record: any, newTodoListId: string) => {
  if (record.todoListId === newTodoListId) return

  const oldTodoList = todoList.value.find((list) => list.todoListId === record.todoListId)
  const newTodoList = todoList.value.find((list) => list.todoListId === newTodoListId)

  const payload = {
    idTodo: record.idTodo,
    idTodoListOld: record.todoListId,
    idTodoListNew: newTodoListId,
    indexBefore: record.indexTodo ?? 0,
    indexAfter: 0, // sẽ được tính trong handleTaskMoved
    nameTodoListOld: oldTodoList?.todoListName ?? '',
    nameTodoListNew: newTodoList?.todoListName ?? ''
  }

  handleTaskMoved(payload)

  // Cập nhật record để UI phản ánh ngay lập tức
  record.todoListId = newTodoListId
  record.todoListName = newTodoList?.todoListName ?? record.todoListName
}

//label
const isLabelModalVisible = ref(false)
const selectedLabel = ref(null)
const modalMode = ref('view')

function openLabelModal(mode, idTodo) {
  modalMode.value = mode
  selectedTodoId.value = idTodo
  isLabelModalVisible.value = true
}
function handleSaveLabel(updatedLabel) {
  emit('refresh-todo')
}

function handleDeleteLabel(labelId) {
  isLabelModalVisible.value = false
  emit('refresh-todo')
}

const handleCloseLabelModal = () => {
  isLabelModalVisible.value = false
  emit('refresh-todo')
}

const phases = ref<any[]>([])
const todoList = ref<any[]>([])

const saveTitle = (record) => {
  if (editedTitle.value.trim() !== record.todoName) {
    const payload = {
      idTodo: record.idTodo,
      nameTodo: editedTitle.value.trim()
    }

    webSocketTodo.sendUpdateNameTodo(payload)
    record.todoName = editedTitle.value.trim()
  }
  editingTodoId.value = null
}

const openDetailModal = (record) => {
  router.replace({
    query: { ...route.query, idTodo: record.idTodo }
  })
}

const columns = [
  {
    title: 'STT',
    key: 'stt',
    width: 100,
    align: 'center',
    fixed: 'left',
    customRender: ({ index, record }) => {
      const sttNumber = index + 1 + (props.paginationParams.page - 1) * props.paginationParams.size

      return h(
        Tooltip,
        { title: 'Xem chi tiết' },
        {
          default: () =>
            h(
              'div',
              {
                style: {
                  cursor: 'pointer',
                  color: 'black',
                  display: 'inline-flex',
                  alignItems: 'center',
                  gap: '4px',
                  padding: '2px 6px',
                  borderRadius: '4px',
                  transition: 'background-color 0.3s'
                },
                onClick: () => openDetailModal(record),
                onMouseenter: (e) => (e.currentTarget.style.backgroundColor = '#f0f0f0'),
                onMouseleave: (e) => (e.currentTarget.style.backgroundColor = 'transparent')
              },
              [
                sttNumber.toString(),
                h(CaretDownOutlined, { style: { fontSize: '12px', color: '#1890ff' } })
              ]
            )
        }
      )
    }
  },
  { title: 'Tên', key: 'todoName', dataIndex: 'todoName', width: 200, align: 'left' },

  {
    title: 'Danh sách',
    key: 'todoListName',
    dataIndex: 'todoListName',
    width: 150,
    align: 'center',
    ellipsis: false
  },

  {
    title: 'Nhãn',
    key: 'label',
    dataIndex: 'label',
    width: 300,
    align: 'center'
  },

  {
    title: 'Thành viên',
    key: 'assignee',
    dataIndex: 'email',
    width: 300,
    align: 'center'
  },
  {
    title: 'Sự ưu tiên',
    key: 'priorityLevel',
    dataIndex: 'priorityLevel',
    width: 150,
    align: 'center'
  },

  {
    title: 'Ngày hết hạn',
    key: 'deadlineTodo',
    dataIndex: 'deadlineTodo',
    width: 150,
    align: 'center',
    customRender: ({ text }) =>
      text ? h(Tag, {}, () => dayjs(Number(text)).format('DD/MM/YYYY HH:mm')) : ''
  },
  {
    title: 'Trạng thái',
    key: 'statusTodo',
    dataIndex: 'statusTodo',
    width: 150,
    align: 'center',
    customRender: ({ text }) => {
      const statusMap = {
        CHUA_HOAN_THANH: { color: 'blue', label: 'Chưa hoàn thành' },
        HOAN_THANH_SOM: { color: 'orange', label: 'Hoàn thành sớm' },
        DA_HOAN_THANH: { color: 'green', label: 'Đã hoàn thành' },
        QUA_HAN: { color: 'red', label: 'Quá hạn' }
      }
      return h(
        Tag,
        { color: statusMap[text]?.color || 'default' },
        () => statusMap[text]?.label || text
      )
    }
  }
]

const statusPriorityLevelOptions = {
  QUAN_TRONG: 'Quan trọng',
  CAO: 'Cao',
  TRUNG_BINH: 'Trung bình',
  THAP: 'Thấp'
}

const statusPriorityLevelColors = {
  THAP: 'blue',
  CAO: 'orange',
  TRUNG_BINH: 'green',
  QUAN_TRONG: 'red'
}

const selectPriority = (record: any, priority: string) => {
  console.log('Selected Priority:', priority)

  const priorityLevelMap = {
    QUAN_TRONG: 0,
    CAO: 1,
    TRUNG_BINH: 2,
    THAP: 3
  }

  record.priorityLevel = priorityLevelMap[priority] || 0

  const payload: MBMeUpdateTodoRequest = {
    idTodo: record.idTodo,
    idTodoChange: record.idTodo,
    priorityLevel: record.priorityLevel
  }

  console.log('Sending payload to WebSocket:', payload)

  webSocketTodo.sendUpdatePriorityLevel(payload)

  emit('updatePriority', { idTodo: record.idTodo, priorityLevel: record.priorityLevel })
}

const handlePageChange = (pagination: any) => {
  emit('page-change', { page: pagination.current, pageSize: pagination.pageSize })
}
</script>

<style scoped>
:deep(.ant-select-selector) {
  border: none !important;
}

:deep(.ant-select-open .ant-select-selector) {
  border: none !important;
  box-shadow: none !important;
}

:deep(.ant-select-selector:focus),
:deep(.ant-select-selector:hover) {
  border: none !important;
  box-shadow: none !important;
}
</style>
