package hello.board.web.controller;

import hello.board.domain.Board;
import hello.board.domain.UploadFile;
import hello.board.domain.User;
import hello.board.repository.UploadFileService;
import hello.board.utils.FileStore;
import hello.board.web.DTO.BoardDto;
import hello.board.web.DTO.BoardSearchCondition;
import hello.board.web.annotation.Auth;
import hello.board.web.constant.SessionConstant;
import hello.board.web.service.BoardService;
import hello.board.web.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    public String boardNewForm(@SessionAttribute(name = SessionConstant.LOGIN_ID) String loginId, @ModelAttribute("board") BoardDto boardDto, Model model) throws IOException {

        log.info("boardDto = [{}] [{}] [{}] [{}]", boardDto.getTitle(), boardDto.getContent(), loginId, boardDto.getAttachFiles());

        User user = userService.findByLoginId(loginId);

//        List<MultipartFile> attachFiles = boardDto.getAttachFiles();
//        for (MultipartFile file : attachFiles) {
//            log.info("file info : getOriginalName() [{}], getName() [{}], getSize() [{}], ", file.getOriginalFilename(), file.getName(), file.getSize());
//        }
//        List<UploadFile> uploadFiles = fileStore.storeFiles(attachFiles);

        // board build
        Board board = Board.builder()
                .title(boardDto.getTitle())
                .content(boardDto.getContent())
                .user(user)
                //.uploadFiles(uploadFiles)
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
        return "redirect:/";
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

}
