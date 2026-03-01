<template>
  <a-modal :open="open" :title="props.title" @cancel="closeModal">
    <template #footer>
      <a-popconfirm
        title="Bạn có chắc chắn muốn lưu thay đổi?"
        @confirm="handleSubmit"
        ok-text="Đồng ý"
        cancel-text="Huỷ"
      >
        <a-button type="primary">Xác nhận</a-button>
      </a-popconfirm>
      <a-button @click="closeModal">Huỷ</a-button>
    </template>
    <FormCustom>
      <a-form
        :model="formState"
        ref="staffForm"
        name="staffForm"
        autocomplete="off"
        class="space-y-4 p-4"
      >
        <div class="flex flex-col">
          
          <label class="font-medium mb-1"><span style="color: red">*</span> Cơ sở</label>
          <a-form-item name="idFacilitySelect">
            <a-select v-model:value="formState.idFacilitySelect" placeholder="Chọn cơ sở">
              <a-select-option
                v-for="facility in filteredStaffDetailFacility"
                :key="facility.id"
                :value="facility.id"
              >
                {{ facility.name }}
              </a-select-option>
            </a-select>
          </a-form-item>
          <!-- <label class="font-medium mb-1">
            <span style="color: red">*</span> Cơ sở
          </label>
          <a-select
            v-model:value="idFacilitySelect"
            placeholder="Chọn cơ sở"
            name="facility"
            :rules="rules.facility"
          >
            <a-select-option
              v-for="facility in staffDetailFacility"
              :key="facility.id"
              :value="facility.id"
            >
              {{ facility.nameFacility }}
            </a-select-option>
          </a-select> -->
        </div>

        <div class="flex flex-col">

          <label class="font-medium mb-1"><span style="color: red">*</span> Bộ môn</label>
          <a-form-item name="idDepartmentSelect">
            <a-select
              v-model:value="formState.idDepartmentSelect"
              placeholder="Chọn bộ môn"
              :disabled="!formState.idFacilitySelect"
            >
              <a-select-option
                v-for="item in filteredStaffDetailDepartment"
                :key="item.idDepartment"
                :value="item.idDepartment"
              >
                {{ item.nameDepartment }}
              </a-select-option>
            </a-select>
          </a-form-item>
        </div>

        <div class="flex flex-col">
          <label class="font-medium mb-1"><span style="color: red">*</span> Chuyên ngành</label>
          <a-form-item name="idMajorSelect">
            <a-select
              v-model:value="formState.idMajorSelect"
              placeholder="Chọn chuyên ngành"
              :disabled="!formState.idDepartmentSelect"
            >
              <a-select-option
                v-for="dataMajor in filteredStaffDetailMajor"
                :key="dataMajor.idMajor"
                :value="dataMajor.idMajor"
              >
                {{ dataMajor.nameMajor }}
              </a-select-option>
            </a-select>
          </a-form-item>
        </div>
      </a-form>
    </FormCustom>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, watch, defineProps, defineEmits, onMounted, reactive, computed } from 'vue'
import { toast } from 'vue3-toastify'
import axios from 'axios'
import {
  createStaffFDM,
  getAllStaffDepartment,
  getAllStaffFacility,
  getAllStaffMajor,
  staffByDepartmentMajor,
  updateStaffFDm
} from '@/services/api/admin/staff.api'
import FormCustom from '@/components/custom/Form/FormCustom.vue'
import { useRoute } from 'vue-router'
import { localStorageAction } from '@/utils/storage'
import { USER_INFO_STORAGE_KEY } from '@/constants/storagekey'

const props = defineProps<{
  open: boolean
  staffId: string | null // staffId sẽ là ID của StaffDetail đang được chỉnh sửa
  title: string
}>()
const emit = defineEmits(['close', 'success'])

// Định nghĩa lại kiểu dữ liệu để chắc chắn có trường `status` (chuỗi 'ACTIVE'/'INACTIVE')
// và các ID/Name cần thiết
interface FacilityItem {
  id: string // idFacility
  nameFacility: string
  status?: string // 'ACTIVE', 'INACTIVE'
}

interface DepartmentItem {
  idDepartment: string
  nameDepartment: string
  status?: string // 'ACTIVE', 'INACTIVE'
}

interface MajorItem {
  idMajor: string
  nameMajor: string
  status?: string // 'ACTIVE', 'INACTIVE'
}

// Kiểu dữ liệu cho chi tiết staff khi chỉnh sửa
interface StaffDetailDataResponse {
  idFacility: string
  idDepartment: string
  idMajor: string
  // Thêm các trường khác của staff detail nếu có
}

// Model cho Ant Design Form
const formState = reactive({
  idFacilitySelect: null as string | null,
  idDepartmentSelect: null as string | null,
  idMajorSelect: null as string | null
})

const staffDetailFacility = ref<FacilityItem[]>([])
const staffDetailDepartment = ref<DepartmentItem[]>([])
const staffDetailMajor = ref<MajorItem[]>([])

const initialStaffDetail = ref<StaffDetailDataResponse | null>(null) // Lưu dữ liệu staffDetail ban đầu khi ở chế độ chỉnh sửa

