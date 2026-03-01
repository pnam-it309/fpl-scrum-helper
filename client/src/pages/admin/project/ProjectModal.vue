<template>
  <a-modal
    :open="open"
    :title="props.title"
    @cancel="closeModal"
    :width="'60%'"
    :bodyStyle="{ maxHeight: '70vh', overflowY: 'auto' }"
    centered
  >
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

    <a-form :model="dataCreateProject" ref="formRef" class="p-1" name="formRef" autocomplete="off">
      <a-form-item label="Mã dự án" name="codeProject" :rules="rules.codeProject">
        <a-input v-model:value="dataCreateProject.codeProject" />
      </a-form-item>

      <a-form-item label="Tên dự án" name="nameProject" :rules="rules.nameProject">
        <a-input v-model:value="dataCreateProject.nameProject" />
      </a-form-item>

      <div class="grid grid-cols-3 gap-4">
        <a-form-item
          label="Thời gian bắt đầu"
          name="startTime"
          :label-col="{ span: 24 }"
          :rules="rules.startTime"
        >
          <a-date-picker class="w-full" v-model:value="dataCreateProject.startTime" />
        </a-form-item>

        <a-form-item
          label="Thời gian kết thúc"
          name="endTime"
          :label-col="{ span: 24 }"
          :rules="rules.endTime"
        >
          <a-date-picker class="w-full" v-model:value="dataCreateProject.endTime" />
        </a-form-item>
        <a-form-item
          label="Thể loại"
          name="idCategory"
          :label-col="{ span: 24 }"
          :rules="rules.idCategory"
        >
          <a-select
            v-model:value="dataCreateProject.idCategory"
            placeholder="Chọn thể loại"
            name="major"
          >
            <a-select-option
              v-for="dataCate in state.category"
              :key="dataCate.id"
              :value="dataCate.id"
            >
              {{ dataCate.categoryName }}
            </a-select-option>
          </a-select>
        </a-form-item>
      </div>

      <div class="grid grid-cols-3 gap-4">
        <a-form-item
          label="Cơ sở"
          name="idFacility"
          :label-col="{ span: 24 }"
          :rules="rules.idFacility"
        >
          <a-select
            v-model:value="dataCreateProject.idFacility"
            placeholder="Chọn cơ sở"
            name="idFacility"
          >
            <a-select-option
              v-for="dataCate in filteredDataFacility"
              :key="dataCate.id"
              :value="dataCate.id"
            >
              {{ dataCate.name }}
            </a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item
          label="Chuyên ngành cơ sở"
          :label-col="{ span: 24 }"
          name="id"
          :rules="rules.id"
        >
          <a-select
            v-model:value="dataCreateProject.id"
            placeholder="Chọn bộ môn cơ sở"
            name="id"
            class="w-full"
          >
            <a-select-option
              v-for="df in dataDepartmentFacility"
              class="w-full"
              :key="df.id"
              :value="df.id"
            >
              {{ df.nameMajor + ' - ' + df.nameDepartment }}
            </a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="Loại thành viên" :label-col="{ span: 24 }" name="memberType">
          <a-select
            ref="select"
            v-model:value="valueSelectMember"
            placeholder="Chọn hiện thị"
            class="w-full"
            :disabled="!dataCreateProject.id"
          >
            <a-select-option value="1">Giảng viên</a-select-option>
            <a-select-option value="2">Sinh viên</a-select-option>
            <a-select-option value="3">GV - SV</a-select-option>
          </a-select>
        </a-form-item>
      </div>
      <div class="">
        <a-form-item name="members" class="w-full" label="">
          <label>Danh sách thành viên</label>
          <a-select
            v-model:value="selectedEmails"
            mode="multiple"
            show-search
            placeholder="Chọn thành viên"
            :filter-option="filterOption"
            class="w-full"
            :disabled="!dataCreateProject.id"
          >
            <template v-for="item in filteredSelectOptions" :key="item.email">
              <a-select-option :value="item.email" :label="item.name">
                <div class="flex items-center space-x-2">
                  <img :src="item.image" class="w-8 h-8 rounded-full" />
                  <span class="font-semibold">{{ item.name }}</span>
                  <span>-</span>
                  <span class="font-semibold">{{ item.email }}</span>
                </div>
              </a-select-option>
            </template>
          </a-select>
        </a-form-item>

        <template v-if="tableData.length > 0">
          <a-table :columns="columns" :data-source="tableData" rowKey="email" bordered>
            <template #bodyCell="{ column, record }">
              <template v-if="column.dataIndex == 'image'">
                <a-avatar :src="record.avatar" :alt="record.name" />
              </template>

              <template v-if="column.dataIndex == 'type'">
                <div style="display: flex; align-items: center; gap: 8px">
                  <span>{{ record.type == 'STAFF' ? 'Giảng viên' : 'Sinh viên' }}</span>
                </div>
              </template>

              <template v-if="column.dataIndex === 'role'">
                <a-select
                  class="w-full"
                  v-model:value="record.role"
                  @change="(value) => updateRole(record.email, value)"
                >
                  <template v-if="record.type === 'STAFF'">
                    <a-select-option value="QUAN_LI">Quản lý</a-select-option>
                    <a-select-option value="DEV">Dev</a-select-option>
                    <a-select-option value="TESTER">Tester</a-select-option>
                  </template>

                  <template v-else-if="record.type === 'STUDENT'">
                    <a-select-option value="DEV">Dev</a-select-option>
                    <a-select-option value="TESTER">Tester</a-select-option>
                  </template>
                </a-select>
              </template>
            </template>
          </a-table>
        </template>
      </div>

      <a-form-item label="Mô tả" name="description" :label-col="{ span: 24 }">
        <a-textarea class="w-full" rows="4" v-model:value="dataCreateProject.descriptions" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, watch, defineProps, defineEmits, onMounted, reactive, computed } from 'vue'
