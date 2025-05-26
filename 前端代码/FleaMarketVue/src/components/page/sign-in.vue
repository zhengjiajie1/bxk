<template>
    <div class="sign-in-container">
        <el-card class="box-card">
            <div class="sign-in-body">
                <div class="sign-in-title">
                    <img src="@/assets/01.png" style="
                                width: 30px;
                                height: 30px;
                                margin: 5px 5px -5px 0;
                                -webkit-user-drag: none;
                                -khtml-user-drag: none;
                                -moz-user-drag: none;
                                user-drag: none;
                            " />
                    <b style="color: black;display: inline-block; margin-bottom: 20px;font-size: 28px;">
                         注册新账号
                    </b>
                </div>
                <el-input placeholder="请输入昵称..." maxlength="30"  v-model="userInfo.nickname" class="sign-in-input" clearable>
                </el-input>
                <el-input placeholder="请输入手机号码..." maxlength="11" v-model="userInfo.accountNumber" class="sign-in-input" clearable>
                </el-input>
                <el-input placeholder="请输入邮箱..." v-model="userInfo.email" class="sign-in-input" clearable>
                </el-input>
                <div class="address-container-add-item">
                    <span class="demonstration">所在地区</span>
                    <el-cascader :options="options" v-model="selectedOptions" @change="handleAddressChange"
                        :separator="' '" class="sign-in-input">
                    </el-cascader>
                </div>
                <el-radio-group v-model="userInfo.gender" class="sign-in-input" style="margin-bottom: 20px; width: 100%;">
                    <el-radio label="男">男</el-radio>
                    <el-radio label="女">女</el-radio>
                </el-radio-group>
                <el-input placeholder="请输入银行卡号(16位数字)..." maxlength="16" v-model="userInfo.bankCard" class="sign-in-input" clearable
                       @input="validateBankCard">
                </el-input>
                <el-input placeholder="请输入密码..." show-password maxlength="16" v-model="userInfo.userPassword" class="sign-in-input" clearable>
                </el-input>
                <el-input placeholder="请再次输入密码..." show-password maxlength="16" v-model="userPassword2" class="sign-in-input" clearable>
                </el-input>
                <div class="captcha-container">
                    <el-input placeholder="请输入验证码..." maxlength="4" v-model="captchaInput" class="captcha-input" clearable>
                    </el-input>
                    <div class="captcha-image" @click="refreshCaptcha">
                        <img v-if="captchaImage" :src="captchaImage" alt="验证码" style="width: 100%; height: 100%;" />
                        <div v-else class="loading-captcha">加载中...</div>
                    </div>
                </div>
                <div class="sign-in-submit">
                    <el-button type="success" @click="signIn"> 注册</el-button>
                    <el-button type="primary" @click="toLogin" style="margin-left: 20px" > 返回登录</el-button>
                </div>

            </div>
        </el-card>
    </div>
</template>

