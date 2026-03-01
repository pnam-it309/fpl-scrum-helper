<template>
  <a-modal :zIndex="10000" v-model:open="visible" :title="modalTitle" @ok="handleOk" @cancel="handleCancel" :width="350">
    <!-- Đường gạch dưới tiêu đề -->
    <hr class="border-gray-300 my-2" />
    
    <div>
      <label class="font-semibold">Link đính kèm:</label>
      <a-input v-model:value="link" placeholder="Dán link đính kèm" />
      <span v-if="linkError" class="text-red-500">{{ linkError }}</span>
    </div>
    
    <div class="mt-3">
      <label class="font-semibold">Tên link đính kèm:</label>
      <a-input v-model:value="linkName" placeholder="Nhập tên link đính kèm" />
      <span v-if="linkNameError" class="text-red-500">{{ linkNameError }}</span>
    </div>
    
    <template #footer>
      <a-button @click="handleCancel">Hủy</a-button>
      <a-button type="primary" @click="handleOk">{{ isEditing ? "Cập nhật" : "Thêm link đính kèm" }}</a-button>
    </template>
  </a-modal>
</template>

<script setup>
import { TODO_ID_STORAGE_KEY } from "@/constants/key";
import { webSocketMeTodoResource } from "@/services/service/member/resource.socket";
import { localStorageAction } from "@/utils/storage";
import { ref, computed, watch, defineEmits, defineProps } from "vue";
import { toast } from "vue3-toastify";

const props = defineProps({
  open: Boolean,
  resource: Object, // Nhận toàn bộ object resource (có thể null nếu thêm mới)
});

const emits = defineEmits(["update:open", "addLink", "updateLink"]);

const visible = computed({
  get: () => props.open,
  set: (value) => emits("update:open", value),
});

import { useRoute, useRouter } from "vue-router";
import { USER_INFO_STORAGE_KEY } from "@/constants/storagekey";
import { getUrlActivity } from "@/utils/urlActivityHelper";
const route = useRoute();

// Biến lưu trữ dữ liệu trên form
const link = ref("");
const linkName = ref("");
const isEditing = ref(false); // Kiểm tra trạng thái sửa hoặc thêm mới
const linkError = ref("");
const linkNameError = ref("");

// Khi resource thay đổi, cập nhật dữ liệu trên form
watch(
  [() => props.open, () => props.resource],
  ([newOpen, newResource]) => {
    if (newOpen) {
      if (newResource) {
        // Trường hợp sửa
        link.value = newResource.url || "";
        linkName.value = newResource.name || "";
        isEditing.value = true;
      } else {
        // Trường hợp thêm mới
        link.value = "";
        linkName.value = "";
        linkError.value = "";
        linkNameError.value = "";
        isEditing.value = false;
      }
    }
  },
  { immediate: true }
);


const modalTitle = computed(() => (isEditing.value ? "Chỉnh sửa link đính kèm" : "Thêm link đính kèm"));

const validateForm = () => {
  let isValid = true;

  if (!link.value.trim()) {
    linkError.value = "Link đính kèm không được để trống";
    isValid = false;
  } else if (!isValidURL(link.value.trim())) {
    linkError.value = "Link đính kèm không đúng định dạng";
    isValid = false;
  } else {
    linkError.value = "";
  }

  if (!linkName.value.trim()) {
    linkNameError.value = "Tên link đính kèm không được để trống";
    isValid = false;
  } else {
    linkNameError.value = "";
  }

  return isValid;
};

const isValidURL = (url) => {
  const pattern = /^(https?:\/\/)(localhost|(\d{1,3}\.){3}\d{1,3}|([\w-]+\.)+[\w-]{2,})(:\d+)?(\/[\w\-._~:/?#[\]@!$&'()*+,;=]*)?$/i;
  return pattern.test(url.trim());
};

const userLogin = localStorageAction.get(USER_INFO_STORAGE_KEY);
const handleOk = () => {
  if (validateForm()) {
    if (isEditing.value && props.resource?.id) {
      const payload = {
        name: linkName.value,
        url: link.value,
        idTodo: localStorageAction.get(TODO_ID_STORAGE_KEY),
        id: props.resource.id,
        projectId:route.params.id,
        idUser:userLogin.userId,
        urlPath:getUrlActivity(route),
      };
      webSocketMeTodoResource.sendUpdateResourceTodo(payload);
      toast.success('Sửa thành công...!');
    } else {
      const payload = {
        name: linkName.value,
        url: link.value,
        idTodo: localStorageAction.get(TODO_ID_STORAGE_KEY),
        projectId:route.params.id,
        idUser:userLogin.userId,
        urlPath:getUrlActivity(route),
      };
      webSocketMeTodoResource.sendAddResourceTodo(payload);
      toast.success('Thêm thành công...!');
    }

    // Reset input và đóng modal
    visible.value = false;
  }
};

const handleCancel = () => {
  link.value = "";
  linkName.value = "";
  linkError.value = "";
  linkNameError.value = "";
  isEditing.value = false;
  visible.value = false;
};
</script>
