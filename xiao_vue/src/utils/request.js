import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const service = axios.create({
  baseURL: '/api',
  timeout: 5000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers['Xiao-Token'] = userStore.token
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || '系统错误')
      return Promise.reject(new Error(res.message || '系统错误'))
    }
    return res
  },
  error => {
    ElMessage.error(error.response?.data?.message || error.message || '系统错误')
    return Promise.reject(error)
  }
)

export default service 