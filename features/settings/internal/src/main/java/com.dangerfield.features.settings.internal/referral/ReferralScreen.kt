package com.dangerfield.features.settings.internal.referral

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.Card
import com.dangerfield.libraries.ui.Dimension
import com.dangerfield.libraries.ui.FieldState
import com.dangerfield.libraries.ui.HorizontalSpacerD200
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.VerticalSpacerD1200
import com.dangerfield.libraries.ui.VerticalSpacerD800
import com.dangerfield.libraries.ui.components.CircularProgressIndicator
import com.dangerfield.libraries.ui.components.HorizontalDivider
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonStyle
import com.dangerfield.libraries.ui.components.header.Header
import com.dangerfield.libraries.ui.components.icon.Icon
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.components.text.InputField
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

@Composable
fun ReferralScreen(
    meReferralCode: String,
    meRedemptionStatus: RedemptionStatus?,
    referralCodeFieldState: FieldState<String>,
    featureMessage: String? = null,
    isFormValid: Boolean,
    maxRedemptions: Int,
    isLoading: Boolean,
    onRedeemClicked: () -> Unit,
    onNavigateBack: () -> Unit,
    onReferralCodeFieldUpdated: (String) -> Unit,
) {
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    Screen(
        topBar = {
            Header(
                title = if (featureMessage != null) "Unlock feature" else "Referral",
                onNavigateBack = onNavigateBack
            )
        }
    ) {
        Column(
            Modifier
                .verticalScroll(scrollState)
                .padding(it)
                .padding(horizontal = Dimension.D1000),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            VerticalSpacerD800()

            if (featureMessage != null) {
                Text(
                    text = featureMessage,
                    typography = OddOneOutTheme.typography.Body.B600
                )
                VerticalSpacerD800()

                HorizontalDivider()

                VerticalSpacerD800()

            }

            Text(
                text = "Your Code:",
                typography = OddOneOutTheme.typography.Body.B600
            )

            VerticalSpacerD800()
            Card {
                Text(
                    text = meReferralCode,
                    typography = OddOneOutTheme.typography.Display.D800
                )
            }

            VerticalSpacerD800()
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (meRedemptionStatus == RedemptionStatus.Redeemed) {
                    Icon(spyfallIcon = SpyfallIcon.Check(null))
                    HorizontalSpacerD200()
                }

                if (meRedemptionStatus != null) {
                    Text(
                        text = if (meRedemptionStatus == RedemptionStatus.Redeemed) {
                            "Redeemed"
                        } else {
                            "Not Redeemed Yet."
                        },
                        typography = OddOneOutTheme.typography.Body.B600
                    )
                }
            }

            VerticalSpacerD1200()

            HorizontalDivider()

            VerticalSpacerD1200()

            InputField(
                title = "Redeem:",
                subtitle = "Redeem another players code to earn you both rewards. You can only redeem ${maxRedemptions} codes.",
                hint = "Enter a referral code",
                hideErrorWhen = { _, _ -> false },
                fieldState = referralCodeFieldState,
                keyboardActions = KeyboardActions {
                    focusManager.clearFocus()
                    if (isFormValid) {
                        onRedeemClicked()
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                onFieldUpdated = onReferralCodeFieldUpdated
            )

            VerticalSpacerD1200()


            if (isLoading) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Button(
                    onClick = onRedeemClicked,
                    modifier = Modifier.fillMaxWidth(),
                    style = ButtonStyle.Background,
                    enabled = isFormValid,
                ) {
                    Text(text = "Redeem")
                }
            }

            VerticalSpacerD1200()
        }
    }
}

@Preview
@Composable
fun PreviewJoinGameScreenFeature() {
    Preview {
        ReferralScreen(
            referralCodeFieldState = FieldState.Idle(""),
            isFormValid = false,
            isLoading = false,
            featureMessage = "In order to unlock custom pack creation you must have another player redeem your code. Share your code with a friend and have them enter it here.",
            onRedeemClicked = { -> },
            onNavigateBack = { -> },
            onReferralCodeFieldUpdated = { _ -> },
            meReferralCode = "abc1234",
            meRedemptionStatus = RedemptionStatus.Redeemed,
            maxRedemptions = 1
        )
    }
}


@Preview
@Composable
fun PreviewJoinGameScreen() {
    Preview {
        ReferralScreen(
            referralCodeFieldState = FieldState.Idle(""),
            isFormValid = false,
            isLoading = false,
            onRedeemClicked = { -> },
            onNavigateBack = { -> },
            onReferralCodeFieldUpdated = { _ -> },
            meReferralCode = "abc1234",
            meRedemptionStatus = RedemptionStatus.NotRedeemed,
            maxRedemptions = 1
        )
    }
}
