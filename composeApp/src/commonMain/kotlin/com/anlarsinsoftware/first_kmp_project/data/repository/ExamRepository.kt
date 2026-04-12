package com.anlarsinsoftware.first_kmp_project.data.repository

import com.anlarsinsoftware.first_kmp_project.data.model.AppConfig
import com.anlarsinsoftware.first_kmp_project.data.remote.RemoteConfigService

class ExamRepository {
    private val remoteConfigService = RemoteConfigService()

    // Cache mekanizması: Her seferinde internete gitmesin, hafızada tutsun.
    private var cachedConfig: AppConfig? = null

    suspend fun getExamConfig(): AppConfig {
        if (cachedConfig == null) {
            cachedConfig = remoteConfigService.fetchAppConfig()
        }
        return cachedConfig!!
    }

    // Config'i zorla yenilemek istersen (Örn: "Yenile" butonuyla)
    fun clearCache() {
        cachedConfig = null
    }
}