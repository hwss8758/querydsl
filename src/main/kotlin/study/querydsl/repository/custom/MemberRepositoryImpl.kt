package study.querydsl.repository.custom

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.beans.factory.annotation.Autowired
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