package com.aurora.ctoip;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootTest
class CtoipApplicationTests {

	@Test
	void contextLoads() {
	}
	@Test
	void test1() throws IOException {
		String[] ipList = {"8.8.8.8", "114.114.114.114","159.203.93.255"}; // 待查询的IP地址列表

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
}
