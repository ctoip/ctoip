package com.aurora.ctoip.controller;

import com.aurora.ctoip.common.lang.Result;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author:Aurora
 * @create: 2023-03-13 09:13
 * @Description: 网络工具箱
 */
@RestController
@RequestMapping("/netTools")
public class NetToolsController extends BaseController{
    @GetMapping("/nmap")
    public Result Nmap (String command) throws IOException {
        //将前端的command转换为命令数组
        String[] commandList = command.split("\\s");
        List<String> commandListArray = new ArrayList<>(Arrays.asList(commandList));
        commandListArray.add(0,"nmap");
        //System.out.println(commandListArray);
        ProcessBuilder builder = new ProcessBuilder(commandListArray);
        builder.directory(new File(System.getProperty("user.dir")));
        Process process = builder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(),"GBK"));
        String line;
        List<String> list = new ArrayList<>();
        while ((line = reader.readLine()) != null) { // 读取命令输出
            //System.out.println(line);
            list.add(line);
        }
        String JsonString = objectMapper.writeValueAsString(list);
        return Result.success(JsonString);
    }
}
