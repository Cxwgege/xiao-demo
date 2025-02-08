import { useUserStore } from '@/stores/user'

export default {
  mounted(el, binding) {
    const { value } = binding
    const userStore = useUserStore()
    const permissions = userStore.permissions

    if (value && value.length > 0) {
      const hasPermission = permissions.some(permission => value === permission)
      if (!hasPermission) {
        el.parentNode?.removeChild(el)
      }
    }
  }
} 