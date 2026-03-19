/// <reference types="vite/client" />

declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<Record<string, unknown>, Record<string, unknown>, any>
  export default component
}

declare module 'js-cookie' {
  const Cookies: any
  export default Cookies
}

declare module 'markdown-it' {
  const MarkdownIt: any
  export default MarkdownIt
}
