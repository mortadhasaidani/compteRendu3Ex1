/*
 * Copyright (C) 2023 The Android Open Source Project
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
package com.example.racetracker.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.racetracker.R
import com.example.racetracker.ui.theme.RaceTrackerTheme
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun RaceTrackerApp() {
    val playerOne = remember { RaceParticipant("Player 1", progressIncrement = 1) }
    val playerTwo = remember { RaceParticipant("Player 2", progressIncrement = 2) }

    var raceInProgress by remember { mutableStateOf(false) }

    LaunchedEffect(raceInProgress) {
        if (raceInProgress) {
            coroutineScope {
                launch { playerOne.run() }
                launch { playerTwo.run() }
            }
            raceInProgress = false
        }
    }

    RaceTrackerScreen(
        playerOne = playerOne,
        playerTwo = playerTwo,
        raceInProgress = raceInProgress,
        onRaceToggle = { raceInProgress = it },
        onResetRace = {
            playerOne.reset()
            playerTwo.reset()
            raceInProgress = false
        },
        modifier = Modifier
            .statusBarsPadding()
            .safeDrawingPadding()
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = dimensionResource(R.dimen.padding_medium))
    )
}

@Composable
private fun RaceTrackerScreen(
    playerOne: RaceParticipant,
    playerTwo: RaceParticipant,
    raceInProgress: Boolean,
    onRaceToggle: (Boolean) -> Unit,
    onResetRace: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.run_a_race),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(vertical = dimensionResource(R.dimen.padding_medium))
        )

        Icon(
            painter = painterResource(R.drawable.ic_walk),
            contentDescription = null,
            modifier = Modifier
                .padding(bottom = dimensionResource(R.dimen.padding_medium))
                .size(dimensionResource(R.dimen.icon_size))
        )

        StatusIndicator(
            participantName = playerOne.name,
            currentProgress = playerOne.currentProgress,
            maxProgress = stringResource(R.string.progress_percentage, playerOne.maxProgress),
            progressFactor = playerOne.progressFactor,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_large)))

        StatusIndicator(
            participantName = playerTwo.name,
            currentProgress = playerTwo.currentProgress,
            maxProgress = stringResource(R.string.progress_percentage, playerTwo.maxProgress),
            progressFactor = playerTwo.progressFactor,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_large)))

        RaceControls(
            isRunning = raceInProgress,
            onRunStateChange = onRaceToggle,
            onReset = onResetRace,
            modifier = Modifier.fillMaxWidth()
        )
    }

@Composable
private fun StatusIndicator(
    participantName: String,
    currentProgress: Int,
    maxProgress: String,
    progressFactor: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
    ) {
        Text(
            text = participantName,
            modifier = Modifier.padding(end = dimensionResource(R.dimen.padding_small))
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
        ) {
            LinearProgressIndicator(
                progress = progressFactor,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.progress_indicator_height))
                    .clip(RoundedCornerShape(dimensionResource(R.dimen.progress_indicator_corner_radius)))
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.progress_percentage, currentProgress),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = maxProgress,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun RaceControls(
    onRunStateChange: (Boolean) -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
    isRunning: Boolean = true,
) {
    Column(
        modifier = modifier.padding(top = dimensionResource(R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
    ) {
        Button(
            onClick = { onRunStateChange(!isRunning) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (isRunning) stringResource(R.string.pause) else stringResource(R.string.start))
        }
        OutlinedButton(
            onClick = onReset,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.reset))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RaceTrackerAppPreview() {
    RaceTrackerTheme {
        RaceTrackerApp()
    }
}