import { toast } from 'vue3-toastify'
import axios from 'axios'
import {
  createProject,
  createProjectRequest,
  detailProject,
  facilityResponse,
  getAllFacilityProject,
  getAllPJDepartFacility,
  pjDepartmentFacilityResponse,
  updateProject
} from '@/services/api/admin/project/project.api'
import {
  getAllProjectStaff,
  getAllProjectStudent,
  getAllStaffByProject,
  getAllStaffByStudent,
  projectStudentResponse
} from '@/services/api/admin/project/project.student.api'
import dayjs from 'dayjs'
import {
  CategoryResponse,
  getAllCategory,
  ParamsGetCategory
} from '@/services/api/admin/category/category.api'
import { localStorageAction } from '@/utils/storage'
import { USER_INFO_STORAGE_KEY } from '@/constants/storagekey'

const selectedMembers = ref<{ email: string; role: string; type: 'STAFF' | 'STUDENT' }[]>([])
const userLogin = localStorageAction.get(USER_INFO_STORAGE_KEY)
const emailLogin = userLogin.email

const allPotentialMembers = ref<projectStudentResponse[]>([])

const dataDepartmentFacility = ref<pjDepartmentFacilityResponse[]>([])
const valueSelectMember = ref<string | null>(null)

const id = ref()
const dataCreateProject = reactive({
  nameProject: '',
  codeProject: '',
  startTime: '',
  endTime: '',
  idMajorFacility: '',
  descriptions: '',
  idCategory: null,
  idFacility: null || undefined,
  id: undefined
})

const isStaffMember = (member: projectStudentResponse) => {
  return member.type === 'STAFF'
}

const listMember = ref<
  {
    name: string
    code: string
    email: string
    image: string
    role: string
    type: 'STAFF' | 'STUDENT'
  }[]
>([])

const dataFacility = ref<facilityResponse[]>([]) // Đã thay đổi thành mảng

const fetchDataFacility = async () => {
  const res = await getAllFacilityProject()
  dataFacility.value = res.data as any

  console.log(dataFacility.value)
}

