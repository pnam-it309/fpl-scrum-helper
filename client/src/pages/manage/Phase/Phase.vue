<template>
  <!-- <<<<<<< HEAD -->

  <BreadcrumbDefaultV1 :routes="breadcrumbRoutes" :label="'Quản lý giai đoạn'" marginBottom="mb-2">
    <DivCustom :label="'Bộ lọc '" :enableToggle="true" :isOpened="false"  customClasses="mt-2">
      <template #icon>
        <FilterOutlined />
      </template>

      <FilterPhase @search-name="handleSearchName" @time="handleTime" />
    </DivCustom>

    <PhaseDetail
      :id-project="idProject"
      :id-todo="idTodo"
      :dataTodo="dataSprint.dataSprint"
      :sprints="sprints"
      :idSprintTodo="idSprintTodo"
      @update-sprint-todos="updateSprintTodos"
      @remove-task-from-sprint="removeTaskFromSprint"
    />
  </BreadcrumbDefaultV1>
</template>

<script setup lang="ts">
const breadcrumbRoutes = [
  { name: 'Quản lý xưởng', nameRoute: ROUTES_CONSTANTS.MANAGE.name },
    { name: 'Tổng quan dự án', nameRoute: ROUTES_CONSTANTS.MANAGE.children.PROJECT.name },
    { name: 'Giai đoạn', nameRoute: ROUTES_CONSTANTS.MANAGE.children.PHASE.name },
]
import PhaseDetail from './PhaseDetail.vue'

import { onMounted, reactive, ref, watch } from 'vue'
import Todo from './Todo.vue'
import Sprint from './Sprint.vue'
import { getAllTodo, todoResponse } from '@/services/api/manage/todo/todo.api'
import { todoWebSocket } from '@/services/api/manage/todo/todo.socket.api'
import { dataPhase, dataSprintProject, phaseResponse } from '@/services/api/manage/phase/phase.api'
import dayjs from 'dayjs'
import { phaseWebSocket } from '@/services/api/manage/phase/phase.socket.api'
import { todoVoteWebSocket } from '@/services/api/manage/todo/todovote.socket.api'
import { useRoute, useRouter } from 'vue-router'
import { detailProject } from '@/services/api/manage/project/maproject.api'
import { projectDetailResponse } from '@/services/api/admin/project/project.api'
import { FilterOutlined, LineChartOutlined, SearchOutlined } from '@ant-design/icons-vue'
import { ROUTES_CONSTANTS } from '@/constants/path'
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import FilterPhase from './FilterPhase.vue'
import BreadcrumbDefault from '@/components/custom/Div/BreadcrumbDefault.vue'
import Chart from '@/pages/member/project/chart/Chart.vue'
import BreadcrumbDefaultV1 from '@/components/custom/Div/BreadcrumbDefaultV1.vue'

const paramsTodo = reactive({
  page: 1,
  size: 100000000,
  totalItem: '',
  idProject: '',
  search: '',
  time: ''
})

const search = ref('')

const dataSprint = reactive({
  dataSprint: [] as phaseResponse[]
})

const handleSearchName = (value: string) => {
  search.value = value
  fetchDataTodo()
}

const handleTime = (value: string) => {
  paramsTodo.time = dayjs(value).valueOf() as unknown as string
}
const dataTodo = reactive({
  data: [] as todoResponse[]
})

watch(search, (newSearch) => {
  search.value = newSearch
})

const route = useRoute()

const idProject = ref('')

