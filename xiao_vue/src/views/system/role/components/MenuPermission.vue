<template>
  <el-dialog
    v-model="visible"
    title="分配菜单权限"
    width="500px"
    :close-on-click-modal="false"
    @closed="handleClosed"
  >
    <el-tree
      ref="treeRef"
      :data="menuOptions"
      show-checkbox
      node-key="id"
      :props="defaultProps"
      :default-checked-keys="checkedKeys"
      :default-expand-all="true"
      check-strictly
    />
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, defineExpose } from 'vue'
import { ElMessage } from 'element-plus'
import { getRoleMenuIds, updateRoleMenus } from '@/api/system/role.js'

const visible = ref(false)
const treeRef = ref(null)
const menuOptions = ref([])
const checkedKeys = ref([])
const currentRoleId = ref(null)

const defaultProps = {
  children: 'children',
  label: 'title'
}

// 打开对话框
const openDialog = async (roleId, menus) => {
  visible.value = true
  currentRoleId.value = roleId
  menuOptions.value = [{
    id: 0,
    title: '全部菜单',
    children: menus
  }]

  try {
    // 获取角色已分配的菜单ID
    const { data } = await getRoleMenuIds(roleId)
    checkedKeys.value = data
  } catch (error) {
    console.error('获取角色菜单失败：', error)
  }
}

// 提交更新
const handleSubmit = async () => {
  const checkedNodes = treeRef.value.getCheckedNodes()
  const menuIds = checkedNodes
    .filter(node => node.id !== 0) // 排除根节点
    .map(node => node.id)

  try {
    await updateRoleMenus({
      roleId: currentRoleId.value,
      menuIds
    })
    ElMessage.success('菜单权限更新成功')
    visible.value = false
  } catch (error) {
    console.error('更新角色菜单失败：', error)
  }
}

// 对话框关闭后的处理
const handleClosed = () => {
  checkedKeys.value = []
  currentRoleId.value = null
}

defineExpose({
  openDialog
})
</script>

<style lang="scss" scoped>
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 20px;
}

:deep(.el-tree) {
  background: none;

  .el-tree-node__content {
    height: 32px;

    &:hover {
      background-color: #f0f2f5;
    }
  }
}
</style>