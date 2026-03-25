<template>
  <div class="app-container resource-studio">
    <section class="resource-studio__hero">
      <div>
        <div class="resource-studio__eyebrow">资源管理</div>
        <h2>课程资源编排中心</h2>
        <p>按“课程 -> 目录节点 -> 文章/课时 -> 资源资产”维护内容，适合教务和资源管理员做日常编排与运营管理。</p>
      </div>
      <div class="resource-studio__actions">
        <el-select v-model="courseId" filterable clearable placeholder="请选择课程" style="width: 320px" @change="handleCourseChange">
          <el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-button type="primary" icon="Plus" :disabled="!courseId" @click="handleInitTree">初始化根目录</el-button>
      </div>
    </section>

    <el-row :gutter="16" class="mb16" v-if="courseId">
      <el-col :span="6"><el-card shadow="never"><div class="metric-label">文章/课时</div><div class="metric-value">{{ overview.totalResourceCount || 0 }}</div></el-card></el-col>
      <el-col :span="6"><el-card shadow="never"><div class="metric-label">待审内容</div><div class="metric-value warning">{{ overview.pendingAuditCount || 0 }}</div></el-card></el-col>
      <el-col :span="6"><el-card shadow="never"><div class="metric-label">高潜内容</div><div class="metric-value success">{{ overview.highPotentialCount || 0 }}</div></el-card></el-col>
      <el-col :span="6"><el-card shadow="never"><div class="metric-label">平均质量分</div><div class="metric-value">{{ overview.avgQualityScore || 0 }}</div></el-card></el-col>
    </el-row>

    <div class="resource-studio__board">
      <aside class="resource-pane resource-tree-pane">
        <div class="resource-pane__header">
          <div>
            <strong>课程目录树</strong>
            <p>目录只管理结构层级，不承载资源类型。</p>
          </div>
          <el-space>
            <el-button size="small" type="primary" plain :disabled="!courseId" @click="openNodeDialog()">新增节点</el-button>
            <el-button size="small" :disabled="!selectedNode" @click="openNodeDialog(selectedNode)">编辑</el-button>
            <el-button size="small" type="danger" plain :disabled="!selectedNode" @click="handleDeleteNode">删除</el-button>
          </el-space>
        </div>
        <div class="resource-tree-pane__body">
          <el-empty v-if="!courseId" description="请先选择课程" />
          <el-empty v-else-if="!treeData.length" description="当前课程还没有目录，先初始化或手动新增节点" />
          <el-tree
            v-else
            node-key="nodeId"
            :data="treeData"
            :props="{ label: 'nodeName', children: 'children' }"
            default-expand-all
            highlight-current
            @node-click="handleNodeSelect"
          >
            <template #default="{ data }">
              <div class="resource-tree-node">
                <span>{{ data.nodeName }}</span>
                <el-tag size="small" effect="plain">{{ data.articleCount || 0 }}</el-tag>
              </div>
            </template>
          </el-tree>
        </div>
      </aside>

      <section class="resource-pane resource-article-pane">
        <div class="resource-pane__header">
          <div>
            <strong>文章 / 课时</strong>
            <p>{{ selectedNode ? `当前节点：${selectedNode.nodeName}` : '选择目录节点后维护文章和课时内容' }}</p>
          </div>
          <el-space>
            <el-button size="small" type="primary" plain :disabled="!selectedNode" @click="openArticleDialog()">新增文章</el-button>
            <el-button size="small" :disabled="!selectedArticle" @click="openArticleDialog(selectedArticle)">编辑</el-button>
            <el-button size="small" type="danger" plain :disabled="!selectedArticle" @click="handleDeleteArticle">删除</el-button>
          </el-space>
        </div>
        <div class="resource-article-pane__body">
          <el-empty v-if="!selectedNode" description="请选择一个目录节点" />
          <div v-else class="resource-article-list">
            <article
              v-for="item in articleList"
              :key="item.resourceId"
              class="article-card"
              :class="{ 'is-active': selectedArticle?.resourceId === item.resourceId }"
              @click="selectArticle(item)"
            >
              <div class="article-card__head">
                <div>
                  <h3>{{ item.resourceName }}</h3>
                  <p>{{ item.summary || '暂无摘要，建议补充文章导学说明。' }}</p>
                </div>
                <el-tag :type="item.auditStatus === '1' ? 'success' : item.auditStatus === '2' ? 'danger' : 'warning'" effect="plain">
                  {{ item.auditStatus === '1' ? '已审核' : item.auditStatus === '2' ? '已驳回' : '待审核' }}
                </el-tag>
              </div>
              <div class="article-card__meta">
                <span>作者：{{ item.authorName || '--' }}</span>
                <span>评分：{{ item.avgRating || 0 }}</span>
                <span>评论：{{ item.commentCount || 0 }}</span>
                <span>分享：{{ item.shareCount || 0 }}</span>
              </div>
            </article>
            <el-empty v-if="!articleList.length" description="当前节点还没有文章/课时" />
          </div>
        </div>
      </section>

      <aside class="resource-pane resource-asset-pane">
        <div class="resource-pane__header">
          <div>
            <strong>资源资产</strong>
            <p>{{ selectedArticle ? `当前文章：${selectedArticle.resourceName}` : '选择文章后维护视频、课件、教学设计等资源资产' }}</p>
          </div>
          <el-space>
            <el-button size="small" type="primary" plain :disabled="!selectedArticle" @click="openAssetDialog()">新增资源</el-button>
            <el-button size="small" :disabled="!selectedAsset" @click="openAssetDialog(selectedAsset)">编辑</el-button>
            <el-button size="small" type="danger" plain :disabled="!selectedAsset" @click="handleDeleteAsset">删除</el-button>
          </el-space>
        </div>
        <div class="resource-asset-pane__body">
          <el-empty v-if="!selectedArticle" description="请选择文章/课时" />
          <template v-else>
            <el-tabs v-model="activeAssetType" class="asset-tabs">
              <el-tab-pane v-for="item in assetTypeOptions" :key="item.value" :label="item.label" :name="item.value" />
            </el-tabs>
            <div class="asset-list">
              <article
                v-for="item in filteredAssets"
                :key="item.assetId"
                class="asset-card"
                :class="{ 'is-active': selectedAsset?.assetId === item.assetId }"
                @click="selectedAsset = item"
              >
                <div class="asset-card__head">
                  <strong>{{ item.assetName }}</strong>
                  <el-tag size="small" effect="plain">{{ assetTypeLabel(item.assetType) }}</el-tag>
                </div>
                <p>{{ item.summary || '暂无资源说明' }}</p>
                <div class="asset-card__meta">
                  <span>浏览 {{ item.viewCount || 0 }}</span>
                  <span>下载 {{ item.downloadCount || 0 }}</span>
                  <span>收藏 {{ item.favoriteCount || 0 }}</span>
                </div>
              </article>
              <el-empty v-if="!filteredAssets.length" description="当前类型下暂无资源资产" />
            </div>

            <div class="comment-panel">
              <div class="comment-panel__title">互动概览</div>
              <div class="comment-panel__summary">
                <span>评分 {{ selectedArticle.avgRating || 0 }} / 5</span>
                <span>评论 {{ comments.length }}</span>
                <span>分享 {{ selectedArticle.shareCount || 0 }}</span>
              </div>
              <div class="comment-panel__list">
                <article v-for="item in comments.slice(0, 4)" :key="item.commentId" class="comment-item">
                  <div class="comment-item__head">
                    <strong>{{ item.userName || `用户${item.userId}` }}</strong>
                    <span>{{ item.createTime }}</span>
                  </div>
                  <p>{{ item.content }}</p>
                </article>
                <el-empty v-if="!comments.length" description="暂无评论" :image-size="80" />
              </div>
            </div>
          </template>
        </div>
      </aside>
    </div>

    <el-dialog v-model="nodeDialogVisible" :title="nodeForm.nodeId ? '编辑目录节点' : '新增目录节点'" width="520px">
      <el-form :model="nodeForm" label-width="88px">
        <el-form-item label="所属课程"><el-select v-model="nodeForm.courseId" filterable><el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
        <el-form-item label="父节点">
          <el-tree-select v-model="nodeForm.parentId" :data="treeSelectData" check-strictly clearable :props="{ label: 'nodeName', children: 'children', value: 'nodeId' }" />
        </el-form-item>
        <el-form-item label="节点名称"><el-input v-model="nodeForm.nodeName" placeholder="如：第一单元·识字" /></el-form-item>
        <el-form-item label="排序号"><el-input-number v-model="nodeForm.sortNo" :min="0" style="width: 100%" /></el-form-item>
        <el-form-item label="默认展开"><el-switch v-model="nodeForm.expandDefaultBool" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="nodeForm.remark" type="textarea" rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="nodeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitNode">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="articleDialogVisible" :title="articleForm.resourceId ? '编辑文章/课时' : '新增文章/课时'" width="760px">
      <el-form :model="articleForm" label-width="96px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="文章标题"><el-input v-model="articleForm.resourceName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="作者/主讲"><el-input v-model="articleForm.authorName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="出版社/单位"><el-input v-model="articleForm.publisherName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="适用对象"><el-select v-model="articleForm.targetUserType"><el-option label="学生" value="student" /><el-option label="教师" value="teacher" /><el-option label="家长" value="parent" /><el-option label="全部" value="all" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="质量分"><el-input-number v-model="articleForm.qualityScore" :min="0" :max="100" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="审核状态"><el-select v-model="articleForm.auditStatus"><el-option label="待审" value="0" /><el-option label="通过" value="1" /><el-option label="驳回" value="2" /></el-select></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="摘要"><el-input v-model="articleForm.summary" type="textarea" rows="3" placeholder="概括这篇文章/课时的学习目标与内容亮点" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="正文"><el-input v-model="articleForm.contentText" type="textarea" rows="8" placeholder="可填写导学文案、课程简介、教学说明等" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="articleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitArticle">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="assetDialogVisible" :title="assetForm.assetId ? '编辑资源资产' : '新增资源资产'" width="820px" class="asset-dialog">
      <div class="asset-dialog__context" v-if="selectedArticle">
        <div class="asset-dialog__context-title">{{ selectedArticle.resourceName }}</div>
        <div class="asset-dialog__context-sub">当前文章下新增一个资源资产，资源类型决定学生端右侧切换项。</div>
      </div>
      <el-form :model="assetForm" label-width="96px" class="asset-form">
        <div class="asset-form__section">
          <div class="asset-form__section-title">基础信息</div>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="资源类型">
                <el-select v-model="assetForm.assetType">
                  <el-option v-for="item in assetTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="资源名称">
                <el-input v-model="assetForm.assetName" placeholder="如：我是中国人 · 视频课程" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="文件地址">
                <el-input v-model="assetForm.fileUrl" :placeholder="assetFilePlaceholder">
                  <template #append>
                    <el-button @click="historyDialogVisible = true">历史选择</el-button>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="封面地址">
                <el-input v-model="assetForm.coverUrl" placeholder="可选，适合视频或课件缩略图">
                  <template #append>
                    <el-button @click="historyDialogVisible = true">历史选择</el-button>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="排序号">
                <el-input-number v-model="assetForm.sortNo" :min="0" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="允许下载">
                <el-switch v-model="assetForm.allowDownloadBool" />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <div class="asset-upload-toolbar">
                <el-upload
                  :action="uploadFileUrl"
                  :headers="uploadHeaders"
                  :show-file-list="false"
                  :on-success="handleAssetFileUploadSuccess"
                  :on-error="handleAssetFileUploadError"
                >
                  <el-button type="primary" plain>上传主文件</el-button>
                </el-upload>
                <el-upload
                  :action="uploadFileUrl"
                  :headers="uploadHeaders"
                  :show-file-list="false"
                  :on-success="handleAssetCoverUploadSuccess"
                  :on-error="handleAssetFileUploadError"
                >
                  <el-button plain>上传封面</el-button>
                </el-upload>
                <span class="asset-upload-toolbar__tip">支持直接上传，也可以通过“历史选择”复用之前上传过的文件。</span>
              </div>
            </el-col>
          </el-row>
        </div>

        <div class="asset-form__section">
          <div class="asset-form__section-title">关联业务</div>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="业务类型">
                <el-select v-model="assetForm.bizType" clearable placeholder="没有可不选">
                  <el-option label="学习任务" value="task" />
                  <el-option label="考试试卷" value="paper" />
                  <el-option label="题目" value="question" />
                  <el-option label="外部链接" value="external" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="业务ID">
                <el-input v-model="assetForm.bizId" placeholder="如任务ID、试卷ID、题目ID" />
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div class="asset-form__section">
          <div class="asset-form__section-title">资源说明</div>
          <el-row :gutter="16">
            <el-col :span="24">
              <el-form-item label="摘要">
                <el-input v-model="assetForm.summary" type="textarea" rows="3" placeholder="建议说明这个资源在当前文章中的作用，如导学、讲解、练习、任务提交等" />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="补充内容">
                <el-input v-model="assetForm.contentText" type="textarea" rows="6" placeholder="可补充使用说明、学习建议、任务要求、参考答案说明等" />
              </el-form-item>
            </el-col>
          </el-row>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="assetDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAsset">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="historyDialogVisible" title="选择历史文件" width="900px">
      <div class="history-dialog">
        <div class="history-dialog__toolbar">
          <el-input v-model="historyKeyword" placeholder="按资源名称或文件地址搜索" clearable style="width: 280px" />
          <el-select v-model="historyTypeFilter" clearable placeholder="资源类型" style="width: 180px">
            <el-option v-for="item in assetTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
          <el-button @click="loadAssetHistory">刷新</el-button>
        </div>
        <el-table :data="filteredAssetHistory" height="460" @row-dblclick="applyHistoryAsset">
          <el-table-column label="资源名称" prop="assetName" min-width="220" show-overflow-tooltip />
          <el-table-column label="类型" width="140">
            <template #default="scope">{{ assetTypeLabel(scope.row.assetType) }}</template>
          </el-table-column>
          <el-table-column label="文件地址" prop="fileUrl" min-width="260" show-overflow-tooltip />
          <el-table-column label="封面地址" prop="coverUrl" min-width="220" show-overflow-tooltip />
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="scope">
              <el-button link type="primary" @click="applyHistoryAsset(scope.row)">使用</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <template #footer>
        <el-button @click="historyDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getToken } from '@/utils/auth'
