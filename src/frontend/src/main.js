import { createApp } from 'vue'
import App from './App.vue'
import store from './store/index.js'
import router from "./router";
import './assets/reset.css';

import "bootstrap";
import "bootstrap/dist/css/bootstrap.min.css";
import { FontAwesomeIcon } from './plugins/font-awesome'

createApp(App)
    .use(store)
    .use(router)
    .component("font-awesome-icon", FontAwesomeIcon)
    .mount('#app')
