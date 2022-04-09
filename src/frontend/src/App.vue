<template>
  <h1>Digital Finance Accountant</h1>
  <AssetsTotal />
  <AssetsList />
</template>

<script>
import AssetsTotal from './components/AssetsTotal.vue'
import AssetsList from './components/AssetsList.vue'

export default {
  name: 'App',
  components: {
    AssetsTotal,
    AssetsList
  }
}
</script>

<style>

</style>