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
        <el-aside width="200px">
          <el-menu
            :default-active="route.path"
            class="el-menu-vertical"
            :router="true"
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
.el-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #fff;
  border-bottom: 1px solid #dcdfe6;
  padding: 0 20px;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.username {
  margin-left: 8px;
}

.el-menu-vertical {
  height: calc(100vh - 60px);
}
</style> 