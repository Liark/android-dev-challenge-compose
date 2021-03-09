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

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androiddevchallenge.ui.theme.MyTheme
import com.example.androiddevchallenge.ui.theme.constants.MyColors
import com.example.androiddevchallenge.ui.theme.typography
import timber.log.Timber
import kotlin.math.ceil

class MainActivity : AppCompatActivity() {

    private val viewModel = MainActivityViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
        setContent {
            MyTheme {
                MainScreen(viewModel)
            }
        }
    }
}

// Start building your app here!
@Composable
fun MainScreen(
    viewModel: MainActivityViewModel = MainActivityViewModel(),
) {
    val hourSelected: Long by viewModel.selectedHourLiveData.observeAsState(0)
    val minuteSelected: Long by viewModel.selectedMinuteLiveData.observeAsState(0)
    val secondSelected: Long by viewModel.selectedSecondLiveData.observeAsState(0)
    val currentHour: Long by viewModel.hoursLeftLiveData.observeAsState(0L)
    val currentMinutes: Long by viewModel.minutesLeftLiveData.observeAsState(0L)
    val currentSeconds: Long by viewModel.countDownTimeTimeLeftLiveData.observeAsState(0L)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                style = typography.h4,
                text = "Time left",
                textAlign = TextAlign.Center,
                color = Color.White
            )
            Row {
                CurrentTimeLeft(timeUnit = HOURS, currentTime = currentHour)
                Spacer(modifier = Modifier.size(15.dp))
                CurrentTimeLeft(timeUnit = MINUTES, currentTime = currentMinutes)
                Spacer(modifier = Modifier.size(15.dp))
                CurrentTimeLeft(timeUnit = SECONDS, currentTime = ceil((currentSeconds / 1000).toDouble()).toLong())
            }
        }
    }
    var dragOffsetY by remember { mutableStateOf(0f) }
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { delta ->
                    val newOffset = dragOffsetY - delta
                    if ((newOffset >= 0f) and (newOffset <= 10f)) {
                        dragOffsetY = 0f
                    } else if ((newOffset >= 10f) and (newOffset <= 500f)) {
                        val deltaAdjusted = delta / 2.5f
                        dragOffsetY -= deltaAdjusted
                    }
                }
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        MyBottomDrawer(
            dragOffsetY.dp,
            hourSelected,
            minuteSelected,
            secondSelected,
            { viewModel.startTimer() },
            { viewModel.stopTimer() }
        ) { title, time ->
            viewModel.setTime(
                title,
                time
            )
        }
    }
}

@Composable
fun CurrentTimeLeft(
    timeUnit: String,
    currentTime: Long,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            style = typography.body1,
            color = Color.White,
            text = timeUnit
        )
        Text(
            style = typography.body1,
            color = Color.White,
            text = "$currentTime"
        )
    }
}

@Composable
fun MyBottomDrawer(
    drawerOffset: Dp,
    hourSelected: Long,
    minuteSelected: Long,
    secondSelected: Long,
    timeStart: () -> Unit,
    timeStop: () -> Unit,
    onButtonClicked: (String, Long) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp, 15.dp, 0.dp, 0.dp))
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MyColors.LightBlue),
                Arrangement.SpaceBetween
            ) {
                Button(onClick = { timeStart() }) {
                    Text(text = "Start Timer")
                }
                Button(onClick = { timeStop() }) {
                    Text(text = "Stop Timer")
                }
            }
            Row(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxWidth()
                    .height(drawerOffset),
                horizontalArrangement = Arrangement.Center
            ) {
                TimeListSelector(HOURS, hoursInADay, hourSelected, onButtonClicked)
                Spacer(Modifier.width(15.dp))
                TimeListSelector(MINUTES, minutesInAnHour, minuteSelected, onButtonClicked)
                Spacer(Modifier.width(15.dp))
                TimeListSelector(SECONDS, secondsInAMinute, secondSelected, onButtonClicked)
            }
        }
    }
}

@Composable
fun TimeListSelector(
    timeUnit: String,
    amountToCount: Int,
    selected: Long,
    onButtonClicked: (String, Long) -> Unit
) {
    val width = 110.dp
    Column(modifier = Modifier.background(MyColors.LightBlue)) {
        Box(
            modifier = Modifier
                .width(width)
                .background(MyColors.DarkBlue),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = timeUnit,
                fontSize = 20.sp
            )
        }
        Column(
            modifier = Modifier
                .width(width)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            repeat(amountToCount) { currentItemCount ->
                val colors = if (selected == currentItemCount.toLong()) {
                    MyColors.DarkBlue
                } else {
                    MyColors.LightBlue
                }

                Button(
                    onClick = {
                        onButtonClicked(timeUnit, currentItemCount.toLong())
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = colors)
                ) {
                    Text(
                        text = "$currentItemCount",
                        fontSize = 16.sp,
                    )
                }
            }
        }
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MainScreen()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MainScreen()
    }
}
