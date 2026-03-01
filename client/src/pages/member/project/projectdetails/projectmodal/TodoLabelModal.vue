<template>
  <a-modal :zIndex="10000" v-model:visible="modalVisible" title="Danh sách nhãn" :footer="null" @cancel="closeModal"
    centered width="400px">
    <!-- Ô TÌM KIẾM -->
    <a-input v-model:value="searchQuery" placeholder="Tìm kiếm theo tên nhãn" class="mb-3 text-sm" />

    <!-- DANH SÁCH NHÃN -->
    <div v-for="label in filteredLabels" :key="label.id" class="flex items-center justify-between mb-2">
      <div class="flex items-center flex-1">
        <a-checkbox v-model:checked="selectedLabelsMap[label.id]" class="mr-2"
          @change="toggleLabel(label.id, $event.target.checked)" />
        <div class="rounded-md px-2 py-1 text-sm font-semibold text-white w-full cursor-pointer"
          :style="{ backgroundColor: label.colorLabel }" @click="toggleLabel(label.id, !selectedLabelsMap[label.id])">
          {{ label.name }}
        </div>
      </div>
      <div class="flex space-x-1 ml-2">
        <a-button type="link" size="small" class="px-1" @click="openEditModal(label)">
          <EditOutlined />
        </a-button>
        <a-popconfirm :zIndex="10000" title="Bạn có chắc muốn xóa nhãn này không?" ok-text="Xóa" cancel-text="Hủy"
          @confirm="deleteLabel(label.id)">
          <a-button type="link" size="small" class="px-1">
            <DeleteOutlined />
          </a-button>
        </a-popconfirm>
      </div>
    </div>

    <a-button block type="primary" @click="openAddModal" class="mt-3">
      + Thêm nhãn
    </a-button>

    <!-- Modal Thêm/Sửa nhãn -->
    <a-modal :zIndex="10001" v-model:visible="editModalVisible" :title="isEditing ? 'Chỉnh sửa nhãn' : 'Thêm nhãn'"
      :footer="null" @cancel="closeModalChild" centered>
      <!--  Xem trước nhãn -->
      <div class="mb-3">
        <label class="block text-sm font-semibold mb-1">Xem trước:</label>
        <div class="rounded-md px-2 py-1 text-sm font-semibold text-white w-full"
          :style="{ backgroundColor: editLabelData.colorLabel || '#ccc' }">
          {{ editLabelData.name || 'Tên nhãn sẽ hiển thị ở đây' }}
        </div>
      </div>

      <!-- Tiêu đề nhãn -->
      <div class="mb-3">
        <label class="block text-sm font-semibold mb-1">Tiêu đề:</label>
        <a-input v-model:value="editLabelData.name" placeholder="Nhập tiêu đề của nhãn" @input="clearNameError" />
        <div v-if="nameError" class="text-red-500 text-sm mt-1">Tiêu đề không được để trống.</div>
        <div v-if="nameTooLongError" class="text-red-500 text-sm mt-1">Tiêu đề không được vượt quá 50 ký tự.</div>
        <div v-if="nameDuplicateError" class="text-red-500 text-sm mt-1">
          Tiêu đề nhãn đã tồn tại.
        </div>

      </div>
<!-- Tiêu đề nhãn -->
<!-- <div class="mb-3">
  <label class="block text-sm font-semibold mb-1">Tiêu đề:</label>
  <a-input
    v-model:value="editLabelData.name"
    placeholder="Nhập tiêu đề của nhãn"
    :status="nameError ? 'error' : ''"
    @input="clearNameError"
  />
  <div v-if="nameError" class="text-red-500 text-sm mt-1">
    {{ nameError }}
  </div>
