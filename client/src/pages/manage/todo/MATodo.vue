<template>
  <!-- Thanh header cố định -->
  <div class="sticky top-0 z-10 w-full bg-white shadow-sm px-4 py-4 flex flex-col bg-white">
    <!-- Dòng 1 -->
    <div class="flex items-center justify-center w-full mb-5 gap-8">
      <div class="flex items-center gap-2">
        <span class="text-base font-semibold text-gray-500">Dự án:</span>
        <span class="text-xl font-medium text-gray-900 tracking-wide">{{
          valueProject?.name
        }}</span>
      </div>
      <div class="flex items-center gap-2">
        <span class="text-base font-semibold text-gray-500">Trạng thái:</span>
        <span
          class="text-sm font-semibold px-3 py-1 rounded-lg transition align-middle"
          :class="{
            'bg-gray-100 text-gray-500': valueProject?.statusProject === 'CHUA_DIEN_RA',
            'bg-blue-100 text-blue-600': valueProject?.statusProject === 'DANG_DIEN_RA',
            'bg-red-100 text-red-500': valueProject?.statusProject === 'DA_DIEN_RA'
          }"
        >
          {{
            valueProject?.statusProject === 'CHUA_DIEN_RA'
              ? 'Chưa diễn ra'
              : valueProject?.statusProject === 'DANG_DIEN_RA'
              ? 'Đang diễn ra'
              : 'Đã diễn ra'
          }}
        </span>
      </div>
    </div>

    <!-- Dòng 2 -->
    <div class="flex items-center w-full gap-4 min-h-[40px]">
      <!-- Thành viên  -->
      <div class="flex items-center gap-2 min-w-[170px] h-10">
        <span class="text-base font-medium text-gray-700 whitespace-nowrap">Thành viên:</span>
        <a-avatar-group :max-count="3" size="large">
          <a-tooltip
            v-for="(user, index) in dataStaffProject"
            :key="index"
            :title="user.staff == null ? user.student.name : user.staff.name"
          >
            <a-avatar
              :src="user.staff == null ? user.student.image : user.staff.picture"
              size="large"
              class="shadow ring-2 ring-white"
              :style="{ width: '32px', height: '32px', fontSize: '16px' }"
            />
          </a-tooltip>
        </a-avatar-group>
      </div>

      <!-- Search box -->
      <div class="flex-1 flex justify-center items-center h-10">
        <a-input
          placeholder="Tìm kiếm công việc..."
          class="h-10 text-base px-3 focus:bg-white focus:ring-2 focus:ring-blue-200 border border-gray-200 rounded-lg transition w-full max-w-xl font-normal text-gray-700 flex items-center"
          v-model:value="search"
          style="box-shadow: none"
        >
          <template #prefix>
            <SearchOutlined class="text-gray-400" />
          </template>
        </a-input>
      </div>

      <!-- Nút chức năng  -->
      <div class="flex items-center gap-2 min-w-[280px] justify-end h-10">
        <button
          @click="openModalBurnDown"
          class="flex flex-col items-center justify-center px-1 py-0.5 hover:bg-blue-50 rounded transition min-w-[55px] h-10"
        >
          <DotChartOutlined class="text-lg text-blue-600 mb-0.5" />
          <span class="text-xs font-medium text-gray-700 leading-tight">Thống kê</span>
        </button>
        <button
          @click="openModalVelo"
          class="flex flex-col items-center justify-center px-1 py-0.5 hover:bg-blue-50 rounded transition min-w-[67px] h-10"
        >
          <BarChartOutlined class="text-lg text-emerald-600 mb-0.5" />
          <span class="text-xs font-medium text-gray-700 leading-tight">Lịch sử GĐ</span>
        </button>
        <button
          @click="goToSprint"
          class="flex flex-col items-center justify-center px-1 py-0.5 hover:bg-blue-50 rounded transition min-w-[56px] h-10"
        >
          <DatabaseOutlined class="text-lg text-indigo-600 mb-0.5" />
          <span class="text-xs font-medium text-gray-700 leading-tight">Giai đoạn HT</span>
        </button>
        <button
          @click="goToPointEsmirate"
          class="flex flex-col items-center justify-center px-1 py-0.5 hover:bg-blue-50 rounded transition min-w-[70px] h-10"
        >
          <MehOutlined class="text-lg text-amber-500 mb-0.5" />
          <span class="text-xs font-medium text-gray-700 leading-tight">Ước lượng NL</span>
        </button>

        <button
          @click="openActivityDrawer"
          class="flex flex-col items-center justify-center px-1 py-0.5 hover:bg-blue-50 rounded transition min-w-[70px] h-10"
        >
          <HistoryOutlined class="text-lg text-rose-500 mb-0.5" />
          <span class="text-xs font-medium text-gray-700 leading-tight">Theo dõi</span>
        </button>
      </div>
    </div>
  </div>

  <transition
    enter-active-class="transition-transform duration-300 ease-in-out"
    enter-from-class="translate-x-full"
    enter-to-class="translate-x-0"
    leave-active-class="transition-transform duration-300 ease-in-out"
    leave-from-class="translate-x-0"
    leave-to-class="translate-x-full"
  >
    <div
      v-if="isActivityDrawerVisible"
      class="fixed top-0 right-0 h-full w-full sm:w-[450px] bg-white shadow-2xl z-50 transform"
    >
      <div class="flex justify-between items-center p-4 border-b bg-gray-50">
        <h2 class="text-base font-semibold text-gray-800">Hoạt động</h2>
        <button
          @click="closeActivityDrawer"
          class="text-gray-500 hover:text-red-500 text-xl font-bold"
        >
          ✖
        </button>
      </div>

      <div class="p-2 overflow-y-auto h-[calc(100%-56px)]">
        <ActivitySidebar @closeAll="closeActivityDrawer" />
      </div>
    </div>
  </transition>

  <BreadcrumbDefaultV3NoMinScreen
    :routes="breadcrumbRoutes"
    :label="'Bảng giai đoạn & khối lượng công việc'"
    marginBottom="mb-2"
  >
    <!-- <div class="h-full"> -->
    <DivCustom label="Giai đoạn & Công việc">
      <!-- Cột trái -->

      <div v-if="!showStageVote" :class="showStageVote ? 'w-3/5' : 'w-full'">
        <Sprint
          :search="search"
          :id-project="idProject"
          :id-todo="idTodo"
          :dataTodo="dataSprint.dataSprint"
          :sprints="sprints"
          :staff="dataStaffProject"
          @update-sprint-todos="updateSprintTodos"
          @remove-task-from-sprint="removeTaskFromSprint"
        />

        <Todo
          :todos="dataTodo.data"
          @update-todos="updateTodos"
          :search="search"
          :idProject="idProject"
          @data-staff-project="handleDatastaff"
          @idTodo="idTodoModal"
        />
      </div>
      <a-button
        type="primary"
        @click="showStageVote = false"
        style="position: absolute; top: 0.75rem; left: 0.75rem"
      >
        Quay lại
      </a-button>
      <!-- Cột phải -->
      <DivCustom v-if="showStageVote" :class="'w-full'" style="position: relative">
        <a-button
          @click="closeStageVote"
          style="
            position: absolute !important;
            top: 0.75rem !important;
            right: 0.75rem !important;
            background: transparent !important;
            border: none !important;
            box-shadow: none !important;
            transition: none !important;
            outline: 2px solid #1890ff !important;
            border-radius: 8% !important;
            padding: 4px !important;
          "
        >
          Quay lại
        </a-button>

        <StageVote :members="dataStaffProject" />
      </DivCustom>
    </DivCustom>
    <!-- </div> -->
  </BreadcrumbDefaultV3NoMinScreen>
  <TodoDetailModal
    v-model="showTaskDetailModal"
    :todoId="selectedTodoId"
    @close="showTaskDetailModal = false"
  />

  <StageVoteModal
    v-model="stageVoteModal"
    :open="dataStage.openModal"
    :title="'Tạo cuộc bình chọn '"
    @close="closeModal"
    :data-phase="dataSprint.dataSprint"
  />

  <VelocityChart
    :open="stageModalVelo.openVelo"
    @close="closeModalVelo"
    :title="'Lịch sử hoàn thành'"
  />

  <BurnDownChart
    :open="stageModalBurnDown.openBurnDown"
    @close="closeModalBurnDown"
    :title="'Thống kê'"
  />
