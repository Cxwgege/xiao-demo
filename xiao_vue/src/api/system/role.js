import request from '@/utils/request.js'

// 获取角色分页列表
export function getRolePage(params) {
  return request({
    url: '/role/page',
    method: 'get',
    params
  })
}

// 新增角色
export function addRole(data) {
  return request({
    url: '/role',
    method: 'post',
    data
  })
}

// 修改角色
export function updateRole(data) {
  return request({
    url: '/role',
    method: 'put',
    data
  })
}

// 删除角色
export function deleteRole(id) {
  return request({
    url: `/role/${id}`,
    method: 'delete'
  })
}

// 获取角色的菜单ID列表
export function getRoleMenuIds(roleId) {
  return request({
    url: `/role/menus/${roleId}`,
    method: 'get'
  })
}

// 更新角色的菜单权限
export function updateRoleMenus(data) {
  return request({
    url: '/role/menus',
    method: 'put',
    data
  })
} 