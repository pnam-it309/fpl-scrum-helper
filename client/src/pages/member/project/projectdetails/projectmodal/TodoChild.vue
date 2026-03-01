<template>
  <div class="mb-4">
    <h3 class="text-base font-semibold mb-2 flex items-center space-x-2">
      <FileDoneOutlined class="text-black text-xl" />
      <span>Những việc cần làm</span>
    </h3>

    <a-progress :percent="safeProgress" :strokeColor="getColorByProgress(safeProgress)" class="mb-3" />

    <!-- Danh sách công việc -->
    <div v-for="(item, index) in todoChild" :key="index"
      class="group flex justify-between items-center px-3 py-2 bg-white rounded-lg border-0 hover:bg-gray-100 transition">

      <!-- Checkbox + tên + trạng thái -->
      <div class="flex items-center space-x-2">
        <a-checkbox :checked="item.statusTodoChild === 1" @change="() => handleStatusChange(item)" />

        <div class="flex flex-col">
          <template v-if="editingId === item.id">
            <div class="flex space-x-2 items-center">
              <a-input v-model:value="editName" size="small" @keyup.enter="saveEdit(item)" />
              <a-button type="primary" size="small" @click="saveEdit(item)">Lưu</a-button>
              <a-button size="small" @click="cancelEdit">Hủy</a-button>
            </div>
          </template>
          <template v-else>
            <span :class="{
              'line-through text-gray-400': item.statusTodoChild === 1,
              'cursor-pointer hover:underline break-words whitespace-normal w-full overflow-hidden': true
            }" style="overflow-wrap: anywhere;" @click="startEdit(item)">
              {{ item.name }}
            </span>

          </template>
        </div>
      </div>

      <!-- Nút xóa -->
      <a-popconfirm :zIndex="10000" title="Bạn có chắc muốn xóa công việc này không?" ok-text="Xóa" cancel-text="Hủy"
        @confirm="() => removeTodoChild(item.id)">
        <a-button type="text" danger size="small"
          class="opacity-0 group-hover:opacity-100 transition-opacity duration-200">
          <DeleteOutlined />
        </a-button>
      </a-popconfirm>

    </div>

    <!-- Thêm việc mới -->
    <div class="mt-4">
      <div v-if="showAddInput" class="flex space-x-2">
        <a-input v-model:value="newTodoChildName" :maxlength="255" placeholder="Nhập tên công việc..."
          @keyup.enter="addTodoChild" ref="addInputRef" />
        <a-button type="primary" @click="addTodoChild">Lưu</a-button>
        <a-button @click="cancelAddInput">Hủy</a-button>

      </div>
      <div v-else>
        <a-button type="primary" @click="showAddInput = true" class="flex items-center space-x-1">
          <PlusOutlined />
          <span>Thêm</span>
        </a-button>
      </div>
    </div>

    <!-- Nút chọn phần trăm nếu không có todo -->
    <div v-if="!todoChild.length" class="flex justify-end mt-4">
      <template v-if="!showSelectProgress">
        <a-button type="dashed" @click="openManualProgress">
          Thiết lập tiến độ tạm thời
        </a-button>


      </template>

      <template v-else>
        <div class="flex items-center gap-2 w-full">

          <a-slider v-model:value="manualProgress" :min="0" :max="100" :step="1" :trackStyle="trackStyle"
            :handleStyle="handleStyle" :tooltipVisible="false"
            :railStyle="{ backgroundColor: '#e5e7eb', height: '6px' }" class="flex-1"
            style="padding: 4px 0; margin: 0;" />
          <a-input v-model:value="manualProgress" size="small"
            class="!w-16 !text-sm !rounded !border-gray-300 hover:!border-blue-400 transition text-center" suffix="%"
            :maxlength="3" @keypress="onNumberKeyPress" />

          <a-button size="small" type="default" shape="circle" @click="handleManualProgress" :style="{
            backgroundColor: getColorByProgress(manualProgress),
            borderColor: getColorByProgress(manualProgress),
            color: '#fff'
          }" class="h-7 w-7 flex items-center justify-center">
            <template #icon>
              <CheckOutlined />
            </template>
          </a-button>



        </div>
      </template>

    </div>


  </div>
</template>

<script setup lang="ts">
import { TODO_ID_STORAGE_KEY } from '@/constants/key';
import { StatusTodoChild } from '@/constants/statusTodoChild';
import { USER_INFO_STORAGE_KEY } from '@/constants/storagekey';
import { MBMeTodoChild_Response, ParamseditTodoChecklistTestRequest } from '@/services/api/member/projectdetail/metodo.api';
import { MBMeCreateTodoChildChecklistRequest, MBMeDeleteTodoChecklistRequest, webSocketTodoChild } from '@/services/service/member/todochild.socket';
import { getColorByProgress } from '@/utils/commom.helper';
import { localStorageAction } from '@/utils/storage';
import { getUrlActivity } from '@/utils/urlActivityHelper';
import { CheckOutlined, DeleteOutlined, EditOutlined, FileDoneOutlined, PlusOutlined, ProfileOutlined, SnippetsOutlined } from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';
import { ref, computed, nextTick, } from 'vue';
const onNumberKeyPress = (e) => {
  const charCode = e.which ? e.which : e.keyCode;
  if (charCode < 48 || charCode > 57) {
    e.preventDefault();
  }
};

const showSelectProgress = ref(false);
const manualProgress = ref(0);

