<template>
    <a-modal :zIndex="10001"  v-model:open="visible" title="Cập nhật ảnh" :footer="null" @cancel="closeModal" width="300px">
      <label class="font-semibold">Tên ảnh:</label>
      <a-input v-model:value="imageData.nameImage" class="mt-1" />
  
      <div class="mt-4">
        <a-button type="primary" block @click="updateImage">Cập nhật</a-button>
      </div>
    </a-modal>
  </template>
  
  <script setup>
  import { webSocketImage } from "@/services/service/member/image.socket";
import { ref, defineProps, defineEmits, watch } from "vue";
import { toast } from "vue3-toastify";
  
  const props = defineProps(["image", "open"]);
  const emit = defineEmits(["update", "close"]);
  
  const visible = ref(false);
  const imageData = ref({ name: "" });
  
  watch(
    () => props.open,
    (newOpen) => {
      visible.value = newOpen;
    }
  );
  
  watch(
    () => props.image,
    (newImage) => {
      if (newImage) {
        imageData.value = { ...newImage };
      }
    },
    { immediate: true } 
  );
  
  const closeModal = () => {
    visible.value = false;
    emit("close");
  };
  
 const updateImage = () => {
  const trimmedName = imageData.value.nameImage?.trim() || "";

 
if (!trimmedName) {
  toast.warning("Tên ảnh không được để trống!");
  return;
}

if (trimmedName.length > 100) {
  toast.warning("Tên ảnh không được vượt quá 100 ký tự!");
  return;
}

  const payload = {
    id: imageData.value.id,
    nameImage: trimmedName,
  };

  webSocketImage.sendUpdateNameImage(payload);
  closeModal();
};

  </script>
  