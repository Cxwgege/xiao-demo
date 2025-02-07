<template>
  <el-dialog
      :title="form.id ? '编辑菜单' : '新增菜单'"
      v-model="visible"
      width="600px"
      :close-on-click-modal="false"
      @closed="handleClosed"
      class="menu-form-dialog"
  >
    <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
    >
      <el-form-item label="上级菜单">
        <el-tree-select
            v-model="form.parentId"
            :data="menuOptions"
            :props="{ label: 'title', value: 'id' }"
            placeholder="请选择上级菜单"
            check-strictly
            default-expand-all
            clearable
        />
      </el-form-item>
      <el-form-item label="菜单类型" prop="type">
        <el-radio-group v-model="form.type">
          <el-radio :value="0">目录</el-radio>
          <el-radio :value="1">菜单</el-radio>
          <el-radio :value="2">按钮</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="菜单名称" prop="title">
        <el-input v-model="form.title" placeholder="请输入菜单名称"/>
      </el-form-item>
      <el-form-item label="路由名称" prop="name" v-if="form.type !== 2">
        <el-input v-model="form.name" placeholder="请输入路由名称"/>
      </el-form-item>
      <el-form-item label="路由路径" prop="path" v-if="form.type !== 2">
        <el-input v-model="form.path" placeholder="请输入路由路径"/>
      </el-form-item>
      <el-form-item label="组件路径" prop="component" v-if="form.type === 1">
        <el-input v-model="form.component" placeholder="请输入组件路径"/>
      </el-form-item>
      <el-form-item label="权限标识" prop="permission" v-if="form.type !== 0">
        <el-input v-model="form.permission" placeholder="请输入权限标识"/>
      </el-form-item>
      <el-form-item label="菜单图标" prop="icon" v-if="form.type !== 2">
        <el-input v-model="form.icon" placeholder="请输入菜单图标">
          <template #prefix>
            <el-icon v-if="isElementIcon" :class="form.icon">
              <component :is="form.icon"/>
            </el-icon>
            <svg v-else-if="form.icon" class="icon" aria-hidden="true">
              <use :xlink:href="form.icon"></use>
            </svg>
            <el-icon v-else><Menu /></el-icon>
          </template>
        </el-input>
        <div class="icon-tip" style="font-size: 12px; color: #909399; margin-top: 5px;">
          提示: 支持Element Plus图标(例如: Edit)或阿里图标(例如: #icon-xxx)
        </div>
      </el-form-item>
      <el-form-item label="排序" prop="sort">
        <el-input-number v-model="form.sort" :min="0" :max="999"/>
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
      <el-button type="primary" :loading="loading" @click="handleSubmit">确 定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import {ref, reactive, computed} from 'vue'
import {ElMessage} from 'element-plus'
import {addMenu, updateMenu} from '@/api/system/menu.js'
import { Menu } from '@element-plus/icons-vue'

const emit = defineEmits(['success'])
const formRef = ref(null)
const visible = ref(false)
const loading = ref(false)
const menuOptions = ref([])

const form = reactive({
  id: undefined,
  parentId: 0,
  name: '',
  path: '',
  component: '',
  redirect: '',
  icon: '',
  title: '',
  type: 0,
  permission: '',
  sort: 0,
  status: 1
})

// 判断是否为Element Plus图标
const isElementIcon = computed(() => {
  return form.icon && !form.icon.startsWith('#icon-')
})

const rules = {
  title: [{required: true, message: '请输入菜单名称', trigger: 'blur'}],
  type: [{required: true, message: '请选择菜单类型', trigger: 'change'}],
  name: [{required: true, message: '请输入路由名称', trigger: 'blur'}],
  path: [{required: true, message: '请输入路由路径', trigger: 'blur'}],
  component: [{required: true, message: '请输入组件路径', trigger: 'blur'}],
  icon: [
    { 
      validator: (rule, value, callback) => {
        if (!value) {
          callback()
        } else if (value.startsWith('#icon-')) {
          callback()
        } else if (!value.match(/^[A-Z][a-zA-Z]*$/)) {
          callback(new Error('Element图标格式应为: 首字母大写的图标名'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 打开弹窗
const openDialog = (row, options) => {
  visible.value = true
  menuOptions.value = options
  if (row) {
    Object.assign(form, row)
  }
}

// 关闭弹窗
const handleClosed = () => {
  formRef.value?.resetFields()
  Object.assign(form, {
    id: undefined,
    parentId: 0,
    name: '',
    path: '',
    component: '',
    redirect: '',
    icon: '',
    title: '',
    type: 0,
    permission: '',
    sort: 0,
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
      await updateMenu(form)
      ElMessage.success('修改成功')
    } else {
      await addMenu(form)
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

<style lang="scss" scoped>
.menu-form-dialog {
  :deep(.el-dialog__body) {
    padding: 20px;
  }
  
  .icon {
    width: 18px;
    height: 18px;
    vertical-align: middle;
    fill: currentColor;
  }
}
</style> 