<template>
  <div class="portal-page task-detail-page">
    <div class="portal-page-header">
      <div class="portal-page-title">
        <div class="portal-page-title-icon"></div>
        <span>任务详情</span>
      </div>
      <div class="portal-page-actions">
        <el-button @click="goBack" plain>返回</el-button>
      </div>
    </div>

    <section class="task-detail-hero">
      <div class="hero-content">
        <div class="hero-eyebrow">任务详情</div>
        <div class="hero-title">{{ taskDetail.taskTitle || '未命名任务' }}</div>
        <p class="hero-desc">{{ taskDetail.taskDesc || '请按任务要求完成本次学习任务。' }}</p>
      </div>
      <div class="hero-stats">
        <div class="hero-metric">
          <span>任务类型</span>
          <strong>{{ taskTypeLabel(taskDetail.taskType) }}</strong>
        </div>
        <div class="hero-metric">
          <span>完成状态</span>
          <strong :class="{'text-success': taskDetail.completionStatus === 'COMPLETED', 'text-warning': taskDetail.completionStatus === 'IN_PROGRESS'}">
            {{ completionStatusLabel(taskDetail.completionStatus) }}
          </strong>
        </div>
      </div>
    </section>

    <div class="task-quick-actions mt20">
      <el-button v-if="primaryAction" type="primary" @click="handlePrimaryAction">{{ primaryAction.label }}</el-button>
      <el-button v-if="courseActionVisible" plain @click="openRelatedCourse">查看关联课程</el-button>
      <el-button v-if="taskDetail.classCourseId" plain @click="openRelatedCourse(true)">打开课程详情</el-button>
    </div>

    <div class="portal-grid portal-grid-2 mt20">
      <div class="task-section-card">
        <div class="section-header">
          <div class="section-title">任务信息</div>
        </div>
        <div class="task-info-grid" v-loading="loading">
          <div class="task-info-item"><span>任务标题</span><strong>{{ taskDetail.taskTitle || '--' }}</strong></div>
          <div class="task-info-item"><span>任务类型</span><strong>{{ taskTypeLabel(taskDetail.taskType) }}</strong></div>
          <div class="task-info-item"><span>关联课程</span><strong>{{ relatedCourseName }}</strong></div>
          <div class="task-info-item"><span>最近提交状态</span><strong :class="latestSubmissionStatusClass">{{ latestSubmissionStatus }}</strong></div>
          <div class="task-info-item"><span>开始时间</span><strong>{{ formatDateTime(taskDetail.startTime) || '--' }}</strong></div>
          <div class="task-info-item"><span>截止时间</span><strong class="text-danger">{{ formatDateTime(taskDetail.dueTime) || '--' }}</strong></div>
          <div class="task-info-item"><span>课程ID</span><strong>{{ taskDetail.courseId || '--' }}</strong></div>
          <div class="task-info-item"><span>教学班ID</span><strong>{{ taskDetail.classCourseId || '--' }}</strong></div>
          <div class="task-info-item task-info-item--full"><span>任务描述</span><div class="desc-text">{{ taskDetail.taskDesc || '暂无任务描述' }}</div></div>
        </div>
      </div>

      <div class="task-section-card task-section-card--soft">
        <div class="section-header">
          <div class="section-title">提交回执</div>
        </div>
        <el-form label-width="88px" v-loading="loading" class="submit-form">
          <el-form-item label="提交说明">
            <el-input v-model="submitForm.submitContent" type="textarea" :rows="7" maxlength="2000" show-word-limit placeholder="填写你的完成说明、答案摘要、实验结果或阅读感想" />
          </el-form-item>
          <el-form-item label="上传附件">
            <el-upload
              class="task-upload"
              action="#"
              :auto-upload="false"
              :show-file-list="false"
              :http-request="handleAttachmentUpload"
              :before-upload="beforeAttachmentUpload"
              multiple
            >
              <el-button :loading="uploading" plain>选择并上传文件</el-button>
            </el-upload>
          </el-form-item>
          <el-form-item label="附件列表">
            <div class="task-attachment-list">
              <div v-if="!attachmentList.length" class="task-attachment-empty">暂未上传附件</div>
              <div v-for="(item, index) in attachmentList" :key="`${item.url}-${index}`" class="task-attachment-item">
                <a :href="item.url" target="_blank" rel="noreferrer">{{ item.name }}</a>
                <el-button link type="danger" @click="removeAttachment(index)">移除</el-button>
              </div>
            </div>
          </el-form-item>
          <el-form-item label="批阅状态">
            <el-tag effect="light" :type="taskDetail.submission?.reviewStatus === 'REVIEWED' ? 'success' : 'info'">{{ reviewStatusLabel(taskDetail.submission?.reviewStatus) }}</el-tag>
          </el-form-item>
          <el-form-item label="教师反馈" v-if="taskDetail.submission?.teacherFeedback">
            <div class="task-feedback">{{ taskDetail.submission?.teacherFeedback }}</div>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="submitting" @click="submitTask" class="submit-btn">提交任务</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { getPortalCourseDetail, getPortalTaskDetail, markPortalTaskRead, submitPortalTask, uploadPortalTaskAttachment } from '@/api/portal'
