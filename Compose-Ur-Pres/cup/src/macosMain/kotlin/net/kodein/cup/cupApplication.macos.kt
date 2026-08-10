package net.kodein.cup

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.window.Window
import platform.AppKit.NSApp
import platform.AppKit.NSApplication
import platform.AppKit.NSApplicationActivationPolicy


internal actual fun cupPlatformApplication(
    title: String,
    content: @Composable () -> Unit
) {
    NSApplication.sharedApplication()
    NSApp?.setActivationPolicy(NSApplicationActivationPolicy.NSApplicationActivationPolicyRegular)
    Window(title = title) {
        withPresentationState {
            val focusRequester = remember { FocusRequester() }
            val onKey = PresentationKeyHandler().asComposeKeyHandler()
            Box(
                Modifier
                    .fillMaxSize()
                    .focusRequester(focusRequester)
                    .focusable()
                    .onKeyEvent(onKey)
            ) {
                content()
            }
            LaunchedEffect(Unit) { focusRequester.requestFocus() }
        }
    }
    NSApp?.activateIgnoringOtherApps(true)
    NSApp?.run()
}
