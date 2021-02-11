package study.querydsl.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import study.querydsl.entity.Member
import study.querydsl.entity.Team
import java.util.stream.IntStream
import javax.annotation.PostConstruct
import javax.persistence.EntityManager

@Profile("local")
@Component
class InitMember {

    @Autowired
    lateinit var initMemberService: InitMemberService

    @PostConstruct
    fun init() {
        initMemberService.init()
    }

    @Component
    class InitMemberService {
        @Autowired
        lateinit var em: EntityManager

        @Transactional
        fun init() {
            val teamA = Team("teamA")
            val teamB = Team("teamB")

            em.persist(teamA)
            em.persist(teamB)

            IntStream.range(0, 101).forEach {

                val selectedTeam = if (it % 2 == 0) teamA
                else teamB

                em.persist(Member("member$it", it, selectedTeam))
            }
        }
    }
}