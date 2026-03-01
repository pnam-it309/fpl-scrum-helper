<template>
  <!-- <BreadcrumbDefault label="Quản lý công việc"> -->
  <TodoListFilter
    :searchQuery="statetodo.search.searchQuery"
    @update:searchQuery="updateSearchQueryTodo"
  />

  <TodoProjectTable
    :searchQuery="statetodo.search.searchQuery"
    :todo="statetodo.todo"
    :todoId="statetodo.idTodo"
    :paginationParams="statetodo.paginationParams"
    :totalItems="statetodo.totalItems"
    @page-change="handlePageChangeTodo"
    @refresh-todo="fetchTodoProject"
    @updatePriority="handlePriorityUpdate"
    @download-template-import="downloadTemplateExcel"
    @open-modal-import-todo-list="handleOpenModalImport"
    @import-log="handleOpenModalImportHistory"
    @download-import-log="downloadImportLogExcel"
    @open-todo-modal="openTodoModal"
  />
  <!-- </BreadcrumbDefault> -->

  <TodoListImportModal
    :open="state.isOpenModalImport"
    :title="'Import công việc'"
    @close="handleCloseModalImport"
    @fetch-all="fetchTodoProject"
    @update-log="handleNewLog"
    @fetch-all-history="fetchHistory"
  />

  <a-modal
    v-model:visible="statetodo.isOpenModalImportHistory"
    :width="1300"
    :footer="null"
    :z-index="9999"
    :wrap-style="{ display: 'flex', alignItems: 'center', justifyContent: 'center' }"
    :style="{ top: '10px' }"
    @cancel="statetodo.isOpenModalImportHistory = false"
  >
    <HistoryImportTodoModalTable
      :history="statetodo.history"
      :paginationParams="{
        page: statetodo.paginationParamsHistory.page,
        size: statetodo.paginationParamsHistory.size
      }"
      :totalItems="statetodo.totalItemsHistory"
      @close="handleCloseModalImportHistory"
      @page-change="handlePageChangeTodoLog"
    />
  </a-modal>

  <TodoModal
    v-model:open="statetodo.isTodoModal"
    :id-project="projectId"
    @close="closeTodoModal"
    @success="handleSuccessCreateNewTodo"
  />

  
</template>

<script lang="ts" setup>
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import {
  downloadExcelTodoList,
  downloadImportLogTodoList,
  getAllHistoryTodo,
  getAllTodoList,
  getAllTodoProject,
  historyImprortTodo,
  ParamsGetTodoList,
  TodoListProjectResponse,
  TodoProjectResponse
} from '@/services/api/manage/todolist/todolist.api'
import { webSocketMemberProject } from '@/services/service/member/memberproject.socket'
import { webSocketTodo } from '@/services/service/member/metodo.socket'
import { TodoListResponse } from '@/services/service/member/metodolist.socket'
import { debounce } from 'lodash'
import { onMounted, reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import TodoListFilter from './TodoListFilter.vue'
import TodoListImportModal from './TodoListImportModal.vue'
import TodoListModalImportLog from './TodoListModalImportLog.vue'
import TodoProjectTable from './TodoProjectTable.vue'
import HistoryImportTodoModalTable from './HistoryImportTodoModalTable.vue'

import TodoModal from './TodoModal.vue'
import BreadcrumbDefault from '@/components/custom/Div/BreadcrumbDefault.vue'

const route = useRoute()
const projectId = ref<string | null>((route.params.id as string) || null)

const state = reactive({
  searchQuery: '',
  search: null as string | null,
  isModalOpen: false,
  todoListId: null as string | null,
  todoList: [] as TodoListProjectResponse[],
  paginationParams: { page: 1, size: 10 },
  totalItems: 0,
  isOpenModalImport: false
  // selectedDepartmentFacilityId: null as string | null,
})

const fetchTodoList = async () => {
  try {
    if (!projectId.value) return

    const params: ParamsGetTodoList = {
      page: state.paginationParams.page,
      size: state.paginationParams.size,
      search: state.searchQuery || ''
    }

    const response = await getAllTodoList(params, projectId.value)
    state.todoList = response?.data?.data || []
    state.totalItems = response?.data?.totalElements || 0

    console.log('Fetched data:', state.todoList)
  } catch (error) {
    console.error('Lỗi khi tải danh sách task:', error)
  }
}

onMounted(fetchTodoList)

const debouncedFetchTodoList = debounce(fetchTodoList, 300)

watch(
  () => state.searchQuery,
  () => {
    state.paginationParams.page = 1
    debouncedFetchTodoList()
  }
)

const downloadTemplateExcel = async () => {
  try {
    const response = await downloadExcelTodoList()
    const blob = new Blob([response], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    })
    const url = URL.createObjectURL(blob)

    const link = document.createElement('a')
    link.href = url
    link.download = 'template_import_todo_list.xlsx'
    link.click()

    URL.revokeObjectURL(url)
  } catch (error) {
    console.error('Failed to download template:', error)
  }
}

const handleOpenModalImport = () => {
  state.isOpenModalImport = true
}

const handleCloseModalImport = () => {
  state.isOpenModalImport = false
}

