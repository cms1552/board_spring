package hello.board.web.service;

import hello.board.domain.Board;
import hello.board.repository.BoardRepository;
import hello.board.web.DTO.BoardDto;
import hello.board.web.DTO.BoardSearchCondition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    public Page<Board> searchPageSimple(Integer index) {
        log.info("index = " + index);

        PageRequest pr = PageRequest.of(index, 10);
        Page<Board> boards = boardRepository.searchPage(pr);
        return boards;
    }

    public Page<Board> searchPageCondition(BoardSearchCondition condition, Integer index) {

        PageRequest pr = PageRequest.of(index, 10);
        Page<Board> boards = boardRepository.searchPageCondition(condition, pr);
        return boards;
    }

    public Board findById(Long id) {
        Board board = boardRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("board doesn't exist"));
        return board;
    }

    public Board createBoard(Board board) {
        Board save = boardRepository.save(board);
        return save;
    }

    @Transactional
    public void updateBoard(Long id, BoardDto boardDto) {
        Board board = boardRepository.findById(id).orElseThrow(() -> {
            throw new IllegalStateException("존재하지 않는 게시물 입니다.");
        });

        board.updateBoard(boardDto.getTitle(), boardDto.getContent());
    }

    public void deleteBoard(Long id) {
        boardRepository.deleteById(id);
    }
}
