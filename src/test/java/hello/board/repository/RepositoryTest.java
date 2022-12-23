package hello.board.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hello.board.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;


import static hello.board.domain.QBoard.board;
import static hello.board.domain.QComment.comment;
import static hello.board.domain.QUser.user;

@SpringBootTest
@Transactional
public class RepositoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    UserRepository userRepository;
    @Autowired
    BoardRepository boardRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    JPAQueryFactory queryFactory;

    @Test
    void UserRepo() {
        User user1 = User.builder()
                .login_id("cms")
                .password("1234")
                .build();

        em.persist(user1);

        List<Tuple> fetch = queryFactory
                .select(user.login_id, user.password)
                .from(user)
                .fetch();

        for (Tuple tuple : fetch) {
            System.out.println("tuple = " + tuple);
        }
    }

    @Test
    void BoardRepo() {
        User user = User.builder()
                .login_id("cms")
                .password("123")
                .build();

        em.persist(user);

        Board board = Board.builder()
                .user(user)
                .title("Board num1")
                .content("hahaha this is content")
                .build();

        em.persist(board);

        List<Board> fetch = queryFactory
                .select(QBoard.board)
                .from(QBoard.board)
                .fetch();

        for (Board board1 : fetch) {
            System.out.println(board1.getTitle() + " " + board1.getContent() + " " + board1.getUser());
        }
    }

    @Test
    void CommentRepo() {
        User user = User.builder()
                .login_id("Cms")
                .password("123")
                .build();

        em.persist(user);

        Board board = Board.builder()
                .user(user)
                .content("haha this is content")
                .authorize(true)
                .build();

        em.persist(board);

        Comment comment = Comment.builder()
                .user(user)
                .board(board)
                .content("this is comment")
                .build();

        commentRepository.save(comment);

        List<Tuple> fetch = queryFactory
                .select(QComment.comment.board.content, QComment.comment.content, QComment.comment.user.login_id)
                .from(QComment.comment)
                .fetch();

        for (Tuple tuple : fetch) {
            System.out.println("tuple = " + tuple);
        }
    }
}
