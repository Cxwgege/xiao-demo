-- 初始化用户数据 (密码为: 123456，使用BCrypt加密)
INSERT INTO `user` (`username`, `password`, `nickname`, `email`, `phone`, `avatar`, `status`) 
VALUES ('admin', '$2a$10$vg5QNHhCknAqb1yZoB.PrOYkxdHxnZ4C5HwT.PGLVUjCLVEq8vUbe', '管理员', 'admin@xiao.com', '13800138000', 'https://avatars.githubusercontent.com/u/1?v=4', 1);

-- 初始化角色数据
INSERT INTO `role` (`role_name`, `role_code`, `description`, `status`) VALUES
('超级管理员', 'SUPER_ADMIN', '超级管理员，拥有所有权限', 1),
('普通用户', 'USER', '普通用户，拥有基本权限', 1);

-- 初始化用户角色关联数据
INSERT INTO `user_role` (`user_id`, `role_id`) VALUES (1, 1);

-- 初始化菜单数据
INSERT INTO `menu` 
(`parent_id`, `name`, `path`, `component`, `redirect`, `icon`, `title`, `is_link`, `is_hide`, `is_keep_alive`, `is_affix`, `type`, `permission`, `sort`, `status`) 
VALUES 
-- 系统管理
(0, 'System', '/system', 'Layout', NULL, 'setting', '系统管理', 0, 0, 1, 0, 0, NULL, 100, 1),

-- 用户管理
(1, 'User', 'user', 'system/user/index', NULL, 'user', '用户管理', 0, 0, 1, 0, 1, 'system:user:list', 1, 1),
(2, 'UserAdd', '', '', NULL, 'plus', '用户新增', 0, 0, 1, 0, 2, 'system:user:add', 1, 1),
(2, 'UserEdit', '', '', NULL, 'edit', '用户编辑', 0, 0, 1, 0, 2, 'system:user:edit', 2, 1),
(2, 'UserDelete', '', '', NULL, 'delete', '用户删除', 0, 0, 1, 0, 2, 'system:user:delete', 3, 1),

-- 角色管理
(1, 'Role', 'role', 'system/role/index', NULL, 'role', '角色管理', 0, 0, 1, 0, 1, 'system:role:list', 2, 1),
(6, 'RoleAdd', '', '', NULL, 'plus', '角色新增', 0, 0, 1, 0, 2, 'system:role:add', 1, 1),
(6, 'RoleEdit', '', '', NULL, 'edit', '角色编辑', 0, 0, 1, 0, 2, 'system:role:edit', 2, 1),
(6, 'RoleDelete', '', '', NULL, 'delete', '角色删除', 0, 0, 1, 0, 2, 'system:role:delete', 3, 1),

-- 菜单管理
(1, 'Menu', 'menu', 'system/menu/index', NULL, 'menu', '菜单管理', 0, 0, 1, 0, 1, 'system:menu:list', 3, 1),
(10, 'MenuAdd', '', '', NULL, 'plus', '菜单新增', 0, 0, 1, 0, 2, 'system:menu:add', 1, 1),
(10, 'MenuEdit', '', '', NULL, 'edit', '菜单编辑', 0, 0, 1, 0, 2, 'system:menu:edit', 2, 1),
(10, 'MenuDelete', '', '', NULL, 'delete', '菜单删除', 0, 0, 1, 0, 2, 'system:menu:delete', 3, 1);

-- 初始化角色菜单关联数据 (超级管理员拥有所有菜单权限)
INSERT INTO `role_menu` (`role_id`, `menu_id`) 
SELECT 1, id FROM `menu`;

-- 普通用户只有查看权限
INSERT INTO `role_menu` (`role_id`, `menu_id`)
SELECT 2, id FROM `menu` WHERE `type` != 2; 