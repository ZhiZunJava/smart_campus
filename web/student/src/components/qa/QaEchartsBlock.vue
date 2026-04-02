<template>
  <div class="qa-echarts-block" :class="{ 'is-embedded': embedded }">
    <div ref="chartRef" class="qa-echarts-block__canvas"></div>
    <div v-if="displayError" class="qa-echarts-block__error">
      <strong>图表渲染失败</strong>
      <pre>{{ displayError }}</pre>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, onUnmounted, shallowRef, watch } from 'vue'
import * as echarts from 'echarts'
import { useJsonrepair } from '@vunk/markdown/composables/jsonrepair'
import { extractQaChartOptionSource } from '@/utils/qaMarkdown'

const props = withDefaults(defineProps<{
  source?: string
  embedded?: boolean
}>(), {
  source: '',
  embedded: false,
})

const chartRef = shallowRef<HTMLElement | null>(null)
const chartInstance = shallowRef<echarts.ECharts | null>(null)
const resizeObserver = shallowRef<ResizeObserver | null>(null)
const renderError = shallowRef('')

const jsonSource = computed(() => {
  const rawSource = String(props.source || '').trim()
  return extractQaChartOptionSource(rawSource) || rawSource
})
const { json } = useJsonrepair(jsonSource, { repair: true })

const displayError = computed(() => {
  return renderError.value
})

function ensureChart() {
  const dom = chartRef.value
  if (!dom) {
    return null
  }
  if (dom.clientWidth <= 0 || dom.clientHeight <= 0) {
    return null
  }
  if (!chartInstance.value) {
    chartInstance.value = echarts.init(dom)
  }
  return chartInstance.value
}

function resizeChart() {
  chartInstance.value?.resize()
}

function applyChartOption() {
  const chart = ensureChart()
  if (!chart) {
    return
  }
  if (!json.value || Array.isArray(json.value)) {
    return
  }
  try {
    chart.setOption(json.value as echarts.EChartsCoreOption, true)
    renderError.value = ''
  } catch (err) {
    renderError.value = err instanceof Error ? err.message : '未知图表错误'
  }
}

onMounted(async () => {
  await nextTick()
  resizeObserver.value = new ResizeObserver(() => {
    resizeChart()
    applyChartOption()
  })
  if (chartRef.value) {
    resizeObserver.value.observe(chartRef.value)
  }
  requestAnimationFrame(() => {
    applyChartOption()
  })
})

watch(json, () => {
  requestAnimationFrame(() => {
    applyChartOption()
  })
}, { deep: true })

onUnmounted(() => {
  resizeObserver.value?.disconnect()
  resizeObserver.value = null
  chartInstance.value?.dispose()
  chartInstance.value = null
})
</script>

<style scoped>
.qa-echarts-block {
  position: relative;
  width: 100%;
  min-width: 0;
  margin: 14px 0;
  border: 1px solid var(--portal-border);
  border-radius: 10px;
  background: var(--portal-card-solid);
  overflow: hidden;
}

.qa-echarts-block.is-embedded {
  margin: 0;
  border: none;
  border-radius: 0;
  background: transparent;
}

.qa-echarts-block__canvas {
  width: 100%;
  min-height: 360px;
}

.qa-echarts-block__error {
  padding: 14px 16px;
  border-top: 1px solid var(--portal-border);
  background: rgba(127, 29, 29, 0.06);
  color: #991b1b;
}

.qa-echarts-block__error strong {
  display: block;
  margin-bottom: 8px;
  font-size: 13px;
}

.qa-echarts-block__error pre {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-word;
  font-size: 12px;
  line-height: 1.6;
}
</style>
