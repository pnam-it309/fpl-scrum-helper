<script setup lang="ts">
import { ROUTES_CONSTANTS } from '@/constants/path'
import { DoubleLeftOutlined } from '@ant-design/icons-vue'
import { ref } from 'vue'
import { useRoute } from 'vue-router'

const isCollapsed = ref(false)
const isMobileMenuOpen = ref(false)

const route = useRoute()

const menuItems = ref([
  {
    icon: `<svg
                  class="fill-current"
                  width="18"
                  height="18"
                  viewBox="0 0 18 18"
                  fill="none"
                  xmlns="http://www.w3.org/2000/svg"
                >
                  <path
                    d="M15.7499 2.9812H14.2874V2.36245C14.2874 2.02495 14.0062 1.71558 13.6405 1.71558C13.2749 1.71558 12.9937 1.99683 12.9937 2.36245V2.9812H4.97803V2.36245C4.97803 2.02495 4.69678 1.71558 4.33115 1.71558C3.96553 1.71558 3.68428 1.99683 3.68428 2.36245V2.9812H2.2499C1.29365 2.9812 0.478027 3.7687 0.478027 4.75308V14.5406C0.478027 15.4968 1.26553 16.3125 2.2499 16.3125H15.7499C16.7062 16.3125 17.5218 15.525 17.5218 14.5406V4.72495C17.5218 3.7687 16.7062 2.9812 15.7499 2.9812ZM1.77178 8.21245H4.1624V10.9968H1.77178V8.21245ZM5.42803 8.21245H8.38115V10.9968H5.42803V8.21245ZM8.38115 12.2625V15.0187H5.42803V12.2625H8.38115ZM9.64678 12.2625H12.5999V15.0187H9.64678V12.2625ZM9.64678 10.9968V8.21245H12.5999V10.9968H9.64678ZM13.8374 8.21245H16.228V10.9968H13.8374V8.21245ZM2.2499 4.24683H3.7124V4.83745C3.7124 5.17495 3.99365 5.48433 4.35928 5.48433C4.7249 5.48433 5.00615 5.20308 5.00615 4.83745V4.24683H13.0499V4.83745C13.0499 5.17495 13.3312 5.48433 13.6968 5.48433C14.0624 5.48433 14.3437 5.20308 14.3437 4.83745V4.24683H15.7499C16.0312 4.24683 16.2562 4.47183 16.2562 4.75308V6.94683H1.77178V4.75308C1.77178 4.47183 1.96865 4.24683 2.2499 4.24683ZM1.77178 14.5125V12.2343H4.1624V14.9906H2.2499C1.96865 15.0187 1.77178 14.7937 1.77178 14.5125ZM15.7499 15.0187H13.8374V12.2625H16.228V14.5406C16.2562 14.7937 16.0312 15.0187 15.7499 15.0187Z"
                    fill=""
                  />
                </svg>`,
    label: 'Quản lý cơ sở',
    routeName: ROUTES_CONSTANTS.ADMIN.children.FACILITY.name
  },
  {
    icon: `<svg
            class="fill-current"
            width="18"
            height="18"
            viewBox="0 0 24 24"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
        >
          <path 
            d="M4 4H20V16H4V4ZM2 2V18H10V20H8V22H16V20H14V18H22V2H2Z" 
            fill="currentColor"
          />
        </svg>`,
    label: 'Quản lý bộ môn',
    routeName: ROUTES_CONSTANTS.ADMIN.children.DEPARTMENT.name
  },

  {
    icon: `<svg
                  class="fill-current"
                  width="18"
                  height="18"
                  viewBox="0 0 18 18"
                  fill="none"
                  xmlns="http://www.w3.org/2000/svg"
                >
                  <path
                    d="M9.0002 7.79065C11.0814 7.79065 12.7689 6.1594 12.7689 4.1344C12.7689 2.1094 11.0814 0.478149 9.0002 0.478149C6.91895 0.478149 5.23145 2.1094 5.23145 4.1344C5.23145 6.1594 6.91895 7.79065 9.0002 7.79065ZM9.0002 1.7719C10.3783 1.7719 11.5033 2.84065 11.5033 4.16252C11.5033 5.4844 10.3783 6.55315 9.0002 6.55315C7.62207 6.55315 6.49707 5.4844 6.49707 4.16252C6.49707 2.84065 7.62207 1.7719 9.0002 1.7719Z"
                    fill=""
                  />
                  <path
                    d="M10.8283 9.05627H7.17207C4.16269 9.05627 1.71582 11.5313 1.71582 14.5406V16.875C1.71582 17.2125 1.99707 17.5219 2.3627 17.5219C2.72832 17.5219 3.00957 17.2407 3.00957 16.875V14.5406C3.00957 12.2344 4.89394 10.3219 7.22832 10.3219H10.8564C13.1627 10.3219 15.0752 12.2063 15.0752 14.5406V16.875C15.0752 17.2125 15.3564 17.5219 15.7221 17.5219C16.0877 17.5219 16.3689 17.2407 16.3689 16.875V14.5406C16.2846 11.5313 13.8377 9.05627 10.8283 9.05627Z"
                    fill=""
                  />
                </svg>`,
    label: 'Quản lý nhân viên',
    routeName: ROUTES_CONSTANTS.ADMIN.children.STAFF.name
  },
  {
    icon: `<svg xmlns="http://www.w3.org/2000/svg"  class="fill-current"
                  width="18"
                  height="18"
                  fill="none"
                  viewBox="0 0 640 512"><path d="M144 0a80 80 0 1 1 0 160A80 80 0 1 1 144 0zM512 0a80 80 0 1 1 0 160A80 80 0 1 1 512 0zM0 298.7C0 239.8 47.8 192 106.7 192l42.7 0c15.9 0 31 3.5 44.6 9.7c-1.3 7.2-1.9 14.7-1.9 22.3c0 38.2 16.8 72.5 43.3 96c-.2 0-.4 0-.7 0L21.3 320C9.6 320 0 310.4 0 298.7zM405.3 320c-.2 0-.4 0-.7 0c26.6-23.5 43.3-57.8 43.3-96c0-7.6-.7-15-1.9-22.3c13.6-6.3 28.7-9.7 44.6-9.7l42.7 0C592.2 192 640 239.8 640 298.7c0 11.8-9.6 21.3-21.3 21.3l-213.3 0zM224 224a96 96 0 1 1 192 0 96 96 0 1 1 -192 0zM128 485.3C128 411.7 187.7 352 261.3 352l117.3 0C452.3 352 512 411.7 512 485.3c0 14.7-11.9 26.7-26.7 26.7l-330.7 0c-14.7 0-26.7-11.9-26.7-26.7z"/></svg>`,
    label: 'Quản lý sinh viên',
    routeName: ROUTES_CONSTANTS.ADMIN.children.STUDENT.name
  },
  {
    icon: `<svg xmlns="http://www.w3.org/2000/svg" 
         class="fill-current"
                  width="18"
                  height="18"
                  fill="none"
        viewBox="0 0 576 512"> <path d="M96 32C60.7 32 32 60.7 32 96l0 288 64 0L96 96l384 0 0 288 64 0 0-288c0-35.3-28.7-64-64-64L96 32zM224 384l0 32L32 416c-17.7 0-32 14.3-32 32s14.3 32 32 32l512 0c17.7 0 32-14.3 32-32s-14.3-32-32-32l-128 0 0-32c0-17.7-14.3-32-32-32l-128 0c-17.7 0-32 14.3-32 32z"/></svg>`,

    label: 'Quản lý thể loại',
    routeName: ROUTES_CONSTANTS.ADMIN.children.CATEGORY.name
  },
  {
    icon: `<svg xmlns="http://www.w3.org/2000/svg"
     class="fill-current"
     width="18"
     height="18"
     viewBox="0 0 576 512">
  <path fill="currentColor" d="M64 32C46.3 32 32 46.3 32 64l0 240 0 48 0 80c0 26.5 21.5 48 48 48l416 0c26.5 0 48-21.5 48-48l0-128 0-151.8c0-18.2-19.4-29.7-35.4-21.1L352 215.4l0-63.2c0-18.2-19.4-29.7-35.4-21.1L160 215.4 160 64c0-17.7-14.3-32-32-32L64 32z"/></svg>`,
    label: 'Quản lý dự án',
    routeName: ROUTES_CONSTANTS.ADMIN.children.PROJECT.name
  },
  {
    icon: `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 384 512"       class="fill-current"
          width="18"
          height="19"
          viewBox="0 0 18 19">
          <path d="M64 48c-8.8 0-16 7.2-16 16l0 384c0 8.8 7.2 16 16 16l80 0 0-64c0-26.5 21.5-48 48-48s48 21.5 48 48l0 64 80 0c8.8 0 16-7.2 16-16l0-384c0-8.8-7.2-16-16-16L64 48zM0 64C0 28.7 28.7 0 64 0L320 0c35.3 0 64 28.7 64 64l0 384c0 35.3-28.7 64-64 64L64 512c-35.3 0-64-28.7-64-64L0 64zm88 40c0-8.8 7.2-16 16-16l48 0c8.8 0 16 7.2 16 16l0 48c0 8.8-7.2 16-16 16l-48 0c-8.8 0-16-7.2-16-16l0-48zM232 88l48 0c8.8 0 16 7.2 16 16l0 48c0 8.8-7.2 16-16 16l-48 0c-8.8 0-16-7.2-16-16l0-48c0-8.8 7.2-16 16-16zM88 232c0-8.8 7.2-16 16-16l48 0c8.8 0 16 7.2 16 16l0 48c0 8.8-7.2 16-16 16l-48 0c-8.8 0-16-7.2-16-16l0-48zm144-16l48 0c8.8 0 16 7.2 16 16l0 48c0 8.8-7.2 16-16 16l-48 0c-8.8 0-16-7.2-16-16l0-48c0-8.8 7.2-16 16-16z"/>
          </svg>`,

    label: 'Quản lý chức vụ',
    routeName: ROUTES_CONSTANTS.ADMIN.children.ROLE.name
  },
  {
    icon: `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 448 512"     class="fill-current"
          width="18"
          height="19"
          viewBox="0 0 18 19">><!--!Font Awesome Free 6.7.2 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2025 Fonticons, Inc.--><path d="M160 80c0-26.5 21.5-48 48-48l32 0c26.5 0 48 21.5 48 48l0 352c0 26.5-21.5 48-48 48l-32 0c-26.5 0-48-21.5-48-48l0-352zM0 272c0-26.5 21.5-48 48-48l32 0c26.5 0 48 21.5 48 48l0 160c0 26.5-21.5 48-48 48l-32 0c-26.5 0-48-21.5-48-48L0 272zM368 96l32 0c26.5 0 48 21.5 48 48l0 288c0 26.5-21.5 48-48 48l-32 0c-26.5 0-48-21.5-48-48l0-288c0-26.5 21.5-48 48-48z"/></svg>`,

    label: 'Thống kê dự án',
    routeName: ROUTES_CONSTANTS.ADMIN.children.PROJECT.children.PROJECT_STATISTICS.name
  }
])
</script>

