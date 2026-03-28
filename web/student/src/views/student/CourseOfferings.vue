<template>
  <div class="portal-page course-offerings-page">
    <!-- Search Area -->
    <section class="portal-card search-card">
      <div class="search-head">
        <div class="search-head__left">
          <div class="search-head__icon">
            <el-icon><Search /></el-icon>
          </div>
          <div>
            <div class="search-head__title">筛选条件</div>
            <div class="search-head__desc">学期和其他筛选条件保持同层级展示，常用条件默认展开，自定义字段优先使用学生端专用下拉数据。</div>
          </div>
        </div>
        <div class="search-head__right">
          <el-button link type="primary" class="link-btn" @click="showQueryConfigDialog = true">
            <el-icon><Setting /></el-icon>
            自定义查询条件
          </el-button>
          <el-button
            v-if="advancedFields.length"
            link
            type="primary"
            class="link-btn"
            @click="showAdvancedFilters = !showAdvancedFilters"
          >
            {{ showAdvancedFilters ? '收起高级筛选' : '展开高级筛选' }}
            <el-icon class="rotate-when-open" :class="{ open: showAdvancedFilters }"><ArrowDown /></el-icon>
          </el-button>
        </div>
      </div>

      <div class="search-grid search-grid--primary">
        <div
          v-for="field in baseFields"
          :key="field.key"
          class="search-field"
          :class="{
            'search-field--range': field.type === 'range',
            'search-field--term': field.key === 'termId',
          }"
        >
          <label>{{ field.label }}</label>
          <div v-if="field.type === 'range'" class="credit-range">
            <el-input-number
              v-model="queryParams.creditsMin"
              :min="0"
              :precision="1"
              :step="0.5"
              :controls="false"
              class="credit-range__input"
              placeholder="最小值"
            />
            <span class="credit-range__separator">~</span>
            <el-input-number
              v-model="queryParams.creditsMax"
              :min="0"
              :precision="1"
              :step="0.5"
              :controls="false"
              class="credit-range__input"
              placeholder="最大值"
            />
          </div>
          <el-select
            v-else-if="field.type === 'select'"
            v-model="queryParams[field.key]"
            :clearable="field.clearable !== false"
            :filterable="field.filterable !== false"
            :placeholder="field.placeholder"
            style="width: 100%"
            popper-class="course-offerings-popper"
            teleported
            @change="field.key === 'termId' ? handleTermChange() : undefined"
          >
            <el-option
              v-for="option in getFieldOptions(field)"
              :key="`${field.key}-${option.value}`"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
          <el-input
            v-else
            v-model="queryParams[field.key]"
            :placeholder="field.placeholder"
            clearable
          />
        </div>
      </div>

      <div v-if="advancedFields.length" class="search-advanced-panel" :class="{ open: showAdvancedFilters }">
        <div class="search-advanced-panel__inner">
          <div class="search-advanced__divider"></div>
          <div class="search-grid search-grid--advanced">
            <div
              v-for="field in advancedFields"
              :key="field.key"
              class="search-field"
              :class="{ 'search-field--range': field.type === 'range' }"
            >
              <label>{{ field.label }}</label>
              <div v-if="field.type === 'range'" class="credit-range">
                <el-input-number
                  v-model="queryParams.creditsMin"
                  :min="0"
                  :precision="1"
                  :step="0.5"
                  :controls="false"
                  class="credit-range__input"
                  placeholder="最小值"
                />
                <span class="credit-range__separator">~</span>
                <el-input-number
                  v-model="queryParams.creditsMax"
                  :min="0"
                  :precision="1"
                  :step="0.5"
                  :controls="false"
                  class="credit-range__input"
                  placeholder="最大值"
                />
              </div>
              <el-select
                v-else-if="field.type === 'select'"
                v-model="queryParams[field.key]"
                :clearable="field.clearable !== false"
                :filterable="field.filterable !== false"
                :placeholder="field.placeholder"
                style="width: 100%"
                popper-class="course-offerings-popper"
                teleported
              >
                <el-option
                  v-for="option in getFieldOptions(field)"
                  :key="`${field.key}-${option.value}`"
                  :label="option.label"
                  :value="option.value"
                />
              </el-select>
              <el-input
                v-else
                v-model="queryParams[field.key]"
                :placeholder="field.placeholder"
                clearable
              />
            </div>
          </div>
        </div>
      </div>

      <div v-if="activeFilterTags.length" class="active-tags">
        <div class="active-tags__label">当前筛选：</div>
        <div class="active-tags__list">
          <el-tag
            v-for="t in activeFilterTags"
            :key="t.key"
            class="active-tag"
            :closable="t.closable"
            @close="t.closable && clearFilter(t.key)"
          >
            <span class="active-tag__k">{{ t.label }}</span>
            <span class="active-tag__v">{{ t.value }}</span>
          </el-tag>
          <el-button link type="primary" class="link-btn" @click="handleReset">
            <el-icon><RefreshRight /></el-icon>
            清空全部
          </el-button>
        </div>
      </div>

      <div class="search-actions">
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
        <el-button :icon="RefreshRight" @click="handleReset">重置</el-button>
      </div>
    </section>

    <!-- Toolbar -->
    <section class="portal-card toolbar-card">
      <div class="toolbar-left">
        <el-button type="primary" plain :icon="Download" @click="showExportDialog = true">导出</el-button>
      </div>
      <div class="toolbar-right">
        <div class="toolbar-status">已选 <span class="toolbar-status__n">{{ selectedRows.length }}</span> 条</div>
        <div class="toolbar-total">共 <span class="toolbar-total__n">{{ total }}</span> 条结果</div>
        <el-button :icon="Setting" circle title="页面设置" @click="showPageSettingsDialog = true" />
        <el-button :icon="Refresh" :loading="loading" circle title="刷新" @click="loadData" />
      </div>
    </section>

    <!-- Data Table -->
    <section class="portal-card table-card">
      <el-table
        ref="tableRef"
        v-loading="loading"
        :data="tableData"
        :class="'density-' + pageSettings.density"
        :table-layout="pageSettings.columnWidth === 'fit' ? 'auto' : 'fixed'"
        row-key="id"
        highlight-current-row
        stripe
        @selection-change="onSelectionChange"
        @row-click="handleRowClick"
      >
        <el-table-column type="selection" width="50" />
        <el-table-column v-if="pageSettings.showIndex" type="index" label="序号" width="60" />

        <el-table-column v-if="isColumnVisible('courseInfo')" label="课程信息" min-width="280">
          <template #default="{ row }">
            <div class="course-info-cell">
              <div class="course-info-top">
                <el-tooltip
                  v-bind="tableTooltipProps"
                  :content="row.courseName || '-'"
                  :disabled="!shouldShowTooltip(row.courseName)"
                >
                  <strong class="course-name-link" @click="openDetail(row)">{{ row.courseName || '-' }}</strong>
                </el-tooltip>
                <el-tooltip
                  v-bind="tableTooltipProps"
                  :content="row.courseCode || '-'"
                  :disabled="!shouldShowTooltip(row.courseCode, 10)"
                >
                  <span class="course-code">{{ row.courseCode || '-' }}</span>
                </el-tooltip>
              </div>
              <div class="course-tags">
                <el-tag
                  v-if="requiredText(row)"
                  size="small"
                  class="pill"
                  :type="requiredType(row)"
                  effect="light"
                >
                  {{ requiredText(row) }}
                </el-tag>
                <el-tag v-if="row.subjectType" size="small" class="pill" effect="plain">{{ row.subjectType }}</el-tag>
                <el-tag v-if="row.courseCategory" size="small" class="pill" effect="plain">{{ row.courseCategory }}</el-tag>
                <el-tag v-if="row.openDeptName" size="small" class="pill" effect="plain">{{ row.openDeptName }}</el-tag>
              </div>
              <div class="course-meta">
                <span>{{ row.totalHours ?? '-' }} 学时</span>
                <span class="dot">•</span>
                <span>{{ row.weeklyHours ?? '-' }} 周学时</span>
                <span class="dot">•</span>
                <span>{{ row.assessmentType || '未设考核' }}</span>
              </div>
              <div class="course-meta course-meta--secondary">
                <span>{{ row.teachingLanguage || '未设授课语言' }}</span>
                <span class="dot">•</span>
                <span>{{ row.campusName || '未设校区' }}</span>
                <span class="dot">•</span>
                <span>{{ row.studentCountText || '-' }}</span>
              </div>
              <div v-if="row.intro" class="course-intro">
                {{ row.intro }}
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column v-if="isColumnVisible('teachingClass')" label="教学班" min-width="180">
          <template #default="{ row }">
            <div class="teaching-class-cell">
              <div class="teaching-class-cell__head">
                <el-tooltip
                  v-bind="tableTooltipProps"
                  :content="row.teachingClassCode || '-'"
                  :disabled="!shouldShowTooltip(row.teachingClassCode, 10)"
                >
                  <span class="code-pill">{{ row.teachingClassCode || '-' }}</span>
                </el-tooltip>
                <span v-if="row.selectionGroupName" class="teaching-class-chip">{{ row.selectionGroupName }}</span>
              </div>
              <el-tooltip
                v-bind="tableTooltipProps"
                :content="row.teachingClassName || row.className || '-'"
                :disabled="!shouldShowTooltip(row.teachingClassName || row.className)"
              >
                <div class="teaching-class-name">{{ row.teachingClassName || row.className || '-' }}</div>
              </el-tooltip>
              <div class="teaching-class-meta">
                <span>{{ row.className || '未配置行政班' }}</span>
                <span class="dot">•</span>
                <span>{{ row.major || row.businessType || '未配置专业' }}</span>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column v-if="isColumnVisible('credits')" label="学分" width="80" align="center">
          <template #default="{ row }">
            <span class="credit-badge">{{ row.credits ?? '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column v-if="isColumnVisible('schedule')" label="时间地点" min-width="240">
          <template #default="{ row }">
            <div class="schedule-cell">
              <div class="schedule-line schedule-line--head">
                <el-icon class="muted"><Calendar /></el-icon>
                <span>{{ row.scheduleText ? '上课安排' : '待排课' }}</span>
              </div>
              <div v-if="row.scheduleText" class="schedule-lines">
                <div v-for="(line, idx) in splitLines(row.scheduleText)" :key="idx" class="schedule-line">
                  <el-tooltip
                    v-bind="tableTooltipProps"
                    :content="line"
                    :disabled="!shouldShowTooltip(line, 18)"
                  >
                    <span class="schedule-line__text schedule-line--ellipsis">{{ line }}</span>
                  </el-tooltip>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column v-if="isColumnVisible('teacher')" label="授课教师" min-width="120">
          <template #default="{ row }">
            <el-tooltip
              v-bind="tableTooltipProps"
              :content="row.teacherName || '-'"
              :disabled="!shouldShowTooltip(row.teacherName, 8)"
            >
              <span class="teacher-cell">
                <el-icon class="muted"><User /></el-icon>
                <span>{{ row.teacherName || '-' }}</span>
              </span>
            </el-tooltip>
          </template>
        </el-table-column>

        <el-table-column v-if="isColumnVisible('requirements')" label="教学要求" min-width="180">
          <template #default="{ row }">
            <div class="requirements-cell">
              <div class="requirements-tags">
                <span class="requirements-tag requirements-tag--primary">{{ row.taskType || '常规教学' }}</span>
                <span
                  v-if="row.courseLevelRequirement && row.courseLevelRequirement !== '无'"
                  class="requirements-tag"
                >
                  {{ row.courseLevelRequirement }}
                </span>
              </div>
              <el-tooltip
                v-bind="tableTooltipProps"
                :content="row.prerequisiteCourse || '无需先修'"
                :disabled="!shouldShowTooltip(row.prerequisiteCourse, 12)"
              >
                <div class="requirements-line">
                  <span class="requirements-label">先修</span>
                  <span class="requirements-value">{{ row.prerequisiteCourse || '无需先修' }}</span>
                </div>
              </el-tooltip>
              <el-tooltip
                v-bind="tableTooltipProps"
                :content="row.courseLevelRequirement || '默认开放'"
                :disabled="!shouldShowTooltip(row.courseLevelRequirement, 12)"
              >
                <div class="requirements-line requirements-line--muted">
                  <span class="requirements-label">等级</span>
                  <span class="requirements-value">{{ row.courseLevelRequirement || '默认开放' }}</span>
                </div>
              </el-tooltip>
            </div>
          </template>
        </el-table-column>

        <template #empty>
          <div class="table-empty">
            <el-empty description="暂无开课数据，试试调整筛选条件或更换学期">
              <template #image>
                <div class="empty-illus">
                  <div class="empty-illus__dot"></div>
                  <div class="empty-illus__card"></div>
                </div>
              </template>
            </el-empty>
          </div>
        </template>
      </el-table>

      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[20, 50, 100, 200, 500, 1000]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        class="table-pagination"
        @size-change="loadData"
        @current-change="loadData"
      />
    </section>

    <!-- Custom Query Config Dialog -->
    <el-dialog
      v-model="showQueryConfigDialog"
      title="自定义查询条件"
      width="680px"
      destroy-on-close
      append-to-body
      modal-append-to-body
      class="course-offerings-dialog"
      modal-class="course-offerings-dialog-modal"
    >
      <div class="query-config-body">
        <div class="query-config-left">
          <div class="query-config-header">
            <span>全部字段</span>
            <span>
              <el-button link type="primary" size="small" @click="toggleAllQueryFields(true)">全选</el-button>
              <el-button link type="primary" size="small" @click="invertQueryFields">反选</el-button>
            </span>
          </div>
          <div class="query-config-search">
            <el-input v-model="queryConfigKeyword" clearable placeholder="搜索字段…" />
          </div>
          <el-scrollbar class="query-config-scroll">
            <div class="query-config-list">
              <el-checkbox
                v-for="field in filteredAllQueryFields"
                :key="field.key"
                v-model="tempActiveQueryKeys[field.key]"
                :disabled="field.key === 'termId'"
              >
                {{ field.label }}
              </el-checkbox>
            </div>
          </el-scrollbar>
        </div>
        <div class="query-config-right">
          <div class="query-config-header">
            <span>已选 ({{ tempActiveCount }})</span>
          </div>
          <el-scrollbar class="query-config-scroll">
            <div class="query-config-selected">
              <div v-for="(field, idx) in tempSelectedFields" :key="field.key" class="selected-item">
                <span class="selected-item__idx">{{ idx + 1 }}</span>
                <el-tag closable @close="tempActiveQueryKeys[field.key] = false">
                  {{ field.label }}
                </el-tag>
              </div>
              <span v-if="!tempSelectedFields.length" class="empty-hint">请从左侧选择字段</span>
            </div>
          </el-scrollbar>
        </div>
      </div>
      <template #footer>
        <el-button @click="resetQueryConfigToDefault">恢复默认</el-button>
        <el-button @click="showQueryConfigDialog = false">取消</el-button>
        <el-button type="primary" @click="saveQueryConfig">保存</el-button>
      </template>
    </el-dialog>

    <!-- Page Settings Dialog -->
    <el-dialog
      v-model="showPageSettingsDialog"
      title="页面设置"
      width="480px"
      destroy-on-close
      append-to-body
      modal-append-to-body
      class="course-offerings-dialog"
      modal-class="course-offerings-dialog-modal"
    >
      <div class="page-settings">
        <div class="ps-section">
          <div class="ps-title">显示列（{{ tempVisibleColumnCount }}/{{ allColumns.length }}）</div>
          <el-checkbox-group v-model="tempPageSettings.visibleColumns" class="ps-cols">
            <el-checkbox v-for="col in allColumns" :key="col.key" :label="col.key">{{ col.label }}</el-checkbox>
          </el-checkbox-group>
        </div>

        <div class="ps-section">
          <div class="ps-title">列表显示</div>
          <div class="ps-inline">
            <el-checkbox v-model="tempPageSettings.showIndex">表格序号</el-checkbox>
          </div>
        </div>

        <div class="ps-section">
          <div class="ps-title">显示模式</div>
          <el-radio-group v-model="tempPageSettings.density" class="ps-radio-group">
            <el-radio-button label="compact">紧凑</el-radio-button>
            <el-radio-button label="medium">适中</el-radio-button>
            <el-radio-button label="loose">宽松</el-radio-button>
          </el-radio-group>
        </div>

        <div class="ps-section">
          <div class="ps-title">列宽策略</div>
          <el-radio-group v-model="tempPageSettings.columnWidth" class="ps-radio-group">
            <el-radio-button label="fit">适应内容</el-radio-button>
            <el-radio-button label="fill">铺满表格</el-radio-button>
          </el-radio-group>
        </div>

        <div class="ps-section">
          <div class="ps-title">每页行数</div>
          <el-radio-group v-model="tempPageSettings.pageSize" class="ps-radio-group ps-radio-group--sizes">
            <el-radio-button v-for="s in [20, 50, 100, 200, 500, 1000]" :key="s" :label="s">{{ s }}</el-radio-button>
          </el-radio-group>
        </div>
      </div>
      <template #footer>
        <el-button @click="resetPageSettingsToDefault">恢复默认</el-button>
        <el-button @click="showPageSettingsDialog = false">取消</el-button>
        <el-button type="primary" @click="savePageSettings">保存</el-button>
      </template>
    </el-dialog>

    <!-- Export Fields Dialog -->
    <el-dialog
      v-model="showExportDialog"
      title="导出字段"
      width="560px"
      destroy-on-close
      append-to-body
      modal-append-to-body
      class="course-offerings-dialog"
      modal-class="course-offerings-dialog-modal"
    >
      <div class="export-intro">
        <div class="export-intro__title">导出说明</div>
        <div class="export-intro__desc">
          导出范围：
          <span class="export-scope">
            {{ selectedRows.length ? `已选 ${selectedRows.length} 条` : '全部数据（按当前筛选）' }}
          </span>
        </div>
      </div>

      <div class="export-actions-top">
        <el-button link type="primary" size="small" @click="toggleAllExportFields(true)">全选</el-button>
        <el-button link type="primary" size="small" @click="invertExportFields">反选</el-button>
        <el-button link type="danger" size="small" @click="toggleAllExportFields(false)">取消选中</el-button>
        <div class="export-picked">已选 <span class="export-picked__n">{{ selectedExportFieldCount }}</span> 个字段</div>
      </div>

      <div class="export-groups">
        <div v-for="g in exportFieldGroups" :key="g.key" class="export-group">
          <div class="export-group__title">{{ g.label }}</div>
          <div class="export-field-grid">
            <el-checkbox v-for="ef in g.fields" :key="ef.key" v-model="exportFieldSelection[ef.key]">
              {{ ef.label }}
            </el-checkbox>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="showExportDialog = false">取消</el-button>
        <el-button type="primary" :loading="exporting" @click="handleExport">
          导出 {{ selectedExportFieldCount }} 个字段
        </el-button>
      </template>
    </el-dialog>

    <!-- Course Detail Dialog -->
    <el-dialog
      v-model="showDetailDialog"
      title="课程详情"
      width="840px"
      destroy-on-close
      append-to-body
      modal-append-to-body
      class="course-offerings-dialog"
      modal-class="course-offerings-dialog-modal"
    >
      <div class="detail-wrap">
        <div class="detail-hero">
          <div class="detail-hero__top">
            <div class="detail-hero__eyebrow">{{ detailRow.termName || currentTermLabel }}</div>
            <div class="detail-code">{{ detailRow.courseCode || '-' }}</div>
          </div>
          <div class="detail-header__main">
            <div class="detail-name">{{ detailRow.courseName || '-' }}</div>
          </div>
          <div class="detail-header__tags">
            <el-tag
              v-if="requiredText(detailRow)"
              size="small"
              class="pill"
              :type="requiredType(detailRow)"
              effect="light"
            >
              {{ requiredText(detailRow) }}
            </el-tag>
            <el-tag v-if="detailRow.subjectType" size="small" class="pill" effect="plain">{{ detailRow.subjectType }}</el-tag>
            <el-tag v-if="detailRow.courseCategory" size="small" class="pill" effect="plain">{{ detailRow.courseCategory }}</el-tag>
            <el-tag v-if="detailRow.openDeptName" size="small" class="pill" effect="plain">{{ detailRow.openDeptName }}</el-tag>
            <el-tag v-if="detailRow.campusName" size="small" class="pill" effect="plain">{{ detailRow.campusName }}</el-tag>
          </div>
          <div class="detail-hero__intro">
            {{ detailIntroText }}
          </div>
          <div class="detail-metrics">
            <div v-for="item in detailMetrics" :key="item.label" class="detail-metric">
              <div class="detail-metric__label">{{ item.label }}</div>
              <div class="detail-metric__value">{{ item.value }}</div>
              <div class="detail-metric__hint">{{ item.hint }}</div>
            </div>
          </div>
        </div>

        <div class="detail-layout">
          <div v-for="group in detailInfoGroups" :key="group.key" class="detail-panel">
            <div class="detail-panel__title">{{ group.title }}</div>
            <div class="detail-grid">
              <div v-for="item in group.items" :key="item.label" class="detail-item">
                <div class="detail-item__k">{{ item.label }}</div>
                <div class="detail-item__v">{{ item.value }}</div>
              </div>
            </div>
          </div>

          <div class="detail-panel detail-panel--wide">
            <div class="detail-panel__title">上课安排</div>
            <div v-if="detailScheduleSummaryLines.length" class="detail-schedule-summary">
              <div class="detail-schedule-summary__title">默认展示</div>
              <div class="detail-schedule-summary__list">
                <div v-for="(line, idx) in detailScheduleSummaryLines" :key="`summary-${idx}`" class="detail-schedule-summary__line">
                  {{ line }}
                </div>
              </div>
            </div>
            <el-collapse v-if="detailScheduleCards.length" v-model="detailScheduleActiveName" class="detail-schedule-collapse">
              <el-collapse-item
                name="schedule-list"
                class="detail-schedule-accordion"
              >
                <template #title>
                  <div class="detail-schedule-accordion__title">
                    <span class="detail-schedule-accordion__day">具体排课</span>
                    <span class="detail-schedule-accordion__section">{{ detailScheduleCards.length }} 条安排</span>
                    <span class="detail-schedule-accordion__time">点击展开查看明细</span>
                  </div>
                </template>
                <el-scrollbar class="detail-schedule-scroll">
                  <div class="detail-schedule-list">
                    <div v-for="(item, idx) in detailScheduleCards" :key="`${item.date}-${idx}`" class="detail-schedule-card">
                      <div class="detail-schedule-card__head">
                        <div class="detail-schedule-card__day">{{ item.weekLabel }}</div>
                        <div class="detail-schedule-card__weeks">{{ item.weeksText }}</div>
                      </div>
                      <div class="detail-schedule-card__time">{{ item.sectionText }}<span v-if="item.timeRange"> · {{ item.timeRange }}</span></div>
                      <div class="detail-schedule-card__place">{{ item.classroom || '待定教室' }}</div>
                      <div class="detail-schedule-card__date">{{ item.date }}</div>
                    </div>
                  </div>
                </el-scrollbar>
              </el-collapse-item>
            </el-collapse>
            <div v-else-if="!detailScheduleSummaryLines.length" class="detail-empty-panel">
              当前还没有排课信息。
            </div>
          </div>

          <div class="detail-panel detail-panel--wide">
            <div class="detail-panel__title">补充说明</div>
            <div class="detail-note">
              {{ detailNoteText }}
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { ArrowDown, Calendar, Download, Refresh, RefreshRight, Search, Setting, User } from '@element-plus/icons-vue'
import {
  exportPortalCourseOfferings,
  getPortalCourseOfferingFilterOptions,
  listPortalCourseOfferings,
  listPortalTermOptions,
} from '@/api/portal'

defineOptions({ name: 'CourseOfferings' })

/* ===================== Query Field Definitions ===================== */
type OptionItem = { label: string; value: any }
type FilterOptionKey =
  | 'termOptions'
  | 'teacherOptions'
  | 'openDeptOptions'
  | 'campusOptions'
  | 'courseCategoryOptions'
  | 'businessTypeOptions'
  | 'majorOptions'
  | 'assessmentTypeOptions'
  | 'teachingLanguageOptions'
  | 'requiredFlagOptions'
type QueryFieldType = 'input' | 'select' | 'range'
type QueryField = {
  key: string
  label: string
  type: QueryFieldType
  placeholder: string
  clearable?: boolean
  filterable?: boolean
  optionKey?: FilterOptionKey
}

const allQueryFields: QueryField[] = [
  { key: 'termId', label: '学期', type: 'select', placeholder: '请选择学期', clearable: false, optionKey: 'termOptions' },
  { key: 'courseCode', label: '课程代码', type: 'input', placeholder: '请输入课程代码' },
  { key: 'courseName', label: '课程名称', type: 'input', placeholder: '请输入课程名称' },
  { key: 'credits', label: '学分', type: 'range', placeholder: '请输入学分区间' },
  { key: 'teachingClassCode', label: '教学班代码', type: 'input', placeholder: '请输入教学班代码' },
  { key: 'className', label: '教学班名称', type: 'input', placeholder: '请输入教学班名称' },
  { key: 'openDeptId', label: '开课部门', type: 'select', placeholder: '请选择开课部门', optionKey: 'openDeptOptions' },
  { key: 'courseCategory', label: '课程类别', type: 'select', placeholder: '请选择课程类别', optionKey: 'courseCategoryOptions' },
  { key: 'teacherId', label: '授课教师', type: 'select', placeholder: '请选择授课教师', optionKey: 'teacherOptions' },
  { key: 'campusName', label: '上课校区', type: 'select', placeholder: '请选择校区', optionKey: 'campusOptions' },
  { key: 'businessType', label: '业务类型', type: 'select', placeholder: '请选择业务类型', optionKey: 'businessTypeOptions' },
  { key: 'major', label: '上课专业', type: 'select', placeholder: '请选择上课专业', optionKey: 'majorOptions' },
  { key: 'assessmentType', label: '考核方式', type: 'select', placeholder: '请选择考核方式', optionKey: 'assessmentTypeOptions' },
  { key: 'teachingLanguage', label: '授课语言', type: 'select', placeholder: '请选择授课语言', optionKey: 'teachingLanguageOptions' },
  { key: 'requiredFlag', label: '是否必修', type: 'select', placeholder: '请选择是否必修', optionKey: 'requiredFlagOptions' },
]

// 默认展示：学期固定显示，常用条件与学期同层级展示
const defaultQueryKeys = ['termId', 'courseCode', 'courseName', 'credits', 'teachingClassCode', 'className', 'openDeptId', 'courseCategory']
const STORAGE_KEY_QUERY = 'co_active_query_keys_v2'
const STORAGE_KEY_PAGE = 'co_page_settings'

function loadActiveQueryKeys(): string[] {
  try {
    const stored = localStorage.getItem(STORAGE_KEY_QUERY)
    if (stored) {
      const keys = JSON.parse(stored)
      // 学期必须固定显示
      return Array.from(new Set(['termId', ...(Array.isArray(keys) ? keys : [])]))
    }
  } catch { /* ignore */ }
  return [...defaultQueryKeys]
}

const activeQueryKeys = ref<string[]>(loadActiveQueryKeys())

const visibleQueryFields = computed(() =>
  allQueryFields.filter(f => activeQueryKeys.value.includes(f.key))
)

const showAdvancedFilters = ref(false)
const MAX_BASE_FILTERS = 8
const baseFields = computed(() => visibleQueryFields.value.slice(0, MAX_BASE_FILTERS))
const advancedFields = computed(() => visibleQueryFields.value.slice(MAX_BASE_FILTERS))

/* ===================== Table Column Definitions ===================== */
const allColumns = [
  { key: 'courseInfo', label: '课程信息' },
  { key: 'teachingClass', label: '教学班' },
  { key: 'credits', label: '学分' },
  { key: 'schedule', label: '时间地点' },
  { key: 'teacher', label: '授课教师' },
  { key: 'requirements', label: '教学要求' },
]

/* ===================== Export Field Definitions ===================== */
const allExportFields = [
  { key: 'termName', label: '学期' },
  { key: 'courseName', label: '课程名称' },
  { key: 'courseCode', label: '课程代码' },
  { key: 'teachingClassCode', label: '教学班' },
  { key: 'credits', label: '学分' },
  { key: 'teacherName', label: '授课教师' },
  { key: 'openDeptName', label: '开课院系' },
  { key: 'major', label: '上课专业' },
  { key: 'className', label: '行政班' },
  { key: 'courseCategory', label: '课程类别' },
  { key: 'requiredLabel', label: '必修选修' },
  { key: 'campusName', label: '校区' },
  { key: 'scheduleText', label: '上课时间地点' },
  { key: 'sectionSummary', label: '典型节次' },
  { key: 'assessmentType', label: '考核方式' },
  { key: 'teachingLanguage', label: '教学语言' },
  { key: 'totalHours', label: '总学时' },
  { key: 'weeklyHours', label: '周学时' },
  { key: 'studentCountText', label: '选课人数' },
  { key: 'materialHint', label: '教学材料' },
]

/* ===================== Page Settings ===================== */
interface PageSettings {
  visibleColumns: string[]
  showIndex: boolean
  columnWidth: 'fit' | 'fill'
  density: 'compact' | 'medium' | 'loose'
  pageSize: number
}

function loadPageSettings(): PageSettings {
  const defaults: PageSettings = {
    visibleColumns: allColumns.map(c => c.key),
    showIndex: false,
    columnWidth: 'fit',
    density: 'medium',
    pageSize: 20,
  }
  try {
    const stored = localStorage.getItem(STORAGE_KEY_PAGE)
    if (stored) {
      const parsed = { ...defaults, ...JSON.parse(stored) }
      const columnKeys = new Set(allColumns.map(c => c.key))
      const normalizedColumns = Array.isArray(parsed.visibleColumns)
        ? parsed.visibleColumns
            .map((key: string) => key === 'material' ? 'requirements' : key)
            .filter((key: string) => columnKeys.has(key))
        : defaults.visibleColumns
      return {
        ...parsed,
        visibleColumns: normalizedColumns.length ? normalizedColumns : defaults.visibleColumns,
      }
    }
  } catch { /* ignore */ }
  return defaults
}

const pageSettings = reactive<PageSettings>(loadPageSettings())

function isColumnVisible(key: string) {
  return pageSettings.visibleColumns.includes(key)
}

/* ===================== State ===================== */
const loading = ref(false)
const exporting = ref(false)
const termOptions = ref<any[]>([])
const tableData = ref<any[]>([])
const total = ref(0)
const selectedRows = ref<any[]>([])
const tableRef = ref<any>(null)

const teacherOptions = ref<OptionItem[]>([])
const openDeptOptions = ref<OptionItem[]>([])
const campusOptions = ref<OptionItem[]>([])
const courseCategoryOptions = ref<OptionItem[]>([])
const businessTypeOptions = ref<OptionItem[]>([])
const majorOptions = ref<OptionItem[]>([])
const assessmentTypeOptions = ref<OptionItem[]>([])
const teachingLanguageOptions = ref<OptionItem[]>([])
const requiredFlagOptions: OptionItem[] = [
  { label: '必修', value: 'Y' },
  { label: '选修', value: 'N' },
]

const queryParams = reactive<Record<string, any>>({
  termId: undefined,
  courseName: undefined,
  courseCode: undefined,
  creditsMin: undefined,
  creditsMax: undefined,
  teachingClassCode: undefined,
  className: undefined,
  courseCategory: undefined,
  openDeptId: undefined,
  teacherId: undefined,
  campusName: undefined,
  businessType: undefined,
  major: undefined,
  assessmentType: undefined,
  teachingLanguage: undefined,
  requiredFlag: undefined,
  pageNum: 1,
  pageSize: pageSettings.pageSize,
})

/* ===================== Dialog State ===================== */
const showQueryConfigDialog = ref(false)
const showPageSettingsDialog = ref(false)
const showExportDialog = ref(false)
const showDetailDialog = ref(false)
const detailRow = ref<any>({})
const detailScheduleActiveName = ref('')

/* ===================== Computed helpers ===================== */
const currentTermLabel = computed(() => {
  const found = termOptions.value?.find((t: any) => t.value === queryParams.termId)
  return found?.label || (queryParams.termId ? String(queryParams.termId) : '未选择')
})

const tableTooltipProps = {
  effect: 'light' as const,
  placement: 'top' as const,
  teleported: true,
  persistent: false,
  popperOptions: {
    strategy: 'fixed' as const,
  },
}

function shouldShowTooltip(value: any, minLength = 14) {
  const text = String(value ?? '').trim()
  if (!text || text === '-' || text === '无' || text === '无需先修' || text === '默认开放') {
    return false
  }
  return text.length > minLength || /[\n;]/.test(text)
}

function getFieldOptions(field: QueryField): OptionItem[] {
  switch (field.optionKey) {
    case 'termOptions':
      return termOptions.value
    case 'teacherOptions':
      return teacherOptions.value
    case 'openDeptOptions':
      return openDeptOptions.value
    case 'campusOptions':
      return campusOptions.value
    case 'courseCategoryOptions':
      return courseCategoryOptions.value
    case 'businessTypeOptions':
      return businessTypeOptions.value
    case 'majorOptions':
      return majorOptions.value
    case 'assessmentTypeOptions':
      return assessmentTypeOptions.value
    case 'teachingLanguageOptions':
      return teachingLanguageOptions.value
    case 'requiredFlagOptions':
      return requiredFlagOptions
    default:
      return []
  }
}

function getOptionLabel(options: OptionItem[], value: any) {
  return options.find(item => String(item.value) === String(value))?.label || String(value)
}

function formatTagValue(field: QueryField): string {
  if (field.key === 'termId') return currentTermLabel.value
  if (field.key === 'credits') {
    const min = queryParams.creditsMin ?? ''
    const max = queryParams.creditsMax ?? ''
    return `${min || '-'} ~ ${max || '-'}`
  }
  const rawValue = queryParams[field.key]
  if (field.type === 'select') {
    return getOptionLabel(getFieldOptions(field), rawValue)
  }
  return String(rawValue)
}

function hasFieldValue(field: QueryField) {
  if (field.key === 'credits') {
    return queryParams.creditsMin !== undefined && queryParams.creditsMin !== null && queryParams.creditsMin !== ''
      || queryParams.creditsMax !== undefined && queryParams.creditsMax !== null && queryParams.creditsMax !== ''
  }
  const value = queryParams[field.key]
  return value !== undefined && value !== null && value !== ''
}

type ActiveFilterTag = { key: string; label: string; value: string; closable: boolean }
const activeFilterTags = computed<ActiveFilterTag[]>(() => {
  const tags: ActiveFilterTag[] = []
  for (const field of allQueryFields) {
    if (!hasFieldValue(field)) continue
    tags.push({
      key: field.key,
      label: field.label,
      value: formatTagValue(field),
      closable: field.key !== 'termId',
    })
  }
  return tags
})

const selectedExportFieldCount = computed(() =>
  allExportFields.filter(ef => exportFieldSelection[ef.key]).length
)

type ExportGroup = { key: string; label: string; fields: { key: string; label: string }[] }
const exportFieldGroups = computed<ExportGroup[]>(() => {
  const byKey = new Map(allExportFields.map(f => [f.key, f]))
  const pick = (keys: string[]) => keys.map(k => byKey.get(k)).filter(Boolean) as any[]
  return [
    {
      key: 'basic',
      label: '基本信息',
      fields: pick(['termName', 'courseName', 'courseCode', 'credits', 'courseCategory', 'requiredLabel', 'openDeptName', 'campusName']),
    },
    {
      key: 'teaching',
      label: '教学信息',
      fields: pick(['teachingClassCode', 'teacherName', 'assessmentType', 'teachingLanguage']),
    },
    {
      key: 'time',
      label: '时间学时',
      fields: pick(['scheduleText', 'sectionSummary', 'totalHours', 'weeklyHours']),
    },
    {
      key: 'other',
      label: '其他',
      fields: pick(['major', 'className', 'studentCountText', 'materialHint']),
    },
  ].map(g => ({ ...g, fields: g.fields.filter(Boolean) }))
})

type DetailMetric = { label: string; value: string; hint: string }
const detailMetrics = computed<DetailMetric[]>(() => {
  const r = detailRow.value || {}
  return [
    {
      label: '学分',
      value: String(r.credits ?? '-'),
      hint: '课程学分',
    },
    {
      label: '教学班',
      value: r.teachingClassCode || '-',
      hint: r.teachingClassName || r.className || '教学班标识',
    },
    {
      label: '总学时',
      value: String(r.totalHours ?? '-'),
      hint: `周学时 ${r.weeklyHours ?? '-'}`,
    },
    {
      label: '选课情况',
      value: r.studentCountText || '-',
      hint: r.remainingSeats != null ? `剩余名额 ${r.remainingSeats}` : '当前已选人数',
    },
  ]
})

const detailIntroText = computed(() => {
  const r = detailRow.value || {}
  return r.intro || '暂无课程简介，建议先查看教学班、教师安排和上课时间地点。'
})

type DetailInfoItem = { label: string; value: string }
type DetailInfoGroup = { key: string; title: string; items: DetailInfoItem[] }
const detailInfoGroups = computed<DetailInfoGroup[]>(() => {
  const r = detailRow.value || {}
  return [
    {
      key: 'basic',
      title: '基本信息',
      items: [
        { label: '课程类别', value: r.courseCategory || '-' },
        { label: '课程属性', value: r.subjectType || '-' },
        { label: '开课部门', value: r.openDeptName || '-' },
        { label: '上课校区', value: r.campusName || '-' },
      ],
    },
    {
      key: 'teaching',
      title: '教学信息',
      items: [
        { label: '授课教师', value: r.teacherName || '-' },
        { label: '授课语言', value: r.teachingLanguage || '-' },
        { label: '考核方式', value: r.assessmentType || '-' },
        { label: '教学班名称', value: r.teachingClassName || r.className || '-' },
      ],
    },
    {
      key: 'requirements',
      title: '选课与要求',
      items: [
        { label: '先修课程', value: r.prerequisiteCourse || '无' },
        { label: '等级要求', value: r.courseLevelRequirement || '无' },
        { label: '任务类型', value: r.taskType || '-' },
        { label: '开课对象', value: r.className || r.major || '-' },
      ],
    },
  ]
})

type DetailScheduleCard = {
  date: string
  weekLabel: string
  weeksText: string
  sectionText: string
  timeRange: string
  classroom: string
}
const detailScheduleCards = computed<DetailScheduleCard[]>(() => {
  const details = Array.isArray(detailRow.value?.scheduleDetails) ? detailRow.value.scheduleDetails : []
  return details.slice(0, 12).map((item: any) => ({
    date: item?.date || '-',
    weekLabel: item?.weekLabel || '-',
    weeksText: item?.weeksText || '未配置周次',
    sectionText: item?.sectionText || '-',
    timeRange: item?.startTime && item?.endTime ? `${item.startTime}-${item.endTime}` : '',
    classroom: item?.classroom || item?.classroomName || '-',
  }))
})

const detailScheduleSummaryLines = computed(() => {
  const text = String(detailRow.value?.scheduleText || '').trim()
  if (!text) return []
  return splitLines(text)
})

const detailNoteText = computed(() => {
  const r = detailRow.value || {}
  return r.remark || r.classroom || r.scheduleText || '暂无补充说明。'
})

// Temp state for query config dialog
const tempActiveQueryKeys = reactive<Record<string, boolean>>({})
const tempActiveCount = computed(() => Object.values(tempActiveQueryKeys).filter(Boolean).length)
const tempSelectedFields = computed(() =>
  allQueryFields.filter(f => tempActiveQueryKeys[f.key])
)
const queryConfigKeyword = ref('')
const filteredAllQueryFields = computed(() => {
  const kw = queryConfigKeyword.value.trim()
  if (!kw) return allQueryFields
  return allQueryFields.filter(f => f.label.includes(kw) || f.key.includes(kw))
})

// Temp state for page settings dialog
const tempPageSettings = reactive<PageSettings>({
  visibleColumns: [],
  showIndex: false,
  columnWidth: 'fit',
  density: 'medium',
  pageSize: 20,
})

// Export field selection
const exportFieldSelection = reactive<Record<string, boolean>>({})
const tempVisibleColumnCount = computed(() => tempPageSettings.visibleColumns.length)

/* ===================== Watchers for dialog open ===================== */
function initQueryConfigTemp() {
  allQueryFields.forEach(f => {
    tempActiveQueryKeys[f.key] = activeQueryKeys.value.includes(f.key)
  })
}

function initPageSettingsTemp() {
  Object.assign(tempPageSettings, {
    visibleColumns: [...pageSettings.visibleColumns],
    showIndex: pageSettings.showIndex,
    columnWidth: pageSettings.columnWidth,
    density: pageSettings.density,
    pageSize: pageSettings.pageSize,
  })
}

function initExportFieldSelection() {
  allExportFields.forEach(ef => {
    exportFieldSelection[ef.key] = true
  })
}

/* ===================== Data Loading ===================== */
async function loadTerms() {
  const res = await listPortalTermOptions()
  termOptions.value = (res.data || []).map((item: any) => ({
    ...item,
    fullLabel: item.label,
    label: item.termCode || item.termName || item.label,
  }))
  const current = termOptions.value.find((t: any) => t.isCurrent === '1')
  if (!queryParams.termId && current) {
    queryParams.termId = current.value
  } else if (!queryParams.termId && termOptions.value.length) {
    queryParams.termId = termOptions.value[0].value
  }
}

async function loadCourseOfferingFilterOptions() {
  const res = await getPortalCourseOfferingFilterOptions({ termId: queryParams.termId })
  const data = res.data || {}
  teacherOptions.value = data.teacherOptions || []
  openDeptOptions.value = data.openDeptOptions || []
  campusOptions.value = data.campusOptions || []
  courseCategoryOptions.value = data.courseCategoryOptions || []
  businessTypeOptions.value = data.businessTypeOptions || []
  majorOptions.value = data.majorOptions || []
  assessmentTypeOptions.value = data.assessmentTypeOptions || []
  teachingLanguageOptions.value = data.teachingLanguageOptions || []
}

async function loadData() {
  loading.value = true
  try {
    const params: Record<string, any> = {}
    for (const key of Object.keys(queryParams)) {
      const val = queryParams[key]
      if (val !== undefined && val !== null && val !== '') {
        params[key] = val
      }
    }
    const res = await listPortalCourseOfferings(params)
    tableData.value = res.rows || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

/* ===================== Actions ===================== */
function handleSearch() {
  queryParams.pageNum = 1
  loadData()
}

async function handleTermChange() {
  if (!queryParams.termId) return
  await loadCourseOfferingFilterOptions()
  handleSearch()
}

function handleReset() {
  const keep = queryParams.termId
  for (const key of Object.keys(queryParams)) {
    if (key === 'pageNum') { queryParams[key] = 1; continue }
    if (key === 'pageSize') continue
    queryParams[key] = undefined
  }
  queryParams.termId = keep
  loadData()
}

function onSelectionChange(rows: any[]) {
  selectedRows.value = rows
}

function handleRowClick(row: any, _column: any, event: MouseEvent) {
  const target = event.target as HTMLElement | null
  if (target?.closest('.course-name-link, .el-checkbox, .el-tag, button, a')) {
    return
  }
  tableRef.value?.toggleRowSelection(row)
  tableRef.value?.setCurrentRow?.(row)
}

function openDetail(row: any) {
  detailRow.value = row
  detailScheduleActiveName.value = ''
  showDetailDialog.value = true
}

function clearFilter(key: string) {
  if (key === 'termId') return
  if (key === 'credits') {
    queryParams.creditsMin = undefined
    queryParams.creditsMax = undefined
  } else if (key in queryParams) {
    queryParams[key] = undefined
  } else {
    return
  }
  queryParams.pageNum = 1
  loadData()
}

function splitLines(text: string) {
  return String(text).split(/\r?\n/).filter(Boolean)
}

function requiredText(row: any) {
  if (!row) return ''
  const flag = row.requiredFlag
  const label = row.requiredLabel
  if (label) return String(label)
  if (flag === 'Y') return '必修'
  if (flag === 'N') return '选修'
  return ''
}

function requiredType(row: any) {
  const t = requiredText(row)
  return t.includes('必') ? 'danger' : 'success'
}

/* ===================== Query Config Dialog ===================== */
function toggleAllQueryFields(val: boolean) {
  allQueryFields.forEach(f => {
    if (f.key === 'termId') { tempActiveQueryKeys[f.key] = true; return }
    tempActiveQueryKeys[f.key] = val
  })
}

function invertQueryFields() {
  allQueryFields.forEach(f => {
    if (f.key === 'termId') { tempActiveQueryKeys[f.key] = true; return }
    tempActiveQueryKeys[f.key] = !tempActiveQueryKeys[f.key]
  })
}

function saveQueryConfig() {
  const keys = Array.from(
    new Set(['termId', ...allQueryFields.filter(f => tempActiveQueryKeys[f.key]).map(f => f.key)])
  )
  activeQueryKeys.value = keys
  if (keys.filter(key => key !== 'termId').length <= MAX_BASE_FILTERS) {
    showAdvancedFilters.value = false
  }
  localStorage.setItem(STORAGE_KEY_QUERY, JSON.stringify(keys))
  showQueryConfigDialog.value = false
}

function resetQueryConfigToDefault() {
  allQueryFields.forEach(f => {
    tempActiveQueryKeys[f.key] = f.key === 'termId' ? true : defaultQueryKeys.includes(f.key)
  })
}

/* ===================== Page Settings Dialog ===================== */
function getDefaultPageSettings(): PageSettings {
  return {
    visibleColumns: allColumns.map(c => c.key),
    showIndex: false,
    columnWidth: 'fit',
    density: 'medium',
    pageSize: 20,
  }
}

function savePageSettings() {
  Object.assign(pageSettings, tempPageSettings)
  queryParams.pageSize = tempPageSettings.pageSize
  localStorage.setItem(STORAGE_KEY_PAGE, JSON.stringify({ ...tempPageSettings }))
  showPageSettingsDialog.value = false
  loadData()
}

function resetPageSettingsToDefault() {
  Object.assign(tempPageSettings, getDefaultPageSettings())
}

/* ===================== Export ===================== */
function toggleAllExportFields(val: boolean) {
  allExportFields.forEach(ef => { exportFieldSelection[ef.key] = val })
}

function invertExportFields() {
  allExportFields.forEach(ef => { exportFieldSelection[ef.key] = !exportFieldSelection[ef.key] })
}

async function handleExport() {
  const selectedFieldKeys = allExportFields.filter(ef => exportFieldSelection[ef.key]).map(ef => ef.key)
  if (!selectedFieldKeys.length) {
    ElMessage.warning('请至少选择一个导出字段')
    return
  }
  exporting.value = true
  try {
    const params: Record<string, any> = {}
    for (const key of Object.keys(queryParams)) {
      if (key === 'pageNum' || key === 'pageSize') continue
      const val = queryParams[key]
      if (val !== undefined && val !== null && val !== '') {
        params[key] = val
      }
    }
    params.exportFields = selectedFieldKeys
    if (selectedRows.value.length) {
      params.classCourseIds = selectedRows.value.map((r: any) => r.id)
    }
    const res = await exportPortalCourseOfferings(params)
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '全校开课查询数据.xlsx'
    link.click()
    window.URL.revokeObjectURL(url)
    showExportDialog.value = false
    ElMessage.success('导出成功')
  } catch {
    ElMessage.error('导出失败')
  } finally {
    exporting.value = false
  }
}

/* ===================== Dialog open hooks ===================== */
watch(showQueryConfigDialog, (val) => { if (val) initQueryConfigTemp() })
watch(showPageSettingsDialog, (val) => { if (val) initPageSettingsTemp() })
watch(showExportDialog, (val) => { if (val) initExportFieldSelection() })
watch(showQueryConfigDialog, (val) => { if (!val) queryConfigKeyword.value = '' })

/* ===================== Init ===================== */
onMounted(async () => {
  await loadTerms()
  await loadCourseOfferingFilterOptions()
  await loadData()
})
</script>

<style scoped>
.course-offerings-page {
  padding: 14px 18px;
}

/* ===================== Design tokens ===================== */
.course-offerings-page {
  --co-primary: #2563eb;
  --co-text: #0f172a;
  --co-muted: #475569;
  --co-faint: #94a3b8;
  --co-bg: #f8fafc;
  --co-bg2: #f1f5f9;
  --co-border: #e2e8f0;
  --co-radius-card: 10px;
  --co-radius-pill: 6px;
  --co-ease: 0.25s ease;
}

/* ===================== Header ===================== */
.co-header {
  display: grid;
  grid-template-columns: 1.2fr 1fr;
  gap: 18px;
  margin-bottom: 16px;
}

.co-header__left {
  background: #fff;
  border: 1px solid var(--co-border);
  border-radius: var(--co-radius-card);
  padding: 16px 18px;
}

.co-eyebrow {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  border-radius: 999px;
  background: var(--co-bg2);
  color: var(--co-muted);
  font-weight: 800;
  font-size: 12px;
}

.co-title-row {
  margin-top: 10px;
}

.co-title {
  margin: 0;
  font-size: 20px;
  line-height: 1.2;
  color: var(--co-text);
  font-weight: 900;
}

.co-subtitle {
  margin: 10px 0 0;
  color: var(--co-muted);
  font-size: 13px;
  line-height: 1.65;
}

.co-header__right {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.co-stat-card {
  border: 1px solid var(--co-border);
  border-radius: var(--co-radius-card);
  padding: 12px 12px;
  background: #fff;
  transition: transform var(--co-ease), box-shadow var(--co-ease);
}

.co-stat-card:hover {
  box-shadow: 0 10px 22px rgba(15, 23, 42, 0.08);
}

.co-stat-label {
  color: var(--co-faint);
  font-size: 12px;
  font-weight: 600;
}

.co-stat-value {
  margin-top: 6px;
  color: var(--co-text);
  font-size: 20px;
  font-weight: 900;
  letter-spacing: -0.02em;
}

.co-stat-hint {
  margin-top: 6px;
  color: var(--co-muted);
  font-size: 12px;
}

/* Search Card */
.search-card {
  margin-bottom: 12px;
  padding: 12px 14px;
  border-radius: var(--co-radius-card);
  border: 1px solid rgba(191, 219, 254, 0.88);
  box-shadow: 0 8px 20px rgba(15, 23, 42, 0.04);
}

.search-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 10px;
}

.search-head__left {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.search-head__icon {
  width: 30px;
  height: 30px;
  border-radius: 8px;
  background: rgba(37, 99, 235, 0.1);
  color: var(--co-primary);
  display: flex;
  align-items: center;
  justify-content: center;
}

.search-head__title {
  color: var(--co-text);
  font-weight: 900;
  font-size: 13px;
}

.search-head__desc {
  margin-top: 4px;
  color: var(--co-muted);
  font-size: 12px;
  line-height: 1.5;
}

.search-head__right {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.link-btn {
  font-weight: 700;
  color: var(--co-primary);
}

.rotate-when-open {
  transition: transform var(--co-ease);
}
.rotate-when-open.open {
  transform: rotate(180deg);
}

.search-grid {
  display: grid;
  gap: 10px 12px;
}

.search-grid--primary {
  grid-template-columns: repeat(8, minmax(0, 1fr));
}

.search-grid--advanced {
  grid-template-columns: repeat(4, minmax(0, 1fr));
}

.search-field label {
  display: block;
  margin-bottom: 8px;
  color: var(--co-muted);
  font-size: 12px;
  font-weight: 600;
  line-height: 1;
}

.search-field :deep(.el-input__wrapper),
.search-field :deep(.el-select__wrapper),
.search-field :deep(.el-textarea__inner),
.credit-range__input :deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 0 0 1px rgba(203, 213, 225, 0.88) inset;
  min-height: 38px;
}

.search-field :deep(.el-input__wrapper:hover),
.search-field :deep(.el-select__wrapper:hover),
.credit-range__input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px rgba(148, 163, 184, 0.96) inset;
}

.search-field :deep(.el-input__wrapper.is-focus),
.search-field :deep(.el-select__wrapper.is-focused),
.credit-range__input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px rgba(37, 99, 235, 0.9) inset;
}

.search-field--term :deep(.el-input__wrapper),
.search-field--term :deep(.el-select__wrapper) {
  background: linear-gradient(180deg, rgba(37, 99, 235, 0.08), rgba(37, 99, 235, 0.03));
  box-shadow: 0 0 0 1px rgba(37, 99, 235, 0.28) inset;
}

.credit-range {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto minmax(0, 1fr);
  align-items: center;
  gap: 8px;
}

.credit-range__input {
  width: 100%;
}

.credit-range__input :deep(.el-input-number) {
  width: 100%;
}

.credit-range__input :deep(.el-input__inner) {
  text-align: left;
}

.credit-range__separator {
  color: var(--co-faint);
  font-size: 14px;
  font-weight: 700;
}

.search-advanced-panel {
  display: grid;
  grid-template-rows: 0fr;
  transition: grid-template-rows 0.28s ease, opacity 0.2s ease, margin-top 0.28s ease;
  opacity: 0;
  margin-top: 0;
}

.search-advanced-panel.open {
  grid-template-rows: 1fr;
  opacity: 1;
  margin-top: 12px;
}

.search-advanced-panel__inner {
  min-height: 0;
  overflow: hidden;
}

.search-advanced__divider {
  height: 1px;
  background: var(--co-border);
  margin: 10px 0 14px;
}

.active-tags {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px dashed var(--co-border);
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.active-tags__label {
  color: var(--co-faint);
  font-size: 12px;
  line-height: 28px;
  flex: 0 0 auto;
}

.active-tags__list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}

.active-tag {
  border-radius: var(--co-radius-pill);
}

.active-tag__k {
  font-weight: 800;
  margin-right: 6px;
}

.active-tag__v {
  color: var(--co-muted);
}

.search-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid var(--co-bg2);
  justify-content: flex-end;
}

:global(.course-offerings-popper.el-popper),
:global(.course-offerings-popper.el-select__popper) {
  z-index: 5205 !important;
}

:global(.course-offerings-dialog-modal.el-overlay) {
  z-index: 5200 !important;
}

/* Toolbar */
.toolbar-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
  padding: 10px 12px;
  border-radius: var(--co-radius-card);
  border: 1px solid var(--co-border);
  background: #fff;
}

.toolbar-left,
.toolbar-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.toolbar-status {
  color: var(--co-muted);
  font-size: 12px;
}

.toolbar-status__n {
  color: var(--co-text);
  font-weight: 900;
}

.toolbar-total {
  color: var(--co-muted);
  font-size: 12px;
  margin-right: 6px;
}

.toolbar-total__n {
  color: var(--co-text);
  font-weight: 900;
}

/* Table */
.table-card {
  padding: 0;
  overflow: hidden;
  border-radius: var(--co-radius-card);
}

.course-info-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.course-info-cell strong {
  color: var(--co-text);
  font-size: 14px;
  font-weight: 700;
}

.course-info-cell span {
  color: #64748b;
  font-size: 12px;
  line-height: 1.6;
}

.course-info-top {
  display: flex;
  align-items: baseline;
  gap: 10px;
  flex-wrap: wrap;
}

.course-code {
  color: var(--co-faint);
  font-size: 12px;
  font-weight: 700;
  padding: 2px 8px;
  border-radius: 999px;
  background: var(--co-bg2);
}

.course-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 2px;
}

