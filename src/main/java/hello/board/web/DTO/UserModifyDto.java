package hello.board.web.DTO;

import hello.board.domain.User;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class UserModifyDto {

    @NotBlank
    private String loginId;
    @NotBlank(message = "비밀번호는 필수 입력란 입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,16}$", message = "비밀번호는 8~16자리수여야 합니다. 영문 대소문자, 숫자, 특수문자를 1개 이상 포함해야 합니다.")
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수 입력란 입니다.")
    private String passwordCheck;
    @NotBlank(message = "이메일은 필수 입력란 입니다.")
    @Email
    private String mailAddress;

    public void toDto(User user) {
        this.loginId = user.getLoginId();
        this.password = user.getPassword();
        this.mailAddress = user.getMailAddress();
    }
}
