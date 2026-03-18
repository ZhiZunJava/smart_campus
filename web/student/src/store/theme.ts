import { defineStore } from 'pinia'

const ThemeStorageKey = 'portal-theme-name'

export type PortalThemeName = 'campus-blue'

interface PaletteConfig {
  name: PortalThemeName
  label: string
  color: string
}

interface PortalThemeState {
  activeTheme: PortalThemeName
  themes: PaletteConfig[]
}

const builtInThemes: PaletteConfig[] = [
  { name: 'campus-blue', label: '校园蓝', color: '#006EFF' },
]

function syncThemeToDom(themeName: PortalThemeName) {
  document.documentElement.dataset.theme = themeName
}

function resolvePreferredTheme(): PortalThemeName {
  const saved = localStorage.getItem(ThemeStorageKey)
  if (builtInThemes.some((item) => item.name === saved)) {
    return saved as PortalThemeName
  }
  return 'campus-blue'
}

const usePortalThemeStore = defineStore('portal-theme', {
  state: (): PortalThemeState => ({
    activeTheme: 'campus-blue',
    themes: builtInThemes,
  }),
  getters: {
    currentTheme(state) {
      return state.themes.find((item) => item.name === state.activeTheme) || state.themes[0]
    },
  },
  actions: {
    initTheme() {
      this.activeTheme = resolvePreferredTheme()
      syncThemeToDom(this.activeTheme)
    },
    setTheme(themeName: PortalThemeName) {
      this.activeTheme = themeName
      localStorage.setItem(ThemeStorageKey, themeName)
      syncThemeToDom(themeName)
    },
  },
})

export default usePortalThemeStore
