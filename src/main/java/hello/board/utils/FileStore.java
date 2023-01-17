package hello.board.utils;

import hello.board.domain.UploadFile;
import hello.board.repository.UploadFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileStore {

    @Value("${file.dir}")
    private String fileDir;

    public String getFullPath(String fileName) {
        return fileDir + fileName;
    }

    public List<UploadFile> storeFiles(List<MultipartFile> multipartFiles) throws IOException {
        List<UploadFile> uploadFiles = new ArrayList<>();
        for (MultipartFile file : multipartFiles) {
            if (!file.isEmpty()) {
                UploadFile uploadFile = storeFile(file);
                uploadFiles.add(uploadFile);
            }
        }

        return uploadFiles;
    }


    private UploadFile storeFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }


        // 저장
        String originalFilename = multipartFile.getOriginalFilename();
        long size = multipartFile.getSize();
        String storeFileName = createStoreFileName(originalFilename);
        String ext = extractExt(originalFilename);
        log.info("[{}] [{}] [{}]",originalFilename, storeFileName, ext);

        multipartFile.transferTo(new File(getFullPath(storeFileName)));

        return new UploadFile(originalFilename, storeFileName, size, ext);
    }

    private String createStoreFileName(String originalFileName) {
        String uuid = UUID.randomUUID().toString();
        String ext = extractExt(originalFileName);
        return uuid + ext;
    }

    private String extractExt(String originalFileName) {
        int pos = originalFileName.lastIndexOf(".");
        return originalFileName.substring(pos);
    }
}
