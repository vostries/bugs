package com.vostrik.buggame

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var player: Player
    private lateinit var editTextFullName: EditText
    private lateinit var radioGroupGender: RadioGroup
    private lateinit var spinnerCourse: Spinner
    private lateinit var seekBarDifficulty: SeekBar
    private lateinit var textViewDifficulty: TextView
    private lateinit var calendarView: CalendarView
    private lateinit var buttonShow: Button
    private lateinit var textViewResult: TextView
    private lateinit var imageViewZodiac: ImageView

    private var selectedDay = 1
    private var selectedMonth = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        player = Player()

        editTextFullName = findViewById(R.id.editTextFullName)
        radioGroupGender = findViewById(R.id.radioGroupGender)
        spinnerCourse = findViewById(R.id.spinnerCourse)
        seekBarDifficulty = findViewById(R.id.seekBarDifficulty)
        textViewDifficulty = findViewById(R.id.textViewDifficulty)
        calendarView = findViewById(R.id.calendarView)
        buttonShow = findViewById(R.id.buttonShow)
        textViewResult = findViewById(R.id.textViewResult)
        imageViewZodiac = findViewById(R.id.imageViewZodiac)

        val courses = resources.getStringArray(R.array.courses)
        spinnerCourse.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, courses).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        seekBarDifficulty.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val difficulty = progress + 1
                textViewDifficulty.text = difficulty.toString()
                player.difficulty = difficulty
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDay = dayOfMonth
            selectedMonth = month + 1
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            player.birthDate = dateFormat.format(calendar.time)
        }

        buttonShow.setOnClickListener {
            player.fullName = editTextFullName.text.toString()
            player.gender = findViewById<RadioButton>(radioGroupGender.checkedRadioButtonId).text.toString()
            player.course = spinnerCourse.selectedItemPosition + 1
            player.zodiacSign = ZodiacHelper.getZodiacSign(selectedDay, selectedMonth)

            val result = """
                ФИО: ${player.fullName}
                Пол: ${player.gender}
                Курс: ${player.course}
                Уровень сложности: ${player.difficulty}
                Дата рождения: ${player.birthDate}
                Знак зодиака: ${player.zodiacSign}
            """.trimIndent()

            textViewResult.text = result
            imageViewZodiac.visibility = ImageView.VISIBLE
        }
    }
}