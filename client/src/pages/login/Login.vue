<template>
  <div class="bg-gray-200 bg-cover min-h-screen flex justify-center items-center px-4 sm:px-6 lg:px-8"
    :style="{ backgroundImage: `url(${backgroundImage})` }">
    <div class="card border-0 w-full max-w-lg sm:max-w-xl md:max-w-2xl lg:max-w-3xl">
      <!-- Logo trên cùng -->
      <div class="mt-6 flex justify-center items-center gap-4">
        <img :src="logo" alt="Logo FPT" class="w-32 sm:w-36" />
        <img src="/src/assets/images/logo-udpm-dark.png" alt="Logo UDPM" class="w-32 sm:w-36" />
      </div>

      <div class="text-center mt-11 mb-6 mt-20">
        <h1 class="text-3xl sm:text-2xl font-semibold text-selectedLogin tracking-wider font-sans">ĐĂNG NHẬP</h1>
      </div>
      
      <div class="flex flex-col sm:flex-row flex-wrap justify-center items-center gap-12 mt-10">

        <!-- Admin -->
        <div class="flex flex-col items-center gap-3 w-full sm:w-[220px]">
          <img src="/src/assets/images/Admin.png" alt="Admin Icon"
            class="w-36 h-auto object-contain transition-transform duration-300 hover:scale-105 cursor-pointer"
            @click="handleRedirectLoginADMIN" />
          <button @click="handleRedirectLoginADMIN"
            class="w-full px-4 py-3 rounded-md bg-selectedLogin font-semibold text-white text-base hover:opacity-90">
            <font-awesome-icon :icon="faUser" class="mr-2" />
            <span>Admin</span>
          </button>
        </div>

        <!-- Quản lý dự án -->
        <div class="flex flex-col items-center gap-3 w-full sm:w-[220px]">
          <img src="/src/assets/images/Manage.png" alt="Manager Icon"
            class="w-36 h-auto object-contain transition-transform duration-300 hover:scale-105 cursor-pointer"
            @click="openFacilityModal('manage')" />
          <button @click="openFacilityModal('manage')"
            class="w-full px-4 py-3 rounded-md font-semibold bg-selectedLogin text-white text-base hover:opacity-90">
            <font-awesome-icon :icon="faUser" class="mr-2" />
            <span>Quản Lý Dự Án</span>
          </button>
        </div>

        <!-- Thành viên dự án -->
        <div class="flex flex-col items-center gap-3 w-full sm:w-[220px]">
          <img src="/src/assets/images/Member.png" alt="Member Icon"
            class="w-36 h-auto object-contain transition-transform duration-300 hover:scale-105 cursor-pointer"
            @click="openFacilityModal('member')" />
          <button @click="openFacilityModal('member')"
            class="w-full px-4 py-3 rounded-md font-semibold bg-selectedLogin text-white text-base hover:opacity-90">
            <font-awesome-icon :icon="faUser" class="mr-2" />
            <span>Thành Viên Dự Án</span>
          </button>
        </div>

      </div>

      <div
        style="min-height: calc(120px + 0vh); display: flex; justify-content: center; align-items: center; z-index: 1000; font-size: 1.3rem; margin-top: 40px;">
        <p class="text-center flex items-center gap-2 flex-wrap mt-15">
          <span style="font-size: 20px">Powered by</span>
          <span style="font-family: Nunito; font-weight: bold; font-size: 23px;">FPLHN-UDPM</span>
          <img src="/src/assets/images/logo-udpm-dark.png" alt="logo" style="max-width: 100px; height: auto;" />
        </p>
      </div>
    </div>

  </div>

  <!-- Modal chọn cơ sở -->
  <FacilityModal :isOpen="isFacilityModalOpen" :loginType="loginType" @close="isFacilityModalOpen = false" />
  <!-- đăng ký sinh viên  -->
  <StudentRegisterModal :isOpen="isRegisterModalOpen" @close="isRegisterModalOpen = false" />
  <!-- Footer powered by -->

