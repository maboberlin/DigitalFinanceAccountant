import UserService from '../services/user.service';
export const user = {
  namespaced: true,
  state: {
    accounts: [],
  },
  getters: {
    accounts: state => state.accounts,
  },
  actions: {
    getAccounts({ commit }, user) {
      return UserService.getAccounts(user).then(
        accounts => {
          commit('setAccounts', accounts);
          return Promise.resolve(accounts);
        },
        error => {
          return Promise.reject(error);
        }
      );
    },

    createAccount({ commit }, user) {
      var accountContent = this.state.user.accounts[this.state.user.accounts.length - 1];
      return UserService.createAccount(user, accountContent).then(
        account => {
          commit('addAccount', account.externalIdentifier);
          this.dispatch('auth/refresh');
          return Promise.resolve(user);
        },
        error => {
          return Promise.reject(error);
        }
      );
    },

    deleteAccount({ commit }, userWithExternalIdentifier) {
      return UserService.deleteAccount(userWithExternalIdentifier.currentUser, userWithExternalIdentifier.externalIdentifier).then(
        () => {
          commit('deleteAccount', userWithExternalIdentifier.externalIdentifier);
          return Promise.resolve();
        },
        error => {
          return Promise.reject(error);
        }
      );
    },

  },

  mutations: {
    openAccount(state, externalIdentifier) {
      state.selectedAccount = externalIdentifier;
    },
    setAccounts(state, accounts) {
      state.accounts = accounts.map(item => ({...item, isEdit: false}));
      state.accounts.push({accountIdentifier:"",isEdit: true});
    },
    addAccount(state, externalIdentifier) {
      state.accounts[state.accounts.length - 1].isEdit = false
      state.accounts[state.accounts.length - 1].externalIdentifier = externalIdentifier
      state.accounts.push({accountIdentifier:"",isEdit: true});
    },
    deleteAccount(state, externalIdentifier) {
      state.accounts.splice(state.accounts.findIndex(e => e.externalIdentifier === externalIdentifier),1);
    },
  }
};