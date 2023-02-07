package hello.board.web.DTO;

import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class LoginIdCheckDto {
    @Pattern(regexp = "^[a-z0-9]{4,20}$", message = "아이디는 영어 소문자와 숫자만 사용하여 4~20자리")
    private String loginId;
}
