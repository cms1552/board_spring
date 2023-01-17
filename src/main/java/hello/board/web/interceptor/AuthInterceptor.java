package hello.board.web.interceptor;

import hello.board.web.annotation.Auth;
import hello.board.web.constant.BasicConstant;
import hello.board.web.constant.SessionConstant;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 요청 url이 정적 리소스 or API 둘 중 하나 일수도 있다.
        // 그래서 구분 해서 캐스팅 해줘야함.
        HandlerMethod method = checkHandlerMethod(handler);

        String requestURI = request.getRequestURI();

        if (method == null) {
            return true;
        }

        Auth auth = method.getMethodAnnotation(Auth.class);

        // 인증이 필요 없는 메소드
        if (auth == null) {
            return true;
        }

        // Auth 가 있을 경우, 세션이 있는지 체크
        HttpSession session = request.getSession();
        if (session == null) {
            //로긴 화면으로 이동, 쿼리 파라미터에 original request URI 추가
            response.sendRedirect(request.getContextPath() + "/user/login?" + BasicConstant.REQUEST_URI + "=" + requestURI);
            return false;
        }

        // 세션이 있지만 유효한 세션인지 체크
        Object attribute = session.getAttribute(SessionConstant.LOGIN_ID);
        if (attribute == null) {
            // 로긴 화면으로 이동, 쿼리 파라미터에 original request URI 추가
            response.setHeader(BasicConstant.REQUEST_URI, requestURI);
            response.sendRedirect(request.getContextPath() + "/user/login?" + BasicConstant.REQUEST_URI + "=" + requestURI);
            return false;
        }

        // 어드민 추가시, 어드민일 경우

        // 모두 통과
        return true;
    }

    private HandlerMethod checkHandlerMethod(Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) handler;
            return method;
        }
        return null;
    }
}