import { fetchCourseOptions } from '@/api/campus/options'
import {
  addResource,
  addResourceAsset,
  addResourceNode,
  delResource,
  delResourceAsset,
  delResourceNode,
  getResourceOperationOverview,
  initResourceTree,
  listResource,
  listResourceAsset,
  listResourceComment,
  listResourceTree,
  updateResource,
  updateResourceAsset,
  updateResourceNode,
} from '@/api/campus/resource'

const assetTypeOptions = [
  { label: '视频课程', value: 'video_course' },
  { label: '课件', value: 'courseware' },
  { label: '教学设计', value: 'teaching_design' },
  { label: '学习任务单', value: 'task_sheet' },
  { label: '课后练习', value: 'after_class_exercise' },
]

const courseOptions = ref<any[]>([])
const courseId = ref<number | undefined>()
const overview = ref<any>({})
const treeData = ref<any[]>([])
const selectedNode = ref<any>(null)
const articleList = ref<any[]>([])
const selectedArticle = ref<any>(null)
const assetList = ref<any[]>([])
const selectedAsset = ref<any>(null)
const comments = ref<any[]>([])
const activeAssetType = ref('video_course')

const nodeDialogVisible = ref(false)
const articleDialogVisible = ref(false)
const assetDialogVisible = ref(false)
const historyDialogVisible = ref(false)

