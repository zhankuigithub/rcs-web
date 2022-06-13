import { login, logout, getInfo,loginBySmsCode } from '@/api/user'
import { getToken, setToken, removeToken } from '@/utils/auth'
import { resetRouter } from '@/router'

const getDefaultState = () => {
  return {
    token: getToken(),
    name: '',
    avatar: '',
    account: '',
    user: {},
    roles: []
  }
}

const state = getDefaultState()

const mutations = {
  RESET_STATE: (state) => {
    Object.assign(state, getDefaultState())
  },
  SET_TOKEN: (state, token) => {
    state.token = token
  },
  SET_NAME: (state, name) => {
    state.name = name
  },
  SET_AVATAR: (state, avatar) => {
    state.avatar = avatar
  },
  SET_ACCOUNT: (state, account) => {
    state.account = account
  },
  SET_USER: (state, user) => {
    state.user = user
  },
  SET_ROLES: (state, roles) => {
    state.roles = roles
  }
}

const actions = {
  // user login
  login({ commit }, userInfo) {
    const { account, password, code } = userInfo
    return new Promise((resolve, reject) => {
      login({ account: account.trim(), password: password ,code:code }).then(response => {
        const { data } = response
        commit('SET_TOKEN', data.accessToken)
        setToken(data.accessToken)
        resolve()
      }).catch(error => {
        reject(error)
      })
    })
  },
  smsLogin({ commit }, userInfo) {
    const { account, code, smsCode} = userInfo
    return new Promise((resolve, reject) => {
      loginBySmsCode({ account: account.trim(), code:code, smsCode:smsCode }).then(response => {
        const { data } = response
        commit('SET_TOKEN', data.accessToken)
        setToken(data.accessToken)
        resolve()
      }).catch(error => {
        reject(error)
      })
    })
  },

  // get user info
  getInfo({ commit, state }) {
    return new Promise((resolve, reject) => {
      getInfo(state.token).then(response => {
        const { data } = response

        if (!data) {
          reject('Verification failed, please Login again.')
        }

        const { roles, userName, avatar, phone } = data

        // roles must be a non-empty array
        if (!roles || roles.length <= 0) {
          reject('getInfo: roles must be a non-null array!')
        }

        commit('SET_ROLES', roles)
        commit('SET_NAME', userName)
        commit('SET_AVATAR', avatar)
        commit('SET_ACCOUNT', phone)
        commit('SET_USER', data)
        resolve(data)
      }).catch(error => {
        reject(error)
      })
    })
  },

  // user logout
  logout({ commit, state }) {
    return new Promise((resolve, reject) => {
      logout(state.token).then(() => {
        removeToken() // must remove  token  first
        resetRouter()
        commit('RESET_STATE')
        resolve()
      }).catch(error => {
        reject(error)
      })
    })
  },

  // remove token
  resetToken({ commit }) {
    return new Promise(resolve => {
      removeToken() // must remove  token  first
      commit('RESET_STATE')
      resolve()
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
