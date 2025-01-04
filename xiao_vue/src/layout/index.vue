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
        <el-aside width="220px">
          <el-menu
            :default-active="route.path"
            class="el-menu-vertical"
            :router="true"
            background-color="#f8fafc"
            text-color="#475569"
            active-text-color="#409EFF"
          >
            <!-- 动态菜单 -->
            <template v-for="menu in menuTree" :key="menu.id">
              <!-- 目录 -->
              <el-sub-menu v-if="menu.type === 0" :index="`/${menu.path}`">
                <template #title>
                  <el-icon><component :is="menu.icon" /></el-icon>
                  <span>{{ menu.title }}</span>
                </template>
                <!-- 菜单 -->
                <el-menu-item 
                  v-for="child in menu.children"
                  :key="child.id"
                  :index="`${menu.path}/${child.path}`"
                >
                  <el-icon><component :is="child.icon" /></el-icon>
                  <span>{{ child.title }}</span>
                </el-menu-item>
              </el-sub-menu>
              <!-- 一级菜单 -->
              <el-menu-item 
                v-else-if="menu.type === 1" 
                :index="`/${menu.path}`"
              >
                <el-icon><component :is="menu.icon" /></el-icon>
                <span>{{ menu.title }}</span>
              </el-menu-item>
            </template>
          </el-menu>
        </el-aside>
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { House } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

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

// 处理下拉菜单命令
const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.clearUserInfo()
    router.push('/login')
  }
}
</script>

<style scoped>
.common-layout {
  min-height: 100vh;
  background-color: #f0f2f5;
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
}

.el-menu-vertical {
  height: calc(100vh - 60px);
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
  padding: 20px;
  background-color: #f0f2f5;
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
</style> 