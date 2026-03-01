<template>
  <BreadcrumbDefaultV1 :routes="breadcrumbRoutes" marginBottom="mb-2" >
    <TabNavigateCustom>
    <a-tabs v-model:activeKey="activeKey" @change="handleTabChange">
      <a-tab-pane key="1">
        <template #tab>
          <span>
            <BarChartOutlined />
            Quản lý thành viên
          </span>
        </template>
      </a-tab-pane>

      <a-tab-pane key="2">
        <template #tab>
          <span>
            <BulbOutlined />
            Quản lý báo cáo
          </span>
        </template>
      </a-tab-pane>
    </a-tabs>

    <router-view />
    </TabNavigateCustom>
  </BreadcrumbDefaultV1>
</template>

<script setup>
import TabNavigateCustom from '@/components/custom/Div/TabNavigateCustom.vue'
const breadcrumbRoutes = [
  { name: 'Quản lý xưởng ', nameRoute: ROUTES_CONSTANTS.MANAGE.name },
  { name: 'Dự án', nameRoute: ROUTES_CONSTANTS.MANAGE.children.PROJECT.name },
  { name: 'Báo cáo ', nameRoute: ROUTES_CONSTANTS.MANAGE.children.REPORT_USER.name },

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
  const id = route.params.id
  if (!id) return

  let routeName = ''
  if (tabKey === '1') {
    routeName = ROUTES_CONSTANTS.MANAGE.children.TAB_REPORT.children.TABLE_REPORT.name
  } else if (tabKey === '2') {
    routeName = ROUTES_CONSTANTS.MANAGE.children.TAB_REPORT.children.REPORT.name
  }

  if (routeName) {
    router.push({ name: routeName, params: { id } })
  }
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
