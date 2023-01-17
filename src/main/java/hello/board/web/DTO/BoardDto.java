package hello.board.web.DTO;

import hello.board.domain.User;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class BoardDto {

    private String title;
    private String content;
    private String userLoginId;
    private List<MultipartFile> attachFiles;
}
