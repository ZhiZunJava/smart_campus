<template>
  <div class="app-container">
    <section class="teaching-page-shell">
      <div class="teaching-page-shell__copy">
        <div class="teaching-page-shell__eyebrow">教学基础</div>
        <h2>课表节次布局</h2>
        <p>统一维护课表节次、时间与时段分组，学生端、教师端和打印布局都会读取这里。</p>
      </div>
      <div class="teaching-page-shell__stats">
        <div v-for="item in headerStats" :key="item.label" class="teaching-page-shell__stat">
          <span>{{ item.label }}</span>
          <strong>{{ item.value }}</strong>
          <small>{{ item.tip }}</small>
        </div>
      </div>
    </section>

    <el-card class="portal-card">
      <div class="toolbar-row">
        <el-input v-model="form.nameZh" placeholder="布局名称" style="width:260px" />
        <el-button type="primary" icon="Plus" @click="addUnit">新增节次</el-button>
        <el-button icon="RefreshLeft" @click="resetDefault">恢复默认布局</el-button>
        <el-button type="success" icon="Check" @click="submitForm">保存布局</el-button>
      </div>

      <div class="layout-grid">
        <el-card class="preview-card" shadow="never">
          <template #header><div class="preview-card__title">打印分组预览</div></template>
          <div class="preview-groups">
            <div v-for="group in printGroups" :key="group.nameEn" class="preview-group">
              <strong>{{ group.nameZh }}</strong>
              <span>{{ group.unitGroup.map((item:any)=>item.join('-')).join(' / ') }}</span>
            </div>
          </div>
        </el-card>

        <el-card class="preview-card" shadow="never">
          <template #header><div class="preview-card__title">布局提示</div></template>
          <div class="preview-notes">
            <span>节次将影响学生端、教师端、家长端课表和打印分组。</span>
            <span>同一时段会自动按每 2 节一组生成打印分组，无需单独维护。</span>
            <span>建议先调整时间，再保存后到课表页核对显示。</span>
          </div>
        </el-card>
      </div>

      <el-table :data="form.courseUnitList" row-key="indexNo" class="unit-table">
        <el-table-column label="显示名称" min-width="120">
          <template #default="{ row }"><el-input v-model="row.nameZh" /></template>
        </el-table-column>
        <el-table-column label="节次编号" width="120">
          <template #default="{ row }"><el-input-number v-model="row.indexNo" :min="1" :max="30" style="width:100%" /></template>
        </el-table-column>
        <el-table-column label="开始时间" width="140">
          <template #default="{ row }"><el-time-picker v-model="row.startText" format="HH:mm" value-format="HH:mm" style="width:100%" /></template>
        </el-table-column>
        <el-table-column label="结束时间" width="140">
          <template #default="{ row }"><el-time-picker v-model="row.endText" format="HH:mm" value-format="HH:mm" style="width:100%" /></template>
        </el-table-column>
        <el-table-column label="时段" width="160">
          <template #default="{ row }"><el-select v-model="row.dayPart" style="width:100%"><el-option label="上午" value="MORNING" /><el-option label="中午" value="NOON" /><el-option label="下午" value="AFTERNOON" /><el-option label="晚上" value="EVENING" /></el-select></template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ $index }"><el-button link type="danger" icon="Delete" @click="removeUnit($index)">删除</el-button></template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { getTimeTableLayout, updateTimeTableLayout } from '@/api/campus/teaching'

const defaultLayout = {
  id:1,
  nameZh:'默认课表布局',
  nameEn:'Default time table layout',
  courseUnitList:[
    { nameZh:'1', indexNo:1, startTime:900, endTime:940, dayPart:'MORNING' },
    { nameZh:'2', indexNo:2, startTime:950, endTime:1030, dayPart:'MORNING' },
    { nameZh:'3', indexNo:3, startTime:1040, endTime:1120, dayPart:'MORNING' },
    { nameZh:'4', indexNo:4, startTime:1130, endTime:1210, dayPart:'MORNING' },
    { nameZh:'中午', indexNo:5, startTime:1215, endTime:1240, dayPart:'NOON' },
    { nameZh:'中午', indexNo:6, startTime:1245, endTime:1315, dayPart:'NOON' },
    { nameZh:'5', indexNo:7, startTime:1320, endTime:1400, dayPart:'AFTERNOON' },
    { nameZh:'6', indexNo:8, startTime:1410, endTime:1450, dayPart:'AFTERNOON' },
    { nameZh:'7', indexNo:9, startTime:1500, endTime:1540, dayPart:'AFTERNOON' },
    { nameZh:'8', indexNo:10, startTime:1550, endTime:1630, dayPart:'AFTERNOON' },
    { nameZh:'9', indexNo:11, startTime:1830, endTime:1910, dayPart:'EVENING' },
    { nameZh:'10', indexNo:12, startTime:1920, endTime:2000, dayPart:'EVENING' },
  ],
}

