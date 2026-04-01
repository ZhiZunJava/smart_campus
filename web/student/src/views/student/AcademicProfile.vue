<template>
  <div class="academic-profile-page">
    <!-- Top bar -->
    <header class="academic-header">
      <div class="academic-header__left">
        <el-button text bg size="small" @click="$router.back()"><i class="ri-arrow-left-line" /></el-button>
        <h1>学籍 <span class="academic-header__sep">-</span> 详情</h1>
      </div>
      <el-button type="primary" @click="$router.push('/student/affairs/academic-status')">主修</el-button>
    </header>

    <!-- Shell: sidebar + content -->
    <section class="academic-shell">
      <!-- Left sidebar -->
      <aside class="academic-sidebar">
        <!-- Identity card -->
        <div class="sidebar-identity">
          <div class="sidebar-photo">
            <img v-if="avatarUrl" :src="avatarUrl" alt="证件照" />
            <div v-else class="sidebar-photo__placeholder"><i class="ri-user-3-line" /></div>
          </div>
          <h2 class="sidebar-name">{{ profile.realName || profile.nickName || '学生用户' }}</h2>
          <p class="sidebar-meta">{{ sexLabel }} · {{ profile.nation || '--' }}</p>
          <div class="sidebar-fields">
            <div class="sidebar-field"><span>学号</span><strong>{{ profile.studentNo || '--' }}</strong></div>
            <div class="sidebar-field"><span>政治面貌</span><strong>{{ portalProfile.politicalStatus || profile.politicalStatus || '--' }}</strong></div>
            <div class="sidebar-field"><span>证件类型</span><strong>{{ idTypeLabel }}</strong></div>
            <div class="sidebar-field"><span>证件号</span><strong>{{ maskedIdNumber }}</strong></div>
          </div>
          <div class="sidebar-completion">
            <div class="sidebar-completion__header">
              <span>信息完整度</span>
              <strong :class="completionRate === 100 ? 'is-complete' : ''">{{ completionRate }}%</strong>
            </div>
            <div class="sidebar-completion__bar">
              <div class="sidebar-completion__fill" :style="{ width: completionRate + '%' }" :class="completionRate === 100 ? 'is-complete' : completionRate >= 50 ? 'is-partial' : 'is-low'"></div>
            </div>
          </div>
        </div>

        <!-- Nav menu -->
        <nav class="sidebar-nav" aria-label="信息导航">
          <button
            v-for="item in navItems"
            :key="item.key"
            class="sidebar-nav__item"
            :class="{ 'is-active': activeSection === item.key }"
            @click="scrollToSection(item.key)"
          >
            <i :class="item.icon" />
            {{ item.label }}
          </button>
        </nav>
      </aside>

      <!-- Right content -->
      <main class="academic-content">
        <section
          v-for="group in sectionGroups"
          :key="group.key"
          :id="`section-${group.key}`"
          class="info-section"
        >
          <h3 class="info-section__title">
            <i :class="group.icon" /> {{ group.label }}
            <span class="info-section__badge" :class="sectionCompletionClass(group)">
              {{ sectionFilledCount(group) }}/{{ group.items.length }}
            </span>
          </h3>
          <div class="info-field-grid">
            <div v-for="field in group.items" :key="field.label" class="info-field" :class="{ 'is-empty': !field.value || field.value === '--' }">
              <span class="info-field__label">{{ field.label }}</span>
              <strong class="info-field__value">{{ field.value }}</strong>
            </div>
          </div>
        </section>
      </main>

      <!-- Loading overlay -->
      <Transition name="fade">
        <div v-if="loading" class="loading-overlay">
          <div class="loading-overlay__content">
            <i class="ri-loader-4-line loading-overlay__spinner" />
            <span>加载中…</span>
          </div>
        </div>
      </Transition>
    </section>

    <!-- Responsive: horizontal tabs for small screens -->
    <nav v-if="false" class="academic-tabs-mobile">
      <!-- rendered via CSS media query swap -->
    </nav>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onBeforeUnmount, ref, nextTick } from 'vue'
import { getPortalAffairCategoryDetail, getPortalAffairOptions, getPortalProfile } from '@/api/portal'

const profile = ref<any>({})
const portalProfile = ref<any>({})
const categoryDetail = ref<any>({})
const optionMap = ref<any>({})
const loading = ref(false)
const activeSection = ref('basic')
let observer: IntersectionObserver | null = null

