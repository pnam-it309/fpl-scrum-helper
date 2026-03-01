<template>
  <BreadcrumbDefaultV1 :routes="breadcrumbRoutes">
    <!-- <a-tabs v-model:activeKey="activeKey" @change="handleTabChange">
            <a-tab-pane key="1">
                <template #tab>
                    <span>
                        <BarChartOutlined />
                        Hệ thống công việc
                    </span>
                </template>
            </a-tab-pane> -->

    <!-- <a-tab-pane key="2">
                <template #tab>
                    <span>
                        <BulbOutlined />
                        Công việc đang theo dõi
                    </span>
                </template>
            </a-tab-pane> -->
    <!-- </a-tabs> -->

    <router-view />

    <ModalDetailStage
      :open="dataStage.openModal"
      :data-stage="dataStage"
      title="Cuộc bình chọn"
      @close="closeModal"
      @success="fetchDataStage"
    />
  </BreadcrumbDefaultV1>
</template>

<script setup>
const breadcrumbRoutes = [
  { name: 'Quản lý xưởng', nameRoute: ROUTES_CONSTANTS.MANAGE.name },
    { name: 'Tổng quan dự án', nameRoute: ROUTES_CONSTANTS.MANAGE.children.PROJECT.name },
  { name: 'Công việc ', nameRoute: ROUTES_CONSTANTS.MANAGE.children.TODOLIST.name },

]
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { SwapOutlined, BarChartOutlined, BulbOutlined } from '@ant-design/icons-vue'
import ModalDetailStage from '@/pages/manage/todo/ModalDetailStage.vue'
import BreadcrumbDefault from '@/components/custom/Div/BreadcrumbDefault.vue'

const activeKey = ref('1')
const router = useRouter()
const route = useRoute()

const dataStage = ref({
  openModal: false
})
import { ROUTES_CONSTANTS } from '@/constants/path'
import BreadcrumbDefaultV1 from '@/components/custom/Div/BreadcrumbDefaultV1.vue'
const handleTabChange = (tabKey) => {
  let selectedTab = null
  if (tabKey === '1') {
    selectedTab = {
      routeName: ROUTES_CONSTANTS.MANAGE.children.TAB_TODOLIST.children.TODOLIST.name
    }
  } else if (tabKey === '2') {
    selectedTab = { routeName: ROUTES_CONSTANTS.MANAGE.children.TAB_TODOLIST.children.MY_TODO.name }
  }

  if (selectedTab) {
    router.push({
      name: selectedTab.routeName,
      params: {
        id: route.params.id
      }
    })
  }

  activeKey.value = tabKey
}

const closeModal = () => {
  dataStage.value.openModal = false
}

const fetchDataStage = () => {}
</script>

<style scoped>
.ant-tabs-tab-active {
  color: #1890ff !important;
}

.ant-tabs-tab {
  color: #595959 !important;
}

.ant-tabs-tab:hover {
  color: #40a9ff !important;
}
</style>
