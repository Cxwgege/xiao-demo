import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: '',
    userInfo: null
  }),
  actions: {
    setToken(token) {
      this.token = token
    },
    setUserInfo(userInfo) {
      this.userInfo = userInfo
    },
    logout() {
      this.token = ''
      this.userInfo = null
    }
  },
  persist: {
    key: 'user-store',
    storage: localStorage,
    paths: ['token', 'userInfo'],
  }
}) 