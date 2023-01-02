package hello.board.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hello.board.domain.Board;
import hello.board.web.DTO.BoardSearchCondition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static hello.board.domain.QBoard.board;
import static hello.board.domain.QUser.user;
import static org.springframework.util.StringUtils.hasText;

@Slf4j
@Repository
@Transactional
@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public List<Board> search(BoardSearchCondition condition) {



//        return queryFactory
//                .select(board)
//                .from(board)
//                .leftJoin(board.user, user)
//                .where(
//                        userEq(cond.getLoginId()),
//                        titleContains(cond.getTitle()),
//                        contentContains(cond.getContent())
//                ).fetch();
        return null;
    }


    @Override
    public Page<Board> searchPage(Pageable pageable) {

        long offset = pageable.getOffset();
        int pageSize = pageable.getPageSize();

        List<Board> contents = queryFactory
                .select(board)
                .from(board)
                .leftJoin(board.user, user)
                .offset(offset)
                .limit(pageSize)
                .fetch();

        int size = queryFactory
                .select(board)
                .from(board)
                .leftJoin(board.user, user)
                .fetch().size();

        log.info("totalCount = {}", size);

        return new PageImpl<>(contents, pageable, size);
    }

    @Override
    public Page<Board> searchPageCondition(BoardSearchCondition cond, Pageable pageable) {
        long offset = pageable.getOffset();
        int pageSize = pageable.getPageSize();

        String selected = cond.getSelected();
        String text = cond.getText();
        BooleanExpression ex = null;

        switch (selected) {
            case "loginId":
                ex = userEq(text);
                break;
            case "title":
                ex = titleContains(text);
                break;
            case "content":
                ex = contentContains(text);
                break;
        }

        //getSelected 에 따라 where절이 달라짐
        List<Board> contents = queryFactory
                .select(board)
                .from(board)
                .leftJoin(board.user, user)
                .where(ex)
                .offset(offset)
                .limit(pageSize)
                .fetch();

        int size = queryFactory
                .select(board)
                .from(board)
                .leftJoin(board.user, user)
                .where(ex)
                .fetch().size();

        log.info("totalCount = {}", size);

        return new PageImpl<>(contents, pageable, size);
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
}
