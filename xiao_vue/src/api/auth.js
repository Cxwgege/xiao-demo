import request from '@/utils/request'

/**
 * 认证相关接口
 */
export const authApi = {
  /**
   * 登录
   * @param {Object} data - 登录参数
   * @param {string} data.username - 用户名
   * @param {string} data.password - 密码
   * @returns {Promise} 返回登录结果
   */
  login: (data) => {
    return request.post('/auth/login', data)
  },

  /**
   * 注销
   * @returns {Promise}
   */
  logout: () => {
    return request.post('/auth/logout')
  }
} 