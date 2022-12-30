package hello.board.web.DTO;

import hello.board.domain.User;
import lombok.Data;

@Data
public class BoardSearchCondition {
    private String selected;
    private String text;
}