const nodeForm = ref<any>({})
const articleForm = ref<any>({})
const assetForm = ref<any>({})
const assetHistory = ref<any[]>([])
const historyKeyword = ref('')
const historyTypeFilter = ref('')

const treeSelectData = computed(() => [{ nodeId: 0, nodeName: '作为根节点', children: treeData.value }])
const filteredAssets = computed(() => assetList.value.filter((item: any) => item.assetType === activeAssetType.value))
const filteredAssetHistory = computed(() => {
  let data = [...assetHistory.value]
  const keyword = historyKeyword.value.trim()
  if (keyword) {
    data = data.filter((item: any) =>
      String(item.assetName || '').includes(keyword) ||
      String(item.fileUrl || '').includes(keyword) ||
      String(item.coverUrl || '').includes(keyword),
    )
  }
  if (historyTypeFilter.value) {
    data = data.filter((item: any) => item.assetType === historyTypeFilter.value)
  }
  return data
})
const assetFilePlaceholder = computed(() => {
  const map: Record<string, string> = {
    video_course: '填写视频地址或媒体文件链接',
    courseware: '填写课件、文档或下载地址',
    teaching_design: '填写教学设计文档地址',
    task_sheet: '填写任务单文件或在线文档地址',
    after_class_exercise: '可填写练习文件地址，或直接关联试卷/题目业务',
  }
  return map[assetForm.value.assetType] || '填写资源地址'
})
const uploadFileUrl = `${import.meta.env.VITE_APP_BASE_API}/common/upload`
const uploadHeaders = { Authorization: `Bearer ${getToken()}` }

