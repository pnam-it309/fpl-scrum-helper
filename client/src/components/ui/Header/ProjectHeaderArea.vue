<template>
  <div>
    <!-- Khi đang mở -->
    <div
      class="w-full bg-white/40 backdrop-blur-md shadow-md rounded-b-lg p-2 transition-all duration-300"
    >
      <div class="flex flex-wrap justify-between items-center py-1 gap-2 sm:gap-4">
        <div class="flex items-center gap-2 text-sm font-medium text-gray-800 px-2 sm:px-4">
          <!-- Nút thu gọn -->
          <a-button
            @click="expanded"
            class="bg-slate-200 hover:bg-slate-300 border-none font-medium text-xs flex items-center justify-center w-8 h-8 p-0"
          >
            <template v-if="isExpanded">
              <MenuFoldOutlined />
            </template>
            <template v-else>
              <DoubleLeftOutlined style="transform: rotate(180deg)" />
            </template>
          </a-button>

          <!-- Nút chuyển view -->
          <a-button
            @click="handleToggleView"
            class="bg-slate-200 hover:bg-slate-300 border-none font-medium flex items-center justify-center px-2 sm:px-3 h-8 gap-1"
          >
            <AppstoreAddOutlined v-if="isTableView" />
            <TableOutlined v-else />
            <p class="hidden sm:block">
              {{ isTableView ? 'Chuyển sang Kanban' : 'Chuyển sang Bảng' }}
            </p>
          </a-button>
        </div>

        <!-- Tên dự án và giai đoạn -->
        <div
          v-if="projectInfo"
          class="hidden md:flex min-w-0 flex-1 items-center gap-x-2 gap-y-1 flex-wrap text-sm font-medium text-gray-800 truncate"
        >
          <p class="text-gray-600 shrink-0">Tên dự án:</p>
          <a-tooltip :title="projectInfo.name">
            <span class="text-blue-700 italic font-semibold truncate max-w-[180px] block">
              {{ projectInfo.name }}
            </span>
          </a-tooltip>

          <p class="text-gray-400 select-none text-sm">➤</p>

          <p class="text-gray-600 shrink-0">Giai đoạn:</p>
          <a-tooltip :title="projectInfo.phaseProjectName || 'Chưa cập nhật'">
            <span class="text-blue-700 italic font-semibold truncate max-w-[180px] block">
              {{ projectInfo.phaseProjectName || 'Chưa cập nhật' }}
            </span>
          </a-tooltip>
        </div>

        <div class="flex flex-wrap items-center gap-2 sm:gap-4">
          <a-button
            @click="showBurnChartModal = true"
            class="bg-slate-200 hover:bg-slate-300 border-none font-medium flex items-center space-x-2"
          >
            <SortAscendingOutlined />
            <p class="hidden sm:block m-0">BurnDown Chart</p>
          </a-button>
          <BurndownChartModal :isOpen="showBurnChartModal" @close="showBurnChartModal = false" />

          <a-button
            @click="showSortModal = !showSortModal"
            class="bg-slate-200 hover:bg-slate-300 border-none font-medium flex items-center space-x-2"
          >
            <SortAscendingOutlined />
            <p class="hidden sm:block m-0">Sắp xếp</p>
          </a-button>
          <SortModal :isOpen="showSortModal" @select="handleSortSelect" />

          <div class="inline-block relative group">
            <!-- Nút bộ lọc -->
            <a-button
              @click="handleOpenFilter"
              class="relative bg-slate-200 hover:bg-slate-300 border-none font-medium flex items-center space-x-2"
            >
              <FilterOutlined />
              <p class="hidden sm:block">Bộ lọc</p>

              <!-- Số lượng bộ lọc -->
              <div
                v-if="filterStore.totalSelected > 0"
                class="absolute -top-2 -right-2 w-5 h-5 rounded-full bg-blue-600 text-white text-xs font-semibold flex items-center justify-center group-hover:hidden"
              >
                {{ filterStore.totalSelected }}
              </div>

              <!-- Nút x xoá tất cả -->
              <button
                v-if="filterStore.totalSelected > 0"
                @click.stop="filterStore.resetFilters"
                class="absolute -top-2 -right-2 w-5 h-5 rounded-full bg-red-500 text-white text-xs flex items-center justify-center hidden group-hover:flex hover:bg-red-600 transition"
                title="Xoá tất cả bộ lọc"
              >
                ✕
              </button>
            </a-button>
          </div>

          <FilterModal :isOpen="showFilterModal" @close="showFilterModal = false" />

          <ThreeDotsMenu />
        </div>
      </div>
    </div>

    <!-- Khi thu gọn: chỉ hiện nút mở rộng -->
    <!-- <div 
      class="w-fit h-10 bg-white/40 backdrop-blur-md shadow-md rounded-b-lg p-2 flex items-center transition-all duration-300">
      <a-button @click="expanded" class="bg-slate-200 hover:bg-slate-300 border-none font-medium text-xs">
        <DoubleLeftOutlined style="transform: rotate(180deg);" />
      </a-button>
    </div> -->
  </div>
