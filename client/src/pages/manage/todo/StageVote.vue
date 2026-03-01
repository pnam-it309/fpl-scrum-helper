<template>
  <div class="min-h-dvh w-full pt-4 space-y-8">
    <!-- 🕒 Giai đoạn đang diễn ra ---------------------------------->
    <a-card
      title="🕒 Giai đoạn đang diễn ra"
      bordered
      class="shadow-lg rounded-lg"
      :headStyle="{ background: '#f0f2f5', fontWeight: 'bold' }"
    >
      <template v-if="check">
        <div class="flex flex-col gap-2 text-sm md:text-base">
          <p><strong>Tên:</strong> {{ stageVoteTakePlace?.name }}</p>

          <p>
            <strong>Thời gian:</strong>
            {{ stageVoteTakePlace.start }} - {{ stageVoteTakePlace.end }}
          </p>
        </div>

        <!-- Bảng & biểu đồ -->
        <div class="mt-4 flex flex-col md:flex-row gap-4">
          <div class="overflow-x-auto md:w-1/2">
            <a-table
              :columns="columnVote"
              :data-source="listStaffProject"
              row-key="id"
              @row-click="onRowClick"
              class="rounded-md"
              :pagination="{ pageSize: 5 }"
            >
              <template #bodyCell="{ column, record }">
                <div v-if="column.dataIndex === 'voteStatus'">
                  <a-tag :color="record.voteStatus === 'true' ? 'green' : 'red'">
                    {{ record.voteStatus === 'true' ? 'Đã bình chọn' : 'Chưa bình chọn' }}
                  </a-tag>
                </div>
              </template>
            </a-table>
          </div>
          <div class="md:w-1/2">
            <Bar :key="chartKey" :data="dataStatisticsTodo" :options="barOptions" />
          </div>
        </div>
      </template>

      <template v-else>
        <a-empty />
      </template>
    </a-card>

    <!-- 📋 Danh sách giai đoạn bình chọn --------------------------->
    <a-card
      title="📋 Danh sách các giai đoạn bình chọn"
      bordered
      class="shadow-lg rounded-lg"
      :headStyle="{ background: '#f0f2f5', fontWeight: 'bold' }"
    >
      <div class="overflow-x-auto">
        <a-table
          :columns="columns"
          :data-source="listStage"
          row-key="id"
          @row-click="onRowClick"
          class="rounded-md"
          :pagination="{ pageSize: 5 }"
        >
          <template #action="{ record }">
            <div class="flex justify-center gap-2">
              <a-tooltip title="Cập nhật giai đoạn bình chọn">
                <a-button
                  @click="handleDetailStage(record.idStage)"
                  class="!bg-blue-200 hover:!bg-blue-400 !text-gray-700 hover:!text-white !border-none flex items-center justify-center rounded-md px-2 py-1"
                >
                  <FormOutlined />
                </a-button>
              </a-tooltip>
              <a-tooltip title="Xóa giai đoạn bình chọn">
                <a-button
                  @click="handleDeleteStage(record.idStage)"
                  class="!bg-red-100 hover:!bg-red-600 !text-red-600 hover:!text-white !border-none flex items-center justify-center rounded-md px-2 py-1"
                >
                  <DeleteOutlined />
                </a-button>
              </a-tooltip>
            </div>
          </template>
        </a-table>
      </div>

      <!-- Chi tiết giai đoạn chọn -->
      <a-card
        v-if="selectedStage"
        title="📌 Chi tiết giai đoạn được chọn"
        bordered
        class="shadow-lg rounded-lg mt-6"
        :headStyle="{ background: '#f0f2f5', fontWeight: 'bold' }"
      >
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm md:text-base">
          <p><strong>Tên:</strong> {{ selectedStage.name }}</p>
          <p><strong>Thời gian:</strong> {{ selectedStage.start }} - {{ selectedStage.end }}</p>
          <p><strong>Mô tả:</strong> {{ selectedStage.description }}</p>
        </div>
      </a-card>
    </a-card>

    <!-- 📋 Danh sách công việc đã bình chọn ------------------------>
    <!-- <a-card
      title="📋 Danh sách công việc đã được bình chọn"
      bordered
      class="shadow-lg rounded-lg"
      :headStyle="{ background: '#f0f2f5', fontWeight: 'bold' }"
    >
      <a-table
        :columns="todoColumns"
        :data-source="tableData"
        row-key="id"
        class="rounded-md"
        table-layout="fixed"
        :pagination="{ pageSize: 5 }"
        :scroll="{ x: 'max-content' }"
      />
    </a-card> -->

    <!-- Modal chi tiết giai đoạn -->
    <ModalDetailStage
      :open="dataStage.openModal"
      :data-stage="dataStage"
      title="Cập nhật bình chọn"
      @close="closeModal"
      @success="fetchDataStage"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive, h } from 'vue'