function assetTypeLabel(value: string) {
  return assetTypeOptions.find((item) => item.value === value)?.label || value || '--'
}

function resetNodeForm() {
  nodeForm.value = {
    nodeId: undefined,
    courseId: courseId.value,
    parentId: selectedNode.value?.nodeId || 0,
    nodeName: '',
    sortNo: 0,
    expandDefaultBool: true,
    remark: '',
  }
}

function resetArticleForm() {
  articleForm.value = {
    resourceId: undefined,
    resourceName: '',
    resourceType: 'article',
    courseId: courseId.value,
    nodeId: selectedNode.value?.nodeId,
    authorName: '',
    publisherName: '',
    targetUserType: 'student',
    qualityScore: 80,
    auditStatus: '1',
    status: '0',
    summary: '',
    contentText: '',
  }
}

function resetAssetForm() {
  assetForm.value = {
    assetId: undefined,
    resourceId: selectedArticle.value?.resourceId,
    assetType: activeAssetType.value,
    assetName: '',
    fileUrl: '',
    coverUrl: '',
    summary: '',
    contentText: '',
    bizType: '',
    bizId: '',
    sortNo: 0,
    allowDownloadBool: true,
    status: '0',
  }
}

async function loadOptions() {
  courseOptions.value = await fetchCourseOptions()
  await loadAssetHistory()
}

