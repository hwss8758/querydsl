package study.querydsl

import com.fasterxml.jackson.databind.ObjectMapper
import com.querydsl.core.Tuple
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.util.AssertionErrors.assertEquals
import study.querydsl.entity.Member
import study.querydsl.entity.QMember
import study.querydsl.entity.QTeam
import study.querydsl.entity.Team
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.PersistenceUnit
import javax.transaction.Transactional

@SpringBootTest
@Transactional
class QuerydslBasicTest {

    @Autowired
    lateinit var objectMapper: ObjectMapper

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

//        val m = QMember("m") // 별칭 직접 지정
        val m = QMember.member // 기본 인스턴스 사용

        val findMember = queryFactory.select(m)
                .from(m)
                .where(m.username.eq("member1"))
                .fetchOne()

        assertThat(findMember?.username).isEqualTo("member1")

    }

    @Test
    fun search() {
        val query = JPAQueryFactory(em)
        val member = QMember.member

        val findMember = query.selectFrom(member)
                .where(member.username.eq("member1")
                        .and(member.age.eq(10)))
                .fetchOne()

        assertEquals("멤버 이름이 잘 조회 되었는지 확인", findMember?.username, "member1")
        assertEquals("멤버 나이가 잘 조회 되었는지 확인", findMember?.age, 10)

    }

    @Test
    fun searchAndParam() {
        val jpaQueryFactory = JPAQueryFactory(em)
        val member = QMember.member

        val fetchOne = jpaQueryFactory.selectFrom(member)
                .where(
                        member.username.eq("member1"),
                        member.age.eq(10)
                )
                .fetchOne()

        assertEquals("멤버 이름이 잘 조회 되었는지 확인", fetchOne?.username, "member1")
        assertEquals("멤버 나이가 잘 조회 되었는지 확인", fetchOne?.age, 10)
    }

    @Test
    fun fetchResult() {
        val jpaQueryFactory = JPAQueryFactory(em)
        val member = QMember.member

        // List
        val fetch = jpaQueryFactory.selectFrom(member)
                .fetch()

        // 단 건
        val fetchOne = jpaQueryFactory.selectFrom(member)
                .where(
                        member.username.eq("member1"),
                        member.age.eq(10)
                )
                .fetchOne()

        // 처음 한 건 조회
        val fetchFirst = jpaQueryFactory.selectFrom(member)
                .fetchFirst()

        // 페이징에서 사용
        val fetchResults = jpaQueryFactory.selectFrom(member)
                .fetchResults()

        println("전체 데이터 건수 : " + fetchResults.total)
        fetchResults.results.forEach { println(objectMapper.writeValueAsString(it)) }

        // count 쿼리
        val fetchCount = jpaQueryFactory.selectFrom(member)
                .fetchCount()
    }

    /**
     * 회원 정렬 순서
     * 1. 회원 나이 내림차순
     * 2. 회원 이름 올림차순
     * 단, 2에서 회원 이름이 없으면 마지막에 출력 (nulls last)
     */
    @Test
    fun sort() {
        em.persist(Member(null, 100))
        em.persist(Member("member5", 100))
        em.persist(Member("member6", 100))
        em.persist(Member("member7", 110))

        val jpaQueryFactory = JPAQueryFactory(em)
        val member = QMember.member

        val fetch = jpaQueryFactory.selectFrom(member)
                .orderBy(
                        member.age.desc(),
                        member.username.asc().nullsLast()
                )
                .fetch()

        val member7 = fetch.get(0)
        val member5 = fetch.get(1)
        val member6 = fetch.get(2)
        val memberNull = fetch.get(3)

        assertThat(member7.username).isEqualTo("member7")
        assertThat(member5.username).isEqualTo("member5")
        assertThat(member6.username).isEqualTo("member6")
        assertThat(memberNull.username).isNull()
    }

    @Test
    fun paging1() {

        val jpaQueryFactory = JPAQueryFactory(em)
        val member = QMember.member

        val fetch = jpaQueryFactory.selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetch()

        assertThat(fetch.size).isEqualTo(2)
    }

    @Test
    fun paging2() {
        val jpaQueryFactory = JPAQueryFactory(em)
        val member = QMember.member

        val fetchResults = jpaQueryFactory.selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetchResults()

        assertThat(fetchResults.limit).isEqualTo(2)
        assertThat(fetchResults.offset).isEqualTo(1)
        assertThat(fetchResults.total).isEqualTo(4)
        assertThat(fetchResults.results.size).isEqualTo(2)
    }

    @Test
    fun aggregation() {
        val jpaQueryFactory = JPAQueryFactory(em)
        val member = QMember.member

        val result = jpaQueryFactory.select(
                member.count(),
                member.age.sum(),
                member.age.avg(),
                member.age.max(),
                member.age.min()
        )
                .from(member)
                .fetch()

        val tuple: com.querydsl.core.Tuple = result.get(0)

        assertThat(tuple.get(member.count())).isEqualTo(4)
        assertThat(tuple.get(member.age.sum())).isEqualTo(100)
        assertThat(tuple.get(member.age.avg())?.toInt()).isEqualTo(25)
        assertThat(tuple.get(member.age.max())).isEqualTo(40)
        assertThat(tuple.get(member.age.min())).isEqualTo(10)
    }

    @Test
    fun aggregation_group() {
        val jpaQueryFactory = JPAQueryFactory(em)
        val member = QMember.member
        val team = QTeam.team

        val result = jpaQueryFactory.select(team.name, member.age.avg())
                .from(member)
                .join(member.team, team)
                .groupBy(team.name)
                .fetch()

        val teamA: Tuple = result.get(0)
        val teamB: Tuple = result.get(1)

        assertThat(teamA.get(team.name)).isEqualTo("teamA")
        assertThat(teamA.get(member.age.avg())).isEqualTo(15.0)

        assertThat(teamB.get(team.name)).isEqualTo("teamB")
        assertThat(teamB.get(member.age.avg())).isEqualTo(35.0)
    }

    @Test
    fun inner_join() {
        val jpaQueryFactory = JPAQueryFactory(em)
        val member = QMember.member
        val team = QTeam.team

        val fetch = jpaQueryFactory.select(member)
                .from(member)
                .join(member.team, team)
                .where(team.name.eq("teamA"))
                .fetch()

        assertThat(fetch)
                .extracting("username")
                .containsExactly("member1", "member2")

        fetch.forEach {
            println(objectMapper.writeValueAsString(it))
        }
    }

    @Test
    fun theta_join() {

        val jpaQueryFactory = JPAQueryFactory(em)
        val member = QMember.member
        val team = QTeam.team

        em.persist(Member("teamA"))
        em.persist(Member("teamB"))

        val fetch = jpaQueryFactory.select(member)
                .from(member, team)
                .where(member.username.eq(team.name))
                .fetch()

        assertThat(fetch)
                .extracting("username")
                .containsExactly("teamA", "teamB")
    }

    @Test
    fun join_on_filtering() {
        val jpaQueryFactory = JPAQueryFactory(em)
        val member = QMember.member
        val team = QTeam.team

        val result = jpaQueryFactory.select(member, team)
                .from(member)
                .leftJoin(member.team, team).on(team.name.eq("teamA"))
                .fetch()
    }

    @Test
    fun 연관관계가_없는_엔티티_외부조인() {
        val jpaQueryFactory = JPAQueryFactory(em)
        val member = QMember.member
        val team = QTeam.team

        jpaQueryFactory.select(member, team)
                .from(member)
                .leftJoin(team).on(member.username.eq(team.name))
                .fetch()
    }

    @PersistenceUnit
    lateinit var emf: EntityManagerFactory

    @Test
    fun fetchJoinNo() {
        val jpaQueryFactory = JPAQueryFactory(em)
        val member = QMember.member
        val team = QTeam.team

        em.flush()
        em.clear()

        val result = jpaQueryFactory.selectFrom(member)
                .where(member.username.eq("member1"))
                .fetchOne()

        val loaded: Boolean = emf.persistenceUnitUtil.isLoaded(result?.team)
        assertThat(loaded).`as`("패치 조인 미적용").isFalse()
    }

    @Test
    fun fetchJoin() {
        val jpaQueryFactory = JPAQueryFactory(em)
        val member = QMember.member
        val team = QTeam.team

        em.flush()
        em.clear()

        val fetchOne = jpaQueryFactory.selectFrom(member)
                .join(member.team, team).fetchJoin()
                .where(member.username.eq("member1"))
                .fetchOne()

        val loaded: Boolean = emf.persistenceUnitUtil.isLoaded(fetchOne?.team)
        assertThat(loaded).`as`("패치 조인 적용").isTrue()
    }

    @Test
    fun subQuery() {
        val jpaQueryFactory = JPAQueryFactory(em)
        val member = QMember.member
        val team = QTeam.team
        val memberSub = QMember("memberSub")

        val fetchOne = jpaQueryFactory.selectFrom(member)
                .where(member.age.eq(
                        JPAExpressions
                                .select(memberSub.age.max())
                                .from(memberSub)
                ))
                .fetchOne()

        assertThat(fetchOne?.age).isEqualTo(40)

    }

    @Test
    fun 나이가_평균이상인_서브쿼리() {
        val jpaQueryFactory = JPAQueryFactory(em)
        val member = QMember.member
        val team = QTeam.team
        val memberSub = QMember("memberSub")

        val fetch = jpaQueryFactory.selectFrom(member)
                .where(
                        member.age.goe(
                                JPAExpressions.select(memberSub.age.avg())
                                        .from(memberSub)
                        )
                )
                .fetch()

        assertThat(fetch).extracting("age").containsExactly(30, 40)
    }

    @Test
    fun 서브쿼리_in() {
        val jpaQueryFactory = JPAQueryFactory(em)
        val member = QMember.member
        val team = QTeam.team
        val memberSub = QMember("memberSub")

        val fetch = jpaQueryFactory
                .selectFrom(member)
                .where(
                        member.age.`in`(
                                JPAExpressions
                                        .select(memberSub.age)
                                        .from(memberSub)
                                        .where(memberSub.age.gt(10))
                        )
                )
                .fetch()

        assertThat(fetch).extracting("age").containsExactly(20, 30, 40)
    }

    @Test
    fun select_절에_sub_query() {
        val jpaQueryFactory = JPAQueryFactory(em)
        val member = QMember.member
        val team = QTeam.team
        val memberSub = QMember("memberSub")

        val fetch = jpaQueryFactory
                .select(
                        member.username,
                        JPAExpressions
                                .select(memberSub.age.max())
                                .from(memberSub)
                )
                .from(member)
                .fetch()

        for (tuple in fetch) {
            println(tuple.get(member.username))
            println(tuple.get(JPAExpressions
                    .select(memberSub.age.max())
                    .from(memberSub)))
        }
    }

    @Test
    fun 상수표현() {
        val jpaQueryFactory = JPAQueryFactory(em)
        val member = QMember.member

        val fetch = jpaQueryFactory
                .select(Expressions.constant("A"), member)
                .from(member)
                .fetch()

        for (tuple in fetch) {
            println(tuple.get(Expressions.constant("A")))
            println(tuple.get(member))
        }
    }

    @Test
    fun 문자더하기_concat() {
        val jpaQueryFactory = JPAQueryFactory(em)
        val member = QMember.member

        val fetchOne = jpaQueryFactory.select(member.username.concat("_").concat(member.age.stringValue()))
                .from(member)
                .fetchFirst()
    }
}