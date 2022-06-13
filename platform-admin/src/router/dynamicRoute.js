import {getMenus} from '@/api/rolePermisssion'
import Layout from '@/layout'
import AppSub from '@/layout/components/AppSub'

const _import = require('./_import')

/**
 * 递归构建路由
 * @param {Object} menus  原始数据
 * @param {Object} pid 父iID
 * @param {Object} routeTable 动态路由
 */
function buildRoute(menus, pid, routeTable) {
  for (let i in menus) {
    if (menus[i].pid === pid) {
      let node = {
        path: menus[i].path,
        meta: {
          title: menus[i].title,
          icon: menus[i].icon,
          permission: menus[i].permissions == null ? [] : menus[i].permissions.split(',')
        },
        hidden: menus[i].hidden === 1,
        component: loadComponent(pid, menus[i].component),
        children: []
      }
      buildRoute(menus, menus[i].id, node.children)
      routeTable.push(node)
    }
  }
}

function loadComponent(pid, path) {
  try {
    if (pid === null) {
      return Layout;
    }

    if (path === null || path === '') {
      return AppSub;
    }

    return _import(path)
  } catch (err) {
    console.log("component load fialed of ", path)
  }
}

/**
 * 获取动态路由
 */
export async function getDynamicRoutes() {
  let routeTable = []
  await getMenus().then(resp => {
    buildRoute(resp.data, null, routeTable)
  })
  routeTable.push({
    path: '*',
    redirect: '/404',
    hidden: true
  })
  return routeTable
}
