package hello.board.web.auth;

import hello.board.domain.User;
import hello.board.repository.UserRepository;
import hello.board.web.constant.SessionConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionLoginService implements LoginService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.invalidate();
    }

    @Override
    public User login(User user) {

        // 아이디가 존재 하지 않을 경우
        User byLoginId = userRepository.findByLoginId(user.getLoginId());
        if (byLoginId == null) {
            throw new IllegalArgumentException("존재하지 않는 아이디 입니다.");
        }

        String encPassword = byLoginId.getPassword();

        // 비밀번호 체크
        boolean matches = bCryptPasswordEncoder.matches(user.getPassword(), encPassword);

        if (matches) {
            return byLoginId;
        }else {
            throw new IllegalStateException("아이디 또는 비밀번호가 맞지 않습니다.");
        }
    }
}
