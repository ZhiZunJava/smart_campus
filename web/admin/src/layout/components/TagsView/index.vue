<template>
  <div id="tags-view-container" class="tags-view-container">
    <scroll-pane ref="scrollPaneRef" class="tags-view-wrapper" @scroll="handleScroll">
      <router-link
        v-for="tag in visitedViews"
        :key="tag.path"
        :data-path="tag.path"
        :class="{ 'active': isActive(tag), 'has-icon': tagsIcon }"
        :to="{ path: tag.path, query: tag.query, fullPath: tag.fullPath }"
        class="tags-view-item"
        :style="activeStyle(tag)"
        @click.middle="!isAffix(tag) ? closeSelectedTag(tag) : ''"
        @contextmenu.prevent="openMenu(tag, $event)"
      >
        <svg-icon v-if="tagsIcon && tag.meta && tag.meta.icon && tag.meta.icon !== '#'" :icon-class="tag.meta.icon" />
        {{ tag.title }}
        <span v-if="!isAffix(tag)" @click.prevent.stop="closeSelectedTag(tag)">
          <close class="el-icon-close" style="width: 1em; height: 1em;vertical-align: middle;" />
        </span>
      </router-link>
    </scroll-pane>
    <ul v-show="visible" :style="{ left: left + 'px', top: top + 'px' }" class="contextmenu">
      <li @click="refreshSelectedTag(selectedTag)">
        <refresh-right style="width: 1em; height: 1em;" /> 刷新页面
      </li>
      <li v-if="!isAffix(selectedTag)" @click="closeSelectedTag(selectedTag)">
        <close style="width: 1em; height: 1em;" /> 关闭当前
      </li>
      <li @click="closeOthersTags">
        <circle-close style="width: 1em; height: 1em;" /> 关闭其他
      </li>
      <li v-if="!isFirstView()" @click="closeLeftTags">
        <back style="width: 1em; height: 1em;" /> 关闭左侧
      </li>
      <li v-if="!isLastView()" @click="closeRightTags">
        <right style="width: 1em; height: 1em;" /> 关闭右侧
      </li>
      <li @click="closeAllTags(selectedTag)">
        <circle-close style="width: 1em; height: 1em;" /> 全部关闭
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import ScrollPane from './ScrollPane.vue'
import { getNormalPath } from '@/utils/ruoyi'
import useTagsViewStore from '@/store/modules/tagsView'
import useSettingsStore from '@/store/modules/settings'
import usePermissionStore from '@/store/modules/permission'

const visible = ref<boolean>(false)
const top = ref<number>(0)
const left = ref<number>(0)
const selectedTag = ref<any>({})
const affixTags = ref<any[]>([])
const scrollPaneRef = ref<any>(null)

const { proxy } = getCurrentInstance()
const route = useRoute()
const router = useRouter()

const visitedViews = computed(() => useTagsViewStore().visitedViews)
const routes = computed(() => usePermissionStore().routes)
const theme = computed(() => useSettingsStore().theme)
const tagsIcon = computed(() => useSettingsStore().tagsIcon)

watch(route, () => {
  addTags()
  moveToCurrentTag()
})

watch(visible, (value: boolean) => {
  if (value) {
    document.body.addEventListener('click', closeMenu)
  } else {
    document.body.removeEventListener('click', closeMenu)
  }
})

onMounted(() => {
  initTags()
  addTags()
})

function isActive(r: any): boolean {
  return r.path === route.path
}

function activeStyle(tag: any): Record<string, string> {
  if (!isActive(tag)) return {}
  return {
    "background-color": theme.value,
    "border-color": theme.value,
    "color": "#ffffff"
  }
}

function isAffix(tag: any): boolean {
  return tag.meta && tag.meta.affix
}

function isFirstView(): boolean {
  try {
    return selectedTag.value.fullPath === '/index' || selectedTag.value.fullPath === visitedViews.value[1].fullPath
  } catch (err) {
    return false
  }
}

