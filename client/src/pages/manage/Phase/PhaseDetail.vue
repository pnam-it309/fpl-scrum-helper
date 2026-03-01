<template>
  <DivCustom
    :label="'Các giai đoạn hoàn thành'"
    customClasses="mt-5 font-semibold text-lg text-gray-800"
  >
    <template #icon>
      <ProjectOutlined />
    </template>

    <div class="w-full space-y-4">
      <!-- Không có giai đoạn -->
      <div
        v-if="localSprints.length === 0"
        class="flex flex-col items-center justify-center text-gray-400 text-sm py-12 bg-white rounded-xl border border-dashed border-gray-300"
      >
        <InboxOutlined class="text-5xl mb-3" />
        <p class="text-center px-4">Dự án chưa có giai đoạn hoàn thành</p>
      </div>

      <!-- Có giai đoạn -->
      <div
        v-else
        v-for="(item, sprintIndex) in localSprints"
        :key="item.id"
        class="bg-white rounded-xl border border-indigo-100 shadow-sm transition hover:shadow-md"
      >
        <!-- Header Giai đoạn -->
        <div
          class="flex flex-wrap justify-between items-center px-6 py-4 border-b bg-indigo-50 rounded-t-xl sticky top-0 z-10 gap-4"
        >
          <!-- Bên trái -->
          <div class="flex flex-wrap items-center gap-4 text-sm flex-1 min-w-0">
            <button
              @click="toggleSprintVisibility(sprintIndex)"
              class="text-indigo-600 hover:text-indigo-700 transition"
            >
              <RightOutlined v-if="item.expanded" />
              <DownOutlined v-else />
            </button>

            <span class="font-semibold text-gray-800 truncate max-w-[200px] sm:max-w-[300px]">
              {{ item.name }}
            </span>

            <div class="flex items-center gap-1 text-gray-500 whitespace-nowrap">
              <span>{{ formatDateNVV(startTime[item.id]) }}</span>
              <span>-</span>
              <span>{{ formatDateNVV(endTime[item.id]) }}</span>
            </div>

            <span
              class="bg-white border border-gray-300 text-gray-600 px-3 py-1 rounded-full text-xs whitespace-nowrap"
            >
              {{ getSprintTaskCount(item.id) }} công việc
            </span>
          </div>

          <!-- Nút Chi tiết -->
          <div class="shrink-0">
            <a-button
              @click="viewSprintDetail(item.id)"
              class="h-9 text-sm font-medium text-white bg-indigo-500 hover:bg-indigo-600 px-4 rounded-lg shadow-sm"
            >
              Chi tiết
            </a-button>
          </div>
        </div>

        <!-- Nội dung công việc -->
        <div
          v-if="item.expanded"
          class="max-h-[420px] overflow-y-auto px-6 py-4 space-y-3 bg-slate-50 rounded-b-xl"
        >
          <div
            v-if="getSprintTaskCount(item.id) === 0"
            class="border-2 border-dashed border-indigo-300 rounded-xl p-6 text-center text-gray-400"
          >
            Chưa có công việc nào trong sprint này
          </div>

          <draggable
            :model-value="getSprintTasks(item.id)"
            :group="{ name: 'tasks', pull: false, put: false }"
            item-key="id"
            class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4"
          >
            <template #item="{ element }">
              <div
                class="bg-white hover:bg-indigo-50 transition-all p-4 rounded-xl border border-gray-200 shadow-sm cursor-pointer space-y-2"
                @click="handleTodoClick(element.id, $event)"
              >
                <!-- Tên và độ ưu tiên -->
                <div class="flex flex-col sm:flex-row sm:justify-between sm:items-center gap-2">
                  <h4 class="text-sm font-semibold text-gray-700 truncate">{{ element.name }}</h4>
                  <span
                    class="text-xs font-semibold"
                    :class="{
                      'text-blue-500': element.priorityLevel === 'THAP',
                      'text-yellow-500': element.priorityLevel === 'TRUNG_BINH',
                      'text-orange-600': element.priorityLevel === 'CAO',
                      'text-red-600': element.priorityLevel === 'QUAN_TRONG'
                    }"
                  >
                    {{
                      element.priorityLevel === 'THAP'
                        ? 'Thấp'
                        : element.priorityLevel === 'TRUNG_BINH'
                        ? 'Trung Bình'
                        : element.priorityLevel === 'CAO'
                        ? 'Cao'
                        : 'Quan Trọng'
                    }}
                  </span>
                </div>

                <!-- Deadline -->
                <div class="text-xs text-gray-500">
                  Hạn: {{ !element.deadline ? '-' : formatDateNVV(Number(element.deadline)) }}
                </div>

                <!-- Tiến độ -->
                <div class="flex flex-col sm:flex-row sm:items-center gap-3">
                  <span class="text-xs text-gray-600 font-medium">Tiến độ:</span>
                  <a-progress
                    class="flex-1"
                    :percent="Number(element.progress)"
                    stroke-color="#6366F1"
                    :show-info="false"
                  />
                  <span class="text-xs text-indigo-600 font-medium">{{ element.progress }}%</span>
                </div>
              </div>
            </template>
          </draggable>
        </div>
      </div>
    </div>
  </DivCustom>

  <TodoDetailModal
    v-model="showTaskDetailModal"
    class="custom-modal"
    :todoId="selectedTodoId"
    :disabled="true"
  />
