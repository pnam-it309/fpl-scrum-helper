<template>
  <DivCustom label="Dự án của tôi">
    <MyProjectFilter
      :searchQuery="state.searchQuery"
      :searchStatus="state.myProjectStatus"
      @update:searchQuery="updateSearchQuery"
      @update:searchStatus="updateSearchStatus"
    />

    <MyProjectTable
      :searchQuery="state.searchQuery"
      :paginationParams="state.paginationParams"
      :myProject="state.myProject"
      :totalItems="state.totalItems"
      @page-change="handlePageChange"
    />
  </DivCustom>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, watch } from 'vue'
import { debounce } from 'lodash'

import { toast } from 'vue3-toastify'
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import MyProjectFilter from './MyProjectFilter.vue'
import MyProjectTable from './MyProjectTable.vue'
import {
  getAllMyProject,
  myProjectResponse,
  ParamsGetMyProject
} from '@/services/api/member/myproject/myproject.api'
import { localStorageAction } from '@/utils/storage'
import { USER_INFO_STORAGE_KEY } from '@/constants/storagekey'
const state = reactive({
  searchQuery: '',
  myProjectStatus: null as string | null,
  id: null as string | null,
  myProject: [] as myProjectResponse[],
  paginationParams: { page: 1, size: 10 },
  totalItems: 0
})

const fetctMyProject = async () => {
  try {
    const user = localStorageAction.get(USER_INFO_STORAGE_KEY)

    const params: ParamsGetMyProject = {
      page: state.paginationParams.page,
      size: state.paginationParams.size,
      idUser: user.userId,
      projectName: state.searchQuery || '',
      ProjectStatus: state.myProjectStatus ?? undefined
    }
    const response = await getAllMyProject(params)

    state.myProject = response?.data?.content || []
    state.totalItems = response?.data?.totalElements || 0
  } catch (error) {
    console.error('Lỗi khi tải danh sách cơ sở:', error)
  }
}

onMounted(fetctMyProject)

const handlePageChange = ({ page, pageSize }: { page: number; pageSize?: number }) => {
  state.paginationParams.page = page
  if (pageSize) {
    state.paginationParams.size = pageSize
  }
  fetctMyProject()
}

const debouncedFetchMyProject = debounce(fetctMyProject, 500)

const updateSearchQuery = (newQuery: string) => {
  state.searchQuery = newQuery.trim()
}

const updateSearchStatus = (newQuery: string) => {
  state.myProjectStatus = newQuery
}

watch(
  () => state.searchQuery,
  () => {
    state.paginationParams.page = 1
    debouncedFetchMyProject()
  }
)

watch(
  () => state.myProjectStatus,
  () => {
    state.paginationParams.page = 1
    debouncedFetchMyProject()
  }
)
</script>
