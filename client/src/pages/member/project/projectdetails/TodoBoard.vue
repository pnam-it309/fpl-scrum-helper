<template>
  <BackgroundBoard :isTableView="isTableView" @toggleView="toggleView">
    <main class="flex-1 h-full overflow-x-auto overflow-y-hidden">
      <div>
        <div v-if="!isView" class="flex gap-6 items-start min-h-[100%] w-max p-3" ref="boardContainer">
          <draggable :disabled="canDragCheck" v-model="filteredBoard" group="columns" item-key="id" animation="300" ghost-class="column-ghost"
            chosen-class="column-chosen" drag-class="column-dragging" class="flex gap-6 items-start "
            @start="onDragStart" @end="onDragEnd">
            <template #item="{ element }">
              <div class="w-64 flex-shrink-0">
                <div class="w-64 bg-white/95 rounded-xl shadow-lg pt-2 p-1 flex flex-col flex-shrink-0"
                  ref="columnContainer">
                  <div class="flex items-center justify-between mb-1">
                    <div class="flex-1">
                      <template v-if="isEditingTitle === element.id">
                        <input ref="titleInputRef" v-model="editableTitle" @blur="updateTitleColumn(element)"
                          @keydown.enter="updateTitleColumn(element)"
                          class="text-lg font-semibold text-gray-700 border border-gray-300 rounded px-2 py-1 w-full" />
                      </template>
                      <template v-else>
                        <h2
                          class="text-base ml-2 font-semibold text-gray-700 hover:text-gray-900 cursor-pointer transition-all duration-200 ease-in-out break-words"
                          style="font-family: 'Inter', sans-serif; overflow-wrap: anywhere;"
                          @click="startEditTitle(element)">
                          {{ element.name }}
                        </h2>
                      </template>

                    </div>
                    <ColumnOptionsDropdown :column="element" @delete="handleDelete" />
                  </div>

                  <div
                    class="flex-1 overflow-y-auto pl-1 pr-1 scrollbar-thin scrollbar-thumb-gray-300 scrollbar-track-gray-100 "
                    :style="{ maxHeight: `calc(100vh - 240px)` }" ref="taskScrollContainer">
                    <draggable :disabled="canDragCheck" v-model="element.tasks" group="tasks" item-key="id" animation="200" ghost-class="ghost"
                      chosen-class="chosen" drag-class="dragging" @start="onDragStartTask" @end="handleTaskDragEnd"
                      :data-list-id="element.id" :data-list-name="element.name">
                      <template #item="{ element: task }">
                        <div
                          class="mb-2 bg-white rounded-xl shadow-md border border-gray-200 hover:border-blue-500 hover:shadow-lg transition-all duration-200 cursor-pointer overflow-hidden"
                          @click="openTaskDetailModal(task.id)">
                          <!-- Ảnh hiển thị đầy đủ mà không bị cắt -->
                          <div v-if="task.urlImage" class="w-full h-32 flex items-center justify-center bg-gray-100 ">
                            <img :src="task.urlImage" alt="Ảnh tiêu đề" class="max-w-full max-h-full object-contain" />
                          </div>

                          <!-- Nội dung -->
                          <div class="p-3 space-y-2">
                            <!-- label -->
                            <div v-if="task.labels && task.labels.length > 0" class="flex flex-wrap gap-1 mt-1">
                              <span v-for="label in task.labels" :key="label.id"
                                class="text-xs font-medium px-2.5 py-0.5 rounded-md" :style="{
                                  backgroundColor: label.colorLabel,
                                  color: getTextColor(label.colorLabel.replace('#', ''))
                                }">
                                {{ label.name }}
                              </span>
                            </div>


                            <!-- Tên và kiểu công việc -->
                            <div class="flex items-center gap-2 w-full text-sm font-medium text-gray-800 truncate">

                              <!-- Kiểu công việc -->
                              <a-tooltip title="Kiểu công việc" v-if="task?.typeTodo">
                                <component :is="getTypeIcon(task.typeTodo)"
                                  :style="{ color: getTypeColor(task.typeTodo) }" class="text-base flex-shrink-0" />
                              </a-tooltip>

                              <!-- Mức ưu tiên -->
                              <a-tooltip title="Mức độ ưu tiên" v-if="task.priorityLevel">
                                <component :is="getPriorityIcon(task.priorityLevel)"
                                  class="text-base text-red-500 flex-shrink-0" />
                              </a-tooltip>

                              <!-- Tên công việc -->
                              <span class="truncate whitespace-nowrap overflow-hidden flex-1">
                                {{ task.name }}
                              </span>
                            </div>



                            <!-- Thông tin  -->
                            <div class="flex flex-wrap items-center gap-2 text-xs text-gray-600">
                              <!--bình luận  -->
                              <a-tooltip title="Số bình luận" v-if="task.numberCommnets > 0">
                                <div class="flex items-center gap-1 text-gray-500">
                                  <CommentOutlined />
                                  <span>{{ task.numberCommnets }}</span>
                                </div>
                              </a-tooltip>

                              <!-- Số đính kèm -->
                              <a-tooltip title="Số tệp đính kèm" v-if="task.numberAttachments > 0">
                                <div class="flex items-center gap-1 text-gray-500">
                                  <PaperClipOutlined />
                                  <span>{{ task.numberAttachments }}</span>
                                </div>
                              </a-tooltip>

                              <!-- Công việc hoàn thành  -->
                              <a-tooltip title="Công việc đã hoàn thành / Tổng công việc" v-if="task.numberTodo > 0">
                                <div class="text-xs font-semibold px-2 py-0.5 rounded-full inline-block" :class="{
                                  'bg-green-100 text-green-700':
                                    task.numberTodo === task.numberTodoComplete,
                                  'bg-gray-100 text-gray-600':
                                    task.numberTodo !== task.numberTodoComplete
                                }">
                                  <template v-if="task.numberTodo === task.numberTodoComplete">
                                    <CheckCircleOutlined style="color: green; margin-right: 4px" />
                                    {{ task.numberTodoComplete }}/{{ task.numberTodo }}
                                  </template>
                                  <template v-else>
                                    <SyncOutlined style="color: orange; margin-right: 4px" />
                                    {{ task.numberTodoComplete }}/{{ task.numberTodo }}
                                  </template>
                                </div>
                              </a-tooltip>
                              <!-- Mức độ hoàn thành  -->
                              <a-tooltip title="Tiến độ công việc" v-if="task.progress > 0">
                                <div v-if="task.progress !== null && task.progress !== undefined"
                                  class="px-2 py-0.5 font-semibold rounded-full flex items-center" :style="{
                                    backgroundColor: getColorByProgress(task.progress) + '20',
                                    color: getColorByProgress(task.progress)
                                  }">
                                  <span v-if="task.progress === 100" class="mr-2 text-green-500">✔</span>
                                  {{ task.progress }}%
                                </div>
                              </a-tooltip>

                              <!-- Thành viên dự án -->
                              <div v-if="task.memberTodo && task.memberTodo.length > 0"
                                class="flex -space-x-2 flex-wrap">
                                <a-tooltip v-for="(member, index) in task.memberTodo" :key="index"
                                  :title="`${member.email}`">
                                  <div
                                    class="w-6 h-6 rounded-full border-2 border-white shadow-sm overflow-hidden flex items-center justify-center bg-gray-100 text-xs font-semibold text-gray-500">
                                    <template v-if="member.image">
                                      <img :src="member.image" alt="avatar" class="w-full h-full object-cover" />
                                    </template>
                                    <template v-else> ? </template>
                                  </div>
                                </a-tooltip>
                              </div>
                            </div>

                            <div class="flex">
                              <a-tooltip :title="getDeadlineTooltip(task.deadline, currentTime)" v-if="task.deadline">
                                <div :class="getDeadlineClass(task.deadline, currentTime)"
                                  class="flex items-center gap-1 text-xs ml-auto">
                                  <ClockCircleOutlined />
                                  <span>{{ formatDate(task.deadline) }}</span>
                                  <CheckCircleOutlined v-if="task.completionTime" class="text-green-500" />
                                  <CloseCircleOutlined v-else class="text-red-500" />
                                </div>
                              </a-tooltip>
                            </div>

                          </div>
                        </div>
                      </template>
                    </draggable>
                  </div>
                </div>
              </div>
            </template>
          </draggable>

          <div class="w-64 flex-shrink-0">
            <div class="h-full flex items-start">
              <AddTodoList v-if="addingColumn" @addColumn="handleAddColumn" @cancel="addingColumn = false" />
              <button v-else @click="addingColumn = true"
                class="w-full min-h-[48px] px-4 py-2 text-sm bg-white border border-gray-300 text-gray-600 rounded-xl shadow-sm hover:bg-gray-100 hover:border-gray-400 hover:text-gray-800 transition-all duration-200 text-left">
                ＋ Thêm danh sách khác
              </button>
            </div>
          </div>
        </div>

        <router-view v-else :tasks="tasksData" :todoLists="todoListsData" :labels="labelsData" :members="membersData" />

        <TodoDetailModal :disabled="canDragCheck" v-model="showTaskDetailModal" :todoId="selectedTodoId" @close="closeDetailModal" />
      
      </div>
    </main>
  </BackgroundBoard>
