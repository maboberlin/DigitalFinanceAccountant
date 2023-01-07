<template>
    <div v-for="(snapshotListDto, accountId) in snapshots" v-bind:key='accountId'>
        <h2>{{ snapshotListDto.accountName }}</h2>
        <table id="snapshotsTable" class="table table-bordered table-striped">
            <thead>
            <tr>
                <th  v-for="header in tableHeaders" v-bind:key='header' @click="sortTable(header)" >
                    {{header}} <i class="bi bi-sort-alpha-down" aria-label='Sort Icon'></i>
                </th>
            </tr>
            </thead>
            <tbody>
                <tr v-for="snapshotItem in snapshotListDto.snapshots" v-bind:key='snapshotItem.snapshotTimestamp'>
                    <td>{{ snapshotItem.snapshotTimestamp }}</td>
                    <td>{{ snapshotItem.currencyValue || 0 }}</td>
                    <td>{{ snapshotItem.bondsValue || 0 }}</td>
                    <td>{{ snapshotItem.stocksValue || 0 }}</td>
                    <td>{{ snapshotItem.kryptoValue || 0 }}</td>
                    <td>{{ snapshotItem.realEstateValue || 0 }}</td>
                    <td>{{ snapshotItem.resourceValue || 0 }}</td>
                    <td>{{ snapshotItem.total || 0 }}</td>
                    <td>{{ snapshotItem.currency }}</td>
                </tr>
            </tbody>
        </table>
        <div>
            <button class="button btn-primary" @click="addSnapshot(accountId)">Take Snapshot</button>
            <select v-model="selectedCurrency" @change="changeCurrency($event)" class="form-select" style="width:140px; height:50px; margin-top:10px">
                <option value="EUR">EUR</option>
                <option value="USD">USD</option>
                <option value="CHF">CHF</option>
            </select>
        </div>
        <hr>
    </div>
</template>

<script>
export default {
  data() {
    return {
        tableHeaders: ['TIME', 'CURRENCY','BOND','STOCK','KRYPTO','REAL_ESTATE', 'RESOURCE', 'TOTAL', 'CURRENCY']
    }
  },
  name: 'Snapshots',
  computed: {
    currentUser() {
      return this.$store.state.auth.user;
    },
    snapshots() {
      return this.$store.state.snapshots.snapshots;
    },
    selectedCurrency() {
      return this.$store.getters.selectedCurrency;
    },
  },
  created() {
    this.$store.dispatch('snapshots/getSnapshots', this.currentUser);
  },
  methods: {
    addSnapshot: function(accountExternalId){
      this.$store.dispatch('snapshots/addSnapshot', { userId: this.currentUser.id, accountId: accountExternalId, currency: this.selectedCurrency });
    },
    changeCurrency: function(event){
       this.$store.dispatch('changeCurrentCurrency', event.target.value );
    },
  },

}
</script>
<style scoped>
  h2 {
    margin-top: 10px;
    margin-bottom: 20px;
  }
</style>