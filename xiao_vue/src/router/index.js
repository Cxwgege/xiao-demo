import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { generateRoutes } from './generateRoutes'
import Layout from '@/layout/index.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/login/index.vue')
    },
    {
      path: '/',
      component: Layout,
      redirect: '/home'
    }
  ]
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  const token = userStore.token
  
  if (token) {
    if (to.path === '/login') {
      next('/')
    } else {
      // 修改判断条件，使用路由数量来判断是否已添加动态路由
      if (router.getRoutes().length === 2) {  // 只有基础路由时
        const routes = generateRoutes(userStore.menus)
        routes.forEach(route => {
          router.addRoute(route)
        })
        // 重新进入当前路由
        next({ ...to, replace: true })
      } else {
        next()
      }
    }
  } else {
    if (to.path === '/login') {
      next()
    } else {
      next('/login')
    }
  }
})

export default router 