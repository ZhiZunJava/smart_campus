<template>
  <div class="portal-page portal-grid portal-grid-2">
    <el-card class="portal-card">
      <template #header><span>可参加考试</span></template>
      <el-table v-loading="loadingPaper" :data="papers" border>
        <el-table-column prop="paperId" label="试卷ID" width="90" />
        <el-table-column prop="paperName" label="试卷名称" min-width="180" />
        <el-table-column prop="paperType" label="类型" width="100" />
        <el-table-column prop="durationMinutes" label="时长" width="90" />
        <el-table-column label="操作" width="120"><template #default="scope"><el-button link type="primary" @click="beginExam(scope.row)">开始考试</el-button></template></el-table-column>
      </el-table>
      <div class="mt16 section-title">我的考试记录</div>
      <el-table v-loading="loadingRecord" :data="records" border>
        <el-table-column prop="recordId" label="记录ID" width="100" />
        <el-table-column prop="paperId" label="试卷ID" width="90" />
        <el-table-column prop="score" label="得分" width="100" />
        <el-table-column prop="correctRate" label="正确率" width="100" />
        <el-table-column label="状态" width="120"><template #default="scope"><el-tag :type="scope.row.examStatus === 'SUBMITTED' ? 'success' : 'warning'">{{ scope.row.examStatus }}</el-tag></template></el-table-column>
        <el-table-column label="操作" width="100"><template #default="scope"><el-button link type="primary" @click="viewAnswer(scope.row)">答案</el-button></template></el-table-column>
      </el-table>
    </el-card>
    <el-card class="portal-card">
      <template #header><span>作答详情 / 错题本</span></template>
      <el-table :data="answers" border>
        <el-table-column prop="questionId" label="题目ID" width="90" />
        <el-table-column prop="userAnswer" label="用户答案" min-width="220" show-overflow-tooltip />
        <el-table-column label="是否正确" width="100"><template #default="scope"><el-tag :type="scope.row.isCorrect === '1' ? 'success' : 'danger'">{{ scope.row.isCorrect === '1' ? '正确' : '错误' }}</el-tag></template></el-table-column>
        <el-table-column prop="score" label="得分" width="100" />
      </el-table>
      <div class="mt16 section-title">我的错题本</div>
      <el-table :data="wrongs" border>
        <el-table-column prop="questionId" label="题目ID" width="90" />
        <el-table-column prop="courseId" label="课程ID" width="90" />
        <el-table-column prop="wrongCount" label="错误次数" width="100" />
        <el-table-column prop="lastWrongTime" label="最后做错时间" min-width="180" />
      </el-table>
      <el-empty v-if="answers.length === 0 && wrongs.length === 0" class="portal-empty" description="请选择考试记录或查看错题本" />
    </el-card>

    <el-dialog v-model="examOpen" :title="currentPaper.paperName || '在线考试'" width="920px">
      <div v-if="currentPaper.questions?.length">
        <div v-for="item in currentPaper.questions" :key="item.questionId" class="question-item">
          <div class="question-title">{{ item.sortNo }}. {{ item.question?.stem }}</div>
          <el-radio-group v-if="item.question?.questionType === 'single'" v-model="answerMap[item.questionId]">
            <el-radio v-for="opt in item.question?.options || []" :key="opt.optionId" :label="opt.optionKey">{{ opt.optionKey }}. {{ opt.optionContent }}</el-radio>
          </el-radio-group>
          <el-checkbox-group v-else-if="item.question?.questionType === 'multiple'" v-model="multiAnswerMap[item.questionId]">
            <el-checkbox v-for="opt in item.question?.options || []" :key="opt.optionId" :label="opt.optionKey">{{ opt.optionKey }}. {{ opt.optionContent }}</el-checkbox>
          </el-checkbox-group>
          <el-input v-else v-model="answerMap[item.questionId]" type="textarea" rows="3" placeholder="请输入答案" />
        </div>
      </div>
      <template #footer><el-button @click="examOpen=false">取消</el-button><el-button type="primary" @click="submitCurrentExam">提交考试</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getPaperDetail, listExamAnswer, listExamPaper, listExamRecord, listWrongBook, startExam, submitExam } from '@/api/portal'
import usePortalUserStore from '@/store/user'
const userStore = usePortalUserStore()
const loadingPaper=ref(false), loadingRecord=ref(false), examOpen=ref(false)
const papers=ref<any[]>([]), records=ref<any[]>([]), answers=ref<any[]>([]), wrongs=ref<any[]>([]), currentPaper=ref<any>({}), currentRecordId=ref<number | undefined>()
const answerMap = ref<Record<string, any>>({})
const multiAnswerMap = ref<Record<string, string[]>>({})
async function loadPapers(){ loadingPaper.value=true; const res=await listExamPaper({ pageNum:1, pageSize:20, status:'0' }); papers.value=res.rows||[]; loadingPaper.value=false }
async function loadRecords(){ const userId = userStore.user?.userId; if(!userId) return; loadingRecord.value=true; const res=await listExamRecord({ pageNum:1,pageSize:20,userId }); records.value=res.rows||[]; const wrongRes = await listWrongBook({ pageNum:1, pageSize:50, userId }); wrongs.value = wrongRes.rows || []; loadingRecord.value=false }
async function beginExam(row:any){ const userId = userStore.user?.userId; if(!userId) return; const startRes=await startExam({ paperId: row.paperId, userId }); currentRecordId.value = startRes.data?.recordId; const res=await getPaperDetail(row.paperId); currentPaper.value=res.data||{}; answerMap.value={}; multiAnswerMap.value={}; examOpen.value=true }
async function submitCurrentExam(){ if(!currentRecordId.value) return; const answersPayload = (currentPaper.value.questions || []).map((item:any)=>({ questionId:item.questionId, userAnswer:item.question?.questionType === 'multiple' ? (multiAnswerMap.value[item.questionId] || []).join(',') : (answerMap.value[item.questionId] || '') })); await submitExam({ recordId: currentRecordId.value, answers: answersPayload }); ElMessage.success('考试提交成功'); examOpen.value=false; loadRecords() }
async function viewAnswer(row:any){ const res=await listExamAnswer({ pageNum:1,pageSize:100, recordId: row.recordId }); answers.value=res.rows||[] }
loadPapers(); loadRecords()
</script>
<style scoped>
.mt16{margin-top:16px}.section-title{font-weight:700;margin-bottom:12px}.question-item{padding:14px 0;border-bottom:1px solid #eef2f6}.question-title{font-weight:700;margin-bottom:10px}
</style>
