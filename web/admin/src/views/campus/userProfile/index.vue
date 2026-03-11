<template>
  <div class="app-container">
    <el-form :model="queryParams" :inline="true" v-show="showSearch" class="mb16">
      <el-form-item label="姓名">
        <el-input v-model="queryParams.realName" placeholder="请输入姓名" clearable style="width: 220px" @keyup.enter="getList" />
      </el-form-item>
      <el-form-item label="用户类型">
        <el-select v-model="queryParams.userType" placeholder="请选择类型" clearable style="width: 180px">
          <el-option label="学生" value="student" />
          <el-option label="教师" value="teacher" />
          <el-option label="家长" value="parent" />
          <el-option label="管理员" value="admin" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="getList">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb16">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="userProfileList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column label="档案ID" prop="profileId" width="90" />
      <el-table-column label="用户ID" prop="userId" width="90" />
      <el-table-column label="姓名" prop="realName" min-width="120" />
      <el-table-column label="用户类型" width="110">
        <template #default="scope">
          <el-tag>{{ userTypeLabel(scope.row.userType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="学号" prop="studentNo" width="120" />
      <el-table-column label="工号" prop="teacherNo" width="120" />
      <el-table-column label="学习目标" prop="learningGoal" min-width="180" show-overflow-tooltip />
      <el-table-column label="状态" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">{{ scope.row.status === '0' ? '正常' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog v-model="open" :title="title" width="680px">
      <el-form :model="form" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="用户"><el-select v-model="form.userId" filterable clearable><el-option v-for="item in userOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="姓名"><el-input v-model="form.realName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="用户类型"><el-select v-model="form.userType"><el-option label="学生" value="student" /><el-option label="教师" value="teacher" /><el-option label="家长" value="parent" /><el-option label="管理员" value="admin" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="学号"><el-input v-model="form.studentNo" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="工号"><el-input v-model="form.teacherNo" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="年级"><el-select v-model="form.gradeId" filterable clearable><el-option v-for="item in gradeOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="班级"><el-select v-model="form.classId" filterable clearable><el-option v-for="item in classOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="状态"><el-select v-model="form.status"><el-option label="正常" value="0" /><el-option label="停用" value="1" /></el-select></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="学习目标"><el-input v-model="form.learningGoal" type="textarea" rows="3" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="open = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addUserProfile, delUserProfile, getUserProfile, listUserProfile, updateUserProfile } from '@/api/campus/userProfile'
import { fetchClassOptions, fetchGradeOptions, fetchUserOptions } from '@/api/campus/options'

const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const userProfileList = ref<any[]>([])
const open = ref(false)
const title = ref('')
const ids = ref<Array<number>>([])
const single = ref(true)
const multiple = ref(true)
const userOptions = ref<any[]>([])
const gradeOptions = ref<any[]>([])
const classOptions = ref<any[]>([])

const queryParams = reactive<any>({ pageNum: 1, pageSize: 10, realName: '', userType: '' })
const form = reactive<any>({})

function resetFormData() {
  Object.assign(form, { profileId: undefined, userId: undefined, realName: '', userType: 'student', studentNo: '', teacherNo: '', gradeId: undefined, classId: undefined, learningGoal: '', status: '0' })
}

async function getList() {
  loading.value = true
  const res = await listUserProfile(queryParams)
  userProfileList.value = res.rows || []
  total.value = res.total || 0
  loading.value = false
}

function resetQuery() {
  queryParams.pageNum = 1
  queryParams.realName = ''
  queryParams.userType = ''
  getList()
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.profileId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

function handleAdd() {
  resetFormData()
  open.value = true
  title.value = '新增用户档案'
}

async function handleUpdate(row?: any) {
  resetFormData()
  const profileId = row?.profileId || ids.value[0]
  if (!profileId) return
  const res = await getUserProfile(profileId)
  Object.assign(form, res.data || {})
  open.value = true
  title.value = '修改用户档案'
}

async function submitForm() {
  if (form.profileId) {
    await updateUserProfile(form)
    ElMessage.success('修改成功')
  } else {
    await addUserProfile(form)
    ElMessage.success('新增成功')
  }
  open.value = false
  getList()
}

async function handleDelete(row?: any) {
  const profileIds = row?.profileId || ids.value
  if (!profileIds || (Array.isArray(profileIds) && profileIds.length === 0)) return
  await ElMessageBox.confirm('确认删除选中的用户档案吗？', '提示', { type: 'warning' })
  await delUserProfile(profileIds)
  ElMessage.success('删除成功')
  getList()
}

function userTypeLabel(type:string){ return ({ student:'学生', teacher:'教师', parent:'家长', admin:'管理员' } as any)[type] || type || '-' }
async function loadOptions(){ userOptions.value = await fetchUserOptions(); gradeOptions.value = await fetchGradeOptions(); classOptions.value = await fetchClassOptions() }
onMounted(async()=>{ await loadOptions(); resetFormData(); getList() })
</script>

<style scoped>
.mb16 { margin-bottom: 16px; }
</style>
