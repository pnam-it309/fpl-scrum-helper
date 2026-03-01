import { defineStore } from 'pinia'

export const useProjectStore = defineStore('idProject', {
  state: () => ({
    idProject: null,
    idPhase: null
  }),
  actions: {
    setIdProject(project: string, phase: string) {
      this.idProject = project as any
      this.idPhase = phase as any
    },
    clearPhase() {
      ;(this.idProject = null), (this.idPhase = null)
    }
  }
})
