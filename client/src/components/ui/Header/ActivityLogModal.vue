<template>
    <a-modal :open="visible" title="Lịch sử" @ok="handleOk" ok-text="Đóng" :closable="true" width="80%"
        :cancelButtonProps="{ style: { display: 'none' } }" @update:visible="handleClose">
        <div v-if="activityLogs.length === 0" class="text-center text-gray-600 py-4">
            <p>Không có dữ liệu</p>
        </div>

        <a-table v-else :columns="columns" :data-source="activityLogs" :pagination="{
            current: page,
            pageSize: size,
            total: totalRecords,
            showSizeChanger: true,
            pageSizeOptions: ['10', '20', '30', '40', '50', '99']
        }" :scroll="{ y: 300 }" :rowKey="record => record.id" @change="handlePageChange">
        </a-table>
    </a-modal>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { getAllActivityLog, ParamsGetActivityLog, ActivityLogResponse } from '@/services/api/admin/activity.log.api'
import { TableColumnsType } from 'ant-design-vue'
import { localStorageAction } from '@/utils/storage'
import { USER_INFO_STORAGE_KEY } from '@/constants/storagekey'
const props = defineProps({
    visible: {
        type: Boolean,
        default: false
    }
})

const emit = defineEmits(['update:visible'])

const activityLogs = ref<ActivityLogResponse[]>([])
const page = ref(1)
const size = ref(10)
const totalRecords = ref(0)

const columns: TableColumnsType = [
    { title: 'STT', key: 'orderNumber', customRender: ({ index }) => index + 1, align: 'center' },
    { title: 'Email người thực hiện', key: 'executorEmail', dataIndex: 'executorEmail', align: 'center' },
    { title: 'Ngày thực hiện', key: 'createDate', dataIndex: 'createDate', align: 'center', customRender: ({ text }) => formatDate(text) },
    { title: 'Nội dung', key: 'content', dataIndex: 'content', align: 'center' },
    { title: 'Chức vụ', key: 'role', dataIndex: 'role', align: 'center' },
]

const fetchActivityLogs = async () => {
    const params: ParamsGetActivityLog = {
        page: page.value,
        size: size.value,
    }

    try {
        const response = await getAllActivityLog(params)
        activityLogs.value = response.data.data
        totalRecords.value = response.data.totalElements || 0
    } catch (error) {
        console.error('Error fetching activity logs:', error)
    }
}

const formatDate = (timestamp: number) => {
    const date = new Date(timestamp)
    return `${date.getDate()}/${date.getMonth() + 1}/${date.getFullYear()}`
}

const handlePageChange = (pagination: any) => {
    page.value = pagination.current
    size.value = pagination.pageSize
    fetchActivityLogs() // Fetch new data when the page changes
}

watch(() => props.visible, (newVal) => {
    if (newVal) {
   
        fetchActivityLogs()
    }
})

const handleOk = () => {
    emit('update:visible', false)
}

const handleClose = (newVisible: boolean) => {
    if (!newVisible) {
        emit('update:visible', false)
    }
}
</script>
