<template>
  <DivCustom label="Danh sách công việc đang theo dõi" customClasses="mt-5">
    <template #icon>
      <UnorderedListOutlined />
    </template>
    <div class="min-h-[360px]">
      <a-table
        :columns="columns"
        size="small"
        :data-source="todo"
        :rowKey="(record) => record.idTodo"
        :pagination="{
          current: paginationParams.page,
          pageSize: paginationParams.size,
          total: totalItems,
          showSizeChanger: true,
          pageSizeOptions: ['10', '20', '30', '40', '50']
        }"
        :scroll="{ x: 1000, y: 600 }"
        @change="handlePageChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'nameTodo'">
            <div
              v-if="editingTodoId !== record.idTodo"
              @click="startEditingTitle(record)"
              class="cursor-pointer hover:bg-gray-100 p-1 rounded"
            >
              {{ record.nameTodo || 'Không có tiêu đề' }}
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

          <template v-if="column.key === 'namePhaseProject'">
            <a-select
              v-model:value="record.namePhaseProject"
              style="min-width: 120px; max-width: 200px"
              @change="(idPhase) => handlePhaseChange(record, idPhase)"
            >
              <!-- Dropdown options lấy từ API fetchPhases -->
              <a-select-option v-for="phase in phases" :key="phase.idPhase" :value="phase.idPhase">
                <span class="ellipsis-text" :title="phase.namePhaseProject">
                  {{ phase.namePhaseProject }}
                </span>
              </a-select-option>
            </a-select>
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

          <template v-if="column.key === 'operation'">
            <div class=" flex gap-1 justify-center">
              <a-tooltip title="Chi tiết">
                <a-button
                  @click="handleNavigate(record.phaseId, record.idTodo)"
                  class="flex items-center justify-center w-8 h-8"
                >
                  <EyeOutlined />
                </a-button>
              </a-tooltip>
            </div>
          </template>
        </template>
      </a-table>

      <MaTodoMemberModal
        v-model="showAddMemberModal"
        @close="showAddMemberModal = false"
        :todoId="selectedTodoId"
      />
    </div>
  </DivCustom>
</template>

<script setup lang="ts">
import { defineProps, defineEmits, h, ref, watch, onMounted } from 'vue'
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import { message, Tag } from 'ant-design-vue'
import dayjs from 'dayjs'
import { MBMeUpdateTodoRequest, webSocketTodo } from '@/services/service/member/metodo.socket'
import MaTodoMemberModal from './MBTodoMemberModal.vue'
import { useRoute, useRouter } from 'vue-router'
import { webSocketMemberProject } from '@/services/service/member/memberproject.socket'
import {
  getAllPhaseMa,
  TodoProjectResponseMember,
  updateTodoPhaseProject
} from '@/services/api/member/phase/phase.api'
import { toast } from 'vue3-toastify'
import { ROUTES_CONSTANTS } from '@/constants/path'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { library } from '@fortawesome/fontawesome-svg-core'
import { faReceipt, faPenToSquare, faCircleInfo, faFilter } from '@fortawesome/free-solid-svg-icons'
import { FilterOutlined, UnorderedListOutlined } from '@ant-design/icons-vue'
import {
  BugOutlined,
  DeleteOutlined,
  DownloadOutlined,
  EyeOutlined,
  FileOutlined,
  FormOutlined,
  HistoryOutlined,
  IdcardOutlined,
  PlusCircleOutlined,
  ReloadOutlined,
  UploadOutlined
} from '@ant-design/icons-vue'

library.add(faReceipt, faPenToSquare, faCircleInfo, faFilter)

const props = defineProps<{
  todoId: string | null
  modelValue: boolean
  searchQuery: string
  todo: any[]
  paginationParams: { page: number; size: number }
  totalItems: number
  currentPriority?: number
}>()
const meAllDetailTodo = ref<TodoProjectResponseMember | null>(null)
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
  editedTitle.value = record.nameTodo || ''
}

const route = useRoute()

const phases = ref<any[]>([])
const loading = ref(true)

const deadlineTodo = ref(null)

const saveTitle = (record) => {
  if (editedTitle.value.trim() !== record.nameTodo) {
    const payload = {
      idTodo: record.idTodo,
      nameTodo: editedTitle.value.trim()
    }
    webSocketTodo.sendUpdateNameTodo(payload)
    record.nameTodo = editedTitle.value.trim()
  }
  editingTodoId.value = null
}
const handlePhaseChange = async (record: any, idPhase: string) => {
  try {
    const response = await updateTodoPhaseProject(idPhase, record.idTodo, record.projectId)

    toast.success('Cập nhật Sprint thành công!')
    emit('refresh-todo')
  } catch (err) {
    console.error('Lỗi khi cập nhật phase:', err)
    toast.error('Cập nhật Sprint thất bại!')
  }
}

