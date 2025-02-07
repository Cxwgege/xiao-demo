import request from '@/utils/request.js'

// 获取菜单树
export function getMenuTree() {
  return request({
    url: '/menu/tree',
    method: 'get'
  })
}

// 新增菜单
export function addMenu(data) {
  return request({
    url: '/menu',
    method: 'post',
    data
  })
}

// 修改菜单
export function updateMenu(data) {
  return request({
    url: '/menu',
    method: 'put',
    data
  })
}

// 删除菜单
export function deleteMenu(id) {
  return request({
    url: `/menu/${id}`,
    method: 'delete'
  })
} 