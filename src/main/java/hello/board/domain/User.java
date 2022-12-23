package hello.board.domain;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    private String login_id;

    private String password;

    private LocalDateTime create_at;

    private LocalDateTime update_at;

    private LocalDateTime delete_at;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Board> boards = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Comment> comments = new ArrayList<>();

    @Builder
    @QueryProjection
    public User(Long id, String login_id, String password, LocalDateTime create_at, LocalDateTime update_at, LocalDateTime delete_at) {
        this.id = id;
        this.login_id = login_id;
        this.password = password;
        this.create_at = create_at;
        this.update_at = update_at;
        this.delete_at = delete_at;
    }
}
