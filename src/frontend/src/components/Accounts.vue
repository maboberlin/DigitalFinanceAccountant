<template>
    <h2>Accounts</h2>
    <table id="accountsTable" class="table table-bordered table-striped">
        <thead>
        <tr>
            <th  v-for="header in tableHeaders" v-bind:key='header' @click="sortTable(header)" >
                {{header}} <i class="bi bi-sort-alpha-down" aria-label='Sort Icon'></i>
            </th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="account in accounts" v-bind:key='account.externalIdentifier'>
            <td v-if="account.isEdit === false">{{ account.accountIdentifier }}</td>

            <td v-if="account.isEdit === true"><input type="text" v-model="account.accountIdentifier"></td>

            <td v-if="account.isEdit === false"><button class="button btn-primary" @click="openAccount(account.externalIdentifier)">Open</button></td>

            <td v-if="account.isEdit === false"><button class="button btn-primary" @click="deleteAccount(account.externalIdentifier)">Delete</button></td>
        </tr>
        </tbody>
    </table>
    <div>
        <button class="button btn-primary" @click="createAccount">Create Account</button>
    </div>
</template>

<script>
export default {
  name: 'Accounts',
  data() {
    return {
        tableHeaders: ['Name']
    }
  },
  computed: {
    currentUser() {
      return this.$store.state.auth.user;
    },
    accounts() {
      return this.$store.state.user.accounts;
    },
  },
  mounted() {
    if (!this.currentUser) {
      this.$router.push('/login');
    }
  },
  created() {
    this.$store.dispatch('user/getAccounts', this.currentUser);
  },
  methods: {
    createAccount: function(){
      this.$store.dispatch('user/createAccount', this.currentUser);
    },
    openAccount: function(externalIdentifier) {
      this.$router.push('/assets/' + externalIdentifier);
    },
    deleteAccount: function(externalIdentifier){
      this.$store.dispatch('user/deleteAccount', {currentUser: this.currentUser, externalIdentifier: externalIdentifier});
    },
  }
};
</script>
<style scoped>
  h2 {
    margin-top: 10px;
    margin-bottom: 20px;
  }
</style>