async function loadOverview() {
  if (!courseId.value) return
  const res = await getResourceOperationOverview({ courseId: courseId.value, limit: 5 })
  overview.value = res.data || {}
}

async function loadTree() {
  if (!courseId.value) {
    treeData.value = []
    return
  }
  const res = await listResourceTree(courseId.value)
  treeData.value = res.data || []
}

async function loadArticles() {
  if (!courseId.value || !selectedNode.value?.nodeId) {
    articleList.value = []
    selectedArticle.value = null
    return
  }
  const res = await listResource({
    pageNum: 1,
    pageSize: 200,
    courseId: courseId.value,
    nodeId: selectedNode.value.nodeId,
    status: '0',
  })
  articleList.value = res.rows || []
  if (articleList.value.length) {
    selectArticle(articleList.value[0])
  } else {
    selectedArticle.value = null
    assetList.value = []
    comments.value = []
  }
}

async function loadAssets() {
  if (!selectedArticle.value?.resourceId) {
    assetList.value = []
    return
  }
  const res = await listResourceAsset({ resourceId: selectedArticle.value.resourceId, status: '0' })
  assetList.value = res.data || []
}

async function loadComments() {
  if (!selectedArticle.value?.resourceId) {
    comments.value = []
    return
  }
  const res = await listResourceComment(selectedArticle.value.resourceId)
  comments.value = res.data || []
}

async function handleCourseChange() {
  selectedNode.value = null
  selectedArticle.value = null
  selectedAsset.value = null
  await Promise.all([loadOverview(), loadTree()])
}

function handleNodeSelect(data: any) {
  selectedNode.value = data
  selectedArticle.value = null
  selectedAsset.value = null
  loadArticles()
}

function selectArticle(article: any) {
  selectedArticle.value = article
  selectedAsset.value = null
  loadAssets()
  loadComments()
}

async function handleInitTree() {
  if (!courseId.value) return
  await initResourceTree(courseId.value)
  ElMessage.success('已初始化课程根目录')
  await loadTree()
}

function openNodeDialog(row?: any) {
  if (row) {
    nodeForm.value = {
      ...row,
      expandDefaultBool: row.expandDefault !== '0',
    }
  } else {
    resetNodeForm()
  }
  nodeDialogVisible.value = true
}

async function submitNode() {
  const payload = {
    ...nodeForm.value,
    courseId: nodeForm.value.courseId || courseId.value,
    expandDefault: nodeForm.value.expandDefaultBool ? '1' : '0',
    status: '0',
  }
  if (payload.nodeId) await updateResourceNode(payload)
  else await addResourceNode(payload)
  ElMessage.success('节点保存成功')
  nodeDialogVisible.value = false
  await loadTree()
}

