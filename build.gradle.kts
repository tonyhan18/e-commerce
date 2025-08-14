plugins {
	java
	id("org.springframework.boot") version "3.4.1"
	id("io.spring.dependency-management") version "1.1.7"
	id("com.avast.gradle.docker-compose") version "0.17.5"
}

fun getGitHash(): String {
	return providers.exec {
		commandLine("git", "rev-parse", "--short", "HEAD")
	}.standardOutput.asText.get().trim()
}

group = "kr.hhplus.be"
version = getGitHash()

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:2024.0.0")
	}
}

val querydslVersion = "5.0.0"
val restAssuredVersion = "5.3.2"
val redissonVersion = "3.27.1"

dependencies {
    // Spring
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springframework.boot:spring-boot-starter-validation")

    // DB
	runtimeOnly("com.mysql:mysql-connector-j")

	// QueryDSL
	implementation("com.querydsl:querydsl-jpa:${querydslVersion}:jakarta")
	annotationProcessor("com.querydsl:querydsl-apt:${querydslVersion}:jakarta")
	annotationProcessor("jakarta.annotation:jakarta.annotation-api")
	annotationProcessor("jakarta.persistence:jakarta.persistence-api")

	// Redisson
	implementation("org.redisson:redisson-spring-boot-starter:${redissonVersion}")

    // Lombok
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:mysql")
	testImplementation("org.testcontainers:testcontainers")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
}

val querydslDir = "$buildDir/generated/querydsl"

sourceSets {
	main {
		java {
			srcDir(querydslDir)
		}
	}
}

tasks.withType<JavaCompile>() {
	options.annotationProcessorGeneratedSourcesDirectory = file(querydslDir)
}

dockerCompose {
    useComposeFiles = listOf("docker-compose.yml")
    waitForTcpPorts = true
}

tasks.bootRun {
    dependsOn("composeUp")
    finalizedBy("composeDown")
}

tasks.withType<Test> {
	useJUnitPlatform()
	systemProperty("user.timezone", "UTC")
	// Java 17에서 Mockito ByteBuddy 문제 해결
	jvmArgs(
		"-XX:+IgnoreUnrecognizedVMOptions",
		"-XX:+UseSerialGC",
		"-Dnet.bytebuddy.agent.attacher.dump=bytebuddy.log"
	)
}
