<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-form :model="queryParams" :inline="true" v-show="showSearch" class="mb16">
      <el-form-item label="批次名称">
        <el-input v-model="queryParams.batchName" placeholder="请输入批次名称" clearable @keyup.enter="getList" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.batchStatus" clearable placeholder="请选择状态" style="width: 180px">
          <el-option label="草稿" value="DRAFT" />
          <el-option label="进行中" value="ACTIVE" />
          <el-option label="已关闭" value="CLOSED" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="getList">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 概览卡片 -->
    <div class="overview-grid mb16">
      <div class="overview-card">
        <span>全部批次</span>
        <strong>{{ total }}</strong>
      </div>
      <div class="overview-card is-warning">
        <span>草稿</span>
        <strong>{{ countByStatus('DRAFT') }}</strong>
      </div>
      <div class="overview-card is-success">
        <span>进行中</span>
        <strong>{{ countByStatus('ACTIVE') }}</strong>
      </div>
      <div class="overview-card is-info">
        <span>已关闭</span>
        <strong>{{ countByStatus('CLOSED') }}</strong>
      </div>
    </div>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb16">
      <el-col :span="1.5">
        <el-button v-hasPermi="['campus:verification:add']" type="primary" plain icon="Plus" @click="openBatchDialog()">新增批次</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <!-- 批次列表 -->
    <el-table v-loading="loading" :data="batchList">
      <el-table-column prop="batchNo" label="批次编号" min-width="160" />
      <el-table-column prop="batchName" label="批次名称" min-width="200" />
      <el-table-column label="范围" min-width="140">
        <template #default="{ row }">
          {{ scopeLabel(row) }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="batchStatusTagType(row.batchStatus)">{{ batchStatusLabel(row.batchStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="核对进度" min-width="200">
        <template #default="{ row }">
          <div v-if="row.totalCount > 0" class="progress-cell">
            <el-progress :percentage="Math.round(((row.confirmedCount || 0) + (row.approvedCount || 0)) / row.totalCount * 100)" :stroke-width="8" />
            <span class="progress-text">{{ (row.confirmedCount || 0) + (row.approvedCount || 0) }}/{{ row.totalCount }}</span>
          </div>
          <span v-else class="text-muted">-</span>
        </template>
      </el-table-column>
      <el-table-column label="时间范围" min-width="220">
        <template #default="{ row }">
          <span v-if="row.startTime">{{ row.startTime }} ~ {{ row.endTime }}</span>
          <span v-else class="text-muted">未设置</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="viewRecords(row)">查看记录</el-button>
          <el-button v-if="row.batchStatus === 'DRAFT'" v-hasPermi="['campus:verification:edit']" link type="primary" @click="openBatchDialog(row)">编辑</el-button>
          <el-button v-if="row.batchStatus === 'DRAFT'" v-hasPermi="['campus:verification:edit']" link type="success" @click="handleActivate(row)">激活</el-button>
          <el-button v-if="row.batchStatus === 'ACTIVE'" v-hasPermi="['campus:verification:edit']" link type="warning" @click="handleClose(row)">关闭</el-button>
          <el-button v-if="row.batchStatus === 'DRAFT'" v-hasPermi="['campus:verification:remove']" link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/编辑批次弹窗 -->
    <el-dialog v-model="batchDialogOpen" :title="isEdit ? '编辑批次' : '新增批次'" width="700px" @closed="resetBatchForm">
      <el-form ref="batchFormRef" :model="batchForm" :rules="batchRules" label-width="100px">
        <el-form-item label="批次名称" prop="batchName">
          <el-input v-model="batchForm.batchName" placeholder="请输入批次名称" maxlength="128" show-word-limit />
        </el-form-item>
        <el-form-item label="批次说明" prop="description">
          <el-input v-model="batchForm.description" type="textarea" :rows="3" placeholder="请输入说明" maxlength="512" show-word-limit />
        </el-form-item>
        <el-form-item label="可编辑字段" prop="editableFields">
          <el-checkbox-group v-model="batchForm.editableFields" class="field-checkbox-group">
            <div v-for="group in fieldGroups" :key="group.name" class="field-group">
              <div class="field-group-header">
                <span class="field-group-title">{{ group.name }}</span>
                <el-button link type="primary" size="small" @click="toggleGroup(group.name)">
                  {{ isGroupAllChecked(group.name) ? '取消全选' : '全选' }}
                </el-button>
              </div>
              <div class="field-group-items">
                <el-checkbox v-for="f in group.fields" :key="f.value" :value="f.value">{{ f.label }}</el-checkbox>
              </div>
            </div>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="范围类型" prop="scopeType">
          <el-radio-group v-model="batchForm.scopeType">
            <el-radio value="ALL">全部学生</el-radio>
            <el-radio value="GRADE">按年级</el-radio>
            <el-radio value="CLASS">按班级</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="batchForm.scopeType === 'GRADE'" label="选择年级" prop="scopeGradeId">
          <el-select v-model="batchForm.scopeGradeId" placeholder="请选择年级" filterable clearable style="width: 100%">
            <el-option v-for="g in gradeOptions" :key="g.gradeId" :label="g.gradeName" :value="g.gradeId" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="batchForm.scopeType === 'CLASS'" label="选择班级" prop="scopeClassId">
          <el-select v-model="batchForm.scopeClassId" placeholder="请选择班级" filterable clearable style="width: 100%">
            <el-option v-for="c in classOptions" :key="c.classId" :label="c.className" :value="c.classId" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker v-model="batchForm.startTime" type="datetime" placeholder="选择开始时间" style="width: 100%" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker v-model="batchForm.endTime" type="datetime" placeholder="选择结束时间" style="width: 100%" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="batchDialogOpen = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitBatchForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 学生记录弹窗 -->
    <el-dialog v-model="recordDialogOpen" :title="'学生记录 - ' + (currentBatch?.batchName || '')" width="1100px" top="5vh">
      <!-- 统计卡片 -->
      <div class="overview-grid mb16" v-if="currentBatch">
        <div class="overview-card"><span>待核对</span><strong>{{ currentBatch.totalCount - (currentBatch.confirmedCount||0) - (currentBatch.pendingReviewCount||0) - (currentBatch.approvedCount||0) }}</strong></div>
        <div class="overview-card is-success"><span>已确认</span><strong>{{ currentBatch.confirmedCount || 0 }}</strong></div>
        <div class="overview-card is-warning"><span>待审核</span><strong>{{ currentBatch.pendingReviewCount || 0 }}</strong></div>
        <div class="overview-card is-info"><span>已通过</span><strong>{{ currentBatch.approvedCount || 0 }}</strong></div>
      </div>

      <!-- 记录搜索 -->
      <el-form :model="recordQueryParams" :inline="true" class="mb16">
        <el-form-item label="学生姓名">
          <el-input v-model="recordQueryParams.studentName" placeholder="请输入姓名" clearable @keyup.enter="getRecordList" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="recordQueryParams.recordStatus" clearable placeholder="请选择状态" style="width: 150px">
            <el-option label="待核对" value="PENDING" />
            <el-option label="已确认" value="CONFIRMED" />
            <el-option label="待审核" value="UNDER_REVIEW" />
            <el-option label="已通过" value="APPROVED" />
            <el-option label="已驳回" value="REJECTED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="getRecordList">搜索</el-button>
        </el-form-item>
      </el-form>

      <!-- 批量操作 -->
      <el-row :gutter="10" class="mb16">
        <el-col :span="1.5">
          <el-button v-hasPermi="['campus:verification:review']" type="success" plain :disabled="!selectedRecordIds.length" @click="openBatchRecordReview('APPROVE')">
            批量通过 ({{ selectedRecordIds.length }})
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button v-hasPermi="['campus:verification:review']" type="danger" plain :disabled="!selectedRecordIds.length" @click="openBatchRecordReview('REJECT')">
            批量驳回 ({{ selectedRecordIds.length }})
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button v-hasPermi="['campus:verification:edit']" type="warning" plain @click="openBatchModifyDialog">批量修改</el-button>
        </el-col>
      </el-row>

      <!-- 记录表格 -->
      <el-table v-loading="recordLoading" :data="recordList" @selection-change="handleRecordSelectionChange">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="studentNo" label="学号" min-width="130" />
        <el-table-column prop="studentName" label="姓名" min-width="100" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="recordStatusTagType(row.recordStatus)">{{ recordStatusLabel(row.recordStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="submitTime" label="提交时间" width="170" />
        <el-table-column prop="reviewUserName" label="审核人" width="100" />
        <el-table-column prop="reviewTime" label="审核时间" width="170" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="viewRecordDetail(row)">详情</el-button>
            <el-button v-if="row.recordStatus === 'UNDER_REVIEW'" v-hasPermi="['campus:verification:review']" link type="success" @click="openRecordReviewDialog(row)">审核</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="recordTotal > 0" :total="recordTotal" v-model:page="recordQueryParams.pageNum" v-model:limit="recordQueryParams.pageSize" @pagination="getRecordList" />
    </el-dialog>

    <!-- 记录详情弹窗 -->
    <el-dialog v-model="recordDetailOpen" title="核对记录详情" width="750px">
      <div v-if="detailRecord" class="detail-layout">
        <div class="detail-hero">
          <div>
            <h3>{{ detailRecord.studentName }} ({{ detailRecord.studentNo }})</h3>
            <p>记录状态：{{ recordStatusLabel(detailRecord.recordStatus) }}</p>
          </div>
          <el-tag :type="recordStatusTagType(detailRecord.recordStatus)" size="large">{{ recordStatusLabel(detailRecord.recordStatus) }}</el-tag>
        </div>
        <div class="detail-card" v-if="detailRecord.changes && detailRecord.changes.length">
          <h4>变更明细</h4>
          <el-table :data="detailRecord.changes" border stripe>
            <el-table-column prop="fieldLabel" label="字段" width="120" />
            <el-table-column label="原值" min-width="180">
              <template #default="{ row }">
                <img v-if="row.fieldName === 'avatarUrl' && row.oldValue" :src="row.oldValue" class="change-img" />
                <span v-else>{{ row.oldValue || '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column label="新值" min-width="180">
              <template #default="{ row }">
                <img v-if="row.fieldName === 'avatarUrl' && row.newValue" :src="row.newValue" class="change-img" />
                <span v-else>{{ row.newValue || '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="changeStatus" label="状态" width="90">
              <template #default="{ row }">
                <el-tag :type="changeStatusTagType(row.changeStatus)" size="small">{{ changeStatusLabel(row.changeStatus) }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="detail-card" v-else>
          <p>学生确认信息无需修改</p>
        </div>
        <div class="detail-card" v-if="detailRecord.reviewComment">
          <h4>审核意见</h4>
          <p>{{ detailRecord.reviewComment }}</p>
        </div>
      </div>
    </el-dialog>

    <!-- 单条审核弹窗 -->
    <el-dialog v-model="reviewDialogOpen" title="审核学生变更" width="620px">
      <div v-if="reviewRow" class="review-panel">
        <div class="review-panel__summary">
          <strong>{{ reviewRow.studentName }} ({{ reviewRow.studentNo }})</strong>
        </div>
        <div class="detail-card mb16" v-if="reviewRow.changes && reviewRow.changes.length">
          <el-table :data="reviewRow.changes" border stripe size="small">
            <el-table-column prop="fieldLabel" label="字段" width="100" />
            <el-table-column label="原值" min-width="150">
              <template #default="{ row }">
                <img v-if="row.fieldName === 'avatarUrl' && row.oldValue" :src="row.oldValue" class="change-img-sm" />
                <span v-else>{{ row.oldValue || '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column label="新值" min-width="150">
              <template #default="{ row }">
                <img v-if="row.fieldName === 'avatarUrl' && row.newValue" :src="row.newValue" class="change-img-sm" />
                <span v-else>{{ row.newValue || '-' }}</span>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <el-form label-width="90px">
          <el-form-item label="审核结论">
            <el-radio-group v-model="reviewForm.actionType">
              <el-radio value="APPROVE">通过</el-radio>
              <el-radio value="REJECT">驳回</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="审核意见">
            <el-input v-model="reviewForm.reviewComment" type="textarea" :rows="3" maxlength="300" show-word-limit placeholder="请输入审核意见（可选）" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="reviewDialogOpen = false">取消</el-button>
        <el-button type="primary" :loading="reviewSubmitting" @click="submitRecordReview">提交审核</el-button>
      </template>
    </el-dialog>

    <!-- 批量审核弹窗 -->
    <el-dialog v-model="batchReviewOpen" title="批量审核" width="520px">
      <p>将对 <strong>{{ selectedRecordIds.length }}</strong> 条记录执行操作</p>
      <el-form label-width="90px">
        <el-form-item label="审核结论">
          <el-radio-group v-model="batchReviewForm.actionType">
            <el-radio value="APPROVE">通过</el-radio>
            <el-radio value="REJECT">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见">
          <el-input v-model="batchReviewForm.reviewComment" type="textarea" :rows="3" maxlength="300" show-word-limit placeholder="批量审核意见（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="batchReviewOpen = false">取消</el-button>
        <el-button type="primary" :loading="reviewSubmitting" @click="submitBatchRecordReview">确认</el-button>
      </template>
    </el-dialog>

    <!-- 批量修改弹窗 -->
    <el-dialog v-model="batchModifyOpen" title="管理员批量修改" width="600px">
      <el-form ref="modifyFormRef" :model="modifyForm" :rules="modifyRules" label-width="100px">
        <el-form-item label="修改字段" prop="fieldName">
          <el-select v-model="modifyForm.fieldName" placeholder="请选择要修改的字段" style="width: 100%">
            <el-option-group v-for="group in fieldGroups" :key="group.name" :label="group.name">
              <el-option v-for="f in group.fields" :key="f.value" :label="f.label" :value="f.value" />
            </el-option-group>
          </el-select>
        </el-form-item>
        <el-form-item label="新值" prop="newValue">
          <el-input v-model="modifyForm.newValue" placeholder="请输入新值" />
        </el-form-item>
        <el-form-item label="目标学生" prop="studentUserIds">
          <el-select v-model="modifyForm.studentUserIds" multiple filterable placeholder="请选择学生" style="width: 100%">
            <el-option v-for="r in recordList" :key="r.studentUserId" :label="`${r.studentName} (${r.studentNo})`" :value="r.studentUserId" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="batchModifyOpen = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitBatchModify">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElForm, ElMessage, ElMessageBox } from 'element-plus'
import {
  listVerificationBatch, getVerificationBatch, addVerificationBatch, updateVerificationBatch,
  activateVerificationBatch, closeVerificationBatch, removeVerificationBatch,
  listVerificationRecord, getVerificationRecord, reviewVerificationRecord,
  batchReviewVerificationRecord, batchModifyVerificationRecord
} from '@/api/campus/verification'

/* ---------- 字段配置（与学籍档案 sc_user_profile 统一） ---------- */
const allFieldOptions = [
  // 基本信息
  { value: 'realName', label: '姓名', group: '基本信息' },
  { value: 'gender', label: '性别', group: '基本信息' },
  { value: 'avatarUrl', label: '照片', group: '基本信息' },
  { value: 'formerName', label: '曾用名', group: '基本信息' },
  { value: 'nation', label: '民族', group: '基本信息' },
  { value: 'birthDate', label: '出生日期', group: '基本信息' },
  { value: 'politicalStatus', label: '政治面貌', group: '基本信息' },
  { value: 'nativePlace', label: '籍贯', group: '基本信息' },
  { value: 'idType', label: '证件类型', group: '基本信息' },
  { value: 'idNumber', label: '证件号码', group: '基本信息' },
  // 学籍信息
  { value: 'studentNo', label: '学号', group: '学籍信息' },
  { value: 'major', label: '专业', group: '学籍信息' },
  { value: 'admissionYear', label: '入学年份', group: '学籍信息' },
  { value: 'cultivationType', label: '培养方式', group: '学籍信息' },
  { value: 'educationLevel', label: '学历层次', group: '学籍信息' },
  { value: 'schoolingLength', label: '学制', group: '学籍信息' },
  // 录取信息
  { value: 'admissionType', label: '录取类型', group: '录取信息' },
  { value: 'examNumber', label: '考生号', group: '录取信息' },
  { value: 'admissionTicketNo', label: '准考证号', group: '录取信息' },
  { value: 'admissionScore', label: '录取成绩', group: '录取信息' },
  { value: 'admissionBatch', label: '录取批次', group: '录取信息' },
  { value: 'sourceRegion', label: '生源地', group: '录取信息' },
  { value: 'graduateSchool', label: '毕业学校', group: '录取信息' },
  // 毕业信息
  { value: 'expectedGraduationDate', label: '预计毕业日期', group: '毕业信息' },
  { value: 'actualGraduationDate', label: '实际毕业日期', group: '毕业信息' },
  { value: 'diplomaNo', label: '毕业证书号', group: '毕业信息' },
  { value: 'completionType', label: '结业类型', group: '毕业信息' },
  { value: 'graduationDestination', label: '毕业去向', group: '毕业信息' },
  { value: 'graduationRemark', label: '毕业备注', group: '毕业信息' },
  // 学位信息
  { value: 'degreeType', label: '学位类型', group: '学位信息' },
  { value: 'degreeAwardDate', label: '学位授予日期', group: '学位信息' },
  { value: 'degreeCertNo', label: '学位证书号', group: '学位信息' },
  { value: 'thesisTitle', label: '论文题目', group: '学位信息' },
  { value: 'degreeRemark', label: '学位备注', group: '学位信息' },
  // 联系信息
  { value: 'contactAddress', label: '通讯地址', group: '联系信息' },
  { value: 'homeAddress', label: '家庭地址', group: '联系信息' },
  { value: 'emergencyContact', label: '紧急联系人', group: '联系信息' },
  { value: 'emergencyPhone', label: '紧急联系电话', group: '联系信息' },
  { value: 'householdAddress', label: '户籍地址', group: '联系信息' },
  { value: 'bankCardNo', label: '银行卡号', group: '联系信息' },
]

/* ---------- 字段分组（用于批次创建表单） ---------- */
const fieldGroups = computed(() => {
  const groupMap = new Map<string, typeof allFieldOptions>()
  for (const f of allFieldOptions) {
    const g = f.group || '其他'
    if (!groupMap.has(g)) groupMap.set(g, [])
    groupMap.get(g)!.push(f)
  }
  return Array.from(groupMap.entries()).map(([name, fields]) => ({ name, fields }))
})

function isGroupAllChecked(groupName: string) {
  const group = fieldGroups.value.find(g => g.name === groupName)
  if (!group) return false
  return group.fields.every(f => batchForm.editableFields.includes(f.value))
}

function toggleGroup(groupName: string) {
  const group = fieldGroups.value.find(g => g.name === groupName)
  if (!group) return
  if (isGroupAllChecked(groupName)) {
    const removeSet = new Set(group.fields.map(f => f.value))
    batchForm.editableFields = batchForm.editableFields.filter(v => !removeSet.has(v))
  } else {
    const existSet = new Set(batchForm.editableFields)
    for (const f of group.fields) {
      if (!existSet.has(f.value)) batchForm.editableFields.push(f.value)
    }
  }
}

/* ---------- 批次列表 ---------- */
const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const batchList = ref<any[]>([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  batchName: '',
  batchStatus: '',
})

function countByStatus(status: string) {
  return batchList.value.filter(b => b.batchStatus === status).length
}

function batchStatusLabel(status?: string) {
  return status === 'DRAFT' ? '草稿' : status === 'ACTIVE' ? '进行中' : status === 'CLOSED' ? '已关闭' : status || '-'
}

function batchStatusTagType(status?: string) {
  return status === 'DRAFT' ? 'info' : status === 'ACTIVE' ? 'success' : status === 'CLOSED' ? '' : 'info'
}

function scopeLabel(row: any) {
  return row.scopeType === 'ALL' ? '全部学生' : row.scopeType === 'GRADE' ? '按年级' : row.scopeType === 'CLASS' ? '按班级' : row.scopeType || '-'
}

function recordStatusLabel(status?: string) {
  const map: Record<string, string> = { PENDING: '待核对', CONFIRMED: '已确认', MODIFIED: '已修改', UNDER_REVIEW: '待审核', APPROVED: '已通过', REJECTED: '已驳回' }
  return map[status || ''] || status || '-'
}

function recordStatusTagType(status?: string) {
  const map: Record<string, string> = { PENDING: 'info', CONFIRMED: '', MODIFIED: 'warning', UNDER_REVIEW: 'warning', APPROVED: 'success', REJECTED: 'danger' }
  return map[status || ''] || 'info'
}

function changeStatusLabel(status?: string) {
  return status === 'PENDING' ? '待审核' : status === 'APPROVED' ? '已通过' : status === 'REJECTED' ? '已驳回' : status || '-'
}

function changeStatusTagType(status?: string) {
  return status === 'APPROVED' ? 'success' : status === 'REJECTED' ? 'danger' : 'warning'
}

async function getList() {
  loading.value = true
  try {
    const res = await listVerificationBatch(queryParams)
    batchList.value = res.rows || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  Object.assign(queryParams, { pageNum: 1, pageSize: 10, batchName: '', batchStatus: '' })
  getList()
}

/* ---------- 新增/编辑批次 ---------- */
const batchDialogOpen = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const batchFormRef = ref<InstanceType<typeof ElForm> | null>(null)
const gradeOptions = ref<any[]>([])
const classOptions = ref<any[]>([])

const batchForm = reactive({
  batchId: undefined as number | undefined,
  batchName: '',
  description: '',
  editableFields: [] as string[],
  scopeType: 'ALL',
  scopeGradeId: undefined as number | undefined,
  scopeClassId: undefined as number | undefined,
  startTime: '',
  endTime: '',
})

const batchRules = {
  batchName: [{ required: true, message: '请输入批次名称', trigger: 'blur' }],
  editableFields: [{ required: true, message: '请选择可编辑字段', trigger: 'change', type: 'array', min: 1 }],
  scopeType: [{ required: true, message: '请选择范围', trigger: 'change' }],
}

function openBatchDialog(row?: any) {
  isEdit.value = !!row
  if (row) {
    Object.assign(batchForm, {
      batchId: row.batchId,
      batchName: row.batchName,
      description: row.description || '',
      editableFields: row.editableFields ? JSON.parse(row.editableFields) : [],
      scopeType: row.scopeType || 'ALL',
      scopeGradeId: row.scopeGradeId,
      scopeClassId: row.scopeClassId,
      startTime: row.startTime || '',
      endTime: row.endTime || '',
    })
  } else {
    resetBatchForm()
  }
  batchDialogOpen.value = true
}

function resetBatchForm() {
  Object.assign(batchForm, {
    batchId: undefined,
    batchName: '',
    description: '',
    editableFields: [],
    scopeType: 'ALL',
    scopeGradeId: undefined,
    scopeClassId: undefined,
    startTime: '',
    endTime: '',
  })
  batchFormRef.value?.clearValidate()
}

async function submitBatchForm() {
  await batchFormRef.value?.validate()
  submitLoading.value = true
  try {
    const data = { ...batchForm, editableFields: batchForm.editableFields }
    if (isEdit.value) {
      await updateVerificationBatch(data)
      ElMessage.success('修改成功')
    } else {
      await addVerificationBatch(data)
      ElMessage.success('新增成功')
    }
    batchDialogOpen.value = false
    await getList()
  } finally {
    submitLoading.value = false
  }
}

async function handleActivate(row: any) {
  await ElMessageBox.confirm(`确认激活批次「${row.batchName}」？激活后将自动为范围内学生创建核对记录。`, '提示', { type: 'warning' })
  await activateVerificationBatch(row.batchId)
  ElMessage.success('激活成功')
  await getList()
}

async function handleClose(row: any) {
  await ElMessageBox.confirm(`确认关闭批次「${row.batchName}」？`, '提示', { type: 'warning' })
  await closeVerificationBatch(row.batchId)
  ElMessage.success('已关闭')
  await getList()
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm(`确认删除批次「${row.batchName}」？相关记录将被级联删除。`, '提示', { type: 'warning' })
  await removeVerificationBatch(row.batchId)
  ElMessage.success('删除成功')
  await getList()
}

/* ---------- 学生记录 ---------- */
const recordDialogOpen = ref(false)
const recordLoading = ref(false)
const recordTotal = ref(0)
const recordList = ref<any[]>([])
const currentBatch = ref<any>(null)
const selectedRecordIds = ref<number[]>([])

const recordQueryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  batchId: undefined as number | undefined,
  studentName: '',
  recordStatus: '',
})

async function viewRecords(row: any) {
  const res = await getVerificationBatch(row.batchId)
  currentBatch.value = res.data || res
  recordQueryParams.batchId = row.batchId
  recordQueryParams.pageNum = 1
  recordQueryParams.studentName = ''
  recordQueryParams.recordStatus = ''
  selectedRecordIds.value = []
  recordDialogOpen.value = true
  await getRecordList()
}

async function getRecordList() {
  recordLoading.value = true
  try {
    const res = await listVerificationRecord(recordQueryParams)
    recordList.value = res.rows || []
    recordTotal.value = res.total || 0
  } finally {
    recordLoading.value = false
  }
}

function handleRecordSelectionChange(selection: any[]) {
  selectedRecordIds.value = selection.filter(r => r.recordStatus === 'UNDER_REVIEW').map(r => r.recordId)
}

/* ---------- 记录详情 ---------- */
const recordDetailOpen = ref(false)
const detailRecord = ref<any>(null)

async function viewRecordDetail(row: any) {
  const res = await getVerificationRecord(row.recordId)
  detailRecord.value = res.data || res
  recordDetailOpen.value = true
}

/* ---------- 单条审核 ---------- */
const reviewDialogOpen = ref(false)
const reviewSubmitting = ref(false)
const reviewRow = ref<any>(null)

const reviewForm = reactive({
  recordId: undefined as number | undefined,
  actionType: 'APPROVE',
  reviewComment: '',
})

async function openRecordReviewDialog(row: any) {
  const res = await getVerificationRecord(row.recordId)
  reviewRow.value = res.data || res
  reviewForm.recordId = row.recordId
  reviewForm.actionType = 'APPROVE'
  reviewForm.reviewComment = ''
  reviewDialogOpen.value = true
}

async function submitRecordReview() {
  if (!reviewForm.recordId) return
  reviewSubmitting.value = true
  try {
    await reviewVerificationRecord(reviewForm)
    ElMessage.success(reviewForm.actionType === 'APPROVE' ? '审核通过' : '已驳回')
    reviewDialogOpen.value = false
    await refreshRecordAndBatch()
  } finally {
    reviewSubmitting.value = false
  }
}

/* ---------- 批量审核 ---------- */
const batchReviewOpen = ref(false)
const batchReviewForm = reactive({ actionType: 'APPROVE', reviewComment: '' })

function openBatchRecordReview(actionType: string) {
  batchReviewForm.actionType = actionType
  batchReviewForm.reviewComment = ''
  batchReviewOpen.value = true
}

async function submitBatchRecordReview() {
  if (!selectedRecordIds.value.length) return
  reviewSubmitting.value = true
  try {
    await batchReviewVerificationRecord({
      recordIds: selectedRecordIds.value,
      actionType: batchReviewForm.actionType,
      reviewComment: batchReviewForm.reviewComment,
    })
    ElMessage.success('批量审核完成')
    batchReviewOpen.value = false
    selectedRecordIds.value = []
    await refreshRecordAndBatch()
  } finally {
    reviewSubmitting.value = false
  }
}

/* ---------- 批量修改 ---------- */
const batchModifyOpen = ref(false)
const modifyFormRef = ref<InstanceType<typeof ElForm> | null>(null)

const modifyForm = reactive({
  batchId: undefined as number | undefined,
  fieldName: '',
  newValue: '',
  studentUserIds: [] as number[],
})

const modifyRules = {
  fieldName: [{ required: true, message: '请选择字段', trigger: 'change' }],
  newValue: [{ required: true, message: '请输入新值', trigger: 'blur' }],
  studentUserIds: [{ required: true, message: '请选择学生', trigger: 'change', type: 'array', min: 1 }],
}

function openBatchModifyDialog() {
  modifyForm.batchId = recordQueryParams.batchId
  modifyForm.fieldName = ''
  modifyForm.newValue = ''
  modifyForm.studentUserIds = []
  batchModifyOpen.value = true
}

async function submitBatchModify() {
  await modifyFormRef.value?.validate()
  submitLoading.value = true
  try {
    await batchModifyVerificationRecord(modifyForm)
    ElMessage.success('批量修改成功')
    batchModifyOpen.value = false
    await refreshRecordAndBatch()
  } finally {
    submitLoading.value = false
  }
}

/* ---------- 辅助 ---------- */
async function refreshRecordAndBatch() {
  await getRecordList()
  if (currentBatch.value?.batchId) {
    const res = await getVerificationBatch(currentBatch.value.batchId)
    currentBatch.value = res.data || res
  }
  await getList()
}

async function loadGradeAndClassOptions() {
  try {
    const { default: req } = await import('@/utils/request')
    const gradeRes = await req({ url: '/campus/grade/list', method: 'get', params: { pageNum: 1, pageSize: 200 } })
    gradeOptions.value = gradeRes.rows || []
    const classRes = await req({ url: '/campus/class/list', method: 'get', params: { pageNum: 1, pageSize: 500 } })
    classOptions.value = classRes.rows || []
  } catch { /* ignore */ }
}

onMounted(async () => {
  await loadGradeAndClassOptions()
  await getList()
})
</script>

<style scoped>
.mb16 { margin-bottom: 16px; }

/* 字段分组选择样式 */
.field-checkbox-group {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.field-group {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;
}

.field-group-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 14px;
  background: #f8fafc;
  border-bottom: 1px solid #ebeef5;
}

.field-group-title {
  font-size: 13px;
  font-weight: 600;
  color: #475569;
}

.field-group-items {
  display: flex;
  flex-wrap: wrap;
  gap: 6px 16px;
  padding: 10px 14px;
}

.overview-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.overview-card {
  padding: 16px;
  border-radius: 12px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
}

.overview-card span {
  display: block;
  color: #64748b;
  font-size: 12px;
}

.overview-card strong {
  display: block;
  margin-top: 8px;
  font-size: 24px;
  color: #0f172a;
}

.overview-card.is-warning strong { color: #d97706; }
.overview-card.is-success strong { color: #059669; }
.overview-card.is-danger strong { color: #dc2626; }
.overview-card.is-info strong { color: #6366f1; }

.text-muted { color: #94a3b8; }

.progress-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.progress-cell .el-progress { flex: 1; }
.progress-text { font-size: 12px; color: #64748b; white-space: nowrap; }

.detail-layout { display: grid; gap: 16px; }

.detail-hero {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px;
  border-radius: 14px;
  background: linear-gradient(135deg, #eff6ff, #f8fafc);
}

.detail-hero h3 { margin: 0 0 6px; }
.detail-hero p { margin: 0; color: #64748b; }

.detail-card {
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 16px;
  background: #fff;
}

.detail-card h4 { margin: 0 0 12px; }
.detail-card p { margin: 0 0 8px; line-height: 1.7; }

.change-img { max-width: 80px; max-height: 80px; border-radius: 6px; }
.change-img-sm { max-width: 50px; max-height: 50px; border-radius: 4px; }

.review-panel__summary {
  margin-bottom: 16px;
  padding: 14px;
  border-radius: 12px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.review-panel__summary span,
.review-panel__summary p {
  color: #667085;
  margin: 0;
}
</style>
