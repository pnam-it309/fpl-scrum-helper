<template>
  <aside
    v-show="headerStore.isHeaderOpen"
    class="fixed flex h-screen flex-col shadow-lg transition-all duration-300 ease-in-out bg-white dark:bg-boxdark lg:relative lg:translate-x-0"
    :class="{
      'translate-x-0': sidebarStore.isSidebarOpen,
      '-translate-x-full': !sidebarStore.isSidebarOpen,
      'w-16': state.collapsed,
      'w-64': !state.collapsed
    }"
    ref="target"
  >
    <div
      v-if="isMobile && sidebarStore.isSidebarOpen"
      class="fixed inset-0 z-40 bg-black bg-opacity-50 lg:hidden"
      @click="sidebarStore.isSidebarOpen = false"
    ></div>

    <div
      class="relative z-50 flex items-center justify-between px-4 py-4 bg-white transition-all"
      :class="{ 'justify-center': state.collapsed }"
    >
      <img
        v-show="!state.collapsed"
        src="/images/logo-udpm-dark.png"
        alt="Logo"
        class="transition-all duration-300 h-auto w-48 object-contain"
      />
      <div v-show="state.collapsed">
        <img src="/images/logo-udpm.png" alt="Collapsed Logo" class="w-10 h-10 object-contain" />
      </div>

      <button
        @click="toggleCollapsed"
        class="hidden lg:flex items-center justify-center w-10 h-10 rounded-full hover:bg-gray-300"
      >
        <div v-if="state.collapsed">
          <DoubleLeftOutlined style="transform: rotate(180deg)" />
        </div>
        <div v-else>
          <DoubleLeftOutlined style="transform: rotate(0deg)" />
        </div>
      </button>

      <button
        @click="sidebarStore.isSidebarOpen = false"
        class="lg:hidden flex items-center justify-center w-8 h-8 rounded-full transition-all"
      >
        <svg
          class="w-6 h-6 text-black-2"
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
          stroke-width="2"
        >
          <path stroke-linecap="round" stroke-linejoin="round" d="M15 19l-7-7 7-7" />
        </svg>
      </button>
    </div>

    <div class="flex-1 overflow-y-auto bg-white dark:bg-boxdark relative z-50">
      <ul class="space-y-2 px-2 py-4">
        <li v-for="item in menuItems" :key="item.key">
          <button
            @click="handleMenuClick(item)"
            class="group flex items-center gap-3 w-full px-4 py-3 rounded-md hover:bg-hoverSideBar dark:hover:bg-gray-700 transition-all"
            :class="{
              'bg-selectedSideBar dark:bg-gray-700': state.selectedKeys.includes(item.key)
            }"
          >
            <component
              :is="item.icon"
              class="w-5 h-5 shrink-0"
              :class="{
                'text-iconSideBarSelected': state.selectedKeys.includes(item.key),
                'text-iconSideBarIsNoSelected dark:text-gray-300 group-hover:text-iconSideBarSelected':
                  !state.selectedKeys.includes(item.key)
              }"
            />
            <span
              v-show="!state.collapsed"
              class="text-sm font-bold truncate transition-all duration-300"
              :class="{
                'text-textSideBarSelected': state.selectedKeys.includes(item.key),
                'text-textSideBarIsNoSelected group-hover:text-textSideBarSelected':
                  !state.selectedKeys.includes(item.key)
              }"
            >
              {{ item.label }}
            </span>
          </button>
        </li>
      </ul>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { reactive, watch, ref, onMounted, nextTick, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useSidebarStore } from '@/stores/sidebar'
import { onClickOutside } from '@vueuse/core'
import { ROUTES_CONSTANTS } from '@/constants/path'
import { detailPhase } from '@/services/api/member/phase/phase.api'
import {
  PlayCircleOutlined,
  BarChartOutlined,
  ScheduleOutlined,
  TableOutlined,
  LikeOutlined,
  LogoutOutlined,
  DoubleLeftOutlined,
  FileAddOutlined,
  HourglassOutlined,
  FormOutlined,
  TrophyOutlined,
  PieChartOutlined
} from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()
const target = ref(null)
const sidebarStore = useSidebarStore()
import { useHeaderStore } from '@/stores/header'

