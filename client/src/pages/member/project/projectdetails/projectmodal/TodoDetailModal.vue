<template>
  <a-modal :visible="visible" @cancel="handleClose" @update:visible="handleVisibleChange" :footer="null" :zIndex="9999"
    :destroyOnClose="true" width="900px" :disabled="disabledModal()">
    <div v-if="props.disabled" class="modal-overlay"></div>
    <!-- Ảnh bìa -->
    <a v-if="meAllDetailTodo?.urlImage" :href="meAllDetailTodo.urlImage" target="_blank">
      <div class="h-40 w-full bg-contain bg-center bg-no-repeat rounded-lg"
        :style="{ backgroundImage: `url('${meAllDetailTodo.urlImage}')` }">
      </div>
    </a>

    <div class="flex flex-col md:flex-row gap-6">
      <!-- Menu bên trái -->
      <div class="w-full md:w-2/3">
        <!-- Tên thẻ có thể chỉnh sửa -->
        <div class="text-base font-semibold mb-2" :class="{ 'mt-4': meAllDetailTodo?.urlImage }">
          <div v-if="!isEditingTitle" @click="startEditingTitle"
            class="flex items-center gap-2 cursor-pointer hover:bg-gray-100 rounded">

            <component :is="getTypeIcon(meAllDetailTodo?.type)" class="text-lg"
              :style="{ color: getTypeColor(meAllDetailTodo?.type) }" />

            <span :style="{ color: getTypeColor(meAllDetailTodo?.type) }">
              {{ meAllDetailTodo?.name || 'Không có tiêu đề' }}
            </span>
          </div>

          <!-- Khi edit -->
          <div v-else class="flex items-center gap-2">
            <component :is="getTypeIcon(meAllDetailTodo?.type)" class="text-lg"
              :style="{ color: getTypeColor(meAllDetailTodo?.type) }" />
            <a-input v-model:value="editedTitle" size="large" @pressEnter="saveTitle" @blur="saveTitle" class="flex-1"
              :style="{ color: getTypeColor(meAllDetailTodo?.type), borderColor: getTypeColor(meAllDetailTodo?.type) }"
              placeholder="Nhập tiêu đề thẻ" />
          </div>


        </div>
        <!-- Trong danh sách  -->

        <!-- T -->
        <div class="relative w-full overflow-x-auto scrollbar-hide">
          <div class="flex items-center gap-1 whitespace-nowrap py-3 " style="min-width: max-content;">
            <template v-for="(list, index) in todoListByPhase" :key="list.id">
              <!-- Block trạng thái -->
              <div
                class="px-3 py-1 rounded-full text-sm flex items-center font-medium transition-all duration-150 shadow-sm border cursor-pointer"
                :class="list.id === meAllDetailTodo?.todoListId
                  ? 'bg-cyan-700 text-white border-cyan-800'
                  : 'bg-white text-gray-800 border-gray-300 hover:bg-gray-100 hover:text-black'"
                @click="handleSelectPhase(list)">
                {{ list.name }}
              </div>

              <!-- Mũi tên phân tách -->
              <div v-if="index !== todoListByPhase.length - 1" class="text-gray-300 font-bold px-1">
                ➤
              </div>
            </template>
          </div>
        </div>


        <!-- -->
        <div class="flex items-start gap-6 mb-4">
          <!-- Thành viên -->
          <div>
            <div class="font-medium mb-1">Thành viên:</div>
            <div class="flex items-center flex-wrap gap-2">
              <a-tooltip :zIndex="100001" v-for="member in meAllDetailTodo?.members" :key="member.id"
                :title="member.email">
                <a-avatar
                  :src="member.image || 'https://cdn.sforum.vn/sforum/wp-content/uploads/2023/10/avatar-trang-4.jpg'"
                  :alt="member.name" />

              </a-tooltip>
              <a-button shape="circle" icon="+" @click="openMemberModal" />
              <TodoChildMemberModal v-model="showAddMemberModal" @close="showAddMemberModal = false" />
            </div>
          </div>

          <!-- Nhãn -->
          <div>
            <div class="font-medium mb-1">Nhãn:</div>
            <div class="flex flex-wrap gap-2 items-center">
              <!-- Hiển thị danh sách nhãn -->
              <a-tag v-for="label in meAllDetailTodo?.labels" :key="label.id" :style="{
                backgroundColor: `${label.colorLabel}`,
                color: getTextColor(label.colorLabel)
              }" class="cursor-pointer" @click="openLabelModal('edit')">
                {{ label.name }}
              </a-tag>

              <!-- Nút thêm nhãn -->
              <a-button shape="circle" @click="openLabelModal('create')">+</a-button>
            </div>

            <!-- ModalLabel component -->
            <TodoLabelModal :visible="isLabelModalVisible" :labelData="selectedLabel" :mode="modalMode"
              @close="isLabelModalVisible = false" @save="handleSaveLabel" @delete="handleDeleteLabel" />
          </div>
        </div>

        <!-- Thông tin ngày hạn, ngày hoàn thành, độ ưu tiên -->
        <div class="grid grid-cols-1 gap-3 mt-4 mb-4">
          <!-- Độ ưu tiên -->
          <div class="flex font-medium items-center gap-2"
            v-if="meAllDetailTodo?.priorityLevel !== null && meAllDetailTodo?.priorityLevel !== undefined">
            <span @click="showPriorityModal = true">Sự ưu tiên:</span>
            <a-tag :color="getPriorityColor(meAllDetailTodo?.priorityLevel)"
              class="text-xs cursor-pointer flex items-center gap-1" @click="showPriorityModal = true">
              <!-- Icon động -->
              <component :is="getPriorityIcon(meAllDetailTodo?.priorityLevel)" />
              {{ getPriorityText(meAllDetailTodo?.priorityLevel) }}
            </a-tag>
          </div>

          <!-- Loại công việc -->
          <div class="flex items-center gap-2 font-medium"
            v-if="meAllDetailTodo?.type !== null && meAllDetailTodo?.type !== undefined">
            <span @click="showModal = true">Kiểu công việc:</span>

            <a-tag :color="getTypeColor(meAllDetailTodo?.type)" class="text-xs cursor-pointer"
              @click="showModal = true">
              <component :is="getTypeIcon(meAllDetailTodo?.type)" class="mr-1" />
              {{ meAllDetailTodo?.type }}
            </a-tag>
          </div>

          <!-- Ngày hạn -->
          <div class="flex items-center gap-2 font-medium" v-if="meAllDetailTodo?.deadline">
            <span @click="showModalTodoDeadline = true">Ngày hạn:</span>
            <a-checkbox v-model:checked="isCompletedTime"> </a-checkbox>
            <a-tag color="blue" class="text-xs" @click="showModalTodoDeadline = true">
              {{ getDateFormat(meAllDetailTodo?.deadline, true) }}
            </a-tag>
          </div>

          <!-- Ngày hoàn thành -->
          <div class="flex items-center gap-2 font-medium" v-if="meAllDetailTodo?.completionTime">
            <span>Ngày hoàn thành:</span>
            <a-tag color="green" class="text-xs">
              {{ getDateFormat(meAllDetailTodo?.completionTime, true) }}
            </a-tag>
            <!-- Trạng thái -->
            <a-tag :style="{
              backgroundColor: getStatusTodoColor(meAllDetailTodo?.statusTodo),
              color: 'white'
            }" class="ml-2 text-xs">
              {{ getStatusTodoText(meAllDetailTodo?.statusTodo) }}
            </a-tag>
          </div>
        </div>

        <!-- Mô tả  -->
        <div class="mb-4">
          <h3 class="font-semibold text-base flex items-center gap-2 mb-2">
            <MenuOutlined class="text-lg text-gray-700" />
            <span>Mô tả</span>
          </h3>
          <div v-if="!isEditingDescription" class="cursor-pointer hover:bg-gray-100 p-2 rounded"
            @click="editDescription">
            <div v-if="meAllDetailTodo?.descriptions" class="whitespace-pre-wrap">
              {{ meAllDetailTodo.descriptions }}
            </div>
            <div v-else class="text-gray-400 italic">Thêm mô tả...</div>
          </div>
          <div v-else>
            <a-textarea v-model:value="editedDescription" :rows="5" class="mb-2" placeholder="Nhập mô tả chi tiết..." />
            <div class="flex space-x-2">
              <a-button type="primary" @click="saveDescription">Lưu</a-button>
              <a-button @click="cancelEditDescription">Hủy</a-button>
            </div>
          </div>
        </div>

        <!-- Link đính kèm -->
        <div class="mb-5">
          <div v-if="meAllResourceTodo?.length" class="mt-4">
            <h3 class="font-semibold text-base flex items-center mb-3 text-gray-800">
              <LinkOutlined class="text-xl text-gray-700" />
              <span class="ml-2">Link đính kèm</span>
            </h3>

            <div class="space-y-3">
              <div v-for="rs in meAllResourceTodo" :key="rs.id"
                class="flex items-center bg-white rounded-lg p-4 border border-gray-200">
                <!-- Ảnh xem trước -->
                <a :href="rs.url" target="_blank" class="flex-shrink-0">
                  <img :src="getPreviewImage(rs.url)" alt="Preview"
                    class="w-14 h-14 object-cover rounded-md border border-gray-300" />
                </a>

                <!-- Nội dung -->
                <div class="ml-4 flex-1">
                  <p class="font-medium text-gray-800 truncate">
                    <a :href="rs.url" target="_blank" class="hover:text-blue-500 transition">
                      {{ rs.name || rs.url }}
                    </a>
                  </p>
                  <p class="text-sm text-gray-500">Ngày tạo: {{ getDateFormat(rs.createdDate) }}</p>
                </div>

                <!-- Nút Chỉnh sửa & Xóa -->
                <div class="flex items-center gap-2">
                  <button @click.stop="editResource(rs)" class="text-gray-700 hover:text-blue-600 text-sm transition">
                    Chỉnh sửa
                  </button>
                  <span class="text-gray-400">|</span>
                  <a-popconfirm :zIndex="10000" title="Bạn có chắc chắn muốn xóa tài nguyên này?" okText="Có"
                    cancelText="Không" @confirm.stop="deleteResource(rs)">
                    <button class="text-gray-700 hover:text-red-500 text-sm transition">Xóa</button>
                  </a-popconfirm>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Việc cần làm -->
        <TodoChild :todoChild="todoChild" :idTodo="localStorageAction.get('idTodo')"
          :progress="meAllDetailTodo?.progress" />

        <!-- Hình ảnh -->
        <div v-if="meAllDetailTodo?.images?.length">
          <!-- Danh sách ảnh -->
          <h2 class="flex items-center text-lg font-semibold mb-4">
            <PictureOutlined class="text-xl mr-2" /> Hình ảnh
          </h2>
          <div v-for="image in meAllDetailTodo?.images" :key="image.id"
            class="flex items-center bg-gray-100 rounded-lg p-3 mb-2 ">
            <img :src="image.urlImage" :alt="image.nameImage" class="w-20 h-20 rounded-lg mr-4 cursor-pointer"
              @click="openModalImage(image)" />
            <div class="flex-1">
              <p class="font-semibold break-words whitespace-normal">
                {{ image.nameImage }}
              </p>

              <p class="text-sm text-gray-500">Ngày tạo: {{ getDateFormat(image.createdDate) }}</p>
              <div class="flex space-x-2 mt-1 flex-wrap md:flex-nowrap">
                <a-button type="link" class="text-black hover:text-blue-500" @click="editImage(image)">
                  Chỉnh sửa
                </a-button>
                <EditImageModal :image="selectedImage" :open="isModalOpenImageUpdate" @close="closeModalImageUpdate" />

                <a-popconfirm :zIndex="10000" title="Bạn có chắc chắn muốn xóa ảnh này?" ok-text="Xóa" cancel-text="Hủy"
                  @confirm="deleteImage(image)">
                  <a-button type="link" class="text-black hover:!text-red-500" :loading="loadingImage === image.id">
                    Xóa
                  </a-button>
                </a-popconfirm>

                <a-button type="link" class="text-black hover:text-blue-500" @click="setAsCoverImage(image)">
                  {{ image.statusImage === 0 ? 'Hủy bỏ tiêu đề' : 'Đặt làm ảnh tiêu đề' }}
                </a-button>
              </div>
            </div>
          </div>

          <!-- Modal hiển thị ảnh lớn -->
          <a-modal :zIndex="10001" v-model:visible="isModalVisibleImage" :footer="null"
            :style="{ width: '85vw', maxWidth: '700px' }">
            <div class="flex flex-col items-center p-4">
              <img :src="selectedImage?.urlImage" :alt="selectedImage?.nameImage"
                class="w-full max-h-[65vh] object-contain" />
              <div class="text-center mt-3">
                <p class="text-lg font-medium">{{ selectedImage?.nameImage }}</p>
                <p class="text-sm text-gray-500">
                  Thêm lúc {{ getDateFormat(selectedImage?.createdDate, true) }}
                </p>
              </div>
              <div class="flex justify-center space-x-4 mt-4">
                <a-button type="link" @click="openNewTabImage(selectedImage?.urlImage)" class="text-blue-500">Mở tab
                  mới</a-button>
                <a-button type="link" class="text-green-500" @click="setAsCoverImage(selectedImage)">
                  {{ selectedImage.statusImage === 0 ? 'Hủy bỏ tiêu đề' : 'Đặt làm ảnh tiêu đề' }}
                </a-button>
                <a-popconfirm :zIndex="10001" title="Bạn có chắc chắn muốn xóa ảnh này?" ok-text="Xóa" cancel-text="Hủy"
                  @confirm="deleteImage(selectedImage)">
                  <a-button type="link" danger :loading="loadingImage === selectedImage?.id">
                    Xóa
                  </a-button>
                </a-popconfirm>
              </div>
            </div>
          </a-modal>
        </div>

        <!--  Bình luận  -->
        <TodoComment />

        <!-- Hoạt động -->
        <TodoActivity />

        <!-- menu -->
      </div>

      <!-- menu bên phải -->
      <div class="w-full md:w-1/3 flex flex-col space-y-2 border-l pl-4">
        <!-- <div class="text-gray-600 font-semibold">Gợi ý</div>
        <a-button block>👥 Tham gia</a-button> -->
        <div class="text-gray-600 font-semibold mt-4">Thêm vào thẻ</div>

        <a-button block @click="openMemberModal">
          <UserOutlined />Thành viên
        </a-button>

        <a-button block @click="showModal = true">
          <LinkOutlined />
          Kiểu công việc
        </a-button>



        <!-- Nhãn  -->
        <a-button block @click="openLabelModal('edit')">
          <TagOutlined />
          Nhãn
        </a-button>
        <!-- Độ ưu tiên  -->
        <a-button block @click="showPriorityModal = true">
          <StarOutlined />
          Độ ưu tiên
        </a-button>
        <TodoPriorityLevelModal v-model="showPriorityModal" :currentPriority="selectedPriority"
          @save="handlePriorityChange" />

        <!-- Ngày hạn -->
        <div>
          <a-button block @click="showModalTodoDeadline = true">
            <CalendarOutlined />
            Ngày hạn
          </a-button>
          <TodoDeadlineModal v-model="showModalTodoDeadline" :currentDeadline="deadlineTodo"
            :currentReminder="reminderTodo" />
        </div>

        <!-- Hình ảnh -->
        <a-button block @click="showUploadModal = true">
          <UploadOutlined />
          Tải ảnh lên
        </a-button>
        <TodoAddImageModal v-model="showUploadModal" />

        <!-- Link đính kèm  -->
        <a-button block @click="ShowResource">
          <LinkOutlined />
          Link đính kèm
        </a-button>

        <!-- Link đính kèm -->
        <TodoResourceModal v-model:open="showAttachLinkModal" :resource="selectedResource" />
        <!-- type -->
        <TypeTodoMOdal v-model:open="showModal" @select="onSelectJobType" :idType="meAllDetailTodo?.idType"
          :type="meAllDetailTodo?.type" />
        <!-- <div class="text-gray-600 font-semibold mt-4">Khác</div>
        <a-button danger block>🗑️ Xóa thẻ</a-button> -->
      </div>

    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { getAllCheckTodoChild } from '@/services/api/member/projectdetail/todo.api'
