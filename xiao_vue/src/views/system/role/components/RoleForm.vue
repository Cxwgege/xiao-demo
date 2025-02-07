<template>
  <el-dialog
    :title="form.id ? '编辑角色' : '新增角色'"
    v-model="visible"
    width="500px"
    :close-on-click-modal="false"
    @closed="handleClosed"
    class="role-form-dialog"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="80px"
    >
      <el-form-item label="角色名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入角色名称" clearable />
      </el-form-item>
      <el-form-item label="角色编码" prop="code">
        <el-input v-model="form.code" placeholder="请输入角色编码" clearable />
      </el-form-item>
      <el-form-item label="角色描述" prop="description">
        <el-input
          v-model="form.description"
          type="textarea"
          :rows="3"
          placeholder="请输入角色描述"
          resize="none"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio :value="1">正常</el-radio>
          <el-radio :value="0">禁用</el-radio>
        </el-radio-group>
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
import { addRole, updateRole } from '@/api/system/role.js'

const emit = defineEmits(['success'])
const formRef = ref(null)
const visible = ref(false)
const loading = ref(false)

const form = reactive({
  id: undefined,
  name: '',
  code: '',
  description: '',
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入角色编码', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// 打开弹窗
const openDialog = (row) => {
  visible.value = true
  if (row) {
    Object.assign(form, row)
  }
}

// 关闭弹窗
const handleClosed = () => {
  formRef.value?.resetFields()
  Object.assign(form, {
    id: undefined,
    name: '',
    code: '',
    description: '',
    status: 1
  })
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate()
  
  try {
    loading.value = true
    if (form.id) {
      await updateRole(form)
      ElMessage.success('修改成功')
    } else {
      await addRole(form)
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
.role-form-dialog {
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