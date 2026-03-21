<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16">
      <el-form-item label="用户">
        <el-select v-model="queryParams.userId" filterable clearable style="width:280px">
          <el-option v-for="item in userOptions" :key="item.value" :label="item.label" :value="item.value">
            <div class="user-option">
              <span class="user-option__name">{{ item.shortLabel || item.label }}</span>
              <el-tag size="small" effect="plain" type="info">{{ item.userTypeLabel || '未分类' }}</el-tag>
            </div>
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="课程"><el-select v-model="queryParams.courseId" filterable clearable style="width:240px"><el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item><el-button type="primary" icon="Search" @click="getList">搜索</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb16">
      <el-col :span="1.5"><el-button type="warning" plain icon="Refresh" @click="handleRebuild">重建画像</el-button></el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>
    <el-table v-loading="loading" :data="profileList">
      <el-table-column label="画像ID" prop="profileId" width="90" />
      <el-table-column label="学生" min-width="180">
        <template #default="scope">
          {{ userLabel(scope.row.userId) }}
        </template>
      </el-table-column>
      <el-table-column label="课程" min-width="180">
        <template #default="scope">
          <span v-if="scope.row.courseId">{{ courseLabel(scope.row.courseId) }}</span>
          <el-tag v-else type="danger" effect="plain">历史异常数据</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="能力等级" prop="abilityLevel" width="110" />
      <el-table-column label="活跃度" prop="activeScore" width="100" />
      <el-table-column label="专注度" prop="concentrationScore" width="100" />
      <el-table-column label="掌握度" prop="masteryScore" width="100" />
      <el-table-column label="兴趣度" prop="interestScore" width="100" />
      <el-table-column label="风险分" prop="riskScore" width="100" />
      <el-table-column label="更新时间" prop="lastCalculatedTime" min-width="180" />
      <el-table-column label="操作" width="140" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="openDiagnosis(scope.row)">AI解读</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-drawer v-model="diagnosisVisible" title="AI画像解读" size="46%">
      <div v-loading="diagnosisLoading">
        <template v-if="diagnosis?.profile">
          <el-card shadow="never">
            <div class="ai-title">{{ diagnosis.profile.portraitTitle || '学习画像解读' }}</div>
            <div class="ai-summary">{{ diagnosis.profile.aiSummary || diagnosis.overallSummary }}</div>
            <div class="ai-meta">学习模式：{{ diagnosis.profile.learningPattern || '--' }} · 可信度：{{ diagnosis.profile.aiConfidence || '--' }}</div>
          </el-card>
          <el-row :gutter="16" class="mt16">
            <el-col :span="12">
              <el-card shadow="never">
                <template #header><span>优势特征</span></template>
                <ul class="simple-list">
                  <li v-for="item in diagnosis.profile.strengths || []" :key="item">{{ item }}</li>
                </ul>
              </el-card>
            </el-col>
            <el-col :span="12">
              <el-card shadow="never">
                <template #header><span>风险短板</span></template>
                <ul class="simple-list">
                  <li v-for="item in diagnosis.profile.risks || []" :key="item">{{ item }}</li>
                </ul>
              </el-card>
            </el-col>
          </el-row>
          <el-card shadow="never" class="mt16">
            <template #header><span>行动建议</span></template>
            <ul class="simple-list">
              <li v-for="item in diagnosis.profile.aiSuggestions || diagnosis.actionSuggestions || []" :key="item">{{ item }}</li>
            </ul>
          </el-card>
        </template>
        <el-empty v-else description="暂无AI画像解读" />
      </div>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { listLearningProfile, rebuildLearningProfile } from '@/api/campus/learning'
import { getLearningDiagnosis } from '@/api/campus/analysis'
import { fetchCourseOptions, fetchUserOptions } from '@/api/campus/options'
const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const profileList = ref<any[]>([])
const diagnosisVisible = ref(false)
const diagnosisLoading = ref(false)
const diagnosis = ref<any>(null)
const userOptions = ref<any[]>([])
const courseOptions = ref<any[]>([])
const queryParams = reactive<any>({ pageNum: 1, pageSize: 10, userId: undefined, courseId: undefined })
function userLabel(userId: number | string) { return userOptions.value.find((item: any) => item.value === userId)?.label || `用户 ${userId}` }
function courseLabel(courseId: number | string) { return courseOptions.value.find((item: any) => item.value === courseId)?.label || `课程 ${courseId}` }
async function getList(){ loading.value = true; const res = await listLearningProfile(queryParams); profileList.value = (res.rows || []); total.value = res.total || 0; loading.value = false }
async function handleRebuild(){ if(!queryParams.userId || !queryParams.courseId){ ElMessage.warning('请先输入用户ID和课程ID'); return } await rebuildLearningProfile({ userId: queryParams.userId, courseId: queryParams.courseId }); ElMessage.success('画像重建成功'); getList() }
async function openDiagnosis(row: any){ if(!row?.userId || !row?.courseId){ ElMessage.warning('该画像缺少课程信息，暂无法进行AI解读'); return } diagnosisVisible.value = true; diagnosisLoading.value = true; try { const res = await getLearningDiagnosis({ userId: row.userId, courseId: row.courseId, recommendLimit: 5, autoGenerate: true }); diagnosis.value = res.data || null } finally { diagnosisLoading.value = false } }
async function loadOptions(){ userOptions.value = await fetchUserOptions(); courseOptions.value = await fetchCourseOptions() }
onMounted(async()=>{ await loadOptions(); getList() })
</script>

<style scoped>
.mb16{margin-bottom:16px;}
.mt16{margin-top:16px;}
.ai-title{font-size:18px;font-weight:700;color:var(--el-text-color-primary);}
.ai-summary{margin-top:10px;line-height:1.8;color:var(--el-text-color-regular);}
.ai-meta{margin-top:8px;font-size:12px;color:var(--el-color-primary);}
.simple-list{margin:0;padding-left:18px;line-height:1.9;}
.user-option{display:flex;align-items:center;justify-content:space-between;gap:12px;width:100%;}
.user-option__name{overflow:hidden;text-overflow:ellipsis;white-space:nowrap;color:var(--el-text-color-primary);}
</style>
