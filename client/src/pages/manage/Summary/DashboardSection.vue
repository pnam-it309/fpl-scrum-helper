<template>
 
  <div class="grid grid-cols-1 md:grid-cols-2 gap-4 p-4">
    <!-- Status Overview -->
    <div class="border rounded-xl p-4 shadow-sm h-100">
      <h3 class="font-semibold text-black text-lg mb-1">Tổng quan trạng thái</h3>
      <p class="text-sm text-gray-500 mb-4">
        Trạng thái giai đoạn và công việc trong dự án
        <!-- <a-button @Click="viewAllTodo" class="text-blue-500 font-semibold text-sm"
          >View all todo</a-button
        > -->
      </p>
      <div class="flex">
        <!-- MAStatisticsPhase -->
        <div>
          <MAStatisticsPhase :data-phase="dataSprint" />
        </div>

        <!-- Trạng thái công việc -->
        <div class="pl-6">
          <h1 class="font-bold text-base pt-2 mb-3">Trạng thái công việc</h1>

          <!-- Hoàn thành -->
          <div class="flex items-center mb-2">
            <div class="w-4 h-4 rounded-sm bg-green-500 mr-2"></div>
            <p class="font-medium text-sm">Hoàn thành: {{ props.countTodoSuccess }}</p>
          </div>

          <!-- Đang thực hiện -->
          <div class="flex items-center mb-2">
            <div class="w-4 h-4 rounded-sm bg-yellow-500 mr-2"></div>
            <p class="font-medium text-sm">Đang thực hiện: {{ props.countTodoDoing }}</p>
          </div>

          <!-- Chưa thực hiện -->
          <div class="flex items-center mb-2">
            <div class="w-4 h-4 rounded-sm bg-gray-400 mr-2"></div>
            <p class="font-medium text-sm">Chưa thực hiện: {{ props.countTodoNoDoing }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Recent Activity -->
    <div class="border rounded-xl p-4 shadow-sm overflow-y-auto h-100">
      <h3 class="font-semibold text-black text-lg mb-2">Hoạt động gần đây</h3>
      <p class="text-sm text-gray-500 mb-4">Cập nhật những thông tin mới nhất về dự án.</p>

      <div>
        <ActivitySidebar />
      </div>
      <!-- Placeholder list items -->
    </div>
  </div>

  <div class="grid grid-cols-1 md:grid-cols-2 gap-4 p-4">
    <!-- Status Overview -->
    <div class="border rounded-xl p-4 shadow-sm">
      <h1 class="font-semibold text-black">Phân tích ưu tiên</h1>
      <p class="text-sm text-gray-500 mb-4">
        Có được cái nhìn toàn diện về cách công việc đang được ưu tiên.
      </p>
      <MAStatisticsTodoLevel :id-phase="null" />
    </div>

    <!-- Recent Activity -->
    <div class="border rounded-xl p-4 shadow-sm h-92 overflow-y-auto">
      <h1 class="font-semibold text-black">Khối lượng công việc</h1>
      <p class="text-sm text-gray-500 mb-4">
        Theo dõi công việc của thành viên :
        <a-button @click="viewSprintDetail" class="text-blue-500 font-semibold text-sm">
          Danh sách công việc</a-button
        >
      </p>
      <div class="flex">
        <div>
          <p class="pr-10 font-semibold text-sm">Người được chuyển nhượng</p>
          <div class="flex pt-3">
            <a-avatar :src="avata" />
            <p class="p-2">Chưa được chỉ định</p>
          </div>

          <div
            v-for="(item, index) in todoList"
            :key="index"
            class="mb-2 flex items-center gap-2 pt-3"
          >
            <a-avatar :src="item.imageStaff ?? item.imageStudent ?? avata" />
            <span class="font-medium text-sm pt-2">
              {{ item.nameStudent ?? item.nameStaff }}
            </span>
          </div>
        </div>
        <div class="w-1/2">
          <p class="font-semibold text-sm">Phân phối công việc</p>
          <div class="flex pt-3">
            <a-progress class="w-11/12 pt-2" :percent="calculatePercent(countTodoNotStaff)" />
            <span class="text-sm text-gray-500">{{ countTodoNotStaff }} công việc</span>
          </div>

          <a-space class="flex justify-center pt-3" direction="vertical">
            <div v-for="(item, index) in todoList" :key="index" class="mb-2">
              <div class="flex justify-between mb-1">
                <a-progress
                  class="w-11/12 pt-2"
                  :percent="calculatePercent(item.countTodo)"
                  :stroke-color="colors[index]"
                />
                <span class="text-sm text-gray-500">{{ item.countTodo }} công việc</span>
              </div>
            </div>
          </a-space>
        </div>
      </div>
    </div>
    <!-- Placeholder list items -->
  </div>
</template>

<script setup lang="ts">

import ActivitySidebar from '@/components/ui/Header/ActivitySidebar.vue'
import MAStatisticsTodoLevel from '../statistics/MAStatisticsTodoLevel.vue'
import MAStatisticsPhase from '../statistics/MAStatisticsPhase.vue'
import {
  getAllPhaseStatistics,
  getPhaseSuccess,
  phaseResponse
} from '@/services/api/manage/phase/phase.api'
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

import { reactive } from 'vue'
import {
  fetchDataTodoByProject,
  getAllTodoStatisticsByStaffProject
} from '@/services/api/manage/todo/todo.api'
import { router } from '@/routes/router'
import { ROUTES_CONSTANTS } from '@/constants/path'
import { CheckCircleOutlined } from '@ant-design/icons-vue'
import BreadcrumbDefaultV1 from '@/components/custom/Div/BreadcrumbDefaultV1.vue'

const route = useRoute()
const emit = defineEmits(['data-phase'])
// Khai báo reactive biến
const dataSprint = ref<phaseResponse[]>()
const idProject = ref<string>()
const countDataAllProject = ref(0)
const countTodoNotStaff = ref(0)
const todoList = ref<any[]>([])
const avata = ref(
  'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTducvN7v_FPk5dEU3vo13ZLeOILMpkrs8YIw&s'
)

const props = defineProps<{
  countTodoSuccess: number
  countTodoDoing: number
  countTodoNoDoing: number
}>()

const colors = ref<string[]>([])

const paramStatisticsTodo = reactive({
  idProject: '',
  idPhase: null as string | null
})

// Hàm tính phần trăm
const calculatePercent = (count: number) => {
  if (countDataAllProject.value === 0) return 0
  return Math.round((count / countDataAllProject.value) * 100)
}

// Gọi API lấy thống kê theo nhân sự
const fetchDataTodo = async () => {
  try {
    const dataTodo = await getAllTodoStatisticsByStaffProject(paramStatisticsTodo)
    todoList.value = dataTodo.data || []

    // Tính tổng công việc đã phân phối
    let todoSuccess = 0
    todoList.value.forEach((a) => {
      todoSuccess += Number(a.countTodo) || 0
    })

    // Tính công việc chưa được phân phối
    countTodoNotStaff.value = countDataAllProject.value - todoSuccess
  } catch (error) {
    console.error('Lỗi khi lấy dữ liệu todo:', error)
  }
}

// Gọi API lấy phase + danh sách tất cả công việc
const fetchDataPhase = async (id: string) => {
  try {
    const res = await getAllPhaseStatistics(id)
    dataSprint.value = res.data

    emit('data-phase', res.data)
    // Lấy tổng công việc toàn dự án
    const data = await fetchDataTodoByProject(id)
    countDataAllProject.value = data.data.length

    // Gọi tiếp fetch todo sau khi có dữ liệu tổng
    await fetchDataTodo()
  } catch (error) {
    console.error('Lỗi khi lấy dữ liệu giai đoạn:', error)
  }
}
const idPhase = ref()

// Gọi khi mounted
onMounted(() => {
  phaseSuccess()
  idProject.value = route.params.id as string
  paramStatisticsTodo.idProject = idProject.value
  fetchDataPhase(idProject.value)
})

const viewSprintDetail = () => {
  router.push({
    name: `${ROUTES_CONSTANTS.MANAGE.children.TODOLIST.name}`,
    params: {
      idProject: idProject.value
    }
  })
}

const viewAllTodo = () => {
  router.push({
    name: `${ROUTES_CONSTANTS.MANAGE.children.TODO.name}`,
    params: {
      idProject: idProject.value
    }
  })
}

const phaseSuccess = async () => {
  const res = await getPhaseSuccess()
  idPhase.value = res.data.id
}
</script>
