<template>
    <div>
        <hr>
        <h2>Total</h2>
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