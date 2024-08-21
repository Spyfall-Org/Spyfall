package com.dangerfield.libraries.game.internal.packs

import com.dangerfield.libraries.game.GetNewCustomPackId
import se.ansman.dagger.auto.AutoBind
import java.util.UUID
import javax.inject.Inject

const val CUSTOM_PACK_PREFIX = "custom_pack_"

@AutoBind
class GetNewCustomPackIdImpl @Inject constructor(): GetNewCustomPackId {
    override fun invoke(): String {
        return CUSTOM_PACK_PREFIX + UUID.randomUUID().toString()
    }
}