// --- Computed helpers ---
const advisorInfo = computed(() => categoryDetail.value?.advisorInfo || {})
const studentStatus = computed(() => categoryDetail.value?.studentStatus || {})
const academicData = computed(() => categoryDetail.value?.specializedData || {})

// --- Student status badge ---
const studentStatusLabel = computed(() => {
  const s = portalProfile.value?.studentStatus || studentStatus.value?.status || 'ENROLLED'
  const map: Record<string, string> = { ENROLLED: '在读', GRADUATED: '已毕业', SUSPENDED: '休学', DROPPED: '退学', DEFERRED: '延期' }
  return map[s] || '在读'
})

const studentStatusType = computed(() => {
  const s = portalProfile.value?.studentStatus || studentStatus.value?.status || 'ENROLLED'
  const map: Record<string, string> = { ENROLLED: 'success', GRADUATED: 'info', SUSPENDED: 'warning', DROPPED: 'danger', DEFERRED: 'warning' }
  return map[s] || 'success'
})

const avatarUrl = computed(() => {
  const url = portalProfile.value?.avatarUrl || profile.value?.avatar
  if (!url || url === '/profile/avatar/default.png') return ''
  return url.startsWith('http') ? url : url
})

const sexLabel = computed(() => {
  const v = String(profile.value.sex ?? '2')
  if (v === '0') return '男'
  if (v === '1') return '女'
  return '未知'
})

const idTypeLabel = computed(() => {
  const t = profile.value.idType || portalProfile.value?.idType || ''
  const map: Record<string, string> = { '1': '居民身份证', '2': '军官证', '3': '护照', '4': '港澳通行证', '5': '台湾通行证' }
  return map[t] || '居民身份证'
})

const maskedIdNumber = computed(() => {
  const id = profile.value.idCard || profile.value.idNumber || portalProfile.value?.idNumber || ''
  if (!id || id.length < 8) return id || '--'
  return id.slice(0, 3) + '****' + id.slice(-4)
})

const deptName = computed(() => resolveOptionLabel(optionMap.value?.deptOptions, profile.value.deptId, '未配置'))
const gradeName = computed(() => resolveOptionLabel(optionMap.value?.gradeOptions, profile.value.gradeId, '未配置'))
const className = computed(() => portalProfile.value?.className || resolveOptionLabel(optionMap.value?.classOptions, profile.value.classId, '未配置'))

// --- Nav items ---
const navItems = [
  { key: 'basic', label: '基本信息', icon: 'ri-profile-line' },
  { key: 'admission', label: '录取信息', icon: 'ri-graduation-cap-line' },
  { key: 'graduation', label: '毕业信息', icon: 'ri-award-line' },
  { key: 'degree', label: '学位信息', icon: 'ri-medal-line' },
  { key: 'contact', label: '联系信息', icon: 'ri-contacts-book-line' },
]

