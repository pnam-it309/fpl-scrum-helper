<template>
    <DivCustom label="Danh sách Giai đoạn" customClasses="mt-5">
        <div class="min-h-[360px]">
            <a-table :columns="columns" :data-source="phase" :pagination="{
                current: paginationParams.page,
                pageSize: paginationParams.size,
                total: totalItems,
                showSizeChanger: true,
                pageSizeOptions: ['10', '20', '30', '40', '50']
            }" :scroll="{ y: 300 }" @change="handlePageChange">
                <template #bodyCell="{ column, record }">
                    <template v-if="column.key === 'operation'">
                        <div class="flex flex gap-1 justify-center">
                            <a-tooltip title="">
                                <a-icon @click="handleNavigate(record.id)">
                                    <font-awesome-icon :icon="['fas', 'circle-info']" class="cursor-pointer p-2"
                                        style="font-size: 20px" />
                                </a-icon>
                            </a-tooltip>
                        </div>
                    </template>
                </template>
            </a-table>
        </div>
    </DivCustom>
</template>

<script setup lang="ts">
import { defineProps, defineEmits, h, ref } from 'vue'
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import { TableColumnsType, Tag } from 'ant-design-vue'
import { useRoute, useRouter } from 'vue-router'
import { ROUTES_CONSTANTS } from '@/constants/path'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { library } from '@fortawesome/fontawesome-svg-core'
import { faReceipt, faPenToSquare, faCircleInfo, faFilter } from '@fortawesome/free-solid-svg-icons'
import dayjs from 'dayjs'

library.add(faReceipt, faPenToSquare, faCircleInfo, faFilter)

defineProps<{
    searchQuery: string
    phase: any[]
    paginationParams: { page: number; size: number }
    totalItems: number
}>()

const emit = defineEmits(['page-change'])

const columns: TableColumnsType = [
    {
        title: 'STT',
        key: 'orderNumber',
        dataIndex: 'orderNumber',
        width: 5,
        align: 'center'
    },
    { title: 'Tên', key: 'name', dataIndex: 'name', width: 10, align: 'center' },


    {
        title: 'Thời gian bắt đầu',
        key: 'startTime',
        dataIndex: 'startTime',
        width: 10,
        align: 'center',
        customRender: ({ text }) =>
            text
                ? h(Tag, { color: 'green' }, () => dayjs(Number(text)).format('DD/MM/YYYY HH:mm'))
                : ''
    },
    {
        title: 'Thời gian kết thúc',
        key: 'endTime',
        dataIndex: 'endTime',
        width: 10,
        align: 'center',
        customRender: ({ text }) =>
            text
                ? h(Tag, { color: 'green' }, () => dayjs(Number(text)).format('DD/MM/YYYY HH:mm'))
                : ''
    },


    {
        title: 'Trạng thái',
        key: 'statusPhase',
        dataIndex: 'statusPhase',
        width: 10,
        align: 'center',

        customRender: ({ text }) => {
            let color = 'default';
            let label = text;

            switch (text) {
                case 'CHUA_HOAN_THANH':
                    color = 'blue';
                    label = 'Chưa hoàn thành';
                    break;
                case 'DANG_LAM':
                    color = 'orange';
                    label = 'Đang làm';
                    break;
                case 'DA_HOAN_THANH':
                    color = 'green';
                    label = 'Đã hoàn thành';
                    break;
                case 'QUA_HAN':
                    color = 'red';
                    label = 'Quá hạn';
                    break;
            }

            return h(Tag, { color }, () => label);
        }
    },
    {
        title: 'Hành động',
        key: 'operation',
        width: 10,
        align: 'center',
        fixed: 'right'
    }
]

const handlePageChange = (Pagination: any) => {
    emit('page-change', { page: Pagination.current, pageSize: Pagination.pageSize })
}

const route = useRoute()
const projectId = ref<string | null>(route.params.id as string || null)

const router = useRouter()

const handleNavigate = (id: string) => {
    router.push({
        path: `/project/project-detail/${projectId.value}/${id}`,
    })
}

</script>