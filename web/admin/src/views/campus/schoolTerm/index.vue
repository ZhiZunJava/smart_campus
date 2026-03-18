<template>
  <div class="app-container">
    <section class="teaching-page-shell">
      <div class="teaching-page-shell__copy">
        <div class="teaching-page-shell__eyebrow">教学基础</div>
        <h2>学年学期</h2>
        <p>统一维护学年与学期，后续课表、班级课程、我的课程都基于这里切学期。</p>
      </div>
      <div class="teaching-page-shell__stats">
        <div v-for="item in headerStats" :key="item.label" class="teaching-page-shell__stat">
          <span>{{ item.label }}</span>
          <strong>{{ item.value }}</strong>
          <small>{{ item.tip }}</small>
        </div>
      </div>
    </section>

    <el-form :inline="true" :model="queryParams" class="mb16">
      <el-form-item label="学期名称"><el-input v-model="queryParams.termName" clearable style="width:220px" @keyup.enter="getList" /></el-form-item>
      <el-form-item label="学年"><el-input v-model="queryParams.schoolYear" clearable style="width:180px" @keyup.enter="getList" /></el-form-item>
      <el-form-item><el-button type="primary" icon="Search" @click="getList">搜索</el-button><el-button icon="Refresh" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb16">
      <el-col :span="1.5"><el-button type="primary" plain icon="Plus" @click="handleAdd">新增</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()">修改</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()">删除</el-button></el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column label="学期" min-width="240">
        <template #default="{ row }">
          <div class="entity-cell">
            <strong>{{ row.termName }}</strong>
            <span>{{ row.schoolYear }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="编码" prop="termCode" width="160" />
      <el-table-column label="起止日期" min-width="220">
        <template #default="{ row }">{{ formatDateRange(row.startDate, row.endDate) }}</template>
      </el-table-column>
      <el-table-column label="总周数" prop="totalWeeks" width="90" />
      <el-table-column label="排序" prop="sortOrder" width="80" />
      <el-table-column label="当前学期" width="100">
        <template #default="{ row }">
          <el-tag :type="row.isCurrent === '1' ? 'success' : 'info'">{{ row.isCurrent === '1' ? '当前' : '普通' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === '0' ? 'success' : 'danger'">{{ row.status === '0' ? '正常' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" min-width="220" show-overflow-tooltip />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(row)">修改</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog v-model="open" :title="title" width="640px">
      <TeachingAiAssist module-key="schoolTerm" module-label="学年学期" :form-data="form" :selected-rows="selectedRows" @apply="applyAiDraft" />
      <el-form :model="form" label-width="90px">
        <el-form-item label="学期名称"><el-input v-model="form.termName" /></el-form-item>
        <el-form-item label="学年"><el-input v-model="form.schoolYear" placeholder="例如 2025-2026" /></el-form-item>
        <el-form-item label="学期编码"><el-input v-model="form.termCode" placeholder="例如 2025-2026-1" /></el-form-item>
        <el-form-item label="开始日期"><el-date-picker v-model="form.startDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
        <el-form-item label="结束日期"><el-date-picker v-model="form.endDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
        <el-form-item label="总周数"><el-input-number v-model="form.totalWeeks" :min="1" :max="30" style="width:100%" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" :max="99" style="width:100%" /></el-form-item>
        <el-form-item label="当前学期"><el-switch v-model="currentSwitch" inline-prompt active-text="是" inactive-text="否" /></el-form-item>
        <el-form-item label="状态"><el-select v-model="form.status" style="width:100%"><el-option label="正常" value="0" /><el-option label="停用" value="1" /></el-select></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" rows="3" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="open = false">取消</el-button><el-button type="primary" @click="submitForm">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, reactive, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addSchoolTerm, delSchoolTerm, listSchoolTerm, updateSchoolTerm } from '@/api/campus/teaching'
import TeachingAiAssist from '@/components/Teaching/TeachingAiAssist.vue'

const loading=ref(false), showSearch=ref(true), total=ref(0), open=ref(false), title=ref(''), ids=ref<any[]>([]), single=ref(true), multiple=ref(true), dataList=ref<any[]>([]), selectedRows=ref<any[]>([])
const queryParams=reactive<any>({ pageNum:1,pageSize:10,termName:'',schoolYear:'' })
const form=reactive<any>({})
const currentSwitch = ref(false)
watch(currentSwitch, (value) => { form.isCurrent = value ? '1' : '0' })
const headerStats = computed(() => [
  { label: '当前页学期', value: total.value, tip: '统一管理学期切换' },
  { label: '当前学期', value: dataList.value.filter((item:any)=>item.isCurrent==='1').length, tip: '建议只保留一个当前学期' },
  { label: '学年覆盖', value: new Set(dataList.value.map((item:any)=>item.schoolYear).filter(Boolean)).size, tip: '课表将基于此切换' },
])
function resetForm(){ Object.assign(form,{ termId:undefined,termName:'',schoolYear:'',termCode:'',sortOrder:0,startDate:'',endDate:'',totalWeeks:20,isCurrent:'0',status:'0',remark:''}); currentSwitch.value=false }
function applyAiDraft(draft: Record<string, any>) { Object.assign(form, draft); currentSwitch.value = form.isCurrent === '1' }
async function getList(){ loading.value=true; const res=await listSchoolTerm(queryParams); dataList.value=res.rows||[]; total.value=res.total||0; loading.value=false }
function resetQuery(){ queryParams.pageNum=1; queryParams.termName=''; queryParams.schoolYear=''; getList() }
function handleSelectionChange(selection:any[]){ selectedRows.value=selection; ids.value=selection.map((i:any)=>i.termId); single.value=selection.length!==1; multiple.value=!selection.length }
function handleAdd(){ resetForm(); title.value='新增学年学期'; open.value=true }
function handleUpdate(row?:any){ const item=row || dataList.value.find((i:any)=>i.termId===ids.value[0]); if(!item) return; resetForm(); Object.assign(form,item); currentSwitch.value = form.isCurrent === '1'; title.value='修改学年学期'; open.value=true }
async function submitForm(){ if(form.termId){ await updateSchoolTerm(form); ElMessage.success('修改成功') } else { await addSchoolTerm(form); ElMessage.success('新增成功') } open.value=false; getList() }
async function handleDelete(row?:any){ const target=row?.termId || ids.value; if(!target || (Array.isArray(target)&&!target.length)) return; await ElMessageBox.confirm('确认删除所选学期吗？','提示',{type:'warning'}); await delSchoolTerm(target); ElMessage.success('删除成功'); getList() }
function formatDateRange(startDate?: string, endDate?: string) { return startDate || endDate ? `${startDate || '-'} 至 ${endDate || '-'}` : '-' }
resetForm(); getList()
</script>

<style scoped>
.mb16{margin-bottom:16px}
.teaching-page-shell{display:grid;grid-template-columns:minmax(0,1.15fr) minmax(320px,.85fr);gap:16px;margin-bottom:16px;padding:18px;border:1px solid #dbe3f0;border-radius:18px;background:linear-gradient(180deg,#fff 0%,#f7fbff 100%)}
.teaching-page-shell__eyebrow{color:#607188;font-size:12px;font-weight:700;letter-spacing:.08em}
.teaching-page-shell__copy h2{margin:8px 0 10px;color:#172033;font-size:24px;line-height:1.2}
.teaching-page-shell__copy p{margin:0;color:#526076;font-size:13px;line-height:1.8}
.teaching-page-shell__stats{display:grid;grid-template-columns:repeat(3,minmax(0,1fr));gap:10px}
.teaching-page-shell__stat{display:flex;flex-direction:column;gap:6px;padding:14px;border-radius:16px;background:#fff;border:1px solid rgba(219,227,240,.92)}
.teaching-page-shell__stat span,.teaching-page-shell__stat small{color:#667085;font-size:12px}
.teaching-page-shell__stat strong{color:#172033;font-size:22px;line-height:1.1}
.entity-cell{display:flex;flex-direction:column;gap:6px}
.entity-cell strong{color:#172033;font-size:13px}
.entity-cell span{color:#667085;font-size:12px}
@media (max-width: 1100px){.teaching-page-shell{grid-template-columns:1fr}.teaching-page-shell__stats{grid-template-columns:1fr}}
</style>
