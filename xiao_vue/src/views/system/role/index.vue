<template>
  <div class="role-container">
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams" class="search-form" @submit.native.prevent>
        <el-form-item>
          <el-input
              v-model="queryParams.keyword"
              placeholder="请输入角色名称或编码"
              clearable
              @keyup.enter="handleQuery"
          >
            <template #prefix>
              <el-icon>
                <Search/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <el-icon>
              <Search/>
            </el-icon>
            搜索
          </el-button>
          <el-button @click="resetQuery">
            <el-icon>
              <Refresh/>
            </el-icon>
            重置
          </el-button>
          <el-button type="primary" class="add-button" @click="handleAdd">
            <el-icon>
              <Plus/>
            </el-icon>
            新增角色
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格区域 -->
    <el-card class="table-card">
      <div class="table-wrapper">
        <el-table
            size="small"
            v-loading="loading"
            :data="roleList"
            border
            style="width: 100%"
            :header-cell-style="{ background: '#f5f7fa' }"
            max-height="calc(100vh - 380px)"
            class="show-scrollbar"
        >
          <el-table-column type="index" label="序号" width="60" align="center"/>
          <el-table-column prop="name" label="角色名称" min-width="120"/>
          <el-table-column prop="code" label="角色编码" min-width="120"/>
          <el-table-column prop="description" label="角色描述" min-width="180" show-overflow-tooltip/>
          <el-table-column prop="status" label="状态" width="70" align="center">
            <template #default="{ row }">
              <el-tag
                  :type="row.status === 1 ? 'success' : 'danger'"
              >
                {{ row.status === 1 ? '正常' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180" align="center">
            <template #default="{ row }">
              {{ formatDateTime(row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="280" align="center" fixed="right">
            <template #default="{ row }">
              <div class="operation-group">
                <el-switch
                    v-model="row.status"
                    :active-value="1"
                    :inactive-value="0"
                    style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949"
                    @change="handleStatusChange(row)"
                />
                <el-button type="primary" link @click="handleEdit(row)">
                  <el-icon>
                    <Edit/>
                  </el-icon>
                  编辑
                </el-button>
                <el-button type="danger" link @click="handleDelete(row)">
                  <el-icon>
                    <Delete/>
                  </el-icon>
                  删除
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页区域 -->
        <div class="pagination-wrapper">
          <el-pagination
              size="small"
              v-model:current-page="queryParams.current"
              v-model:page-size="queryParams.size"
              :total="total"
              :page-sizes="[10, 20, 30, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              background
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </el-card>

    <!-- 添加表单组件 -->
    <role-form ref="roleFormRef" @success="handleQuery"/>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {getRolePage, deleteRole, updateRole} from '@/api/role'
import {Search, Refresh, Plus, Edit, Delete} from '@element-plus/icons-vue'
import RoleForm from './components/RoleForm.vue'
import {formatDateTime} from '@/utils/format'

// 查询参数
const queryParams = ref({
  current: 1,
  size: 20,
  keyword: ''
})

// 角色列表数据
const roleList = ref([])
const total = ref(0)
const loading = ref(false)

// 获取角色列表数据
const getList = async () => {
  loading.value = true
  try {
    const {data} = await getRolePage(queryParams.value)
    roleList.value = data.records
    total.value = data.total
  } catch (error) {
    console.error('获取角色列表失败：', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleQuery = () => {
  queryParams.value.current = 1
  getList()
}

// 重置
const resetQuery = () => {
  queryParams.value = {
    current: 1,
    size: 20,
    keyword: ''
  }
  getList()
}

// 分页大小改变
const handleSizeChange = (val) => {
  queryParams.value.size = val
  getList()
}

// 页码改变
const handleCurrentChange = (val) => {
  queryParams.value.current = val
  getList()
}

// 删除角色
const handleDelete = (row) => {
  ElMessageBox.confirm(
      '确定要删除该角色吗？',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
  ).then(async () => {
    try {
      await deleteRole(row.id)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除角色失败：', error)
    }
  }).catch(() => {
  })
}

// 编辑角色
const handleEdit = (row) => {
  roleFormRef.value?.openDialog(row)
}

// 新增角色
const handleAdd = () => {
  roleFormRef.value?.openDialog()
}

// 添加状态变更处理函数
const handleStatusChange = async (row) => {
  try {
    await updateRole({
      id: row.id,
      status: row.status
    })
    ElMessage.success('状态更新成功')
  } catch (error) {
    row.status = row.status === 1 ? 0 : 1 // 恢复状态
    console.error('更新状态失败：', error)
  }
}

const roleFormRef = ref(null)

// 页面加载时获取数据
onMounted(() => {
  getList()
})
</script>

<style lang="scss" scoped>
.role-container {
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

.toolbar {
  display: flex;
  justify-content: flex-start;
}

.add-button {
  background: linear-gradient(135deg, #00d2ff 0%, #928dab 100%);
  border: none;
  margin-left: 8px;

  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(0, 210, 255, 0.3);
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

.pagination-wrapper {
  margin-top: 20px;
  padding: 10px 0;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  height: 32px;
  background-color: #fff;
  z-index: 1;
}

:deep(.el-pagination) {
  margin: 0;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: flex-end;

  .el-pagination__total,
  .el-pagination__sizes,
  .el-pagination__jump {
    margin-right: 12px;
  }
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
</style> 