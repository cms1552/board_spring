package hello.board.web.service;

import hello.board.domain.User;
import hello.board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    public User userSave(User user) {
        User savedUser = repository.save(user);
        return savedUser;
    }

    public User findByLoginId(String loginId) {
       return repository.findByLoginId(loginId);
    }

    public boolean loginIdDuplicateCheck(String loginId) {
        User byLoginId = repository.findByLoginId(loginId);
        if (byLoginId == null) {
            return true;
        }
        return false;
    }
}
