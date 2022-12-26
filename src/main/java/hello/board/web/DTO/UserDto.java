package hello.board.web.DTO;

import hello.board.domain.Board;
import hello.board.domain.Comment;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserDto {

    @NotNull(message = "아이디는 필수 입력란 입니다.")
    private String login_id;

    @NotNull(message = "비밀번호는 필수 입력란 입니다.")
    private String password;

}
