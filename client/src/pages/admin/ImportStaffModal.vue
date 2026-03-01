<template>
  <ModalCustom
    :open="props.open"
    @cancel="handleCloseModal"
    @close="handleCloseModal"
    :title="props.title"
    @submit="handleUpload"
  >
    <div class="clearfix text-center">
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
import ModalCustom from '@/components/custom/Modal/ModalCustom.vue'
import { ref } from 'vue'
import { UploadOutlined } from '@ant-design/icons-vue'
import type { UploadProps } from 'ant-design-vue'
import { toast } from 'vue3-toastify'
import { getAllCountStaff, getAllStaff, importStaff, paramGetStaff } from '@/services/api/admin/staff.api'

const props = defineProps<{ open: boolean; title: string }>()
const emit = defineEmits(['close', 'fetchAll','fetchAllHistory'])

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

const handleUpload = async () => {
  const formData = new FormData()

  // ✅ Kiểm tra nếu chưa chọn file
  if (!fileList?.value?.length) {
    toast.error('Vui lòng chọn FILE')
    return
  }

  fileList.value.forEach((file: UploadProps['fileList'][number]) => {
    formData.append('file', file as any)
  })

  try {
    const initialData = await getAllCountStaff()

    const response = await importStaff(formData)

    // ✅ Nếu response là string và chứa lỗi định dạng
    if (typeof response === 'string' && response.includes('File Excel không đúng định dạng')) {
      toast.error('File Excel không đúng định dạng. Vui lòng sử dụng file mẫu.')
      return
    }

    handleCloseModal()
    await new Promise((resolve) => setTimeout(resolve, 0))

    const updatedData = await getAllCountStaff()

    const isDataChanged = initialData.data !== updatedData.data
    console.log('dữ liệu trước import:', initialData)
    console.log('dữ liệu sau import:', updatedData)

    if (isDataChanged) {
      toast.success('Import thành công')
      emit('fetchAll')
    } else {
      toast.error('Import thất bại')
      emit('fetchAllHistory')
    }

    handleCloseModal()
  } catch (error: any) {
    console.error('Lỗi khi import:', error)

    // ✅ Xử lý lỗi đúng cách: lấy lỗi từ response Axios
    const errorMsg =
      typeof error?.response?.data === 'string'
        ? error.response.data
        : error?.response?.data?.message || error?.message || ''

    if (errorMsg.includes('File Excel không đúng định dạng')) {
      toast.error('File Excel không đúng định dạng. Vui lòng sử dụng file mẫu.')
    } else if (errorMsg.includes('Required request part') || errorMsg.includes('file')) {
      toast.error('Vui lòng chọn FILE')
    } else {
      toast.error('Đã xảy ra lỗi khi import. Vui lòng thử lại.')
    }
  }
}

</script>
<style scoped></style>
