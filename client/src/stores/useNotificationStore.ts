import { defineStore } from 'pinia';

export const useNotificationStore = defineStore('notification', {
  state: () => ({
    selectedTodoId: null as string | null,
    showTaskDetailModal: false,
  }),
  actions: {
    openModal(todoId: string) {
      this.selectedTodoId = todoId;
      this.showTaskDetailModal = true;
    },
    closeModal() {
      this.selectedTodoId = null;
      this.showTaskDetailModal = false;
    },
  },
});
