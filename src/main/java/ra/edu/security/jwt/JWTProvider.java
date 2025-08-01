package ra.edu.security.jwt;

import io.jsonwebtoken.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
@Getter
public class JWTProvider {
    @Value("${jwt_secret}")
    private String jwtSecret;
    @Value("${jwt_expire}")
    private int jwtExpire;
    @Value("${jwt_refresh}")
    private int jwtRefresh;

    public String generateToken(String username){
        Date now = new Date();
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + jwtExpire))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public boolean validateToken(String token){
        try{
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        }catch (ExpiredJwtException e){
            log.error("Token JWT đã hết hạn!");
        }catch (UnsupportedJwtException e){
            log.error("Token JWT không được hỗ trợ!");
        }catch (MalformedJwtException e){
            log.error("Token JWT không đúng định dạng!");
        }catch (SignatureException e){
            log.error("Lỗi chữ ký trong token JWT!");
        }catch (IllegalArgumentException e){
            log.error("Tham số token JWT không hợp lệ!");
        }
        return false;
    }

    public String getUsernameFromToken(String token){
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }
}