// --- Section groups ---
const sectionGroups = computed(() => [
  {
    key: 'basic', label: '基本信息', icon: 'ri-profile-line',
    items: [
      { label: '学号', value: profile.value.studentNo || '--' },
      { label: '姓名', value: profile.value.realName || profile.value.nickName || '--' },
      { label: '曾用名', value: portalProfile.value?.formerName || '--' },
      { label: '性别', value: sexLabel.value },
      { label: '民族', value: profile.value.nation || portalProfile.value?.nation || '--' },
      { label: '出生日期', value: portalProfile.value?.birthDate || profile.value.birthday || '--' },
      { label: '政治面貌', value: portalProfile.value?.politicalStatus || '--' },
      { label: '籍贯', value: portalProfile.value?.nativePlace || '--' },
      { label: '年级', value: gradeName.value },
      { label: '院系', value: deptName.value },
      { label: '班级', value: className.value },
      { label: '专业', value: profile.value.major || '--' },
      { label: '培养类型', value: portalProfile.value?.cultivationType || '主修' },
      { label: '学历层次', value: portalProfile.value?.educationLevel || '--' },
      { label: '学制', value: portalProfile.value?.schoolingLength || '--' },
    ],
  },
  {
    key: 'admission', label: '录取信息', icon: 'ri-graduation-cap-line',
    items: [
      { label: '入学年份', value: profile.value.admissionYear || portalProfile.value?.admissionYear || '--' },
      { label: '录取方式', value: portalProfile.value?.admissionType || '--' },
      { label: '考生号', value: portalProfile.value?.examNumber || '--' },
      { label: '准考证号', value: portalProfile.value?.admissionTicketNo || '--' },
      { label: '录取分数', value: portalProfile.value?.admissionScore || '--' },
      { label: '录取批次', value: portalProfile.value?.admissionBatch || '--' },
      { label: '生源地', value: portalProfile.value?.sourceRegion || '--' },
      { label: '毕业中学', value: portalProfile.value?.graduateSchool || '--' },
    ],
  },
  {
    key: 'graduation', label: '毕业信息', icon: 'ri-award-line',
    items: [
      { label: '预计毕业时间', value: portalProfile.value?.expectedGraduationDate || '--' },
      { label: '实际毕业时间', value: portalProfile.value?.actualGraduationDate || '--' },
      { label: '毕业证书编号', value: portalProfile.value?.diplomaNo || '--' },
      { label: '结业/肄业标识', value: portalProfile.value?.completionType || '--' },
      { label: '毕业去向', value: portalProfile.value?.graduationDestination || '--' },
      { label: '备注', value: portalProfile.value?.graduationRemark || '--' },
    ],
  },
  {
    key: 'degree', label: '学位信息', icon: 'ri-medal-line',
    items: [
      { label: '学位类别', value: portalProfile.value?.degreeType || '--' },
      { label: '学位授予日期', value: portalProfile.value?.degreeAwardDate || '--' },
      { label: '学位证书编号', value: portalProfile.value?.degreeCertNo || '--' },
      { label: '学位论文题目', value: portalProfile.value?.thesisTitle || '--' },
      { label: '备注', value: portalProfile.value?.degreeRemark || '--' },
    ],
  },
  {
    key: 'contact', label: '联系信息', icon: 'ri-contacts-book-line',
    items: [
      { label: '手机号', value: profile.value.phonenumber || '--' },
      { label: '邮箱', value: profile.value.email || '--' },
      { label: '通讯地址', value: portalProfile.value?.contactAddress || '--' },
      { label: '家庭住址', value: portalProfile.value?.homeAddress || '--' },
      { label: '紧急联系人', value: portalProfile.value?.emergencyContact || '--' },
      { label: '紧急联系电话', value: portalProfile.value?.emergencyPhone || '--' },
      { label: '户口所在地', value: portalProfile.value?.householdAddress || '--' },
      { label: '银行卡号', value: maskBankCard(portalProfile.value?.bankCardNo) },
    ],
  },
])

// --- Helpers ---
function resolveOptionLabel(options: any[], value: any, fallback = '--') {
  const list = Array.isArray(options) ? options : []
  const match = list.find((item: any) => String(item.value || item.deptId) === String(value))
  return match?.label || match?.deptName || fallback
}

function maskBankCard(card?: string) {
  if (!card || card.length < 8) return card || '--'
  return card.slice(0, 4) + ' **** **** ' + card.slice(-4)
}

function sectionFilledCount(group: any) {
  return group.items.filter((f: any) => f.value && f.value !== '--').length
}

function sectionCompletionClass(group: any) {
  const filled = sectionFilledCount(group)
  const total = group.items.length
  if (filled === total) return 'is-complete'
  if (filled >= total * 0.5) return 'is-partial'
  return 'is-low'
}