const staffForm = ref()

const router = useRoute()

const isUpdateMode = computed(() => !!props.staffId) // Biến tính toán để kiểm tra chế độ cập nhật

// Rules cho form validation
const rules = {
  idFacilitySelect: [{ required: true, message: 'Vui lòng chọn cơ sở!', trigger: 'change' }],
  idDepartmentSelect: [{ required: true, message: 'Vui lòng chọn bộ môn!', trigger: 'change' }],
  idMajorSelect: [{ required: true, message: 'Vui lòng chọn chuyên ngành!', trigger: 'change' }]
}

onMounted(() => {
  fetchStaffDetailFacility()
})

// --- Computed properties để lọc dữ liệu ---

// Lọc Cơ sở
const filteredStaffDetailFacility = computed(() => {
  if (isUpdateMode.value && initialStaffDetail.value?.idFacility) {
    const currentFacility = staffDetailFacility.value.find(
      (f) => f.id === initialStaffDetail.value?.idFacility
    )
    const activeFacilities = staffDetailFacility.value.filter((f) => f.status === 'ACTIVE')

    const uniqueFacilities = new Map<string, FacilityItem>()
    if (currentFacility) {
      uniqueFacilities.set(currentFacility.id, currentFacility)
    }
    activeFacilities.forEach((f) => {
      if (!uniqueFacilities.has(f.id)) {
        uniqueFacilities.set(f.id, f)
      }
    })
    return Array.from(uniqueFacilities.values())
  } else {
    // Chế độ thêm mới hoặc không có staffId, chỉ hiển thị các cơ sở đang hoạt động
    return staffDetailFacility.value.filter((f) => f.status === 'ACTIVE')
  }
})

// Lọc Bộ môn
const filteredStaffDetailDepartment = computed(() => {
  if (isUpdateMode.value && initialStaffDetail.value?.idDepartment) {
    const currentDepartment = staffDetailDepartment.value.find(
      (d) => d.idDepartment === initialStaffDetail.value?.idDepartment
    )
    const activeDepartments = staffDetailDepartment.value.filter((d) => d.status === 'ACTIVE')

    const uniqueDepartments = new Map<string, DepartmentItem>()
    if (currentDepartment) {
      uniqueDepartments.set(currentDepartment.idDepartment, currentDepartment)
    }
    activeDepartments.forEach((d) => {
      if (!uniqueDepartments.has(d.idDepartment)) {
        uniqueDepartments.set(d.idDepartment, d)
      }
    })
    return Array.from(uniqueDepartments.values())
  } else {
    return staffDetailDepartment.value.filter((d) => d.status === 'ACTIVE')
  }
})

// Lọc Chuyên ngành
const filteredStaffDetailMajor = computed(() => {
  if (isUpdateMode.value && initialStaffDetail.value?.idMajor) {
    const currentMajor = staffDetailMajor.value.find(
      (m) => m.idMajor === initialStaffDetail.value?.idMajor
    )
    const activeMajors = staffDetailMajor.value.filter((m) => m.status === 'ACTIVE')

    const uniqueMajors = new Map<string, MajorItem>()
    if (currentMajor) {
      uniqueMajors.set(currentMajor.idMajor, currentMajor)
    }
    activeMajors.forEach((m) => {
      if (!uniqueMajors.has(m.idMajor)) {
        uniqueMajors.set(m.idMajor, m)
      }
    })
    return Array.from(uniqueMajors.values())
  } else {
    return staffDetailMajor.value.filter((m) => m.status === 'ACTIVE')
  }
})

// --- Fetching functions ---

const fetchStaffDetailFacility = async () => {
  try {
    const response = await getAllStaffFacility()
    // Giả định response.data có chứa trường 'status' (chuỗi 'ACTIVE'/'INACTIVE')
    staffDetailFacility.value = response.data as FacilityItem[]
  } catch (error) {
    if (axios.isAxiosError(error)) {
      if (error?.response?.data?.message) {
        toast.error(error?.response?.data?.message)
      }
    }
  }
}

const fetchStaffDetailDepartment = async (id: string) => {
  try {
    const response = await getAllStaffDepartment(id)
    // Giả định response.data có chứa trường 'status' (chuỗi 'ACTIVE'/'INACTIVE')
    staffDetailDepartment.value = response.data as DepartmentItem[]
  } catch (error) {
    if (axios.isAxiosError(error)) {
      if (error?.response?.data?.message) {
        toast.error(error?.response?.data?.message)
      }
    }
  }
}

const fetchStaffDetailMajor = async (id: string) => {
  try {
    const response = await getAllStaffMajor(id)
    // Giả định response.data có chứa trường 'status' (chuỗi 'ACTIVE'/'INACTIVE')
    staffDetailMajor.value = response.data as MajorItem[]
  } catch (error) {
    if (axios.isAxiosError(error)) {
      if (error?.response?.data?.message) {
        toast.error(error?.response?.data?.message)
      }
    }
  }
}

// --- Watchers for select changes ---