</template>

<script setup lang="ts">
import { ref, onMounted, defineProps, defineEmits, computed, nextTick, watch, reactive, watchEffect } from 'vue'
import draggable from 'vuedraggable'
import AddTodoList from './AddTodoList.vue'
import ColumnOptionsDropdown from './ColumnOptionsDropdown.vue'
import TodoDetailModal from './projectmodal/TodoDetailModal.vue'
import { getBoard, MBMeBoardResponse } from '@/services/api/member/projectdetail/metodo.api'
import { webSocketTodoList } from '@/services/service/member/metodolist.socket'
import { MBMeUpdateIndexTodoRequest, webSocketTodo } from '@/services/service/member/metodo.socket'
import { toast } from 'vue3-toastify'
import { localStorageAction } from '@/utils/storage'
import { USER_INFO_STORAGE_KEY } from '@/constants/storagekey'
import {
  formatDate,
  getColorByProgress,
  getDateFormat,
  getDeadlineClass,
  getDeadlineTooltip,
  getPriorityIcon,
  getPriorityText,
  getTextColor,
  getTypeColor,
  getTypeIcon
} from '@/utils/commom.helper'
import { webSocketTodoChild } from '@/services/service/member/todochild.socket'
import { webSocketMemberProject } from '@/services/service/member/memberproject.socket'
import { webSocketLabelProject } from '@/services/service/member/melabelproject.socket'
import { webSocketMeTodoComment } from '@/services/service/member/mecomment.socket'
import { webSocketLabelProjectTodo } from '@/services/service/member/melabeltodo.socket'
import { webSocketMeTodoResource } from '@/services/service/member/resource.socket'
import { webSocketImage } from '@/services/service/member/image.socket'
import {
  CheckCircleOutlined,
  CloseCircleOutlined,
  CommentOutlined,
  PaperClipOutlined,
  SyncOutlined
} from '@ant-design/icons-vue'
import BackgroundBoard from './BackgroundBoard.vue'