import { ref, watch, computed, onMounted, defineComponent } from 'vue'
import { WebSocketTodoChild, webSocketTodoChild } from '@/services/service/member/todochild.socket'
import { localStorageAction } from '@/utils/storage'
import {
  getAllDetailTodo,
  MBMeAllDetailTodo,
  MBMeTodoChild_Response
} from '@/services/api/member/projectdetail/metodo.api'
import {
  MBMeUpdateDescriptionsTodoRequest,
  webSocketTodo
} from '@/services/service/member/metodo.socket'
import { webSocketMemberProject } from '@/services/service/member/memberproject.socket'
import { TODO_ID_STORAGE_KEY } from '@/constants/key'
import TodoChild from './TodoChild.vue'
import TodoChildMemberModal from './TodoMemberModal.vue'
import {
  CalendarOutlined,
  FormOutlined,
  LinkOutlined,
  MenuOutlined,
  PictureOutlined,
  ProfileOutlined,
  StarOutlined,
  TagOutlined,
  UploadOutlined,
  UserOutlined
} from '@ant-design/icons-vue'
import TodoLabelModal from './TodoLabelModal.vue'
import { webSocketLabelProjectTodo } from '@/services/service/member/melabeltodo.socket'
import { webSocketLabelProject } from '@/services/service/member/melabelproject.socket'
import TodoDeadlineModal from './TodoDeadlineModal.vue'
import {
  getDateFormat,
  getPriorityColor,
  getPriorityIcon,
  getPriorityText,
  getStatusTodoColor,
  getStatusTodoText,
  getTypeColor,
  getTypeIcon
} from '@/utils/commom.helper'
import TodoPriorityLevelModal from './TodoPriorityLevelModal.vue'
import TodoComment from './TodoComment.vue'
import TodoResourceModal from './TodoResourceModal.vue'
const isModalOpenImageUpdate = ref(false)
const selectedResource = ref('')
const isEditingTitle = ref(false)
const editedTitle = ref('')
const getPreviewImage = (url) => {
  return `https://image.thum.io/get/width/600/crop/400/noanimate/${url}`
}
const showUploadModal = ref(false)
const userLogin = localStorageAction.get(USER_INFO_STORAGE_KEY)
const handleUploadSuccess = (data) => {
  console.log('Ảnh đã tải lên:', data)
  showUploadModal.value = false
}
const meAllDetailTodo = ref<MBMeAllDetailTodo | null>(null)
const todoChild = ref<MBMeTodoChild_Response[]>([])
const showAddMemberModal = ref(false)
const memberModalRef = ref<any>(null)

