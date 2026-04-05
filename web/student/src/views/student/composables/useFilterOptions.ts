import { ref, isRef } from 'vue'
import type { Ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getPortalCourseOfferingFilterOptions } from '@/api/portal'

export type OptionItem = { label: string; value: any }

export type FilterOptionKey =
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

export type QueryFieldType = 'input' | 'select' | 'range'

export type QueryField = {
  key: string
  label: string
  type: QueryFieldType
  placeholder: string
  clearable?: boolean
  filterable?: boolean
  optionKey?: FilterOptionKey
}

export function useFilterOptions() {
  const filterOptionsLoading = ref(false)

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

  const fieldOptionsMap: Record<string, Ref<OptionItem[]> | OptionItem[]> = {
    teacherOptions,
    openDeptOptions,
    campusOptions,
    courseCategoryOptions,
    businessTypeOptions,
    majorOptions,
    assessmentTypeOptions,
    teachingLanguageOptions,
    requiredFlagOptions,
  }

  function getFieldOptions(field: QueryField, termOptions?: Ref<any[]>): OptionItem[] {
    if (!field.optionKey) return []
    if (field.optionKey === 'termOptions' && termOptions) return termOptions.value
    const src = fieldOptionsMap[field.optionKey]
    if (!src) return []
    return isRef(src) ? src.value : src
  }

  function getOptionLabel(options: OptionItem[], value: any) {
    return options.find(item => String(item.value) === String(value))?.label || String(value)
  }

  async function loadFilterOptions(termId: any) {
    filterOptionsLoading.value = true
    try {
      const res = await getPortalCourseOfferingFilterOptions({ termId })
      const data = res.data || {}
      teacherOptions.value = data.teacherOptions || []
      openDeptOptions.value = data.openDeptOptions || []
      campusOptions.value = data.campusOptions || []
      courseCategoryOptions.value = data.courseCategoryOptions || []
      businessTypeOptions.value = data.businessTypeOptions || []
      majorOptions.value = data.majorOptions || []
      assessmentTypeOptions.value = data.assessmentTypeOptions || []
      teachingLanguageOptions.value = data.teachingLanguageOptions || []
    } catch {
      ElMessage.warning('加载筛选选项失败')
    } finally {
      filterOptionsLoading.value = false
    }
  }

  return {
    filterOptionsLoading,
    teacherOptions,
    openDeptOptions,
    campusOptions,
    courseCategoryOptions,
    businessTypeOptions,
    majorOptions,
    assessmentTypeOptions,
    teachingLanguageOptions,
    requiredFlagOptions,
    getFieldOptions,
    getOptionLabel,
    loadFilterOptions,
  }
}
