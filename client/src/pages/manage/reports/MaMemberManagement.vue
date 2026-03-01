<template>
  <div class="teams-container">
    <div class="header">
      <h2 style="font-weight: bold;">Dự án: {{ people[0]?.codeProject || '' }}</h2>
      <div class="buttons">
        <button class="btn btn-primary" @click="openAddModal">
          <span class="mr-2">+</span> Thêm thành viên
        </button>
        <MaUserModal
          :idFacilityProject="idFacilityProject ?? ''"
          :open="data.isModalOpen"
          :projectId="idProject"
          :title="modalTitle"
          @add="openAddModal"
          @close="closeModal"
        />
      </div>
    </div>

    <div class="search-bar flex items-center space-x-2">
      <a-input v-model:value="searchText" placeholder="Tìm kiếm theo tên / mã / email" class="w-full" />
      <a-tooltip title="Làm mới tìm kiếm" :overlayStyle="{ zIndex: 9999 }">
        <a-button @click="resetSearch" class="flex items-center justify-center">
          <ReloadOutlined />
        </a-button>
      </a-tooltip>
    </div>

    <div class="people-section">
      <h3 style="font-weight: bold;">Thành viên trong dự án</h3>
      <div v-if="people.length === 0" class="mt-2 text-gray-500">Không có thành viên nào.</div>

      <div v-else class="bg-white p-4 rounded-lg shadow-sm people-list">
        <div class="grid grid-cols-7 gap-4 text-sm font-bold text-gray-700 bg-gray-100 p-2 rounded-t-lg mb-2">
          <span>Avatar</span>
          <span>Tên</span>
          <span>Mã</span>
          <span>Chức vụ</span>
          <span>Tổng todo đã làm trong dự án</span>
          <span>Tổng todo đang làm trong sprint</span>
          <span>Tiến độ</span>
          <span>Hành động</span>
        </div>
        <div
          v-for="person in people"
          :key="person.id"
          class="grid grid-cols-7 gap-4 py-2 border-b last:border-b-0 items-center relative"
        >
          <div class="flex items-center">
            <div class="avatar" :style="{ backgroundColor: person.color || getColor(person.name) }">
              {{ getInitials(person.name) }}
            </div>
          </div>
          <div>
            <p class="text-sm font-medium text-gray-900">{{ person.name }}</p>
            <p class="text-xs text-gray-500">{{ person.email }}</p>
          </div>
          <p class="text-xs text-gray-500">{{ person.code }}</p>
          <p class="text-xs text-gray-500">{{ getRoleName(person.role) }}</p>
          <span class="text-gray-600">{{ person.countTodo }} todo</span>
          <span class="text-gray-600">{{ person.countTodoByPhase }} todo</span>
          <div class="w-64">
            <a-tooltip :title="`${person.countCompletedTodo || 0} todo / ${person.countTodoByPhase || 0} todo`" :overlayStyle="{ zIndex: 9999 }">
              <div class="w-full bg-gray-200 rounded-full h-2">
                <div
                  class="h-2 rounded-full transition-all duration-300"
                  :style="{ width: (person.progress || 0) + '%', backgroundColor: person.color || '#4CAF50' }"
                ></div>
              </div>
            </a-tooltip>
            <!-- <div class="w-full bg-gray-200 rounded-full h-2">
              <div
                class="h-2 rounded-full transition-all duration-300"
                :style="{ width: (person.progress || 0) + '%', backgroundColor: person.color || '#4CAF50' }"
              ></div>
            </div> -->
            <div class="flex justify-between text-xs text-gray-600 mt-1">
              <span>{{ person.progress || 0 }}%</span>
              <span>{{ 100 - (person.progress || 0) }}%</span>
            </div>
          </div>
          <div class="flex justify-end">
            <a-tooltip title="Xóa thành viên" :overlayStyle="{ zIndex: 9999 }">
              <button class="delete-btn" @click="removePerson(person.idStaffProject)">
                <DeleteOutlined style="font-size: 16px; color: white;" />
              </button>
            </a-tooltip>
          </div>
        </div>

        <a-pagination
          class="mt-4 text-right"
          :current="pagination.page"
          :pageSize="pagination.pageSize"
          :total="pagination.total"
          :showSizeChanger="true"
          :pageSizeOptions="['10', '20', '30', '40', '50']"
          @change="handlePageChange"
          @showSizeChange="handlePageSizeChange"
        />
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import { toast } from 'vue3-toastify'
import { Modal } from 'ant-design-vue'
import { DeleteOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import MaUserModal from './MaUserModal.vue'
import { getListUser, getFacilityByProjectId, updateStatusUser, ParamsGetUser } from '@/services/api/manage/user/user.api'
import { detailPhase } from '@/services/api/member/phase/phase.api'

const route = useRoute()

const idProject = route.params.id as string

const idPhase = ref()
const check = ref(false)
const phaseSuccess = async () => {
  if (!idProject) return

  try {
    const res = await detailPhase(idProject)
    idPhase.value = res.data.id
    check.value = !!idPhase.value

    console.log('idPhase:', idPhase.value)
  } catch (error) {
    console.error('Lỗi khi gọi getPhaseSuccess:', error)
  }
}

const searchText = ref('')
const people = ref<any[]>([])
const idFacilityProject = ref<string | null>(null)
const data = reactive({ isModalOpen: false })
const modalTitle = computed(() => (idProject ? 'Cập nhật dự án' : 'Thêm dự án'))

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const roleMap: Record<string, string> = {
  "0": "Quản lý",
  "1": "Dev",
  "2": "Tester"
}

const getRoleName = (role: string) => roleMap[role] || "Không rõ"
const getInitials = (name: string) => {
  if (!name) return ''
  const parts = name.trim().split(' ')
  return parts.length > 1 ? parts[0][0] + parts[1][0] : parts[0].slice(0, 2)
}
const getColor = (name: string) => {
  const colors = ['#00A170', '#0078D4', '#FF5733', '#FFC107', '#28A745']
  const hash = name.split('').reduce((acc, char) => acc + char.charCodeAt(0), 0)
  return colors[hash % colors.length]
}

const fetchUsers = async () => {
  const params: ParamsGetUser = {
    search: searchText.value.trim() || null,
    page: pagination.page,
    size: pagination.pageSize
  }
  try {
    const res = await getListUser(params, idProject, idPhase.value)
    pagination.total = res.data.totalElements 
    people.value = res.data.data.map(user => ({
      ...user,
      color: getColor(user.name),
      progress: user.progressPercentage,
      time: user.countTodoByPhase ? 'todo' : 'N/A',
      countCompletedTodo: user.countCompletedTodo || 0
    }))
  } catch (err) {
    console.error('Lỗi khi load danh sách người dùng:', err)
  }
}

const handlePageChange = (page: number) => {
  pagination.page = page
  fetchUsers()
}

const handlePageSizeChange = (_current: number, size: number) => {
  pagination.pageSize = size
  pagination.page = 1
  fetchUsers()
}


const fetchFacilityId = async (id: string) => {
  try {
    const res = await getFacilityByProjectId(id)
    idFacilityProject.value = res.data
  } catch (err) {
    console.error('Lỗi khi lấy facility:', err)
  }
}

const resetSearch = () => {
  searchText.value = ''
  fetchUsers()
}

const removePerson = (id: string) => {
  Modal.confirm({
    title: 'Xác nhận xoá',
    content: 'Bạn có chắc muốn loại thành viên này khỏi dự án?',
    okText: 'Xoá',
    okType: 'danger',
    cancelText: 'Hủy',
    centered: true,
    onOk: async () => {
      try {
        await updateStatusUser(id)
        toast.success("Xóa thành viên thành công")
      } catch (err) {
        toast.error("Xóa thất bại")
        console.error(err)
      } finally {
        fetchUsers()
      }
    }
  })
}

const openAddModal = () => (data.isModalOpen = true)
const closeModal = () => {
  data.isModalOpen = false
  fetchUsers()
}

onMounted(async () => {
  await phaseSuccess()
  await fetchUsers()
  fetchFacilityId(idProject)
})

watch(searchText, (newValue, oldValue) => {
  const timeout = setTimeout(() => {
    fetchUsers()
  }, 500)
  return () => clearTimeout(timeout)
})
</script>

<style scoped>
.teams-container {
  padding: 15px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.buttons {
  display: flex;
  gap: 8px;
}

.btn {
  padding: 6px 16px;
  border: 1px solid #ccc;
  border-radius: 4px;
  background-color: white;
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
}

.btn-primary {
  background-color: #2c3e50;
  color: white;
  border: none;
  transition: background 0.3s ease;
}

.btn-primary:hover {
  background: linear-gradient(to right, #302d29, #906d2a);
}

.search-bar {
  margin-bottom: 15px;
}

.people-section {
  margin-top: 15px;
}

.people-section h3 {
  font-size: 18px;
  margin-bottom: 8px;
}

.people-list .bg-white {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
}

.grid-cols-7 {
  grid-template-columns: min-content 2fr 1fr 1fr 1fr 1fr 2fr min-content;
}

.gap-4 {
  gap: 0.75rem;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #FFFFFF;
  font-weight: bold;
  font-size: 14px;
  background-color: #FF0000;
  text-transform: uppercase;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.text-lg {
  font-size: 16px;
}

.text-base {
  font-size: 14px;
}

.text-gray-900 {
  color: #1f2937;
}

.text-gray-600 {
  color: #4b5563;
  font-family: 'Times New Roman', serif;
  font-size: 14px;
}

.text-gray-500 {
  color: #6b7280;
  font-family: 'Times New Roman', serif;
  font-size: 14px;
}

.text-gray-700 {
  color: #374151;
}

.h-2 {
  height: 6px;
}

.w-64 {
  width: 14rem;
}

.w-full {
  min-width: 0;
}

.transition-all {
  transition: width 0.3s ease-in-out;
}

.shadow-sm {
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.delete-btn {
  padding: 6px 12px; /* Giống padding của .btn */
  border: none;
  border-radius: 4px; /* Giống border-radius của .btn */
  background-color: #2c3e50; /* Giống background-color của .btn-primary */
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.3s ease; /* Giống transition của .btn-primary */
}

.delete-btn:hover {
  background: linear-gradient(to right, #302d29, #906d2a); /* Giống hover của .btn-primary */
}

/* Đảm bảo cột "Mã" và các cột khác thẳng hàng */
div.grid-cols-7 > div:nth-child(3) {
  display: flex;
  align-items: center;
  height: 100%;
}

/* Tùy chỉnh thêm cho giao diện chuyên nghiệp */
.people-list .bg-gray-100 {
  background-color: #f7f7f7;
  border-bottom: 1px solid #e5e7eb;
}

.people-list .border-b {
  border-color: #e5e7eb;
}
</style>