<template>
  <el-dialog
    :model-value="visible"
    title="课程详情"
    width="840px"
    destroy-on-close
    append-to-body
    modal-append-to-body
    class="course-offerings-dialog"
    modal-class="course-offerings-dialog-modal"
    @update:model-value="$emit('update:visible', $event)"
  >
    <div class="detail-wrap">
      <div class="detail-hero">
        <div class="detail-hero__top">
          <div class="detail-hero__eyebrow">{{ row.termName || currentTermLabel }}</div>
          <div class="detail-code">{{ row.courseCode || '-' }}</div>
        </div>
        <div class="detail-header__main">
          <div class="detail-name">{{ row.courseName || '-' }}</div>
        </div>
        <div class="detail-header__tags">
          <el-tag
            v-if="requiredText"
            size="small"
            class="pill"
            :type="requiredType"
            effect="light"
          >
            {{ requiredText }}
          </el-tag>
          <el-tag v-if="row.subjectType" size="small" class="pill" effect="plain">{{ row.subjectType }}</el-tag>
          <el-tag v-if="row.courseCategory" size="small" class="pill" effect="plain">{{ row.courseCategory }}</el-tag>
          <el-tag v-if="row.openDeptName" size="small" class="pill" effect="plain">{{ row.openDeptName }}</el-tag>
          <el-tag v-if="row.campusName" size="small" class="pill" effect="plain">{{ row.campusName }}</el-tag>
        </div>
        <div class="detail-hero__intro">
          {{ introText }}
        </div>
        <div class="detail-metrics">
          <div v-for="item in metrics" :key="item.label" class="detail-metric">
            <div class="detail-metric__label">{{ item.label }}</div>
            <div class="detail-metric__value">{{ item.value }}</div>
            <div class="detail-metric__hint">{{ item.hint }}</div>
          </div>
        </div>
      </div>

      <div class="detail-layout">
        <div v-for="group in infoGroups" :key="group.key" class="detail-panel">
          <div class="detail-panel__title">{{ group.title }}</div>
          <div class="detail-grid">
            <div v-for="item in group.items" :key="item.label" class="detail-item">
              <div class="detail-item__k">{{ item.label }}</div>
              <div class="detail-item__v">{{ item.value }}</div>
            </div>
          </div>
        </div>

        <div class="detail-panel detail-panel--wide">
          <div class="detail-panel__title">上课安排</div>
          <div v-if="scheduleSummaryLines.length" class="detail-schedule-summary">
            <div class="detail-schedule-summary__title">默认展示</div>
            <div class="detail-schedule-summary__list">
              <div v-for="(line, idx) in scheduleSummaryLines" :key="`summary-${idx}`" class="detail-schedule-summary__line">
                {{ line }}
              </div>
            </div>
          </div>
          <el-collapse v-if="scheduleCards.length" v-model="scheduleActiveName" class="detail-schedule-collapse">
            <el-collapse-item
              name="schedule-list"
              class="detail-schedule-accordion"
            >
              <template #title>
                <div class="detail-schedule-accordion__title">
                  <span class="detail-schedule-accordion__day">具体排课</span>
                  <span class="detail-schedule-accordion__section">{{ scheduleCards.length }} 条安排</span>
                  <span class="detail-schedule-accordion__time">点击展开查看明细</span>
                </div>
              </template>
              <el-scrollbar class="detail-schedule-scroll">
                <div class="detail-schedule-list">
                  <div v-for="(item, idx) in scheduleCards" :key="`${item.date}-${idx}`" class="detail-schedule-card">
                    <div class="detail-schedule-card__head">
                      <div class="detail-schedule-card__day">{{ item.weekLabel }}</div>
                      <div class="detail-schedule-card__weeks">{{ item.weeksText }}</div>
                    </div>
                    <div class="detail-schedule-card__time">{{ item.sectionText }}<span v-if="item.timeRange"> · {{ item.timeRange }}</span></div>
                    <div class="detail-schedule-card__place">{{ item.classroom || '待定教室' }}</div>
                    <div class="detail-schedule-card__date">{{ item.date }}</div>
                  </div>
                </div>
              </el-scrollbar>
            </el-collapse-item>
          </el-collapse>
          <div v-else-if="!scheduleSummaryLines.length" class="detail-empty-panel">
            当前还没有排课信息。
          </div>
        </div>

        <div class="detail-panel detail-panel--wide">
          <div class="detail-panel__title">补充说明</div>
          <div class="detail-note">
            {{ noteText }}
          </div>
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'

