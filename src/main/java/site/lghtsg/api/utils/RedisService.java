package site.lghtsg.api.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static site.lghtsg.api.config.Secret.Secret.JWT_SECRET_KEY;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public void setValues(String key, String value){
        redisTemplate.opsForValue().set(key, value);
    }

    // 만료시간 설정 -> 자동 삭제
    @Transactional
    public void setValuesWithTimeout(String key, String value, long timeout){
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.MILLISECONDS);
    }

    public String getValues(String key){
        return redisTemplate.opsForValue().get(key);
    }

    @Transactional
    public void deleteValues(String key) {
        redisTemplate.delete(key);
    }

    // token 유효성 검사
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(JWT_SECRET_KEY).parseClaimsJws(jwtToken);
            ValueOperations<String, String> logoutValueOperations = redisTemplate.opsForValue();
            if (logoutValueOperations.get(jwtToken) != null) {
                return false;
            }
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
