<template>
  <div class="score-analysis-chart">
    <div v-if="showEmpty" class="score-analysis-chart__empty">
      <el-empty :description="emptyText" :image-size="72" />
    </div>
    <div v-else ref="chartRef" class="score-analysis-chart__canvas" :style="{ height: normalizedHeight }"></div>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, onUnmounted, shallowRef, watch } from 'vue'
import * as echarts from 'echarts'
import type { EChartsCoreOption } from 'echarts'

const props = withDefaults(defineProps<{
  option?: EChartsCoreOption
  height?: string | number
  loading?: boolean
  emptyText?: string
}>(), {
  option: undefined,
  height: 320,
  loading: false,
  emptyText: '暂无图表数据',
})

const chartRef = shallowRef<HTMLElement | null>(null)
const chartInstance = shallowRef<echarts.ECharts | null>(null)
const resizeObserver = shallowRef<ResizeObserver | null>(null)

const normalizedHeight = computed(() => typeof props.height === 'number' ? `${props.height}px` : props.height)
const showEmpty = computed(() => !props.loading && !props.option)

function ensureChart() {
  const dom = chartRef.value
  if (!dom || dom.clientWidth <= 0 || dom.clientHeight <= 0) {
    return null
  }
  if (!chartInstance.value) {
    chartInstance.value = echarts.init(dom, undefined, {
      renderer: 'canvas',
      devicePixelRatio: window.devicePixelRatio || 1,
    })
  }
  return chartInstance.value
}

function updateLoadingState() {
  const chart = chartInstance.value
  if (!chart) {
    return
  }
  if (props.loading) {
    chart.showLoading('default', {
      text: '图表加载中',
      color: '#2563eb',
      maskColor: 'rgba(247, 250, 255, 0.72)',
      textColor: '#566a82',
    })
  } else {
    chart.hideLoading()
  }
}

function applyOption() {
  if (showEmpty.value) {
    chartInstance.value?.dispose()
    chartInstance.value = null
    return
  }
  const chart = ensureChart()
  if (!chart) {
    return
  }
  if (!props.option) {
    updateLoadingState()
    return
  }
  chart.setOption(props.option, true)
  updateLoadingState()
}

function resizeChart() {
  chartInstance.value?.resize()
}

onMounted(async () => {
  await nextTick()
  resizeObserver.value = new ResizeObserver(() => {
    resizeChart()
    applyOption()
  })
  if (chartRef.value) {
    resizeObserver.value.observe(chartRef.value)
  }
  requestAnimationFrame(() => {
    applyOption()
  })
})

watch(() => props.option, () => {
  requestAnimationFrame(() => {
    applyOption()
  })
}, { deep: true })

watch(() => props.loading, () => {
  requestAnimationFrame(() => {
    updateLoadingState()
  })
})

onUnmounted(() => {
  resizeObserver.value?.disconnect()
  resizeObserver.value = null
  chartInstance.value?.dispose()
  chartInstance.value = null
})
</script>

<style scoped>
.score-analysis-chart {
  position: relative;
  width: 100%;
  min-width: 0;
  padding: 10px;
  border-radius: 18px;
  background:
    radial-gradient(circle at top right, rgba(91, 140, 255, 0.1), transparent 32%),
    linear-gradient(180deg, rgba(248, 250, 255, 0.98) 0%, rgba(243, 247, 255, 0.9) 100%);
  border: 1px solid rgba(220, 228, 238, 0.92);
  overflow: visible;
}

.score-analysis-chart::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(90deg, rgba(47, 107, 255, 0.02), transparent 22%, transparent 78%, rgba(60, 200, 216, 0.03));
  pointer-events: none;
}

.score-analysis-chart__canvas {
  position: relative;
  z-index: 1;
  width: 100%;
  min-height: 220px;
}

.score-analysis-chart__empty {
  position: relative;
  z-index: 1;
  min-height: 220px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.56);
  border: 1px dashed var(--portal-border-strong);
}
</style>
