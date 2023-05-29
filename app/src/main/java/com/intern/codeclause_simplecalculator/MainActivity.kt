package com.intern.codeclause_simplecalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.intern.codeclause_simplecalculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var canAddOperation = false
    private var canAddDecimal = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun numberAction(view: View) {
        if (view is Button) {
            if (view.text == ".") {
                if (canAddDecimal)
                    binding.workingsTV.append(view.text)

                canAddDecimal = false
            } else {
                binding.workingsTV.append(view.text)
            }
            canAddOperation = true
        }
    }


    fun operationAction(view: View) {
        if (view is Button && canAddOperation) {
            binding.workingsTV.append(view.text)
            canAddOperation = false
            canAddDecimal = true
        }
    }

    fun allClearAction(view: View) {
        binding.workingsTV.text = ""
        binding.resultsTV.text = ""
    }

    fun backSpaceAction(view: View) {
        val length = binding.workingsTV.length()
        if (length > 0) {
            binding.workingsTV.text = binding.workingsTV.text.subSequence(0, length - 1)
        }
    }

    fun equalsAction(view: View) {
        try {
            binding.resultsTV.text = calculateResults()
        } catch (e: ArithmeticException) {
            binding.resultsTV.text = "Cannot be divided"
        }
    }

    private fun calculateResults(): String {
        val digitsOperators = digitsOperators()
        if (digitsOperators.isEmpty()) return ""

        val timesDivision = timesDivisionCalculate(digitsOperators)
        if (timesDivision.isEmpty()) return ""

        val result = addSubtractCalculate(timesDivision)
        return if (result.isFinite()) {
            result.toString()
        } else {
            "NULL"
        }
    }

    private fun addSubtractCalculate(passedList: MutableList<Any>): Float {
        var result = 0f

        for (i in passedList.indices) {
            val element = passedList[i]
            if (element is Float) {
                if (i == 0) {
                    result = element
                } else {
                    val operator = passedList[i - 1] as Char
                    val nextDigit = element
                    if (operator == '+') {
                        result += nextDigit
                    } else if (operator == '-') {
                        result -= nextDigit
                    }
                }
            } else if (element is String) {
                return Float.NaN // Return NaN (Not a Number) to indicate an error
            }
        }

        return result
    }


    private fun timesDivisionCalculate(passedList: MutableList<Any>): MutableList<Any> {
        val list = passedList.toMutableList()
        var i = 0

        while (i < list.size) {
            val element = list[i]
            if (element is Char) {
                if (element == 'X') {
                    val prevIndex = i - 1
                    val nextIndex = i + 1
                    if (prevIndex >= 0 && nextIndex < list.size) {
                        val prevElement = list[prevIndex]
                        val nextElement = list[nextIndex]
                        if (prevElement is Float && nextElement is Float) {
                            val result = prevElement * nextElement
                            list[prevIndex] = result
                            list.removeAt(i) // Remove the operator
                            list.removeAt(i) // Remove the next element
                            i-- // Decrement i to process the next element
                        }
                    }
                } else if (element == '/') {
                    val prevIndex = i - 1
                    val nextIndex = i + 1
                    if (prevIndex >= 0 && nextIndex < list.size) {
                        val prevElement = list[prevIndex]
                        val nextElement = list[nextIndex]
                        if (prevElement is Float && nextElement is Float) {
                            if (nextElement != 0f) {
                                val result = prevElement / nextElement
                                list[prevIndex] = result
                                list.removeAt(i) // Remove the operator
                                list.removeAt(i) // Remove the next element
                                i-- // Decrement i to process the next element
                            } else {
                                throw ArithmeticException("Cannot be divided")
                            }
                        }
                    }
                }
            }
            i++
        }

        return list
    }
    private fun calcTimesDiv(passedList: MutableList<Any>): MutableList<Any> {
        var list = passedList.toMutableList()
        while (list.contains('X') || list.contains('/')) {
            list = timesDivisionCalculate(list)
        }
        return list
    }

    private fun digitsOperators(): MutableList<Any> {
        val list = mutableListOf<Any>()
        var currentDigit = ""
        for (character in binding.workingsTV.text) {
            if (character.isDigit() || character == '.') {
                currentDigit += character
            } else if (character == 'X') {
                if (currentDigit != "") {
                    list.add(currentDigit.toFloat())
                    currentDigit = ""
                }
                list.add(character)
            } else {
                if (currentDigit != "") {
                    list.add(currentDigit.toFloat())
                    currentDigit = ""
                }
                list.add(character)
            }
        }

        if (currentDigit != "") {
            list.add(currentDigit.toFloat())
        }

        return list
    }

}