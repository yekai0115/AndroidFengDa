package com.fengda.app.constant;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;

/**
 * 阿里云配置
 */

public class MyOSSConfig {
    /**
     * 设置网络参数
     * @return
     */
    public static ClientConfiguration getOSSConfig() {
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        OSSLog.enableLog();
        return conf;
    }
    //设置凭证
    public static OSSCredentialProvider getProvider() {

        return new OSSPlainTextAKSKCredentialProvider(MyConstant.ALI_KEYID, MyConstant.ALI_KEYSECRET);
    }
}
