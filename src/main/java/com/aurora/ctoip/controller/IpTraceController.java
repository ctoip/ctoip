package com.aurora.ctoip.controller;

import com.aurora.ctoip.common.dto.IpInfoDto;
import com.aurora.ctoip.common.lang.Const;
import com.aurora.ctoip.common.lang.Result;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author:Aurora
 * @create: 2023-03-02 16:53
 * @Description:
 */
@RestController
@RequestMapping("/IpTrace")
public class IpTraceController extends BaseController {
    @GetMapping("/getIpInfo")
    public Result getIpInfo(String ipaddress) throws IOException {
        String jsonIpInfo = "";
        if (!ipaddress.matches("(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)"))
            return Result.fail("输入错误");
        if (!redisUtil.lHasKey(Const.IPINFO_KEY, ipaddress)) {
            jsonIpInfo = objectMapper.writeValueAsString(this.getIpInfoFromAPI(ipaddress));
            jsonIpInfo = Pattern.compile("\\s{5,}").matcher(jsonIpInfo).replaceAll("");
            //存入redis
            redisUtil.lSet(Const.IPINFO_KEY, jsonIpInfo);
        } else {
            //从redis拿到数据
            jsonIpInfo = redisUtil.lGet(Const.IPINFO_KEY, ipaddress);
            //IpInfoDto ipInfoDto = objectMapper.readValue(jsonIpInfo, IpInfoDto.class);
        }
        //最终处理数据
        return Result.success(jsonIpInfo);
    }

    public IpInfoDto getIpInfoFromAPI(String ipaddress) throws IOException {
        //微步查询IP信誉
        String Json1 = restTemplate.getForObject("https://api.threatbook.cn/v3/scene/ip_reputation?" +
                "apikey=" + threatBookApiKey +
                "&resource=" + ipaddress +
                "&lang=zh", String.class);
        //读取节点
        //Object Json1Obj = objectMapper.readValue(Json1, Object.class);
        JsonNode jsonNodeInJson1 = objectMapper.readTree(Json1);
        IpInfoDto ipInfoDto = new IpInfoDto();
        //读取设置IP
        ipInfoDto.setIp(ipaddress);
        //读取归属地
        String area = jsonNodeInJson1.get("data").get(ipaddress).get("basic").get("location").get("country").asText() + " " +
                jsonNodeInJson1.get("data").get(ipaddress).get("basic").get("location").get("province").asText() + " " +
                jsonNodeInJson1.get("data").get(ipaddress).get("basic").get("location").get("city").asText();
        ipInfoDto.setArea(area);
        //读取judgments
        Iterator<JsonNode> judgments = jsonNodeInJson1.get("data").get(ipaddress).get("judgments").elements();
        ArrayList<String> list = new ArrayList<String>();
        judgments.forEachRemaining(judgment -> {
            list.add(judgment.asText());
        });
        ipInfoDto.setJudgments(list);
        //读取is_malicious
        boolean b = jsonNodeInJson1.get("data").get(ipaddress).get("is_malicious").asBoolean();
        if (b) {
            ipInfoDto.setIs_malicious("true");
        } else {
            ipInfoDto.setIs_malicious("false");
        }
        //读取更新时间
        String update_time = jsonNodeInJson1.get("data").get(ipaddress).get("update_time").asText();
        ipInfoDto.setUpdate_time(update_time);
        /**
         * 读取ASN
         */
        String asn = jsonNodeInJson1.get("data").get(ipaddress).get("basic").get("carrier").asText();
        ipInfoDto.setAsn(asn);
        return ipInfoDto;
    }

    //返回redis中所有的ip
    @GetMapping("/getIpInfoList")
    public Result getIpInfoList() throws JsonProcessingException {
        List<Object> objects = redisUtil.lGet(Const.IPINFO_KEY, 0, -1);
        List<ObjectNode> objectNodeList = new ArrayList<>();
        //将字符串转化为List<ObjectNode>,里面存储的是JsonNode对象
        objects.forEach((value) -> {
            try {
                //单个元素转化为ObjectNode
                ObjectNode objectNode = objectMapper.readValue(value.toString(), ObjectNode.class);
                objectNodeList.add(objectNode);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        //生成一个JSON数组,遍历添加数据
        ArrayNode arrayNodeList = objectMapper.createArrayNode();
        objectNodeList.forEach(arrayNodeList::add);
        String jsonArrayString = objectMapper.writeValueAsString(arrayNodeList);
        return Result.success(jsonArrayString);
    }

    //删除
    @GetMapping("/delIpInfo")
    public Result delIpInfo(String ipaddress) {
        if (redisUtil.lHasKey(Const.IPINFO_KEY, ipaddress)) {
            redisUtil.lRemoveFromList(Const.IPINFO_KEY, ipaddress);
        }
        return Result.success("");
    }

    @PostMapping("/delIpInfoList")
    public Result delIpInfoList(@RequestBody String ipaddress) throws JsonProcessingException {
        List<String> list = objectMapper.readValue(ipaddress, List.class);
        redisUtil.lBatchRemoveFromList(Const.IPINFO_KEY, list);
        return Result.success("");
    }
}
