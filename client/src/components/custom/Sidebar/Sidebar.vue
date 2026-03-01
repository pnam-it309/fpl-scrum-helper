<template>
  <li>
    <button
      class="group flex items-center gap-3 w-full px-4 py-3 font-medium rounded-md hover:bg-hoverSideBar dark:hover:bg-gray-700 transition-all"
      :class="{ 'bg-selectedSideBar': selected }"
      @click="$emit('click')"
    >
      <component
        :is="icon"
        class="w-5 h-5"
        :class="{
          'text-iconSideBarSelected': selected,
          'text-iconSideBarIsNoSelected dark:text-gray-300 group-hover:text-iconSideBarSelected':
            !selected
        }"
      />
      <span
        v-if="!collapsed"
        class="text-sm"
        :class="{
          'text-textSideBarSelected font-bold': selected,
          'text-textSideBarIsNoSelected font-bold group-hover:text-textSideBarSelected': !selected
        }"
      >
        {{ label }}
      </span>
    </button>
  </li>
</template>

<script setup>
defineProps({
  icon: String,
  label: String,
  keyName: String,
  collapsed: Boolean,
  selected: Boolean
})
</script>