const showModalTodoDeadline = ref(false)
const deadlineTodo = ref(null)
const reminderTodo = ref(null)

const todoListByPhase = ref<MBTodoListResponse[]>([])

const props = defineProps<{
  todoId: string | null
  modelValue: boolean
  disabled: boolean
}>()

const showModal = ref(false)

const onSelectJobType = (jobType) => {
  console.log('Người dùng đã chọn:', jobType)
}
const VNodes = defineComponent({
  props: {
    vnodes: {
      type: Object,
      required: true,
    },
  },
  render() {
    return this.vnodes;
  },
});

const showAttachLinkModal = ref(false)

const isEditingDescription = ref(false)
const editedDescription = ref('')

const editDescription = () => {
  isEditingDescription.value = true
  editedDescription.value = meAllDetailTodo.value?.descriptions || ''
}

const cancelEditDescription = () => {
  isEditingDescription.value = false
  editedDescription.value = ''
}

import { useRoute, useRouter } from 'vue-router'
const route = useRoute()
// thời hạn
const isCompletedTime = computed({
  get: () => !!meAllDetailTodo.value?.completionTime,
  set: (val) => {
    const payload = {
      id: localStorageAction.get(TODO_ID_STORAGE_KEY),
      idTodo: localStorageAction.get(TODO_ID_STORAGE_KEY),
      //status=0 (hoàn thành), false -> status=1 (chưa hoàn thành)
      status: val ? 0 : 1,
      projectId: route.params.id,
      idTodo: localStorageAction.get(TODO_ID_STORAGE_KEY),
      idUser: userLogin.userId,
      urlPath: getUrlActivity(route),
      isAddDeadline: false
    }
    webSocketTodo.sendupdateCompleteTodo(payload)
  }
})

