package com.flab.shoeauction.domain.users.user;

import static com.flab.shoeauction.domain.users.user.QUser.user;
import static org.springframework.util.StringUtils.hasText;

import com.flab.shoeauction.controller.dto.AdminDto.SearchRequest;
import com.flab.shoeauction.controller.dto.AdminDto.UsersResponse;
import com.flab.shoeauction.domain.users.common.UserLevel;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<UsersResponse> searchByUsers(SearchRequest searchRequest, Pageable pageable) {
        QueryResults<UsersResponse> results = jpaQueryFactory
            .select(Projections.fields(UsersResponse.class,
                user.id,
                user.email,
                user.userLevel))
            .from(user)
            .where(
                userEmailEq(searchRequest.getEmail()),
                userIdEq(searchRequest.getId()),
                userLevelEq(searchRequest.getUserLevel())
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetchResults();

        List<UsersResponse> users = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(users, pageable, total);
    }


    private BooleanExpression userIdEq(Long userId) {
        return userId != null ? user.id.eq(userId) : null;
    }

    private BooleanExpression userEmailEq(String userEmail) {
            return hasText(userEmail) ? user.email.endsWith(userEmail) : null;
    }

    private BooleanExpression userLevelEq(UserLevel userLevel) {
        return userLevel != null ? user.userLevel.eq(userLevel) : null;
    }
}
