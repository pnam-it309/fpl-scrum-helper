<template>
  <div class="page-wrapper flex flex-col min-h-16 pt-10">
    <div
      class="content-wrapper bg-slate-100 dark:bg-boxdark shadow-lg rounded-lg relative border border-gray-200 dark:border-strokedark space-y-4"
    >
      <!-- Header -->
      <div class="flex h-12 items-center px-5 w-full">
        <div class="flex items-center">
          <a-checkbox
            :checked="isAllSelected"
            :indeterminate="isIndeterminate"
            @change="handleSelectAll"
            class="ml-3"
            @click.stop
          />
          <button
            @click="toggleTaskVisibility()"
            class="ml-2 px-3 rounded font-semibold flex text-zinc-900 items-center"
          >
            <DownOutlined v-if="openTask" class="font-bold" />
            <RightOutlined v-else class="font-bold" />
          </button>
          <p class="font-semibold text-zinc-900">Danh sách công việc</p>
          <div class="font-semibold text-b rounded-lg text-sm ml-2">
            ({{ countTask }} công việc)
          </div>
          <span class="bg-slate-100 px-2 text-sm rounded-full font-satoshi ml-2">
            {{ totalPoint }} điểm công việc
          </span>
        </div>

        <!-- Nút này sẽ nằm ở cuối hàng bên phải -->
        <div class="ml-auto flex items-center">
          <a-button
            @click="addSprint"
            class="w-full p-2 text-sm h-full text-center items-center border border-none !hover:bg-slate-400 !bg-white"
          >
            + Giai đoạn
          </a-button>
        </div>
      </div>

      <!-- Nút tạo giai đoạn được di chuyển xuống đây -->
      <!-- <div class="mt-3 ml-5">
        <a-button
          @click="showAddSprintInput"
          v-if="isAddingSprint == false"
          class="w-30 text-center items-center border border-none !hover:bg-slate-400 !bg-white"
          style="background: none !important; color: black !important"
        >
          + Tạo giai đoạn
        </a-button>
      </div> -->

      <div v-if="openTask == false" class="overflow-y-auto space-y-4 w-full p-5">
        <div v-if="todoList.length == 0">
          <div class="border-dashed border-2 border-slate-300 w-full p-5 text-center">
            <p Lên class="font-satoshi text-sm text-slate-700">
              Danh sách công việc đang trống hãy tạo thêm
            </p>
          </div>
        </div>
        <!-- 
        <div class="p-3 border border-x-0 w-full" v-if="isAddingSprint">
          <input
            placeholder="Tạo giai đoạn mới"
            @keyup.enter="addSprint"
            @blur="addSprint"
            class="w-full p-2 rounded-lg h-11 border-blue-300 hover:!ring-0 hover:!border-blue-300 outline-none shadow-none !border-2 focus:!ring-0 focus:!border-blue-300"
            autofocus
          />
        </div> -->
        <table
          class="w-full border border-gray-400 border-collapse min-w-[800px] text-sm leading-none"
        >
          <draggable
            v-model="todoList"
            group="tasks"
            item-key="id"
            animation="300"
            tag="tbody"
            class="rounded-lg"
            @change="handleDraggableChange"
            @start="handleDragStart"
            @end="handleTodoDrop"
          >
            <template #item="{ element }">
              <tr
                class="bg-white text-black border-b border-gray-400 hover:bg-gray-100 cursor-grab leading-none text-sm"
                :class="{ 'selected-todo': checkedTodo.has(element.id) }"
                :data-id="element.id"
                @click="handleTodoClick(element.id, $event)"
              >
                <td class="text-center align-middle w-1/12 border border-x-0">
                  <a-checkbox
                    :checked="isChecked(element.id)"
                    @change="(e) => handleCheckboxChange(element.id, e)"
                    @click.stop
                  />
                </td>

                <td class="text-center w-5 border border-x-0">
                  <a-tooltip title="Loại công việc">
                    <template v-if="element.type === 'Lỗi'">
                      <BugOutlined style="color: red" />
                    </template>
                    <template v-else-if="element.type === 'Công việc'">
                      <CheckSquareOutlined style="color: #0083ff" />
                    </template>
                    <template v-else>
                      <ThunderboltOutlined style="color: orange" />
                    </template>
                  </a-tooltip>
                </td>

                <td class="w-2/6 max-w-[300px] truncate border border-x-0">
                  <a-tooltip title="Tên công việc">
                    <template v-if="editedTaskId === element.id">
                      <input
                        v-model="editedTaskName[element.id]"
                        @keyup.enter="updateTask(element.id)"
                        @blur="updateTask(element.id)"
                        class="p-1 border-b border-gray-500 focus:outline-none w-full"
                        @click.stop
                      />
                    </template>
                    <template v-else>
                      <p @click="enableEdit(element)" class="cursor-pointer truncate" @click.stop>
                        {{ element.name }}
                      </p>
                    </template>
                  </a-tooltip>
                </td>

                <td class="text-center w-1/5 truncate border border-x-0">
                  <a-tooltip title="Ngày tạo công việc">
                    {{ formatDate(Number(element.createdDate ?? element.date)) }}
                    <!-- Priority Dropdown -->
                  </a-tooltip>

                  <a-tooltip title="Mức độ công việc">
                    <a-dropdown :trigger="['click']" @click.stop>
                      <a class="cursor-pointer text-sm font-bold ml-2">
                        <span
                          v-if="selectedText[element.id]"
                          :class="getPriorityClass(selectedKey[element.id]?.[0])"
                        >
                          {{ selectedText[element.id] }}
                        </span>
                        <template v-else>
                          <font-awesome-icon :icon="['fas', 'bars']" />
                          <UnorderedListOutlined />
                        </template>
                      </a>
                      <template #overlay>
                        <a-menu
                          :selectedKeys="selectedKey[element.id]"
                          @click="(e) => handleSelect(e, element.id)"
                        >
                          <a-menu-item key="3" class="!text-blue-400">Thấp</a-menu-item>
                          <a-menu-item key="2" class="!text-orange-500">Trung bình</a-menu-item>
                          <a-menu-item key="1" class="!text-red-400">Cao</a-menu-item>
                          <a-menu-item key="0" class="!text-red-600">Quan trọng</a-menu-item>
                        </a-menu>
                      </template>
                    </a-dropdown>
                  </a-tooltip>
                </td>

                <td class="text-center w-1/12 border border-x-0">
                  <a-avatar-group
                    :max-count="3"
                    :max-style="{ color: '#f56a00', backgroundColor: '#fde3cf' }"
                  >
                    <a-tooltip
                      v-for="(user, index) in userVoteMapByTaskId[element.id]"
                      :key="index"
                      :title="user.nameStaff ?? user.nameStudent"
                    >
                      <a-avatar :src="user.imageStaff ?? user.imageStudent" :size="25" />
                    </a-tooltip>
                  </a-avatar-group>
                </td>

                <td class="w-3/12 border border-x-0">
                  <a-tooltip title="Tỷ lệ bình chọn">
                    <div class="flex items-center justify-end space-x-2">
                      <p class="text-sm whitespace-nowrap">Bình chọn:</p>
                      <a-progress
                        class="min-w-[100px]"
                        :percent="percentMap[element.id] || 0"
                        stroke-color="#4F46E5"
                        :show-info="true"
                        size="small"
                      />
                    </div>
                  </a-tooltip>
                </td>

                <td class="text-center w-1/12 border border-x-0" @click.stop>
                  <a-tooltip title="Điểm">
                    <template v-if="!element.editing">
                      <span
                        @click="element.editing = true"
                        class="inline-block bg-slate-300 hover:bg-slate-200 text-gray-600 text-xs rounded-md cursor-pointer px-2 py-1"
                      >
                        {{ element.point === null || element.point === '0' ? '-' : element.point }}
                      </span>
                    </template>
                    <template v-else>
                      <a-input
                        v-model:value="element.point"
                        type="number"
                        min="0"
                        size="small"
                        @blur="updateTaskStoryPoint(element.id, element.point)"
                        @keyup.enter="updateTaskStoryPoint(element.id, element.point)"
                        @input="onInput"
                      />
                    </template>
                  </a-tooltip>
                </td>

                <td class="text-end border border-x-0" @click.stop>
                  <a-popconfirm
                    title="Bạn có chắc chắn muốn xóa công việc này không?"
                    ok-text="Xóa"
                    cancel-text="Hủy"
                    @confirm="removeTask(element.id)"
                  >
                    <a-tooltip title="Xóa công việc">
                      <a-button class="flex items-center justify-center w-8 h-8">
                        <DeleteOutlined />
                      </a-button>
                    </a-tooltip>
                  </a-popconfirm>
                </td>
              </tr>
            </template>
          </draggable>
        </table>

        <a-button @click="showAddTaskInput" v-if="isAddingTask == false">+ Tạo công việc </a-button>

        <div class="border border-spacing-1 w-full flex items-center space-x-2" v-if="isAddingTask">
          <a-select
            v-model:value="typeValue"
            class="w-2/12 max-w-35"
            :bordered="false"
            placeholder="Chọn loại"
          >
            <a-select-option v-for="item in listTypeTodo" :key="item.id" :value="item.id">
              <template #default>
                <template v-if="item.type === 'Lỗi'">
                  <BugOutlined style="color: red; margin-right: 6px" />
                </template>
                <template v-else-if="item.type === 'Công việc'">
                  <CheckSquareOutlined style="color: #0083ff; margin-right: 6px" />
                </template>
                <template v-else>
                  <ThunderboltOutlined style="color: orange; margin-right: 6px" />
                </template>
                {{ item.type }}
              </template>
            </a-select-option>
          </a-select>

          <div class="flex flex-col w-full">
            <input
              v-model="newTask"
              @keyup.enter="addTask"
              @blur="validateNewTask"
              placeholder="Tạo công việc mới?"
              class="flex-1 p-2 rounded-lg h-11 border border-blue-300 outline-none focus:border-blue-500"
              autofocus
            />

            <div v-if="newTaskError" class="text-red-500 text-sm mt-1">
              {{ newTaskError }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- <TodoDetailModal v-model="showTaskDetailModal" :todoId="selectedTodoId" class="custom-modal" /> -->
</template>

<script setup lang="ts">
import { USER_INFO_STORAGE_KEY } from '@/constants/storagekey'
import { phaseWebSocket } from '@/services/api/manage/phase/phase.socket.api'
import {
  getAllStaffProject,
  getAllTodo,
  getAllUserByTodo,
  todoResponse
} from '@/services/api/manage/todo/todo.api'
import { dataCreateTodo, todoWebSocket } from '@/services/api/manage/todo/todo.socket.api'
import { todoVoteWebSocket } from '@/services/api/manage/todo/todovote.socket.api'
import { getListTypeTodo } from '@/services/api/manage/todo/typetodo.ap'
import { TypeTodoResponse } from '@/services/api/member/projectdetail/typetodo.api'
import { localStorageAction } from '@/utils/storage'
import {
  BugOutlined,
  CheckSquareOutlined,
  DeleteOutlined,
  DownOutlined,
  RightOutlined,
  ThunderboltOutlined,
  UnorderedListOutlined
} from '@ant-design/icons-vue'
import { library } from '@fortawesome/fontawesome-svg-core'
import {
  faCircleInfo,
  faEye,
  faFilter,
  faPenToSquare,
  faReceipt,
  faRotateRight,
  faTrash,
  faTrashAlt
} from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { debounce, size } from 'lodash'
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { toast } from 'vue3-toastify'
import draggable from 'vuedraggable'
library.add(
  faReceipt,
  faPenToSquare,
  faCircleInfo,
  faFilter,
  faRotateRight,
  faTrash,
  faTrashAlt,
  faEye
)

const props = defineProps<{
  todos: todoResponse[]
  idProject: string
}>()
const emit = defineEmits(['update-todos', 'level', 'data-staff-project'])
const paramsTodo = reactive({
  page: 1,
  size: 10000,
  idProject: '',
  totalItem: '',
  level: ''
})
const isAddingSprint = ref(false)
const newSprintName = ref('')
const newSprint = ref('')
const showAddSprintInput = () => {
  isAddingSprint.value = true
  newSprintName.value = ''
}

const addSprint = async () => {
  const payload = { name: newSprint.value, code: newSprint.value, idProject: props.idProject }
  await phaseWebSocket.sendMessageCreatePhase(payload)
  newSprint.value = ''
  isAddingSprint.value = !isAddingSprint.value
  // fetchDataPhase()
  toast.success('Thêm giai đoạn thành công!')
}

const isAllSelected = computed(() => {
  return todoList.value.length > 0 && todoList.value.every((todo) => checkedTodo.value.has(todo.id))
})

const isIndeterminate = computed(() => {
  const checkedCount = todoList.value.filter((todo) => checkedTodo.value.has(todo.id)).length
  return checkedCount > 0 && checkedCount < todoList.value.length
})

// Thêm method để xử lý chọn tất cả
const handleSelectAll = (e: any) => {
  const checked = e.target.checked

  // Tạo một Set mới để lưu trữ các ID đã chọn
  const newCheckedTodo = new Set<string>()

  if (checked) {
    // Chọn tất cả
    todoList.value.forEach((todo) => {
      newCheckedTodo.add(todo.id)
    })
  }

  // Gán lại giá trị mới cho checkedTodo
  checkedTodo.value = newCheckedTodo

  console.log('Updated checked todos:', Array.from(checkedTodo.value))
}

// type todo
const listTypeTodo = ref<TypeTodoResponse[]>()
const typeValue = ref('')
const itemDefault = ref<TypeTodoResponse[]>()

const fetchDataTypeTodo = async () => {
  const res = await getListTypeTodo()
  listTypeTodo.value = res.data
  const typeDefault = listTypeTodo.value.filter((item) => item.type === 'Công việc')
  if (typeDefault) {
    itemDefault.value = typeDefault
    typeValue.value = typeDefault[0].id
  }
}

const selectedText = reactive<{ [key: string]: string }>({})
const selectedKey = reactive<{ [key: string]: string[] }>({})

// const handleSelect = async (event, id) => {
//   try {
//     const levelMap = {
//       '3': 'Thấp',
//       '2': 'Trung bình',
//       '1': 'Cao',
//       '0': 'Quan trọng'
//     }
//     const key = event.key
//     console.log('🚀 Bắt đầu cập nhật - Selected key:', key, 'for ID:', id)

//     // Cập nhật UI trước
//     selectedKey[id] = [key]
//     selectedText[id] = levelMap[key]

//     // Tạo payload để gửi
//     const payload = {
//       name: editedTaskName[id],
//       code: editedTaskName[id],
//       priorityLevel: key
//     }
//     console.log('📤 Payload sẽ gửi đi:', payload)

//     // Gọi API cập nhật
//     try {
//       await todoWebSocket.sendMessageUpdateTodo(id, payload)

//       // Gọi fetchDataTodo để cập nhật lại dữ liệu
//       // await fetchDataTodo()

//       toast.success('Cập nhật mức độ thành công')
//     } catch (error) {
//       throw error // Ném lỗi để catch bên ngoài xử lý
//     }
//   } catch (error) {
//     toast.error('Có lỗi xảy ra khi cập nhật mức độ')
//   }
// }

const handleSelect = async (event, id) => {
  try {
    const levelMap = {
      '3': 'Thấp',
      '2': 'Trung bình',
      '1': 'Cao',
      '0': 'Quan trọng'
    }
    const key = event.key
    console.log('🚀 Bắt đầu cập nhật - Selected key:', key, 'for ID:', id)

    // Cập nhật UI trước
    selectedKey[id] = [key]
    selectedText[id] = levelMap[key]

    const priorityLevelMap = {
      '3': 'THAP',
      '2': 'TRUNG_BINH',
      '1': 'CAO',
      '0': 'QUAN_TRONG'
    }

    // Tạo payload để gửi
    const payload = {
      name: editedTaskName[id],
      code: editedTaskName[id],
      priorityLevel: key
    }
    console.log('📤 Payload sẽ gửi đi:', payload)

    // Gọi API cập nhật
    try {
      await todoWebSocket.sendMessageUpdateTodo(id, payload)
      console.log('✅ API đã được gọi thành công')

      // Gọi fetchDataTodo để cập nhật lại dữ liệu
      // await fetchDataTodo()

      // toast.success('Cập nhật mức độ thành công')
    } catch (error) {
      throw error // Ném lỗi để catch bên ngoài xử lý
    }
  } catch (error) {
    toast.error('Có lỗi xảy ra khi cập nhật mức độ')
  }
}

const handleTodoClick = (idTodo: string, event: Event) => {
  selectedTodoId.value = idTodo
  showTaskDetailModal.value = true
}

const newTaskError = ref('')

function validateNewTask(): boolean {
  const task = newTask.value.trim()
  newTaskError.value = ''
  return true
}

const showTaskDetailModal = ref(false)

const selectedTodoId = ref<string | null>(null)

const openTask = ref(false)
const toggleTaskVisibility = () => {
  openTask.value = !openTask.value
}

const isAddingTask = ref(false)

const newTaskName = ref('')

const showAddTaskInput = () => {
  isAddingTask.value = true
  newTaskName.value = ''
}

const valuePriorityLevel = reactive({})

const checkedTodo = ref(new Set<string>())

const countStaffProject = ref(0)

const todoList = ref([...props.todos])

const listUserVote = ref([])
// Khi dữ liệu thay đổi, cập nhật danh sách Todo
watch(
  () => props.todos,
  (newTodos) => {
    idProject.value = route.params.id as string
    dataTodoProject()
    // todoList.value = [...newTodos]
  },
  { deep: true }
)

const route = useRoute()
const idProject = ref('')
const dataTodoProject = debounce(async () => {
  const res = await getAllTodo({
    idProject: idProject.value,
    page: 1,
    size: 1000
  })

  todoList.value = res.data as unknown as todoResponse[]

  for (const todo of todoList.value) {
    const key =
      todo.priorityLevel === 'CAO'
        ? '1'
        : todo.priorityLevel === 'THAP'
        ? '3'
        : todo.priorityLevel === 'TRUNG_BINH'
        ? '2'
        : todo.priorityLevel === 'QUAN_TRONG'
        ? '0'
        : null

    selectedKey[todo.id as string] = [key]
    selectedText[todo.id as string] = {
      '3': 'Thấp',
      '2': 'Trung bình',
      '1': 'Cao',
      '0': 'Quan trọng'
    }[key]

    valuePriorityLevel[todo.id as string] = key

    editedTaskName[todo.id as string] = todo.name
  }
}, 100)

// watchEffect(() => {
//   todoList.value = [...props.todos]
// })

// Một computed property đang thay đổi giá trị

watch(
  todoList,
  (newValue) => {
    console.log('🔥 Danh sách Todo sau khi kéo thả:', newValue)
    countTask.value = newValue.length
    // dataTodoProject()
  },
  { deep: true }
)

// Gửi sự kiện cập nhật Todo

const handleDraggableChange = (event) => {
  if (event.removed) {
    const removedTodo = event.removed.element

    // Kiểm tra nếu todo bị xóa có trong danh sách đã chọn
    if (checkedTodo.value.has(removedTodo.id)) {
      // Lấy tất cả các todo đã chọn
      const selectedTodos = props.todos.filter((todo) => checkedTodo.value.has(todo.id))
      // Log các todo đã chọn

      // Xóa tất cả các todo đã chọn
      selectedTodos.forEach((todo) => {
        const index = props.todos.findIndex((t) => t.id === todo.id)
        if (index !== -1) {
          props.todos.splice(index, 1)
        }
      })

      // Xóa danh sách đã chọn
      checkedTodo.value.clear()
      dataTodoProject()
    } else {
      // Xử lý xóa một todo đơn lẻ
      const index = props.todos.findIndex((todo) => todo.id === removedTodo.id)
      if (index !== -1) {
        props.todos.splice(index, 1)
      }
      dataTodoProject()
    }
  }
}

const rederDataa = debounce(() => {
  dataTodoProject()
}, 100)

const dataFecth = async () => {
  try {
    const res = await getAllTodo(paramsTodo)
    todoList.value = res.data as unknown as todoResponse[]

    countTask.value = todoList.value.length
  } catch (error) {
    console.error('❌ Lỗi khi lấy dữ liệu todo:', error)
  }
}

const handleCheckboxChange = (id: string, e: any) => {
  if (e.target.checked) {
    checkedTodo.value.add(id)
  } else {
    checkedTodo.value.delete(id)
  }
}

const isChecked = (id: string) => {
  return checkedTodo.value.has(id)
}

const percentMap = reactive<Record<string, number>>({})
const updatePercentVoted = async (id: string) => {
  if (!id || countStaffProject.value === 0) {
    percentMap[id] = 0
    return
  }

  console.log(`📡 Gọi API getAllUserByTodo cho Task ID: ${id}`)

  try {
    const res = await getAllUserByTodo(paramsTodo, id)
    console.log(`✅ Dữ liệu trả về cho Task ID: ${id}:`, res.data)
    userVoteMapByTaskId.value[id] = res.data.content
    console.log('Danh sách user in todo')
    console.log(userVoteMapByTaskId.value[id])

    if (!res || !res.data || !res.data.content) {
      console.error(`❌ Lỗi: Không có dữ liệu hợp lệ cho Task ID: ${id}`)
      return
    }

    // for (const item of res.data.content) {
    //   userVoteMap[item.id as string] =
    //     item.imageStudent == null ? item.imageStaff : item.imageStudent
    // }

    const votedPercent = Math.floor((res.data.content.length / countStaffProject.value) * 100)
    console.log(`📊 Phần trăm cập nhật cho Task ID ${id}: ${votedPercent}%`)

    percentMap[id] = votedPercent

    // sortTasksByProgress()
  } catch (error) {
    console.error(`❌ Lỗi khi gọi API getAllUserByTodo cho Task ID ${id}:`, error)
  }
}

const formatDate = (timestamp) => {
  const date = new Date(timestamp)
  const day = String(date.getDate()).padStart(2, '0')
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const year = date.getFullYear()
  return `${day}/${month}/${year}`
}

const countTask = ref(0)
const editedTaskId = ref<string | null>(null)
// const editedTaskName = ref<string>('')
const editedTaskName = reactive<{ [key: string]: string }>({})

const user = localStorageAction.get(USER_INFO_STORAGE_KEY)
// Xóa task
const removeTask = (id: string) => {
  // Xóa Todo khỏi danh sách
  const index = todoList.value.findIndex((todo) => todo.id === id)
  if (index !== -1) {
    todoList.value.splice(index, 1)
  }

  // Gửi API xóa (nếu cần)
  todoWebSocket.sendMessageDeleteTodo(id)

  toast.success('Xóa công việc thành công')
  console.log(`🗑 Đã xóa Task ID: ${id}`)
}

const newTask = ref('')

// Dữ liệu
const userVoteMapByTaskId = ref<Record<string, any[]>>({})
const totalPoint = ref<number>(0)
const fetchDataTodo = debounce(async () => {
  try {
    const resData = await getAllStaffProject(idProject.value)
    countStaffProject.value = resData.data.length as number

    console.log('số lượng thành viên ', countStaffProject.value)

    emit('data-staff-project', resData.data)

    const res = await getAllTodo({ idProject: idProject.value, page: 1, size: 1000 })
    todoList.value = res.data as unknown as todoResponse[]
    await Promise.all(
      todoList.value.map(async (todo) => {
        await updatePercentVoted(todo.id as string)
      })
    )

    totalPoint.value = res.data.reduce((total, todo) => {
      const point = Number(todo?.point)
      return total + (isNaN(point) ? 0 : point)
    }, 0)

    for (const todo of props.todos) {
      console.log('todo')
      console.log(todo.priorityLevel)
      const key =
        todo.priorityLevel === 'CAO'
          ? '1'
          : todo.priorityLevel === 'THAP'
          ? '3'
          : todo.priorityLevel === 'TRUNG_BINH'
          ? '2'
          : todo.priorityLevel === 'QUAN_TRONG'
          ? '0'
          : null

      selectedKey[todo.id as string] = [key as any]
      selectedText[todo.id as string] = {
        '3': 'Thấp',
        '2': 'Trung bình',
        '1': 'Cao',
        '0': 'Quan trọng'
      }[key as any]

      valuePriorityLevel[todo.id as string] = key

      editedTaskName[todo.id as string] = todo.name
    }
    // sortTasksByProgress()
  } catch (error) {
    console.error('❌ Lỗi khi lấy dữ liệu todo:', error)
  }
}, 100)

onMounted(async () => {
  idProject.value = route.params.id as string
  dataFecth()

  fetchDataTypeTodo()
  await fetchDataTodo()
  phaseWebSocket.subscribeCreatePhase(dataTodoProject)
  phaseWebSocket.subscribeDeletePhase(dataTodoProject)
  phaseWebSocket.subscribeUpdatePhase(dataTodoProject)
  phaseWebSocket.subscribeCreateTodoByPhase(dataTodoProject)
  phaseWebSocket.subscribeDeleteTodoByPhase(dataTodoProject)
  todoWebSocket.subscribeCreateTodo(dataTodoProject)
  todoWebSocket.subscribeDeleteTodo(dataTodoProject)
  todoWebSocket.subscribeUpdateTodo(dataTodoProject)
  todoVoteWebSocket.subscribeCreateTodoVote(fetchDataTodo)
  todoVoteWebSocket.subscribeDeleteTodoVote(fetchDataTodo)
  valueLevel.value = null
})

const closeInput = () => {
  isAddingTask.value = !isAddingTask.value
}
const userLogin = localStorageAction.get(USER_INFO_STORAGE_KEY)
// Thêm Task mới
const addTask = async () => {
  if (!validateNewTask()) return

  if (newTask.value == null || newTask.value == '' || typeValue.value == null) {
    isAddingTask.value = !isAddingTask.value
    toast.error('Vui lòng nhập đủ thông tin')
    return
  } else {
    toast.success('Thêm công việc thành công')
    const payload: dataCreateTodo = {
      name: newTask.value,
      code: newTask.value,
      idProject: props.idProject as string,
      idType: typeValue.value,
      idUser: userLogin.userId, // Người thao tác
      nameUser: userLogin.fullName, // Tên người thao tác
      urlPath: route.fullPath // Đường dẫn activity
    }

    isAddingTask.value = !isAddingTask.value
    console.log('📤 Payload gửi đi: ', payload)
    todoWebSocket.sendMessageCreateTodo(payload)
    newTask.value = ''
    newTaskError.value = ''
    typeValue.value = itemDefault[0].id
    dataTodoProject()
  }
}

// Bật chế độ chỉnh sửa
const enableEdit = (task: todoResponse) => {
  editedTaskId.value = task.id as string
  editedTaskName[task.id as string] = task.name
}

const valueLevel = ref(null)

const handlePriorityChange = (id: string, value: string) => {
  todoWebSocket.sendMessageUpdateTodo(id, {
    name: editedTaskName[id], // Nếu chưa chỉnh sửa thì giữ nguyên
    code: editedTaskName[id], // N
    priorityLevel: value,
    idType: typeValue.value
  })

  // sortTasksByProgress()

  // toast.success('Cập nhật mức độ thành công')
}

// Cập nhật Task

const updateTask = async (id: string) => {
  if (!editedTaskId.value) return

  const currentPriority = valuePriorityLevel[id] // Giữ nguyên priority đã có

  await todoWebSocket.sendMessageUpdateTodo(id, {
    name: editedTaskName[id], // ✅ Lấy từ danh sách chỉnh sửa
    code: editedTaskName[id],
    priorityLevel: currentPriority as string,
    idType: typeValue.value
  })

  // toast.success('Task cập nhật thành công!')
  // fetchDataTodo()
  editedTaskId.value = null
}

watch(valueLevel, (newValue) => {
  emit('level', newValue)
})

// Thêm method mới
const handleDragStart = (evt) => {
  const draggedTodoId = evt.item.dataset.id

  // Nếu todo được kéo nằm trong danh sách đã chọn
  if (checkedTodo.value.has(draggedTodoId)) {
    // Lấy tất cả các todo đã chọn
    const selectedTodos = todoList.value.filter((todo) => checkedTodo.value.has(todo.id))
    // Gán thông tin về các todo đã chọn vào element được kéo
    evt.item._underlying_vm_.selectedTodos = selectedTodos
  }
}

const handleTodoDrop = (evt) => {
  if (!evt.item.dataset.selectedTodos) return

  const selectedTodos = JSON.parse(evt.item.dataset.selectedTodos)
  // Xóa tất cả các todo đã chọn khỏi danh sách
  todoList.value = todoList.value.filter((todo) => !selectedTodos.includes(todo.id))
  // emit('update-todos', todoList.value)
}

const getPriorityClass = (key: string) => {
  switch (key) {
    case '3':
      return 'priority-low'
    case '2':
      return 'priority-medium'
    case '1':
      return 'priority-high'
    case '0':
      return 'priority-important'
    default:
      return ''
  }
}

// update story point todo
const storyPoint = ref<string>('')

const updateTaskStoryPoint = async (id: string, point: string) => {
  console.log('id todo :', id)

  if (Number(point) < 0) {
    point = '0'
  } else {
    point = point as any
  }
  await todoWebSocket.sendMessageUpdateTodo(id, {
    point: point as string
  })

  fetchDataTodo()
}
const onInput = (e) => {
  const value = Number(e.target.value)
  if (value < 0) {
    storyPoint.value = '0'
  } else {
    storyPoint.value = value as any
  }
}

const countStoryPointSprint = reactive<Record<string, number>>({})
</script>

<style scoped>
/* Đặt style bên ngoài scoped để đảm bảo có thể override styles của Ant Design */
.ant-modal-mask {
  position: fixed !important;
  top: 0 !important;
  left: 0 !important;
  right: 0 !important;
  bottom: 0 !important;
  background-color: rgba(0, 0, 0, 0.65) !important;
  backdrop-filter: blur(4px) !important;
  z-index: 1000 !important;
}

.custom-modal {
  width: 300% !important;
}

.ant-modal-wrap {
  position: fixed !important;
  top: 0 !important;
  left: 0 !important;
  right: 0 !important;
  bottom: 0 !important;
  z-index: 1001 !important;
  overflow: auto !important;
  outline: 0 !important;
  -webkit-overflow-scrolling: touch !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
}

.ant-modal-body {
  padding: 24px !important;
  max-height: 80vh !important;
  overflow-y: auto !important;
}

.ant-modal {
  position: relative !important;
  width: 800px !important;
  max-width: 90vw !important;
  margin: 0 auto !important;
  top: 0 !important;
  padding-bottom: 24px !important;
}

.ant-modal-content {
  position: relative !important;
  background-color: #fff !important;
  border-radius: 8px !important;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15) !important;
  width: 100% !important;
}

