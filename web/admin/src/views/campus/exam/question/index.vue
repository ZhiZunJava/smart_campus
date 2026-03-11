<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16">
      <el-form-item label="课程ID"><el-input v-model="queryParams.courseId" style="width:160px" /></el-form-item>
      <el-form-item label="题型"><el-select v-model="queryParams.questionType" clearable style="width:160px"><el-option label="单选" value="single" /><el-option label="多选" value="multiple" /><el-option label="判断" value="judge" /><el-option label="填空" value="fill" /><el-option label="简答" value="essay" /></el-select></el-form-item>
      <el-form-item><el-button type="primary" icon="Search" @click="getList">搜索</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb16"><el-col :span="1.5"><el-button type="primary" plain icon="Plus" @click="handleAdd">新增题目</el-button></el-col></el-row>

    <el-table v-loading="loading" :data="dataList">
      <el-table-column label="题目ID" prop="questionId" width="90" />
      <el-table-column label="课程ID" prop="courseId" width="90" />
      <el-table-column label="题型" width="100">
        <template #default="scope">
          <el-tag>{{ questionTypeLabel(scope.row.questionType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="难度" width="90">
        <template #default="scope">
          <el-rate :model-value="Number(scope.row.difficultyLevel || 0)" disabled show-score text-color="#ff9900" score-template="{value}" />
        </template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">{{ scope.row.status === '0' ? '正常' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="题干" prop="stem" min-width="260" show-overflow-tooltip />
      <el-table-column label="操作" width="160">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleView(scope.row)">详情</el-button>
          <el-button link type="primary" icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog v-model="open" :title="title" width="820px">
      <el-form :model="form" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="课程ID"><el-input v-model="form.courseId" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="章节ID"><el-input v-model="form.chapterId" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="知识点ID"><el-input v-model="form.knowledgePointId" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="题型"><el-select v-model="form.questionType"><el-option label="单选" value="single" /><el-option label="多选" value="multiple" /><el-option label="判断" value="judge" /><el-option label="填空" value="fill" /><el-option label="简答" value="essay" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="难度"><el-input-number v-model="form.difficultyLevel" :min="1" :max="5" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="质量分"><el-input-number v-model="form.qualityScore" :min="0" :max="100" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="状态"><el-select v-model="form.status"><el-option label="正常" value="0" /><el-option label="停用" value="1" /></el-select></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="题干"><el-input v-model="form.stem" type="textarea" rows="4" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="答案"><el-input v-model="form.answer" type="textarea" rows="2" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="解析"><el-input v-model="form.analysis" type="textarea" rows="3" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer><el-button @click="open=false">取消</el-button><el-button type="primary" @click="submitForm">确定</el-button></template>
    </el-dialog>

    <el-dialog v-model="detailOpen" title="题目详情" width="820px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="题型">{{ questionTypeLabel(detail.questionType) }}</el-descriptions-item>
        <el-descriptions-item label="难度">{{ detail.difficultyLevel }}</el-descriptions-item>
        <el-descriptions-item label="题干" :span="2">{{ detail.stem }}</el-descriptions-item>
        <el-descriptions-item label="答案" :span="2">{{ detail.answer }}</el-descriptions-item>
        <el-descriptions-item label="解析" :span="2">{{ detail.analysis }}</el-descriptions-item>
      </el-descriptions>
      <el-table :data="detail.options || []" class="mt16">
        <el-table-column label="选项" prop="optionKey" width="80" />
        <el-table-column label="内容" prop="optionContent" min-width="220" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { addQuestion, getQuestionDetail, listQuestion, updateQuestion } from '@/api/campus/exam'

const loading = ref(false)
const total = ref(0)
const open = ref(false)
const detailOpen = ref(false)
const title = ref('')
const dataList = ref<any[]>([])
const detail = ref<any>({})
const queryParams = reactive<any>({ pageNum: 1, pageSize: 10, courseId: undefined, questionType: '' })
const form = reactive<any>({})

function resetForm() {
  Object.assign(form, { questionId: undefined, courseId: undefined, chapterId: undefined, knowledgePointId: undefined, questionType: 'single', difficultyLevel: 1, stem: '', answer: '', analysis: '', qualityScore: 80, status: '0' })
}

function questionTypeLabel(type: string) {
  return ({ single: '单选', multiple: '多选', judge: '判断', fill: '填空', essay: '简答' } as any)[type] || type || '-'
}

async function getList() {
  loading.value = true
  const res = await listQuestion(queryParams)
  dataList.value = res.rows || []
  total.value = res.total || 0
  loading.value = false
}

function handleAdd() {
  resetForm()
  title.value = '新增题目'
  open.value = true
}

function handleEdit(row: any) {
  Object.assign(form, row)
  title.value = '编辑题目'
  open.value = true
}

async function submitForm() {
  if (form.questionId) {
    await updateQuestion(form)
    ElMessage.success('修改成功')
  } else {
    await addQuestion(form)
    ElMessage.success('新增成功')
  }
  open.value = false
  getList()
}

async function handleView(row: any) {
  const res = await getQuestionDetail(row.questionId)
  detail.value = res.data || {}
  detailOpen.value = true
}

resetForm()
getList()
</script>

<style scoped>
.mb16 { margin-bottom: 16px }
.mt16 { margin-top: 16px }
</style>