function isLastView(): boolean {
  try {
    return selectedTag.value.fullPath === visitedViews.value[visitedViews.value.length - 1].fullPath
  } catch (err) {
    return false
  }
}

function filterAffixTags(routes: any[], basePath = ''): any[] {
  const tags: any[] = []
  routes.forEach(route => {
    if (route.meta && route.meta.affix) {
      const tagPath = getNormalPath(basePath + '/' + route.path)
      tags.push({
        fullPath: tagPath,
        path: tagPath,
        name: route.name,
        meta: { ...route.meta }
      })
    }
    if (route.children) {
      const tempTags = filterAffixTags(route.children, route.path)
      if (tempTags.length >= 1) {
        tags.push(...tempTags)
      }
    }
  })
  return tags
}

function initTags(): void {
  const res = filterAffixTags(routes.value)
  affixTags.value = res
  for (const tag of res) {
    // Must have tag name
    if (tag.name) {
       useTagsViewStore().addVisitedView(tag)
    }
  }
}

function addTags(): void {
  const { name } = route
  if (name) {
    useTagsViewStore().addView(route)
  }
}

function moveToCurrentTag(): void {
  nextTick(() => {
    for (const r of visitedViews.value) {
      if (r.path === route.path) {
        scrollPaneRef.value?.moveToTarget(r)
        // when query is different then update
        if (r.fullPath !== route.fullPath) {
          useTagsViewStore().updateVisitedView(route)
        }
      }
    }
  })
}

function refreshSelectedTag(view: any): void {
  proxy.$tab.refreshPage(view)
  if (route.meta.link) {
    useTagsViewStore().delIframeView(route)
  }
}

function closeSelectedTag(view: any): void {
  proxy.$tab.closePage(view).then(({ visitedViews }: any) => {
    if (isActive(view)) {
      toLastView(visitedViews, view)
    }
  })
}

function closeRightTags(): void {
  proxy.$tab.closeRightPage(selectedTag.value).then((visitedViews: any) => {
    if (!visitedViews.find((i: any) => i.fullPath === route.fullPath)) {
      toLastView(visitedViews)
    }
  })
}

function closeLeftTags(): void {
  proxy.$tab.closeLeftPage(selectedTag.value).then((visitedViews: any) => {
    if (!visitedViews.find((i: any) => i.fullPath === route.fullPath)) {
      toLastView(visitedViews)
    }
  })
}

function closeOthersTags(): void {
  router.push(selectedTag.value).catch(() => { })
  proxy.$tab.closeOtherPage(selectedTag.value).then(() => {
    moveToCurrentTag()
  })
}

function closeAllTags(view: any): void {
  proxy.$tab.closeAllPage().then(({ visitedViews }: any) => {
    if (affixTags.value.some((tag: any) => tag.path === route.path)) {
      return
    }
    toLastView(visitedViews, view)
  })
}

function toLastView(visitedViews: any[], view?: any): void {
  const latestView = visitedViews.slice(-1)[0]
  if (latestView) {
    router.push(latestView.fullPath)
  } else {
    // now the default is to redirect to the home page if there is no tags-view,
    // you can adjust it according to your needs.
    if (view && view.name === 'Dashboard') {
      // to reload home page
      router.replace({ path: '/redirect' + view.fullPath })
    } else {
      router.push('/')
    }
  }
}

function openMenu(tag: any, e: MouseEvent): void {
  const menuMinWidth = 132
  const menuMinHeight = 220
  const containerRect = proxy.$el.getBoundingClientRect()
  const offsetWidth = proxy.$el.offsetWidth
  const offsetHeight = proxy.$el.offsetHeight
  const maxLeft = Math.max(offsetWidth - menuMinWidth - 8, 0)
  const maxTop = Math.max(offsetHeight - menuMinHeight - 8, 0)
  const l = e.clientX - containerRect.left + 12
  const t = e.clientY - containerRect.top + 12

  left.value = l > maxLeft ? maxLeft : l
  top.value = t > maxTop ? maxTop : t
  visible.value = true
  selectedTag.value = tag
}

