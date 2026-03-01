import { defineStore } from 'pinia'

export const useFilterStore = defineStore('filter', {
  state: () => ({
    memberId: '',
    noMember: false,
    membersSelected: [] as string[],
    myTodo: false,
    dueDate: [] as string[],
    labels: [] as string[],
    labelsSelected: [] as string[],
    searchText: '',
    typesSelected: [] as string[]
  }),

  getters: {
    totalSelected(state): number {
      let count = 0

      if (state.noMember) count++
      if (state.myTodo) count++
      if (state.memberId) count++
      count += state.membersSelected.length

      count += state.dueDate.length

      count += state.labels.length
      count += state.labelsSelected.length

      if (state.searchText.trim().length > 0) count++

      count += state.typesSelected.length

      return count
    }
  },

  actions: {
    toggleNoMember() {
      this.noMember = !this.noMember
    },
    toggleMyTodo() {
      this.myTodo = !this.myTodo
    },
    setDueDate(dueDate: string[]) {
      this.dueDate = dueDate
    },
    toggleLabel(label: string) {
      const index = this.labels.indexOf(label)
      if (index === -1) {
        this.labels.push(label)
      } else {
        this.labels.splice(index, 1)
      }
    },
    setLabelsSelected(labels: string[]) {
      this.labelsSelected = labels
    },
    setSearchText(searchText: string) {
      this.searchText = searchText
    },
    resetFilters() {
      this.noMember = false
      this.dueDate = []
      this.labels = []
      this.labelsSelected = []
      this.searchText = ''
      this.memberId = ''
      this.membersSelected = []
      this.myTodo = false
      this.typesSelected = []
    }
  }
})
