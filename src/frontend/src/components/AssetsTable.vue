<template>
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
            <td v-if="position.isEdit === false">{{ position.name }}</td>
            <td v-if="position.isEdit === false">{{ position.amount }}</td>
            <td v-if="position.isEdit === false">{{ position.currency }}</td>
            <td v-if="position.isEdit === false">{{ position.type }}</td>

            <td v-if="position.isEdit === true"><input type="text" v-model="position.identifier"></td>
            <td v-if="position.isEdit === true"><input type="text" v-model="position.name"></td>
            <td v-if="position.isEdit === true"><input type="text" v-model="position.amount"></td>
            <td v-if="position.isEdit === true"><input type="text" v-model="position.currency"></td>
            <td v-if="position.isEdit === true">
                <select v-model="position.type">
                    <option value="STOCK">Stock</option>
                    <option value="RESOURCE">Resource</option>
                    <option value="CURRENCY">Currency</option>
                    <option value="KRYPTO">Krypto</option>
                </select>
            </td>
            <td v-if="position.isEdit === false"><button class="button btn-primary" @click="removePosition(position.externalIdentifier)">Remove</button></td>
        </tr>
        </tbody>
    </table>
    <div>
        <button class="button btn-primary" @click="addPosition">Add Position</button>
    </div>
</template>

<script>
export default {
  data() {
    return {
        tableHeaders: ['Symbol','Name','Amount','Currency','Type'],
        currencies: ['USD', 'EUR']
    }
  },
  name: 'AssetsTable',
  computed: {
    positions() {
      return this.$store.getters.positions;
    },
  },
  created() {
    this.$store.dispatch('loadPositions');
  },
  methods: {
    addPosition: function(){
      this.$store.dispatch('addPosition');
    },
    removePosition: function(externalIdentifier){
      this.$store.dispatch('removePosition', externalIdentifier);
    },
  }
}
</script>