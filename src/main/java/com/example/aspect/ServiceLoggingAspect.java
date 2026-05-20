package com.example.aspect;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.entity.User;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class ServiceLoggingAspect {

    // Pointcut: com.example.service パッケージ配下の全クラス・全メソッドが対象
    @Pointcut("execution(* com.example.service.*.*(..))")
    public void serviceLayer() {}

    // @Around: メソッド実行の前後を包んで、開始・終了・例外をまとめてログに記録する
    @Around("serviceLayer()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String className  = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String loginUser  = getCurrentUsername();
        String args       = formatArgs(joinPoint.getArgs());

        log.info("[START] {}.{} - ログインユーザー={}, 引数=[{}]", className, methodName, loginUser, args);

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long elapsed = System.currentTimeMillis() - start;
            log.info("[END]   {}.{} - 実行時間={}ms", className, methodName, elapsed);
            return result;
        } catch (Throwable e) {
            long elapsed = System.currentTimeMillis() - start;
            log.error("[ERROR] {}.{} - 実行時間={}ms, 例外={}: {}",
                    className, methodName, elapsed, e.getClass().getSimpleName(), e.getMessage());
            throw e;
        }
    }

    // SecurityContext からログインユーザーのメールアドレスを取得する
    private String getCurrentUsername() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()
                    && !"anonymousUser".equals(auth.getPrincipal())) {
                return auth.getName();
            }
        } catch (Exception ignored) {}
        return "anonymous";
    }

    // User エンティティは @Data により password フィールドも toString() に含まれるため、
    // ログ出力時は id と email のみを表示してパスワードハッシュの漏洩を防ぐ
    private String formatArgs(Object[] args) {
        return Arrays.stream(args)
                .map(arg -> {
                    if (arg instanceof User u) {
                        return "User{id=" + u.getId() + ", email=" + u.getEmail() + "}";
                    }
                    return String.valueOf(arg);
                })
                .collect(Collectors.joining(", "));
    }
}
