import Vue from 'vue';
import Vuex from 'vuex';
import '../../initialize'

Vue.use(Vuex);

export default new Vuex.Store({
    state: {
        // 状态对象
        baseApi: 'http://localhost:9321'
    },
    mutations: {
        // 更改状态的方法
    },
    actions: {
        // 触发mutations中方法的方法
    },
    getters: {
        // 用于从 store 中的 state 中派生出一些状态
    },
    modules: {
        // 可以将store拆分成多个模块
    }
});