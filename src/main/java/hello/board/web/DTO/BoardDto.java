package hello.board.web.DTO;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class BoardDto {

    @NotBlank(message = "제목은 필수 입력란입니다.")
    private String title;
    @NotBlank(message = "게시물 내용은 필수 입력란입니다.")
    private String content;

    private String userLoginId;
    private List<MultipartFile> attachFiles;
}
