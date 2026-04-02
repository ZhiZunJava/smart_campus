import remend from 'remend'

const CODE_FENCE_BLOCK_RE = /(```[\s\S]*?```|~~~[\s\S]*?~~~)/g
const STRUCTURE_AHEAD_RE = '(?:#{1,6}|>|[-*+•●▪◦·‣]|\\d+[.\\)\\uFF09、]|\\|(?:[^\\n|]+\\|){1,}|:::\\s*echarts|```|~~~|第[一二三四五六七八九十]+阶段|[（(]?[一二三四五六七八九十]+[、)）]|周[一二三四五六日天末])'
const TABLE_PREFIX_RE = /(?:[：:]|\*\*|如下|如下所示|表格如下|示例如下)$/
const CHART_TYPE_RE = /["']?type["']?\s*[:=]\s*["']?(line|bar|pie|scatter|radar|funnel|gauge|treemap|sunburst|map|heatmap|candlestick|boxplot|effectScatter|sankey|graph|parallel|themeRiver|pictorialBar)/i

type MarkdownLineKind = 'blank' | 'heading' | 'table' | 'list' | 'blockquote' | 'paragraph'
type TextRange = { start: number; end: number; text: string }

function countMatches(content: string, pattern: RegExp) {
  return (content.match(pattern) || []).length
}

function closeUnbalancedFence(content: string, fence: '```' | '~~~') {
  const escapedFence = fence.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
  const fenceCount = countMatches(content, new RegExp(escapedFence, 'g'))
  if (fenceCount % 2 === 0) {
    return content
  }
  return `${content.trimEnd()}\n${fence}`
}

function normalizePseudoLineBreaks(content: string) {
  return content
    .replace(/^\uFEFF/, '')
    .replace(/\r\n?/g, '\n')
    .replace(/\\r\\n?/g, '\n')
    .replace(/\\n/g, '\n')
    .replace(/\\t/g, '  ')
    .replace(/<br\s*\/?>/gi, '\n')
    .replace(/\u00A0/g, ' ')
    .split('\n')
    .map((line) => {
      if (line.includes('|')) {
        return normalizeBrokenTableSeparatorLine(line)
      }
      return line
        .replace(new RegExp(`([^\\nA-Za-z])nn(?=${STRUCTURE_AHEAD_RE})`, 'gu'), '$1\n\n')
        .replace(new RegExp(`([^\\nA-Za-z])n(?=${STRUCTURE_AHEAD_RE})`, 'gu'), '$1\n')
    })
    .join('\n')
}

