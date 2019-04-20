package com.form.commit.formcommit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.form.commit.formcommit.jdbc.GenConnection;
import com.form.commit.formcommit.req.CommitReq;
import com.form.commit.formcommit.service.HttpClient4;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.util.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@SpringBootApplication
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
@EntityScan("com.form")
@EnableFeignClients(basePackages = "com.form")
@Slf4j
public class FormcommitApplication {

    private static String BASE_URL = "http://www.bxjjren.com";

    public static void main(String[] args) throws Exception {

        // 1、登录获取jessionId
        String sessionId = loginReturnSessionId();
        HttpClient4.jessionId = sessionId;

        // 2、获取当前订单列表，获取orderIds
        String orderListString = HttpClient4.doGet(BASE_URL + "/perform/getMyOrderList?cityId=340100");
        Map myorderListMap = (Map) JSON.parse(orderListString);
        JSONArray myOrders = (JSONArray) myorderListMap.get("dataList");
        Iterator iterator = myOrders.iterator();
        List orderIds = new ArrayList<>();
        while (iterator.hasNext()) {
            Map obj = (Map) iterator.next();
            Integer id = (Integer) obj.get("id");
            orderIds.add(id);
        }
        System.out.println("processing order ids ======================" + orderIds);

        orderIds.stream().forEach(id -> {
            Map insertReq = genBaseRequest();

            // 数据库获取真实数据
            CommitReq commitReq = loadInfo();

            insertReq.put("orderId", id);
            insertReq.put("insuranceEnd", commitReq.getBusinessTime());
            insertReq.put("compulsoryStartTime", commitReq.getCompulsoryTime());
            insertReq.put("compulsoryTimeFlag", "1");

            // TODO 重要数据拼接-zhangzhibing请完成
            insertReq.put("fileItems", "[{\"file_type\":1,\"file_path\":\"\"},{\"file_type\":2,\"file_path\":\"\"},{\"file_type\":3,\"file_path\":\"\"},{\"file_type\":4,\"file_path\":\"\"},{\"file_type\":9,\"file_path\":\"\"},{\"file_type\":10,\"file_path\":\"\"},{\"file_type\":11,\"file_path\":\"\"}]");
            insertReq.put("insuranceSet", "[{\"id\":\"122\",\"name\":\"交强险\",\"type\":\"2\",\"code\":\"J1\",\"attribute\":\"null\",\"text\":\"\",\"priceNum\":\"1\"},{\"id\":\"123\",\"name\":\"车船税\",\"type\":\"2\",\"code\":\"CCS\",\"attribute\":\"缴税,减税,免税,完税,不缴税\",\"text\":\"缴税\",\"isuTextarea\":\"\",\"priceNum\":\"1\"},{\"id\":\"100\",\"name\":\"车损险\",\"type\":\"1\",\"code\":\"Z1\",\"attribute\":\"null\",\"text\":\"\",\"isuTextarea\":\"\",\"priceNum\":\"\"},{\"id\":\"105\",\"name\":\"车损险不计免赔\",\"type\":\"1\",\"code\":\"B1\",\"text\":\"\",\"textSelect\":\"\",\"isuTextarea\":\"\",\"priceNum\":\"\"},{\"id\":\"102\",\"name\":\"三者险\",\"type\":\"1\",\"code\":\"Z3\",\"attribute\":\"5万元,10万元,15万元,20万元,30万元,50万元,100万元,150万元,200万元,300万元,500万元\",\"text\":\"\",\"textSelect\":\"100万元\",\"isuTextarea\":\"\",\"priceNum\":\"\"},{\"id\":\"107\",\"name\":\"三者险不计免赔\",\"type\":\"1\",\"code\":\"B3\",\"text\":\"\",\"textSelect\":\"\",\"isuTextarea\":\"\",\"priceNum\":\"\"}]");


            updateToUsed(commitReq.getId());

            if (!StringUtils.isEmpty(commitReq.getCarNum())) {
                insertReq.put("carNo", commitReq.getCarNum());
                // TODO 最后一步，提交
//                String commit = HttpClient4.doPost(BASE_URL + "/perform/InsertMemberInfo", insertReq, null);
//                System.out.println(commit);
            }

        });
    }

    private static Map genBaseRequest() {
        Map insertReq = new HashMap();
        insertReq.put("insuranceEnd", "2019-04-01");
        insertReq.put("insuranceTimeFlag", "1");
        insertReq.put("compulsoryTimeFlag", "1");
        insertReq.put("carUsage", "1");
        insertReq.put("receiptName", "陈玉芳");
        insertReq.put("receiptPhone", "13615617759");
        insertReq.put("receiptAddress", "长江西路844号");
        insertReq.put("attribute", 1);
        insertReq.put("invoiceFlag", 0);
        insertReq.put("buttonFlag", 2);
        insertReq.put("receiptProvince", "安徽");
        insertReq.put("receiptCity", "合肥市");
        insertReq.put("receiptArea", "蜀山区");
        insertReq.put("initialInsuredFlag", 1);
        insertReq.put("commercialInsurance", 2);
        insertReq.put("collectionFlag", 0);
        insertReq.put("advanceFlag", 0);
        insertReq.put("exceptionPhone", "13615617759");
        insertReq.put("deliverType", 1);
        insertReq.put("codePayType", 2);

        return insertReq;
    }


    private static CommitReq loadInfo() {
        Connection conn = GenConnection.getConn();
        String sql = "select * from insurance_inquiry where used = 0 limit 1";
        CommitReq commitReq = new CommitReq();

        PreparedStatement pstmt;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            int col = rs.getMetaData().getColumnCount();
            System.out.println("============================");
            if (rs.next()) {
                System.out.print(rs.getString(1) + "\t");
                commitReq.setId(rs.getString(1));
                String num = rs.getString(2);
                commitReq.setCarNum(num);
                System.out.print(rs.getString(2) + "\t");

                commitReq.setVin(rs.getString(3));
                commitReq.setCompulsory(rs.getBoolean(4));
                commitReq.setCompulsoryTime(rs.getString(5));
                commitReq.setCompulsoryAmount(rs.getDouble(6));
                commitReq.setCompulsoryTax(rs.getDouble(7));

                commitReq.setBusiness(rs.getBoolean(8));
                commitReq.setBusinessTime(rs.getString(9));
                commitReq.setBusinessAmount(rs.getDouble(10));

            }
            System.out.println("============================");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commitReq;
    }

    private static int updateToUsed(String uuid) {
        Connection conn = GenConnection.getConn();
        int i = 0;
        String sql = "update insurance_inquiry set used = 1 where id = '" + uuid + "'";
        PreparedStatement pstmt;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            i = pstmt.executeUpdate();
            System.out.println("resutl: " + i);
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;
    }

    private static String loginReturnSessionId() {
        Map requset = new HashMap();
        requset.put("phoneNo", "13615617759");
        requset.put("loginPwd", "111");
        String result = HttpClient4.loadSessionId(BASE_URL + "/user/login", requset);
        return result;
    }

}
