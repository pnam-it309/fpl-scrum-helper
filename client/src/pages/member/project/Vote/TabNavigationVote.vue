<template>
  <BreadcrumbDefaultV1 :routes="breadcrumbRoutes" marginBottom="mb-2">
    <TabNavigateCustom>
    <a-tabs v-model:activeKey="activeKey" @change="handleTabChange">
      <a-tab-pane key="1">
        <template #tab>
          <span>
            <SwapOutlined />
            Bình chọn
          </span>
        </template>
      </a-tab-pane>

      <a-tab-pane key="2">
        <template #tab>
          <span>
            <BarChartOutlined />
            Chi tiết bình chọn
          </span>
        </template>
      </a-tab-pane>

      <a-tab-pane key="3">
        <template #tab>
          <span>
            <FormOutlined />
            Tạo bình chọn
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
  { name: 'Bình chọn', nameRoute: ROUTES_CONSTANTS.MEMBER.children.VOTE.name },

]
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { SwapOutlined, BarChartOutlined, FormOutlined } from '@ant-design/icons-vue'
import ModalDetailStage from '@/pages/manage/todo/ModalDetailStage.vue'
import BreadcrumbDefault from '@/components/custom/Div/BreadcrumbDefault.vue'
import { ROUTES_CONSTANTS } from '@/constants/path'
import BreadcrumbDefaultV1 from '@/components/custom/Div/BreadcrumbDefaultV1.vue'

const activeKey = ref('1')
const previousTabKey = ref('1')

const router = useRouter()
const route = useRoute()

const dataStage = ref({
  openModal: false,  
  idStage: '',
  idPhase: ''
})

const handleTabChange = (tabKey) => {
  if (tabKey === '3') {
    if (!dataStage.value.openModal) {
      dataStage.value.openModal = true
    }
    activeKey.value = previousTabKey.value
    return 
  } else {
    previousTabKey.value = tabKey
    activeKey.value = tabKey
  }

  let selectedTab = null
  if (tabKey === '1') {
    selectedTab = { routeName: 'todo-vote-mb'}
  } else if (tabKey === '2') {
    selectedTab = { routeName: 'vote-details-mb'}
  }

  if (selectedTab) {
    router.push({
      name: selectedTab.routeName,
      params: {
        id: route.params.id
      }
    })
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
