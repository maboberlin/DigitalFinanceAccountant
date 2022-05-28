<template>
    <h2>POSITIONS</h2>
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
            <td>{{ position.identifier }}</td>
            <td>{{ position.name }}</td>
            <td>{{ position.amount }}</td>
        </tr>
        </tbody>
    </table>
</template>

<script>
export default {
  data() {
    return {
        tableHeaders: ['Symbol','Name','Amount'],
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
}
</script>