package com.example.week2_v1
/*
import io.ktor.application.*
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

data class User(val id: Int, val username: String, val email: String)

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        jackson {}
    }

    install(StatusPages) {
        exception<Throwable> { cause ->
            call.respond(HttpStatusCode.InternalServerError, cause.localizedMessage)
        }
    }

    routing {
        route("/users") {
            get {
                val users = listOf(
                    User(1, "user1", "user1@example.com"),
                    User(2, "user2", "user2@example.com")
                )
                call.respond(users)
            }
            post {
                val user = call.receive<User>()
                // 새로운 사용자를 데이터베이스에 추가하는 로직
                call.respond(HttpStatusCode.Created, "User created")
            }
        }
    }
}
 */