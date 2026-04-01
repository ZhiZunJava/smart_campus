<template>
  <div class="ov-page">
    <!-- ===== Hero ===== -->
    <section class="ov-hero ov-hero--teal">
      <div class="ov-hero__bg"></div>
      <div class="ov-hero__content">
        <div class="ov-hero__top">
          <div class="ov-hero__badge"><i class="ri-user-star-line" /> 教师概览</div>
          <el-select
            v-model="selectedTeacherId"
            filterable
            clearable
            placeholder="选择教师查看个人数据"
            style="width: 280px;"
            @change="onTeacherChange"
          >
            <el-option v-for="t in teacherOptions" :key="t.value" :label="t.shortLabel || t.label" :value="t.value" />
          </el-select>
        </div>

        <div class="ov-hero__intro">
          <h1>教师工作总览</h1>
          <p>查看教师团队规模、授课任务分配和教学资源建设情况。选择具体教师可查看个人工作数据。</p>
        </div>

        <div class="ov-metric-grid ov-metric-grid--4">
          <div v-for="item in globalMetrics" :key="item.title" class="ov-metric-card ov-metric-card--glass">
            <div class="ov-metric-card__head">
              <span class="ov-metric-card__label">{{ item.title }}</span>
              <i :class="item.icon" class="ov-metric-card__icon" />
            </div>
            <div class="ov-metric-card__value">{{ item.value }}</div>
            <p class="ov-metric-card__desc">{{ item.desc }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- ===== Individual Teacher Panel ===== -->
    <section v-if="selectedTeacherId && teacherInfo.loaded" class="ov-card ov-card--highlight">
      <div class="ov-card__header">
        <div>
          <h2><i class="ri-user-star-line" /> {{ teacherInfo.nickName || '教师' }} 的工作概况</h2>
          <p>工号: {{ teacherInfo.userId || '--' }}</p>
        </div>
        <el-button size="small" @click="loadTeacherDetail" :loading="teacherLoading"><i class="ri-refresh-line" /> 刷新</el-button>
      </div>

      <div class="ov-metric-grid ov-metric-grid--4">
        <div v-for="item in teacherDetailMetrics" :key="item.title" class="ov-stat-box" :class="item.cls">
          <div class="ov-stat-box__label">{{ item.title }}</div>
          <div class="ov-stat-box__value">{{ item.value }}</div>
          <p class="ov-stat-box__desc">{{ item.desc }}</p>
        </div>
      </div>

      <!-- Teaching Courses -->
      <div v-if="teacherCourses.length" class="ov-section-sub">
        <h3>授课教学班</h3>
        <el-table :data="teacherCourses" stripe size="small" max-height="280">
          <el-table-column prop="courseName" label="课程" min-width="160" show-overflow-tooltip />
          <el-table-column prop="className" label="班级" min-width="140" show-overflow-tooltip />
          <el-table-column prop="totalHours" label="总课时" width="80" align="center" />
          <el-table-column prop="arrangedHours" label="已排课时" width="90" align="center" />
          <el-table-column label="进度" width="80" align="center">
            <template #default="{ row }">
              <span :class="{ 'is-complete': Number(row.arrangedHours || 0) >= Number(row.totalHours || 1) }">
                {{ row.totalHours ? Math.round((row.arrangedHours || 0) / row.totalHours * 100) + '%' : '--' }}
              </span>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- Schedule -->
      <div v-if="teacherSchedules.length" class="ov-section-sub">
        <h3>排课记录</h3>
        <el-table :data="teacherSchedules" stripe size="small" max-height="280">
          <el-table-column prop="courseName" label="课程" min-width="140" show-overflow-tooltip />
          <el-table-column prop="className" label="班级" min-width="120" show-overflow-tooltip />
          <el-table-column prop="classroomName" label="教室" width="120" show-overflow-tooltip />
          <el-table-column label="星期" width="70" align="center">
            <template #default="{ row }">
              {{ ['','一','二','三','四','五','六','日'][row.weekDay] || row.weekDay }}
            </template>
          </el-table-column>
          <el-table-column label="节次" width="90" align="center">
            <template #default="{ row }">
              {{ row.startSection }}{{ row.endSection && row.endSection !== row.startSection ? '-' + row.endSection : '' }}
            </template>
          </el-table-column>
          <el-table-column prop="weeksText" label="周次" width="120" show-overflow-tooltip />
        </el-table>
      </div>
    </section>

    <!-- ===== Global View ===== -->
    <section v-else-if="!selectedTeacherId" class="ov-body">
      <div class="ov-body__main">
        <section class="ov-card">
          <div class="ov-card__header">
            <div>
              <h2>教学任务分布</h2>
              <p>教学班、排课和教室的整体情况</p>
            </div>
            <span class="ov-card__tag">Teaching</span>
          </div>
          <div class="ov-progress-list">
            <div v-for="item in readinessMetrics" :key="item.title" class="ov-progress-item">
              <div class="ov-progress-item__head">
                <div>
                  <div class="ov-progress-item__title">{{ item.title }}</div>
                  <p class="ov-progress-item__desc">{{ item.desc }}</p>
                </div>
                <div class="ov-progress-item__right">
                  <div class="ov-progress-item__value">{{ item.value }}</div>
                  <div class="ov-progress-item__caption">{{ item.caption }}</div>
                </div>
              </div>
              <div class="ov-progress-bar">
                <div class="ov-progress-bar__fill" :class="item.barCls" :style="{ width: item.percent + '%' }" />
              </div>
            </div>
          </div>
          <div class="ov-metric-grid ov-metric-grid--3" style="margin-top: 16px;">
            <div v-for="item in teachingDetailMetrics" :key="item.title" class="ov-stat-box">
              <div class="ov-stat-box__label">{{ item.title }}</div>
              <div class="ov-stat-box__value">{{ item.value }}</div>
              <p class="ov-stat-box__desc">{{ item.desc }}</p>
            </div>
          </div>
        </section>

        <section class="ov-card">
          <div class="ov-card__header">
            <div>
              <h2>教学资源建设</h2>
              <p>题库、试卷和课程资源</p>
            </div>
            <span class="ov-card__tag">Resources</span>
          </div>
          <div class="ov-metric-grid ov-metric-grid--3">
            <div v-for="item in resourceMetrics" :key="item.title" class="ov-stat-box">
              <div class="ov-stat-box__label">{{ item.title }}</div>
              <div class="ov-stat-box__value">{{ item.value }}</div>
              <p class="ov-stat-box__desc">{{ item.desc }}</p>
            </div>
          </div>
        </section>
      </div>

      <aside class="ov-body__side">
        <section class="ov-card">
          <div class="ov-card__header">
            <div><h2>快捷入口</h2><p>教师相关管理模块</p></div>
            <i class="ri-links-line ov-card__icon" />
          </div>
          <div class="ov-links">
            <button v-for="link in quickLinks" :key="link.title" class="ov-link" @click="navigateTo(link.path)">
              <div class="ov-link__icon"><i :class="link.icon" /></div>
              <div class="ov-link__text">
                <div class="ov-link__title">{{ link.title }}</div>
                <p class="ov-link__desc">{{ link.desc }}</p>
              </div>
            </button>
          </div>
        </section>

        <section class="ov-card">
          <div class="ov-card__header">
            <div><h2>运营提示</h2></div>
            <i class="ri-flashlight-line ov-card__icon is-teal" />
          </div>
          <ul class="ov-tips">
            <li v-for="(tip, idx) in tips" :key="idx">
              <span class="ov-tips__dot"><i class="ri-check-line" /></span>
              <span>{{ tip }}</span>
            </li>
          </ul>
        </section>
      </aside>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { getTeacherDashboard } from '@/api/campus/overview'
import { listUser } from '@/api/system/user'
import { listCourse, listClassCourse, listCourseSchedule, listClassroom, listClass } from '@/api/campus/teaching'
import { listQuestion, listPaper } from '@/api/campus/exam'
import { listResource } from '@/api/campus/resource'
import { fetchUserOptions } from '@/api/campus/options'

const router = useRouter()

const teacherOptions = ref<any[]>([])
const selectedTeacherId = ref<number | undefined>(undefined)
const teacherLoading = ref(false)
const teacherInfo = reactive<any>({})
const teacherCourses = ref<any[]>([])
const teacherSchedules = ref<any[]>([])

const g = reactive({
  teacherCount: 0,
  courseCount: 0,
  classCount: 0,
  classCourseCount: 0,
  arrangedCount: 0,
  pendingCount: 0,
  scheduleCount: 0,
  classroomCount: 0,
  questionCount: 0,
  paperCount: 0,
  resourceCount: 0,
})

const td = reactive({
  courseCount: 0,
  classCourseCount: 0,
  scheduleCount: 0,
  resourceCount: 0,
})

const schedulePct = computed(() => g.classCourseCount ? Math.min(100, Math.round((g.arrangedCount / g.classCourseCount) * 100)) : 0)

const globalMetrics = computed(() => [
  { title: '教师总数', value: g.teacherCount, desc: '平台在册教师', icon: 'ri-user-star-line' },
  { title: '教学班', value: g.classCourseCount, desc: '班课关联总数', icon: 'ri-node-tree' },
  { title: '排课记录', value: g.scheduleCount, desc: '已生成的课表', icon: 'ri-calendar-schedule-line' },
  { title: '教室资源', value: g.classroomCount, desc: '可排课教室', icon: 'ri-building-4-line' },
])

const teacherDetailMetrics = computed(() => [
  { title: '授课课程', value: td.courseCount, desc: '当前任教课程', cls: '' },
  { title: '教学班', value: td.classCourseCount, desc: '负责的班课', cls: '' },
  { title: '排课记录', value: td.scheduleCount, desc: '个人课表数', cls: 'is-accent' },
  { title: '教学资源', value: td.resourceCount, desc: '关联资源数', cls: '' },
])

const readinessMetrics = computed(() => [
  { title: '排课覆盖率', value: schedulePct.value + '%', caption: `${g.arrangedCount}/${g.classCourseCount}`, percent: schedulePct.value, desc: '教学班已排课覆盖', barCls: schedulePct.value >= 80 ? 'is-green' : 'is-amber' },
])

const teachingDetailMetrics = computed(() => [
  { title: '课程', value: g.courseCount, desc: '全校课程总数' },
  { title: '班级', value: g.classCount, desc: '教学班级总数' },
  { title: '教学班', value: g.classCourseCount, desc: '班课关联数量' },
  { title: '已排课', value: g.arrangedCount, desc: '已有排课的教学班' },
  { title: '待排课', value: g.pendingCount, desc: '课时未排满' },
  { title: '教室', value: g.classroomCount, desc: '可用教室数量' },
])

const resourceMetrics = computed(() => [
  { title: '题目', value: g.questionCount, desc: '题库中题目量' },
  { title: '试卷', value: g.paperCount, desc: '已组装试卷数' },
  { title: '教学资源', value: g.resourceCount, desc: '课程关联资源' },
])

const quickLinks = [
  { title: '教师管理', desc: '查看教师账号信息', path: '/system/user', icon: 'ri-user-star-line' },
  { title: '班级课程', desc: '教学班与任课关联', path: '/campus/classCourse', icon: 'ri-book-open-line' },
  { title: '排课管理', desc: '课表排课与调整', path: '/campus/courseSchedule', icon: 'ri-calendar-2-line' },
  { title: '题库管理', desc: '题目维护与导入', path: '/campus/exam/question', icon: 'ri-questionnaire-line' },
]

const tips = computed(() => [
  `当前共 ${g.teacherCount} 名教师，负责 ${g.classCourseCount} 个教学班。`,
  g.pendingCount > 0
    ? `还有 ${g.pendingCount} 个教学班课时未排满，建议优先处理。`
    : '所有教学班排课已完成，运转正常。',
  `题库包含 ${g.questionCount} 道题目，已有 ${g.resourceCount} 个教学资源。`,
])

function navigateTo(path: string) { router.push(path) }

async function loadGlobal() {
  const [tRes, courseRes, classRes, ccRes, schedRes, crRes, qRes, pRes, resRes] = await Promise.all([
    listUser({ pageNum: 1, pageSize: 1, userType: 'teacher' }),
    listCourse({ pageNum: 1, pageSize: 1 }),
    listClass({ pageNum: 1, pageSize: 1 }),
    listClassCourse({ pageNum: 1, pageSize: 500 }),
    listCourseSchedule({ pageNum: 1, pageSize: 1 }),
    listClassroom({ pageNum: 1, pageSize: 1 }),
    listQuestion({ pageNum: 1, pageSize: 1 }),
    listPaper({ pageNum: 1, pageSize: 1 }),
    listResource({ pageNum: 1, pageSize: 1 }),
  ])
  g.teacherCount = Number(tRes.total || 0)
  g.courseCount = Number(courseRes.total || 0)
  g.classCount = Number(classRes.total || 0)
  const ccRows = ccRes.rows || []
  g.classCourseCount = Number(ccRes.total || ccRows.length || 0)
  g.scheduleCount = Number(schedRes.total || 0)
  g.classroomCount = Number(crRes.total || 0)
  g.questionCount = Number(qRes.total || 0)
  g.paperCount = Number(pRes.total || 0)
  g.resourceCount = Number(resRes.total || 0)
  g.arrangedCount = ccRows.filter((i: any) => Number(i.arrangedHours || 0) > 0).length
  g.pendingCount = ccRows.filter((i: any) => Number(i.totalHours || 0) > Number(i.arrangedHours || 0)).length
}

async function loadTeacherDetail() {
  if (!selectedTeacherId.value) return
  teacherLoading.value = true
  try {
    const opt = teacherOptions.value.find(o => o.value === selectedTeacherId.value)
    teacherInfo.loaded = true
    teacherInfo.userId = selectedTeacherId.value
    if (opt) teacherInfo.nickName = opt.nickName || opt.shortLabel

    try {
      const res = await getTeacherDashboard({ teacherId: selectedTeacherId.value })
      const data = res.data || {}
      Object.assign(teacherInfo, data)
      td.courseCount = Number(data.courseCount || 0)
      td.classCourseCount = Number(data.classCount || data.classCourseCount || 0)
      td.resourceCount = Number(data.resourceCount || 0)
    } catch {
      td.courseCount = 0
      td.classCourseCount = 0
      td.resourceCount = 0
    }

    // Load teacher's class courses
    try {
      const ccRes = await listClassCourse({ pageNum: 1, pageSize: 100, teacherUserId: selectedTeacherId.value })
      teacherCourses.value = ccRes.rows || []
      if (!td.classCourseCount) td.classCourseCount = teacherCourses.value.length
    } catch { teacherCourses.value = [] }

    // Load teacher's schedules by filtering through classCourse IDs
    // (listCourseSchedule API doesn't support teacherUserId filter)
    // Schedule records only have classCourseId — merge classCourse data to get courseName/className
    try {
      const ccIds = teacherCourses.value.map((c: any) => c.id).filter(Boolean)
      if (ccIds.length) {
        // Build a lookup map: classCourseId -> { courseName, className, ... }
        const ccMap = new Map<number, any>()
        teacherCourses.value.forEach((c: any) => ccMap.set(c.id, c))

        const sRes = await listCourseSchedule({ pageNum: 1, pageSize: 500, status: '0' })
        const allSchedules = sRes.rows || []
        // Filter for this teacher's classCourses and merge names
        teacherSchedules.value = allSchedules
          .filter((s: any) => ccIds.includes(s.classCourseId))
          .map((s: any) => {
            const cc = ccMap.get(s.classCourseId) || {}
            return { ...s, courseName: s.courseName || cc.courseName || '', className: s.className || cc.className || '', teacherName: s.teacherName || cc.teacherName || '' }
          })
        td.scheduleCount = teacherSchedules.value.length
      } else {
        teacherSchedules.value = []
        td.scheduleCount = 0
      }
    } catch { teacherSchedules.value = []; td.scheduleCount = 0 }
  } finally {
    teacherLoading.value = false
  }
}

function onTeacherChange(val: number | undefined) {
  if (val) {
    loadTeacherDetail()
  } else {
    Object.keys(teacherInfo).forEach(k => delete teacherInfo[k])
    teacherCourses.value = []
    teacherSchedules.value = []
  }
}

onMounted(async () => {
  teacherOptions.value = await fetchUserOptions('teacher')
  await loadGlobal()
})
</script>

<style scoped>
.ov-page { padding: 28px 16px 16px; display: flex; flex-direction: column; gap: 16px; min-height: 100%; background: var(--el-bg-color-page); }

/* ===== Hero ===== */
.ov-hero { position: relative; overflow: hidden; border-radius: 12px; border: 1px solid #e2e8f0; }
.ov-hero--teal { background: linear-gradient(135deg, #f0fdfa 0%, #fff 40%, #ecfeff 100%); }
.ov-hero__bg { position: absolute; inset: 0; background: radial-gradient(circle at top right, rgba(20,184,166,.1), transparent 60%); pointer-events: none; }
.ov-hero__content { position: relative; padding: 20px 24px; }
.ov-hero__top { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; flex-wrap: wrap; gap: 12px; }
.ov-hero__badge { display: inline-flex; align-items: center; gap: 6px; padding: 4px 12px; border-radius: 4px; border: 1px solid #99f6e4; background: rgba(255,255,255,.7); font-size: 11px; font-weight: 600; letter-spacing: .18em; text-transform: uppercase; color: #134e4a; }
.ov-hero__intro h1 { margin: 0 0 6px; font-size: 26px; font-weight: 800; color: #0f172a; }
.ov-hero__intro p { margin: 0 0 18px; font-size: 14px; color: #475569; line-height: 1.7; max-width: 680px; }

/* ===== Metric Grid ===== */
.ov-metric-grid { display: grid; gap: 12px; }
.ov-metric-grid--4 { grid-template-columns: repeat(4, 1fr); }
.ov-metric-grid--3 { grid-template-columns: repeat(3, 1fr); }
.ov-metric-card { padding: 14px; border-radius: 8px; border: 1px solid rgba(255,255,255,.7); }
.ov-metric-card--glass { background: rgba(255,255,255,.78); backdrop-filter: blur(6px); }
.ov-metric-card__head { display: flex; align-items: center; justify-content: space-between; }
.ov-metric-card__label { font-size: 11px; font-weight: 600; letter-spacing: .14em; text-transform: uppercase; color: #64748b; }
.ov-metric-card__icon { font-size: 18px; color: #0d9488; }
.ov-metric-card__value { margin-top: 12px; font-size: 28px; font-weight: 800; color: #0f172a; letter-spacing: -.02em; }
.ov-metric-card__desc { margin: 6px 0 0; font-size: 12px; color: #64748b; line-height: 1.5; }

/* ===== Body Layout ===== */
.ov-body { display: grid; gap: 16px; grid-template-columns: 1fr 340px; align-items: start; }
.ov-body__main { display: flex; flex-direction: column; gap: 16px; }
.ov-body__side { display: flex; flex-direction: column; gap: 16px; }

/* ===== Card ===== */
.ov-card { background: #fff; border-radius: 12px; border: 1px solid #e5eef8; padding: 18px 20px; }
.ov-card--highlight { border-color: #99f6e4; background: linear-gradient(180deg, #f0fdfa 0%, #fff 100%); }
.ov-card__header { display: flex; align-items: flex-start; justify-content: space-between; margin-bottom: 16px; padding-bottom: 12px; border-bottom: 1px solid #f1f5f9; }
.ov-card__header h2 { margin: 0; font-size: 16px; font-weight: 700; color: #0f172a; display: flex; align-items: center; gap: 8px; }
.ov-card__header h2 i { color: #0d9488; }
.ov-card__header p { margin: 4px 0 0; font-size: 13px; color: #94a3b8; }
.ov-card__tag { font-size: 11px; font-weight: 600; letter-spacing: .16em; text-transform: uppercase; color: #94a3b8; white-space: nowrap; }
.ov-card__icon { font-size: 18px; color: #94a3b8; }
.ov-card__icon.is-teal { color: #0d9488; }

/* ===== Stat Box ===== */
.ov-stat-box { padding: 12px 14px; border-radius: 8px; border: 1px solid #e5eef8; background: #f8fafc; }
.ov-stat-box.is-accent { background: #f0fdfa; border-color: #99f6e4; }
.ov-stat-box__label { font-size: 11px; font-weight: 600; letter-spacing: .14em; text-transform: uppercase; color: #64748b; }
.ov-stat-box__value { margin-top: 8px; font-size: 24px; font-weight: 800; color: #0f172a; }
.ov-stat-box__desc { margin: 4px 0 0; font-size: 12px; color: #94a3b8; }

/* ===== Progress ===== */
.ov-progress-list { display: flex; flex-direction: column; gap: 14px; }
.ov-progress-item { padding: 14px; border-radius: 8px; border: 1px solid #e5eef8; background: #fff; }
.ov-progress-item__head { display: flex; justify-content: space-between; align-items: flex-start; gap: 12px; }
.ov-progress-item__title { font-size: 14px; font-weight: 600; color: #1e293b; }
.ov-progress-item__desc { margin: 2px 0 0; font-size: 12px; color: #94a3b8; }
.ov-progress-item__right { text-align: right; }
.ov-progress-item__value { font-size: 24px; font-weight: 800; color: #0f172a; }
.ov-progress-item__caption { font-size: 11px; font-weight: 600; color: #94a3b8; }
.ov-progress-bar { margin-top: 10px; height: 6px; border-radius: 3px; background: #f1f5f9; overflow: hidden; }
.ov-progress-bar__fill { height: 100%; border-radius: 3px; transition: width .3s; }
.ov-progress-bar__fill.is-green { background: linear-gradient(to right, #10b981, #06b6d4); }
.ov-progress-bar__fill.is-amber { background: linear-gradient(to right, #f59e0b, #f97316); }

/* ===== Section Sub ===== */
.ov-section-sub { margin-top: 18px; }
.ov-section-sub h3 { margin: 0 0 10px; font-size: 14px; font-weight: 600; color: #334155; }
.is-complete { color: #059669; font-weight: 700; }

/* ===== Quick Links ===== */
.ov-links { display: flex; flex-direction: column; gap: 8px; }
.ov-link { display: flex; align-items: flex-start; gap: 12px; width: 100%; padding: 10px 12px; border-radius: 8px; border: 1px solid #e5eef8; background: #f8fafc; cursor: pointer; transition: all .2s; text-align: left; }
.ov-link:hover { border-color: #99f6e4; background: #f0fdfa; transform: translateY(-1px); }
.ov-link__icon { width: 36px; height: 36px; border-radius: 8px; background: #fff; display: flex; align-items: center; justify-content: center; flex-shrink: 0; box-shadow: 0 1px 3px rgba(0,0,0,.06); }
.ov-link__icon i { font-size: 18px; color: #0d9488; }
.ov-link__text { min-width: 0; }
.ov-link__title { font-size: 14px; font-weight: 600; color: #0f172a; }
.ov-link__desc { margin: 2px 0 0; font-size: 12px; color: #94a3b8; }

/* ===== Tips ===== */
.ov-tips { list-style: none; padding: 0; margin: 0; display: flex; flex-direction: column; gap: 10px; }
.ov-tips li { display: flex; align-items: flex-start; gap: 10px; font-size: 13px; color: #475569; line-height: 1.6; }
.ov-tips__dot { width: 20px; height: 20px; border-radius: 4px; background: #ecfdf5; display: flex; align-items: center; justify-content: center; flex-shrink: 0; margin-top: 2px; }
.ov-tips__dot i { font-size: 12px; color: #059669; }

/* ===== Responsive ===== */
@media (max-width: 1200px) {
  .ov-body { grid-template-columns: 1fr; }
}
@media (max-width: 768px) {
  .ov-metric-grid--4 { grid-template-columns: repeat(2, 1fr); }
  .ov-metric-grid--3 { grid-template-columns: repeat(2, 1fr); }
  .ov-hero__intro h1 { font-size: 20px; }
}
@media (max-width: 480px) {
  .ov-page { padding: 10px; }
  .ov-metric-grid--4, .ov-metric-grid--3 { grid-template-columns: 1fr; }
}
</style>
