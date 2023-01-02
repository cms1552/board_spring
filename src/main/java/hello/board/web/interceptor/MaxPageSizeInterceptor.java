package hello.board.web.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class MaxPageSizeInterceptor implements HandlerInterceptor {
    private final int MaxPageSize = 5;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 최대 페이지 수 공통 처리 , 세션 값 저장
        HttpSession session = request.getSession();
        session.setAttribute("MaxPageSize", MaxPageSize);
    }
}
