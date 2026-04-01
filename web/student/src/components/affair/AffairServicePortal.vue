<template>
  <div class="affair-portal">
    <!-- B1: Skeleton loading state -->
    <template v-if="loading">
      <div class="portal-skeleton" aria-hidden="true">
        <div class="skeleton-hero">
          <div class="skeleton-line skeleton-line--eyebrow" />
          <div class="skeleton-line skeleton-line--title" />
          <div class="skeleton-line skeleton-line--subtitle" />
          <div class="skeleton-stats">
            <div v-for="n in 3" :key="n" class="skeleton-stat-card" />
          </div>
        </div>
        <div class="skeleton-section">
          <div class="skeleton-line skeleton-line--heading" />
          <div class="skeleton-grid">
            <div v-for="n in 4" :key="n" class="skeleton-quick-card" />
          </div>
        </div>
        <div class="skeleton-section">
          <div class="skeleton-line skeleton-line--heading" />
          <div class="skeleton-module">
            <div class="skeleton-line skeleton-line--tag" />
            <div class="skeleton-grid skeleton-grid--wide">
              <div v-for="n in 3" :key="n" class="skeleton-category-card" />
            </div>
          </div>
          <div class="skeleton-module">
            <div class="skeleton-line skeleton-line--tag" />
            <div class="skeleton-grid skeleton-grid--wide">
              <div v-for="n in 2" :key="n" class="skeleton-category-card" />
            </div>
          </div>
        </div>
      </div>
    </template>

    <template v-else>
    <section class="portal-hero">
      <div class="portal-hero__text">
        <p class="portal-hero__eyebrow">{{ roleLabel }}事务中心</p>
        <h1>{{ heroTitle }}</h1>
        <p class="portal-hero__summary">{{ heroSubtitle }}</p>
      </div>
      <div class="portal-stats">
        <div class="stat-card" role="button" tabindex="0" @click="$emit('navigate', 'my-requests')" @keydown.enter="$emit('navigate', 'my-requests')">
          <span>我提交的</span>
          <strong>{{ dashboard.myRequestCount || 0 }}</strong>
        </div>
        <div class="stat-card is-warning" role="button" tabindex="0" @click="$emit('navigate', 'my-requests', 'PENDING')" @keydown.enter="$emit('navigate', 'my-requests', 'PENDING')">
          <span>审核中</span>
          <strong>{{ dashboard.pendingCount || 0 }}</strong>
        </div>
        <div class="stat-card is-success" role="button" tabindex="0" @click="$emit('navigate', 'my-requests', 'APPROVED')" @keydown.enter="$emit('navigate', 'my-requests', 'APPROVED')">
          <span>已通过</span>
          <strong>{{ dashboard.approvedCount || 0 }}</strong>
        </div>
        <div v-if="allowReview" class="stat-card is-danger" role="button" tabindex="0" @click="$emit('navigate', 'reviews')" @keydown.enter="$emit('navigate', 'reviews')">
          <span>待我审核</span>
          <strong>{{ dashboard.reviewTodoCount || 0 }}</strong>
        </div>
      </div>
    </section>

    <section class="portal-section">
      <div class="section-header">
        <h2>常用服务</h2>
        <el-button v-if="frequentTemplates.length" link type="primary" @click="$emit('navigate', 'my-requests')">查看全部申请</el-button>
      </div>
      <template v-if="frequentTemplates.length">
        <p class="section-subtitle">最近办理过的服务，快速再次发起</p>
        <div class="quick-grid">
          <article
            v-for="item in frequentTemplates"
            :key="item.templateId"
            class="quick-card"
            tabindex="0"
            @click="handleQuickTemplate(item)"
            @keydown.enter="handleQuickTemplate(item)"
          >
            <div class="quick-card__icon"><i :class="resolveIcon(item.categoryCode)" /></div>
            <div>
              <strong>{{ item.templateName }}</strong>
              <span>已办理 {{ item.useCount }} 次</span>
            </div>
          </article>
        </div>
      </template>
      <div v-else class="empty-state">
        <i class="ri-apps-line empty-state__icon" />
        <p class="empty-state__text">暂无常用服务</p>
        <p class="empty-state__subtext">办理业务后，常用服务会出现在这里</p>
      </div>
    </section>

    <section v-if="allowApply" class="portal-section">
      <div class="section-header">
        <h2>服务模块</h2>
        <span class="section-header__hint">{{ serviceCatalog.length }} 个分类 · {{ totalTemplates }} 项服务</span>
      </div>
      <div class="module-grid">
        <article
          v-for="group in groupedCatalog"
          :key="group.groupKey"
          class="module-block"
          :class="group.accentClass"
        >
          <div class="module-block__header">
            <span class="module-block__tag">{{ group.groupLabel }}</span>
            <span class="module-block__count">{{ group.categories.length }}</span>
          </div>
          <div class="module-block__cards">
            <div
              v-for="cat in group.categories"
              :key="cat.categoryId"
              class="category-card"
              tabindex="0"
              @click="$emit('navigate', 'category', cat)"
              @keydown.enter="$emit('navigate', 'category', cat)"
            >
              <div class="category-card__icon"><i :class="cat.icon || 'ri-service-line'" /></div>
              <div class="category-card__body">
                <strong>{{ cat.categoryName }}</strong>
                <span>{{ cat.templates?.length || 0 }} 项服务</span>
              </div>
              <i class="ri-arrow-right-s-line category-card__arrow" />
            </div>
          </div>
        </article>
      </div>
    </section>

    <section class="portal-section">
      <div class="section-header">
        <h2>最近动态</h2>
      </div>
      <div v-if="recentActivity.length" class="activity-list">
        <div v-for="item in recentActivity" :key="item.requestId" class="activity-item" @click="$emit('navigate', 'request-detail', item)">
          <div class="activity-item__info">
            <strong>{{ item.templateName || item.title }}</strong>
            <span>{{ item.requestNo }}</span>
          </div>
          <el-tag :type="statusType(item.requestStatus)" size="small">{{ statusLabel(item.requestStatus) }}</el-tag>
          <span class="activity-item__time">{{ item.submittedTime }}</span>
        </div>
      </div>
      <el-empty v-else description="暂无最近动态，提交申请后会在这里显示进度" :image-size="80" />
    </section>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { statusLabel, statusType } from './student-pages/useStudentAffairDetail'

