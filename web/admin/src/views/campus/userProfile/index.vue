<template>
  <div class="app-container">
    <el-form :model="queryParams" :inline="true" v-show="showSearch" class="mb16">
      <el-form-item label="姓名">
        <el-input v-model="queryParams.realName" placeholder="请输入姓名" clearable style="width: 200px" @keyup.enter="getList" />
      </el-form-item>
      <el-form-item label="学号">
        <el-input v-model="queryParams.studentNo" placeholder="请输入学号" clearable style="width: 180px" @keyup.enter="getList" />
      </el-form-item>
      <el-form-item label="用户类型">
        <el-select v-model="queryParams.userType" placeholder="全部" clearable style="width: 140px">
          <el-option label="学生" value="student" />
          <el-option label="教师" value="teacher" />
          <el-option label="家长" value="parent" />
        </el-select>
      </el-form-item>
      <el-form-item label="辅导员">
        <el-select v-model="queryParams.advisorUserId" placeholder="全部" clearable filterable style="width: 200px">
          <el-option v-for="item in advisorOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="getList">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="overview-grid mb16">
      <div class="overview-card">
        <span>档案总数</span>
        <strong>{{ total }}</strong>
      </div>
      <div class="overview-card is-success">
        <span>学生档案</span>
        <strong>{{ studentCount }}</strong>
      </div>
      <div class="overview-card is-warning">
        <span>已绑辅导员</span>
        <strong>{{ boundAdvisorCount }}</strong>
      </div>
      <div class="overview-card is-info">
        <span>待绑定学生</span>
        <strong>{{ unboundStudentCount }}</strong>
      </div>
    </div>

    <el-row :gutter="10" class="mb16">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Connection" :disabled="!selectedStudentIds.length" @click="openAdvisorBindDialog">批量绑定辅导员</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()">档案配置</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="userProfileList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" />
      <el-table-column label="档案ID" prop="profileId" width="80" />
      <el-table-column label="姓名" prop="realName" min-width="100" />
      <el-table-column label="类型" width="80">
        <template #default="scope">
          <el-tag size="small">{{ userTypeLabel(scope.row.userType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="学号" prop="studentNo" width="120" />
      <el-table-column label="性别" width="60">
        <template #default="scope">{{ genderLabel(scope.row.gender) }}</template>
      </el-table-column>
      <el-table-column label="民族" prop="nation" width="80" />
      <el-table-column label="专业" prop="major" min-width="120" show-overflow-tooltip />
      <el-table-column label="班级" min-width="110" show-overflow-tooltip>
        <template #default="scope">{{ getOptionLabel(classOptions, scope.row.classId) }}</template>
      </el-table-column>
      <el-table-column label="辅导员" min-width="100" show-overflow-tooltip>
        <template #default="scope">{{ getOptionLabel(advisorOptions, scope.row.advisorUserId) }}</template>
      </el-table-column>
      <el-table-column label="政治面貌" prop="politicalStatus" width="100" show-overflow-tooltip />
      <el-table-column label="状态" width="70">
        <template #default="scope">
          <el-tag size="small" :type="scope.row.status === '0' ? 'success' : 'danger'">{{ scope.row.status === '0' ? '正常' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)">配置</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 档案配置弹窗 -->
    <el-dialog v-model="open" :title="title" width="960px" destroy-on-close>
      <el-tabs v-model="activeFormTab" class="profile-tabs">
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息" name="basic">
          <el-form :model="form" label-width="100px" class="profile-form">
            <el-row :gutter="16">
              <el-col :span="12"><el-form-item label="关联用户"><el-select v-model="form.userId" filterable clearable placeholder="请选择用户" style="width:100%"><el-option v-for="item in userOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="姓名"><el-input v-model="form.realName" placeholder="请输入姓名" /></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="曾用名"><el-input v-model="form.formerName" placeholder="请输入曾用名" /></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="性别"><el-select v-model="form.gender" placeholder="请选择" style="width:100%"><el-option label="男" value="0" /><el-option label="女" value="1" /><el-option label="未知" value="2" /></el-select></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="民族"><el-input v-model="form.nation" placeholder="如：汉族" /></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="出生日期"><el-date-picker v-model="form.birthDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width:100%" /></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="政治面貌"><el-select v-model="form.politicalStatus" placeholder="请选择" clearable style="width:100%"><el-option label="中共党员" value="中共党员" /><el-option label="中共预备党员" value="中共预备党员" /><el-option label="共青团员" value="共青团员" /><el-option label="群众" value="群众" /><el-option label="民主党派" value="民主党派" /></el-select></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="籍贯"><el-input v-model="form.nativePlace" placeholder="如：广东广州" /></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="证件类型"><el-select v-model="form.idType" placeholder="请选择" clearable style="width:100%"><el-option label="居民身份证" value="1" /><el-option label="军官证" value="2" /><el-option label="护照" value="3" /><el-option label="港澳通行证" value="4" /><el-option label="台湾通行证" value="5" /></el-select></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="证件号码"><el-input v-model="form.idNumber" placeholder="请输入证件号码" /></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="用户类型"><el-select v-model="form.userType" style="width:100%"><el-option label="学生" value="student" /><el-option label="教师" value="teacher" /><el-option label="家长" value="parent" /><el-option label="管理员" value="admin" /></el-select></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="状态"><el-select v-model="form.status" style="width:100%"><el-option label="正常" value="0" /><el-option label="停用" value="1" /></el-select></el-form-item></el-col>
            </el-row>
          </el-form>
        </el-tab-pane>

        <!-- 学籍信息（仅学生） -->
        <el-tab-pane label="学籍信息" name="academic" v-if="form.userType === 'student'">
          <el-form :model="form" label-width="100px" class="profile-form">
            <el-row :gutter="16">
              <el-col :span="12"><el-form-item label="学号"><el-input v-model="form.studentNo" placeholder="请输入学号" /></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="工号"><el-input v-model="form.teacherNo" placeholder="请输入工号" /></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="年级"><el-select v-model="form.gradeId" filterable clearable placeholder="请选择年级" style="width:100%"><el-option v-for="item in gradeOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="班级"><el-select v-model="form.classId" filterable clearable placeholder="请选择班级" style="width:100%"><el-option v-for="item in classOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="专业"><el-input v-model="form.major" placeholder="请输入专业" /></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="入学年份"><el-input v-model="form.admissionYear" placeholder="如：2023" /></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="培养类型"><el-select v-model="form.cultivationType" clearable style="width:100%"><el-option label="主修" value="主修" /><el-option label="辅修" value="辅修" /><el-option label="双学位" value="双学位" /></el-select></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="学历层次"><el-select v-model="form.educationLevel" clearable style="width:100%"><el-option label="专科" value="专科" /><el-option label="本科" value="本科" /><el-option label="硕士" value="硕士" /><el-option label="博士" value="博士" /></el-select></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="学制"><el-input v-model="form.schoolingLength" placeholder="如：4年" /></el-form-item></el-col>
              <el-col :span="12" v-if="form.userType === 'student'"><el-form-item label="辅导员"><el-select v-model="form.advisorUserId" filterable clearable placeholder="请选择辅导员" style="width:100%"><el-option v-for="item in advisorOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
            </el-row>
          </el-form>
        </el-tab-pane>

        <!-- 录取信息（仅学生） -->
        <el-tab-pane label="录取信息" name="admission" v-if="form.userType === 'student'">
          <el-form :model="form" label-width="100px" class="profile-form">
            <el-row :gutter="16">
              <el-col :span="12"><el-form-item label="录取方式"><el-select v-model="form.admissionType" clearable style="width:100%"><el-option label="统招" value="统招" /><el-option label="自主招生" value="自主招生" /><el-option label="保送" value="保送" /><el-option label="特长生" value="特长生" /><el-option label="其他" value="其他" /></el-select></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="考生号"><el-input v-model="form.examNumber" placeholder="请输入考生号" /></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="准考证号"><el-input v-model="form.admissionTicketNo" placeholder="请输入准考证号" /></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="录取分数"><el-input-number v-model="form.admissionScore" :precision="1" :min="0" :max="999" controls-position="right" style="width:100%" /></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="录取批次"><el-input v-model="form.admissionBatch" placeholder="如：本科一批" /></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="生源地"><el-input v-model="form.sourceRegion" placeholder="如：广东省" /></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="毕业中学"><el-input v-model="form.graduateSchool" placeholder="请输入毕业中学" /></el-form-item></el-col>
            </el-row>
          </el-form>
        </el-tab-pane>

        <!-- 毕业信息（仅学生） -->
        <el-tab-pane label="毕业信息" name="graduation" v-if="form.userType === 'student'">
          <el-form :model="form" label-width="120px" class="profile-form">
            <el-row :gutter="16">
              <el-col :span="12"><el-form-item label="预计毕业时间"><el-date-picker v-model="form.expectedGraduationDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="实际毕业时间"><el-date-picker v-model="form.actualGraduationDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="毕业证书编号"><el-input v-model="form.diplomaNo" /></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="结业/肄业标识"><el-select v-model="form.completionType" clearable style="width:100%"><el-option label="毕业" value="毕业" /><el-option label="结业" value="结业" /><el-option label="肄业" value="肄业" /></el-select></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="毕业去向"><el-input v-model="form.graduationDestination" placeholder="如：就业、升学、出国" /></el-form-item></el-col>
              <el-col :span="24"><el-form-item label="毕业备注"><el-input v-model="form.graduationRemark" type="textarea" :rows="2" /></el-form-item></el-col>
            </el-row>
          </el-form>
        </el-tab-pane>

        <!-- 学位信息（仅学生） -->
        <el-tab-pane label="学位信息" name="degree" v-if="form.userType === 'student'">
          <el-form :model="form" label-width="120px" class="profile-form">
            <el-row :gutter="16">
              <el-col :span="12"><el-form-item label="学位类别"><el-select v-model="form.degreeType" clearable style="width:100%"><el-option label="学士" value="学士" /><el-option label="硕士" value="硕士" /><el-option label="博士" value="博士" /></el-select></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="学位授予日期"><el-date-picker v-model="form.degreeAwardDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="学位证书编号"><el-input v-model="form.degreeCertNo" /></el-form-item></el-col>
              <el-col :span="24"><el-form-item label="学位论文题目"><el-input v-model="form.thesisTitle" /></el-form-item></el-col>
              <el-col :span="24"><el-form-item label="学位备注"><el-input v-model="form.degreeRemark" type="textarea" :rows="2" /></el-form-item></el-col>
            </el-row>
          </el-form>
        </el-tab-pane>

        <!-- 联系信息 -->
        <el-tab-pane label="联系信息" name="contact">
          <el-form :model="form" label-width="110px" class="profile-form">
            <el-row :gutter="16">
              <el-col :span="12"><el-form-item label="通讯地址"><el-input v-model="form.contactAddress" placeholder="请输入通讯地址" /></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="家庭住址"><el-input v-model="form.homeAddress" placeholder="请输入家庭住址" /></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="紧急联系人"><el-input v-model="form.emergencyContact" placeholder="请输入紧急联系人" /></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="紧急联系电话"><el-input v-model="form.emergencyPhone" placeholder="请输入紧急联系电话" /></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="户口所在地"><el-input v-model="form.householdAddress" placeholder="请输入户口所在地" /></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="银行卡号"><el-input v-model="form.bankCardNo" placeholder="请输入银行卡号" /></el-form-item></el-col>
            </el-row>
          </el-form>
        </el-tab-pane>
      </el-tabs>
      <template #footer>
        <el-button @click="open = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>

    <!-- 批量绑定辅导员弹窗 -->
    <el-dialog v-model="advisorBindOpen" title="批量绑定辅导员" width="520px">
      <div class="advisor-bind-panel">
        <p>已选择 <strong>{{ selectedStudentIds.length }}</strong> 个学生档案</p>
        <el-select v-model="advisorBindForm.advisorUserId" clearable filterable placeholder="请选择辅导员，清空表示解绑" style="width: 100%">
          <el-option v-for="item in advisorOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </div>
      <template #footer>
        <el-button @click="advisorBindOpen = false">取消</el-button>
        <el-button type="primary" :loading="bindingAdvisor" @click="submitAdvisorBind">确认绑定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { addUserProfile, bindUserProfileAdvisor, getUserProfile, listUserProfile, updateUserProfile } from '@/api/campus/userProfile'
import { fetchClassOptions, fetchGradeOptions, fetchTeachingUserOptions, fetchUserOptions } from '@/api/campus/options'

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
const advisorOptions = ref<any[]>([])
const advisorBindOpen = ref(false)
const bindingAdvisor = ref(false)
const selectedProfileRows = ref<any[]>([])
const activeFormTab = ref('basic')

const queryParams = reactive<any>({ pageNum: 1, pageSize: 10, realName: '', studentNo: '', userType: '', advisorUserId: undefined })
const form = reactive<any>({})
const advisorBindForm = reactive<any>({ advisorUserId: undefined })

const studentCount = computed(() => userProfileList.value.filter((item: any) => item.userType === 'student').length)
const boundAdvisorCount = computed(() => userProfileList.value.filter((item: any) => item.userType === 'student' && item.advisorUserId).length)
const unboundStudentCount = computed(() => userProfileList.value.filter((item: any) => item.userType === 'student' && !item.advisorUserId).length)
const selectedStudentIds = computed(() => selectedProfileRows.value.filter((item: any) => item.userType === 'student').map((item: any) => item.profileId))

/* 当用户类型切换为非学生时，若当前 Tab 是学籍相关则自动回退到基本信息 */
const studentOnlyTabs = new Set(['academic', 'admission', 'graduation', 'degree'])
watch(() => form.userType, (val) => {
  if (val !== 'student' && studentOnlyTabs.has(activeFormTab.value)) {
    activeFormTab.value = 'basic'
  }
})

const defaultFormData = {
  profileId: undefined, userId: undefined, realName: '', formerName: '', userType: 'student',
  gender: '', nation: '', birthDate: '', politicalStatus: '', nativePlace: '', idType: '', idNumber: '',
  studentNo: '', teacherNo: '', gradeId: undefined, classId: undefined, major: '', admissionYear: '',
  cultivationType: '', educationLevel: '', schoolingLength: '', advisorUserId: undefined, status: '0',
  admissionType: '', examNumber: '', admissionTicketNo: '', admissionScore: undefined, admissionBatch: '', sourceRegion: '', graduateSchool: '',
  expectedGraduationDate: '', actualGraduationDate: '', diplomaNo: '', completionType: '', graduationDestination: '', graduationRemark: '',
  degreeType: '', degreeAwardDate: '', degreeCertNo: '', thesisTitle: '', degreeRemark: '',
  contactAddress: '', homeAddress: '', emergencyContact: '', emergencyPhone: '', householdAddress: '', bankCardNo: '',
}

function resetFormData() { Object.assign(form, { ...defaultFormData }) }

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
  queryParams.studentNo = ''
  queryParams.userType = ''
  queryParams.advisorUserId = undefined
  getList()
}

function handleSelectionChange(selection: any[]) {
  selectedProfileRows.value = selection
  ids.value = selection.map((item) => item.profileId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

async function handleUpdate(row?: any) {
  resetFormData()
  activeFormTab.value = 'basic'
  const profileId = row?.profileId || ids.value[0]
  if (!profileId) return
  const res = await getUserProfile(profileId)
  Object.assign(form, res.data || {})
  open.value = true
  const typeLabel = userTypeLabel(form.userType)
  title.value = form.userType === 'student' ? '学籍档案配置' : `${typeLabel}档案配置`
}

async function submitForm() {
  const payload = {
    ...form,
    advisorUserId: form.userType === 'student' ? (form.advisorUserId ?? 0) : 0,
    admissionYear: form.admissionYear ? Number(form.admissionYear) : undefined,
  }
  if (form.profileId) {
    await updateUserProfile(payload)
    ElMessage.success('保存成功')
  } else {
    await addUserProfile(payload)
    ElMessage.success('新增成功')
  }
  open.value = false
  getList()
}

function userTypeLabel(type: string) { return ({ student: '学生', teacher: '教师', parent: '家长', admin: '管理员' } as any)[type] || type || '-' }
function genderLabel(g: string) { return ({ '0': '男', '1': '女' } as any)[g] || '-' }
function getOptionLabel(options: any[], value: any, fallback = '-') { return options.find((item) => item.value === value)?.label || fallback }

function openAdvisorBindDialog() {
  advisorBindForm.advisorUserId = undefined
  advisorBindOpen.value = true
}
async function submitAdvisorBind() {
  if (!selectedStudentIds.value.length) return
  bindingAdvisor.value = true
  try {
    await bindUserProfileAdvisor({ profileIds: selectedStudentIds.value, advisorUserId: advisorBindForm.advisorUserId ?? 0 })
    ElMessage.success('辅导员绑定已更新')
    advisorBindOpen.value = false
    getList()
  } finally {
    bindingAdvisor.value = false
  }
}

async function loadOptions() {
  userOptions.value = await fetchUserOptions()
  gradeOptions.value = await fetchGradeOptions()
  classOptions.value = await fetchClassOptions()
  advisorOptions.value = await fetchTeachingUserOptions('head_teacher')
}

onMounted(async () => { await loadOptions(); resetFormData(); getList() })
</script>

<style scoped>
.mb16 { margin-bottom: 16px; }
.overview-grid { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 12px; }
.overview-card { padding: 16px; border-radius: 12px; background: #f8fafc; border: 1px solid #e2e8f0; }
.overview-card span { display: block; color: #64748b; font-size: 12px; }
.overview-card strong { display: block; margin-top: 8px; font-size: 24px; color: #0f172a; }
.overview-card.is-success strong { color: #059669; }
.overview-card.is-warning strong { color: #d97706; }
.overview-card.is-info strong { color: #2563eb; }
.advisor-bind-panel { display: grid; gap: 16px; }
.profile-tabs :deep(.el-tabs__header) { margin-bottom: 20px; }
.profile-form { padding: 0 8px; }
</style>
