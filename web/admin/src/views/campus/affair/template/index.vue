<template>
  <div class="app-container">
    <el-form :model="queryParams" :inline="true" v-show="showSearch" class="mb16">
      <el-form-item label="分类">
        <el-select v-model="queryParams.categoryId" clearable filterable placeholder="请选择分类" style="width: 220px">
          <el-option v-for="item in categoryOptions" :key="item.categoryId" :label="item.categoryName" :value="item.categoryId" />
        </el-select>
      </el-form-item>
      <el-form-item label="模板名称">
        <el-input v-model="queryParams.templateName" placeholder="请输入模板名称" clearable @keyup.enter="getList" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" clearable placeholder="请选择状态" style="width: 140px">
          <el-option label="正常" value="0" />
          <el-option label="停用" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="getList">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb16">
      <el-col :span="1.5">
        <el-button v-hasPermi="['campus:affairTemplate:add']" type="primary" plain icon="Plus" @click="handleAdd">新增模板</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <div class="overview-grid mb16">
      <div class="overview-card">
        <span>模板总数</span>
        <strong>{{ templateList.length }}</strong>
      </div>
      <div class="overview-card is-success">
        <span>启用模板</span>
        <strong>{{ enabledTemplateCount }}</strong>
      </div>
      <div class="overview-card is-warning">
        <span>学籍异动模板</span>
        <strong>{{ academicStatusTemplateCount }}</strong>
      </div>
      <div class="overview-card is-info">
        <span>平均流程节点</span>
        <strong>{{ averageWorkflowSteps }}</strong>
      </div>
    </div>

    <el-table v-loading="loading" :data="templateList">
      <el-table-column prop="templateName" label="模板名称" min-width="200" />
      <el-table-column prop="categoryName" label="所属分类" width="160" />
      <el-table-column prop="businessName" label="业务名称" min-width="160" />
      <el-table-column prop="audienceRoles" label="可发起角色" min-width="160">
        <template #default="{ row }">{{ formatRoleLabel(row.audienceRoles) }}</template>
      </el-table-column>
      <el-table-column prop="targetStatusName" label="目标学籍状态" min-width="140" />
      <el-table-column label="流程节点" width="100">
        <template #default="{ row }">{{ safeJsonParse(row.workflowSchemaJson).length }}</template>
      </el-table-column>
      <el-table-column label="表单字段" width="100">
        <template #default="{ row }">{{ safeJsonParse(row.formSchemaJson).length }}</template>
      </el-table-column>
      <el-table-column label="配置健康" width="120">
        <template #default="{ row }">
          <el-tag :type="resolveRowHealth(row).type">{{ resolveRowHealth(row).label }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === '0' ? 'success' : 'info'">{{ row.status === '0' ? '正常' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="180" show-overflow-tooltip />
      <el-table-column label="操作" width="170" fixed="right">
        <template #default="{ row }">
          <el-button link type="info" @click="handlePreview(row)">预览</el-button>
          <el-button v-hasPermi="['campus:affairTemplate:edit']" link type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button v-hasPermi="['campus:affairTemplate:add']" link type="success" @click="handleClone(row)">复制</el-button>
          <el-button v-hasPermi="['campus:affairTemplate:remove']" link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog
      v-model="open"
      :title="title"
      width="1320px"
      top="3vh"
      destroy-on-close
      :close-on-click-modal="false"
    >
      <div class="template-editor">
        <aside class="template-editor__aside">
          <section class="editor-card">
            <div class="editor-card__head">
              <h3>当前配置</h3>
              <el-tag :type="form.status === '0' ? 'success' : 'info'" size="small">{{ form.status === '0' ? '正常' : '停用' }}</el-tag>
            </div>
            <div class="summary-lines">
              <div class="summary-line">
                <span>所属分类</span>
                <strong>{{ currentCategory?.categoryName || '未选择' }}</strong>
              </div>
              <div class="summary-line">
                <span>模板编码</span>
                <strong>{{ form.templateCode || '待填写' }}</strong>
              </div>
              <div class="summary-line">
                <span>可发起角色</span>
                <strong>{{ audienceRolePreview }}</strong>
              </div>
              <div class="summary-line">
                <span>表单字段</span>
                <strong>{{ editorStats.fieldCount }} 项</strong>
              </div>
              <div class="summary-line">
                <span>必填字段</span>
                <strong>{{ editorStats.requiredFieldCount }} 项</strong>
              </div>
              <div class="summary-line">
                <span>流程节点</span>
                <strong>{{ editorStats.workflowCount }} 步</strong>
              </div>
            </div>
          </section>

          <section class="editor-card">
            <div class="editor-card__head">
              <h3>配置诊断</h3>
              <el-tag :type="blockingDiagnostics.length ? 'danger' : 'success'" size="small">
                {{ blockingDiagnostics.length ? '需处理' : '可提交' }}
              </el-tag>
            </div>
            <div class="diagnostic-list">
              <div v-for="item in templateDiagnostics" :key="item.text" class="diagnostic-item" :class="`is-${item.level}`">
                <i :class="diagnosticIcon(item.level)" />
                <span>{{ item.text }}</span>
              </div>
            </div>
          </section>

          <section class="editor-card">
            <div class="editor-card__head">
              <h3>模板预设</h3>
              <el-tag size="small" type="info">高校常用</el-tag>
            </div>
            <div class="starter-list">
              <button
                v-for="item in availableStarterVariants"
                :key="item.key"
                type="button"
                class="starter-item"
                @click="applyStarterVariant(item)"
              >
                <strong>{{ item.label }}</strong>
                <span>{{ item.desc }}</span>
              </button>
              <el-empty v-if="!availableStarterVariants.length" :image-size="46" description="选择分类后显示推荐预设" />
            </div>
          </section>

          <section class="editor-card">
            <div class="editor-card__head">
              <h3>配置建议</h3>
            </div>
            <ul class="tips-list">
              <li v-for="tip in currentSceneTips" :key="tip">{{ tip }}</li>
            </ul>
          </section>
        </aside>

        <div class="template-editor__main">
          <el-tabs v-model="activeEditorTab" class="editor-tabs" stretch>
            <el-tab-pane label="基础设置" name="basic">
              <div class="editor-pane">
                <section class="pane-card">
                  <div class="pane-card__head">
                    <div>
                      <h3>基础信息</h3>
                      <p>定义模板归属、编码、发起角色和流程控制策略。</p>
                    </div>
                    <el-button v-if="availableStarterVariants.length" type="primary" plain size="small" @click="applyStarterVariant(availableStarterVariants[0])">
                      套用推荐配置
                    </el-button>
                  </div>

                  <el-form ref="formRef" :model="form" :rules="rules" label-width="110px" class="editor-form">
                    <el-row :gutter="16">
                      <el-col :span="12">
                        <el-form-item label="所属分类" prop="categoryId">
                          <el-select v-model="form.categoryId" filterable placeholder="请选择分类" style="width: 100%" @change="syncCategorySnapshot">
                            <el-option v-for="item in categoryOptions" :key="item.categoryId" :label="item.categoryName" :value="item.categoryId" />
                          </el-select>
                        </el-form-item>
                      </el-col>
                      <el-col :span="12">
                        <el-form-item label="模板名称" prop="templateName">
                          <el-input v-model="form.templateName" placeholder="请输入模板名称" />
                        </el-form-item>
                      </el-col>
                      <el-col :span="12">
                        <el-form-item label="模板编码" prop="templateCode">
                          <el-input v-model="form.templateCode" placeholder="请输入唯一模板编码" />
                        </el-form-item>
                      </el-col>
                      <el-col :span="12">
                        <el-form-item label="业务名称">
                          <el-input v-model="form.businessName" placeholder="如 休学申请 / 综测申请" />
                        </el-form-item>
                      </el-col>
                      <el-col :span="12">
                        <el-form-item label="业务编码">
                          <el-input v-model="form.businessCode" placeholder="如 ACADEMIC_STATUS_SUSPEND" />
                        </el-form-item>
                      </el-col>
                      <el-col :span="12">
                        <el-form-item label="可发起角色">
                          <el-select v-model="audienceRoleList" multiple collapse-tags style="width: 100%">
                            <el-option v-for="item in roleOptions" :key="item.value" :label="item.label" :value="item.value" />
                          </el-select>
                        </el-form-item>
                      </el-col>
                      <el-col :span="12">
                        <el-form-item label="目标学籍状态">
                          <el-input v-model="form.targetStatusName" placeholder="仅学籍异动模板填写，如 休学 / 在籍在校" />
                        </el-form-item>
                      </el-col>
                      <el-col :span="12">
                        <el-form-item label="目标状态编码">
                          <el-input v-model="form.targetStatusCode" placeholder="如 SUSPENDED / IN_SCHOOL" />
                        </el-form-item>
                      </el-col>
                      <el-col :span="12">
                        <el-form-item label="排序">
                          <el-input-number v-model="form.sortOrder" :min="0" style="width: 180px" />
                        </el-form-item>
                      </el-col>
                      <el-col :span="12">
                        <el-form-item label="允许附件">
                          <el-radio-group v-model="form.allowAttachment">
                            <el-radio value="1">允许</el-radio>
                            <el-radio value="0">禁止</el-radio>
                          </el-radio-group>
                        </el-form-item>
                      </el-col>
                      <el-col :span="12">
                        <el-form-item label="允许撤回">
                          <el-radio-group v-model="form.allowRevoke">
                            <el-radio value="1">允许</el-radio>
                            <el-radio value="0">禁止</el-radio>
                          </el-radio-group>
                        </el-form-item>
                      </el-col>
                      <el-col :span="12">
                        <el-form-item label="状态">
                          <el-radio-group v-model="form.status">
                            <el-radio value="0">正常</el-radio>
                            <el-radio value="1">停用</el-radio>
                          </el-radio-group>
                        </el-form-item>
                      </el-col>
                      <el-col :span="24">
                        <el-form-item label="备注">
                          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入模板说明、办理范围或配置备注" />
                        </el-form-item>
                      </el-col>
                    </el-row>
                  </el-form>
                </section>

                <section class="pane-card">
                  <div class="pane-card__head">
                    <div>
                      <h3>申请范围</h3>
                      <p>用于控制哪些学生可以看到并发起该模板，适合奖学金、专项资助等精细化业务。</p>
                    </div>
                    <el-tag effect="plain">{{ scopeMode === 'ALL' ? '全量开放' : '自定义范围' }}</el-tag>
                  </div>
                  <el-form label-width="110px" class="editor-form">
                    <el-row :gutter="16">
                      <el-col :span="24">
                        <el-form-item label="范围模式">
                          <el-radio-group v-model="scopeMode">
                            <el-radio value="ALL">全部符合角色用户</el-radio>
                            <el-radio value="CUSTOM">自定义学生范围</el-radio>
                          </el-radio-group>
                        </el-form-item>
                      </el-col>
                      <template v-if="scopeMode === 'CUSTOM'">
                        <el-col :span="12">
                          <el-form-item label="院系/部门">
                            <el-select v-model="scopeConfig.deptIds" multiple filterable collapse-tags style="width: 100%" placeholder="选择院系/部门">
                              <el-option v-for="item in deptOptions" :key="item.deptId || item.value" :label="item.deptName || item.label" :value="item.deptId || item.value" />
                            </el-select>
                          </el-form-item>
                        </el-col>
                        <el-col :span="12">
                          <el-form-item label="年级">
                            <el-select v-model="scopeConfig.gradeIds" multiple filterable collapse-tags style="width: 100%" placeholder="选择年级">
                              <el-option v-for="item in gradeOptions" :key="item.value" :label="item.label" :value="item.value" />
                            </el-select>
                          </el-form-item>
                        </el-col>
                        <el-col :span="12">
                          <el-form-item label="班级">
                            <el-select v-model="scopeConfig.classIds" multiple filterable collapse-tags style="width: 100%" placeholder="选择班级">
                              <el-option v-for="item in classOptions" :key="item.value" :label="item.label" :value="item.value" />
                            </el-select>
                          </el-form-item>
                        </el-col>
                        <el-col :span="12">
                          <el-form-item label="指定学生">
                            <el-select v-model="scopeConfig.userIds" multiple filterable collapse-tags style="width: 100%" placeholder="选择学生">
                              <el-option v-for="item in studentUserOptions" :key="item.value" :label="item.label" :value="item.value" />
                            </el-select>
                          </el-form-item>
                        </el-col>
                      </template>
                    </el-row>
                  </el-form>
                </section>

                <section class="pane-card">
                  <div class="pane-card__head">
                    <div>
                      <h3>业务规则</h3>
                      <p>控制学期、申请窗口、前置条件和重复提交限制，提升高校学生事务治理精度。</p>
                    </div>
                    <el-tag effect="plain">{{ selectedTermLabel }}</el-tag>
                  </div>
                  <el-form label-width="120px" class="editor-form">
                    <el-row :gutter="16">
                      <el-col :span="12">
                        <el-form-item label="绑定学期">
                          <el-select v-model="businessRuleConfig.termId" clearable filterable style="width: 100%" placeholder="可选，绑定到具体学期">
                            <el-option v-for="item in termOptions" :key="item.value" :label="item.label" :value="item.value" />
                          </el-select>
                        </el-form-item>
                      </el-col>
                      <el-col :span="12">
                        <el-form-item label="待审拦截">
                          <el-switch v-model="businessRuleConfig.requireNoPending" />
                        </el-form-item>
                      </el-col>
                      <el-col :span="12">
                        <el-form-item label="申请窗口">
                          <el-switch v-model="businessRuleConfig.openWindowEnabled" />
                        </el-form-item>
                      </el-col>
                      <el-col :span="12">
                        <el-form-item label="岗位前置">
                          <el-switch v-model="businessRuleConfig.requirePublishedJob" />
                        </el-form-item>
                      </el-col>
                      <template v-if="businessRuleConfig.openWindowEnabled">
                        <el-col :span="12">
                          <el-form-item label="开放开始">
                            <el-date-picker
                              v-model="businessRuleConfig.openStartTime"
                              type="datetime"
                              value-format="YYYY-MM-DD HH:mm:ss"
                              style="width: 100%"
                              placeholder="请选择开放开始时间"
                            />
                          </el-form-item>
                        </el-col>
                        <el-col :span="12">
                          <el-form-item label="开放结束">
                            <el-date-picker
                              v-model="businessRuleConfig.openEndTime"
                              type="datetime"
                              value-format="YYYY-MM-DD HH:mm:ss"
                              style="width: 100%"
                              placeholder="请选择开放结束时间"
                            />
                          </el-form-item>
                        </el-col>
                      </template>
                      <el-col :span="12">
                        <el-form-item label="前置分类">
                          <el-select v-model="businessRuleConfig.prerequisiteCategoryCodes" multiple filterable collapse-tags style="width: 100%" placeholder="可选，需先通过的事务分类">
                            <el-option v-for="item in categoryOptions" :key="item.categoryCode" :label="item.categoryName" :value="item.categoryCode" />
                          </el-select>
                        </el-form-item>
                      </el-col>
                      <el-col :span="12">
                        <el-form-item label="前置模板编码">
                          <el-select v-model="businessRuleConfig.prerequisiteTemplateCodes" multiple filterable allow-create default-first-option collapse-tags style="width: 100%" placeholder="可手工录入模板编码">
                            <el-option v-for="item in templateList" :key="item.templateCode" :label="item.templateName" :value="item.templateCode" />
                          </el-select>
                        </el-form-item>
                      </el-col>
                      <el-col :span="24">
                        <el-form-item label="窗口公告">
                          <el-input v-model="businessRuleConfig.notice" type="textarea" :rows="3" placeholder="可填写申请窗口说明、评审提醒或业务公告" />
                        </el-form-item>
                      </el-col>
                    </el-row>
                  </el-form>
                </section>
              </div>
            </el-tab-pane>

            <el-tab-pane label="表单设计" name="form" lazy>
              <section class="pane-card">
                <div class="pane-card__head">
                  <div>
                    <h3>表单设计器</h3>
                    <p>字段编辑仅在切到此页时渲染，降低弹窗首屏卡顿。</p>
                  </div>
                  <el-space>
                    <el-tag effect="plain">已配置 {{ editorStats.fieldCount }} 项</el-tag>
                    <el-button v-if="availableStarterVariants.length" size="small" @click="applyStarterVariant(availableStarterVariants[0])">重置为推荐</el-button>
                  </el-space>
                </div>
                <FormSchemaBuilder v-model="formSchemaData" />
              </section>
            </el-tab-pane>

            <el-tab-pane label="流程设计" name="workflow" lazy>
              <section class="pane-card">
                <div class="pane-card__head">
                  <div>
                    <h3>流程设计器</h3>
                    <p>支持角色审核、固定审核人和表单指定审核人，适配高校多级审批链路。</p>
                  </div>
                  <el-space>
                    <el-tag effect="plain">已配置 {{ editorStats.workflowCount }} 步</el-tag>
                    <el-tag v-if="audienceRoleList.includes('student')" type="warning" effect="plain">学生事务建议保留辅导员节点</el-tag>
                  </el-space>
                </div>
                <WorkflowSchemaBuilder
                  v-model="workflowSchemaData"
                  :reviewer-options="reviewerUserOptions"
                  :field-options="workflowFieldOptions"
                />
              </section>
            </el-tab-pane>

            <el-tab-pane label="预览校验" name="preview" lazy>
              <div class="editor-pane">
                <section class="pane-card">
                  <div class="pane-card__head">
                    <div>
                      <h3>实时预览</h3>
                      <p>保存前从业务说明、表单字段和流程节点三个层面再次确认配置效果。</p>
                    </div>
                  </div>

                  <div class="preview-meta">
                    <div class="preview-meta__item">
                      <span>模板名称</span>
                      <strong>{{ form.templateName || '待填写' }}</strong>
                    </div>
                    <div class="preview-meta__item">
                      <span>业务归属</span>
                      <strong>{{ currentCategory?.categoryName || '未选择分类' }}</strong>
                    </div>
                    <div class="preview-meta__item">
                      <span>目标状态</span>
                      <strong>{{ form.targetStatusName || form.targetStatusCode || '无' }}</strong>
                    </div>
                    <div class="preview-meta__item">
                      <span>发起角色</span>
                      <strong>{{ audienceRolePreview }}</strong>
                    </div>
                  </div>
                </section>

                <section class="pane-card">
                  <div class="pane-card__head">
                    <div>
                      <h3>表单字段预览</h3>
                      <p>确认字段数量、组件类型和必填要求是否符合线上办理习惯。</p>
                    </div>
                    <span class="pane-card__meta">{{ editorStats.fieldCount }} 项</span>
                  </div>
                  <div class="preview-field-grid">
                    <div v-for="field in formSchemaData" :key="field.field" class="preview-field-card">
                      <strong>{{ field.label }}</strong>
                      <span>{{ field.field }}</span>
                      <p>{{ componentLabel(field.component) }}{{ field.optionSource ? ` · 数据源:${field.optionSource}` : '' }}{{ field.required ? ' · 必填' : '' }}</p>
                    </div>
                  </div>
                  <el-empty v-if="!formSchemaData.length" :image-size="46" description="暂未配置表单字段" />
                </section>

                <section class="pane-card">
                  <div class="pane-card__head">
                    <div>
                      <h3>流程节点预览</h3>
                      <p>确认审批顺序和审核方式是否适合辅导员、教师、教务等协同场景。</p>
                    </div>
                    <span class="pane-card__meta">{{ editorStats.workflowCount }} 步</span>
                  </div>
                  <div class="preview-flow-list">
                    <div v-for="(step, index) in workflowSchemaData" :key="step.stepCode || index" class="preview-flow-card">
                      <span class="preview-flow-card__index">{{ String(index + 1).padStart(2, '0') }}</span>
                      <div>
                        <strong>{{ step.stepName }}</strong>
                        <p>{{ assignmentPreview(step) }}</p>
                      </div>
                    </div>
                  </div>
                  <el-empty v-if="!workflowSchemaData.length" :image-size="46" description="暂未配置流程节点" />
                </section>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>

      <template #footer>
        <el-button @click="open = false">取消</el-button>
        <el-button @click="activeEditorTab = 'preview'">预览校验</el-button>
        <el-button type="primary" :loading="submitting" @click="submitForm">保存模板</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="previewOpen" title="模板预览" size="720px">
      <div v-if="previewTemplate" class="preview-drawer">
        <section class="preview-summary">
          <h3>{{ previewTemplate.templateName }}</h3>
          <p>{{ previewTemplate.businessName || previewTemplate.remark || '暂无模板说明' }}</p>
          <div class="preview-tags">
            <el-tag effect="plain">{{ previewTemplate.categoryName }}</el-tag>
            <el-tag effect="plain">{{ formatRoleLabel(previewTemplate.audienceRoles) }}</el-tag>
            <el-tag :type="previewTemplate.status === '0' ? 'success' : 'info'">{{ previewTemplate.status === '0' ? '正常' : '停用' }}</el-tag>
          </div>
        </section>

        <section class="preview-section">
          <div class="preview-section__header">
            <h4>表单字段</h4>
            <span>{{ safeJsonParse(previewTemplate.formSchemaJson).length }} 项</span>
          </div>
          <div class="preview-field-grid">
            <div v-for="field in safeJsonParse(previewTemplate.formSchemaJson)" :key="field.field" class="preview-field-card">
              <strong>{{ field.label }}</strong>
              <span>{{ field.field }}</span>
              <p>{{ componentLabel(field.component) }}{{ field.optionSource ? ` · 数据源:${field.optionSource}` : '' }}{{ field.required ? ' · 必填' : '' }}</p>
            </div>
          </div>
        </section>

        <section class="preview-section">
          <div class="preview-section__header">
            <h4>审核流程</h4>
            <span>{{ safeJsonParse(previewTemplate.workflowSchemaJson).length }} 步</span>
          </div>
          <div class="preview-flow-list">
            <div v-for="(step, index) in safeJsonParse(previewTemplate.workflowSchemaJson)" :key="step.stepCode || index" class="preview-flow-card">
              <span class="preview-flow-card__index">{{ String(Number(index) + 1).padStart(2, '0') }}</span>
              <div>
                <strong>{{ step.stepName }}</strong>
                <p>{{ ASSIGNMENT_TYPE_LABEL[step.assignmentType] || step.assignmentType }}{{ step.reviewerRole ? ` · ${ROLE_LABEL_MAP[step.reviewerRole] || step.reviewerRole}` : '' }}{{ step.reviewerField ? ` · 字段: ${step.reviewerField}` : '' }}</p>
              </div>
            </div>
          </div>
        </section>
      </div>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addAffairTemplate, getAffairTemplate, listAffairCategory, listAffairTemplate, removeAffairTemplate, updateAffairTemplate } from '@/api/campus/affair'
