package study.querydsl.dto

import com.querydsl.core.annotations.QueryProjection

class MemberTeamDto {
    var memberId: Long? = null
    var username: String? = null
    var age: Int? = null
    var teamId: Long? = null
    var teamName: String? = null

    @QueryProjection
    constructor(
        memberId: Long?,
        username: String?,
        age: Int?,
        teamId: Long?,
        teamName: String?
    ) {
        this.memberId = memberId
        this.username = username
        this.age = age
        this.teamId = teamId
        this.teamName = teamName
    }
}
