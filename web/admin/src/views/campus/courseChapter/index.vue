<template>
  <div class="app-container">
    <section class="teaching-page-shell">
      <div class="teaching-page-shell__copy">
        <div class="teaching-page-shell__eyebrow">教学基础</div>
        <h2>课程章节</h2>
        <p>支持按课程快速维护章节结构，AI 可协助生成层级名称、排序和章节草案。</p>
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
      <el-form-item label="课程"><el-select v-model="queryParams.courseId" filterable clearable style="width:240px"><el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item label="章节名称"><el-input v-model="queryParams.chapterName" clearable style="width:220px" @keyup.enter="getList" /></el-form-item>
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
      <el-table-column label="章节" min-width="260">
        <template #default="{ row }">
          <div class="entity-cell">
            <strong>{{ row.chapterName }}</strong>
            <span>{{ getOptionLabel(courseOptions, row.courseId, '课程') }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="父章节ID" prop="parentId" width="100" />
      <el-table-column label="排序" prop="chapterOrder" width="80" />
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

    <el-dialog v-model="open" :title="title" width="660px">
      <TeachingAiAssist
        module-key="courseChapter"
        module-label="课程章节"
        :form-data="form"
        :selected-rows="selectedRows"
        :available-options="{ courseOptions }"
        @apply="applyAiDraft"
      />
      <el-form :model="form" label-width="90px">
        <el-form-item label="课程"><el-select v-model="form.courseId" filterable clearable style="width:100%"><el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
        <el-form-item label="父章节ID"><el-input v-model="form.parentId" /></el-form-item>
        <el-form-item label="章节名称"><el-input v-model="form.chapterName" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.chapterOrder" :min="0" :max="999" style="width:100%" /></el-form-item>
        <el-form-item label="状态"><el-select v-model="form.status" style="width:100%"><el-option label="正常" value="0" /><el-option label="停用" value="1" /></el-select></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" rows="3" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="open=false">取消</el-button><el-button type="primary" @click="submitForm">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addCourseChapter, delCourseChapter, listCourseChapter, updateCourseChapter } from '@/api/campus/teaching'
import { fetchCourseOptions } from '@/api/campus/options'
import TeachingAiAssist from '@/components/Teaching/TeachingAiAssist.vue'

const loading=ref(false), showSearch=ref(true), total=ref(0), open=ref(false), title=ref(''), ids=ref<any[]>([]), single=ref(true), multiple=ref(true), dataList=ref<any[]>([])
const courseOptions=ref<any[]>([])
const selectedRows=ref<any[]>([])
const queryParams=reactive<any>({ pageNum:1,pageSize:10,courseId:undefined,chapterName:'' })
const form=reactive<any>({})
const headerStats = computed(() => [
  { label: '当前页章节', value: total.value, tip: '支持按课程筛选' },
  { label: '已启用', value: dataList.value.filter((item: any) => item.status === '0').length, tip: '层级结构更清晰' },
  { label: '课程覆盖', value: new Set(dataList.value.map((item: any) => item.courseId).filter(Boolean)).size, tip: 'AI 可生成章节树' },
])

function getOptionLabel(options: any[], value: any, fallback: string) {
  return options.find((item) => item.value === value)?.label || `${fallback} ${value || '-'}`
}
function resetFormData(){ Object.assign(form,{ chapterId:undefined,courseId:undefined,parentId:0,chapterName:'',chapterOrder:0,status:'0',remark:''}) }
function applyAiDraft(draft: Record<string, any>) { Object.assign(form, draft) }
async function getList(){ loading.value=true; const res=await listCourseChapter(queryParams); dataList.value=res.rows||[]; total.value=res.total||0; loading.value=false }
function resetQuery(){ queryParams.pageNum=1; queryParams.courseId=undefined; queryParams.chapterName=''; getList() }
function handleSelectionChange(selection:any[]){ selectedRows.value = selection; ids.value=selection.map(i=>i.chapterId); single.value=selection.length!==1; multiple.value=!selection.length }
function handleAdd(){ resetFormData(); title.value='新增章节'; open.value=true }
function handleUpdate(row?:any){ const item=row || dataList.value.find((i:any)=>i.chapterId===ids.value[0]); if(!item) return; resetFormData(); Object.assign(form,item); title.value='修改章节'; open.value=true }
async function submitForm(){ if(form.chapterId){ await updateCourseChapter(form); ElMessage.success('修改成功') } else { await addCourseChapter(form); ElMessage.success('新增成功') } open.value=false; getList() }
async function handleDelete(row?:any){ const target=row?.chapterId || ids.value; if(!target || (Array.isArray(target)&&!target.length)) return; await ElMessageBox.confirm('确认删除所选章节吗？','提示',{type:'warning'}); await delCourseChapter(target); ElMessage.success('删除成功'); getList() }
async function loadOptions(){ courseOptions.value = await fetchCourseOptions() }
onMounted(async()=>{ await loadOptions(); resetFormData(); getList() })
</script>

<style scoped>
.mb16{margin-bottom:16px;}
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
