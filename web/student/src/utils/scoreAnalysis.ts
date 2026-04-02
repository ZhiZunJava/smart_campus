import type { EChartsCoreOption } from 'echarts'

const PALETTE = {
  brand: '#2f6bff',
  brandDeep: '#1e52c8',
  brandLight: '#6b9aff',
  brandSoft: '#eff4ff',
  cyan: '#14b8a6',
  teal: '#0ea5e9',
  amber: '#f59e0b',
  orange: '#f97316',
  text: '#1e293b', // Slate 800
  textSecondary: '#64748b', // Slate 500
  border: '#e2e8f0', // Slate 200
  grid: 'rgba(226, 232, 240, 0.6)',
}

type TooltipRow = {
  label: string
  value: string
  color?: string
  emphasis?: boolean
}

export function formatScoreNumber(value: any, fallback = '-') {
  if (value == null || value === '') return fallback
  const num = Number(value)
  if (Number.isNaN(num)) return String(value)
  return Number.isInteger(num) ? String(num) : num.toFixed(2)
}

export function resolveScoreColor(value: number) {
  if (value >= 90) return PALETTE.brand
  if (value >= 80) return PALETTE.brandLight
  if (value >= 60) return '#94a3b8'
  return PALETTE.orange
}

export function resolveGradeColor(level: string) {
  if (level.startsWith('A')) return PALETTE.brand
  if (level.startsWith('B')) return PALETTE.brandLight
  if (level.startsWith('C')) return '#8baaff'
  if (level === 'D') return PALETTE.amber
  return PALETTE.orange
}

export function buildCourseChartLabel(item: any, withTerm?: boolean) {
  const courseName = item?.courseName || '未命名课程'
  if (withTerm && item?.termName) {
    return `${courseName} · ${item.termName}`
  }
  return courseName
}

function hexToRgba(hex: string, alpha: number) {
  const normalized = hex.replace('#', '')
  const safe = normalized.length === 3
    ? normalized.split('').map((char) => `${char}${char}`).join('')
    : normalized
  const value = Number.parseInt(safe, 16)
  const r = (value >> 16) & 255
  const g = (value >> 8) & 255
  const b = value & 255
  return `rgba(${r}, ${g}, ${b}, ${alpha})`
}

function tooltipMarker(color: string) {
  return `<span style="display:inline-flex;width:8px;height:8px;border-radius:50%;background:${color};box-shadow:0 0 0 3px ${hexToRgba(color, 0.15)};"></span>`
}

function buildTooltipRows(rows: TooltipRow[]) {
  return rows.map((row) => `
    <div style="display:flex;align-items:center;justify-content:space-between;gap:20px;margin-top:8px;">
      <span style="display:inline-flex;align-items:center;gap:8px;color:${PALETTE.textSecondary};font-size:12px;">
        ${tooltipMarker(row.color || PALETTE.brand)}
        ${row.label}
      </span>
      <strong style="color:${PALETTE.text};font-size:${row.emphasis ? '14px' : '13px'};font-weight:700;">
        ${row.value}
      </strong>
    </div>
  `).join('')
}

function buildTooltipCard(title: string, rows: TooltipRow[], subtitle?: string) {
  return `
    <div style="
      min-width: 160px;
      padding: 12px 16px;
      -webkit-font-smoothing: antialiased;
    ">
      <div style="color:${PALETTE.text};font-size:14px;font-weight:700;line-height:1.2;">${title}</div>
      ${subtitle ? `<div style="margin-top:4px;color:#94a3b8;font-size:11px;">${subtitle}</div>` : ''}
      <div style="margin-top: 10px; border-top: 1px dashed ${PALETTE.border}; padding-top: 4px;">
        ${buildTooltipRows(rows)}
      </div>
    </div>
  `
}

function resolveTooltipPosition(point: number[] = [], size?: { contentSize?: number[]; viewSize?: number[] }) {
  const [contentWidth = 0, contentHeight = 0] = size?.contentSize || [0, 0]
  const [viewWidth = 0, viewHeight = 0] = size?.viewSize || [0, 0]
  const gap = 12
  const [x = 0, y = 0] = point

  const rawX = x + contentWidth + gap > viewWidth ? x - contentWidth - gap : x + gap
  const rawY = y + contentHeight + gap > viewHeight ? y - contentHeight - gap : y + gap
  const maxX = Math.max(gap, viewWidth - contentWidth - gap)
  const maxY = Math.max(gap, viewHeight - contentHeight - gap)
  const safeX = Math.min(maxX, Math.max(gap, rawX))
  const safeY = Math.min(maxY, Math.max(gap, rawY))

  return [Math.round(safeX), Math.round(safeY)]
}

