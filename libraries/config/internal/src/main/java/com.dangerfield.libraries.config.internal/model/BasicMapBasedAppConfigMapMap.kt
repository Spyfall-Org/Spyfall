package com.dangerfield.libraries.config.internal.model

import com.dangerfield.libraries.config.AppConfigMap

/**
 * App config implementation based on basic map passed as input
 */
class BasicMapBasedAppConfigMapMap(
    override val map: Map<String, *>,
) : AppConfigMap()
