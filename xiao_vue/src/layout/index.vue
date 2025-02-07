<template>
  <div class="common-layout">
    <el-container>
      <el-header height="60px">
        <div class="header-left">
          <h2>后台管理系统</h2>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="userStore.userInfo?.avatar"/>
              <span class="username">{{ userStore.userInfo?.nickname }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-container>
        <el-aside :width="isCollapse ? '64px' : '220px'">
          <div class="menu-fold" @click="isCollapse = !isCollapse">
            <el-icon class="fold-icon">
              <component :is="isCollapse ? 'Expand' : 'Fold'" />
            </el-icon>
          </div>
          <el-menu
            :default-active="route.path"
            class="el-menu-vertical"
            :router="true"
            :collapse="isCollapse"
            background-color="#f8fafc"
            text-color="#475569"
            active-text-color="#409EFF"
          >
            <!-- 动态菜单 -->
            <template v-for="menu in menuTree" :key="menu.id">
              <!-- 目录 -->
              <el-sub-menu v-if="menu.type === 0" :index="`/${menu.path}`">
                <template #title>
                  <!-- Element Plus 图标 -->
                  <el-icon v-if="isElementIcon(menu.icon)">
                    <component :is="menu.icon"/>
                  </el-icon>
                  <!-- 阿里图标库图标 (Symbol方式) -->
                  <svg v-else-if="menu.icon" class="icon" aria-hidden="true" style="width: 1.6em">
                    <use :xlink:href="menu.icon"></use>
                  </svg>
                  <span>{{ menu.title }}</span>
                </template>
                <!-- 菜单 -->
                <el-menu-item
                  v-for="child in menu.children"
                  :key="child.id"
                  :index="`${menu.path}/${child.path}`"
                >
                  <!-- Element Plus 图标 -->
                  <el-icon v-if="isElementIcon(child.icon)">
                    <component :is="child.icon"/>
                  </el-icon>
                  <!-- 阿里图标库图标 (Symbol方式) -->
                  <svg v-else-if="child.icon" class="icon" aria-hidden="true" style="width: 1.6em">
                    <use :xlink:href="child.icon"></use>
                  </svg>
                  <span>{{ child.title }}</span>
                </el-menu-item>
              </el-sub-menu>
              <!-- 一级菜单 -->
              <el-menu-item
                v-else-if="menu.type === 1"
                :index="`/${menu.path}`"
              >
                <!-- Element Plus 图标 -->
                <el-icon v-if="isElementIcon(menu.icon)">
                  <component :is="menu.icon"/>
                </el-icon>
                <!-- 阿里图标库图标 (Symbol方式) -->
                <svg v-else-if="menu.icon" class="icon" aria-hidden="true"  style="width: 1.6em">
                  <use :xlink:href="menu.icon"></use>
                </svg>
                <span>{{ menu.title }}</span>
              </el-menu-item>
            </template>
          </el-menu>
        </el-aside>
        <el-container class="right-container">
          <div class="nav-bar">
            <el-tabs
              v-model="activeTab"
              class="nav-tabs"
              @tab-remove="removeTab"
              @tab-click="handleClick"
              type="card"
            >
              <el-tab-pane
                v-for="item in visitedViews"
                :key="item.path"
                :label="item.title"
                :name="item.path"
                :closable="item.path !== '/home'"
              >
              </el-tab-pane>
            </el-tabs>
          </div>
          <el-main>
            <router-view />
          </el-main>
        </el-container>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { House, Menu } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 添加折叠状态控制
const isCollapse = ref(false)

// 将扁平的菜单数据转换为树形结构
const menuTree = computed(() => {
  const menus = userStore.menus
  console.log('原始菜单数据：', menus)

  if (!Array.isArray(menus)) {
    console.warn('菜单数据不是数组：', menus)
    return []
  }

  // 找到所有根菜单（parentId === 0），包括首页
  const rootMenus = menus.filter(menu => menu.parentId === 0)
  console.log('根菜单：', rootMenus)

  // 为每个根菜单动态添加其子菜单
  rootMenus.forEach(rootMenu => {
    rootMenu.children = menus.filter(menu => menu.parentId === rootMenu.id)
    console.log(`${rootMenu.title} 的子菜单：`, rootMenu.children)
  })

  return rootMenus
})

// 判断是否为Element Plus图标
const isElementIcon = (icon) => {
  return icon && !icon.startsWith('#icon-')
}

// 处理下拉菜单命令
const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.clearUserInfo()
    router.push('/login')
  }
}

