import Layout from '@/layout/index.vue'

// 创建组件映射表
const componentMap = {
  'system/user/index': () => import('@/views/system/user/index.vue'),
  'system/role/index': () => import('@/views/system/role/index.vue'),
  'system/menu/index': () => import('@/views/system/menu/index.vue'),
  'home/index': () => import('@/views/home/index.vue'),
  'house/index': () => import('@/views/house/index.vue')
}

export function generateRoutes(menus) {
  const routes = []
  
  // 处理菜单数据生成路由
  menus.forEach(menu => {
    if (menu.type === 0) { // 目录
      const route = {
        path: `/${menu.path}`,
        name: menu.name,
        component: Layout,
        redirect: `/${menu.path}/${menu.children?.[0]?.path || ''}`,
        children: []
      }
      
      // 添加子路由
      menus.forEach(subMenu => {
        if (subMenu.parentId === menu.id && subMenu.type === 1) {
          const component = componentMap[subMenu.component]
          if (!component) {
            console.warn(`未找到组件: ${subMenu.component}`)
            return
          }
          
          route.children.push({
            path: subMenu.path,
            name: subMenu.name,
            component: component,
            meta: {
              title: subMenu.title,
              icon: subMenu.icon
            }
          })
        }
      })
      
      routes.push(route)
    } else if (menu.type === 1 && menu.parentId === 0) { // 一级菜单
      const component = componentMap[menu.component]
      if (!component) {
        console.warn(`未找到组件: ${menu.component}`)
        return
      }
      
      routes.push({
        path: `/${menu.path}`,
        name: menu.name,
        component: Layout,
        children: [
          {
            path: '',
            name: `${menu.name}Index`,
            component: component,
            meta: {
              title: menu.title,
              icon: menu.icon
            }
          }
        ]
      })
    }
  })

  return routes
} 