</div> -->


      <!-- Chọn màu -->
      <div class="mb-3">
        <label class="block text-sm font-semibold mb-1">Chọn màu:</label>
        <div class="grid grid-cols-5 gap-2">
          <div v-for="color in colorOptions" :key="color" @click="selectColor(color)"
            class="w-10 h-10 rounded cursor-pointer border-2 transition-all duration-150"
            :class="{ 'border-black scale-105': editLabelData.colorLabel === color }"
            :style="{ backgroundColor: color }"></div>
        </div>
        <div v-if="colorError" class="text-red-500 text-sm mt-1">Màu sắc không được để trống.</div>
      </div>

      <!-- Nút hành động -->
      <div class="flex justify-end space-x-2 mt-4">
        <a-button @click="closeModalChild">Hủy</a-button>
        <a-button type="primary" @click="saveLabel">Lưu</a-button>
      </div>
    </a-modal>
  </a-modal>
</template>

<script setup lang="ts">
import { TODO_ID_STORAGE_KEY } from '@/constants/key';
import { getAllLabelByIdProject, MBMeLabelResponse } from '@/services/api/member/projectdetail/melabelproject.api';
import { webSocketLabelProject } from '@/services/service/member/melabelproject.socket';
import { webSocketLabelProjectTodo } from '@/services/service/member/melabeltodo.socket';
import { localStorageAction } from '@/utils/storage';
import { DeleteOutlined, EditOutlined } from '@ant-design/icons-vue';
import { defineProps, defineEmits, ref, computed, watch } from 'vue';
import { useRoute } from 'vue-router'
const route = useRoute()

const isEditing = computed(() => !!editLabelData.value.id);

const props = defineProps({
  visible: Boolean,
});
const emit = defineEmits(['close', 'edit', 'delete', 'add']);

// Modal chính
const modalVisible = ref(props.visible);

watch(() => props.visible, (val) => {
  if (val) {
    modalVisible.value = true;
    fetchLabelProject();
  }
});

watch(modalVisible, (val) => {
  if (!val) {
    emit('close');
  }
});

// Dữ liệu LabelProject
const labelProjectList = ref<MBMeLabelResponse[]>([]);

// Tìm kiếm
const searchQuery = ref('');

const filteredLabels = computed(() => {
  const query = (searchQuery.value || '').toLowerCase();
  return labelProjectList.value.filter(label =>
    (label.name || '').toLowerCase().includes(query)
  );
});

// Checkbox
const selectedLabelsMap = ref({});

function toggleLabel(id: string, isChecked: boolean) {
  selectedLabelsMap.value[id] = isChecked;

  const payload = {
    idLabel: id,
    idTodo: localStorageAction.get(TODO_ID_STORAGE_KEY),
    projectId: projectId.value,
    idTodoJoinOrOut: id,
  };

  if (isChecked) {
    webSocketLabelProjectTodo.sendjoinLabeProjectTodo(payload)
  } else {
    webSocketLabelProjectTodo.sendoutLabeProjectTodo(payload)
  }
}

// Modal thêm/sửa nhãn
const editModalVisible = ref(false);
const projectId = ref(route.params.id)
const editLabelData = ref({ id: null, name: '', colorLabel: '#f87171' });
const colorOptions = [
  '#f87171', '#fb923c', '#facc15', '#4ade80', '#60a5fa',
  '#a78bfa', '#f472b6', '#34d399', '#e879f9', '#c084fc',
  '#fcd34d', '#38bdf8', '#10b981', '#6366f1', '#ec4899',
  '#ef4444', '#14b8a6', '#8b5cf6', '#f97316', '#84cc16',
  '#22d3ee', '#f43f5e', '#0ea5e9', '#fbbf24', '#3b82f6',
  '#6ee7b7', '#fde68a', '#fca5a5', '#cbd5e1', '#a3e635'
];

// Lỗi validation
const nameError = ref(false);
const colorError = ref(false);

// Mở modal
function openAddModal() {
  editLabelData.value = { id: null, name: '', colorLabel: '' };
  editModalVisible.value = true;
}

function openEditModal(label) {
  editLabelData.value = { ...label };
  editModalVisible.value = true;
}

// Chọn màu
function selectColor(color) {
  editLabelData.value.colorLabel = color;
  colorError.value = false;
}

