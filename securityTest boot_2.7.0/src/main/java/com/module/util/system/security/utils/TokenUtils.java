package com.module.util.system.security.utils;

import io.jsonwebtoken.*;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.module.util.system.security.test.UserVo;

import java.security.Key;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * JWT 관련된 토큰 Util
 *
 * @author lee
 * @fileName TokenUtils
 * @since 2022.12.23
 */
public class TokenUtils {
	private static final Logger logger = LoggerFactory.getLogger(TokenUtils.class);

    //    @Value(value = "${custom.jwt-secret-key}")
    private static final String jwtSecretKey = "exampleSecretKey";
    
    

    /**
     * 사용자 정보를 기반으로 토큰을 생성하여 반환 해주는 메서드
     *
     * @param UserVo UserVo : 사용자 정보
     * @return String : 토큰
     */
    public static String generateJwtToken(UserVo UserVo) {
        // 사용자 시퀀스를 기준으로 JWT 토큰을 발급하여 반환해줍니다.
        JwtBuilder builder = Jwts.builder()
                .setHeader(createHeader())                              // Header 구성
                .setClaims(createClaims(UserVo))                       // Payload - Claims 구성
                .setSubject(String.valueOf(UserVo))        // Payload - Subject 구성
                .signWith(SignatureAlgorithm.HS256, createSignature())  // Signature 구성
                .setExpiration(createExpiredDate());                    // Expired Date 구성
        return builder.compact();
    }

    /**
     * 토큰을 기반으로 사용자 정보를 반환 해주는 메서드
     *
     * @param token String : 토큰
     * @return String : 사용자 정보
     */
    public static String parseTokenToUserInfo(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * 유효한 토큰인지 확인 해주는 메서드
     *
     * @param token String  : 토큰
     * @return boolean      : 유효한지 여부 반환
     */
    public static boolean isValidToken(String token) {
        try {
            Claims claims = getClaimsFormToken(token);
            
            logger.info("expireTime :" + claims.getExpiration());
            logger.info("userId :" + claims.get("userId"));
            logger.info("userNm :" + claims.get("userNm"));
            logger.info("userId :" + claims.get("userName"));
            logger.info("userNm :" + claims.get("userPassword"));
            
            for (Map.Entry<String, Object> entry : claims.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                System.out.println(key + ": " + value);
            }

            return true;
        } catch (ExpiredJwtException exception) {
            logger.error("Token Expired");
            return false;
        } catch (JwtException exception) {
            logger.error("Token Tampered");
            return false;
        } catch (NullPointerException exception) {
            logger.error("Token is null");
            return false;
        }
    }

    /**
     * Header 내에 토큰을 추출합니다.
     *
     * @param header 헤더
     * @return String
     */
    public static String getTokenFromHeader(String header) {
        return header.split(" ")[1];
    }

    /**
     * 토큰의 만료기간을 지정하는 함수
     *
     * @return Calendar
     */
    private static Date createExpiredDate() {
        // 토큰 만료시간은 30일으로 설정
        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR, 8);     // 8시간
        // c.add(Calendar.DATE, 1);         // 1일
        return c.getTime();
    }

    /**
     * JWT의 "헤더" 값을 생성해주는 메서드
     *
     * @return HashMap<String, Object>
     */
    private static Map<String, Object> createHeader() {
        Map<String, Object> header = new HashMap<>();

        header.put("typ", "JWT");
        header.put("alg", "HS256");
        header.put("regDate", System.currentTimeMillis());
        return header;
    }

    /**
     * 사용자 정보를 기반으로 클래임을 생성해주는 메서드
     *
     * @param UserVo 사용자 정보
     * @return Map<String, Object>
     */
    private static Map<String, Object> createClaims(UserVo UserVo) {
        // 공개 클레임에 사용자의 이름과 이메일을 설정하여 정보를 조회할 수 있다.
        Map<String, Object> claims = new HashMap<>();

        logger.info("userId :" + UserVo.getUsername());
        logger.info("userNm :" + UserVo.getPassword());

        claims.put("userId", UserVo.getUsername());
        claims.put("userNm", UserVo.getPassword());
        return claims;
    }

    /**
     * JWT "서명(Signature)" 발급을 해주는 메서드
     *
     * @return Key
     */
    private static Key createSignature() {
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(jwtSecretKey);
        return new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());
    }


    /**
     * 토큰 정보를 기반으로 Claims 정보를 반환받는 메서드
     *
     * @param token : 토큰
     * @return Claims : Claims
     */
    private static Claims getClaimsFormToken(String token) {
        return Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(jwtSecretKey))
                .parseClaimsJws(token).getBody();
    }

    /**
     * 토큰을 기반으로 사용자 정보를 반환받는 메서드
     *
     * @param token : 토큰
     * @return String : 사용자 아이디
     */
    public static String getUserIdFromToken(String token) {
        Claims claims = getClaimsFormToken(token);
        return claims.get("userId").toString();
    }
    
    
    ///////////////// version 2 ////// ///////////////// version 2 ////// ///////////////// version 2 ////// ///////////////// version 2 //////

    public static String createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // 토큰의 expire 시간을 설정
        Date validity = createExpiredDate();
        
        UserVo user = (UserVo) authentication.getPrincipal();
        
        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities) // 정보 저장
                .claim("userId", user.getUsername()) // 정보 저장
                .claim("userPassword", user.getPassword()) // 정보 저장
                .claim("userVo", user) // 정보 저장
                .signWith(SignatureAlgorithm.HS256, createSignature())  // 사용할 암호화 알고리즘과 , signature 에 들어갈 secret값 세팅
                .setExpiration(validity) // set Expire Time 해당 옵션 안넣으면 expire안함
                .compact();
    }
    
    
    

    // 토큰으로 클레임을 만들고 이를 이용해 유저 객체를 만들어서 최종적으로 authentication 객체를 리턴
    public static Authentication getAuthentication(String token) {
        Claims claims = getClaimsFormToken(token);
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserVo principal = new UserVo(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    
}
