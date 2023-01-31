package hello.board.web.service;

import hello.board.domain.Board;
import hello.board.domain.Comment;
import hello.board.repository.CommentRepository;
import hello.board.web.DTO.DeleteCommentDto;
import hello.board.web.DTO.UpdateCommentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public UpdateCommentDto updateComment(UpdateCommentDto updateCommentDto) {
        Comment comment = repository.findById(updateCommentDto.getId()).orElseThrow(() -> {
            throw new IllegalStateException("존재하지 않는 댓글 입니다.");
        });
        comment.updateCommentContent(updateCommentDto.getContent());
        UpdateCommentDto updateCommentDto1 = new UpdateCommentDto();
        updateCommentDto1.toDto(comment);
        return updateCommentDto1;
    }

    public void deleteComment(DeleteCommentDto deleteCommentDto) {
        Comment comment = repository.findById(deleteCommentDto.getId())
                .orElseThrow(() -> {
                    throw new IllegalStateException("존재하지 않는 댓글 입니다.");
                });
        repository.delete(comment);
    }
}
