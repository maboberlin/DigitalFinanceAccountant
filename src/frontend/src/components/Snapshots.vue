<template>
</template>

<script>
export default {
  data() {
    return {
        tableHeaders: ['CURRENCY','BOND','STOCK','KRYPTO','REAL_ESTATE', 'RESOURCE']
    }
  },
  name: 'Snapshots',
  computed: {
    accounts() {
      return this.$store.state.user.accounts;
    },
  },
  created() {
  },
  methods: {
  },

}
</script>
<style scoped>
  h2 {
    margin-top: 10px;
    margin-bottom: 20px;
  }
</style>