import { fetchClassOptions, fetchGradeOptions, fetchTermOptions, fetchUserOptions } from '@/api/campus/options'
import { listDept } from '@/api/system/dept'
import FormSchemaBuilder from './FormSchemaBuilder.vue'
import WorkflowSchemaBuilder from './WorkflowSchemaBuilder.vue'

const roleOptions = [
  { label: '学生', value: 'student' },
  { label: '教师', value: 'teacher' },
  { label: '辅导员', value: 'advisor' },
]

const ROLE_LABEL_MAP: Record<string, string> = {
  student: '学生',
  teacher: '教师',
  advisor: '辅导员',
  admin: '管理员',
}

const ASSIGNMENT_TYPE_LABEL: Record<string, string> = {
  ROLE: '按角色分配',
  USER: '指定用户',
  FORM_FIELD: '按表单字段',
}

function formatRoleLabel(roles?: string) {
  if (!roles) return '—'
  return roles.split(',').map((r) => ROLE_LABEL_MAP[r.trim()] || r.trim()).join('、')
}

const CATEGORY_SCENE_TIPS: Record<string, string[]> = {
  ASK_LEAVE: [
    '请销假模板建议同时覆盖请假申请和返校销假两个子业务，避免业务闭环缺失。',
    '面向学生的考勤事务，流程中建议至少保留辅导员审核节点。',
    '请优先配置请假类型、起止时间、去向和紧急联系人等信息项。',
  ],
  LEAVE_RETURN_SCHOOL: [
    '离返校场景建议拆分离校申请和返校登记两个模板，便于追踪返校闭环。',
    '流程中建议保留辅导员审核和学工审核，适配大多数高校离返校管理流程。',
    '离校日期、预计返校日期和当前去向是这类模板的核心字段。',
  ],
  TEXTBOOK: [
    '教材管理建议统一复用一套模板，通过办理类型区分领用、补领和征订。',
    '教材业务建议保留数量、对应课程和 ISBN 等字段，方便后续台账统计。',
    '如需学生端更顺手，建议允许直接带入办理类型默认值。',
  ],
  ACADEMIC_STATUS: [
    '学籍异动模板建议单独配置每一种业务，并明确目标学籍状态编码和名称。',
    '休学、复学、转专业等模板都建议保留辅导员审核和教务审核两级流程。',
    '生效日期、异动原因和关键证明材料字段决定了后续状态联动质量。',
  ],
  DEFAULT: [
    '优先保证模板编码、业务编码、表单字段和流程节点完整后再投用。',
    '学生端场景通常建议保留说明类字段，方便后续审核人员判断。',
    '复杂业务推荐先套用预设，再根据学院办事口径做微调。',
  ],
}

