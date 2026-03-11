<template>
  <div class="sidebar-logo-container" :class="{ 'collapse': collapse }">
    <transition name="sidebarLogoFade">
      <router-link v-if="collapse" key="collapse" class="sidebar-logo-link" to="/">
        <h1 class="sidebar-title sidebar-title--collapse">{{ collapsedTitle }}</h1>
      </router-link>
      <router-link v-else key="expand" class="sidebar-logo-link" to="/">
        <h1 class="sidebar-title">{{ displayTitle }}</h1>
      </router-link>
    </transition>
  </div>
</template>

<script setup lang="ts">
import useSettingsStore from '@/store/modules/settings'
import variables from '@/assets/styles/variables.module.scss'

defineProps({
  collapse: {
    type: Boolean,
    required: true
  }
})

const settingsStore = useSettingsStore()
const sideTheme = computed(() => settingsStore.sideTheme)
const displayTitle = computed(() => '智慧校园')
const collapsedTitle = computed(() => '校园')

// 获取Logo背景色
const getLogoBackground = computed(() => {
  if (settingsStore.isDark) {
    return 'var(--sidebar-bg)'
  }
  if (settingsStore.navType == 3) {
    return variables.menuLightBg
  }
  return sideTheme.value === 'theme-dark' ? variables.menuBg : variables.menuLightBg
})

// 获取Logo文字颜色
const getLogoTextColor = computed(() => {
  if (settingsStore.isDark) {
    return 'var(--sidebar-text)'
  }
  if (settingsStore.navType == 3) {
    return variables.menuLightText
  }
  return sideTheme.value === 'theme-dark' ? '#fff' : variables.menuLightText
})
</script>

<style lang="scss" scoped>
.sidebarLogoFade-enter-active {
  transition: opacity 1.5s;
}

.sidebarLogoFade-enter,
.sidebarLogoFade-leave-to {
  opacity: 0;
}

.sidebar-logo-container {
  position: relative;
  height: 58px;
  line-height: 58px;
  background: v-bind(getLogoBackground);
  text-align: center;
  overflow: hidden;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);

  & .sidebar-logo-link {
    height: 100%;
    width: 100%;

    & .sidebar-title {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      width: 100%;
      height: 58px;
      margin: 0;
      color: v-bind(getLogoTextColor);
      font-weight: 700;
      line-height: 1;
      font-size: 18px;
      letter-spacing: 1px;
      font-family: PingFang SC, Microsoft YaHei, Arial, sans-serif;
      vertical-align: middle;
      white-space: nowrap;
    }

    & .sidebar-title--collapse {
      font-size: 16px;
      letter-spacing: 0.5px;
    }
  }

  &.collapse {
    .sidebar-logo-link {
      justify-content: center;
    }
  }
}
</style>