package study.querydsl.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import study.querydsl.dto.MemberSearchCondition
import study.querydsl.dto.MemberTeamDto
import study.querydsl.repository.MemberRepository
import study.querydsl.repository.querydsl.MemberQuerydslRepository

@RestController
class MemberController {

    @Autowired
    lateinit var memberQuerydslRepository: MemberQuerydslRepository

    @Autowired
    lateinit var memberRepository: MemberRepository

    @PostMapping("/v1/members")
    fun searchMemberV1(@RequestBody condition: MemberSearchCondition?): Result<MutableList<MemberTeamDto>> {

        println("------------------------------")
        println(condition)
        println("------------------------------")
        return Result(memberQuerydslRepository.searchByWhere(condition))
    }

    data class Result<T>(var data: T? = null)

    @PostMapping("/v2/members")
    fun searchMemberV2(@RequestBody condition: MemberSearchCondition?,
                       pageable: Pageable):
            Result<Page<MemberTeamDto>> {

        println("------------------------------")
        println(condition)
        println("------------------------------")
        return Result(memberRepository.searchPageComplex2(condition, pageable))
    }
}