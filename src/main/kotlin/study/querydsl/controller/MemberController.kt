package study.querydsl.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import study.querydsl.dto.MemberSearchCondition
import study.querydsl.dto.MemberTeamDto
import study.querydsl.repository.querydsl.MemberQuerydslRepository

@RestController
class MemberController {

    @Autowired
    lateinit var memberQuerydslRepository: MemberQuerydslRepository

    @GetMapping("/v1/members")
    fun searchMemberV1(condition: MemberSearchCondition?): Result<MutableList<MemberTeamDto>> {
        return Result(memberQuerydslRepository.searchByWhere(condition))
    }

    data class Result<T>(var data: T? = null)
}