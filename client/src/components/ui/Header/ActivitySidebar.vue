<template>
  <div class="flex flex-col h-full w-full max-w-lg mx-auto bg-white">
    <!-- Nội dung hoạt động -->
    <div v-if="activeTab === 'activities'" class="p-2 sm:p-4 space-y-4 flex-1 min-h-[200px] max-h-[80vh]">
      <!-- Kiểm tra nếu không có hoạt động nào -->
      <div v-if="activities.length === 0" class="text-center text-gray-500">
        Chưa có hoạt động nào
      </div>

      <div v-else>
        <div v-for="(item, index) in activities" :key="index" class="flex gap-2 cursor-pointer mb-4"
          @click="openActivityDetail(item)">
          <img :src="item.memberImage || 'https://cdn.sforum.vn/sforum/wp-content/uploads/2023/10/avatar-trang-4.jpg'"
            alt="Avatar" class="w-6 h-6 sm:w-8 sm:h-8 rounded-full object-cover" />

          <div class="flex-1">
            <p class="text-xs sm:text-sm leading-snug">
              <span class="font-bold text-blue-600">{{ item.memberName }}</span>
              {{ item.contentActionProject }}
            </p>
            <p class="text-[10px] sm:text-xs text-gray-500">
              {{ getDateFormat(item.createdDate, true) }}
            </p>
            <div v-if="item.urlImage" class="mt-1 sm:mt-2">
              <img :src="item.urlImage" alt="Activity Image"
                class="max-w-[80px] sm:max-w-[120px] h-auto rounded-md shadow-sm object-cover" />
            </div>
          </div>
        </div>
      </div>

      <!-- Nút "Xem thêm" -->
      <div v-if="activities.length < totalRecords" class="text-center mt-4">
        <button @click="loadMoreActivities"
          class="w-64 py-1 text-gray-700 bg-gray-200 rounded-lg hover:bg-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-400 transition-all duration-300 ease-in-out">
          Tải thêm hoạt động
        </button>
      </div>
      <div class="h-2"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ROLES } from '@/constants/roles'
import { USER_INFO_STORAGE_KEY } from '@/constants/storagekey'
import { router } from '@/routes/router'
import {
  getAllActivityWhereIdProject,
  getCountTotalActivitiesWhereIdProject,
  MBMeActivityResponse
} from '@/services/api/member/projectdetail/activity.api'
import { webSocketActivity } from '@/services/service/member/activity.socket'
import { getDateFormat } from '@/utils/commom.helper'
import { localStorageAction } from '@/utils/storage'
import { LeftOutlined } from '@ant-design/icons-vue'
import { ref, onMounted, reactive } from 'vue'
import { useRoute } from 'vue-router'
const userLogin = localStorageAction.get(USER_INFO_STORAGE_KEY)
const route = useRoute()
const activeTab = ref('activities') // Mặc định là tab "Hoạt động"
const totalRecords = ref(0)
const pageable = reactive({
  page: 1,
  size: 10
})

// Danh sách riêng biệt
const activities = ref<MBMeActivityResponse[]>([])

// State for handling activity detail drawer
const selectedActivity = ref<MBMeActivityResponse | null>(null)

// Gọi API lấy hoạt động
const fetchProjectActivity = async () => {
  try {
    const param = {
      idProject: route.params.id,
      page: pageable.page,
      size: pageable.size
    }
    const res = await getAllActivityWhereIdProject(param)
    const rescount = await getCountTotalActivitiesWhereIdProject(param)
    activities.value = res.data.data
    totalRecords.value = rescount.data
    console.log('Hoạt động:', activities.value)
  } catch (err) {
    console.error('Lỗi khi lấy dữ liệu hoạt động:', err)
  }
}

// Hàm tải thêm hoạt động
const loadMoreActivities = () => {
  pageable.size += 10
  fetchProjectActivity()
}

const openActivityDetail = (activity: MBMeActivityResponse) => {
  selectedActivity.value = activity
  try {
    // Nếu thông báo chưa được đọc, cập nhật trạng thái

    if (userLogin.roleScreen == ROLES.ADMIN) {
      router.push('/manage' + activity.urlPath)
    } else if (userLogin.roleScreen == ROLES.MANAGE) {
      router.push('/manage' + activity.urlPath)
    } else if (userLogin.roleScreen == ROLES.MEMBER) {
      router.push('/member' + activity.urlPath)
    }
  } catch (error) {
    console.error('Lỗi khi xử lý thông báo:', error)
  }
}

// Gọi API hoạt động khi load trang
onMounted(() => {
  fetchProjectActivity()
})

// Lắng nghe sự kiện WebSocket chỉ cho hoạt động
webSocketActivity.getAllActivityWhereIdTodo(() => {
  fetchProjectActivity()
})

const emit = defineEmits(['closeAll'])

const closeAll = () => {
  emit('closeAll')
}

const closeActivityDetail = () => {
  isActivityDrawerVisible.value = false
}
</script>
<style scoped>
/* Loại bỏ scroll ở phần hoạt động */
div.p-2.sm\:p-4 {
  overflow-y: unset;
  /* Loại bỏ cuộn dọc */
}

.flex-1 {
  flex: unset;
  /* Loại bỏ sự co dãn của phần tử */
}
</style>
