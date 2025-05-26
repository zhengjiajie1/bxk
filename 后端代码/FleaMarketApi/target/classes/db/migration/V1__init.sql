/*
 Navicat Premium Data Transfer

 Source Server         : Mysql8-Docker
 Source Server Type    : MySQL
 Source Server Version : 80100
 Source Host           : localhost:13306
 Source Schema         : 2022011100

 Target Server Type    : MySQL
 Target Server Version : 80100
 File Encoding         : 65001

 Date: 14/01/2025 18:07:34
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;


-- ----------------------------
-- Table structure for sh_admin
-- ----------------------------
DROP TABLE IF EXISTS `sh_admin`;
CREATE TABLE `sh_admin`  (
                             `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
                             `account_number` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '管理员账号',
                             `admin_password` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '密码',
                             `admin_name` varchar(8) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '管理员名字',
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE INDEX `account_number`(`account_number` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of sh_admin
-- ----------------------------
INSERT INTO `sh_admin` VALUES (1, 'admin', 'admin', '管理员');



-- ----------------------------
-- Table structure for sh_type
-- ----------------------------
DROP TABLE IF EXISTS `sh_type`;
CREATE TABLE `sh_type`  (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                            `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT '分类名称',
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin COMMENT = '类别表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sh_type
-- ----------------------------
INSERT INTO `sh_type` VALUES (1, '服饰首饰');
INSERT INTO `sh_type` VALUES (2, '家电家具');
INSERT INTO `sh_type` VALUES (3, '电子产品');
INSERT INTO `sh_type` VALUES (4, '图书音像');
INSERT INTO `sh_type` VALUES (5, '食品生鲜');
INSERT INTO `sh_type` VALUES (6, '运动户外');
INSERT INTO `sh_type` VALUES (7, '宠物花卉');
INSERT INTO `sh_type` VALUES (8, '其他');
INSERT INTO `sh_type` VALUES (9, '娱乐');



-- ----------------------------
-- Table structure for sh_user
-- ----------------------------
DROP TABLE IF EXISTS `sh_user`;
CREATE TABLE `sh_user`  (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
                            `account_number` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '账号（手机号）',
                            `user_password` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '登录密码',
                            `nickname` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '昵称',
                            `avatar` varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '头像',
                            `sign_in_time` datetime NOT NULL COMMENT '注册时间',
                            `user_status` tinyint NOT NULL DEFAULT 2 COMMENT '状态（0正常，1封禁，2待审核）',
                            `email` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '邮箱',
                            `city` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '城市',
                            `gender` varchar(8) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '男' COMMENT '性别',
                            `bank_card` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '银行卡号',
                            `bio` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '无' COMMENT '个人简介',
                            `account_balance` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '账户余额',
                            `consumer_points` int NOT NULL DEFAULT 0 COMMENT '消费积分',
                            `user_level` tinyint NOT NULL DEFAULT 5 COMMENT '用户等级',
                            PRIMARY KEY (`id`) USING BTREE,
                            UNIQUE INDEX `account_number`(`account_number` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of sh_user
-- ----------------------------
INSERT INTO `sh_user` VALUES (11, '13333333333', '123456', '北信科郑嘉杰', '/image?imageName=file1702122419646101007.jpg', '2025-04-08 13:18:34', 0, NULL, '北京', '男', NULL, '无', 100000.00, 0, 5);
INSERT INTO `sh_user` VALUES (12, '13555555555', '123456', '北信科李睿丰', '/image?imageName=noasndo123.jpg', '2025-04-09 11:43:04', 0, NULL, '北京', '男', NULL, '无', 100000.00, 0, 5);
INSERT INTO `sh_user` VALUES (13, '12345678910', '123456', 'user', '/image?imageName=noasndo123.jpg', '2025-03-13 06:13:54', 0, NULL, '北京', '男', NULL, '无', 100000.00, 0, 5);
INSERT INTO `sh_user` VALUES (14, '17638166573', '123456', '北信科派蒙', '/image?imageName=file170521137266510022.jpg', '2025-05-20 05:31:15', 0, NULL, '上海', '女', NULL, '无', 100000.00, 0, 5);
INSERT INTO `sh_user` VALUES (15, '18899965432', '123456', '例瑞峰', '/image?imageName=file17052248519481017OIP.jpg', '2025-05-20 08:56:07', 0, NULL, '北京', '女', NULL, '无', 100000.00, 0, 5);



-- ----------------------------
-- Table structure for sh_address
-- ----------------------------
DROP TABLE IF EXISTS `sh_address`;
CREATE TABLE `sh_address`  (
                               `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
                               `consignee_name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '收货人姓名',
                               `consignee_phone` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '收货人手机号',
                               `province_name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '省',
                               `city_name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '市',
                               `region_name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '区',
                               `detail_address` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '详细地址',
                               `default_flag` tinyint NOT NULL COMMENT '是否默认地址',
                               `user_id` bigint NOT NULL COMMENT '用户主键id',
                               PRIMARY KEY (`id`) USING BTREE,
                               INDEX `user_id_index`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 33 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of sh_address
-- ----------------------------
INSERT INTO `sh_address` VALUES (30, '北信科郑嘉杰', '13333333333', '北京市', '市辖区', '海淀区', '海淀公园', 1, 11);
INSERT INTO `sh_address` VALUES (31, '北信科李睿丰', '13555555555', '北京市', '市辖区', '朝阳区', '朝阳公园', 1, 12);
INSERT INTO `sh_address` VALUES (32, '北信科小白', '16235645236', '北京市', '市辖区', '东城区', '天安门广场', 1, 13);
INSERT INTO `sh_address` VALUES (33, '北信科小光光', '17638166572', '上海市', '市辖区', '闵行区', '华宝小区', 1, 14);
INSERT INTO `sh_address` VALUES (34, '北信科小美', '17638177654', '北京市', '市辖区', '东城区', '翻斗花园101号', 1, 15);



-- ----------------------------
-- Table structure for sh_recharge_record
-- ----------------------------
DROP TABLE IF EXISTS `sh_recharge_record`;
CREATE TABLE `sh_recharge_record` (
                                      `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '充值记录ID',
                                      `user_id` bigint(20) NOT NULL COMMENT '用户ID',
                                      `amount` decimal(10,2) NOT NULL COMMENT '充值金额',
                                      `recharge_time` datetime NOT NULL COMMENT '充值时间',
                                      `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '充值状态: 0-处理中，1-成功',
                                      PRIMARY KEY (`id`),
                                      KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户充值记录表';



-- ----------------------------
-- Table structure for sh_idle_item
-- ----------------------------
DROP TABLE IF EXISTS `sh_idle_item`;
CREATE TABLE `sh_idle_item`  (
                                 `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
                                 `idle_name` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '闲置物名称',
                                 `idle_details` varchar(2048) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '详情',
                                 `picture_list` varchar(1024) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '图集',
                                 `idle_price` decimal(10, 2) NOT NULL COMMENT '价格',
                                 `idle_place` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '发货地区',
                                 `idle_label` int NOT NULL COMMENT '分类标签',
                                 `release_time` datetime NOT NULL COMMENT '发布时间',
                                 `idle_status` tinyint NOT NULL COMMENT '状态（上架1、下架2、删除0）',
                                 `user_id` bigint NOT NULL COMMENT '用户主键id',
                                 `idle_stock` int NOT NULL DEFAULT 1 COMMENT '商品库存数量',
                                 `sold_count` int NOT NULL DEFAULT 0 COMMENT '历史销量',
                                 `avg_rating` decimal(3,2) DEFAULT NULL COMMENT '平均评分',
                                 `review_count` int DEFAULT 0 COMMENT '评价数量',
                                 PRIMARY KEY (`id`) USING BTREE,
                                 INDEX `user_id_index`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 116 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of sh_idle_item
-- ----------------------------
INSERT INTO `sh_idle_item` VALUES (105, '穿过的短裙', '这是一件时尚轻便的单品，适合多种场合的穿着。它采用优质舒适的面料制成，穿着舒适，给人轻松自在的感觉。设计简约大方，腰部设计修身显瘦，展现出优美的曲线。这款短裙的长度适中，既不会显得过于暴露，也不会过于保守，非常适合日常穿着。它的风格多样，适合搭配各种上衣，可以轻松打造出属于你自己的时尚造型。不论是约会、聚会还是日常休闲，都是不错的选择。总之，这条二手短裙不仅具有实用性，而且能够展现你的气质与品味。希望它能够成为新主人的新宠，为她带来更多的美丽时刻。', '[\"/image?imageName=file1702111254837100201.jpg\"]', 60.00, '市辖区', 1, '2025-12-09 08:42:29', 1, 11, 10, 0, NULL, 0);
INSERT INTO `sh_idle_item` VALUES (106, '闲置桌椅套装', '这套桌椅绝对是你家居空间的"大件"，不仅仅是为了添置家具，更是为了打造一个舒适时尚的居家环境。这套桌椅采用高品质原木制作，坚固耐用，同时也展现出自然清新的风格。桌面宽敞，椅子设计人性化，坐起来很舒服，背部不易疲劳。它们在家庭聚会、办公学习时都能大显身手。无论是用于工作、用餐还是休闲娱乐，都是非常实用的选择。这套桌椅的设计不仅时尚个性，而且简约大方，无论是现代、北欧或者田园风格的家庭都能搭配得恰到好处。当然，更重要的是，它将会成为你家中的一部分，见证你和家人在其中共度的美好时光。愿它找到一个全新的归宿，成为新家的焦点和温馨的象征。', '[\"/image?imageName=file1702117095675100203.jpg\"]', 199.00, '石家庄市', 2, '2025-12-09 10:18:31', 2, 11, 1, 0, NULL, 0);
INSERT INTO `sh_idle_item` VALUES (107, '运动鞋', '这双鞋可不是一般的潮流单品，而是诠释着你个人品味与生活态度的体现。它采用高品质的材料制成，注重舒适度的同时也不失时尚感，无论是走路还是跑步，都能确保你的脚部得到良好的支撑和舒适的体验。其设计简约大方，彰显了大胆、时尚、个性的魅力。这双鞋不仅仅是一双鞋，更是你和时尚生活的连接点。它能很好地搭配各种穿搭，无论是运动风格还是休闲搭配，都能展现出独特的魅力。无论是周末约会还是平日出行，都能成为你的得力助手。它诚意满满，希望它能找到一个新主人，为他带来更多出彩的精彩时刻。让我们一起期待它在新的旅程中，闪闪发光。', '[\"/image?imageName=file1702117196351100304.jpg\"]', 88.00, '石家庄市', 1, '2025-12-09 10:21:34', 2, 11, 1, 0, NULL, 0);
INSERT INTO `sh_idle_item` VALUES (108, '《书》', '这本书虽然已经历了一些风雨，但依旧保持着鲜活的生命力。它曾被人阅读过、翻看过，每一页都沉淀着一段时光，记录着无数个思想的火花。即便已经七分新，但它内心依旧充满活力。它的纸张虽有些泛黄，但更显出岁月的韵味。随着每一页的翻阅，你可以感受到前人的智慧，体味到知识的厚重。这本书不仅是一本书，更像是一部历史的注脚，见证了无数人的成长与坚持。它所传递的温暖和智慧将会伴随你度过更多美好的时光，希望它能找到一个懂得珍惜的新主人，陪伴他探索更广阔的世界，开启新的故事。愿这本书在新的归宿中，继续闪耀光芒。', '[\"/image?imageName=file1702117328116100405.jpg\"]', 20.00, '唐山市', 4, '2025-12-09 10:24:08', 1, 11, 102, 0, NULL, 0);
INSERT INTO `sh_idle_item` VALUES (109, '高级红酒', '这瓶酒可不是一般的酒，它蕴含着无数酿酒师的心血和智慧，散发着浓厚的历史和传奇。每一滴酒都如同一个故事，传承着世代的匠心和悠久的文化。它曾陪伴着人们共度欢乐时光，见证着无数个美好的瞬间。或许它已有一些年头，但这只是岁月的痕迹，酒中散发出的芳香和回味却始终如一。它是品质与品味的结晶，更是友情与珍藏的象征。随着每一次开启，你都能品味到不同年份的记忆，感受到不同口味的惊喜。这瓶酒犹如老友般值得信赖，它希望找到一个懂得品味生活、热爱生活的新主人，与他共同分享更多精彩的故事。相信它定能为你的美好生活再添一丝色彩。', '[\"/image?imageName=file1702117551063100506.jpg\"]', 2100.00, '唐山市', 5, '2025-12-09 10:26:50', 1, 11, 33, 0, NULL, 0);
INSERT INTO `sh_idle_item` VALUES (110, '曼巴背心', '前美国职业男子篮球联赛（NBA）洛杉矶湖人队的球星科比·布莱恩特有个绰号叫“黑曼巴”，那是非洲草原上毒性最烈的一种蛇，而科比的精神，也被称为“曼巴精神”，“永不言弃”是科比自己给予这种精神的定义。总的来说，曼巴精神，就是从不退却，从不放弃，从不逃遁，忍辱负重，在困难中创造奇迹。', '[\"/image?imageName=file1702117633184100602.jpg\"]', 30.00, '秦皇岛市', 1, '2025-12-09 10:27:53', 1, 11, 1, 0, NULL, 0);
INSERT INTO `sh_idle_item` VALUES (111, '二手游戏碟片', '这张游戏碟片就像是一个时间胶囊，里面记录着无数个燃烧的游戏时光。即便已经流出了一些时间，但它依旧散发出强大的游戏魅力。每一次插入主机，都像是打开了一个充满冒险和挑战的大门，期待着再次开启那段激情燃烧的游戏旅程。尽管碟片可能略显磨损，但这都是岁月的印记，印证着它陪伴游戏玩家成长的经历。它承载着无数个惊喜与回忆，如今它渴望找到一个合适的新主人，重新点燃游戏热情。或许它会成为你游戏生涯中的一部分，见证你在游戏世界中的成长，陪伴你度过更多激动人心的冒险。愿这张碟片在新的归宿中，继续传播着游戏的独特魅力，带来更多欢乐和挑战。', '[\"/image?imageName=file1702117790448100704.jpg\"]', 21.00, '邯郸市', 3, '2025-12-09 10:30:42', 2, 11, 1, 0, NULL, 0);
INSERT INTO `sh_idle_item` VALUES (112, '《幻想城》影片', '这张《幻想城》电影影片可不一般，它记录了无数观众在银幕前的热血沸腾和泪流满面。这些影像承载着无数个观众的情感与记忆，每一个场景都是如此熟悉而动人。或许影片已经有一段时光了，但它仍保留着那份珍贵与难忘。影片中的情节、对白、甚至音乐，都是那样的深入人心。它见证了无数个观众的成长与梦想，留下了无数个感动和情感的交融。如今，它渴望找到一个新主人，或者一个收藏者，重新点燃观影的激情，让更多人领略电影的魅力。或许它能成为你影视收藏的一部分，陪伴你度过更多美好的观影时光，每一次播放都唤起一段段情感记忆。愿它在新的归宿中，依旧闪耀着电影的光芒，带来更多的观影情感。', '[\"/image?imageName=file1702122206452100801.jpg\"]', 60.00, '秦皇岛市', 4, '2025-12-09 11:44:29', 2, 12, 1, 0, NULL, 0);
INSERT INTO `sh_idle_item` VALUES (113, '爱宠哈士奇', '西伯利亚雪橇犬（拉丁学名：Canis lupus familiaris，俄语：Сибирский хаски，英语：Siberian Husky），别称为哈士奇（哈士奇名字是源自其独特的嘶哑叫声。），昵称二哈，犬科犬属动物，中型犬，原始的古老犬种。雪橇犬有八种，分别是阿拉斯加雪橇犬、哈士奇、萨摩耶、格陵兰犬、加拿大爱斯基摩犬、奇努克犬、北因努伊特犬和桦太犬[1]。该犬脚步轻快，动作优美，身体紧凑，有着很厚的被毛，耳朵直立，尾如毛刷，显示出北方地区的遗传特征。该犬是和狼的血统非常近的犬种，所以外形非常的像狼，有着比大多数犬种都要厚的毛发。，西伯利亚雪橇犬（哈士奇）生性好群居，但在群体中有着明显的等级制度。西伯利亚雪橇犬主要生活在在西伯利亚东北部、格陵兰南部。由于西伯利亚雪橇犬良好的性格和优雅的外形特点，使它们成为了广受欢迎的家庭宠物和用于比赛展示的犬种，分布于世界各地。同时，作为工作犬，它们常用于雪橇比赛、物流运输和极地考察', '[\"/image?imageName=file1702122292774100902.jpg\"]', 360.00, '唐山市', 7, '2025-12-09 11:46:17', 1, 12, 8, 0, NULL, 0);
INSERT INTO `sh_idle_item` VALUES (114, '《黑色幽默》', '《黑色幽默》是一部真正经典的电影，兼具悬疑、幽默和社会讽刺元素。影片以独特的叙事风格讲述了一系列匪夷所思的故事，让人捧腹不已，同时引人深思。\n\n影片中，深刻而丰富的角色刻画，以及令人意想不到的情节发展，使这部影片成为不太寻常的电影佳作。观众跟随着角色一同踏上一段独特、离奇而又充满戏剧性的旅程。这部影片以其黑色幽默感，勾勒出一幅荒诞不经但又异常真实的社会画卷。\n\n无论是影片的幽默元素、出人意料的转折，还是对人性和社会的深刻思考，都让《黑色幽默》成为了影迷心中的经典之作。希望它将会为新的主人带来乐趣和启发，成为您收藏中的一份珍贵。', '[\"/image?imageName=file1702129711240101105.jpg\"]', 60.00, '秦皇岛市', 4, '2025-12-09 13:51:52', 2, 12, 1, 0, NULL, 0);
INSERT INTO `sh_idle_item` VALUES (116, '宠物东北虎', '东北虎（学名：Panthera tigris ssp. altaica）：是猫科、豹属动物。是虎的亚种之一。是体重最大的猫科动物，成年东北虎雄性体重平均为250千克，头体长约为2.3米；成年母虎平均体重约为170千克，体长约为2米，肩高1.1米左右，尾长1.3米左右。最大身长可达2.9米（含尾长） [1]。该物种体色夏毛棕黄色，冬毛淡黄色。背部和体侧具有多条横列黑色窄条纹，通常2条靠近呈柳叶状。头大而圆，前额上的数条黑色横纹，中间常被串通，极似“王”字，故有“丛林之王”之美称。东北虎的牙齿较强大，一般为30个。4个非常锐利的虎爪使用时伸出，不用时缩回爪鞘避免行走时摩擦地面。', '[\"/image?imageName=file1705210345877100220230422002996.jpg\"]', 999.00, '沈阳市', 7, '2025-05-20 05:32:28', 1, 14, 31, 0, NULL, 0);
INSERT INTO `sh_idle_item` VALUES (117, '阿凡达电影院票', '十多年过去了，曾经的截瘫的前海军士兵 杰克·萨利 （ 萨姆·沃辛顿 饰）永久变身为拥有地球人基因和纳威人基因的“阿凡达”，并与奥马蒂卡亚部落的族长之女妮特丽 （ 佐伊·索尔达娜 饰）结为夫妇。杰克已经完全适应了他的新身体和族长的身份，并和奈蒂莉有了三个孩子，还收养了格蕾丝留下的女儿 琪莉 （ 西格妮·韦弗 饰）。四个孩子里，老大 纳特亚姆 （ 杰米·福雷特斯 饰）最为懂事听话，也很会照顾弟弟妹妹；老二琪莉 （ 西格妮·韦弗 饰）因为不知道自己的父亲是谁，有些敏感；老三洛克 （ 布里坦·道尔顿 饰）酷爱冒险，是家里的闯祸精；最小的妹妹图克 （特里尼蒂·布利斯 饰）总是黏着哥哥姐姐们。四人跟人类留下的孤儿“蜘蛛” （ 杰克·尚皮永 饰）是形影不离的好朋友。原本，将地球人赶走后，杰克一家与族人过上了田园牧歌般的生活。然而，原本死去的迈尔斯·夸里奇上校 （ 史蒂芬·朗 饰）化身阿凡达重新归来，一心要找杰克和奈蒂莉复仇。为了族人的安危，也为了保护孩子，杰克决定带着家人离开，前往不易被找到的由罗娜尔 （ 凯特·温斯莱特 饰）和 特诺瓦里 （ 克利夫·柯蒂斯 饰）领导的海洋部落，展开全新的生活。正当一家人经过重重磨合，终于适应以水构筑的新家园时，新的危机也在慢慢靠近 。。', '[\"/image?imageName=file17052223681301003.png\",\"/image?imageName=file1705222381626100420240101170113.png\",\"/image?imageName=file1705222391103100520240101170957.png\",\"/image?imageName=file1705222391104100620240101170847.png\"]', 9.90, '市辖区', 9, '2025-05-20 08:53:20', 1, 14, 72, 0, NULL, 0);
INSERT INTO `sh_idle_item` VALUES (118, '100%中奖刮刮乐', '刮刮乐（Scratch Tickets）是一种网点即开型福利彩票[1]，由中国福利彩票发行[2]。刮刮乐以“扶老、助残、救孤、济困”为发行目的，即买即开，包含多种形式[1]。刮开彩票“兑奖刮开区”，凡出现符合与彩票游戏规则中已确定的中奖符号相同的彩票，即为中奖彩票，可兑付与中奖符号相对应的等额奖金。', '[\"/image?imageName=file17052231051221007.png\",\"/image?imageName=file17052231121911008202401131850458.png\"]', 19.90, '市辖区', 9, '2025-05-20 09:05:14', 1, 15, 7, 0, NULL, 0);
INSERT INTO `sh_idle_item` VALUES (119, 'Apple/苹果 iPhone 39全网通5G手机', '快递: 免运费\n现货，现在付款，48小时内发货', '[\"/image?imageName=file17052237045931009Snipaste.png\",\"/image?imageName=file17052237045961010Snipaste.png\"]', 9999.00, '市辖区', 3, '2025-05-20 09:15:06', 2, 15, 1, 0, NULL, 0);
INSERT INTO `sh_idle_item` VALUES (120, '书桌', '一体带书柜简约家用卧室初中学生学习写字桌子台式电脑桌', '[\"/image?imageName=file17052238267951011Snipaste.png\",\"/image?imageName=file17052238267951012Snipaste.png\"]', 129.80, '秦皇岛市', 2, '2025-05-20 09:17:09', 1, 14, 67, 0, NULL, 0);
INSERT INTO `sh_idle_item` VALUES (121, '野餐垫帐篷防潮垫', '帐篷（音：zhàng peng）是撑在地上遮蔽风雨﹑日光并供临时居住的棚子。多用帆布做成，连同支撑用的东西，可随时拆下转移。帐篷是以部件的方式携带，到达现场后才加以组装，所以，需要各种部件和工具。了解各个部件的名称和使用方法，熟悉帐篷的构造，才能快速、方便地搭起帐篷。', '[\"/image?imageName=file17052239624641013Snipaste.png\",\"/image?imageName=file17052239624671014Snipaste.png\"]', 78.90, '鹤岗市', 6, '2025-05-20 09:19:24', 1, 14, 99, 0, NULL, 0);
INSERT INTO `sh_idle_item` VALUES (122, '原石', '原石，是游戏《原神》中的抽卡道具，主要用于游戏角色、武器的抽卡，也可用作补充原粹树脂。', '[\"/image?imageName=file17052240463761016ITSource2.jpg\",\"/image?imageName=file17052240463761015.png\"]', 9.90, '市辖区', 8, '2025-05-20 09:20:57', 1, 14, 1, 0, NULL, 0);
INSERT INTO `sh_idle_item` VALUES (123, ' iPhone 19 Pro Max', '网络类型：5G全网通品牌：Apple/苹果Apple型号：iPhone 15 Pro Max\n售后服务：全国联保接口类型：Type-C分辨率：2796x1290\n蓝牙版本：5.3机身颜色：白色钛金属 蓝色钛金属 黑色钛金属 原色钛金属套餐类型：官方标配\n后置摄像头：4800万存储容量：256GB 512GB 1TB屏幕材质：超视网膜 XDR 显示屏\n手机类型：拍照手机摄像头类型：三摄像头（后双）网络模式：5G\n电信设备进网许可证编号：00-8573-228778屏幕刷新率：120Hz操作系统：iOS\nCPU品牌：Apple/苹果上市时间：2025-09屏幕尺寸：6.7英寸\n电池容量：3877mAh机身厚度：8.25mmCPU型号：A17 Pro 芯片\n是否支持无线充电：是充电功率：20W保修期：12个月\n前置摄像头像素：12百万像素商品系列：iPhone 15 Pro Max3C证书编号：2022011606490410', '[\"/image?imageName=file17052259070171018Snipaste.png\"]', 7899.00, '郑州市', 3, '2025-05-20 09:52:23', 2, 14, 1, 0, NULL, 0);



-- ----------------------------
-- Table structure for sh_order
-- ----------------------------
DROP TABLE IF EXISTS `sh_order`;
CREATE TABLE `sh_order`  (
                             `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
                             `order_number` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '订单编号',
                             `user_id` bigint NOT NULL COMMENT '用户主键id',
                             `idle_id` bigint NOT NULL COMMENT '闲置物品主键id',
                             `order_price` decimal(10, 2) NOT NULL COMMENT '订单总价',
                             `payment_status` tinyint NOT NULL COMMENT '支付状态',
                             `payment_way` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '支付方式',
                             `create_time` datetime NOT NULL COMMENT '创建时间',
                             `payment_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
                             `order_status` tinyint NOT NULL COMMENT '订单状态',
                             `is_deleted` tinyint NULL DEFAULT NULL COMMENT '是否删除',
                             `purchase_quantity` int NOT NULL DEFAULT 1 COMMENT '购买数量',
                             `service_fee` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '服务费',
                             `service_fee_rate` decimal(5, 3) NOT NULL DEFAULT 0.000 COMMENT '服务费费率',
                             `seller_amount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '卖家实收金额',
                             `points_used` int NOT NULL DEFAULT 0 COMMENT '使用的积分数量',
                             `points_discount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '积分抵扣金额',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 94 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of sh_order
-- ----------------------------
INSERT INTO `sh_order` VALUES (86, '170212944205710002', 12, 107, 88.00, 1, '账户余额', '2025-12-09 13:44:02', '2025-12-09 13:45:18', 3, NULL, 1, 0.09, 0.001, 87.91, 0, 0.00);
INSERT INTO `sh_order` VALUES (87, '170213014337110003', 11, 114, 60.00, 1, '账户余额', '2025-12-09 13:55:43', '2025-12-09 13:55:57', 3, NULL, 1, 0.06, 0.001, 59.94, 0, 0.00);
INSERT INTO `sh_order` VALUES (88, '170512649457710002', 13, 113, 360.00, 0, NULL, '2025-04-13 06:14:55', NULL, 4, NULL, 1, 0.36, 0.001, 359.64, 0, 0.00);
INSERT INTO `sh_order` VALUES (89, '170512654603410003', 13, 111, 21.00, 1, '账户余额', '2025-04-13 06:15:46', '2025-04-13 06:15:50', 1, NULL, 1, 0.02, 0.001, 20.98, 0, 0.00);
INSERT INTO `sh_order` VALUES (90, '170513032359410004', 13, 113, 360.00, 0, NULL, '2025-04-13 07:18:44', NULL, 4, NULL, 1, 0.36, 0.001, 359.64, 0, 0.00);
INSERT INTO `sh_order` VALUES (91, '170513033302310005', 13, 110, 30.00, 0, NULL, '2025-04-13 07:18:53', NULL, 4, NULL, 1, 0.03, 0.001, 29.97, 0, 0.00);
INSERT INTO `sh_order` VALUES (92, '170513055192310006', 13, 108, 20.00, 0, NULL, '2025-04-13 07:22:32', NULL, 4, NULL, 1, 0.02, 0.001, 19.98, 0, 0.00);
INSERT INTO `sh_order` VALUES (93, '170513057404410007', 13, 106, 199.00, 1, '账户余额', '2025-03-13 07:22:54', '2025-03-13 07:22:59', 1, NULL, 1, 0.20, 0.001, 198.80, 0, 0.00);
INSERT INTO `sh_order` VALUES (94, '170522469188210002', 14, 119, 9999.00, 1, '账户余额', '2025-05-20 09:31:32', '2025-05-20 09:33:02', 2, NULL, 1, 10.00, 0.001, 9989.00, 0, 0.00);
INSERT INTO `sh_order` VALUES (95, '170522603880110003', 15, 123, 7899.00, 1, '账户余额', '2025-05-20 09:53:59', '2025-05-20 09:55:56', 1, NULL, 1, 7.90, 0.001, 7891.10, 0, 0.00);



-- ----------------------------
-- Table structure for sh_order_address
-- ----------------------------
DROP TABLE IF EXISTS `sh_order_address`;
CREATE TABLE `sh_order_address`  (
                                     `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
                                     `order_id` bigint NOT NULL COMMENT '订单id',
                                     `consignee_name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '收货人',
                                     `consignee_phone` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '电话',
                                     `detail_address` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '收货地址',
                                     PRIMARY KEY (`id`) USING BTREE,
                                     UNIQUE INDEX `orderId`(`order_id` ASC) USING BTREE,
                                     INDEX `order_id_index`(`order_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 31 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of sh_order_address
-- ----------------------------
INSERT INTO `sh_order_address` VALUES (23, 86, '北信科李睿丰', '13555555555', '北京市市辖区朝阳区朝阳公园');
INSERT INTO `sh_order_address` VALUES (24, 87, '北信科郑嘉杰', '13333333333', '北京市市辖区海淀区海淀公园');
INSERT INTO `sh_order_address` VALUES (25, 89, '北信科小白', '16235645236', '北京市市辖区东城区天安门广场');
INSERT INTO `sh_order_address` VALUES (26, 88, '北信科小白', '16235645236', '北京市市辖区东城区天安门广场');
INSERT INTO `sh_order_address` VALUES (27, 90, '北信科小白', '16235645236', '北京市市辖区东城区天安门广场');
INSERT INTO `sh_order_address` VALUES (28, 91, '北信科小白', '16235645236', '北京市市辖区东城区天安门广场');
INSERT INTO `sh_order_address` VALUES (29, 92, '北信科小白', '16235645236', '北京市市辖区东城区天安门广场');
INSERT INTO `sh_order_address` VALUES (30, 93, '北信科小白', '16235645236', '北京市市辖区东城区天安门广场');
INSERT INTO `sh_order_address` VALUES (31, 94, '北信科小光光', '17638166572', '上海市市辖区闵行区华宝小区');
INSERT INTO `sh_order_address` VALUES (32, 95, '例瑞峰', '17638177654', '北京市市辖区东城区翻斗花园101号');



-- ----------------------------
-- Table structure for sh_review
-- ----------------------------
DROP TABLE IF EXISTS `sh_review`;
CREATE TABLE `sh_review`  (
                              `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价ID',
                              `order_id` bigint NOT NULL COMMENT '订单ID',
                              `user_id` bigint NOT NULL COMMENT '评价用户ID(买家)',
                              `idle_id` bigint NOT NULL COMMENT '商品ID',
                              `rating` int NOT NULL COMMENT '评分(0-5分)',
                              `content` varchar(512) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '评价内容',
                              `create_time` datetime NOT NULL COMMENT '评价时间',
                              `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态(1:正常,0:违规被隐藏)',
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `order_id_index` (`order_id`),
                              KEY `idle_id_index` (`idle_id`),
                              KEY `user_id_index` (`user_id`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '评价表' ROW_FORMAT = COMPACT;

SET FOREIGN_KEY_CHECKS = 1;