const props = defineProps<{
  visible: boolean
  row: any
  currentTermLabel: string
}>()

defineEmits<{
  'update:visible': [value: boolean]
}>()

const scheduleActiveName = ref('')

const requiredText = computed(() => {
  const r = props.row
  if (!r) return ''
  if (r.requiredLabel) return String(r.requiredLabel)
  if (r.requiredFlag === 'Y') return '必修'
  if (r.requiredFlag === 'N') return '选修'
  return ''
})

const requiredType = computed(() => {
  return requiredText.value.includes('必') ? 'danger' : 'success'
})

const introText = computed(() => {
  return props.row?.intro || '暂无课程简介，建议先查看教学班、教师安排和上课时间地点。'
})

type DetailMetric = { label: string; value: string; hint: string }
const metrics = computed<DetailMetric[]>(() => {
  const r = props.row || {}
  return [
    { label: '学分', value: String(r.credits ?? '-'), hint: '课程学分' },
    { label: '教学班', value: r.teachingClassCode || '-', hint: r.teachingClassName || r.className || '教学班标识' },
    { label: '总学时', value: String(r.totalHours ?? '-'), hint: `周学时 ${r.weeklyHours ?? '-'}` },
    { label: '选课情况', value: r.studentCountText || '-', hint: r.remainingSeats != null ? `剩余名额 ${r.remainingSeats}` : '当前已选人数' },
  ]
})

type DetailInfoItem = { label: string; value: string }
type DetailInfoGroup = { key: string; title: string; items: DetailInfoItem[] }
const infoGroups = computed<DetailInfoGroup[]>(() => {
  const r = props.row || {}
  return [
    {
      key: 'basic',
      title: '基本信息',
      items: [
        { label: '课程类别', value: r.courseCategory || '-' },
        { label: '课程属性', value: r.subjectType || '-' },
        { label: '开课部门', value: r.openDeptName || '-' },
        { label: '上课校区', value: r.campusName || '-' },
      ],
    },
    {
      key: 'teaching',
      title: '教学信息',
      items: [
        { label: '授课教师', value: r.teacherName || '-' },
        { label: '授课语言', value: r.teachingLanguage || '-' },
        { label: '考核方式', value: r.assessmentType || '-' },
        { label: '教学班名称', value: r.teachingClassName || r.className || '-' },
      ],
    },
    {
      key: 'requirements',
      title: '选课与要求',
      items: [
        { label: '先修课程', value: r.prerequisiteCourse || '无' },
        { label: '等级要求', value: r.courseLevelRequirement || '无' },
        { label: '任务类型', value: r.taskType || '-' },
        { label: '开课对象', value: r.className || r.major || '-' },
      ],
    },
  ]
})

type ScheduleCard = {
  date: string; weekLabel: string; weeksText: string
  sectionText: string; timeRange: string; classroom: string
}
const scheduleCards = computed<ScheduleCard[]>(() => {
  const details = Array.isArray(props.row?.scheduleDetails) ? props.row.scheduleDetails : []
  return details.slice(0, 12).map((item: any) => ({
    date: item?.date || '-',
    weekLabel: item?.weekLabel || '-',
    weeksText: item?.weeksText || '未配置周次',
    sectionText: item?.sectionText || '-',
    timeRange: item?.startTime && item?.endTime ? `${item.startTime}-${item.endTime}` : '',
    classroom: item?.classroom || item?.classroomName || '-',
  }))
})

const scheduleSummaryLines = computed(() => {
  const text = String(props.row?.scheduleText || '').trim()
  if (!text) return []
  return text.split(/\r?\n/).filter(Boolean)
})

const noteText = computed(() => {
  const r = props.row || {}
  return r.remark || r.classroom || r.scheduleText || '暂无补充说明。'
})
</script>

<style scoped>
.detail-wrap {
  padding: 2px 2px 4px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.detail-hero {
  border: 1px solid #e2e8f0;
  border-radius: 14px;
  padding: 16px 16px;
  background: linear-gradient(135deg, rgba(37, 99, 235, 0.08), rgba(248, 250, 252, 0.96));
}

.detail-hero__top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.detail-hero__eyebrow {
  color: #2563eb;
  font-size: 12px;
  font-weight: 800;
}

.detail-header__main {
  margin-top: 10px;
}

.detail-name {
  color: #0f172a;
  font-weight: 900;
  font-size: 24px;
  line-height: 1.2;
}

.detail-code {
  color: #94a3b8;
  font-weight: 800;
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.7);
  border: 1px solid rgba(226, 232, 240, 0.8);
}

