<script setup lang="ts">
import { onMounted, onUnmounted } from 'vue'
import { websocketService } from './services/configsocket/websocket'
import themeConfig from './theme/themeConfig'

onMounted(() => {
  websocketService.connect(() => {});
})
onUnmounted(() => {
  websocketService.disconnect();
})
</script>

<template>
  <a-config-provider :theme="themeConfig">
    <router-view />
  </a-config-provider>
</template>
