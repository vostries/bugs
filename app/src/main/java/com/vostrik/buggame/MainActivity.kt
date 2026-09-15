package com.vostrik.buggame

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var player: Player
    private lateinit var editTextFullName: EditText
    private lateinit var radioGroupGender: RadioGroup
    private lateinit var spinnerCourse: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        player = Player()

        editTextFullName = findViewById(R.id.editTextFullName)
        radioGroupGender = findViewById(R.id.radioGroupGender)
        spinnerCourse = findViewById(R.id.spinnerCourse)

        val courses = resources.getStringArray(R.array.courses)
        spinnerCourse.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, courses).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }
}