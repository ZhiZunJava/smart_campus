import { defineConfig, loadEnv } from 'vite'
import { resolve } from 'node:path'
import { fileURLToPath } from 'node:url'
import createVitePlugins from './vite/plugins'

const baseUrl = 'http://localhost:8080' // 后端接口
const rootDir = fileURLToPath(new URL('.', import.meta.url))

// https://vitejs.dev/config/
export default defineConfig(({ mode, command }) => {
  const env = loadEnv(mode, process.cwd())
  const { VITE_APP_ENV } = env
  return {
    // 部署生产环境和开发环境下的URL。
    // 默认情况下，vite 会假设你的应用是被部署在一个域名的根路径上
    // 例如 https://www.ruoyi.vip/。如果应用被部署在一个子路径上，你就需要用这个选项指定这个子路径。例如，如果你的应用被部署在 https://www.ruoyi.vip/admin/，则设置 baseUrl 为 /admin/。
    base: VITE_APP_ENV === 'production' ? '/' : '/',
    plugins: createVitePlugins(env, command === 'build'),
    resolve: {
      // https://cn.vitejs.dev/config/#resolve-alias
      alias: {
        // 设置路径
        '~': resolve(rootDir, './'),
        // 设置别名
        '@': resolve(rootDir, './src')
      },
      // https://cn.vitejs.dev/config/#resolve-extensions
      extensions: ['.mjs', '.js', '.ts', '.jsx', '.tsx', '.json', '.vue']
    },
    // 打包配置
    build: {
      // https://vite.dev/config/build-options.html
      cssMinify: 'esbuild',
      chunkSizeWarningLimit: 1200,
      rolldownOptions: {
        checks: {
          pluginTimings: false
        },
        output: {
          chunkFileNames: 'static/js/[name]-[hash].js',
          entryFileNames: 'static/js/[name]-[hash].js',
          assetFileNames: (assetInfo: { name?: string }) => {
            // 图片资源统一放入 img 文件夹
            if (/\.(png|jpe?g|gif|svg|webp|ico|bmp|tiff?)$/i.test(assetInfo.name ?? '')) {
              return 'static/img/[name]-[hash][extname]'
            }
            // CSS 文件放入 css 文件夹
            if (/\.css$/i.test(assetInfo.name ?? '')) {
              return 'static/css/[name]-[hash][extname]'
            }
            // 字体等其他资源按扩展名分类
            return 'static/[ext]/[name]-[hash][extname]'
          }
        }
      },
      sourcemap: command === 'build' ? false : 'inline',
      outDir: 'dist',
      assetsDir: 'static'
    },
    // vite 相关配置
    server: {
      port: 80,
      host: true,
      open: true,
      proxy: {
        // https://cn.vitejs.dev/config/#server-proxy
        '/dev-api': {
          target: baseUrl,
          changeOrigin: true,
          rewrite: (p) => p.replace(/^\/dev-api/, '')
        },
         // springdoc proxy
         '^/v3/api-docs/(.*)': {
          target: baseUrl,
          changeOrigin: true,
        }
      }
    },
    css: {
      postcss: {
        plugins: [
          {
            postcssPlugin: 'internal:charset-removal',
            AtRule: {
              charset: (atRule: any) => {
                if (atRule.name === 'charset') {
                  atRule.remove()
                }
              }
            }
          }
        ]
      }
    }
  }
})
