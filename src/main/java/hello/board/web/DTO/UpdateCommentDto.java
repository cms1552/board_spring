package hello.board.web.DTO;

import hello.board.domain.Comment;
import lombok.Data;

@Data
public class UpdateCommentDto {
    private Long id;
    private String content;

    public void toDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
    }
}
