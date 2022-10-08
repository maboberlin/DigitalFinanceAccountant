import axios from "axios";
import authHeader from '../services/auth-header';

export const positions = {
  state: {
    total: {},
    positions: [],
    selectedCurrency: 'EUR',
    selectedAccount: null
  },
  getters: {
    total: state => state.total,
    positions: state => state.positions,
    selectedCurrency: state => state.selectedCurrency,
    selectedAccount: state => state.selectedAccount,
  },
  mutations: {
    SET_CURRENT_ACCOUNT (state, currentAccount) {
        state.selectedAccount = currentAccount;
    },
    SET_TOTAL_CURRENCY (state, currency) {
        state.selectedCurrency = currency;
    },
    SET_TOTAL (state, total) {
        state.total = total;
    },
    SET_POSITIONS (state, positions) {
        state.positions = positions.map(item => ({...item, isEdit: false}));
        state.positions.push({identifier:"",name:"",amount:"",isEdit: true});
    },
    ADD_POSITION (state, externalIdentifier) {
        state.positions[state.positions.length - 1].isEdit = false
        state.positions[state.positions.length - 1].externalIdentifier = externalIdentifier
        state.positions.push({identifier:"",name:"",amount:"",isEdit: true});
    },
    DELETE_POSITION (state, externalIdentifier) {
        state.positions.splice(state.positions.findIndex(e => e.externalIdentifier === externalIdentifier),1);
    },
  },
  actions: {
    setCurrentAccount({ commit }, currentAccount) {
      commit('SET_CURRENT_ACCOUNT', currentAccount);
    },

    setTotalCurrency ({commit}, accountExternalIdentifierWithCurrency) {
        commit('SET_TOTAL_CURRENCY', accountExternalIdentifierWithCurrency.currency);
        this.dispatch('loadTotal', { accountId: accountExternalIdentifierWithCurrency.accountId, currency: accountExternalIdentifierWithCurrency.currency });
    },

    loadTotal ({commit}, accountExternalIdentifierWithCurrency) {
      const path = `/api/finance/total/${accountExternalIdentifierWithCurrency.accountId}?currency=${accountExternalIdentifierWithCurrency.currency}&byType=true`
      axios.get(
        path, { headers: authHeader() }
      ).then((response) => {
        const json = response.data;
        commit('SET_TOTAL', json);
      }).catch(function (error) {
        if (error.response) {
          console.log(error.response.data);
          console.log(error.response.status);
          console.log(error.response.headers);
        } else if (error.request) {
          console.log(error.request);
        } else {
          console.log('Error', error.message);
        }
      });
    },

    loadPositions ({commit}, accountExternalIdentifier) {
      const path = `/api/finance/positions/${accountExternalIdentifier}`
      axios.get(
        path, { headers: authHeader() }
      ).then((response) => {
        const json = response.data;
        commit('SET_POSITIONS', json);
      }).catch(function (error) {
        if (error.response) {
          console.log(error.response.data);
          console.log(error.response.status);
          console.log(error.response.headers);
        } else if (error.request) {
          console.log(error.request);
        } else {
          console.log('Error', error.message);
        }
      });
    },

    addPosition ({commit}, accountExternalIdentifierWithCurrency) {
      const path = `/api/finance/positions/${accountExternalIdentifierWithCurrency.accountId}`;
      var positionContent = [this.getters.positions[this.getters.positions.length - 1]];
      axios.post(
        path, positionContent, { headers: authHeader() }
      ).then((response) => {
        const json = response.data;
        commit('ADD_POSITION', json[0].externalIdentifier);
        this.dispatch('loadTotal', accountExternalIdentifierWithCurrency);
        this.dispatch('loadPositions', accountExternalIdentifierWithCurrency.accountId);
      }).catch(function (error) {
        if (error.response) {
          console.log(error.response.data);
          console.log(error.response.status);
          console.log(error.response.headers);
        } else if (error.request) {
          console.log(error.request);
        } else {
          console.log('Error', error.message);
        }
      });
    },

    removePosition ({commit}, accountExternalIdentifierWithCurrencyWithPosition) {
      const path = `/api/finance/positions/${accountExternalIdentifierWithCurrencyWithPosition.accountId}/${accountExternalIdentifierWithCurrencyWithPosition.positionId}`;
      axios.delete(
        path, { headers: authHeader() }
      ).then(() => {
        commit('DELETE_POSITION', accountExternalIdentifierWithCurrencyWithPosition.positionId);
        this.dispatch('loadTotal', { accountId: accountExternalIdentifierWithCurrencyWithPosition.accountId, currency: accountExternalIdentifierWithCurrencyWithPosition.currency });
      }).catch(function (error) {
        if (error.response) {
          console.log(error.response.data);
          console.log(error.response.status);
          console.log(error.response.headers);
        } else if (error.request) {
          console.log(error.request);
        } else {
          console.log('Error', error.message);
        }
      });
    }
  }
}

