<template>
    <a-modal :zIndex="10000" v-model:open="visible" title="Ngày hạn" @cancel="handleCancel" :footer="null" :width="350">
        <div class="mb-4">
            <label class="block mb-1">Thời gian:</label>
            <a-date-picker :getPopupContainer="trigger => trigger.parentElement" v-model:value="deadline" show-time
                format="DD/MM/YYYY hh:mm A" style="width: 100%" :class="{ 'border-red-500': deadlineError }" />
            <div v-if="deadlineError" class="text-red-500 text-sm mt-1">
                {{ deadlineError }}
            </div>
        </div>

        <div class="mb-4">
            <label class="block mb-1">Đặt nhắc nhở:</label>
            <a-select v-model:value="reminder" style="width: 100%"
                :getPopupContainer="trigger => trigger.parentElement">
                <a-select-option :value="null">Không có</a-select-option>
                <a-select-option :value="0">Vào thời điểm ngày hết hạn</a-select-option>
                <a-select-option :value="5">5 phút trước</a-select-option>
                <a-select-option :value="10">10 phút trước</a-select-option>
                <a-select-option :value="15">15 phút trước</a-select-option>
                <a-select-option :value="60">1 giờ trước</a-select-option>
                <a-select-option :value="120">2 giờ trước</a-select-option>
                <a-select-option :value="1440">1 ngày trước</a-select-option>
                <a-select-option :value="2880">2 ngày trước</a-select-option>
            </a-select>

        </div>

        <div style="color: red; font-size: 13px;" class="mb-4">
            (*) Lưu ý: Tất cả các thành viên trong thẻ sẽ nhận được thông báo
        </div>

        <!-- Nút Lưu -->
        <a-button type="primary" block class="mb-2" @click="handleSave">
            Lưu
        </a-button>

        <!-- Nút Xóa -->
        <a-button danger block @click="handleDelete">
            Xóa
        </a-button>
    </a-modal>
</template>

<script setup lang="ts">
import { TODO_ID_STORAGE_KEY } from '@/constants/key';
import { webSocketTodo } from '@/services/service/member/metodo.socket';
import { getDateFormat } from '@/utils/commom.helper';
import { localStorageAction } from '@/utils/storage';
import { ref, watch } from 'vue';
import dayjs from 'dayjs';
import { toast } from 'vue3-toastify';
import { USER_INFO_STORAGE_KEY } from '@/constants/storagekey';
import { useRoute, useRouter } from "vue-router";
import { getUrlActivity } from '@/utils/urlActivityHelper';

const route = useRoute();
const props = defineProps<{
    modelValue: boolean;
    currentDeadline?: number | null;
    currentReminder?: number | null;
}>();

const userLogin = localStorageAction.get(USER_INFO_STORAGE_KEY);

const emit = defineEmits(['update:modelValue', 'updateSuccessDeadline']);

const visible = ref(props.modelValue);
const deadline = ref(null);
const reminder = ref<number | null>(props.currentReminder ? Number(props.currentReminder) : null);

const deadlineError = ref('');

// Cập nhật props khi mở modal
watch(() => props.modelValue, (val) => {
    visible.value = val;
    if (val) {
        deadline.value = props.currentDeadline ? dayjs(props.currentDeadline) : null;
        reminder.value = props.currentReminder !== undefined ? props.currentReminder : null;
        deadlineError.value = false; // Reset lỗi khi mở modal
    }
});

// Đóng modal
watch(visible, (val) => emit('update:modelValue', val));

// Lưu 
const handleSave = () => {

    if (!deadline.value) {
        deadlineError.value = 'Vui lòng chọn ngày hạn!';
        return;
    }

    if (dayjs(deadline.value).isBefore(dayjs())) {
        deadlineError.value = 'Ngày hạn không được ở quá khứ!';
        return;
    }

    deadlineError.value = ''; // xóa lỗi nếu hợp lệ

    const payload = {
        idTodo: localStorageAction.get(TODO_ID_STORAGE_KEY),
        deadline: getDateFormat(deadline.value.toDate(), true),
        projectId: route.params.id,
        idUser: userLogin.userId,
        reminder: reminder.value,
        urlPath: getUrlActivity(route)
    };

    webSocketTodo.sendUpdateDeadlineTodo(payload);
    emit('updateSuccessDeadline');
    toast.success('Cập nhật hạn công việc...!');
    visible.value = false;
};


// Xóa
const handleDelete = () => {
    if (!deadline.value) {
        toast.warning('Không có ngày hạn để xóa!',);
        return;
    }
    deadline.value = null;
    reminder.value = null;

    const payload = {
        idTodo: localStorageAction.get(TODO_ID_STORAGE_KEY),
        idTodoDelete: localStorageAction.get(TODO_ID_STORAGE_KEY),
        projectId: route.params.id,
        idUser: userLogin.userId,
        urlPath: getUrlActivity(route),
    };
    webSocketTodo.sendDeleteDeadlineTodo(payload);
    emit('updateSuccessDeadline');
    toast.success('Hủy ngày hạn công việc...!');
    visible.value = false;
};

const updateSuccessDeadline = () => {
    emit('updateSuccessDeadline');
}

// Đóng modal
const handleCancel = () => {
    visible.value = false;
};
</script>

<style scoped>
/* Thêm style cho border nếu có lỗi */
.border-red-500 {
    border-color: #f56565;
}
</style>
