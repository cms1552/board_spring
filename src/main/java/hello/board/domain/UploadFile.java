package hello.board.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "uploadfile")
public class UploadFile extends BaseEntity{
    @Id
    @GeneratedValue
    @Column(name = "uploadfile_id")
    private Long id;

    private String original_name;
    private String stored_name;
    private Integer down_count;
    private Long size;
    private String ext;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;


    public UploadFile(String originalFilename, String storeFileName, long size, String ext) {
        this.original_name = originalFilename;
        this.stored_name = storeFileName;
        this.size = size;
        this.ext = ext;
    }

    public void updateBoardId(Board board) {
        this.board = board;
    }
}