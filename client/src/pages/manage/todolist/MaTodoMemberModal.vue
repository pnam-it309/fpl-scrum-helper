<template>
  <a-modal
    v-model:visible="internalVisible"
    title="Thêm thành viên"
    @cancel="handleClose"
    :footer="null"
    width="400px"
    :zIndex="2000"
  >
    <div>
      <!-- Tìm kiếm thành viên -->
      <a-input
        v-model:value="searchQuery"
        placeholder="Tìm kiếm thành viên..."
        class="mb-3"
        size="large"
      />

      <!-- Danh sách thành viên -->
      <div class="space-y-2">
        <div
          v-for="member in filteredMembers"
          :key="member.id"
          class="flex items-center p-2 bg-gray-50 rounded-lg cursor-pointer transition hover:bg-gray-100"
          :class="{ 'bg-blue-100': member.isAdded }"
          @click="toggleMember(member)"
        >
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

<style scoped>
:deep(.ant-modal-root) {
  z-index: 2000 !important;
}
</style>


<script setup lang="ts">
import {
  getAllMemberProject,
  MBMeMemberProjectResponse,
  ParamsjoinTodoVoteMemberProjectAPIRequest,
} from '@/services/api/member/projectdetail/memberproject.api';
import { webSocketMemberProject } from '@/services/service/member/memberproject.socket';
import { CheckCircleOutlined } from '@ant-design/icons-vue';
import { computed, ref, watch } from 'vue';
import { useRoute } from 'vue-router';

const CheckIcon = CheckCircleOutlined;



const props = defineProps<{
  modelValue: boolean;
  todoId: String | null,
}>();

watch(() => props.todoId, (newTodoId) => {
  console.log("ID của task:", newTodoId); // Kiểm tra ID nhận được
  // Bạn có thể gọi các API hoặc hành động khác tại đây để lấy thông tin về task nếu cần.
});


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

    console.log("todoId đang nhận: ", props.todoId);
    const res = await getAllMemberProject(route.params.id as string, props.todoId as string);
    members.value = res.data.map((m: MBMeMemberProjectResponse) => ({
      ...m,
      isAdded: m.isAdded ?? false,
    }));
    console.log("rouProject: ", route.params.idProject)
    console.log("rouTodo: ", props.todoId)

  } catch (err) {
    console.error("Lỗi khi load member:", err);
  }
};

// Toggle member add/remove
const toggleMember = async (member: MBMeMemberProjectResponse) => {
  const payload: ParamsjoinTodoVoteMemberProjectAPIRequest = {
    idMember: member.id,
    idTodo: props.todoId as string,
    projectId: route.params.id as string,
  };

  if (member.isAdded) {
    webSocketMemberProject.sendOutTodoVoteMemberProject(payload);
    // await outTodoVoteMemberProjectAPI(payload);
    emit('memberRemoved', member);
  } else {
    webSocketMemberProject.sendJoinTodoVote(payload);
    // await joinTodoVoteMemberProjectAPI(payload);
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
