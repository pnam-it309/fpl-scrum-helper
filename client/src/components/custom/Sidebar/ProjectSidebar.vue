<template>
  <aside
    v-show="headerStore.isHeaderOpen"
    class="fixed flex h-screen flex-col shadow-lg transition-all duration-300 ease-in-out dark:bg-boxdark lg:relative lg:translate-x-0"
    :class="{
      'translate-x-0': sidebarStore.isSidebarOpen,
      '-translate-x-full': !sidebarStore.isSidebarOpen,
      'w-16': state.collapsed,
      'w-64': !state.collapsed
    }"
    ref="target"
  >
    <!-- Overlay for mobile -->
    <div
      v-if="isMobile && sidebarStore.isSidebarOpen"
      class="fixed inset-0 z-40 bg-black bg-opacity-50 lg:hidden"
      @click="sidebarStore.isSidebarOpen = false"
    ></div>

    <!-- Sidebar Header -->
    <div
      class="relative z-50 flex items-center justify-between px-4 py-4 bg-white transition-all"
      :class="{ 'justify-center': state.collapsed }"
    >
      <img
        src="../../../assets/images/logo-udpm-dark.png"
        alt="Logo"
        style="width: 180px; height: auto"
        :class="{ hidden: state.collapsed }"
      />
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

    <!-- Sidebar Menu -->
    <div class="flex-1 overflow-y-auto bg-white dark:bg-boxdark relative z-50">
      <ul class="px-2 py-4">
        <li>
          <button
            @click="handleMenuClick({ key: 'statistics' })"
            class="group flex items-center gap-3 w-full px-4 py-3 font-medium rounded-md hover:bg-hoverSideBar dark:hover:bg-gray-700 transition-all"
            :class="{
              'bg-selectedSideBar': state.selectedKeys.includes('statistics')
            }"
          >
            <ChromeOutlined
              class="w-5 h-5"
              :class="{
                'text-iconSideBarSelected': state.selectedKeys.includes('statistics'),
                'text-iconSideBarIsNoSelected dark:text-gray-300 group-hover:text-iconSideBarSelected':
                  !state.selectedKeys.includes('statistics')
              }"
            />
            <span
              v-if="!state.collapsed"
              class="text-sm"
              :class="{
                'text-textSideBarSelected font-bold': state.selectedKeys.includes('statistics'),
                'text-textSideBarIsNoSelected font-bold group-hover:text-textSideBarSelected':
                  !state.selectedKeys.includes('statistics')
              }"
            >
              Bản tóm tắt
            </span>
          </button>
        </li>

        <li>
          <button
            @click="handleMenuClick({ key: 'planning' })"
            class="group flex items-center gap-3 w-full px-4 py-3 font-medium rounded-md hover:bg-hoverSideBar dark:hover:bg-gray-700 transition-all"
            :class="{
              'bg-selectedSideBar dark:bg-gray-700': state.selectedKeys.includes('planning')
            }"
          >
            <PlayCircleOutlined
              class="w-5 h-5"
              :class="{
                'text-iconSideBarSelected': state.selectedKeys.includes('planning'),
                'text-iconSideBarIsNoSelected dark:text-gray-300 group-hover:text-iconSideBarSelected':
                  !state.selectedKeys.includes('planning')
              }"
            />
            <span
              v-if="!state.collapsed"
              class="text-sm"
              :class="{
                'text-textSideBarSelected font-bold': state.selectedKeys.includes('planning'),
                'text-textSideBarIsNoSelected font-bold group-hover:text-textSideBarSelected':
                  !state.selectedKeys.includes('planning')
              }"
            >
              Giai đoạn
            </span>
          </button>
        </li>

        <li>
          <button
            @click="handleMenuClick({ key: 'report' })"
            class="group flex items-center gap-3 w-full px-4 py-3 font-medium rounded-md hover:bg-hoverSideBar dark:hover:bg-gray-700 transition-all"
            :class="{
              'bg-selectedSideBar': state.selectedKeys.includes('report')
            }"
          >
            <BarChartOutlined
              class="w-5 h-5"
              :class="{
                'text-iconSideBarSelected': state.selectedKeys.includes('report'),
                'text-iconSideBarIsNoSelected dark:text-gray-300 group-hover:text-iconSideBarSelected':
                  !state.selectedKeys.includes('report')
              }"
            />
            <span
              v-if="!state.collapsed"
              class="text-sm"
              :class="{
                'text-textSideBarSelected font-bold': state.selectedKeys.includes('report'),
                'text-textSideBarIsNoSelected font-bold group-hover:text-textSideBarSelected':
                  !state.selectedKeys.includes('report')
              }"
            >
              Báo cáo
            </span>
          </button>
        </li>

        <li>
          <button
            @click="handleMenuClick({ key: 'task' })"
            class="group flex items-center gap-3 w-full px-4 py-3 font-medium rounded-md hover:bg-hoverSideBar dark:hover:bg-gray-700 transition-all"
            :class="{
              'bg-selectedSideBar': state.selectedKeys.includes('task')
            }"
          >
            <ScheduleOutlined
              class="w-5 h-5"
              :class="{
                'text-iconSideBarSelected': state.selectedKeys.includes('task'),
                'text-iconSideBarIsNoSelected dark:text-gray-300 group-hover:text-iconSideBarSelected':
                  !state.selectedKeys.includes('task')
              }"
            />
            <span
              v-if="!state.collapsed"
              class="text-sm"
              :class="{
                'text-textSideBarSelected font-bold': state.selectedKeys.includes('task'),
                'text-textSideBarIsNoSelected font-bold group-hover:text-textSideBarSelected':
                  !state.selectedKeys.includes('task')
              }"
            >
              Công việc
            </span>
          </button>
        </li>

        <li>
          <!-- <button
            @click="handleMenuClick({ key: 'bc' })"
            class="group flex items-center gap-3 w-full px-4 py-3 font-medium rounded-md hover:bg-hoverSideBar dark:hover:bg-gray-700 transition-all"
            :class="{
              'bg-selectedSideBar': state.selectedKeys.includes('bc')
            }"
          >
            <ScheduleOutlined
              class="w-5 h-5"
              :class="{
                'text-iconSideBarSelected': state.selectedKeys.includes('bc'),
                'text-iconSideBarIsNoSelected dark:text-gray-300 group-hover:text-iconSideBarSelected':
                  !state.selectedKeys.includes('bc')
              }"
            />
            <span
              v-if="!state.collapsed"
              class="text-sm"
              :class="{
                'text-textSideBarSelected font-bold': state.selectedKeys.includes('bc'),
                'text-textSideBarIsNoSelected font-bold group-hover:text-textSideBarSelected':
                  !state.selectedKeys.includes('bc')
              }"
            >
              Thống kê
            </span>
          </button> -->
        </li>

        <li>
          <button
            v-if="check"
            @click="handleMenuClick({ key: 'trelo' })"
            class="group flex items-center gap-3 w-full px-4 py-3 font-medium rounded-md hover:bg-hoverSideBar dark:hover:bg-gray-700 transition-all"
            :class="{
              'bg-selectedSideBar': state.selectedKeys.includes('trelo')
            }"
          >
            <TableOutlined
              class="w-5 h-5"
              :class="{
                'text-iconSideBarSelected': state.selectedKeys.includes('trelo'),
                'text-iconSideBarIsNoSelected dark:text-gray-300 group-hover:text-iconSideBarSelected':
                  !state.selectedKeys.includes('trelo')
              }"
            />
            <span
              v-if="!state.collapsed"
              class="text-sm"
              :class="{
                'text-textSideBarSelected font-bold': state.selectedKeys.includes('trelo'),
                'text-textSideBarIsNoSelected font-bold group-hover:text-textSideBarSelected':
                  !state.selectedKeys.includes('trelo')
              }"
            >
              Bảng
            </span>
          </button>

          <button
            v-else
            @click="handleMenuClick({ key: 'no-content' })"
            class="group flex items-center gap-3 w-full px-4 py-3 font-medium rounded-md hover:bg-hoverSideBar dark:hover:bg-gray-700 transition-all"
            :class="{
              'bg-selectedSideBar': state.selectedKeys.includes('no-content')
            }"
          >
            <TableOutlined
              class="w-5 h-5"
              :class="{
                'text-iconSideBarSelected': state.selectedKeys.includes('no-content'),
                'text-iconSideBarIsNoSelected dark:text-gray-300 group-hover:text-iconSideBarSelected':
                  !state.selectedKeys.includes('no-content')
              }"
            />
            <span
              v-if="!state.collapsed"
              class="text-sm"
              :class="{
                'text-textSideBarSelected font-bold': state.selectedKeys.includes('no-content'),
                'text-textSideBarIsNoSelected font-bold group-hover:text-textSideBarSelected':
                  !state.selectedKeys.includes('no-content')
              }"
            >
              Bảng
            </span>
          </button>
        </li>

        <li>
          <button
            @click="handleMenuClick({ key: 'manage' })"
            class="group flex items-center gap-3 w-full px-4 py-3 font-medium rounded-md hover:bg-hoverSideBar dark:hover:bg-gray-700 transition-all"
            :class="{
              'bg-selectedSideBar': state.selectedKeys.includes('manage')
            }"
          >
            <LogoutOutlined
              class="w-5 h-5"
              :class="{
                'text-iconSideBarSelected': state.selectedKeys.includes('manage'),
                'text-iconSideBarIsNoSelected dark:text-gray-300 group-hover:text-iconSideBarSelected':
                  !state.selectedKeys.includes('manage')
              }"
            />
            <span
              v-if="!state.collapsed"
              class="text-sm"
              :class="{
                'text-textSideBarSelected font-bold': state.selectedKeys.includes('manage'),
                'text-textSideBarIsNoSelected font-bold group-hover:text-textSideBarSelected':
                  !state.selectedKeys.includes('manage')
              }"
            >
              Quay lại
            </span>
          </button>
        </li>
      </ul>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted, watch, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useSidebarStore } from '@/stores/sidebar'
