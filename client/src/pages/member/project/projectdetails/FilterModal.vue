<template>
  <a-drawer v-model:open="isVisible" title="🔍 Bộ lọc" placement="right" :width="330" :closable="true"
    @close="closeModal" class="custom-drawer">
    <div class="space-y-4">
      <!-- Tìm kiếm -->
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">Tìm kiếm</label>
        <Input v-model:value="filterStore.searchText" @input="updateSearchText" placeholder="Nhập tên thẻ..."
          class="w-full" :style="{ padding: '6px 12px' }" />
        <p class="text-red-500 text-xs mt-1 ml-1">Tìm kiếm theo tên thẻ</p>
      </div>

      <!-- Thành viên -->
      <div>
        <p class="text-sm font-semibold text-gray-700 mb-2">Thành viên</p>
        <div class="space-y-2">
          <label class="flex items-center gap-2 mb-2">
            <input type="checkbox" v-model="filterStore.noMember" @click="toggleNoMember" />
            <img
              src="https://png.pngtree.com/png-vector/20190919/ourlarge/pngtree-user-login-or-authenticate-icon-on-gray-background-flat-icon-ve-png-image_1742031.jpg"
              alt="Avatar" class="w-6 h-6 rounded-full object-cover" />
            <span>Thẻ không có thành viên</span>
          </label>
        </div>
        <div class="space-y-2">
          <label class="flex items-center gap-2 mb-3 ">
            <input type="checkbox" v-model="filterStore.myTodo" @click="toggleMyTodo" />
            <img :src="userLogin.pictureUrl || 'https://ui-avatars.com/api/?name=User'" alt="Avatar"
              class="w-6 h-6 rounded-full object-cover" />
            <span>Thẻ đã chỉ định cho tôi</span>
          </label>
        </div>

        <!-- Combobox chọn thành viên -->
        <div class="flex items-center space-x-2">
          <input type="checkbox" :checked="isAllMembersSelected" :indeterminate="isIndeterminateMembers"
            @change="toggleSelectAllMembers" class="shrink-0" />

          <a-select v-model:value="filterStore.membersSelected" mode="multiple" :max-tag-count="0"
            :max-tag-placeholder="() => `${filterStore.membersSelected.length} thành viên đã chọn`" show-search
            allow-clear placeholder="Chọn hoặc tìm thành viên" :filter-option="filterMemberOption"
            @change="onMemberChange" class="w-full">
            <a-select-option v-for="member in filteredMembers" :key="member.id" :value="member.id">
              <div class="flex items-center gap-2">
                <img :src="member.image || 'https://ui-avatars.com/api/?name=' + member.name"
                  class="w-6 h-6 rounded-full object-cover" />
                <div class="flex flex-col">
                  <span class="text-sm font-medium">{{ member.name }}</span>
                  <span class="text-xs">{{ member.email }}</span>
                </div>
              </div>
            </a-select-option>
          </a-select>
        </div>

      </div>

      <!-- Ngày hạn (Checkbox đa lựa chọn) -->
      <div>
        <p class="text-sm font-semibold text-gray-700 mb-2">Ngày hạn</p>
        <div class="space-y-2">
          <label class="flex items-center gap-2">
            <input type="checkbox" value="no_due" v-model="filterStore.dueDate" />
            ⏰ Thẻ không có ngày hạn
          </label>
          <label class="flex items-center gap-2">
            <input type="checkbox" value="overdue" v-model="filterStore.dueDate" />
            🔴 Thẻ quá hạn
          </label>
          <label class="flex items-center gap-2">
            <input type="checkbox" value="not_done" v-model="filterStore.dueDate" />
            🟠 Thẻ chưa hoàn thành
          </label>
          <label class="flex items-center gap-2">
            <input type="checkbox" value="done_early" v-model="filterStore.dueDate" />
            ✅ Thẻ đã hoàn thành (sớm)
          </label>
          <label class="flex items-center gap-2">
            <input type="checkbox" value="done_late" v-model="filterStore.dueDate" />
            🔵 Thẻ đã hoàn thành (muộn)
          </label>
        </div>
      </div>

      <!-- PHẦN COMBOBOX VÀ CHECKBOX LOẠI CÔNG VIỆC  -->
      <!-- Loại công việc (Type Todo) -->
      <div>
        <p class="text-sm font-semibold text-gray-700 mb-2">Loại công việc</p>

        <div class="flex items-center space-x-2">
          <input type="checkbox" :checked="isAllTypesSelected" :indeterminate="isIndeterminateTypes"
            @change="toggleSelectAllTypes" class="shrink-0" />

          <a-select v-model:value="filterStore.typesSelected" mode="multiple" :max-tag-count="0"
            :max-tag-placeholder="() => `${filterStore.typesSelected.length} loại đã chọn`" show-search allow-clear
            placeholder="Chọn hoặc tìm loại công việc" :filter-option="filterTypeOption" @change="onTypeChange"
            class="w-full">
            <a-select-option v-for="type in filteredTypes" :key="type.type" :value="type.type">
              <div class="flex items-center gap-2">
                <component :is="getTypeIcon(type.type)" :style="{ color: getTypeColor(type.type) }"
                  class="text-base flex-shrink-0" />
                <span>{{ type.type }}</span>
              </div>
            </a-select-option>

          </a-select>
        </div>
      </div>

      <!-- PHẦN COMBOBOX VÀ CHECKBOX NHÃN -->

      <!-- Nhãn -->
      <div>
        <p class="text-sm font-semibold text-gray-700 mb-2">Nhãn</p>

        <!-- Checkbox “Không có nhãn” -->
        <div class="space-y-2">
          <label class="flex items-center gap-2 mb-2">
            <input type="checkbox" value="no_label" v-model="filterStore.labels" />
            <img src="https://img.pikbest.com/png-images/qianku/cartoon-vector-label-icon_2102996.png!bw700"
              alt="Avatar" class="w-6 h-6 rounded-full object-cover" />
            Thẻ không có nhãn
          </label>
        </div>

        <!-- Combobox chọn nhãn -->
        <div class="flex items-center space-x-2">
          <!-- Checkbox Chọn tất cả -->
          <input type="checkbox" :checked="isAllLabelsSelected" :indeterminate="isIndeterminate"
            @change="toggleSelectAllLabels" class="shrink-0" />

          <!-- Combobox chọn nhãn -->
          <a-select v-model:value="filterStore.labelsSelected" mode="multiple" :max-tag-count="0"
            :max-tag-placeholder="() => `${filterStore.labelsSelected.length} nhãn đã chọn`" show-search allow-clear
            placeholder="Chọn hoặc tìm nhãn " :filter-option="filterOption" @change="onLabelChange" class="w-full">
            <a-select-option v-for="label in filteredLabels" :key="label.id" :value="label.id">
              <span :style="{
                backgroundColor: label.colorLabel,
                color: 'white',
                padding: '4px 8px',
                borderRadius: '4px',
              }">
                {{ label.name }}
              </span>
            </a-select-option>
          </a-select>
        </div>
      </div>

    </div>
  </a-drawer>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { Input } from 'ant-design-vue'; // Sử dụng đúng Input