</template>

<script setup lang="ts">
const isActivityDrawerVisible = ref(false)

const openActivityDrawer = () => {
  isActivityDrawerVisible.value = true
}

const closeActivityDrawer = () => {
  isActivityDrawerVisible.value = false
}

const breadcrumbRoutes = [
  { name: 'Thành viên dự án', nameRoute: ROUTES_CONSTANTS.MEMBER.name },
  { name: 'Dự án', nameRoute: ROUTES_CONSTANTS.MEMBER.children.MYPROJECT.name },
  { name: 'Giai đoạn', nameRoute: ROUTES_CONSTANTS.MEMBER.children.PHASE.name },
]

import { h, onMounted, reactive, ref, watch } from 'vue'
import Todo from './Todo.vue'
import Sprint from './Sprint.vue'
import { getAllTodo, todoResponse } from '@/services/api/manage/todo/todo.api'
import { dataPhase, phaseResponse } from '@/services/api/manage/phase/phase.api'
import dayjs from 'dayjs'
import { phaseWebSocket } from '@/services/api/manage/phase/phase.socket.api'
import { todoVoteWebSocket } from '@/services/api/manage/todo/todovote.socket.api'
import { useRoute, useRouter } from 'vue-router'
import { detailProject } from '@/services/api/manage/project/maproject.api'
import { projectDetailResponse } from '@/services/api/admin/project/project.api'
import {
  AppstoreOutlined,
  BarChartOutlined,
  CloseOutlined,
  DatabaseOutlined,
  DotChartOutlined,
  DownOutlined,
  HistoryOutlined,
  LikeOutlined,
  LineChartOutlined,
  MailOutlined,
  MehOutlined,
  SearchOutlined
} from '@ant-design/icons-vue'
import { useProjectStore } from '@/utils/store'
import TodoDetailModal from '@/pages/member/project/projectdetails/projectmodal/TodoDetailModal.vue'
import StageVoteModal from './StageVoteModal.vue'
import StageVote from './StageVote.vue'
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import { MenuProps } from 'ant-design-vue'
import DivCustomOld from '@/components/custom/Div/DivCustomOld.vue'
import BreadcrumbDefault from '@/components/custom/Div/BreadcrumbDefault.vue'
import { ROUTES_CONSTANTS } from '@/constants/path'
import VelocityChart from '@/pages/member/project/chart/VelocityChart.vue'
import BurnDownChart from '@/pages/member/project/chart/BurnDownChart.vue'
import BreadcrumbDefaultV1 from '@/components/custom/Div/BreadcrumbDefaultV1.vue'
import BreadcrumbDefaultV3NoMinScreen from '@/components/custom/Div/BreadcrumbDefaultV3NoMinScreen.vue'
import ActivitySidebar from '@/components/ui/Header/ActivitySidebar.vue'
import { webSocketTodoChild } from '@/services/service/member/todochild.socket'
import { debounce } from 'lodash'

