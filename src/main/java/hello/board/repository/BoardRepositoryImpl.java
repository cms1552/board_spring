package hello.board.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hello.board.domain.Board;
import hello.board.domain.QBoard;
import hello.board.domain.QUser;
import hello.board.domain.User;
import hello.board.web.DTO.BoardSearchCondition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

import static hello.board.domain.QBoard.board;
import static hello.board.domain.QUser.user;
import static java.util.Optional.ofNullable;
import static org.springframework.util.StringUtils.hasText;

@Slf4j
@Repository
@Transactional
@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public List<Board> search(BoardSearchCondition condition) {



        return queryFactory
                .select(board)
                .from(board)
                .leftJoin(board.user, user)
                .where(
                        userEq(condition.getLoginId()),
                        titleContains(condition.getTitle()),
                        contentContains(condition.getContent()),
                        isAuthorize(condition.getAuthorize())
                ).fetch();
    }


    @Override
    public Page<Board> searchPage(Pageable pageable) {

        long offset = pageable.getOffset();
        int pageSize = pageable.getPageSize();

        List<Board> boards = queryFactory
                .select(board)
                .from(board)
                .leftJoin(board.user, user)
                .offset(offset)
                .limit(pageSize)
                .fetch();

        return new PageImpl<>(boards, pageable, boards.size());
    }

    @Override
    public Page<Board> searchPageCondition(BoardSearchCondition condition, Pageable pageable) {
        long offset = pageable.getOffset();
        int pageSize = pageable.getPageSize();

        List<Board> content = queryFactory
                .select(board)
                .from(board)
                .leftJoin(board.user, user)
                .where(
                        userEq(condition.getLoginId()),
                        titleContains(condition.getTitle()),
                        contentContains(condition.getContent()),
                        isAuthorize(condition.getAuthorize())
                )
                .offset(offset)
                .limit(pageSize)
                .fetch();

        return new PageImpl<>(content, pageable, content.size());
    }

    //    private String loginId;
//    private String title;
//    private String content;
//    private Boolean authorize;
    private BooleanExpression userEq(String loginId) {
        return !hasText(loginId) ? null : board.user.loginId.eq(loginId);
    }

    private BooleanExpression titleContains(String title) {
        return !hasText(title) ? null : board.title.contains(title);
    }

    private BooleanExpression contentContains(String content) {
        return !hasText(content) ? null : board.content.contains(content);
    }

    private BooleanExpression isAuthorize(Boolean authorize) {
        return authorize == null ? null : board.authorize.eq(authorize);
    }
}
