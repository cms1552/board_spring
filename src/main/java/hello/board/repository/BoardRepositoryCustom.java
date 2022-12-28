package hello.board.repository;

import hello.board.domain.Board;
import hello.board.web.DTO.BoardSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BoardRepositoryCustom {
    List<Board> search(BoardSearchCondition condition);

    Page<Board> searchPage(Pageable pageable);
    Page<Board> searchPageCondition(BoardSearchCondition condition, Pageable pageable);
}
