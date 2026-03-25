<template>
  <div ref="containerRef" class="resource-video-player"></div>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'
import Player from 'xgplayer'
import 'xgplayer/dist/index.min.css'

const props = defineProps<{
  src: string
}>()

const containerRef = ref<HTMLElement | null>(null)
let player: Player | null = null

function mountPlayer() {
  if (!containerRef.value || !props.src) return
  player?.destroy()
  player = new Player({
    el: containerRef.value,
    url: props.src,
    fluid: true,
    autoplay: false,
    pip: true,
    playbackRate: [0.75, 1, 1.25, 1.5, 2],
    download: true,
    cssFullscreen: true,
    controls: {
      initShow: true,
    },
  })
}

watch(() => props.src, () => {
  mountPlayer()
})

onMounted(() => {
  mountPlayer()
})

onBeforeUnmount(() => {
  player?.destroy()
  player = null
})
</script>

<style scoped>
.resource-video-player {
  width: 100%;
  min-height: 560px;
  background: #000;
}
</style>
