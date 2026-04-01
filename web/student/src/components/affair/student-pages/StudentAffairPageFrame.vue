<template>
  <div class="student-affair-frame" :class="presentation?.accentClass || ''">
    <div class="back-nav">
      <el-button text bg size="small" @click="$emit('back')"><i class="ri-arrow-left-line" /> 返回事务总览</el-button>
    </div>

    <div v-if="loading" class="loading-center">
      <el-icon class="is-loading"><i class="ri-loader-4-line" /></el-icon>
    </div>

    <template v-else-if="hasDetail">
      <!-- Hero: title + inline metrics -->
      <section class="frame-hero">
        <div class="frame-hero__copy">
          <p class="frame-hero__eyebrow">{{ presentation?.highlightLabel || '学生事务' }}</p>
          <h2>{{ title }}</h2>
          <p>{{ subtitle }}</p>
        </div>
        <div v-if="metrics.length" class="frame-hero__metrics">
          <div v-for="item in metrics" :key="item.label" class="hero-metric" :class="resolveTone(item.tone || item.className)">
            <strong>{{ item.value }}</strong>
            <span>{{ item.label }}</span>
          </div>
        </div>
      </section>

      <!-- Summary: icon-driven cards -->
      <section v-if="summaryCards.length" class="summary-bar">
        <div v-for="card in summaryCards" :key="card.title" class="summary-bar__item" :class="resolveTone(card.tone)">
          <i v-if="card.icon" :class="card.icon" class="summary-bar__icon" />
          <div class="summary-bar__body">
            <div class="summary-bar__head">
              <strong>{{ card.title }}</strong>
              <el-tag v-if="card.badge" size="small" effect="plain">{{ card.badge }}</el-tag>
            </div>
            <span v-if="card.value !== undefined && card.value !== null && card.value !== ''" class="summary-bar__value">{{ card.value }}</span>
            <p v-for="line in card.lines || []" :key="line">{{ line }}</p>
          </div>
        </div>
      </section>

      <!-- Quick actions — compact, no sub-description -->
      <section v-if="actions.length" class="frame-section">
        <div class="section-head">
          <h3>快捷办理</h3>
        </div>
        <div class="action-row">
          <el-button
            v-for="action in actions"
            :key="action.label"
            :type="action.type || 'primary'"
            plain
            :disabled="action.disabled"
            @click="$emit('action', action)"
          >
            {{ action.label }}
          </el-button>
        </div>
      </section>

      <!-- Page-specific slot content -->
      <slot />

      <!-- Guide: collapsible instead of always-visible card grid -->
      <section v-if="guideSteps.length" class="frame-section">
        <el-collapse v-model="guideActive" class="guide-collapse">
          <el-collapse-item name="guide">
            <template #title>
              <div class="guide-collapse__title">
                <span>办理指南</span>
                <el-badge :value="guideSteps.length" :max="9" class="guide-badge" />
                <span>了解当前业务的办理顺序和审核链路</span>
              </div>
            </template>
            <div class="guide-steps">
              <div v-for="(step, index) in guideSteps" :key="step.title" class="guide-step">
                <span class="guide-step__num">{{ index + 1 }}</span>
                <div>
                  <strong>{{ step.title }}</strong>
                  <p>{{ step.desc }}</p>
                </div>
              </div>
            </div>
          </el-collapse-item>
        </el-collapse>
      </section>
    </template>

    <el-empty v-else :description="emptyDescription || '加载失败或事务分类不存在'" />
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'

const props = withDefaults(defineProps<{
  loading?: boolean
  hasDetail?: boolean
  title: string
  subtitle: string
  presentation?: any
  metrics?: any[]
  summaryCards?: any[]
  actions?: any[]
  guideSteps?: any[]
  emptyDescription?: string
}>(), {
  loading: false,
  hasDetail: false,
  presentation: null,
  metrics: () => [],
  summaryCards: () => [],
  actions: () => [],
  guideSteps: () => [],
  emptyDescription: '',
})

defineEmits<{
  back: []
  action: [action: any]
}>()

function resolveTone(tone?: string) {
  if (!tone) return ''
  return tone.startsWith('is-') ? tone : `is-${tone}`
}

const guideActive = ref<string[]>([])

onMounted(() => {
  const key = `guide_seen_${props.presentation?.categoryCode || 'default'}`
  if (!sessionStorage.getItem(key)) {
    guideActive.value = ['guide']
  }
})

watch(guideActive, (val) => {
  const key = `guide_seen_${props.presentation?.categoryCode || 'default'}`
  if (!val.includes('guide')) {
    sessionStorage.setItem(key, '1')
  }
})
</script>