const showPriorityModal = ref(false)

const selectedPriority = ref(null)

const handlePriorityChange = (priority: number) => {
  selectedPriority.value = priority
}

// Label
const isLabelModalVisible = ref(false)
const selectedLabel = ref(null)
const modalMode = ref('view') // 'create' | 'edit' | 'view'

function openLabelModal(mode) {
  modalMode.value = mode
  isLabelModalVisible.value = true
}

function handleSaveLabel(updatedLabel) { }

function handleDeleteLabel(labelId) {
  // const index = meAllDetailTodo.value?.labels.findIndex(l => l.id === labelId)
  // if (index !== -1) {
  //   // meAllDetailTodo.value?.labels.splice(index, 1)
  // }
  isLabelModalVisible.value = false
}

// Title
const startEditingTitle = () => {
  isEditingTitle.value = true
  editedTitle.value = meAllDetailTodo.value?.name || ''
}
// Tên todo
const saveTitle = () => {
  if (meAllDetailTodo.value && editedTitle.value.trim() !== meAllDetailTodo.value.name) {
    const payload = {
      idTodo: localStorageAction.get(TODO_ID_STORAGE_KEY),
      nameTodo: editedTitle.value.trim()
    }
    webSocketTodo.sendUpdateNameTodo(payload)
    meAllDetailTodo.value.name = editedTitle.value.trim()
  }
  isEditingTitle.value = false
}

