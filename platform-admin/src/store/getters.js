const getters = {
  sidebar: state => state.app.sidebar,
  device: state => state.app.device,
  token: state => state.user.token,
  avatar: state => state.user.avatar,
  name: state => state.user.name,
  account: state => state.user.account,
  roles: state => state.user.roles,
  user: state => state.user.user,
  permission_routes: state => state.permission.routes
}
export default getters