const form = reactive<any>({ id:1, nameZh:'默认课表布局', nameEn:'Default time table layout', courseUnitList:[] })
const headerStats = computed(() => [
  { label: '节次数', value: form.courseUnitList.length, tip: '前台课表按这里渲染' },
  { label: '时段数', value: new Set(form.courseUnitList.map((item:any)=>item.dayPart).filter(Boolean)).size, tip: '自动生成打印分组' },
  { label: '最早开始', value: form.courseUnitList[0]?.startText || '-', tip: '用于课表头部和时间推导' },
])
const printGroups = computed(() => {
  const grouped = new Map<string, number[]>()
  form.courseUnitList.forEach((item:any) => {
    const dayPart = item.dayPart || 'MORNING'
    if(!grouped.has(dayPart)) grouped.set(dayPart, [])
    grouped.get(dayPart)?.push(Number(item.indexNo))
  })
  return Array.from(grouped.entries()).map(([dayPart, values]) => ({
    nameEn: dayPart,
    nameZh: dayPart==='NOON' ? '中午' : dayPart==='AFTERNOON' ? '下午' : dayPart==='EVENING' ? '晚上' : '上午',
    unitGroup: values.sort((a,b)=>a-b).reduce((acc:number[][], value:number) => {
      if(!acc.length || acc[acc.length-1].length>=2) acc.push([])
      acc[acc.length-1].push(value)
      return acc
    }, []),
  }))
})

function toText(value:number){ const hours = String(Math.floor(value / 100)).padStart(2,'0'); const minutes = String(value % 100).padStart(2,'0'); return `${hours}:${minutes}` }
function toNumber(value:string){ const [hours, minutes] = String(value || '00:00').split(':').map((item)=>Number(item)); return (hours || 0) * 100 + (minutes || 0) }
function normalizeUnits(){ form.courseUnitList.sort((a:any,b:any)=>Number(a.indexNo)-Number(b.indexNo)); form.courseUnitList.forEach((item:any,index:number)=>{ if(!item.nameZh) item.nameZh=String(index+1) }) }
function addUnit(){ form.courseUnitList.push({ nameZh:String(form.courseUnitList.length+1), indexNo:form.courseUnitList.length+1, startText:'08:00', endText:'08:40', dayPart:'MORNING' }) }
function removeUnit(index:number){ form.courseUnitList.splice(index,1) }
function applyLayout(data:any){
  form.id = data.id || 1
  form.nameZh = data.nameZh || '默认课表布局'
  form.nameEn = data.nameEn || 'Default time table layout'
  form.courseUnitList = (data.courseUnitList || []).map((item:any)=>({
    ...item,
    startText: toText(Number(item.startTime || 0)),
    endText: toText(Number(item.endTime || 0)),
  }))
  normalizeUnits()
}
function resetDefault(){ applyLayout(defaultLayout); ElMessage.success('已恢复默认布局，记得保存后生效') }

async function loadData(){
  const res = await getTimeTableLayout()
  applyLayout(res.data || defaultLayout)
}

async function submitForm(){
  normalizeUnits()
  const payload = {
    id: form.id,
    nameZh: form.nameZh,
    nameEn: form.nameEn,
    courseUnitList: form.courseUnitList.map((item:any)=>({
      nameZh: item.nameZh,
      indexNo: Number(item.indexNo),
      startTime: toNumber(item.startText),
      endTime: toNumber(item.endText),
      dayPart: item.dayPart,
    })),
  }
  await updateTimeTableLayout(payload)
  ElMessage.success('保存成功')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.teaching-page-shell{display:grid;grid-template-columns:minmax(0,1.15fr) minmax(320px,.85fr);gap:16px;margin-bottom:16px;padding:18px;border:1px solid #dbe3f0;border-radius:18px;background:linear-gradient(180deg,#fff 0%,#f7fbff 100%)}
.teaching-page-shell__eyebrow{color:#607188;font-size:12px;font-weight:700;letter-spacing:.08em}
.teaching-page-shell__copy h2{margin:8px 0 10px;color:#172033;font-size:24px;line-height:1.2}
.teaching-page-shell__copy p{margin:0;color:#526076;font-size:13px;line-height:1.8}
.teaching-page-shell__stats{display:grid;grid-template-columns:repeat(3,minmax(0,1fr));gap:10px}
.teaching-page-shell__stat{display:flex;flex-direction:column;gap:6px;padding:14px;border-radius:16px;background:#fff;border:1px solid rgba(219,227,240,.92)}
.teaching-page-shell__stat span,.teaching-page-shell__stat small{color:#667085;font-size:12px}
.teaching-page-shell__stat strong{color:#172033;font-size:22px;line-height:1.1}
.toolbar-row{display:flex;align-items:center;gap:12px;flex-wrap:wrap;margin-bottom:16px}
.layout-grid{display:grid;grid-template-columns:1.2fr .8fr;gap:16px;margin-bottom:16px}
.preview-card{border-radius:18px;border:1px solid #dbe5f0;background:linear-gradient(180deg,#f9fbff 0%,#ffffff 100%)}
.preview-card__title{font-size:14px;font-weight:700;color:#172033}
.preview-groups,.preview-notes{display:flex;flex-direction:column;gap:12px}
.preview-group{display:flex;justify-content:space-between;gap:12px;padding:12px 14px;border-radius:14px;background:#f3f7fd;color:#526076}
.preview-group strong{color:#172033}
.preview-notes span{padding:12px 14px;border-radius:14px;background:#f7fafc;color:#526076;line-height:1.7}
.unit-table :deep(.el-input-number){width:100%}
@media (max-width: 1100px){.teaching-page-shell{grid-template-columns:1fr}.teaching-page-shell__stats{grid-template-columns:1fr}}
@media (max-width: 960px){.layout-grid{grid-template-columns:1fr}}
</style>
