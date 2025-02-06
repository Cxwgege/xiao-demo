<template>
  <div class="menu-container">
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams" class="search-form" @submit.native.prevent>
        <el-form-item>
          <x-button type="success" @click="handleAdd">
          <el-icon>
              <Plus/>
            </el-icon>-->
            新增菜单
          </x-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格区域 -->
    <el-card class="table-card">
      <div class="table-wrapper">
        <el-table
            size="small"
            v-loading="loading"
            :data="menuList"
            row-key="id"
            border
            style="width: 100%"
            :header-cell-style="{ background: '#f5f7fa' }"
            :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        >
          <el-table-column prop="title" label="菜单名称" min-width="180" show-overflow-tooltip/>
          <el-table-column prop="name" label="路由名称" min-width="120" show-overflow-tooltip/>
          <el-table-column prop="path" label="路由路径" min-width="120" show-overflow-tooltip/>
          <el-table-column prop="component" label="组件路径" min-width="150" show-overflow-tooltip/>
          <el-table-column prop="permission" label="权限标识" min-width="150" show-overflow-tooltip/>
          <el-table-column label="图标" align="center" width="100">
            <template #default="{ row }">
              <!-- Element Plus 图标 -->
              <el-icon v-if="isElementIcon(row.icon)" :class="row.icon">
                <component :is="row.icon"/>
              </el-icon>
              <!-- 阿里图标库图标 (Symbol方式) -->
              <svg v-else-if="row.icon" class="icon" aria-hidden="true">
                <use :xlink:href="row.icon"></use>
              </svg>
            </template>
          </el-table-column>
          <el-table-column prop="type" label="类型" width="80" align="center">
            <template #default="{ row }">
              <el-tag v-if="row.type === 0" type="info">目录</el-tag>
              <el-tag v-else-if="row.type === 1" type="success">菜单</el-tag>
              <el-tag v-else type="warning">按钮</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="sort" label="排序" width="80" align="center"/>
          <el-table-column prop="status" label="状态" width="80" align="center">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'" class="status-tag">
                {{ row.status === 1 ? '正常' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right" align="center">
            <template #default="{ row }">
              <div class="operation-group">
                <el-switch
                    v-model="row.status"
                    :active-value="1"
                    :inactive-value="0"
                    style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949"
                    @change="handleStatusChange(row)"
                />
                <el-button
                    link
                    type="primary"
                    @click="handleEdit(row)">
                  <el-icon>
                    <Edit/>
                  </el-icon>
                  编辑
                </el-button>
                <el-button
                    link
                    type="danger"
                    @click="handleDelete(row)">
                  <el-icon>
                    <Delete/>
                  </el-icon>
                  删除
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>

    <!-- 添加/编辑表单组件 -->
    <menu-form ref="menuFormRef" @success="getList"/>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {getMenuTree, deleteMenu, updateMenu} from '@/api/menu'
import {Search, Plus, Refresh, Edit, Delete, Check, Warning, Menu} from '@element-plus/icons-vue'
import MenuForm from './components/MenuForm.vue'
import XButton from "@/components/XButton/index.vue";

// 查询参数
const queryParams = ref({
  keyword: ''
})

// 菜单列表数据
const menuList = ref([])
const loading = ref(false)
const menuFormRef = ref(null)

// 获取菜单列表数据
const getList = async () => {
  loading.value = true
  try {
    const {data} = await getMenuTree()
    menuList.value = data
  } catch (error) {
    console.error('获取菜单列表失败：', error)
  } finally {
    loading.value = false
  }
}

// 新增菜单
const handleAdd = () => {
  const options = [{id: 0, title: '顶级菜单', children: menuList.value}]
  menuFormRef.value?.openDialog(null, options)
}

// 编辑菜单
const handleEdit = (row) => {
  const options = [{id: 0, title: '顶级菜单', children: menuList.value}]
  menuFormRef.value?.openDialog(row, options)
}

// 删除菜单
const handleDelete = (row) => {
  ElMessageBox.confirm(
      '确定要删除该菜单吗？',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
  ).then(async () => {
    try {
      await deleteMenu(row.id)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除菜单失败：', error)
    }
  })
}

// 状态变更
const handleStatusChange = async (row) => {
  try {
    await updateMenu({
      id: row.id,
      status: row.status
    })
    ElMessage.success('状态更新成功')
  } catch (error) {
    row.status = row.status === 1 ? 0 : 1 // 恢复状态
    console.error('更新状态失败：', error)
  }
}

// 判断是否为 Element Plus 图标
const isElementIcon = (icon) => {
  return icon && !icon.startsWith('#icon-')
}

// 页面加载时获取数据
onMounted(() => {
  getList()
})
</script>

<style lang="scss" scoped>
.menu-container {
  height: 100%;
  padding: 20px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  gap: 20px;
  background-color: #f6f8fa;
}

.search-card {
  margin-bottom: 0;

  :deep(.el-card__body) {
    padding: 16px 20px;
  }
}

.search-form {
  margin-bottom: -18px;

  :deep(.el-input__wrapper) {
    background-color: #fff;
  }

  :deep(.el-input__suffix) {
    .el-icon {
      pointer-events: auto !important;
    }
  }

  :deep(.el-input__prefix) {
    .el-icon {
      pointer-events: auto !important;
    }
  }

  :deep(.el-button) {
    display: flex;
    align-items: center;
    gap: 5px;
    padding: 8px 15px;

    .el-icon {
      margin-right: 3px;
      pointer-events: auto !important;
    }
  }
}

.table-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;

  :deep(.el-card__body) {
    height: 100%;
    padding: 20px;
    display: flex;
    flex-direction: column;
  }
}

.table-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

:deep(.el-table) {
  flex: 1;
  overflow: hidden;
}

.status-tag {
  padding: 4px 12px;
  border-radius: 12px;
}

/* 表格hover效果 */
:deep(.el-table__row) {
  transition: all 0.3s;

  &:hover {
    background-color: #f8fafd !important;
    transform: translateY(-1px);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  }
}

/* 按钮hover效果 */
:deep(.el-button--primary.is-link:hover) {
  color: #00d2ff;
}

:deep(.el-button--danger.is-link:hover) {
  color: #ff4d4f;
}

/* 修改表格操作列按钮样式 */
:deep(.el-table) {
  .el-button.is-link {
    height: 28px;
    padding: 0 8px;
    margin: 0;
  }
}

/* 操作栏样式 */
.operation-group {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

/* 修改图标样式 */
:deep(.el-table) {
  .el-icon {
    font-size: 18px;
    vertical-align: middle;
  }
  
  .icon {
    width: 18px;
    height: 18px;
    vertical-align: middle;
    fill: currentColor;
  }
}
</style>