// Thuộc tính tính toán để lọc dataFacility dựa trên trạng thái và chế độ cập nhật/thêm mới
const filteredDataFacility = computed(() => {
  if (isUpdate.value) {
    // Trong chế độ cập nhật, chỉ hiển thị cơ sở hiện tại được chọn và các cơ sở đang hoạt động
    const currentFacility = dataFacility.value.find(
      (facility) => facility.id === props.idFacilityProject
    )
    const activeFacilities = dataFacility.value.filter((facility) => facility.status == 'ACTIVE')

    const uniqueFacilities = new Map()
    if (currentFacility) {
      uniqueFacilities.set(currentFacility.id, currentFacility)
    }
    activeFacilities.forEach((facility) => {
      // Chỉ thêm nếu chưa có trong map (tránh trùng lặp nếu currentFacility cũng đang hoạt động)
      if (!uniqueFacilities.has(facility.id)) {
        uniqueFacilities.set(facility.id, facility)
      }
    })

    return Array.from(uniqueFacilities.values())
  } else {
    // Trong chế độ thêm mới, chỉ hiển thị các cơ sở có status === 0 (đang hoạt động)
    return dataFacility.value.filter((facility) => facility.status === 'ACTIVE')
  }
})

const selectedEmails = computed({
  get: () => selectedMembers.value.map((member) => member.email),
  set: (emails: string[]) => {
    selectedMembers.value = emails.map((email) => {
      const existingMember = selectedMembers.value.find((m) => m.email === email)
      if (existingMember) {
        return existingMember
      } else {
        const newMemberDetail = allPotentialMembers.value.find((m) => m.email === email)
        let defaultRole: 'QUAN_LI' | 'DEV' | 'TESTER' = 'DEV'
        if (newMemberDetail) {
          defaultRole = newMemberDetail.type === 'STAFF' ? 'QUAN_LI' : 'DEV'
        }
        return { email, role: defaultRole, type: newMemberDetail?.type || 'STUDENT' }
      }
    })
    updateListMember()
  }
})

const updateRole = (email: string, newRole: string) => {
  selectedMembers.value = selectedMembers.value.map((member) =>
    member.email === email ? { ...member, role: newRole } : member
  )
  updateListMember()
}

const filterOption = (input: string, option: any) => {
  return (
    option?.value?.toLowerCase().includes(input.toLowerCase()) ||
    option?.label?.toLowerCase().includes(input.toLowerCase())
  )
}

var idMajorFacilityDefault: string = ''

watch(
  () => dataCreateProject.idFacility,
  async (newValue) => {
    if (!newValue) {
      dataDepartmentFacility.value = []
      allPotentialMembers.value = []
      clearMajorFacilityOption()
      clearMemberOption()
      return
    }

    try {
      const res = await getAllPJDepartFacility(newValue as string)
      dataDepartmentFacility.value = res.data || []

      if (props.idFacilityProject && props.idFacilityProject === newValue) {
        const defaultDepartment = dataDepartmentFacility.value.find(
          (department) => department.id === idMajorFacilityDefault
        )
        dataCreateProject.id = defaultDepartment ? defaultDepartment.id : undefined
      } else {
        dataCreateProject.id = undefined
        clearMemberOption()
      }
    } catch (error) {
      console.error('Lỗi khi lấy danh sách bộ môn theo cơ sở:', error)
      dataDepartmentFacility.value = []
    }
  }
)

const tableData = computed(() => {
  return selectedMembers.value
    .map((selected) => {
      const foundMember = allPotentialMembers.value.find((m) => m.email === selected.email)
      if (foundMember) {
        return {
          label: foundMember.name,
          email: foundMember.email,
          avatar: foundMember.image,
          role: selected.role,
          type: foundMember.type
        }
      }
      return null
    })
    .filter(Boolean)
})

const filteredSelectOptions = computed(() => {
  let filtered: projectStudentResponse[] = []
  if (valueSelectMember.value === '1') {
    filtered = allPotentialMembers.value.filter((member) => member.type === 'STAFF')
  } else if (valueSelectMember.value === '2') {
    filtered = allPotentialMembers.value.filter((member) => member.type === 'STUDENT')
  } else {
    filtered = allPotentialMembers.value
  }

  const alreadySelectedEmails = new Set(selectedEmails.value)
  return filtered.filter((member) => !alreadySelectedEmails.has(member.email))
})