function normalizeStructuredBlocks(content: string) {
  let normalized = content
    .replace(/(:::\s*echarts)\s*```/g, '$1\n```')
    .replace(/(^|\n)(:::\s*echarts)(?=[^\n])/g, '$1$2\n')
    .replace(/([}\]])\s*(```|~~~)/g, '$1\n$2')
    .replace(/(```|~~~)\s*:::/g, '$1\n:::')

  normalized = normalized
    .split('\n')
    .flatMap((line) => {
      const fencePrefixMatch = line.match(/^(```|~~~)\s*[A-Za-z0-9_-]+/)
      if (!fencePrefixMatch) {
        return [line]
      }

      const normalizedFencePrefix = fencePrefixMatch[0].replace(/\s+/g, '')
      const rest = line.slice(fencePrefixMatch[0].length)
      if (!rest || !rest.trim()) {
        return [line]
      }

      return [normalizedFencePrefix, rest.trimStart()]
    })
    .join('\n')

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

  normalized = closeUnbalancedFence(normalized, '```')
  normalized = closeUnbalancedFence(normalized, '~~~')

  return normalized
}

function normalizeFenceSpacing(content: string) {
  const lines = content.split('\n')
  const output: string[] = []
  let activeFence: '```' | '~~~' | '' = ''

  for (let index = 0; index < lines.length; index += 1) {
    const line = lines[index]
    if (!activeFence) {
      const inlineOpeningMatch = line.match(/^(.*?)(```|~~~)([A-Za-z0-9_-]*)\s*$/)
      if (inlineOpeningMatch && inlineOpeningMatch[1].trim()) {
        const [, prefix, fence, info] = inlineOpeningMatch
        const trimmedPrefix = prefix.replace(/[ \t]+$/g, '')
        if (trimmedPrefix) {
          output.push(trimmedPrefix)
        }
        if (output.length && output[output.length - 1] !== '') {
          output.push('')
        }
        output.push(`${fence}${info}`)
        activeFence = fence as '```' | '~~~'
        continue
      }
    }

    const trimmed = line.trim()
    const fenceMarker = trimmed.startsWith('```')
      ? '```'
      : trimmed.startsWith('~~~')
        ? '~~~'
        : ''

    if (!activeFence && fenceMarker) {
      if (output.length && output[output.length - 1] !== '') {
        output.push('')
      }
      output.push(line)
      activeFence = fenceMarker
      continue
    }

    if (activeFence && fenceMarker === activeFence && new RegExp(`^${activeFence}\\s*$`).test(trimmed)) {
      output.push(line)
      activeFence = ''
      const nextLine = lines[index + 1]
      if (nextLine?.trim()) {
        output.push('')
      }
      continue
    }

    output.push(line)
  }

  return output.join('\n')
}

function normalizeIntroTitle(content: string) {
  return content.replace(
    /^\s*([^\n#`]{2,40}?)(?=(?:##|###|[一二三四五六七八九十]+、|[（(][一二三四五六七八九十]+[)）]|第[一二三四五六七八九十]+阶段))/,
    '# $1\n\n',
  )
}

function normalizeHeadings(content: string) {
  return content
    .replace(/(^|\n)\*\*([一二三四五六七八九十]+、[^*\n]+)\*\*(?=\n|$)/g, '$1## $2')
    .replace(/(^|\n)\*\*([（(][一二三四五六七八九十]+[)）][^*\n]+)\*\*(?=\n|$)/g, '$1### $2')
    .replace(/(^|\n)([一二三四五六七八九十]+、[^\n#]+)/g, '$1## $2')
    .replace(/(^|\n)([（(][一二三四五六七八九十]+[)）][^\n#]+)/g, '$1### $2')
    .replace(/(^|\n)(第[一二三四五六七八九十]+阶段[：:][^\n#]+)/g, '$1### $2')
    .replace(/([^\n#])\s*(#{1,6})([^\s#\n])/g, '$1\n\n$2 $3')
    .replace(/(^|\n)(#{1,6})([^\s#\n])/g, '$1$2 $3')
    .replace(/([^\n])\s*(#{1,6}\s+)/g, '$1\n\n$2')
    .replace(/^\s*#{1,6}\s*$/gm, '')
    .replace(/(^|\n)(#{1,6}\s+[^\n#]*?)(\d+[.\)\uFF09]\s+(?=(?:\*\*|[^\s])))/g, '$1$2\n$3')
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

function looksLikeTableLine(line: string) {
  if (!line.includes('|')) {
    return false
  }

  const cells = getPipeCells(line).filter(Boolean)
  return cells.length >= 2
}

function normalizeTableLeadInLine(line: string) {
  if (!line.includes('|')) {
    return line
  }

  const firstPipeIndex = line.indexOf('|')
  if (firstPipeIndex <= 0) {
    return line
  }

  const prefix = line.slice(0, firstPipeIndex).trimEnd()
  const tableLine = line.slice(firstPipeIndex).trimStart()

  if (!prefix || !looksLikeTableLine(tableLine) || !TABLE_PREFIX_RE.test(prefix)) {
    return line
  }

  return `${prefix}\n${tableLine}`
}

function splitDenseOrderedList(line: string) {
  let normalized = line
  let guard = 0

  while (guard < 12) {
    const next = normalized.replace(
      /(^\s*\d+\.\s+[^\n]+?)\s+(\d+)\.\s+(?=[\u4E00-\u9FA5A-Za-z(（【"“])/u,
      '$1\n$2. ',
    )

    if (next === normalized) {
      return normalized
    }

    normalized = next
    guard += 1
  }

  return normalized
}

function splitDenseBulletList(line: string) {
  let normalized = line
  let guard = 0

  while (guard < 12) {
    const next = normalized.replace(
      /(^\s*-\s+[^\n]+?)\s+-\s+(?=[\u4E00-\u9FA5A-Za-z(（【"“])/u,
      '$1\n- ',
    )

    if (next === normalized) {
      return normalized
    }

    normalized = next
    guard += 1
  }

  return normalized
}

function normalizeLineMarkers(content: string) {
  return content
    .split('\n')
    .map((line) => {
      let normalized = normalizeTableLeadInLine(line.replace(/[ \t]+$/g, ''))

      if (normalized.includes('\n')) {
        return normalized
      }

      return normalized
        .replace(/^(\s*)[•●▪◦·‣]\s*/u, '$1- ')
        .replace(/^(\s*)(\d+)[\)\uFF09、]\s*/u, '$1$2. ')
        .replace(/^(\s*)([-*+])(?=\S)/u, '$1$2 ')
        .replace(/^(\s*)(>+)(?=\S)/u, '$1$2 ')
        .replace(/^(\s*)(#{1,6})([^\s#\n])/u, '$1$2 $3')
    })
    .join('\n')
}

function normalizeInlineLists(content: string) {
  return content
    .split('\n')
    .map((line) => {
      if (looksLikeTableLine(line)) {
        return line
      }

      const normalized = line
        .replace(/([：:])\s*[•●▪◦·‣-]\s*(?=\S)/gu, '$1\n- ')
        .replace(/([。！？；;])\s*[•●▪◦·‣-]\s*(?=\S)/gu, '$1\n- ')
        .replace(/([：:])\s*(\d+)[.\)\uFF09、]\s*(?=\S)/gu, '$1\n$2. ')
        .replace(/([。！？；;])\s*(\d+)[.\)\uFF09、]\s*(?=\S)/gu, '$1\n$2. ')
        .replace(/([：:])\s*(>+)(?=\S)/g, '$1\n$2 ')

      return splitDenseBulletList(splitDenseOrderedList(normalized))
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

function classifyMarkdownLine(line: string): MarkdownLineKind {
  const trimmed = line.trim()

  if (!trimmed) {
    return 'blank'
  }
  if (/^#{1,6}\s/.test(trimmed)) {
    return 'heading'
  }
  if (/^>+\s/.test(trimmed)) {
    return 'blockquote'
  }
  if (/^(?:[-*+]\s|\d+\.\s)/.test(trimmed)) {
    return 'list'
  }
  if (looksLikeTableLine(trimmed) || looksLikeBrokenTableSeparatorLine(trimmed)) {
    return 'table'
  }
  return 'paragraph'
}

function normalizeParagraphSpacing(content: string) {
  const output: string[] = []

  for (const line of content.split('\n')) {
    const currentKind = classifyMarkdownLine(line)

    if (currentKind === 'blank') {
      if (output[output.length - 1] !== '') {
        output.push('')
      }
      continue
    }

    const previousLine = output.length ? output[output.length - 1] : ''
    const previousKind = classifyMarkdownLine(previousLine)
    const shouldInsertBlankLine
      = previousKind !== 'blank'
        && (
          currentKind === 'heading'
          || currentKind === 'table'
          || currentKind === 'blockquote'
          || previousKind === 'heading'
          || previousKind === 'table'
          || previousKind === 'blockquote'
          || (previousKind === 'paragraph' && currentKind === 'list')
          || (previousKind === 'list' && currentKind === 'paragraph')
        )
        && !(previousKind === 'table' && currentKind === 'table')
        && !(previousKind === 'list' && currentKind === 'list')

    if (shouldInsertBlankLine && output[output.length - 1] !== '') {
      output.push('')
    }

    output.push(line)
  }

  while (output[output.length - 1] === '') {
    output.pop()
  }

  return output.join('\n')
}

function normalizeOutsideCodeFences(content: string) {
  return content
    .split(CODE_FENCE_BLOCK_RE)
    .map((segment) => {
      if (segment.startsWith('```') || segment.startsWith('~~~')) {
        return segment
      }

      return normalizeParagraphSpacing(
        normalizeMarkdownTables(
          normalizeInlineLists(
            normalizeLineMarkers(
              normalizeHeadings(
                normalizeIntroTitle(segment),
              ),
            ),
          ),
        ),
      )
    })
    .join('')
}

function normalizePotentialChartSource(content: string) {
  return String(content || '')
    .replace(/\r\n?/g, '\n')
    .replace(/[“”]/g, '"')
    .replace(/[‘’]/g, '\'')
    .replace(/（/g, '(')
    .replace(/）/g, ')')
    .replace(/，/g, ',')
    .replace(/：/g, ':')
    .replace(/；/g, ';')
}

function looksLikeCompleteChartOption(content: string) {
  const source = normalizePotentialChartSource(content).trim()
  if (!source) return false

  if (!((source.startsWith('{') && source.endsWith('}')) || (source.startsWith('[') && source.endsWith(']')))) {
    return false
  }

  let depth = 0
  let inString = false
  let escaped = false

  for (const char of source) {
    if (escaped) {
      escaped = false
      continue
    }

    if (char === '\\') {
      escaped = true
      continue
    }

    if (char === '"') {
      inString = !inString
      continue
    }

    if (inString) {
      continue
    }

    if (char === '{' || char === '[') {
      depth += 1
    } else if (char === '}' || char === ']') {
      depth -= 1
      if (depth < 0) {
        return false
      }
    }
  }

  return depth === 0 && !inString
}

function extractBalancedRange(content: string, start: number): TextRange | null {
  const source = normalizePotentialChartSource(content)
  const opening = source[start]
  const closing = opening === '{' ? '}' : opening === '[' ? ']' : ''
  if (!closing) return null

  let depth = 0
  let inString = false
  let escaped = false

  for (let index = start; index < source.length; index += 1) {
    const char = source[index]

    if (escaped) {
      escaped = false
      continue
    }

    if (char === '\\') {
      escaped = true
      continue
    }

    if (char === '"') {
      inString = !inString
      continue
    }

    if (inString) {
      continue
    }

    if (char === opening) {
      depth += 1
    } else if (char === closing) {
      depth -= 1
      if (depth === 0) {
        return {
          start,
          end: index + 1,
          text: source.slice(start, index + 1).trim(),
        }
      }
    }
  }

  return null
}

function extractFirstJsonObject(content: string) {
  const source = normalizePotentialChartSource(content)
  const start = source.indexOf('{')
  if (start < 0) {
    return ''
  }

  return extractBalancedRange(source, start)?.text || ''
}

function countChartKeys(content: string) {
  const matches = normalizePotentialChartSource(content).matchAll(
    /["']?(series|dataset|xAxis|yAxis|legend|tooltip|grid|title|visualMap|radar|polar|angleAxis|radiusAxis|geo|calendar|parallelAxis|singleAxis|timeline)["']?\s*[:=]/gi,
  )
  const keys = new Set<string>()

  for (const match of matches) {
    keys.add(String(match[1] || ''))
  }

  return keys
}

function looksLikeChartOptionCandidate(content: string) {
  const source = normalizePotentialChartSource(content).trim()
  if (!source || !looksLikeCompleteChartOption(source)) {
    return false
  }

  const keys = countChartKeys(source)
  const hasSeries = keys.has('series')
  const hasDataset = keys.has('dataset')
  const hasAxis = keys.has('xAxis') || keys.has('yAxis') || keys.has('angleAxis') || keys.has('radiusAxis') || keys.has('singleAxis')
  const hasMeta = keys.has('legend') || keys.has('tooltip') || keys.has('grid') || keys.has('title') || keys.has('visualMap') || keys.has('radar') || keys.has('polar') || keys.has('geo') || keys.has('calendar') || keys.has('parallelAxis') || keys.has('timeline')
  const hasChartType = CHART_TYPE_RE.test(source)

  return (hasSeries && (hasAxis || hasMeta || hasChartType))
    || (hasDataset && (hasAxis || hasMeta || hasChartType))
}

function findNextObjectRange(content: string, startIndex: number) {
  const source = normalizePotentialChartSource(content)
  for (let index = Math.max(0, startIndex); index < source.length; index += 1) {
    const char = source[index]
    if (char === '{' || char === '[') {
      return extractBalancedRange(source, index)
    }
  }

  return null
}

function collectTopLevelObjectRanges(content: string) {
  const source = normalizePotentialChartSource(content)
  const ranges: TextRange[] = []

  for (let index = 0; index < source.length; index += 1) {
    const char = source[index]
    if (char !== '{' && char !== '[') {
      continue
    }

    const range = extractBalancedRange(source, index)
    if (!range) {
      continue
    }

    ranges.push(range)
    index = range.end - 1
  }

  return ranges
}

function dedupeOverlappingRanges(ranges: TextRange[]) {
  const sorted = [...ranges].sort((left, right) => {
    if (left.start !== right.start) {
      return left.start - right.start
    }
    return (right.end - right.start) - (left.end - left.start)
  })

  const deduped: TextRange[] = []
  for (const range of sorted) {
    const previous = deduped[deduped.length - 1]
    if (!previous || range.start >= previous.end) {
      deduped.push(range)
    }
  }

  return deduped
}

function collectLikelyChartOptionRanges(content: string) {
  const source = normalizePotentialChartSource(content)
  const candidateRanges: TextRange[] = []

  for (const match of source.matchAll(/setOption\s*\(/gi)) {
    const matchIndex = match.index ?? -1
    if (matchIndex < 0) {
      continue
    }

    const range = findNextObjectRange(source, matchIndex + match[0].length)
    if (range && looksLikeChartOptionCandidate(range.text)) {
      candidateRanges.push(range)
    }
  }

  for (const match of source.matchAll(/(?:(?:const|let|var)\s+)?options?\s*[:=]\s*/gi)) {
    const matchIndex = match.index ?? -1
    if (matchIndex < 0) {
      continue
    }

    const range = findNextObjectRange(source, matchIndex + match[0].length)
    if (range && looksLikeChartOptionCandidate(range.text)) {
      candidateRanges.push(range)
    }
  }

  for (const range of collectTopLevelObjectRanges(source)) {
    if (looksLikeChartOptionCandidate(range.text)) {
      candidateRanges.push(range)
    }
  }

  return dedupeOverlappingRanges(candidateRanges)
}

function extractLikelyChartOptionSource(content: string) {
  return collectLikelyChartOptionRanges(content)[0]?.text || ''
}

function toEchartsFence(optionSource: string) {
  const normalizedOptionSource = extractLikelyChartOptionSource(optionSource) || normalizePotentialChartSource(optionSource).trim()
  if (!looksLikeChartOptionCandidate(normalizedOptionSource)) {
    return '\n> 图表生成中...\n'
  }

  return `\`\`\`qa-echarts\n${normalizedOptionSource}\n\`\`\``
}

function normalizeBrokenChartFenceDeclarations(content: string) {
  return content
    .replace(/```+\s*qa[\s_-]*echart\s*\n\s*s(?=\s*\n[\[{])/gi, '```qa-echarts')
    .replace(/```+\s*qa[\s_-]*echarts?\b/gi, '```qa-echarts')
    .replace(/```+\s*echarts?\b/gi, '```qa-echarts')
    .replace(/```+\s*chart\b/gi, '```qa-echarts')
}

function normalizeChartFenceBlocks(content: string) {
  return normalizeBrokenChartFenceDeclarations(content).replace(
    /```([^\n`]*)\n([\s\S]*?)\n```/g,
    (fullMatch, info: string, body: string) => {
      const normalizedInfo = String(info || '').trim().toLowerCase().replace(/[_\s]+/g, '-')
      const isChartFence = ['qa-echarts', 'qa-echart', 'echarts', 'echart', 'chart'].includes(normalizedInfo)
      const isGenericFence = ['json', 'js', 'javascript', 'ts', 'typescript', 'yaml', 'yml'].includes(normalizedInfo)
      if (!isChartFence && !isGenericFence) {
        return fullMatch
      }

      const candidateRanges = collectLikelyChartOptionRanges(body)
      if (!candidateRanges.length) {
        return fullMatch
      }

      return replaceChartRangesWithFences(body, candidateRanges).trim()
    },
  )
}

function replaceChartRangesWithFences(content: string, ranges: TextRange[]) {
  let normalized = content

  for (const range of [...ranges].sort((left, right) => right.start - left.start)) {
    const before = normalized.slice(0, range.start).replace(/[ \t]*$/g, '').replace(/\n+$/g, '')
    const after = normalized.slice(range.end).replace(/^[ \t]*\n*/g, '')
    const prefix = before ? '\n\n' : ''
    const suffix = after ? '\n\n' : ''
    normalized = `${before}${prefix}${toEchartsFence(range.text)}${suffix}${after}`
  }

  return normalized
}

function normalizePlainChartObjects(content: string) {
  return content
    .split(CODE_FENCE_BLOCK_RE)
    .map((segment) => {
      if (segment.startsWith('```') || segment.startsWith('~~~')) {
        return segment
      }

      const candidateRanges = collectLikelyChartOptionRanges(segment)

      if (!candidateRanges.length) {
        return segment
      }

      return replaceChartRangesWithFences(segment, candidateRanges)
    })
    .join('')
}

function normalizeEchartsContainerBlocks(content: string) {
  let normalized = normalizeBrokenChartFenceDeclarations(content).replace(
    /:::\s*echarts\s*\n+```(?:json|yaml)?\s*\n([\s\S]*?)\n```(?:\s*\n)?(?::{3}|\.{3,})?/g,
    (_, optionSource: string) => {
      const candidateRanges = collectLikelyChartOptionRanges(optionSource)
      if (!candidateRanges.length) {
        return '\n> 图表生成中...\n'
      }

      return replaceChartRangesWithFences(optionSource, candidateRanges).trim()
    },
  )

  if (/:::\s*echarts/.test(normalized) && !/```qa-echarts/.test(normalized)) {
    normalized = normalized.replace(
      /:::\s*echarts[\s\S]*?(?=($|\n#{1,6}\s|\n##\s|\n###\s))/g,
      (block) => {
        const optionSource = extractFirstJsonObject(block)
        return optionSource ? toEchartsFence(optionSource) : '\n> 图表生成中...\n'
      },
    )
  }

  return normalizePlainChartObjects(normalizeChartFenceBlocks(normalized))
}

function normalizeMarkdownCore(content: string) {
  const normalized = normalizeFenceSpacing(
    normalizeOutsideCodeFences(
      normalizeFenceSpacing(
        normalizeStructuredBlocks(
          normalizePseudoLineBreaks(String(content || '')),
        ),
      ),
    ),
  )

  return remend(
    normalized
      .replace(/[ \t]+\n/g, '\n')
      .replace(/\n{3,}/g, '\n\n')
      .trim(),
    {
      linkMode: 'text-only',
    },
  )
}

export function normalizeQaMarkdownSource(content: string) {
  return normalizeMarkdownCore(content)
}

export function normalizeQaMarkdownDocument(content: string) {
  return normalizeMarkdownCore(
    normalizeEchartsContainerBlocks(String(content || '').replace(/\r\n?/g, '\n')),
  )
}

export function extractQaChartOptionSource(content: string) {
  return extractLikelyChartOptionSource(content)
}

export function extractQaChartOptionSources(content: string) {
  return collectLikelyChartOptionRanges(content).map((range) => range.text)
}
