<template>
  <div class="qa-markdown-renderer">
    <VkMarkdown
      :source="renderSource"
      :containers="markdownContainers"
      :fences="markdownFences"
      :tags="markdownTags"
      :markdown-it-options="markdownItOptions"
      :markdown-it-setup="markdownItSetup"
    >
      <VkRendererTemplate v-slot="{ raw, Renderer }" type="tag:table">
        <div class="qa-table-wrap">
          <table v-bind="tokenAttrsToObject(raw.open)">
            <component :is="Renderer" :source="raw.children" />
          </table>
        </div>
      </VkRendererTemplate>

      <VkRendererTemplate v-slot="{ raw, Renderer }" type="tag:a">
        <a v-bind="buildLinkAttrs(raw.open)">
          <component :is="Renderer" :source="raw.children" />
        </a>
      </VkRendererTemplate>

      <VkRendererTemplate v-slot="{ raw }" type="fence">
        <QaEchartsBlock
          v-if="isEchartsFence(raw.info)"
          :source="raw.content"
        />
        <pre v-else class="hljs"><code :class="getFenceClass(raw.info)" v-html="renderHighlightedCode(raw.content, raw.info)"></code></pre>
      </VkRendererTemplate>
    </VkMarkdown>
  </div>
</template>

<script setup lang="ts">
import 'highlight.js/styles/github-dark.css'
import '@vunk/markdown/index.css'

import { computed } from 'vue'
import hljs from 'highlight.js'
import { VkMarkdown } from '@vunk/markdown'
import { VkRendererTemplate } from '@vunk/markdown/components/strategy-renderer'
import { normalizeQaMarkdownSource } from '@/utils/qaMarkdown'
import QaEchartsBlock from '@/components/qa/QaEchartsBlock.vue'

const props = withDefaults(defineProps<{
  source?: string
}>(), {
  source: '',
})

const markdownContainers: string[] = []
const markdownFences: string[] = []
const markdownTags = ['table', 'a']
const markdownItOptions = {
  breaks: true,
  linkify: true,
  html: false,
}
const markdownItSetup = (md: any) => {
  md.enable?.('table', true)
}

const normalizedSource = computed(() => normalizeQaMarkdownSource(props.source))
const renderSource = computed(() => normalizeEchartsContainerBlocks(normalizedSource.value))

function tokenAttrsToObject(token?: { attrs?: Array<[string, string]> }) {
  return Object.fromEntries(token?.attrs || [])
}

function buildLinkAttrs(token?: { attrs?: Array<[string, string]> }) {
  const attrs = tokenAttrsToObject(token)
  const href = String(attrs.href || '').trim()
  if (!href || href.startsWith('#')) {
    return attrs
  }

  return {
    ...attrs,
    target: attrs.target || '_blank',
    rel: attrs.rel || 'noopener noreferrer nofollow',
  }
}

function getFenceLanguage(info?: string) {
  return String(info || '').trim().split(/\s+/)[0] || ''
}

function isEchartsFence(info?: string) {
  return getFenceLanguage(info) === 'qa-echarts'
}

function getFenceClass(info?: string) {
  const language = getFenceLanguage(info)
  return language ? `language-${language}` : undefined
}

function renderHighlightedCode(content?: string, info?: string) {
  const code = String(content || '')
  const language = getFenceLanguage(info)

  try {
    if (language && hljs.getLanguage(language)) {
      return hljs.highlight(code, { language }).value
    }

    return hljs.highlightAuto(code).value
  } catch {
    return hljs.highlightAuto(code).value
  }
}

function normalizeEchartsContainerBlocks(content: string) {
  let normalized = content.replace(
    /:::\s*echarts\s*\n+```(?:json|yaml)?\s*\n([\s\S]*?)\n```(?:\s*\n)?(?:::|\.{3,})?/g,
    (_, optionSource: string) => toEchartsFence(optionSource),
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

  return normalized
}

function looksLikeCompleteChartOption(content: string) {
  const source = content.trim()
  if (!source) {
    return false
  }
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

function extractFirstJsonObject(content: string) {
  const source = String(content || '')
  const start = source.indexOf('{')
  if (start < 0) {
    return ''
  }

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

    if (char === '{') {
      depth += 1
    } else if (char === '}') {
      depth -= 1
      if (depth === 0) {
        return source.slice(start, index + 1).trim()
      }
    }
  }

  return ''
}

function toEchartsFence(optionSource: string) {
  const normalizedOptionSource = String(optionSource || '').trim()
  if (!looksLikeCompleteChartOption(normalizedOptionSource)) {
    return '\n> 图表生成中...\n'
  }
  return `\`\`\`qa-echarts\n${normalizedOptionSource}\n\`\`\``
}
</script>

<style scoped>
.qa-markdown-renderer {
  min-width: 0;
  width: 100%;
}
</style>
