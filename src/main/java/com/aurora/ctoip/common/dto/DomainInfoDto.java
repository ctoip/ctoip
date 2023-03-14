package com.aurora.ctoip.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author:Aurora
 * @create: 2023-03-13 22:40
 * @Description: 域名信息
 */
@Data
public class DomainInfoDto implements Serializable {
    //域名
    private String domain;
    //最后一次域名解析IP
    private String last_dns_records;
    //引擎检出恶意 的数量
    private String malicious;
    //检出 可疑的数量
    private String suspicious;
    //域名等级公司
    private String registrar;
    //categories, 检出标记
    private String categories = "";
    //域名whois
    private String whois;
}
