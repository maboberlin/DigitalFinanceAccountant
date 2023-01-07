import SnapshotsService from '../services/snapshots.service';
export const snapshots = {
  namespaced: true,
  state: {
    snapshots: null,
  },
  getters: {
    snapshots: state => state.snapshots,
  },
  actions: {
    getSnapshots({ commit }, user) {
      return SnapshotsService.getSnapshots(user.id).then(
        snapshots => {
          commit('setSnapshots', snapshots);
          return Promise.resolve(snapshots);
        },
        error => {
          return Promise.reject(error);
        }
      );
    },

    addSnapshot({ commit }, userIdAndAccountsIdAndCurrency) {
      return SnapshotsService.createSnapshot(userIdAndAccountsIdAndCurrency.userId, userIdAndAccountsIdAndCurrency.accountId, userIdAndAccountsIdAndCurrency.currency ).then(
        snapshot => {
          commit('addSnapshot', { accountId: userIdAndAccountsIdAndCurrency.accountId, snapshot: snapshot });
          return Promise.resolve(snapshot);
        },
        error => {
          return Promise.reject(error);
        }
      );
    },
  },

  mutations: {
    setSnapshots(state, snapshots) {
      state.snapshots = snapshots.snapshotMap;
    },
    addSnapshot(state, dto) {
      state.snapshots[dto.accountId].snapshots.push(dto.snapshot);
    },
  }
};