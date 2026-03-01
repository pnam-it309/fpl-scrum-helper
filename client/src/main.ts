import { router } from '@/routes/router'
import { VueQueryPlugin } from '@tanstack/vue-query'
import Antd from 'ant-design-vue'
import 'ant-design-vue/dist/reset.css'
import { createPinia } from 'pinia' 
import { createApp } from 'vue'
import './index.scss'
import 'vue3-toastify/dist/index.css'
import './index.css'
import 'jsvectormap/dist/jsvectormap.min.css'
import 'flatpickr/dist/flatpickr.min.css'
import './index.scss'
import './assets/style/style.css'
import 'vue3-toastify/dist/index.css'
import websocketPlugin from "@/services/configsocket/websocketPlugin";
import VueApexCharts from "vue3-apexcharts";

import App from '@/App.vue'
import Vue3Toastify, { ToastContainerOptions } from 'vue3-toastify'
(window as any).global = window
const app = createApp(App)

app.use(websocketPlugin);
app.use(router)
app.use(Antd)
app.use(VueQueryPlugin)
app.use(createPinia())
app.use(VueApexCharts);
app.component('apexchart', VueApexCharts)

app.use(Vue3Toastify, {
  autoClose: 2000,
  position: 'top-right',
  theme: 'light',
  pauseOnHover: true,
  closeOnClick: true,
  transition: 'slide',
  transitionDuration: 100, // 👈 Thời gian animation (ms) — chỉnh nhỏ hơn cho toast trượt nhanh hơn
  style: {
    zIndex: 999999,
  },
} as ToastContainerOptions);


app.mount('#app');

