<template>
    <a-modal :open="props.isOpen" @cancel="closeModal" title="Cập nhật ngày kết thúc dự kiến">
        <template #footer>
            <a-popconfirm title="Bạn có chắc chắn muốn lưu thay đổi?" @confirm="handleSubmit" ok-text="Đồng ý"
                cancel-text="Huỷ">
                <a-button type="primary">Xác nhận</a-button>
            </a-popconfirm>
            <a-button @click="closeModal">Huỷ</a-button>
        </template>

        <div class="flex items-center justify-center my-4">
            <a-form ref="formRef" :model="formState" :rules="rules" :label-col="{ span: 12 }"
                :wrapper-col="{ span: 12 }">
                <a-form-item label="Ngày kết thúc dự kiến" name="completionDate" required>
                    <a-date-picker v-model:value="formState.completionDate" picker="date"
                        placeholder="Chọn ngày kết thúc dự kiến" />
                </a-form-item>
            </a-form>
        </div>
    </a-modal>

</template>

<script lang="ts" setup>
import { defineEmits, defineProps, ref } from 'vue'
import { RestartProjectRequest, restartProject } from '@/services/api/manage/project/maproject.api';
import { toast } from 'vue3-toastify';
import { Dayjs } from 'dayjs';
import { Rule } from 'ant-design-vue/es/form';
import type { UnwrapRef } from 'vue';
import { reactive } from 'vue';
import { localStorageAction } from '@/utils/storage';
import { USER_INFO_STORAGE_KEY } from '@/constants/storagekey';
const userLogin = localStorageAction.get(USER_INFO_STORAGE_KEY)
const emailLogin = userLogin.email
const props = defineProps<{
    isOpen: boolean,
    idProject: string | undefined,
}>();

const emit = defineEmits(['close', 'success']);

const formRef = ref();

const handleSubmit = async () => {
    try {
        formRef.value.validate()
            .then(async () => {
                
                const data: RestartProjectRequest = {
                    completionDate: formState.completionDate?.valueOf() as number,
                    emailLogin: emailLogin,
                }

                const res = await restartProject(props.idProject as string, data);

                toast.success(res.message);

                emit('success');
            })
    } catch (error) {
        console.log(error);
    }
}

const formState: UnwrapRef<{ completionDate: Dayjs | undefined }> = reactive({
    completionDate: undefined
})

const rules: Record<string, Rule[]> = {
    completionDate: [
        { required: true, message: 'Cần chọn ngày kết thúc dự kiến', trigger: ['blur', 'change'] }
    ]
}

const closeModal = () => {
    formRef.value.resetFields();
    emit('close');
}

</script>