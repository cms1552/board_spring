package hello.board.web.controller;

import hello.board.domain.Board;
import hello.board.domain.Comment;
import hello.board.domain.UploadFile;
import hello.board.domain.User;
import hello.board.web.service.UploadFileService;
import hello.board.utils.FileStore;
import hello.board.web.DTO.BoardDto;
import hello.board.web.DTO.BoardSearchCondition;
import hello.board.web.DTO.CommentDto;
import hello.board.web.annotation.Auth;
import hello.board.web.constant.SessionConstant;
import hello.board.web.service.BoardService;
import hello.board.web.service.CommentService;
import hello.board.web.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final UserService userService;
    private final CommentService commentService;
    private final UploadFileService uploadFileService;
    private final FileStore fileStore;

    // 게시판 리스트
    @GetMapping
    public String boardList(@RequestParam(value = "page", required = false) Integer page, Model model) {

        // null or 음수
        if (page == null || page.intValue() < 0) {
            page = 0;
        }

        Page<Board> boards = boardService.searchPageSimple(page);
        model.addAttribute("boards", boards);

        // 공통 로직
//        int maxPageSize = 5;
//        model.addAttribute("maxPageSize", maxPageSize);
        // 공통 로직

        model.addAttribute("cond", new BoardSearchCondition());

        log.info("Page info getTotalElements()=[{}] getTotalPages()=[{}] getNumber()=[{}] getNumberOfElements()=[{}] getSize()=[{}]", boards.getTotalElements(), boards.getTotalPages(), boards.getNumber(), boards.getNumberOfElements(), boards.getSize());

        return "boardList";
    }

    // 게시판 조건 검색 리스트
    @PostMapping
    public String boardConditionList(@RequestParam(value = "page", required = false) Integer page, @ModelAttribute("cond") BoardSearchCondition cond, Model model) {
        // null or 음수
        if (page == null || page.intValue() < 0) {
            page = 0;
        }


        // index에 따라서 10크기로 잘라서 페이징
        Page<Board> boards = boardService.searchPageCondition(cond, page);
        model.addAttribute("boards", boards);

        // 공통 로직
//        int maxPageSize = 5;
//        model.addAttribute("maxPageSize", maxPageSize);
        // 공통 로직

        log.info("Page info getTotalElements()=[{}] getTotalPages()=[{}] getNumber()=[{}] getNumberOfElements()=[{}] getSize()=[{}]", boards.getTotalElements(), boards.getTotalPages(), boards.getNumber(), boards.getNumberOfElements(), boards.getSize());
        return "boardList";
    }

    // BoardList 상세 페이지
    @GetMapping("/{id}")
    public String board(@PathVariable Long id, Model model) {

        Board board = boardService.findById(id);
        model.addAttribute("board", board);

        List<UploadFile> uploadFiles = board.getUploadFiles();
        log.info("BoardController uploadFiles = [{}]", uploadFiles);

        model.addAttribute("comment", new CommentDto());

        //댓글 전달
        List<Comment> commentsByBoardId = commentService.findCommentsByBoardAndParentIsNullOrderByCreateAtAsc(board);
        model.addAttribute("comments", commentsByBoardId);
        return "board";
    }

    // 게시판 글 쓰기

    @Auth
    @GetMapping("/new")
    public String boardNewForm(Model model) {

        model.addAttribute("board", new BoardDto());

        return "boardNewForm";
    }

    @Auth
    @PostMapping("/new")
    public String boardNewForm(@SessionAttribute(name = SessionConstant.LOGIN_ID) String loginId, @Validated @ModelAttribute("board") BoardDto boardDto, BindingResult bindingResult, Model model) throws IOException {

        log.info("boardDto = [{}] [{}] [{}] [{}]", boardDto.getTitle(), boardDto.getContent(), loginId, boardDto.getAttachFiles());

        if (bindingResult.hasErrors()) {
            log.info("Board create validate exception {}", bindingResult);
            return "boardNewForm";
        }

        User user = userService.findByLoginId(loginId);

        // board build
        Board board = Board.builder()
                .title(boardDto.getTitle())
                .content(boardDto.getContent())
                .user(user)
                .build();

        // dto 첨부 파일 가져오기
        List<MultipartFile> attachFiles = boardDto.getAttachFiles();
        for (MultipartFile file : attachFiles) {
            log.info("file info : getOriginalName() [{}], getName() [{}], getSize() [{}], ", file.getOriginalFilename(), file.getName(), file.getSize());
        }
        // 첨부파일 저장 및 UploadFiles 로 객체 변환
        // uploadfile entity save
        // uploadfile 객체의 board_id -> null
        // 연관 관계를 맺어줘야한다.
        // board_id 가 필요하다. -> board 생성이 돼야 함.
        List<UploadFile> uploadFiles = fileStore.storeFiles(attachFiles);

        // create board
        Board savedBoard = boardService.createBoard(board);

        // 게시판 생성 후 파일 객체 생성
        for (UploadFile file : uploadFiles) {
            file.updateBoardId(savedBoard);
            uploadFileService.createUploadFile(file);
        }
        return "redirect:/board/" + savedBoard.getId().toString();
    }

    // 첨부파일 다운로드 API
    @GetMapping("/attach/{fileId}")
    public ResponseEntity<UrlResource> downloadAttach(@PathVariable Long fileId) throws MalformedURLException {

        UploadFile uploadFile = uploadFileService.findById(fileId);

        String originalName = uploadFile.getOriginal_name();
        String storedName = uploadFile.getStored_name();
        Long size = uploadFile.getSize();

        UrlResource resource = new UrlResource("file:" + fileStore.getFullPath(storedName));
        String encodedFileName = UriUtils.encode(originalName, StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"" + encodedFileName + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }

    // 게시물 수정 폼
    @Auth
    @GetMapping("/modify/{id}")
    public String boardModifyForm(@PathVariable Long id, Model model) {

        // id로 title, content, attachFile 가져옴
        Board board = boardService.findById(id);
        log.info("board :  [{}] [{}] [{}] ", board.getId(), board.getTitle(), board.getContent());
        model.addAttribute("board", board);
        model.addAttribute("boardDto", new BoardDto());

        return "boardModify";
    }

    // 게시물 수정
    @Auth
    @PostMapping("/modify/{id}")
    public String modifyBoard(@PathVariable Long id, @Validated @ModelAttribute BoardDto boardDto, @RequestParam(name = "deleteFile",required = false) List<Long> deleteFiles, BindingResult bindingResult, Model model) throws IOException {

        log.info("deleteFiles : [{}] ", deleteFiles );

        /**
         * id : 게시물 id
         * boardDto : 게시물 dto
         * deleteFile : 첨부파일이 있는 게시물일 경우, 삭제 버튼 클릭 시 해당 파일 id 전달
         * */

        if (bindingResult.hasErrors()) {
            log.info("board modify validate exception , [{}] ", bindingResult);
            Board board = boardService.findById(id);
            model.addAttribute("board", board);
            return "boardModify";
        }

        boardService.updateBoard(id, boardDto);

        /**
         * 파일 수정
         *
         * 1. 파일 store 삭제 -> 2. 파일 db 삭제
         * */
        if (deleteFiles != null) {
            deleteFiles.forEach(aLong -> {
                UploadFile file = uploadFileService.findById(aLong);
                fileStore.removeFile(file.getStored_name()); // 파일 삭제
                uploadFileService.deleteUploadFile(file);
            });
        }

        /**
         * 파일 수정
         *
         * 3. 파일 추가
         * */

        List<MultipartFile> attachFiles = boardDto.getAttachFiles();
        for (MultipartFile file : attachFiles) {
            log.info("file info : getOriginalName() [{}], getName() [{}], getSize() [{}], ", file.getOriginalFilename(), file.getName(), file.getSize());
        }

        // 첨부파일 저장 및 UploadFiles 로 객체 변환
        List<UploadFile> uploadFiles = fileStore.storeFiles(attachFiles);

        Board board = boardService.findById(id);

        for (UploadFile file : uploadFiles) {
            file.updateBoardId(board);
            uploadFileService.createUploadFile(file);
        }

        return "redirect:/board/" + id;
    }

    // 게시물 삭제
    @Auth
    @GetMapping("/delete/{id}")
    public String deleteBoard(@PathVariable Long id) {

        /**
         *  게시물 삭제시 첨부파일도 함께 삭제
         * */
        fileStore.removeBoardFiles(id);
        boardService.deleteBoard(id);

        return "redirect:/board";
    }
}
