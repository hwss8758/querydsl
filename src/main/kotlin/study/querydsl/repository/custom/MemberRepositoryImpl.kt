package study.querydsl.repository.custom

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.util.StringUtils
import study.querydsl.dto.MemberSearchCondition
import study.querydsl.dto.MemberTeamDto
import study.querydsl.dto.QMemberTeamDto
import study.querydsl.entity.QMember
import study.querydsl.entity.QTeam
import javax.persistence.EntityManager

class MemberRepositoryImpl : MemberRepositoryCustom {

    @Autowired
    lateinit var em: EntityManager

    override fun search(condition: MemberSearchCondition): MutableList<MemberTeamDto> {
        val jpaQueryFactory = JPAQueryFactory(em)
        val member = QMember.member
        val team = QTeam.team

        return jpaQueryFactory
                .select(
                        QMemberTeamDto(
                                member.id.`as`("memberId"),
                                member.username,
                                member.age,
                                team.id.`as`("teamId"),
                                team.name
                        )
                )
                .from(member)
                .leftJoin(member.team, team)
                .where(searchCondition(condition))
                .fetch()

    }

    override fun searchPageSimple(condition: MemberSearchCondition?, pageable: Pageable): Page<MemberTeamDto> {
        val jpaQueryFactory = JPAQueryFactory(em)
        val member = QMember.member
        val team = QTeam.team

        val results = jpaQueryFactory
                .select(
                        QMemberTeamDto(
                                member.id.`as`("memberId"),
                                member.username,
                                member.age,
                                team.id.`as`("teamId"),
                                team.name
                        )
                )
                .from(member)
                .leftJoin(member.team, team)
                .where(searchCondition(condition))
                .offset(pageable.offset)
                .limit(pageable.pageSize.toLong())
                .fetchResults() // 내용과 전체 카운트를 한번에 조회할 수 있다.

        val content: MutableList<MemberTeamDto> = results.results
        val total = results.total

        return PageImpl(content, pageable, total)
    }

    override fun searchPageComplex(condition: MemberSearchCondition?, pageable: Pageable): Page<MemberTeamDto> {
        val jpaQueryFactory = JPAQueryFactory(em)
        val member = QMember.member
        val team = QTeam.team

        val content: MutableList<MemberTeamDto> = jpaQueryFactory
                .select(
                        QMemberTeamDto(
                                member.id.`as`("memberId"),
                                member.username,
                                member.age,
                                team.id.`as`("teamId"),
                                team.name
                        )
                )
                .from(member)
                .leftJoin(member.team, team)
                .where(searchCondition(condition))
                .offset(pageable.offset)
                .limit(pageable.pageSize.toLong())
                .fetch()

        val total: Long = jpaQueryFactory
                .select(member)
                .from(member)
                .leftJoin(member.team, team)
                .where(searchCondition(condition))
                .offset(pageable.offset)
                .limit(pageable.pageSize.toLong())
                .fetchCount()

        return PageImpl(content, pageable, total)
    }

    override fun searchPageComplex2(condition: MemberSearchCondition?,
                                    pageable: Pageable): Page<MemberTeamDto> {
        val jpaQueryFactory = JPAQueryFactory(em)
        val member = QMember.member
        val team = QTeam.team

        val content: MutableList<MemberTeamDto> = jpaQueryFactory
                .select(
                        QMemberTeamDto(
                                member.id.`as`("memberId"),
                                member.username,
                                member.age,
                                team.id.`as`("teamId"),
                                team.name
                        )
                )
                .from(member)
                .leftJoin(member.team, team)
                .where(searchCondition(condition))
                .offset(pageable.offset)
                .limit(pageable.pageSize.toLong())
                .fetch()

        val countQuery = jpaQueryFactory
                .select(member)
                .from(member)
                .leftJoin(member.team, team)
                .where(searchCondition(condition))
                .offset(pageable.offset)
                .limit(pageable.pageSize.toLong())

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchCount)
    }

    private fun searchCondition(condition: MemberSearchCondition?): BooleanExpression? {
        return usernameEq(condition?.username)
                ?.and(teamNameEq(condition?.teamName))
                ?.and(ageGoe(condition?.ageGoe))
                ?.and(ageLoe(condition?.ageLoe))
    }

    private fun ageGoe(ageGoe: Int?): BooleanExpression? {
        val member = QMember.member
        if (ageGoe == null) return null
        return member.age.goe(ageGoe)
    }

    private fun ageLoe(ageLoe: Int?): BooleanExpression? {
        val member = QMember.member
        if (ageLoe == null) return null
        return member.age.loe(ageLoe)
    }

    private fun teamNameEq(teamName: String?): BooleanExpression? {
        val team = QTeam.team
        if (!StringUtils.hasText(teamName)) return null
        return team.name.eq(teamName)
    }

    private fun usernameEq(username: String?): BooleanExpression? {
        val member = QMember.member
        if (!StringUtils.hasText(username)) return null
        return member.username.eq(username)

    }
}