/* Animation cho modal */
.ant-modal-enter-active,
.ant-modal-leave-active {
  transition: opacity 0.3s ease !important;
}

.ant-modal-enter-from,
.ant-modal-leave-to {
  opacity: 0 !important;
}

.ant-modal-enter-to,
.ant-modal-leave-from {
  opacity: 1 !important;
}

.cursor-grab {
  cursor: grab;
}

.custom-row-height td {
  height: 10px !important;
  /* Giảm chiều cao hàng */
}

.selected-todo {
  background-color: rgba(59, 130, 246, 0.1) !important;
}

/* Áp dụng gradient cho nút */
:deep(.ant-btn) {
  background: var(--selected-header) !important;
  /* Gradient nền */
  color: var(--selected-text) !important;
  /* Màu chữ trắng */
  border-color: var(--selected-header) !important;
  /* Viền màu từ gradient */
}

/* Hiệu ứng hover */
:deep(.ant-btn:hover),
:deep(.ant-btn:focus) {
  background: var(--selected-header-hover) !important;
  /* Gradient khi hover */
  border-color: var(--selected-header-hover) !important;
  /* Viền khi hover */
}

.priority-low {
  color: #209fe9 !important;
}

.priority-medium {
  color: #f97316 !important;
}

.priority-high {
  color: #f87171 !important;
}

.priority-important {
  color: #dc2626 !important;
}
</style>
