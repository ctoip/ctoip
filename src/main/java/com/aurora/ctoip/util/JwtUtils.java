package com.aurora.ctoip.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;

@Data
@Component
@ConfigurationProperties(prefix = "aurora.jwt")
public class JwtUtils {
    private long expire;
    private String secret;
    private String header;

    /*
    payLoad:
    iss (issuer)：JWT的签发者
    sub (subject)：JWT所面向的用户
    aud (audience)：接收JWT的一方
    exp (expiration time)：JWT的过期时间
    nbf (not before)：JWT生效的开始时间
    iat (issued at)：JWT的签发时间
    jti (JWT ID)：JWT的唯一标识符
    自定义字段：根据业务需求可以设置自定义字段来存放一些额外的信息，例如用户ID、角色等。
     */
    public String generateToken(String username) {
        Date nowDate = new Date();
        Date expireDate = new Date(nowDate.getTime() + 1000 * expire);
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(username)
                .setIssuedAt(nowDate)
                .setExpiration(expireDate)// 7天過期
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    // 解析jwt
    public Claims getClaimByToken(String jwt) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(jwt)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    // jwt是否过期
    public boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

}
