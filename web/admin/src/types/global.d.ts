import 'axios'
import 'vue'

/** Vite 环境变量类型 */
interface ImportMetaEnv {
  readonly VITE_APP_TITLE: string
  readonly VITE_APP_BASE_API: string
  readonly VITE_APP_ENV: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}

declare module 'vue' {
  interface ComponentInternalInstance { proxy: any }

  interface ComponentCustomProperties {
    $modal: any
    $tab: any
    $download: any
    useDict: any
    download: any
    parseTime: any
    resetForm: any
    handleTree: any
    addDateRange: any
    getConfigKey: any
    selectDictLabel: any
    selectDictLabels: any
    idGlobal: any
  }
}

declare module 'axios' {
  interface AxiosInstance {
    <T = any>(config: AxiosRequestConfig): Promise<T>
  }
}
