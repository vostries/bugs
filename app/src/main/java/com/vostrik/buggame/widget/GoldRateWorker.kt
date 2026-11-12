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

        if (goldRate != null) {
            val prefs = applicationContext.getSharedPreferences("gold_widget", Context.MODE_PRIVATE)
            prefs.edit()
                .putString("rate", String.format("%.2f ₽/г", goldRate.ratePerGram))
                .putString("date", goldRate.date)
                .apply()

            val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
            val widgetComponent = ComponentName(applicationContext, GoldWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(widgetComponent)

            appWidgetIds.forEach { appWidgetId ->
                GoldWidgetProvider.updateWidget(applicationContext, appWidgetManager, appWidgetId)
            }
        }

        return Result.success()
    }
}