import {
  deleteStage,
  findAllStageVote,
  getAllStage,
  stageTakePlace,
  todoVoteStage,
  getTodoVoteLevelStatistics,
  getVoteTodo
} from '../../../services/api/manage/stage/stage.api'
import { MenuProps, Modal, TableColumnsType, Tag } from 'ant-design-vue'
import {
  AppstoreOutlined,
  DeleteOutlined,
  EyeOutlined,
  FormOutlined,
  MailOutlined
} from '@ant-design/icons-vue'
import { Bar } from 'vue-chartjs'
import {
  Chart as ChartJS,
  Title,
  Tooltip as ChCh,
  Legend,
  BarElement,
  CategoryScale,
  LinearScale
} from 'chart.js'
import {
  getStaffStudentVote,
  StaffStudentVoteResponse
} from '@/services/api/member/votetodo/votetodo.api'
import { useRoute } from 'vue-router'
import dayjs from 'dayjs'
import ModalDetailStage from './ModalDetailStage.vue'
import { toast } from 'vue3-toastify'
import { Tooltip, Avatar } from 'ant-design-vue'
import {
  getAllStaffProject,
  getAllTodo,
  getAllUserByTodo,
  todoResponse
} from '@/services/api/manage/todo/todo.api'

ChartJS.register(Title, ChCh, Legend, BarElement, CategoryScale, LinearScale)
const tableData = ref<any[]>([])
const short = ref(0)

const medium = ref(0)

const important = ref(0)
const high = ref(0)
const barOptions = {
  responsive: true,
  maintainAspectRatio: false,
  animation: {
    duration: 500
  },
  plugins: {
    legend: {
      display: false // Ẩn legend
    },
    title: {
      display: true,
      text: 'Thống kê mức độ công việc được bình chọn',
      font: {
        size: 14
      }
    }
  },
  scales: {
    y: {
      type: 'linear' // Đảm bảo rằng trục y sử dụng scale 'linear'
    }
  }
}

const dataStatisticsTodo = ref({
  labels: ['Mức độ thấp', 'Mức độ trung bình', 'Mức độ cao', 'Mức độ quan trọng'],
  datasets: [
    {
      label: ['Thống kê báo cáo'],
      data: [],

      backgroundColor: [
        'rgb(54, 162, 235)',
        'rgb(255, 99, 132)',
        'rgb(255, 200, 132)',
        'rgb(255,100,100)'
      ],
      hoverOffset: 4
    }
  ]
})

const voteSuccess = ref(0)
const voteFail = ref(0)
const route = useRoute()
const idStageVote = ref<string>()
const chartKey = ref(0)
const listStaffProject = ref<StaffStudentVoteResponse[]>()
const listTodoVoteStage = ref<todoVoteStage[]>()

const idProject = ref('')
const fetchDataVote = async () => {
  const response = await getStaffStudentVote(route.params.id as string)

  const res = await findAllStageVote(idStageVote.value as string)

  const statistics = await getTodoVoteLevelStatistics(idProject.value as string)

  listTodoVoteStage.value = res.data

  statistics.data.forEach((item) => {
    switch (item.level) {
      case 3:
        short.value = item.total
        break
      case 2:
        medium.value = item.total
        break
      case 1:
        high.value = item.total
        break
      case 0:
        important.value = item.total
        break
    }
  })

  dataStatisticsTodo.value.datasets[0].data = [
    short.value,
    medium.value,
    high.value,
    important.value
  ]

  chartKey.value++

  listStaffProject.value = response.data.data
  const data = response.data.data as []

  voteSuccess.value = data.filter((item) => item.voteStatus === 'true').length
  voteFail.value = data.filter((item) => item.voteStatus === 'false').length

  console.log(voteFail.value)
}