const STARTER_LIBRARY = [
  {
    key: 'ask-leave-apply',
    categoryCodes: ['ASK_LEAVE'],
    label: '请假申请模板',
    desc: '学生请假标准申请，适合事假、病假、公假场景。',
    templateCode: 'ASK_LEAVE_STANDARD',
    templateName: '学生请销假申请',
    businessCode: 'ASK_LEAVE_STANDARD',
    businessName: '学生请销假申请',
    allowAttachment: '1',
    allowRevoke: '1',
    fields: [
      { field: 'leaveType', label: '请假类型', component: 'select', required: true, options: [{ label: '事假', value: 'PERSONAL' }, { label: '病假', value: 'SICK' }, { label: '公假', value: 'OFFICIAL' }] },
      { field: 'startDate', label: '开始时间', component: 'date', required: true },
      { field: 'endDate', label: '结束时间', component: 'date', required: true },
      { field: 'destination', label: '去向地点', component: 'input', required: true },
      { field: 'emergencyContact', label: '紧急联系人', component: 'input', required: true },
      { field: 'reason', label: '申请说明', component: 'textarea', required: true },
    ],
    workflow: [{ stepCode: 'advisor_review', stepName: '辅导员审核', assignmentType: 'ROLE', reviewerRole: 'advisor' }],
  },
  {
    key: 'ask-leave-cancel',
    categoryCodes: ['ASK_LEAVE'],
    label: '销假登记模板',
    desc: '返校后的销假确认单，适合与请假业务闭环联动。',
    templateCode: 'ASK_LEAVE_CANCEL',
    templateName: '销假登记',
    businessCode: 'ASK_LEAVE_CANCEL',
    businessName: '销假登记',
    allowAttachment: '0',
    allowRevoke: '1',
    fields: [
      { field: 'leaveRequestNo', label: '原请假编号', component: 'select', required: true, optionSource: 'myLeaveRequests', hintText: '仅显示已审批通过且尚未销假的请假记录' },
      { field: 'actualReturnDate', label: '实际返校日期', component: 'date', required: true },
      { field: 'cancelRemark', label: '销假说明', component: 'textarea', required: true },
    ],
    workflow: [{ stepCode: 'advisor_review', stepName: '辅导员确认', assignmentType: 'ROLE', reviewerRole: 'advisor' }],
  },
  {
    key: 'leave-return-apply',
    categoryCodes: ['LEAVE_RETURN_SCHOOL'],
    label: '离校申请模板',
    desc: '适用于离校出行、实习离校等场景的申请模板。',
    templateCode: 'LEAVE_RETURN_STANDARD',
    templateName: '离返校申请',
    businessCode: 'LEAVE_RETURN_STANDARD',
    businessName: '离返校申请',
    allowAttachment: '1',
    allowRevoke: '1',
    fields: [
      { field: 'leaveSchoolDate', label: '离校日期', component: 'date', required: true },
      { field: 'expectedReturnDate', label: '预计返校日期', component: 'date', required: true },
      { field: 'location', label: '当前去向', component: 'input', required: true },
      { field: 'healthStatus', label: '健康状态', component: 'select', required: true, options: [{ label: '良好', value: 'GOOD' }, { label: '需关注', value: 'WATCH' }, { label: '特殊情况', value: 'SPECIAL' }] },
      { field: 'reason', label: '申请说明', component: 'textarea', required: true },
    ],
    workflow: [
      { stepCode: 'advisor_review', stepName: '辅导员审核', assignmentType: 'ROLE', reviewerRole: 'advisor' },
      { stepCode: 'admin_review', stepName: '学工审核', assignmentType: 'ROLE', reviewerRole: 'admin' },
    ],
  },
  {
    key: 'leave-return-confirm',
    categoryCodes: ['LEAVE_RETURN_SCHOOL'],
    label: '返校登记模板',
    desc: '学生返校后的确认模板，支持关联离校申请编号。',
    templateCode: 'LEAVE_RETURN_CONFIRM',
    templateName: '返校登记',
    businessCode: 'LEAVE_RETURN_CONFIRM',
    businessName: '返校登记',
    allowAttachment: '0',
    allowRevoke: '1',
    fields: [
      { field: 'leaveRequestNo', label: '离校申请编号', component: 'select', required: true, optionSource: 'myLeaveReturnRequests', hintText: '仅显示已审批通过且尚未登记返校的离校申请' },
      { field: 'returnDate', label: '返校日期', component: 'date', required: true },
      { field: 'travelSummary', label: '行程说明', component: 'textarea', required: true, hintText: '请填写返校交通方式、返校时间及需要说明的信息。' },
    ],
    workflow: [{ stepCode: 'advisor_review', stepName: '辅导员确认', assignmentType: 'ROLE', reviewerRole: 'advisor' }],
  },
  {
    key: 'textbook-apply',
    categoryCodes: ['TEXTBOOK'],
    label: '教材申请模板',
    desc: '通过办理类型统一支持教材领用、补领和征订三类业务。',
    templateCode: 'TEXTBOOK_APPLY',
    templateName: '教材申请',
    businessCode: 'TEXTBOOK_APPLY',
    businessName: '教材申请',
    allowAttachment: '1',
    allowRevoke: '1',
    fields: [
      { field: 'textbookType', label: '办理类型', component: 'select', required: true, options: [{ label: '教材领用', value: 'CLAIM' }, { label: '教材补领', value: 'REISSUE' }, { label: '教材征订', value: 'ORDER' }] },
      { field: 'courseId', label: '对应课程', component: 'select', required: true, optionSource: 'myCourses', hintText: '请选择教材对应的课程' },
      { field: 'isbn', label: 'ISBN/教材编号', component: 'input', required: true, hintText: '请填写教材封底的ISBN编号' },
      { field: 'quantity', label: '数量', component: 'number', required: true, hintText: '每次申请最多10本' },
      { field: 'reason', label: '申请说明', component: 'textarea', required: false, hintText: '如为补领请说明原因，征订请说明需求' },
    ],
    workflow: [
      { stepCode: 'advisor_review', stepName: '辅导员审核', assignmentType: 'ROLE', reviewerRole: 'advisor' },
      { stepCode: 'admin_review', stepName: '教务审核', assignmentType: 'ROLE', reviewerRole: 'admin' },
    ],
  },
  {
    key: 'scholarship-apply',
    categoryCodes: ['SCHOLARSHIP'],
    label: '奖学金申请模板',
    desc: '适合国家奖学金、校级奖学金、专项奖学金等按学期申报场景。',
    templateCode: 'SCHOLARSHIP_APPLY',
    templateName: '奖学金申请',
    businessCode: 'SCHOLARSHIP_APPLY',
    businessName: '奖学金申请',
    allowAttachment: '1',
    allowRevoke: '1',
    fields: [
      { field: 'termId', label: '申报学期', component: 'select', required: true, optionSource: 'term' },
      { field: 'scholarshipType', label: '奖项类型', component: 'select', required: true, options: [{ label: '国家奖学金', value: 'NATIONAL' }, { label: '校级奖学金', value: 'SCHOOL' }, { label: '专项奖学金', value: 'SPECIAL' }] },
      { field: 'deptId', label: '申报院系', component: 'select', required: false, optionSource: 'dept' },
      { field: 'mentorTeacherId', label: '指导教师', component: 'user-select', required: true, optionSource: 'teacher' },
      { field: 'honorLevel', label: '成果/荣誉', component: 'textarea', required: true, hintText: '请列举主要成果及荣誉，如国家奖学金、省级竞赛一等奖、SCI论文等，每项一行' },
      { field: 'reason', label: '申请陈述', component: 'textarea', required: true, hintText: '请简要陈述申请理由及未来规划' },
    ],
    workflow: [
      { stepCode: 'teacher_review', stepName: '指导教师审核', assignmentType: 'FORM_FIELD', reviewerField: 'mentorTeacherId' },
      { stepCode: 'advisor_review', stepName: '辅导员审核', assignmentType: 'ROLE', reviewerRole: 'advisor' },
    ],
  },
  {
    key: 'grant-apply',
    categoryCodes: ['GRANT'],
    label: '助学金申请模板',
    desc: '默认带贫困认定前置条件，适合学期制资助业务。',
    templateCode: 'GRANT_APPLY',
    templateName: '助学金申请',
    businessCode: 'GRANT_APPLY',
    businessName: '助学金申请',
    allowAttachment: '1',
    allowRevoke: '1',
    fields: [
      { field: 'termId', label: '申请学期', component: 'select', required: true, optionSource: 'term' },
      { field: 'grantType', label: '助学金类型', component: 'select', required: true, options: [{ label: '国家助学金', value: 'NATIONAL' }, { label: '校级助学金', value: 'SCHOOL' }] },
      { field: 'familyIncome', label: '家庭年收入（元）', component: 'number', required: true, hintText: '请填写全家年收入总额（税前），包括所有家庭成员收入之和' },
      { field: 'hardshipLevel', label: '困难等级', component: 'select', required: true, options: [{ label: '特别困难', value: 'LEVEL_A' }, { label: '困难', value: 'LEVEL_B' }, { label: '一般困难', value: 'LEVEL_C' }] },
      { field: 'reason', label: '申请说明', component: 'textarea', required: true, hintText: '请说明家庭经济困难情况及申请助学金的具体原因' },
    ],
    workflow: [
      { stepCode: 'advisor_review', stepName: '辅导员审核', assignmentType: 'ROLE', reviewerRole: 'advisor' },
      { stepCode: 'admin_review', stepName: '资助中心审核', assignmentType: 'ROLE', reviewerRole: 'admin' },
    ],
  },
  {
    key: 'poverty-apply',
    categoryCodes: ['POVERTY_IDENTIFICATION'],
    label: '贫困认定模板',
    desc: '按学期和窗口期开放申请，支持认定三档结果留档。',
    templateCode: 'POVERTY_IDENTIFICATION_APPLY',
    templateName: '贫困生认定申请',
    businessCode: 'POVERTY_IDENTIFICATION_APPLY',
    businessName: '贫困生认定申请',
    allowAttachment: '1',
    allowRevoke: '1',
    fields: [
      { field: 'termId', label: '认定学期', component: 'select', required: true, optionSource: 'term' },
      { field: 'familyIncome', label: '家庭年收入（元）', component: 'number', required: true, hintText: '请填写全家年收入总额（税前），单位：元' },
      { field: 'familyMembers', label: '家庭人口数', component: 'number', required: true, hintText: '含本人在内的共同生活家庭成员人数' },
      { field: 'incomeSource', label: '主要收入来源', component: 'select', required: true, options: [{ label: '务农', value: 'FARMING' }, { label: '务工', value: 'LABOR' }, { label: '经商', value: 'BUSINESS' }, { label: '其他', value: 'OTHER' }] },
      { field: 'familyAddress', label: '家庭住址', component: 'input', required: true, hintText: '请填写户籍所在地详细地址' },
      { field: 'hardshipLevel', label: '困难等级', component: 'select', required: true, options: [{ label: '特别困难', value: 'LEVEL_A' }, { label: '困难', value: 'LEVEL_B' }, { label: '一般困难', value: 'LEVEL_C' }] },
      { field: 'reason', label: '认定说明', component: 'textarea', required: true, hintText: '请说明家庭经济困难的具体情况（如家庭变故、重大疾病等）' },
    ],
    workflow: [
      { stepCode: 'advisor_review', stepName: '辅导员审核', assignmentType: 'ROLE', reviewerRole: 'advisor' },
      { stepCode: 'admin_review', stepName: '资助中心审核', assignmentType: 'ROLE', reviewerRole: 'admin' },
    ],
  },
  {
    key: 'work-study-apply',
    categoryCodes: ['WORK_STUDY'],
    label: '勤工助学申请模板',
    desc: '适合结合后台岗位发布后按岗位申请的勤工助学业务。',
    templateCode: 'WORK_STUDY_APPLY',
    templateName: '勤工助学申请',
    businessCode: 'WORK_STUDY_APPLY',
    businessName: '勤工助学申请',
    allowAttachment: '1',
    allowRevoke: '1',
    fields: [
      { field: 'termId', label: '申请学期', component: 'select', required: true, optionSource: 'term' },
      { field: 'jobPreference', label: '岗位意向', component: 'input', required: true, hintText: '请描述期望的岗位类型，如图书馆助理、实验室助理、行政办公等' },
      { field: 'availableTime', label: '每周可工作时段', component: 'input', required: true, hintText: '请说明每周可工作时间段，如周一至周五下午14:00-17:00' },
      { field: 'availableHours', label: '每周可投入时长（小时）', component: 'number', required: true },
      { field: 'skillTags', label: '技能特长', component: 'select', required: false, options: [{ label: '办公软件', value: 'OFFICE' }, { label: '英语', value: 'ENGLISH' }, { label: '编程', value: 'CODING' }, { label: '设计', value: 'DESIGN' }, { label: '沟通协调', value: 'COMMUNICATION' }, { label: '体力劳动', value: 'LABOR' }] },
      { field: 'reason', label: '申请说明', component: 'textarea', required: true },
    ],
    workflow: [{ stepCode: 'advisor_review', stepName: '辅导员审核', assignmentType: 'ROLE', reviewerRole: 'advisor' }],
  },
  {
    key: 'activity-apply',
    categoryCodes: ['ACTIVITY'],
    label: '活动申请模板',
    desc: '支持活动名称、学期、场地、指导教师、参与人数等活动立项要素。',
    templateCode: 'ACTIVITY_APPLY',
    templateName: '活动申请',
    businessCode: 'ACTIVITY_APPLY',
    businessName: '活动申请',
    allowAttachment: '1',
    allowRevoke: '1',
    fields: [
      { field: 'termId', label: '活动学期', component: 'select', required: true, optionSource: 'term' },
      { field: 'activityName', label: '活动名称', component: 'input', required: true },
      { field: 'activityDate', label: '活动日期', component: 'date', required: true },
      { field: 'venue', label: '活动地点', component: 'input', required: true },
      { field: 'participantCount', label: '参与人数', component: 'number', required: true },
      { field: 'mentorTeacherId', label: '指导教师', component: 'user-select', required: true, optionSource: 'teacher' },
      { field: 'reason', label: '活动说明', component: 'textarea', required: true },
    ],
    workflow: [
      { stepCode: 'teacher_review', stepName: '指导教师审核', assignmentType: 'FORM_FIELD', reviewerField: 'mentorTeacherId' },
      { stepCode: 'advisor_review', stepName: '辅导员审核', assignmentType: 'ROLE', reviewerRole: 'advisor' },
    ],
  },
  {
    key: 'competition-apply',
    categoryCodes: ['SUBJECT_COMPETITION'],
    label: '学科竞赛模板',
    desc: '适合竞赛报名、团队申报、立项审批等场景。',
    templateCode: 'SUBJECT_COMPETITION_APPLY',
    templateName: '学科竞赛申请',
    businessCode: 'SUBJECT_COMPETITION_APPLY',
    businessName: '学科竞赛申请',
    allowAttachment: '1',
    allowRevoke: '1',
    fields: [
      { field: 'termId', label: '竞赛学期', component: 'select', required: true, optionSource: 'term' },
      { field: 'competitionName', label: '竞赛名称', component: 'input', required: true },
      { field: 'competitionLevel', label: '竞赛级别', component: 'select', required: true, options: [{ label: '校级', value: 'SCHOOL' }, { label: '市级', value: 'CITY' }, { label: '省级', value: 'PROVINCE' }, { label: '国家级', value: 'NATIONAL' }] },
      { field: 'competitionDate', label: '竞赛时间', component: 'date', required: true },
      { field: 'mentorTeacherId', label: '指导教师', component: 'user-select', required: true, optionSource: 'teacher' },
      { field: 'teamInfo', label: '团队成员', component: 'textarea', required: false },
      { field: 'reason', label: '申请说明', component: 'textarea', required: true },
    ],
    workflow: [
      { stepCode: 'teacher_review', stepName: '指导教师审核', assignmentType: 'FORM_FIELD', reviewerField: 'mentorTeacherId' },
      { stepCode: 'advisor_review', stepName: '辅导员审核', assignmentType: 'ROLE', reviewerRole: 'advisor' },
    ],
  },
  {
    key: 'innovation-apply',
    categoryCodes: ['INNOVATION_ENTREPRENEURSHIP'],
    label: '创新创业模板',
    desc: '适合大学生创新创业项目申报、经费申请与过程管理。',
    templateCode: 'INNOVATION_PROJECT_APPLY',
    templateName: '创新创业项目申请',
    businessCode: 'INNOVATION_PROJECT_APPLY',
    businessName: '创新创业项目申请',
    allowAttachment: '1',
    allowRevoke: '1',
    fields: [
      { field: 'termId', label: '项目学期', component: 'select', required: true, optionSource: 'term' },
      { field: 'projectName', label: '项目名称', component: 'input', required: true },
      { field: 'projectType', label: '项目类型', component: 'select', required: true, options: [{ label: '创新训练', value: 'INNOVATION' }, { label: '创业实践', value: 'ENTREPRENEURSHIP' }, { label: '重点孵化', value: 'INCUBATION' }] },
      { field: 'mentorTeacherId', label: '指导教师', component: 'user-select', required: true, optionSource: 'teacher' },
      { field: 'fundingNeed', label: '经费需求（元）', component: 'number', required: false },
      { field: 'reason', label: '项目说明', component: 'textarea', required: true },
    ],
    workflow: [
      { stepCode: 'teacher_review', stepName: '指导教师审核', assignmentType: 'FORM_FIELD', reviewerField: 'mentorTeacherId' },
      { stepCode: 'advisor_review', stepName: '辅导员审核', assignmentType: 'ROLE', reviewerRole: 'advisor' },
      { stepCode: 'admin_review', stepName: '创新创业中心审核', assignmentType: 'ROLE', reviewerRole: 'admin' },
    ],
  },
  {
    key: 'evaluation-apply',
    categoryCodes: ['COMPREHENSIVE_EVALUATION'],
    label: '综测测评模板',
    desc: '适合按学期汇总德育、竞赛、志愿、科研等综合测评成果。',
    templateCode: 'COMPREHENSIVE_EVALUATION_APPLY',
    templateName: '综测测评申请',
    businessCode: 'COMPREHENSIVE_EVALUATION_APPLY',
    businessName: '综测测评申请',
    allowAttachment: '1',
    allowRevoke: '1',
    fields: [
      { field: 'termId', label: '评价学期', component: 'select', required: true, optionSource: 'term' },
      { field: 'achievementSummary', label: '成果摘要', component: 'textarea', required: true, hintText: '请列举本学期主要成果：德育、竞赛获奖、科研论文、志愿服务等，每项一行' },
      { field: 'volunteerHours', label: '志愿时长（小时）', component: 'number', required: false, hintText: '本学期累计志愿服务时长' },
      { field: 'reason', label: '补充说明', component: 'textarea', required: false },
    ],
    workflow: [{ stepCode: 'advisor_review', stepName: '辅导员审核', assignmentType: 'ROLE', reviewerRole: 'advisor' }],
  },
  {
    key: 'academic-suspend',
    categoryCodes: ['ACADEMIC_STATUS'],
    label: '休学申请模板',
    desc: '适合休学、保留学籍等需要同步状态变更的业务。',
    templateCode: 'ACADEMIC_STATUS_SUSPEND',
    templateName: '学籍异动-休学',
    businessCode: 'ACADEMIC_STATUS_SUSPEND',
    businessName: '休学申请',
    targetStatusCode: 'SUSPENDED',
    targetStatusName: '休学',
    allowAttachment: '1',
    allowRevoke: '1',
    fields: [
      { field: 'suspendType', label: '休学类型', component: 'select', required: true, options: [{ label: '因病休学', value: 'ILLNESS' }, { label: '因事休学', value: 'PERSONAL' }, { label: '创业休学', value: 'ENTREPRENEURSHIP' }, { label: '其他', value: 'OTHER' }] },
      { field: 'effectiveDate', label: '生效日期', component: 'date', required: true },
      { field: 'expectedResumeDate', label: '预计复学日期', component: 'date', required: false },
      { field: 'guardianName', label: '监护人姓名', component: 'input', required: true },
      { field: 'guardianPhone', label: '监护人联系电话', component: 'input', required: true, hintText: '休学期间需保持畅通' },
      { field: 'reason', label: '异动原因', component: 'textarea', required: true, hintText: '请详细说明休学原因，因病休学请注明病情及就诊医院' },
    ],
    workflow: [
      { stepCode: 'advisor_review', stepName: '辅导员审核', assignmentType: 'ROLE', reviewerRole: 'advisor' },
      { stepCode: 'admin_review', stepName: '教务审核', assignmentType: 'ROLE', reviewerRole: 'admin' },
    ],
  },
  {
    key: 'academic-resume',
    categoryCodes: ['ACADEMIC_STATUS'],
    label: '复学申请模板',
    desc: '适合休学返校后的复学恢复办理。',
    templateCode: 'ACADEMIC_STATUS_RESUME',
    templateName: '学籍异动-复学',
    businessCode: 'ACADEMIC_STATUS_RESUME',
    businessName: '复学申请',
    targetStatusCode: 'IN_SCHOOL',
    targetStatusName: '在籍在校',
    allowAttachment: '1',
    allowRevoke: '1',
    fields: [
      { field: 'effectiveDate', label: '生效日期', component: 'date', required: true },
      { field: 'expectedReturnDate', label: '预计返校日期', component: 'date', required: true },
      { field: 'recoveryProof', label: '复学依据', component: 'input', required: false, hintText: '因病休学请上传康复证明；因事休学请说明事由已解决；创业休学请提供创业情况说明' },
      { field: 'contactPhone', label: '本人联系电话', component: 'input', required: true, hintText: '请确保为当前在用手机号，复学报到期间需保持畅通' },
      { field: 'reason', label: '异动原因', component: 'textarea', required: true },
    ],
    workflow: [
      { stepCode: 'advisor_review', stepName: '辅导员审核', assignmentType: 'ROLE', reviewerRole: 'advisor' },
      { stepCode: 'admin_review', stepName: '教务审核', assignmentType: 'ROLE', reviewerRole: 'admin' },
    ],
  },
  {
    key: 'academic-transfer',
    categoryCodes: ['ACADEMIC_STATUS'],
    label: '转专业申请模板',
    desc: '适合转专业、转方向等需要变更培养路径的业务。',
    templateCode: 'ACADEMIC_STATUS_TRANSFER_MAJOR',
    templateName: '学籍异动-转专业',
    businessCode: 'ACADEMIC_STATUS_TRANSFER_MAJOR',
    businessName: '转专业申请',
    targetStatusCode: 'TRANSFERRED_MAJOR',
    targetStatusName: '转专业',
    allowAttachment: '1',
    allowRevoke: '1',
    fields: [
      { field: 'effectiveDate', label: '生效日期', component: 'date', required: true },
      { field: 'currentMajor', label: '当前专业', component: 'input', required: true, hintText: '请填写完整专业名称，如计算机科学与技术' },
      { field: 'targetMajor', label: '目标专业', component: 'input', required: true, hintText: '请填写目标专业全称及所属学院，如软件工程（信息学院）' },
      { field: 'transferReason', label: '转专业原因', component: 'select', required: true, options: [{ label: '专业兴趣', value: 'INTEREST' }, { label: '学业成绩适配', value: 'ACADEMIC_FIT' }, { label: '就业方向调整', value: 'CAREER' }, { label: '其他', value: 'OTHER' }] },
      { field: 'reason', label: '详细说明', component: 'textarea', required: true, hintText: '请补充说明转专业的具体理由，如已选择"其他"请在此详述' },
    ],
    workflow: [
      { stepCode: 'advisor_review', stepName: '辅导员审核', assignmentType: 'ROLE', reviewerRole: 'advisor' },
      { stepCode: 'admin_review', stepName: '教务审核', assignmentType: 'ROLE', reviewerRole: 'admin' },
    ],
  },
  {
    key: 'academic-withdraw',
    categoryCodes: ['ACADEMIC_STATUS'],
    label: '退学申请模板',
    desc: '适合学生因个人、学业、健康或经济等原因申请退学的场景，需家长知情同意并确认离校手续。',
    templateCode: 'ACADEMIC_STATUS_WITHDRAW',
    templateName: '学籍异动-退学',
    businessCode: 'ACADEMIC_STATUS_WITHDRAW',
    businessName: '退学申请',
    targetStatusCode: 'WITHDRAWN',
    targetStatusName: '退学',
    allowAttachment: '1',
    allowRevoke: '1',
    fields: [
      { field: 'withdrawType', label: '退学类型', component: 'select', required: true, options: [{ label: '自愿退学', value: 'VOLUNTARY' }, { label: '学业退学', value: 'ACADEMIC' }, { label: '健康原因', value: 'HEALTH' }, { label: '经济原因', value: 'FINANCIAL' }, { label: '其他', value: 'OTHER' }] },
      { field: 'effectiveDate', label: '生效日期', component: 'date', required: true },
      { field: 'reason', label: '退学原因', component: 'textarea', required: true, hintText: '请详细说明退学原因，包括主要考虑因素及个人规划' },
      { field: 'guardianName', label: '监护人姓名', component: 'input', required: true },
      { field: 'guardianPhone', label: '监护人联系电话', component: 'input', required: true, hintText: '退学手续办理期间需保持畅通' },
      { field: 'guardianOpinion', label: '家长/监护人意见', component: 'textarea', required: true, hintText: '请填写家长或监护人对退学的知情及意见情况' },
      { field: 'dormCheckout', label: '是否已办理宿舍退宿', component: 'select', required: true, options: [{ label: '已办理', value: 'YES' }, { label: '未办理', value: 'NO' }] },
      { field: 'libraryCleared', label: '是否已归还图书馆借阅物品', component: 'select', required: true, options: [{ label: '已归还', value: 'YES' }, { label: '未归还/无借阅', value: 'NO' }] },
    ],
    workflow: [
      { stepCode: 'advisor_review', stepName: '辅导员审核', assignmentType: 'ROLE', reviewerRole: 'advisor' },
      { stepCode: 'dept_review', stepName: '学院审核', assignmentType: 'ROLE', reviewerRole: 'admin' },
      { stepCode: 'admin_review', stepName: '学籍管理部门审核', assignmentType: 'ROLE', reviewerRole: 'admin' },
    ],
  },
  {
    key: 'academic-retain',
    categoryCodes: ['ACADEMIC_STATUS'],
    label: '保留学籍申请模板',
    desc: '适合学生因入伍、创业、健康等原因暂时离校但保留学籍及返校权利的业务。',
    templateCode: 'ACADEMIC_STATUS_RETAIN',
    templateName: '学籍异动-保留学籍',
    businessCode: 'ACADEMIC_STATUS_RETAIN',
    businessName: '保留学籍申请',
    targetStatusCode: 'RETAINED',
    targetStatusName: '保留学籍',
    allowAttachment: '1',
    allowRevoke: '1',
    fields: [
      { field: 'retainReason', label: '保留学籍原因', component: 'select', required: true, options: [{ label: '应征入伍', value: 'MILITARY' }, { label: '创业', value: 'ENTREPRENEURSHIP' }, { label: '身体原因', value: 'HEALTH' }, { label: '其他', value: 'OTHER' }] },
      { field: 'retainDuration', label: '申请保留时长', component: 'select', required: true, options: [{ label: '1年', value: '1' }, { label: '2年', value: '2' }], hintText: '保留学籍一般最长不超过2年' },
      { field: 'effectiveDate', label: '生效日期', component: 'date', required: true },
      { field: 'reason', label: '详细说明', component: 'textarea', required: true, hintText: '请详细说明保留学籍的具体原因及计划返校时间' },
      { field: 'guardianName', label: '监护人姓名', component: 'input', required: true },
      { field: 'guardianPhone', label: '监护人联系电话', component: 'input', required: true },
      { field: 'contactAddress', label: '保留学籍期间联系地址', component: 'input', required: true, hintText: '请填写保留学籍期间的常住地址，便于学校联系' },
    ],
    workflow: [
      { stepCode: 'advisor_review', stepName: '辅导员审核', assignmentType: 'ROLE', reviewerRole: 'advisor' },
      { stepCode: 'admin_review', stepName: '学院审核', assignmentType: 'ROLE', reviewerRole: 'admin' },
    ],
  },
  {
    key: 'default',
    categoryCodes: ['*'],
    label: '通用事务模板',
    desc: '适合普通学生事务快速起步，再按学院流程补充字段和审核链路。',
    templateCode: '',
    templateName: '',
    businessCode: '',
    businessName: '',
    allowAttachment: '1',
    allowRevoke: '1',
    fields: [
      { field: 'title', label: '事项标题', component: 'input', required: true },
      { field: 'reason', label: '申请说明', component: 'textarea', required: true },
    ],
    workflow: [{ stepCode: 'advisor_review', stepName: '辅导员审核', assignmentType: 'ROLE', reviewerRole: 'advisor' }],
  },
]

