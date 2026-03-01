<template>
  <div class="redirect-container">
    <p class="redirect-message">
      <a-spin size="large" />
    </p>
  </div>
</template>

<script lang="ts" setup>
import { ROUTES_CONSTANTS } from '@/constants/path'
import { ROLES } from '@/constants/roles'
import { useAuthStore } from '@/stores/auth'
import { getUserInformation } from '@/utils/token.helper'
import { onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()

const router = useRouter()

const authStore = useAuthStore()

const { state } = route.query

onMounted(() => {
  if (state) {
    const decodedState = atob(state as string)

    const { accessToken, refreshToken } = JSON.parse(decodedState)

    const user = getUserInformation(accessToken)

    authStore.login({
      user,
      accessToken,
      refreshToken
    })

    if (user.rolesCodes.includes(ROLES.ADMIN) && user.roleScreen === ROLES.ADMIN) {
      router.push({ name: ROUTES_CONSTANTS.ADMIN.children.DEPARTMENT.name })
      return
    } else if (user.rolesCodes.includes(ROLES.MANAGE) && user.roleScreen === ROLES.MANAGE) {
      router.push({ name: ROUTES_CONSTANTS.MANAGE.children.PROJECT.name })
      return
    }
    router.push({ name: ROUTES_CONSTANTS.MEMBER.children.MYPROJECT.name })
    return
  }
  router.push({ name: ROUTES_CONSTANTS.LOGIN.name })
})
</script>

<style scoped>
.redirect-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f0f0;
}

.redirect-message {
  font-size: 1.5rem;
  color: #333;
  font-weight: bold;
}
</style>
