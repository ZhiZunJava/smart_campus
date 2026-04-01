import { computed, ref, unref, watch } from 'vue'
import type { Ref } from 'vue'
import { getPortalAffairCategoryDetail, listPortalTermOptions } from '@/api/portal'
import { resolveAffairPresentation } from '@/utils/affairPresentation'

type MaybeRef<T> = T | Ref<T>

/* ---- 模块级学期选项缓存（所有页面共享，只请求一次） ---- */
const _termOptions = ref<Array<{ value: any; label: string }>>([])
let _termLoading: Promise<void> | null = null

function ensureTermOptions() {
  if (_termOptions.value.length || _termLoading) return _termLoading || Promise.resolve()
  _termLoading = listPortalTermOptions()
    .then((res: any) => { _termOptions.value = res.data || res || [] })
    .catch(() => { /* 静默失败 */ })
    .finally(() => { _termLoading = null })
  return _termLoading
}

/** 将 termId 转为中文学期名称，找不到时降级显示"学期 <id>" */
export function resolveTermLabel(termId: any): string {
  if (termId === undefined || termId === null || termId === '') return '未绑定学期'
  const match = _termOptions.value.find((item) => String(item.value) === String(termId))
  return match?.label || `学期 ${termId}`
}

export function useStudentAffairDetail(categoryCode: MaybeRef<string>) {
  const loading = ref(false)
  const detail = ref<any>(null)

  const presentation = computed(() => resolveAffairPresentation(unref(categoryCode)))
  const templates = computed(() => detail.value?.templates || [])
  const requests = computed(() => (detail.value?.myRequests || []).map((item: any) => ({
    ...item,
    parsedFormData: parseJsonObject(item.formDataJson),
  })))
  const stats = computed(() => detail.value?.stats || {})
  const specializedData = computed(() => detail.value?.specializedData || {})
  const advisorInfo = computed(() => detail.value?.advisorInfo || null)
  const studentStatus = computed(() => detail.value?.studentStatus || null)
  const guideSteps = computed(() => presentation.value?.guideSteps || [])

  async function loadDetail() {
    const currentCode = unref(categoryCode)
    if (!currentCode) {
      detail.value = null
      return
    }
    loading.value = true
    try {
      // 并行加载：事务详情 + 学期选项（学期缓存只请求一次）
      const [res] = await Promise.all([
        getPortalAffairCategoryDetail('student', currentCode),
        ensureTermOptions(),
      ])
      detail.value = res.data || res
    } finally {
      loading.value = false
    }
  }

  function findTemplate(templateCode: string) {
    return templates.value.find((item: any) => item.templateCode === templateCode)
  }

  watch(() => unref(categoryCode), () => {
    loadDetail()
  }, { immediate: true })

  return {
    loading,
    detail,
    presentation,
    templates,
    requests,
    stats,
    specializedData,
    advisorInfo,
    studentStatus,
    guideSteps,
    findTemplate,
    loadDetail,
  }
}

export function parseJsonObject(value?: string) {
  try {
    return JSON.parse(value || '{}')
  } catch {
    return {}
  }
}

export function statusLabel(status?: string) {
  if (status === 'APPROVED') return '已通过'
  if (status === 'REJECTED') return '已驳回'
  if (status === 'CANCELLED') return '已撤回'
  return '待审核'
}

export function statusType(status?: string) {
  if (status === 'APPROVED') return 'success'
  if (status === 'REJECTED') return 'danger'
  if (status === 'CANCELLED') return 'info'
  return 'warning'
}

export function formatDateRange(start?: string, end?: string) {
  if (start && end) return `${start} 至 ${end}`
  return start || end || '-'
}

export function pickLatestRecord<T extends Record<string, any>>(records: T[], ...timeKeys: string[]) {
  const keys = timeKeys.length ? timeKeys : ['submittedTime', 'update_time', 'updateTime', 'create_time', 'createTime']
  return [...records].sort((a, b) => {
    const bTime = keys.map((key) => String(b?.[key] || '')).find(Boolean) || ''
    const aTime = keys.map((key) => String(a?.[key] || '')).find(Boolean) || ''
    return bTime.localeCompare(aTime)
  })[0]
}

export function buildApplyPayload(action: any) {
  // AffairWorkbench reads presetData.formData & presetData.title
  return {
    template: action.template,
    presetData: action.presetData
      ? { formData: action.presetData, title: action.title || '' }
      : null,
  }
}

export function resolveOptionLabel(options: Array<{ label: string; value: string | number }>, value: unknown, fallback = '-') {
  const match = options.find((item) => String(item.value) === String(value))
  return match?.label || (value === undefined || value === null || value === '' ? fallback : String(value))
}

/** 判断申报时段是否在开放期内 */
export function isWindowOpen(rules: any) {
  if (!rules?.openWindowEnabled) return true
  const now = Date.now()
  const start = rules.openStartTime ? new Date(rules.openStartTime).getTime() : 0
  const end = rules.openEndTime ? new Date(rules.openEndTime).getTime() : Number.MAX_SAFE_INTEGER
  return now >= start && now <= end
}

/** 从模板列表构建申报时段卡片数据（仅包含启用了窗口期的模板） */
export function buildWindowCards(templates: any[]) {
  return templates
    .filter((item: any) => {
      const rules = parseJsonObject(item.businessRulesJson)
      return !!rules.openWindowEnabled
    })
    .map((item: any) => {
      const rules = parseJsonObject(item.businessRulesJson)
      return {
        templateId: item.templateId,
        title: item.businessName || item.templateName,
        termLabel: resolveTermLabel(rules.termId),
        windowLabel: `${rules.openStartTime || '未设置'} ~ ${rules.openEndTime || '未设置'}`,
        notice: rules.notice || item.remark || '',
        open: isWindowOpen(rules),
        template: item,
      }
    })
}
