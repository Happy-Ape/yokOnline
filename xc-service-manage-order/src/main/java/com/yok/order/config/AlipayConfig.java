package com.yok.order.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Data
@Slf4j
@Configuration
public class AlipayConfig {

    @Value("${alipay.pay.gatewayUrl}")
    private String gatewayUrl;

    @Value("${alipay.pay.appid}")
    private String appid;

    @Value("${alipay.pay.app-private-key}")
    private String appPrivateKey;

    @Value("${alipay.pay.alipay-public-key}")
    private String alipayPublicKey;

    private String signType = "RSA2";

    private String formate = "json";

    private String charset = "UTF-8";

    private static int maxQueryRetry = 5;

    private static long queryDuration = 5000;

    private static int maxCancelRetry = 3;

    private static long cancelDuration = 3000;

    @Bean
    public AlipayClient alipayClient() {
        String appid = this.getAppid();
        String appPrivateKey = this.getAppPrivateKey();
        String alipayPublicKey = this.getAlipayPublicKey();
        return new DefaultAlipayClient(this.getGatewayUrl(),
                appid,
                appPrivateKey,
                this.getFormate(),
                this.getCharset(),
                alipayPublicKey,
                this.getSignType());
    }

//    private String getKey(String path) {
//        Resource resource = new ClassPathResource(path);
//        try {
//            InputStreamReader inputStreamReader = new InputStreamReader(resource.getInputStream());
//            BufferedReader br = new BufferedReader(inputStreamReader);
//            return br.lines().collect(Collectors.joining("\n"));
//        } catch (IOException ioe) {
//            return null;
//        }
//    }
}
