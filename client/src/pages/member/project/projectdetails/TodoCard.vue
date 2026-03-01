<template>
  <draggable :list="tasks" group="tasks" item-key="id" animation="200" ghost-class="ghost" chosen-class="chosen"
    drag-class="dragging" @end="handleDragEnd" @change="handleListChange" :data-list-id="props.todoListId"
    :data-list-name="props.todoListName" class="flex flex-col gap-2">
    <template #item="{ element }">
      <div
        class="task-card p-3 bg-white rounded-3xl shadow-md border border-gray-200 hover:border-blue-400 hover:shadow-lg transition-all duration-200 cursor-pointer space-y-2"
        @click="openTaskDetailModal(element.id)">
        
        <div v-if="element.urlImage"
          class="w-full h-24 rounded-xl bg-gray-100 p-0 m-0 flex items-center justify-center">
          <img :src="element.urlImage" alt="Ảnh tiêu đề" class="w-auto h-full object-contain">
        </div>

        <div v-if="element.labels && element.labels.length > 0" class="flex flex-wrap gap-1 mt-1">
          <span v-for="label in element.labels" :key="label.id" class="text-xs font-medium px-2.5 py-0.5 rounded-md"
            :style="{ backgroundColor: label.colorLabel, color: getTextColor(label.colorLabel.replace('#', '')) }">
            {{ label.name }}
          </span>
        </div>

        <!-- tên Todo -->
        <div class="text-sm font-semibold text-gray-800 truncate">{{ element.name }}</div>

        <!-- Mức ưu tiên -->
        <div class="flex flex-wrap items-center gap-2 text-xs text-gray-600">
          <a-tooltip title="Mức độ ưu tiên" v-if="element.priorityLevel">
            <div class="flex items-center gap-1 text-red-500 font-medium">
              <CrownOutlined />
              <span>{{ getPriorityText(element.priorityLevel) }}</span>
            </div>
          </a-tooltip>

          <!--bình luận  -->
          <a-tooltip title="Số bình luận" v-if="element.numberCommnets > 0">
            <div class="flex items-center gap-1 text-gray-500">
              <CommentOutlined />
              <span>{{ element.numberCommnets }}</span>
            </div>
          </a-tooltip>

          <!-- Số đính kèm -->
          <a-tooltip title="Số tệp đính kèm" v-if="element.numberAttachments > 0">
            <div class="flex items-center gap-1 text-gray-500">
              <PaperClipOutlined />
              <span>{{ element.numberAttachments }}</span>
            </div>
          </a-tooltip>

          <!-- Task Completion Progress -->
          <a-tooltip title="Công việc đã hoàn thành / Tổng công việc" v-if="element.numberTodo > 0">
            <div class="text-xs font-semibold px-2 py-0.5 rounded-full inline-block" :class="{
              'bg-green-100 text-green-700': element.numberTodo === element.numberTodoComplete,
              'bg-gray-100 text-gray-600': element.numberTodo !== element.numberTodoComplete
            }">
              <template v-if="element.numberTodo === element.numberTodoComplete">
                <CheckCircleOutlined style="color: green; margin-right: 4px;" />
                {{ element.numberTodoComplete }}/{{ element.numberTodo }}
              </template>
              <template v-else>
                <SyncOutlined style="color: orange; margin-right: 4px;" />
                {{ element.numberTodoComplete }}/{{ element.numberTodo }}
              </template>

            </div>
          </a-tooltip>

          <div v-if="element.progress !== null && element.progress !== undefined"
            class="px-2 py-0.5 bg-blue-100 text-blue-700 font-semibold rounded-full">
            {{ element.progress }}%
          </div>
        </div>

        <!-- Deadline -->
        <a-tooltip :title="getDeadlineTooltip(element.deadline)" v-if="element.deadline">
          <div :class="getDeadlineClass(element.deadline)" class="flex items-center gap-1 text-xs">
            <ClockCircleOutlined />
            <span>{{ formatDate(element.deadline) }}</span>
            <CheckCircleOutlined v-if="element.completionTime" class="text-green-500" />
            <CloseCircleOutlined v-else class="text-red-500" />
          </div>
        </a-tooltip>

        <!-- Thành viên dự án -->
        <div v-if="element.memberTodo && element.memberTodo.length > 0" class="flex -space-x-2 mt-2 flex-wrap">
          <a-tooltip v-for="(member, index) in element.memberTodo" :key="index" :title="`${member.email}`">
            <div
              class="w-6 h-6 rounded-full border-2 border-white shadow-sm overflow-hidden flex items-center justify-center bg-gray-100 text-xs font-semibold text-gray-500">
              <template v-if="member.image">
                <img :src="member.image" alt="avatar" class="w-full h-full object-cover" />
              </template>
              <template v-else>
                ?
              </template>
            </div>
          </a-tooltip>
        </div>

      </div>

    </template>

    <template #placeholder>
      <div class="drop-placeholder rounded-xl border-2 border-dashed border-sky-400 bg-sky-100 h-16 animate-pulse">
      </div>
    </template>
  </draggable>

  <TodoDetailModal v-model="showTaskDetailModal" :todoId="selectedTodoId" @close="closeModal" />


</template>

