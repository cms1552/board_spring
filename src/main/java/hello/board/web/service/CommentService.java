package hello.board.web.service;

import hello.board.domain.Board;
import hello.board.domain.Comment;
import hello.board.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository repository;
    public Comment createComment(Comment comment) {
        Comment save = repository.save(comment);
        return save;
    }

    public Comment findById(Long id) {
        return repository.findById(id).orElseThrow(
                () -> {throw new IllegalStateException("존재하지 않는 댓글 입니다.");}
        );
    }

    public List<Comment> findCommentsByBoardAndParentIsNullOrderByCreateAtAsc(Board board) {
        return repository.findCommentsByBoardAndParentIsNullOrderByCreateAtAsc(board);
    }
}
