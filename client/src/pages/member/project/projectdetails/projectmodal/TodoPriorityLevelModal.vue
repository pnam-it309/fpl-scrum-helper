<template>
    <a-modal
    :zIndex="10000"
      v-model:open="visible"
      title="Độ ưu tiên"
      :footer="null"
      :width="240"
      @cancel="handleCancel"

    >
      <div>
        <div
          v-for="(option, index) in priorityOptions"
          :key="index"
          class="flex items-center p-2 border rounded-lg cursor-pointer"
          :class="{
            'border-red-500 bg-red-50': selectedPriority === option.value && option.value === 0,
            'border-orange-500 bg-orange-50': selectedPriority === option.value && option.value === 1,
            'border-blue-500 bg-blue-50': selectedPriority === option.value && option.value === 2,
            'border-brown-500 bg-slate-400': selectedPriority === option.value && option.value === 3,
            'border-gray-300': selectedPriority !== option.value
          }"
          @click="selectPriority(option.value)"
        >
          <!-- Sử dụng icon từ Ant Design -->
          <component :is="option.icon" class="text-xl" />
          <span class="text-sm font-medium ml-2">{{ option.label }}</span>
          <span v-if="selectedPriority === option.value" class="ml-auto text-blue-500">✔</span>
        </div>
      </div>
    </a-modal>
  </template>
  
  <script setup lang="ts">
  import { TODO_ID_STORAGE_KEY } from '@/constants/key';
  import { webSocketTodo } from '@/services/service/member/metodo.socket';
  import { localStorageAction } from '@/utils/storage';
  import { ref, watch, defineProps, defineEmits } from 'vue';
  // Import các icon từ Ant Design
  import { AlertOutlined, FireOutlined, SafetyCertificateOutlined, ArrowDownOutlined, UpOutlined, DownOutlined } from '@ant-design/icons-vue';
  
  const props = defineProps<{
      modelValue: boolean;
      currentPriority?: number;
  }>();
  
  const emit = defineEmits(['update:modelValue', 'updatePriority']);
  
  const visible = ref(props.modelValue);
  const selectedPriority = ref<number | null>(props.currentPriority ?? null);
  
  watch(() => props.modelValue, (val) => {
      visible.value = val;
      if (val) {
          selectedPriority.value = props.currentPriority ?? null;
      }
  });
  
  watch(visible, (val) => emit('update:modelValue', val));
  
  const priorityOptions = [
      { value: 0, label: 'Quan trọng', icon: AlertOutlined },
      { value: 1, label: 'Cao', icon: UpOutlined },
      { value: 2, label: 'Trung bình', icon: SafetyCertificateOutlined },
      { value: 3, label: 'Thấp', icon: DownOutlined },
  ];
  
  const selectPriority = (priority: number) => {
      selectedPriority.value = priority;
  
      const payload = {
          idTodoChange: localStorageAction.get(TODO_ID_STORAGE_KEY),
          idTodo: localStorageAction.get(TODO_ID_STORAGE_KEY),
          priorityLevel: selectedPriority.value
      };
  
      webSocketTodo.sendUpdatePriorityLevel(payload);
  
      emit('updatePriority', priority);
      visible.value = false;
  };
  
  const handleCancel = () => {
      visible.value = false;
  };
  </script>
  