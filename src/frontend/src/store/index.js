import { createStore } from 'vuex'
import axios from "axios";

export default createStore({
  state: {
    defaultAccount: "c848566e-438b-40a1-90ea-0d052fca323a",
    total: {},
    positions: []
  },
  getters: {
    defaultAccount: state => state.defaultAccount,
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
    loadTotal ({commit}) {
      const path = `/api/finance/total/${this.getters.defaultAccount}?currency=EUR&byType=true`
      axios.get(
        path
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

    loadPositions ({commit}) {
      const path = `/api/finance/positions/${this.getters.defaultAccount}`
      axios.get(
        path
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

    addPosition ({commit}) {
      const path = `/api/finance/positions/${this.getters.defaultAccount}`;
      var positionContent = [this.getters.positions[this.getters.positions.length - 1]];
      axios.post(
        path, positionContent
      ).then((response) => {
        const json = response.data;
        commit('ADD_POSITION', json[0].externalIdentifier);
        this.dispatch('loadTotal');
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

    removePosition ({commit}, externalIdentifier) {
      const path = `/api/finance/positions/${this.getters.defaultAccount}/${externalIdentifier}`;
      axios.delete(
        path
      ).then(() => {
        commit('DELETE_POSITION', externalIdentifier);
        this.dispatch('loadTotal');
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
})

