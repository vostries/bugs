package com.vostrik.buggame.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.vostrik.buggame.MainActivity
import com.vostrik.buggame.R
import java.util.concurrent.TimeUnit

class GoldWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            updateWidget(context, appWidgetManager, appWidgetId)
        }

        // Загружаем курс сразу при добавлении виджета
        WorkManager.getInstance(context).enqueue(
            androidx.work.OneTimeWorkRequestBuilder<GoldRateWorker>().build()
        )
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        val workRequest = PeriodicWorkRequestBuilder<GoldRateWorker>(30, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "gold_rate_update",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        WorkManager.getInstance(context).cancelUniqueWork("gold_rate_update")
    }

    companion object {
        fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val prefs = context.getSharedPreferences("gold_widget", Context.MODE_PRIVATE)
            val rate = prefs.getString("rate", "...") ?: "..."
            val date = prefs.getString("date", "") ?: ""

            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val views = RemoteViews(context.packageName, R.layout.widget_gold_rate).apply {
                setTextViewText(R.id.widget_rate, rate)
                setTextViewText(R.id.widget_date, date)
                setOnClickPendingIntent(R.id.widget_rate, pendingIntent)
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