const saveDescription = () => {
  if (editedDescription.value.trim() === '') {
    toast.warning('Vui lòng nhập mô tả!', {
      style: {
        zIndex: 999999,
      },
    });
    return;
  }

  if (meAllDetailTodo.value) {
    meAllDetailTodo.value.descriptions = editedDescription.value
  }
  const payload: MBMeUpdateDescriptionsTodoRequest = {
    idTodo: localStorageAction.get(TODO_ID_STORAGE_KEY),
    descriptions: editedDescription.value
  }
  webSocketTodo.sendUpdateDescriptionsTodo(payload)
  isEditingDescription.value = false
}

const isModalVisibleImage = ref(false)
const selectedImage = ref(null)

const openModalImage = (image) => {
  selectedImage.value = image
  isModalVisibleImage.value = true
}

const editImage = (image) => {
  selectedImage.value = image
  isModalOpenImageUpdate.value = true
}
const closeModalImageUpdate = () => {
  isModalOpenImageUpdate.value = false
}


const loadingImage = ref(null)
const deleteImage = (image) => {
  loadingImage.value = image.id // Bật trạng thái loading

  const payload = {
    id: image.id,
    nameImage: image.nameImage,
    urlImage: image.urlImage,
    statusImage: image.statusImage,
    idTodo: localStorageAction.get(TODO_ID_STORAGE_KEY),
    urlImage: image.urlImage,
    projectId: route.params.id,
    idUser: userLogin.userId,
    urlPath: getUrlActivity(route)
  }

  webSocketImage.sendDeleteImage(payload)
  isModalVisibleImage.value = false
}