import usePortalUserStore from '@/store/user'

const router = useRouter()
const route = useRoute()
const userStore = usePortalUserStore()
const loading = ref(false)
const submitting = ref(false)
const uploading = ref(false)
const taskDetail = ref<any>({})
const relatedCourse = ref<any>(null)
const submitForm = reactive<any>({
  submitContent: '',
})
const attachmentList = ref<Array<{ name: string; url: string }>>([])

const primaryAction = computed(() => {
  const type = String(taskDetail.value.taskType || '').toUpperCase()
  if (type === 'EXAM') return { label: '去考试', action: 'exam' as const }
  if (type === 'COURSE') return { label: '查看课程内容', action: 'course' as const }
  if (type === 'HOMEWORK') return { label: '打开课程查看作业', action: 'course' as const }
  if (type === 'PRACTICE') return { label: '打开课程查看实验任务', action: 'course' as const }
  if (type === 'READING') return { label: '打开课程查看阅读任务', action: 'course' as const }
  return null
})

const courseActionVisible = computed(() => Boolean(taskDetail.value.classCourseId || taskDetail.value.courseId))
const relatedCourseName = computed(() => {
  if (relatedCourse.value?.courseName) {
    const className = relatedCourse.value?.className ? ` / ${relatedCourse.value.className}` : ''
    return `${relatedCourse.value.courseName}${className}`
  }
  if (taskDetail.value.classCourseId) return `教学班 ${taskDetail.value.classCourseId}`
  if (taskDetail.value.courseId) return `课程 ${taskDetail.value.courseId}`
  return '--'
})
const latestSubmissionStatus = computed(() => {
  const submission = taskDetail.value.submission
  if (!submission) return '尚未提交'
  const reviewStatus = String(submission.reviewStatus || '')
  if (reviewStatus === 'REVIEWED') return `最近已提交，${reviewStatusLabel(reviewStatus)}`
  if (reviewStatus === 'RETURNED') return `最近已提交，${reviewStatusLabel(reviewStatus)}`
  if (reviewStatus === 'PENDING') return '最近已提交，待批阅'
  if (submission.submitTime || submission.submitContent || submission.attachmentUrls) return '最近已提交，等待处理'
  return '尚未提交'
})
const latestSubmissionStatusClass = computed(() => {
  const reviewStatus = String(taskDetail.value.submission?.reviewStatus || '')
  if (reviewStatus === 'REVIEWED') return 'text-success'
  if (reviewStatus === 'RETURNED') return 'text-danger'
  if (taskDetail.value.submission) return 'text-warning'
  return ''
})

function taskTypeLabel(value?: string) {
  const map: Record<string, string> = {
    HOMEWORK: '作业任务',
    EXAM: '考试任务',
    PRACTICE: '实验任务',
    READING: '阅读任务',
    COURSE: '课程任务',
  }
  return map[String(value || '')] || value || '--'
}

function completionStatusLabel(value?: string) {
  const map: Record<string, string> = {
    PENDING: '待完成',
    IN_PROGRESS: '进行中',
    COMPLETED: '已完成',
  }
  return map[String(value || '')] || '待完成'
}

function reviewStatusLabel(value?: string) {
  const map: Record<string, string> = {
    PENDING: '待批阅',
    REVIEWED: '已批阅',
    RETURNED: '需修改',
  }
  return map[String(value || '')] || '待批阅'
}

function formatDateTime(value?: string | number | Date | null) {
  if (!value) return ''
  const date = value instanceof Date ? value : new Date(value)
  if (Number.isNaN(date.getTime())) return String(value)
  const year = date.getFullYear()
  const month = `${date.getMonth() + 1}`.padStart(2, '0')
  const day = `${date.getDate()}`.padStart(2, '0')
  const hour = `${date.getHours()}`.padStart(2, '0')
  const minute = `${date.getMinutes()}`.padStart(2, '0')
  const second = `${date.getSeconds()}`.padStart(2, '0')
  return `${year}-${month}-${day} ${hour}:${minute}:${second}`
}

