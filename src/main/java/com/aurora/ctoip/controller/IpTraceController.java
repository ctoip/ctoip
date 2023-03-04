package com.aurora.ctoip.controller;

import cn.hutool.json.JSON;
import com.aurora.ctoip.common.dto.iptrace.IpInfoDto;
import com.aurora.ctoip.common.lang.Const;
import com.aurora.ctoip.common.lang.Result;
import com.aurora.ctoip.util.RedisUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * @author:Aurora
 * @create: 2023-03-02 16:53
 * @Description:
 */
@RestController
@RequestMapping("/IpTrace")
public class IpTraceController extends BaseController {

    //处理JSON数据
    @Resource
    ObjectMapper objectMapper;


    @GetMapping("/getIpInfo")
    public Result getIpInfo(String ipaddress) throws IOException {
        String jsonIpInfo = "";
        if(!redisUtil.hHasKey(Const.IPINFO_KEY,ipaddress)) {
            jsonIpInfo = objectMapper.writeValueAsString(this.getIpInfoFromAPI(ipaddress));
            //存入redis
            redisUtil.hset(Const.IPINFO_KEY, ipaddress,jsonIpInfo);
        }
        else{
            //从redis拿到数据
            jsonIpInfo = (String) redisUtil.hget(Const.IPINFO_KEY, ipaddress);
            //IpInfoDto ipInfoDto = objectMapper.readValue(jsonIpInfo, IpInfoDto.class);
        }
        //最终处理数据
        return Result.success(Pattern.compile("\\s{5,}").matcher(jsonIpInfo).replaceAll(""));
    }
    public IpInfoDto getIpInfoFromAPI (String ipaddress) throws IOException {
        //微步查询IP信誉
        String Json1 = restTemplate.getForObject("https://api.threatbook.cn/v3/scene/ip_reputation?"+
                "apikey=5ea5eb7c777e432997a42a7dda717c2a1402def6763540f6b6a39a35f010b02a"+
                "&resource="+ipaddress+
                "&lang=zh", String.class);
        //读取节点
        //Object Json1Obj = objectMapper.readValue(Json1, Object.class);
        JsonNode jsonNodeInJson1 = objectMapper.readTree(Json1);
        IpInfoDto ipInfoDto = new IpInfoDto();
        //读取设置IP
        ipInfoDto.setIp(ipaddress);
        //读取归属地
        String area = jsonNodeInJson1.get("data").get(ipaddress).get("basic").get("location").get("country").asText() +" "+
                jsonNodeInJson1.get("data").get(ipaddress).get("basic").get("location").get("province").asText()+" "+
                jsonNodeInJson1.get("data").get(ipaddress).get("basic").get("location").get("city").asText();
        ipInfoDto.setArea(area);
        //读取judgments
        Iterator<JsonNode> judgments = jsonNodeInJson1.get("data").get(ipaddress).get("judgments").elements();
        ArrayList<String> list = new ArrayList<String>();
        judgments.forEachRemaining(judgment->{
            list.add(judgment.asText());
        });
        ipInfoDto.setJudgments(list);
        //读取is_malicious
        boolean b = jsonNodeInJson1.get("data").get(ipaddress).get("is_malicious").asBoolean();
        if(b){
            ipInfoDto.setIs_malicious("true");
        }
        else {
            ipInfoDto.setIs_malicious("false");
        }
        //读取更新时间
        String update_time = jsonNodeInJson1.get("data").get(ipaddress).get("update_time").asText();
        ipInfoDto.setUpdate_time(update_time);
        //读取ASN
        try (Socket socket = new Socket("whois.arin.net", 43);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            socket.getOutputStream().write((ipaddress + "\r\n").getBytes()); // 发送查询请求
            String line;
            while ((line = reader.readLine()) != null) { // 读取响应
                String pattern1 = "^OrgName.*";
                String pattern2 = "^OrgId.*";
                String pattern3 = "^Address.*";
                if (Pattern.matches(pattern1, line)) {
                    ipInfoDto.getAsn().setOrgName(line.trim());
                }
                if (Pattern.matches(pattern2, line)) {
                    ipInfoDto.getAsn().setOrgId(line.trim());
                }
                if (Pattern.matches(pattern3, line)) {
                    ipInfoDto.getAsn().setAddress(line.trim());
                    break;
                }
            }
            //System.out.println(ipInfoDto.toString());
        }
        return ipInfoDto;
    }
}
