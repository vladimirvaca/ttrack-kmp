package com.rvladimir.ttrack.core.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.rvladimir.ttrack.ui.theme.BrandGreen
import com.rvladimir.ttrack.ui.theme.InputBackground
import com.rvladimir.ttrack.ui.theme.InputBorder
import com.rvladimir.ttrack.ui.theme.TextGray

/**
 * Shared styled [OutlinedTextField] used across the login and registration forms.
 *
 * Handles the common pattern of an optional leading icon, optional password-visibility
 * toggle, and the consistent brand styling (rounded corners, [BrandGreen] focus border,
 * [InputBackground] container, [InputBorder] unfocused border).
 *
 * @param value Current text value.
 * @param onValueChange Called on every user keystroke.
 * @param placeholder Hint text shown when the field is empty.
 * @param modifier Optional outer modifier (default fills max width).
 * @param leadingIcon Optional icon displayed at the start of the field.
 * @param isPassword When `true`, applies [PasswordVisualTransformation] and shows the toggle icon.
 * @param passwordVisible Whether the password characters are currently readable.
 * @param onPasswordToggle Called when the user taps the visibility icon.
 * @param keyboardType The keyboard type to show (default: [KeyboardType.Text]).
 */
@Composable
internal fun TTrackTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordToggle: () -> Unit = {},
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = TextGray) },
        leadingIcon =
            leadingIcon?.let {
                {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = BrandGreen,
                        modifier = Modifier.size(20.dp),
                    )
                }
            },
        trailingIcon =
            if (isPassword) {
                {
                    IconButton(onClick = onPasswordToggle) {
                        Icon(
                            imageVector =
                                if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription =
                                if (passwordVisible) "Hide password" else "Show password",
                            tint = TextGray,
                        )
                    }
                }
            } else {
                null
            },
        visualTransformation =
            if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(12.dp),
        colors =
            OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = InputBackground,
                focusedContainerColor = InputBackground,
                unfocusedBorderColor = InputBorder,
                focusedBorderColor = BrandGreen,
            ),
        singleLine = true,
    )
}