const removeTaskFromSprint = (sprintId: string, todo) => {
  if (Array.isArray(todo)) {
    // Xử lý nhiều todo
    todo.forEach((t) => {
      const index = idSprintTodo.value[sprintId]?.findIndex((item) => item.id === t.id)
      if (index !== -1) {
        idSprintTodo.value[sprintId].splice(index, 1)
      }
      phaseWebSocket.sendMessageDeleteTodoByPhase(t.id)

      if (!dataTodo.data.some((item) => item.id === t.id)) {
        dataTodo.data.push(t)
      }
    })
  } else {
    // Xử lý một todo
    const index = idSprintTodo.value[sprintId]?.findIndex((t) => t.id === todo.id)
    if (index !== -1) {
      idSprintTodo.value[sprintId].splice(index, 1)
    }
    phaseWebSocket.sendMessageDeleteTodoByPhase(todo.id)

    if (!dataTodo.data.some((t) => t.id === todo.id)) {
      dataTodo.data.push(todo)
    }
  }
}

const sprints = ref([]) // Danh sách các Sprint

const idSprintTodo = ref<Record<string, any[]>>({}) // Task trong từng Sprint

const idTodo = ref<string>()

const valueProject = ref<projectDetailResponse>()

// ✅ Cập nhật danh sách Sprint và loại bỏ Todo khi kéo vào Sprint
const updateSprintTodos = async ({ sprintId, tasks }) => {
  idSprintTodo.value[sprintId] = tasks

  // ✅ Xóa Todo đã được thêm vào Sprint khỏi danh sách chung
  const taskIds = new Set(tasks.map((task) => task.id))
  dataTodo.data = dataTodo.data.filter((todo) => !taskIds.has(todo.id))
}

watch(search, (newSearch) => {
  paramsTodo.search = newSearch

  console.log(paramsTodo)

  fetchDataPhase()
})

watch(
  () => paramsTodo.time,
  (newTime) => {
    if (newTime) {
      paramsTodo.time = dayjs(newTime).valueOf()
    } else {
      paramsTodo.time = ''
    }

    fetchDataPhase()
  }
)

const fetchDataTodo = async () => {
  try {
    console.table(paramsTodo)
    const resDetail = await detailProject(idProject.value)
    valueProject.value = resDetail.data
    console.log(valueProject.value.name)

    const res = await getAllTodo(paramsTodo)
    dataTodo.data = res.data as unknown as todoResponse[]
  } catch (error) {
    console.error('❌ Lỗi khi lấy dữ liệu todo:', error)
  }
}

const fetchDataPhase = async () => {
  paramsTodo.search = search.value
  console.log(paramsTodo.search + 'lslls')

  const res = await dataSprintProject(paramsTodo)
  dataSprint.dataSprint = res.data.content.map((item) => ({
    ...item,
    startTime: item.startTime ? dayjs(Number(item.startTime)) : null,
    endTime: item.endTime ? dayjs(Number(item.endTime)) : null,
    expanded: false
  })) as unknown as phaseResponse[]

  dataSprint.dataSprint.forEach((sprint) => {
    idSprintTodo.value[sprint.id as string] = []
  })
}

onMounted(() => {
  idProject.value = route.params.id as string
  paramsTodo.idProject = idProject.value as string
  fetchDataTodo()
  fetchDataPhase()
  console.log('✅ WebSocket đã kết nối thành công!')
  todoWebSocket.subscribeCreateTodo(fetchDataTodo)
  todoWebSocket.subscribeDeleteTodo(fetchDataTodo)
  todoWebSocket.subscribeUpdateTodo(fetchDataTodo)
  todoVoteWebSocket.subscribeCreateTodoVote(fetchDataTodo)
  todoVoteWebSocket.subscribeDeleteTodoVote(fetchDataTodo)
  phaseWebSocket.subscribeCreatePhase(fetchDataPhase)
  phaseWebSocket.subscribeDeleteTodoByPhase(fetchDataPhase)
  phaseWebSocket.subscribeDeletePhase(fetchDataPhase)
  phaseWebSocket.subscribeUpdatePhase(fetchDataPhase)
  phaseWebSocket.subscribeCreateTodoByPhase(fetchDataPhase)
})
const router = useRouter()

const goToSprint = () => {
  router.push({ name: 'phase', params: { id: idProject.value } }) // Sử dụng name thay vì path
}
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
