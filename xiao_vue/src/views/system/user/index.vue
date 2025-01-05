<template>
  <div class="user-container">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams" class="search-form" @submit.native.prevent>
        <el-form-item>
          <el-input
              v-model="queryParams.keyword"
              placeholder="用户名/昵称/手机号"
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
            新增用户
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card class="table-card">
      <div class="table-wrapper">
        <el-table
            v-loading="loading"
            :data="userList"
            border
            style="width: 100%"
            :header-cell-style="{ background: '#f5f7fa' }"
            max-height="calc(100vh - 380px)"
            class="show-scrollbar"
        >
          <el-table-column type="index" label="序号" width="60" align="center" header-align="center"/>
          <el-table-column prop="username" label="用户名" align="center" header-align="center"/>
          <el-table-column prop="nickname" label="昵称" align="center" header-align="center"/>
          <el-table-column prop="email" label="邮箱" align="center" header-align="center"/>
          <el-table-column prop="phone" label="手机号" align="center" header-align="center"/>
          <el-table-column prop="roles" label="角色" min-width="200" align="center" header-align="center">
            <template #default="{ row }">
              <el-tag
                  v-for="role in row.roles"
                  :key="role.id"
                  class="role-tag"
                  type="info"
                  effect="plain"
              >
                {{ role.name }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="80" align="center" header-align="center">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'">
                {{ row.status === 1 ? '正常' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180" align="center" header-align="center"/>
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
                <el-button type="primary" link @click="handleUpdate(row)">
                  <el-icon>
                    <Edit/>
                  </el-icon>
                  编辑
                </el-button>
                <el-button type="primary" link @click="handleResetPassword(row)">
                  <el-icon>
                    <Edit/>
                  </el-icon>
                  重置密码
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

        <!-- 分页 -->
        <div class="pagination-wrapper">
          <el-pagination
              size="small"
              v-model:current-page="queryParams.current"
              v-model:page-size="queryParams.size"
              :total="total"
              :page-sizes="[10, 20, 30, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </el-card>

    <!-- 用户表单弹窗 -->
    <user-form ref="userFormRef" @success="handleQuery"/>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {Search, Refresh, Plus, Edit, Delete} from '@element-plus/icons-vue'
import {getUserPage, deleteUser, resetPassword, updateUser} from '@/api/user'
import UserForm from './components/UserForm.vue'

// 查询参数
const queryParams = ref({
  current: 1,
  size: 20,
  keyword: ''
})

const userList = ref([])
const total = ref(0)
const loading = ref(false)
const userFormRef = ref(null)

// 查询用户列表
const getList = async () => {
  loading.value = true
  try {
    const {data} = await getUserPage(queryParams.value)
    userList.value = data.records
    total.value = data.total
  } catch (error) {
    console.error('获取用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleQuery = () => {
  queryParams.value.current = 1
  getList()
}

// 重置查询
const resetQuery = () => {
  queryParams.value = {
    current: 1,
    size: 20,
    keyword: ''
  }
  getList()
}

// 新增用户
const handleAdd = () => {
  userFormRef.value.openDialog()
}

// 修改用户
const handleUpdate = (row) => {
  userFormRef.value.openDialog(row)
}

// 删除用户
const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该用户吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteUser(row.id)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除用户失败：', error)
    }
  }).catch(() => {
  })
}

// 重置密码
const handleResetPassword = (row) => {
  ElMessageBox.confirm('确认重置该用户的密码吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await resetPassword(row.id)
      ElMessage.success('密码重置成功')
    } catch (error) {
      console.error('密码重置失败：', error)
    }
  }).catch(() => {
  })
}
// 添加状态变更处理函数
const handleStatusChange = async (row) => {
  try {
    await updateUser({
      id: row.id,
      status: row.status
    })
    ElMessage.success('状态更新成功')
  } catch (error) {
    row.status = row.status === 1 ? 0 : 1 // 恢复状态
    console.error('更新状态失败：', error)
  }
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

onMounted(() => {
  getList()
})
</script>

<style lang="scss" scoped>
.user-container {
  padding: 20px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  gap: 20px;
  background-color: #f6f8fa;
  height: 100%;
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

.role-tag {
  margin-right: 4px;
  margin-bottom: 4px;
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