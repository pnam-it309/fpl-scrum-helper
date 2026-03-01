<template>
  <!-- <BreadcrumbDefault label="Bình chọn công việc"> -->
  <!-- <template #icon>
      <CheckCircleOutlined style="font-size: 24px" />
    </template> -->
  <div class="w-full">
    <ListTodoVote
      :todos="dataTodo.data"
      @update-todos="
        (newTodos) => {
          dataTodo.data = [...newTodos]
          console.log('📌 Dữ liệu sau khi cập nhật:', dataTodo.data)
        }
      "
      :idProject="idProject"
      @level="(level) => searchLevel(level)"
      @search="(value) => search(value)"
    />
  </div>
  <!-- </BreadcrumbDefault> -->
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, watch } from 'vue'
import { getAllTodo, todoResponse } from '@/services/api/manage/todo/todo.api'
import { todoWebSocket } from '@/services/api/manage/todo/todo.socket.api'
import { todoVoteWebSocket } from '@/services/api/manage/todo/todovote.socket.api'
import { useRoute } from 'vue-router'
import ListTodoVote from './ListTodoVote.vue'
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import { BarChartOutlined, CheckCircleOutlined, LikeOutlined } from '@ant-design/icons-vue'
import BreadcrumbDefault from '@/components/custom/Div/BreadcrumbDefault.vue'

const paramsTodo = reactive({
  page: 1,
  size: 100000,
  totalItem: '',
  idProject: '',
  level: ' ' as string,
  search: ''
})

const router = useRoute()

const dataTodo = reactive({
  data: [] as todoResponse[]
})

const idProject = ref('')

const searchLevel = (level: string) => {
  paramsTodo.level = level
}

const search = (search: string) => {
  paramsTodo.search = search
}

const fetchDataTodo = async () => {
  try {
    const res = await getAllTodo(paramsTodo)
    dataTodo.data = res.data as unknown as todoResponse[]
    console.log(dataTodo.data)
  } catch (error) {
    console.error('❌ Lỗi khi lấy dữ liệu todo:', error)
  }
}

onMounted(() => {
  idProject.value = router.params.id as string
  paramsTodo.idProject = idProject.value as string
  fetchDataTodo()
  console.log('✅ WebSocket đã kết nối thành công!')
  todoWebSocket.subscribeCreateTodo(fetchDataTodo)
  todoWebSocket.subscribeUpdateTodo(fetchDataTodo)
  todoWebSocket.subscribeDeleteTodo((data) => {
    console.log('🔴 Nhận sự kiện DeleteTodo:', data)
    fetchDataTodo()
  })

  todoVoteWebSocket.subscribeCreateTodoVote(fetchDataTodo)
  todoVoteWebSocket.subscribeDeleteTodoVote(fetchDataTodo)

  // })
})

watch(
  () => [paramsTodo.level, paramsTodo.search],
  () => {
    fetchDataTodo()
  }
)

// onUnmounted(() => {
//   websocketService.disconnect()
// })
</script>

<style scoped>
.cursor-grab {
  cursor: grab;
}
</style>
