package com.fengda.app.api;


import com.fengda.app.bean.Address;
import com.fengda.app.bean.AreaBean;
import com.fengda.app.bean.AssembkungInfo;
import com.fengda.app.bean.AssemblingBase;
import com.fengda.app.bean.AssemblingPayBean;
import com.fengda.app.bean.BankInfo;
import com.fengda.app.bean.BannerBase;
import com.fengda.app.bean.BaseResponse;
import com.fengda.app.bean.BindCard;
import com.fengda.app.bean.CartGoodsBase;
import com.fengda.app.bean.Catergory;
import com.fengda.app.bean.CityBean;
import com.fengda.app.bean.DeclarationBean;
import com.fengda.app.bean.ExchangeBean;
import com.fengda.app.bean.ExchangeInfo;
import com.fengda.app.bean.FavoriteShop;
import com.fengda.app.bean.FruitInfo;
import com.fengda.app.bean.OrderCartBase;
import com.fengda.app.bean.OrderDetals;
import com.fengda.app.bean.OrderInfo;
import com.fengda.app.bean.OrderMailInfo;
import com.fengda.app.bean.OrderPay;
import com.fengda.app.bean.ParentCatergory;
import com.fengda.app.bean.PayMethod;
import com.fengda.app.bean.PayReturnInfo;
import com.fengda.app.bean.PersonAuth;
import com.fengda.app.bean.PomeloBuyBean;
import com.fengda.app.bean.ProvinceBean;
import com.fengda.app.bean.RecomFriendBase;
import com.fengda.app.bean.RecommendInfo;
import com.fengda.app.bean.Shipping;
import com.fengda.app.bean.UserBean;
import com.fengda.app.bean.WisdomOrderDetals;
import com.fengda.app.bean.WxPayResult;
import com.fengda.app.model.AmountDetail;
import com.fengda.app.model.GoodsBean;
import com.fengda.app.model.IntegralPoint;
import com.fengda.app.model.Inventory;
import com.fengda.app.model.InventoryDetails;
import com.fengda.app.model.Message;
import com.fengda.app.model.MessageBase;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface APIService {

    /**
     * 登陆
     */
    @GET("login/validateCredentials")
    Call<BaseResponse<String>> loginRepo(@Header("X-AUTH-TOKEN") String token);


    /**
     * 注册
     */
    @POST("registerUser/doRegister")
    @FormUrlEncoded
    Call<BaseResponse<String>> registerRepo(@Field("mobile") String mobile, @Field("vcode") String vcode, @Field("password") String password, @Field("referer") String referer);


    /**
     * 注册:获取验证码
     */
    @POST("registerUser/sendSMS")
    @FormUrlEncoded
    Call<BaseResponse<String>> sendSMS(@Field("mobile") String mobile);

    /**
     * 忘记密码:获取验证码
     */
    @POST("common/getCode")
    @FormUrlEncoded
    Call<BaseResponse<String>> getCode(@Field("mobile") String mobile);

    /**
     * 忘记密码:验证验证码(type:1修改登录密码:4支付密码;3修改、绑定银行卡验证)
     */
    @POST("common/verifyCode")
    @FormUrlEncoded
    Call<BaseResponse<Object>> checkCode(@Field("mobile") String mobile, @Field("vtype") String vtype, @Field("code") String code);
    /**
     * 忘记密码:验证验证码(忘记登录密码)
     */
    @POST("forgetSecret/verifyCode")
    @FormUrlEncoded
    Call<BaseResponse<Object>> checkForgetCode(@Field("mobile") String mobile, @Field("code") String code);
    /**
     * 忘记密码:修改密码
     */
    @POST("forgetSecret/alterAccountPwd")
    @FormUrlEncoded
    Call<BaseResponse<Object>> resetPwd(@Field("mobile") String mobile, @Field("code") String code, @Field("password") String password);


    /**
     * 支付密码:获取验证码
     */
    @POST("api/sendSMSForPwd")
    @FormUrlEncoded
    Call<BaseResponse<Object>> sendSMSForPwd(@Header("X-AUTH-TOKEN") String token, @Field("mobile") String mobile);


    /**
     * 支付密码:修改、设置支付密码
     */
    @POST("api/alterPayPwd")
    @FormUrlEncoded
    Call<BaseResponse<Object>> resetPayPwd(@Header("X-AUTH-TOKEN") String token, @Field("mobile") String mobile, @Field("code") String code, @Field("password") String password);

    /**
     * 鼓励积分兑换记录
     *
     * @param token
     * @return
     */
    @POST("api/zphExchangeLog")
    Call<BaseResponse<List<ExchangeBean>>> getExchangeList(@Header("X-AUTH-TOKEN") String token);

    /**
     * 兑换产果记录
     *
     * @param token
     * @return
     */
    @POST("api/returnExchangeLogList")
    Call<BaseResponse<List<ExchangeBean>>> returnExchangeLogList(@Header("X-AUTH-TOKEN") String token);

    /**
     * 转让产果记录
     *
     * @param token
     * @return
     */
    @POST("api/transferReturnLogList")
    Call<BaseResponse<List<ExchangeBean>>> transferReturnLogList(@Header("X-AUTH-TOKEN") String token);

    /**
     * 复投产果记录
     *
     * @param token
     * @return
     */
    @POST("api/recastReturnLogList")
    Call<BaseResponse<List<ExchangeBean>>> recastReturnLogList(@Header("X-AUTH-TOKEN") String token);
    /**
     * 吉优美兑换记录
     *
     * @param token
     * @return
     */
    @POST("api/jiYouMeiExchangeLogList")
    Call<BaseResponse<List<ExchangeBean>>> jiYouMeiExchangeLogList(@Header("X-AUTH-TOKEN") String token);

    /**
     * 购买金柚计划记录
     *
     * @param token
     * @return
     */
    @POST("api/jinYouLogList")
    Call<BaseResponse<List<PomeloBuyBean>>> jinYouLogList(@Header("X-AUTH-TOKEN") String token);


    /**
     * 待产果记录
     *
     * @param token
     * @return
     */
    @POST("api/ExpectantLogList")
    Call<BaseResponse<List<FruitInfo>>> expectantLogList(@Header("X-AUTH-TOKEN") String token);


    /**
     * 产果记录
     *
     * @param token
     * @return
     */
    @POST("api/FruitLogList")
    Call<BaseResponse<List<FruitInfo>>> fruitLogList(@Header("X-AUTH-TOKEN") String token);
    /**
     * 产果记录
     *
     * @param token
     * @return
     */
    @POST("api/consumptionLogList")
    Call<BaseResponse<List<FruitInfo>>> consumptionLogList(@Header("X-AUTH-TOKEN") String token);
    /**
     * 吉优美记录
     *
     * @param token
     * @return
     */
    @POST("api/JiYouMeiLogList")
    Call<BaseResponse<List<FruitInfo>>> JiYouMeiLogList(@Header("X-AUTH-TOKEN") String token);


    /**
     * 用户信息
     *
     * @param token
     * @return
     */
    @POST("api/userInformation")
    Call<BaseResponse<List<UserBean>>> getUserInformation(@Header("X-AUTH-TOKEN") String token);

    /**
     * 金柚计划列表
     *
     * @param
     * @return
     */
    @POST("common/jinyouActivity")
    Call<BaseResponse<List<AssemblingBase>>> jinyouActivity();
    /**
     * 是否设置了支付密码
     *
     * @param token
     * @return
     */
    @POST("api/getSecret")
    Call<BaseResponse<Object>> getSecret(@Header("X-AUTH-TOKEN") String token);

    /**
     * 兑换（转让，复投）页面数据
     *
     * @param token
     * @return
     */
    @POST("api/ExchangeInfo")
    Call<BaseResponse<List<ExchangeInfo>>> exchangeInfo(@Header("X-AUTH-TOKEN") String token);

    /**
     * 金柚计划页面数据
     *
     * @param token
     * @return
     */
    @POST("api/jinYouInfo")
    @FormUrlEncoded
    Call<BaseResponse<List<AssembkungInfo>>> jinYouInfo(@Header("X-AUTH-TOKEN") String token, @Field("activity_id") String activity_id);

    /**
     * 货款兑换页面数据
     *
     * @param token
     * @return
     */
    @POST("api/getExchangeInfoSurplus")
    Call<BaseResponse<List<ExchangeInfo>>> getExchangeInfoSurplus(@Header("X-AUTH-TOKEN") String token);


    /**
     * 收货地址列表
     *
     * @param token
     * @return
     */
    @POST("api/deliveryAdrList")
    Call<BaseResponse<List<Address>>> getAcceptAddressRepo(@Header("X-AUTH-TOKEN") String token);


    /**
     * 删除收货地址
     *
     * @param token
     * @param addr_id
     * @return
     */
    @POST("api/deliveryAdrDel")
    @FormUrlEncoded
    Call<BaseResponse<Object>> deliveryAdrDel(@Header("X-AUTH-TOKEN") String token, @Field("addr_id") String addr_id);

    /**
     * 设置默认地址
     *
     * @param token
     * @param addr_id
     * @return
     */
    @POST("api/deliveryAdrDefault")
    @FormUrlEncoded
    Call<BaseResponse<Object>> deliveryAdrDefault(@Header("X-AUTH-TOKEN") String token, @Field("addr_id") String addr_id);


    /**
     * 修改收货地址
     *
     * @param params
     * @return
     */
    @POST("api/deliveryAdrEdit")
    @FormUrlEncoded
    Call<BaseResponse<Object>> updateAddrRepo(@Header("X-AUTH-TOKEN") String token, @FieldMap Map<String, String> params);


    /**
     * 新增收货地址
     *
     * @param params
     * @return
     */
    @POST("api/deliveryAdrAdd")
    @FormUrlEncoded
    Call<BaseResponse<Object>> addAddrRepo(@Header("X-AUTH-TOKEN") String token, @FieldMap Map<String, String> params);


    /**
     * 获取省信息
     */
    @POST("common/getProviceList")
    Call<BaseResponse<List<ProvinceBean>>> getProivinceList();


    /**
     * 获取市信息
     */
    @POST("common/getCityList")
    @FormUrlEncoded
    Call<BaseResponse<List<CityBean>>> getCityList(@Field("region_id") String region_id);


    /**
     * 获取区信息
     */
    @POST("common/getRegionList")
    @FormUrlEncoded
    Call<BaseResponse<List<AreaBean>>> getAreaList(@Field("region_id") String region_id);

    /**
     * 获取银行卡列表
     */
    @POST("common/getBankList")
    Call<BaseResponse<List<BindCard>>> getBankLst();


    /**
     * 绑卡:获取验证码
     */
    @POST("api/sendSMS")
    @FormUrlEncoded
    Call<BaseResponse<Object>> getBindCode(@Header("X-AUTH-TOKEN") String token, @Field("mobile") String mobile);

    /**
     * 实名认证
     */
    @POST("api/personAuth")
    @FormUrlEncoded
    Call<BaseResponse<Object>> bindBankSubmit(@Header("X-AUTH-TOKEN") String token,
                                              @Field("name") String name,
                                              @Field("card_number") String card_number,
                                              @Field("hand_logo") String hand_logo,
                                              @Field("front_card") String front_card,
                                              @Field("rear_card") String rear_card,
                                              @Field("bank_id") String bank_id,
                                              @Field("bank_card") String bank_card,
                                              @Field("province") String province,
                                              @Field("city") String city,
                                              @Field("county") String county,
                                              @Field("province_name") String province_name,
                                              @Field("city_name") String city_name,
                                              @Field("county_name") String county_name,
                                              @Field("branch") String branch,
                                              @Field("bank_logo") String bank_logo,
                                              @Field("code") String code);


    /**
     * 查询实名认证
     *
     * @return
     */
    @POST("api/getPersonAuth")
    Call<BaseResponse<List<PersonAuth>>> getPersonAuth(@Header("X-AUTH-TOKEN") String token);

    /**
     * 修改实名认证
     *
     * @return
     */
    @POST("api/updateAuthentication")
    @FormUrlEncoded
    Call<BaseResponse<Object>> updateAuthentication(@Header("X-AUTH-TOKEN") String token, @FieldMap Map<String, String> params);

    /**
     * 获取银行卡信息
     *
     * @param token
     * @return
     */
    @POST("api/changeBank")
    Call<BaseResponse<List<BankInfo>>> getBankInfo(@Header("X-AUTH-TOKEN") String token);

    /**
     * 修改银行卡信息
     *
     * @param token
     * @param params
     * @return
     */
    @POST("api/updateBank")
    @FormUrlEncoded
    Call<BaseResponse<Object>> updateBank(@Header("X-AUTH-TOKEN") String token, @FieldMap Map<String, String> params);


    @POST("common/getHomePage")
    Call<BaseResponse<List<BannerBase>>> getBanner();

    /**
     * 获取首页商品列表
     */
    @POST("common/getMall")
    Call<BaseResponse<List<GoodsBean>>> getMall();

    /**
     * 获取商品详情
     */
    @POST("common/getGoodDetail")
    @FormUrlEncoded
    Call<BaseResponse<List<GoodsBean>>> getGoodDetail(@Header("X-AUTH-TOKEN") String token, @Field("goods_id") String goods_id);


    /**
     * 普通商品生成确认订单
     */
    @POST("api/generateOrder")
    @FormUrlEncoded
    Call<BaseResponse<List<OrderCartBase>>> generateOrder(@Header("X-AUTH-TOKEN") String token,
                                                          @Field("goods_attr_id") String goods_attr_id,
                                                          @Field("queryId") String queryId,
                                                          @Field("addr_id") String addr_id);


    /**
     * 小哈生成确认订单
     */
    @POST("api/xhGenerateOrder")
    @FormUrlEncoded
    Call<BaseResponse<List<OrderCartBase>>> xhGenerateOrder(@Header("X-AUTH-TOKEN") String token,
                                                            @Field("goods_type") int goods_type,
                                                            @Field("goods_attr_id") String goods_attr_id,
                                                            @Field("number") String number,
                                                            @Field("addr_id") String addr_id);



    /**
     * 小哈生成订单
     */
    @POST("api/submitOrder")
    @FormUrlEncoded
    Call<BaseResponse<List<OrderPay>>> submitOrder(@Header("X-AUTH-TOKEN") String token, @FieldMap Map<String, Object> params);


    /**
     * 普通商品生成订单
     */
    @POST("api/submitOrderGoods")
    @FormUrlEncoded
    Call<BaseResponse<List<OrderPay>>> submitOrderGoods(@Header("X-AUTH-TOKEN") String token, @FieldMap Map<String, Object> params);

    /**
     * 获取支付方式控制
     *
     * @return
     */
    @POST("common/payrole")
    Call<BaseResponse<List<PayMethod>>> getPayMethod();

    /**
     * 生成支付订单
     */
    @POST("api/submitOrder")
    @FormUrlEncoded
    Call<BaseResponse<List<PayReturnInfo>>> submitOrder(@Header("X-AUTH-TOKEN") String token, @Field("addr_id") String addr_id,
                                                         @Field("goods_attr_id") String goods_attr_id,@Field("queryId") String queryId,
                                                         @Field("type") String type,@Field("pay_method") int pay_method,@Field("use_consumption") int use_consumption);
    /**
     * 支付待付款订单
     */
    @POST("api/payOrder")
    @FormUrlEncoded
    Call<BaseResponse<List<PayReturnInfo>>> payOrder(@Header("X-AUTH-TOKEN") String token, @Field("order_sn") String order_sn,@Field("pay_method") int pay_method);

    /**
     * 提交金柚计划
     */
    @POST("api/submitJinYouActivity")
    @FormUrlEncoded
    Call<BaseResponse<List<PayReturnInfo>>> submitJinYouActivity(@Header("X-AUTH-TOKEN") String token, @Field("activity_id") String activity_id, @Field("pay_method") int pay_method);

    /**
     * 微信支付
     */
    @POST("api/getWXOrder")
    @FormUrlEncoded
    Call<BaseResponse<List<PayReturnInfo>>> getWXOrder(@Header("X-AUTH-TOKEN") String token, @Field("trade_id") String trade_id, @Field("type") int type);

    /**
     * 银联支付
     */
    @POST("api/getUnionOrder")
    @FormUrlEncoded
    Call<BaseResponse<List<String>>> getUnionOrder(@Header("X-AUTH-TOKEN") String token, @Field("trade_id") String trade_id);


    /**
     * 提货支付宝支付
     */
    @POST("api/deliveryAlipayOrder")
    @FormUrlEncoded
    Call<BaseResponse<List<String>>> deliveryAlipayOrder(@Header("X-AUTH-TOKEN") String token, @Field("trade_id") String trade_id);

    /**
     * 提货微信支付
     */
    @POST("api/deliveryGetWXOrder")
    @FormUrlEncoded
    Call<BaseResponse<List<WxPayResult>>> deliveryGetWXOrder(@Header("X-AUTH-TOKEN") String token, @Field("trade_id") String trade_id, @Field("type") int type);

    /**
     * 提货银联支付
     */
    @POST("api/deliveryUnionOrder")
    @FormUrlEncoded
    Call<BaseResponse<List<String>>> deliveryUnionOrder(@Header("X-AUTH-TOKEN") String token, @Field("trade_id") String trade_id);


    /**
     * 鼓励积分支付
     */
    @POST("api/pointPay")
    @FormUrlEncoded
    Call<BaseResponse<Object>> pointPay(@Header("X-AUTH-TOKEN") String token, @Field("trade_id") String trade_id, @Field("verifySecret") String verifySecret);


    /**
     * 验证支付密码
     */
    @POST("api/verifySecret")
    @FormUrlEncoded
    Call<BaseResponse<Object>> verifyPayPassword(@Header("X-AUTH-TOKEN") String token, @Field("verifySecret") String verifySecret);
    /**
     * 提交报单
     */
    @POST("api/declarationForm")
    @FormUrlEncoded
    Call<BaseResponse<List<DeclarationBean>>> declarationForm(@Header("X-AUTH-TOKEN") String token,
                                                              @Field("pic") String pic, @Field("money") String money,
                                                              @Field("banknumber") String banknumber,@Field("realname") String realname);

    /**
     * 获取报单信息
     */
    @POST("api/declarationLeft")
    Call<BaseResponse<List<DeclarationBean>>> declarationLeft(@Header("X-AUTH-TOKEN") String token);

    /**
     * 金柚计划兑换
     */
    @POST("api/joinJinYouActivity")
    @FormUrlEncoded
    Call<BaseResponse<List<AssemblingPayBean>>> joinJinYouActivity(@Header("X-AUTH-TOKEN") String token, @Field("activity_id") String activity_id);


    /**
     * 产果兑换
     */
    @POST("api/exchangeReturn")
    @FormUrlEncoded
    Call<BaseResponse<List<ExchangeInfo>>> canCashExchange(@Header("X-AUTH-TOKEN") String token, @Field("verifySecret") String verifySecret, @Field("money") String money);

    /**
     * 产果转让
     */
    @POST("api/transferReturn")
    @FormUrlEncoded
    Call<BaseResponse<List<ExchangeInfo>>> transferReturn(@Header("X-AUTH-TOKEN") String token, @Field("verifySecret") String verifySecret, @Field("money") String money, @Field("mobile") String mobile);

    /**
     * 产果复投
     */
    @POST("api/recastReturn")
    @FormUrlEncoded
    Call<BaseResponse<List<ExchangeInfo>>> recastReturn(@Header("X-AUTH-TOKEN") String token, @Field("verifySecret") String verifySecret, @Field("money") String money);

    /**
     * 吉优美兑换
     */
    @POST("api/exchangeJiYouMei")
    @FormUrlEncoded
    Call<BaseResponse<List<ExchangeInfo>>> exchangeJiYouMei(@Header("X-AUTH-TOKEN") String token, @Field("verifySecret") String verifySecret, @Field("money") String money);


    /**
     * 货款提现
     */
    @POST("api/exchangeSurplus")
    @FormUrlEncoded
    Call<BaseResponse<List<ExchangeInfo>>> exchangeSurplus(@Header("X-AUTH-TOKEN") String token, @Field("verifySecret") String verifySecret, @Field("money") String money);


    /**
     * 获取好友列表
     */
    @POST("api/myFriend")
    @FormUrlEncoded
    Call<BaseResponse<List<RecomFriendBase>>> getFriends(@Header("X-AUTH-TOKEN") String token, @Field("type") int type);

    /**
     * 推荐面板信息
     *
     * @param token
     * @return
     */
    @POST("api/myReferInformation")
    Call<BaseResponse<List<RecommendInfo>>> myReferInformation(@Header("X-AUTH-TOKEN") String token);


    /**
     * 获取普通商品订单列表
     */
    @POST("api/myOrderList")
    @FormUrlEncoded
    Call<BaseResponse<List<OrderMailInfo>>> getMainOrderList(@Header("X-AUTH-TOKEN") String token, @Field("status") int status);


    /**
     * 获取智品订单购买列表
     */
    @POST("api/myBuyOrderLog")
    Call<BaseResponse<List<OrderInfo>>> myBuyOrderLog(@Header("X-AUTH-TOKEN") String token);

    /**
     * 获取智品订单提货列表
     */
    @POST("api/myDeliveryOrders")
    Call<BaseResponse<List<OrderInfo>>> myDeliveryOrders(@Header("X-AUTH-TOKEN") String token);


    /**
     * 确认收货
     *
     * @param token
     * @param order_sn
     * @return
     */
    @POST("api/confirmReceipt")
    @FormUrlEncoded
    Call<BaseResponse<Object>> confirmReceipt(@Header("X-AUTH-TOKEN") String token, @Field("order_sn") String order_sn);


    /**
     * 取消订单
     *
     * @param token
     * @param order_sn
     * @return
     */
    @POST("api/cancelOrder")
    @FormUrlEncoded
    Call<BaseResponse<Object>> cancelOrder(@Header("X-AUTH-TOKEN") String token, @Field("order_sn") String order_sn);



    /**
     * 查询微信支付结果
     *
     * @param token
     * @param trade_id
     * @return
     */
    @POST("api/queryWXOrder")
    @FormUrlEncoded
    Call<BaseResponse<List<Object>>> queryWXOrder(@Header("X-AUTH-TOKEN") String token, @Field("trade_id") String trade_id);


    /**
     * 获取忘记支付密码token
     */
    @POST("api/getResetPayPwdToken")
    @FormUrlEncoded
    Call<BaseResponse<String>> getResetPayPwdToken(@Header("X-AUTH-TOKEN") String token, @Field("mobile") String mobile, @Field("code") String code);


    /**
     * 修改密码
     */
    @POST("supplier/getBinkBankToken")
    @FormUrlEncoded
    Call<BaseResponse<Object>> getBinkBankToken(@Header("X-AUTH-TOKEN") String token, @Field("code") String code);

    /**
     * 获取支付验证码
     */
    @POST("supplier/getVerifyCode")
    @FormUrlEncoded
    Call<BaseResponse<String>> getVerifyCode(@Header("X-AUTH-TOKEN") String token, @Field("type") int type);

    /**
     * 充值支付密码
     */
    @POST("api/resetPayPwdPost")
    @FormUrlEncoded
    Call<BaseResponse<String>> resetPayPwdPos(@Header("X-AUTH-TOKEN") String token, @Field("mobile") String mobile, @Field("reset_token") String reset_token, @Field("password") String password);


    /**
     * 获取消息列表
     */
    @POST("supplier/myMessage")
    @FormUrlEncoded
    Call<BaseResponse<MessageBase>> myMessage(@Header("X-AUTH-TOKEN") String token,
                                              @Field("msg_type") String msg_type,
                                              @Field("page") String page);


    /**
     * 获取消息列表
     */
    @POST("api/myNotify")
    Call<BaseResponse<List<Message>>> myMessage(@Header("X-AUTH-TOKEN") String token);

    /**
     * 阅读消息
     */
    @POST("api/setNotifyRead")
    @FormUrlEncoded
    Call<BaseResponse<Object>> setNotifyRead(@Header("X-AUTH-TOKEN") String token,@Field("id") String id);


    /**
     * 激活好友
     */
    @POST("api/activeFriend")
    @FormUrlEncoded
    Call<BaseResponse<String>> activateFriend(@Header("X-AUTH-TOKEN") String token, @Field("mobile") String mobile);

    /**
     * 获取激活记录
     */
    @POST("api/activeFriendLog")
    Call<BaseResponse<List<AmountDetail>>> activeFriendLog(@Header("X-AUTH-TOKEN") String token);

    /**
     * 获取智品积分列表
     */
    @POST("api/intelligencePointsLog")
    Call<BaseResponse<List<IntegralPoint>>> intelligencePointsLog(@Header("X-AUTH-TOKEN") String token);


    /**
     * 获取平台库存信息
     */
    @POST("api/getstocks")
    Call<BaseResponse<List<Inventory>>> getstocks(@Header("X-AUTH-TOKEN") String token);

    /**
     * 获取平台库存明细列表
     */
    @POST("api/getstocksLog")
    Call<BaseResponse<List<InventoryDetails>>> getstocksLog(@Header("X-AUTH-TOKEN") String token);

    /**
     * 提货
     *
     * @param token
     * @param addr_id
     * @param good_id
     * @param number
     * @return
     */
    @POST("api/pickUpGood")
    @FormUrlEncoded
    Call<BaseResponse<List<OrderPay>>> pickUpGood(@Header("X-AUTH-TOKEN") String token, @Field("addr_id") String addr_id, @Field("good_id") String good_id, @Field("number") String number, @Field("verifySecret") String verifySecret);


    /**
     * 获取物流信息
     *
     * @param order_sn
     * @return
     */
    @POST("common/getExpressInfomation")
    @FormUrlEncoded
    Call<Object> getExpressInfomation(@Field("order_sn") String order_sn,@Field("delivery_sn") String delivery_sn);

    /**
     * 订单详情
     *
     * @param token
     * @param order_sn
     * @
     */
    @POST("api/myOrderListDetail")
    @FormUrlEncoded
    Call<BaseResponse<List<OrderDetals>>> myOrderListDetail(@Header("X-AUTH-TOKEN") String token, @Field("order_sn") String order_sn);

    /**
     * 智品订单详情
     * @param token
     * @param order_sn
     * @return
     */
    @POST("api/myOrdersDetail")
    @FormUrlEncoded
    Call<BaseResponse<List<WisdomOrderDetals>>> myOrdersDetail(@Header("X-AUTH-TOKEN") String token, @Field("order_sn") String order_sn);


    /**
     * 查询运费
     *
     * @param token
     * @param addr_id
     * @param good_id
     * @return
     */
    @POST("api/getDeliveryPriceByAddId")
    @FormUrlEncoded
    Call<BaseResponse<List<Shipping>>> getDeliveryPriceByAddId(@Header("X-AUTH-TOKEN") String token, @Field("addr_id") String addr_id, @Field("good_id") String good_id);


    /**
     * 删除购物车商品
     *
     * @param token
     * @return
     */
    @POST("api/deleteShoppingCartList")
    @FormUrlEncoded
    Call<BaseResponse<Object>> delCartGoodsBat(@Header("X-AUTH-TOKEN") String token, @Field("shopcartId") String shopcartId);


    /**
     * 获取购物车列表
     */
    @POST("api/shoppingCartList")
    Call<BaseResponse<List<CartGoodsBase>>> queryUserCart(@Header("X-AUTH-TOKEN") String token);

    /**
     * 加入购物车
     *
     * @param token
     * @param goods_attr_id
     * @param number
     * @return
     */
    @POST("api/addShoppingCart")
    @FormUrlEncoded
    Call<BaseResponse<Object>> addShoppingCart(@Header("X-AUTH-TOKEN") String token, @Field("goods_attr_id") String goods_attr_id, @Field("number") String number, @Field("type") String type);
    /**
     * 加入收藏
     * @param token
     * @param goods_id
     * @return
     */
    @POST("api/addFavorite")
    @FormUrlEncoded
    Call<BaseResponse<Object>> addFavorite(@Header("X-AUTH-TOKEN") String token, @Field("goods_id") String goods_id);

    /**
     * 移除收藏
     * @param token
     * @param goods_id
     * @return
     */
    @POST("api/deleteFavorite")
    @FormUrlEncoded
    Call<BaseResponse<Object>> deleteFavorite(@Header("X-AUTH-TOKEN") String token, @Field("goods_id") String goods_id);

    /**
     * 获取收藏列表
     * @param token
     * @return
     */
    @POST("api/getFavoriteList")
    Call<BaseResponse<List<FavoriteShop>>> getFavoriteList(@Header("X-AUTH-TOKEN") String token);


    /**
     * 取类目及一级标签
     *
     * @return
     */
    @POST("common/getCategary")
    @Headers("Cache-Control: public, max-age=120")
    Call<BaseResponse<List<ParentCatergory>>> getCategoryChildlistRepo();


    /**
     * 根据一级分类取二级分类
     *
     * @return
     */
    @POST("common/goodCategary")
    @Headers("Cache-Control: public, max-age=120")
    @FormUrlEncoded
    Call<BaseResponse<List<Catergory>>> goodCategary(@Field("jump_id") String jump_id);


    /**
     * 根据一级分类取二级分类
     *
     * @return
     */
    @POST("common/goodCategaryGoodsList")
    @FormUrlEncoded
    Call<BaseResponse<List<GoodsBean>>> goodCategaryList(@Field("category_id") String category_id);

    /**
     * 搜索商品
     *
     * @param name
     * @return
     */
    @POST("common/searchGood")
    @FormUrlEncoded
    Call<BaseResponse<List<GoodsBean>>> searchGood(@Field("name") String name);

    /**
     * 验证二级密码
     *
     * @param
     * @return
     */
    @POST("api/verifySecret")
    @FormUrlEncoded
    Call<BaseResponse<List<GoodsBean>>> verifySecret(@Header("X-AUTH-TOKEN") String token,@Field("verifySecret") String verifySecret);

    /**
     * 新品推荐
     * @return
     */
    @POST("common/searchGood")
    Call<BaseResponse<List<GoodsBean>>> getRecommend();



    /*
    *修改昵称
     */
    @POST("api/updateName")
    @FormUrlEncoded
    Call<BaseResponse<Object>> updateNick(@Header("X-AUTH-TOKEN") String token, @Field("nickname") String name);

    /**
     * 修改头像
     * @param token
     * @param headpic
     * @return
     */
    @POST("api/updateHeadPic")
    @FormUrlEncoded
    Call<BaseResponse<Object>> updateHeadPic(@Header("X-AUTH-TOKEN") String token, @Field("headpic") String headpic);

    /**
     * 公告
     * @return
     */
    @GET("common/getNotify")
    Call<BaseResponse<List<Message>>> getNotify();

}
