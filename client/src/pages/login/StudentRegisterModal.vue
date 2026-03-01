<template>
    <a-modal :open="isOpen" title="Đăng Ký Tài Khoản Sinh Viên" @cancel="closeModal" :maskClosable="false"
        :centered="true">
        <template #footer>
            <a-button @click="closeModal">Hủy</a-button>
            <a-button type="primary" :loading="loading" @click="handleRegister">Đăng Ký</a-button>
        </template>

        <a-form :model="student" ref="registerForm" name="registerForm" layout="vertical">
            <a-form-item label="Mã sinh viên" name="code" :rules="rules.code">
                <a-input v-model:value="student.code" placeholder="Mã sinh viên" />
            </a-form-item>

            <a-form-item label="Họ và Tên" name="fullName" :rules="rules.fullName">
                <a-input v-model:value="student.fullName" placeholder="Nhập họ và tên" />
            </a-form-item>

            <a-form-item label="Email" name="email" :rules="rules.email">
                <a-input v-model:value="student.email" placeholder="Nhập email" />
            </a-form-item>

            <a-form-item label="Chọn cơ sở" name="facility" :rules="rules.facility">
                <a-select v-model:value="student.facility" placeholder="Chọn cơ sở">
                    <a-select-option v-for="facility in studentDetailFacility" :key="facility.id" :value="facility.id">
                        {{ facility.nameFacility }}
                    </a-select-option>
                </a-select>
            </a-form-item>

            <a-form-item label="Chọn bộ môn" name="department" :rules="rules.department">
                <a-select v-model:value="student.department" placeholder="Chọn bộ môn">
                    <a-select-option v-for="item in studentDetailDepartment" :key="item.idDepartment" :value="item.idDepartment">
                        {{ item.nameDepartment }}
                    </a-select-option>
                </a-select>
            </a-form-item>

            <a-form-item label="Chọn chuyên ngành" name="major" :rules="rules.major">
                <a-select v-model:value="student.major" placeholder="Chọn chuyên ngành">
                    <a-select-option v-for="dataMajor in studentDetailMajor" :key="dataMajor.id" :value="dataMajor.idMajor">
                        {{ dataMajor.nameMajor }}
                    </a-select-option>
                </a-select>
            </a-form-item>
        </a-form>
    </a-modal>
</template>

<script setup lang="ts">
import { ref, onMounted, defineProps, defineEmits, watch } from 'vue'
import { toast } from 'vue3-toastify'
import axios from 'axios'
import { getAllStaffFacility, getAllStaffDepartment, getAllStaffMajor, staffDetailIdResponse } from '@/services/api/admin/staff.api'
import { studentRegister } from '@/services/api/permitall/register/student-register.api'

const props = defineProps<{ isOpen: boolean }>()
const emit = defineEmits(['close', 'success'])

const registerForm = ref()
const studentDetailFacility = ref<staffDetailIdResponse[]>([])
const studentDetailDepartment = ref<staffDetailIdResponse[]>()
const studentDetailMajor = ref<staffDetailIdResponse[]>()
const loading = ref(false)

const student = ref({
    code: '',
    fullName: '',
    email: '',
    facility: '',  
    department: '', 
    major: ''
})

const rules = {
    code: [{ required: true, message: 'Mã sinh viên không được để trống!', trigger: 'blur' }],
    fullName: [{ required: true, message: 'Họ và tên không được để trống!', trigger: 'blur' }],
    email: [{ required: true, type: 'email', message: 'Email không hợp lệ!', trigger: 'blur' }],
    facility: [{ required: true, message: 'Vui lòng chọn cơ sở!', trigger: 'change' }],
    department: [{ required: true, message: 'Vui lòng chọn bộ môn!', trigger: 'change' }],
    major: [{ required: true, message: 'Vui lòng chọn chuyên ngành!', trigger: 'change' }]
}

onMounted(fetchFacilities)

async function fetchFacilities() {
    try {
        const response = await getAllStaffFacility()
        studentDetailFacility.value = response.data as [] 
    } catch (error) {
        handleAxiosError(error)
    }
}

async function fetchDepartment(id: string) {
    student.value.department = ''
    studentDetailDepartment.value = []
    try {
        const response = await getAllStaffDepartment(id)
        studentDetailDepartment.value = response.data as [] 
    } catch (error) {
        handleAxiosError(error)
    }
}

async function fetchMajor(id: string) {
    student.value.major = ''
    studentDetailMajor.value = []
    try {
        const response = await getAllStaffMajor(id)
        studentDetailMajor.value = response.data as [] 
    } catch (error) {
        handleAxiosError(error)
    }
}


watch(() => student.value.facility, (newValue) => {
    if (newValue) fetchDepartment(newValue)
})

watch(() => student.value.department, (newValue) => {
    if (newValue) fetchMajor(newValue)
})

const handleRegister = async () => {
    try {
        loading.value = true
      
        await registerForm.value.validate()
       
        const formData = new FormData()
        formData.append('idFacility', student.value.facility)
        formData.append('idDepartment', student.value.department)
        formData.append('idMajor', student.value.major)
        formData.append('code', student.value.code)
        formData.append('name', student.value.fullName)
        formData.append('email', student.value.email)

        const res = await studentRegister(formData)
        toast.success(res.message)
        emit('success')
        closeModal()
    } catch (error) {
        handleAxiosError(error)
    } finally {
        loading.value = false
    }
}

const closeModal = () => emit('close')

const handleAxiosError = (error: any) => {
    if (axios.isAxiosError(error) && error.response?.data?.message) {
        toast.error(error.response.data.message)
    } else {
        toast.error('Đã xảy ra lỗi, vui lòng thử lại!')
    }
}
</script>
