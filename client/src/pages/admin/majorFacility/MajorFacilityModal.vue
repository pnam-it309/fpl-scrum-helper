<template>
    <a-modal :open="open" :title="title" @cancel="closeModal" style="top: 20px">
        <template #footer>
            <a-popconfirm title="Bạn có chắc chắn muốn lưu thay đổi?" @confirm="handleSubmit" ok-text="Đồng ý" cancel-text="Huỷ">
                <a-button type="primary">Xác nhận</a-button>
            </a-popconfirm>
            <a-button @click="closeModal">Huỷ</a-button>
        </template>

        <a-form :model="majorFacility" ref="majorFacilityForm" name="majorFacilityForm" autocomplete="off">
            <a-form-item label="Chuyên ngành" name="majorId">
                <a-select 
                    v-model:value="majorFacility.majorId"
                    :options="majorOptions"
                    placeholder="Chọn chuyên ngành"
                    show-search
                />
            </a-form-item>
        </a-form>
    </a-modal>
</template>

<script setup lang="ts">
import { ref, watch, defineProps, defineEmits, onMounted } from 'vue'
import { getDFListMajorByDepartmentId, detailMajorFacility, addMajorFacility, updateMajorFacility } from '@/services/api/majorFacility.api'
import { toast } from 'vue3-toastify'
import { useRoute } from 'vue-router';

const props = defineProps<{ 
    open: boolean; 
    majorFacilityId: string | null; 
    title: string;
    departmentFacilityId: string | null;
}>();

const departmentFacilityId = ref<string | null>(props.departmentFacilityId || null);

watch(() => props.departmentFacilityId, (newId) => {
    departmentFacilityId.value = newId;
});

const emit = defineEmits(['close', 'success'])

const route = useRoute()
const departmentId = ref<string>(route.query.departmentId as string || '')

const majorFacility = ref({
    majorFacilityId: '',
    majorId: '',
})

const majorFacilityForm = ref()
const majorOptions = ref<{ label: string; value: string }[]>([])

const fetchMajors = async () => {
    try {
        if (!departmentId.value) {
            return;
        }

        const response = await getDFListMajorByDepartmentId(departmentId.value);
            
        majorOptions.value = response.data.map(major => ({
            label: major.majorName,
            value: major.id ?? major.majorId,
        }));
    } catch (error) {
        console.error('Lỗi khi lấy danh sách chuyên ngành:', error);
    }
};

const fetchMajorFacilityDetails = async (id: string) => {
    try {
        if (!id) {
            return;
        }

        const response = await detailMajorFacility(id);
        majorFacility.value = {
            majorFacilityId: response.data.majorFacilityId,
            majorId: response.data.majorId,
        };
    } catch (error) {
        console.error('Lỗi khi lấy thông tin chuyên ngành:', error);
    }
};


watch(
    () => props.majorFacilityId,
    async (newId) => {
        if (newId) {
            await fetchMajorFacilityDetails(newId);
        } else {
            majorFacility.value = { majorFacilityId: '', majorId: '' };
        }
    },
    { immediate: true }
)

const closeModal = () => emit('close');

const handleSubmit = async () => {
    try {
        if (!majorFacilityForm.value) {
            console.error("🔥 Lỗi: majorFacilityForm chưa được khởi tạo!");
            return;
        }

        await majorFacilityForm.value.validate();

        if (!departmentId.value) {
            toast.error('Thiếu departmentId, vui lòng kiểm tra lại!');
            return;
        }

        const formData = {
            departmentId: departmentId.value,
            majorId: majorFacility.value.majorId,
            departmentFacilityId: departmentFacilityId.value ?? ""
        };

        if (props.majorFacilityId) {
            await updateMajorFacility(formData, props.majorFacilityId);
            toast.success('Cập nhật thành công!')
        } else {
            await addMajorFacility(formData);
            toast.success('Thêm thành công!')
        }

        closeModal();
        emit('success');
    } catch (error) {
        if (props.majorFacilityId) {
      toast.error(error.response?.data?.message || error.message || 'Cập nhật thất bại')
      } else {
       toast.error(error.response?.data?.message || error.message || 'Thêm thất bại')
      }
    }
};

onMounted(fetchMajors);
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