function goBack() {
  const from = String(route.query.from || '')
  if (from === 'dashboard') {
    router.push('/student/dashboard')
    return
  }
  if (from === 'messages') {
    router.push('/student/messages?tab=todo')
    return
  }
  router.push('/student/plaza')
}

function openRelatedCourse(forceOpen = false) {
  const openCourseId = taskDetail.value.classCourseId || taskDetail.value.courseId
  if (!openCourseId) {
    ElMessage.warning('当前任务没有关联课程信息')
    return
  }
  const taskType = String(taskDetail.value.taskType || '').toUpperCase()
  const tab = taskType === 'EXAM' ? 'papers' : 'basic'
  const relatedPaperId = taskDetail.value.paperId || taskDetail.value.action?.targetId || taskDetail.value.action?.paperId || taskDetail.value.action?.row?.paperId
  router.push({
    path: '/student/courses',
    query: {
      openCourseId: String(openCourseId),
      fromTask: '1',
      tab,
      taskType,
      taskTitle: String(taskDetail.value.taskTitle || ''),
      dispatchId: String(taskDetail.value.dispatchId || route.params.dispatchId || ''),
      latestSubmissionStatus: String(latestSubmissionStatus.value || ''),
      dueTime: String(taskDetail.value.dueTime || ''),
      ...(relatedPaperId ? { paperId: String(relatedPaperId) } : {}),
    },
  })
}

function handlePrimaryAction() {
  if (!primaryAction.value) return
  if (primaryAction.value.action === 'exam') {
    router.push('/student/exams')
    return
  }
  if (primaryAction.value.action === 'course') {
    openRelatedCourse(true)
  }
}

function parseAttachmentUrls(value?: string) {
  if (!value) return []
  try {
    const parsed = JSON.parse(value)
    if (!Array.isArray(parsed)) return []
    return parsed.map((item: any) => ({
      name: getAttachmentName(String(item || '')),
      url: String(item || ''),
    })).filter((item: any) => item.url)
  } catch {
    return []
  }
}

function stringifyAttachmentUrls() {
  return JSON.stringify(attachmentList.value.map((item) => item.url))
}

function getAttachmentName(url: string) {
  if (!url) return '未命名附件'
  const parts = url.split('/')
  return decodeURIComponent(parts[parts.length - 1] || url)
}

function beforeAttachmentUpload(rawFile: File) {
  const sizeMb = rawFile.size / 1024 / 1024
  if (sizeMb > 20) {
    ElMessage.warning('单个附件大小不能超过 20MB')
    return false
  }
  return true
}

async function handleAttachmentUpload(options: any) {
  const rawFile = options.file as File
  uploading.value = true
  try {
    const res = await uploadPortalTaskAttachment(rawFile)
    const url = res.url || res.data?.url || ''
    if (!url) {
      throw new Error('上传结果缺少文件地址')
    }
    attachmentList.value.push({
      name: rawFile.name || getAttachmentName(url),
      url,
    })
    ElMessage.success('附件上传成功')
    options.onSuccess?.(res)
  } catch (error: any) {
    ElMessage.error(error?.message || '附件上传失败')
    options.onError?.(error)
  } finally {
    uploading.value = false
  }
}

function removeAttachment(index: number) {
  attachmentList.value.splice(index, 1)
}

async function loadData() {
  const dispatchId = Number(route.params.dispatchId)
  if (!dispatchId) return
  loading.value = true
  try {
    await markPortalTaskRead(dispatchId).catch(() => {})
    const res = await getPortalTaskDetail(dispatchId)
    taskDetail.value = res.data || {}
    submitForm.submitContent = taskDetail.value.submission?.submitContent || ''
    attachmentList.value = parseAttachmentUrls(taskDetail.value.submission?.attachmentUrls)
    const userId = userStore.user?.userId
    if (userId && taskDetail.value.classCourseId) {
      try {
        const courseRes = await getPortalCourseDetail({ userId, classCourseId: taskDetail.value.classCourseId })
        relatedCourse.value = courseRes.data?.course || null
      } catch {
        relatedCourse.value = null
      }
    } else {
      relatedCourse.value = null
    }
  } finally {
    loading.value = false
  }
}

