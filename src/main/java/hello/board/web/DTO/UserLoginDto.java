package hello.board.web.DTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserLoginDto {
    @NotBlank(message = "아이디는 필수 입력란 입니다.")
    private String loginId;

    @NotBlank(message = "비밀번호는 필수 입력란 입니다.")
    private String password;
}
