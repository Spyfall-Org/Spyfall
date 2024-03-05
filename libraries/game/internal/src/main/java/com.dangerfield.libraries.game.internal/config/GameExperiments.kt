@file:Suppress("MatchingDeclarationName")

package com.dangerfield.libraries.game.internal.config

import com.dangerfield.libraries.config.AppConfigMap
import com.dangerfield.libraries.config.Experiment
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
class IsSingleDeviceModeEnabled @Inject constructor(
    private val appConfigMap: AppConfigMap
): Experiment<Boolean>() {
    override val displayName: String
        get() = "Single Device Mode"

    override val id: String = "isSingleDeviceModeEnabled"

    override val control: Boolean
        get() = false

    override val test: Boolean
        get() = true

    override val isDebugOnly: Boolean
        get() = false

    override val default: Boolean
        get() = true

    override fun resolveValue(): Boolean = appConfigMap.experiment(this)
}

@AutoBindIntoSet
class IsVideoCallLinkEnabled @Inject constructor(
    private val appConfigMap: AppConfigMap
): Experiment<Boolean>() {
    override val displayName: String
        get() = "Video Call Links Enabled"

    override val id: String
        get() = "isVideoCallLinkEnabled"

    override val control: Boolean
        get() = false

    override val test: Boolean
        get() = true

    override val isDebugOnly: Boolean
        get() = false

    override val default: Boolean
        get() = true

    override fun resolveValue(): Boolean = appConfigMap.experiment(this)
}


/*
To create a virtual game
- add a switch to the new game screen with "Open to anyone"
- require a valid video link

To join a virtual game
- add option to welcome screen to join a lobby or something

to find the game we will check the collection
public-games-en or public-games-fr just appending the locale of the user.

when someone creates a game that is public it will get added to the normal games collection
but also to the public games collection (just the access code)
when a public game reaches max players or starts it will be removed from the public games collection
when it either is reset or players leave it will be added back to the public games collection

so when someone joins a public game we will check the public games collection for the access code
and it will try to join that game

hypothetically someone could join right before the started boolean hits true if we double check again
before really starting then we can reassign roles

what if someone starts the game and starts assigning roles but that takes SUPER long
someone could join in the meantime.

maybe we double check on the game screen that everyone has a role and if not then the host will
have to reassign roles.

problem with the idea of the host, if the host leaves then someone else will need to be assigned the host
 */
@AutoBindIntoSet
class IsPublicGamingEnabled @Inject constructor(
    private val appConfigMap: AppConfigMap
): Experiment<Boolean>() {
    override val displayName: String
        get() = "Public Gaming"

    override val id: String
        get() = "isPublicGamingEnabled"

    override val description: String
        get() = "Create games open to anyone to join, video link required."

    override val control: Boolean
        get() = false

    override val test: Boolean
        get() = true

    override val isDebugOnly: Boolean
        get() = true

    override fun resolveValue(): Boolean = appConfigMap.experiment(this)
}

