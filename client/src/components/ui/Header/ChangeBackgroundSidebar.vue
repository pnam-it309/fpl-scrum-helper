<template>
    <!-- :style="{
        backgroundImage: selectedBackground.image ? `url('${selectedBackground.image}')` : 'none',
        backgroundColor: selectedBackground.color || 'transparent'
      }" -->
    <div class="min-h-screen bg-cover bg-center pt-6 transition-all duration-300">
        <div class="flex justify-end gap-4 mb-4 px-6">
  <!-- Nút chọn ảnh -->
  <button
    @click="toggleImageOptions"
    class="flex items-center gap-2 bg-white px-4 py-2 rounded-xl shadow-md transition-all duration-200 hover:scale-105 hover:bg-blue-50"
    :class="{ 'ring-2 ring-blue-400': showImageOptions }"
  >
    <img src="https://img.icons8.com/ios/50/image.png" alt="Ảnh" class="w-6 h-6" />
    <span class="text-sm font-medium text-gray-700">Chọn ảnh nền</span>
  </button>

  <!-- Nút chọn màu -->
  <button
    @click="toggleColorOptions"
    class="flex items-center gap-2 bg-white px-4 py-2 rounded-xl shadow-md transition-all duration-200 hover:scale-105 hover:bg-yellow-50"
    :class="{ 'ring-2 ring-blue-400': showColorOptions }"
  >
    <span class="text-lg">🎨</span>
    <span class="text-sm font-medium text-gray-700">Chọn màu nền</span>
  </button>
</div>


        <!-- Danh sách ảnh -->
        <div v-if="showImageOptions" class="grid grid-cols-2 gap-3 bg-white rounded mb-4 px-4 py-2">
            <div v-for="(img, idx) in backgroundImages" :key="idx" class="relative group cursor-pointer"
                @click="selectBackgroundImage(img.url)">
                <img :src="img.url" class="w-full h-22 object-cover rounded border-4 transition" :class="{
                    'border-blue-500': selectedBackground.image === img.url,
                    'border-transparent': selectedBackground.image !== img.url
                }" />
                <div
                    class="absolute bottom-0 left-0 w-full bg-black bg-opacity-60 text-white text-center text-sm py-1 opacity-0 group-hover:opacity-100 transition">
                    {{ img.name }}
                </div>
            </div>
        </div>

        <!-- Danh sách màu -->
        <div v-if="showColorOptions" class="flex gap-3 flex-wrap pt-4 rounded-xl mb-4 mx-4 justify-center">
            <div v-for="(color, idx) in backgroundColors" :key="idx" class="relative group">
                <!-- Nút chọn màu -->
                <div :style="{ backgroundColor: color }"
                    class="w-14 h-14 rounded-lg cursor-pointer border-4 transition-transform duration-200 ease-in-out transform hover:scale-110"
                    :class="{
                        'border-red-100': selectedBackground.color === color,
                        'border-white': selectedBackground.color !== color
                    }" @click="selectBackgroundColor(color)"></div>

                <!-- Tooltip hiện lên phía trên -->
                <div
                    class="absolute bottom-full mb-2 left-1/2 transform -translate-x-1/2 bg-gray-800 text-white text-xs px-2 py-1 rounded opacity-0 group-hover:opacity-100 transition duration-200 whitespace-nowrap z-10">
                    {{ color }}
                </div>
            </div>
        </div>

        <div class="text-white text-xl font-semibold px-4">
            Đây là phần nội dung trên nền
        </div>
    </div>
</template>

<script setup lang="ts">
import { webSocketImage } from '@/services/service/member/image.socket'
import { ref } from 'vue'
import { useRoute } from 'vue-router'
const route = useRoute()
const showImageOptions = ref(false)
const showColorOptions = ref(false)

const selectedBackground = ref<{ image: string | null; color: string | null }>({
    image: null,
    color: '#4CAF50'
})