const columnVote: TableColumnsType = [
  {
    title: 'STT',
    key: 'orderNumber',
    dataIndex: 'orderNumber',
    width: 5,
    align: 'center'
  },
  { title: 'Thành viên', key: 'nameStaff', dataIndex: 'nameStaff', width: 100, align: 'center' },
  { title: 'Chức vụ', key: 'role', dataIndex: 'role', width: 10, align: 'center' },
  {
    title: 'Trạng thái',
    key: 'voteStatus',
    dataIndex: 'voteStatus',
    width: 10,
    align: 'center',
    customRender: ({ text }: { text: string }) =>
      text === 'true' ? 'Đã bình chọn' : 'Chưa bình chọn'
  }
]

interface Stage {
  id: number
  name: string
  start: string
  end: string
  description: string
  status: string
}

const current = ref<string[]>(['detail'])
const items = ref<MenuProps['items']>([
  {
    key: 'detail',
    icon: () => h(MailOutlined),
    label: 'Chi tiêt',

    title: 'Chi tiêt'
  },
  {
    key: 'stage',
    icon: () => h(AppstoreOutlined),
    label: 'Danh sách giai đoạn',
    title: 'Danh sách giai đoạn'
  },
  {
    key: 'todo',
    icon: () => h(AppstoreOutlined),
    label: 'Danh sách công việc',
    title: 'Danh sách công việc'
  }
])

const selectedStage = ref<Stage | null>(null)

const columns = [
  { title: 'STT', dataIndex: 'orderNumber', key: 'orderNumber', width: '10%' },
  { title: 'Tên', dataIndex: 'name', key: 'name', width: '20%' },
  {
    title: 'Bắt đầu',
    dataIndex: 'startTime',
    width: '25%',
    key: 'startTime',
    customRender: ({ text }: { text: string }) => {
      const timestamp = parseInt(text, 10) // Chuyển đổi chuỗi thà nh số nguyên
      return !isNaN(timestamp) ? dayjs(timestamp).format('DD/MM/YYYY HH:mm:ss') : '---'
    }
  },
  {
    title: 'Kết thúc',
    dataIndex: 'endTime',
    width: '25%',
    key: 'endTime',
    customRender: ({ text }: { text: string }) => {
      const timestamp = parseInt(text, 10) // Chuyển đổi chuỗi thành số nguyên
      return !isNaN(timestamp) ? dayjs(timestamp).format('DD/MM/YYYY HH:mm:ss') : '---'
    }
  },

  {
    title: 'Hành động',
    key: 'action',
    align: 'center',
    width: '15%',
    // KHÔNG cần dataIndex nếu dùng slot
    slots: { customRender: 'action' } // sử dụng slot action
  }
]

const stageVoteTakePlace = reactive({
  name: '',
  start: '',
  end: '',
  idPhase: ''
})

const check = ref(false)
const stageDetail = async () => {
  const res = await stageTakePlace(idProject.value) // đảm bảo stageTakePlace là async

  if (!res || !res.data) {
    console.warn('Dữ liệu trả về không hợp lệ:', res)
    return
  }

  idStageVote.value = res.data.id
  stageVoteTakePlace.start = dayjs(Number(res.data.startTime)).format('DD/MM/YYYY HH:mm:ss')

  stageVoteTakePlace.end = dayjs(Number(res.data.endTime)).format('DD/MM/YYYY HH:mm:ss')
  stageVoteTakePlace.name = res.data.name

  stageVoteTakePlace.idPhase = res.data.id

  if (stageVoteTakePlace.idPhase == null || stageVoteTakePlace.idPhase == '') {
    check.value = false
  } else {
    check.value = true
  }
}

const onRowClick = (record: Stage) => {
  selectedStage.value = record
}

const listStage = ref<stageTakePlace[]>()

const paramStage = reactive({
  page: 1,
  size: 100,
  idProject: ''
})

const fetchDataStage = async () => {
  paramStage.idProject = route.params.id as string
  const res = await getAllStage(paramStage)
  listStage.value = res.data.content
}