import { useFilterStore } from '@/stores/filterStore'
import { useRoute } from 'vue-router'
import { getAllLabelByIdProject, getAllLabelSearchByIdProject, MBMeLabelResponse } from '@/services/api/member/projectdetail/melabelproject.api';
import { localStorageAction } from '@/utils/storage';
import { USER_INFO_STORAGE_KEY } from '@/constants/storagekey';
import { getAllFilterMemberProject, MBMeMemberProjectResponse } from '@/services/api/member/projectdetail/memberproject.api';
import { getAllTypeProject, MBTypeProjectResponse } from '@/services/api/member/projectdetail/typeproject.api';
import { getTypeColor, getTypeIcon } from '@/utils/commom.helper';
const route = useRoute()
const props = defineProps<{ isOpen: boolean }>()
const emit = defineEmits(['close'])
const userLogin = localStorageAction.get(USER_INFO_STORAGE_KEY)
const isVisible = ref(props.isOpen)

watch(() => props.isOpen, (val) => isVisible.value = val)
watch(isVisible, (val) => {
  if (!val) emit('close')
})

const closeModal = () => {
  isVisible.value = false
}

const filterStore = useFilterStore()

const toggleNoMember = () => {
  filterStore.toggleNoMember()
}
const toggleMyTodo = () => {
  filterStore.toggleMyTodo()
}

const updateSearchText = () => {
  filterStore.setSearchText(filterStore.searchText)
}

const selectedOnlyTypes = computed(() => filterStore.typesSelected || [])

const isAllTypesSelected = computed(() => {
  const all = typeOptions.value.map(t => t.id)
  return all.every(id => selectedOnlyTypes.value.includes(id))
})

const isIndeterminateTypes = computed(() => {
  const selected = selectedOnlyTypes.value
  const all = typeOptions.value.map(t => t.id)
  return selected.length > 0 && selected.length < all.length
})

