package hello.board.domain;

import com.querydsl.core.annotations.QueryProjection;
import hello.board.web.DTO.UserModifyDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity{
    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    private String loginId;

    private String password;

    private String mailAddress;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Board> boards = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Comment> comments = new ArrayList<>();

    @Builder
    @QueryProjection
    public User(Long id, String login_id, String password, String mailAddress) {
        this.id = id;
        this.loginId = login_id;
        this.password = password;
        this.mailAddress = mailAddress;
    }

    public void updateUser(UserModifyDto userModifyDto) {
        this.password = userModifyDto.getPassword();
        this.mailAddress = userModifyDto.getMailAddress();
    }
}
