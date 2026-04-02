export const SCORE_ANALYSIS_QA_PRESET_KEY = 'portal-score-ai-preset'

export type ScoreAnalysisQaPreset = {
  courseId?: number
  prompt?: string
  contextLabel?: string
  contextPrompt?: string
}

export function saveScoreAnalysisQaPreset(preset: ScoreAnalysisQaPreset) {
  sessionStorage.setItem(SCORE_ANALYSIS_QA_PRESET_KEY, JSON.stringify(preset || {}))
}

export function consumeScoreAnalysisQaPreset() {
  const raw = sessionStorage.getItem(SCORE_ANALYSIS_QA_PRESET_KEY)
  sessionStorage.removeItem(SCORE_ANALYSIS_QA_PRESET_KEY)
  if (!raw) {
    return null
  }
  try {
    return JSON.parse(raw) as ScoreAnalysisQaPreset
  } catch {
    return null
  }
}
