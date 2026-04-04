<template>
  <el-menu class="topbar-menu" :ellipsis="false" :default-active="activeMenu" :active-text-color="theme" mode="horizontal">
    <sidebar-item :key="route.path + index" v-for="(route, index) in topMenus" :item="route" :base-path="route.path" />

    <el-sub-menu index="more" class="el-sub-menu__hide-arrow" v-if="moreRoutes.length > 0">
      <template #title>
        <span>更多菜单</span>
      </template>
      <sidebar-item :key="route.path + index" v-for="(route, index) in moreRoutes" :item="route" :base-path="route.path" />
    </el-sub-menu>
  </el-menu>
</template>

<script setup lang="ts">
import SidebarItem from '../Sidebar/SidebarItem.vue'
import useAppStore from '@/store/modules/app'
import useSettingsStore from '@/store/modules/settings'
import usePermissionStore from '@/store/modules/permission'

const route = useRoute()
const appStore = useAppStore()
const settingsStore = useSettingsStore()
const permissionStore = usePermissionStore()

const sidebarRouters = computed(() => permissionStore.sidebarRouters)
const theme = computed(() => settingsStore.theme)
const device = computed(() => appStore.device)
const activeMenu = computed(() => {
  const { meta, path } = route
  if (meta.activeMenu) {
    return meta.activeMenu
  }
  return path
})

const visibleNumber = ref<number>(5)
const topMenus = computed(() => {
  return permissionStore.sidebarRouters.filter((f: any) => !f.hidden).slice(0, visibleNumber.value)
})
const moreRoutes = computed(() => {
  return permissionStore.sidebarRouters.filter((f: any) => !f.hidden).slice(visibleNumber.value, sidebarRouters.value.length - visibleNumber.value)
})
function setVisibleNumber(): void {
  const width = document.body.getBoundingClientRect().width / 3
  visibleNumber.value = parseInt(String(width / 85))
}

onMounted(() => {
  window.addEventListener('resize', setVisibleNumber)
})
onBeforeUnmount(() => {
  window.removeEventListener('resize', setVisibleNumber)
})

onMounted(() => {
  setVisibleNumber()
})
</script>

<style lang="scss">
/* ===== TopBar (navType 3) horizontal menu reset ===== */

/* Reset sidebar styles that leak into horizontal mode */
.topbar-menu.el-menu--horizontal {
  border-bottom: none !important;
  background: transparent !important;
  display: flex;
  align-items: center;
  gap: 0;

  /* All direct menu items */
  > .el-menu-item,
  > .el-sub-menu {
    float: none !important;
    width: auto !important;
    min-width: auto !important;
    max-width: none !important;
    margin: 0 !important;
    margin-bottom: 0 !important;
    border-radius: 0 !important;
    box-shadow: none !important;
    background: transparent !important;
    flex-shrink: 0;
  }

  > .el-menu-item {
    height: 58px !important;
    line-height: 58px !important;
    padding: 0 8px !important;
    color: var(--navbar-text) !important;
    font-size: 14px;
    font-weight: 600;
    border-bottom: 2px solid transparent;
    transition: all 0.2s ease;

    &:hover {
      background: var(--navbar-hover, rgba(0, 0, 0, 0.04)) !important;
      color: var(--el-color-primary) !important;
    }

    &.is-active {
      color: var(--el-color-primary) !important;
      border-bottom-color: var(--el-color-primary) !important;
      background: transparent !important;
    }

    /* Reset sidebar ::before pseudo-element */
    &::before {
      display: none !important;
    }
  }

  > .el-sub-menu {
    .el-sub-menu__title {
      height: 58px !important;
      line-height: 58px !important;
      padding: 0 8px !important;
      margin: 0 !important;
      color: var(--navbar-text) !important;
      font-size: 14px;
      font-weight: 600;
      border-bottom: 2px solid transparent;
      border-radius: 0 !important;
      width: auto !important;
      min-width: auto !important;
      max-width: none !important;

      &::before {
        display: none !important;
      }
    }

    &:hover > .el-sub-menu__title {
      background: var(--navbar-hover, rgba(0, 0, 0, 0.04)) !important;
      color: var(--el-color-primary) !important;
    }

    &.is-active > .el-sub-menu__title {
      color: var(--el-color-primary) !important;
      border-bottom-color: var(--el-color-primary) !important;
    }
  }

  /* SVG icons in topbar */
  .svg-icon {
    margin-right: 6px !important;
    width: 16px;
    height: 16px;
  }

  /* Menu title text */
  .menu-title {
    display: inline !important;
    white-space: nowrap;
    font-size: inherit;
    font-weight: inherit;
  }

  /* Sub-menu arrow */
  .el-sub-menu__icon-arrow {
    position: static !important;
    margin-left: 6px !important;
    margin-right: 0 !important;
    margin-top: 0 !important;
    display: inline-flex !important;
    font-size: 12px;
  }

  /* Active state icon color */
  .el-menu-item.is-active .svg-icon,
  .el-sub-menu.is-active .svg-icon,
  .el-sub-menu.is-active .el-sub-menu__title span {
    color: var(--el-color-primary);
  }
}
</style>
