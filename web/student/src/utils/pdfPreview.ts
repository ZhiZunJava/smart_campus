import { getDocument, GlobalWorkerOptions } from 'pdfjs-dist'
import type { PDFDocumentProxy } from 'pdfjs-dist/types/src/display/api'
import pdfWorker from 'pdfjs-dist/build/pdf.worker.mjs?url'

const MAX_PDF_CACHE_SIZE = 6
const pdfDocumentCache = new Map<string, PDFDocumentProxy>()
const pdfDocumentTaskCache = new Map<string, Promise<PDFDocumentProxy>>()

if (!GlobalWorkerOptions.workerSrc) {
  GlobalWorkerOptions.workerSrc = pdfWorker
}

function touchPdfDocumentCache(src: string, doc: PDFDocumentProxy) {
  if (pdfDocumentCache.has(src)) {
    pdfDocumentCache.delete(src)
  }

  pdfDocumentCache.set(src, doc)

  if (pdfDocumentCache.size <= MAX_PDF_CACHE_SIZE) return

  const oldestKey = pdfDocumentCache.keys().next().value as string | undefined
  if (!oldestKey || oldestKey === src) return

  pdfDocumentCache.delete(oldestKey)
}

export function getCachedPdfDocument(src?: string) {
  const key = String(src || '').trim()
  if (!key) return null

  const cached = pdfDocumentCache.get(key) || null
  if (cached) {
    touchPdfDocumentCache(key, cached)
  }

  return cached
}

export async function getOrLoadPdfDocument(src?: string) {
  const key = String(src || '').trim()
  if (!key) {
    throw new Error('当前没有可预览的 PDF 地址')
  }

  const cached = getCachedPdfDocument(key)
  if (cached) return cached

  const loadingTask = pdfDocumentTaskCache.get(key)
  if (loadingTask) return loadingTask

  const task = getDocument(key).promise
    .then((doc) => {
      pdfDocumentTaskCache.delete(key)
      touchPdfDocumentCache(key, doc)
      return doc
    })
    .catch((error) => {
      pdfDocumentTaskCache.delete(key)
      throw error
    })

  pdfDocumentTaskCache.set(key, task)
  return task
}

export function warmPdfDocument(src?: string) {
  const key = String(src || '').trim()
  if (!key || pdfDocumentCache.has(key) || pdfDocumentTaskCache.has(key)) return

  void getOrLoadPdfDocument(key).catch(() => {})
}
