// stores/breadcrumb.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'

interface BreadcrumbItem {
  label: string
  link: string
}

const useBreadcrumbStore = defineStore('breadcrumb', () => {
  const routes = ref<BreadcrumbItem[]>([])

  const setRoutes = (data: BreadcrumbItem[]) => {
    routes.value = data
  }

  const push = (data: BreadcrumbItem) => {
    routes.value.push(data)
  }

  const clear = () => {
    routes.value = []
  }

  return {
    routes,
    setRoutes,
    push,
    clear
  }
})

export default useBreadcrumbStore
