<template>
  <div id="app">
    <nav class="navbar navbar-expand navbar-dark bg-dark">
      <a href="/" class="navbar-brand">Digital Accountant</a>
      <div v-if="!currentUser" class="navbar-nav ml-auto">
        <li class="nav-item">
          <router-link to="/register" class="nav-link">
            <font-awesome-icon icon="user-plus" /> Sign Up
          </router-link>
        </li>
        <li class="nav-item">
          <router-link to="/login" class="nav-link">
            <font-awesome-icon icon="sign-in-alt" /> Login
          </router-link>
        </li>
      </div>
      <div v-if="currentUser" class="navbar-nav ml-auto">
        <li class="nav-item">
          <router-link to="/user" class="nav-link">
            <font-awesome-icon icon="user" />
            {{ currentUser.forename }} {{ currentUser.surname }}
          </router-link>
        </li>
        <li class="nav-item">
          <router-link v-if="currentUser" to="/accounts" class="nav-link">Accounts</router-link>
        </li>
        <li class="nav-item">
          <router-link v-if="currentUser" to="/snapshots" class="nav-link">Snapshots</router-link>
        </li>
        <li class="nav-item" @click.prevent="logOut">
            <router-link v-if="currentUser" to="/logout" class="nav-link"><font-awesome-icon icon="sign-out-alt" />LogOut</router-link>
        </li>
      </div>
    </nav>
    <div class="container">
      <router-view />
    </div>
  </div>
</template>
<script>
export default {
  computed: {
    currentUser() {
      return this.$store.state.auth.user;
    },
  },
  methods: {
    logOut() {
      this.$store.dispatch('auth/logout');
      this.$router.push('/login');
    }
  }
};
</script>