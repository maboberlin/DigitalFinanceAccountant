<template>
    <div>
        <h2>POSITIONS</h2>
        <div v-for="position in positions" v-bind:key="position.identifier">
            <div>
                <div style="padding:0px;width: 200px;float:left">{{ position.name }}</div>
                <div style="padding:0px;margin-bottom: 10px">{{ position.amount }}</div>
            </div>
        </div>
    </div>
</template>

<script>
export default {
  name: 'AssetsList',
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