const dataStage = reactive({
  openModal: false,
  stage: '',
  days: 3,
  endDate: null
})

const showStageVote = ref(false)

const stageModalVelo = reactive({
  openVelo: false
})

const closeModalVelo = () => {
  stageModalVelo.openVelo = false
}
const openModalVelo = () => {
  stageModalVelo.openVelo = true
}

const stageModalBurnDown = reactive({
  openBurnDown: false
})

const closeModalBurnDown = () => {
  stageModalBurnDown.openBurnDown = false
}
const openModalBurnDown = () => {
  stageModalBurnDown.openBurnDown = true
}

// Hàm đóng StageVote
function closeStageVote() {
  showStageVote.value = false
}
const handleDropdownClick = (e: any) => {
  if (e.key === '1') {
    // Tạo cuộc bình chọn
    dataStage.openModal = true
    showStageVote.value = false
  } else if (e.key === '2') {
    // Chi tiết các cuộc bình chọn
    showStageVote.value = true
  }
}

const openModal = () => {
  dataStage.openModal = true
}

const closeModal = () => {
  dataStage.openModal = false
}

const paramsTodo = reactive({
  page: 1,
  size: 1000000,
  totalItem: '',
  idProject: '',
  search: ''
})

const project = useProjectStore()

const dataStaffProject = ref([])

const search = ref('')

const dataSprint = reactive({
  dataSprint: [] as phaseResponse[]
})

const dataTodo = reactive({
  data: [] as todoResponse[]
})

watch(search, (newSearch) => {
  console.log(newSearch)

  search.value = newSearch
})

const showTaskDetailModal = ref(false)

const selectedTodoId = ref<string | null>(null)

const idTodoModal = (data) => {
  selectedTodoId.value = data
  showTaskDetailModal.value = true
}

const route = useRoute()

const open = ref(false)

const openDrawer = () => {
  open.value = true
}

const handleDatastaff = (data) => {
  dataStaffProject.value = data
}

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

// const updateTodos = (newTodos) => {
//   dataTodo.data = newTodos
// }

const valueProject = ref<projectDetailResponse>()