const trackStyle = computed(() => [
  {
    backgroundColor: getColorByProgress(manualProgress.value),
    transition: 'all 0.3s ease'
  }
]);

// Style cho nút kéo
const handleStyle = computed(() => ({
  borderColor: getColorByProgress(manualProgress.value),
  backgroundColor: '#fff',
  borderWidth: '2px',
  boxShadow: `0 0 0 2px ${getColorByProgress(manualProgress.value)}33`,
  transition: 'all 0.3s ease'
}));

import { useRoute, useRouter } from 'vue-router'
import { toast } from 'vue3-toastify';

const route = useRoute()

const userLogin = localStorageAction.get(USER_INFO_STORAGE_KEY)

const handleManualProgress = () => {
  const value = manualProgress.value;
  console.log('Phần trăm được chọn:', value);
  const payload = {
    idTodo: localStorageAction.get(TODO_ID_STORAGE_KEY),
    projectId: route.params.id,
    idUser: userLogin.userId,
    progress: manualProgress.value,
    urlPath: getUrlActivity(route),
  }
  webSocketTodoChild.sendUpdateProgress(payload)
  showSelectProgress.value = false;
};



const props = defineProps<{
  todoChild: MBMeTodoChild_Response[];
  idTodo: string | null
  progress?: number | null;
}>();

const emit = defineEmits<{
  (e: 'status-change', item: MBMeTodoChild_Response): void;
  (e: 'add-todoChild', name: string): void;
  (e: 'remove-todoChild', id: string): void;
  (e: 'edit-todoChild', item: MBMeTodoChild_Response): void;
}>();

// Tính phần trăm công việc hoàn thành
// const progress = computed(() => {
//   if (!props.todoChild.length) return 0;
//   const done = props.todoChild.filter(item => item.statusTodoChild === 1).length;
//   return Math.round((done / props.todoChild.length) * 100);
// });

const openManualProgress = () => {
  manualProgress.value = safeProgress.value;  // Đồng bộ với progress hiện tại
  showSelectProgress.value = true;
};


const safeProgress = computed(() => {
  const value = props.progress;
  return typeof value === 'number' && isFinite(value) ? value : 0;
});

// Thay đổi trạng thái công việc
const handleStatusChange = (item: MBMeTodoChild_Response) => {
  // Đổi trạng thái local
  item.statusTodoChild = item.statusTodoChild === 1 ? 0 : 1;
  emit('status-change', item);

  // Dùng enum để gửi WebSocket
  const statusEnum = item.statusTodoChild === 1
    ? StatusTodoChild.COMPLETE
    : StatusTodoChild.UNCOMPLETE;

  webSocketTodoChild.sendUpdateStatusTodoChecklist({
    idTodoChild: item.id,
    statusTodoChild: statusEnum,
    idTodo: localStorageAction.get(TODO_ID_STORAGE_KEY),
    sessionId: sessionStorage.getItem('sessionId') || '',
  });

};

const cancelAddInput = () => {
  newTodoChildName.value = ''; // reset nội dung input
  showAddInput.value = false;  // ẩn input
};
// Thêm công việc mới
const newTodoChildName = ref('');
const showAddInput = ref(false);
const addInputRef = ref();

const addTodoChild = async () => {
  const trimmedName = newTodoChildName.value.trim();

  if (!trimmedName) {
    toast.error("Tên công việc không được để trống");
    return;
  }

  if (trimmedName.length > 255) {
    toast.error("Tên công việc không được vượt quá 255 ký tự");
    return;
  }

  const payload: MBMeCreateTodoChildChecklistRequest = {
    name: newTodoChildName.value.trim(),
    idTodo: props.idTodo,
  };
  // await createTodoChecklistAPI(payload)
  webSocketTodoChild.sendCreateTodoChildChecklist(payload);


  newTodoChildName.value = '';

  emit('add-todoChild', newTodoChildName.value);

  // Focus lại vào ô input
  nextTick(() => {
    addInputRef.value?.focus();
  });
};

// Xóa công việc
const removeTodoChild = async (idTodoChild: string) => {

  const payload: MBMeDeleteTodoChecklistRequest = {
    idTodoChild: idTodoChild,
    idTodo: localStorageAction.get(TODO_ID_STORAGE_KEY)
  };

  // await deleteTodoChecklistAPI(payload);
  webSocketTodoChild.sendDeleteTodoChecklist(payload);
  emit('remove-todoChild', idTodoChild);

};
const editingId = ref<string | null>(null); // id đang sửa
const editName = ref('');

// Bắt đầu sửa
const startEdit = (item: MBMeTodoChild_Response) => {
  editingId.value = item.id;
  editName.value = item.name;
};

// Hủy sửa
const cancelEdit = () => {
  editingId.value = null;
  editName.value = '';
};

// Lưu sửa
const saveEdit = async (item: MBMeTodoChild_Response) => {
  const trimmedName = editName.value.trim();


  if (!trimmedName) {
    toast.error("Tên công việc không được để trống");
    return;
  }

  if (trimmedName.length > 255) {
    toast.error("Tên công việc không được vượt quá 255 ký tự");
    return;
  }

  if (!trimmedName || trimmedName === item.name) {
    cancelEdit();
    return;
  }

  item.name = trimmedName;

  const payload: ParamseditTodoChecklistTestRequest = {
    name: trimmedName,
    idTodoChild: item.id,
  };
  webSocketTodoChild.sendEditTodoChecklist(payload);
  emit('edit-todoChild', item);
  cancelEdit();
};

</script>