</template>

<script setup lang="ts">
import { ref, watch, onMounted, reactive, onUnmounted, computed } from 'vue'
import draggable from 'vuedraggable'
import {
  DownOutlined,
  PlusOutlined,
  ProjectOutlined,
  RightOutlined,
  UpOutlined
} from '@ant-design/icons-vue'
import { getAllTodoByPhase, todoResponse } from '@/services/api/manage/todo/todo.api'

import { phaseWebSocket } from '@/services/api/manage/phase/phase.socket.api'
import {
  faReceipt,
  faPenToSquare,
  faCircleInfo,
  faFilter,
  faRotateRight,
  faTrash,
  faTrashAlt,
  faEye
} from '@fortawesome/free-solid-svg-icons'
import { library } from '@fortawesome/fontawesome-svg-core'
library.add(
  faReceipt,
  faPenToSquare,
  faCircleInfo,
  faFilter,
  faRotateRight,
  faTrash,
  faTrashAlt,
  faEye
)
import { router } from '@/routes/router'
import { ROUTES_CONSTANTS } from '@/constants/path'
import TodoDetailModal from '@/pages/member/project/projectdetails/projectmodal/TodoDetailModal.vue'
import { formatDate, formatDateNVV, getDateFormat } from '@/utils/commom.helper'
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import { localStorageAction } from '@/utils/storage'
import { USER_INFO_STORAGE_KEY } from '@/constants/storagekey'
import { ROLES } from '@/constants/roles'

const idSprintTodo = ref<Record<string, todoResponse[]>>({})

const props = defineProps<{
  idTodo?: string
  dataTodo: any[]
  sprints: any[]
  idProject: string
  idSprintTodo: Record<string, todoResponse[]>
}>()
const emit = defineEmits(['update-sprint-todos', 'count-todo', 'remove-task-from-sprint'])
const editedSprintName = reactive<{ [key: string]: string }>({})
const startTime = reactive<{ [key: string]: string }>({})
const endTime = reactive<{ [key: string]: string }>({})
// ✅ Đồng bộ `localSprints` với props.dataTodo
const localSprints = ref([...props.dataTodo])
const paramsTodo = reactive({
  page: 1,
  size: 10,
  totalItem: '',
  search: ''
})

const handleTodoClick = (idTodo: string, event: Event) => {
  console.log(idTodo + 'lsslsl')

  selectedTodoId.value = idTodo
  showTaskDetailModal.value = true
}

const showTaskDetailModal = ref(false)