<script>
    import options from '../common/country-data.js'
    
    export default {
        name: "sign-in",
        data(){
            return{
                userPassword2:'',
                captchaInput: '',
                captchaText: '',
                captchaId: '',  // 存储后端返回的验证码ID
                captchaImage: '', // 后端返回的验证码图片
                userInfo:{
                    accountNumber:'',
                    userPassword:'',
                    nickname:'',
                    email: '',
                    city: '',
                    gender: '男',
                    bankCard: '',
                    provinceName: '',
                    cityName: '',
                    regionName: ''
                },
                selectedOptions: [],
                options,
                cityOptions: [
                    { value: '北京', label: '北京' },
                    { value: '上海', label: '上海' },
                    { value: '广州', label: '广州' },
                    { value: '深圳', label: '深圳' },
                    { value: '杭州', label: '杭州' },
                    { value: '南京', label: '南京' },
                    { value: '武汉', label: '武汉' },
                    { value: '西安', label: '西安' },
                    { value: '成都', label: '成都' },
                    { value: '重庆', label: '重庆' },
                    { value: '天津', label: '天津' },
                    { value: '苏州', label: '苏州' },
                    { value: '长沙', label: '长沙' },
                    { value: '郑州', label: '郑州' },
                    { value: '青岛', label: '青岛' }
                ]
            };
        },
        mounted() {
            this.getCaptcha();
        },
        methods:{
            toLogin(){
                this.$router.replace({path: '/login'});
            },
            validateBankCard() {
                // 只保留数字
                this.userInfo.bankCard = this.userInfo.bankCard.replace(/\D/g, '');
            },
            validateEmail(email) {
                const regex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
                return regex.test(email);
            },
            handleAddressChange(value) {
                console.log(value);
                this.userInfo.provinceName = value[0];
                this.userInfo.cityName = value[1];
                this.userInfo.regionName = value[2];
                // 设置城市字段为省份名称，保持与后端兼容性
                this.userInfo.city = value[0];
            },
            // 从服务器获取验证码
            getCaptcha() {
                this.$api.getCaptcha().then(res => {
                    if (res.status_code === 1) {
                        this.captchaId = res.data.captchaId;
                        this.captchaImage = res.data.captchaImage;
                    } else {
                        this.$message.error('获取验证码失败，请刷新页面重试');
                    }
                }).catch(err => {
                    console.error(err);
                    this.$message.error('获取验证码失败，请刷新页面重试');
                });
            },
            // 刷新验证码
            refreshCaptcha() {
                this.getCaptcha();
                this.captchaInput = '';
            },
            signIn(){
                console.log(this.userInfo.nickname);
                if(!this.userInfo.accountNumber || !this.userInfo.userPassword || !this.userInfo.nickname || 
                   !this.userInfo.email || !this.userInfo.provinceName || !this.userInfo.gender || !this.userInfo.bankCard){
                    this.$message.error('注册信息未填写完整！');
                    return;
                }
                
                if(!this.validateEmail(this.userInfo.email)) {
                    this.$message.error('邮箱格式不正确！');
                    return;
                }
                
                if(this.userInfo.bankCard.length !== 16) {
                    this.$message.error('银行卡号必须为16位数字！');
                    return;
                }
                
                if(this.userInfo.userPassword !== this.userPassword2){
                    this.$message.error('两次输入的密码不相同！');
                    return;
                }
                
                // 验证码验证
                if (!this.captchaInput) {
                    this.$message.error('请输入验证码！');
                    return;
                }
                
                // 创建注册提交对象
                const registerData = {
                    userModel: this.userInfo,
                    captchaId: this.captchaId,
                    captchaText: this.captchaInput
                };
                
                this.$api.signIn(registerData).then(res=>{
                    if(res.status_code===1){
                        this.$message({
                            message: '注册申请已提交，请等待管理员审核通过后再登录！',
                            type: 'success',
                            duration: 5000
                        });
                        this.$router.replace({path: '/login'});
                    } else if (res.status_code === 0 && res.msg) {
                        this.$message.error(res.msg);
                        
                        // 如果是验证码错误，刷新验证码
                        if (res.msg === '验证码错误' || res.msg === '验证码已过期') {
                            this.refreshCaptcha();
                        }
                    } else {
                        this.$message.error('注册失败，用户已存在！');
                    }
                }).catch(e=>{
                    console.log(e);
                    this.$message.error('注册失败，网络异常！');
                })
            }
        }
    }
</script>

<style scoped>
    .sign-in-container {
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

    .sign-in-body {
        padding: 30px;
        width: 300px;
        height: 100%;
    }

    .sign-in-title {
        padding-bottom: 30px;
        text-align: center;
        font-weight: 600;
        font-size: 20px;
        color: #409EFF;
    }

    .sign-in-input {
        margin-bottom: 20px;
    }
    .sign-in-submit{
        margin-top: 20px;
        display: flex;
        justify-content: center;
    }
    .login-container{
        padding: 0 10px;
    }
    .login-text{
        color: #409EFF;
        font-size: 16px;
        cursor:pointer;
    }
    .address-container-add-item {
        margin-bottom: 20px;
    }
    .demonstration {
        display: block;
        margin-bottom: 10px;
    }
    .captcha-container {
        display: flex;
        margin-bottom: 20px;
        align-items: center;
    }
    .captcha-input {
        flex: 1;
        margin-right: 10px;
        margin-bottom: 0;
    }
    .captcha-image {
        width: 120px;
        height: 40px;
        cursor: pointer;
        border: 1px solid #dcdfe6;
        border-radius: 4px;
        overflow: hidden;
    }
    .captcha-image canvas {
        width: 100%;
        height: 100%;
    }
    .loading-captcha {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 100%;
        height: 100%;
        background-color: #f0f0f0;
        color: #666;
        font-size: 12px;
    }
</style>