const toggleSelectAllTypes = () => {
  const all = typeOptions.value.map(t => t.id)
  filterStore.typesSelected = isAllTypesSelected.value ? [] : [...all]
}

const onTypeChange = (newSelected: string[]) => {
  filterStore.typesSelected = [...newSelected]
}

const filteredTypes = computed(() => {
  return typeOptions.value.filter(type =>
    type.type.toLowerCase().includes(filterStore.searchText.toLowerCase())
  )
})

const filterTypeOption = (input: string, option: any) => {
  const typeId = option.value
  const type = typeOptions.value.find(t => t.id === typeId)
  return type && type.type.toLowerCase().includes(input.toLowerCase())
}


const labelOptions = ref<MBMeLabelResponse[]>([])
const typeOptions = ref<MBTypeProjectResponse[]>([])

const selectedOnlyLabels = computed(() =>
  filterStore.labelsSelected
)

const isAllLabelsSelected = computed(() => {
  const all = labelOptions.value.map(o => o.id)
  return all.every(label => selectedOnlyLabels.value.includes(label))
})

const isIndeterminate = computed(() => {
  const selected = selectedOnlyLabels.value
  const all = labelOptions.value.map(o => o.id)
  return selected.length > 0 && selected.length < all.length
})

const toggleSelectAllLabels = () => {
  const all = labelOptions.value.map(opt => opt.id)
  filterStore.labelsSelected = isAllLabelsSelected.value ? [] : [...all]
}

const onLabelChange = (newSelected: string[]) => {
  filterStore.labelsSelected = [...newSelected]
}

// Hàm lọc nhãn
const filteredLabels = computed(() => {
  return labelOptions.value.filter(label =>
    label.name.toLowerCase().includes(filterStore.searchText.toLowerCase())
  )
})

// Hàm lọc cho a-select
const filterOption = (input: string, option: any) => {
  const labelId = option.value;
  const label = labelOptions.value.find(label => label.id === labelId);
  return label && label.name.toLowerCase().includes(input.toLowerCase());
}

const fetchLabelProject = async () => {
  try {
    const param = {
      idProject: route.params.id
    };
    const response = await getAllLabelSearchByIdProject(param);
    labelOptions.value = response.data;

  } catch (error) {
    console.error("Lỗi khi lấy danh sách label todo:", error);
  }
};

const fetchTypeProject = async () => {
  try {

    const response = await getAllTypeProject();
    typeOptions.value = response.data;
  } catch (error) {
    console.error("Lỗi khi lấy danh sách type todo:", error);
  }
};

onMounted(() => {
  fetchLabelProject()
  fetchMembers()
  fetchTypeProject()
})

const memberOptions = ref<MBMeMemberProjectResponse[]>([])

const fetchMembers = async () => {
  try {
    const response = await getAllFilterMemberProject(route.params.id as string)
    memberOptions.value = response.data

  } catch (error) {
    console.error("Lỗi khi lấy danh sách thành viên:", error)
  }
}

const selectedMembers = computed(() => filterStore.membersSelected || [])

const isAllMembersSelected = computed(() => {
  const all = memberOptions.value.map(m => m.id)
  return all.every(id => selectedMembers.value.includes(id))
})

const isIndeterminateMembers = computed(() => {
  const selected = selectedMembers.value
  const all = memberOptions.value.map(m => m.id)
  return selected.length > 0 && selected.length < all.length
})

const toggleSelectAllMembers = () => {
  const all = memberOptions.value.map(m => m.id)
  filterStore.membersSelected = isAllMembersSelected.value ? [] : [...all]
}

const onMemberChange = (newSelected: string[]) => {
  filterStore.membersSelected = [...newSelected]
}

const filteredMembers = computed(() => {
  return memberOptions.value.filter(member =>
    member.name.toLowerCase().includes(filterStore.searchText.toLowerCase()) ||
    member.email.toLowerCase().includes(filterStore.searchText.toLowerCase())
  )
})

const filterMemberOption = (input: string, option: any) => {
  const memberId = option.value
  const member = memberOptions.value.find(m => m.id === memberId)
  return member &&
    (member.name.toLowerCase().includes(input.toLowerCase()) ||
      member.email.toLowerCase().includes(input.toLowerCase()))
}

</script>
<style scoped>
::v-deep .ant-select-selection-item {
  background-color: #f5f5f5 !important;
  /* Màu nền xám nhẹ */
  color: #333 !important;
  /* Màu chữ đậm nhẹ */
  border-radius: 9999px;
  padding: 2px 10px;
  font-size: 13px;
}
</style>
