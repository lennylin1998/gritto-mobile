package com.gritto.app.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.URLBuilder
import io.ktor.http.contentType
import io.ktor.http.path
import kotlinx.serialization.Serializable
import io.ktor.http.encodedPath
import io.ktor.http.takeFrom

class ApiClient(
    val httpClient: HttpClient,
    val baseUrl: String,
    val tokenProvider: () -> String?,
) {

    suspend inline fun <reified T> get(
        path: String,
        crossinline block: HttpRequestBuilder.() -> Unit = {},
    ): ApiResult<T> = execute {
        httpClient.get {
            setup(path, this)
            block()
        }
    }

    suspend inline fun <reified Req : Any, reified Res> post(
        path: String,
        body: Req,
        crossinline block: HttpRequestBuilder.() -> Unit = {},
    ): ApiResult<Res> = execute {
        httpClient.post {
            setup(path, this)
            contentType(ContentType.Application.Json)
            setBody(body)
            block()
        }
    }

    suspend inline fun <reified Req : Any, reified Res> put(
        path: String,
        body: Req,
        crossinline block: HttpRequestBuilder.() -> Unit = {},
    ): ApiResult<Res> = execute {
        httpClient.put {
            setup(path, this)
            setBody(body)
            block()
        }
    }

    suspend inline fun <reified Req : Any, reified Res> patch(
        path: String,
        body: Req,
        crossinline block: HttpRequestBuilder.() -> Unit = {},
    ): ApiResult<Res> = execute {
        httpClient.patch {
            setup(path, this)
            setBody(body)
            block()
        }
    }

    suspend inline fun <reified Res> delete(
        path: String,
        crossinline block: HttpRequestBuilder.() -> Unit = {},
    ): ApiResult<Res> = execute {
        httpClient.delete {
            setup(path, this)
            block()
        }
    }

    fun HttpRequestBuilder.setup(
        path: String,
        builder: HttpRequestBuilder,
    ) {
        builder.url {
            takeFrom(baseUrl)
            encodedPath = path
        }
        tokenProvider()?.let { token ->
            headers.append("Authorization", "Bearer $token")
        }
    }

    suspend inline fun <reified Res> execute(
        crossinline block: suspend () -> HttpResponse,
    ): ApiResult<Res> = try {
        val response = block()
        if (response.status.value in 200..299) {
            val body: Res = response.body()
            ApiResult.Success(body)
        } else {
            val errorBody: ApiErrorResponse? = runCatching { response.body<ApiErrorResponse>() }.getOrNull()
            ApiResult.Error(
                message = errorBody?.error?.message ?: "Request failed with status ${response.status.value}",
                statusCode = response.status.value,
            )
        }
    } catch (thr: Throwable) {
        ApiResult.Error(message = thr.message ?: "Unexpected error", cause = thr)
    }
}

sealed class ApiResult<out T> {
    data class Success<T>(val value: T) : ApiResult<T>()
    data class Error(
        val message: String,
        val statusCode: Int? = null,
        val cause: Throwable? = null,
    ) : ApiResult<Nothing>()
}

@Serializable
data class ApiErrorResponse(
    val error: ApiErrorBody,
) {
    @Serializable
    data class ApiErrorBody(
        val code: Int? = null,
        val message: String,
    )
}