const setAsCoverImage = (image) => {
  console.log('Đặt làm ảnh tiêu đề:', image)
  const payload = {
    idTodo: localStorageAction.get(TODO_ID_STORAGE_KEY),
    idImage: image.id,
    urlImage: image.urlImage,
    status: image.statusImage
  }
  webSocketImage.sendChangeCoverTodo(payload)
  isModalVisibleImage.value = false
}

const openNewTabImage = (url) => {
  window.open(url, '_blank')
}

const setAsBackgroundImage = (image) => {
  console.log('Đặt làm ảnh nền:', image)
}

const fetchTodoChild = async () => {
  try {
    const response = await getAllCheckTodoChild({
      idTodo: localStorageAction.get(TODO_ID_STORAGE_KEY)
    })
    todoChild.value = response.data
    console.log('todoChild', todoChild.value)
  } catch (error) {
    console.error('Lỗi khi lấy danh sách cột:', error)
  }
}

const ShowResource = () => {
  showAttachLinkModal.value = true
  selectedResource.value = ''
}

const fetchMeAllDetailTodo = async () => {
  try {
    const response = await getAllDetailTodo({ idTodo: localStorageAction.get(TODO_ID_STORAGE_KEY) })
    meAllDetailTodo.value = response.data
    deadlineTodo.value = meAllDetailTodo.value.deadline
    selectedPriority.value = meAllDetailTodo.value.priorityLevel
    reminderTodo.value = meAllDetailTodo.value.reminderTime
    console.log("DetailsTodo:", meAllDetailTodo.value)
  } catch (error) {
    console.error('Lỗi khi lấy chi tiết todo:', error)
  }
}

const fetchTodoListByPhaseProject = async () => {
  try {
    const response = await getTodoListByPhaseProject({
      idPhaseProject: String(route.params.idPhase)
    })
    todoListByPhase.value = response.data
    console.log('TodoListByPhase:', todoListByPhase.value)
  } catch (error) {
    console.error('Lỗi khi lấy danh sách todo list theo phase:', error)
  }
}


// resource
import { getAllResourceTodo } from '@/services/api/member/projectdetail/resource.api'
import { webSocketMeTodoResource } from '@/services/service/member/resource.socket'
import { Alert, Modal, message } from 'ant-design-vue'
import { toast } from 'vue3-toastify'
import TodoAddImageModal from './TodoAddImageModal.vue'
import { webSocketImage } from '@/services/service/member/image.socket'
import EditImageModal from './EditImageModal.vue'

// State
const meAllResourceTodo = ref([])

// Fetch dữ liệu
const fetchMeAllResourceTodo = async () => {
  try {
    const response = await getAllResourceTodo({
      idTodo: localStorageAction.get(TODO_ID_STORAGE_KEY)
    })
    meAllResourceTodo.value = response.data
  } catch (error) {
    console.error('Lỗi khi lấy resource:', error)
  }
}

// Mở modal chỉnh sửa
const editResource = (resource) => {
  // alert(resource.id)
  // editedResource.value = { ...resource };
  selectedResource.value = resource
  showAttachLinkModal.value = true
}

// Xóa resource với Modal.confirm từ Ant Design
const deleteResource = async (resource) => {
  try {
    console.log('vào ', resource)
    const payload = {
      id: resource.id,
      idTodo: localStorageAction.get(TODO_ID_STORAGE_KEY),
      projectId: route.params.id,
      idUser: userLogin.userId,
      name: resource.name,
      url: resource.url,
      urlPath: getUrlActivity(route)
    }
    webSocketMeTodoResource.sendDeleteResourceTodo(payload)
    toast.success('Xóa thành công...!')
  } catch (error) {
    message.error('Lỗi khi xóa resource!')
    console.error('Lỗi khi xóa resource:', error)
  }
}

// Lắng nghe WebSocket để cập nhật UI real-time

