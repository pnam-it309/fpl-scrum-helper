<template>
    <a-modal :zIndex="10000" :open="modelValue" title="Tải ảnh lên" centered @cancel="closeModal" :footer="null" width="350px">
        <div class="flex flex-col gap-3">
            <!-- Chọn ảnh -->
            <div>
                <a-upload :before-upload="beforeUpload" :show-upload-list="false">
                    <a-button class="flex items-center gap-2">
                        <UploadOutlined />
                        Chọn ảnh
                    </a-button>
                </a-upload>
                <p v-if="errors.image" class="text-red-500 text-xs mt-1">{{ errors.image }}</p>
            </div>

            <!-- Hiển thị ảnh đã chọn -->
            <div v-if="previewUrl" class="flex justify-center">
                <a-image :src="previewUrl" alt="Ảnh đã chọn" class="rounded-md border shadow-md" :width="150"
                    :preview="true" />
            </div>

            <!-- Nhập tên ảnh -->
            <div>
                <label for="imageName" class="text-sm font-semibold">Tên ảnh:</label>
                <a-input id="imageName" v-model:value="imageName" placeholder="Nhập tên ảnh" />
                <p v-if="errors.name" class="text-red-500 text-xs mt-1">{{ errors.name }}</p>
            </div>

            <!-- Nút tải ảnh lên + Spinner -->
            <a-button type="primary" block @click="uploadImage" :disabled="isUploading">
                <template v-if="isUploading">
                    <a-spin /> Đang tải lên...
                </template>
                <template v-else>
                    Tải ảnh lên
                </template>
            </a-button>
        </div>
    </a-modal>
</template>

<script setup>
import { ref, defineProps, defineEmits } from "vue";
import { UploadOutlined } from "@ant-design/icons-vue";
import { uploadFile } from "@/services/api/member/projectdetail/image.api";
import { webSocketImage } from "@/services/service/member/image.socket";
import { localStorageAction } from "@/utils/storage";
import { TODO_ID_STORAGE_KEY } from "@/constants/key";
import { USER_INFO_STORAGE_KEY } from "@/constants/storagekey";
import { useRoute, useRouter } from "vue-router";
import { getUrlActivity } from "../../../../../utils/urlActivityHelper";
const route = useRoute();
const userLogin = localStorageAction.get(USER_INFO_STORAGE_KEY);
const props = defineProps(["modelValue"]);
const emit = defineEmits(["update:modelValue", "upload"]);

const imageName = ref("");
const selectedFile = ref(null);
const previewUrl = ref("");
const errors = ref({ name: "", image: "" });
const isUploading = ref(false); // Trạng thái loading

const allowedExtensions = ["png", "jpg", "jpeg", "gif", "webp"];

const beforeUpload = (file) => {
    const fileExtension = file.name.split(".").pop().toLowerCase();

    if (!allowedExtensions.includes(fileExtension)) {
        errors.value.image = "Vui lòng chọn tệp hình ảnh hợp lệ (png, jpg, jpeg, gif, webp)";
        return false;
    }

    selectedFile.value = file;
    previewUrl.value = URL.createObjectURL(file);
    errors.value.image = "";
    return false;
};

const uploadImage = async () => {
  errors.value = { name: "", image: "" };

  const trimmedName = imageName.value.trim();

  // Kiểm tra tên rỗng
  if (!trimmedName) {
    errors.value.name = "Tên ảnh không được để trống";
  }

  //  Kiểm tra độ dài vượt quá 100 ký tự
  if (trimmedName.length > 100) {
    errors.value.name = "Tên ảnh không được vượt quá 100 ký tự";
  }

  // Kiểm tra ảnh chưa chọn
  if (!selectedFile.value) {
    errors.value.image = "Vui lòng chọn ảnh";
  }

  // Nếu có lỗi thì dừng lại
  if (errors.value.name || errors.value.image) {
    return;
  }

  isUploading.value = true;

  try {
    const urlImage = await uploadFile(selectedFile.value);
    const payload = {
      urlImage: urlImage.data,
      nameFileOld: trimmedName,
      idTodo: localStorageAction.get(TODO_ID_STORAGE_KEY),
      projectId: route.params.id,
      idUser: userLogin.userId,
      urlPath: getUrlActivity(route),
    };

    webSocketImage.sendCreateImage(payload);
    closeModal();
  } catch (error) {
    console.error("Lỗi khi tải ảnh:", error);
    errors.value.image = "Có lỗi xảy ra khi tải ảnh. Vui lòng thử lại.";
  } finally {
    isUploading.value = false;
  }
};

const closeModal = () => {
    emit("update:modelValue", false);
    imageName.value = "";
    selectedFile.value = null;
    previewUrl.value = "";
    errors.value = { name: "", image: "" };
    isUploading.value = false; 
};
</script>