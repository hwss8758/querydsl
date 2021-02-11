package study.querydsl.dto

data class MemberSearchCondition(
    var username: String? = null,
    var teamName: String? = null,
    var ageGoe: Int? = null,
    var ageLoe: Int? = null
)
