package com.example.android.advancedcoroutines

import okhttp3.*

class AssetNetworkInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val pathSegment = chain.request().url().pathSegments()
        val fileName = pathSegment[pathSegment.size - 1]

        val response: Response? = runCatching {
            val context = AppContext.get() ?: return@runCatching null

            context.assets.open(fileName).use { stream ->
                val fileBytes = ByteArray(stream.available())
                stream.read(fileBytes)

                Response.Builder()
                    .code(200)
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .message("OK")
                    .body(
                        ResponseBody.create(
                            MediaType.get("application/json"),
                            fileBytes
                        )
                    ).build()
            }
        }.getOrNull()

        return response ?: chain.proceed(request)
    }
}