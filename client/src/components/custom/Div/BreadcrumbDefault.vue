  <template>
    <div class="page-wrapper flex flex-col min-h-screen">
      <div
        class="content-wrapper bg-white dark:bg-textSideBarIsNoSelected p-6 shadow-lg border border-gray-200 dark:border-strokedark"
        :class="customClasses"
      >
        <!-- Breadcrumb -->
        <div class="mb-4">
          <div class="flex gap-1 text-sm font-medium flex-wrap">
            <template v-for="(item, index) in breadcrumbs" :key="index">
              <router-link
                v-if="index < breadcrumbs.length - 1"
                :to="item.path"
                class="text-gray-400 hover:text-gray-500 no-underline"
              >
                {{ item.label }}
              </router-link>
              <span v-else class="text-textSideBarIsNoSelected font-semibold">
                {{ item.label }}
              </span>
              <span v-if="index < breadcrumbs.length - 1" class="text-gray-300">/</span>
            </template>
          </div>
        </div>

        <!-- Page content -->
        <slot />
      </div>
    </div>
  </template>

  <script setup lang="ts">
  import { computed } from 'vue'
  import { useRoute, useRouter } from 'vue-router'

  const props = defineProps({
    customClasses: String
  })

  const route = useRoute()
  const router = useRouter()

  const breadcrumbs = computed(() => {
    const result: { label: string; path: string }[] = []

    const addParentBreadcrumb = (routeName?: string) => {
      if (!routeName) return
      const route = router.getRoutes().find((r) => r.name === routeName)
      if (!route || !route.meta?.breadcrumb) return

      addParentBreadcrumb(route.meta.breadcrumbParent)

      const label =
        typeof route.meta.breadcrumb === 'function'
          ? route.meta.breadcrumb(route)
          : route.meta.breadcrumb

      result.push({
        label,
        path: router.resolve({ name: route.name!, params: route.params }).path
      })
    }

    const current = route.matched.at(-1)
    if (current?.meta?.breadcrumb) {
      addParentBreadcrumb(current.meta.breadcrumbParent)
      result.push({
        label:
          typeof current.meta.breadcrumb === 'function'
            ? current.meta.breadcrumb(route)
            : current.meta.breadcrumb,
        path: route.fullPath
      })
    }

    return result
  })
  </script>

  <style scoped>
  .page-wrapper {
    @apply flex flex-col min-h-screen;
  }

  .content-wrapper {
    @apply flex-grow;
  }

  a.no-underline {
    text-decoration: none;
  }
  </style>
