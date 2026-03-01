<template>
  <div class="mt-6 w-full border-t pt-4">
    <!-- Tiêu đề & Toggle -->
    <div class="flex justify-between items-center mb-4">
      <h3 class="text-base font-semibold text-gray-800">
        <MessageOutlined /> Bình luận
      </h3>
      <button @click="toggleComments" class="flex items-center gap-1 text-sm text-black-2 hover:underline">
        <span>{{ showComments ? 'Ẩn Chi tiết' : 'Hiện Chi tiết' }}</span>
        <UpOutlined v-if="showComments" />
        <DownOutlined v-else />
      </button>
    </div>

    <!-- Form bình luận -->
    <div ref="commentInputRef" class="flex items-start gap-3 mb-4 relative">
      <img :src="userLogin?.pictureUrl" alt="Avatar" class="w-8 h-8 rounded-full object-cover" />
      <div class="flex-1 relative">
        <a-textarea v-model:value="newCommentText" @input="handleTyping(); adjustTextareaHeightSend()"
          @keydown.down.prevent="highlightNextUser" @keydown.up.prevent="highlightPrevUser" @keydown.enter="handleEnter"
          ref="textareaRef" placeholder="Viết bình luận..., nhập @ để nhắc tới ai đó" class="w-full border border-gray-300 rounded-xl px-3 py-2 text-sm focus:outline-none 
         focus:ring-1 italic focus:ring-blue-500 resize-none overflow-hidden" style="height: 38px;">
        </a-textarea>

        <!-- Gợi ý người dùng -->
        <div v-if="showSuggestions && filteredUsers.length"
          class="absolute z-10 bg-white border mt-1 rounded-md shadow-lg w-full max-h-40 overflow-auto text-sm">
          <ul>
            <li v-for="(user, index) in filteredUsers" :key="user.userEmail" :class="[
              'px-3 py-2 cursor-pointer flex items-center gap-3',
              index === selectedSuggestionIndex ? 'bg-blue-100 font-medium' : ''
            ]" @click="selectUser(user.userEmail)">
              <img :src="user.userImage || defaultAvatar" alt="Avatar" class="w-6 h-6 rounded-full object-cover" />
              <span>{{ user.userName }} <span class="text-gray-400">{{ user.userEmail }}</span></span>
            </li>
          </ul>
        </div>

        <div class="text-right mt-2">
          <a-tooltip title="Gửi">
            <SendOutlined class="text-lg text-gray-500 cursor-pointer hover:text-blue-600 transition rotate-[-45deg]"
              @click="addComment" />
          </a-tooltip>

        </div>
      </div>
    </div>

    <!-- Danh sách bình luận -->
    <div class="space-y-4">
      <div v-for="(comment, index) in comments" :key="index" class="flex items-start gap-3">
        <img :src="comment.userImage || 'https://cdn.sforum.vn/sforum/wp-content/uploads/2023/10/avatar-trang-4.jpg'"
          class="w-8 h-8 rounded-full object-cover mt-1" alt="Avatar" />

        <div class="flex-1">
          <div class="flex justify-between items-center">
            <span class="text-sm font-medium text-gray-800">{{ comment.userName }}</span>
            <span class="text-xs text-gray-400">{{ getTimeAgo(comment.createdDate) }}</span>
          </div>
          <!-- Nội dung bình luận -->
          <div :class="editingCommentIndex === index ? 'p-0' : 'mt-1 border border-gray-200 rounded-xl p-2 bg-gray-50'">
            <div v-if="editingCommentIndex === index">
              <a-textarea v-model:value="editingText" @input="adjustTextareaHeight" @mousedown="handleMouseDown"
                class="font-light w-full h-auto border border-gray-300 rounded-xl  bg-transparent px-3 py-2 text-sm focus:ring-black-2 overflow-hidden"
                ref="editingTextareaRef"></a-textarea>
              <div class="flex gap-2 mt-2">
                <a-button type="link" size="small" class="text-gray-800 hover:!text-gray-600"
                  @click="saveEdit(comment, index)">
                  Lưu
                </a-button>
                <a-button type="link" size="small" class="text-gray-800 hover:!text-gray-600" @click="cancelEdit">
                  Hủy
                </a-button>
              </div>

            </div>
            <div v-else class="w-full font-light text-sm text-gray-700  break-all whitespace-pre-line"
              v-html="highlightTags(comment.content)">
            </div>
          </div>
          <!-- Hành động -->
          <div class="mt-1 flex text-xs text-gray-800 items-center">
            <template v-if="comment.userEmail === userLogin?.email && editingCommentIndex !== index">
              <a-button type="link" class="text-gray-800 hover:!text-gray-600" @click="startEdit(index)">Chỉnh
                sửa</a-button>
              <a-popconfirm :zIndex="10000" title="Bạn có chắc muốn xóa bình luận này?" okText="Có" cancelText="Không"
                @confirm="deleteComment(comment, index)">
                <a-button type="link" class="text-gray-800 hover:!text-red-600">Xóa</a-button>
              </a-popconfirm>
            </template>
            <template v-else-if="editingCommentIndex !== index">
              <a-button type="link" class="text-gray-800 hover:!text-gray-600" @click="replyTo(comment.userEmail)">Trả
                lời</a-button>
            </template>

            <!-- Ngày tháng  -->
            <!-- <span class="ml-2 text-gray-500">{{getDateFormat(comment.createdDate) }}</span> -->
          </div>

        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick, reactive } from 'vue'