const props = defineProps<{
  portalRole: 'student' | 'teacher' | 'advisor'
  allowApply: boolean
  allowReview: boolean
  loading?: boolean
  heroTitle: string
  heroSubtitle: string
  dashboard: any
  serviceCatalog: any[]
  frequentTemplates: any[]
  recentActivity: any[]
}>()

const emit = defineEmits<{
  navigate: [target: string, payload?: any]
}>()

const roleLabel = computed(() => props.portalRole === 'student' ? '学生' : props.portalRole === 'teacher' ? '教师' : '辅导员')

const totalTemplates = computed(() =>
  props.serviceCatalog.reduce((sum, cat) => sum + (cat.templates?.length || 0), 0)
)

const SERVICE_GROUP_LABELS: Record<string, string> = {
  ATTENDANCE: '考勤服务',
  FUNDING: '资助服务',
  GROWTH: '成长发展',
  ACADEMIC: '教务服务',
}

const SERVICE_GROUP_ACCENT: Record<string, string> = {
  ATTENDANCE: 'is-attendance',
  FUNDING: 'is-funding',
  GROWTH: 'is-growth',
  ACADEMIC: 'is-academic',
}

const groupedCatalog = computed(() => {
  const grouped: Record<string, any[]> = {}
  for (const cat of props.serviceCatalog) {
    const key = cat.serviceGroup || 'OTHER'
    if (!grouped[key]) grouped[key] = []
    grouped[key].push(cat)
  }
  return Object.entries(grouped).map(([key, categories]) => ({
    groupKey: key,
    groupLabel: SERVICE_GROUP_LABELS[key] || key,
    accentClass: SERVICE_GROUP_ACCENT[key] || '',
    categories,
  }))
})