const headerStore = useHeaderStore()
const state = reactive({
  collapsed: false,
  selectedKeys: [route.name as string],
  openKeys: ['myproject'],
  preOpenKeys: ['myproject']
})

const isMobile = computed(() => window.innerWidth < 1024)

const projectDetail = ref(null)
const projectIdd = ref<string | null>((route.params.id as string) || null)

const toggleCollapsed = () => {
  state.collapsed = !state.collapsed
}

onMounted(async () => {
  if (projectIdd.value) {
    try {
      const response = await detailPhase(projectIdd.value)
      projectDetail.value = response.data.id
    } catch (error) {
      console.error('Lỗi khi lấy dữ liệu báo cáo:', error)
    }
  }
})

const menuItems = [
  { key: 'todo-member', label: 'Giai đoạn', icon: HourglassOutlined },
  { key: 'todo-vote-mb', label: 'Bình chọn', icon: TrophyOutlined },
  { key: 'member-report', label: 'Báo cáo', icon: BarChartOutlined },
  { key: 'member-phase', label: 'Công việc', icon: ScheduleOutlined },
  { key: 'project-detail-member', label: 'Bảng', icon: TableOutlined },
  // { key: 'chart', label: 'Biểu đồ', icon: PieChartOutlined },
  { key: 'member', label: 'Quay lại', icon: LogoutOutlined }
]

const handleMenuClick = async (item) => {
  state.selectedKeys = [item.key]

  switch (item.key) {
    case 'todo-member':
      router.push({
        name: ROUTES_CONSTANTS.MEMBER.children.TODO.name,
        params: { id: route.params.idProject }
      })
      break
    case 'todo-vote-mb':
      router.push({
        name: ROUTES_CONSTANTS.MEMBER.children.VOTE.children.TODO_VOTE.name,
        params: { id: route.params.idProject }
      })
      break
    case 'member-report':
      router.push({
        name: ROUTES_CONSTANTS.MEMBER.children.REPORT.name,
        params: { id: route.params.idProject }
      })
      break
    case 'member-phase':
      router.push({
        name: ROUTES_CONSTANTS.MEMBER.children.TAB_TODOLIST.children.TODOLIST.name,
        params: { id: route.params.idProject }
      })
      break
    case 'phaseProject':
      router.push({
        name: ROUTES_CONSTANTS.MEMBER.children.PHASE_PROJECT.name,
        params: { id: route.params.idProject }
      })
      break
    case 'member':
      router.push({
        name: ROUTES_CONSTANTS.MEMBER.children.MYPROJECT.name
      })
      break
    case 'project-detail-member':
      if (!projectDetail.value) {
        router.push({
          name: ROUTES_CONSTANTS.MEMBER.children.LOG.name,
          params: { idProject: route.params.id }
        })
      } else {
        router.push({
          name: ROUTES_CONSTANTS.MEMBER.children.PROJECT_DETAIL.name,
          params: { idProject: route.params.id, idPhase: projectDetail.value }
        })
      }

      break

    case 'chart':
      if (!projectDetail.value) {
        router.push({
          name: ROUTES_CONSTANTS.MEMBER.children.LOG.name,
          params: { id: route.params.idProject }
        })
      } else {
        router.push({
          name: ROUTES_CONSTANTS.MEMBER.children.CHART.name,
          params: { id: route.params.id, phaseId: projectDetail.value }
        })
      }

      break
  }

  await nextTick()
}

watch(
  () => route.name,
  (newRouteName) => {
    const menuItem = menuItems.find((item) => item.key === newRouteName)
    if (menuItem) {
      state.selectedKeys = [menuItem.key]
    }
  },
  { immediate: true }
)

onClickOutside(target, () => {
  sidebarStore.isSidebarOpen = false
})
</script>

<style scoped>
/* Smooth transition for sidebar width */
aside {
  transition: none;
}

button {
  transition: all 0.3s ease-in-out;
}

/* Adjust for collapsed view */
aside.w-16 .group span {
  display: none;
}

aside.w-16 .group .w-5 {
  display: block;
}

/* Overlay for mobile */
@media (max-width: 1024px) {
  aside {
    z-index: 50;
  }

  .overlay {
    z-index: 40;
  }
}
</style>