<script setup lang="ts">
import { CommentOutlined, ArrowUpOutlined, ClockCircleOutlined, CheckCircleOutlined, SyncOutlined, HistoryOutlined, FlagOutlined, BellOutlined, ThunderboltOutlined, PushpinOutlined, RocketOutlined, CrownOutlined, PaperClipOutlined } from "@ant-design/icons-vue";
import { defineProps, defineEmits, onMounted, watch } from "vue";
import draggable from "vuedraggable";
import { MBMeConvertTodoResponse } from "@/services/api/member/projectdetail/metodo.api";

const props = defineProps<{
  tasks: MBMeConvertTodoResponse[];
  todoListId: string;
  todoListName: string;
}>();

const emit = defineEmits<{
  (e: "taskMoved", payload: {
    idTodo: string;
    idTodoListOld: string;
    idTodoListNew: string;
    indexBefore: number;
    indexAfter: number;
    nameTodoListOld: string;
    nameTodoListNew: string;
  }): void;
  (e: "taskUpdated", updatedTasks: MBMeConvertTodoResponse[]): void;
}>();

import { ref } from "vue";
import TodoDetailModal from "./projectmodal/TodoDetailModal.vue";
import { getPriorityText } from "@/utils/commom.helper";
import { localStorageAction } from "@/utils/storage";
import { TODO_ID_STORAGE_KEY, TODO_LIST_ID_STORAGE_KEY } from "@/constants/key";
import { useRoute, useRouter } from "vue-router";
const route = useRoute();
const router = useRouter();
const selectedTodoId = ref<string | null>(null);
const showTaskDetailModal = ref(false);

const openTaskDetailModal = (taskId: string) => {

  selectedTodoId.value = taskId;
  showTaskDetailModal.value = true;

  // Cập nhật URL khi mở modal
  router.replace({
    query: { ...route.query, idTodo: taskId }
  });
};

const local = localStorageAction.get("local")

// watch(() => route.query.idTodo, (newId) => {

//   if (localStorageAction.get(TODO_ID_STORAGE_KEY) === null) {
//     if (newId) {
//       // alert(localStorageAction.get(TODO_ID_STORAGE_KEY))
//       selectedTodoId.value = newId;
//       showTaskDetailModal.value = true;
//     }
//   }

// }, { immediate: true }
// );

const closeModal = () => {
  showTaskDetailModal.value = false;
  selectedTodoId.value = null;

  // Xóa idTodo khỏi URL khi đóng modal
  const newQuery = { ...route.query };
  delete newQuery.idTodo;
  router.replace({ query: newQuery });
};

const currentTime = ref(Date.now());

const getDeadlineClass = (deadline: number) => {
  const now = currentTime.value;
  const today = new Date();
  const endOfToday = new Date(today.getFullYear(), today.getMonth(), today.getDate(), 23, 59, 59).getTime();

  if (deadline < now) {
    return 'text-red-500'; //  Quá hạn
  } else if (deadline <= endOfToday) {
    return 'text-yellow-500'; //  Đến hạn hôm nay
  } else {
    return 'text-green-500'; //  Còn hạn
  }
};

const getDeadlineTooltip = (deadline: number) => {
  const now = currentTime.value;
  const today = new Date();
  const endOfToday = new Date(today.getFullYear(), today.getMonth(), today.getDate(), 23, 59, 59).getTime();

  if (deadline < now) {
    return '❌Công việc Đã hết hạn';
  } else if (deadline <= endOfToday) {
    return '⚠️ Sắp hết hạn (trong hôm nay)';
  } else {
    return '✅Công việc Còn hạn';
  }
};

const handleDragEnd = (event: any) => {
  const { from, to, item, oldIndex, newIndex } = event;
  const task = item?.__draggable_context?.element;
  if (!task?.id) return;

  emit("taskMoved", {
    idTodo: task.id,
    idTodoListOld: from?.dataset?.listId,
    idTodoListNew: to?.dataset?.listId,
    indexBefore: oldIndex,
    indexAfter: newIndex,
    nameTodoListOld: from?.dataset?.listName,
    nameTodoListNew: to?.dataset?.listName,
  });
};

const handleListChange = () => {
  emit("taskUpdated", props.tasks);
};

const getTextColor = (bgColor: string): string => {
  if (!bgColor || bgColor.length < 3) return '#ffffff';

  const r = parseInt(bgColor.substring(0, 2), 16);
  const g = parseInt(bgColor.substring(2, 4), 16);
  const b = parseInt(bgColor.substring(4, 6), 16);

  const brightness = (r * 299 + g * 587 + b * 114) / 1000;
  return brightness > 125 ? '#000000' : '#ffffff';
};

const formatDate = (date: string | Date | null) => {
  if (!date) return "-";
  return new Date(date).toLocaleDateString("vi-VN", {
    day: "2-digit",
    month: "short"
  });
};


onMounted(() => {
  // thời gian todo 
  setInterval(() => {
    currentTime.value = Date.now();
  }, 1000);
});

</script>

<style scoped>
.ghost {
  opacity: 0 !important;
  transform: scale(0.98);
}

.chosen {
  @apply shadow-lg ring-2 ring-sky-400;
}

.dragging {
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
</style>
