package com.aurora.ctoip.controller;

import cn.hutool.core.map.MapUtil;
import com.aurora.ctoip.common.lang.Result;
import com.aurora.ctoip.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.whois.WhoisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;

/**
 * @author:Aurora
 * @create: 2023-02-20 18:48
 * @Description:
 */
@Slf4j
@RestController
public class TestController extends BaseController {
    @Resource
    SysUserService sysUserService;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/test/selectUser")
    public Result selectUser() {
        return Result.success(sysUserService.list());
    }

    @GetMapping("/test/pass")
    public Result passEncode() {
        // 密码加密
        String pass = bCryptPasswordEncoder.encode("111111");
        // 密码验证
        boolean matches = bCryptPasswordEncoder.matches("111111", pass);

        return Result.success(MapUtil.builder()
                .put("pass", pass)
                .put("marches", matches)
                .build()
        );
    }

    @GetMapping("/test/test1")
    public Result paramResultTest(Integer num) {
        System.out.println(num);
        log.info(num.toString());
        return Result.success("成功");
    }

    @GetMapping("/test/test2")
    public Result CrossRegionTest() {
        //微步查询IP信誉
        //String object = restTemplate.getForObject("https://api.threatbook.cn/v3/scene/ip_reputation?apikey=" +
        //                "5ea5eb7c777e432997a42a7dda717c2a1402def6763540f6b6a39a35f010b02a&resource=159.203.93.255&lang=zh"
        //        , String.class);
        //System.out.println(object);

        //whois信息
        String ip = "159.203.93.255"; // IP地址
        //String server = "whois.arin.net"; // Whois服务器

        try (Socket socket = new Socket("whois.arin.net", 43);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            socket.getOutputStream().write((ip + "\r\n").getBytes()); // 发送查询请求
            String line;
            while ((line = reader.readLine()) != null) { // 读取响应
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.success("成功");
    }

    //https://ipapi.co/159.203.93.255/json/
    //域名详情
    @GetMapping("/test/test3")
    public Result Test3() throws IOException {
        WhoisClient whoisClient = new WhoisClient();
        try {
            whoisClient.connect(WhoisClient.DEFAULT_HOST);
            String whoisData = whoisClient.query("=" + "baidu.com");
            System.out.println(whoisData);
            System.out.println(Arrays.asList(whoisData.split("\n")));
            return Result.success("");
        } finally {
            whoisClient.disconnect();
        }
    }
}
