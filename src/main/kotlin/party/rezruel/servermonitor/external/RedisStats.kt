package party.rezruel.servermonitor.external

import party.rezruel.servermonitor.Monitor

class RedisStats(private val monitor: Monitor) {

    private val rootUrl = "http://localhost.1491"
//
//    private val linkedBlockingDeque = LinkedBlockingDeque<Runnable>()
//    private val threadPoolExecutor = ThreadPoolExecutor(2, 4, 60, TimeUnit.SECONDS, linkedBlockingDeque)
//    private val completedRequests = mutableListOf<Map<String, Any>>()
//
//    fun getUserStats(service: String) {
//        runHttpRequestInThread(rootUrl, "/users/${service.toLowerCase()}", HttpMethod.GET)
//        while (threadPoolExecutor.)
//    }
//
//    private fun runHttpRequestInThread(url: String, path: String, method: HttpMethod, body: String = ""): Map<String, Any> {
//
//        val thi = threadPoolExecutor.submit(Runnable {
//            mapOf<String, Any>()
//        }).get()
//        val thing = threadPoolExecutor.submit(kotlinx.coroutines.Runnable {
//            when (method) {
//                HttpMethod.GET -> {
//                    val respMap: Map<String, Any> =
//                        Gson().fromJson(url.httpGet().body()?.string(), object : TypeToken<Map<String, Any>>() {}.type)
//                    completedRequests.add(respMap)
//                    return@Runnable respMap
//                }
//                HttpMethod.POST -> {
//                    val resp = httpPost {
//                        host = url
//                        body {
//                            json(body)
//                        }
//                    }
//                    val respMap: Map<String, Any> =
//                        Gson().fromJson(resp.body()?.string(), object : TypeToken<Map<String, Any>>() {}.type)
//                    completedRequests.add(respMap)
//                    return@Runnable respMap
//                }
//                else -> return@Runnable mapOf<String, Any>()
//            }
//        })
//    }
}