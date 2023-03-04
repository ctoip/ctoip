package com.aurora.ctoip.common.dto.iptrace;

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
public class IpInfoDto implements Serializable {
    private String ip;
    //IP归属地
    private String area;
    //运营商信息
    private Asn asn = new Asn();
    //IP推断
    private ArrayList<String> judgments;
    //是否为恶意IP
    private String is_malicious;
    //更新时间
    private String update_time;

    @Data
    public class Asn{
        String orgName = "";
        String orgId = "";
        String address = "";
    }

}
