<template>
  <div class="w-64 bg-white rounded-3xl shadow-lg p-4 flex flex-col flex-shrink-0" ref="columnContainer">
    <div class="flex items-center justify-between mb-3">
      <div class="flex-1">
        <template v-if="isEditingTitle">
          <input ref="titleInputRef" v-model="editableTitle" @blur="updateTitleColumn"
            @keydown.enter="updateTitleColumn"
            class="text-lg font-semibold text-gray-700 border border-gray-300 rounded px-2 py-1 w-full" />
        </template>
        <template v-else>
          <h2 class="text-lg font-semibold text-gray-700 cursor-pointer" @click="startEditTitle">
            {{ props.column.name }}
          </h2>
        </template>
      </div>
      
      <ColumnOptionsDropdown :column="column" @delete="handleDelete" />
    </div>

    <div class="flex-1 overflow-y-auto pr-1 scrollbar-thin scrollbar-thumb-gray-300 scrollbar-track-gray-100"
      :style="{ maxHeight: `calc(100vh - 250px)` }" ref="taskScrollContainer">
      <TodoCard :tasks="column.tasks" :todoListId="column.id" :todoListName="column.name" @taskMoved="handleTaskMoved"
        @taskUpdated="updateTaskList" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, onMounted, onBeforeUnmount } from "vue";
import TodoCard from "./TodoCard.vue";
import { MBMeBoardResponse, MBMeConvertTodoResponse } from "@/services/api/member/projectdetail/metodo.api";
import {
  MBMeUpdateIndexTodoRequest,
  webSocketTodo,
} from "@/services/service/member/metodo.socket";
import ColumnOptionsDropdown from "./ColumnOptionsDropdown.vue";
import { webSocketTodoList } from "@/services/service/member/metodolist.socket";
import { toast } from "vue3-toastify";
import { useRoute } from "vue-router";
import { localStorageAction } from "@/utils/storage";
import { USER_INFO_STORAGE_KEY } from "@/constants/storagekey";
const route = useRoute();

const props = defineProps<{ column: MBMeBoardResponse; allColumns: MBMeBoardResponse[] }>();
let oldColumnsBeforeDrag: MBMeBoardResponse[] = [];

const taskScrollContainer = ref<HTMLElement | null>(null);
const columnContainer = ref<HTMLElement | null>(null);
const boardContainer = ref<HTMLElement | null>(null);
const isDragging = ref(false);

const isEditingTitle = ref(false)
const editableTitle = ref(props.column.name)
const titleInputRef = ref<HTMLInputElement | null>(null)
const userLogin =localStorageAction.get(USER_INFO_STORAGE_KEY);
// Constants
const phaseId = route.params.idPhase;
const projectId = route.params.idProject;
const sessionId = "SESSION_ID";
const idUser = userLogin.userId

const handleDelete = (column: MBMeBoardResponse) => {
  if (column.tasks.length > 0) {
    toast.error('Không thể xóa khi có công việc');
    return;
  }
  webSocketTodoList.sendDeleteTodoList(route.params.id as string, {
    id: column.id,
    projectId: route.params.idProject as string,
    idPhase: route.params.idPhase
  });
}

const startEditTitle = () => {
  isEditingTitle.value = true
  editableTitle.value = props.column.name
  nextTick(() => {
    titleInputRef.value?.focus()
    titleInputRef.value?.select()
  })
}

const updateTitleColumn = () => {
  isEditingTitle.value = false;

  if (editableTitle.value.trim() && editableTitle.value !== props.column.name) {
    // Cập nhật local UI trước
    props.column.name = editableTitle.value;

    const payload = {
      idTodoList: props.column.id,
      name: editableTitle.value,
    };

    webSocketTodoList.sendUpdateNameTodoList(route.params.id as string, payload);
    
  }
};

const handleTaskMoved = async (payload: {
  idTodo: string;
  idTodoListOld: string;
  idTodoListNew: string;
  indexBefore: number;
  indexAfter: number;
  nameTodoListOld: string;
  nameTodoListNew: string;
}) => {
  const columnOld = oldColumnsBeforeDrag.find(col => col.id === payload.idTodoListOld);
  const columnNew = oldColumnsBeforeDrag.find(col => col.id === payload.idTodoListNew);
  const movingTask = columnOld?.tasks.find(task => task.id === payload.idTodo);
  const indexBefore = movingTask?.indexTodo ?? 0;
  let indexAfter = 0;

  if (columnNew) {
    if (payload.indexAfter === 0) {
      indexAfter = columnNew.tasks[0]?.indexTodo ?? 0;
    } else if (payload.indexAfter >= columnNew.tasks.length) {
      indexAfter = (columnNew.tasks[columnNew.tasks.length - 1]?.indexTodo ?? 0) + 1;
    } else {
      indexAfter = columnNew.tasks[payload.indexAfter]?.indexTodo ?? indexBefore;
    }
  }
  alert(indexBefore+"/"+indexAfter)
  const request: MBMeUpdateIndexTodoRequest = {
    ...payload,
    indexBefore,
    indexAfter,
    phaseId,
    projectId,
    sessionId,
    idUser,
    urlPath:route.fullPath,
  };

  webSocketTodo.sendUpdateIndexTodo(projectId, phaseId, request);
};

const updateTaskList = (newTasks: MBMeConvertTodoResponse[]) => {
  props.column.tasks.splice(0, props.column.tasks.length, ...newTasks);
};

const handleDragOver = (e: DragEvent) => {
  if (!isDragging.value) return;
  const verticalBuffer = 40;
  const horizontalBuffer = 40;
  const scrollSpeed = 2;

  const columnScrollEl = taskScrollContainer.value;
  if (columnScrollEl) {
    const rect = columnScrollEl.getBoundingClientRect();
    if (e.clientY < rect.top + verticalBuffer) columnScrollEl.scrollTop -= scrollSpeed;
    else if (e.clientY > rect.bottom - verticalBuffer) columnScrollEl.scrollTop += scrollSpeed;
  }

  const boardScrollEl = boardContainer.value;
  if (boardScrollEl) {
    const boardRect = boardScrollEl.getBoundingClientRect();
    if (e.clientX < boardRect.left + horizontalBuffer) boardScrollEl.scrollLeft -= scrollSpeed;
    else if (e.clientX > boardRect.right - horizontalBuffer) boardScrollEl.scrollLeft += scrollSpeed;
  }

};

const findScrollableParent = (el: HTMLElement | null): HTMLElement | null => {
  while (el) {
    const hasScroll = el.scrollWidth > el.clientWidth;
    if (hasScroll && getComputedStyle(el).overflowX.includes("auto")) return el;
    el = el.parentElement;
  }
  return null;
};

const onDragStart = () => {
  isDragging.value = true;
  oldColumnsBeforeDrag = JSON.parse(JSON.stringify(props.allColumns));
};

const onDragEnd = () => (isDragging.value = false);

onMounted(() => {
  boardContainer.value = findScrollableParent(columnContainer.value);
  console.log(boardContainer.value)
  document.addEventListener("dragstart", onDragStart);
  document.addEventListener("dragend", onDragEnd);
  document.addEventListener("dragover", handleDragOver);

});

onBeforeUnmount(() => {
  document.removeEventListener("dragstart", onDragStart);
  document.removeEventListener("dragend", onDragEnd);
  document.removeEventListener("dragover", handleDragOver);
});

</script>
