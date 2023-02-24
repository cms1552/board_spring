package hello.board.web.service;

import hello.board.domain.User;
import hello.board.repository.UserRepository;
import hello.board.web.DTO.LoginIdCheckDto;
import hello.board.web.DTO.UserModifyDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public boolean loginIdDuplicateCheck(LoginIdCheckDto loginId) {
        User byLoginId = repository.findByLoginId(loginId.getLoginId());
        if (byLoginId == null) {
            return true;
        }
        return false;
    }

    @Transactional
    public UserModifyDto userUpdate(UserModifyDto userModifyDto) {
        User byLoginId = repository.findByLoginId(userModifyDto.getLoginId());
        byLoginId.updateUser(userModifyDto);
        UserModifyDto userModifyDto1 = new UserModifyDto();
        userModifyDto1.toDto(byLoginId);
        return userModifyDto1;
    }
}