async function submitTask() {
  const dispatchId = Number(route.params.dispatchId)
  if (!dispatchId) return
  if (!String(submitForm.submitContent || '').trim() && !attachmentList.value.length) {
    ElMessage.warning('请填写提交说明或上传附件')
    return
  }
  submitting.value = true
  try {
    await submitPortalTask(dispatchId, {
      submitContent: submitForm.submitContent,
      attachmentUrls: stringifyAttachmentUrls(),
    })
    ElMessage.success('任务提交成功')
    await loadData()
  } finally {
    submitting.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.task-detail-page {
  padding: 24px 24px 40px;
}

.portal-page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.portal-page-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.portal-page-title-icon {
  width: 4px;
  height: 20px;
  background: var(--brand-primary, #266fcb);
  border-radius: 2px;
}

.task-detail-hero {
  display: grid;
  grid-template-columns: minmax(0, 1.35fr) minmax(260px, 0.8fr);
  gap: 20px;
  padding: 32px;
  border-radius: 12px;
  background: linear-gradient(135deg, #f0f7ff 0%, #ffffff 100%);
  border: 1px solid #e1edfa;
  margin-bottom: 24px;
}

.hero-content {
  display: flex;
  flex-direction: column;
}

.hero-eyebrow {
  display: inline-flex;
  align-self: flex-start;
  padding: 4px 12px;
  border-radius: 4px;
  background: rgba(38, 111, 203, 0.1);
  color: var(--brand-primary, #266fcb);
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 12px;
}

.hero-title {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 16px;
  line-height: 1.3;
}

.hero-desc {
  font-size: 15px;
  line-height: 1.8;
  color: #606266;
  max-width: 800px;
  margin: 0;
}

.hero-stats {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.hero-metric {
  background: #ffffff;
  padding: 20px;
  border-radius: 8px;
  border: 1px solid #ebeef5;
  display: flex;
  flex-direction: column;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.02);
}

.hero-metric span {
  color: #909399;
  font-size: 14px;
  margin-bottom: 8px;
}

.hero-metric strong {
  font-size: 20px;
  color: #303133;
  font-weight: 600;
}

.text-success {
  color: #67c23a !important;
}

.text-warning {
  color: #e6a23c !important;
}

.text-danger {
  color: #f56c6c !important;
}

.task-quick-actions {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.task-section-card {
  background: #fff;
  border-radius: 8px;
  border: 1px solid #ebeef5;
  padding: 24px;
  height: 100%;
}

.task-section-card--soft {
  background: #fafafa;
  border: 1px solid #e4e7ed;
}

.section-header {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.task-info-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20px;
}

.task-info-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.task-info-item span {
  font-size: 13px;
  color: #909399;
}

.task-info-item strong {
  font-size: 15px;
  color: #303133;
  font-weight: 500;
}

.desc-text {
  font-size: 15px;
  color: #303133;
  line-height: 1.6;
  background: #f8f9fa;
  padding: 16px;
  border-radius: 6px;
  white-space: pre-wrap;
}

.task-info-item--full {
  grid-column: 1 / -1;
  margin-top: 8px;
}

.submit-form {
  max-width: 100%;
}

.task-feedback {
  padding: 16px;
  border-radius: 6px;
  background: #f0f9eb;
  border: 1px solid #e1f3d8;
  color: #67c23a;
  line-height: 1.6;
  font-size: 14px;
}

.task-upload {
  width: 100%;
}

.task-attachment-list {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 8px;
}

.task-attachment-empty {
  color: #c0c4cc;
  font-size: 14px;
  font-style: italic;
}

.task-attachment-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-radius: 6px;
  background: #ffffff;
  border: 1px solid #ebeef5;
  transition: all 0.3s;
}

.task-attachment-item:hover {
  border-color: #c6e2ff;
  background: #ecf5ff;
}

.task-attachment-item a {
  color: var(--brand-primary, #266fcb);
  text-decoration: none;
  font-size: 14px;
  word-break: break-all;
  margin-right: 16px;
  display: flex;
  align-items: center;
}

.task-attachment-item a:hover {
  text-decoration: underline;
}

.submit-btn {
  width: 120px;
  margin-top: 16px;
}

@media (max-width: 992px) {
  .task-detail-hero {
    grid-template-columns: 1fr;
  }
  .hero-stats {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .task-info-grid {
    grid-template-columns: 1fr;
  }
  .hero-stats {
    grid-template-columns: 1fr;
  }
}
</style>
