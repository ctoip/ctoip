package com.aurora.ctoip;

import com.aurora.ctoip.common.dto.DomainInfoDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicReference;

@SpringBootTest
class CtoipApplicationTests {

    @Resource
    RestTemplate restTemplate;

    @Resource
    ObjectMapper objectMapper;

    @Test
    void contextLoads() {
    }

    @Test
    void test1() throws IOException {
        String[] ipList = {"8.8.8.8", "114.114.114.114", "159.203.93.255"}; // 待查询的IP地址列表
        for (String ipAddress : ipList) {
            try {
                // 获取主机名
                byte[] ip = InetAddress.getByName(ipAddress).getAddress();
                InetAddress addr = InetAddress.getByAddress(ip);
                String hostname = addr.getHostName();
                System.out.println("IP地址 " + ipAddress + " 对应的主机名为：" + hostname);
                // 查询历史解析记录
                Process process = Runtime.getRuntime().exec("nslookup -debug " + hostname);
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    // 提取历史解析记录和对应的域名信息
                    if (line.startsWith(";; ANSWER SECTION:")) {
                        while ((line = reader.readLine()) != null && !line.startsWith(";;")) {
                            String[] tokens = line.trim().split("\\s+");
                            if (tokens.length >= 5 && tokens[3].equals("A")) {
                                System.out.println("IP地址 " + tokens[4] + " 在 " + tokens[1] + " 被解析为 " + tokens[0]);
                            }
                        }
                    }
                }
                // 输出查询结果
                process.waitFor();
                int exitValue = process.exitValue();
                if (exitValue != 0) {
                    System.out.println("查询失败，返回码为 " + exitValue);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    void test2() {
        try {
            String command = "nmap"; // 要执行的命令
            Process process = Runtime.getRuntime().exec(command); // 执行命令

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
            String line;
            while ((line = reader.readLine()) != null) { // 读取命令输出
                System.out.println(line);
            }
            process.waitFor(); // 等待命令执行完成
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void test3() throws IOException {
        // 创建 ProcessBuilder 对象并指定要执行的命令
        //String[] command = {"ls", "-l", "/usr"};
        ProcessBuilder builder = new ProcessBuilder("curl", "www.baidu.com");
        // 设置命令执行时的工作目录
        builder.directory(new File(System.getProperty("user.dir")));
        // 开始执行命令
        Process process = builder.start();
        // 从进程中读取输出结果
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
        String line;
        while ((line = reader.readLine()) != null) { // 读取命令输出
            System.out.println(line);
        }
    }

    @Test
    void test4() {
        String command = "-Ss 192.168.50.100";
        String[] commandList = command.split("\\s");
        System.out.println(commandList[0]);
    }

    @Test
    void test5() throws JsonProcessingException {
        DomainInfoDto domainInfoDto = new DomainInfoDto();
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-apikey", "e2a080bbf4ea756b8734bc8b9bc40cad03d16145f67f71bafe9c86d76e50ae3e");
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        ResponseEntity<String> response = restTemplate.exchange("https://www.virustotal.com/api/v3/domains/" + "pro.csocools.com", HttpMethod.GET, entity, String.class);
        //先转化为JsonNode再转化为Dto对象
        JsonNode jsonNode = objectMapper.readTree(response.getBody());
        domainInfoDto.setDomain(jsonNode.get("data").get("id").asText());
        //数组
        AtomicReference<String> str = new AtomicReference<>("");
        jsonNode.get("data").get("attributes").get("last_dns_records").elements().forEachRemaining(item -> {
            //domainInfoDto.getLast_dns_records().add(item.toString());
            item.forEach(a -> {
                str.getAndUpdate(b -> b + (a.asText() + "|"));
            });
            str.getAndUpdate(b -> b + ",");
            //System.out.println(str);
        });
        //domainInfoDto.setCategories(jsonNode.get("data").get("attributes").get("categories").asText());
        AtomicReference<String> Categories = new AtomicReference<>("");
        jsonNode.get("data").get("attributes").get("categories").elements().forEachRemaining(item -> {
            //jsonNode.get("data").get("attributes").get("categories").fields().forEachRemaining(.getKey()) //遍历元素名
            Categories.getAndUpdate(s -> s + item.asText() + "/");
        });
        domainInfoDto.setCategories(Categories.toString());
        domainInfoDto.setRegistrar(jsonNode.get("data").get("attributes").get("registrar").asText());
        domainInfoDto.setMalicious(jsonNode.get("data").get("attributes").get("last_analysis_stats").get("malicious").asText());
        domainInfoDto.setSuspicious(jsonNode.get("data").get("attributes").get("last_analysis_stats").get("suspicious").asText());
        domainInfoDto.setWhois(jsonNode.get("data").get("attributes").get("whois").asText());
        //System.out.println(domainInfoDto);
    }
}
