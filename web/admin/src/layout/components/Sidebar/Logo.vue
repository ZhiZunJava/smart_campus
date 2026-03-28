<template>
  <div class="sidebar-logo-container" :class="{ 'is-collapse': collapse }">
    <transition name="sidebarLogoFade">
      <router-link v-if="collapse" key="collapse" class="sidebar-logo-link" to="/">
        <div class="sidebar-brand sidebar-brand--collapse" aria-label="校园智学后台">
          <img class="sidebar-brand__image sidebar-brand__image--compact" :src="smallLogo" alt="校园智学后台">
        </div>
      </router-link>
      <router-link v-else key="expand" class="sidebar-logo-link" to="/">
        <div class="sidebar-brand">
          <img class="sidebar-brand__image sidebar-brand__image--full" :src="fullLogo" alt="校园智学后台">
        </div>
      </router-link>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import useSettingsStore from '@/store/modules/settings'

defineProps({
  collapse: {
    type: Boolean,
    required: true
  }
})

const settingsStore = useSettingsStore()
const fullLogo = `${import.meta.env.BASE_URL}logo.png`
const smallLogo = `${import.meta.env.BASE_URL}small-logo.png`

// 获取Logo背景色
const getLogoBackground = computed(() => {
  if (settingsStore.isDark) {
    return 'var(--sidebar-bg)'
  }
  return '#f8fbff'
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

  &.is-collapse {
    .sidebar-logo-link {
      width: 100%;
      justify-content: center;
    }
  }
}

.sidebar-brand {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 58px;
  padding: 0 10px;
}

.sidebar-brand__image {
  display: block;
  object-fit: contain;
  max-width: 100%;
  flex-shrink: 0;
}
.sidebar-brand__image--full {
  width: 146px;
  height: 34px;
}

.sidebar-brand__image--compact {
  width: 100%;
  height: 100%;
}

</style>
