import { createApp } from 'vue';
import App from './App.vue';
import router from './router/router.js';
import axios from 'axios';
import VueAxios from 'vue-axios';
import ElementPlus from 'element-plus'

import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import {zhTw} from "element-plus/es/locale/index";
import VueCookies from 'vue-cookies'

const app = createApp(App);

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}
app.use(router);
app.use(VueAxios, axios);
app.use(ElementPlus, {
    locale: zhTw,
})
app.use(VueCookies, { expires: '1d'})
app.mount('#app');
