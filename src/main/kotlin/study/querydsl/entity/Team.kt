package study.querydsl.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
class Team {
    @Id
    @GeneratedValue
    var id: Long? = null
    var name: String? = null

    @OneToMany(mappedBy = "team")
    @JsonIgnore
    var members: MutableList<Member?> = mutableListOf()

    constructor(name: String?) {
        this.name = name
    }
}
