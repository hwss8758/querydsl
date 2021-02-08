package study.querydsl.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
class Member {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    var id: Long? = null
    var username: String? = null
    var age: Int? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn("team_id")
    var team: Team? = null

    constructor(username: String?) {
        this.username = username
    }

    constructor(username: String?, age: Int?) {
        this.username = username
        this.age = age
    }

    constructor(username: String?, age: Int?, team: Team?) {
        this.username = username
        this.age = age
        this.changeTeam(team)
    }

    fun changeTeam(team: Team?) {
        this.team = team
        team?.members?.add(this)
    }
}