const selectedTodoId = ref<string | null>(null)

const getSprintTaskCount = (sprintId) => {
  return idSprintTodo.value[sprintId]?.length || 0
}

// ✅ Lấy danh sách công việc theo Sprint ID
const getSprintTasks = (id: string) => {
  return idSprintTodo.value[id] ?? []
}

// ✅ Xử lý hiển thị/ẩn Sprint
const toggleSprintVisibility = (index: number) => {
  // Tạo một bản sao để đảm bảo Vue cập nhật phản ứng
  localSprints.value = localSprints.value.map((sprint, i) => {
    if (i === index) {
      return { ...sprint, expanded: !sprint.expanded }
    }
    return sprint
  })
}

// ✅ Lấy dữ liệu danh sách Sprint từ API
const fetchDataPhase = async () => {
  if (!props.dataTodo || props.dataTodo.length === 0) return
  for (const sprint of props.dataTodo) {
    if (!sprint.id) continue
    console.log('sksk')

    const dataTodoByPhase = await getAllTodoByPhase(paramsTodo, sprint.id)
    emit('count-todo', dataTodoByPhase.data.length)

    idSprintTodo.value[sprint.id] = dataTodoByPhase.data || []


    // Cập nhật trạng thái nút dựa vào status từ server
    switch (sprint.statusPhase) {
      case 'CHUA_HOAN_THANH':
        sprintStatus.value[sprint.id] = 'notStarted'
        break
      case 'DANG_LAM':
        sprintStatus.value[sprint.id] = 'inProgress'
        break
      case 'DA_HOAN_THANH':
        sprintStatus.value[sprint.id] = 'completed'
        break
      case 'QUA_HAN':
        sprintStatus.value[sprint.id] = 'completed'
        break
      default:
        sprintStatus.value[sprint.id] = 'notStarted'
    }

    for (const item of props.dataTodo) {
      editedSprintName[item.id as string] = item.name
      startTime[item.id as string] = item.startTime
      endTime[item.id as string] = item.endTime
    }
  }
}

watch(
  () => props.dataTodo,
  (newData) => {
    localSprints.value = [...newData]
    fetchDataPhase() // Gọi lại API khi có dữ liệu mới
  },
  { deep: true }
)

onMounted(() => {
  fetchDataPhase()
})

// chi tiết giai đoạn dự án
const currentSprintTodos = ref([])

// const detailSprint = (idPhase: string) => {
//   if (!sprintStatus.value[idPhase] || sprintStatus.value[idPhase] === 'notStarted') {
//     // Khi nhấn "Bắt đầu"
//     phaseWebSocket.sendMessageUpdateStatusPhase(idPhase, '1')
//     sprintStatus.value[idPhase] = 'inProgress'

//     // Lưu idPhase và idProject vào localStorage
//     localStorage.setItem('currentPhaseId', idPhase)
//     localStorage.setItem('currentProjectId', props.idProject)

//     console.log('Ádas')

//     console.log(localStorage.getItem('currentPhaseId'))
//     console.log(localStorage.getItem('currentProjectId'))

//     // Chuyển trang
//     router.push({
//       name: `${ROUTES_CONSTANTS.PROJECT.children.PROJECT_DETAIL.name}`,
//       params: {
//         idProject: props.idProject,
//         idPhase: idPhase
//       }
//     })
//   } else if (sprintStatus.value[idPhase] === 'inProgress') {
//     // Khi nhấn "Hoàn thành" - mở drawer
//     const sprintTasks = getSprintTasks(idPhase) || []
//     currentSprintTodos.value = sprintTasks
//       .map((task) => {
//         if (!task) return null
//         return {
//           id: task.id,
//           name: task.name || '',
//           status: task.status || 'CHUA_HOAN_THANH',
//           progress: Number(task.progress || 0),
//           type: task.type || ''
//         }
//       })
//       .filter((task) => task !== null)

//     currentSprintName.value = editedSprintName[idPhase] || ''
//     openDrawer.value = true
//   }
// }

