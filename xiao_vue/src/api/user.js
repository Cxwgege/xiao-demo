import request from '@/utils/request'

// 获取用户分页列表
export function getUserPage(params) {
  return request({
    url: '/user/page',
    method: 'get',
    params
  })
}

// 新增用户
export function addUser(data) {
  return request({
    url: '/user',
    method: 'post',
    data
  })
}

// 修改用户
export function updateUser(data) {
  return request({
    url: '/user',
    method: 'put',
    data
  })
}

// 删除用户
export function deleteUser(id) {
  return request({
    url: `/user/${id}`,
    method: 'delete'
  })
}

// 重置密码
export function resetPassword(id) {
  return request({
    url: `/user/${id}/password/reset`,
    method: 'put'
  })
} 