import { webSocketSortTodo } from '@/services/service/member/sorttodo.socket'
import { ROLES } from '@/constants/roles'
import { ROUTES_CONSTANTS } from '@/constants/path'
import { useRoute, useRouter } from 'vue-router'
const route = useRoute()
const router = useRouter()
defineProps<{ isTableView: boolean }>()
defineEmits(['toggleView'])
const currentTime = ref(Date.now())
const meBoard = ref<MBMeBoardResponse[]>([])
const addingColumn = ref(false)
const isDragging = ref(false)
const showTaskDetailModal = ref(false)
const selectedTodoId = ref<string | null>(null)
const isEditingTitle = ref<string | null>(null)
const editableTitle = ref('')
const titleInputRef = ref<HTMLInputElement | null>(null)
const userLogin = localStorageAction.get(USER_INFO_STORAGE_KEY)
let oldColumnsBeforeDrag: MBMeBoardResponse[] = []

// Constants
const phaseId = route.params.idPhase
const projectId = route.params.id
const sessionId = 'SESSION_ID'
const idUser = userLogin.userId

// Dữ liệu tasks, todoLists, labels và members từ meBoard
const tasksData = computed(() => meBoard.value.flatMap((column) => column.tasks || []))

const todoListsData = computed(() =>
  meBoard.value.map((column) => ({
    id: column.id,
    name: column.name
  }))
)