const backgroundImages = [
    { url: 'https://a-static.besthdwallpaper.com/sunrise-anime-scenery-art-wallpaper-2560x1600-90530_7.jpg', name: 'Thành phố' },
    { url: 'https://wallpaper-mania.com/wp-content/uploads/2018/09/High_resolution_wallpaper_background_ID_77701599633.jpg', name: 'Hoàng hôn trên núi' },
    { url: 'https://khoinguonsangtao.vn/wp-content/uploads/2022/07/hinh-nen-desktop-phong-canh-full-hd.jpg', name: 'Không gian' },
    { url: 'https://scr.vn/wp-content/uploads/2020/07/%E1%BA%A3nh-n%E1%BB%81n-c%E1%BB%B1c-%C4%91%E1%BB%99c-d%C3%A1o.jpg', name: 'Rừng mùa đông' },
    { url: 'https://anhdepfree.com/wp-content/uploads/2018/10/hinh-nen-phong-canh-thien-nhien-04.jpg', name: 'Cánh đồng lúa' },
    { url: 'https://i.pinimg.com/originals/55/8c/ee/558cee160eb3ee39bb6e45b09f60a3ee.png', name: 'Trăng tròn' },
    { url: 'https://khoinguonsangtao.vn/wp-content/uploads/2022/07/hinh-nen-may-tinh-thien-nhien-tuyet-tac-dong-co-xanh.jpg', name: 'Thiên nhiên hùng vĩ' },
    { url: 'https://1.bp.blogspot.com/-v1cUEuJCeBk/VvAQaxf4dqI/AAAAAAAAAaY/HFvg7gPPxRYN_Po57dEIBJFOYPicFKBzQ/s1600/hinh-nen-may-tinh-dep-nhat-04.jpg', name: 'Rừng đầy hoa' },
    { url: 'https://img5.thuthuatphanmem.vn/uploads/2021/11/17/anh-nen-dong-song-binh-yen_111415123.jpg', name: 'Thác nước rừng sâu' },
    { url: 'https://cdn.sforum.vn/sforum/wp-content/uploads/2022/12/hinh-nen-powerpoint-tet-8-4.png', name: 'Sa mạc vàng' },
    { url: 'https://img.pikbest.com/origin/09/20/82/55zpIkbEsT79X.jpg!w700wp', name: 'Hoa nở mùa xuân' },
    { url: 'https://images3.alphacoders.com/823/82317.jpg', name: 'Bầu trời bình minh' },
    { url: 'https://img6.thuthuatphanmem.vn/uploads/2022/07/19/hinh-nen-binh-minh-viet-nam-4k_025242455.jpg', name: 'Sương mù' },
    { url: 'https://png.pngtree.com/background/20230614/original/pngtree-tree-on-the-green-field-wallpaper-128x256-hd-1920x1080-desktop-hd-picture-image_3534543.jpg', name: 'Tuyết phủ trắng xóa' },
    { url: 'https://anhdepfree.com/wp-content/uploads/2020/11/anh-nen-3d-cho-desktop.jpg', name: 'Rừng thu yên tĩnh' },
    { url: 'https://img4.thuthuatphanmem.vn/uploads/2020/05/13/anh-nen-phong-canh-4k_062609928.jpg', name: 'Núi hoang vu' },
    { url: 'https://anhdepfree.com/wp-content/uploads/2018/12/bo-hinh-nen-may-tinh-full-hd-46.jpg', name: 'Cảnh đẹp' },
    { url: 'https://thuthuatnhanh.com/wp-content/uploads/2022/04/Hinh-nen-mat-bien.jpg', name: 'Bờ biển' },
    { url: 'https://img4.thuthuatphanmem.vn/uploads/2020/05/13/anh-nen-desktop-4k_062607771.jpg', name: 'Lắng đọng' },
    { url: 'https://vn-live-01.slatic.net/p/49f629287dcab5c35fcd946bc8ec9848.jpg', name: 'Cánh đồng hoa oải hương' },
    { url: 'https://png.pngtree.com/background/20211217/original/pngtree-fresh-and-beautiful-blue-sky-and-white-clouds-photography-map-picture-image_1560337.jpg', name: 'Bình minh trên hồ' },
    { url: 'https://vn-test-11.slatic.net/p/041afff639d3c6a5c21b68fa84cb117c.jpg', name: 'Bầu trời đầy sao' },
    { url: 'http://thuthuatphanmem.vn/uploads/2018/04/10/hinh-nen-thung-lung-nui-doi-dep_052339827.jpg', name: 'Cầu gỗ xuyên rừng' },
    { url: 'https://studiochupanhdep.com/Upload/Newsimages/phong-khach-san-tt-studio.jpg', name: 'Sa mạc huyền bí' },
    { url: 'https://vn-live-01.slatic.net/p/2e24fb05cb63de74db5346a79781fbcc.jpg', name: 'Thảo nguyên xanh' },
    { url: 'http://viettelinternet24h.com/wp-content/uploads/2022/08/img_630afc915a2d0.jpg', name: 'Đường đèo sương mù' }
]


const backgroundColors = [
  '#4CAF50', '#2196F3', '#FF5722', '#9C27B0', '#f1c40f', '#34495e', '#E91E63', '#009688',
  '#FF9800', '#3F51B5', '#673AB7', '#00BCD4', '#8BC34A', '#FFEB3B', '#607D8B', '#9E9E9E', '#CDDC39',
  '#F44336', '#1ABC9C', '#2ECC71', '#2980B9', '#8E44AD', '#D35400', '#16A085',
];


function toggleImageOptions() {
    showImageOptions.value = !showImageOptions.value
    showColorOptions.value = false
}

function toggleColorOptions() {
    showColorOptions.value = !showColorOptions.value
    showImageOptions.value = false
}

function selectBackgroundImage(img: string) {
    selectedBackground.value.image = img
    selectedBackground.value.color = null
    const payload = {
        idProject: route.params.id,
        backgroundColor: selectedBackground.value.color,
        backgroundImage: selectedBackground.value.image
    }
    webSocketImage.sendUpdateBackgroundProject(payload)
    // showImageOptions.value = false
}

function selectBackgroundColor(color: string) {
    selectedBackground.value.color = color
    selectedBackground.value.image = null
    const payload = {
        idProject: route.params.id,
        backgroundColor: selectedBackground.value.color,
        backgroundImage: selectedBackground.value.image
    }
    webSocketImage.sendUpdateBackgroundProject(payload)
    // showColorOptions.value = false
}
</script>

<style scoped>
.h-22 {
    height: 88px;
}
</style>