const getTextColor = (bgColor: string): string => {
  if (!bgColor || bgColor.length < 3) return '#ffffff'

  const r = parseInt(bgColor.substring(0, 2), 16)
  const g = parseInt(bgColor.substring(2, 4), 16)
  const b = parseInt(bgColor.substring(4, 6), 16)

  const brightness = (r * 299 + g * 587 + b * 114) / 1000
  return brightness > 125 ? '#000000' : '#ffffff'
}

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
  (e: 'close'): void
}>()

const visible = computed({
  get: () => props.modelValue,
  set: (val: boolean) => emit('update:modelValue', val)
})

// Đóng modal
const handleClose = () => {
  visible.value = false
  isEditingDescription.value = false
  editedDescription.value = ''
  meAllDetailTodo.value = null
  todoChild.value = []
  emit('close')
  localStorageAction.remove(TODO_ID_STORAGE_KEY)
}

// Thay đổi trạng thái modal
const handleVisibleChange = (val: boolean) => {
  emit('update:modelValue', val)
  if (!val) emit('close')
}

// Mở modal thêm thành viên
const openMemberModal = () => {
  showAddMemberModal.value = true
}

const disabledModal = () => {
  return props.disabled
}
const handleSelectPhase = (targetList: MBTodoListResponse) => {
  const currentTodo = meAllDetailTodo.value

  if (!currentTodo) {
    console.error('Không có todo đang chọn')
    return
  }

  const payload = {
    idTodo: localStorageAction.get(TODO_ID_STORAGE_KEY),
    idTodoListOld: currentTodo.todoListId,
    idTodoListNew: targetList.id,
    indexBefore: currentTodo.indexTodo,
    indexAfter: 0,
    phaseId: String(route.params.idPhase),
    projectId: String(route.params.id),
    sessionId: 'user-session-abc-xyz',
    idUser: localStorageAction.get(USER_INFO_STORAGE_KEY).userId,
    urlPath: getUrlActivity(route) ,
    nameTodoListOld: currentTodo.todoListName,
    nameTodoListNew: targetList.name
  }

  webSocketTodo.sendUpdateIndexTodo(payload.projectId, payload.phaseId, payload)
}

import TodoActivity from './TodoActivity.vue'
import { USER_INFO_STORAGE_KEY } from '@/constants/storagekey'
import { getUrlActivity } from '@/utils/urlActivityHelper'
import TypeTodoMOdal from './TypeTodoModal.vue'
import { webSocketTypeTodo } from '@/services/service/member/typetodo.socket'
import { getTodoListByPhaseProject, MBTodoListResponse } from '@/services/api/member/projectdetail/todolist.api'

watch(
  [() => props.todoId, () => visible.value],
  ([id, isVisible]) => {
    if (isVisible && id) {
      localStorageAction.set(TODO_ID_STORAGE_KEY, id)
      fetchTodoChild()
      fetchMeAllDetailTodo()
      fetchMeAllResourceTodo()
      fetchTodoListByPhaseProject()
    }
  },
  { immediate: true }
)

webSocketTodo.subscribeUpdateIndexTodo(() => {
  if (props.todoId == localStorageAction.get(TODO_ID_STORAGE_KEY)) {
    fetchTodoListByPhaseProject()
  }
})

