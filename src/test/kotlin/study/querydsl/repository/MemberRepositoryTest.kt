package study.querydsl.repository

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.transaction.annotation.Transactional
import study.querydsl.dto.MemberSearchCondition
import study.querydsl.entity.Member
import study.querydsl.entity.Team
import javax.persistence.EntityManager

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    lateinit var em: EntityManager

    @Autowired
    lateinit var memberRepository: MemberRepository


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
    fun memberCustomTest() {
        val memberSearchCondition =
                MemberSearchCondition("member2", "teamA", 15, 35)
        val searchByWhere = memberRepository.search(memberSearchCondition)

        println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
        for (memberTeamDto in searchByWhere) {
            println(memberTeamDto.memberId)
            println(memberTeamDto.username)
            println(memberTeamDto.age)
            println(memberTeamDto.teamId)
            println(memberTeamDto.teamName)
        }
        println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
    }

    @Test
    fun searchPageSimpleTest() {
        val memberSearchCondition =
                MemberSearchCondition("member2", "teamA", 15, 35)
        val searchByWhere =
                memberRepository.searchPageSimple(memberSearchCondition, PageRequest.of(0, 1))

        println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
        for (memberTeamDto in searchByWhere) {
            println(memberTeamDto.memberId)
            println(memberTeamDto.username)
            println(memberTeamDto.age)
            println(memberTeamDto.teamId)
            println(memberTeamDto.teamName)
        }
        println(searchByWhere.totalPages)
        println(searchByWhere.size)
        println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
    }

    @Test
    fun searchPageComplexTest() {
        val memberSearchCondition =
                MemberSearchCondition("member2", "teamA", 15, 35)
        val searchByWhere =
                memberRepository.searchPageComplex(memberSearchCondition, PageRequest.of(0, 1))

        println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
        for (memberTeamDto in searchByWhere) {
            println(memberTeamDto.memberId)
            println(memberTeamDto.username)
            println(memberTeamDto.age)
            println(memberTeamDto.teamId)
            println(memberTeamDto.teamName)
        }
        println(searchByWhere.totalPages)
        println(searchByWhere.size)
        println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
    }

    @Test
    fun searchPageComplexTest2() {
        val memberSearchCondition =
                MemberSearchCondition("member2", "teamA", 15, 35)
        val searchByWhere =
                memberRepository.searchPageComplex2(memberSearchCondition, PageRequest.of(0, 1))

        println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
        for (memberTeamDto in searchByWhere) {
            println(memberTeamDto.memberId)
            println(memberTeamDto.username)
            println(memberTeamDto.age)
            println(memberTeamDto.teamId)
            println(memberTeamDto.teamName)
        }
        println(searchByWhere.totalPages)
        println(searchByWhere.size)
        println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
    }
}