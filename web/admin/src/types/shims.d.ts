declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<Record<string, unknown>, Record<string, unknown>, any>
  export default component
}

declare module 'element-plus'

declare module 'nprogress' {
  const nprogress: any
  export default nprogress
}

declare module 'js-cookie' {
  const Cookies: any
  export default Cookies
}

declare module 'file-saver' {
  export const saveAs: any
}

declare module 'jsencrypt' {
  const JSEncrypt: any
  export default JSEncrypt
}

declare module '@vueup/vue-quill' {
  export const QuillEditor: any
  const VueQuill: any
  export default VueQuill
}

declare module 'sortablejs' {
  const Sortable: any
  export default Sortable
}

declare module 'fuse.js' {
  const Fuse: any
  export default Fuse
}

declare module 'vuedraggable/dist/vuedraggable.common' {
  const draggable: any
  export default draggable
}

declare module 'vue-cropper' {
  export const VueCropper: any
}

declare module 'splitpanes' {
  export const Splitpanes: any
  export const Pane: any
}
