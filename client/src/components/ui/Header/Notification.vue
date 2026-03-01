<template>
  <div class="relative">
    <a-badge :count="unreadNotifications.length" :offset="[1, 8]">
      <a-dropdown
        placement="bottomRight"
        :trigger="['click']"
        :visible="showNotifications"
        @visibleChange="toggleNotifications"
      >
        <template #overlay>
          <div class="w-[340px] bg-white rounded-xl shadow-2xl border border-gray-200">
            <!-- Header -->
            <div class="px-4 py-3 border-b flex items-center justify-between bg-gray-50 rounded-t-xl">
              <span class="font-semibold text-gray-800 text-base flex items-center gap-1">
                🔔 Thông báo
              </span>
              <div class="flex items-center space-x-2 text-xs">
                <span class="text-gray-600">Chưa đọc</span>
                <a-switch v-model:checked="showOnlyUnread" @change="filterNotifications" />
              </div>
            </div>

            <!-- Danh sách thông báo, chỉ scroll dọc, không scroll ngang -->
            <div class="divide-y divide-gray-100 p-2 max-h-[350px] overflow-y-auto overflow-x-hidden">
              <template v-if="filteredNotifications.length > 0">
                <div
                  v-for="item in filteredNotifications"
                  :key="item.id"
                  @click="handleNotificationClick(item)"
                  class="p-3 rounded-lg cursor-pointer transition hover:bg-gray-100 mb-2"
                  :class="{ 'bg-blue-50': item.status === 0 }"
                >
                  <div class="flex gap-3 items-start">
                    <!-- Dot trạng thái -->
                    <div>
                      <span
                        v-if="item.status === 0"
                        class="inline-block w-2.5 h-2.5 bg-blue-500 rounded-full mt-1"
                      ></span>
                      <span
                        v-else
                        class="inline-block w-2.5 h-2.5 bg-gray-400 rounded-full mt-1"
                      ></span>
                    </div>
                    <!-- Nội dung thông báo -->
                    <div class="flex-1 min-w-0 ">
                  <p class="text-sm font-medium text-gray-800 leading-snug break-words" v-html="item.content"></p>

                      <p class="text-xs text-gray-500 mt-1">
                        {{ getDateFormat(item.createdDate, true) }}
                      </p>
                    </div>
                  </div>
                </div>
                <!-- Xem thêm -->
                <div v-if="hasMore" class="text-center mt-2">
                  <a-button type="link" size="small" @click="loadMoreNotifications">
                    Xem thêm ↓
                  </a-button>
                </div>
              </template>
              <div v-else class="text-center py-4">
                <p class="text-gray-500 text-sm">🎉 Không có thông báo nào</p>
              </div>
            </div>
          </div>
        </template>
        <a-button
          type="text"
          class="notification-button flex items-center justify-center"
          @click.prevent="toggleNotifications"
        >
         <span class="text-[23px] leading-none">🔔</span>
        </a-button>
      </a-dropdown>
    </a-badge>

    <!-- Modal chi tiết (nếu cần) -->
    <TodoDetailModal v-model="showTaskDetailModal" :todoId="selectedTodoId" @close="closeModal" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from "vue";
import { useRoute, useRouter } from "vue-router";
import { BellOutlined } from "@ant-design/icons-vue";
import { getAllNotificationMember, MBMeNotificationMemberResponse, updateStatusNotificationNumber } from "@/services/api/member/projectdetail/notification-member.api";
import { getDateFormat } from "@/utils/commom.helper";
import { localStorageAction } from "@/utils/storage";
import { USER_INFO_STORAGE_KEY } from "@/constants/storagekey";
import { webSocketNotificationMember } from "@/services/service/member/notification-member.socket";
import TodoDetailModal from "@/pages/member/project/projectdetails/projectmodal/TodoDetailModal.vue";
import { TODO_ID_STORAGE_KEY } from "@/constants/key";
import Role from "@/pages/admin/role/Role.vue";
import { ROLES } from "@/constants/roles";

// State và API
const userLogin = localStorageAction.get(USER_INFO_STORAGE_KEY);
const route = useRoute();
const router = useRouter();
const showNotifications = ref(false);
const showOnlyUnread = ref(false);
const notificationsMember = ref<MBMeNotificationMemberResponse[]>([]);
const page = ref(1);
const pageSize = ref(10); // Tùy chỉnh số lượng mỗi lần "Xem thêm"
const hasMore = ref(true);

const unreadNotifications = computed(() =>
  notificationsMember.value.filter(noti => noti.status === 0)
);
const filteredNotifications = computed(() =>
  showOnlyUnread.value ? unreadNotifications.value : notificationsMember.value
    .slice(0, page.value * pageSize.value)
);

const selectedTodoId = ref<string | null>(null);
const showTaskDetailModal = ref(false);

const fetchNotificationMember = async (reset = false) => {
  try {
    if (reset) {
      page.value = 1;
      notificationsMember.value = [];
      hasMore.value = true;
    }
    const res = await getAllNotificationMember({ page: 1, size: 1000, idMember: userLogin.userId });
    notificationsMember.value = res.data.data || [];
    // Giả lập phân trang frontend
    hasMore.value = notificationsMember.value.length > page.value * pageSize.value;
  } catch (err) {
    console.error("Lỗi khi lấy thông báo:", err);
  }
};

webSocketNotificationMember.getCreateNotificationMember((data) => {
  if (data == true) {
    fetchNotificationMember(true);
  }
});

const handleNotificationClick = async (notification: MBMeNotificationMemberResponse) => {
  try {
    // Nếu thông báo chưa được đọc, cập nhật trạng thái
    if (notification.status === 0) {
      await updateStatusNotificationNumber(notification.id);
      fetchNotificationMember(true);
    }
    const url = notification.url || "";
    // Nếu url bắt đầu bằng '/project-detail' thì thêm prefix
    if (url.startsWith("/project-detail")) {
      if (userLogin.roleScreen == ROLES.ADMIN || userLogin.roleScreen == ROLES.MANAGE) {
        await router.push("/manage" + url);
      } else if (userLogin.roleScreen == ROLES.MEMBER) {
        await router.push("/member" + url);
      }
    } else {
      // Ngược lại, dùng nguyên url từ backend trả về
      await router.push(url);
    }
    await nextTick();
  } catch (error) {
    console.error("Lỗi khi xử lý thông báo:", error);
  }
};

const loadMoreNotifications = () => {
  page.value += 1;
  hasMore.value = notificationsMember.value.length > page.value * pageSize.value;
};

const filterNotifications = () => {
  page.value = 1;
  hasMore.value = notificationsMember.value.length > page.value * pageSize.value;
};

// Đóng modal và xóa `todoId` khỏi URL
const closeModal = () => {
  showTaskDetailModal.value = false;
  selectedTodoId.value = null;
  const query = { ...route.query };
  delete query.todoId;
  router.replace({ query });
  localStorageAction.remove(TODO_ID_STORAGE_KEY)
};

const toggleNotifications = (visible?: boolean) => {
  showNotifications.value = visible !== undefined ? visible : !showNotifications.value;
};

onMounted(() => fetchNotificationMember(true));
webSocketNotificationMember.getNotificationMember(() => fetchNotificationMember(true));
</script>

<style scoped>
.notification-button {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.3s;
}
.notification-button:hover {
  background-color: #e0e0e0;
}
.notification-icon {
  font-size: 20px;
  color: #4a4a4a;
}
</style>
