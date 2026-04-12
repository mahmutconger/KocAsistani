package com.anlarsinsoftware.first_kmp_project.data.remote

import com.anlarsinsoftware.first_kmp_project.data.model.AppConfig
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.remoteconfig.remoteConfig
import kotlinx.serialization.json.Json

class RemoteConfigService {
    private val remoteConfig = Firebase.remoteConfig

    // JSON verisini çeken ana fonksiyon
    suspend fun fetchAppConfig(): AppConfig {
        try {
            // 1. Ayarlar
            remoteConfig.settings {
                minimumFetchIntervalInSeconds = 3600 // 1 Saat
            }

            // 2. Fetch ve Activate
            remoteConfig.fetchAndActivate()

            // 3. Veriyi Oku (DÜZELTİLEN KISIM)
            // Köşeli parantez [] yerine getValue() ve asString() kullanıyoruz.
            val jsonString = remoteConfig.getValue("app_config_v1").asString()

            // Boş mu kontrolü
            if (jsonString.isBlank()) {
                println("Remote Config boş geldi!")
                return AppConfig()
            }

            // 4. JSON'ı Kotlin Objesine çevir
            val json = Json { ignoreUnknownKeys = true }
            return json.decodeFromString(AppConfig.serializer(), jsonString)

        } catch (e: Exception) {
            println("Remote Config Hatası: ${e.message}")
            return AppConfig()
        }
    }
}