const loading = ref(false)
const submitting = ref(false)
const showSearch = ref(true)
const open = ref(false)
const title = ref('')
const total = ref(0)
const templateList = ref<any[]>([])
const categoryOptions = ref<any[]>([])
const reviewerUserOptions = ref<any[]>([])
const termOptions = ref<any[]>([])
const deptOptions = ref<any[]>([])
const gradeOptions = ref<any[]>([])
const classOptions = ref<any[]>([])
const studentUserOptions = ref<any[]>([])
const formRef = ref<any>()
const formSchemaData = ref<any[]>([])
const workflowSchemaData = ref<any[]>([])
const audienceRoleList = ref<string[]>(['student'])
const previewOpen = ref(false)
const previewTemplate = ref<any>(null)
const activeEditorTab = ref('basic')
const scopeMode = ref<'ALL' | 'CUSTOM'>('ALL')
const scopeConfig = reactive<any>({
  deptIds: [],
  gradeIds: [],
  classIds: [],
  userIds: [],
})
const businessRuleConfig = reactive<any>({
  termId: undefined,
  openWindowEnabled: false,
  openStartTime: '',
  openEndTime: '',
  requireNoPending: true,
  prerequisiteCategoryCodes: [],
  prerequisiteTemplateCodes: [],
  requirePublishedJob: false,
  notice: '',
})

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  categoryId: undefined,
  templateName: '',
  status: '',
})

