package com.dangerfield.libraries.dictionary

import oddoneout.core.Try
import oddoneout.core.logOnError

fun String.applyArgs(args: Map<String,String>): String {
    var result = this
    args.forEach { (key, value) ->
        Try {
            result = result.replace("{$key}", value)
        }
            .logOnError()
    }
    return result
}
