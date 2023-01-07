import { createStore } from "vuex";
import { auth } from "./auth.module";
import { positions } from "./positions.module";
import { user } from "./user.module";
import { snapshots } from "./snapshots.module";
const store = createStore({
  modules: {
    auth,
    positions,
    user,
    snapshots,
  },
});
export default store;