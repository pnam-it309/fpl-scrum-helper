<template>
<BreadcrumbDefaultV1 label="Quản lý cơ sở" :routes="breadcrumbRoutes">
    <FacilityFilter
    :searchQuery="state.searchQuery"
    :searchStatus="state.facilityStatus"
    @update:searchQuery="updateSearchQuery"
    @update:searchStatus="updateSearchStatus"
/>

    <FacilityTable
        :searchQuery="state.searchQuery"
        :facility="state.facility"
        :paginationParams="state.paginationParams" 
        :totalItems="state.totalItems"
        @add="openAddModal"
        @view="openViewModal"
        @page-change="handlePageChange" 
        @update-status="handleUpdateStatusClick"
    />
</BreadcrumbDefaultV1>
    <FacilityModal
        :open="state.isModalOpen"
        :facilityId="state.id"
        :title="state.id ? 'Cập nhật cơ sở' : 'Thêm mới cơ sở'"
        @close="closeModal"
        @success="fetchFacility"
    />
</template>

<script setup lang="ts">
const breadcrumbRoutes = [
  { name: 'Quản trị hệ thống', nameRoute: ROUTES_CONSTANTS.ADMIN.name },
  { name: 'Cơ sở ', nameRoute: ROUTES_CONSTANTS.ADMIN.children.FACILITY.name },
]
import { computed, onMounted, reactive, watch } from 'vue'
import { debounce } from 'lodash';
import { changeStatusFacility, FacilityResponse, getAllFacility, ParamsGetFacility } from '@/services/api/facility.api';

import FacilityTable from './FacilityTable.vue';
import FacilityFilter from './FacilityFilter.vue';
import FacilityModal from './FacilityModal.vue';
import { toast } from 'vue3-toastify';
import DivCustom from '@/components/custom/Div/DivCustom.vue';
import BreadcrumbDefault from '@/components/custom/Div/BreadcrumbDefault.vue';
import BreadcrumbDefaultV1 from '@/components/custom/Div/BreadcrumbDefaultV1.vue';
import { ROUTES_CONSTANTS } from '@/constants/path';

const state = reactive({
    searchQuery: '',
    facilityName: null as string | null,
    facilityStatus: null as string | null,
    isModalOpen: false,
    id: null as string | null,
    facility: [] as FacilityResponse[],
    paginationParams: { page: 1, size: 10 },
    totalItems: 0,
});

const fetchFacility = async () => {
    try {
        const params: ParamsGetFacility = {
            page: state.paginationParams.page,
            size: state.paginationParams.size,
            facilityName: state.searchQuery || '',
            facilityStatus: state.facilityStatus ?? undefined,
        };
        const response = await getAllFacility(params);
        state.facility = response?.data?.data || [];
        state.totalItems = response?.data?.totalElements || 0;
    } catch (error) {
        console.error('Lỗi khi tải danh sách cơ sở:', error);
    }
};

onMounted(fetchFacility);

const openAddModal = () => {
    state.id = null;
    state.isModalOpen = true;
};

const openViewModal = (id: string) => {
    state.id = id;
    state.isModalOpen = true;
};

const closeModal = () => {
    state.isModalOpen = false;
};

const handlePageChange = ({ page, pageSize }: { page: number; pageSize?: number }) => {
    state.paginationParams.page = page;
    if (pageSize) {
        state.paginationParams.size = pageSize;
    }
    fetchFacility();
};

const handleUpdateStatus = async (id: string) => {
    try {
        await changeStatusFacility(id)
        toast.success('Cập nhật trạng thái thành công!')
        fetchFacility()
    } catch (error) {
        toast.error('Cập nhật trạng thái thất bại!')
    }
}

const debouncedFetchFacility = debounce(fetchFacility, 1000);

const updateSearchQuery = (newQuery: string) => {
    state.searchQuery = newQuery.trim();
};

const updateSearchStatus = (newQuery: string) =>{
    state.facilityStatus = newQuery
}

watch(() => state.searchQuery, () => {
    state.paginationParams.page = 1;
    debouncedFetchFacility();
});

watch(() => state.facilityStatus, () => {
    state.paginationParams.page = 1;
    debouncedFetchFacility();
});

const handleUpdateStatusClick = (id: string) => {
    handleUpdateStatus(id)
}
</script>
<style scoped>
/* Áp dụng gradient cho nút */
:deep(.ant-btn) {
  background: var(--selected-header) !important; /* Gradient nền */
  color: var(--selected-text) !important; /* Màu chữ trắng */
  border-color: var(--selected-header) !important; /* Viền màu từ gradient */
}

/* Hiệu ứng hover */
:deep(.ant-btn:hover),
:deep(.ant-btn:focus) {
  background: var(--selected-header-hover) !important; /* Gradient khi hover */
  border-color: var(--selected-header-hover) !important; /* Viền khi hover */
}

</style>