import { getAllCommentByIdTodo, getAllMemberInProject, MBMeCommentResponse, MBMeUserInProjectResponse } from '@/services/api/member/projectdetail/mecomment.api'
import { localStorageAction } from '@/utils/storage'
import { USER_INFO_STORAGE_KEY } from '@/constants/storagekey'
import { TODO_ID_STORAGE_KEY } from '@/constants/key'
import { webSocketMeTodoComment } from '@/services/service/member/mecomment.socket'
import { getTimeAgo } from '@/utils/commom.helper'
import { Input } from 'ant-design-vue'
import { DownOutlined, MessageOutlined, SendOutlined, UpOutlined } from '@ant-design/icons-vue'
import { useRoute } from "vue-router";
import { createNotificationTag } from '@/services/api/member/projectdetail/notification.api'
import { webSocketNotificationMember } from '@/services/service/member/notification-member.socket'
import { getUrlActivity } from "../../../../../utils/urlActivityHelper";
import { toast } from 'vue3-toastify'
const route = useRoute();
const { TextArea } = Input

// Thông tin người dùng
const userLogin = localStorageAction.get(USER_INFO_STORAGE_KEY)
const defaultAvatar = 'https://cdn.sforum.vn/sforum/wp-content/uploads/2023/10/avatar-trang-4.jpg'; // Path to default avatar image

// Refs
const commentInputRef = ref<HTMLElement | null>(null)
const textareaRef = ref<HTMLTextAreaElement | null>(null)
const editingTextareaRef = ref<HTMLTextAreaElement | null>(null)

// Dữ liệu
const showComments = ref(false)
const newCommentText = ref('')
const editingCommentIndex = ref<number | null>(null)
const editingText = ref('')
const comments = ref<MBMeCommentResponse[]>([])
const allUsers = ref<MBMeUserInProjectResponse[]>([])
const pageable = reactive({
  page: 1,
  size: 1
})

const showSuggestions = ref(false)
const searchKeyword = ref('')
const selectedSuggestionIndex = ref(0)

const filteredUsers = computed(() =>
  allUsers.value.filter(user =>
    user.userEmail.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
    user.userName.toLowerCase().includes(searchKeyword.value.toLowerCase())
  )
)

// Toggle hiển thị
const toggleComments = () => {
  // đóng 
  if (showComments.value) {
    pageable.size = 1
    fetchComments()
  } else {
    pageable.size = 10000
    fetchComments()
  }
  showComments.value = !showComments.value
}

// Adjust textarea height based on content
const adjustTextareaHeight = (event) => {
  const textarea = event.target
  if (textarea && textarea instanceof HTMLTextAreaElement) {
    textarea.style.height = 'auto'
    textarea.style.height = textarea.scrollHeight + 'px'
  }
}
const adjustTextareaHeightSend = () => {
  nextTick(() => {
    const textarea = textareaRef.value?.resizableTextArea?.textArea;
    if (textarea) {
      textarea.style.height = "36px"; // Chiều cao mặc định
      textarea.style.height = `${textarea.scrollHeight}px`; // Tự động mở rộng
    }
  });
};

const handleMouseDown = (event) => {
  if (event.button === 0) {
    adjustTextareaHeight(event)
  }
};

// Gợi ý người dùng khi nhập @
const handleTyping = () => {
  if (textareaRef.value) {
    adjustTextareaHeight({ target: textareaRef.value })
  }
  const value = newCommentText.value
  const match = value.match(/(?:^|\s)@([^\s@]*)$/)
  if (match) {
    searchKeyword.value = match[1]
    showSuggestions.value = true
  } else {
    showSuggestions.value = false
  }
}

const highlightNextUser = () => {
  if (showSuggestions.value && selectedSuggestionIndex.value < filteredUsers.value.length - 1) {
    selectedSuggestionIndex.value++
  }
}

const highlightPrevUser = () => {
  if (showSuggestions.value && selectedSuggestionIndex.value > 0) {
    selectedSuggestionIndex.value--
  }
}

const handleEnter = (e: KeyboardEvent) => {
  if (showSuggestions.value) {
    e.preventDefault()
    selectUserFromSuggestion()
  } else {
    // Allow new line insertion
    e.preventDefault()
    const textarea = e.target as HTMLTextAreaElement
    const start = textarea.selectionStart
    const end = textarea.selectionEnd
    newCommentText.value = newCommentText.value.substring(0, start) + '\n' + newCommentText.value.substring(end)
    nextTick(() => {
      textarea.selectionStart = textarea.selectionEnd = start + 1
      adjustTextareaHeight({ target: textarea })
    })
  }
}

