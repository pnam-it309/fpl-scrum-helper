<template>
  <a-modal v-model:visible="internalVisible" title="Thêm thành viên" @cancel="handleClose" :footer="null" width="400px"
    :zIndex="10000">
    <div>
      <!-- Tìm kiếm thành viên -->
      <a-input v-model:value="searchQuery" placeholder="Tìm kiếm thành viên..." class="mb-3" size="large" />

      <!-- Danh sách thành viên -->
      <div class="space-y-2">
        <div v-for="member in filteredMembers" :key="member.id"
          class="flex items-center p-2 bg-gray-50 rounded-lg cursor-pointer transition hover:bg-gray-100"
          :class="{ 'bg-blue-100': member.isAdded }" @click="toggleMember(member)">
          <!-- Avatar -->
          <a-avatar :src="member.image" class="w-8 h-8 rounded-full" />

          <!-- Tên + Email -->
          <div class="ml-2 text-sm">
            <p class="font-semibold">{{ member.name }}</p>
            <p class="text-xs text-gray-500">{{ member.email }}</p>
          </div>

          <!-- Check icon nếu isAdded -->
          <CheckIcon v-if="member.isAdded" class="ml-auto text-blue-500 text-lg" />
        </div>
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import { useRoute } from 'vue-router';
import { CheckCircleOutlined } from '@ant-design/icons-vue';
import {
  getAllMemberProject,
  MBMeMemberProjectResponse,
  ParamsjoinTodoVoteMemberProjectAPIRequest,
} from '@/services/api/member/projectdetail/memberproject.api';
import { localStorageAction } from '@/utils/storage';
import { MBMeCreateOrDeleteTodoVoteRequest, webSocketMemberProject } from '@/services/service/member/memberproject.socket';
import { TODO_ID_STORAGE_KEY } from '@/constants/key';
import { USER_INFO_STORAGE_KEY } from '@/constants/storagekey';
import { createV1NotificationTag, createV2NotificationTag, MBMeCreate1NotificationCommentRequest, MBMeCreatev2NotificationCommentRequest } from '@/services/api/member/projectdetail/notification.api';
import { webSocketNotificationMember } from '@/services/service/member/notification-member.socket';
import { getUrlActivity } from '@/utils/urlActivityHelper';

const CheckIcon = CheckCircleOutlined;

const props = defineProps<{
  modelValue: boolean;
}>();

const emit = defineEmits(['update:modelValue', 'close', 'memberAdded', 'memberRemoved']);

// Trạng thái modal
const internalVisible = ref(props.modelValue);
watch(() => props.modelValue, (val) => {
  internalVisible.value = val;
  if (val) {
    fetchAllModalMemberProject(); // load member khi mở modal
  }
});

watch(internalVisible, (val) => {
  emit('update:modelValue', val);
});

// Tìm kiếm
const searchQuery = ref('');
const members = ref<MBMeMemberProjectResponse[]>([]);

// Route
const route = useRoute();

// Lọc member theo search
const filteredMembers = computed(() =>
  members.value.filter((member) =>
    member.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    member.email.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
);

// Đóng modal
const handleClose = () => {
  internalVisible.value = false;
  emit('close');
};

// Lấy danh sách member
const fetchAllModalMemberProject = async () => {
  try {
    const res = await getAllMemberProject(route.params.id as string, localStorageAction.get(TODO_ID_STORAGE_KEY));
    members.value = res.data.map((m: MBMeMemberProjectResponse) => ({
      ...m,
      isAdded: m.isAdded ?? false,
    }));
  } catch (err) {
    console.error("Lỗi khi load member:", err);
  }
};

// Toggle member add/remove
const toggleMember = async (member: MBMeMemberProjectResponse) => {
  const todoId = localStorageAction.get(TODO_ID_STORAGE_KEY);
  const userLogin = localStorageAction.get(USER_INFO_STORAGE_KEY); // Người thao tác
  const memberName = member.name;
  const todoName = todoId ? `Công việc #${todoId}` : 'công việc này';

  const payload: MBMeCreateOrDeleteTodoVoteRequest = {
    idMember: member.id,                   // Người nhận (assignee)
  nameMember: member.name,               // Tên người nhận
  idTodo: todoId,                        // ID công việc
  projectId: route.params.id as string,  // ID dự án
  idUser: userLogin.userId,              // Người thao tác (assigner)
  nameAssigner: userLogin.fullName,      // Tên người thao tác
  urlPath: getUrlActivity(route)                // URL hiển thị khi click thông báo
  };

  if (member.isAdded) {
    //  Người rời công việc
    webSocketMemberProject.sendOutTodoVoteMemberProject(payload);
    // Nếu có API backend cho rời công việc, có thể gọi ở đây
    // await outTodoVoteMemberProjectAPI(payload);

    // Tạo thông báo cho người rời
    const content = member.id === userLogin.userId
      ? `
👤 <span class="text-gray-800 font-semibold">Bạn</span> 
đã xác nhận rời công việc. Có thể vào để xem chi tiết. 📝
`
      : `
👤 <span class="text-gray-800 font-semibold">${userLogin.fullName}</span> 
đã gỡ <span class="text-blue-600 font-semibold">${memberName}</span> 
khỏi công việc.
`;
    
    const notificationPayload: MBMeCreatev2NotificationCommentRequest = {
      idProject: route.params.id as string,
      idUser: userLogin.userId,      // Người thao tác
      username: userLogin.fullName,  // Tên người thao tác
      url: route.fullPath,
      content,
      idReceiver: member.id,         // Người nhận thông báo
    };

    await createV2NotificationTag(notificationPayload);
    webSocketNotificationMember.sendNotificationMember(notificationPayload);

    emit('memberRemoved', member);
  } else {
    // 🔹 Người được thêm vào công việc
    webSocketMemberProject.sendJoinTodoVote(payload);
    // await joinTodoVoteMemberProjectAPI(payload);

    // Nội dung thông báo khi được thêm
    const content = member.id === userLogin.userId
      ? `
👤 <span class="text-gray-800 font-semibold">Bạn</span> 
đã nhận một công việc. Hãy hoàn thành nó ngay nhé! 
`
      : `
👤 <span class="text-gray-800 font-semibold">${userLogin.fullName}</span> 
đã <b class="text-green-600">phân công</b> 
<span class="text-blue-600 font-semibold">${memberName}</span> 
một công việc. Hãy hoàn thành nó ngay nhé! 
`;

    const notificationPayload: MBMeCreatev2NotificationCommentRequest = {
      idProject: route.params.id as string,
      idUser: userLogin.userId,      // Người thao tác
      username: userLogin.fullName,  // Tên người thao tác
      url: route.fullPath,
      content,
      idReceiver: member.id,         // Người nhận thông báo
    };

    await createV2NotificationTag(notificationPayload);
    webSocketNotificationMember.sendNotificationMember(notificationPayload);

    emit('memberAdded', member);
  }
};



webSocketMemberProject.getJoinTodoVote(() => {
  fetchAllModalMemberProject()
})

webSocketMemberProject.getOutTodoVoteMemberProject(() => {
  fetchAllModalMemberProject()
})

</script>
