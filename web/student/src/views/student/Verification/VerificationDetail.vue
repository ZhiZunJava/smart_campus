<template>
  <div class="vd-page">
    <header class="vd-topbar">
      <div class="vd-topbar__left">
        <el-button text bg size="small" @click="$router.push('/student/verification')"><i class="ri-arrow-left-line" /></el-button>
        <div class="vd-topbar__title">
          <h1>{{ batch?.batchName || '学籍核对' }}</h1>
          <p>核对当前批次开放的信息项，确认无误或提交修改后等待学校审核。</p>
        </div>
      </div>
      <div class="vd-topbar__right">
        <el-tag v-if="record" :type="statusTagType(record.recordStatus)" size="large" effect="dark">
          {{ statusLabel(record.recordStatus) }}
        </el-tag>
        <span v-if="batch?.endTime" class="vd-topbar__deadline">
          <i class="ri-time-line" />
          {{ deadlineMeta(batch).label }}
        </span>
      </div>
    </header>

    <div v-if="loading" class="loading-center">
      <div class="vd-skeleton">
        <div class="vd-skeleton__topbar"></div>
        <div class="vd-skeleton__banner"></div>
        <div class="vd-skeleton__summary"></div>
        <div class="vd-skeleton__section"></div>
      </div>
    </div>

    <template v-else-if="record">
      <el-alert
        v-if="record.recordStatus === 'REJECTED'"
        type="error" :closable="false" class="vd-alert"
      >
        <template #title>审核未通过，请修改后重新提交</template>
        <p v-if="record.reviewComment" style="margin:4px 0 0">审核意见：{{ record.reviewComment }}</p>
      </el-alert>
      <el-alert v-if="record.recordStatus === 'UNDER_REVIEW'" type="warning" title="您的修改已提交，正在等待审核" :closable="false" class="vd-alert" />
      <el-alert v-if="record.recordStatus === 'APPROVED'" type="success" title="您的信息已审核通过" :closable="false" class="vd-alert" />
      <el-alert v-if="record.recordStatus === 'CONFIRMED'" type="info" title="您已确认信息无需修改" :closable="false" class="vd-alert" />

      <div class="vd-batch-banner" v-if="batch?.description || batch?.endTime">
        <div class="vd-batch-banner__inner">
          <p v-if="batch.description" class="vd-batch-banner__desc">{{ batch.description }}</p>
          <span v-if="batch.endTime" class="vd-batch-banner__time"><i class="ri-time-line" /> 截止：{{ batch.endTime }}</span>
        </div>
      </div>

      <section class="vd-summary-grid">
        <article class="vd-summary-card">
          <span>当前状态</span>
          <strong>{{ statusLabel(record.recordStatus) }}</strong>
          <p>{{ statusHintText }}</p>
        </article>
        <article class="vd-summary-card is-accent">
          <span>截止时间</span>
          <strong>{{ batch?.endTime || '未设置' }}</strong>
          <p>{{ deadlineMeta(batch).label }}</p>
        </article>
        <article class="vd-summary-card is-success">
          <span>可改字段</span>
          <strong>{{ editableFieldList.length }}</strong>
          <p>本批次开放给学生修改的信息项</p>
        </article>
        <article class="vd-summary-card is-warning">
          <span>待提交修改</span>
          <strong>{{ pendingChangeCount }}</strong>
          <p>{{ pendingChangeCount ? '提交后将进入学校审核流程' : '当前还没有新的修改内容' }}</p>
        </article>
      </section>

      <section class="vd-section">
        <div class="vd-section__header">
          <i class="ri-user-3-line" />
          <span>学生基本信息</span>
        </div>
        <div class="vd-info-grid">
          <div class="vd-info-cell" v-if="profile.avatarUrl" style="grid-row: span 3;">
            <span class="vd-info-cell__label">证件照</span>
            <img :src="profile.avatarUrl" class="vd-info-photo" alt="证件照" />
          </div>
          <div class="vd-info-cell">
            <span class="vd-info-cell__label">姓名</span>
            <strong>{{ profile.realName || '--' }}</strong>
          </div>
          <div class="vd-info-cell">
            <span class="vd-info-cell__label">学号</span>
            <strong>{{ profile.studentNo || '--' }}</strong>
          </div>
          <div class="vd-info-cell">
            <span class="vd-info-cell__label">性别</span>
            <strong>{{ genderLabel(profile.gender) }}</strong>
          </div>
          <div class="vd-info-cell">
            <span class="vd-info-cell__label">民族</span>
            <strong>{{ profile.nation || '--' }}</strong>
          </div>
          <div class="vd-info-cell">
            <span class="vd-info-cell__label">出生日期</span>
            <strong>{{ profile.birthDate || '--' }}</strong>
          </div>
          <div class="vd-info-cell">
            <span class="vd-info-cell__label">政治面貌</span>
            <strong>{{ profile.politicalStatus || '--' }}</strong>
          </div>
          <div class="vd-info-cell">
            <span class="vd-info-cell__label">专业</span>
            <strong>{{ profile.major || '--' }}</strong>
          </div>
          <div class="vd-info-cell">
            <span class="vd-info-cell__label">入学年份</span>
            <strong>{{ profile.admissionYear || '--' }}</strong>
          </div>
          <div class="vd-info-cell">
            <span class="vd-info-cell__label">学历层次</span>
            <strong>{{ profile.educationLevel || '--' }}</strong>
          </div>
          <div class="vd-info-cell">
            <span class="vd-info-cell__label">证件号码</span>
            <strong>{{ maskedId }}</strong>
          </div>
        </div>
      </section>

      <section class="vd-section">
        <div class="vd-section__header">
          <i class="ri-edit-line" />
          <span>申请信息</span>
          <span class="vd-section__sub" v-if="editableFieldList.length">共 {{ editableFieldList.length }} 项可修改字段</span>
        </div>

        <div class="vd-change-banner" v-if="canEdit">
          <div class="vd-change-banner__copy">
            <strong>{{ pendingChangeCount ? `已填写 ${pendingChangeCount} 项修改` : '先核对信息，再填写需要更正的字段' }}</strong>
            <p>{{ pendingChangeCount ? '确认后可直接提交；如果信息本身无误，也可以点“确认无误”。' : '绿色字段表示你已经填写了新的值，提交前可以继续调整。' }}</p>
            <div v-if="pendingChangePreviewList.length" class="vd-change-banner__chips">
              <span v-for="item in pendingChangePreviewList" :key="item.key">{{ item.label }}：{{ item.value }}</span>
              <span v-if="pendingChangeOverflowCount" class="is-muted">+{{ pendingChangeOverflowCount }} 项</span>
            </div>
          </div>
          <el-button v-if="pendingChangeCount" text type="primary" @click="resetAllChanges">
            <i class="ri-refresh-line" />
            清空修改
          </el-button>
        </div>

        <div v-if="!editableFieldList.length" class="vd-section__empty">
          本批次未开放可修改字段
        </div>

        <template v-else>
          <div v-for="group in editableFieldGroups" :key="group.name" class="vd-edit-group">
            <div class="vd-edit-group__title">
              <div class="vd-edit-group__title-main">
                <strong>{{ group.name }}</strong>
                <p>{{ group.desc }}</p>
              </div>
              <span>{{ group.fields.length }} 项</span>
            </div>
            <div class="vd-edit-grid">
              <div
                v-for="field in group.fields"
                :key="field.key"
                class="vd-edit-field"
                :class="{ 'is-full-width': isFullWidth(field.key), 'is-changed': changeMap[field.key] }"
              >
                <div class="vd-edit-field__header">
                  <div class="vd-edit-field__title">
                    <span class="vd-edit-field__label">{{ field.label }}</span>
                    <span v-if="hasFieldChange(field.key)" class="vd-edit-field__changed-badge">已修改</span>
                  </div>
                  <div class="vd-edit-field__header-right">
                    <span class="vd-edit-field__current">当前：{{ getFieldDisplay(field.key) || '--' }}</span>
                    <button
                      v-if="canEdit && hasFieldChange(field.key)"
                      type="button"
                      class="vd-edit-field__reset"
                      @click="resetField(field.key)"
                    >
                      清除
                    </button>
                  </div>
                </div>

                <template v-if="field.key === 'avatarUrl'">
                  <div class="vd-avatar-edit">
                    <div class="vd-avatar-edit__preview">
                      <img v-if="changeMap['avatarUrl'] || getFieldValue('avatarUrl')" :src="changeMap['avatarUrl'] || getFieldValue('avatarUrl')" />
                      <div v-else class="vd-avatar-edit__ph"><i class="ri-user-3-line" /></div>
                    </div>
                    <el-upload
                      v-if="canEdit"
                      :action="uploadUrl"
                      :headers="uploadHeaders"
                      :show-file-list="false"
                      accept="image/*"
                      :on-success="handleAvatarUploadSuccess"
                    >
                      <el-button size="small" type="primary" plain>上传新照片</el-button>
                    </el-upload>
                    <el-tag v-if="changeMap['avatarUrl']" type="success" size="small">已更新</el-tag>
                  </div>
                </template>

                <template v-else-if="field.type === 'select'">
                  <el-select
                    v-if="canEdit"
                    v-model="changeMap[field.key]"
                    :placeholder="`选择新的${field.label}`"
                    clearable size="default"
                    style="width: 100%"
                  >
                    <el-option v-for="opt in (selectOptionsMap[field.key] || [])" :key="opt.value" :label="opt.label" :value="opt.value" />
                  </el-select>
                  <span v-else class="vd-edit-field__readonly">{{ getFieldDisplay(field.key) || '--' }}</span>
                </template>

                <template v-else-if="field.type === 'date'">
                  <el-date-picker
                    v-if="canEdit"
                    v-model="changeMap[field.key]"
                    type="date"
                    :placeholder="`选择${field.label}`"
                    value-format="YYYY-MM-DD"
                    style="width: 100%"
                  />
                  <span v-else class="vd-edit-field__readonly">{{ getFieldDisplay(field.key) || '--' }}</span>
                </template>

                <template v-else>
                  <el-input
                    v-if="canEdit"
                    v-model="changeMap[field.key]"
                    :placeholder="`输入新的${field.label}`"
                  />
                  <span v-else class="vd-edit-field__readonly">{{ getFieldDisplay(field.key) || '--' }}</span>
                </template>

                <div v-if="hasFieldChange(field.key)" class="vd-edit-field__pending">
                  <span>将提交：</span>
                  <strong>{{ getPendingDisplay(field.key) }}</strong>
                </div>
              </div>
            </div>
          </div>
        </template>

        <div v-if="canEdit" class="vd-actions">
          <el-button @click="handleConfirm" :loading="submitting" size="large">
            <i class="ri-check-line" /> 确认无误
          </el-button>
          <el-button plain @click="resetAllChanges" :disabled="!pendingChangeCount" size="large">
            <i class="ri-eraser-line" /> 清空修改
          </el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting" :disabled="!hasChanges" size="large">
            <i class="ri-send-plane-line" /> 提交修改
          </el-button>
        </div>
      </section>

      <section class="vd-section">
        <div class="vd-section__header">
          <i class="ri-history-line" />
          <span>审核日志</span>
        </div>

        <div v-if="!auditLogs.length && !(record.changes && record.changes.length)" class="vd-section__empty">
          暂无审核记录
        </div>

        <!-- 变更明细表格 -->
        <div v-if="record.changes && record.changes.length" class="vd-log-block">
          <h4 class="vd-log-block__title">变更明细</h4>
          <el-table :data="record.changes" border stripe size="small" class="vd-log-table">
            <el-table-column prop="fieldLabel" label="字段" width="120" />
            <el-table-column label="原值" min-width="150">
              <template #default="{ row }">
                <img v-if="row.fieldName === 'avatarUrl' && row.oldValue" :src="row.oldValue" class="change-img" />
                <span v-else>{{ row.oldValue || '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column label="新值" min-width="150">
              <template #default="{ row }">
                <img v-if="row.fieldName === 'avatarUrl' && row.newValue" :src="row.newValue" class="change-img" />
                <span v-else>{{ row.newValue || '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="90">
              <template #default="{ row }">
                <el-tag :type="changeTagType(row.changeStatus)" size="small">{{ changeLabel(row.changeStatus) }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 时间线日志 -->
        <div v-if="auditLogs.length" class="vd-timeline">
          <div v-for="(log, idx) in auditLogs" :key="idx" class="vd-timeline__item" :class="'is-' + log.type">
            <div class="vd-timeline__dot" />
            <div class="vd-timeline__content">
              <div class="vd-timeline__title-row">
                <div class="vd-timeline__title">{{ log.title }}</div>
                <span v-if="log.badge" class="vd-timeline__badge">{{ log.badge }}</span>
              </div>
              <div v-if="log.detail" class="vd-timeline__detail">{{ log.detail }}</div>
              <div class="vd-timeline__time">{{ log.time }}</div>
            </div>
          </div>
        </div>
      </section>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getVerificationInfo, confirmVerificationNoChange, submitVerificationChanges } from '@/api/portal'
import { getToken } from '@/utils/auth'

const route = useRoute()
const router = useRouter()
const batchId = Number(route.params.batchId)

const loading = ref(false)
const submitting = ref(false)
const record = ref<any>(null)
const profile = ref<any>(null)
const batch = ref<any>(null)
const changeMap = reactive<Record<string, string>>({})

const uploadUrl = (import.meta.env.VITE_APP_BASE_API || '') + '/common/upload'
const uploadHeaders = computed(() => ({ Authorization: 'Bearer ' + getToken() }))

/* ---------- 字段定义 ---------- */
const allFields = [
  { key: 'realName', label: '姓名', group: '基本信息' },
  { key: 'gender', label: '性别', group: '基本信息', type: 'select' },
  { key: 'avatarUrl', label: '照片', group: '基本信息' },
  { key: 'formerName', label: '曾用名', group: '基本信息' },
  { key: 'nation', label: '民族', group: '基本信息' },
  { key: 'birthDate', label: '出生日期', group: '基本信息', type: 'date' },
  { key: 'politicalStatus', label: '政治面貌', group: '基本信息', type: 'select' },
  { key: 'nativePlace', label: '籍贯', group: '基本信息' },
  { key: 'idType', label: '证件类型', group: '基本信息', type: 'select' },
  { key: 'idNumber', label: '证件号码', group: '基本信息' },
  { key: 'studentNo', label: '学号', group: '学籍信息' },
  { key: 'major', label: '专业', group: '学籍信息' },
  { key: 'admissionYear', label: '入学年份', group: '学籍信息' },
  { key: 'cultivationType', label: '培养方式', group: '学籍信息' },
  { key: 'educationLevel', label: '学历层次', group: '学籍信息' },
  { key: 'schoolingLength', label: '学制', group: '学籍信息' },
  { key: 'admissionType', label: '录取类型', group: '录取信息' },
  { key: 'examNumber', label: '考生号', group: '录取信息' },
  { key: 'admissionTicketNo', label: '准考证号', group: '录取信息' },
  { key: 'admissionScore', label: '录取成绩', group: '录取信息' },
  { key: 'admissionBatch', label: '录取批次', group: '录取信息' },
  { key: 'sourceRegion', label: '生源地', group: '录取信息' },
  { key: 'graduateSchool', label: '毕业学校', group: '录取信息' },
  { key: 'expectedGraduationDate', label: '预计毕业日期', group: '毕业信息', type: 'date' },
  { key: 'actualGraduationDate', label: '实际毕业日期', group: '毕业信息', type: 'date' },
  { key: 'diplomaNo', label: '毕业证书号', group: '毕业信息' },
  { key: 'completionType', label: '结业类型', group: '毕业信息' },
  { key: 'graduationDestination', label: '毕业去向', group: '毕业信息' },
  { key: 'graduationRemark', label: '毕业备注', group: '毕业信息' },
  { key: 'degreeType', label: '学位类型', group: '学位信息' },
  { key: 'degreeAwardDate', label: '学位授予日期', group: '学位信息', type: 'date' },
  { key: 'degreeCertNo', label: '学位证书号', group: '学位信息' },
  { key: 'thesisTitle', label: '论文题目', group: '学位信息' },
  { key: 'degreeRemark', label: '学位备注', group: '学位信息' },
  { key: 'contactAddress', label: '通讯地址', group: '联系信息' },
  { key: 'homeAddress', label: '家庭地址', group: '联系信息' },
  { key: 'emergencyContact', label: '紧急联系人', group: '联系信息' },
  { key: 'emergencyPhone', label: '紧急联系电话', group: '联系信息' },
  { key: 'householdAddress', label: '户籍地址', group: '联系信息' },
  { key: 'bankCardNo', label: '银行卡号', group: '联系信息' },
]

const selectOptionsMap: Record<string, { label: string; value: string }[]> = {
  gender: [{ label: '男', value: '0' }, { label: '女', value: '1' }],
  politicalStatus: [
    { label: '中共党员', value: '中共党员' }, { label: '中共预备党员', value: '中共预备党员' },
    { label: '共青团员', value: '共青团员' }, { label: '群众', value: '群众' },
    { label: '民主党派', value: '民主党派' }, { label: '无党派人士', value: '无党派人士' },
  ],
  idType: [
    { label: '身份证', value: '身份证' }, { label: '护照', value: '护照' },
    { label: '军官证', value: '军官证' }, { label: '其他', value: '其他' },
  ],
}

const fullWidthFields = new Set(['avatarUrl', 'contactAddress', 'homeAddress', 'householdAddress', 'graduationRemark', 'degreeRemark', 'thesisTitle'])
const fieldOrderMap = new Map(allFields.map((field, index) => [field.key, index]))
const fieldGroupOrder = ['基本信息', '学籍信息', '录取信息', '联系信息', '毕业信息', '学位信息', '其他']
const fieldGroupMetaMap: Record<string, string> = {
  基本信息: '用于核对个人身份与基础画像信息。',
  学籍信息: '确认在校学籍、专业和培养信息是否准确。',
  录取信息: '核对入学来源、考试与录取相关记录。',
  联系信息: '用于学校日常联系与应急通知。',
  毕业信息: '涉及毕业时间、证书与去向等归档内容。',
  学位信息: '用于学位授予、论文与证书信息归档。',
  其他: '未归类但允许本批次修改的字段。',
}

/* ---------- 计算属性 ---------- */
const editableFieldSet = computed(() => {
  if (!batch.value?.editableFields) return new Set<string>()
  try {
    const fields = typeof batch.value.editableFields === 'string'
      ? JSON.parse(batch.value.editableFields)
      : batch.value.editableFields
    return new Set(fields)
  } catch {
    return new Set<string>()
  }
})

const editableFieldList = computed(() =>
  allFields.filter(f => editableFieldSet.value.has(f.key))
)

const editableFieldGroups = computed(() => {
  const groupMap = new Map<string, typeof editableFieldList.value>()
  for (const f of editableFieldList.value) {
    const g = f.group || '其他'
    if (!groupMap.has(g)) groupMap.set(g, [])
    groupMap.get(g)!.push(f)
  }
  return Array.from(groupMap.entries())
    .sort((left, right) => {
      const leftIndex = fieldGroupOrder.indexOf(left[0])
      const rightIndex = fieldGroupOrder.indexOf(right[0])
      const safeLeft = leftIndex === -1 ? Number.MAX_SAFE_INTEGER : leftIndex
      const safeRight = rightIndex === -1 ? Number.MAX_SAFE_INTEGER : rightIndex
      return safeLeft - safeRight
    })
    .map(([name, fields]) => ({
      name,
      desc: fieldGroupMetaMap[name] || fieldGroupMetaMap.其他,
      fields: [...fields].sort((left, right) => Number(fieldOrderMap.get(left.key) || 0) - Number(fieldOrderMap.get(right.key) || 0)),
    }))
})

const canEdit = computed(() => {
  const s = record.value?.recordStatus
  return s === 'PENDING' || s === 'REJECTED'
})

const hasChanges = computed(() => Object.values(changeMap).some(v => v !== '' && v !== undefined && v !== null))
const pendingChangeCount = computed(() => Object.keys(changeMap).filter((key) => hasFieldChange(key)).length)
const pendingChangePreviewList = computed(() => {
  return editableFieldList.value
    .filter((field) => hasFieldChange(field.key))
    .slice(0, 4)
    .map((field) => ({
      key: field.key,
      label: field.label,
      value: getPendingDisplay(field.key),
    }))
})
const pendingChangeOverflowCount = computed(() => Math.max(0, pendingChangeCount.value - pendingChangePreviewList.value.length))

const maskedId = computed(() => {
  const id = profile.value?.idNumber || ''
  if (!id || id.length < 8) return id || '--'
  return id.slice(0, 3) + '****' + id.slice(-4)
})

const statusHintText = computed(() => {
  const status = record.value?.recordStatus
  if (status === 'REJECTED') return '请根据审核意见修改后重新提交，提交后将再次进入审核。'
  if (status === 'UNDER_REVIEW') return '你的修改已经提交，当前只需等待学校审核。'
  if (status === 'APPROVED') return '当前记录已经通过审核，相关信息已确认。'
  if (status === 'CONFIRMED') return '你已确认信息无误，本批次无需再修改。'
  return '请核对开放字段；有问题就提交修改，没有问题可直接确认。'
})

/* 审核日志（从record数据中整理） */
const auditLogs = computed(() => {
  const logs: Array<{ title: string; detail?: string; time: string; type: string; badge?: string }> = []
  const r = record.value
  if (!r) return logs
  const changeSummary = buildChangeSummary(r.changes || [])
  const reviewerName = String(r.reviewUserName || '学校管理员').trim()

  if (r.createTime) {
    logs.push({
      title: '核对批次已开启',
      detail: batch.value?.endTime ? `请在 ${batch.value.endTime} 前完成核对。` : '请尽快完成本批次学籍信息核对。',
      time: r.createTime,
      type: 'info',
      badge: '待学生处理',
    })
  }

  if (r.submitTime) {
    const isConfirm = r.recordStatus === 'CONFIRMED' || (!r.changes?.length)
    logs.push({
      title: isConfirm ? '你已确认当前信息无误' : '你已提交信息更正申请',
      detail: isConfirm
        ? '系统已记录你的确认结果，当前无需补充修改。'
        : (changeSummary ? `申请修改：${changeSummary}` : '已提交修改内容，等待学校审核。'),
      time: r.submitTime,
      type: 'primary',
      badge: isConfirm ? '学生确认' : '学生提交',
    })
  }

  if (r.reviewTime) {
    const approved = r.recordStatus === 'APPROVED'
    const reviewDetails = [
      approved && changeSummary ? `已确认修改项：${changeSummary}` : '',
      !approved && changeSummary ? `需重新核对：${changeSummary}` : '',
      r.reviewComment ? `审核意见：${r.reviewComment}` : '',
    ].filter(Boolean).join('；')
    logs.push({
      title: approved ? `${reviewerName} 已审核通过` : `${reviewerName} 已退回本次修改`,
      detail: reviewDetails || undefined,
      time: r.reviewTime,
      type: approved ? 'success' : 'danger',
      badge: approved ? '学校审核' : '待重新处理',
    })
  }

  return logs.reverse()
})

/* ---------- 工具函数 ---------- */
function isFullWidth(key: string) {
  return fullWidthFields.has(key)
}

function getFieldValue(key: string) {
  return profile.value?.[key] ?? ''
}

function hasFieldChange(key: string) {
  const value = changeMap[key]
  return value !== '' && value !== undefined && value !== null
}

function genderLabel(v: any) {
  if (v === '0' || v === 0) return '男'
  if (v === '1' || v === 1) return '女'
  return v || '--'
}

function getFieldDisplay(key: string) {
  const val = getFieldValue(key)
  if (key === 'gender') return genderLabel(val)
  return val
}

function getPendingDisplay(key: string) {
  const val = changeMap[key]
  if (!hasFieldChange(key)) return '--'
  if (key === 'gender') return genderLabel(val)
  return String(val)
}

function buildChangeSummary(changes: any[] = []) {
  const labels = changes
    .map((item: any) => String(item?.fieldLabel || item?.fieldName || '').trim())
    .filter(Boolean)
  if (!labels.length) return ''
  const uniqueLabels = Array.from(new Set(labels))
  if (uniqueLabels.length <= 3) return uniqueLabels.join('、')
  return `${uniqueLabels.slice(0, 3).join('、')} 等 ${uniqueLabels.length} 项`
}

function statusLabel(status?: string) {
  const map: Record<string, string> = { PENDING: '待核对', CONFIRMED: '已确认', MODIFIED: '已修改', UNDER_REVIEW: '待审核', APPROVED: '已通过', REJECTED: '已驳回' }
  return map[status || ''] || status || '-'
}

function statusTagType(status?: string) {
  const map: Record<string, string> = { PENDING: 'info', CONFIRMED: '', UNDER_REVIEW: 'warning', APPROVED: 'success', REJECTED: 'danger' }
  return map[status || ''] || 'info'
}

function changeLabel(status?: string) {
  return status === 'APPROVED' ? '已通过' : status === 'REJECTED' ? '已驳回' : '待审核'
}

function changeTagType(status?: string) {
  return status === 'APPROVED' ? 'success' : status === 'REJECTED' ? 'danger' : 'warning'
}

function resolveTimeValue(value?: string) {
  if (!value) return Number.POSITIVE_INFINITY
  const normalized = String(value).replace(/-/g, '/')
  const time = new Date(normalized).getTime()
  return Number.isFinite(time) ? time : Number.POSITIVE_INFINITY
}

function deadlineMeta(batchInfo?: any) {
  const time = resolveTimeValue(batchInfo?.endTime)
  if (!Number.isFinite(time) || time === Number.POSITIVE_INFINITY) return { label: '未设置截止时间' }
  const diff = time - Date.now()
  const dayMs = 24 * 60 * 60 * 1000
  const days = Math.ceil(diff / dayMs)
  if (days < 0) return { label: '当前批次已截止' }
  if (days === 0) return { label: '今天截止，请尽快完成核对' }
  if (days <= 3) return { label: `${days} 天后截止，建议优先处理` }
  return { label: `${days} 天后截止` }
}

function handleAvatarUploadSuccess(res: any) {
  if (res.code === 200 || res.fileName) {
    changeMap['avatarUrl'] = res.fileName || res.url || res.data
    ElMessage.success('照片上传成功')
  } else {
    ElMessage.error(res.msg || '上传失败')
  }
}

function resetField(fieldName: string) {
  changeMap[fieldName] = ''
}

function resetAllChanges() {
  Object.keys(changeMap).forEach((key) => {
    changeMap[key] = ''
  })
}

/* ---------- 操作 ---------- */
async function handleConfirm() {
  if (!record.value?.recordId) return
  await ElMessageBox.confirm('确认您的学籍信息无需修改？', '提示', { type: 'info' })
  submitting.value = true
  try {
    await confirmVerificationNoChange(record.value.recordId)
    ElMessage.success('已确认')
    await loadData()
  } finally {
    submitting.value = false
  }
}

async function handleSubmit() {
  if (!record.value?.recordId) return
  const changes: { fieldName: string; newValue: string }[] = []
  for (const [fieldName, newValue] of Object.entries(changeMap)) {
    if (newValue !== '' && newValue !== undefined && newValue !== null) {
      changes.push({ fieldName, newValue })
    }
  }
  if (!changes.length) {
    ElMessage.warning('请至少修改一个字段')
    return
  }
  await ElMessageBox.confirm(`确认提交 ${changes.length} 项修改？提交后需等待管理员审核。`, '提示', { type: 'warning' })
  submitting.value = true
  try {
    await submitVerificationChanges({
      recordId: record.value.recordId,
      batchId,
      changes,
    })
    ElMessage.success('提交成功，请等待审核')
    Object.keys(changeMap).forEach(k => { changeMap[k] = '' })
    await loadData()
  } finally {
    submitting.value = false
  }
}

/* ---------- 数据加载 ---------- */
async function loadData() {
  loading.value = true
  try {
    const res = await getVerificationInfo(batchId)
    const data = res.data ?? res
    record.value = data
    profile.value = res.profile || data.profile || {}
    batch.value = res.batch || data.batch || {}
  } catch {
    ElMessage.error('加载失败')
    router.push('/student/verification')
  } finally {
    loading.value = false
  }
}

onMounted(() => loadData())
</script>

<style scoped>
/* ===== 页面容器 ===== */
.vd-page {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* ===== 顶部导航 ===== */
.vd-topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 20px;
  background: #fff;
  border-radius: 12px;
  border: 1px solid #e5eef8;
}

.vd-topbar__left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.vd-topbar__title h1 {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: #0f172a;
}

.vd-topbar__title p {
  margin: 4px 0 0;
  color: #64748b;
  font-size: 13px;
}

.vd-topbar__right {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.vd-topbar__deadline {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  min-height: 34px;
  padding: 0 12px;
  border-radius: 999px;
  background: #eff6ff;
  color: #2563eb;
  font-size: 12px;
  font-weight: 700;
}

/* ===== 提示 ===== */
.vd-alert {
  border-radius: 10px;
}

/* ===== 批次横幅 ===== */
.vd-batch-banner {
  background: linear-gradient(135deg, #eff6ff, #f0f9ff);
  border: 1px solid #bfdbfe;
  border-radius: 10px;
  padding: 14px 18px;
}

.vd-batch-banner__inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
}

.vd-batch-banner__desc {
  margin: 0;
  color: #1e40af;
  font-size: 14px;
}

.vd-batch-banner__time {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: #3b82f6;
  font-size: 13px;
  white-space: nowrap;
}

.vd-summary-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
}

.vd-summary-card {
  padding: 16px 18px;
  border-radius: 12px;
  background: #fff;
  border: 1px solid #e5eef8;
}

.vd-summary-card span {
  display: block;
  color: #64748b;
  font-size: 12px;
  font-weight: 700;
}

.vd-summary-card strong {
  display: block;
  margin-top: 10px;
  color: #0f172a;
  font-size: 22px;
  line-height: 1.25;
  font-weight: 800;
}

.vd-summary-card p {
  margin: 10px 0 0;
  color: #94a3b8;
  font-size: 12px;
  line-height: 1.55;
}

.vd-summary-card.is-accent strong { color: #2563eb; }
.vd-summary-card.is-success strong { color: #059669; }
.vd-summary-card.is-warning strong { color: #d97706; }

/* ===== 通用 Section ===== */
.vd-section {
  background: #fff;
  border: 1px solid #e5eef8;
  border-radius: 12px;
  overflow: hidden;
}

.vd-section__header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 20px;
  background: #f8fafc;
  border-bottom: 1px solid #e5eef8;
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
}

.vd-section__header i {
  font-size: 17px;
  color: #3b82f6;
}

.vd-section__sub {
  margin-left: auto;
  font-size: 12px;
  font-weight: 400;
  color: #94a3b8;
}

.vd-section__empty {
  padding: 40px 20px;
  text-align: center;
  color: #94a3b8;
  font-size: 14px;
}

.vd-change-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 14px 20px;
  background: #f8fafc;
  border-bottom: 1px solid #eef2f7;
}

.vd-change-banner__copy strong {
  display: block;
  color: #0f172a;
  font-size: 14px;
}

.vd-change-banner__copy p {
  margin: 6px 0 0;
  color: #64748b;
  font-size: 12px;
  line-height: 1.55;
}

.vd-change-banner__chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 10px;
}

.vd-change-banner__chips span {
  display: inline-flex;
  align-items: center;
  min-height: 26px;
  padding: 0 10px;
  border-radius: 999px;
  background: #fff;
  border: 1px solid #dbeafe;
  color: #1d4ed8;
  font-size: 12px;
  font-weight: 700;
}

.vd-change-banner__chips span.is-muted {
  border-color: #e2e8f0;
  color: #64748b;
}

/* ===== 基本信息网格 ===== */
.vd-info-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 0;
}

.vd-info-cell {
  padding: 12px 20px;
  border-bottom: 1px solid #f1f5f9;
  border-right: 1px solid #f1f5f9;
}

.vd-info-cell:nth-child(3n) {
  border-right: none;
}

.vd-info-cell__label {
  display: block;
  font-size: 12px;
  color: #94a3b8;
  margin-bottom: 4px;
}

.vd-info-cell strong {
  font-size: 14px;
  font-weight: 500;
  color: #0f172a;
}

.vd-info-photo {
  width: 72px;
  height: 96px;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

/* ===== 编辑分组 ===== */
.vd-edit-group {
  border-bottom: 1px solid #f1f5f9;
}

.vd-edit-group:last-child {
  border-bottom: none;
}

.vd-edit-group__title {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 14px;
  padding: 12px 20px;
  background: #fafbfc;
  border-bottom: 1px solid #f1f5f9;
}

.vd-edit-group__title-main strong {
  display: block;
  font-size: 13px;
  font-weight: 700;
  color: #334155;
}

.vd-edit-group__title-main p {
  margin: 4px 0 0;
  font-size: 12px;
  line-height: 1.5;
  color: #94a3b8;
}

.vd-edit-group__title span {
  display: inline-flex;
  align-items: center;
  min-height: 26px;
  padding: 0 10px;
  border-radius: 999px;
  background: #fff;
  color: #64748b;
  font-size: 12px;
  font-weight: 700;
  border: 1px solid #e2e8f0;
}

.vd-edit-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 0;
}

.vd-edit-field {
  padding: 14px 20px;
  border-bottom: 1px solid #f5f5f5;
  border-right: 1px solid #f5f5f5;
}

.vd-edit-field:nth-child(2n) {
  border-right: none;
}

.vd-edit-field.is-full-width {
  grid-column: 1 / -1;
  border-right: none;
}

.vd-edit-field.is-changed {
  background: #f0fdf4;
}

.vd-edit-field__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
  gap: 12px;
}

.vd-edit-field__title {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.vd-edit-field__label {
  font-size: 13px;
  font-weight: 600;
  color: #334155;
}

.vd-edit-field__changed-badge {
  display: inline-flex;
  align-items: center;
  min-height: 22px;
  padding: 0 8px;
  border-radius: 999px;
  background: #dcfce7;
  color: #166534;
  font-size: 11px;
  font-weight: 700;
}

.vd-edit-field__header-right {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.vd-edit-field__current {
  font-size: 12px;
  color: #94a3b8;
}

.vd-edit-field__reset {
  border: none;
  background: transparent;
  color: #2563eb;
  cursor: pointer;
  font-size: 12px;
  font-weight: 700;
  padding: 0;
}

.vd-edit-field__reset:hover {
  color: #1d4ed8;
}

.vd-edit-field__readonly {
  font-size: 14px;
  color: #0f172a;
}

.vd-edit-field__pending {
  margin-top: 10px;
  display: flex;
  align-items: baseline;
  gap: 6px;
  padding: 8px 10px;
  border-radius: 10px;
  background: #ffffff;
  border: 1px dashed #86efac;
}

.vd-edit-field__pending span {
  color: #64748b;
  font-size: 12px;
}

.vd-edit-field__pending strong {
  color: #166534;
  font-size: 13px;
  font-weight: 700;
  word-break: break-word;
}

/* ===== 头像编辑 ===== */
.vd-avatar-edit {
  display: flex;
  align-items: center;
  gap: 14px;
}

.vd-avatar-edit__preview {
  width: 64px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e2e8f0;
  flex-shrink: 0;
}

.vd-avatar-edit__preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.vd-avatar-edit__ph {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f1f5f9;
  color: #94a3b8;
  font-size: 24px;
}

/* ===== 操作按钮 ===== */
.vd-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  padding: 20px;
  border-top: 1px solid #f1f5f9;
  flex-wrap: wrap;
}

/* ===== 审核日志 ===== */
.vd-log-block {
  padding: 16px 20px;
}

.vd-log-block__title {
  margin: 0 0 12px;
  font-size: 14px;
  font-weight: 600;
  color: #334155;
}

.vd-log-table {
  border-radius: 8px;
  overflow: hidden;
}

.change-img {
  max-width: 50px;
  max-height: 50px;
  border-radius: 4px;
}

/* ===== 时间线 ===== */
.vd-timeline {
  padding: 16px 20px;
}

.vd-timeline__item {
  display: flex;
  gap: 14px;
  position: relative;
  padding-bottom: 20px;
  padding-left: 4px;
}

.vd-timeline__item:not(:last-child)::before {
  content: '';
  position: absolute;
  left: 9px;
  top: 18px;
  bottom: 0;
  width: 2px;
  background: #e5e7eb;
}

.vd-timeline__item:last-child {
  padding-bottom: 0;
}

.vd-timeline__dot {
  width: 14px;
  height: 14px;
  border-radius: 50%;
  flex-shrink: 0;
  margin-top: 3px;
  border: 3px solid #d1d5db;
  background: #fff;
}

.vd-timeline__item.is-primary .vd-timeline__dot { border-color: #3b82f6; }
.vd-timeline__item.is-success .vd-timeline__dot { border-color: #10b981; }
.vd-timeline__item.is-danger .vd-timeline__dot { border-color: #ef4444; }
.vd-timeline__item.is-info .vd-timeline__dot { border-color: #94a3b8; }

.vd-timeline__content {
  flex: 1;
  min-width: 0;
}

.vd-timeline__title-row {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.vd-timeline__title {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
  line-height: 1.4;
}

.vd-timeline__badge {
  display: inline-flex;
  align-items: center;
  min-height: 24px;
  padding: 0 8px;
  border-radius: 999px;
  background: #f8fafc;
  color: #64748b;
  font-size: 11px;
  font-weight: 700;
}

.vd-timeline__detail {
  font-size: 13px;
  color: #64748b;
  margin-top: 4px;
  line-height: 1.5;
}

.vd-timeline__time {
  font-size: 12px;
  color: #94a3b8;
  margin-top: 4px;
}

/* ===== Loading ===== */
.loading-center {
  padding: 0;
}

.vd-skeleton {
  display: grid;
  gap: 16px;
}

.vd-skeleton__topbar,
.vd-skeleton__banner,
.vd-skeleton__summary,
.vd-skeleton__section {
  border-radius: 12px;
  background: linear-gradient(90deg, #eef4f8 0%, #f8fbfd 50%, #eef4f8 100%);
  background-size: 200% 100%;
  animation: verification-detail-skeleton 1.4s ease-in-out infinite;
}

.vd-skeleton__topbar { height: 72px; }
.vd-skeleton__banner { height: 72px; }
.vd-skeleton__summary { height: 120px; }
.vd-skeleton__section { height: 420px; }

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .vd-page {
    padding: 12px;
  }

  .vd-summary-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .vd-topbar,
  .vd-change-banner {
    flex-direction: column;
    align-items: flex-start;
  }

  .vd-edit-group__title {
    flex-direction: column;
    align-items: flex-start;
  }

  .vd-info-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .vd-info-cell:nth-child(3n) {
    border-right: 1px solid #f1f5f9;
  }

  .vd-info-cell:nth-child(2n) {
    border-right: none;
  }

  .vd-edit-grid {
    grid-template-columns: 1fr;
  }

  .vd-edit-field {
    border-right: none;
  }
}

@media (max-width: 480px) {
  .vd-summary-grid,
  .vd-info-grid {
    grid-template-columns: 1fr;
  }

  .vd-info-cell {
    border-right: none;
  }

  .vd-actions {
    flex-direction: column;
  }
}

@keyframes verification-detail-skeleton {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}
</style>
