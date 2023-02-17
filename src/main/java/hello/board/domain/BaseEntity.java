package hello.board.domain;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@MappedSuperclass
@Getter
@EntityListeners(value = {AuditingEntityListener.class})
public class BaseEntity {

//    @CreatedDate
//    @Column(updatable = false)
//    private LocalDateTime createAt;

    @Column(updatable = false)
    private String createAt;

//    @LastModifiedDate
//    private LocalDateTime updateAt;

    private String updateAt;

    @PrePersist // 엔티티 저장하기 전
    void onPrePersist() {
        this.createAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.updateAt = createAt;
    }

    @PreUpdate  // 엔티티 수정하기 전
    void onPreUpdate() {
        this.updateAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

}