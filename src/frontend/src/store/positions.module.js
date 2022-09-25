import axios from "axios";
import authHeader from '../services/auth-header';

export const positions = {
  state: {
    total: {},
    positions: []
  },
  getters: {
    total: state => state.total,
    positions: state => state.positions
  },
  mutations: {
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
    loadTotal ({commit}, accountExternalIdentifier) {
      const path = `/api/finance/total/${accountExternalIdentifier}?currency=EUR&byType=true`
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

    addPosition ({commit}, accountExternalIdentifier) {
      const path = `/api/finance/positions/${accountExternalIdentifier}`;
      var positionContent = [this.getters.positions[this.getters.positions.length - 1]];
      axios.post(
        path, positionContent, { headers: authHeader() }
      ).then((response) => {
        const json = response.data;
        commit('ADD_POSITION', json[0].externalIdentifier);
        this.dispatch('loadTotal', accountExternalIdentifier);
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

    removePosition ({commit}, accountIdWithPositionId) {
      const path = `/api/finance/positions/${accountIdWithPositionId.accountId}/${accountIdWithPositionId.positionId}`;
      axios.delete(
        path, { headers: authHeader() }
      ).then(() => {
        commit('DELETE_POSITION', accountIdWithPositionId.positionId);
        this.dispatch('loadTotal', accountIdWithPositionId.accountId);
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

