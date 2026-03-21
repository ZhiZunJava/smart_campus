<template>
  <div class="app-container">
    <el-form :model="queryParams" :inline="true" v-show="showSearch" class="mb16">
      <el-form-item label="资源名称"><el-input v-model="queryParams.resourceName" placeholder="请输入资源名称" clearable style="width: 220px" @keyup.enter="getList" /></el-form-item>
      <el-form-item label="资源类型"><el-select v-model="queryParams.resourceType" clearable style="width: 180px"><el-option label="视频" value="video" /><el-option label="文档" value="doc" /><el-option label="PPT" value="ppt" /><el-option label="PDF" value="pdf" /><el-option label="题目" value="question" /></el-select></el-form-item>
      <el-form-item label="课程"><el-select v-model="queryParams.courseId" filterable clearable style="width: 240px"><el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item label="知识点"><el-select v-model="queryParams.knowledgePointId" filterable clearable multiple collapse-tags collapse-tags-tooltip style="width: 260px"><el-option v-for="item in filteredKnowledgePointOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item><el-button type="primary" icon="Search" @click="getList">搜索</el-button><el-button icon="Refresh" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb16">
      <el-col :span="1.5"><el-button type="primary" plain icon="Plus" @click="handleAdd">新增</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()">修改</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()">删除</el-button></el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <div class="resource-toolbar">
      <el-space wrap>
        <el-tag
          v-for="item in typeFilterOptions"
          :key="item.value"
          :type="activeTypeFilter === item.value ? 'primary' : 'info'"
          :effect="activeTypeFilter === item.value ? 'dark' : 'plain'"
          class="resource-filter-tag"
          @click="handleTypeFilter(item.value)"
        >
          {{ item.label }}
        </el-tag>
      </el-space>
      <el-radio-group v-model="viewMode" size="default">
        <el-radio-button label="list">列表视图</el-radio-button>
        <el-radio-button label="card">卡片视图</el-radio-button>
      </el-radio-group>
    </div>

    <el-row :gutter="16" class="mb16" v-if="overview">
      <el-col :span="6"><el-card shadow="never"><div class="metric-label">资源总数</div><div class="metric-value">{{ overview.totalResourceCount || 0 }}</div></el-card></el-col>
      <el-col :span="6"><el-card shadow="never"><div class="metric-label">待审资源</div><div class="metric-value warning">{{ overview.pendingAuditCount || 0 }}</div></el-card></el-col>
      <el-col :span="6"><el-card shadow="never"><div class="metric-label">平均质量分</div><div class="metric-value">{{ overview.avgQualityScore || 0 }}</div></el-card></el-col>
      <el-col :span="6"><el-card shadow="never"><div class="metric-label">高潜资源</div><div class="metric-value success">{{ overview.highPotentialCount || 0 }}</div></el-card></el-col>
    </el-row>

    <el-row :gutter="16" class="mb16" v-if="overview">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header><span>热门资源 Top5</span></template>
          <div v-if="overview.hottestResources?.length">
            <div v-for="item in overview.hottestResources" :key="item.resourceId" class="insight-item">
              <div class="insight-item__title">{{ item.resourceName }}</div>
              <div class="insight-item__meta">热度 {{ item.heatScore }} · 互动 {{ item.interactionCount }} · {{ item.resourceType }}</div>
            </div>
          </div>
          <el-empty v-else description="暂无数据" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <template #header><span>推荐潜力 Top5</span></template>
          <div v-if="overview.recommendedResources?.length">
            <div v-for="item in overview.recommendedResources" :key="item.resourceId" class="insight-item">
              <div class="insight-item__title">{{ item.resourceName }}</div>
              <div class="insight-item__meta">推荐分 {{ item.recommendationScore }} · {{ item.recommendationReason }}</div>
            </div>
          </div>
          <el-empty v-else description="暂无数据" />
        </el-card>
      </el-col>
    </el-row>

    <el-table v-if="viewMode === 'list'" v-loading="loading" :data="resourceList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column label="资源ID" prop="resourceId" width="90" />
      <el-table-column label="资源名称" prop="resourceName" min-width="180" show-overflow-tooltip />
      <el-table-column label="类型" prop="resourceType" width="100" />
      <el-table-column label="课程ID" prop="courseId" width="90" />
      <el-table-column label="知识点" min-width="220">
        <template #default="scope">
          <div class="knowledge-tags">
            <el-tag v-for="item in knowledgePointNames(scope.row)" :key="item" size="small" effect="plain" type="success">{{ item }}</el-tag>
            <span v-if="!knowledgePointNames(scope.row).length">--</span>
          </div>
        </template>
      </el-table-column>
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
      <el-table-column label="热度" width="90">
        <template #default="scope">
          {{ calcHeatScore(scope.row) }}
        </template>
      </el-table-column>
      <el-table-column label="推荐潜力" width="100">
        <template #default="scope">
          {{ calcRecommendPotential(scope.row) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="210" fixed="right">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleView(scope.row)">详情</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div v-else v-loading="loading" class="resource-card-grid">
      <el-card v-for="item in resourceList" :key="item.resourceId" shadow="hover" class="resource-card">
        <div class="resource-card__head">
          <div>
            <div class="resource-card__title">{{ item.resourceName }}</div>
            <div class="resource-card__meta">{{ item.resourceType }} · {{ courseLabel(item.courseId) }}</div>
          </div>
          <el-tag :type="item.auditStatus === '1' ? 'success' : item.auditStatus === '2' ? 'danger' : 'warning'" effect="plain">
            {{ item.auditStatus === '1' ? '通过' : item.auditStatus === '2' ? '驳回' : '待审' }}
          </el-tag>
        </div>
        <div class="resource-card__summary">{{ item.summary || '暂无摘要，建议补充资源简介。' }}</div>
        <div class="resource-card__tags">
          <el-tag v-for="tag in knowledgePointNames(item).slice(0, 4)" :key="tag" size="small" effect="plain" type="success">{{ tag }}</el-tag>
        </div>
        <div class="resource-card__stats">
          <span>质量 {{ item.qualityScore || 0 }}</span>
          <span>热度 {{ calcHeatScore(item) }}</span>
          <span>推荐 {{ calcRecommendPotential(item) }}</span>
        </div>
        <div class="resource-card__actions">
          <el-button link type="primary" @click="handleView(item)">详情</el-button>
          <el-button link type="primary" @click="handleUpdate(item)">修改</el-button>
          <el-button link type="danger" @click="handleDelete(item)">删除</el-button>
        </div>
      </el-card>
      <el-empty v-if="!resourceList.length" description="暂无资源数据" />
    </div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog v-model="open" :title="title" width="980px" top="5vh">
      <div class="resource-editor">
        <div class="resource-editor__sidebar">
          <div class="resource-editor__mode-card">
            <div class="resource-editor__type-title">编辑模式</div>
            <el-radio-group v-model="editorMode" class="resource-editor__mode-group">
              <el-radio-button label="quick">快速入库</el-radio-button>
              <el-radio-button label="full">完整配置</el-radio-button>
            </el-radio-group>
            <p class="resource-editor__type-tip">
              {{ editorMode === 'quick' ? '先完成标题、类型、课程、知识点、正文和主文件即可快速入库。' : '完整模式适合补充运营、素材和类型专属配置。' }}
            </p>
          </div>
          <div class="resource-editor__type-card">
            <div class="resource-editor__type-title">资源类型</div>
            <el-select v-model="form.resourceType" style="width:100%">
              <el-option label="视频" value="video" />
              <el-option label="文档" value="doc" />
              <el-option label="PPT" value="ppt" />
              <el-option label="PDF" value="pdf" />
              <el-option label="题目" value="question" />
            </el-select>
            <p class="resource-editor__type-tip">{{ typeTip }}</p>
          </div>
          <div class="resource-editor__score-card">
            <div class="resource-editor__score-title">推荐潜力预估</div>
            <div class="resource-editor__score-value">{{ calcRecommendPotential(form) }}</div>
            <div class="resource-editor__score-sub">质量 {{ form.qualityScore || 0 }} · 审核 {{ form.auditStatus === '1' ? '通过' : form.auditStatus === '2' ? '驳回' : '待审' }}</div>
          </div>
          <div class="resource-editor__guide-card">
            <div class="resource-editor__score-title">{{ resourceTypeMeta.title }}</div>
            <div class="resource-editor__guide-desc">{{ resourceTypeMeta.desc }}</div>
            <div class="resource-editor__guide-tags">
              <el-tag v-for="item in resourceTypeMeta.focus" :key="item" size="small" effect="plain">{{ item }}</el-tag>
            </div>
          </div>
        </div>

        <div class="resource-editor__main">
          <el-form :model="form" label-width="100px">
            <div v-if="editorMode === 'quick'" class="quick-editor">
              <el-steps :active="quickStep" finish-status="success" simple class="quick-editor__steps">
                <el-step title="资源基础" />
                <el-step title="内容文件" />
                <el-step title="智能完善" />
              </el-steps>
              <el-alert type="info" :closable="false" title="快速入库模式下，建议先录入核心信息，后续再补充高级配置和运营数据。" />
              <el-row v-if="quickStep === 0" :gutter="16" class="mt16">
                <el-col :span="12"><el-form-item label="资源名称"><el-input v-model="form.resourceName" placeholder="请输入资源标题" /></el-form-item></el-col>
                <el-col :span="12"><el-form-item label="资源类型"><el-select v-model="form.resourceType"><el-option label="视频" value="video" /><el-option label="文档" value="doc" /><el-option label="PPT" value="ppt" /><el-option label="PDF" value="pdf" /><el-option label="题目" value="question" /></el-select></el-form-item></el-col>
                <el-col :span="12"><el-form-item label="课程"><el-select v-model="form.courseId" filterable clearable><el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
                <el-col :span="12"><el-form-item label="上传者"><el-select v-model="form.uploaderId" filterable clearable><el-option v-for="item in userOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
                <el-col :span="24"><el-form-item label="知识点标签"><el-select v-model="form.knowledgePointIds" filterable clearable multiple collapse-tags collapse-tags-tooltip style="width:100%"><el-option v-for="item in currentKnowledgePointOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
                <el-col :span="24"><el-form-item label="摘要"><el-input v-model="form.summary" type="textarea" rows="3" placeholder="简要说明资源内容、适用对象和使用场景" /></el-form-item></el-col>
              </el-row>
              <el-row v-else-if="quickStep === 1" :gutter="16" class="mt16">
                <el-col :span="12"><el-form-item label="封面图片"><image-upload v-model="form.coverUrl" :limit="1" :file-size="8" /></el-form-item></el-col>
                <el-col :span="12"><el-form-item label="主文件"><file-upload v-model="form.fileUrl" :limit="1" :file-type="uploadFileTypes" :file-size="videoFileSize" /></el-form-item></el-col>
                <el-col :span="24"><el-form-item label="正文内容"><editor v-model="form.contentText" :min-height="220" /></el-form-item></el-col>
              </el-row>
              <el-row v-else :gutter="16" class="mt16">
                <el-col :span="24">
                  <el-alert v-if="aiStatusText" type="info" :closable="false" :title="aiStatusText" class="mb16" />
                  <el-form-item label="关键词"><el-input v-model="form.keywordsText" placeholder="自动提取或手动补充" /></el-form-item>
                  <el-form-item label="学习建议"><el-input v-model="form.learningAdvice" type="textarea" rows="4" placeholder="可由规则或AI自动补全" /></el-form-item>
                  <el-form-item label="AI 建议"><el-input v-model="form.aiInsights" type="textarea" rows="5" placeholder="这里会保留 AI 输出的教学建议和资源适配判断" /></el-form-item>
                </el-col>
              </el-row>
              <div class="quick-editor__actions">
                <el-button v-if="quickStep > 0" @click="quickStep--">上一步</el-button>
                <el-button v-if="quickStep < 2" type="primary" @click="quickStep++">下一步</el-button>
                <el-button v-if="quickStep === 2" type="primary" plain icon="MagicStick" @click="autoFillMetadata">规则填充</el-button>
                <el-button v-if="quickStep === 2" type="success" plain :loading="aiLoading" icon="Cpu" @click="analyzeByAi">AI 分析填充</el-button>
                <el-button link type="primary" @click="editorMode = 'full'">切换到完整配置</el-button>
              </div>
            </div>

            <el-tabs v-else v-model="activeEditorTab" class="resource-editor__tabs">
              <el-tab-pane label="基础信息" name="base">
                <el-row :gutter="16">
                  <el-col :span="12"><el-form-item label="资源名称"><el-input v-model="form.resourceName" placeholder="请输入资源标题" /></el-form-item></el-col>
                  <el-col :span="12"><el-form-item label="课程"><el-select v-model="form.courseId" filterable clearable><el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
                  <el-col :span="12"><el-form-item label="上传者"><el-select v-model="form.uploaderId" filterable clearable><el-option v-for="item in userOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
                  <el-col :span="12"><el-form-item label="封面图"><image-upload v-model="form.coverUrl" :limit="1" :file-size="8" /></el-form-item></el-col>
                  <el-col :span="24"><el-form-item label="知识点标签"><el-select v-model="form.knowledgePointIds" filterable clearable multiple collapse-tags collapse-tags-tooltip style="width:100%" placeholder="按课程选择关联知识点"><el-option v-for="item in currentKnowledgePointOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
                  <el-col :span="12"><el-form-item label="质量分"><el-input-number v-model="form.qualityScore" :min="0" :max="100" style="width:100%" /></el-form-item></el-col>
                  <el-col :span="24"><el-form-item label="摘要"><el-input v-model="form.summary" type="textarea" rows="3" placeholder="面向推荐、检索和画像解释的摘要说明" /></el-form-item></el-col>
                </el-row>
              </el-tab-pane>

              <el-tab-pane label="内容配置" name="content">
                <el-row :gutter="16">
                  <el-col :span="24"><el-form-item label="主文件"><file-upload v-model="form.fileUrl" :limit="1" :file-type="uploadFileTypes" :file-size="videoFileSize" /></el-form-item></el-col>
                  <el-col :span="24"><el-form-item label="正文内容"><editor v-model="form.contentText" :min-height="260" /></el-form-item></el-col>
                </el-row>
              </el-tab-pane>

              <el-tab-pane label="类型专属" name="type">
                <el-row :gutter="16" v-if="form.resourceType === 'video'">
                  <el-col :span="12"><el-form-item label="视频时长"><el-input v-model="form.videoDuration" placeholder="如 18:30 / 1110秒" /></el-form-item></el-col>
                  <el-col :span="12"><el-form-item label="清晰度"><el-select v-model="form.videoResolution"><el-option label="标清" value="sd" /><el-option label="高清" value="hd" /><el-option label="超清" value="uhd" /></el-select></el-form-item></el-col>
                  <el-col :span="12"><el-form-item label="封面帧"><el-input v-model="form.posterTime" placeholder="如 00:00:03" /></el-form-item></el-col>
                  <el-col :span="12"><el-form-item label="字幕语言"><el-input v-model="form.captionLanguage" placeholder="如 zh-CN" /></el-form-item></el-col>
                  <el-col :span="24"><el-alert type="info" :closable="false" title="视频资源建议补充封面、时长和阶段标签，推荐效果会更好。" />
                  </el-col>
                </el-row>

                <el-row :gutter="16" v-else-if="['doc', 'pdf', 'ppt'].includes(form.resourceType)">
                  <el-col :span="12"><el-form-item label="页数/页码"><el-input v-model="form.pageCount" placeholder="如 32 页" /></el-form-item></el-col>
                  <el-col :span="12"><el-form-item label="阅读难度"><el-select v-model="form.readingLevel"><el-option label="基础" value="basic" /><el-option label="进阶" value="intermediate" /><el-option label="拓展" value="advanced" /></el-select></el-form-item></el-col>
                  <el-col :span="12"><el-form-item label="适读时长"><el-input v-model="form.readingMinutes" placeholder="如 15 分钟" /></el-form-item></el-col>
                  <el-col :span="12"><el-form-item label="可打印"><el-switch v-model="form.printable" /></el-form-item></el-col>
                  <el-col :span="24"><el-form-item label="重点提要"><el-input v-model="form.keyPoints" type="textarea" rows="4" placeholder="可填写知识点、适用场景、阅读建议等" /></el-form-item></el-col>
                </el-row>

                <el-row :gutter="16" v-else-if="form.resourceType === 'question'">
                  <el-col :span="12"><el-form-item label="题型"><el-select v-model="form.questionType"><el-option label="单选" value="single" /><el-option label="多选" value="multiple" /><el-option label="判断" value="judge" /><el-option label="简答" value="short" /></el-select></el-form-item></el-col>
                  <el-col :span="12"><el-form-item label="难度"><el-select v-model="form.difficultyLevel"><el-option label="基础" :value="1" /><el-option label="中等" :value="2" /><el-option label="较难" :value="3" /></el-select></el-form-item></el-col>
                  <el-col :span="24"><el-form-item label="题干"><el-input v-model="form.questionStem" type="textarea" rows="4" placeholder="请输入题干内容" /></el-form-item></el-col>
                  <el-col :span="12"><el-form-item label="标准答案"><el-input v-model="form.questionAnswer" /></el-form-item></el-col>
                  <el-col :span="12"><el-form-item label="解析"><el-input v-model="form.questionAnalysis" /></el-form-item></el-col>
                </el-row>
              </el-tab-pane>

              <el-tab-pane label="智能填充" name="ai">
                <div class="ai-tools">
                  <el-button type="primary" plain icon="MagicStick" @click="autoFillMetadata">自动分析元数据</el-button>
                  <el-button plain icon="Document" @click="generateSummary">生成摘要</el-button>
                  <el-button plain icon="CollectionTag" @click="generateKeywords">提取关键词</el-button>
                  <el-button type="success" plain :loading="aiLoading" icon="Cpu" @click="analyzeByAi">AI 深度分析</el-button>
                </div>
                <el-alert v-if="aiStatusText" type="info" :closable="false" :title="aiStatusText" class="mb16" />
                <el-alert type="success" :closable="false" title="根据标题、正文、资源类型和知识点自动补全摘要、关键词、阅读/学习建议。" />
                <el-form label-width="100px" class="mt16">
                  <el-form-item label="关键词"><el-input v-model="form.keywordsText" placeholder="自动分析后会填充，如：微积分, 极限, 导数" /></el-form-item>
                  <el-form-item label="学习建议"><el-input v-model="form.learningAdvice" type="textarea" rows="4" placeholder="自动分析后生成，便于推荐解释和教师查看" /></el-form-item>
                  <el-form-item label="AI 建议"><el-input v-model="form.aiInsights" type="textarea" rows="5" placeholder="这里会保留 AI 输出的教学建议、适配场景与内容质量判断" /></el-form-item>
                </el-form>
              </el-tab-pane>

              <el-tab-pane label="发布配置" name="publish">
                <el-alert type="info" :closable="false" title="默认只需要完成基础信息、正文内容和知识点标签。以下运营字段适合管理员在资源成熟后再补充。" />
                <el-button class="mt16" link type="primary" @click="showAdvancedOps = !showAdvancedOps">
                  {{ showAdvancedOps ? '收起高级运营配置' : '展开高级运营配置' }}
                </el-button>
                <el-row v-if="showAdvancedOps" :gutter="16" class="mt16">
                  <el-col :span="12"><el-form-item label="审核状态"><el-select v-model="form.auditStatus"><el-option label="待审" value="0" /><el-option label="通过" value="1" /><el-option label="驳回" value="2" /></el-select></el-form-item></el-col>
                  <el-col :span="12"><el-form-item label="状态"><el-select v-model="form.status"><el-option label="启用" value="0" /><el-option label="停用" value="1" /></el-select></el-form-item></el-col>
                  <el-col :span="8"><el-form-item label="浏览数"><el-input-number v-model="form.viewCount" :min="0" style="width:100%" /></el-form-item></el-col>
                  <el-col :span="8"><el-form-item label="下载数"><el-input-number v-model="form.downloadCount" :min="0" style="width:100%" /></el-form-item></el-col>
                  <el-col :span="8"><el-form-item label="收藏数"><el-input-number v-model="form.favoriteCount" :min="0" style="width:100%" /></el-form-item></el-col>
                </el-row>
              </el-tab-pane>
            </el-tabs>
          </el-form>
        </div>
      </div>
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
        <el-descriptions-item label="知识点" :span="2">
          <div class="knowledge-tags">
            <el-tag v-for="item in knowledgePointNames(detail)" :key="item" size="small" effect="plain" type="success">{{ item }}</el-tag>
            <span v-if="!knowledgePointNames(detail).length">--</span>
          </div>
        </el-descriptions-item>
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
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addResource, delResource, getResource, getResourceOperationOverview, listResource, updateResource } from '@/api/campus/resource'
import { fetchCourseOptions, fetchUserOptions } from '@/api/campus/options'
import { listKnowledgePoint } from '@/api/campus/teaching'
import { chatAiModel } from '@/api/campus/ai'

const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const resourceList = ref<any[]>([])
const open = ref(false)
const detailOpen = ref(false)
const title = ref('')
const detail = ref<any>({})
const overview = ref<any>(null)
const activeEditorTab = ref('base')
const editorMode = ref('quick')
const quickStep = ref(0)
const showAdvancedOps = ref(false)
const aiLoading = ref(false)
const aiStatusText = ref('')
const ids = ref<any[]>([])
const single = ref(true)
const multiple = ref(true)
const courseOptions = ref<any[]>([])
const userOptions = ref<any[]>([])
const knowledgePointOptions = ref<any[]>([])
const viewMode = ref('list')
const activeTypeFilter = ref('all')
const queryParams = reactive<any>({ pageNum: 1, pageSize: 10, resourceName: '', resourceType: '', courseId: undefined, knowledgePointId: [] })
const form = reactive<any>({})
const typeFilterOptions = [
  { label: '全部', value: 'all' },
  { label: '视频', value: 'video' },
  { label: '文档', value: 'doc' },
  { label: 'PPT', value: 'ppt' },
  { label: 'PDF', value: 'pdf' },
  { label: '题目', value: 'question' },
]

const typeTip = computed(() => {
  const tips: Record<string, string> = {
    video: '适合首轮学习、进度追赶和教学讲解。',
    doc: '适合讲义、学习资料和知识梳理。',
    ppt: '适合课件演示、课堂展示和结构化讲授。',
    pdf: '适合制度文档、标准资料和打印阅读。',
    question: '适合练习、测验和错题巩固。',
  }
  return tips[form.resourceType] || '请选择合适的资源类型。'
})

const fileUrlPlaceholder = computed(() => {
  if (form.resourceType === 'video') return '请输入视频播放地址或点播地址'
  if (form.resourceType === 'question') return '可填写题库来源、题目链接或留空'
  return '请输入文档/PDF/PPT 文件地址'
})

const contentPlaceholder = computed(() => {
  if (form.resourceType === 'question') return '可填写题目内容、选项、答案说明或解析正文'
  if (form.resourceType === 'video') return '可填写字幕摘要、讲解提纲、适合学习阶段'
  return '可填写正文摘要、知识点、目录说明、适用对象'
})

const currentKnowledgePointOptions = computed(() => knowledgePointOptions.value.filter((item: any) => !form.courseId || item.courseId === form.courseId))
const filteredKnowledgePointOptions = computed(() => knowledgePointOptions.value.filter((item: any) => !queryParams.courseId || item.courseId === queryParams.courseId))
const uploadFileTypes = computed(() => form.resourceType === 'video'
  ? ['mp4', 'mov', 'avi', 'm3u8']
  : form.resourceType === 'question'
    ? ['txt', 'doc', 'docx', 'pdf', 'json']
    : ['doc', 'docx', 'ppt', 'pptx', 'pdf', 'txt'])
const videoFileSize = computed(() => form.resourceType === 'video' ? 500 : 100)

const resourceTypeMeta = computed(() => {
  const map: Record<string, { title: string; desc: string; focus: string[] }> = {
    video: { title: '视频资源', desc: '适合讲解、演示、导学和重点串讲。', focus: ['封面清晰', '时长合理', '适配知识点'] },
    doc: { title: '文档资源', desc: '适合讲义、阅读材料、制度说明。', focus: ['摘要明确', '正文结构清晰', '便于检索'] },
    ppt: { title: '课件资源', desc: '适合课堂展示、汇报和结构化讲授。', focus: ['封面统一', '重点提要完整', '可快速预览'] },
    pdf: { title: 'PDF资源', desc: '适合标准资料、论文、试卷和归档文档。', focus: ['页数信息', '阅读建议', '知识点绑定'] },
    question: { title: '题目资源', desc: '适合练习、测验、作业和错题巩固。', focus: ['题干清楚', '答案明确', '解析可理解'] },
  }
  return map[form.resourceType] || { title: '教学资源', desc: '请先选择合适的资源类型。', focus: [] }
})

function resetFormData() { Object.assign(form, { resourceId: undefined, resourceName: '', resourceType: 'video', courseId: undefined, uploaderId: undefined, fileUrl: '', coverUrl: '', summary: '', contentText: '', remark: '', auditStatus: '1', qualityScore: 80, status: '0', viewCount: 0, downloadCount: 0, favoriteCount: 0, knowledgePointIds: [], keywordsText: '', learningAdvice: '', aiInsights: '', videoDuration: '', videoResolution: 'hd', posterTime: '', captionLanguage: 'zh-CN', pageCount: '', readingLevel: 'basic', readingMinutes: '', printable: true, keyPoints: '', questionType: 'single', difficultyLevel: 1, questionStem: '', questionAnswer: '', questionAnalysis: '' }); activeEditorTab.value = 'base'; aiStatusText.value = ''; showAdvancedOps.value = false; editorMode.value = 'quick'; quickStep.value = 0 }
async function getList() { loading.value = true; const [res, overviewRes] = await Promise.all([listResource(queryParams), getResourceOperationOverview({ courseId: queryParams.courseId, limit: 5 })]); resourceList.value = (res.rows || []).filter((item: any) => { if (!queryParams.knowledgePointId?.length) return true; const meta = parseExtendedMeta(item.remark); const ids = meta.knowledgePointIds || []; return queryParams.knowledgePointId.every((value: any) => ids.includes(value)) }); total.value = queryParams.knowledgePointId?.length ? resourceList.value.length : (res.total || 0); overview.value = overviewRes.data || null; loading.value = false }
function resetQuery() { queryParams.pageNum = 1; queryParams.resourceName = ''; queryParams.resourceType = ''; queryParams.courseId = undefined; queryParams.knowledgePointId = []; getList() }
function handleTypeFilter(value: string) { activeTypeFilter.value = value; queryParams.resourceType = value === 'all' ? '' : value; queryParams.pageNum = 1; getList() }
function handleSelectionChange(selection: any[]) { ids.value = selection.map(i => i.resourceId); single.value = selection.length !== 1; multiple.value = !selection.length }
function handleAdd() { resetFormData(); title.value = '新增资源'; open.value = true }
async function handleUpdate(row?: any) { resetFormData(); const resourceId = row?.resourceId || ids.value[0]; const res = await getResource(resourceId); const data = res.data || {}; Object.assign(form, data, parseExtendedMeta(data.remark)); title.value = '修改资源'; open.value = true }
function parseExtendedMeta(remark: string) {
  if (!remark) return {}
  try {
    return JSON.parse(remark)
  } catch {
    return {}
  }
}
function buildExtendedMeta() {
  return JSON.stringify({
    knowledgePointIds: form.knowledgePointIds || [],
    keywordsText: form.keywordsText || '',
    learningAdvice: form.learningAdvice || '',
    aiInsights: form.aiInsights || '',
    videoDuration: form.videoDuration || '',
    videoResolution: form.videoResolution || '',
    posterTime: form.posterTime || '',
    captionLanguage: form.captionLanguage || '',
    pageCount: form.pageCount || '',
    readingLevel: form.readingLevel || '',
    readingMinutes: form.readingMinutes || '',
    printable: !!form.printable,
    keyPoints: form.keyPoints || '',
    questionType: form.questionType || '',
    difficultyLevel: form.difficultyLevel,
    questionStem: form.questionStem || '',
    questionAnswer: form.questionAnswer || '',
    questionAnalysis: form.questionAnalysis || '',
  })
}
function buildResourcePayload() {
  const typeBlocks: string[] = []
  const knowledgeNamesText = (form.knowledgePointIds || []).map((id: any) => knowledgePointOptions.value.find((item: any) => item.value === id)?.label).filter(Boolean).join('、')
  if (knowledgeNamesText) typeBlocks.push(`关联知识点：${knowledgeNamesText}`)
  if (form.resourceType === 'video') {
    if (form.videoDuration) typeBlocks.push(`视频时长：${form.videoDuration}`)
    if (form.videoResolution) typeBlocks.push(`清晰度：${form.videoResolution}`)
    if (form.posterTime) typeBlocks.push(`封面帧：${form.posterTime}`)
    if (form.captionLanguage) typeBlocks.push(`字幕语言：${form.captionLanguage}`)
  } else if (['doc', 'pdf', 'ppt'].includes(form.resourceType)) {
    if (form.pageCount) typeBlocks.push(`页数：${form.pageCount}`)
    if (form.readingLevel) typeBlocks.push(`阅读难度：${form.readingLevel}`)
    if (form.readingMinutes) typeBlocks.push(`建议阅读时长：${form.readingMinutes}`)
    typeBlocks.push(`可打印：${form.printable ? '是' : '否'}`)
    if (form.keyPoints) typeBlocks.push(`重点提要：${form.keyPoints}`)
  } else if (form.resourceType === 'question') {
    if (form.questionType) typeBlocks.push(`题型：${form.questionType}`)
    if (form.difficultyLevel !== undefined) typeBlocks.push(`难度：${form.difficultyLevel}`)
    if (form.questionStem) typeBlocks.push(`题干：${form.questionStem}`)
    if (form.questionAnswer) typeBlocks.push(`答案：${form.questionAnswer}`)
    if (form.questionAnalysis) typeBlocks.push(`解析：${form.questionAnalysis}`)
  }
  const contentText = [form.contentText, ...typeBlocks].filter(Boolean).join('\n')
  return {
    resourceId: form.resourceId,
    resourceName: form.resourceName,
    resourceType: form.resourceType,
    courseId: form.courseId,
    uploaderId: form.uploaderId,
    fileUrl: form.fileUrl,
    coverUrl: form.coverUrl,
    contentText,
    summary: form.summary,
    remark: buildExtendedMeta(),
    auditStatus: form.auditStatus,
    qualityScore: form.qualityScore,
    status: form.status,
    viewCount: form.viewCount,
    downloadCount: form.downloadCount,
    favoriteCount: form.favoriteCount,
  }
}
async function submitForm() { const payload = buildResourcePayload(); if (payload.resourceId) { await updateResource(payload); ElMessage.success('修改成功') } else { await addResource(payload); ElMessage.success('新增成功') } open.value = false; getList() }
async function handleDelete(row?: any) { const resourceIds = row?.resourceId || ids.value; if (!resourceIds || (Array.isArray(resourceIds) && !resourceIds.length)) return; await ElMessageBox.confirm('确认删除选中的资源吗？', '提示', { type: 'warning' }); await delResource(resourceIds); ElMessage.success('删除成功'); getList() }
async function handleView(row: any) { const res = await getResource(row.resourceId); detail.value = res.data || {}; detailOpen.value = true }
async function loadOptions(){ courseOptions.value = await fetchCourseOptions(); userOptions.value = await fetchUserOptions(); const kpRes = await listKnowledgePoint({ pageNum: 1, pageSize: 500, status: '0' }); knowledgePointOptions.value = (kpRes.rows || []).map((item: any) => ({ label: item.knowledgeName, value: item.knowledgePointId, courseId: item.courseId, difficultyLevel: item.difficultyLevel, keyword: item.keyword, description: item.description })) }
function courseLabel(courseId: number | string) { return courseOptions.value.find((item: any) => item.value === courseId)?.label || `课程 ${courseId || '-'}` }
function calcHeatScore(row: any) { const value = (Number(row.viewCount || 0) * 0.4) + (Number(row.downloadCount || 0) * 1.2) + (Number(row.favoriteCount || 0) * 1.8) + (Number(row.qualityScore || 80) * 0.55); return value.toFixed(1) }
function calcRecommendPotential(row: any) { const base = (Number(calcHeatScore(row)) * 0.6) + (Number(row.qualityScore || 80) * 0.4) + (row.resourceType === 'question' ? 6 : row.resourceType === 'video' ? 4 : 2) - (row.auditStatus === '1' ? 0 : 10); return Math.max(base, 0).toFixed(1) }
function knowledgePointNames(row: any) { const meta = parseExtendedMeta(row?.remark); return (meta.knowledgePointIds || []).map((id: any) => knowledgePointOptions.value.find((item: any) => item.value === id)?.label).filter(Boolean) }
function autoFillMetadata() {
  const selectedKnowledgePoints = (form.knowledgePointIds || []).map((id: any) => knowledgePointOptions.value.find((item: any) => item.value === id)).filter(Boolean)
  const knowledgeNames = selectedKnowledgePoints.map((item: any) => item.label)
  const keywords = new Set<string>()
  knowledgeNames.forEach((item: string) => keywords.add(item))
  selectedKnowledgePoints.forEach((item: any) => {
    String(item.keyword || '').split(/[，,]/).map((v: string) => v.trim()).filter(Boolean).forEach((v: string) => keywords.add(v))
  })
  if (!form.summary) {
    form.summary = `${form.resourceName || '该资源'}适用于${knowledgeNames.join('、') || '课程学习'}场景，适合${form.resourceType === 'video' ? '讲解学习' : form.resourceType === 'question' ? '练习巩固' : '阅读梳理'}。`
  }
  form.keywordsText = Array.from(keywords).slice(0, 8).join(', ')
  form.learningAdvice = `建议用于${knowledgeNames.join('、') || '相关知识点'}学习，${form.resourceType === 'question' ? '可先做题再看解析' : form.resourceType === 'video' ? '建议结合笔记和回看进行吸收' : '建议先速览目录再精读重点部分'}。`
  form.aiInsights = form.aiInsights || `当前资源更适合${knowledgeNames.join('、') || '课程核心内容'}教学使用，可结合${form.resourceType === 'question' ? '练习反馈' : form.resourceType === 'video' ? '讲解视频' : '阅读笔记'}形成学习闭环。`
  if (form.resourceType === 'doc' || form.resourceType === 'pdf' || form.resourceType === 'ppt') {
    form.readingMinutes = form.readingMinutes || '15-20 分钟'
  }
}
function generateSummary() {
  const textSource = String(form.contentText || '').replace(/<[^>]+>/g, ' ').replace(/\s+/g, ' ').trim()
  if (!textSource && !form.resourceName) {
    ElMessage.warning('请先填写标题或正文内容')
    return
  }
  const base = textSource || form.resourceName
  form.summary = base.slice(0, 120) + (base.length > 120 ? '...' : '')
}
function generateKeywords() {
  const tokens = new Set<string>()
  String(form.resourceName || '').split(/[，,、\s]/).map((v: string) => v.trim()).filter((v: string) => v.length >= 2).forEach((v: string) => tokens.add(v))
  ;(form.knowledgePointIds || []).forEach((id: any) => {
    const item = knowledgePointOptions.value.find((option: any) => option.value === id)
    if (item?.label) tokens.add(item.label)
  })
  form.keywordsText = Array.from(tokens).slice(0, 8).join(', ')
}
function extractJsonObject(raw: string) {
  const normalized = String(raw || '').trim()
  const firstBrace = normalized.indexOf('{')
  const lastBrace = normalized.lastIndexOf('}')
  if (firstBrace >= 0 && lastBrace > firstBrace) {
    return normalized.slice(firstBrace, lastBrace + 1)
  }
  return normalized
}
async function analyzeByAi() {
  if (!form.resourceName && !form.contentText) {
    ElMessage.warning('请先填写资源标题或正文内容')
    return
  }
  aiLoading.value = true
  aiStatusText.value = 'AI 正在分析资源内容、知识点适配和元数据，请稍候...'
  try {
    const selectedKnowledgePoints = (form.knowledgePointIds || []).map((id: any) => knowledgePointOptions.value.find((item: any) => item.value === id)?.label).filter(Boolean)
    const prompt = JSON.stringify({
      task: 'resource_metadata_analysis',
      instruction: '请仅返回 JSON，结构为 {"summary":"","keywords":[],"learningAdvice":"","aiInsights":"","suggestedKnowledgePoints":[],"qualityScore":0}。summary 80-120字，keywords 最多8个，qualityScore 0-100。',
      resourceName: form.resourceName || '',
      resourceType: form.resourceType || '',
      courseId: form.courseId || null,
      selectedKnowledgePoints,
      contentText: String(form.contentText || '').replace(/<[^>]+>/g, ' ').replace(/\s+/g, ' ').trim().slice(0, 4000),
    })
    const res = await chatAiModel({
      bizType: 'resource_analysis',
      systemPrompt: '你是智慧校园资源运营助手，擅长分析教学资源内容、提取元数据、判断适配知识点，并返回严格 JSON。',
      userPrompt: prompt,
      stream: false,
      deepThinking: false,
    })
    const content = res.data?.content || ''
    const parsed = JSON.parse(extractJsonObject(content))
    if (parsed.summary) form.summary = parsed.summary
    if (Array.isArray(parsed.keywords)) form.keywordsText = parsed.keywords.join(', ')
    if (parsed.learningAdvice) form.learningAdvice = parsed.learningAdvice
    if (parsed.aiInsights) form.aiInsights = parsed.aiInsights
    if (parsed.qualityScore !== undefined && parsed.qualityScore !== null && !Number.isNaN(Number(parsed.qualityScore))) {
      form.qualityScore = Math.max(0, Math.min(100, Number(parsed.qualityScore)))
    }
    if (Array.isArray(parsed.suggestedKnowledgePoints) && parsed.suggestedKnowledgePoints.length && !form.knowledgePointIds?.length) {
      const matched = knowledgePointOptions.value
        .filter((item: any) => parsed.suggestedKnowledgePoints.includes(item.label))
        .map((item: any) => item.value)
      if (matched.length) form.knowledgePointIds = matched
    }
    aiStatusText.value = 'AI 分析已完成，已将结果回填到摘要、关键词、建议和质量分。'
    ElMessage.success('AI 分析完成')
  } catch (error: any) {
    aiStatusText.value = 'AI 分析失败，已保留当前内容。'
    ElMessage.error(error?.message || 'AI 分析失败')
  } finally {
    aiLoading.value = false
  }
}
onMounted(async()=>{ await loadOptions(); resetFormData(); getList() })
</script>

<style scoped>
.mb16{margin-bottom:16px;}
.metric-label{color:var(--el-text-color-secondary);}
.metric-value{margin-top:8px;font-size:28px;font-weight:700;color:var(--el-color-primary);}
.metric-value.warning{color:var(--el-color-warning);}
.metric-value.success{color:var(--el-color-success);}
.resource-toolbar{display:flex;justify-content:space-between;align-items:center;gap:16px;flex-wrap:wrap;margin-bottom:16px;}
.resource-filter-tag{cursor:pointer;}
.insight-item{padding:10px 0;border-bottom:1px solid var(--el-border-color-lighter);}
.insight-item:last-child{border-bottom:none;}
.insight-item__title{font-weight:600;color:var(--el-text-color-primary);}
.insight-item__meta{margin-top:4px;font-size:12px;color:var(--el-text-color-secondary);line-height:1.6;}
.knowledge-tags{display:flex;flex-wrap:wrap;gap:6px;align-items:center;}
.resource-card-grid{display:grid;grid-template-columns:repeat(3,minmax(0,1fr));gap:16px;margin-bottom:16px;}
.resource-card{border-radius:18px;}
.resource-card__head{display:flex;justify-content:space-between;gap:12px;align-items:flex-start;}
.resource-card__title{font-size:16px;font-weight:700;color:var(--el-text-color-primary);line-height:1.5;}
.resource-card__meta{margin-top:6px;font-size:12px;color:var(--el-text-color-secondary);}
.resource-card__summary{margin-top:14px;line-height:1.8;color:var(--el-text-color-regular);min-height:66px;}
.resource-card__tags{display:flex;flex-wrap:wrap;gap:6px;margin-top:12px;}
.resource-card__stats{display:flex;gap:12px;flex-wrap:wrap;margin-top:14px;font-size:12px;color:var(--el-color-primary);}
.resource-card__actions{display:flex;gap:12px;justify-content:flex-end;margin-top:14px;}
.resource-editor{display:grid;grid-template-columns:240px 1fr;gap:20px;}
.resource-editor__sidebar{display:flex;flex-direction:column;gap:16px;}
.resource-editor__mode-card,.resource-editor__type-card,.resource-editor__score-card,.resource-editor__guide-card{padding:16px;border-radius:16px;background:linear-gradient(180deg,#f8fbff 0%,#f4f7fb 100%);border:1px solid var(--el-border-color-lighter);}
.resource-editor__type-title,.resource-editor__score-title{font-size:13px;color:var(--el-text-color-secondary);}
.resource-editor__type-tip{margin:12px 0 0;line-height:1.7;color:var(--el-text-color-regular);}
.resource-editor__mode-group{margin-top:12px;display:flex;flex-wrap:wrap;gap:8px;}
.resource-editor__score-value{margin-top:12px;font-size:34px;font-weight:700;color:var(--el-color-primary);}
.resource-editor__score-sub{margin-top:6px;font-size:12px;color:var(--el-text-color-secondary);line-height:1.6;}
.resource-editor__guide-desc{margin-top:10px;line-height:1.7;color:var(--el-text-color-regular);}
.resource-editor__guide-tags{margin-top:12px;display:flex;flex-wrap:wrap;gap:8px;}
.resource-editor__main{min-width:0;}
.resource-editor__tabs :deep(.el-tabs__content){padding-top:8px;}
.quick-editor__steps{margin:0 0 16px;}
.quick-editor__actions{display:flex;gap:12px;flex-wrap:wrap;margin-top:8px;}
.ai-tools{display:flex;gap:12px;flex-wrap:wrap;margin-bottom:12px;}
.mt16{margin-top:16px;}
@media (max-width: 900px){
  .resource-card-grid{grid-template-columns:1fr;}
  .resource-editor{grid-template-columns:1fr;}
}
</style>