// Cột của bảng
const todoColumns = [
  {
    title: 'STT',
    key: 'index',
    width: '5%',
    customRender: ({ index }) => index + 1
  },
  {
    title: 'Tên công việc',
    dataIndex: 'name',
    key: 'name',
    width: '55%',
    customRender: ({ text }) =>
      h('div', { style: { wordBreak: 'break-word', whiteSpace: 'normal' } }, text)
  },
  {
    title: 'Thành viên bình chọn',
    dataIndex: 'memberNames',
    key: 'memberNames',
    width: '15%',
    customRender: ({ text }) => {
      if (!Array.isArray(text)) return null
      return h(
        'div',
        { class: 'flex items-center gap-1 flex-wrap' },
        text.map((member, index) =>
          h(
            Tooltip,
            { title: member.name, key: index },
            {
              default: () =>
                h(Avatar, {
                  src: member.image,
                  alt: member.name,
                  size: 'small'
                })
            }
          )
        )
      )
    }
  },
  {
    title: 'Mức độ',
    dataIndex: 'level',
    key: 'level',
    width: '10%',
    customRender: ({ text }) => {
      const colorMap = {
        3: { text: 'Thấp', color: 'green' },
        2: { text: 'Trung bình', color: 'blue' },
        1: { text: 'Cao', color: 'orange' },
        0: { text: 'Quan trọng', color: 'red' }
      }
      const { text: label = 'Không xác định', color = 'gray' } = colorMap[text] || {}
      return h(Tag, { color }, () => label)
    }
  },
  {
    title: 'Phần trăm bình chọn',
    dataIndex: 'votePercent',
    key: 'votePercent',
    width: '15%',
    customRender: ({ text }) => `${text}`
  }
]

const countStaffProject = ref(0)

// Hàm fetch dữ liệu
const fetchVotedTodo = async () => {
  if (!idProject.value) return

  try {
    const response = await getVoteTodo(idProject.value)
    const rawData = response.data

    const resData = await getAllStaffProject(idProject.value)
    countStaffProject.value = resData.data.length as number

    // Tổng số thành viên duy nhất
    const uniqueMembers = [...new Set(rawData.map((item) => item.memberName))]

    const groupedData = rawData.reduce((acc, item) => {
      const existing = acc.find((task) => task.name === item.name)
      const member = {
        name: item.memberName,
        image: item.memberImage
      }

      if (existing) {
        const alreadyAdded = existing.memberNames.some((m) => m.name === member.name)
        if (!alreadyAdded) {
          existing.memberNames.push(member)
        }
      } else {
        acc.push({
          name: item.name,
          memberNames: [member],
          level: item.level
        })
      }
      return acc
    }, [])

    // Tính phần trăm
    groupedData.forEach((task) => {
      task.votePercent = `${((task.memberNames.length / countStaffProject.value) * 100).toFixed(
        1
      )}%`
    })
    groupedData.sort((a, b) => b.memberNames.length - a.memberNames.length)
    tableData.value = groupedData
  } catch (error) {
    console.error('Lỗi khi lấy danh sách công việc đã được bình chọn:', error)
  }
}

// Giả lập fetch dữ liệu
onMounted(() => {
  idProject.value = route.params.id as string
  paramStage.idProject = idProject.value as string
  stageDetail()
  fetchVotedTodo()
  fetchDataVote()
  fetchDataStage()
  // Dữ liệu mẫu
})

const dataStage = reactive({
  openModal: false,
  idStage: '',
  idPhase: ''
})

const handleDetailStage = (id: string) => {
  dataStage.idStage = id
  dataStage.openModal = true
}

const handleDeleteStage = (id: string) => {
  Modal.confirm({
    title: 'Xác nhận xóa',
    content: 'Bạn có chắc chắn muốn xóa giai đoạn này không?',
    okText: 'Xóa',
    cancelText: 'Hủy',
    okType: 'danger',
    onOk: async () => {
      const res = await deleteStage(id)
      toast.success(res.message)
      fetchDataStage()
    }
  })
}
const closeModal = () => {
  dataStage.openModal = false
}
</script>

<style scoped></style>