const form = reactive<any>({
  templateId: undefined,
  categoryId: undefined,
  templateCode: '',
  templateName: '',
  businessCode: '',
  businessName: '',
  audienceRoles: 'student',
  formSchemaJson: '[]',
  workflowSchemaJson: '[]',
  allowAttachment: '1',
  allowRevoke: '1',
  applyScopeJson: '',
  businessRulesJson: '',
  targetStatusCode: '',
  targetStatusName: '',
  sortOrder: 0,
  status: '0',
  remark: '',
})

const rules = {
  categoryId: [{ required: true, message: '请选择所属分类', trigger: 'change' }],
  templateCode: [{ required: true, message: '请输入模板编码', trigger: 'blur' }],
  templateName: [{ required: true, message: '请输入模板名称', trigger: 'blur' }],
}

const categoryMap = computed(() => Object.fromEntries(categoryOptions.value.map((item: any) => [item.categoryId, item])))
const currentCategory = computed(() => categoryMap.value[form.categoryId] || null)
const workflowFieldOptions = computed(() => formSchemaData.value
  .filter((item: any) => item.component === 'user-select')
  .map((item: any) => ({ label: `${item.label}（${item.field}）`, value: item.field })))
const enabledTemplateCount = computed(() => templateList.value.filter((item: any) => item.status === '0').length)
const academicStatusTemplateCount = computed(() => templateList.value.filter((item: any) => item.categoryCode === 'ACADEMIC_STATUS').length)
const averageWorkflowSteps = computed(() => {
  if (!templateList.value.length) return '0'
  const totalSteps = templateList.value.reduce((sum: number, item: any) => sum + safeJsonParse(item.workflowSchemaJson).length, 0)
  return (totalSteps / templateList.value.length).toFixed(1)
})
const audienceRolePreview = computed(() => audienceRoleList.value.length ? audienceRoleList.value.map((item) => ROLE_LABEL_MAP[item] || item).join(' / ') : '未配置')
const editorStats = computed(() => ({
  fieldCount: formSchemaData.value.length,
  requiredFieldCount: formSchemaData.value.filter((item: any) => !!item.required).length,
  workflowCount: workflowSchemaData.value.length,
}))
const availableStarterVariants = computed(() => {
  const categoryCode = currentCategory.value?.categoryCode
  if (!categoryCode) return []
  const matched = STARTER_LIBRARY.filter((item) => item.categoryCodes.includes(categoryCode))
  return matched.length ? matched : STARTER_LIBRARY.filter((item) => item.categoryCodes.includes('*'))
})
const currentSceneTips = computed(() => CATEGORY_SCENE_TIPS[currentCategory.value?.categoryCode] || CATEGORY_SCENE_TIPS.DEFAULT)
const templateDiagnostics = computed(() => buildTemplateDiagnostics())
const blockingDiagnostics = computed(() => templateDiagnostics.value.filter((item) => item.level === 'error'))
const selectedTermLabel = computed(() => termOptions.value.find((item: any) => String(item.value) === String(businessRuleConfig.termId))?.label || '未绑定')

