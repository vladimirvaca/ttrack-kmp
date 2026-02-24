package com.rvladimir.ttrack

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.rvladimir.ttrack.core.navigation.AppNavGraph
import com.rvladimir.ttrack.ui.theme.TTrackTheme

@Composable
@Preview
fun App() {
    TTrackTheme {
        AppNavGraph()
    }
}
