package com.jetbrains.kmpapp.screens.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
internal fun AppAlertDialog(
    titleText: String? = null,
    messageText: String? = null,
    confirmText: String,
    dismissText: String? = null,
    onDismissRequest: (() -> Unit)? = null,
    onClickConfirmButton: (() -> Unit)? = null,
    onClickDismissButton: (() -> Unit)? = null
) {
    var openDialog by rememberSaveable { mutableStateOf(true) }
    AppAlertDialogStateless(
        titleText = titleText,
        messageText = messageText,
        confirmText = confirmText,
        dismissText = dismissText,
        openDialog = openDialog,
        onDismissRequest = {
            openDialog = false
            onDismissRequest?.invoke()
        },
        onClickConfirmButton = {
            openDialog = false
            onClickConfirmButton?.invoke()
        },
        onClickDismissButton = {
            openDialog = false
            onClickDismissButton?.invoke()
        },
    )
}

@Composable
private fun AppAlertDialogStateless(
    titleText: String? = null,
    messageText: String? = null,
    confirmText: String,
    dismissText: String? = null,
    openDialog: Boolean,
    onDismissRequest: () -> Unit,
    onClickConfirmButton: () -> Unit,
    onClickDismissButton: (() -> Unit?)? = null
) {
    if (openDialog) {
        AlertDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onDismissRequest.
                onDismissRequest()
            },
            title = {
                titleText?.let { Text(text = it) }
            },
            text = {
                messageText?.let { Text(text = it) }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClickConfirmButton()
                    }
                ) {
                    Text(confirmText)
                }
            },
            dismissButton = {
                dismissText?.let {
                    TextButton(
                        onClick = {
                            onClickDismissButton?.invoke()
                        }
                    ) {
                        Text(text = it)
                    }
                }
            }
        )
    }
}