</template>

<script setup lang="ts">
import { defineProps, defineEmits, computed, ref, onMounted } from 'vue'
import { ROUTES_CONSTANTS } from '@/constants/path'
import { localStorageAction } from '@/utils/storage'
import { USER_INFO_STORAGE_KEY } from '@/constants/storagekey'
import { USER_TYPE } from '@/constants/userType'
import { useRoute } from 'vue-router'
import ThreeDotsMenu from './ThreeDotsMenu.vue'
import SortModal from '@/pages/member/project/projectdetails/SortModal.vue'
import FilterModal from '@/pages/member/project/projectdetails/FilterModal.vue'
import {
  AppstoreAddOutlined,
  DoubleLeftOutlined,
  FilterOutlined,
  MenuFoldOutlined,
  SortAscendingOutlined,
  TableOutlined
} from '@ant-design/icons-vue'

import { useFilterStore } from '@/stores/filterStore'
import { useHeaderStore } from '@/stores/header'
import BurndownChartModal from '@/pages/member/project/chart/BurndownChartModal.vue'
import {
  getProjectByPhaseProjectId,
  ProjectViewResponse
} from '@/services/api/member/projectdetail/todolist.api'
const headerStore = useHeaderStore()

const filterStore = useFilterStore()

const route = useRoute()
const showSortModal = ref(false)
const showFilterModal = ref(false)
const isExpanded = ref(true)

const projectInfo = ref<ProjectViewResponse | null>(null)

const expanded = () => {
  isExpanded.value = !isExpanded.value
  headerStore.toggleHeader()
}

const fetchProjectInfo = async () => {
  try {
    const response = await getProjectByPhaseProjectId(route.params.idPhase as string)
    projectInfo.value = response.data
    console.log('projectInfo', projectInfo.value)
  } catch (error) {
    console.error('Lỗi khi lấy thông tin dự án:', error)
  }
}

const handleSortSelect = (value: string) => {
  console.log('Bạn chọn:', value)
  showSortModal.value = false
}

const handleOpenFilter = () => {
  showFilterModal.value = true
}

const showBurnChartModal = ref(false)

const userLogin = localStorageAction.get(USER_INFO_STORAGE_KEY)

const props = defineProps<{ isTableView: boolean; idProject: string }>()
const emit = defineEmits(['toggleView', 'showStatistics'])

const projectRoute = computed(() => {
  if (userLogin.roleScreen === USER_TYPE.ADMIN) {
    return { name: ROUTES_CONSTANTS.ADMIN.children.PROJECT.name }
  } else if (userLogin.roleScreen === USER_TYPE.MANAGE) {
    return { name: ROUTES_CONSTANTS.MANAGE.children.PROJECT.name }
  } else {
    return { name: ROUTES_CONSTANTS.MEMBER.children.MYPROJECT.name }
  }
})

const projectPhaseRoute = computed(() => {
  if (userLogin.roleScreen === USER_TYPE.ADMIN || userLogin.roleScreen === USER_TYPE.MANAGE) {
    return {
      name: `${ROUTES_CONSTANTS.MANAGE.children.TODO.name}`,
      params: { id: route.params.id }
    }
  } else {
    return {
      name: `${ROUTES_CONSTANTS.PROJECT.children.VOTE_TODO.name}`,
      params: { id: route.params.id }
    }
  }
})

const projectLabelPhase = computed(() => {
  return userLogin.roleScreen === USER_TYPE.MEMBER ? 'Bình chọn công việc' : 'Giai đoạn dự án '
})

const projectLabel = computed(() => {
  if (userLogin.roleScreen === USER_TYPE.ADMIN) return 'Quản lý tất cả dự án'
  if (userLogin.roleScreen === USER_TYPE.MANAGE) return 'Dự án tôi quản lý'
  return 'Dự án tham gia'
})

const projectName = computed(() => props.idProject || 'Chưa xác định')

const handleToggleView = () => {
  emit('toggleView')
}

onMounted(fetchProjectInfo)
</script>
