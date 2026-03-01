import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useSortStore = defineStore('sortStore', () => {
  const selectedSort = ref<string>('') 

  function setSelectedSort(value: string) {
    selectedSort.value = value
  }

  return { selectedSort, setSelectedSort }
})