// Thêm các ref mới
const openDrawer = ref(false)
const currentSprintName = ref('')

// Thêm state để theo dõi trạng thái của từng sprint
const sprintStatus = ref<{ [key: string]: 'notStarted' | 'inProgress' | 'completed' }>({})

// Thêm computed property mới
const userLogin = localStorageAction.get(USER_INFO_STORAGE_KEY)
const viewSprintDetail = (idPhase: string) => {
  if (userLogin.roleScreen == ROLES.ADMIN) {
    router.push({
      name: `${ROUTES_CONSTANTS.MANAGE.children.PROJECT_DETAIL.name}`,
      params: {
        idProject: props.idProject,
        idPhase: idPhase
      }
    })
  } else if (userLogin.roleScreen == ROLES.MANAGE) {
    router.push({
      name: `${ROUTES_CONSTANTS.MANAGE.children.PROJECT_DETAIL.name}`,
      params: {
        idProject: props.idProject,
        idPhase: idPhase
      }
    })
  } else if (userLogin.roleScreen == ROLES.MEMBER) {
    router.push({
      name: `${ROUTES_CONSTANTS.MEMBER.children.PROJECT_DETAIL.name}`,
      params: {
        idProject: props.idProject,
        idPhase: idPhase
      }
    })
  }
}

watch(
  () => props.dataTodo,
  (newData) => {
    localSprints.value = newData.map((item, index) => ({
      ...item,
      expanded: index === 0
    }))

    fetchDataPhase()
  },
  { deep: true, immediate: true }
)
</script>

<style scoped>
.border-dashed {
  border-style: dashed;
}

.ant-picker {
  border: none !important;
  box-shadow: none !important;
}

.ant-picker-focused {
  border: none !important;
  box-shadow: none !important;
}

.cursor-grab {
  cursor: grab;
}

.custom-row-height td {
  height: 10px !important;
  /* Giảm chiều cao hàng */
}

.cursor-grab {
  cursor: grab;
}

.custom-row-height td {
  height: 10px !important;
}

.selected-todo {
  background-color: rgba(59, 130, 246, 0.1) !important;
}

/* Thêm style cho modal */
:deep(.ant-modal-mask) {
  background-color: rgba(0, 0, 0, 0.45) !important;
  backdrop-filter: blur(4px);
  z-index: 1000;
}

:deep(.ant-modal-wrap) {
  z-index: 1001;
}

:deep(.ant-modal) {
  top: 50%;
  transform: translateY(-50%);
}

:deep(.ant-modal-content) {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  border-radius: 8px;
}

/* Animation cho modal */
:deep(.ant-modal-enter),
:deep(.ant-modal-appear) {
  transform: scale(0.8);
  opacity: 0;
}

:deep(.ant-modal-enter-active),
:deep(.ant-modal-appear-active) {
  transform: scale(1);
  opacity: 1;
  transition: all 0.3s cubic-bezier(0.08, 0.82, 0.17, 1);
}

:deep(.ant-modal-leave) {
  transform: scale(1);
  opacity: 1;
}

:deep(.ant-modal-leave-active) {
  transform: scale(0.8);
  opacity: 0;
  transition: all 0.3s cubic-bezier(0.78, 0.14, 0.15, 0.86);
}

/* ✅ Định dạng lại nền, viền, chữ cho Modal */

/* Thêm style cho nút Chi tiết */
.bg-blue-500 {
  background-color: #3b82f6;
}

.bg-blue-500:hover {
  background-color: #2563eb;
}

:deep(.ant-btn) {
  background-color: rgba(102, 99, 121, 0.95) !important;
  color: var(--selected-text) !important;
  border-color: rgba(102, 99, 121, 1) !important;
}

:deep(.ant-btn:hover),
:deep(.ant-btn:focus) {
  background-color: rgba(102, 99, 121, 1) !important;
  border-color: rgba(102, 99, 121, 1) !important;
}
</style>
