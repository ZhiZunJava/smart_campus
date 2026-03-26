import router from './router'
import { ElMessage } from 'element-plus'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getToken } from '@/utils/auth'
import { isHttp, isPathMatch } from '@/utils/validate'
import { isRelogin } from '@/utils/request'
import useUserStore from '@/store/modules/user'
import useSettingsStore from '@/store/modules/settings'
import usePermissionStore from '@/store/modules/permission'

NProgress.configure({ showSpinner: false })

const whiteList = ['/login', '/register']

const isWhiteList = (path: string): boolean => {
  return whiteList.some((pattern: string) => isPathMatch(pattern, path))
}

router.beforeEach(async (to) => {
  NProgress.start()
  if (getToken()) {
    to.meta.title && useSettingsStore().setTitle(to.meta.title as string)
    /* has token*/
    if (to.path === '/login') {
      NProgress.done()
      return { path: '/' }
    } else if (isWhiteList(to.path)) {
      return true
    } else {
      if (useUserStore().roles.length === 0) {
        isRelogin.show = true
        try {
          // 判断当前用户是否已拉取完user_info信息
          await useUserStore().getInfo()
          isRelogin.show = false
          const accessRoutes = await usePermissionStore().generateRoutes()
          // 根据roles权限生成可访问的路由表
          accessRoutes.forEach((route: any) => {
            if (!isHttp(route.path)) {
              router.addRoute(route) // 动态添加可访问路由表
            }
          })
          return { ...to, replace: true } // hack方法 确保addRoutes已完成
        } catch (err: any) {
          await useUserStore().logOut()
          ElMessage.error(err as string)
          NProgress.done()
          return { path: '/' }
        }
      } else {
        return true
      }
    }
  } else {
    // 没有token
    if (isWhiteList(to.path)) {
      // 在免登录白名单，直接进入
      return true
    } else {
      NProgress.done()
      return `/login?redirect=${to.fullPath}` // 否则全部重定向到登录页
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})