const updateListMember = () => {
  const newListMember: typeof listMember.value = []
  selectedMembers.value.forEach((selected) => {
    const memberDetail = allPotentialMembers.value.find((m) => m.email === selected.email)
    if (memberDetail) {
      newListMember.push({
        name: memberDetail.name,
        code: memberDetail.code || '',
        email: memberDetail.email,
        image: memberDetail.image || 'default.png',
        role: selected.role,
        type: memberDetail.type
      })
    }
  })
  listMember.value = newListMember
  console.log('Danh sách thành viên (listMember) sau cập nhật:', listMember.value)
}

watch(
  () => dataCreateProject.id,
  async (newMajorId) => {
    if (!newMajorId) {
      listMember.value = []
      return
    }

    try {
      const [resStaff, resStudent] = await Promise.all([
        getAllProjectStaff(newMajorId as string),
        getAllProjectStudent(newMajorId as string)
      ])

      const staffMembers = (resStaff.data || []).map((m) => ({ ...m, type: 'STAFF' as 'STAFF' }))
      const studentMembers = (resStudent.data || []).map((m) => ({
        ...m,
        type: 'STUDENT' as 'STUDENT'
      }))

      allPotentialMembers.value = [...staffMembers, ...studentMembers]
      console.log('Tất cả thành viên tiềm năng đã tải:', allPotentialMembers.value)

      const updatedSelectedMembers = selectedMembers.value.filter((selected) =>
        allPotentialMembers.value.some((m) => m.email === selected.email)
      )

      selectedMembers.value = updatedSelectedMembers.map((selected) => {
        const memberInfo = allPotentialMembers.value.find((m) => m.email === selected.email)
        return {
          ...selected,
          role: selected.role || (memberInfo && memberInfo.type === 'STAFF' ? 'QUAN_LI' : 'DEV'),
          type: memberInfo?.type || 'STUDENT'
        }
      })

      updateListMember()

      if (!valueSelectMember.value && allPotentialMembers.value.length > 0) {
        const hasStaff = allPotentialMembers.value.some((m) => m.type === 'STAFF')
        const hasStudent = allPotentialMembers.value.some((m) => m.type === 'STUDENT')

        if (hasStaff && hasStudent) {
          valueSelectMember.value = '3'
        } else if (hasStaff) {
          valueSelectMember.value = '1'
        } else if (hasStudent) {
          valueSelectMember.value = '2'
        } else {
          valueSelectMember.value = null
        }
      } else if (!allPotentialMembers.value.length) {
        valueSelectMember.value = null
      }
    } catch (error) {
      console.error('Lỗi khi lấy dữ liệu thành viên theo bộ môn cơ sở:', error)
      allPotentialMembers.value = []
    }
  },
  { immediate: true }
)

onMounted(() => {
  fecthCategory()
  fetchDataFacility()
})

const isUpdate = computed(() => !!props.projectId)

const props = defineProps<{
  open: boolean
  projectId: string | null
  title: string
  idFacilityProject: string
}>()
const emit = defineEmits(['close', 'success'])

