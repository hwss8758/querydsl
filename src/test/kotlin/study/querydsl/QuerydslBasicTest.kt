package study.querydsl

import com.querydsl.jpa.impl.JPAQueryFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import study.querydsl.entity.Member
import study.querydsl.entity.QMember
import study.querydsl.entity.Team
import javax.persistence.EntityManager
import javax.transaction.Transactional

@SpringBootTest
@Transactional
class QuerydslBasicTest {

    @Autowired
    lateinit var em: EntityManager

    @BeforeEach
    fun before() {
        val teamA = makeTeam("teamA")
        val teamB = makeTeam("teamB")

        em.persist(teamA)
        em.persist(teamB)

        val member1 = makeMember(teamA, "member1", 10)
        val member2 = makeMember(teamA, "member2", 20)
        val member3 = makeMember(teamB, "member3", 30)
        val member4 = makeMember(teamB, "member4", 40)

        em.persist(member1)
        em.persist(member2)
        em.persist(member3)
        em.persist(member4)
    }

    private fun makeMember(teamA: Team, username: String, age: Int): Member {
        val member = Member(username, age, teamA)
        return member
    }

    private fun makeTeam(name: String): Team {
        val team = Team(name)
        return team
    }

    @Test
    fun startJPQL() {
        val findByJPQL = em.createQuery("select m from Member as m where m.username = :username", Member::class.java)
                .setParameter("username", "member1")
                .resultList

        assertThat(findByJPQL[0].username).isEqualTo("member1")
    }

    @Test
    fun startQuerydsl() {
        val queryFactory = JPAQueryFactory(em)
        val m = QMember("m")

        val findMember = queryFactory.select(m)
                .from(m)
                .where(m.username.eq("member1"))
                .fetchOne()

        assertThat(findMember?.username).isEqualTo("member1")

    }
}