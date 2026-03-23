import { defineStore } from 'pinia'

export interface TabItem {
  title: string
  path: string
  name?: string
  closable?: boolean
}

export const useTabsStore = defineStore('portal-tabs', {
  state: () => ({
    visitedTabs: [] as TabItem[],
    activeTabPath: ''
  }),
  actions: {
    addTab(tab: TabItem) {
      if (tab.path === '/student/dashboard' || tab.path === '/' || tab.path === '/teacher/dashboard' || tab.path === '/parent/dashboard') {
        this.activeTabPath = tab.path
        return
      }
      if (!this.visitedTabs.find(t => t.path === tab.path)) {
        this.visitedTabs.push({ ...tab, closable: true })
      }
      this.activeTabPath = tab.path
    },
    removeTab(path: string) {
      const index = this.visitedTabs.findIndex(t => t.path === path)
      if (index > -1) {
        this.visitedTabs.splice(index, 1)
        if (this.activeTabPath === path) {
          // If we closed the active tab, go to the previous one
          const nextTab = this.visitedTabs[index - 1] || this.visitedTabs[index]
          if (nextTab) {
            this.activeTabPath = nextTab.path
          } else {
            // Default fallback to dashboard is handled by component
            this.activeTabPath = ''
          }
        }
      }
    },
    setActiveTab(path: string) {
      this.activeTabPath = path
    },
    closeAll() {
      this.visitedTabs = this.visitedTabs.filter(t => !t.closable)
      this.activeTabPath = ''
    }
  },
  getters: {
    cachedViews: (state) => {
      return state.visitedTabs.map(t => t.name).filter(Boolean) as string[]
    }
  }
})