async function handleDeleteNode() {
  if (!selectedNode.value?.nodeId) return
  await ElMessageBox.confirm(`确认删除节点「${selectedNode.value.nodeName}」吗？`, '提示', { type: 'warning' })
  await delResourceNode(selectedNode.value.nodeId)
  ElMessage.success('节点删除成功')
  selectedNode.value = null
  await loadTree()
  articleList.value = []
}

function openArticleDialog(row?: any) {
  if (row) articleForm.value = { ...row }
  else resetArticleForm()
  articleDialogVisible.value = true
}

async function submitArticle() {
  const payload = {
    ...articleForm.value,
    courseId: articleForm.value.courseId || courseId.value,
    nodeId: articleForm.value.nodeId || selectedNode.value?.nodeId,
    status: '0',
  }
  if (payload.resourceId) await updateResource(payload)
  else await addResource(payload)
  ElMessage.success('文章保存成功')
  articleDialogVisible.value = false
  await loadArticles()
  await loadOverview()
}

async function handleDeleteArticle() {
  if (!selectedArticle.value?.resourceId) return
  await ElMessageBox.confirm(`确认删除文章「${selectedArticle.value.resourceName}」吗？`, '提示', { type: 'warning' })
  await delResource(selectedArticle.value.resourceId)
  ElMessage.success('文章删除成功')
  selectedArticle.value = null
  await loadArticles()
  await loadOverview()
}

function openAssetDialog(row?: any) {
  if (row) {
    assetForm.value = {
      ...row,
      allowDownloadBool: row.allowDownload !== '0',
    }
  } else {
    resetAssetForm()
  }
  assetDialogVisible.value = true
}

async function loadAssetHistory() {
  const res = await listResourceAsset({ status: '0' })
  assetHistory.value = res.data || []
}

function applyHistoryAsset(row: any) {
  assetForm.value.fileUrl = row.fileUrl || ''
  if (!assetForm.value.coverUrl) {
    assetForm.value.coverUrl = row.coverUrl || ''
  }
  if (!assetForm.value.summary) {
    assetForm.value.summary = row.summary || ''
  }
  ElMessage.success('已填充历史文件资源')
  historyDialogVisible.value = false
}

function handleAssetFileUploadSuccess(res: any) {
  if (res?.code === 200 && res?.fileName) {
    assetForm.value.fileUrl = res.fileName
    ElMessage.success('文件上传成功')
    void loadAssetHistory()
    return
  }
  ElMessage.error(res?.msg || '文件上传失败')
}

function handleAssetCoverUploadSuccess(res: any) {
  if (res?.code === 200 && res?.fileName) {
    assetForm.value.coverUrl = res.fileName
    ElMessage.success('封面上传成功')
    void loadAssetHistory()
    return
  }
  ElMessage.error(res?.msg || '封面上传失败')
}

function handleAssetFileUploadError() {
  ElMessage.error('上传失败，请稍后重试')
}

async function submitAsset() {
  const payload = {
    ...assetForm.value,
    resourceId: assetForm.value.resourceId || selectedArticle.value?.resourceId,
    allowDownload: assetForm.value.allowDownloadBool ? '1' : '0',
    status: '0',
  }
  if (payload.assetId) await updateResourceAsset(payload)
  else await addResourceAsset(payload)
  ElMessage.success('资源资产保存成功')
  assetDialogVisible.value = false
  await loadAssets()
}

async function handleDeleteAsset() {
  if (!selectedAsset.value?.assetId) return
  await ElMessageBox.confirm(`确认删除资源「${selectedAsset.value.assetName}」吗？`, '提示', { type: 'warning' })
  await delResourceAsset(selectedAsset.value.assetId)
  ElMessage.success('资源删除成功')
  selectedAsset.value = null
  await loadAssets()
}

onMounted(loadOptions)
</script>

