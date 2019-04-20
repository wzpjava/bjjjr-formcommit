package com.form.commit.formcommit;

import com.form.commit.formcommit.req.GenOrderIdReq;
import com.form.commit.formcommit.req.LoginReq;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "sys", url = "www.bxjjren.com")
@Headers(
        {
                "Accept: */*",
                "Accept-Encoding: gzip, deflate",
                "Accept-Language: en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7",
                "Cache-Control: no-cache",
                "Connection: keep-alive",
                "Content-Type: application/x-www-form-urlencoded; charset=UTF-8",
                "Cookie: JSESSIONID=df833a0f-7419-466e-9ce5-bc522b8b41dc",
                "Host: www.bxjjren.com",
                "Origin: http://www.bxjjren.com",
                "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36",
                "X-Requested-With: XMLHttpRequest"
        }
)
public interface SysClient {

    @PostMapping("/user/login")
    ResponseEntity login(@RequestBody LoginReq req);

    @PostMapping("/order/toSubscription?endDate=2019-04-28&saleSet=5769&minSubscription=100000&commercialInsuranceRate=42&extraCommercialInsuranceRate=27&baseCommercialInsuranceRate=15&compulsoryInsuranceRate=0&orderAmount=110000&name=平安财险42%+0%&companyId=2&city=340100&kpiId&attribute=3&payType=2&yCarUsage=1&nCitySet&nProvinceSet&nCarBrand&nCarSeries&nMinDate&nMaxYear&nMaxNum&nMinPrice&nMaxPrice&remarks")
    ResponseEntity genOrderId();

}