const columns = [
  { title: 'STT', key: 'orderNumber', dataIndex: 'orderNumber', width: 60, align: 'center' },
  { title: 'Tên', key: 'nameTodo', dataIndex: 'nameTodo', width: 300, align: 'left' },

  {
    title: 'Giai đoạn',
    key: 'namePhaseProject',
    dataIndex: 'namePhaseProject',
    width: 222,
    align: 'left'
  },

  {
    title: 'Người được giao',
    key: 'assignee',
    dataIndex: 'email',
    width: 300,
    align: 'left'
  },

  {
    title: 'Sự ưu tiên',
    key: 'priorityLevel',
    dataIndex: 'priorityLevel',
    width: 150,
    align: 'left'
  },
  {
    title: 'Trạng thái',
    key: 'statusTodo',
    dataIndex: 'statusTodo',
    width: 150,
    align: 'left',
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
  },
  {
    title: 'Ngày tạo',
    key: 'createdDate',
    dataIndex: 'createdDate',
    width: 150,
    align: 'left',
    customRender: ({ text }) =>
      text ? h(Tag, {}, () => dayjs(Number(text)).format('DD/MM/YYYY HH:mm')) : ''
  },
  {
    title: 'Ngày cập nhật',
    key: 'lastModifiedDate',
    dataIndex: 'lastModifiedDate',
    width: 150,
    align: 'left',
    customRender: ({ text }) =>
      text ? h(Tag, {}, () => dayjs(Number(text)).format('DD/MM/YYYY HH:mm')) : ''
  },

  {
    title: 'Hành động',
    key: 'operation',
    width: 150,
    align: 'center',
    fixed: 'right'
  }
]

const handlePageChange = (pagination: any) => {
  emit('page-change', { page: pagination.current, pageSize: pagination.pageSize })
}

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

const fetchPhases = async (id: string) => {
  try {
    const response = await getAllPhaseMa(id) // Gọi API `getAllPhaseMa`
    phases.value = response.data // Lưu dữ liệu Phase vào phases
    console.log('ssss: ', response.data)
  } catch (error) {
    console.error('Lỗi khi tải danh sách Phase:', error)
  }
}
onMounted(async () => {
  await fetchPhases(route.params.id as string)
  console.log('Phases đã được tải:', phases.value)
})

const visible = ref(props.modelValue)
const selectedPriority = ref<number | null>(props.currentPriority ?? null)

watch(
  () => props.modelValue,
  (val) => {
    visible.value = val
    if (val) {
      selectedPriority.value = props.currentPriority ?? null
    }
  }
)

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

webSocketTodo.getUpdateNameTodo((updatedTodo) => {
  const todoItem = props.todo.find((item) => item.id === updatedTodo.idTodo)
  if (todoItem) {
    todoItem.nameTodo = updatedTodo.nameTodo
  }
  emit('refresh-todo')
})

webSocketMemberProject.getJoinTodoVote((record) => {
  if (props.todoId == record.idTodo) {
    emit('refresh-todo')
  }
})
webSocketMemberProject.getOutTodoVoteMemberProject((record) => {
  if (props.todoId == record.idTodo) {
    emit('refresh-todo')
  }
})

webSocketTodo.getUpdatePriorityLevel((record: MBMeUpdateTodoRequest) => {
  if (props.todoId === record.idTodo) {
    emit('refresh-todo')
  }
})

const projectId = ref<string | null>((route.params.id as string) || null)

const router = useRouter()
const handleNavigate = (phaseId: string, idTodo: string) => {
  router.push({
    path: `/member/project-detail/${projectId.value}/${phaseId}`,
    query: { idTodo: idTodo }
  })
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

:deep(.ant-btn) {
  background: var(--selected-header) !important; /* Gradient nền */
  color: var(--selected-text) !important; /* Màu chữ trắng */
  border-color: var(--selected-header) !important; /* Viền màu từ gradient */
}

/* Hiệu ứng hover */
:deep(.ant-btn:hover),
:deep(.ant-btn:focus) {
  background: var(--selected-header-hover) !important; /* Gradient khi hover */
  border-color: var(--selected-header-hover) !important; /* Viền khi hover */
}

.ellipsis-text {
  display: inline-block;
  width: 100%;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
