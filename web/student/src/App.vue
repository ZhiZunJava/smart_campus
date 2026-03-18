<template>
  <router-view v-slot="{ Component, route }">
    <transition :name="resolveTransitionName(route)" mode="out-in" appear>
      <component :is="Component" :key="route.fullPath" />
    </transition>
  </router-view>
</template>

<script setup lang="ts">
import type { RouteLocationNormalizedLoaded } from 'vue-router'

function resolveTransitionName(route: RouteLocationNormalizedLoaded) {
  return typeof route.meta.transition === 'string' ? route.meta.transition : 'app-shell-fade'
}
</script>
