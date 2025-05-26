<template>
    <div class="login-container">
        <el-card class="box-card">
            <div class="login-body">
                <div class="login-title" @click="toIndex">
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
                        二手交易平台
                    </b>
                </div>
                <el-form ref="form" :model="userForm">
                    <el-input placeholder="请输入手机号码..." v-model="userForm.accountNumber" class="login-input">
                        <template slot="prepend">
                            <div class="iconfont icon-r-user2" style="font-size: 24px;"></div>
                        </template>
                    </el-input>
                    <el-input placeholder="请输入密码..." v-model="userForm.userPassword" class="login-input"
                        @keyup.enter.native="login" show-password>
                        <template slot="prepend">
                            <div class="iconfont icon-r-lock" style="font-size: 24px;"></div>
                        </template>
                    </el-input>
                    <div class="login-submit">
                        <el-button type="primary" @click="login"> 登录</el-button>
                        <el-button type="warning" autocomplete="off" @click="$router.push('/sign-in')"
                            style="margin-left: 20px"> 注册</el-button>
                    </div>
                    <div class="other-submit">
                        <router-link to="/login-admin" class="sign-in-text"> 管理员登录</router-link>
                    </div>
                </el-form>
            </div>
        </el-card>
    </div>
</template>

<script>
export default {
    name: "login",
    data() {
        return {
            userForm: {
                accountNumber: '',
                userPassword: ''
            }
        };
    },

    methods: {
        login() {
            this.$api.userLogin({
                accountNumber: this.userForm.accountNumber,
                userPassword: this.userForm.userPassword
            }).then(res => {
                console.log(res);
                if (res.status_code === 1) {
                    // 检查用户状态
                    if (res.data.userStatus === 1) {
                        this.$message.error('您的账号已被封禁，请联系管理员！');
                        return;
                    } else if (res.data.userStatus === 2) {
                        this.$message({
                            message: '您的账号正在审核中，请耐心等待管理员审核！',
                            type: 'warning',
                            duration: 5000
                        });
                        return;
                    }
                    
                    res.data.signInTime = res.data.signInTime.substring(0, 10);
                    this.$globalData.userInfo = res.data;
                    this.$router.replace({ path: '/index' });
                } else {
                    this.$message.error(res.msg);
                }
            }).catch((e) => {
                console.log(e)
                if (
                    e.response == undefined ||
                    e.response.data == undefined
                ) {
                    this.$message({
                        showClose: true,
                        message: e,
                        type: "error",
                        duration: 20000,
                    });
                } else {
                    this.$message({
                        showClose: true,
                        message: e.response.data,
                        type: "error",
                        duration: 20000,
                    });
                }
            });
        },
        toIndex() {
            this.$router.replace({ path: '/index' });
        }
    }
}
</script>

<style scoped>
.login-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    width: 100%;
    
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    overflow-y: auto;
    height: 100%;
    background: url("../../assets/background.jpg") center top / cover no-repeat;

}

.login-body {
    padding: 30px;
    width: 300px;
    height: 100%;
}

.login-title {
    padding-bottom: 30px;
    text-align: center;
    font-weight: 600;
    font-size: 20px;
    color: #409EFF;
    cursor: pointer;
}

.login-input {
    margin-bottom: 20px;
}

.login-submit {
    margin-top: 20px;
    display: flex;
    justify-content: center;
}

.sign-in-container {
    padding: 0 10px;
}

.sign-in-text {
    color: #409EFF;
    font-size: 16px;
    text-decoration: none;
    line-height: 28px;
}

.other-submit {
    display: flex;
    justify-content: space-between;
    margin-top: 30px;
    margin-left: 200px;
}
</style>