function buildCommonTooltip() {
  return {
    renderMode: 'html',
    backgroundColor: 'transparent',
    borderWidth: 0,
    padding: 0,
    transitionDuration: 0.2,
    confine: true,
    position: (point: number[], _params: any, _dom: any, _rect: any, size: any) => resolveTooltipPosition(point, size),
    textStyle: {
      fontFamily: 'inherit',
    },
    // 利用 extraCssText 实现毛玻璃和微质感阴影
    extraCssText: `
      box-shadow: 0 8px 30px rgba(15, 23, 42, 0.08); 
      background: rgba(255, 255, 255, 0.9); 
      backdrop-filter: blur(10px); 
      border: 1px solid rgba(226, 232, 240, 0.8); 
      border-radius: 12px;
    `,
  }
}

function buildCommonValueAxis() {
  return {
    type: 'value',
    axisLine: { show: false },
    axisTick: { show: false },
    axisLabel: {
      color: PALETTE.textSecondary,
      fontSize: 11,
    },
    splitLine: {
      lineStyle: {
        color: PALETTE.grid,
        type: 'dashed',
      },
    },
  }
}

function buildPassMarkLine() {
  return {
    silent: true,
    symbol: 'none',
    label: {
      formatter: '及格线 60',
      color: '#94a3b8',
      fontSize: 11,
      backgroundColor: 'transparent',
      padding: [0, 0, 0, 4],
      position: 'insideEndTop',
    },
    lineStyle: {
      color: '#cbd5e1',
      type: 'dashed',
      width: 1,
    },
    data: [{ yAxis: 60 }],
  }
}

function blueHorizontalGradient() {
  return {
    type: 'linear',
    x: 0, y: 0, x2: 1, y2: 0,
    colorStops: [
      { offset: 0, color: hexToRgba(PALETTE.brand, 0.8) },
      { offset: 1, color: PALETTE.brandLight },
    ],
  }
}

function blueVerticalGradient(alpha = 0.15) {
  return {
    type: 'linear',
    x: 0, y: 0, x2: 0, y2: 1,
    colorStops: [
      { offset: 0, color: hexToRgba(PALETTE.brand, alpha) },
      { offset: 1, color: hexToRgba(PALETTE.brand, 0.01) },
    ],
  }
}

export function buildTermTrendOption(trend: any[] = []): EChartsCoreOption | undefined {
  if (!trend.length) return undefined
  return {
    animationDuration: 500,
    grid: { left: 32, right: 24, top: 34, bottom: 24, containLabel: true },
    tooltip: {
      ...buildCommonTooltip(),
      trigger: 'axis',
      axisPointer: {
        type: 'line',
        lineStyle: { color: hexToRgba(PALETTE.brand, 0.2), width: 2 },
      },
      formatter: (params: any) => {
        const current = trend[params?.[0]?.dataIndex || 0] || {}
        return buildTooltipCard(
          current.termName || '未配置学期',
          [
            { label: '平均分', value: formatScoreNumber(current.avgScore, '0'), color: PALETTE.brand, emphasis: true },
            { label: '课程数', value: `${current.courseCount || 0} 门`, color: PALETTE.teal },
            { label: '平均绩点', value: formatScoreNumber(current.avgGradePoint, '0'), color: PALETTE.cyan },
          ],
          '趋势维度'
        )
      },
    },
    xAxis: {
      type: 'category',
      boundaryGap: true,
      data: trend.map((item: any) => item.termName || '未配置学期'),
      axisTick: { show: false },
      axisLine: { lineStyle: { color: PALETTE.border } },
      axisLabel: {
        color: PALETTE.textSecondary,
        fontSize: 11,
        margin: 12,
      },
    },
    yAxis: {
      ...buildCommonValueAxis(),
      min: 0, max: 100, splitNumber: 5,
    },
    series: [
      {
        type: 'line',
        smooth: 0.4,
        symbol: 'circle',
        showSymbol: true,
        symbolSize: trend.length > 1 ? 8 : 12,
        data: trend.map((item: any) => Number(item.avgScore || 0)),
        lineStyle: { width: 3, color: PALETTE.brand },
        itemStyle: {
          color: '#fff',
          borderColor: PALETTE.brand,
          borderWidth: 2,
        },
        areaStyle: { color: blueVerticalGradient(0.12) },
        label: {
          show: trend.length <= 6,
          position: 'top',
          color: PALETTE.text,
          fontSize: 12,
          fontWeight: 600,
          formatter: (params: any) => formatScoreNumber(params.value, '0'),
        },
        markLine: buildPassMarkLine(),
      },
    ],
  }
}

