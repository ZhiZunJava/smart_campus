export function resolveCurrentWeek(term: any): number {
  const startDate = parseDate(term?.startDate)
  if (!startDate) return 1

  const today = startOfDay(new Date())
  const endDate = parseDate(term?.endDate)
  const totalWeeks = Number(term?.totalWeeks) > 0 ? Number(term.totalWeeks) : 20

  if (today.getTime() < startDate.getTime()) return 1
  if (endDate && today.getTime() > endDate.getTime()) return totalWeeks

  const firstMondayOfTerm = getPreviousOrSameMonday(startDate)
  const diffDays = Math.floor((today.getTime() - firstMondayOfTerm.getTime()) / DAY_MS)
  const currentWeek = Math.floor(diffDays / 7) + 1
  return Math.min(Math.max(currentWeek, 1), totalWeeks)
}

function startOfDay(date: Date) {
  return new Date(date.getFullYear(), date.getMonth(), date.getDate())
}

function parseDate(value: any) {
  if (!value) return null
  const date = startOfDay(new Date(value))
  return Number.isNaN(date.getTime()) ? null : date
}

function getPreviousOrSameMonday(date: Date) {
  const day = date.getDay()
  const offset = day === 0 ? -6 : 1 - day
  return new Date(date.getFullYear(), date.getMonth(), date.getDate() + offset)
}

const DAY_MS = 24 * 60 * 60 * 1000
