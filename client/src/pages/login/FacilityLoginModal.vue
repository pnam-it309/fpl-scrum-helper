<template>
  <div
    v-if="isOpen"
    class="fixed inset-0 z-50 bg-black/50 flex items-center justify-center transition-opacity duration-300 ease-in-out"
    @click.self="$emit('close')"
  >
    <div
      class="bg-white w-[400px] max-w-full rounded-2xl p-6 shadow-lg relative transform transition-all duration-300 ease-in-out scale-100 opacity-100"
    >
      <!-- Logo + Title -->
      <div class="text-center mb-4">
        <img src="/src/assets/images/favicon.ico" alt="FPT Logo" class="mx-auto h-10 mb-2" />
      </div>

      <!-- Select Cơ Sở -->
      <div>
        <select
          v-model="selectedFacility"
          @change="clearError"
          class="w-full border border-gray-300 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-blue-400"
        >
          <option disabled value="">Chọn cơ sở</option>
          <option
            v-for="facility in facilities"
            :key="facility.id"
            :value="facility.id"
          >
            {{ facility.nameFacility }}
          </option>
        </select>
        <p v-if="errorMessage" class="text-red-500 text-sm mt-1">{{ errorMessage }}</p>
      </div>

      <!-- SOCIAL line -->
      <div class="relative my-6">
        <hr />
        <span
          class="absolute -top-3 left-1/2 -translate-x-1/2 bg-white px-2 text-gray-500 text-sm font-semibold"
        >
          SOCIAL
        </span>
      </div>

      <!-- Google Login Button -->
      <div class="flex justify-center">
        <button
          @click="handleLogin"
          class="bg-selectedLogin hover:opacity-90 text-white w-full flex items-center justify-center gap-2 py-2 rounded-xl  transition duration-200"
        >
          <font-awesome-icon :icon="faGoogle" />
          Google
        </button>
      </div>

      <!-- Close Button -->
      <button
        class="absolute top-2 right-3 text-gray-500 hover:text-black text-lg"
        @click="$emit('close')"
      >
        ×
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, defineProps, onMounted, watch } from 'vue'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { faGoogle } from '@fortawesome/free-brands-svg-icons'
import { URL_OAUTH2_GOOGLE_MANAGE, URL_OAUTH2_GOOGLE_MEMBER } from '@/constants/url'
import { getAllStaffFacility } from '@/services/api/admin/staff.api'
import { getAllFacilityLogin } from '@/services/api/permitall/register/student-register.api'

const props = defineProps({
  isOpen: Boolean,
  loginType: String
})

const selectedFacility = ref('')
const facilities = ref([])
const errorMessage = ref('')

const clearError = () => {
  errorMessage.value = ''
}

const handleLogin = () => {
  if (!selectedFacility.value) {
    errorMessage.value = 'Vui lòng chọn cơ sở!'
    return
  }

  document.cookie = `facility_login=${selectedFacility.value}; path=/`
  const loginUrl =
    props.loginType === 'manage'
      ? URL_OAUTH2_GOOGLE_MANAGE(selectedFacility.value)
      : URL_OAUTH2_GOOGLE_MEMBER(selectedFacility.value)
  window.location.href = loginUrl
}

const fetchFacilities = async () => {
  try {
    const response = await getAllFacilityLogin()
    facilities.value = response.data || []
  } catch (error) {
    console.error('Lỗi khi lấy danh sách cơ sở:', error)
  }
}

watch(
  () => props.isOpen,
  (newVal) => {
    if (!newVal) {
      selectedFacility.value = ''
      errorMessage.value = ''
    }
  }
)

onMounted(fetchFacilities)
</script>
