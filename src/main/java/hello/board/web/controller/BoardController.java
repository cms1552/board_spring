package hello.board.web.controller;

import hello.board.domain.Board;
import hello.board.web.DTO.BoardSearchCondition;
import hello.board.web.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping
    public String boardList(@RequestParam(value = "page", required = false) Integer page, Model model) {
        if(page==null) page = 0;
        Page<Board> boards = boardService.searchPageSimple(page);
        model.addAttribute("boards", boards);

        return "boardList";
    }

    @PostMapping()
    public String boardConditionList(@RequestParam("page") Integer page, @ModelAttribute("condition") BoardSearchCondition condition, Model model) {

        // index에 따라서 10크기로 잘라서 페이징
        Page<Board> boards = boardService.searchPageCondition(condition, page);
        model.addAttribute("boards", boards);

        return "boardList";
    }
}
