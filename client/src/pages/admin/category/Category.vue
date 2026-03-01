<template>
    <BreadcrumbDefaultV1 :routes="breadcrumbRoutes" label="Quản lý thể loại">
        <CategoryFilter :searchQuery="state.searchQuery" @update:searchQuery="updateSearchQuery"
            :searchStatus="state.categoryStatus" @update:searchStatus="updateSearchStatus" />

        <CategoryTable :searchQuery="state.searchQuery" :category="state.category"
            :paginationParams="state.paginationParams" :totalItems="state.totalItems" @add="openAddModal"
            @view="openViewModal" @page-change="handlePageChange" @update-status="handleUpdateStatusClick" />
    </BreadcrumbDefaultV1>
    <CategoryModal :open="state.isModalOpen" :categoryId="state.id"
        :title="state.id ? 'Cập nhật thể loại' : 'Thêm mới thể loại'" @close="closeModal" @success="fetchCategory" />
           <!-- <FooterCustom/> -->
</template>

<script setup lang="ts">
const breadcrumbRoutes = [
  { name: 'Quản trị hệ thống', nameRoute: ROUTES_CONSTANTS.ADMIN.name },
  { name: 'Thể loại', nameRoute: ROUTES_CONSTANTS.ADMIN.children.CATEGORY.name },
]
import { onMounted, reactive, watch } from 'vue'
import { debounce } from 'lodash';
import CategoryFilter from './CategoryFilter.vue';
import CategoryTable from './CategoryTable.vue';
import CategoryModal from './CategoryModal.vue';
import { toast } from 'vue3-toastify';
import DivCustom from '@/components/custom/Div/DivCustom.vue';
import { CategoryResponse, changeStatusCategory, getAllCategory, ParamsGetCategory } from '@/services/api/admin/category/category.api';
import BreadcrumbDefault from '@/components/custom/Div/BreadcrumbDefault.vue';
import BreadcrumbDefaultV1 from '@/components/custom/Div/BreadcrumbDefaultV1.vue';
import { ROUTES_CONSTANTS } from '@/constants/path';

const state = reactive({
    searchQuery: '',
    categoryName: null as string | null,
    categoryStatus: null as string | null,
    isModalOpen: false,
    id: null as string | null,
    category: [] as CategoryResponse[],
    paginationParams: { page: 1, size: 10 },
    totalItems: 0,
});

const fetchCategory = async () => {
    try {

        const params: ParamsGetCategory = {
            page: state.paginationParams.page,
            size: state.paginationParams.size,
            categoryName: state.searchQuery || '',
            categoryStatus: state.categoryStatus ?? undefined,
        };
        const response = await getAllCategory(params);
        console.log(response.data)
        state.category = response?.data?.data || [];
        state.totalItems = response?.data?.totalElements || 0;
    } catch (error) {
        console.error('Lỗi khi tải danh sách thể loại:', error);
    }
};

onMounted(fetchCategory);

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
    fetchCategory();
};

const handleUpdateStatus = async (id: string) => {
    try {
        await changeStatusCategory(id)
        toast.success('Cập nhật trạng thái thành công!')
        fetchCategory()
    } catch (error) {
        toast.error('Cập nhật trạng thái thất bại!')
    }
}

const debouncedFetchCategory = debounce(fetchCategory, 500);

const updateSearchQuery = (newQuery: string) => {
    state.searchQuery = newQuery.trim();
};

const updateSearchStatus = (newQuery: string) => {
    state.categoryStatus = newQuery
}

// Theo dõi sự thay đổi của bộ lọc để gọi API
watch(() => state.searchQuery, () => {
    state.paginationParams.page = 1;
    debouncedFetchCategory();
});

watch(() => state.categoryStatus, () => {
    state.paginationParams.page = 1;
    debouncedFetchCategory();
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
