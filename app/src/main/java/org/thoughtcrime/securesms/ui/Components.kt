package org.thoughtcrime.securesms.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonColors
import androidx.compose.material.Card
import androidx.compose.material.Colors
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.pager.HorizontalPagerIndicator
import kotlinx.coroutines.launch
import network.loki.messenger.R
import org.session.libsession.utilities.recipients.Recipient
import org.thoughtcrime.securesms.components.ProfilePictureView

@Composable
fun ItemButton(
    text: String,
    @DrawableRes icon: Int,
    colors: ButtonColors = transparentButtonColors(),
    contentDescription: String = text,
    onClick: () -> Unit
) {
    TextButton(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        colors = colors,
        onClick = onClick,
        shape = RectangleShape,
    ) {
        Box(modifier = Modifier
            .width(80.dp)
            .fillMaxHeight()) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = contentDescription,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Text(text, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun Cell(content: @Composable () -> Unit) {
    CellWithPaddingAndMargin(padding = 0.dp) { content() }
}
@Composable
fun CellNoMargin(content: @Composable () -> Unit) {
    CellWithPaddingAndMargin(padding = 0.dp, margin = 0.dp) { content() }
}

@Composable
fun CellWithPaddingAndMargin(
    padding: Dp = 24.dp,
    margin: Dp = 32.dp,
    content: @Composable () -> Unit
) {
    Card(
        backgroundColor = MaterialTheme.colors.cellColor,
        shape = RoundedCornerShape(16.dp),
        elevation = 0.dp,
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(horizontal = margin),
    ) {
        Box(Modifier.padding(padding)) { content() }
    }
}

private val Colors.cellColor: Color
    @Composable
    get() = LocalExtraColors.current.settingsBackground

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BoxScope.HorizontalPagerIndicator(pagerState: PagerState) {
    if (pagerState.pageCount >= 2) Card(
        shape = RoundedCornerShape(50.dp),
        backgroundColor = Color.Black.copy(alpha = 0.4f),
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(8.dp)
    ) {
        Box(modifier = Modifier.padding(8.dp)) {
            HorizontalPagerIndicator(
                pagerState = pagerState,
                pageCount = pagerState.pageCount,
                activeColor = Color.White,
                inactiveColor = classicDarkColors[5])
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RowScope.CarouselPrevButton(pagerState: PagerState) {
    CarouselButton(pagerState, pagerState.canScrollBackward, R.drawable.ic_prev, -1)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RowScope.CarouselNextButton(pagerState: PagerState) {
    CarouselButton(pagerState, pagerState.canScrollForward, R.drawable.ic_next, 1)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RowScope.CarouselButton(
    pagerState: PagerState,
    enabled: Boolean,
    @DrawableRes id: Int,
    delta: Int
) {
    if (pagerState.pageCount <= 1) Spacer(modifier = Modifier.width(32.dp))
    else {
        val animationScope = rememberCoroutineScope()
        IconButton(
            modifier = Modifier
                .width(40.dp)
                .align(Alignment.CenterVertically),
            enabled = enabled,
            onClick = { animationScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + delta) } }) {
            Icon(
                painter = painterResource(id = id),
                contentDescription = "",
            )
        }
    }
}

@Composable
fun Divider() {
    androidx.compose.material.Divider(
        modifier = Modifier.padding(horizontal = 16.dp),
    )
}

@Composable
fun RowScope.Avatar(recipient: Recipient) {
    Box(
        modifier = Modifier
            .width(60.dp)
            .align(Alignment.CenterVertically)
    ) {
        AndroidView(
            factory = {
                ProfilePictureView(it).apply { update(recipient) }
            },
            modifier = Modifier
                .width(46.dp)
                .height(46.dp)
        )
    }
}
