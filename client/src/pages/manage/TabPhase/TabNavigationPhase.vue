<template>
  <BreadcrumbDefault>
    <a-tabs v-model:activeKey="activeKey" @change="handleTabChange">
      <a-tab-pane key="1">
        <template #tab>
          <span>
            <BarChartOutlined />
            Giai đoạn chưa bắt đầu
          </span>
        </template>
      </a-tab-pane>

      <a-tab-pane key="2">
        <template #tab>
          <span>
            <BulbOutlined />
            Giai đoạn đang bắt đầu
          </span>
        </template>
      </a-tab-pane>

      <a-tab-pane key="3">
        <template #tab>
          <span>
            <BulbOutlined />
            Giai đoạn đã hoàn thành
          </span>
        </template>
      </a-tab-pane>
    </a-tabs>

    <router-view />

    <ModalDetailStage
      :open="dataStage.openModal"
      :data-stage="dataStage"
      title="Cuộc bình chọn"
      @close="closeModal"
      @success="fetchDataStage"
    />
  </BreadcrumbDefault>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { SwapOutlined, BarChartOutlined, BulbOutlined } from '@ant-design/icons-vue'
import ModalDetailStage from '@/pages/manage/todo/ModalDetailStage.vue'
import BreadcrumbDefault from '@/components/custom/Div/BreadcrumbDefault.vue'

const activeKey = ref('3')
const router = useRouter()
const route = useRoute()

const dataStage = ref({
  openModal: false
})
import { ROUTES_CONSTANTS } from '@/constants/path'
const handleTabChange = (tabKey) => {
  let selectedTab = null
  if (tabKey === '1') {
    selectedTab = {
      routeName: ROUTES_CONSTANTS.MANAGE.children.TAB_TODOLIST.children.TODOLIST.name
    }
  } else if (tabKey === '2') {
    selectedTab = { routeName: ROUTES_CONSTANTS.MANAGE.children.TAB_TODOLIST.children.MY_TODO.name }
  } else if (tabKey === '3') {
    selectedTab = {
      routeName: ROUTES_CONSTANTS.MANAGE.children.TAB_PHASE.children.DOING_PHASE.name
    }
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
