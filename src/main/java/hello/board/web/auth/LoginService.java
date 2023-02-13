package hello.board.web.auth;

import hello.board.domain.User;

import javax.servlet.http.HttpServletRequest;

public interface LoginService {

    public default void logout() {

    }

    public void logout(HttpServletRequest request);
    public <T> T login(User user);
}