</template>

<script setup>
import { ref, computed } from 'vue';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';
import { faGoogle } from '@fortawesome/free-brands-svg-icons';
import FacilityModal from './FacilityLoginModal.vue';
import { URL_OAUTH2_GOOGLE_ADMIN } from '@/constants/url';
import backgroundImage from '/images/bg-simple.jpg';
import logo from '/images/Logo_FPT.png';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { ACCOUNT_EXPIRED, ACCOUNT_NOT_ASSIGNED_TO_ANY_PROJECT, ACCOUNT_NOT_ASSIGNED_TO_ANY_PROJECT_MESSAGE, ACCOUNT_NOT_EXIST, ACCOUNT_NOT_EXIST_MESSAGE, ACCOUNT_NOT_HAVE_PERMISSION, ACCOUNT_NOT_HAVE_PERMISSION_MESSAGE, STUDENT_NEEDS_REGISTRATION, STUDENT_NEEDS_REGISTRATION_MESSAGE } from '@/constants/cookie.constants';
import { cookieStorageAction } from '@/utils/storage';
import { toast } from 'vue3-toastify'
import { onMounted } from 'vue';
import StudentRegisterModal from './StudentRegisterModal.vue';
import { faUser } from '@fortawesome/free-solid-svg-icons'; // icon người

const isFacilityModalOpen = ref(false);
const loginType = ref('');


//authen
const authStore = useAuthStore()

//router
const router = useRouter()

const isRegisterModalOpen = ref(false)

onMounted(() => {
  // if (authStore.accessToken) {
  //   const user = getUserInformation(authStore.accessToken)
  //   if (user.typeUse === USER_TYPE.ADMIN) {
  //     console.log("vào đây admin")
  //     router.push('/manage')
  //     return
  //   }
  // }

  const accountNotExistError = cookieStorageAction.get(ACCOUNT_NOT_EXIST)
  const accountNotHavePermission = cookieStorageAction.get(ACCOUNT_NOT_HAVE_PERMISSION)
  const accountNotAssignedToAnyProject = cookieStorageAction.get(ACCOUNT_NOT_ASSIGNED_TO_ANY_PROJECT)
  const accountExpried = cookieStorageAction.get(ACCOUNT_EXPIRED)
  const student_needs_registration = cookieStorageAction.get(STUDENT_NEEDS_REGISTRATION)
  if (accountNotExistError) {
    toast.error(ACCOUNT_NOT_EXIST_MESSAGE)
    cookieStorageAction.remove(ACCOUNT_NOT_EXIST)
  }
  if (accountNotHavePermission) {
    toast.error(ACCOUNT_NOT_HAVE_PERMISSION_MESSAGE)
    cookieStorageAction.remove(ACCOUNT_NOT_HAVE_PERMISSION)
  }
  if (accountNotAssignedToAnyProject) {
    toast.error(ACCOUNT_NOT_ASSIGNED_TO_ANY_PROJECT_MESSAGE)
    cookieStorageAction.remove(ACCOUNT_NOT_ASSIGNED_TO_ANY_PROJECT)
  }
  if (accountExpried) {
    toast.error(ACCOUNT_EXPIRED_MESSAGE)
    cookieStorageAction.remove(ACCOUNT_EXPIRED)
  }
  // tài khoản student ko tồn tại 
  if (student_needs_registration) {
    isRegisterModalOpen.value = true
    toast.info(STUDENT_NEEDS_REGISTRATION_MESSAGE)
    cookieStorageAction.remove(STUDENT_NEEDS_REGISTRATION)
  }
})

const handleRedirectLoginADMIN = () => {
  window.location.href = URL_OAUTH2_GOOGLE_ADMIN();
};

const openFacilityModal = (type) => {
  loginType.value = type;
  isFacilityModalOpen.value = true;
};

const buttonStyleLogin = computed(() => ({
  backgroundColor: '#0a58ca',
  color: '#fff',
  borderRadius: '12px',
  fontSize: '16px',
}));

</script>