// Xóa lỗi tiêu đề
function clearNameError() {
  nameError.value = false;
  nameTooLongError.value = false;
  nameDuplicateError.value = false;
}


const nameTooLongError = ref(false);
const nameDuplicateError = ref(false);

// Lưu nhãn
function saveLabel() {
  // Kiểm tra lỗi rỗng và độ dài
  const trimmedName = editLabelData.value.name.trim();
  nameError.value = trimmedName === '';
  nameTooLongError.value = trimmedName.length > 50;
// function validateName() {
//   const name = editLabelData.value.name;
//   if (!name || name.trim() === '') {
//     nameError.value = !name ? 'Tiêu đề không được để trống.' : 'Tiêu đề không được chỉ chứa khoảng trắng.';
//     return false;
//   }

  
//   if (name.trim().length > 50) {
//     nameError.value = 'Tiêu đề không được vượt quá 50 ký tự.';
//     return false;
//   }
//   return true;

// }

// // Lưu nhãn
// function saveLabel() {

//     const isValidName = validateName();
//   if (!isValidName) return;

//   if (!editLabelData.value.colorLabel) {
//     colorError.value = true;
//     return;
//   }

//   // Kiểm tra lỗi
//   nameError.value = !editLabelData.value.name;
//   colorError.value = !editLabelData.value.colorLabel;

  // Kiểm tra trùng tên (không phân biệt hoa thường)
  nameDuplicateError.value = labelProjectList.value.some(label =>
    label.name.trim().toLowerCase() === trimmedName.toLowerCase() &&
    label.id !== editLabelData.value.id // bỏ qua chính nó khi edit
  );


  colorError.value = !editLabelData.value.colorLabel;
  if (nameError.value || nameTooLongError.value || nameDuplicateError.value || colorError.value) {
    return;
  }

  if (editLabelData.value.id) {
    const payload = {
      name: editLabelData.value.name,
      color: editLabelData.value.colorLabel,
      id: editLabelData.value.id
    };
    webSocketLabelProject.sendUpdateLabelProject(payload);
    emit('edit', editLabelData.value);
  } else {
    const payload = {
      name: editLabelData.value.name,
      color: editLabelData.value.colorLabel,
      projectId: projectId.value
    };
    webSocketLabelProject.sendCreateLabelProject(payload);
    emit('add', editLabelData.value);
  }
  editModalVisible.value = false;
}


// Xóa nhãn
function deleteLabel(idLabelProject) {
  const payload = {
    idLabelProject: idLabelProject,
    projectId: projectId.value
  };
  webSocketLabelProject.sendDeleteLabelProject(payload);
  emit('delete', idLabelProject);
}

// Đóng modal
function closeModal() {
  modalVisible.value = false;
  emit('close');
}

function closeModalChild() {
  nameError.value = false;
  colorError.value = false;
  editModalVisible.value = false
}

const fetchLabelProject = async () => {
  try {
    const param = {
      idProject: route.params.id,
      idTodo: localStorageAction.get(TODO_ID_STORAGE_KEY)
    };
    const response = await getAllLabelByIdProject(param);
    labelProjectList.value = response.data;

    selectedLabelsMap.value = {}; // reset trước
    response.data.forEach((label: MBMeLabelResponse) => {
      selectedLabelsMap.value[label.id] = label.isChecked === true;
    });

  } catch (error) {
    console.error("Lỗi khi lấy danh sách label todo:", error);
  }
};

webSocketLabelProject.getCreateLabelProject(() => {
  fetchLabelProject()
});
webSocketLabelProject.getUpdateLabelProject(() => {
  fetchLabelProject()
});
webSocketLabelProject.getDeleteLabelProject(() => {
  fetchLabelProject()
});
webSocketLabelProjectTodo.getjoinLabeProjectTodo(fetchLabelProject)
webSocketLabelProjectTodo.getoutLabeProjectTodo(fetchLabelProject)
</script>