export function buildCourseComparisonOption(courseSeries: any[] = [], withTerm?: boolean): EChartsCoreOption | undefined {
  const raw = courseSeries.slice(0, 10)
  if (!raw.length) return undefined
  const data = [...raw].reverse()
  return {
    animationDuration: 500,
    grid: { left: 16, right: 32, top: 16, bottom: 16, containLabel: true },
    tooltip: {
      ...buildCommonTooltip(),
      trigger: 'axis',
      axisPointer: {
        type: 'shadow',
        shadowStyle: { color: 'rgba(241, 245, 249, 0.6)' }, // 极柔和的选中态底色
      },
      formatter: (params: any) => {
        const current = data[params?.[0]?.dataIndex || 0] || {}
        return buildTooltipCard(
          buildCourseChartLabel(current, withTerm),
          [
            { label: '总评', value: formatScoreNumber(current.totalScore, '0'), color: resolveScoreColor(Number(current.totalScore || 0)), emphasis: true },
            { label: '绩点', value: formatScoreNumber(current.gradePoint, '0'), color: PALETTE.teal },
            { label: '排名', value: current.rankNo ? `第 ${current.rankNo} 名` : '-', color: PALETTE.cyan },
          ],
          current.className || '课程对比'
        )
      },
    },
    xAxis: {
      ...buildCommonValueAxis(),
      min: 0, max: 100,
    },
    yAxis: {
      type: 'category',
      data: data.map((item: any) => buildCourseChartLabel(item, withTerm)),
      axisTick: { show: false },
      axisLine: { show: false },
      axisLabel: {
        color: PALETTE.textSecondary,
        fontSize: 12,
        width: 120,
        overflow: 'truncate',
      },
    },
    series: [
      {
        type: 'bar',
        barMaxWidth: 14,
        showBackground: true,
        backgroundStyle: {
          color: '#f1f5f9', // slate-100 更浅的轨道底色
          borderRadius: 4,
        },
        label: {
          show: true,
          position: 'right',
          color: PALETTE.text,
          fontSize: 12,
          fontWeight: 600,
          formatter: (params: any) => formatScoreNumber(params.value, '0'),
        },
        data: data.map((item: any) => ({
          value: Number(item.totalScore || 0),
          itemStyle: {
            color: blueHorizontalGradient(),
            borderRadius: 4,
          },
        })),
      },
    ],
  }
}

export function buildComponentRadarOption(componentSeries: any[] = []): EChartsCoreOption | undefined {
  if (!componentSeries.length) return undefined
  return {
    animationDuration: 500,
    tooltip: {
      ...buildCommonTooltip(),
      trigger: 'item',
      formatter: () => buildTooltipCard(
        '成绩构成均分',
        componentSeries.map((item: any) => ({
          label: item.label,
          value: `${formatScoreNumber(item.avgScore, '0')} 分`,
          color: PALETTE.brand,
        })),
        '构成雷达'
      ),
    },
    radar: {
      center: ['50%', '50%'],
      radius: '65%',
      splitNumber: 4,
      indicator: componentSeries.map((item: any) => ({ name: item.label, max: 100 })),
      axisName: {
        color: PALETTE.textSecondary,
        fontSize: 12,
      },
      splitArea: {
        areaStyle: {
          color: ['transparent', '#f8fafc'], // 极其轻微的交替底色
        },
      },
      splitLine: { lineStyle: { color: PALETTE.border } },
      axisLine: { lineStyle: { color: PALETTE.border } },
    },
    series: [
      {
        type: 'radar',
        data: [
          {
            value: componentSeries.map((item: any) => Number(item.avgScore || 0)),
            name: '成绩构成',
            itemStyle: { color: PALETTE.brand },
            lineStyle: { color: PALETTE.brand, width: 2 },
            areaStyle: { color: blueVerticalGradient(0.15) },
            symbol: 'circle',
            symbolSize: 6,
          },
        ],
      },
    ],
  }
}

