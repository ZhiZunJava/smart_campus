<template>
  <div class="app-container">
    <el-form :model="queryParams" :inline="true" v-show="showSearch" class="mb16">
      <el-form-item label="资源名称"><el-input v-model="queryParams.resourceName" placeholder="请输入资源名称" clearable style="width: 220px" @keyup.enter="getList" /></el-form-item>
      <el-form-item label="资源类型"><el-select v-model="queryParams.resourceType" clearable style="width: 180px"><el-option label="视频" value="video" /><el-option label="文档" value="doc" /><el-option label="PPT" value="ppt" /><el-option label="PDF" value="pdf" /><el-option label="题目" value="question" /></el-select></el-form-item>
      <el-form-item label="课程"><el-select v-model="queryParams.courseId" filterable clearable style="width: 240px"><el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item><el-button type="primary" icon="Search" @click="getList">搜索</el-button><el-button icon="Refresh" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb16">
      <el-col :span="1.5"><el-button type="primary" plain icon="Plus" @click="handleAdd">新增</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()">修改</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()">删除</el-button></el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="resourceList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column label="资源ID" prop="resourceId" width="90" />
      <el-table-column label="资源名称" prop="resourceName" min-width="180" show-overflow-tooltip />
      <el-table-column label="类型" prop="resourceType" width="100" />
      <el-table-column label="课程ID" prop="courseId" width="90" />
      <el-table-column label="审核状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.auditStatus === '1' ? 'success' : scope.row.auditStatus === '2' ? 'danger' : 'warning'">
            {{ scope.row.auditStatus === '1' ? '通过' : scope.row.auditStatus === '2' ? '驳回' : '待审' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="浏览" prop="viewCount" width="80" />
      <el-table-column label="下载" prop="downloadCount" width="80" />
      <el-table-column label="收藏" prop="favoriteCount" width="80" />
      <el-table-column label="质量分" prop="qualityScore" width="90" />
      <el-table-column label="操作" width="210" fixed="right">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleView(scope.row)">详情</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog v-model="open" :title="title" width="760px">
      <el-form :model="form" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="资源名称"><el-input v-model="form.resourceName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="资源类型"><el-select v-model="form.resourceType"><el-option label="视频" value="video" /><el-option label="文档" value="doc" /><el-option label="PPT" value="ppt" /><el-option label="PDF" value="pdf" /><el-option label="题目" value="question" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="课程"><el-select v-model="form.courseId" filterable clearable><el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="章节ID"><el-input v-model="form.chapterId" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="上传者"><el-select v-model="form.uploaderId" filterable clearable><el-option v-for="item in userOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="文件地址"><el-input v-model="form.fileUrl" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="封面地址"><el-input v-model="form.coverUrl" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="审核状态"><el-select v-model="form.auditStatus"><el-option label="待审" value="0" /><el-option label="通过" value="1" /><el-option label="驳回" value="2" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="质量分"><el-input v-model="form.qualityScore" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="摘要"><el-input v-model="form.summary" type="textarea" rows="2" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer><el-button @click="open = false">取消</el-button><el-button type="primary" @click="submitForm">确定</el-button></template>
    </el-dialog>

    <el-dialog v-model="detailOpen" title="资源详情" width="760px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="资源名称">{{ detail.resourceName }}</el-descriptions-item>
        <el-descriptions-item label="资源类型">{{ detail.resourceType }}</el-descriptions-item>
        <el-descriptions-item label="课程ID">{{ detail.courseId }}</el-descriptions-item>
        <el-descriptions-item label="审核状态">{{ detail.auditStatus }}</el-descriptions-item>
        <el-descriptions-item label="浏览数">{{ detail.viewCount }}</el-descriptions-item>
        <el-descriptions-item label="下载数">{{ detail.downloadCount }}</el-descriptions-item>
        <el-descriptions-item label="收藏数">{{ detail.favoriteCount }}</el-descriptions-item>
        <el-descriptions-item label="质量分">{{ detail.qualityScore }}</el-descriptions-item>
        <el-descriptions-item label="摘要" :span="2">{{ detail.summary }}</el-descriptions-item>
        <el-descriptions-item label="文件地址" :span="2">
          <el-link v-if="detail.fileUrl" :href="detail.fileUrl" target="_blank" type="primary">{{ detail.fileUrl }}</el-link>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="内容文本" :span="2">{{ detail.contentText || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addResource, delResource, getResource, listResource, updateResource } from '@/api/campus/resource'
import { fetchCourseOptions, fetchUserOptions } from '@/api/campus/options'

const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const resourceList = ref<any[]>([])
const open = ref(false)
const detailOpen = ref(false)
const title = ref('')
const detail = ref<any>({})
const ids = ref<any[]>([])
const single = ref(true)
const multiple = ref(true)
const courseOptions = ref<any[]>([])
const userOptions = ref<any[]>([])
const queryParams = reactive<any>({ pageNum: 1, pageSize: 10, resourceName: '', resourceType: '', courseId: undefined })
const form = reactive<any>({})

function resetFormData() { Object.assign(form, { resourceId: undefined, resourceName: '', resourceType: 'video', courseId: undefined, chapterId: undefined, uploaderId: undefined, fileUrl: '', coverUrl: '', summary: '', auditStatus: '1', qualityScore: 80, status: '0', viewCount: 0, downloadCount: 0, favoriteCount: 0 }) }
async function getList() { loading.value = true; const res = await listResource(queryParams); resourceList.value = res.rows || []; total.value = res.total || 0; loading.value = false }
function resetQuery() { queryParams.pageNum = 1; queryParams.resourceName = ''; queryParams.resourceType = ''; getList() }
function handleSelectionChange(selection: any[]) { ids.value = selection.map(i => i.resourceId); single.value = selection.length !== 1; multiple.value = !selection.length }
function handleAdd() { resetFormData(); title.value = '新增资源'; open.value = true }
async function handleUpdate(row?: any) { resetFormData(); const resourceId = row?.resourceId || ids.value[0]; const res = await getResource(resourceId); Object.assign(form, res.data || {}); title.value = '修改资源'; open.value = true }
async function submitForm() { if (form.resourceId) { await updateResource(form); ElMessage.success('修改成功') } else { await addResource(form); ElMessage.success('新增成功') } open.value = false; getList() }
async function handleDelete(row?: any) { const resourceIds = row?.resourceId || ids.value; if (!resourceIds || (Array.isArray(resourceIds) && !resourceIds.length)) return; await ElMessageBox.confirm('确认删除选中的资源吗？', '提示', { type: 'warning' }); await delResource(resourceIds); ElMessage.success('删除成功'); getList() }
async function handleView(row: any) { const res = await getResource(row.resourceId); detail.value = res.data || {}; detailOpen.value = true }
async function loadOptions(){ courseOptions.value = await fetchCourseOptions(); userOptions.value = await fetchUserOptions() }
onMounted(async()=>{ await loadOptions(); resetFormData(); getList() })
</script>

<style scoped>
.mb16{margin-bottom:16px;}
</style>