function safeJsonParse(value: string) {
  try {
    return JSON.parse(value || '[]')
  } catch {
    return []
  }
}

function cloneSchema<T>(value: T): T {
  return JSON.parse(JSON.stringify(value))
}

function resetScopeAndRules() {
  scopeMode.value = 'ALL'
  Object.assign(scopeConfig, {
    deptIds: [],
    gradeIds: [],
    classIds: [],
    userIds: [],
  })
  Object.assign(businessRuleConfig, {
    termId: undefined,
    openWindowEnabled: false,
    openStartTime: '',
    openEndTime: '',
    requireNoPending: true,
    prerequisiteCategoryCodes: [],
    prerequisiteTemplateCodes: [],
    requirePublishedJob: false,
    notice: '',
  })
}

function parseJsonObject(value?: string) {
  try {
    return JSON.parse(value || '{}')
  } catch {
    return {}
  }
}

function hydrateScopeAndRules() {
  resetScopeAndRules()
  const scope = parseJsonObject(form.applyScopeJson)
  const rules = parseJsonObject(form.businessRulesJson)
  const hasCustomScope = ['deptIds', 'gradeIds', 'classIds', 'userIds'].some((key) => Array.isArray(scope[key]) && scope[key].length)
  scopeMode.value = hasCustomScope ? 'CUSTOM' : 'ALL'
  scopeConfig.deptIds = Array.isArray(scope.deptIds) ? scope.deptIds : []
  scopeConfig.gradeIds = Array.isArray(scope.gradeIds) ? scope.gradeIds : []
  scopeConfig.classIds = Array.isArray(scope.classIds) ? scope.classIds : []
  scopeConfig.userIds = Array.isArray(scope.userIds) ? scope.userIds : []
  businessRuleConfig.termId = rules.termId ?? undefined
  businessRuleConfig.openWindowEnabled = Boolean(rules.openWindowEnabled)
  businessRuleConfig.openStartTime = rules.openStartTime || ''
  businessRuleConfig.openEndTime = rules.openEndTime || ''
  businessRuleConfig.requireNoPending = rules.requireNoPending !== false
  businessRuleConfig.prerequisiteCategoryCodes = Array.isArray(rules.prerequisiteCategoryCodes) ? rules.prerequisiteCategoryCodes : []
  businessRuleConfig.prerequisiteTemplateCodes = Array.isArray(rules.prerequisiteTemplateCodes) ? rules.prerequisiteTemplateCodes : []
  businessRuleConfig.requirePublishedJob = Boolean(rules.requirePublishedJob)
  businessRuleConfig.notice = rules.notice || ''
}

