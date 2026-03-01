<template>
    <!-- Modal Sort -->
    <a-modal :visible="isOpen" @update:visible="handleUpdateVisible" @cancel="handleCancel" :footer="null" :title="null"
        :mask="false" :zIndex="10000" width="350px" centered="false" class="absolute top-[60%] right-4">
        <!-- Custom title -->
        <h2 class="text-center text-lg font-semibold mb-4">
            <SortAscendingOutlined class="text-lg mr-2" />Sắp xếp
        </h2>

        <div class="flex flex-col">
            <!-- Priority -->
            <button @click="selectSortOption('priority_desc')" :class="{ underline: selectedValue === 'priority_desc' }"
                class="flex items-center gap-2 px-4 py-2 text-sm hover:bg-gray-100 text-left transition duration-150">
                <DownCircleOutlined class="text-base" />
                Sắp xếp theo độ ưu tiên giảm dần
            </button>
            <button @click="selectSortOption('priority_asc')" :class="{ underline: selectedValue === 'priority_asc' }"
                class="flex items-center gap-2 px-4 py-2 text-sm hover:bg-gray-100 text-left transition duration-150">
                <UpCircleOutlined class="text-base" />
                Sắp xếp theo độ ưu tiên tăng dần
            </button>
            <hr class="my-2 border-t" />

            <!-- Due -->
            <button @click="selectSortOption('due_desc')" :class="{ underline: selectedValue === 'due_desc' }"
                class="flex items-center gap-2 px-4 py-2 text-sm hover:bg-gray-100 text-left transition duration-150">
                <ClockCircleOutlined class="text-base" />
                Sắp xếp theo ngày hạn giảm dần
            </button>
            <button @click="selectSortOption('due_asc')" :class="{ underline: selectedValue === 'due_asc' }"
                class="flex items-center gap-2 px-4 py-2 text-sm hover:bg-gray-100 text-left transition duration-150">
                <ClockCircleOutlined class="text-base" />
                Sắp xếp theo ngày hạn tăng dần
            </button>
            <hr class="my-2 border-t" />

            <!-- Progress -->
            <button @click="selectSortOption('progress_desc')" :class="{ underline: selectedValue === 'progress_desc' }"
                class="flex items-center gap-2 px-4 py-2 text-sm hover:bg-gray-100 text-left transition duration-150">
                <LineChartOutlined class="text-base" />
                Sắp xếp theo tiến độ giảm dần
            </button>
            <button @click="selectSortOption('progress_asc')" :class="{ underline: selectedValue === 'progress_asc' }"
                class="flex items-center gap-2 px-4 py-2 text-sm hover:bg-gray-100 text-left transition duration-150">
                <LineChartOutlined class="text-base" />
                Sắp xếp theo tiến độ tăng dần
            </button>
            <hr class="my-2 border-t" />

            <!-- Created -->
            <button @click="selectSortOption('newest')" :class="{ underline: selectedValue === 'newest' }"
                class="flex items-center gap-2 px-4 py-2 text-sm hover:bg-gray-100 text-left transition duration-150">
                <CalendarOutlined class="text-base" />
                Sắp xếp theo ngày tạo mới nhất
            </button>
            <button @click="selectSortOption('oldest')" :class="{ underline: selectedValue === 'oldest' }"
                class="flex items-center gap-2 px-4 py-2 text-sm hover:bg-gray-100 text-left transition duration-150">
                <CalendarOutlined class="text-base" />
                Sắp xếp theo ngày tạo lâu nhất
            </button>
            <hr class="my-2 border-t" />

            <!-- Name -->
            <button @click="selectSortOption('name_desc')" :class="{ underline: selectedValue === 'name_desc' }"
                class="flex items-center gap-2 px-4 py-2 text-sm hover:bg-gray-100 text-left transition duration-150">
                <FileTextOutlined class="text-base" />
                Sắp xếp theo tên thẻ giảm dần
            </button>
            <button @click="selectSortOption('name_asc')" :class="{ underline: selectedValue === 'name_asc' }"
                class="flex items-center gap-2 px-4 py-2 text-sm hover:bg-gray-100 text-left transition duration-150">
                <FileTextOutlined class="text-base" />
                Sắp xếp theo tên thẻ tăng dần
            </button>
        </div>
    </a-modal>
</template>

<script setup lang="ts">
import { webSocketSortTodo } from '@/services/service/member/sorttodo.socket';
import {
    DownCircleOutlined,
    UpCircleOutlined,
    ClockCircleOutlined,
    LineChartOutlined,
    CalendarOutlined,
    FileTextOutlined,
    SortAscendingOutlined
} from '@ant-design/icons-vue';
import { useRoute } from 'vue-router';
const route = useRoute();
defineProps<{ isOpen: boolean }>();
const emit = defineEmits(['select', 'update:visible']);

let selectedValue = '';
import { useSortStore } from '@/stores/sortStore'
import { watch } from 'vue';
const sortStore = useSortStore()

watch(() => sortStore.selectedSort, val => {
  selectedValue = val
}, { immediate: true })


const selectSortOption = (value: string) => {
    selectedValue = value;
    sortStore.setSelectedSort(value) 
    const payloadDesc = { type: 1, idPhase: route.params.idPhase };
    const payloadAsc = { type: 0, idPhase: route.params.idPhase };

    switch (value) {
        case 'priority_desc':
            webSocketSortTodo.sendSortTodoPriority(payloadDesc);
            break;
        case 'priority_asc':
            webSocketSortTodo.sendSortTodoPriority(payloadAsc);
            break;
        case 'due_desc':
            webSocketSortTodo.sendSortTodoDeadline(payloadDesc);
            break;
        case 'due_asc':
            webSocketSortTodo.sendSortTodoDeadline(payloadAsc);
            break;
        case 'progress_desc':
            webSocketSortTodo.sendSortTodoProgress(payloadDesc);
            break;
        case 'progress_asc':
            webSocketSortTodo.sendSortTodoProgress(payloadAsc);
            break;
        case 'newest':
            webSocketSortTodo.sendSortTodoCreatedDate(payloadDesc);
            break;
        case 'oldest':
            webSocketSortTodo.sendSortTodoCreatedDate(payloadAsc);
            break;
        case 'name_desc':
            webSocketSortTodo.sendSortTodoName(payloadDesc);
            break;
        case 'name_asc':
            webSocketSortTodo.sendSortTodoName(payloadAsc);
            break;
    }

    emit('select', value);
};

const handleCancel = () => emit('select', '');
const handleUpdateVisible = (visible: boolean) => emit('update:visible', visible);
</script>

<style scoped>
.underline {
    text-decoration: underline;
}
</style>