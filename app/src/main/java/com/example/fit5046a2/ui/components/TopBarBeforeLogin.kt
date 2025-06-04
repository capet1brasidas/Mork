package com.example.fit5046a2.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension


    @Composable
    fun TopBarBeforeLogin(
        title: String  = "Welcome to your workplace!",
        modifier: Modifier = Modifier.Companion
    ) {
        ConstraintLayout(modifier = modifier) {
            val (backgroundBox, titleText) = createRefs()

            Box(
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .height(69.dp)
                    .background(Color(0.48f, 0.79f, 0.9f, 1.0f))
                    .constrainAs(backgroundBox) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )

            Text(
                title,
                modifier = Modifier.Companion.constrainAs(titleText) {
                    top.linkTo(parent.top, margin = 50.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.Companion.value(180.dp)
                    height = Dimension.Companion.value(20.dp)
                },
                style = LocalTextStyle.current.copy(
                    color = Color.Companion.White,
                    textAlign = TextAlign.Companion.Center,
                    fontSize = 14.sp
                )
            )
        }
    }