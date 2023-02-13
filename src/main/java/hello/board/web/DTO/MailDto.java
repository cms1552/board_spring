package hello.board.web.DTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class MailDto {
    @NotBlank(message = "이메일을 입력해주세요")
    private String address;
}
