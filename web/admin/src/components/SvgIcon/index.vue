<template>
  <i v-if="showRemixIcon" :class="[svgClass, resolvedIcon]" aria-hidden="true"></i>
  <svg v-else :class="svgClass" aria-hidden="true">
    <use :xlink:href="iconName" :fill="color" />
  </svg>
</template>

<script setup lang="ts">
import { isRemixIcon, resolveMenuIcon } from '@/utils/menuIcon'

const props = defineProps({
  iconClass: {
    type: String,
    required: true
  },
  className: {
    type: String,
    default: ''
  },
  color: {
    type: String,
    default: ''
  },
})

const resolvedIcon = computed(() => resolveMenuIcon(props.iconClass))
const showRemixIcon = computed(() => isRemixIcon(props.iconClass))
const iconName = computed(() => `#icon-${resolvedIcon.value}`)
const svgClass = computed(() => {
  if (props.className) {
    return `svg-icon ${props.className}`
  }
  return 'svg-icon'
})
</script>

<style scope lang="scss">
.sub-el-icon,
.nav-icon {
  display: inline-block;
  font-size: 15px;
  margin-right: 12px;
  position: relative;
}

.svg-icon {
  width: 1em;
  height: 1em;
  position: relative;
  fill: currentColor;
  vertical-align: -2px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  line-height: 1;
}
</style>
