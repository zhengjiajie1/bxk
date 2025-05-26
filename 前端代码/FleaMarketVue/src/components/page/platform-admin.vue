<template>
    <div style="background-color: white;min-height:100vh;">
        <el-header>
            <div class="header">
                <div class="app-name">
                    <router-link to="/platform-admin">
                    </router-link>
                </div>
                <span class="app-title">
                    <img src="@/assets/04.png" style="
                            width: 40px;
                            height: 40px;
                            margin: 5px 5px 10px 0;
                            -webkit-user-drag: none;
                            -khtml-user-drag: none;
                            -moz-user-drag: none;
                            user-drag: none;
                        " />
                    <span style="color: black;display: inline-block;margin-top: 10px; font-size: 24px;">
                        欢迎使用二手交易平台管理系统，<b>{{ admin.nickname }}</b>
                    </span>
                </span>
                <div class="app-logOut">
                    <el-button style="margin-right: 100px" type="primary" @click="logout"> 退出账号</el-button>
                </div>
            </div>
        </el-header>
        <el-container>
            <div class="mainBody" background-color="white">
                <el-aside>
                    <el-col :span="24">
                        <el-menu default-active="1" class="el-menu-vertical-demo" @select="handleSelect"
                            background-color="white" text-color="grey" active-text-color="black">
                            <el-menu-item index="1" class="el-item-menu">
                                <span slot="title"> 交易物品管理</span>
                            </el-menu-item>
                            <el-menu-item index="2" class="el-item-menu">
                                <span slot="title"> 订单信息管理</span>
                            </el-menu-item>
                            <el-menu-item index="3" class="el-item-menu">
                                <span slot="title"> 用户信息管理</span>
                            </el-menu-item>
                            <el-menu-item index="4" class="el-item-menu">
                                <span slot="title"> 用户注册审核</span>
                            </el-menu-item>
                            <el-menu-item index="5" class="el-item-menu">
                                <span slot="title"> 商品发布审核</span>
                            </el-menu-item>
                        </el-menu>
                    </el-col>
                </el-aside>
                <el-main>
                    <IdleGoods v-if="mode == 1"></IdleGoods>
                    <orderList v-if="mode == 2"></orderList>
                    <userList v-if="mode == 3"></userList>
                    <userAudit v-if="mode == 4"></userAudit>
                    <goodsAudit v-if="mode == 5"></goodsAudit>
                </el-main>
            </div>
        </el-container>
    </div>
</template>

<script>
import IdleGoods from '../common/IdleGoods.vue'
import orderList from '../common/orderList.vue'
import userList from '../common/userList.vue'
import userAudit from '../common/userAudit.vue'
import goodsAudit from '../common/goodsAudit.vue'
export default {
    name: "platform-admin",
    components: {
        IdleGoods,
        orderList,
        userList,
        userAudit,
        goodsAudit,
    },
    data() {
        return {
            mode: 1,
            admin: {
                nickname: '管理员',
            },
        }
    },
    created() {
        this.admin.nickname = this.$sta.adminName;
    },
    methods: {
        logout() {
            this.$api.loginOut({
            }).then(res => {
                if (res.status_code === 1) {
                    this.$sta.isLogin = false;
                    this.$sta.adminName = '';
                    this.$router.push({ path: '/login-admin' });
                }
            }).catch(e => {
                console.log(e)
            })
        },
        handleSelect(key) {
            this.mode = key;
        },
    },
}
</script>

<style scoped>
.header {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    min-width: 100vw;
    height: 58px;
    background: #ffffff;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: lightgrey solid 2px;
    z-index: 1000;
}

.app-name {
    display: flex;
    justify-content: center;
    align-items: center;
    min-width: 11vw;
    flex: 1;
    height: 100%;
    border-right: 1px solid #fff;
    background-color: white;

}

.app-name a {
    color: #FFFFFF;
    font-size: 18px;
    font-weight: 800;
    text-decoration: none;
    padding: 10px 20px 0 0;
}

.app-title {
    display: flex;
    justify-content: center;
    flex: 8;
    color: black;
    font-size: 28px;
}

.app-logOut {
    display: flex;
    flex: 1;
    justify-content: flex-end;
    align-items: center;
}

.mainBody {
    display: flex;
    width: 100%;
}

aside {
    /* flex: 1; */
    box-sizing: content-box;
    min-height: calc(100vh - 100px);
    background-color: rgb(255, 255, 255);
    border-top: 1px solid lightgrey;
    border-right: 1px solid lightgrey;
}

main {
    flex: 9;
}

.foot {
    position: absolute;
    left: 0;
    bottom: 0;
    width: 100%;
    height: 58px;
    background-color: #ffffff;
}

/* 统一菜单项字体样式 */
.el-item-menu span {
    font-weight: normal !important;
    font-size: 20px !important;
}
</style>