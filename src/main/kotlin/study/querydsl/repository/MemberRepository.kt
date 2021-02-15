package study.querydsl.repository

import org.springframework.data.jpa.repository.JpaRepository
import study.querydsl.entity.Member
import study.querydsl.repository.custom.MemberRepositoryCustom

interface MemberRepository : JpaRepository<Member, Long>, MemberRepositoryCustom {
    fun findByUsername(username: String): MutableList<Member>
}