package com.dangerfield.libraries.config.internal

import com.dangerfield.libraries.config.AppConfigMap

/**
 * App config implementation based on basic map passed as input
 */
class BasicAppConfigMapMap(override val map: Map<String, *>) : AppConfigMap()

