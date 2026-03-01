<template>
    <a-modal :open="props.isOpen" width="1200px" @cancel="closeModal" style="top: 20px">
        <template #footer>
            <a-popconfirm v-if="!checkProjectFinish" title="Bạn có chắc chắn muốn lưu thay đổi?" @confirm="handleSubmit"
                ok-text="Đồng ý" cancel-text="Huỷ">
                <a-button type="primary">Kết thúc</a-button>
            </a-popconfirm>
            <a-button v-else type="primary" @click="openStartingAgainModal">Bắt đầu lại</a-button>
            <a-button @click="closeModal">Huỷ</a-button>
        </template>
        <div class="flex">
            <div class="w-2/5 mt-4 mr-3">
                <a-card title="Thông tin dự án">
                    <p class="line">
                        <FileTextOutlined class="mr-2" style="color: #1890ff;" /> <strong class="mr-1">Tên dự án
                            :</strong>
                        {{ dataSummaryProject?.name }} - {{ dataSummaryProject?.amountOfMembers }} thành viên
                    </p>
                    <p class="line">
                        <BarcodeOutlined class="mr-2" style="color: #722ed1;" /> <strong class="mr-1">Mã dự án
                            :</strong> {{
                                dataSummaryProject?.code }}
                    </p>
                    <p class="line">
                        <AppstoreOutlined class="mr-2" style="color: #13c2c2;" /> <strong class="mr-1">Thể loại :
                        </strong> {{
                            dataSummaryProject?.category }}
                    </p>
                    <p class="line">
                        <BankOutlined class="mr-2" style="color: #fa541c;" /> <strong class="mr-1">Chuyên ngành cơ sở
                            :</strong> {{ dataSummaryProject?.nameDepartment }}
                    </p>
                    <p class="line">
                        <UserOutlined class="mr-2" style="color: #1890ff;" /> <strong class="mr-1">Số lượng thành viên
                            :</strong> {{ dataSummaryProject?.amountOfMembers }}
                    </p>
                </a-card>
                <a-card title="Trạng thái chi tiết" class="mt-4">
                    <p class="line">
                        <SyncOutlined class="mr-2" style="color: #52c41a;" /> <strong class="mr-1">Trạng thái : </strong>
                        <a-tag :color="getColorStatusProject(dataSummaryProject?.status as StatusProject)">{{
                            getTextStatusProject(
                                dataSummaryProject?.status as StatusProject) }} </a-tag>
                    </p>
                    <p class="line">
                        <CalendarOutlined class="mr-2" style="color: #2f54eb;" /> <strong class="mr-1">Ngày bắt đầu :
                        </strong> {{
                            formatDate(dataSummaryProject?.startTime as number) }}
                    </p>
                    <p class="line">
                        <CalendarOutlined class="mr-2" style="color: #2f54eb;" /> <strong class="mr-1">Ngày kết thúc dự
                            kiến :
                        </strong> {{
                            formatDate(dataSummaryProject?.endTime as number) }}
                    </p>
                    <p class="line">
                        <CalendarOutlined class="mr-2" style="color: #2f54eb;" /> <strong class="mr-1">Ngày kết thúc
                            thực tế :
                        </strong> {{
                            dataSummaryProject?.actualEndTime ? formatDate(dataSummaryProject?.actualEndTime as number) :
                                '--:--' }}
                    </p>
                </a-card>
            </div>
            <div class="w-3/4 mt-4">
                <div class="h-3/5 w-full">
                    <div class="w-full h-full" v-if="dataCharts.dataTodoByPhaseCharts.length != 0">
                        <h1 class="font-semibold text-lg "><i>Số lượng công việc theo giai đoạn</i></h1>
                        <VueApexCharts width="100%" height="90%" type="area" :series="seriesAmountOfTodoByPhase"
                            :options="optionsAmountTodoByPhaseOptions" />
                    </div>
                    <a-empty v-else>
                        <template #description>
                            Không có dữ liệu số lượng công việc
                        </template>
                    </a-empty>
                </div>
                <div class="h-2/5 flex text-4xl font-semibold ">
                    <div class="w-1/2 m-2 rounded-lg shadow-xl p-5 flex">
                        <div class="w-1/3 flex flex-col justify-center items-center font-extrabold">
                            <p> {{ getSumAmountOfStatus }}
                            </p>
                            <p class="text-base font-medium mt-5">Công việc</p>
                        </div>
                        <div v-if="getSumAmountOfStatus != 0" class="w-2/3 flex items-center flex-col">
                            <!-- <VueApexCharts :key="keyStatusDonutCharts" type="donut" height="70%"
                                    :series="seriesAmountOfTodoByStatus" :options="optionsAmountTodoByStatus" /> -->
                            <div class="size-[70%] flex justify-center">
                                <DoughnutChart :chartData="dataStatusCharts" :chartOptions="pieOptions" />
                            </div>
                            <p class="text-xs">Tỷ lệ công việc theo trạng thái</p>
                        </div>
                        <a-empty v-else></a-empty>
                    </div>
                    <div class="w-1/2 m-2 rounded-lg shadow-xl p-5 flex">
                        <div class="w-1/3 flex flex-col justify-center items-center font-extrabold">
                            <p> {{ getSumAmountofPhase }}
                            </p>
                            <p class="text-base font-medium mt-5">Giai đoạn</p>
                        </div>
                        <div v-if="getSumAmountofPhase != 0" class="w-2/3 flex items-center flex-col">
                            <!-- <VueApexCharts type="donut" height="70%" :series="seriesAmountOfPhase"
                                    :options="optionsAmountOfPhase" /> -->
                            <div class="size-[70%] flex justify-center">
                                <DoughnutChart class="w-full" :chartData="dataPhaseCharts" :chartOptions="pieOptions" />
                            </div>
                            <p class="text-xs"><i>Tỷ lệ giai đoạn theo trạng thái</i></p>
                        </div>
                        <a-empty v-else></a-empty>
                    </div>
                </div>
            </div>
        </div>
    </a-modal>

    <MAStartingAgainModal :is-open="isOpenStartingAgainModal" :id-project="props.idProject"
        @success="handleSubmitStartingAgainSuccess" @close="closeStartingAgainModal" />