webSocketTodoChild.getCreateTodoChildChecklist(() => {
  if (props.todoId == localStorageAction.get(TODO_ID_STORAGE_KEY)) {
    fetchTodoChild()
    fetchMeAllDetailTodo()
  }
})
webSocketTodoChild.getDeleteTodoChecklist(() => {
  if (props.todoId == localStorageAction.get(TODO_ID_STORAGE_KEY)) {
    fetchTodoChild()
    fetchMeAllDetailTodo()
  }
})
webSocketTodoChild.getEditTodoChecklist(() => {
  if (props.todoId == localStorageAction.get(TODO_ID_STORAGE_KEY)) {
    fetchTodoChild()
    fetchMeAllDetailTodo()
  }
})
webSocketTodoChild.getUpdateStatusTodoChecklist(() => {
  if (props.todoId == localStorageAction.get(TODO_ID_STORAGE_KEY)) {
    fetchTodoChild()
    fetchMeAllDetailTodo()
  }
})
webSocketTodo.getUpdateDescriptionsTodo(() => {
  if (props.todoId == localStorageAction.get(TODO_ID_STORAGE_KEY)) {
    fetchMeAllDetailTodo()
  }
})
webSocketMemberProject.getJoinTodoVote(() => {
  if (props.todoId == localStorageAction.get(TODO_ID_STORAGE_KEY)) {
    fetchMeAllDetailTodo()
  }
})
webSocketMemberProject.getOutTodoVoteMemberProject(() => {
  if (props.todoId == localStorageAction.get(TODO_ID_STORAGE_KEY)) {
    fetchMeAllDetailTodo()
  }
})
webSocketTodo.getUpdateNameTodo(() => {
  if (props.todoId == localStorageAction.get(TODO_ID_STORAGE_KEY)) {
    fetchMeAllDetailTodo()
  }
})
webSocketLabelProjectTodo.getjoinLabeProjectTodo(() => {
  if (props.todoId == localStorageAction.get(TODO_ID_STORAGE_KEY)) {
    fetchMeAllDetailTodo()
  }
})
webSocketLabelProjectTodo.getoutLabeProjectTodo(() => {
  if (props.todoId == localStorageAction.get(TODO_ID_STORAGE_KEY)) {
    fetchMeAllDetailTodo()
  }
})
webSocketLabelProject.getCreateLabelProject(() => {
  if (props.todoId == localStorageAction.get(TODO_ID_STORAGE_KEY)) {
    fetchMeAllDetailTodo()
  }
})
webSocketLabelProject.getUpdateLabelProject(() => {
  if (props.todoId == localStorageAction.get(TODO_ID_STORAGE_KEY)) {
    fetchMeAllDetailTodo()
  }
})
webSocketLabelProject.getDeleteLabelProject(() => {
  if (props.todoId == localStorageAction.get(TODO_ID_STORAGE_KEY)) {
    fetchMeAllDetailTodo()
  }
})
webSocketTodo.getupdateCompleteTodo(() => {
  if (props.todoId == localStorageAction.get(TODO_ID_STORAGE_KEY)) {
    fetchMeAllDetailTodo()
  }
})
webSocketTodo.getUpdateDeadlineTodo(() => {
  if (props.todoId == localStorageAction.get(TODO_ID_STORAGE_KEY)) {
    fetchMeAllDetailTodo()
  }
})
webSocketTodo.getDeleteDeadlineTodo(() => {
  if (props.todoId == localStorageAction.get(TODO_ID_STORAGE_KEY)) {
    fetchMeAllDetailTodo()
  }
})
webSocketTodo.getUpdatePriorityLevel(() => {
  if (props.todoId == localStorageAction.get(TODO_ID_STORAGE_KEY)) {
    fetchMeAllDetailTodo()
  }
})
webSocketMeTodoResource.getDeleteResourceTodo(() => {
  if (props.todoId == localStorageAction.get(TODO_ID_STORAGE_KEY)) {
    fetchMeAllResourceTodo()
  }
})

webSocketMeTodoResource.getAddResourceTodo(() => {
  if (props.todoId == localStorageAction.get(TODO_ID_STORAGE_KEY)) {
    fetchMeAllResourceTodo()
  }
})

webSocketMeTodoResource.getUpdateResourceTodo(() => {
  if (props.todoId == localStorageAction.get(TODO_ID_STORAGE_KEY)) {
    fetchMeAllResourceTodo()
  }
})

webSocketImage.getCreateImage(() => {
  if (props.todoId == localStorageAction.get(TODO_ID_STORAGE_KEY)) {
    fetchMeAllDetailTodo()
  }
})

webSocketImage.getDeleteImage(() => {
  loadingImage.value = null
  if (props.todoId == localStorageAction.get(TODO_ID_STORAGE_KEY)) {
    fetchMeAllDetailTodo()
  }
})

webSocketImage.getUpdateNameImage(() => {
  if (props.todoId == localStorageAction.get(TODO_ID_STORAGE_KEY)) {
    fetchMeAllDetailTodo()
  }
})

webSocketImage.getChangeCoverTodo(() => {
  if (props.todoId == localStorageAction.get(TODO_ID_STORAGE_KEY)) {
    fetchMeAllDetailTodo()
  }
})

webSocketTypeTodo.getJoinTypeTodo(() => {
  if (props.todoId == localStorageAction.get(TODO_ID_STORAGE_KEY)) {
    fetchMeAllDetailTodo()
  }
})

webSocketTodoChild.getUpdateProgress(() => {
  if (props.todoId == localStorageAction.get(TODO_ID_STORAGE_KEY)) {
    fetchMeAllDetailTodo()
    fetchTodoChild()
  }
})
webSocketTodo.subscribeUpdateIndexTodo(() => {
  if (props.todoId == localStorageAction.get(TODO_ID_STORAGE_KEY)) {
    fetchMeAllDetailTodo()
  }
})

</script>
<style scoped>
.modal-overlay {
  position: absolute;
  inset: 0;
  z-index: 10;
  cursor: not-allowed;
}
</style>
