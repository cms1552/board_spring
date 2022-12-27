package hello.board.domain;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "board")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseEntity{
    @Id
    @GeneratedValue
    @Column(name = "board_id")
    private Long id;

    private String title;
    private String content;

    private Boolean authorize;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "board")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "board")
    private List<UploadFile> uploadFiles = new ArrayList<>();

    @Builder
    @QueryProjection
    public Board(Long id, User user, String title, String content, Boolean authorize, List<UploadFile> uploadFiles) {
        this.id = id;
        this.user = user;
        this.title = title;
        this.content = content;
        this.authorize = authorize;
        this.uploadFiles = uploadFiles;
    }

}