import {
  BarChartOutlined,
  PlayCircleOutlined,
  ScheduleOutlined,
  TableOutlined,
  LogoutOutlined,
  ChromeOutlined,
  DoubleLeftOutlined
} from '@ant-design/icons-vue'
import { useProjectStore } from '@/utils/store'
import { getPhaseSuccess } from '@/services/api/manage/phase/phase.api'
import { ROUTES_CONSTANTS } from '@/constants/path'
import { detailPhase } from '@/services/api/member/phase/phase.api'
import { useHeaderStore } from '@/stores/header'

const headerStore = useHeaderStore()
const router = useRouter()
const route = useRoute()
const sidebarStore = useSidebarStore()
const target = ref(null)

// Reactive state
const state = reactive({
  collapsed: false,
  selectedKeys: [route.name as string]
})

// Mobile check
const isMobile = computed(() => window.innerWidth < 1024)

const check = ref(false)
const project = useProjectStore()

const idPhase = ref()
const idProject = route.params.id as string

const phaseSuccess = async () => {
  if (!idProject) return

  try {
    const res = await detailPhase(idProject)
    idPhase.value = res.data.id
    check.value = !!idPhase.value

    console.log('idPhase:', idPhase.value)
  } catch (error) {
    console.error('Lỗi khi gọi getPhaseSuccess:', error)
  }
}

