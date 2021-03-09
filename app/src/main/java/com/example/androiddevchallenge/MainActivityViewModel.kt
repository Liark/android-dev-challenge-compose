/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.concurrent.TimeUnit

const val hoursInADay = 24
const val minutesInAnHour = 60
const val secondsInAMinute = 60
const val HOURS = "HOURS"
const val MINUTES = "MINUTES"
const val SECONDS = "SECONDS"

class MainActivityViewModel : ViewModel() {

    private val _countDownTimerTimeLeftLiveData = MutableLiveData(0L)
    val countDownTimeTimeLeftLiveData: LiveData<Long> = _countDownTimerTimeLeftLiveData

    private val _minutesLeftLiveData = MutableLiveData(0L)
    val minutesLeftLiveData: LiveData<Long> = _minutesLeftLiveData

    private val _hoursLeftLiveData = MutableLiveData(0L)
    val hoursLeftLiveData: LiveData<Long> = _hoursLeftLiveData

    private val selectedTimeHashMap = hashMapOf(
        HOURS to 0L,
        MINUTES to 0L,
        SECONDS to 0L
    )
    private val _selectedHourLiveData = MutableLiveData(0L)
    val selectedHourLiveData: LiveData<Long> = _selectedHourLiveData

    private val _selectedMinuteLiveData = MutableLiveData(0L)
    val selectedMinuteLiveData: LiveData<Long> = _selectedMinuteLiveData

    private val _selectedSecondLiveData = MutableLiveData(0L)
    val selectedSecondLiveData: LiveData<Long> = _selectedSecondLiveData

    private var countDownTimer: CountDownTimer? = null

    fun startTimer() {
        fun setNewCountTimer(seconds: Long, minutes: Long, hours: Long) {
            val oneSecondInMillis = 1000L
            var currentMinutes = minutes
            var currentHours = hours
            _minutesLeftLiveData.value = currentMinutes
            _hoursLeftLiveData.value = currentHours

            countDownTimer = object : CountDownTimer(seconds, oneSecondInMillis) {
                override fun onTick(millisUntilFinished: Long) {
                    _countDownTimerTimeLeftLiveData.value = millisUntilFinished
                }

                override fun onFinish() {
                    var newTimer = false
                    _countDownTimerTimeLeftLiveData.value = 0L
                    if (currentMinutes > 0) {
                        currentMinutes--
                        _minutesLeftLiveData.value = currentMinutes
                        newTimer = true
                    } else if (currentMinutes <= 0) {
                        if (currentHours > 0) {
                            currentMinutes = 59
                            _minutesLeftLiveData.value = currentMinutes
                            currentHours--
                            _hoursLeftLiveData.value = currentHours
                            newTimer = true
                        }
                    }

                    if (newTimer) {
                        setNewCountTimer(
                            TimeUnit.SECONDS.toMillis(secondsInAMinute.toLong()),
                            currentMinutes,
                            currentHours
                        )
                        countDownTimer?.start()
                    }
                }
            }
        }
        stopTimer()
        var hoursSelected = 0L
        var minutesSelected = 0L
        var secondsSelected = 0L
        selectedTimeHashMap[HOURS]?.let {
            hoursSelected = it
        }
        selectedTimeHashMap[MINUTES]?.let {
            minutesSelected = it
        }
        selectedTimeHashMap[SECONDS]?.let {
            secondsSelected = TimeUnit.SECONDS.toMillis(it)
        }

        setNewCountTimer(secondsSelected, minutesSelected, hoursSelected)

        countDownTimer?.start()
    }

    fun stopTimer() {
        countDownTimer?.cancel()
        countDownTimer = null
        _countDownTimerTimeLeftLiveData.value = 0L
        _minutesLeftLiveData.value = 0L
        _hoursLeftLiveData.value = 0L
    }

    fun setTime(timeUnit: String, time: Long) {
        selectedTimeHashMap[timeUnit] = time
        when (timeUnit) {
            HOURS -> _selectedHourLiveData.value = time
            MINUTES -> _selectedMinuteLiveData.value = time
            SECONDS -> _selectedSecondLiveData.value = time
        }
    }
}