function scrollToSection(key: string) {
  activeSection.value = key
  const el = document.getElementById(`section-${key}`)
  if (el) el.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

// --- Data loading ---
onMounted(async () => {
  loading.value = true
  try {
    const [profileRes, categoryRes, optionRes] = await Promise.all([
      getPortalProfile(),
      getPortalAffairCategoryDetail('student', 'ACADEMIC_STATUS'),
      getPortalAffairOptions(),
    ])
    profile.value = profileRes.data?.user || {}
    portalProfile.value = profileRes.data?.profile || {}
    categoryDetail.value = categoryRes.data || categoryRes
    optionMap.value = optionRes.data || optionRes

    // Setup scroll spy via IntersectionObserver
    await nextTick()
    activeSection.value = 'basic'
    setupScrollSpy()
  } finally {
    loading.value = false
  }
})

function setupScrollSpy() {
  if (observer) observer.disconnect()
  observer = new IntersectionObserver(
    (entries) => {
      for (const entry of entries) {
        if (entry.isIntersecting) {
          const id = entry.target.id.replace('section-', '')
          activeSection.value = id
        }
      }
    },
    { rootMargin: '-20% 0px -60% 0px', threshold: 0.1 }
  )
  for (const item of navItems) {
    const el = document.getElementById(`section-${item.key}`)
    if (el) observer.observe(el)
  }
}

onBeforeUnmount(() => {
  if (observer) { observer.disconnect(); observer = null }
})

/* 信息完整度 */
const completionRate = computed(() => {
  const allItems = sectionGroups.value.flatMap(g => g.items)
  const filled = allItems.filter(i => i.value && i.value !== '--').length
  return allItems.length ? Math.round((filled / allItems.length) * 100) : 0
})
</script>

<style scoped>
/* ===== Page container ===== */
.academic-profile-page {
  padding: 20px;
  display: grid;
  gap: 16px;
}

/* ===== Top header bar ===== */
.academic-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 20px;
  background: #fff;
  border-radius: 16px;
  border: 1px solid #e5eef8;
}

.academic-header__left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.academic-header__left h1 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #0f172a;
}

.academic-header__sep {
  color: #94a3b8;
  margin: 0 2px;
}

/* ===== Shell: 2-column grid ===== */
.academic-shell {
  display: grid;
  grid-template-columns: 260px minmax(0, 1fr);
  gap: 16px;
  min-height: 680px;
  position: relative;
}

/* ===== Left sidebar ===== */
.academic-sidebar {
  display: grid;
  gap: 12px;
  align-content: start;
  position: sticky;
  top: 20px;
  align-self: start;
}

.sidebar-identity {
  padding: 20px;
  background: #fff;
  border-radius: 18px;
  border: 1px solid #e5eef8;
  text-align: center;
}

.sidebar-photo {
  width: 96px;
  height: 96px;
  margin: 0 auto 14px;
  border-radius: 12px;
  overflow: hidden;
  background: #f1f5f9;
  border: 2px solid #e2e8f0;
}

.sidebar-photo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.sidebar-photo__placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #94a3b8;
  font-size: 36px;
}

.sidebar-name {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: #0f172a;
}

.sidebar-meta {
  margin: 6px 0 0;
  color: #64748b;
  font-size: 13px;
}

.sidebar-fields {
  margin-top: 16px;
  display: grid;
  gap: 10px;
  text-align: left;
}

.sidebar-field {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}

.sidebar-field span {
  color: #64748b;
  font-size: 12px;
  flex-shrink: 0;
}

.sidebar-field strong {
  color: #0f172a;
  font-size: 13px;
  text-align: right;
  word-break: break-all;
}

/* ===== Completion bar ===== */
.sidebar-completion {
  margin-top: 18px;
  padding-top: 14px;
  border-top: 1px solid #f1f5f9;
}

.sidebar-completion__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.sidebar-completion__header span {
  font-size: 12px;
  color: #64748b;
}

.sidebar-completion__header strong {
  font-size: 14px;
  font-weight: 700;
  color: #0f172a;
}

.sidebar-completion__header strong.is-complete {
  color: #059669;
}

.sidebar-completion__bar {
  height: 6px;
  background: #f1f5f9;
  border-radius: 3px;
  overflow: hidden;
}

.sidebar-completion__fill {
  height: 100%;
  border-radius: 3px;
  transition: width 0.6s cubic-bezier(0.4, 0, 0.2, 1);
  background: #dc2626;
}

