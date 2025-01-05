<template>
  <el-dialog
    :title="form.id ? '编辑用户' : '新增用户'"
    v-model="visible"
    width="500px"
    :close-on-click-modal="false"
    @closed="handleClosed"
    class="user-form-dialog"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="80px"
    >
      <el-form-item label="用户名" prop="username">
        <el-input v-model="form.username" placeholder="请输入用户名" clearable />
      </el-form-item>
      
      <el-form-item label="昵称" prop="nickname">
        <el-input v-model="form.nickname" placeholder="请输入昵称" clearable />
      </el-form-item>
      
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="form.email" placeholder="请输入邮箱" clearable />
      </el-form-item>
      
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="form.phone" placeholder="请输入手机号" clearable />
      </el-form-item>
      
      <el-form-item label="头像" prop="avatar">
        <el-input v-model="form.avatar" placeholder="请输入头像URL" clearable />
      </el-form-item>
      
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio :label="1">正常</el-radio>
          <el-radio :label="0">禁用</el-radio>
        </el-radio-group>
      </el-form-item>
      
      <el-form-item label="角色" prop="roleIds">
        <el-select
          v-model="form.roleIds"
          multiple
          placeholder="请选择角色"
          style="width: 100%"
          clearable
        >
          <el-option
            v-for="role in roleOptions"
            :key="role.id"
            :label="role.name"
            :value="role.id"
          />
        </el-select>
      </el-form-item>
    </el-form>
    
    <template #footer>
      <el-button @click="visible = false">取 消</el-button>
      <el-button 
        type="primary" 
        :loading="loading" 
        @click="handleSubmit"
        class="confirm-button"
      >
        确 定
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { addUser, updateUser } from '@/api/user'
import { getRolePage } from '@/api/role'

const emit = defineEmits(['success'])
const formRef = ref(null)
const visible = ref(false)
const loading = ref(false)
const roleOptions = ref([])

// 表单数据
const form = reactive({
  id: undefined,
  username: '',
  nickname: '',
  email: '',
  phone: '',
  avatar: '',
  status: 1,
  roleIds: []
})

// 表单校验规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  roleIds: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

// 获取角色选项
const getRoleOptions = async () => {
  try {
    const { data } = await getRolePage({ current: 1, size: 100 })
    roleOptions.value = data.records
  } catch (error) {
    console.error('获取角色列表失败：', error)
  }
}

// 打开弹窗
const openDialog = (row) => {
  getRoleOptions()
  visible.value = true
  if (row) {
    Object.assign(form, row)
    form.roleIds = row.roles.map(role => role.id)
  }
}

// 关闭弹窗
const handleClosed = () => {
  formRef.value?.resetFields()
  Object.assign(form, {
    id: undefined,
    username: '',
    nickname: '',
    email: '',
    phone: '',
    avatar: '',
    status: 1,
    roleIds: []
  })
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate()
  
  try {
    loading.value = true
    if (form.id) {
      await updateUser(form)
      ElMessage.success('修改成功')
    } else {
      await addUser(form)
      ElMessage.success('新增成功')
    }
    visible.value = false
    emit('success')
  } catch (error) {
    console.error('提交失败:', error)
  } finally {
    loading.value = false
  }
}

defineExpose({
  openDialog
})
</script>

<style scoped>
.user-form-dialog {
  :deep(.el-dialog) {
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 4px 24px rgba(0, 0, 0, 0.1);
    
    .el-dialog__header {
      margin: 0;
      padding: 20px 24px;
      border-bottom: 1px solid #f0f0f0;
      background: linear-gradient(135deg, #00d2ff 0%, #928dab 100%);
      
      .el-dialog__title {
        color: #fff;
        font-size: 16px;
        font-weight: 500;
      }
      
      .el-dialog__close {
        color: #fff;
        
        &:hover {
          color: #fff;
          transform: rotate(90deg);
          transition: all 0.3s;
        }
      }
    }
    
    .el-dialog__body {
      padding: 24px;
    }

    .el-dialog__footer {
      padding: 16px 24px;
      border-top: 1px solid #f0f0f0;
      background-color: #fafafa;
    }
  }
  :deep(.el-form-item) {
    margin-bottom: 24px;

    &:last-child {
      margin-bottom: 0;
    }
  }

  :deep(.el-input) {
    .el-input__wrapper {
      box-shadow: 0 0 0 1px #dcdfe6 inset;
      transition: all 0.3s;

      &:hover {
        box-shadow: 0 0 0 1px #00d2ff inset;
      }

      &.is-focus {
        box-shadow: 0 0 0 1px #00d2ff inset !important;
      }
    }
  }

  :deep(.el-radio) {
    margin-right: 24px;

    .el-radio__input {
      .el-radio__inner {
        &:hover {
          border-color: #00d2ff;
        }
      }

      &.is-checked {
        .el-radio__inner {
          background: #00d2ff;
          border-color: #00d2ff;
        }

        & + .el-radio__label {
          color: #00d2ff;
        }
      }
    }
  }

  :deep(.el-textarea) {
    .el-textarea__inner {
      box-shadow: 0 0 0 1px #dcdfe6 inset;
      transition: all 0.3s;

      &:hover {
        box-shadow: 0 0 0 1px #00d2ff inset;
      }

      &:focus {
        box-shadow: 0 0 0 1px #00d2ff inset !important;
      }
    }
  }
}

.confirm-button {
  background: linear-gradient(135deg, #00d2ff 0%, #928dab 100%);
  border: none;
  padding: 8px 20px;
  transition: all 0.3s;

  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(0, 210, 255, 0.3);
  }

  &:active {
    transform: translateY(0);
  }
}
</style> 