// Update selected key based on route changes
const updateSelectedKey = () => {
  const currentRoute = route.name

  if (currentRoute === ROUTES_CONSTANTS.MANAGE.children.DESCRIBE.name) {
    state.selectedKeys = ['statistics']
  }
  if (currentRoute === ROUTES_CONSTANTS.MANAGE.children.TODO.name) {
    state.selectedKeys = ['planning']
  } else if (
    currentRoute === ROUTES_CONSTANTS.MANAGE.children.TAB_REPORT.children.TABLE_REPORT.name
  ) {
    state.selectedKeys = ['report']
  } else if (currentRoute === ROUTES_CONSTANTS.MANAGE.children.TODOLIST.name) {
    state.selectedKeys = ['task']
  } else if (currentRoute === ROUTES_CONSTANTS.MANAGE.children.PROJECT.name) {
    state.selectedKeys = ['manage']
  } else if (currentRoute === ROUTES_CONSTANTS.MANAGE.children.PROJECT_DETAIL.name) {
    state.selectedKeys = ['trelo']
  } else if (currentRoute === ROUTES_CONSTANTS.MANAGE.children.NOCONTENT.name) {
    state.selectedKeys = ['no-content']
  }
}

// When component mounts
onMounted(() => {
  // idProject.value = route.params.id as string
  updateSelectedKey()
  phaseSuccess()
})

// Watch for route changes
watch(
  () => route.name,
  () => {
    updateSelectedKey()
  }
)

// Handle menu click
const handleMenuClick = ({ key }) => {
  state.selectedKeys = [key]
  sidebarStore.isSidebarOpen = false

  switch (key) {
    case 'statistics':
      router.push({
        name: `${ROUTES_CONSTANTS.MANAGE.children.DESCRIBE.name}`,
        params: {
          idProject: idProject
        }
      })
      break
    case 'planning':
      router.push({
        name: ROUTES_CONSTANTS.MANAGE.children.PHASE.name,
        params: { id: route.params.idProject }
      })
      break
    case 'report':
      router.push({
        name: ROUTES_CONSTANTS.MANAGE.children.TAB_REPORT.children.TABLE_REPORT.name,
        params: { id: route.params.idProject }
      })
      break
    case 'task':
      router.push({
        name: `${ROUTES_CONSTANTS.MANAGE.children.TAB_TODOLIST.children.TODOLIST.name}`,
        params: { id: route.params.id }
      })
      break
    case 'manage':
      router.push({
        name: `${ROUTES_CONSTANTS.MANAGE.children.PROJECT.name}`
      })
      break
    case 'statistics':
      router.push({
        name: `${ROUTES_CONSTANTS.MANAGE.children.DESCRIBE.name}`,
        params: {
          idProject: idProject
        }
      })
      break

    case 'bc':
      router.push({
        name: `${ROUTES_CONSTANTS.MANAGE.children.STATISTICS.name}`,
        params: {
          idProject: idProject
        }
      })
      break

    case 'trelo':
      router.push({
        name: `${ROUTES_CONSTANTS.MANAGE.children.PROJECT_DETAIL.name}`,
        params: {
          idProject: idProject,
          idPhase: idPhase.value
        }
      })
      break

    case 'no-content':
      router.push({
        name: `${ROUTES_CONSTANTS.MANAGE.children.NOCONTENT.name}`,
        params: {
          idProject: idProject
        }
      })
      break
  }
}

const toggleCollapsed = () => {
  state.collapsed = !state.collapsed
}
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
  /* Ensure icons always appear */
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
