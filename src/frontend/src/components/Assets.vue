<template>
    <h2><b>{{ selectedAccount }}</b></h2>
    <hr>
    <div>
        <h2>Total</h2>
        <table id="totalTable" class="table table-bordered table-striped" style="width:50%">
            <thead>
            <tr>
                <th>Type</th>
                <th>Value</th>
                <th>Ratio</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="(value, key) in total.valueByPosition" v-bind:key="key">
                <td>{{ key }}</td>
                <td>{{ value }}</td>
                <td>{{ ((value / total.value) * 100).toFixed(2) }} %</td>
            </tr>
            <tr>
                <td><b>TOTAL</b></td>
                <td><b>{{total.value}}</b></td>
                <td><b>100.00 %</b></td>
            </tr>
            </tbody>
        </table>
        <select v-model="selectedCurrency" @change="totalCurrencyChange($event)" class="form-select" style="width:150px; height:50px">
            <option value="EUR">EUR</option>
            <option value="USD">USD</option>
            <option value="CHF">CHF</option>
        </select>
    </div>
    <hr>
    <h2>Positions</h2>
    <table id="assetsTable" class="table table-bordered table-striped">
        <thead>
        <tr>
            <th  v-for="header in tableHeaders" v-bind:key='header' @click="sortTable(header)" >
                {{header}} <i class="bi bi-sort-alpha-down" aria-label='Sort Icon'></i>
            </th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="position in positions" v-bind:key='position.externalIdentifier'>
            <td v-if="position.isEdit === false">{{ position.identifier }}</td>
            <td v-if="position.isEdit === true"><input type="text" v-model="position.identifier"></td>

            <td v-if="position.isEdit === false && position.isNameEdit === false" @click="toggleEditName(position)">{{ position.name }}</td>
            <td v-if="position.isEdit === false && position.isNameEdit === true"  v-on:keyup.enter="toggleEditName(position)" @change="updatePosition(position)"><input type="text" v-model="position.name"></td>
            <td v-if="position.isEdit === true"><input type="text" v-model="position.name"></td>

            <td v-if="position.isEdit === false && position.isAmountEdit === false" @click="toggleEditAmount(position)">{{ position.amount }}</td>
            <td v-if="position.isEdit === false && position.isAmountEdit === true"  v-on:keyup.enter="toggleEditAmount(position)" @change="updatePosition(position)"><input type="text" v-model="position.amount"></td>
            <td v-if="position.isEdit === true"><input type="text" v-model="position.amount"></td>

            <td v-if="position.isEdit === false">{{ position.price }}</td>
            <td v-if="position.isEdit === true"></td>

            <td v-if="position.isEdit === false">{{ position.currency }}</td>
            <td v-if="position.isEdit === true"></td>

            <td v-if="position.isEdit === false">{{ position.type }}</td>
            <td v-if="position.isEdit === true">
                <select v-model="position.type">
                    <option value="STOCK">Stock</option>
                    <option value="RESOURCE">Resource</option>
                    <option value="CURRENCY">Currency</option>
                    <option value="KRYPTO">Krypto</option>
                    <option value="BOND">Bond</option>
                    <option value="REAL_ESTATE">Real Estate</option>
                </select>
            </td>

            <td v-if="position.isEdit === false"><button class="button btn-primary" @click="removePosition(position.externalIdentifier)">Remove</button></td>
        </tr>
        </tbody>
    </table>
    <div>
        <button class="button btn-primary" @click="addPosition">Add Position</button>
    </div>
    <hr>
</template>

<script>
export default {
  data() {
    return {
        tableHeaders: ['Symbol','Name','Amount','Price','Currency', 'Type']
    }
  },
  name: 'Assets',
  computed: {
    total() {
      return this.$store.getters.total;
    },
    positions() {
      return this.$store.getters.positions;
    },
    selectedAccount() {
      return this.$store.getters.selectedAccount;
    },
    selectedCurrency() {
      return this.$store.getters.selectedCurrency;
    },
  },
  created() {
    this.$store.dispatch('loadPositions', this.$route.params.accountExternalIdentifier );
    this.$store.dispatch('loadTotal', { accountId: this.$route.params.accountExternalIdentifier, currency: this.$store.getters.selectedCurrency });
  },
  methods: {
    addPosition: function(){
      this.$store.dispatch('addPosition', { accountId: this.$route.params.accountExternalIdentifier, currency: this.$store.getters.selectedCurrency });
    },
    removePosition: function(externalIdentifier){
      this.$store.dispatch('removePosition', { accountId: this.$route.params.accountExternalIdentifier, positionId: externalIdentifier, currency: this.$store.getters.selectedCurrency });
    },
    totalCurrencyChange: function(event){
       this.$store.dispatch('setTotalCurrency', { accountId: this.$route.params.accountExternalIdentifier, currency: event.target.value });
    },
    updatePosition: function(position){
       this.$store.dispatch('updatePosition', { accountId: this.$route.params.accountExternalIdentifier, positionId: position.externalIdentifier, positionJson: position, currency: this.$store.getters.selectedCurrency })
    },
    toggleEditAmount: function(position){
        position.isAmountEdit = ( position.isAmountEdit == true) ? false : true
    },
    toggleEditName: function(position){
        position.isNameEdit = ( position.isNameEdit == true) ? false : true
    }
  },

}
</script>
<style scoped>
  h2 {
    margin-top: 10px;
    margin-bottom: 20px;
  }
</style>