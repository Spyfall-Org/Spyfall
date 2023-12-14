package com.dangerfield.features.gameplay.internal

class Fake() {
    val players = listOf(
        DisplayablePlayer("John", "123", role = "role", isFirst = false, isOddOneOut = false),
        DisplayablePlayer(
            "Jacob",
            "234234",
            role = "role",
            isFirst = false,
            isOddOneOut = false
        ),
        DisplayablePlayer(
            "Jingleheimer",
            "456453",
            role = "role",
            isFirst = true,
            isOddOneOut = false
        ),
        DisplayablePlayer(
            "Schmidt",
            "334566",
            role = "role",
            isFirst = false,
            isOddOneOut = false
        ),
    )
}
