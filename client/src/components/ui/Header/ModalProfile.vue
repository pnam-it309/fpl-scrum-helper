<script setup lang="ts">
import { UserOutlined } from '@ant-design/icons-vue';
import { defineProps, defineEmits } from 'vue';
import { Modal, Avatar, Button } from 'ant-design-vue';

const props = defineProps({
  visible: Boolean,
  userInfor: Object
});

const emit = defineEmits(['update:visible']);

const closeModal = () => {
  emit('update:visible', false);
};
</script>

<template>
  <Modal
    v-model:visible="props.visible"
    title="Thông tin cá nhân"
    centered
    :closable="true"
    :maskClosable="true"
    @cancel="closeModal"
  >
    <div class="flex flex-col items-center text-center space-y-2">
      <Avatar :src="props.userInfor?.pictureUrl" :size="90" class="shadow-md border" />
      <h2 class="text-xl font-semibold mt-2">{{ props.userInfor?.fullName || 'Không có tên' }}</h2>
      <p class="text-gray-500 text-sm">{{ props.userInfor?.email || 'Không có email' }}</p>
      <div class="flex items-center justify-center mt-1 text-sm text-gray-700">
        <UserOutlined class="mr-1 text-base" />
        <span><strong>Vai trò:</strong> {{ props.userInfor?.roleScreen || 'Không rõ' }}</span>
      </div>
    </div>

    <template #footer>
      <div class="text-center">
        <Button type="primary" @click="closeModal" class="px-6">Đóng</Button>
      </div>
    </template>
  </Modal>
</template>