</template>

<script lang="ts" setup>
import {
    finishEarlyProject,
    getDetailSummaryProject,
    ProjectDetailSummaryResponse,
    getAmountOfPhasesByStatus,
    getAmountOfTodoByPhase
} from '@/services/api/manage/project/maproject.api';
import { countTodoByTodoStatus } from '@/services/api/manage/todo/todo.api';
import { PieChart } from 'echarts/charts';
import {
    LegendComponent,
    TitleComponent,
    TooltipComponent,
} from 'echarts/components';
import { use } from 'echarts/core';
import { CanvasRenderer } from 'echarts/renderers';
import { computed, defineProps, ref, watch } from 'vue';
import { toast } from 'vue3-toastify';
import {
    FileTextOutlined,
    BarcodeOutlined,
    AppstoreOutlined,
    BankOutlined,
    SyncOutlined,
    CalendarOutlined,
    UserOutlined
} from '@ant-design/icons-vue';
import { reactive } from 'vue';
import VueApexCharts from 'vue3-apexcharts';
import DoughnutChart from '@/pages/admin/statistics/DoughnutChart.vue';
import {
    Chart,
    LinearScale,
    CategoryScale,
    BarController,
    BarElement,
    PointElement,
    LineController,
    LineElement,
    ArcElement,
    RadarController,
    RadialLinearScale,
    Tooltip,
    PieController,
    Legend
} from 'chart.js'
import MAStartingAgainModal from './MAStartingAgainModal.vue';
import { localStorageAction } from '@/utils/storage';
import { USER_INFO_STORAGE_KEY } from '@/constants/storagekey';
Chart.register(
    LinearScale,
    CategoryScale,
    BarController,
    BarElement,
    PointElement,
    LineController,
    LineElement,
    ArcElement,
    RadialLinearScale,
    Tooltip,
    RadarController,
    PieController,
    Legend
)
const userLogin = localStorageAction.get(USER_INFO_STORAGE_KEY)
const emailLogin = userLogin.email

const props = defineProps<{
    isOpen: boolean,
    idProject: string,
}>();

type StatusProject = "CHUA_DIEN_RA" | "DANG_DIEN_RA" | "DA_DIEN_RA";

type StatusTodo = 'CHUA_HOAN_THANH' | 'DA_HOAN_THANH' | 'HOAN_THANH_SOM' | 'QUA_HAN'

type StatusPhase = "CHUA_HOAN_THANH" | "DANG_LAM" | "DA_HOAN_THANH" | "QUA_HAN";

const emits = defineEmits(['close', 'success']);

const getTextStatusProject = (status: StatusProject) => {
    switch (status) {
        case 'CHUA_DIEN_RA':
            return 'Chưa diễn ra';
        case 'DANG_DIEN_RA':
            return 'Đang diễn ra';
        case 'DA_DIEN_RA':
            return 'Đã diễn ra';
    }
}

const getColorStatusProject = (status: StatusProject): string => {
    switch (status) {
        case 'CHUA_DIEN_RA':
            return 'red'
        case 'DANG_DIEN_RA':
            return 'green'
        case 'DA_DIEN_RA':
            return 'orange'
        default:
            return 'gray'
    }
}

const getTextStatusTodo = (status: StatusTodo): string => {
    const translations = {
        CHUA_HOAN_THANH: "Chưa hoàn thành",
        DA_HOAN_THANH: "Đã hoàn thành",
        HOAN_THANH_SOM: "Hoàn thành sớm",
        QUA_HAN: "Quá hạn"
    };

    return translations[status] || "Không xác định";
}

const getTextPhase = (status: StatusPhase) => {
    const translations = {
        CHUA_HOAN_THANH: "Chưa hoàn thành",
        DANG_LAM: "Đang làm",
        DA_HOAN_THANH: "Đã hoàn thành",
        QUA_HAN: "Quá hạn"
    };

    return translations[status] || "Không xác định";
}

