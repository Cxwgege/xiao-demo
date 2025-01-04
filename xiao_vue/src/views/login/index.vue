<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-left">
        <div class="title">
          <h2>后台管理系统</h2>
          <p>Welcome Back!</p>
        </div>
      </div>
      <div class="login-right">
        <el-form 
          ref="loginFormRef" 
          :model="loginForm" 
          :rules="loginRules" 
          class="login-form"
        >
          <h3>登录</h3>
          
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="用户名"
              :prefix-icon="User"
              clearable
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              placeholder="密码"
              type="password"
              :prefix-icon="Lock"
              clearable
              show-password
            />
          </el-form-item>

          <el-button
            :loading="loading"
            type="primary"
            class="login-button"
            @click="handleLogin(loginFormRef)"
          >
            登录
          </el-button>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { User, Lock } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { authApi } from '@/api/auth'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref()
const loading = ref(false)

// 表单数据
const loginForm = reactive({
  username: 'admin',
  password: '123456'
})

// 表单校验规则
const loginRules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ]
})

// 登录方法
const handleLogin = async (formEl) => {
  if (!formEl) return
  
  await formEl.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const { data } = await authApi.login(loginForm)
        userStore.setToken(data.token)
        userStore.setUserInfo(data)
        console.log('登录成功，用户信息：', data)
        router.push('/')
        ElMessage.success('登录成功')
      } catch (error) {
        console.error('登录失败：', error)
        ElMessage.error(error.message || '登录失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #00d2ff 0%, #928dab 100%);
  padding: 20px;
}

.login-box {
  width: 1000px;
  height: 600px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 20px;
  box-shadow: 0 15px 25px rgba(0, 0, 0, 0.1);
  display: flex;
  overflow: hidden;
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, #00d2ff 0%, #928dab 100%);
  padding: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.title {
  text-align: center;
}

.title h2 {
  font-size: 36px;
  margin-bottom: 20px;
  font-weight: 600;
}

.title p {
  font-size: 18px;
  opacity: 0.9;
}

.login-right {
  flex: 1;
  padding: 40px;
  display: flex;
  align-items: center;
}

.login-form {
  width: 100%;
  max-width: 400px;
  margin: 0 auto;
}

.login-form h3 {
  font-size: 24px;
  color: #333;
  margin-bottom: 30px;
  text-align: center;
}

:deep(.el-input__wrapper) {
  background-color: #f5f5f5;
  border: none;
  height: 50px;
  border-radius: 25px;
}

:deep(.el-input__inner) {
  height: 50px;
}

.login-button {
  width: 100%;
  height: 50px;
  border-radius: 25px;
  font-size: 16px;
  margin-top: 20px;
  background: linear-gradient(135deg, #00d2ff 0%, #928dab 100%);
  border: none;
}

.login-button:hover {
  background: linear-gradient(135deg, #00b8e6 0%, #827d99 100%);
}

@media screen and (max-width: 768px) {
  .login-box {
    width: 100%;
    height: auto;
    flex-direction: column;
  }
  
  .login-left {
    padding: 30px;
  }
  
  .login-right {
    padding: 30px;
  }
  
  .title h2 {
    font-size: 28px;
  }
}
</style> 