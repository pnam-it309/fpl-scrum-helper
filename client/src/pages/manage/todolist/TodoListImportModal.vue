<template>
  <ModalCustom
    visible=""
    :open="props.open"
    @cancel="handleCloseModal"
    @close="handleCloseModal"
    :title="props.title"
    @submit="handleUpload"
  >
    <div class="clearfix text-center items-center">
      <a-upload
        :file-list="fileList"
        :before-upload="beforeUpload"
        @remove="handleRemove"
        accept=".xls,.xlsx"
      >
        <a-button>
          <upload-outlined></upload-outlined>
          Select File
        </a-button>
      </a-upload>
    </div>
  </ModalCustom>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { UploadOutlined } from '@ant-design/icons-vue'
import type { UploadProps } from 'ant-design-vue'
import { toast } from 'vue3-toastify'
import { localStorageAction } from '@/utils/storage'
import { USER_INFO_STORAGE_KEY } from '@/constants/storagekey'

import {
  getAllTodoList,
  getAllTodoProject,
  importLogTodoList,
  importTodoList,
  ParamsGetTodoList
} from '@/services/api/manage/todolist/todolist.api'
import { useRoute } from 'vue-router'
import ModalCustom from '@/components/custom/Modal/ModalCustom.vue'

const props = defineProps<{ open: boolean; title: string }>()
const emit = defineEmits(['close', 'fetchAll', 'updateLog', 'fetchAllHistory'])

const handleCloseModal = () => {
  fileList.value = []
  emit('close')
}

const fileList = ref<UploadProps['fileList']>([])

const handleRemove: UploadProps['onRemove'] = (file) => {
  const index = fileList?.value?.indexOf(file)
  const newFileList = fileList?.value?.slice()
  newFileList?.splice(index, 1)
  fileList.value = newFileList
}

const beforeUpload: UploadProps['beforeUpload'] = (file) => {
  fileList.value = [file]
  return false
}

// Thêm dòng mới vào logImport
const formattedDate = new Date(Date.now()).toLocaleString('vi-VN', {
  year: 'numeric',
  month: '2-digit',
  day: '2-digit',
  hour: '2-digit',
  minute: '2-digit',
  second: '2-digit'
})

const getFormattedDate = () => {
  return new Date().toLocaleString('vi-VN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

const params: ParamsGetTodoList = {
  search: '',
  page: 1,
  size: 100
}

const route = useRoute()
const projectId = ref<string | null>((route.query.projectId as string) || null)

const user = localStorageAction.get(USER_INFO_STORAGE_KEY)
const dataImportLog = async () => {
  const res = await importLogTodoList()
}

const handleUpload = async () => {
  if (fileList.value.length === 0) {
    toast.warning('Vui lòng chọn một file Excel để import.')
    return
  }

  const formData = new FormData()
  fileList.value.forEach((file: UploadProps['fileList'][number]) => {
    formData.append('filePath', file as any)
  })

  try {
    const initialData = await getAllTodoProject(params, route.params.id as string)

    const response = await importTodoList(formData, route.params.id as string)

    // ✅ Kiểm tra phản hồi lỗi định dạng từ backend
    if (typeof response === 'string' && response.includes('File Excel không đúng định dạng')) {
      toast.error('File Excel không đúng định dạng. Vui lòng sử dụng file mẫu.')
            emit('fetchAllHistory')

      return
    }

    const updatedData = await getAllTodoProject(params, route.params.id as string)

    if (updatedData.data.data.length === initialData.data.data.length) {
      toast.error('Import thất bại')
      emit('fetchAllHistory')
    } else {
      toast.success('Import thành công')
    }

    await dataImportLog()
    handleCloseModal()
    emit('fetchAll')
  } catch (error: any) {
    if (error.response?.data?.message) {
      toast.error(error.response.data.message)
    } else {
      toast.error('Đã xảy ra lỗi trong quá trình import')
            emit('fetchAllHistory')

    }

    console.error('❌ Lỗi khi Import:', error)
  }
}

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