export function buildScoreBandChartOption(scoreBands: any[] = []): EChartsCoreOption | undefined {
  const raw = scoreBands.filter((item: any) => Number(item.count || 0) > 0)
  if (!raw.length) return undefined
  const total = raw.reduce((sum: number, item: any) => sum + Number(item.count || 0), 0)
  const colors = [PALETTE.brand, PALETTE.brandLight, '#94a3b8', '#cbd5e1', PALETTE.orange]
  
  return {
    animationDuration: 500,
    title: {
      text: String(total),
      subtext: '门课程',
      left: 'center',
      top: '38%',
      textStyle: { color: PALETTE.text, fontSize: 24, fontWeight: 700 },
      subtextStyle: { color: PALETTE.textSecondary, fontSize: 11 },
    },
    tooltip: {
      ...buildCommonTooltip(),
      trigger: 'item',
      formatter: (params: any) => buildTooltipCard(
        params.name || '分数段',
        [
          { label: '课程数', value: `${params.value || 0} 门`, color: params.color || PALETTE.brand, emphasis: true },
          { label: '占比', value: `${total ? Math.round(((params.value || 0) / total) * 100) : 0}%`, color: PALETTE.teal },
        ],
        '分数段分布'
      ),
    },
    series: [
      {
        type: 'pie',
        radius: ['55%', '75%'],
        center: ['50%', '50%'],
        startAngle: 180,
        avoidLabelOverlap: true,
        itemStyle: {
          borderRadius: 6,
          borderColor: '#fff',
          borderWidth: 2,
        },
        labelLine: {
          length: 12,
          length2: 12,
          lineStyle: { color: '#cbd5e1' },
        },
        label: {
          color: PALETTE.textSecondary,
          fontSize: 12,
          formatter: (params: any) => `${params.name}\n{val|${params.value}}`,
          rich: {
            val: {
              color: PALETTE.text,
              fontSize: 13,
              fontWeight: 'bold',
              lineHeight: 20
            }
          }
        },
        data: raw.map((item: any, index: number) => ({
          name: item.label,
          value: Number(item.count || 0),
          itemStyle: { color: colors[index % colors.length] },
        })),
      },
    ],
  }
}

export function buildGradeDistributionChartOption(gradeDistribution: any[] = []): EChartsCoreOption | undefined {
  const raw = gradeDistribution.filter((item: any) => Number(item.count || 0) > 0)
  if (!raw.length) return undefined
  return {
    animationDuration: 500,
    grid: { left: 16, right: 24, top: 16, bottom: 8, containLabel: true },
    tooltip: {
      ...buildCommonTooltip(),
      trigger: 'axis',
      axisPointer: {
        type: 'shadow',
        shadowStyle: { color: 'rgba(241, 245, 249, 0.6)' },
      },
      formatter: (params: any) => {
        const current = raw[params?.[0]?.dataIndex || 0] || {}
        return buildTooltipCard(
          `${current.gradeLevel || '未评级'} 等级`,
          [
            { label: '课程数', value: `${current.count || 0} 门`, color: resolveGradeColor(String(current.gradeLevel || '')), emphasis: true },
          ],
          '等级分布'
        )
      },
    },
    xAxis: {
      ...buildCommonValueAxis(),
      minInterval: 1,
    },
    yAxis: {
      type: 'category',
      data: raw.map((item: any) => item.gradeLevel || '未评级'),
      axisTick: { show: false },
      axisLine: { show: false },
      axisLabel: {
        color: PALETTE.text,
        fontSize: 12,
        fontWeight: 600,
      },
    },
    series: [
      {
        type: 'bar',
        barMaxWidth: 14,
        showBackground: true,
        backgroundStyle: {
          color: '#f1f5f9',
          borderRadius: 4,
        },
        label: {
          show: true,
          position: 'right',
          color: PALETTE.text,
          fontSize: 12,
          fontWeight: 600,
        },
        data: raw.map((item: any) => ({
          value: Number(item.count || 0),
          itemStyle: {
            color: resolveGradeColor(String(item.gradeLevel || '')),
            borderRadius: 4,
          },
        })),
      },
    ],
  }
}