const labelsData = computed(() => {
  const labelsSet = new Map()
  meBoard.value.forEach((column) => {
    column.tasks?.forEach((task) => {
      task.labels?.forEach((label) => {
        if (!labelsSet.has(label.id)) {
          labelsSet.set(label.id, label)
        }
      })
    })
  })
  return Array.from(labelsSet.values())
})

// Lấy danh sách thành viên từ tất cả các tasks
const membersData = computed(() => {
  const membersSet = new Map()
  meBoard.value.forEach((column) => {
    column.tasks?.forEach((task) => {
      task.members?.forEach((member) => {
        if (!membersSet.has(member.id)) {
          membersSet.set(member.id, member)
        }
      })
    })
  })
  return Array.from(membersSet.values())
})

import { useFilterStore } from '@/stores/filterStore'
import { getUrlActivity } from '@/utils/urlActivityHelper'
import { webSocketTypeTodo } from '@/services/service/member/typetodo.socket'
import { filter } from 'lodash'
import { getStatusByIdPhase } from '@/services/api/member/projectdetail/phase.api'

const filteredBoard = ref<MBMeBoardResponse[]>([])

// Lấy filter từ Pinia store
const filterStore = useFilterStore()

watchEffect(() => {
  const filters = {
    memberId: filterStore.memberId,
    noMember: filterStore.noMember,
    myTodo: filterStore.myTodo,
    dueDate: filterStore.dueDate,
    labels: filterStore.labels,
    labelSelected: filterStore.labelsSelected,
    searchText: filterStore.searchText,
    membersSelected: filterStore.membersSelected,
    typeSelected: filterStore.typesSelected
  }

  const now = Date.now()

  const result = meBoard.value.map(board => {
    const filteredTasks = board.tasks.filter(task => {
      // Không có bộ lọc nào được chọn -> giữ lại hết
      if (!filters || Object.values(filters).every(value => {
        if (Array.isArray(value)) return value.length === 0
        return !value
      })) {
        return true
      }

      const hasMember = (task.memberTodo?.length ?? 0) > 0

      // 🔍 Lọc theo tên
      if (filters.searchText && filters.searchText.trim().length > 0) {
        const taskName = task.name.trim().toLowerCase().replace(/\s+/g, ' ')
        const searchText = filters.searchText.trim().toLowerCase().replace(/\s+/g, ' ')
        if (!taskName.includes(searchText)) return false
      }

      // 👤 Lọc theo "Không có thành viên" hoặc "Của tôi"
      if (filters.noMember || filters.myTodo) {
        const isNoMember = !hasMember
        const isMyTask = hasMember && task.memberTodo.some(
          member => member?.id === userLogin.userId
        )

        const match = (filters.noMember && isNoMember) || (filters.myTodo && isMyTask)
        if (!match) return false
      }

      // 👥 Lọc theo danh sách thành viên cụ thể
      if (filters.membersSelected && filters.membersSelected.length > 0) {
        const matched = task.memberTodo.some(member =>
          filters.membersSelected.includes(member.id)
        )
        if (!matched) return false
      }

      // 🏷️ Lọc theo labels đặc biệt (ví dụ: 'no_label')
      if (filters.labels && filters.labels.length > 0) {
        const matchedLabel = filters.labels.some(label => {
          switch (label) {
            case 'no_label':
              return task.labels.length === 0
            default:
              return false
          }
        })
        if (!matchedLabel) return false
      }

      // 🏷️ Lọc theo nhãn cụ thể (id)
      if (filters.labelSelected && filters.labelSelected.length > 0) {
        const taskLabelIds = task.labels.map(label => label.id)
        const matched = filters.labelSelected.some(id => taskLabelIds.includes(id))
        if (!matched) return false
      }

      // 📂 Lọc theo kiểu công việc
      if (filters.typeSelected && filters.typeSelected.length > 0) {
        if (!filters.typeSelected.includes(task.typeTodo)) {
          return false
        }
      }

      // ⏰ Lọc theo thời hạn
      if (filters.dueDate.length > 0) {
        const matchDue = filters.dueDate.some(dateFilter => {
          switch (dateFilter) {
            case 'no_due':
              return task.deadline === null
            case 'overdue':
              return task.deadline > 0 && task.deadline < now && task.progress < 100
            case 'not_done':
              return !task.completionTime
            case 'done_early':
              return task.completionTime && task.completionTime < task.deadline
            case 'done_late':
              return task.completionTime && task.completionTime > task.deadline
            default:
              return false
          }
        })

        if (!matchDue) return false
      }

      return true // Nếu không bị loại bởi điều kiện nào, giữ lại
    })

    return {
      ...board,
      tasks: filteredTasks
    }
  })

  filteredBoard.value = result
})


