package study.querydsl.dto

import com.querydsl.core.annotations.QueryProjection

class MemberSubDto {
    var username: String? = null
    var age: Int? = null

    @QueryProjection
    constructor(username: String?, age: Int?) {
        this.username = username
        this.age = age
    }

    override fun toString(): String {
        return "MemberSubDto(username=$username, age=$age)"
    }
}