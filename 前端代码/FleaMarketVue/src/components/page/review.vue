<template>
  <div class="review-container">
    <div class="crumbs">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item>
          <i class="el-icon-tickets"></i> 订单评价
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    
    <div class="review-form-wrapper">
      <el-card>
        <div class="order-info">
          <h2>商品信息</h2>
          <div class="item-info" v-if="order">
            <img :src="$store.state.baseApi + (order.idleItem.pictureList ? JSON.parse(order.idleItem.pictureList)[0] : '')" alt="商品图片">
            <div class="item-details">
              <h3>{{ order.idleItem.idleName }}</h3>
              <p>价格: ¥{{ order.idleItem.idlePrice }}</p>
              <p>购买数量: {{ order.purchaseQuantity }}</p>
              <p>订单编号: {{ order.orderNumber }}</p>
              <p>下单时间: {{ formatTime(order.createTime) }}</p>
              <p>支付时间: {{ formatTime(order.paymentTime) }}</p>
            </div>
          </div>
        </div>

        <div class="review-form">
          <h2>评价商品</h2>
          <el-form :model="reviewForm" :rules="rules" ref="reviewForm" label-width="100px">
            <el-form-item label="评分" required>
              <el-rate
                v-model="reviewForm.rating"
                :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
                :texts="['非常差', '差', '一般', '好', '非常好']"
                show-text
              ></el-rate>
            </el-form-item>
            
            <el-form-item label="评价内容" prop="content">
              <el-input
                type="textarea"
                :rows="5"
                placeholder="请分享您对商品的使用体验"
                v-model="reviewForm.content">
              </el-input>
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="submitReview">提交评价</el-button>
              <el-button @click="goBack">返回</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      orderId: null,
      order: null,
      reviewForm: {
        orderId: null,
        idleId: null,
        rating: 5,
        content: ''
      },
      rules: {
        content: [
          { required: true, message: '请填写评价内容', trigger: 'blur' },
          { min: 5, max: 500, message: '长度在 5 到 500 个字符', trigger: 'blur' }
        ]
      },
      loading: false
    };
  },
  created() {
    this.orderId = this.$route.query.orderId;
    if (this.orderId) {
      this.getOrderInfo();
      this.checkReviewExists();
    } else {
      this.$message.error('订单ID不存在');
      this.goBack();
    }
  },
  methods: {
    // 获取订单信息
    getOrderInfo() {
      this.loading = true;
      this.$api.getOrder({
        id: this.orderId
      }).then(res => {
        if (res.status_code === 1) {
          this.order = res.data;
          this.reviewForm.orderId = this.orderId;
          // 保存商品ID以便评价使用
          this.reviewForm.idleId = this.order.idleId;
        } else {
          this.$message.error(res.msg || '获取订单信息失败');
          this.goBack();
        }
      }).catch(err => {
        console.error('获取订单信息失败', err);
        this.$message.error('订单获取失败');
      }).finally(() => {
        this.loading = false;
      });
    },
    
    // 检查是否已经评价过
    checkReviewExists() {
      this.$api.getIdleReviews({
        orderId: this.orderId
      }).then(res => {
        if (res.status_code === 1 && res.data && res.data.length > 0) {
          this.$message.warning('您已经评价过该订单');
          this.$router.push({ path: '/orders' });
        }
      }).catch(err => {
        console.error('检查评价失败', err);
      });
    },
    
    // 提交评价
    submitReview() {
      this.$refs.reviewForm.validate(valid => {
        if (valid) {
          this.loading = true;
          this.$api.addReview(this.reviewForm)
            .then(res => {
              if (res.status_code === 1) {
                this.$message.success('评价成功');
                this.$router.push({ path: '/me' });
              } else {
                this.$message.error(res.msg || '评价失败');
              }
            })
            .catch(err => {
              console.error('评价失败', err);
              this.$message.error('评价失败，请重试');
            })
            .finally(() => {
              this.loading = false;
            });
        }
      });
    },
    
    // 返回订单列表
    goBack() {
      this.$router.push({ path: '/me' });
    },
    
    // 格式化时间
    formatTime(time) {
      if (!time) return '无';
      return new Date(time).toLocaleString();
    }
  }
};
</script>

<style scoped>
.review-container {
  padding: 20px;
}

.review-form-wrapper {
  margin-top: 20px;
}

.order-info {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.item-info {
  display: flex;
  margin-top: 20px;
}

.item-info img {
  width: 100px;
  height: 100px;
  object-fit: cover;
  margin-right: 20px;
}

.item-details {
  flex: 1;
}

.item-details h3 {
  margin-top: 0;
  margin-bottom: 10px;
}

.item-details p {
  margin: 5px 0;
  color: #666;
}

.review-form h2 {
  margin-bottom: 20px;
}
</style> 