watch(
  () => formState.idFacilitySelect,
  async (newValue, oldValue) => {
    // Luôn reset department và major khi idFacilitySelect thay đổi
    staffDetailDepartment.value = []
    staffDetailMajor.value = []
    formState.idDepartmentSelect = null
    formState.idMajorSelect = null

    if (newValue) {
      await fetchStaffDetailDepartment(newValue)

      // Nếu đang ở chế độ cập nhật và giá trị mới khớp với giá trị ban đầu của Facility
      // thì cố gắng khôi phục giá trị ban đầu của Department
      if (isUpdateMode.value && newValue === initialStaffDetail.value?.idFacility) {
        formState.idDepartmentSelect = initialStaffDetail.value?.idDepartment || null
      }
    }
  },
  { immediate: true } // Chạy ngay khi component mounted để load department ban đầu nếu có
)

watch(
  () => formState.idDepartmentSelect,
  async (newValue, oldValue) => {
    // Luôn reset major khi idDepartmentSelect thay đổi
    staffDetailMajor.value = []
    formState.idMajorSelect = null

    if (newValue) {
      await fetchStaffDetailMajor(newValue)

      // Nếu đang ở chế độ cập nhật và giá trị mới khớp với giá trị ban đầu của Department
      // thì cố gắng khôi phục giá trị ban đầu của Major
      if (isUpdateMode.value && newValue === initialStaffDetail.value?.idDepartment) {
        formState.idMajorSelect = initialStaffDetail.value?.idMajor || null
      }
    }
  },
  { immediate: true }
)

// --- Watcher for props.staffId and props.open ---
watch(
  () => [props.staffId, props.open],
  async ([newId, isOpen]) => {
    if (!isOpen) {
      // Khi modal đóng, reset tất cả
      closeModal()
      return
    }

    if (staffForm.value) {
      staffForm.value.resetFields()
    }

    // ✅ Update mode
    if (newId) {
      try {
        const data = await staffByDepartmentMajor(newId as string)
        initialStaffDetail.value = data.data as StaffDetailDataResponse // Lưu chi tiết ban đầu

        // Gán giá trị cho formState để các watcher tự động tải dữ liệu
        formState.idFacilitySelect = initialStaffDetail.value.idFacility
        // Các watcher cho Department và Major sẽ tự động kích hoạt và gán giá trị
      } catch (error) {
        console.error('Lỗi khi lấy chi tiết nhân viên:', error)
        toast.error('Không thể tải chi tiết nhân viên.')
        closeModal()
      }
    }
    // ✅ Create mode
    else {
      formState.idFacilitySelect = null
      formState.idDepartmentSelect = null
      formState.idMajorSelect = null

      staffDetailDepartment.value = []
      staffDetailMajor.value = []
      initialStaffDetail.value = null // Reset initialStaffDetail khi tạo mới
    }
  },
  { immediate: true } // Chạy ngay khi component được mount
)

const userLogin = localStorageAction.get(USER_INFO_STORAGE_KEY)
const handleSubmit = async () => {
  try {
    await staffForm.value.validate() // Validate form before submitting

    const emailLogin = userLogin.email

    if (props.staffId == null) {
      // Create logic
      const formData = new FormData()
      formData.append('idFacility', formState.idFacilitySelect as string)
      formData.append('idDepartment', formState.idDepartmentSelect as string)
      formData.append('idMajor', formState.idMajorSelect as string)
      formData.append('idStaffDetail', router.params.id as string) // Đây có phải là ID của Staff đang được thêm không?
      formData.append('emailLogin', emailLogin)

      const res = await createStaffFDM(formData)
      toast.success(res.message)
    } else {
      // Update logic
      const formDataUpdate = new FormData()
      // Gửi các giá trị ban đầu và giá trị mới để backend xử lý
      formDataUpdate.append('idFacility', initialStaffDetail.value?.idFacility as string)
      formDataUpdate.append('idDepartment', initialStaffDetail.value?.idDepartment as string)
      formDataUpdate.append('idMajor', initialStaffDetail.value?.idMajor as string)
      formDataUpdate.append('idStaffDetail', router.params.id as string) // ID của Staff đang chỉnh sửa

      formDataUpdate.append('idUpdateFacility', formState.idFacilitySelect as string)
      formDataUpdate.append('idUpdateDepartment', formState.idDepartmentSelect as string)
      formDataUpdate.append('idUpdateMajor', formState.idMajorSelect as string)
      formDataUpdate.append('emailLogin', emailLogin)

      const res = await updateStaffFDm(formDataUpdate)
      toast.success(res.message)
    }
    closeModal()
    emit('success')
  } catch (error: any) {
    if (axios.isAxiosError(error)) {
      if (error?.response?.data?.message) {
        toast.error(error?.response?.data?.message)
      }
    } else {
      console.error('Validation failed or unexpected error:', error)
      toast.error('Vui lòng điền đầy đủ và đúng thông tin.')
    }
  }
}

const closeModal = () => {
  formState.idFacilitySelect = null
  formState.idDepartmentSelect = null
  formState.idMajorSelect = null

  staffDetailDepartment.value = []
  staffDetailMajor.value = []
  initialStaffDetail.value = null // Reset initialStaffDetail

  staffForm.value?.resetFields()
  emit('close')
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
