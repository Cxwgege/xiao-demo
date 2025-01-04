import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref('')
  const userInfo = ref(null)
  const menus = ref([])

  // 设置token
  function setToken(newToken) {
    token.value = newToken
  }

  // 设置用户信息
  function setUserInfo(info) {
    userInfo.value = info.userInfo
    // 确保menus数据正确设置
    if (info.userInfo && Array.isArray(info.userInfo.menus)) {
      menus.value = info.userInfo.menus
      console.log('存储的菜单数据：', menus.value)
    }
  }

  // 清除用户信息
  function clearUserInfo() {
    token.value = ''
    userInfo.value = null
    menus.value = []
  }

  return {
    token,
    userInfo,
    menus,
    setToken,
    setUserInfo,
    clearUserInfo
  }
}, {
  persist: true
}) 