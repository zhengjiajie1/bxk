<template>
    <div class="header">
        <div class="header-container">
            <div class="app-name">
                <router-link to="/">
                        <img src="@/assets/03.png" style="
                                width: 40px;
                                height: 40px;
                                margin: 5px 5px -5px 0;
                                -webkit-user-drag: none;
                                -khtml-user-drag: none;
                                -moz-user-drag: none;
                                user-drag: none;
                            " />
                    <b style="color: black;display: inline-block; margin-bottom: 20px;font-size: 28px;">
                        北信科二手交易平台
                    </b>
                </router-link>

            </div>
            <div class="search-container">
                <el-input placeholder="搜索二手物品" v-model="searchValue" @keyup.enter.native="searchIdle" size="large">
                    <el-button slot="append" @click="searchIdle">
                        <i class="iconfont icon-r-find" style="font-size: 24px;"> </i>
                    </el-button>
                </el-input>
            </div>
            <el-button type="success" @click="toRelease"> 出售二手物品</el-button>
            <el-button v-if="isMaster&&!isAdmin" type="warning" @click="toMyStore"> 我的橱窗</el-button>
            <router-link v-if="!isLogin" class="user-name-text" to="/login"> 登录</router-link>
            <el-dropdown trigger="click" v-else>
                <div style="cursor:pointer;display: flex;align-items: center;">
                    <div style="font-size: 28px;color: #409EFF;padding-right: 5px;">
                        <b>

                            {{ nicknameValue ? nicknameValue : nickname }}
                        </b>
                    </div>
                    <el-avatar :src="avatar" shape="square" :size="60"></el-avatar>
                </div>
                <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item>
                        <div @click="toMe">个人中心</div>
                    </el-dropdown-item>
                    <el-dropdown-item>
                        <div @click="toWallet">我的钱包</div>
                    </el-dropdown-item>
                    <el-dropdown-item divided style="color: red;">
                        <div @click="loginOut"> 退出登录</div>
                    </el-dropdown-item>
                </el-dropdown-menu>
            </el-dropdown>
        </div>
    </div>
</template>
<script>

export default {
    name: 'Header',
    props: ['searchInput', 'nicknameValue', 'avatarValue'],
    data() {
        return {
            searchValue: this.searchInput,
            nickname: '登录',
            avatar: '/avatar.jpg',
            isLogin: false
        };
    },
    computed: {
        // 计算是否为普通用户（已登录且非管理员）
        isMaster() {
            return this.isLogin;
        },
        // 计算是否为管理员
        isAdmin() {
            return this.$route.path.includes('/platform-admin') || this.$route.path.includes('/login-admin');
        }
    },
    created() {
        if (!this.$globalData.userInfo.nickname) {
            this.$api.getUserInfo().then(res => {
                console.log('Header getUserInfo:', res);
                if (res.status_code === 1) {
                    this.nickname = res.data.nickname;
                    this.avatar = this.$store.state.baseApi + res.data.avatar;
                    res.data.signInTime = res.data.signInTime.substring(0, 10);
                    this.$globalData.userInfo = res.data;
                    this.isLogin = true;
                }
            })
        } else {
            this.nickname = this.$globalData.userInfo.nickname;
            this.avatar = this.$store.state.baseApi + this.$globalData.userInfo.avatar;
            this.isLogin = true;
        }
    },
    methods: {
        searchIdle() {
            if ('/search' !== this.$route.path) {
                this.$router.push({ path: '/search', query: { searchValue: this.searchValue } });
            } else {
                this.$router.replace({ path: '/search', query: { searchValue: this.searchValue } });
                this.$router.go(0);
            }

        },
        toMe() {
            if ('/me' !== this.$route.path) {
                this.$router.push({ path: '/me' });
            }
        },
        toWallet() {
            if ('/wallet' !== this.$route.path) {
                this.$router.push({ path: '/wallet' });
            }
        },
        toRelease() {
            if ('/release' !== this.$route.path) {
                this.$router.push({ path: '/release' });
            }
        },
        toMyStore() {
            if ('/store' !== this.$route.path) {
                this.$router.push({ path: '/store' });
            }
        },
        loginOut() {
            this.$api.logout().then(res => {
                if (res.status_code === 1) {
                    this.$globalData.userInfo = {};
                    console.log("login out");
                    if ('/index' === this.$route.path) {
                        this.$router.go(0);
                    } else {
                        this.$router.push({ path: '/index' });
                    }
                } else {
                    this.$message.error('网络或系统异常，退出登录失败！');
                }
            });

        }
    }
};
</script>
<style scoped>
.header {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    width: 100%;
    height: 70px;
    background: #f0f8ff;
    display: flex;
    justify-content: center;
    border-bottom: #eeeeee solid 2px;
    z-index: 1000;
}

.header-container {
    width: 1000px;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.app-name a {
    color: #409EFF;
    font-size: 24px;
    text-decoration: none;
}

.search-container {
    width: 300px;
}

.user-name-text {
    font-size: 16px;
    font-weight: 600;
    color: #409EFF;
    cursor: pointer;
    text-decoration: none;
}</style>
