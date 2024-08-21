package com.dangerfield.libraries.game

object LocationPackFakes {
    val Pack1 = Pack.LocationPack(
        locations = listOf(
            LocationFakes.Location1,
            LocationFakes.Location2,
            LocationFakes.Location3,
            LocationFakes.Location4,
            LocationFakes.Location5,
        ),
        name = "Location Pack 1",
        id = "1",
        version = 1,
        languageCode = "en",
        isPublic = true,
        owner = OwnerDetails.MeUser,
        isUserSaved = false,
        hasUserPlayed = false
    )

    val Pack2 = Pack.LocationPack(
        locations = listOf(
            LocationFakes.Location3,
            LocationFakes.Location4,
            LocationFakes.Location5,
            LocationFakes.Location7,
        ),
        name = "Location Pack 2",
        id = "1",
        version = 1,
        languageCode = "en",
        isPublic = true,
        owner = OwnerDetails.MeUser,
        isUserSaved = false,
        hasUserPlayed = false
    )

    val Pack3 = Pack.LocationPack(
        locations = listOf(
            LocationFakes.Location1,
            LocationFakes.Location3,
            LocationFakes.Location5,
            LocationFakes.Location6,
        ),
        name = "Location Pack 3",
        id = "1",
        version = 1,
        languageCode = "en",
        isPublic = true,
        owner = OwnerDetails.MeUser,
        isUserSaved = false,
        hasUserPlayed = false
    )
}

object LocationFakes {
    val Location1 = PackItem.Location(
        name = "Island",
        roles = listOf(
            "Pirate",
            "Parrot",
            "Treasure Hunter",
            "Cannibal",
            "Shipwreck Survivor",
            "Castaway",
            "Mermaid",
            "Captain",
            "Siren",
            "Ghost",
            "Sea Monster",
            "Kraken",
        )
    )

    val Location2 = PackItem.Location(
        name = "Space Station",
        roles = listOf(
            "Astronaut",
            "Alien",
            "Robot",
            "Scientist",
            "Engineer",
            "Captain",
            "Doctor",
            "Security Officer",
            "Communications Officer",
            "Navigator",
            "Janitor",
            "Cook",
        )
    )

    val Location3 = PackItem.Location(
        name = "Hospital",
        roles = listOf(
            "Doctor",
            "Nurse",
            "Patient",
            "Surgeon",
            "Therapist",
            "Receptionist",
            "Janitor",
            "Security Guard",
            "Anesthesiologist",
            "Pediatrician",
            "Psychiatrist",
            "Radiologist",
        )
    )

    val Location4 = PackItem.Location(
        name = "School",
        roles = listOf(
            "Teacher",
            "Student",
            "Principal",
            "Janitor",
            "Lunch Lady",
            "Coach",
            "Librarian",
            "Counselor",
            "Bus Driver",
            "Secretary",
            "Nurse",
            "Security Guard",
        )
    )

    val Location5 = PackItem.Location(
        name = "Zoo",
        roles = listOf(
            "Zookeeper",
            "Visitor",
            "Animal",
            "Veterinarian",
            "Janitor",
            "Security Guard",
            "Cashier",
            "Educator",
            "Trainer",
            "Photographer",
            "Food Vendor",
            "Gift Shop Clerk",
        )
    )

    val Location6 = PackItem.Location(
        name = "Cruise Ship",
        roles = listOf(
            "Captain",
            "Crew Member",
            "Passenger",
            "Entertainer",
            "Chef",
            "Bartender",
            "Security Guard",
            "Janitor",
            "Tourist",
            "Photographer",
            "Musician",
            "Dancer",
        )
    )

    val Location7 = PackItem.Location(
        name = "Castle",
        roles = listOf(
            "King",
            "Queen",
            "Knight",
            "Princess",
            "Prince",
            "Jester",
            "Wizard",
            "Dragon",
            "Servant",
            "Blacksmith",
            "Baker",
            "Guard",
        )
    )
}