const selectUserFromSuggestion = () => {
  if (filteredUsers.value.length > 0) {
    const user = filteredUsers.value[selectedSuggestionIndex.value]
    selectUser(user.userEmail)
  }
}

const getUsernamePart = (email: string) => {
  return email.split('@')[0]
}

const selectUser = (email: string) => {
  const username = getUsernamePart(email)
  const value = newCommentText.value
  const replaced = value.replace(/(?:^|\s)@([^\s@]*)$/, matched => matched.replace(/@([^\s@]*)$/, `@${username}`))
  newCommentText.value = replaced + ' '
  showSuggestions.value = false
  selectedSuggestionIndex.value = 0
  nextTick(() => textareaRef.value?.focus())
}

const extractMentionedUsers = (content: string): string[] => {
  const matches = content.match(/@([\w\.]+)(?=\s|$)/g)
  return matches ? matches.map(username => username.trim().replace('@', '')) : []
}

const addComment = async () => {
  const content = newCommentText.value.trim()
  if (!content) return

  // Extract mentioned users from the content
  const mentionedUsers = extractMentionedUsers(content)
  const mentionedData = mentionedUsers.map(username => {
    const user = allUsers.value.find(user =>
      user.userEmail.startsWith(username) || user.userName.toLowerCase() === username.toLowerCase()
    );
    return user ? { email: user.userEmail, id: user.userId } : null;
  }).filter(user => user !== null);

  // Lấy danh sách email và ID riêng 
  const mentionedEmails = mentionedData.map(user => user!.email);
  const mentionedIds = mentionedData.map(user => user!.id);

  const payload = {
    idTodo: localStorageAction.get(TODO_ID_STORAGE_KEY),
    email: userLogin.email,
    content: content,
    mentionedEmails: mentionedEmails,
    mentionedIds: mentionedIds,
    idProject: route.params.id,
    username: userLogin.fullName,
    idUser: userLogin.userId,
    url: getUrlActivity(route)
  }

  await createNotificationTag(payload);

  webSocketMeTodoComment.sendAddComment(payload)
  if (mentionedEmails.length > 0) {
    webSocketNotificationMember.sendNotificationMember({})
  }
  newCommentText.value = ''
  nextTick(() => {
    textareaRef.value.style.height = 'auto'
  })
}

// Chỉnh sửa
const startEdit = (index: number) => {
  editingCommentIndex.value = index
  editingText.value = comments.value[index].content
  nextTick(() => {
    if (editingTextareaRef.value) {
      adjustTextareaHeight({ target: editingTextareaRef.value })
    }
  })
}

const saveEdit = (comment, index) => {
  const content = editingText.value.trim()
  if (!content) {
    toast.error("Bình luận không được để trống")
    return
  }

  if (!content) return
  comments.value[index].content = content

  const payload = {
    id: comment.id,
    content: content,
    idStaffProject: comment.staffProjectId
  }

  webSocketMeTodoComment.sendUpdateComment(payload)
  editingCommentIndex.value = null
  editingText.value = ''
}

const cancelEdit = () => {
  editingCommentIndex.value = null
  editingText.value = ''
}

// Xóa
const deleteComment = (comment, index) => {
  comments.value.splice(index, 1)
  const payload = {
    id: comment.id,
    idStaffProject: comment.staffProjectId
  }
  webSocketMeTodoComment.sendDeleteComment(payload)
}

// Trả lời
const replyTo = (email: string) => {
  const username = getUsernamePart(email)
  newCommentText.value += `@${username} `
  nextTick(() => {
    commentInputRef.value?.scrollIntoView({ behavior: 'smooth', block: 'center' })
    textareaRef.value?.focus()
    adjustTextareaHeight({ target: textareaRef.value })
  })
}

// Tô màu tag
const highlightTags = (content: string) => {
  return content.replace(/@([\w\.]+)(?=\s|$)/g, '<span class="text-blue-600 font-semibold">@$1</span>')
}

const fetchComments = async () => {
  try {
    const res = await getAllCommentByIdTodo({
      idTodo: localStorageAction.get(TODO_ID_STORAGE_KEY),
      page: pageable.page,
      size: pageable.size
    })
    comments.value = res.data.data
  } catch (err) {
    console.error('Lỗi khi lấy bình luận:', err)
  }
}

const fetchMemberInProject = async () => {
  try {
    const res = await getAllMemberInProject({
      idProject: route.params.id,
      email: userLogin.email
    })
    allUsers.value = res.data
  } catch (err) {
    console.error('Lỗi khi lấy member project:', err)
  }
}

onMounted(() => {
  fetchComments()
  fetchMemberInProject()
})

webSocketMeTodoComment.getUpdateComment(fetchComments)
webSocketMeTodoComment.getDeleteComment(fetchComments)
webSocketMeTodoComment.getAddComment(fetchComments)

</script>