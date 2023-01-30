package hello.board.web.controller;

import hello.board.domain.Board;
import hello.board.domain.Comment;
import hello.board.domain.User;
import hello.board.web.DTO.CommentDto;
import hello.board.web.constant.SessionConstant;
import hello.board.web.service.BoardService;
import hello.board.web.service.CommentService;
import hello.board.web.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;
    private final BoardService boardService;
    private final UserService userService;

    @PostMapping("/create")
    public String createComment(@SessionAttribute(name = SessionConstant.LOGIN_ID)String loginId, @ModelAttribute CommentDto commentDto, @RequestParam("board1") Long board_id, @RequestParam(value = "parentId", required = false) Long parent_id) {

        log.info("commentDto [{}]" , commentDto);
        log.info("board_id [{}] ", board_id);
        log.info("parent_id [{}] ", parent_id);

        Board board = boardService.findById(board_id);
        User user = userService.findByLoginId(loginId);
        commentDto.setBoard(board);
        commentDto.setUser(user);
        if (parent_id != null) {
            Comment parent = commentService.findById(parent_id);
            commentDto.setParent(parent);
        }

        Comment comment = commentDto.toEntity();
        commentService.createComment(comment);
        return "redirect:/board/" + board_id;
    }

    @PostMapping("/delete")
    public void deleteComment() {

    }
}
