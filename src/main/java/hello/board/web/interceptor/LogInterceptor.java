package hello.board.web.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Slf4j
public class LogInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //요청 Url, 요청 ip,
        String requestURI = request.getRequestURI();
        String remoteAddr = request.getRemoteAddr();
        String uuid = UUID.randomUUID().toString();

        request.setAttribute("uuid", uuid);

        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler; //호출할 컨트롤러 메서드
        }
        log.info("REQUEST  [{}] [{}] [{}]", uuid, requestURI, remoteAddr);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String uuid = request.getAttribute("uuid").toString();
        String requestURI = request.getRequestURI();

        log.info("RESPONSE [{}] [{}]", uuid, requestURI);

        if (ex != null) {
            log.error("afterCompletion error!", ex);
        }
    }
}