// 添加tabs相关的响应式数据
const activeTab = ref('/home')
const visitedViews = ref([
  { path: '/home', title: '首页' }
])

// 监听路由变化，动态添加标签页
watch(() => route.path, (newPath) => {
  const currentRoute = router.currentRoute.value
  const title = currentRoute.meta?.title

  if (title && newPath !== '/home') {
    const isExist = visitedViews.value.some(view => view.path === newPath)
    if (!isExist) {
      visitedViews.value.push({
        path: newPath,
        title: title
      })
    }
    activeTab.value = newPath
  }
}, { immediate: true })

// 处理标签页点击
const handleClick = (tab) => {
  router.push(tab.props.name)
}

// 处理标签页关闭
const removeTab = (targetPath) => {
  const tabs = visitedViews.value
  let activePath = activeTab.value

  if (activePath === targetPath) {
    tabs.forEach((tab, index) => {
      if (tab.path === targetPath) {
        const nextTab = tabs[index + 1] || tabs[index - 1]
        if (nextTab) {
          activePath = nextTab.path
        }
      }
    })
  }

  activeTab.value = activePath
  visitedViews.value = tabs.filter(tab => tab.path !== targetPath)
  router.push(activePath)
}
</script>

<style scoped>
.common-layout {
  min-height: 100vh;
  background-color: #fff;
}

.el-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: linear-gradient(135deg, #00d2ff 0%, #928dab 100%);
  padding: 0 20px;
  color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-left h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 20px;
  transition: all 0.3s;
}

.user-info:hover {
  background: rgba(255, 255, 255, 0.1);
}

.username {
  margin-left: 8px;
  color: white;
  font-weight: 500;
}

.el-aside {
  background-color: #f8fafc;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
  border-right: 1px solid #e2e8f0;
  transition: width 0.3s;
}

.el-menu-vertical {
  height: calc(100vh - 84px);
  border-right: none;
}

.el-menu-vertical :deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
  margin: 4px 0;
  color: #475569;
}

.el-menu-vertical :deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, #60a5fa 0%, #3b82f6 100%);
  color: white;
  border-radius: 4px;
  margin: 4px 10px;
  width: calc(100% - 20px);
  box-sizing: unset;
}

.el-menu-vertical :deep(.el-sub-menu__title) {
  height: 50px;
  line-height: 50px;
  color: #475569;
}

.el-menu-vertical :deep(.el-menu-item:hover),
.el-menu-vertical :deep(.el-sub-menu__title:hover) {
  background-color: #e2e8f0 !important;
  color: #1e293b !important;
}

.el-main {
  padding: 0;
  height: calc(100vh - 100px); /* 减去header和tabs的高度 */
  overflow: hidden; /* 防止出现滚动条 */
  background-color: #fff;
}

:deep(.el-card) {
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
}

:deep(.el-card:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
}

/* 子菜单样式 */
.el-menu-vertical :deep(.el-sub-menu .el-menu) {
  background-color: #f1f5f9;
}

.el-menu-vertical :deep(.el-sub-menu .el-menu-item) {
  min-width: unset;
}

/* 图标颜色 */
.el-menu-vertical :deep(.el-icon) {
  color: #409EFF;
}

.el-menu-vertical :deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, #60a5fa 0%, #3b82f6 100%);
  color: white;
  border-radius: 4px;
  margin: 4px 10px;
  width: calc(100% - 20px);
}

.el-menu-vertical :deep(.el-menu-item.is-active .el-icon) {
  color: white;
}

.el-menu-vertical :deep(.el-sub-menu__title .el-icon) {
  color: #409EFF;
}

.el-menu-vertical :deep(.el-menu-item:hover .el-icon),
.el-menu-vertical :deep(.el-sub-menu__title:hover .el-icon) {
  color: #1e293b;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.fold-icon {
  font-size: 14px;
  color: #475569;
  pointer-events: none;
}