.pill {
  border-radius: var(--co-radius-pill);
  font-weight: 700;
}

.course-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--co-muted);
  font-size: 12px;
}

.course-meta--secondary {
  color: var(--co-faint);
}

.course-meta .dot {
  color: var(--co-faint);
}

.capacity {
  margin-top: 4px;
  padding: 10px 10px;
  border-radius: 10px;
  background: var(--co-bg);
  border: 1px solid var(--co-bg2);
}

.capacity__label {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 12px;
  color: var(--co-muted);
  margin-bottom: 6px;
}

.capacity__n {
  color: var(--co-text);
  font-weight: 900;
}

.capacity__bar :deep(.el-progress-bar__inner) {
  background: var(--co-primary);
}

.course-name-link {
  cursor: pointer;
  transition: color var(--co-ease);
  display: inline-block;
  max-width: 220px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-name-link:hover {
  color: var(--co-primary) !important;
}

.course-intro {
  color: #64748b;
  font-size: 12px;
  line-height: 1.55;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.schedule-cell {
  color: #334155;
  font-size: 13px;
  line-height: 1.7;
}

.schedule-lines {
  margin-top: 6px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.schedule-line {
  color: #334155;
  font-size: 12px;
  line-height: 1.6;
}

.schedule-line__text {
  display: inline-block;
  max-width: 100%;
  vertical-align: top;
}

.schedule-line--ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.schedule-line--head {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--co-muted);
  font-weight: 700;
  font-size: 12px;
}

.muted {
  color: var(--co-faint);
}

.teacher-cell {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: #334155;
  font-size: 13px;
  max-width: 100%;
}

.teacher-cell span {
  display: inline-block;
  max-width: 110px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.teaching-class-cell {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.teaching-class-cell__head {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.teaching-class-chip {
  display: inline-flex;
  align-items: center;
  padding: 2px 8px;
  border-radius: 999px;
  background: rgba(37, 99, 235, 0.08);
  color: var(--co-primary);
  font-size: 12px;
  font-weight: 700;
}

.teaching-class-name {
  color: #64748b;
  font-size: 13px;
  line-height: 1.4;
  font-weight: 700;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.teaching-class-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--co-faint);
  font-size: 12px;
}

.code-pill {
  display: inline-flex;
  align-items: center;
  padding: 3px 8px;
  border-radius: 8px;
  border: 1px solid var(--co-border);
  background: #fff;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace;
  font-size: 12px;
  color: var(--co-text);
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.credit-badge {
  width: 34px;
  height: 34px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: rgba(37, 99, 235, 0.1);
  color: var(--co-primary);
  font-weight: 900;
  font-size: 13px;
}

.table-empty {
  padding: 24px 0 10px;
}

.empty-illus {
  width: 110px;
  height: 74px;
  position: relative;
  margin: 0 auto;
}

.empty-illus__dot {
  position: absolute;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  left: 8px;
  top: 14px;
  background: rgba(37, 99, 235, 0.18);
}

.empty-illus__card {
  position: absolute;
  left: 28px;
  top: 10px;
  width: 72px;
  height: 52px;
  border-radius: 12px;
  border: 1px solid var(--co-border);
  background: linear-gradient(180deg, #fff, var(--co-bg));
  box-shadow: 0 16px 28px rgba(15, 23, 42, 0.08);
}

.detail-schedule {
  line-height: 1.7;
}

.table-pagination {
  padding: 16px 22px;
  justify-content: flex-end;
}

/* Density */
.density-compact :deep(.el-table__row td) {
  padding: 4px 0;
}

.density-medium :deep(.el-table__row td) {
  padding: 8px 0;
}

.density-loose :deep(.el-table__row td) {
  padding: 14px 0;
}

/* Row hover indicator */
:deep(.el-table__body tr:hover > td) {
  background: rgba(37, 99, 235, 0.05) !important;
}

:deep(.el-table__body tr) {
  position: relative;
  cursor: pointer;
}

:deep(.el-table__body tr.current-row > td) {
  background: rgba(37, 99, 235, 0.07) !important;
}

:deep(.el-table__body tr:hover) {
  box-shadow: inset 3px 0 0 var(--co-primary);
}

/* Query Config Dialog */
.query-config-body {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  min-height: 320px;
}

.query-config-left,
.query-config-right {
  border: 1px solid #e2e8f0;
  border-radius: var(--co-radius-card);
  overflow: hidden;
}

.query-config-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 14px;
  background: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
  font-size: 13px;
  font-weight: 600;
  color: #334155;
}

.query-config-search {
  padding: 12px 14px 0;
}

.query-config-list {
  padding: 12px 14px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.query-config-selected {
  padding: 12px 14px;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  align-content: start;
  gap: 10px 12px;
}

.query-config-scroll {
  height: 380px;
}

.selected-item {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.selected-item :deep(.el-tag) {
  width: 100%;
  justify-content: space-between;
  overflow: hidden;
}

.selected-item__idx {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: rgba(37, 99, 235, 0.12);
  color: var(--co-primary);
  font-weight: 900;
  font-size: 12px;
}

.empty-hint {
  color: #94a3b8;
  font-size: 13px;
  grid-column: 1 / -1;
}

.requirements-cell {
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-width: 0;
}

.requirements-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.requirements-tag {
  display: inline-flex;
  align-items: center;
  padding: 2px 8px;
  border-radius: 999px;
  background: #f1f5f9;
  color: #475569;
  font-size: 12px;
  font-weight: 700;
}

.requirements-tag--primary {
  background: rgba(37, 99, 235, 0.1);
  color: var(--co-primary);
}

.requirements-line {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.requirements-line--muted .requirements-value {
  color: #64748b;
}

.requirements-label {
  flex: 0 0 auto;
  color: var(--co-faint);
  font-size: 12px;
  font-weight: 700;
}

.requirements-value {
  min-width: 0;
  color: var(--co-text);
  font-size: 13px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.tooltip-multiline {
  max-width: 420px;
  white-space: pre-line;
  line-height: 1.6;
}

/* Export Dialog */
.export-intro {
  padding: 12px 14px;
  border: 1px solid var(--co-border);
  background: linear-gradient(180deg, rgba(37, 99, 235, 0.06), rgba(37, 99, 235, 0.02));
  border-radius: var(--co-radius-card);
  margin-bottom: 14px;
}

.export-intro__title {
  font-weight: 900;
  color: var(--co-text);
  font-size: 14px;
}

.export-intro__desc {
  margin-top: 6px;
  color: var(--co-muted);
  font-size: 12px;
}

.export-scope {
  color: var(--co-text);
  font-weight: 900;
}

.export-actions-top {
  display: flex;
  gap: 12px;
  margin-bottom: 14px;
  align-items: center;
  flex-wrap: wrap;
}

.export-picked {
  margin-left: auto;
  color: var(--co-muted);
  font-size: 12px;
}

.export-picked__n {
  color: var(--co-text);
  font-weight: 900;
}

.export-groups {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.export-group__title {
  font-weight: 900;
  color: var(--co-text);
  font-size: 13px;
  margin: 4px 0 10px;
}

.export-field-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
}

/* Page Settings */
.page-settings {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.ps-section {
  padding: 12px 12px;
  border: 1px solid var(--co-border);
  border-radius: var(--co-radius-card);
  background: #fff;
}

.ps-title {
  font-weight: 900;
  color: var(--co-text);
  font-size: 14px;
}

.ps-cols {
  margin-top: 10px;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px 14px;
}

.ps-inline {
  margin-top: 10px;
}

.ps-radio-group {
  margin-top: 10px;
}

.ps-radio-group :deep(.el-radio-button__inner) {
  min-width: 64px;
}

.ps-radio-group--sizes :deep(.el-radio-button__inner) {
  min-width: 52px;
}

/* Detail */
.detail-wrap {
  padding: 2px 2px 4px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.detail-hero {
  border: 1px solid var(--co-border);
  border-radius: 14px;
  padding: 16px 16px;
  background: linear-gradient(135deg, rgba(37, 99, 235, 0.08), rgba(248, 250, 252, 0.96));
}

.detail-hero__top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.detail-hero__eyebrow {
  color: var(--co-primary);
  font-size: 12px;
  font-weight: 800;
}

.detail-header__main {
  margin-top: 10px;
}

.detail-name {
  color: var(--co-text);
  font-weight: 900;
  font-size: 24px;
  line-height: 1.2;
}

.detail-code {
  color: var(--co-faint);
  font-weight: 800;
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.7);
  border: 1px solid rgba(226, 232, 240, 0.8);
}

.detail-header__tags {
  margin-top: 12px;
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.detail-hero__intro {
  margin-top: 12px;
  color: #475569;
  font-size: 13px;
  line-height: 1.7;
}

.detail-metrics {
  margin-top: 14px;
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10px;
}

.detail-metric {
  padding: 12px 12px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.88);
  border: 1px solid rgba(226, 232, 240, 0.88);
}

.detail-metric__label {
  color: var(--co-faint);
  font-size: 12px;
  font-weight: 700;
}

.detail-metric__value {
  margin-top: 6px;
  color: var(--co-text);
  font-size: 18px;
  font-weight: 900;
}

.detail-metric__hint {
  margin-top: 4px;
  color: #64748b;
  font-size: 12px;
}

.detail-layout {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.detail-panel {
  border: 1px solid var(--co-border);
  border-radius: 14px;
  background: #fff;
  padding: 14px 14px;
}

.detail-panel--wide {
  grid-column: 1 / -1;
}

.detail-panel__title {
  color: var(--co-text);
  font-size: 15px;
  font-weight: 900;
  margin-bottom: 12px;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px 12px;
}

.detail-item {
  border: 1px solid var(--co-border);
  border-radius: 12px;
  background: #f8fafc;
  padding: 10px 12px;
}

.detail-item__k {
  color: var(--co-faint);
  font-size: 12px;
  font-weight: 700;
}

.detail-item__v {
  margin-top: 6px;
  color: var(--co-text);
  font-size: 13px;
  line-height: 1.55;
}

.detail-schedule-card {
  border: 1px solid var(--co-border);
  border-radius: 14px;
  padding: 12px 12px;
  background: linear-gradient(180deg, #fff, #f8fafc);
}

.detail-schedule-summary {
  border: 1px solid var(--co-border);
  border-radius: 14px;
  background: #f8fafc;
  padding: 12px 14px;
}

.detail-schedule-summary__title {
  color: var(--co-text);
  font-size: 13px;
  font-weight: 800;
}

.detail-schedule-summary__list {
  margin-top: 10px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.detail-schedule-summary__line {
  color: #475569;
  font-size: 13px;
  line-height: 1.7;
}

.detail-schedule-collapse {
  margin-top: 12px;
  border-top: 0;
}

.detail-schedule-collapse :deep(.el-collapse-item__header) {
  font-size: 13px;
  font-weight: 700;
  color: var(--co-text);
  padding: 0 14px;
  border-radius: 12px;
  background: #fff;
  border: 1px solid var(--co-border);
}

.detail-schedule-collapse :deep(.el-collapse-item__wrap) {
  border-bottom: 0;
  background: transparent;
}

.detail-schedule-collapse :deep(.el-collapse-item__content) {
  padding: 10px 0 2px;
}

.detail-schedule-accordion__title {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  min-width: 0;
}

.detail-schedule-accordion__day {
  color: var(--co-text);
  font-weight: 900;
}

.detail-schedule-accordion__section {
  color: var(--co-primary);
  font-weight: 700;
}

.detail-schedule-accordion__time {
  color: var(--co-faint);
  font-size: 12px;
}

.detail-schedule-scroll {
  max-height: 320px;
}

.detail-schedule-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding-right: 4px;
}

.detail-schedule-card__head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
}

.detail-schedule-card__day {
  color: var(--co-text);
  font-weight: 900;
  font-size: 14px;
}

.detail-schedule-card__weeks {
  color: var(--co-primary);
  font-size: 12px;
  font-weight: 700;
}

.detail-schedule-card__time {
  margin-top: 10px;
  color: var(--co-text);
  font-size: 13px;
  font-weight: 700;
}

.detail-schedule-card__place {
  margin-top: 8px;
  color: #475569;
  font-size: 13px;
  line-height: 1.6;
}

.detail-schedule-card__date {
  margin-top: 8px;
  color: var(--co-faint);
  font-size: 12px;
}

.detail-note {
  border-radius: 12px;
  background: #f8fafc;
  border: 1px dashed var(--co-border);
  padding: 12px 14px;
  color: #475569;
  font-size: 13px;
  line-height: 1.75;
}

.detail-empty-panel {
  border-radius: 12px;
  background: #f8fafc;
  border: 1px dashed var(--co-border);
  padding: 16px 14px;
  color: var(--co-faint);
  font-size: 13px;
  text-align: center;
}

@media (max-width: 960px) {
  .course-offerings-page {
    padding: 14px;
  }

  .search-grid--primary {
    grid-template-columns: repeat(4, minmax(0, 1fr));
  }

  .search-grid--advanced {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .export-field-grid {
    grid-template-columns: repeat(3, 1fr);
  }

  .detail-layout {
    grid-template-columns: 1fr;
  }

  .detail-metrics {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

}

@media (max-width: 640px) {
  .search-grid--primary,
  .search-grid--advanced {
    grid-template-columns: 1fr;
  }

  .query-config-body {
    grid-template-columns: 1fr;
  }

  .query-config-selected {
    grid-template-columns: 1fr;
  }

  .export-field-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .toolbar-card {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }

  .toolbar-right {
    justify-content: flex-end;
  }

  .search-head {
    flex-direction: column;
  }

  .search-head__right {
    width: 100%;
    justify-content: flex-start;
  }

  .search-actions {
    justify-content: stretch;
  }

  .search-actions :deep(.el-button) {
    flex: 1;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }

  .detail-metrics {
    grid-template-columns: 1fr;
  }
}
</style>
