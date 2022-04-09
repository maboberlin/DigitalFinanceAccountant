<template>
  <h1>{{ msg }}</h1>
  <p>{{ name }}</p>
</template>

<script>
export default {
  name: 'HelloWorld',
  data() {
    return {
      msg: ''
    }
  },
  computed: {
    name() {
        return this.$store.state.name;
      }
  },
  mounted() {
    fetch("/api/messages/hello")
      .then((response) => response.text())
      .then((data) => {
          this.msg = data;
      });
  }
}
</script>