function buildScopePayload() {
  if (scopeMode.value === 'ALL') {
    return { scopeType: 'ALL', deptIds: [], gradeIds: [], classIds: [], userIds: [] }
  }
  return {
    scopeType: 'CUSTOM',
    deptIds: scopeConfig.deptIds || [],
    gradeIds: scopeConfig.gradeIds || [],
    classIds: scopeConfig.classIds || [],
    userIds: scopeConfig.userIds || [],
  }
}

function buildRulesPayload() {
  return {
    termId: businessRuleConfig.termId || null,
    openWindowEnabled: !!businessRuleConfig.openWindowEnabled,
    openStartTime: businessRuleConfig.openStartTime || '',
    openEndTime: businessRuleConfig.openEndTime || '',
    requireNoPending: businessRuleConfig.requireNoPending !== false,
    prerequisiteCategoryCodes: businessRuleConfig.prerequisiteCategoryCodes || [],
    prerequisiteTemplateCodes: businessRuleConfig.prerequisiteTemplateCodes || [],
    requirePublishedJob: !!businessRuleConfig.requirePublishedJob,
    notice: businessRuleConfig.notice || '',
  }
}

function resetForm() {
  Object.assign(form, {
    templateId: undefined,
    categoryId: queryParams.categoryId,
    templateCode: '',
    templateName: '',
    businessCode: '',
    businessName: '',
    audienceRoles: 'student',
    formSchemaJson: '[]',
    workflowSchemaJson: '[]',
    allowAttachment: '1',
    allowRevoke: '1',
    applyScopeJson: '',
    businessRulesJson: '',
    targetStatusCode: '',
    targetStatusName: '',
    sortOrder: 0,
    status: '0',
    remark: '',
  })
  resetScopeAndRules()
  formSchemaData.value = []
  workflowSchemaData.value = []
  audienceRoleList.value = ['student']
  activeEditorTab.value = 'basic'
  formRef.value?.clearValidate()
  if (queryParams.categoryId) {
    syncCategorySnapshot()
  }
}

function syncCategorySnapshot() {
  const category = currentCategory.value
  if (!category) return
  if (!form.businessName) form.businessName = category.categoryName
  if (!form.businessCode) form.businessCode = category.categoryCode
  if (!form.remark) form.remark = category.remark || ''
  if (!form.businessRulesJson) {
    if (category.categoryCode === 'GRANT') {
      businessRuleConfig.prerequisiteCategoryCodes = ['POVERTY_IDENTIFICATION']
    }
    if (category.categoryCode === 'WORK_STUDY') {
      businessRuleConfig.requirePublishedJob = true
    }
    if (category.categoryCode === 'POVERTY_IDENTIFICATION') {
      businessRuleConfig.openWindowEnabled = true
    }
  }
  if (!formSchemaData.value.length && !workflowSchemaData.value.length) {
    const starter = availableStarterVariants.value[0] || STARTER_LIBRARY.find((item) => item.categoryCodes.includes('*'))
    if (starter) {
      applyStarterVariant(starter, false)
    }
  }
}

function applyStarterVariant(starter: any, switchTab = true) {
  if (!starter) return
  const categoryCode = currentCategory.value?.categoryCode
  const ruleDefaults = {
    termId: null,
    openWindowEnabled: categoryCode === 'POVERTY_IDENTIFICATION',
    openStartTime: '',
    openEndTime: '',
    requireNoPending: true,
    prerequisiteCategoryCodes: categoryCode === 'GRANT' ? ['POVERTY_IDENTIFICATION'] : [],
    prerequisiteTemplateCodes: [],
    requirePublishedJob: categoryCode === 'WORK_STUDY',
    notice: '',
  }
  formSchemaData.value = cloneSchema(starter.fields || [])
  workflowSchemaData.value = cloneSchema(starter.workflow || [])
  if (!form.templateId || !form.templateCode) form.templateCode = starter.templateCode || form.templateCode
  if (!form.templateId || !form.templateName) form.templateName = starter.templateName || form.templateName
  form.businessCode = starter.businessCode || form.businessCode
  form.businessName = starter.businessName || form.businessName
  form.allowAttachment = starter.allowAttachment || form.allowAttachment
  form.allowRevoke = starter.allowRevoke || form.allowRevoke
  form.applyScopeJson = JSON.stringify({ scopeType: 'ALL', deptIds: [], gradeIds: [], classIds: [], userIds: [] })
  form.businessRulesJson = JSON.stringify(ruleDefaults)
  hydrateScopeAndRules()
  if (starter.targetStatusCode !== undefined) form.targetStatusCode = starter.targetStatusCode || ''
  if (starter.targetStatusName !== undefined) form.targetStatusName = starter.targetStatusName || ''
  if (switchTab) {
    activeEditorTab.value = 'form'
    ElMessage.success(`已套用预设：${starter.label}`)
  }
}

function buildTemplateDiagnostics() {
  const result: Array<{ level: 'success' | 'warning' | 'error'; text: string }> = []
  if (!form.categoryId) result.push({ level: 'error', text: '尚未选择事务分类。' })
  if (!form.templateCode) result.push({ level: 'error', text: '模板编码不能为空。' })
  if (!form.templateName) result.push({ level: 'error', text: '模板名称不能为空。' })
  if (!formSchemaData.value.length) result.push({ level: 'error', text: '请至少配置一个表单字段。' })
  if (!workflowSchemaData.value.length) result.push({ level: 'error', text: '请至少配置一个流程节点。' })

  const fieldKeys = formSchemaData.value.map((item: any) => String(item.field || '').trim()).filter(Boolean)
  const duplicateFields = fieldKeys.filter((key, index) => fieldKeys.indexOf(key) !== index)
  if (duplicateFields.length) {
    result.push({ level: 'error', text: `表单字段 Key 重复：${Array.from(new Set(duplicateFields)).join('、')}` })
  }

  const optionIssues = formSchemaData.value.filter((item: any) =>
    ['select', 'user-select', 'radio'].includes(item.component)
    && !item.optionSource
    && (!Array.isArray(item.options) || !item.options.length))
  if (optionIssues.length) {
    result.push({ level: 'warning', text: `以下字段尚未配置选项来源：${optionIssues.map((item: any) => item.label || item.field).join('、')}` })
  }

  const workflowIssues = workflowSchemaData.value.filter((item: any) => {
    if (item.assignmentType === 'ROLE') return !item.reviewerRole
    if (item.assignmentType === 'USER') return !item.reviewerUserId
    if (item.assignmentType === 'FORM_FIELD') return !item.reviewerField
    return false
  })
  if (workflowIssues.length) {
    result.push({ level: 'error', text: `以下流程节点分派信息不完整：${workflowIssues.map((item: any) => item.stepName || item.stepCode || '未命名节点').join('、')}` })
  }

  if (audienceRoleList.value.includes('student') && !workflowSchemaData.value.some((item: any) => item.reviewerRole === 'advisor')) {
    result.push({ level: 'warning', text: '学生端事务通常建议保留辅导员审核节点。' })
  }

  if (currentCategory.value?.categoryCode === 'ACADEMIC_STATUS' && (!form.targetStatusCode || !form.targetStatusName)) {
    result.push({ level: 'error', text: '学籍异动模板必须配置目标学籍状态编码和名称。' })
  }

  if (scopeMode.value === 'CUSTOM'
    && !scopeConfig.deptIds.length
    && !scopeConfig.gradeIds.length
    && !scopeConfig.classIds.length
    && !scopeConfig.userIds.length) {
    result.push({ level: 'warning', text: '已切换为自定义申请范围，但还没有选择任何学生范围。' })
  }

  if (businessRuleConfig.openWindowEnabled && (!businessRuleConfig.openStartTime || !businessRuleConfig.openEndTime)) {
    result.push({ level: 'error', text: '启用窗口期后，请同时配置开放开始和结束时间。' })
  }

  if (currentCategory.value?.categoryCode === 'GRANT' && !businessRuleConfig.prerequisiteCategoryCodes.includes('POVERTY_IDENTIFICATION')) {
    result.push({ level: 'warning', text: '助学金建议配置“贫困认定已通过”作为前置条件。' })
  }

  if (!result.length) {
    result.push({ level: 'success', text: '当前模板配置完整，可直接保存发布。' })
  }
  return result
}

