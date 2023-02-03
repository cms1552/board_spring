package hello.board.web.service;

import hello.board.domain.Board;
import hello.board.domain.UploadFile;
import hello.board.repository.UploadFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadFileService {

    private final UploadFileRepository uploadFileRepository;

    public UploadFile findById(Long id) {
        return uploadFileRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 파일 입니다."));
    }

    public Long createUploadFile(UploadFile uploadFile) {
        UploadFile save = uploadFileRepository.save(uploadFile);
        return save.getId();
    }

    public void updateBoardId(Long fileId, Board board) {
        UploadFile uploadFile = uploadFileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 파일 입니다."));

        uploadFile.updateBoardId(board);
    }

    public void deleteUploadFile(UploadFile uploadFile) {
        uploadFileRepository.delete(uploadFile);
    }
}