// ✅ Cập nhật danh sách Sprint và loại bỏ Todo khi kéo vào Sprint
const updateSprintTodos = async ({ sprintId, tasks }) => {
  idSprintTodo.value[sprintId] = tasks

  // ✅ Xóa Todo đã được thêm vào Sprint khỏi danh sách chung
  const taskIds = new Set(tasks.map((task) => task.id))
  dataTodo.data = dataTodo.data.filter((todo) => !taskIds.has(todo.id))
}

watch(search, (newSearch) => {
  console.log('Giá trị tìm kiếm mới:', newSearch)
  paramsTodo.search = newSearch
  fetchDataTodo()
})

const fetchDataTodo = async () => {
  try {
    const resDetail = await detailProject(idProject.value)
    valueProject.value = resDetail.data

    console.log('Dữ liệu request')

    console.log(paramsTodo)

    const res = await getAllTodo(paramsTodo)
    dataTodo.data = res.data as unknown as todoResponse[]

    console.log(dataTodo.data)
  } catch (error) {
    console.error('❌ Lỗi khi lấy dữ liệu todo:', error)
  }
}

const fetchDataPhase = async () => {
  const res = await dataPhase(paramsTodo)

  console.table(res.data.content)

  dataSprint.dataSprint = res.data.content.map((item) => ({
    ...item,
    startTime: item.startTime ? dayjs(Number(item.startTime)) : null,
    endTime: item.endTime ? dayjs(Number(item.endTime)) : null,
    statusPhase: item.statusPhase,
    expanded: true
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
  project.setIdProject(idProject.value)

  console.log('✅ WebSocket đã kết nối thành công!')
  todoVoteWebSocket.subscribeCreateTodoVote(fetchDataTodo)
  todoVoteWebSocket.subscribeDeleteTodoVote(fetchDataTodo)
  phaseWebSocket.subscribeCreatePhase(fetchDataPhase)
  phaseWebSocket.subscribeDeletePhase(fetchDataPhase)
  phaseWebSocket.subscribeUpdatePhase(fetchDataPhase)
  phaseWebSocket.subscribeCreateTodoByPhase(fetchDataPhase)
  // phaseWebSocket.subscribeCreateTodoByPhase(fetchDataPhase)
  phaseWebSocket.subscribeUpdateStatusPhase(fetchDataPhase)

  webSocketTodoChild.getUpdateProgress(fetchDataPhase)
  // phần websocket modal
  // webSocketTodo.getUpdateNameTodo(fetchDataPhase)
  // webSocketTodoChild.getUpdateStatusTodoChecklist(fetchDataPhase)
  // webSocketTodoChild.getDeleteTodoChecklist(fetchDataPhase)
  // webSocketTodoChild.getCreateTodoChildChecklist(fetchDataPhase)
  // webSocketTodoChild.getEditTodoChecklist(fetchDataPhase)
  // webSocketMemberProject.getJoinTodoVote(fetchDataPhase)
  // webSocketMemberProject.getOutTodoVoteMemberProject(fetchDataPhase)
  // webSocketTodo.getDeleteDeadlineTodo(fetchDataPhase)
  // webSocketTodo.getUpdateDeadlineTodo(fetchDataPhase)
  // // webSocketTodo.getupdateCompleteTodo(fetchBoard)
  // webSocketTodo.getUpdatePriorityLevel(fetchDataPhase)
})

const stageVoteModal = ref(false)

const stageVote = () => {}

const router = useRouter()
const goToStatistics = () => {
  router.push({ name: 'statistics', params: { id: idProject.value } }) // Sử dụng name thay vì path
}

const goToSprint = () => {
  router.push({ name: 'phase-project', params: { id: idProject.value } }) // Sử dụng name thay vì path
}

const goToPointEsmirate = () => {
  router.push({
    name: ROUTES_CONSTANTS.MEMBER.children.POINT_ESTIMATE.name,
    params: { id: idProject.value }
  }) //
}
</script>

<style scoped>
.cursor-grab {
  cursor: grab;
}

.heght-blackbog {
  height: 100%;
}

:deep(.ant-btn) {
  background: var(--selected-header) !important;
  /* Gradient nền */
  color: var(--selected-text) !important;
  /* Màu chữ trắng */
  border-color: var(--selected-header) !important;
  /* Viền màu từ gradient */
}

/* Hiệu ứng hover */
:deep(.ant-btn:hover),
:deep(.ant-btn:focus) {
  background: var(--selected-header-hover) !important;
  /* Gradient khi hover */
  border-color: var(--selected-header-hover) !important;
  /* Viền khi hover */
}

/* Áp dụng gradient cho nút */

/* Hiệu ứng hover */
</style>
