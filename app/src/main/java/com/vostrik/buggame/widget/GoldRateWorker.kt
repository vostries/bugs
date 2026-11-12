package com.vostrik.buggame.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vostrik.buggame.R
import com.vostrik.buggame.api.GoldRateRepository

class GoldRateWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val repo = GoldRateRepository.getInstance()
        val goldRate = repo.getGoldRate()
        val prefs = applicationContext.getSharedPreferences("gold_widget", Context.MODE_PRIVATE)

        if (goldRate != null) {
            prefs.edit()
                .remove("error")
                .putString("rate", String.format("%.2f ₽/г", goldRate.ratePerGram))
                .putString("date", goldRate.date)
                .apply()
        } else {
            prefs.edit().putString("error", "Нет данных ЦБ").apply()
        }

        val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
        val widgetComponent = ComponentName(applicationContext, GoldWidgetProvider::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(widgetComponent)

        appWidgetIds.forEach { appWidgetId ->
            GoldWidgetProvider.updateWidget(applicationContext, appWidgetManager, appWidgetId)
        }

        return if (goldRate != null) Result.success() else Result.retry()
    }
}