<style scoped>
.student-affair-frame { display: grid; gap: 20px; }
.back-nav { margin-bottom: -8px; }
.back-nav :deep(.el-button) { color: #64748b; }
.back-nav :deep(.el-button:hover) { color: #0f172a; background: #f1f5f9; }
.loading-center { display: flex; justify-content: center; padding: 40px; font-size: 28px; color: #94a3b8; }

/* ── Hero ── */
.frame-hero {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 24px;
  padding: 20px 24px;
  border-radius: 14px;
  background: linear-gradient(135deg, #f0f7ff 0%, #f8fafc 100%);
  border: 1px solid #dbeafe;
  border-left: 4px solid #2563eb;
}

.frame-hero__eyebrow {
  margin: 0 0 4px;
  color: #266fcb;
  font-size: 12px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

.frame-hero__copy h2 {
  margin: 0 0 4px;
  font-size: 22px;
  color: #0f172a;
}

.frame-hero__copy > p {
  margin: 0;
  color: #64748b;
  line-height: 1.5;
  font-size: 13px;
}

/* Compact inline metrics inside hero */
.frame-hero__metrics {
  display: flex;
  gap: 0;
  flex-shrink: 0;
}

.hero-metric {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0 20px;
  border-right: 1px solid rgba(0, 0, 0, 0.06);
}
.hero-metric:last-child { border-right: none; padding-right: 0; }
.hero-metric:first-child { padding-left: 0; }

.hero-metric strong {
  font-size: 24px;
  color: #0f172a;
  line-height: 1.2;
}
.hero-metric span {
  margin-top: 2px;
  color: #94a3b8;
  font-size: 11px;
  white-space: nowrap;
}

.hero-metric.is-success strong { color: #059669; }
.hero-metric.is-warning strong { color: #d97706; }
.hero-metric.is-danger strong { color: #dc2626; }
.hero-metric.is-info strong { color: #2563eb; }

/* ── Summary bar (icon-driven) ── */
.summary-bar {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.summary-bar__item {
  flex: 1 1 180px;
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 14px 16px;
  border-radius: 10px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  transition: box-shadow 0.15s, border-color 0.15s;
}
.summary-bar__item:hover {
  border-color: #c7d2fe;
  box-shadow: 0 1px 4px rgba(37, 99, 235, 0.06);
}

.summary-bar__icon {
  font-size: 22px;
  color: #94a3b8;
  flex-shrink: 0;
  margin-top: 1px;
}
.summary-bar__item.is-success .summary-bar__icon { color: #059669; }
.summary-bar__item.is-warning .summary-bar__icon { color: #d97706; }
.summary-bar__item.is-danger .summary-bar__icon { color: #dc2626; }
.summary-bar__item.is-info .summary-bar__icon { color: #2563eb; }

.summary-bar__body { flex: 1; min-width: 0; }

.summary-bar__head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}

.summary-bar__head strong {
  font-size: 13px;
  color: #334155;
}

.summary-bar__value {
  display: block;
  margin-top: 2px;
  font-size: 18px;
  font-weight: 700;
  color: #0f172a;
}

.summary-bar__item.is-success .summary-bar__value { color: #059669; }
.summary-bar__item.is-warning .summary-bar__value { color: #d97706; }
.summary-bar__item.is-danger .summary-bar__value { color: #dc2626; }
.summary-bar__item.is-info .summary-bar__value { color: #2563eb; }

.summary-bar__item p {
  margin: 2px 0 0;
  color: #94a3b8;
  font-size: 12px;
  line-height: 1.5;
}

/* ── Section ── */
.frame-section { display: grid; gap: 10px; }
.section-head { display: flex; justify-content: space-between; align-items: end; gap: 12px; }
.section-head h3 { margin: 0; font-size: 15px; color: #0f172a; font-weight: 600; }
.section-head p { margin: 2px 0 0; color: #94a3b8; font-size: 12px; }

.action-row { display: flex; gap: 10px; flex-wrap: wrap; }

/* ── Guide collapse ── */
.guide-collapse {
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  overflow: hidden;
}

.guide-collapse :deep(.el-collapse-item__header) {
  padding: 10px 16px;
  background: #f8fafc;
  border-bottom-color: #e2e8f0;
  height: auto;
  line-height: 1.4;
}

.guide-collapse :deep(.el-collapse-item__wrap) {
  border-bottom: none;
}

.guide-collapse :deep(.el-collapse-item__content) {
  padding: 0;
}

.guide-collapse__title {
  display: flex;
  align-items: baseline;
  gap: 10px;
}

.guide-collapse__title > span:first-child {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: #0f172a;
}

.guide-collapse__title > span:last-child {
  color: #94a3b8;
  font-size: 12px;
}

.guide-steps {
  display: flex;
  flex-direction: column;
}

.guide-step {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px 16px;
  border-bottom: 1px solid #f1f5f9;
}

.guide-step:last-child { border-bottom: none; }

.guide-step__num {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: #eff6ff;
  color: #2563eb;
  font-size: 12px;
  font-weight: 600;
  flex-shrink: 0;
  margin-top: 1px;
}

.guide-step strong {
  display: block;
  font-size: 13px;
  color: #0f172a;
  margin-bottom: 2px;
}

.guide-step p {
  margin: 0;
  color: #94a3b8;
  font-size: 12px;
  line-height: 1.5;
}

/* ── Accent theme overrides ── */
.student-affair-frame.is-attendance .frame-hero { border-left-color: #059669; }
.student-affair-frame.is-funding .frame-hero { border-left-color: #d97706; }
.student-affair-frame.is-academic .frame-hero { border-left-color: #2563eb; }
.student-affair-frame.is-growth .frame-hero { border-left-color: #7c3aed; }

/* ── Responsive ── */
@media (max-width: 900px) {
  .frame-hero {
    flex-direction: column;
    align-items: stretch;
  }
  .frame-hero__metrics {
    justify-content: space-around;
    padding-top: 16px;
    border-top: 1px solid rgba(0, 0, 0, 0.06);
  }
}

@media (max-width: 768px) {
  .frame-hero__metrics {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 12px;
  }
  .hero-metric { border-left: none; padding-left: 0; }
}

@media (max-width: 640px) {
  .frame-hero__metrics {
    flex-wrap: wrap;
    gap: 12px;
  }
  .hero-metric {
    border-right: none;
    padding: 0;
  }
  .summary-bar { flex-direction: column; }
}

@media (max-width: 480px) {
  .frame-hero { padding: 16px; }
  .summary-bar { flex-direction: column; }
  .summary-bar__item { flex: 1 1 auto; }
  .frame-hero__title h2 { font-size: 20px; }
}

@media (prefers-reduced-motion: reduce) {
  .frame-hero, .summary-bar__item, .frame-section { transition: none !important; }
}
</style>