const CATEGORY_ICON_MAP: Record<string, string> = {
  ASK_LEAVE: 'ri-calendar-check-line',
  LEAVE_RETURN_SCHOOL: 'ri-map-pin-user-line',
  SCHOLARSHIP: 'ri-award-line',
  GRANT: 'ri-hand-coin-line',
  LOAN: 'ri-bank-card-line',
  WORK_STUDY: 'ri-briefcase-4-line',
  ACTIVITY: 'ri-flag-2-line',
  SUBJECT_COMPETITION: 'ri-trophy-line',
  TEXTBOOK: 'ri-book-open-line',
  INNOVATION_ENTREPRENEURSHIP: 'ri-lightbulb-flash-line',
  ACADEMIC_STATUS: 'ri-exchange-box-line',
  COMPREHENSIVE_EVALUATION: 'ri-bar-chart-grouped-line',
  POVERTY_IDENTIFICATION: 'ri-heart-pulse-line',
}

function resolveIcon(categoryCode: string) {
  return CATEGORY_ICON_MAP[categoryCode] || 'ri-service-line'
}

function handleQuickTemplate(item: any) {
  if (item?.formSchemaJson && item?.workflowSchemaJson) {
    emit('navigate', 'apply', item)
    return
  }
  emit('navigate', 'category', item)
}

</script>

<style scoped>
.affair-portal { display: grid; gap: 24px; }

