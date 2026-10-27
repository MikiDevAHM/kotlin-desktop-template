package com.example.app.ui.types

import androidx.compose.runtime.Composable
import com.example.app.generated.resources.Res
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable fun res(resource: StringResource): String = stringResource(resource)
@Composable fun res(resource: StringResource, vararg args: Any): String = stringResource(resource, *args)

internal typealias str = Res.string