<template>
  <!-- MOBILE MENU BUTTON -->
  <button
    v-if="!isMobileMenuOpen"
    @click="isMobileMenuOpen = true"
    class="bg-black-2fixed top-4 left-4 z-50 flex items-center justify-center w-10 h-10 rounded-full hover:bg-gray-400 lg:hidden"
  >
    <svg
      class="w-6 h-6 fill-current text-gray-800"
      xmlns="http://www.w3.org/2000/svg"
      viewBox="0 0 24 24"
    >
      <path d="M4 6h16M4 12h16M4 18h16" />
    </svg>
  </button>

  <!-- SIDEBAR -->
  <aside
    class="fixed inset-0 lg:inset-y-0 lg:left-0 z-40 flex h-screen flex-col bg-white transition-transform duration-300 ease-in-out lg:relative lg:translate-x-0"
    :class="{
      'hidden lg:block': !isMobileMenuOpen,
      'translate-x-0 w-[80%]': isMobileMenuOpen,
      'w-20': isCollapsed && !isMobileMenuOpen,
      'w-64': !isCollapsed && !isMobileMenuOpen
    }"
  >
    <!-- SIDEBAR HEADER -->
    <div class="flex items-center justify-between px-4 py-4 bg-white">
      <img
        src="../../../assets/images/logo-udpm-dark.png"
        alt="Logo"
        class="transition-all duration-300"
        :class="{ hidden: isCollapsed }"
        style="width: 180px; height: auto"
      />
      <!-- Collapse/Expand button -->
      <button
        v-if="!isMobileMenuOpen"
        @click="isCollapsed = !isCollapsed"
        class="hidden lg:flex items-center justify-center w-10 h-10 rounded-full hover:bg-gray-300"
      >
        <div v-if="isCollapsed">
          <DoubleLeftOutlined style="transform: rotate(180deg)" />
        </div>
        <div v-else>
          <DoubleLeftOutlined style="transform: rotate(0deg)" />
        </div>
      </button>

      <!-- Close button (only on mobile) -->
      <button
        v-if="isMobileMenuOpen"
        @click="isMobileMenuOpen = false"
        class="lg:hidden flex items-center justify-center w-8 h-8"
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

    <!-- SIDEBAR MENU -->
    <div class="px-4 py-2">
      <nav>
        <ul>
          <li v-for="(menuItem, index) in menuItems" :key="index">
            <router-link
              :to="{ name: menuItem.routeName }"
              class="group flex items-center gap-4 p-3 rounded-lg hover:bg-hoverSideBar transition-all duration-200"
              :class="{ 'bg-selectedSideBar': String(route.name).startsWith(menuItem.routeName) }"
            >
              <!-- ICON -->
              <span
                v-html="menuItem.icon"
                class="w-6 h-6"
                :class="{
                  'text-iconSideBarSelected': String(route.name).startsWith(menuItem.routeName),
                  'text-iconSideBarIsNoSelected group-hover:text-iconSideBarSelected': !String(
                    route.name
                  ).startsWith(menuItem.routeName)
                }"
              ></span>

              <!-- LABEL -->
              <span
                v-if="!isCollapsed"
                class="text-sm"
                :class="{
                  'text-textSideBarSelected font-bold': String(route.name).startsWith(
                    menuItem.routeName
                  ),
                  'text-textSideBarIsNoSelected font-bold group-hover:text-textSideBarSelected':
                    !String(route.name).startsWith(menuItem.routeName)
                }"
              >
                {{ menuItem.label }}
              </span>
            </router-link>
          </li>
        </ul>
      </nav>
    </div>
  </aside>
</template>

<style scoped>
/* Smooth transition for sidebar width */
/* aside {
    transition: transform 0.2s ease-out, width 0.2s ease-out;
    will-change: transform, width, height;
  } */

/* Loại bỏ hiệu ứng hover trên logo */
.logo:hover {
  background: none;
}
</style>
