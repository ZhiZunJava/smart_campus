<template>
  <div class="sidebar-logo-container" :class="{ 'collapse': collapse }">
    <transition name="sidebarLogoFade">
      <router-link v-if="collapse" key="collapse" class="sidebar-logo-link" to="/">
        <div class="sidebar-brand sidebar-brand--collapse">
          <i class="sidebar-brand__icon ri-graduation-cap-line"></i>
        </div>
      </router-link>
      <router-link v-else key="expand" class="sidebar-logo-link" to="/">
        <div class="sidebar-brand">
          <i class="sidebar-brand__icon ri-graduation-cap-line"></i>
          <div class="sidebar-brand__text">
            <strong>{{ displayTitle }}</strong>
            <span>{{ subtitle }}</span>
          </div>
        </div>
      </router-link>
    </transition>
  </div>
</template>

<script setup lang="ts">
import useSettingsStore from '@/store/modules/settings'

defineProps({
  collapse: {
    type: Boolean,
    required: true
  }
})

const settingsStore = useSettingsStore()
const displayTitle = computed(() => '校园智学后台')
const subtitle = computed(() => 'Campus Admin')

// 获取Logo背景色
const getLogoBackground = computed(() => {
  if (settingsStore.isDark) {
    return 'var(--sidebar-bg)'
  }
  return '#f8fbff'
})

// 获取Logo文字颜色
const getLogoTextColor = computed(() => {
  if (settingsStore.isDark) {
    return 'var(--sidebar-text)'
  }
  return '#1b3d67'
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
  border-bottom: 1px solid var(--el-border-color-lighter);

  & .sidebar-logo-link {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100%;
    width: 100%;
  }
}

.sidebar-brand {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  width: 100%;
  height: 58px;
  padding: 0 12px;
  color: v-bind(getLogoTextColor);
}

.sidebar-brand__icon {
  font-size: 20px;
  line-height: 1;
  color: inherit;
  flex-shrink: 0;
}

.sidebar-brand__text {
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.sidebar-brand__text strong {
  color: inherit;
  font-size: 17px;
  font-weight: 800;
  line-height: 1.1;
  letter-spacing: .02em;
  white-space: nowrap;
}

.sidebar-brand__text span {
  margin-top: 2px;
  color: var(--el-text-color-secondary);
  font-size: 10px;
  line-height: 1.2;
  white-space: nowrap;
}

.sidebar-brand--collapse {
  justify-content: center;
  padding: 0;
}
</style>
