package hello.board.web.auth;

import hello.board.domain.User;
import hello.board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionLoginService implements LoginService {

    private final UserRepository userRepository;

    @Override
    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.invalidate();
    }

    @Override
    public User login(User user) {

        User byLoginId = userRepository.findByLoginId(user.getLoginId());

        if (byLoginId.getPassword().equals(user.getPassword())) {
            return byLoginId;
        }else {
            throw new IllegalStateException("아이디 또는 비밀번호가 맞지 않습니다.");
        }
    }
}
