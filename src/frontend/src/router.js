import { createWebHistory, createRouter } from "vue-router";
import Login from "./components/Login.vue";
import Empty from "./components/Empty.vue";
import Register from "./components/Register.vue";
import User from "./components/User.vue";
import Snapshots from "./components/Snapshots.vue";
// lazy-loaded
const Accounts = () => import("./components/Accounts.vue")
const Assets = () => import("./components/Assets.vue")
const routes = [
  {
    path: "/",
    name: "empty",
    component: Empty,
  },
  {
    path: "/login",
    component: Login,
  },
  {
    path: "/register",
    component: Register,
  },
  {
    path: "/accounts",
    name: "accounts",
    // lazy-loaded
    component: Accounts,
  },
  {
    path: "/assets/:accountExternalIdentifier",
    name: "assets",
    // lazy-loaded
    component: Assets,
  },
  {
    path: "/snapshots",
    name: "snapshots",
    // lazy-loaded
    component: Snapshots,
  },
  {
    path: "/user",
    name: "user",
    component: User,
  },
  {
    path: "/logout",
    name: "logout",
    component: Login,
  }
];
const router = createRouter({
  history: createWebHistory(),
  routes,
});
export default router;