function diagnosticIcon(level: string) {
  if (level === 'error') return 'ri-close-circle-line'
  if (level === 'warning') return 'ri-error-warning-line'
  return 'ri-checkbox-circle-line'
}

function componentLabel(type: string) {
  const map: Record<string, string> = {
    input: '输入框',
    textarea: '多行文本',
    select: '下拉选择',
    date: '日期',
    'date-range': '日期范围',
    number: '数字',
    'user-select': '人员选择',
    radio: '单选按钮',
    switch: '开关',
    file: '文件上传',
  }
  return map[type] || type || '未设置'
}

function assignmentPreview(step: any) {
  if (step.assignmentType === 'ROLE') return `角色审核 · ${ROLE_LABEL_MAP[step.reviewerRole] || step.reviewerRole || '未配置'}`
  if (step.assignmentType === 'USER') return `固定审核人 · ${resolveReviewerLabel(step.reviewerUserId)}`
  if (step.assignmentType === 'FORM_FIELD') return `表单指定 · ${resolveFieldLabel(step.reviewerField)}`
  return step.assignmentType || '未设置'
}

function resolveReviewerLabel(userId?: string | number) {
  if (!userId) return '未配置'
  return reviewerUserOptions.value.find((item: any) => String(item.value) === String(userId))?.label || String(userId)
}

function resolveFieldLabel(fieldKey?: string) {
  if (!fieldKey) return '未配置'
  return workflowFieldOptions.value.find((item: any) => item.value === fieldKey)?.label || fieldKey
}

function resolveRowHealth(row: any) {
  const fieldCount = safeJsonParse(row.formSchemaJson).length
  const workflowCount = safeJsonParse(row.workflowSchemaJson).length
  if (!fieldCount || !workflowCount) return { label: '待补全', type: 'danger' as const }
  if (row.categoryCode === 'ACADEMIC_STATUS' && (!row.targetStatusCode || !row.targetStatusName)) {
    return { label: '缺状态', type: 'warning' as const }
  }
  return { label: '良好', type: 'success' as const }
}

async function loadOptions() {
  const categoryRes = await listAffairCategory({ pageNum: 1, pageSize: 200 })
  categoryOptions.value = categoryRes.rows || []
  const teacherOptions = await fetchUserOptions('teacher')
  const adminOptions = await fetchUserOptions('admin')
  const studentOptions = await fetchUserOptions('student')
  const deptRes = await listDept()
  termOptions.value = await fetchTermOptions()
  gradeOptions.value = await fetchGradeOptions()
  classOptions.value = await fetchClassOptions()
  deptOptions.value = Array.isArray(deptRes.data) ? deptRes.data : []
  studentUserOptions.value = studentOptions
  reviewerUserOptions.value = [...teacherOptions, ...adminOptions]
}

async function getList() {
  loading.value = true
  try {
    const res = await listAffairTemplate(queryParams)
    templateList.value = res.rows || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  Object.assign(queryParams, { pageNum: 1, pageSize: 10, categoryId: undefined, templateName: '', status: '' })
  getList()
}

function handleAdd() {
  resetForm()
  title.value = '新增事务模板'
  open.value = true
}

async function handleEdit(row: any) {
  resetForm()
  const res = await getAffairTemplate(row.templateId)
  Object.assign(form, res.data || res)
  audienceRoleList.value = String(form.audienceRoles || 'student').split(',').filter(Boolean)
  formSchemaData.value = safeJsonParse(form.formSchemaJson)
  workflowSchemaData.value = safeJsonParse(form.workflowSchemaJson)
  hydrateScopeAndRules()
  title.value = '编辑事务模板'
  activeEditorTab.value = 'basic'
  open.value = true
}

function handlePreview(row: any) {
  previewTemplate.value = row
  previewOpen.value = true
}

async function handleClone(row: any) {
  resetForm()
  const res = await getAffairTemplate(row.templateId)
  Object.assign(form, res.data || res)
  form.templateId = undefined
  form.templateCode = `${form.templateCode}_COPY`
  form.templateName = `${form.templateName}（复制）`
  audienceRoleList.value = String(form.audienceRoles || 'student').split(',').filter(Boolean)
  formSchemaData.value = safeJsonParse(form.formSchemaJson)
  workflowSchemaData.value = safeJsonParse(form.workflowSchemaJson)
  hydrateScopeAndRules()
  title.value = '复制事务模板'
  activeEditorTab.value = 'basic'
  open.value = true
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm(`确定删除模板“${row.templateName}”吗？`, '提示', { type: 'warning' })
  await removeAffairTemplate(row.templateId)
  ElMessage.success('删除成功')
  getList()
}

async function submitForm() {
  await formRef.value?.validate()
  if (blockingDiagnostics.value.length) {
    activeEditorTab.value = 'preview'
    ElMessage.warning(blockingDiagnostics.value[0].text)
    return
  }
  const payload = {
    ...form,
    audienceRoles: audienceRoleList.value.join(','),
    formSchemaJson: JSON.stringify(formSchemaData.value),
    workflowSchemaJson: JSON.stringify(workflowSchemaData.value),
    applyScopeJson: JSON.stringify(buildScopePayload()),
    businessRulesJson: JSON.stringify(buildRulesPayload()),
  }
  submitting.value = true
  try {
    if (payload.templateId) {
      await updateAffairTemplate(payload)
      ElMessage.success('模板已更新')
    } else {
      await addAffairTemplate(payload)
      ElMessage.success('模板已新增')
    }
    open.value = false
    getList()
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  await loadOptions()
  await getList()
})
</script>

<style scoped>
.mb16 { margin-bottom: 16px; }

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

.overview-card.is-success strong { color: #059669; }
.overview-card.is-warning strong { color: #d97706; }
.overview-card.is-info strong { color: #2563eb; }

.template-editor {
  display: grid;
  grid-template-columns: 300px minmax(0, 1fr);
  gap: 18px;
  min-height: 640px;
}

.template-editor__aside,
.template-editor__main,
.editor-pane { display: grid; gap: 16px; }

.editor-card,
.pane-card,
.preview-summary,
.preview-section {
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 16px;
  padding: 18px;
  background: #fff;
}

.editor-card__head,
.pane-card__head,
.preview-section__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 12px;
}

.editor-card__head h3,
.pane-card__head h3,
.preview-summary h3,
.preview-section h4 {
  margin: 0;
  font-size: 16px;
  color: #0f172a;
}

.pane-card__head p,
.preview-summary p {
  margin: 6px 0 0;
  color: #667085;
  line-height: 1.7;
}

.pane-card__meta {
  color: #94a3b8;
  font-size: 12px;
}

.summary-lines {
  display: grid;
  gap: 10px;
}

.summary-line {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
}

.summary-line span {
  color: #667085;
  font-size: 13px;
}

.summary-line strong {
  color: #0f172a;
  text-align: right;
}

.diagnostic-list,
.starter-list {
  display: grid;
  gap: 10px;
}

.diagnostic-item {
  display: flex;
  gap: 8px;
  align-items: flex-start;
  padding: 10px 12px;
  border-radius: 12px;
  font-size: 13px;
}

.diagnostic-item i { margin-top: 1px; }
.diagnostic-item.is-success { background: #ecfdf3; color: #027a48; }
.diagnostic-item.is-warning { background: #fffaeb; color: #b54708; }
.diagnostic-item.is-error { background: #fef3f2; color: #b42318; }

.starter-item {
  border: 1px solid #dbe7f5;
  border-radius: 14px;
  background: linear-gradient(180deg, #ffffff, #f8fbff);
  padding: 12px 14px;
  text-align: left;
  cursor: pointer;
  transition: transform .15s ease, box-shadow .15s ease, border-color .15s ease;
}

.starter-item:hover {
  transform: translateY(-1px);
  border-color: #93c5fd;
  box-shadow: 0 10px 24px rgba(37, 99, 235, .08);
}

.starter-item strong {
  display: block;
  color: #172033;
  font-size: 14px;
}

.starter-item span {
  display: block;
  margin-top: 6px;
  color: #667085;
  font-size: 12px;
  line-height: 1.65;
}

.tips-list {
  margin: 0;
  padding-left: 18px;
  display: grid;
  gap: 8px;
  color: #667085;
  line-height: 1.7;
}

.editor-tabs :deep(.el-tabs__header) {
  margin-bottom: 16px;
}

.editor-form { margin-top: 8px; }

.preview-meta {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.preview-meta__item {
  padding: 14px;
  border-radius: 14px;
  border: 1px solid #e5e7eb;
  background: #f8fafc;
}

.preview-meta__item span {
  display: block;
  color: #64748b;
  font-size: 12px;
}

.preview-meta__item strong {
  display: block;
  margin-top: 6px;
  color: #111827;
}

.preview-drawer {
  display: grid;
  gap: 18px;
}

.preview-summary p {
  margin: 0 0 12px;
}

.preview-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.preview-field-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 12px;
}

.preview-field-card,
.preview-flow-card {
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 14px;
  background: #f8fafc;
}

.preview-field-card strong,
.preview-flow-card strong {
  display: block;
  color: #111827;
}

.preview-field-card span,
.preview-field-card p,
.preview-flow-card p {
  margin: 4px 0 0;
  color: #667085;
  font-size: 12px;
}

.preview-flow-list {
  display: grid;
  gap: 12px;
}

.preview-flow-card {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.preview-flow-card__index {
  display: inline-flex;
  min-width: 34px;
  height: 34px;
  align-items: center;
  justify-content: center;
  border-radius: 999px;
  background: #eff6ff;
  color: #2563eb;
  font-size: 12px;
  font-weight: 700;
}

@media (max-width: 1200px) {
  .template-editor {
    grid-template-columns: 1fr;
  }

  .template-editor__aside {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 900px) {
  .overview-grid,
  .preview-meta,
  .template-editor__aside {
    grid-template-columns: 1fr;
  }
}
</style>
