package study.querydsl.entity

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@SpringBootTest
@Transactional
class MemberTest {

    @Autowired
    lateinit var em: EntityManager

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun testEntity() {
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

        em.flush()
        em.clear()

        val members = em.createQuery("select m from Member m", Member::class.java)
                .resultList

        println("==========================================")
        members.forEach {
            println("member = " + objectMapper.writeValueAsString(it))
            println("-> member.team = " + objectMapper.writeValueAsString(it.team))
        }
        println("==========================================")
    }

    private fun makeMember(teamA: Team, username: String, age: Int): Member {
        val member = Member(username, age, teamA)
        return member
    }

    private fun makeTeam(name: String): Team {
        val team = Team(name)
        return team
    }
}