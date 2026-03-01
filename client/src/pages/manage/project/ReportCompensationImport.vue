<template>
  <ModalCustom :open="props.open" @cancel="handleCloseModal" :title="props.title" @submit="handleUpload">
    <div class="clearfix text-center">
      <a-upload :file-list="fileList" :before-upload="beforeUpload" @remove="handleRemove" accept=".xls,.xlsx">
        <a-button>
          <upload-outlined></upload-outlined>
          Select File
        </a-button>
      </a-upload>
    </div>
  </ModalCustom>
</template>

<script setup lang="ts">
import ModalCustom from '@/components/custom/Modal/ModalCustom.vue';
import { ref } from 'vue';
import { UploadOutlined } from '@ant-design/icons-vue';
import type { UploadProps } from 'ant-design-vue';
import { toast } from 'vue3-toastify';
import { localStorageAction } from '@/utils/storage';
import { USER_INFO_STORAGE_KEY } from '@/constants/storagekey';
import { getAllReport, ParamsGetReport } from '@/services/api/member/report/report.api';
import { importReport } from '@/services/api/manage/project/maproject.api';
import { getAllReportCompensation } from '@/services/api/manage/report/report.api';

const props = defineProps<{ open: boolean; title: string; projectId: string }>();
const emit = defineEmits(['close', 'fetchAll', 'updateLog', 'fetchAllHistory']);

const handleCloseModal = () => {
  fileList.value = [];
  emit('close');
};

const fileList = ref<UploadProps['fileList']>([]);

const handleRemove: UploadProps['onRemove'] = (file) => {
  const index = fileList?.value?.indexOf(file);
  const newFileList = fileList?.value?.slice();
  newFileList?.splice(index, 1);
  fileList.value = newFileList;
};

const beforeUpload: UploadProps['beforeUpload'] = (file) => {
  fileList.value = [file];
  return false;
};

const getFormattedDate = () => {
  return new Date().toLocaleString('vi-VN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
  });
};

const user = localStorageAction.get(USER_INFO_STORAGE_KEY);

const dataImportLog = async () => {
  console.log('Ghi log import:', { projectId: props.projectId, time: getFormattedDate() });
};

const handleUpload = async () => {
  try {
    const formData = new FormData();
    fileList?.value?.forEach((file: UploadProps['fileList'][number]) => {
      formData.append('filePath', file as any);
    });

    if (!props.projectId) {
      toast.error('Thiếu ID Project trong route');
      console.error('Thiếu ID Project trong route');
      return;
    }

    const responseAffter = await getAllReportCompensation()

    const importResponse = await importReport(formData, props.projectId);
    console.log('📤 Phản hồi importReport:', JSON.stringify(importResponse, null, 2));

    const responseBefore = await getAllReportCompensation()

    if(responseAffter.data >= responseBefore.data){
      toast.error('Import thất bại')
       emit('fetchAllHistory');
    } else {
      toast.success('Import thành công');
      await dataImportLog();
      emit('fetchAll');
    }

    handleCloseModal();
  } catch (error: any) {
    const errorMessage = error.response?.data?.message || 'Đã xảy ra lỗi trong quá trình import';
    toast.error(errorMessage);
    console.error('Lỗi khi Import:', error);

    const importTime = getFormattedDate();
    const newLogEntry = {
      email: user?.email || 'Chưa xác định',
      date: importTime,
      rolesCodes: user?.rolesNames ? user.rolesNames.join(', ') : 'Chưa xác định',
    };
    const stockLogs = JSON.parse(localStorage.getItem('IMPORT_LOGS') || '[]');
    const updatedLogs = [newLogEntry, ...stockLogs];
    localStorage.setItem('IMPORT_LOGS', JSON.stringify(updatedLogs));
    emit('updateLog', newLogEntry);

    handleCloseModal();
  }
};
</script>

<style scoped>
.ant-btn {
  background-image: var(--selected-header) !important;
  color: var(--selected-text) !important;
  border: none !important;
}

.ant-btn:hover,
.ant-btn:focus {
  background-image: var(--selected-header-hover) !important;
  color: var(--selected-text) !important;
  border: none !important;
}
</style>