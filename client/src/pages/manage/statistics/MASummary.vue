<template>
  <a-menu v-model:selectedKeys="current" mode="horizontal" :items="items" @click="onMenuClick" />
</template>

<script setup lang="ts">
import { ROUTES_CONSTANTS } from '@/constants/path'
import { MenuProps } from 'ant-design-vue'
import { h, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
const route = useRoute()
const router = useRouter()
const current = ref<string[]>(['summary'])

const idProject = ref()

const onMenuClick = (info: any) => {
  const id = route.params.id
  current.value = [info.key]

  if (info.key === 'summary') {
    router.push({
      name: `${ROUTES_CONSTANTS.MANAGE.children.DESCRIBE.name}`,
      params: {
        idProject: idProject.value
      }
    })
  } else if (info.key === 'statistics') {
    router.push({
      name: `${ROUTES_CONSTANTS.MANAGE.children.STATISTICS.name}`,
      params: {
        idProject: idProject.value
      }
    })
  }
}

const items = ref<MenuProps['items']>([
  {
    key: 'summary',
    label: 'Tóm tắt dự án',
    title: 'Tóm tắt dự án'
  },
  {
    key: 'statistics',
    label: 'Thống kê biểu đồ',
    title: 'Thống kê biểu đồ'
  }
])

onMounted(() => {
  idProject.value = route.params.id as string
  const path = route.path
  if (path.includes('summary')) {
    current.value = ['summary']
  } else if (path.includes('statistics')) {
    current.value = ['statistics']
  }
})
</script>