const logRef = ref()

const handleNewLog = (newEntry) => {
  if (logRef.value) {
    logRef.value.addNewLogEntry(newEntry) // Thêm log mới vào bảng Import Log
  }
}

const downloadImportLogExcel = async () => {
  try {
    const blob = await downloadImportLogTodoList()

    const url = window.URL.createObjectURL(new Blob([blob]))

    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', 'import-log.csv')
    document.body.appendChild(link)
    link.click()

    // Dọn dẹp URL sau khi tải xong
    window.URL.revokeObjectURL(url)
    link.remove()
  } catch (error) {
    console.error('Lỗi khi tải file:', error)
  }
}

//todoProject
const statetodo = reactive({
  search: {
    searchQuery: '',
    level: '' as string | undefined,
    status: '' as string | undefined
  },
  isModalOpen: false,
  idTodo: null as string | null,
  nameTodo: null as string | null,
  todo: [] as TodoProjectResponse[],
  paginationParams: { page: 1, size: 10 },
  paginationParamsHistory: { page: 1, size: 10 },
  totalItems: 0,
  totalItemsHistory: 0,
  isOpenModalImport: false,
  isTodoModal: false,
  isOpenModalImportHistory: false,
  history: [] as historyImprortTodo[]
})

const fetchTodoProject = async () => {
  try {
    if (!projectId.value) return

    const params: ParamsGetTodoList = {
      page: statetodo.paginationParams.page,
      size: statetodo.paginationParams.size,
      search: statetodo.search.searchQuery ?? '',
      level: statetodo.search.level || null,
      status: statetodo.search.status || null
    }

    console.log('Params gọi API:', params)

    const response = await getAllTodoProject(params, projectId.value)
    statetodo.todo = response?.data?.data || []
    statetodo.totalItems = response.data.totalElements
    console.log(statetodo.todo)
    console.log('Total items from API:', response?.data?.totalElements)

    statetodo.todo = statetodo.todo.filter(
      (value, index, self) => index === self.findIndex((t) => t.idTodo === value.idTodo)
    )

    statetodo.totalItems = response?.data?.totalElements || 0
  } catch (error) {
    console.error('Lỗi khi tải danh sách task:', error)
  }
}

onMounted(fetchTodoProject)

const fetchHistory = async () => {
  try {
    const response = await getAllHistoryTodo(route.params.id as string)
    console.log('Response data:', response)

    if (response?.data?.data) {
      statetodo.history = response.data.data
      statetodo.totalItemsHistory = response.data.totalElements
    } else {
      statetodo.history = []
      statetodo.totalItems = 0
    }
  } catch (error) {
    console.error('Lỗi khi tải lịch sử import:', error)
  }
}

onMounted(fetchHistory)

const handlePageChangeTodoLog = (pagination: any) => {
  statetodo.paginationParamsHistory.page = pagination.page
  statetodo.paginationParamsHistory.size = pagination.pageSize
  fetchHistory()
}

const handleOpenModalImportHistory = () => {
  statetodo.isOpenModalImportHistory = true
}

const handleCloseModalImportHistory = () => {
  statetodo.isOpenModalImportHistory = false
}

const handlePageChangeTodo = ({ page, pageSize }: { page: number; pageSize?: number }) => {
  statetodo.paginationParams.page = page
  if (pageSize) {
    statetodo.paginationParams.size = pageSize
  }
  fetchTodoProject()
}

const debouncedFetchTodoProject = debounce(fetchTodoProject, 300)

watch(
  () => [statetodo.search.searchQuery, statetodo.search.level, statetodo.search.status],
  () => {
    statetodo.paginationParams.page = 1
    debouncedFetchTodoProject()
  }
)

const updateSearchQueryTodo = ({
  newQuery,
  newLevel,
  newStatus
}: {
  newQuery: string
  newLevel: string | undefined
  newStatus: string | undefined
}) => {
  statetodo.search.searchQuery = newQuery.trim()
  statetodo.search.level = newLevel
  statetodo.search.status = newStatus
}

const handlePriorityUpdate = ({ idTodo, priorityLevel }) => {
  const todoItem = statetodo.todo.find((item) => item.idTodo === idTodo)
  if (todoItem) {
    todoItem.priorityLevel = priorityLevel
  }

  fetchTodoProject()
}

// create new todo
const openTodoModal = () => {
  statetodo.isTodoModal = true
}

const closeTodoModal = () => {
  statetodo.isTodoModal = false
}

const handleSuccessCreateNewTodo = () => {
  fetchTodoProject()
}

webSocketTodo.getUpdatePriorityLevel((record) => {
  if (statetodo.idTodo == record.idTodo) {
    fetchTodoProject
  }
})

webSocketTodo.getUpdateNameTodo(() => {
  fetchTodoProject()
})

webSocketMemberProject.getJoinTodoVote(() => {
  fetchTodoProject()
})
webSocketMemberProject.getOutTodoVoteMemberProject(() => {
  fetchTodoProject()
})

webSocketTodo.getUpdatePriorityLevel(() => {
  fetchTodoProject()
})
</script>
<style scoped>
/* Áp dụng gradient cho nút */
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
</style>
