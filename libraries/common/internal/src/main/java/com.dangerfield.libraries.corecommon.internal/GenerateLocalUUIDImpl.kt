package com.dangerfield.libraries.corecommon.internal

import oddoneout.core.GenerateLocalUUID
import se.ansman.dagger.auto.AutoBind
import java.util.UUID
import javax.inject.Inject

@AutoBind
class GenerateLocalUUIDImpl @Inject constructor(): GenerateLocalUUID {

    override fun invoke(): String = "generated_" + UUID.randomUUID().toString()
}