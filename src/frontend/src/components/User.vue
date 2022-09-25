<template>
    <div class="container">
        <header class="jumbotron">
            <h2>
                <strong>{{currentUser.forename}} {{currentUser.surname}}</strong>
            </h2>
        </header>
        <p>
            <strong>Token:</strong>
            {{currentUser.accessToken.substring(0, 20)}} ... {{currentUser.accessToken.substr(currentUser.accessToken.length - 20)}}
        </p>
        <p>
            <strong>Id:</strong>
            {{currentUser.id}}
        </p>
        <p>
            <strong>Email:</strong>
            {{currentUser.email}}
        </p>
        <strong>Authorities:</strong>
        <ul>
            <li v-for="role in currentUser.roles" :key="role">{{role}}</li>
        </ul>
    </div>
</template>

<script>
export default {
  name: 'User',
  computed: {
    currentUser() {
      return this.$store.state.auth.user;
    }
  },
  mounted() {
    if (!this.currentUser) {
      this.$router.push('/login');
    }
  }
};
</script>

<style scoped>
  h2 {
    margin-top: 10px;
    margin-bottom: 20px;
  }
</style>