<template>
    <DivCustom label="Bộ lọc" :enableToggle="true" :isOpened="true"  customClasses="mt-2">
        <div class="flex items-center space-x-2">
            <a-input 
                v-model:value="localSearchQuery" 
                placeholder="Nhập tên dự án..." 
                class="w-8/12" 
            />
            <a-select
                v-model:value="localSearchStatus"
                :options="options"
                placeholder="Trạng thái"
                class="w-4/12"
            />
                <a-tooltip title="Làm mới bộ lọc">
                <a-button @click="resetFilters" class="p-2 flex items-center justify-center">
                    <ReloadOutlined />
                </a-button>
            </a-tooltip>
        </div>
    </DivCustom>
</template>

<script setup lang="ts">
import { ref, watch, defineProps, defineEmits } from 'vue';
import DivCustom from '@/components/custom/Div/DivCustom.vue';
import { ReloadOutlined } from '@ant-design/icons-vue';

const props = defineProps<{ searchQuery: string }>();
const emit = defineEmits(['update:searchQuery', 'update:searchStatus']);

const localSearchQuery = ref(props.searchQuery);

const localSearchStatus = ref<string | null>(null);

const options = [
    { label: 'Tất cả', value: '' },
    { label: 'Đang diễn ra', value: 'DANG_DIEN_RA' },
    { label: 'Chưa diễn ra', value: 'CHUA_DIEN_RA' },
    { label: 'Đã kết thúc', value: 'DA_DIEN_RA' }
];

watch([localSearchQuery, localSearchStatus], ([newQuery, newStatus]) => {
    emit('update:searchQuery', newQuery);
    emit('update:searchStatus', newStatus);
});

const resetFilters = () => {
    localSearchQuery.value = '';
    emit('update:searchQuery', '');
    localSearchStatus.value = null;
    emit('update:searchStatus', null)
};

</script>
