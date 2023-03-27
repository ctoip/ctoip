package com.aurora.ctoip.controller;

import com.aurora.ctoip.common.dto.DomainInfoDto;
import com.aurora.ctoip.common.lang.Const;
import com.aurora.ctoip.common.lang.Result;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author:Aurora
 * @create: 2023-03-13 23:55
 * @Description: 域名whois控制器
 */
@RestController
@RequestMapping("/DomainQuery")
public class DomainQueryController extends BaseController {
    @GetMapping("/DomainInfo")
    public Result getDomainInfo(String domain) throws JsonProcessingException {
        String JsonData = "";
        if (!domain.matches("[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+.?"))
            return Result.fail("输入错误");
        if (!redisUtil.lHasKey(Const.DOMAININFO_KEY, domain)) {
            JsonData = objectMapper.writeValueAsString(this.getDomainInfoFromApi(domain));
            //存入redis
            redisUtil.lSet(Const.DOMAININFO_KEY, JsonData);
        } else {
            //从redis拿到数据
            JsonData = redisUtil.lGet(Const.DOMAININFO_KEY, domain);
        }
        //最终处理数据
        return Result.success(JsonData);
    }

    public DomainInfoDto getDomainInfoFromApi(String domain) throws JsonProcessingException {
        DomainInfoDto domainInfoDto = new DomainInfoDto();
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-apikey", VTApiKey);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        ResponseEntity<String> response = restTemplate.exchange("https://www.virustotal.com/api/v3/domains/" + domain, HttpMethod.GET, entity, String.class);
        //先转化为JsonNode再转化为Dto对象
        JsonNode jsonNode = objectMapper.readTree(response.getBody());
        domainInfoDto.setDomain(jsonNode.get("data").get("id").asText());
        //数组
        AtomicReference<String> last_dns_records = new AtomicReference<>("");
        jsonNode.get("data").get("attributes").get("last_dns_records").elements().forEachRemaining(item -> {
            //domainInfoDto.getLast_dns_records().add(item.toString());
            item.forEach(a -> {
                last_dns_records.getAndUpdate(b -> b + (a.asText() + "|"));
            });
            last_dns_records.getAndUpdate(b -> b + "-");
        });
        //System.out.println(last_dns_records);
        domainInfoDto.setLast_dns_records(last_dns_records.toString());
        //domainInfoDto.setCategories(jsonNode.get("data").get("attributes").get("categories").asText());
        AtomicReference<String> Categories = new AtomicReference<>("");
        jsonNode.get("data").get("attributes").get("categories").elements().forEachRemaining(item -> {
            //jsonNode.get("data").get("attributes").get("categories").fields().forEachRemaining(.getKey()) //遍历元素名
            Categories.getAndUpdate(s -> s + item.asText() + "|");
        });
        domainInfoDto.setCategories(Categories.toString());
        domainInfoDto.setRegistrar(jsonNode.get("data").get("attributes").get("registrar").asText());
        domainInfoDto.setMalicious(jsonNode.get("data").get("attributes").get("last_analysis_stats").get("malicious").asText());
        domainInfoDto.setSuspicious(jsonNode.get("data").get("attributes").get("last_analysis_stats").get("suspicious").asText());
        domainInfoDto.setWhois(jsonNode.get("data").get("attributes").get("whois").asText());
        //System.out.println(domainInfoDto);
        return domainInfoDto;
    }

    @GetMapping("/getDomainInfoList")
    public Result getDomainInfoList() throws JsonProcessingException {
        List<Object> objects = redisUtil.lGet(Const.DOMAININFO_KEY, 0, -1);
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

    @GetMapping("/delDomainInfo")
    public Result delDomainInfo(String domain) {
        if (redisUtil.lHasKey(Const.DOMAININFO_KEY, domain)) {
            redisUtil.lRemoveFromList(Const.DOMAININFO_KEY, domain);
        }
        return Result.success("");
    }

    @PostMapping("/delDomainInfoList")
    public Result delDomainInfoList(@RequestBody String domainlist) throws JsonProcessingException {
        List<String> list = objectMapper.readValue(domainlist, List.class);
        redisUtil.lBatchRemoveFromList(Const.DOMAININFO_KEY, list);
        return Result.success("");
    }
}