.sidebar-completion__fill.is-partial {
  background: linear-gradient(90deg, #f59e0b, #eab308);
}

.sidebar-completion__fill.is-complete {
  background: linear-gradient(90deg, #10b981, #059669);
}

/* ===== Sidebar nav ===== */
.sidebar-nav {
  background: #fff;
  border-radius: 18px;
  border: 1px solid #e5eef8;
  padding: 8px;
  display: grid;
  gap: 2px;
}

.sidebar-nav__item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  border: none;
  background: transparent;
  border-radius: 12px;
  cursor: pointer;
  font-size: 14px;
  color: #475569;
  transition: background .15s, color .15s;
  text-align: left;
  width: 100%;
  border-left: 3px solid transparent;
}

.sidebar-nav__item i {
  font-size: 16px;
  color: #94a3b8;
  transition: color .15s;
}

.sidebar-nav__item:hover {
  background: #f8fafc;
  color: #1e293b;
}

.sidebar-nav__item.is-active {
  background: #eff6ff;
  color: #2563eb;
  border-left-color: #2563eb;
  font-weight: 600;
}

.sidebar-nav__item.is-active i {
  color: #2563eb;
}

/* ===== Right content ===== */
.academic-content {
  display: grid;
  gap: 16px;
  align-content: start;
}

.info-section {
  background: #fff;
  border-radius: 18px;
  border: 1px solid #e5eef8;
  padding: 24px;
}

.info-section__title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 18px;
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  padding-bottom: 14px;
  border-bottom: 1px solid #f1f5f9;
}

.info-section__title i {
  font-size: 18px;
  color: #2563eb;
}

.info-section__badge {
  margin-left: auto;
  font-size: 11px;
  font-weight: 500;
  padding: 2px 10px;
  border-radius: 20px;
  line-height: 1.6;
}

.info-section__badge.is-complete {
  background: #ecfdf5;
  color: #059669;
}

.info-section__badge.is-partial {
  background: #fffbeb;
  color: #d97706;
}

.info-section__badge.is-low {
  background: #fef2f2;
  color: #dc2626;
}

/* ===== 3-column info grid ===== */
.info-field-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 0;
}

.info-field {
  padding: 14px 16px;
  border-bottom: 1px solid #f1f5f9;
  transition: background .15s;
}

.info-field:hover {
  background: #f8fafc;
}

.info-field:nth-child(3n+1) { border-right: 1px solid #f1f5f9; }
.info-field:nth-child(3n+2) { border-right: 1px solid #f1f5f9; }

.info-field__label {
  display: block;
  color: #94a3b8;
  font-size: 12px;
  margin-bottom: 6px;
}

.info-field__value {
  display: block;
  color: #0f172a;
  font-size: 14px;
  font-weight: 500;
  line-height: 1.5;
  word-break: break-all;
}

.info-field.is-empty .info-field__value {
  color: #cbd5e1;
}

/* ===== Loading overlay ===== */
.loading-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255,255,255,.85);
  border-radius: 18px;
  z-index: 10;
}

.loading-overlay__content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: #64748b;
  font-size: 13px;
}

.loading-overlay__spinner {
  font-size: 28px;
  color: #2563eb;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* ===== Transitions ===== */
.fade-enter-active, .fade-leave-active { transition: opacity .2s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

/* ===== Responsive ===== */
@media (max-width: 1080px) {
  .academic-shell {
    grid-template-columns: 1fr;
  }

  .academic-sidebar {
    position: static;
    grid-template-columns: 1fr 1fr;
    gap: 12px;
  }

  .sidebar-identity {
    grid-column: 1;
  }

  .sidebar-nav {
    grid-column: 2;
    align-self: start;
  }
}

@media (max-width: 768px) {
  .academic-sidebar {
    grid-template-columns: 1fr;
  }

  .sidebar-nav {
    grid-column: 1;
    display: flex;
    overflow-x: auto;
    gap: 4px;
    padding: 6px;
  }

  .sidebar-nav__item {
    white-space: nowrap;
    flex-shrink: 0;
    border-left: none;
    border-bottom: 2px solid transparent;
    padding: 10px 14px;
    border-radius: 10px;
  }

  .sidebar-nav__item.is-active {
    border-left-color: transparent;
    border-bottom-color: #2563eb;
  }

  .info-field-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .info-field:nth-child(3n+1),
  .info-field:nth-child(3n+2) {
    border-right: none;
  }

  .info-field:nth-child(odd) {
    border-right: 1px solid #f1f5f9;
  }
}

@media (max-width: 640px) {
  .academic-profile-page {
    padding: 12px;
  }

  .academic-header {
    padding: 10px 14px;
  }

  .info-field-grid {
    grid-template-columns: 1fr;
  }

  .info-field:nth-child(odd) {
    border-right: none;
  }

  .sidebar-photo {
    width: 72px;
    height: 72px;
  }
}

@media (prefers-reduced-motion: reduce) {
  * { transition: none !important; animation: none !important; }
}
</style>
