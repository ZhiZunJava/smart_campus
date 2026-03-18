import MarkdownIt from 'markdown-it'
import remend from 'remend'
import DOMPurify from 'dompurify'
import hljs from 'highlight.js'

const md = new MarkdownIt({
  breaks: true,
  linkify: true,
  html: true,
  highlight(code, lang) {
    try {
      if (lang && hljs.getLanguage(lang)) {
        return `<pre class="hljs"><code>${hljs.highlight(code, { language: lang }).value}</code></pre>`
      }
      return `<pre class="hljs"><code>${hljs.highlightAuto(code).value}</code></pre>`
    } catch {
      return `<pre class="hljs"><code>${md.utils.escapeHtml(code)}</code></pre>`
    }
  },
})

function normalizeInlineLists(content: string) {
  return content
    .replace(/(^|\n)(\d+[\.\)\uFF09])(?=\S)/g, '$1$2 ')
    .replace(/([：:])\s*(\d+[\.\)\uFF09])(?=\S)/g, '$1\n$2 ')
    .replace(/([：:])\s*(\d+[\.\)\uFF09])\s*/g, '$1\n$2 ')
    .replace(/([^\n])(\d+[\.\)\uFF09])(?=[\u4e00-\u9fa5A-Za-z])/g, '$1\n$2 ')
    .replace(/([^\n])\s+(\d+[\.\)\uFF09])\s+/g, '$1\n$2 ')
}

function normalizeReasoningParagraphs(content: string) {
  return content
    .replace(/([。！？；：])([^\n])/g, '$1\n$2')
    .replace(/\n{3,}/g, '\n\n')
}

function normalizeHeadings(content: string) {
  return content
    .replace(/(^|\n)(#{1,6})([^\s#\n])/g, '$1$2 $3')
    .replace(/([^\n])\s*(#{1,6}\s+)/g, '$1\n\n$2')
}

function getTableCells(line: string) {
  return line
    .trim()
    .replace(/^\|/, '')
    .replace(/\|$/, '')
    .split('|')
    .map((item) => item.trim())
}

function formatTableLine(cells: string[]) {
  return `| ${cells.join(' | ')} |`
}

function normalizeTableLine(line: string) {
  const trimmed = line.trim()
  if (!trimmed.includes('|')) return line
  return formatTableLine(getTableCells(trimmed))
}

function looksLikeTableSeparator(line: string) {
  return /^[:\-\|\s]+$/.test(line.trim()) && line.includes('-')
}

function looksLikeTableRow(line: string) {
  const trimmed = line.trim()
  if (!trimmed.includes('|')) return false
  if (trimmed.startsWith('```')) return false
  const cells = getTableCells(trimmed).filter(Boolean)
  return cells.length >= 2
}

function escapeHtml(text: string) {
  return text
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;')
}

function renderHtmlTable(headers: string[], rows: string[][]) {
  const headerHtml = headers.map((cell) => `<th>${escapeHtml(cell)}</th>`).join('')
  const bodyHtml = rows.map((row) => {
    const cells = row.map((cell) => `<td>${escapeHtml(cell)}</td>`).join('')
    return `<tr>${cells}</tr>`
  }).join('')
  return [
    '<div class="qa-table-wrap">',
    '<table>',
    `<thead><tr>${headerHtml}</tr></thead>`,
    `<tbody>${bodyHtml}</tbody>`,
    '</table>',
    '</div>',
  ].join('')
}

function splitInlineTableSegments(content: string) {
  return content
    .replace(/(\*\*[^*\n]+\*\*)(\|)/g, '$1\n$2')
    .replace(/\|([-:\s|]{3,})\|/g, '\n|$1|\n')
    .replace(/([^\n])(\|[-:\s|]{3,}\|)/g, '$1\n$2')
    .replace(/(\|[-:\s|]{3,}\|)([^\n|])/g, '$1\n$2')
}

function normalizeMarkdownTables(content: string) {
  const prepared = splitInlineTableSegments(content)
  const lines = prepared.replace(/\r\n/g, '\n').split('\n')
  const normalized: string[] = []
  let index = 0

  while (index < lines.length) {
    const current = lines[index]
    const next = lines[index + 1]

    if (looksLikeTableRow(current) && next && looksLikeTableSeparator(next)) {
      const headerCells = getTableCells(current)
      const columnCount = headerCells.length
      const trailingParagraphs: string[] = []
      const dataRows: string[] = []
      index += 2

      while (index < lines.length && looksLikeTableRow(lines[index])) {
        const cells = getTableCells(lines[index])
        if (cells.length > columnCount) {
          dataRows.push(formatTableLine(cells.slice(0, columnCount)))
          const remainder = cells.slice(columnCount).join(' | ').trim()
          if (remainder) {
            trailingParagraphs.push(remainder)
          }
        } else if (cells.length === columnCount) {
          dataRows.push(formatTableLine(cells))
        } else {
          break
        }
        index += 1
      }

      if (!dataRows.length) {
        normalized.push(current)
        normalized.push(next)
        continue
      }

      if (normalized[normalized.length - 1] !== '') {
        normalized.push('')
      }
      normalized.push(renderHtmlTable(headerCells, dataRows.map((row) => getTableCells(row))))
      if (trailingParagraphs.length) {
        normalized.push('')
        normalized.push(...trailingParagraphs)
      }
      if (lines[index] !== '') {
        normalized.push('')
      }
      continue
    }

    normalized.push(current)
    index += 1
  }

  return normalized
    .join('\n')
    .replace(/\n{3,}/g, '\n\n')
}

export function renderQaMarkdown(content: string) {
  const normalized = normalizeMarkdownTables(
    normalizeReasoningParagraphs(
      normalizeHeadings(
        normalizeInlineLists(content || '')
      )
    )
  )
  const repaired = remend(normalized, {
    linkMode: 'text-only',
  })
  return DOMPurify.sanitize(md.render(repaired))
}