const fetchBoard = async () => {
  try {
    const response = await getBoard({
      projectId: route.params.id,
      idPhase: route.params.idPhase
    })
    meBoard.value = response.data
    console.log("MeBoard", meBoard.value);

  } catch (error) {
    console.error('Lỗi khi lấy danh sách cột:', error)
  }
}


const isView = computed(() => route.path.includes('/table'))

const handleAddColumn = () => {
  addingColumn.value = false
}

const onDragStart = () => {
  isDragging.value = true
  // oldColumnsBeforeDrag = JSON.parse(JSON.stringify(meBoard.value));
}
const onDragStartTask = () => {
  isDragging.value = true
  oldColumnsBeforeDrag = JSON.parse(JSON.stringify(meBoard.value))
}

const onDragEnd = (event: any) => {
  isDragging.value = false

  const draggedColumn = event.item?.__draggable_context?.element
  if (!draggedColumn) return

  const { id: columnId } = draggedColumn
  const indexBefore = event.oldIndex
  const indexAfter = event.newIndex

  webSocketTodoList.sendUpdateIndexColumn(route.params.id as string, {
    idTodoList: columnId,
    idProject: route.params.id as string,
    indexBefore: indexBefore.toString(),
    indexAfter: indexAfter.toString(),
    sessionId: '1',
    idPhase: route.params.idPhase
  })
}

const handleDelete = (column: MBMeBoardResponse) => {
  if (column.tasks.length > 0) {
    toast.error('Không thể xóa khi có công việc')
    return
  }
  webSocketTodoList.sendDeleteTodoList(route.params.id as string, {
    id: column.id,
    projectId: route.params.id as string,
    idPhase: route.params.idPhase
  })
}

const startEditTitle = (column: MBMeBoardResponse) => {
  isEditingTitle.value = column.id
  editableTitle.value = column.name
  nextTick(() => {
    titleInputRef.value?.focus()
    titleInputRef.value?.select()
  })
}

