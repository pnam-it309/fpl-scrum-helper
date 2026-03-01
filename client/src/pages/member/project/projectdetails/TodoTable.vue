<template>
  <TodoPhaseProjectTable :searchQuery="statetodo.searchQuery" :todo="filteredBoardTable" :todoId="statetodo.idTodo"
    :paginationParams="statetodo.paginationParams" :totalItems="statetodo.totalItems"
    @page-change="handlePageChangeTodo" @updatePriority="handlePriorityUpdate" />

</template>

<script lang="ts" setup>
import DivCustom from '@/components/custom/Div/DivCustom.vue';
import { onMounted, ref, watch, watchEffect } from 'vue';
import { useRoute } from 'vue-router';
import TodoPhaseProjectTable from './tabletodo/TodoPhaseProjectTable.vue';
import { reactive } from 'vue';
import { debounce, filter } from 'lodash';
import { downloadExcelTodoList, downloadImportLogTodoList, getAllTodoList, getAllTodoPhaseProject, getAllTodoProject, ParamsGetTodoList, TodoListProjectResponse, TodoPhaseProjectResponse, TodoProjectResponse } from '@/services/api/manage/todolist/todolist.api';
import { TodoListResponse } from '@/services/service/member/metodolist.socket';
import { MBMeUpdateTodoRequest, webSocketTodo } from '@/services/service/member/metodo.socket';
import { webSocketMemberProject } from '@/services/service/member/memberproject.socket';
import { webSocketLabelProjectTodo } from '@/services/service/member/melabeltodo.socket';
import { useFilterStore } from '@/stores/filterStore';
import { MBMeBoardResponse } from '@/services/api/member/projectdetail/metodo.api';
import { localStorageAction } from '@/utils/storage';
import { USER_INFO_STORAGE_KEY } from '@/constants/storagekey';
import { webSocketSortTodo } from '@/services/service/member/sorttodo.socket';

const route = useRoute()
const projectId = ref<string | null>(route.params.id as string || null)

// const reset = () => {
//     state.selectedDepartmentFacilityId = ''; 
// };

//todoProject
const statetodo = reactive({
  searchQuery: '',
  search: null as string | null,
  isModalOpen: false,
  idTodo: null as string | null,
  nameTodo: null as string | null,
  todo: [] as TodoPhaseProjectResponse[],
  paginationParams: { page: 1, size: 10 },
  totalItems: 0,
  isOpenModalImport: false
});

const deadlineTodo = ref(null);

const meAllDetailTodo = ref<TodoPhaseProjectResponse | null>(null);

const userLogin = localStorageAction.get(USER_INFO_STORAGE_KEY)

const filteredBoardTable = ref<TodoPhaseProjectResponse[]>([])

// Lấy filter từ Pinia store
const filterStore = useFilterStore()

import { useSortStore } from '@/stores/sortStore'

const sortStore = useSortStore()

watch(() => sortStore.selectedSort, (newSort, oldSort) => {
  if (newSort) {
   fetchTodoProject()
  }
})

function parseSortOption(sortValue: string) {
  let field = ''
  let order: 'asc' | 'desc' = 'asc'

  if (sortValue.endsWith('_asc')) order = 'asc'
  else if (sortValue.endsWith('_desc')) order = 'desc'

  if (sortValue.startsWith('priority')) field = 'priorityLevel'
  else if (sortValue.startsWith('due')) field = 'deadlineTodo'
  else if (sortValue.startsWith('progress')) field = 'statusTodo' // hoặc field progress nếu bạn có kiểu khác
  else if (sortValue === 'newest' || sortValue === 'oldest') field = 'createdDate'
  else if (sortValue.startsWith('name')) field = 'todoName'

  return { field, order }
}
function mapPriorityToRank(priority: any): number {
  switch (priority) {
    case 'QUAN_TRONG':
      return 0
    case 1:
    case 'CAO':
      return 1
    case 2:
    case 'TRUNG_BINH':
      return 2
    case 3:
    case 'THAP':
      return 3
    default:
      return 4 // ưu tiên thấp nhất nếu không xác định
  }
}

function sortTodos(arr: TodoPhaseProjectResponse[], sortValue: string) {
  const { field, order } = parseSortOption(sortValue)
  if (!field) return arr

  return arr.slice().sort((a, b) => {
    let valA: any = a[field]
    let valB: any = b[field]

    if (field === 'createdDate' && typeof valA === 'string') {
      valA = Date.parse(valA)
      valB = Date.parse(valB)
    }
       if (field === 'priorityLevel') {
      valA = mapPriorityToRank(valA)
      valB = mapPriorityToRank(valB)
    }

    if (valA == null) return 1
    if (valB == null) return -1

    if (valA < valB) return order === 'asc' ? -1 : 1
    if (valA > valB) return order === 'asc' ? 1 : -1
    return 0
  })
}

