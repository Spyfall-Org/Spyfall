package com.dangerfield.libraries.game.internal

import com.dangerfield.libraries.game.GenerateLocalUUID
import se.ansman.dagger.auto.AutoBind
import java.util.UUID
import javax.inject.Inject

@AutoBind
class GenerateLocalUUIDImpl @Inject constructor(): GenerateLocalUUID {

    override fun invoke(): String = "generated_" + UUID.randomUUID().toString()
}