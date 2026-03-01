<template>
  <!-- <BreadcrumbDefault label="Quản lý công việc"> -->
  <PhaseFilter :searchQuery="statetodo.searchQuery" @update:searchQuery="updateSearchQueryTodo" />

  <!-- <PhaseTable 
        :searchQuery="state.searchQuery" 
        :phase="state.phase" 
        :paginationParams="state.paginationParams"
        :totalItems="state.totalItems"
         @page-change="handlePageChange" /> -->

  <TodoProjectMemberTable
    :searchQuery="statetodo.searchQuery"
    :todo="statetodo.todo"
    :todoId="statetodo.idTodo"
    :paginationParams="statetodo.paginationParams"
    :totalItems="statetodo.totalItems"
    @page-change="handlePageChangeTodo"
    @refresh-todo="fetchTodoProject"
    @updatePriority="handlePriorityUpdate"
  />
  <!-- </BreadcrumbDefault> -->
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { debounce } from 'lodash'
import PhaseTable from './PhaseTable.vue'
import PhaseFilter from './PhaseFilter.vue'
import TodoProjectMemberTable from './TodoProjectMemberTable.vue'
import { toast } from 'vue3-toastify'
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import {
  detailPhase,
  getAllPhase,
  getAllTodoProject,
  getAllTodoProjectMember,
  MBPhaseResponse,
  ParamsGetMBPhase,
  TodoProjecMemBertResponse
} from '@/services/api/member/phase/phase.api'
import { useRoute } from 'vue-router'
import { webSocketTodo } from '@/services/service/member/metodo.socket'
import { webSocketMemberProject } from '@/services/service/member/memberproject.socket'
import BreadcrumbDefault from '@/components/custom/Div/BreadcrumbDefault.vue'

const route = useRoute()
const projectId = ref<string | null>((route.params.id as string) || null)

const state = reactive({
  searchQuery: '',
  search: null as string | null,
  isModalOpen: false,
  id: null as string | null,
  phase: [] as MBPhaseResponse[],
  paginationParams: { page: 1, size: 10 },
  totalItems: 0
})

const fetchPhase = async () => {
  try {
    const params: ParamsGetMBPhase = {
      page: state.paginationParams.page,
      size: state.paginationParams.size,
      search: state.searchQuery || ''
    }

    console.log('Danh sách dữ liệu phân trang')

    console.log(params)

    const response = await getAllPhase(params, projectId.value || '')
    state.phase = response?.data?.content || []
    state.totalItems = response?.data?.totalElements || 0
    console.log('du lieu: ', response.data)
    console.log('du lieu1: ', state.phase)
  } catch (error) {
    console.error('Lỗi khi tải danh sách sprint:', error)
  }
}

const projectDetail = ref(null)
onMounted(async () => {
  if (projectId) {
    try {
      const response = await detailPhase(route.params.id as string)
      projectDetail.value = response.data // Lưu dữ liệu vào state
      console.log(response.data.id + 'dddddđ')
    } catch (error) {
      console.error('Lỗi khi lấy dữ liệu báo cáo:', error)
    }
  }
})

onMounted(fetchPhase)

const handlePageChange = ({ page, pageSize }: { page: number; pageSize?: number }) => {
  state.paginationParams.page = page
  if (pageSize) {
    state.paginationParams.size = pageSize
  }
  fetchPhase()
}

const debounced = debounce(fetchPhase, 300)

const updateSearchQuery = (newQuery: string) => {
  state.searchQuery = newQuery.trim()
}

watch(
  () => state.searchQuery,
  () => {
    state.paginationParams.page = 1
    debounced()
  }
)

//todoProject
const statetodo = reactive({
  searchQuery: '',
  search: null as string | null,
  isModalOpen: false,
  todo: [] as TodoProjecMemBertResponse[],
  paginationParams: { page: 1, size: 10 },
  totalItems: 0,
  isOpenModalImport: false,
  phaseId: null as string | null,
  idTodo: null as string | null
})

const fetchTodoProject = async () => {
  try {
    if (!projectId.value) return

    const params: ParamsGetMBPhase = {
      page: statetodo.paginationParams.page,
      size: statetodo.paginationParams.size,
      search: statetodo.searchQuery || ''
    }

    const response = await getAllTodoProject(params, projectId.value)
    statetodo.todo = response?.data?.data || []
    statetodo.todo = statetodo.todo.filter(
      (value, index, self) => index === self.findIndex((t) => t.idTodo === value.idTodo)
    )
    statetodo.totalItems = response?.data?.totalElements || 0
  } catch (error) {
    console.error('Lỗi khi tải danh sách task:', error)
  }
}

onMounted(fetchTodoProject)

const handlePageChangeTodo = ({ page, pageSize }: { page: number; pageSize?: number }) => {
  statetodo.paginationParams.page = page
  if (pageSize) {
    statetodo.paginationParams.size = pageSize
  }
  fetchTodoProject()
}

const debouncedFetchTodoProject = debounce(fetchTodoProject, 300)

watch(
  () => statetodo.searchQuery,
  () => {
    statetodo.paginationParams.page = 1
    debouncedFetchTodoProject()
  }
)

const updateSearchQueryTodo = (newQuery: string) => {
  statetodo.searchQuery = newQuery.trim()
}

const handlePriorityUpdate = ({ idTodo, priorityLevel }) => {
  const todoItem = statetodo.todo.find((item) => item.idTodo === idTodo)
  if (todoItem) {
    todoItem.priorityLevel = priorityLevel
  }

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
<style scoped></style>
