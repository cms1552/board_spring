package hello.board.web.DTO;


import hello.board.domain.Board;
import hello.board.domain.Comment;
import hello.board.domain.User;
import lombok.Data;

@Data
public class CommentDto {
    private String content;
    private User user;
    private Board board;
    private Comment parent;

    public Comment toEntity() {
        Comment comment = Comment.builder()
                .content(content)
                .user(user)
                .board(board)
                .parent(parent)
                .build();
        return comment;
    }
}
