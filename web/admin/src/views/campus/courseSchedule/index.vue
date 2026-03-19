<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16">
      <el-form-item label="学期"><el-select v-model="queryParams.termId" filterable clearable style="width:220px"><el-option v-for="item in termOptions" :key="`term-${item.value}`" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item label="部门">
        <el-tree-select v-model="queryParams.deptId" :data="deptOptions" :props="{ value: 'id', label: 'label', children: 'children' }" value-key="id" check-strictly clearable filterable style="width:220px" />
      </el-form-item>
      <el-form-item label="班级"><el-select v-model="queryParams.classId" filterable clearable placeholder="请选择班级" style="width:200px"><el-option v-for="item in classOptions" :key="`class-${item.value}`" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item label="课程"><el-select v-model="queryParams.courseId" filterable clearable placeholder="请选择课程" style="width:200px"><el-option v-for="item in courseFilterOptions" :key="`course-${item.value}`" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item label="教室"><el-select v-model="queryParams.classroomId" filterable clearable style="width:200px"><el-option v-for="item in classroomOptions" :key="`cr-${item.value}`" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item label="星期"><el-select v-model="queryParams.weekDay" clearable style="width:120px"><el-option v-for="item in weekOptions" :key="`w-${item.value}`" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item label="周次"><el-select v-model="queryParams.weekNo" clearable style="width:120px"><el-option v-for="item in weekNumberOptions" :key="`wn-${item}`" :label="`第 ${item} 周`" :value="item" /></el-select></el-form-item>
      <el-form-item><el-button type="primary" icon="Search" @click="handleSearch">搜索</el-button><el-button icon="Refresh" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb16">
      <el-col :span="1.5"><el-button type="primary" plain icon="Plus" @click="handleAdd">新增</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()">修改</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()">删除</el-button></el-col>
      <el-col :span="1.8"><el-button type="warning" plain icon="MagicStick" :loading="autoArrangeLoading" @click="openAutoArrangeDialog">自动排课</el-button></el-col>
      <el-col :span="1.8"><el-button type="info" plain icon="Upload" @click="openImportDialog">Excel导入</el-button></el-col>
      <el-col :span="4">
        <el-radio-group v-model="viewMode" size="default">
          <el-radio-button label="table">列表视图</el-radio-button>
          <el-radio-button label="board">周课表</el-radio-button>
        </el-radio-group>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="handleSearch" />
    </el-row>

    <div v-if="scheduleFilterTags.length" class="filter-tag-bar">
      <span class="filter-tag-bar__label">当前排课范围</span>
      <el-tag v-for="tag in scheduleFilterTags" :key="tag.label" size="small" round>{{ tag.label }}</el-tag>
      <el-button link type="primary" @click="resetQuery">清空筛选</el-button>
    </div>

    <!-- 列表视图 -->
    <el-table v-if="viewMode === 'table'" v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column label="班级" min-width="140"><template #default="{ row }">{{ getMergedSchedule(row)?.className || '-' }}</template></el-table-column>
      <el-table-column label="课程" min-width="160"><template #default="{ row }">{{ getMergedSchedule(row)?.courseName || '-' }}</template></el-table-column>
      <el-table-column label="教师" min-width="100"><template #default="{ row }">{{ getMergedSchedule(row)?.teacherName || '-' }}</template></el-table-column>
      <el-table-column label="星期" width="90"><template #default="{ row }">{{ weekLabel(row.weekDay) }}</template></el-table-column>
      <el-table-column label="节次" width="120"><template #default="{ row }">{{ getSectionText(row) }}</template></el-table-column>
      <el-table-column label="教室" min-width="200"><template #default="{ row }">{{ row.classroomName || row.classroom || '待定教室' }}{{ row.buildingName ? ` · ${row.buildingName}` : '' }}</template></el-table-column>
      <el-table-column label="周次" prop="weeksText" min-width="140" />
      <el-table-column label="人数" width="90"><template #default="{ row }">{{ getStudentCount(getMergedSchedule(row)) }}</template></el-table-column>
      <el-table-column label="状态" width="80"><template #default="{ row }"><el-tag :type="row.status === '0' ? 'success' : 'danger'" size="small">{{ row.status === '0' ? '正常' : '停用' }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="150" fixed="right"><template #default="{ row }"><el-button link type="primary" icon="Edit" @click="handleUpdate(row)">修改</el-button><el-button link type="danger" icon="Delete" @click="handleDelete(row)">删除</el-button></template></el-table-column>
    </el-table>

    <!-- 周课表视图 -->
    <el-card v-else class="schedule-board-card">
      <div class="schedule-toolbar">
        <div class="schedule-weekbar">
          <el-button type="primary" @click="setCurrentWeek">本周</el-button>
          <el-button @click="shiftWeek(-1)">上一周</el-button>
          <el-button type="primary" plain @click="shiftWeek(1)">下一周</el-button>
        </div>
        <div class="schedule-toolbar__hint">{{ currentTermMeta?.label || '全部学期' }} / {{ currentClassLabel }} / 第 {{ currentWeek }} 周</div>
      </div>
      <div class="schedule-board">
        <table class="courseTable" cellspacing="0" cellpadding="0">
          <thead><tr>
            <th width="4.8%" class="courseTable__section-head">节次</th>
            <th v-for="day in weekOptions" :key="day.value" width="13.7%">{{ day.label }}</th>
          </tr></thead>
          <tbody>
            <tr v-for="row in tableRows" :key="row.key">
              <td class="dayPartUnit" :style="{ background: row.sideColor }">{{ row.label }}</td>
              <template v-for="day in weekOptions" :key="`${day.value}-${row.key}`">
                <td v-if="shouldRenderCell(row.key, day.value)" class="schedule-board__cell" :rowspan="getRowSpan(row.key, day.value)">
                  <div v-if="getSchedule(row.key, day.value)" class="schedule-card" :style="getCardStyle(getSchedule(row.key, day.value))">
                    <div class="course-name">{{ getSchedule(row.key, day.value)?.courseName || '-' }}</div>
                    <div class="course-meta-list">
                      <div class="course-meta-item course-meta-item--wide"><i class="ri-calendar-event-line"></i><span>{{ getSchedule(row.key, day.value)?.weeksText || '全周' }} · {{ getSectionText(getSchedule(row.key, day.value)) }}</span></div>
                      <div class="course-meta-item course-meta-item--wide"><i class="ri-map-pin-2-line"></i><span>{{ getScheduleRoom(getSchedule(row.key, day.value)) }}</span></div>
                      <div class="course-meta-item"><i class="ri-user-star-line"></i><span>{{ getSchedule(row.key, day.value)?.teacherName || '未配置' }}</span></div>
                    </div>
                    <div class="course-footer">
                      <div class="lesson-name">{{ getSchedule(row.key, day.value)?.className || '-' }}</div>
                      <div class="course-population"><i class="ri-group-line"></i><span>{{ getStudentCount(getSchedule(row.key, day.value)) }}</span></div>
                    </div>
                  </div>
                </td>
              </template>
            </tr>
          </tbody>
        </table>
      </div>
      <el-empty v-if="!loading && !visibleSchedules.length" description="当前条件下暂无排课数据" />
    </el-card>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/修改排课 -->
    <el-dialog v-model="open" :title="title" width="980px">
      <TeachingAiAssist module-key="courseSchedule" module-label="排课表" :form-data="form" :selected-rows="selectedRows" :available-options="{ termOptions, classCourseOptions, classroomOptions }" @apply="applyAiDraft" />
      <div class="dialog-dual-grid">
        <div class="dialog-panel">
          <div class="dialog-panel__title">排课条件</div>
          <el-form :model="form" label-width="92px">
            <el-form-item label="学期"><el-select v-model="form.termId" filterable clearable style="width:100%"><el-option v-for="item in termOptions" :key="`d-term-${item.value}`" :label="item.label" :value="item.value" /></el-select></el-form-item>
            <el-form-item label="班级课程"><el-select v-model="form.classCourseId" filterable clearable style="width:100%"><el-option v-for="item in classCourseOptions" :key="`d-cc-${item.value}`" :label="item.label" :value="item.value" /></el-select></el-form-item>
            <el-form-item label="星期"><el-select v-model="form.weekDay" style="width:100%"><el-option v-for="item in weekOptions" :key="`d-w-${item.value}`" :label="item.label" :value="item.value" /></el-select></el-form-item>
            <el-form-item label="教室"><el-select v-model="form.classroomId" filterable clearable style="width:100%" @change="handleClassroomChange"><el-option v-for="item in classroomOptions" :key="`d-cr-${item.value}`" :label="item.label" :value="item.value" /></el-select></el-form-item>
            <el-form-item label="状态"><el-select v-model="form.status" style="width:100%"><el-option label="正常" value="0" /><el-option label="停用" value="1" /></el-select></el-form-item>
            <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" rows="4" /></el-form-item>
          </el-form>
        </div>
        <div class="dialog-panel">
          <div class="dialog-panel__title">节次与周次</div>
          <el-form :model="form" label-width="92px">
            <el-form-item label="开始节次"><el-input-number v-model="form.startSection" :min="1" :max="12" style="width:100%" /></el-form-item>
            <el-form-item label="结束节次"><el-input-number v-model="form.endSection" :min="1" :max="12" style="width:100%" /></el-form-item>
            <div class="section-preview"><strong>当前节次显示</strong><span>{{ getSectionText(form) || '未选择节次' }}</span></div>
            <el-form-item label="周次">
              <div class="weeks-field">
                <div class="weeks-field__toolbar">
                  <span class="weeks-field__meta">共 {{ currentFormTotalWeeks }} 周</span>
                  <div class="weeks-field__actions"><el-button link type="primary" @click="selectAllWeeks">全选</el-button><el-button link @click="clearWeeks">清空</el-button></div>
                </div>
                <el-select v-model="form.selectedWeeks" multiple filterable collapse-tags collapse-tags-tooltip placeholder="请选择上课周次" style="width:100%"><el-option v-for="item in currentWeekOptions" :key="`d-wn-${item}`" :label="`第${item}周`" :value="item" /></el-select>
                <div class="weeks-field__preview">已生成: {{ form.weeksText || '未选择周次' }}</div>
              </div>
            </el-form-item>
          </el-form>
        </div>
      </div>
      <el-alert v-if="conflictState.hasConflict" class="mt12" type="warning" :closable="false" show-icon :title="buildConflictTitle()" :description="buildConflictDescription()" />
      <template #footer><el-button @click="open=false">取消</el-button><el-button type="primary" @click="submitForm">确定</el-button></template>
    </el-dialog>

    <!-- 自动排课 -->
    <el-dialog v-model="autoArrangeOpen" title="自动排课配置" width="1060px">
      <el-form :model="autoArrangeForm" label-width="110px">
        <el-row :gutter="16">
          <el-col :span="8"><el-form-item label="学期"><el-select v-model="autoArrangeForm.termId" style="width:100%"><el-option v-for="item in termOptions" :key="`a-term-${item.value}`" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
          <el-col :span="8">
            <el-form-item label="排课范围">
              <el-select v-model="autoArrangeForm.scopeType" style="width:100%" @change="handleAutoArrangeScopeChange">
                <el-option label="全部班级课程" value="all" />
                <el-option label="按部门" value="dept" />
                <el-option label="按班级" value="class" />
                <el-option label="按课程" value="course" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item v-if="autoArrangeForm.scopeType === 'dept'" label="选择部门">
              <el-tree-select v-model="autoArrangeForm.deptId" :data="deptOptions" :props="{ value: 'id', label: 'label', children: 'children' }" value-key="id" check-strictly clearable filterable style="width:100%" />
            </el-form-item>
            <el-form-item v-else-if="autoArrangeForm.scopeType === 'class'" label="选择班级">
              <el-select v-model="autoArrangeForm.classId" filterable clearable style="width:100%"><el-option v-for="item in classOptions" :key="`a-class-${item.value}`" :label="item.label" :value="item.value" /></el-select>
            </el-form-item>
            <el-form-item v-else-if="autoArrangeForm.scopeType === 'course'" label="选择课程">
              <el-select v-model="autoArrangeForm.courseId" filterable clearable style="width:100%"><el-option v-for="item in distinctCourseOptions" :key="`a-course-${item.value}`" :label="item.label" :value="item.value" /></el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8"><el-form-item label="保留已有排课"><el-switch v-model="autoArrangeForm.keepExisting" inline-prompt active-text="是" inactive-text="否" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="课块优先长度"><el-select v-model="autoArrangeForm.preferredSessionDurations" multiple collapse-tags collapse-tags-tooltip style="width:100%"><el-option label="4节连排" :value="4" /><el-option label="3节连排" :value="3" /><el-option label="2节连排" :value="2" /><el-option label="1节拆分" :value="1" /></el-select></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="禁排时段"><el-select v-model="autoArrangeForm.excludedDayParts" multiple collapse-tags collapse-tags-tooltip style="width:100%"><el-option label="中午" value="NOON" /><el-option label="晚上" value="EVENING" /><el-option label="下午" value="AFTERNOON" /><el-option label="上午" value="MORNING" /></el-select></el-form-item></el-col>
        </el-row>
      </el-form>

      <div class="auto-arrange-summary">
        共 <strong>{{ autoArrangeItems.filter((r:any) => r.selected).length }}</strong> 门课程参与排课
        <el-button link type="primary" @click="toggleAllAutoArrangeItems(true)" style="margin-left:12px">全选</el-button>
        <el-button link @click="toggleAllAutoArrangeItems(false)">全不选</el-button>
      </div>

      <el-table :data="autoArrangeItems" max-height="420">
        <el-table-column label="参与" width="70"><template #default="{ row }"><el-switch v-model="row.selected" /></template></el-table-column>
        <el-table-column label="班级" min-width="120"><template #default="{ row }">{{ row.className || '-' }}</template></el-table-column>
        <el-table-column label="课程" min-width="140"><template #default="{ row }">{{ row.courseName || '-' }}</template></el-table-column>
        <el-table-column label="教师" min-width="100"><template #default="{ row }">{{ row.teacherName || '-' }}</template></el-table-column>
        <el-table-column label="指定教室" min-width="200">
          <template #default="{ row }">
            <el-select v-model="row.assignedClassroomId" filterable clearable placeholder="自动分配" style="width:100%"><el-option v-for="item in classroomOptions" :key="`a-cr-${row.id}-${item.value}`" :label="item.label" :value="item.value" /></el-select>
          </template>
        </el-table-column>
        <el-table-column label="排课周次" min-width="200">
          <template #default="{ row }">
            <el-select v-model="row.selectedWeeks" multiple collapse-tags collapse-tags-tooltip style="width:100%"><el-option v-for="item in autoArrangeWeekOptions" :key="`a-wk-${row.id}-${item}`" :label="`第${item}周`" :value="item" /></el-select>
            <div class="auto-arrange-week-preview">{{ row.weeksText || '未选择周次' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="最多节/周" width="120"><template #default="{ row }"><el-input-number v-model="row.maxWeeklySections" :min="1" :max="12" style="width:100%" /></template></el-table-column>
      </el-table>
      <template #footer><el-button @click="autoArrangeOpen=false">取消</el-button><el-button type="primary" :loading="autoArrangeLoading" @click="handleAutoArrange">开始自动排课</el-button></template>
    </el-dialog>

    <el-dialog v-model="autoArrangeResultOpen" title="自动排课结果" width="980px">
      <div class="auto-arrange-result-head">
        <div class="auto-arrange-result-head__summary">
          已排 <strong>{{ autoArrangeResult.arrangedLessonTasks || 0 }}</strong> / {{ autoArrangeResult.totalLessonTasks || 0 }} 个课块
        </div>
        <div class="auto-arrange-result-head__meta">
          <span>适应度：{{ autoArrangeResult.bestFitnessScore ?? '-' }}</span>
          <span v-if="autoArrangeResult.termName">学期：{{ autoArrangeResult.termName }}</span>
        </div>
      </div>
      <el-alert
        v-for="(warning, index) in autoArrangeResult.warnings || []"
        :key="`warning-${index}`"
        class="mb12"
        type="warning"
        :title="warning"
        :closable="false"
        show-icon
      />
      <el-table :data="autoArrangeResult.arrangedLessons || []" max-height="360" class="mb12">
        <el-table-column label="班级" min-width="120" prop="className" />
        <el-table-column label="课程" min-width="140" prop="courseName" />
        <el-table-column label="教师" min-width="100" prop="teacherName" />
        <el-table-column label="时间" min-width="180">
          <template #default="{ row }">{{ weekLabel(row.weekDay) }} {{ getSectionText(row) }} / {{ row.weeksText || '-' }}</template>
        </el-table-column>
        <el-table-column label="教室" min-width="180">
          <template #default="{ row }">{{ getAutoArrangeResultRoom(row) }}</template>
        </el-table-column>
        <el-table-column label="选择依据" min-width="260">
          <template #default="{ row }">
            <div class="reason-tag-list">
              <el-tag v-for="reason in formatAutoArrangeReasons(row)" :key="reason" size="small" effect="plain">{{ reason }}</el-tag>
              <span v-if="!formatAutoArrangeReasons(row).length" class="reason-tag-list__empty">已找到可行教室</span>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <el-collapse v-if="(autoArrangeResult.unscheduledLessons || []).length">
        <el-collapse-item :title="`未排课块（${autoArrangeResult.unscheduledLessons.length}）`" name="unscheduled">
          <el-table :data="autoArrangeResult.unscheduledLessons" max-height="240">
            <el-table-column label="班级" min-width="120" prop="className" />
            <el-table-column label="课程" min-width="140" prop="courseName" />
            <el-table-column label="教师" min-width="100" prop="teacherName" />
            <el-table-column label="周次" min-width="120" prop="weeksText" />
            <el-table-column label="原因" min-width="260" prop="reason" show-overflow-tooltip />
          </el-table>
        </el-collapse-item>
      </el-collapse>
      <template #footer><el-button @click="autoArrangeResultOpen=false">关闭</el-button></template>
    </el-dialog>

    <!-- Excel导入 -->
    <el-dialog v-model="importOpen" title="Excel导入排课数据" width="680px">
      <el-form :model="importForm" label-width="100px">
        <el-form-item label="目标学期"><el-select v-model="importForm.termId" filterable style="width:100%"><el-option v-for="item in termOptions" :key="`i-term-${item.value}`" :label="item.label" :value="item.value" /></el-select></el-form-item>
        <el-form-item label="导入范围">
          <el-select v-model="importForm.scopeType" style="width:100%"><el-option label="按部门导入" value="dept" /><el-option label="按班级导入" value="class" /></el-select>
        </el-form-item>
        <el-form-item v-if="importForm.scopeType === 'dept'" label="选择部门">
          <el-tree-select v-model="importForm.deptId" :data="deptOptions" :props="{ value: 'id', label: 'label', children: 'children' }" value-key="id" check-strictly clearable filterable style="width:100%" />
        </el-form-item>
        <el-form-item v-else label="选择班级"><el-select v-model="importForm.classId" filterable clearable style="width:100%"><el-option v-for="item in classOptions" :key="`i-class-${item.value}`" :label="item.label" :value="item.value" /></el-select></el-form-item>
        <el-form-item label="是否覆盖"><el-switch v-model="importForm.overwrite" inline-prompt active-text="覆盖已有" inactive-text="仅新增" /></el-form-item>
        <el-form-item label="上传文件">
          <el-upload ref="importUploadRef" :auto-upload="false" :limit="1" accept=".xls,.xlsx" :on-change="handleImportFileChange" :file-list="importFileList" drag style="width:100%">
            <el-icon style="font-size:36px;color:#8c939d;margin-bottom:8px"><upload-filled /></el-icon>
            <div>将文件拖到此处，或<em>点击上传</em></div>
            <template #tip><div class="el-upload__tip">仅支持 .xls / .xlsx 文件，<el-button link type="primary" @click.stop="downloadImportTemplate">下载模板</el-button></div></template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer><el-button @click="importOpen=false">取消</el-button><el-button type="primary" :loading="importLoading" :disabled="!importFileList.length" @click="handleImport">开始导入</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import { addCourseSchedule, autoArrangeCourseSchedule, checkCourseScheduleConflict, delCourseSchedule, getTimeTableLayout, listClassCourse, listCourseSchedule, updateCourseSchedule } from '@/api/campus/teaching'
import { fetchClassOptions, fetchClassroomOptions, fetchTermOptions } from '@/api/campus/options'
import { deptTreeSelect } from '@/api/system/user'
import { getToken } from '@/utils/auth'
import TeachingAiAssist from '@/components/Teaching/TeachingAiAssist.vue'
import { resolveCurrentWeek } from '@/utils/termWeek'

const loading = ref(false), showSearch = ref(true), total = ref(0), open = ref(false), title = ref('')
const ids = ref<any[]>([]), single = ref(true), multiple = ref(true), dataList = ref<any[]>([]), selectedRows = ref<any[]>([])
const autoArrangeLoading = ref(false), autoArrangeOpen = ref(false), autoArrangeResultOpen = ref(false)
const importOpen = ref(false), importLoading = ref(false), importUploadRef = ref<any>(null), importFileList = ref<any[]>([])
const termOptions = ref<any[]>([]), deptOptions = ref<any[]>([]), classOptions = ref<any[]>([])
const classCourseOptions = ref<any[]>([]), classroomOptions = ref<any[]>([])
const classCourseMap = ref<Map<number, any>>(new Map())
const autoArrangeItems = ref<any[]>([])
const autoArrangeResult = ref<any>({ arrangedLessons: [], unscheduledLessons: [], warnings: [] })
const viewMode = ref('table'), currentWeek = ref(1)
const queryParams = reactive<any>({ pageNum: 1, pageSize: 10, termId: undefined, deptId: undefined, classId: undefined, courseId: undefined, classroomId: undefined, weekDay: undefined, weekNo: undefined })
const form = reactive<any>({})
const autoArrangeForm = reactive<any>({ termId: undefined, keepExisting: true, scopeType: 'all', deptId: undefined, classId: undefined, courseId: undefined, preferredSessionDurations: [4, 2, 1], excludedDayParts: ['NOON', 'EVENING'] })
const importForm = reactive<any>({ termId: undefined, scopeType: 'dept', deptId: undefined, classId: undefined, overwrite: false })
const conflictState = reactive<any>({ hasConflict: false })
const weekOptions = [{ label: '星期一', value: 1 }, { label: '星期二', value: 2 }, { label: '星期三', value: 3 }, { label: '星期四', value: 4 }, { label: '星期五', value: 5 }, { label: '星期六', value: 6 }, { label: '星期日', value: 7 }]
const defaultTableRows = [
  { key: '1', unit: 1, label: '1', sideColor: '#dcecff' }, { key: '2', unit: 2, label: '2', sideColor: '#dcecff' },
  { key: '3', unit: 3, label: '3', sideColor: '#dcecff' }, { key: '4', unit: 4, label: '4', sideColor: '#dcecff' },
  { key: 'noon-1', unit: null, label: '中午1', sideColor: '#ffe7a3' }, { key: 'noon-2', unit: null, label: '中午', sideColor: '#ffe7a3' },
  { key: '5', unit: 5, label: '5', sideColor: '#c9f0ea' }, { key: '6', unit: 6, label: '6', sideColor: '#c9f0ea' },
  { key: '7', unit: 7, label: '7', sideColor: '#c9f0ea' }, { key: '8', unit: 8, label: '8', sideColor: '#c9f0ea' },
  { key: '9', unit: 9, label: '9', sideColor: '#d7eef8' }, { key: '10', unit: 10, label: '10', sideColor: '#d7eef8' },
]
const tableRows = ref<any[]>([...defaultTableRows])

function getDefaultTermId() { return termOptions.value.find((t: any) => t.isCurrent === '1')?.value ?? termOptions.value[0]?.value }
const currentFormTerm = computed(() => termOptions.value.find((t: any) => t.value === form.termId))
const currentTermMeta = computed(() => termOptions.value.find((t: any) => t.value === queryParams.termId) || {} as any)
const currentFormTotalWeeks = computed(() => Math.max(Number(currentFormTerm.value?.totalWeeks || 20), 1))
const currentTermTotalWeeks = computed(() => Math.max(Number(currentTermMeta.value?.totalWeeks || 20), 1))
const currentWeekOptions = computed(() => Array.from({ length: currentFormTotalWeeks.value }, (_, i) => i + 1))
const weekNumberOptions = computed(() => Array.from({ length: currentTermTotalWeeks.value }, (_, i) => i + 1))
const autoArrangeWeekOptions = computed(() => {
  const term = termOptions.value.find((t: any) => t.value === autoArrangeForm.termId) || currentTermMeta.value || {}
  return Array.from({ length: Math.max(Number(term?.totalWeeks || 20), 1) }, (_, i) => i + 1)
})

const courseFilterOptions = computed(() => {
  let items = Array.from(classCourseMap.value.values())
  if (queryParams.classId) items = items.filter((cc: any) => cc.classId === queryParams.classId)
  const seen = new Map<number, string>()
  items.forEach((cc: any) => { if (cc.courseId && !seen.has(cc.courseId)) seen.set(cc.courseId, cc.courseName || `课程${cc.courseId}`) })
  return Array.from(seen.entries()).map(([id, name]) => ({ label: name, value: id }))
})

const distinctCourseOptions = computed(() => {
  const seen = new Map<number, string>()
  Array.from(classCourseMap.value.values()).forEach((cc: any) => {
    if (cc.courseId && !seen.has(cc.courseId)) seen.set(cc.courseId, cc.courseName || `课程${cc.courseId}`)
  })
  return Array.from(seen.entries()).map(([id, name]) => ({ label: name, value: id }))
})

const currentClassLabel = computed(() => classOptions.value.find((c: any) => c.value === queryParams.classId)?.label || '全部班级')
const scheduleFilterTags = computed(() => {
  const tags: Array<{ label: string }> = []
  const termLabel = termOptions.value.find((item: any) => item.value === queryParams.termId)?.label
  const deptLabel = findDeptLabel(deptOptions.value, queryParams.deptId)
  const classLabel = classOptions.value.find((item: any) => item.value === queryParams.classId)?.label
  const courseLabel = courseFilterOptions.value.find((item: any) => item.value === queryParams.courseId)?.label
  if (termLabel) tags.push({ label: `学期：${termLabel}` })
  if (deptLabel) tags.push({ label: `部门：${deptLabel}` })
  if (classLabel) tags.push({ label: `班级：${classLabel}` })
  if (courseLabel) tags.push({ label: `课程：${courseLabel}` })
  return tags
})

const needsClientFilter = computed(() => !!(queryParams.deptId || queryParams.classId || queryParams.courseId || queryParams.weekNo != null))

const allowedCCIds = computed(() => {
  let items = Array.from(classCourseMap.value.values())
  if (queryParams.classId) items = items.filter((cc: any) => cc.classId === queryParams.classId)
  if (queryParams.courseId) items = items.filter((cc: any) => cc.courseId === queryParams.courseId)
  return new Set(items.map((cc: any) => cc.id))
})

const visibleSchedules = computed(() => dataList.value.filter((item: any) => matchesWeek(item.weeksText, currentWeek.value)))
const activityMap = computed(() => {
  const map = new Map<string, any>()
  visibleSchedules.value.forEach((item: any) => {
    const s = Number(item.startSection || 0), d = Number(item.weekDay || 0)
    if (s && d) map.set(`${s}-${d}`, item)
  })
  return map
})
const occupiedMap = computed(() => {
  const map = new Map<string, { startKey: string; span: number }>()
  visibleSchedules.value.forEach((item: any) => {
    const start = Number(item.startSection || 0), end = Number(item.endSection || start), day = Number(item.weekDay || 0)
    if (!start || !day) return
    const span = Math.max(1, end - start + 1)
    for (let u = start; u <= end; u++) map.set(`${u}-${day}`, { startKey: String(start), span })
  })
  return map
})

function weekLabel(v: any) { return weekOptions.find(o => o.value === Number(v))?.label || `星期${v || '-'}` }
function findDeptLabel(options: any[], value: any): string {
  if (value == null) return ''
  for (const item of options || []) {
    if (item?.id === value) return item.label || ''
    const childLabel = findDeptLabel(item?.children || [], value)
    if (childLabel) return childLabel
  }
  return ''
}
function dayPartColor(p?: string) { return p === 'NOON' ? '#ffe7a3' : p === 'AFTERNOON' ? '#c9f0ea' : p === 'EVENING' ? '#d7eef8' : '#dcecff' }
function buildTableRowsFromLayout(layout: any) {
  const units = Array.isArray(layout?.courseUnitList) ? layout.courseUnitList : []
  if (!units.length) { tableRows.value = [...defaultTableRows]; return }
  tableRows.value = units.slice().sort((a: any, b: any) => (a.indexNo || 0) - (b.indexNo || 0)).map((item: any, i: number) => ({
    key: String(item.indexNo || i + 1), unit: Number(item.indexNo || i + 1), label: item.nameZh || String(item.indexNo || i + 1), sideColor: dayPartColor(item.dayPart),
  }))
}
function parseWeeksText(text?: string) {
  if (!text) return []
  const values = new Set<number>()
  const norm = String(text).replace(/第|周次|周/g, '').replace(/[，、；;]/g, ',').replace(/[～~至到]/g, '-').replace(/[（]/g, '(').replace(/[）]/g, ')').replace(/\s+/g, '')
  norm.split(',').forEach(seg => {
    const p = seg.trim(); if (!p) return
    const odd = p.includes('单'), even = p.includes('双')
    const c = p.replace(/\(单\)|\(双\)|单周|双周|单|双|\(|\)/g, ''); if (!c) return
    if (c.includes('-')) {
      let [s, e] = c.split('-').map(Number)
      if (Number.isFinite(s) && Number.isFinite(e)) {
        if (s > e) [s, e] = [e, s]; let cur = s; const step = odd || even ? 2 : 1
        if (odd && cur % 2 === 0) cur++; if (even && cur % 2 !== 0) cur++
        for (let w = cur; w <= e; w += step) values.add(w)
      }; return
    }
    const n = Number(c); if (!Number.isFinite(n)) return
    if ((!odd && !even) || (odd && n % 2 !== 0) || (even && n % 2 === 0)) values.add(n)
  })
  return Array.from(values).sort((a, b) => a - b)
}
function buildWeeksText(weeks: number[]) {
  const sorted = Array.from(new Set((weeks || []).map(Number).filter(n => Number.isFinite(n) && n > 0))).sort((a, b) => a - b)
  if (!sorted.length) return ''
  const segs: string[] = []; let start = sorted[0], prev = sorted[0]
  for (let i = 1; i <= sorted.length; i++) {
    const cur = sorted[i]
    if (cur === prev + 1) { prev = cur; continue }
    segs.push(start === prev ? `${start}` : `${start}-${prev}`); start = cur; prev = cur
  }
  return `${segs.join(',')}周`
}
function matchesWeek(text: string | undefined, week: number) { const w = parseWeeksText(text); return w.length ? w.includes(week) : true }
function getMergedSchedule(item: any) { return { ...classCourseMap.value.get(Number(item.classCourseId)), ...item } }
function shouldRenderCell(rowKey: string, day: number) {
  const row = tableRows.value.find((r: any) => r.key === rowKey); if (!row?.unit) return true
  const occ = occupiedMap.value.get(`${row.unit}-${day}`); return !occ || occ.startKey === String(row.unit)
}
function getRowSpan(rowKey: string, day: number) {
  const row = tableRows.value.find((r: any) => r.key === rowKey); if (!row?.unit) return 1
  return occupiedMap.value.get(`${row.unit}-${day}`)?.span || 1
}
function getSchedule(rowKey: string, day: number) {
  const row = tableRows.value.find((r: any) => r.key === rowKey); if (!row?.unit) return null
  const item = activityMap.value.get(`${row.unit}-${day}`); return item ? getMergedSchedule(item) : null
}
function getSectionText(item: any) {
  const s = Number(item?.startSection || 0), e = Number(item?.endSection || s); if (!s) return '-'
  const labels: string[] = []; for (let u = s; u <= e; u++) { const r = tableRows.value.find((row: any) => Number(row.unit) === u); labels.push(String(r?.label || u)) }
  return labels.length ? `${labels[0]}-${labels[labels.length - 1]}节` : '-'
}
function getScheduleRoom(item: any) {
  const parts = [item?.campusName, item?.buildingName, item?.classroomName || item?.classroom || '待定教室']
  const res: string[] = []; parts.forEach(p => { const t = String(p || '').trim(); if (t && !res.some(x => x === t || x.includes(t) || t.includes(x))) res.push(t) })
  return res.join(' ')
}
function getAutoArrangeResultRoom(item: any) {
  return getScheduleRoom(item)
}
function formatAutoArrangeReasons(item: any) {
  const reasons = Array.isArray(item?.reasons) ? item.reasons : []
  return reasons.filter((reason: any) => String(reason || '').trim())
}
function getStudentCount(item: any) {
  const a = item?.actualStudentCount, l = item?.studentLimit
  if (a == null && l == null) return '0'; if (!l) return `${a ?? 0}`; return `${a ?? 0}/${l}`
}
function colorFromKey(key: string | number) {
  const palette = ['#d2a1f2', '#38c8b4', '#f49060', '#7996ca', '#a9ce95', '#6fb0f3', '#dac4a5', '#7fc5a6']
  let h = 0; const s = String(key || ''); for (let i = 0; i < s.length; i++) { h = ((h << 5) - h) + s.charCodeAt(i); h |= 0 }
  return palette[Math.abs(h) % palette.length]
}
function getCardStyle(item: any) {
  const c = colorFromKey(item?.courseCode || item?.classCourseId || item?.scheduleId), n = c.replace('#', ''), b = parseInt(n, 16)
  return { background: `rgba(${(b >> 16) & 255},${(b >> 8) & 255},${b & 255},0.18)`, borderLeft: `2px solid ${c}` }
}
function buildConflictTitle() {
  if (conflictState.conflictType === 'teacher') return `教师时间冲突：${conflictState.teacherName || '教师'} 在 ${weekLabel(conflictState.weekDay)} ${conflictState.startSection}-${conflictState.endSection}节已有安排`
  return `教室时间冲突：${conflictState.classroomName || '教室'} 在 ${weekLabel(conflictState.weekDay)} ${conflictState.startSection}-${conflictState.endSection}节已被占用`
}
function buildConflictDescription() {
  const loc = `${conflictState.buildingName ? '，楼栋：' + conflictState.buildingName : ''}${conflictState.campusName ? '，校区：' + conflictState.campusName : ''}`
  if (conflictState.conflictType === 'teacher') return `冲突周次：${conflictState.weeksText || '-'}${conflictState.className ? '，班级：' + conflictState.className : ''}${loc}`
  return `冲突周次：${conflictState.weeksText || '-'}${loc}`
}
function shiftWeek(offset: number) {
  const opts = weekNumberOptions.value, idx = opts.indexOf(currentWeek.value)
  currentWeek.value = opts[Math.min(Math.max(idx + offset, 0), opts.length - 1)] || currentWeek.value
}
function setCurrentWeek() {
  const cur = resolveCurrentWeek(currentTermMeta.value || {}); currentWeek.value = weekNumberOptions.value.includes(cur) ? cur : (weekNumberOptions.value[0] || 1)
}

function resetForm() {
  const tid = queryParams.termId ?? getDefaultTermId(), tw = Math.max(Number(termOptions.value.find((t: any) => t.value === tid)?.totalWeeks || 20), 1)
  Object.assign(form, { scheduleId: undefined, termId: tid, classCourseId: undefined, classroomId: undefined, classroom: '', weekDay: 1, startSection: 1, endSection: 2, selectedWeeks: Array.from({ length: tw }, (_, i) => i + 1), weeksText: '', status: '0', remark: '' })
  form.weeksText = buildWeeksText(form.selectedWeeks); Object.assign(conflictState, { hasConflict: false })
}
function applyAiDraft(draft: Record<string, any>) {
  Object.assign(form, draft)
  form.selectedWeeks = Array.isArray(draft.selectedWeeks) && draft.selectedWeeks.length ? draft.selectedWeeks.map(Number).filter(Number.isFinite) : parseWeeksText(draft.weeksText)
  form.weeksText = buildWeeksText(form.selectedWeeks)
}

async function reloadClassCourses() {
  const res = await listClassCourse({ pageNum: 1, pageSize: 500, status: '0', termId: queryParams.termId, openDeptId: queryParams.deptId })
  const rows = res.rows || []
  classCourseMap.value = new Map(rows.map((r: any) => [Number(r.id), r]))
  classCourseOptions.value = rows.map((r: any) => ({ label: `${r.className || '-'} - ${r.courseName || '-'}（${r.id}）`, value: r.id }))
  if (!classCourseOptions.value.some((item: any) => item.value === form.classCourseId)) form.classCourseId = undefined
}

function syncDependentFilters() {
  if (!classOptions.value.some((c: any) => c.value === queryParams.classId)) queryParams.classId = undefined
  if (!courseFilterOptions.value.some((c: any) => c.value === queryParams.courseId)) queryParams.courseId = undefined
}

async function getList() {
  loading.value = true
  try {
    const useClientPaging = needsClientFilter.value || viewMode.value === 'board'
    const apiParams = useClientPaging ? { termId: queryParams.termId, classroomId: queryParams.classroomId, weekDay: queryParams.weekDay, classCourseId: undefined, status: '0', pageNum: 1, pageSize: 500 } : { ...queryParams, courseId: undefined }
    const res = await listCourseSchedule(apiParams)
    let rows = res.rows || []

    if (queryParams.deptId || queryParams.classId || queryParams.courseId) {
      rows = rows.filter((item: any) => allowedCCIds.value.has(item.classCourseId))
    }
    if (queryParams.weekNo != null) {
      rows = rows.filter((item: any) => matchesWeek(item.weeksText, Number(queryParams.weekNo)))
    }

    if (useClientPaging && viewMode.value === 'table') {
      total.value = rows.length
      const start = (Number(queryParams.pageNum || 1) - 1) * Number(queryParams.pageSize || 10)
      dataList.value = rows.slice(start, start + Number(queryParams.pageSize || 10))
    } else {
      dataList.value = rows
      total.value = useClientPaging ? rows.length : (res.total || 0)
    }
    if (viewMode.value === 'board') {
      currentWeek.value = Number(queryParams.weekNo || 0) || currentWeek.value
      if (!queryParams.weekNo) setCurrentWeek()
    }
  } finally { loading.value = false }
}

async function handleSearch() { queryParams.pageNum = 1; await reloadClassCourses(); getList() }
function resetQuery() {
  queryParams.pageNum = 1; queryParams.termId = getDefaultTermId(); queryParams.deptId = undefined; queryParams.classId = undefined
  queryParams.courseId = undefined; queryParams.classroomId = undefined; queryParams.weekDay = undefined; queryParams.weekNo = undefined; getList()
}
function handleSelectionChange(sel: any[]) { selectedRows.value = sel; ids.value = sel.map((i: any) => i.scheduleId); single.value = sel.length !== 1; multiple.value = !sel.length }
function handleAdd() { resetForm(); title.value = '新增排课'; open.value = true }
function handleUpdate(row?: any) {
  const item = row || dataList.value.find((i: any) => i.scheduleId === ids.value[0]); if (!item) return
  resetForm(); Object.assign(form, item); form.weekDay = Number(form.weekDay || 1)
  form.selectedWeeks = parseWeeksText(form.weeksText); form.weeksText = buildWeeksText(form.selectedWeeks)
  title.value = '修改排课'; open.value = true
}
function handleClassroomChange(v: any) { form.classroom = classroomOptions.value.find((o: any) => o.value === v)?.classroomName || '' }
function selectAllWeeks() { form.selectedWeeks = [...currentWeekOptions.value] }
function clearWeeks() { form.selectedWeeks = [] }
async function checkConflict() {
  if (!open.value || !form.classroomId || !form.termId || !form.weekDay || !form.startSection || !form.endSection) { Object.assign(conflictState, { hasConflict: false }); return }
  const res = await checkCourseScheduleConflict(form); Object.assign(conflictState, { hasConflict: false }, res.data || {})
}
async function submitForm() {
  form.weeksText = buildWeeksText(form.selectedWeeks || [])
  form.weeksJson = JSON.stringify((form.selectedWeeks || []).map(Number).filter(Number.isFinite))
  if (!form.weeksText) { ElMessage.warning('请选择至少一个上课周次'); return }
  await checkConflict()
  if (conflictState.hasConflict) { ElMessage.warning(conflictState.conflictType === 'teacher' ? '教师时间冲突，请调整' : '教室时间冲突，请调整'); return }
  if (form.scheduleId) { await updateCourseSchedule(form); ElMessage.success('修改成功') } else { await addCourseSchedule(form); ElMessage.success('新增成功') }
  open.value = false; getList()
}
async function handleDelete(row?: any) {
  const target = row?.scheduleId || ids.value; if (!target || (Array.isArray(target) && !target.length)) return
  await ElMessageBox.confirm('确认删除所选排课吗？', '提示', { type: 'warning' }); await delCourseSchedule(target); ElMessage.success('删除成功'); getList()
}

function handleAutoArrangeScopeChange() { autoArrangeForm.deptId = undefined; autoArrangeForm.classId = undefined; autoArrangeForm.courseId = undefined; refreshAutoArrangeItems() }
function toggleAllAutoArrangeItems(selected: boolean) { autoArrangeItems.value.forEach((i: any) => { i.selected = selected }) }
function refreshAutoArrangeItems() {
  let items = Array.from(classCourseMap.value.values())
  if (autoArrangeForm.scopeType === 'dept' && autoArrangeForm.deptId) items = items.filter((c: any) => c.openDeptId === autoArrangeForm.deptId || c.deptId === autoArrangeForm.deptId)
  else if (autoArrangeForm.scopeType === 'class' && autoArrangeForm.classId) items = items.filter((c: any) => c.classId === autoArrangeForm.classId)
  else if (autoArrangeForm.scopeType === 'course' && autoArrangeForm.courseId) items = items.filter((c: any) => c.courseId === autoArrangeForm.courseId)
  autoArrangeItems.value = items.map((c: any) => ({
    ...c, weeksText: c.requiredWeeks ? `1-${c.requiredWeeks}周` : '',
    selectedWeeks: c.requiredWeeks ? Array.from({ length: c.requiredWeeks }, (_, i) => i + 1) : [...autoArrangeWeekOptions.value],
    maxWeeklySections: c.weeklyHours || 2, selected: true, assignedClassroomId: undefined,
  }))
}
async function openAutoArrangeDialog() {
  autoArrangeForm.termId = queryParams.termId ?? getDefaultTermId()
  autoArrangeForm.scopeType = queryParams.classId ? 'class' : (queryParams.deptId ? 'dept' : 'all')
  autoArrangeForm.deptId = queryParams.deptId; autoArrangeForm.classId = queryParams.classId; autoArrangeForm.courseId = undefined
  refreshAutoArrangeItems()
  if (!autoArrangeItems.value.length) { ElMessage.warning('当前条件下没有可配置的班级课程'); return }
  autoArrangeOpen.value = true
}
async function handleAutoArrange() {
  const tid = autoArrangeForm.termId ?? getDefaultTermId(); if (!tid) { ElMessage.warning('请先选择学期'); return }
  const selected = autoArrangeItems.value.filter((i: any) => i.selected); if (!selected.length) { ElMessage.warning('请至少保留一个课程'); return }
  autoArrangeLoading.value = true
  try {
    const res = await autoArrangeCourseSchedule({
      termId: tid, classCourseIds: selected.map((i: any) => i.id),
      items: selected.map((i: any) => ({ classCourseId: i.id, weeksText: i.weeksText, weeksJson: JSON.stringify(i.selectedWeeks || []), maxWeeklySections: i.maxWeeklySections, classroomId: i.assignedClassroomId || undefined })),
      clearExistingSchedules: !autoArrangeForm.keepExisting, populationSize: 60, generationCount: 120, mutationRate: 0.12,
      preferredSessionDurations: autoArrangeForm.preferredSessionDurations, excludedDayParts: autoArrangeForm.excludedDayParts,
    })
    const d = res.data || {}, arr = Number(d.arrangedLessonTasks || 0), tot = Number(d.totalLessonTasks || 0), un = Array.isArray(d.unscheduledLessons) ? d.unscheduledLessons.length : 0
    autoArrangeResult.value = d
    autoArrangeResultOpen.value = true
    ElMessage.success(`自动排课完成，已排 ${arr}/${tot} 个课块${un ? `，未排 ${un} 个` : ''}`)
    autoArrangeOpen.value = false
    getList()
  } finally { autoArrangeLoading.value = false }
}

function openImportDialog() {
  importForm.termId = queryParams.termId ?? getDefaultTermId()
  importForm.scopeType = queryParams.deptId ? 'dept' : 'dept'; importForm.deptId = queryParams.deptId; importForm.classId = queryParams.classId
  importForm.overwrite = false; importFileList.value = []; importOpen.value = true
}
function handleImportFileChange(file: any) { importFileList.value = [file] }
async function downloadImportTemplate() {
  try {
    const resp = await fetch(`${import.meta.env.VITE_APP_BASE_API || ''}/campus/courseSchedule/import-template`, { headers: { 'Authorization': `Bearer ${getToken()}` } })
    if (!resp.ok) { ElMessage.error('下载模板失败'); return }
    const blob = await resp.blob()
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a'); a.href = url; a.download = 'schedule_import_template.xlsx'; document.body.appendChild(a); a.click(); document.body.removeChild(a); URL.revokeObjectURL(url)
  } catch { ElMessage.error('下载模板失败') }
}
async function handleImport() {
  if (!importForm.termId) { ElMessage.warning('请选择目标学期'); return }
  if (!importFileList.value.length) { ElMessage.warning('请选择文件'); return }
  importLoading.value = true
  try {
    const fd = new FormData(); fd.append('file', importFileList.value[0].raw); fd.append('termId', String(importForm.termId)); fd.append('overwrite', String(importForm.overwrite))
    if (importForm.scopeType === 'dept' && importForm.deptId) fd.append('deptId', String(importForm.deptId))
    if (importForm.scopeType === 'class' && importForm.classId) fd.append('classId', String(importForm.classId))
    const resp = await fetch(`${import.meta.env.VITE_APP_BASE_API || ''}/campus/courseSchedule/import`, { method: 'POST', headers: { 'Authorization': `Bearer ${getToken()}` }, body: fd })
    const result = await resp.json()
    if (result.code === 200) { ElMessage.success(result.msg || '导入成功'); importOpen.value = false; getList() } else { ElMessage.error(result.msg || '导入失败') }
  } catch (e: any) { ElMessage.error(e?.message || '导入失败') } finally { importLoading.value = false }
}

async function loadOptions() {
  termOptions.value = await fetchTermOptions()
  if (!queryParams.termId) queryParams.termId = getDefaultTermId()
  const deptRes = await deptTreeSelect(); deptOptions.value = deptRes.data || []
  classOptions.value = await fetchClassOptions(undefined, queryParams.deptId)
  classroomOptions.value = await fetchClassroomOptions()
  try { buildTableRowsFromLayout((await getTimeTableLayout()).data) } catch { tableRows.value = [...defaultTableRows] }
  await reloadClassCourses()
}

watch(() => form.selectedWeeks, (v: any) => { form.weeksText = buildWeeksText(v || []) }, { deep: true })
watch(() => autoArrangeItems.value.map((i: any) => i.selectedWeeks), (v: any) => { autoArrangeItems.value.forEach((i: any, idx: number) => { i.weeksText = buildWeeksText(v[idx] || []) }) }, { deep: true })
watch(() => form.termId, () => {
  const lim = currentFormTotalWeeks.value
  const cur = Array.isArray(form.selectedWeeks) ? form.selectedWeeks.map(Number).filter((n: number) => Number.isFinite(n) && n >= 1 && n <= lim) : []
  form.selectedWeeks = cur.length ? cur : [...currentWeekOptions.value]
})
watch(() => autoArrangeForm.termId, () => {
  const lim = autoArrangeWeekOptions.value
  autoArrangeItems.value.forEach((i: any) => { const c = Array.isArray(i.selectedWeeks) ? i.selectedWeeks.map(Number).filter((n: number) => lim.includes(n)) : []; i.selectedWeeks = c.length ? c : [...lim]; i.weeksText = buildWeeksText(i.selectedWeeks) })
})
watch(() => queryParams.deptId, async () => {
  classOptions.value = await fetchClassOptions(undefined, queryParams.deptId)
  await reloadClassCourses()
  syncDependentFilters()
  queryParams.pageNum = 1
  getList()
})
watch(() => queryParams.classId, () => {
  if (!courseFilterOptions.value.some((c: any) => c.value === queryParams.courseId)) queryParams.courseId = undefined
  queryParams.pageNum = 1
  getList()
})
watch(() => queryParams.termId, async (value) => {
  if (!value) queryParams.termId = getDefaultTermId()
  classOptions.value = await fetchClassOptions(undefined, queryParams.deptId)
  await reloadClassCourses()
  syncDependentFilters()
  queryParams.pageNum = 1
  getList()
})
watch(() => [autoArrangeForm.scopeType, autoArrangeForm.deptId, autoArrangeForm.classId, autoArrangeForm.courseId], () => { if (autoArrangeOpen.value) refreshAutoArrangeItems() })
watch(viewMode, () => { getList() })
watch(() => queryParams.weekNo, (v: any) => { queryParams.pageNum = 1; if (viewMode.value === 'board' && v != null) currentWeek.value = Number(v) })
watch(() => [open.value, form.classroomId, form.termId, form.weekDay, form.startSection, form.endSection, form.weeksText, form.status], () => { checkConflict() })
onMounted(async () => { await loadOptions(); resetForm(); getList() })
</script>

<style scoped>
.mb16{margin-bottom:16px}
.mb12{margin-bottom:12px}
.filter-tag-bar{display:flex;align-items:center;gap:8px;flex-wrap:wrap;margin:-2px 0 16px;padding:10px 12px;border-radius:14px;background:linear-gradient(180deg,#f8fbff 0%,#f3f8ff 100%);border:1px solid #dbe6f5}
.filter-tag-bar__label{color:#526076;font-size:12px;font-weight:600}
.dialog-dual-grid{display:grid;grid-template-columns:repeat(2,minmax(0,1fr));gap:16px}
.dialog-panel{padding:14px 14px 6px;border:1px solid #e4ebf4;border-radius:16px;background:linear-gradient(180deg,#fff 0%,#fafcff 100%)}
.dialog-panel__title{margin-bottom:14px;color:#172033;font-size:14px;font-weight:700}
.section-preview{margin:-4px 0 10px;padding:10px 12px;border-radius:12px;background:#f6f9fc;color:#526076;font-size:12px;line-height:1.7}
.section-preview strong{display:block;color:#172033;font-size:12px;margin-bottom:4px}
.mt12{margin-top:12px}
.weeks-field{width:100%}
.weeks-field__toolbar{display:flex;align-items:center;justify-content:space-between;gap:12px;margin-bottom:8px}
.weeks-field__meta{color:#667085;font-size:12px}
.weeks-field__actions{display:flex;align-items:center;gap:8px}
.weeks-field__preview{margin-top:8px;color:#526076;font-size:12px;line-height:1.5}
.schedule-board-card{margin-bottom:16px;border:none;border-radius:22px;background:linear-gradient(180deg,#f8fbff 0%,#eef7f2 100%);box-shadow:0 18px 42px rgba(15, 23, 42, 0.08)}
.schedule-toolbar{display:flex;align-items:center;justify-content:space-between;gap:12px;margin-bottom:18px;flex-wrap:wrap}
.schedule-toolbar__hint{color:#526076;font-size:13px;padding:9px 14px;border-radius:999px;background:rgba(255,255,255,0.92);border:1px solid rgba(210,223,238,0.95)}
.schedule-weekbar{display:flex;align-items:center;gap:8px;flex-wrap:wrap}
.schedule-board{overflow:auto;background:transparent}
.courseTable{width:100%;font-size:12px;word-break:break-word;white-space:normal;border:1px solid #c8d8eb;border-collapse:collapse;table-layout:fixed}
.courseTable th{border:1px solid #d4e0ef;padding:0 8px;text-align:center;vertical-align:middle;height:40px;color:#172033;background:linear-gradient(180deg,#f6faff 0%,#edf4fb 100%);font-size:12px;font-weight:700}
.courseTable__section-head{text-align:center !important}
.courseTable td{padding:0;border:1px solid #d4e0ef;vertical-align:top;background:rgba(255,255,255,0.82)}
.dayPartUnit{width:4.8%;text-align:center !important;vertical-align:middle !important;color:#172033;font-size:12px;font-weight:700;box-shadow:inset 0 0 0 1px rgba(255,255,255,0.28)}
.schedule-board__cell{position:relative;height:52px;background:linear-gradient(180deg,rgba(255,255,255,0.88) 0%,rgba(247,250,253,0.96) 100%)}
.schedule-card{display:flex;flex-direction:column;gap:4px;padding:5px 8px 6px;min-height:46px;height:calc(100% - 4px);box-sizing:border-box;margin:2px;box-shadow:0 6px 16px rgba(15,23,42,0.05)}
.course-name{color:#122033;font-size:15px;font-weight:800;line-height:1.18;margin-bottom:3px;text-decoration:underline;text-decoration-thickness:1px;text-underline-offset:2px;text-decoration-color:rgba(18,32,51,0.24)}
.course-meta-list{display:flex;flex-direction:column;gap:2px;min-width:0}
.course-meta-item{display:inline-flex;align-items:center;gap:4px;min-width:0;color:#405068;font-size:12px;line-height:1.3}
.course-meta-item i,.course-population i{flex:0 0 auto;font-size:13px;color:#245a97}
.course-meta-item span,.lesson-name,.course-population span{min-width:0;overflow:hidden;text-overflow:ellipsis;white-space:nowrap}
.course-meta-item--wide{color:#33455d}
.course-footer{display:flex;align-items:center;justify-content:space-between;gap:8px;margin-top:auto}
.lesson-name{color:#1d4f91;font-weight:600;flex:1;font-size:12px;line-height:1.25}
.course-population{display:inline-flex;align-items:center;gap:3px;flex:0 0 auto;padding:1px 6px;border-radius:999px;background:rgba(36,90,151,0.08);color:#23476d;font-size:12px;font-weight:600}
.el-dialog :deep(.el-dialog__body){padding-top:12px}
.el-dialog :deep(.el-dialog__footer){padding-top:8px}
.auto-arrange-summary{margin-bottom:12px;padding:10px 14px;border-radius:12px;background:#f0f5ff;color:#2d4a7a;font-size:13px;display:flex;align-items:center;gap:4px}
.auto-arrange-summary strong{color:#1d3557;font-size:16px}
.auto-arrange-week-preview{margin-top:6px;color:#667085;font-size:12px;line-height:1.5}
.auto-arrange-result-head{display:flex;align-items:flex-start;justify-content:space-between;gap:16px;margin-bottom:12px;padding:12px 14px;border-radius:14px;background:linear-gradient(180deg,#f6fbff 0%,#eef5ff 100%);border:1px solid #dbe6f5}
.auto-arrange-result-head__summary{color:#172033;font-size:14px}
.auto-arrange-result-head__summary strong{font-size:22px;color:#1d4f91}
.auto-arrange-result-head__meta{display:flex;align-items:center;gap:12px;flex-wrap:wrap;color:#526076;font-size:12px}
.reason-tag-list{display:flex;align-items:center;gap:6px;flex-wrap:wrap}
.reason-tag-list__empty{color:#8a94a6;font-size:12px}
@media (max-width:900px){.dialog-dual-grid{grid-template-columns:1fr}}
@media (max-width:900px){.auto-arrange-result-head{flex-direction:column}}
:deep(.el-input-number) .el-input__wrapper{border-radius:4px}
:deep(.el-input-number__decrease),:deep(.el-input-number__increase){border-radius:0}
:deep(.el-input-number__decrease){border-left:1px solid var(--el-border-color);border-radius:4px 0 0 4px}
:deep(.el-input-number__increase){border-right:1px solid var(--el-border-color);border-radius:0 4px 4px 0}
</style>