const updateTitleColumn = (column: MBMeBoardResponse) => {
  isEditingTitle.value = null

  if (editableTitle.value.trim() && editableTitle.value !== column.name) {
    // Cập nhật local UI trước
    column.name = editableTitle.value

    const payload = {
      idTodoList: column.id,
      name: editableTitle.value
    }

    webSocketTodoList.sendUpdateNameTodoList(route.params.id as string, payload)
  }
}

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

  if (columnNew) {
    if (payload.indexAfter === 0) {
      indexAfter = columnNew.tasks[0]?.indexTodo ?? 0
    } else if (payload.indexAfter >= columnNew.tasks.length) {
      indexAfter = (columnNew.tasks[columnNew.tasks.length - 1]?.indexTodo ?? 0) + 1
    } else {
      indexAfter = columnNew.tasks[payload.indexAfter]?.indexTodo ?? indexBefore
    }
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

const handleTaskDragEnd = (event: any) => {
  const { from, to, item, oldIndex, newIndex } = event
  const task = item?.__draggable_context?.element
  if (!task?.id) return

  const payload = {
    idTodo: task.id,
    idTodoListOld: from?.dataset?.listId,
    idTodoListNew: to?.dataset?.listId,
    indexBefore: oldIndex,
    indexAfter: newIndex,
    nameTodoListOld: from?.dataset?.listName,
    nameTodoListNew: to?.dataset?.listName
  }

  handleTaskMoved(payload)
}

watch(
  () => route.path,
  (newPath) => {
    console.log('Route path changed:', newPath)
  }
)

onMounted(() => {
  fetchBoard()
})

const closeDetailModal = () => {
  showTaskDetailModal.value = false
  selectedTodoId.value = null
  const newQuery = { ...route.query }
  delete newQuery.idTodo
  router.replace({ query: newQuery })
}
const openTaskDetailModal = (taskId: string) => {
  router.replace({
    query: { ...route.query, idTodo: taskId }
  })
}

watch(
  () => route.query,
  (newQuery) => {
    const newId = newQuery.idTodo
    if (newId) {
      selectedTodoId.value = newId
      showTaskDetailModal.value = true
      fetchBoard()
    }
  },
  { immediate: true }
)
const statusPhase = ref('')
const fetchStatusByIdPhase = async () => {
  try {
    const response = await getStatusByIdPhase(
      route.params.idPhase
    )
     statusPhase.value = response.data
  } catch (error) {
    console.error('Lỗi khi lấy danh sách cột:', error)
  }
}
const canDragCheck = computed(() => {
  if(statusPhase.value =="DA_HOAN_THANH")
    return true; 
  else 
  return false
})

onMounted(()=>{
  fetchStatusByIdPhase()
})
webSocketTodoList.getUpdateIndexColumn(fetchBoard)
webSocketTodoList.getDeleteTodoList(fetchBoard)
webSocketTodoList.getCreateTodoList(fetchBoard)
webSocketTodoList.getUpdateNameTodoList(fetchBoard)
webSocketTodo.subscribeUpdateIndexTodo(fetchBoard)
webSocketTodo.getUpdateNameTodo(fetchBoard)
webSocketTodoChild.getUpdateStatusTodoChecklist(fetchBoard)
webSocketTodoChild.getDeleteTodoChecklist(fetchBoard)
webSocketTodoChild.getCreateTodoChildChecklist(fetchBoard)
webSocketTodoChild.getEditTodoChecklist(fetchBoard)
webSocketMemberProject.getJoinTodoVote(fetchBoard)
webSocketMemberProject.getOutTodoVoteMemberProject(fetchBoard)
webSocketLabelProjectTodo.getjoinLabeProjectTodo(fetchBoard)
webSocketLabelProjectTodo.getoutLabeProjectTodo(fetchBoard)
webSocketLabelProject.getCreateLabelProject(fetchBoard)
webSocketLabelProject.getUpdateLabelProject(fetchBoard)
webSocketLabelProject.getDeleteLabelProject(fetchBoard)
webSocketTodo.getDeleteDeadlineTodo(fetchBoard)
webSocketTodo.getUpdateDeadlineTodo(fetchBoard)
webSocketTodo.getupdateCompleteTodo(fetchBoard)
webSocketTodo.getUpdatePriorityLevel(fetchBoard)
webSocketMeTodoComment.getDeleteComment(fetchBoard)
webSocketMeTodoComment.getAddComment(fetchBoard)
webSocketMeTodoComment.getUpdateComment(fetchBoard)
webSocketMeTodoResource.getAddResourceTodo(fetchBoard)
webSocketMeTodoResource.getDeleteResourceTodo(fetchBoard)
webSocketMeTodoResource.getUpdateResourceTodo(fetchBoard)
webSocketImage.getChangeCoverTodo(fetchBoard)
webSocketImage.getCreateImage(fetchBoard)
webSocketImage.getDeleteImage(fetchBoard)
webSocketImage.getUpdateNameImage(fetchBoard)
webSocketSortTodo.getSortTodoProgress(fetchBoard)
webSocketSortTodo.getSortTodoCreatedDate(fetchBoard)
webSocketSortTodo.getSortTodoDeadline(fetchBoard)
webSocketSortTodo.getSortTodoName(fetchBoard)
webSocketSortTodo.getSortTodoPriority(fetchBoard)
webSocketSortTodo.getSortTodoProgress(fetchBoard)
webSocketTypeTodo.getJoinTypeTodo(fetchBoard)
webSocketTodoChild.getUpdateProgress(fetchBoard)
onMounted(() => {
  setInterval(() => {
    currentTime.value = Date.now()
  }, 1000)
})

watch(
  () => route.path,
  (newPath) => {
    console.log('Route path changed:', newPath)
  }
)

// Xác định trạng thái hiển thị dựa trên route
const isTableView = computed(() => route.path.includes('/table'))

const toggleView = () => {
  const projectId = route.params.id
  const phaseId = route.params.idPhase
  if (userLogin.roleScreen == ROLES.MANAGE) {
    if (isTableView.value) {

      router.push({
        name: `${ROUTES_CONSTANTS.MANAGE.children.PROJECT_DETAIL.name}`,
        params: {
          idProject: projectId,
          idPhase: phaseId
        }
      })

    } else {

      router.push({
        name: `${ROUTES_CONSTANTS.MANAGE.children.PROJECT_DETAIL.children.TABLE.name}`,
        params: {
          idProject: projectId,
          idPhase: phaseId
        }
      })

    }
  } else if (userLogin.roleScreen == ROLES.MEMBER) {

    if (isTableView.value) {

      router.push({
        name: `${ROUTES_CONSTANTS.MEMBER.children.PROJECT_DETAIL.name}`,
        params: {
          idProject: projectId,
          idPhase: phaseId
        }
      })

    } else {

      router.push({
        name: `${ROUTES_CONSTANTS.MEMBER.children.PROJECT_DETAIL.children.TABLE.name}`,
        params: {
          idProject: projectId,
          idPhase: phaseId
        }
      })

    }

  }

}
</script>

<style scoped>
.scroll-container {
  scrollbar-width: thin;
  scrollbar-color: rgba(100, 100, 100, 0.3) transparent;
  overflow: auto;
}

.scroll-container::-webkit-scrollbar {
  height: 8px;
}

.scroll-container::-webkit-scrollbar-track {
  background: transparent;
}

.scroll-container::-webkit-scrollbar-thumb {
  background-color: rgba(100, 100, 100, 0.4);
  border-radius: 4px;
}

.column-chosen {
  transition: transform 0.2s ease;
}

.column-ghost {
  opacity: 0 !important;
  transform: scale(0.98);
}

.column-dragging {
  z-index: 50;
  transform: scale(1.05);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.3);
}

.ghost {
  opacity: 0 !important;
  transform: scale(0.98);
}

.chosen {
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
  border: 2px solid #38bdf8;
}

.dragging {
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
</style>