<style scoped>
.resource-studio {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.resource-studio__hero {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  align-items: flex-start;
  padding: 18px 20px;
  border-radius: 16px;
  background: #fff;
  border: 1px solid var(--el-border-color-light);
}
.resource-studio__eyebrow {
  display: inline-flex;
  padding: 3px 10px;
  border-radius: 999px;
  background: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
  font-size: 12px;
  font-weight: 700;
}
.resource-studio__hero h2 {
  margin: 10px 0 6px;
  font-size: 28px;
  font-weight: 700;
  color: var(--el-text-color-primary);
}
.resource-studio__hero p {
  margin: 0;
  max-width: 860px;
  line-height: 1.7;
  color: var(--el-text-color-regular);
}
.resource-studio__actions {
  display: flex;
  gap: 12px;
  align-items: center;
}
.metric-label {
  color: var(--el-text-color-secondary);
  margin-bottom: 10px;
}
.metric-value {
  font-size: 28px;
  font-weight: 800;
  color: var(--el-text-color-primary);
}
.metric-value.warning { color: var(--el-color-warning); }
.metric-value.success { color: var(--el-color-success); }
.resource-studio__board {
  display: grid;
  grid-template-columns: 300px minmax(0, 1.1fr) minmax(360px, 0.9fr);
  gap: 16px;
}
.resource-pane {
  min-height: 720px;
  border-radius: 16px;
  border: 1px solid var(--el-border-color-light);
  background: #ffffff;
  box-shadow: none;
  overflow: hidden;
}
.resource-pane__header {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: flex-start;
  padding: 16px 18px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}
.resource-pane__header strong {
  display: block;
  font-size: 16px;
  color: var(--el-text-color-primary);
}
.resource-pane__header p {
  margin: 6px 0 0;
  font-size: 13px;
  color: var(--el-text-color-secondary);
  line-height: 1.7;
}
.resource-tree-pane__body,
.resource-article-pane__body,
.resource-asset-pane__body {
  padding: 18px;
}
.resource-tree-node {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
}
.resource-article-list,
.asset-list,
.comment-panel__list {
  display: grid;
  gap: 12px;
}
.article-card,
.asset-card,
.comment-item {
  padding: 14px;
  border-radius: 12px;
  border: 1px solid var(--el-border-color-light);
  background: #fff;
  cursor: pointer;
  transition: all .2s ease;
}
.article-card:hover,
.asset-card:hover {
  transform: translateY(-1px);
  border-color: var(--el-color-primary-light-5);
}
.article-card.is-active,
.asset-card.is-active {
  border-color: var(--el-color-primary);
  background: var(--el-color-primary-light-9);
}
.article-card__head,
.asset-card__head,
.comment-item__head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: flex-start;
}
.article-card__head h3 {
  margin: 0;
  font-size: 18px;
  color: var(--el-text-color-primary);
}
.article-card__head p,
.asset-card p,
.comment-item p {
  margin: 8px 0 0;
  color: var(--el-text-color-regular);
  line-height: 1.7;
}
.article-card__meta,
.asset-card__meta,
.comment-panel__summary {
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
  margin-top: 12px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}
.comment-panel {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px dashed var(--el-border-color);
}
.comment-panel__title {
  font-size: 16px;
  font-weight: 700;
  color: var(--el-text-color-primary);
}
.asset-dialog :deep(.el-dialog__body) {
  padding-top: 12px;
}
.asset-dialog__context {
  margin-bottom: 16px;
  padding: 12px 14px;
  border-radius: 10px;
  background: var(--el-fill-color-extra-light);
  border: 1px solid var(--el-border-color-lighter);
}
.asset-dialog__context-title {
  font-size: 14px;
  font-weight: 700;
  color: var(--el-text-color-primary);
}
.asset-dialog__context-sub {
  margin-top: 6px;
  font-size: 12px;
  line-height: 1.7;
  color: var(--el-text-color-secondary);
}
.asset-form {
  display: grid;
  gap: 14px;
}
.asset-form__section {
  padding: 14px 16px;
  border-radius: 12px;
  border: 1px solid var(--el-border-color-lighter);
  background: #fff;
}
.asset-form__section-title {
  margin-bottom: 12px;
  font-size: 14px;
  font-weight: 700;
  color: var(--el-text-color-primary);
}
.asset-upload-toolbar {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
  padding-top: 4px;
}
.asset-upload-toolbar__tip {
  color: var(--el-text-color-secondary);
  font-size: 12px;
}
.history-dialog {
  display: grid;
  gap: 14px;
}
.history-dialog__toolbar {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}
@media (max-width: 1400px) {
  .resource-studio__board {
    grid-template-columns: 280px minmax(0, 1fr);
  }
  .resource-asset-pane {
    grid-column: 1 / -1;
  }
}
@media (max-width: 960px) {
  .resource-studio__hero {
    flex-direction: column;
  }
  .resource-studio__actions {
    width: 100%;
    flex-direction: column;
    align-items: stretch;
  }
  .resource-studio__board {
    grid-template-columns: 1fr;
  }
}
</style>
