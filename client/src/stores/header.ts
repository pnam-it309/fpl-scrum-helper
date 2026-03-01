
import { defineStore } from 'pinia'

export const useHeaderStore = defineStore('header', {
  state: () => ({
    isHeaderOpen: true, 
  }),

  actions: {
    toggleHeader() {
      this.isHeaderOpen = !this.isHeaderOpen
    },
    openHeader() {
      this.isHeaderOpen = true
    },
    closeHeader() {
      this.isHeaderOpen = false
    },
  }
  
})
