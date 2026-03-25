export function isExternalUrl(url?: string) {
  return /^(https?:|mailto:|tel:|data:|blob:)/.test(String(url || ''))
}

export function resolveResourceUrl(url?: string) {
  const value = String(url || '').trim()
  if (!value) return ''
  if (isExternalUrl(value)) return value
  const base = String(import.meta.env.VITE_APP_BASE_API || '').replace(/\/$/, '')
  const path = value.startsWith('/') ? value : `/${value}`
  return `${base}${path}`
}

export function isPdfFile(url?: string) {
  return /\.pdf($|\?)/i.test(String(url || ''))
}

export function isOfficeFile(url?: string) {
  return /\.(ppt|pptx|doc|docx)($|\?)/i.test(String(url || ''))
}

export function isVideoFile(url?: string) {
  return /\.(mp4|webm|ogg|m3u8)($|\?)/i.test(String(url || ''))
}

export function buildOfficePreviewUrl(url?: string) {
  const resolved = resolveResourceUrl(url)
  if (!resolved) return ''
  return `https://view.officeapps.live.com/op/embed.aspx?src=${encodeURIComponent(resolved)}`
}
