package com.aurora.ctoip.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author:Aurora
 * @create: 2023-03-02 17:28
 * @Description: IP信息
 */
@Data
//自动生成构造方法和toString
public class IpInfoDto implements Serializable {
    private String ip;
    //IP归属地
    private String area;
    //IP推断
    private ArrayList<String> judgments;
    //是否为恶意IP
    private String is_malicious;
    //更新时间
    private String update_time;
    //运营商信息
    private String asn;

    //@Data
    //public class Asn{
    //    String orgName = "";
    //    String orgId = "";
    //    String address = "";
    //}

}
