package com.dangerfield.libraries.game.storage

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.time.Instant


@Entity(
    "packs",
    // packs are also unique by name, languageCode, and version combines
    indices = [(Index(value = ["name","languageCode", "version"], unique = true))],
)
data class PackEntity(
    @PrimaryKey val id: String,
    val name: String,
    val version: Int,
    val languageCode: String,
    val groupId: String?,
    val type: DbPackType,
    val isPublic: Boolean,
    val ownerId: String?,
    val dbPackOwner: DbPackOwner,
    val isUserSaved: Boolean
)

enum class DbPackType {
    Location,
    Celebrity
}

enum class DbPackOwner {
    User,
    Community,
    App
}

@Entity(
    "pack_items",
    indices = [(Index(value = ["name"], unique = true)), Index(value = ["packId"])]
)
data class PackItemEntity(
   @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val roles: List<String>?,
    val languageCode: String,
    val packId: String
)

@Entity(
    tableName = "pack_access_records",
    foreignKeys = [
        ForeignKey(
            entity = PackEntity::class,
            parentColumns = ["id"],
            childColumns = ["packId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PackAccessRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val packId: String,
    val lastAccessed: Instant
)

data class PackWithItems(
    @Embedded val pack: PackEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "packId"
    )
    val items: List<PackItemEntity>
)