const dataSummaryProject = ref<ProjectDetailSummaryResponse>();

const fetchDetailSummaryProject = async () => {
    try {
        const res = await getDetailSummaryProject(props.idProject);

        dataSummaryProject.value = res.data;
    } catch (error) {

    }
}

watch(() => [props.idProject, props.isOpen],
    () => {
        fetchDetailSummaryProject();
        fetchDataCharts();
    }
)

const formatDate = (timestamp: number) => {
    if (!timestamp) return 'N/A'
    const date = new Date(timestamp)
    return date.toLocaleDateString('vi-VN')
}

// charts

use([
    CanvasRenderer,
    PieChart,
    TitleComponent,
    TooltipComponent,
    LegendComponent,
]);

const dataCharts = reactive({
    dataStatusCharts: [] as { name: string, value: number }[],
    dataPhaseCharts: [] as { name: string, value: number }[],
    dataTodoByPhaseCharts: [] as { name: string, value: number }[],
})

const fetchDataCharts = async () => {
    try {
        const [amountTodos, amountPhases, amountTodoByPhase] = await Promise.all([
            countTodoByTodoStatus(props.idProject),
            getAmountOfPhasesByStatus(props.idProject),
            getAmountOfTodoByPhase(props.idProject),
        ])

        dataCharts.dataStatusCharts = amountTodos.data.map((data) => ({ value: data.amount, name: getTextStatusTodo(data.todoStatus as StatusTodo) }))
        dataCharts.dataPhaseCharts = amountPhases.data.map((data) => ({ value: data.amount, name: getTextPhase(data.status as StatusPhase) }))
        dataCharts.dataTodoByPhaseCharts = amountTodoByPhase.data.map((data) => ({ value: data.amount, name: data.namePhase }))

        dataStatusCharts.value = {
            labels: amountTodos.data.map((data) => getTextStatusTodo(data.todoStatus as StatusTodo)),
            datasets: [
                {
                    label: 'Số lượng công việc',
                    data: amountTodos.data.map((item) => item.amount),
                    backgroundColor: ['#f87171', '#facc15', '#34d399', '#60a5fa', '#a78bfa', '#f472b6']
                }
            ]
        }

        dataPhaseCharts.value = {
            labels: amountPhases.data.map((data) => getTextPhase(data.status as StatusPhase)),
            datasets: [
                {
                    label: 'Số lượng giai đoạn',
                    data: amountPhases.data.map((item) => item.amount),
                    backgroundColor: ['#f87171', '#facc15', '#34d399', '#60a5fa', '#a78bfa', '#f472b6']
                }
            ]
        }
    } catch (error) {

    }
}

const seriesAmountOfTodoByPhase = computed(() => {
    return [
        {
            name: 'Số lượng công việc',
            data: dataCharts.dataTodoByPhaseCharts.map((data) => data.value)
        }
    ]
})

const optionsAmountTodoByPhaseOptions = computed(() => {
    return {
        xaxis: {
            categories: dataCharts.dataTodoByPhaseCharts.map((data) => data.name)
        },
        chart: {
            type: 'area',
            toolbar: {
                show: false
            },
        },
    }
})

const pieOptions = {
    responsive: true,
    plugins: { legend: { position: 'bottom', display: false, align: 'start' } }
}

const dataStatusCharts = ref({
    labels: [] as string[],
    datasets: [] as { label: string; data: number[]; backgroundColor: string[] }[]
})

const dataPhaseCharts = ref({
    labels: [] as string[],
    datasets: [] as { label: string; data: number[]; backgroundColor: string[] }[]
})

const getSumAmountOfStatus = computed(() => {
    return dataCharts.dataStatusCharts.map((data) => data.value).reduce((sum, data) => sum +
        data, 0)
})

const getSumAmountofPhase = computed(() => {
    return dataCharts.dataPhaseCharts.map((data) => data.value).reduce((sum, data) => sum +
        data, 0)
})

const checkProjectFinish = computed(() => {
    return dataSummaryProject.value?.status == 'DA_DIEN_RA';
})

// starting again modal

const isOpenStartingAgainModal = ref<boolean>(false);

const openStartingAgainModal = () => {
    isOpenStartingAgainModal.value = true;
}

const closeStartingAgainModal = () => {
    isOpenStartingAgainModal.value = false;
}

const handleSubmitStartingAgainSuccess = () => {
    closeStartingAgainModal();
    closeModal();
    fetchDetailSummaryProject();
    emits('success');
}
//

const handleSubmit = async () => {
    try {
        await finishEarlyProject(props.idProject,emailLogin);
        toast.success('Thay đổi trạng thái thành công');
        emits('success');
        fetchDetailSummaryProject();
        closeModal();
    } catch (error) {
        toast.error('Có lỗi xảy ra');
        console.log(error);
    }
}

const closeModal = () => {
    console.log('close modal')
    emits('close');
}
</script>

<style scoped>
.line {
    display: flex;
    align-items: center;
    font-size: 14px;
}

.line+.line {
    margin-top: 20px;
}

.line .title-line {
    font-weight: 600;
    margin-right: 5px;
}
</style>