.el-menu-vertical:not(.el-menu--collapse) {
  width: 220px;
}

.tabs-container {
  padding: 6px 6px 0;
  background: white;
  border-bottom: 1px solid #e4e7ed;
}

.nav-tabs {
  :deep(.el-tabs__header) {
    margin: 0;
    border: none;
    height: 40px;
  }

  :deep(.el-tabs__nav-wrap) {
    padding: 0;
    &::after {
      display: none;
    }
  }

  :deep(.el-tabs__nav) {
    border: none;
    height: 40px;
  }

  :deep(.el-tabs__item) {
    height: 40px;
    line-height: 40px;
    font-size: 14px;
    border: none !important;
    background: #f6f8fa !important;
    margin: 0 2px;
    padding: 0 16px;
    transition: all 0.3s;
    position: relative;
    top: 0;

    .close-icon {
      pointer-events: auto !important;
    }
  }
}

.main-container {
  background-color: #f6f8fa;
}

.nav-bar {
  background-color: #fff;
  padding: 0;
  border-bottom: 1px solid #dcdfe6;
  margin: 0;
  height: 40px;
  line-height: 40px;
}

.nav-tabs {
  :deep(.el-tabs__header) {
    margin: 0;
    border: none;
    height: 40px;
  }

  :deep(.el-tabs__nav-wrap) {
    padding: 0;
    &::after {
      display: none;
    }
  }

  :deep(.el-tabs__nav) {
    border: none;
    height: 40px;
  }

  :deep(.el-tabs__item) {
    height: 40px;
    line-height: 40px;
    font-size: 14px;
    border: none !important;
    background: #f6f8fa !important;
    margin: 0 2px;
    padding: 0 16px;
    transition: all 0.3s;
    position: relative;
    top: 0;

    .close-icon {
      pointer-events: auto !important;
    }
  }
}

.el-main {
  padding: 16px;
  height: calc(100vh - 100px); /* 减去header高度(60px)和tabs高度(40px) */
  overflow-y: auto;
  background-color: #fff;
}

.right-container {
  display: flex;
  flex-direction: column;
  background-color: #fff;
}

.nav-bar {
  background-color: #fff;
  padding: 0;
  border-bottom: 1px solid #dcdfe6;
  height: 40px;
  line-height: 40px;
}

.nav-tabs {
  :deep(.el-tabs__header) {
    margin: 0;
    border: none;
    height: 40px;
  }

  :deep(.el-tabs__nav-wrap) {
    padding: 0;
    &::after {
      display: none;
    }
  }

  :deep(.el-tabs__nav) {
    border: none;
    height: 40px;
  }

  :deep(.el-tabs__item) {
    height: 40px;
    line-height: 40px;
    font-size: 14px;
    border: none !important;
    background: #f6f8fa !important;
    margin: 0 2px;
    padding: 0 16px;
    transition: all 0.3s;
    position: relative;
    top: 0;

    .close-icon {
      pointer-events: auto !important;
    }
  }
}

.el-main {
  padding: 16px;
  height: calc(100vh - 100px); /* 减去header高度(60px)和tabs高度(40px) */
  overflow-y: auto;
  background-color: #fff;
}

.menu-fold {
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  border-bottom: 1px solid #e2e8f0;
  background-color: #f8fafc;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    background-color: #edf2f7;
  }
}



/* 覆盖 el-icon 的默认样式 */
:deep(.el-icon) {
  pointer-events: auto !important;
}

:deep(.el-tabs__item) .el-icon-close {
  pointer-events: auto !important;
  cursor: pointer;
  border-radius: 50%;
  transition: all 0.3s;

  &:hover {
    background-color: #ff4d4f;
    color: #fff;
  }
}

/* 修改图标样式 */
.el-menu-vertical {
  :deep(.el-menu-item),
  :deep(.el-sub-menu__title) {
    .el-icon {
      font-size: 18px;
      vertical-align: middle;
    }

    .icon {
      width: 18px;
      height: 18px;
      vertical-align: middle;
      fill: currentColor;
      margin-right: 5px;
    }
  }
}

/* 激活状态下的图标颜色 */
.el-menu-vertical :deep(.el-menu-item.is-active) {
  .el-icon,
  .icon {
    color: white;
  }
}
</style> 