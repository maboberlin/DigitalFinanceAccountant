<template>
  <h1>Digital Finance Accountant</h1>
  <AssetsTotal />
  <AssetsTable />
</template>

<script>
import AssetsTotal from './components/AssetsTotal.vue'
import AssetsTable from './components/AssetsTable.vue'

export default {
  name: 'App',
  components: {
    AssetsTotal,
    AssetsTable
  }
}
</script>

<style>

</style>