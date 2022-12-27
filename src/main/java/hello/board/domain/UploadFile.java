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
    private String path;
    private Integer down_count;
    private Integer size;
    private String ex;
    private Boolean is_image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;
}
