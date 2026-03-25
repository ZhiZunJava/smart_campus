import remend from 'remend'

const CODE_FENCE_BLOCK_RE = /(```[\s\S]*?```|~~~[\s\S]*?~~~)/g
const STRUCTURE_AHEAD_RE = '(?:#{1,6}|[-*+]|\\d+[.\\)\\uFF09]|:::\\s*echarts|```|~~~|第[一二三四五六七八九十]+阶段|[一二三四五六七八九十]+、|周[一二三四五六日天末])'

function countMatches(content: string, pattern: RegExp) {
  return (content.match(pattern) || []).length
}

function normalizePseudoLineBreaks(content: string) {
  return content
    .replace(/\r\n?/g, '\n')
    .replace(/\\n/g, '\n')
    .split('\n')
    .map((line) => {
      if (line.includes('|')) {
        return normalizeBrokenTableSeparatorLine(line)
      }
      return line
        .replace(new RegExp(`([^\\nA-Za-z])nn(?=${STRUCTURE_AHEAD_RE})`, 'g'), '$1\n\n')
        .replace(new RegExp(`([^\\nA-Za-z])n(?=${STRUCTURE_AHEAD_RE})`, 'g'), '$1\n')
    })
    .join('\n')
}

function normalizeStructuredBlocks(content: string) {
  let normalized = content
    .replace(/(:::\s*echarts)\s*```/g, '$1\n```')
    .replace(/(^|\n)(:::\s*echarts)(?=[^\n])/g, '$1$2\n')
    .replace(/(```[A-Za-z0-9_-]+)(?=[^\n])/g, '$1\n')
    .replace(/(~~~[A-Za-z0-9_-]+)(?=[^\n])/g, '$1\n')
    .replace(/([}\]])\s*(```|~~~)/g, '$1\n$2')
    .replace(/(```|~~~)\s*:::/g, '$1\n:::')

  const echartsOpenCount = countMatches(normalized, /(^|\n):::\s*echarts\b/g)
  const echartsCloseCount = countMatches(normalized, /(^|\n):::\s*$/gm)

  if (echartsOpenCount > echartsCloseCount) {
    const fenceCount = countMatches(normalized, /```/g)
    normalized = normalized.trimEnd()
    if (fenceCount % 2 === 1) {
      normalized += '\n```'
    }
    normalized += '\n:::'
  }

  return normalized
}

function normalizeIntroTitle(content: string) {
  return content.replace(
    /^\s*([^\n#`]{2,40}?)(?=(?:##|###|[一二三四五六七八九十]+、|第[一二三四五六七八九十]+阶段))/,
    '# $1\n\n',
  )
}

function normalizeHeadings(content: string) {
  return content
    .replace(/(^|\n)([一二三四五六七八九十]+、[^\n#]+)/g, '$1## $2')
    .replace(/(^|\n)(第[一二三四五六七八九十]+阶段[：:][^\n#]+)/g, '$1### $2')
    .replace(/([^\n#])\s*(#{1,6})([^\s#\n])/g, '$1\n\n$2 $3')
    .replace(/(^|\n)(#{1,6})([^\s#\n])/g, '$1$2 $3')
    .replace(/([^\n])\s*(#{1,6}\s+)/g, '$1\n\n$2')
    .replace(/^\s*#{1,6}\s*$/gm, '')
    .replace(/(^|\n)(#{1,6}\s+[^\n#]*?)(\d+[.\)\uFF09]\s+(?=(?:\*\*|[^\s])))/g, '$1$2\n$3')
}

function normalizeInlineLists(content: string) {
  return content
    .split('\n')
    .map((line) => {
      if (line.includes('|')) {
        return line
      }
      return line
        .replace(/^([-*+])(?=\S)/g, '$1 ')
        .replace(/([：:])\s*([-*+])(?=\S)/g, '$1\n$2 ')
        .replace(/([^\n])\s+([-*+])(?=[\u4e00-\u9fa5A-Za-z(（【])/g, '$1\n$2 ')
        .replace(/^(\d+[.\)\uFF09])(?=\S)/g, '$1 ')
        .replace(/([：:])\s*(\d+[.\)\uFF09])(?=\S)/g, '$1\n$2 ')
        .replace(/([：:])\s*(\d+[.\)\uFF09])\s*/g, '$1\n$2 ')
        .replace(/([^\n])(\d+[.\)\uFF09])(?=[\u4e00-\u9fa5A-Za-z])/g, '$1\n$2 ')
        .replace(/([^\n])\s+(\d+[.\)\uFF09])\s+/g, '$1\n$2 ')
    })
    .join('\n')
}

function normalizeMarkdownTables(content: string) {
  return content
    .replace(/(\*\*[^*\n]+\*\*)(\|)/g, '$1\n$2')
    .replace(/([^\n| \t])(\|[-: \t|]{3,}\|)(?=[^\n]*$)/gm, '$1\n$2')
    .replace(/^(\|[-: \t|]{3,}\|)([^\n| \t])/gm, '$1\n$2')
    .split('\n')
    .map((line) => normalizeBrokenTableSeparatorLine(line))
    .join('\n')
}

function getPipeCells(line: string) {
  return line
    .trim()
    .replace(/^\|/, '')
    .replace(/\|$/, '')
    .split('|')
    .map((cell) => cell.trim())
}

function looksLikeBrokenTableSeparatorLine(line: string) {
  if (!line.includes('|')) {
    return false
  }

  const cells = getPipeCells(line).filter(Boolean)
  return cells.length >= 2 && cells.every((cell) => /^[\s:n-]+$/.test(cell) && cell.includes('-'))
}

function normalizeTableSeparatorCell(cell: string) {
  const compact = cell.replace(/[n\s]/g, '')
  const hasLeadingColon = compact.startsWith(':')
  const hasTrailingColon = compact.endsWith(':')

  if (hasLeadingColon && hasTrailingColon) {
    return ':---:'
  }
  if (hasLeadingColon) {
    return ':---'
  }
  if (hasTrailingColon) {
    return '---:'
  }
  return '---'
}

function normalizeBrokenTableSeparatorLine(line: string) {
  if (!looksLikeBrokenTableSeparatorLine(line)) {
    return line
  }

  const cells = getPipeCells(line).map((cell) => normalizeTableSeparatorCell(cell))
  return `| ${cells.join(' | ')} |`
}

function normalizeOutsideCodeFences(content: string) {
  return content
    .split(CODE_FENCE_BLOCK_RE)
    .map((segment) => {
      if (segment.startsWith('```') || segment.startsWith('~~~')) {
        return segment
      }

      return normalizeMarkdownTables(
        normalizeInlineLists(
          normalizeHeadings(
            normalizeIntroTitle(segment),
          ),
        ),
      )
    })
    .join('')
}

export function normalizeQaMarkdownSource(content: string) {
  const normalized = normalizeOutsideCodeFences(
    normalizeStructuredBlocks(
      normalizePseudoLineBreaks(String(content || '')),
    ),
  )

  return remend(
    normalized
      .replace(/\n{3,}/g, '\n\n')
      .trim(),
    {
      linkMode: 'text-only',
    },
  )
}