.portal-hero { display: grid; gap: 20px; padding: 32px; border-radius: 24px; background: #f8fafc; border: 1px solid #dbeafe; }
.portal-hero__eyebrow { margin: 0 0 8px; color: #266fcb; font-size: 12px; letter-spacing: 0.08em; text-transform: uppercase; }
.portal-hero h1 { margin: 0 0 8px; font-size: 28px; color: #0f172a; }
.portal-hero__summary { margin: 0; color: #506173; max-width: 760px; line-height: 1.7; }
.portal-stats { display: grid; grid-template-columns: repeat(auto-fit, minmax(140px, 1fr)); gap: 12px; }
.stat-card { padding: 18px; border-radius: 16px; background: rgba(255,255,255,.85); border: 1px solid rgba(191,219,254,.75); cursor: pointer; transition: box-shadow .15s, translate .15s; }
.stat-card:hover { box-shadow: 0 6px 20px rgba(15,23,42,.07); translate: 0 -2px; }
.stat-card:focus-visible { outline: 2px solid #2563eb; outline-offset: 2px; box-shadow: 0 6px 20px rgba(15,23,42,.07); }
.stat-card span { display: block; color: #64748b; font-size: 12px; }
.stat-card strong { display: block; margin-top: 8px; color: #0f172a; font-size: 26px; font-variant-numeric: tabular-nums; }
.stat-card.is-warning strong { color: #d97706; }
.stat-card.is-success strong { color: #059669; }
.stat-card.is-danger strong { color: #dc2626; }

.portal-section { display: grid; gap: 14px; }
.section-header { display: flex; align-items: center; justify-content: space-between; }
.section-header h2 { margin: 0; font-size: 20px; color: #0f172a; }
.section-header__hint { color: #94a3b8; font-size: 13px; }
.section-subtitle { color: #94a3b8; font-size: 13px; margin: -8px 0 4px; }

.quick-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 12px; }
.quick-card { display: flex; align-items: center; gap: 14px; padding: 16px; border-radius: 16px; background: #fff; border: 1px solid #e5eef8; cursor: pointer; transition: box-shadow .15s, translate .15s, border-color .15s; }
.quick-card:hover { box-shadow: 0 6px 20px rgba(15,23,42,.07); translate: 0 -2px; border-color: #93c5fd; }
.quick-card:focus-visible { outline: 2px solid #2563eb; outline-offset: 2px; }
.quick-card__icon { width: 42px; height: 42px; display: flex; align-items: center; justify-content: center; border-radius: 12px; background: #eff6ff; color: #266fcb; font-size: 20px; }
.quick-card strong { display: block; color: #172033; font-size: 14px; }
.quick-card span { display: block; color: #94a3b8; font-size: 12px; margin-top: 2px; }

.module-grid { display: grid; gap: 18px; }
.module-block { padding: 20px; border-radius: 20px; background: #fff; border: 1px solid #e5eef8; border-left: 4px solid #e5eef8; transition: border-color .2s; }
.module-block__header { display: flex; align-items: center; gap: 8px; margin-bottom: 14px; }
.module-block__tag { display: inline-block; padding: 4px 14px; border-radius: 20px; background: #eff6ff; color: #266fcb; font-size: 12px; font-weight: 600; letter-spacing: .05em; }
.module-block__count { display: inline-flex; align-items: center; justify-content: center; min-width: 22px; height: 22px; padding: 0 6px; border-radius: 11px; font-size: 11px; font-weight: 600; background: #e5eef8; color: #64748b; }

/* B2: Group accent border + background */
.module-block.is-attendance { border-left-color: #34d399; background: #f0fdf9; }
.module-block.is-funding    { border-left-color: #fbbf24; background: #fffcf0; }
.module-block.is-growth     { border-left-color: #a78bfa; background: #faf5ff; }
.module-block.is-academic   { border-left-color: #5b8cff; background: #f0f4ff; }

/* B9: Tag pill + B4: count badge coloring per group */
.module-block.is-attendance .module-block__tag { background: #d1fae5; color: #065f46; }
.module-block.is-attendance .module-block__count { background: #d1fae5; color: #065f46; }
.module-block.is-funding .module-block__tag    { background: #fef3c7; color: #92400e; }
.module-block.is-funding .module-block__count  { background: #fef3c7; color: #92400e; }
.module-block.is-growth .module-block__tag     { background: #ede9fe; color: #5b21b6; }
.module-block.is-growth .module-block__count   { background: #ede9fe; color: #5b21b6; }
.module-block.is-academic .module-block__tag   { background: #dbeafe; color: #1e40af; }
.module-block.is-academic .module-block__count { background: #dbeafe; color: #1e40af; }
.module-block__cards { display: grid; grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); gap: 12px; }
.category-card { display: flex; align-items: center; gap: 14px; padding: 16px; border-radius: 16px; background: #fff; border: 1px solid #dbe7f5; cursor: pointer; transition: box-shadow .15s, translate .15s, border-color .15s; }
.category-card:hover { box-shadow: 0 6px 20px rgba(15,23,42,.07); translate: 0 -2px; border-color: #93c5fd; }
.category-card:focus-visible { outline: 2px solid #2563eb; outline-offset: 2px; }
.category-card__icon { width: 44px; height: 44px; display: flex; align-items: center; justify-content: center; border-radius: 14px; background: #eff6ff; color: #266fcb; font-size: 22px; flex-shrink: 0; }

/* B3: Icon coloring by group */
.module-block.is-attendance .category-card__icon { background: #d1fae5; color: #059669; }
.module-block.is-funding .category-card__icon    { background: #fef3c7; color: #d97706; }
.module-block.is-growth .category-card__icon     { background: #ede9fe; color: #7c3aed; }
.module-block.is-academic .category-card__icon   { background: #dbeafe; color: #2563eb; }

/* B2: Module accent border + tint background */
.module-block.is-attendance { border-left-color: #34d399; background: #f0fdf9; }
.module-block.is-funding    { border-left-color: #fbbf24; background: #fffbeb; }
.module-block.is-growth     { border-left-color: #a78bfa; background: #f5f3ff; }
.module-block.is-academic   { border-left-color: #5b8cff; background: #eff4ff; }

/* B9: Tag pill coloring by group */
.module-block.is-attendance .module-block__tag { background: #d1fae5; color: #047857; }
.module-block.is-funding .module-block__tag    { background: #fef3c7; color: #b45309; }
.module-block.is-growth .module-block__tag     { background: #ede9fe; color: #6d28d9; }
.module-block.is-academic .module-block__tag   { background: #dbeafe; color: #1d4ed8; }

/* B4: Count badge coloring by group */
.module-block.is-attendance .module-block__count { background: #d1fae5; color: #047857; }
.module-block.is-funding .module-block__count    { background: #fef3c7; color: #b45309; }
.module-block.is-growth .module-block__count     { background: #ede9fe; color: #6d28d9; }
.module-block.is-academic .module-block__count   { background: #dbeafe; color: #1d4ed8; }
.category-card__body { flex: 1; min-width: 0; }
.category-card__body strong { display: block; color: #172033; font-size: 15px; }
.category-card__body span { display: block; color: #94a3b8; font-size: 12px; margin-top: 2px; }
.category-card__arrow { color: #cbd5e1; font-size: 18px; flex-shrink: 0; }

.activity-list { display: grid; gap: 8px; }
.activity-item { display: flex; align-items: center; gap: 14px; padding: 14px 16px; border-radius: 14px; background: #fff; border: 1px solid #e5eef8; cursor: pointer; transition: background .15s, border-color .15s; }
.activity-item:hover { background: #f0f7ff; border-color: #93c5fd; }
.activity-item__info { flex: 1; min-width: 0; display: flex; flex-direction: column; gap: 2px; }
.activity-item__info strong { color: #172033; font-size: 14px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.activity-item__info span { color: #94a3b8; font-size: 12px; }
.activity-item__time { color: #94a3b8; font-size: 12px; flex-shrink: 0; }

@media (max-width: 900px) {
  .portal-stats { grid-template-columns: repeat(2, 1fr); }
  .module-block__cards { grid-template-columns: 1fr; }
}
@media (max-width: 640px) {
  .portal-hero { padding: 20px 16px; }
  .portal-hero h1 { font-size: 22px; }
  .portal-stats { grid-template-columns: 1fr; }
  .quick-grid { grid-template-columns: 1fr; }
  .activity-item { flex-direction: column; align-items: flex-start; gap: 8px; }
  .activity-item__time { align-self: flex-end; }
}
/* B1: Skeleton loading */
.portal-skeleton { display: grid; gap: 24px; }
.skeleton-hero { padding: 32px; border-radius: 24px; background: #f8fafc; border: 1px solid #dbeafe; }
.skeleton-stats { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px; margin-top: 1rem; }
.skeleton-stat-card { height: 4.5rem; border-radius: 16px; }
.skeleton-section { display: grid; gap: 14px; }
.skeleton-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 12px; }
.skeleton-grid--wide { grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); }
.skeleton-module { display: grid; gap: 12px; }
.skeleton-quick-card { height: 4rem; border-radius: 16px; }
.skeleton-category-card { height: 4.5rem; border-radius: 16px; }

.skeleton-line,
.skeleton-stat-card,
.skeleton-quick-card,
.skeleton-category-card {
  background: linear-gradient(90deg, #f0f2f5 25%, #e8ebf0 50%, #f0f2f5 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s ease-in-out infinite;
  border-radius: 6px;
}

.skeleton-line--eyebrow { height: 0.75rem; width: 25%; margin-bottom: 0.5rem; }
.skeleton-line--title { height: 1.75rem; width: 40%; margin-bottom: 0.5rem; }
.skeleton-line--subtitle { height: 1rem; width: 60%; }
.skeleton-line--heading { height: 1.25rem; width: 20%; }
.skeleton-line--tag { height: 1.5rem; width: 15%; border-radius: 20px; }

@keyframes shimmer {
  0%   { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

/* B7: Empty state */
.empty-state { display: flex; flex-direction: column; align-items: center; padding: 2rem 1rem; color: #94a3b8; }
.empty-state__icon { font-size: 2rem; margin-bottom: 0.5rem; opacity: 0.6; }
.empty-state__text { font-size: 0.875rem; margin: 0; }
.empty-state__subtext { font-size: 0.75rem; color: #cbd5e1; margin-top: 0.25rem; }

@media (max-width: 900px) {
  .portal-stats, .skeleton-stats { grid-template-columns: repeat(2, 1fr); }
  .module-block__cards { grid-template-columns: 1fr; }
}
@media (max-width: 640px) {
  .portal-hero { padding: 20px 16px; }
  .portal-hero h1 { font-size: 22px; }
  .portal-stats, .skeleton-stats { grid-template-columns: 1fr; }
  .quick-grid { grid-template-columns: 1fr; }
  .activity-item { flex-direction: column; align-items: flex-start; gap: 8px; }
  .activity-item__time { align-self: flex-end; }
}
@media (prefers-reduced-motion: reduce) {
  .stat-card, .quick-card, .category-card, .activity-item { transition: none !important; }
  .stat-card:hover, .quick-card:hover, .category-card:hover { translate: none; box-shadow: none; }
  .skeleton-line, .skeleton-stat-card, .skeleton-quick-card, .skeleton-category-card { animation: none; }
}
</style>
