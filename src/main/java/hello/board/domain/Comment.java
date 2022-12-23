package hello.board.domain;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "comment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {
    @Id
    @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    private String content;

    private LocalDateTime create_at;
    private LocalDateTime update_at;
    private LocalDateTime delete_at;

    private Long group_id;

    private Long group_order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Board board;

    @Builder
    @QueryProjection
    public Comment(Long id, Board board, User user, Long group_id, Long group_order, String content, LocalDateTime create_at, LocalDateTime update_at, LocalDateTime delete_at) {
        this.id = id;
        this.board = board;
        this.user = user;
        this.group_id = group_id;
        this.group_order = group_order;
        this.content = content;
        this.create_at = create_at;
        this.update_at = update_at;
        this.delete_at = delete_at;
    }
}