const rules = {
  codeProject: [
    { required: true, message: 'Mã không được để trống!', trigger: 'blur' },
    {
      min: 3,
      max: 50,
      message: 'Mã phải từ 3 đến 50 ký tự!',
      trigger: 'blur'
    },
    {
      validator: (_: any, value: string, callback: (error?: Error) => void) => {
        const trimmed = value?.trim()
        const regex = /^[a-zA-Z0-9_-]+$/
        if (!regex.test(trimmed)) {
          callback(
            new Error(
              'Mã chỉ được chứa chữ cái, số, dấu gạch dưới (_) hoặc dấu gạch ngang (-), không chứa khoảng trắng!'
            )
          )
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],

  nameProject: [
    {
      required: true,
      message: 'Tên không được để trống!',
      trigger: 'blur'
    },
    {
      validator: (_: any, value: string, callback: (error?: Error) => void) => {
        const trimmed = value?.trim() || ''
        if (trimmed.length < 3 || trimmed.length > 100) {
          callback(new Error('Tên phải từ 3 đến 100 ký tự!'))
        } else if (!/^[a-zA-ZÀ-Ỹà-ỹ0-9 _-]+$/.test(trimmed)) {
          callback(new Error('Tên chỉ được chứa chữ cái (có dấu), số, khoảng trắng, dấu _ hoặc -!'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],

  startTime: [
    { required: true, message: 'Thời gian bắt đầu không được để trống!', trigger: 'blur' },
    {
      validator: (_: any, value: any, callback: (error?: Error) => void) => {
        const selectedStart = new Date(value)
        selectedStart.setHours(0, 0, 0, 0)

        const today = new Date()
        today.setHours(0, 0, 0, 0)

        if (!isUpdate.value && selectedStart < today) {
          callback(new Error('Thời gian bắt đầu phải từ hôm nay trở đi!'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ],

  endTime: [
    { required: true, message: 'Thời gian kết thúc không được để trống!', trigger: 'blur' },
    {
      validator: (_: any, value: any, callback: (error?: Error) => void) => {
        const start = new Date(dataCreateProject.startTime as string)
        const end = new Date(value)
        if (end < start) {
          callback(new Error('Thời gian kết thúc không được nhỏ hơn thời gian bắt đầu!'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ],

  idCategory: [{ required: true, message: 'Thể loại không được để trống!', trigger: 'blur' }],

  idFacility: [{ required: true, message: 'Vui lòng chọn cơ sở!', trigger: ['change', 'blur'] }],
  id: [
    {
      required: true,
      message: 'Chuyên ngành bộ môn không được để trống!',
      trigger: ['change', 'blur']
    }
  ]
}

const formRef = ref<any>()

const handleSubmit = async () => {
  try {
    await formRef.value.validate()

    const newProject: createProjectRequest = {
      nameProject: dataCreateProject.nameProject,
      codeProject: dataCreateProject.codeProject,
      idMajorFacility: dataCreateProject.id as string,
      descriptions: dataCreateProject.descriptions,
      startTime: dayjs(dataCreateProject.startTime).valueOf() as unknown as string,
      endTime: dayjs(dataCreateProject.endTime).valueOf() as unknown as string,
      idCategory: dataCreateProject.idCategory as string,
      listMembers: listMember.value.map((member) => ({
        email: member.email,
        role: member.role
      })),
      emailLogin: emailLogin
    }

    if (props.projectId == null) {
      const res = await createProject(newProject)
      toast.success(res.message)
      closeModal()
      emit('success')
    } else {
      try {
        const res = await updateProject(props.projectId, newProject)
        closeModal()
        emit('success')
        toast.success(res.message)
      } catch (error: any) {
        toast.error(error?.response?.data?.message)
      }
    }
  } catch (error: any) {
    if (axios.isAxiosError(error)) {
      if (error?.response?.data?.message) {
        toast.error(error?.response?.data?.message)
      }
    } else {
      console.error('Validation failed or unexpected error:', error)
    }
  }
}
const state = reactive({
  searchQuery: '',
  categoryName: null as string | null,
  categoryStatus: null as number | null,
  isModalOpen: false,
  id: null as string | null,
  category: [] as CategoryResponse[],
  paginationParams: { page: 1, size: 10 },
  totalItems: 0
})

const fecthCategory = async (currentCategoryId: string | null = null) => {
  const params: ParamsGetCategory = {
    page: 1,
    size: 1000,
    categoryName: '',
    categoryStatus: null as unknown as string
  }
  const response = await getAllCategory(params)
  let categories = response.data.data

  if (currentCategoryId) {
    const selectedCategory = categories.find((cat) => cat.id === currentCategoryId)
    const activeCategories = categories.filter((cat) => cat.categoryStatus === 0)

    state.category = []
    if (selectedCategory) {
      state.category.push(selectedCategory)
    }
    activeCategories.forEach((cat) => {
      if (!state.category.some((existingCat) => existingCat.id === cat.id)) {
        state.category.push(cat)
      }
    })
  } else {
    state.category = categories.filter((cat) => cat.categoryStatus === 0)
  }
}

const closeModal = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }

  allPotentialMembers.value = []
  selectedMembers.value = []
  listMember.value = []
  dataCreateProject.idFacility = null
  dataCreateProject.idCategory = null
  valueSelectMember.value = null

  Object.assign(dataCreateProject, {
    nameProject: '',
    codeProject: '',
    startTime: '',
    endTime: '',
    idMajorFacility: '',
    descriptions: '',
    idCategory: null,
    idFacility: null,
    id: null
  })

  emit('close')
}

const fetchProjectDetails = async (idProject: string) => {
  try {
    const response = await detailProject(idProject)
    const resStaff = await getAllStaffByProject(idProject)
    const resStudent = await getAllStaffByStudent(idProject)

    const staffMembers = (resStaff.data || []).map((m) => ({ ...m, type: 'STAFF' as 'STAFF' }))
    const studentMembers = (resStudent.data || []).map((m) => ({
      ...m,
      type: 'STUDENT' as 'STUDENT'
    }))

    allPotentialMembers.value = [...staffMembers, ...studentMembers]

    fecthCategory(response.data.category.id)

    dataCreateProject.codeProject = response.data.code
    dataCreateProject.nameProject = response.data.name
    dataCreateProject.descriptions = response.data.descriptions
    dataCreateProject.endTime = dayjs(response.data.endTime)
    dataCreateProject.startTime = dayjs(response.data.startTime)
    dataCreateProject.idCategory = response.data.category.id
    dataCreateProject.id = response.data.majorFacility.id

    idMajorFacilityDefault = response.data.majorFacility.id

    selectedMembers.value = allPotentialMembers.value.map((member) => ({
      email: member.email,
      role: member.role ?? (member.type === 'STAFF' ? 'QUAN_LI' : 'DEV'),
      type: member.type
    }))

    updateListMember()

    const hasStaff = allPotentialMembers.value.some((m) => m.type === 'STAFF')
    const hasStudent = allPotentialMembers.value.some((m) => m.type === 'STUDENT')

    if (hasStaff && hasStudent) {
      valueSelectMember.value = '3'
    } else if (hasStaff) {
      valueSelectMember.value = '1'
    } else if (hasStudent) {
      valueSelectMember.value = '2'
    } else {
      valueSelectMember.value = null
    }
  } catch (error: any) {
    if (axios.isAxiosError(error)) {
      if (error?.response?.data?.message) {
        toast.error(error?.response?.data?.message)
      }
    }
  }
}

watch(
  () => [props.projectId, props.open],
  async ([newId, isOpen]) => {
    if (isOpen) {
      if (formRef.value) {
        formRef.value.resetFields()
      }
      if (newId) {
        dataCreateProject.idFacility = props.idFacilityProject
        await fetchProjectDetails(newId as string)
      } else {
        allPotentialMembers.value = []
        selectedMembers.value = []
        listMember.value = []
        dataCreateProject.idFacility = null
        dataCreateProject.idCategory = null
        dataCreateProject.id = null
        valueSelectMember.value = null
        await fecthCategory()
      }
    }
  },
  { immediate: true }
)

const columns = [
  { title: 'Avatar', dataIndex: 'image', key: 'image', width: 30, ellipsis: true },
  { title: 'Tên thành viên', dataIndex: 'label', key: 'label', width: 100, ellipsis: true },
  { title: 'Thể loại thành viên', dataIndex: 'type', key: 'type', width: 100, ellipsis: true },
  { title: 'Email', dataIndex: 'email', key: 'email', width: 100, ellipsis: true },
  { title: 'Vai trò', dataIndex: 'role', key: 'role', width: 60, ellipsis: true }
]

const clearMajorFacilityOption = () => {
  dataCreateProject.id = undefined
  valueSelectMember.value = null
  id.value = ''
  allPotentialMembers.value = []
  selectedMembers.value = []
  listMember.value = []
}

const clearMemberOption = () => {
  valueSelectMember.value = null
  // No longer clearing selectedMembers or listMember here.
  // The new watch on `valueSelectMember` handles removal of invalid selections.
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
