import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.4.2"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.4.21"
    kotlin("plugin.spring") version "1.4.21"
    kotlin("plugin.jpa") version "1.4.21"

    /**
     * jpa 양방향 무한로프 방지
     */
    id("org.jetbrains.kotlin.plugin.allopen") version "1.3.61"

    /**
     * query DSL
     */
    id("com.ewerk.gradle.plugins.querydsl") version "1.0.10"
    kotlin("kapt") version "1.3.61"
    idea
}

group = "study"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
    maven("https://plugins.gradle.org/m2/")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    runtimeOnly("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    /**
     * querydsl start
     */
    // 4.4.0 => 최신버전으로 설정해 줘야 에러가 나지 않는다.
    api("com.querydsl:querydsl-jpa:4.4.0")
    // kapt로 dependency를 지정해 준다.
    // kotlin 코드가 아니라면 kapt 대신 annotationProcessor를 사용한다.
    kapt("com.querydsl:querydsl-apt:4.4.0:jpa") // ":jpa 꼭 붙여줘야 한다!!"
    //kapt("org.hibernate.javax.persistence:hibernate-jpa-2.1-api:1.0.2.Final")
    /**
     * querydsl end
     */

    // 쿼리 로그를 남기기 위해 디펜던시 추가
    implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.5.6")

    // hibernate5
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-hibernate5")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

/**
 * jpa 양방향 무한로프 방지
 */
allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}

/**
 * querydsl start
 */
// 생성된 QClass들을 intelliJ IDEA가 사용할 수 있도록 소스코드 경로에 추가해 준다.
idea {
    module {
        val kaptMain = file("$buildDir/generated/source/kapt/main")
        sourceDirs.add(kaptMain)
        generatedSourceDirs.add(kaptMain)
    }
}
/**
 * querydsl end
 */