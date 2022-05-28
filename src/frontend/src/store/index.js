import { createStore } from 'vuex'
import axios from "axios";

export default createStore({
  state: {
    defaultAccount: "537ec339-c63e-4baf-ae64-57a7ee4248c9",
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
        state.positions = positions;
    }
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
    }
  }
})

