import { defineConfig, loadEnv } from 'vite'
import { resolve } from 'node:path'
import { fileURLToPath } from 'node:url'
import vue from '@vitejs/plugin-vue'
import tailwindcss from '@tailwindcss/vite'

const baseUrl = 'http://localhost:8080'
const rootDir = fileURLToPath(new URL('.', import.meta.url))

export default defineConfig(({ mode, command }) => {
  const env = loadEnv(mode, process.cwd())
  const { VITE_APP_ENV } = env
  return {
    base: VITE_APP_ENV === 'production' ? '/' : '/',
    plugins: [vue(), tailwindcss()],
    resolve: {
      alias: {
        '@': resolve(rootDir, './src')
      },
      extensions: ['.mjs', '.js', '.ts', '.jsx', '.tsx', '.json', '.vue']
    },
    build: {
      cssMinify: 'esbuild',
      sourcemap: command === 'build' ? false : 'inline',
      outDir: 'dist',
      assetsDir: 'static',
      chunkSizeWarningLimit: 1200,
      rolldownOptions: {
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
      }
    },
    server: {
      port: 3000,
      host: true,
      open: true,
      proxy: {
        '/dev-api': {
          target: baseUrl,
          changeOrigin: true,
          rewrite: (p) => p.replace(/^\/dev-api/, '')
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
