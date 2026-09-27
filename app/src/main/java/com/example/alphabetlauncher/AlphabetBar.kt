package com.example.alphabetlauncher

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.exp
import kotlinx.coroutines.launch

private val ALPHABET: List<Char> = ('A'..'Z').toList()


@Composable
fun AlphabetBar(
    modifier: Modifier = Modifier,
    onLetterChanged: (Char?) -> Unit
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val haptics = LocalHapticFeedback.current

    var barHeightPx by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var activeLetter by remember { mutableStateOf<Char?>(null) }
    var touchYPx by remember { mutableFloatStateOf(0f) }

    val maxOffsetPx = with(density) { 48.dp.toPx() }
    val offsets = remember { ALPHABET.map { Animatable(0f) } }

    fun letterForY(y: Float): Char {
        if (barHeightPx <= 0f) return ALPHABET.first()
        val slotHeight = barHeightPx / ALPHABET.size
        val index = (y / slotHeight).toInt().coerceIn(0, ALPHABET.size - 1)
        return ALPHABET[index]
    }

    fun applyCurve(y: Float) {
        val slotHeight = if (barHeightPx > 0f) barHeightPx / ALPHABET.size else 1f
        val sigma = slotHeight * 2.0f
        offsets.forEachIndexed { i, anim ->
            val slotCenter = slotHeight * (i + 0.5f)
            val dy = slotCenter - y
            val influence = exp(-(dy * dy) / (2f * sigma * sigma))
            scope.launch { anim.snapTo(-maxOffsetPx * influence) }
        }
    }

    fun release() {
        isDragging = false
        activeLetter = null
        onLetterChanged(null)
        offsets.forEach { anim ->
            scope.launch {
                anim.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
        }
    }

    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(28.dp)
                .onGloballyPositioned { barHeightPx = it.size.height.toFloat() }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            isDragging = true
                            touchYPx = offset.y
                            val letter = letterForY(offset.y)
                            activeLetter = letter
                            onLetterChanged(letter)
                            applyCurve(offset.y)
                        },
                        onDrag = { change, _ ->
                            touchYPx = change.position.y
                            val letter = letterForY(change.position.y)
                            if (letter != activeLetter) {
                                activeLetter = letter
                                onLetterChanged(letter)
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                            applyCurve(change.position.y)
                        },
                        onDragEnd = { release() },
                        onDragCancel = { release() }
                    )
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(bottom = 2.dp)
                    .size(12.dp)
            )

            ALPHABET.forEachIndexed { index, letter ->
                val offsetX = offsets[index].value
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .offset(x = with(density) { offsetX.toDp() }),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = letter.toString(),
                        fontSize = 10.sp,
                        fontWeight = if (letter == activeLetter) FontWeight.Bold else FontWeight.Normal,
                        color = if (letter == activeLetter)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Box(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurfaceVariant)
            )
        }

        val bubbleHalfHeightPx = with(density) { 28.dp.toPx() }
        val bubbleYDp = with(density) { (touchYPx - bubbleHalfHeightPx).toDp() }

        AnimatedVisibility(
            visible = isDragging && activeLetter != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-56).dp, y = bubbleYDp)
        ) {
            LetterBubble(letter = activeLetter ?: ' ')
        }
    }
}

@Composable
private fun LetterBubble(letter: Char) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letter.toString(),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}
