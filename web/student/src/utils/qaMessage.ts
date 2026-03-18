export function parseQaAttachments(attachmentJson?: string) {
  if (!attachmentJson) return []
  try {
    const parsed = JSON.parse(attachmentJson)
    return Array.isArray(parsed) ? parsed : []
  } catch {
    return []
  }
}

export function parseAssistantReferenceSource(referenceSource?: string) {
  if (!referenceSource) {
    return { modelName: '', reasoningContent: '', attachments: [] as any[] }
  }
  try {
    const parsed = JSON.parse(referenceSource)
    return {
      modelName: parsed?.modelName || '',
      reasoningContent: parsed?.reasoningContent || '',
      attachments: parsed?.attachments || [],
    }
  } catch {
    return {
      modelName: referenceSource.replace('AI模型：', ''),
      reasoningContent: '',
      attachments: [],
    }
  }
}

export function normalizeQaErrorMessage(message: string) {
  if (message.includes('image_url') || message.includes('视觉模型')) {
    return '当前所选 DeepSeek 聊天模型不支持图片理解，请切换到支持图片的视觉模型后再上传。'
  }
  return message
}