function closeMenu(): void {
  visible.value = false
}

function handleScroll(): void {
  closeMenu()
}
</script>

<style lang="scss" scoped>
.tags-view-container {
height: 40px;
width: 100%;
background: var(--tags-bg, rgba(255, 255, 255, 0.84));
border-bottom: 1px solid var(--tags-item-border, #d8dce5);
box-shadow: none;

  .tags-view-wrapper {
    .tags-view-item {
      display: inline-flex;
      align-items: center;
      position: relative;
      cursor: pointer;
      height: 30px;
      line-height: 30px;
      border: 1px solid var(--tags-item-border, #d8dce5);
      color: var(--tags-item-text, #495060);
      background: var(--tags-item-bg, #fff);
      padding: 0 12px;
      font-size: 12px;
      border-radius: 6px;
      margin-left: 6px;
      margin-top: 5px;
      transition: all 0.2s ease;

      &:first-of-type {
        margin-left: 12px;
      }

      &:last-of-type {
        margin-right: 12px;
      }

      &:hover {
        background: var(--tags-item-hover, #f4f8ff);
        border-color: var(--el-color-primary-light-8);
      }

      &.active {
        color: #ffffff;
        box-shadow: none;

        &::before {
          content: '';
          background: rgba(255, 255, 255, 0.92);
          display: inline-block;
          width: 6px;
          height: 6px;
          border-radius: 50%;
          position: relative;
          margin-right: 6px;
        }
      }
    }
  }

  .tags-view-item.active.has-icon::before {
    content: none !important;
  }

  .contextmenu {
    margin: 0;
    min-width: 132px;
    background: rgba(255, 255, 255, 0.96);
    z-index: 3000;
    position: absolute;
    list-style-type: none;
    padding: 6px;
    border-radius: 6px;
    font-size: 13px;
    font-weight: 500;
    color: var(--tags-item-text, #333);
    box-shadow: 0 12px 32px rgba(15, 23, 42, 0.14);
    border: 1px solid var(--el-border-color-lighter, #e4e7ed);

    li {
      display: flex;
      align-items: center;
      gap: 8px;
      margin: 0;
      padding: 9px 12px;
      border-radius: 4px;
      cursor: pointer;
      line-height: 1;
      transition: all 0.2s ease;

      svg {
        flex-shrink: 0;
        width: 14px;
        height: 14px;
        color: var(--el-text-color-regular);
      }

      &:hover {
        background: var(--tags-item-hover, #f4f8ff);
        color: var(--el-color-primary);

        svg {
          color: var(--el-color-primary);
        }
      }
    }
  }
}
</style>

<style lang="scss">
//reset element css of el-icon-close
.tags-view-wrapper {
  .tags-view-item {
    .el-icon-close {
      width: 16px;
      height: 16px;
      vertical-align: 2px;
      border-radius: 50%;
      text-align: center;
      transition: all .3s cubic-bezier(.645, .045, .355, 1);
      transform-origin: 100% 50%;

      &:before {
        transform: scale(.6);
        display: inline-block;
        vertical-align: -3px;
      }

      &:hover {
        background-color: var(--tags-close-hover, #b4bccc);
        color: #fff;
        width: 12px !important;
        height: 12px !important;
      }
    }
  }
}

html.dark {
  .tags-view-container {
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.16);

    .contextmenu {
      background: rgba(34, 44, 54, 0.94);
      border-color: #3a4653;
      box-shadow: 0 12px 32px rgba(0, 0, 0, 0.28);

      li {
        color: rgba(255, 255, 255, 0.86);

        svg {
          color: rgba(255, 255, 255, 0.6);
        }

        &:hover {
          background: #24303c;
          color: #6eadff;

          svg {
            color: #6eadff;
          }
        }
      }
    }
  }
}
</style>
