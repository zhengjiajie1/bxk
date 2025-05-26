<template>
    <div class="login-container">
        <el-card class="box-card">
            <div class="login-body">
                <div class="login-title">
                        <img src="@/assets/02.png" style="
                                width: 35px;
                                height: 35px;
                                margin: 5px 5px -5px 0;
                                -webkit-user-drag: none;
                                -khtml-user-drag: none;
                                -moz-user-drag: none;
                                user-drag: none;
                            " />
                    <b style="color: black;display: inline-block; margin-bottom: 20px;font-size: 28px;">
                         登录后台管理系统
                    </b>
                </div>
                <el-form ref="form" :model="userForm">
                    <el-input placeholder="请输入管理员账号" v-model="userForm.accountNumber" class="login-input">
                        <template slot="prepend">
                            <div class="iconfont icon-r-user2" style="font-size: 24px;"></div>
                        </template>
                    </el-input>
                    <el-input placeholder="请输入管理员密码" v-model="userForm.adminPassword" class="login-input"
                              @keyup.enter.native="login"  show-password>
                        <template slot="prepend">
                            <div class="iconfont icon-r-lock" style="font-size: 24px;"></div>
                        </template>
                    </el-input>
                    <div class="login-submit" style="margin-top: 20px"  >
                        <el-button type="primary" @click="login"> 登录</el-button>
                        <el-button type="warning" autocomplete="off" @click="$router.push('/login')" style="margin-left: 20px"> 返回前台</el-button>
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
                    adminPassword: ''
                }
            };
        },
        methods: {
            login() {
                this.$api.adminLogin({
                    accountNumber: this.userForm.accountNumber,
                    adminPassword: this.userForm.adminPassword
                }).then(res => {
                    console.log(res);
                    if (res.status_code === 1) {
                        console.log(res);
                        this.$sta.isLogin = true;
                        this.$sta.adminName=res.data.adminName;
                        this.$router.replace({path:'/platform-admin'});
                    } else {
                        this.$message.error('登录失败，账号或密码错误！');
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
        background-color: #f1f1f1;
    
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
        display: flex;
        justify-content: center;
    }
    .sign-in-text {
        color: #409EFF;
        font-size: 16px;
        text-decoration: none;
        line-height:28px;
    }
    .other-submit{
        display:flex;
        justify-content: space-between;
        margin-top: 10px;
    }
</style>