<template>
    <div>
        <h2>TOTAL</h2>
        <p>{{ total.value }}</p>
    </div>
</template>

<script>
export default {
  name: 'AssetsTotal',
  computed: {
    total() {
      return this.$store.getters.total;
    },
  },
  created() {
    this.$store.dispatch('loadTotal');
  },
}
</script>