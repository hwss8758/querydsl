package study.querydsl.repository.querydsl

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import org.springframework.util.StringUtils
import study.querydsl.dto.MemberSearchCondition
import study.querydsl.dto.MemberTeamDto
import study.querydsl.dto.QMemberTeamDto
import study.querydsl.entity.Member
import study.querydsl.entity.QMember
import study.querydsl.entity.QTeam
import javax.persistence.EntityManager

@Repository
class MemberQuerydslRepository() {

    @Autowired
    lateinit var em: EntityManager

    fun findByAll_Querydsl(): MutableList<Member> {
        val jpaQueryFactory = JPAQueryFactory(em)
        val member = QMember.member

        return jpaQueryFactory
            .selectFrom(member)
            .fetch()
    }

    fun findByUsername_Querydsl(username: String): MutableList<Member> {
        val jpaQueryFactory = JPAQueryFactory(em)
        val member = QMember.member

        return jpaQueryFactory
            .selectFrom(member)
            .where(member.username.eq(username))
            .fetch()
    }

    fun searchByBooleanBuilder(condition: MemberSearchCondition): MutableList<MemberTeamDto> {
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
            .where(booleanBuilder(condition))
            .fetch()

    }

    private fun booleanBuilder(condition: MemberSearchCondition): BooleanBuilder {
        val member = QMember.member
        val team = QTeam.team
        val builder = BooleanBuilder()

        if(StringUtils.hasText(condition.username)) builder.and(member.username.eq(condition.username))
        if(StringUtils.hasText(condition.teamName)) builder.and(team.name.eq(condition.teamName))
        if(condition.ageGoe != null) builder.and(member.age.goe(condition.ageGoe))
        if(condition.ageLoe != null) builder.and(member.age.loe(condition.ageLoe))

        return builder
    }

    fun searchByWhere(condition: MemberSearchCondition?): MutableList<MemberTeamDto> {
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