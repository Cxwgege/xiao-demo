import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref('')
  const userInfo = ref(null)
  const menus = ref([])
  const permissions = ref([])

  // 设置token
  function setToken(newToken) {
    token.value = newToken
  }

  // 设置用户信息
  function setUserInfo(info) {
    userInfo.value = info.userInfo
    if (info.userInfo) {
      if (Array.isArray(info.userInfo.menus)) {
        menus.value = info.userInfo.menus
      }
      if (Array.isArray(info.userInfo.perms)) {
        permissions.value = info.userInfo.perms
      }
    }
  }

  // 清除用户信息
  function clearUserInfo() {
    token.value = ''
    userInfo.value = null
    menus.value = []
    permissions.value = []
  }

  return {
    token,
    userInfo,
    menus,
    permissions,
    setToken,
    setUserInfo,
    clearUserInfo
  }
}, {
  persist: true
}) 