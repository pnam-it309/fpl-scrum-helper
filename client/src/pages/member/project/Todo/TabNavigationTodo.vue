<template>
  <BreadcrumbDefaultV1 :routes="breadcrumbRoutes" marginBottom="mb-2">
    <TabNavigateCustom>
    <a-tabs v-model:activeKey="activeKey" @change="handleTabChange">
      <a-tab-pane key="1">
        <template #tab>
          <span>
            <BarChartOutlined />
            Hệ thống công việc
          </span>
        </template>
      </a-tab-pane>

      <a-tab-pane key="2">
        <template #tab>
          <span>
            <BulbOutlined />
            Công việc đang theo dõi
          </span>
        </template>
      </a-tab-pane>
    </a-tabs>

    <router-view />

    <ModalDetailStage
      :open="dataStage.openModal"
      :data-stage="dataStage"
      title="Tạo cuộc bình chọn"
      @close="closeModal"
      @success="fetchDataStage"
    />
   </TabNavigateCustom>
  </BreadcrumbDefaultV1>
</template>

<script setup>
import TabNavigateCustom from '@/components/custom/Div/TabNavigateCustom.vue'
const breadcrumbRoutes = [
  { name: 'Thành viên dự án ', nameRoute: ROUTES_CONSTANTS.MEMBER.name },
    { name: 'Dự án', nameRoute: ROUTES_CONSTANTS.MEMBER.children.MYPROJECT.name },
  { name: 'Công việc', nameRoute: ROUTES_CONSTANTS.MEMBER.children.TODO.name },

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
import DivCustom from '@/components/custom/Div/DivCustom.vue'
const handleTabChange = (tabKey) => {
  let selectedTab = null
  if (tabKey === '1') {
    selectedTab = {
      routeName: ROUTES_CONSTANTS.MEMBER.children.TAB_TODOLIST.children.TODOLIST.name
    }
  } else if (tabKey === '2') {
    selectedTab = { routeName: ROUTES_CONSTANTS.MEMBER.children.TAB_TODOLIST.children.MY_TODO.name }
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
