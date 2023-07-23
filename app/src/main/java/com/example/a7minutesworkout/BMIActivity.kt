package com.example.a7minutesworkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.a7minutesworkout.databinding.ActivityBmiactivityBinding
import java.math.BigDecimal
import java.math.RoundingMode

class BMIActivity : AppCompatActivity() {
    private var binding : ActivityBmiactivityBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityBmiactivityBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarBmiActivity)
        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "CALCULATE BMI"
        }
        binding?.toolbarBmiActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
        binding?.btnCalculateUnits?.setOnClickListener{
            if(validateMetricUnits()){
                val heightValue = binding?.etMetricUnitHeight?.text.toString().toFloat()/100
                val weightValue = binding?.etMetricUnitWeight?.text.toString().toFloat()
                val BMI = weightValue / (heightValue * heightValue)
                displayBMI(BMI)
            }else{
                Toast.makeText(this,"Please enter valid values",Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun validateMetricUnits():Boolean{
        var isValid = true
        if(binding?.etMetricUnitHeight?.text.toString().isEmpty()){
            isValid =false
        }else if(binding?.etMetricUnitWeight?.text.toString().isEmpty()){
            isValid = false
        }
        return isValid
    }

    private fun displayBMI(bmi : Float){
        val bmiLabel : String
        val bmiDescription : String
        if(bmi <= 15 ){
            bmiLabel = "Very severely underweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Eat more!"
        }else if(bmi > 15 && bmi <=16){
            bmiLabel = "Severely underweight"
            bmiDescription = "Oops!You really need to take better care of yourself! Eat more!"
        }else if(bmi > 16 && bmi <=18.5){
            bmiLabel = "Underweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Eat more!"
        }else if(bmi > 18.5 && bmi <=25){
            bmiLabel = "Normal"
            bmiDescription = "Congratulations! You are in a good shape!"
        }else if(bmi > 25 && bmi <=30){
            bmiLabel = "Overweight"
            bmiDescription = "Oops! You really need to take care of your yourself! Workout maybe!"
        }else if(bmi >30 && bmi <=35){
            bmiLabel = "Obese Class | (Moderately obese)"
            bmiDescription = "Oops! You really need to take care of your yourself! Workout maybe!"
        }else if(bmi > 35 && bmi <= 40){
            bmiLabel = "Obese Class || (Severely obese)"
            bmiDescription = "OMG! You are in a very dangerous condition! Act now!"
        }else{
            bmiLabel = "Obese Class ||| (Very Severely obese)"
            bmiDescription = "OMG! You are in a very dangerous condition! Act now!"
        }
        val bmiValue = BigDecimal(bmi.toDouble()).setScale(2, RoundingMode.HALF_EVEN).toString()
        binding?.tvBMIValue?.text = bmiValue // Value is set to TextView
        binding?.tvBMIType?.text = bmiLabel // Label is set to TextView
        binding?.tvBMIDescription?.text = bmiDescription // Description is set to TextView
    }

}