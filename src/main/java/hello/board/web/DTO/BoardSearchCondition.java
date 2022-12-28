package hello.board.web.DTO;

import hello.board.domain.User;
import lombok.Data;

@Data
public class BoardSearchCondition {
    private String loginId;
    private String title;
    private String content;
    private Boolean authorize;
}