watchEffect(() => {
  const filters = {
    memberId: filterStore.memberId,
    noMember: filterStore.noMember,
    myTodo: filterStore.myTodo,
    dueDate: filterStore.dueDate,
    labels: filterStore.labels,
    labelSelected: filterStore.labelsSelected,
    searchText: filterStore.searchText,
    membersSelected: filterStore.membersSelected,
    typeSelected: filterStore.typesSelected
  }

  const now = Date.now()

  let result = statetodo.todo.filter(task => {
    const hasMember = (task.students?.length ?? 0) > 0 || (task.staff?.length ?? 0) > 0
    const isDone = task.statusTodo === 'done' || !!task.completionTime

    // Lọc theo từ khóa tìm kiếm
    if (filters.searchText?.trim()) {
      const name = task.todoName?.trim().toLowerCase()
      const keyword = filters.searchText.trim().toLowerCase()
      if (!name.includes(keyword)) return false
    }

    // Lọc theo không có thành viên
    if (filters.noMember && hasMember) return false

    // Lọc theo "việc của tôi"
    if (filters.myTodo) {
      const isMyTask = task.staff?.some(m => m.id === userLogin.userId) || task.students?.some(m => m.id === userLogin.userId)
      if (!isMyTask) return false
    }

    // Lọc theo thành viên được chọn
    if (filters.membersSelected?.length > 0) {
      const matched = task.staff?.some(m => filters.membersSelected.includes(m.id)) ||
        task.students?.some(m => filters.membersSelected.includes(m.id))
      if (!matched) return false
    }

    // Lọc theo nhãn 'no_label'
    if (filters.labels?.includes('no_label') && (task.label?.length ?? 0) > 0) return false

    // Lọc theo các label được chọn
    if (filters.labelSelected?.length > 0) {
      const labelIds = task.label?.map(l => l.id) ?? []
      const hasMatch = filters.labelSelected.some(id => labelIds.includes(id))
      if (!hasMatch) return false
    }
    console.log("dữu liệu task ",task)
    if (filters.typeSelected?.length > 0) {
  if (!filters.typeSelected.includes(task.typeTodo)) return false
}


    // Lọc theo hạn
    if (filters.dueDate?.length > 0) {
      const isMatch = filters.dueDate.some(cond => {
        switch (cond) {
          case 'no_due':
            return task.deadlineTodo == null
          case 'overdue':
            return task.deadlineTodo > 0 && task.deadlineTodo < now
          case 'not_done':
            return !task.completionTime
          case 'done_early':
            return task.completionTime !== null && task.completionTime < task.deadlineTodo
          case 'done_late':
            return task.completionTime !== null && task.completionTime > task.deadlineTodo
          default:
            return false
        }
      })
      if (!isMatch) return false
    }

    return true
  })

  // Sắp xếp nếu có sort được chọn
  if (sortStore.selectedSort) {
    result = sortTodos(result, sortStore.selectedSort)
  }

  filteredBoardTable.value = result
})



// const fetchTodoProject = async () => {
//   try {
//     if (!projectId.value) return;

//     const response = await getAllTodoPhaseProject(projectId.value, route.params.idPhase as string);
//     statetodo.todo = response?.data?.data || [];
//     statetodo.totalItems = response?.data?.totalElements || 0;
//     deadlineTodo.value = meAllDetailTodo.value?.deadlineTodo
//   } catch (error) {
//     console.error('Lỗi khi tải danh sách task:', error);
//   }
// };

const fetchTodoProject = async () => {
  try {
    if (!projectId.value) return;

    const { page, size } = statetodo.paginationParams;
    // ⚠️ Backend tính từ 0 → giảm 1 nếu page frontend bắt đầu từ 1
    const response = await getAllTodoPhaseProject(
      projectId.value,
      route.params.idPhase as string,
      page - 1,
      size
    );
    console.log("vào đây dự án todo bảng : ",response.data)

    statetodo.todo = response?.data?.data || [];
    statetodo.totalItems = response?.data?.totalElements || 0;
    deadlineTodo.value = meAllDetailTodo.value?.deadlineTodo;
  } catch (error) {
    console.error('Lỗi khi tải danh sách task:', error);
  }
};


onMounted(fetchTodoProject);

const handlePageChangeTodo = ({ page, pageSize }: { page: number; pageSize?: number }) => {
  statetodo.paginationParams.page = page;
  if (pageSize) {
    statetodo.paginationParams.size = pageSize;
  }
  fetchTodoProject();
};

const debouncedFetchTodoProject = debounce(fetchTodoProject, 300);

watch(() => statetodo.searchQuery, () => {
  statetodo.paginationParams.page = 1;
  debouncedFetchTodoProject();
});

const handlePriorityUpdate = ({ idTodo, priorityLevel }) => {

  const todoItem = statetodo.todo.find(item => item.todoId === idTodo);
  if (todoItem) {
    todoItem.priorityLevel = priorityLevel;
  }

  fetchTodoProject();
};


webSocketTodo.getUpdatePriorityLevel(() => {
  fetchTodoProject()
});

webSocketTodo.getUpdateNameTodo(() => {
  fetchTodoProject()
});

webSocketMemberProject.getJoinTodoVote(() => {
  fetchTodoProject()

});

webSocketMemberProject.getOutTodoVoteMemberProject(() => {
  fetchTodoProject()
});

webSocketLabelProjectTodo.getjoinLabeProjectTodo(fetchTodoProject)
webSocketLabelProjectTodo.getoutLabeProjectTodo(fetchTodoProject)

webSocketTodo.getDeleteDeadlineTodo(fetchTodoProject)
webSocketTodo.getUpdateDeadlineTodo(fetchTodoProject)
webSocketTodo.getupdateCompleteTodo(fetchTodoProject)
</script>