.detail-header__tags {
  margin-top: 12px;
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.pill {
  border-radius: 6px;
  font-weight: 700;
}

.detail-hero__intro {
  margin-top: 12px;
  color: #475569;
  font-size: 13px;
  line-height: 1.7;
}

.detail-metrics {
  margin-top: 14px;
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10px;
}

.detail-metric {
  padding: 12px 12px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.88);
  border: 1px solid rgba(226, 232, 240, 0.88);
}

.detail-metric__label {
  color: #94a3b8;
  font-size: 12px;
  font-weight: 700;
}

.detail-metric__value {
  margin-top: 6px;
  color: #0f172a;
  font-size: 18px;
  font-weight: 900;
}

.detail-metric__hint {
  margin-top: 4px;
  color: #64748b;
  font-size: 12px;
}

.detail-layout {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.detail-panel {
  border: 1px solid #e2e8f0;
  border-radius: 14px;
  background: #fff;
  padding: 14px 14px;
}

.detail-panel--wide {
  grid-column: 1 / -1;
}

.detail-panel__title {
  color: #0f172a;
  font-size: 15px;
  font-weight: 900;
  margin-bottom: 12px;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px 12px;
}

.detail-item {
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  background: #f8fafc;
  padding: 10px 12px;
}

.detail-item__k {
  color: #94a3b8;
  font-size: 12px;
  font-weight: 700;
}

.detail-item__v {
  margin-top: 6px;
  color: #0f172a;
  font-size: 13px;
  line-height: 1.55;
}

.detail-schedule-summary {
  border: 1px solid #e2e8f0;
  border-radius: 14px;
  background: #f8fafc;
  padding: 12px 14px;
}

.detail-schedule-summary__title {
  color: #0f172a;
  font-size: 13px;
  font-weight: 800;
}

.detail-schedule-summary__list {
  margin-top: 10px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.detail-schedule-summary__line {
  color: #475569;
  font-size: 13px;
  line-height: 1.7;
}

.detail-schedule-collapse {
  margin-top: 12px;
  border-top: 0;
}

.detail-schedule-collapse :deep(.el-collapse-item__header) {
  font-size: 13px;
  font-weight: 700;
  color: #0f172a;
  padding: 0 14px;
  border-radius: 12px;
  background: #fff;
  border: 1px solid #e2e8f0;
}

.detail-schedule-collapse :deep(.el-collapse-item__wrap) {
  border-bottom: 0;
  background: transparent;
}

.detail-schedule-collapse :deep(.el-collapse-item__content) {
  padding: 10px 0 2px;
}

.detail-schedule-accordion__title {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  min-width: 0;
}

.detail-schedule-accordion__day {
  color: #0f172a;
  font-weight: 900;
}

.detail-schedule-accordion__section {
  color: #2563eb;
  font-weight: 700;
}

.detail-schedule-accordion__time {
  color: #94a3b8;
  font-size: 12px;
}

.detail-schedule-scroll {
  max-height: 320px;
}

.detail-schedule-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding-right: 4px;
}

.detail-schedule-card {
  border: 1px solid #e2e8f0;
  border-radius: 14px;
  padding: 12px 12px;
  background: linear-gradient(180deg, #fff, #f8fafc);
}

.detail-schedule-card__head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
}

.detail-schedule-card__day {
  color: #0f172a;
  font-weight: 900;
  font-size: 14px;
}

.detail-schedule-card__weeks {
  color: #2563eb;
  font-size: 12px;
  font-weight: 700;
}

.detail-schedule-card__time {
  margin-top: 10px;
  color: #0f172a;
  font-size: 13px;
  font-weight: 700;
}

.detail-schedule-card__place {
  margin-top: 8px;
  color: #475569;
  font-size: 13px;
  line-height: 1.6;
}

.detail-schedule-card__date {
  margin-top: 8px;
  color: #94a3b8;
  font-size: 12px;
}

.detail-note {
  border-radius: 12px;
  background: #f8fafc;
  border: 1px dashed #e2e8f0;
  padding: 12px 14px;
  color: #475569;
  font-size: 13px;
  line-height: 1.75;
}

.detail-empty-panel {
  border-radius: 12px;
  background: #f8fafc;
  border: 1px dashed #e2e8f0;
  padding: 16px 14px;
  color: #94a3b8;
  font-size: 13px;
  text-align: center;
}

@media (max-width: 768px) {
  .detail-layout {
    grid-template-columns: 1fr;
  }

  .detail-metrics {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .detail-metrics {
    grid-template-columns: 1fr;
  }
}
</style>
