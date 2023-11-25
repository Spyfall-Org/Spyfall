package com.dangerfield.libraries.ui.icon

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Upcoming
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.Bookmarks
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Grid3x3
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.ShortText
import androidx.compose.material.icons.rounded.Upcoming
import androidx.compose.material.icons.rounded.ViewDay
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.components.text.Text


sealed class SpyfallIcon(
    val imageVector: ImageVector,
    val contentDescription: String
) {
    class Add(contentDescription: String) : SpyfallIcon(
        imageVector = Icons.Rounded.Add,
        contentDescription = contentDescription
    )

    class ArrowBack(contentDescription: String) : SpyfallIcon(
        imageVector = Icons.Rounded.ArrowBack,
        contentDescription = contentDescription
    )

    class Bookmark(contentDescription: String) : SpyfallIcon(
        imageVector = Icons.Rounded.Bookmark,
        contentDescription = contentDescription
    )

    class BookmarkBorder(contentDescription: String) : SpyfallIcon(
        contentDescription = contentDescription,
        imageVector = Icons.Rounded.BookmarkBorder,
    )

    class Bookmarks(contentDescription: String) :
        SpyfallIcon(imageVector = Icons.Rounded.Bookmarks, contentDescription = contentDescription)

    class BookmarksBorder(contentDescription: String) :
        SpyfallIcon(imageVector = Icons.Outlined.Bookmarks, contentDescription = contentDescription)

    class Info(contentDescription: String) :
        SpyfallIcon(imageVector = Icons.Default.Info, contentDescription = contentDescription)

    class Check(contentDescription: String) :
        SpyfallIcon(imageVector = Icons.Rounded.Check, contentDescription = contentDescription)

    class Close(contentDescription: String) :
        SpyfallIcon(imageVector = Icons.Rounded.Close, contentDescription = contentDescription)

    class Grid3x3(contentDescription: String) :
        SpyfallIcon(imageVector = Icons.Rounded.Grid3x3, contentDescription = contentDescription)

    class MoreVert(contentDescription: String) :
        SpyfallIcon(imageVector = Icons.Default.MoreVert, contentDescription = contentDescription)

    class Person(contentDescription: String) :
        SpyfallIcon(imageVector = Icons.Rounded.Person, contentDescription = contentDescription)

    class Search(contentDescription: String) :
        SpyfallIcon(imageVector = Icons.Rounded.Search, contentDescription = contentDescription)

    class Settings(contentDescription: String) :
        SpyfallIcon(imageVector = Icons.Rounded.Settings, contentDescription = contentDescription)

    class ShortText(contentDescription: String) :
        SpyfallIcon(imageVector = Icons.Rounded.ShortText, contentDescription = contentDescription)

    class Upcoming(contentDescription: String) :
        SpyfallIcon(imageVector = Icons.Rounded.Upcoming, contentDescription = contentDescription)

    class UpcomingBorder(contentDescription: String) :
        SpyfallIcon(imageVector = Icons.Outlined.Upcoming, contentDescription = contentDescription)

    class ViewDay(contentDescription: String) :
        SpyfallIcon(imageVector = Icons.Rounded.ViewDay, contentDescription = contentDescription)

    class ChevronLeft(contentDescription: String) : SpyfallIcon(
        imageVector = Icons.Default.ChevronLeft,
        contentDescription = contentDescription
    )

    class ChevronRight(contentDescription: String) : SpyfallIcon(
        imageVector = Icons.Default.ChevronRight,
        contentDescription = contentDescription
    )

    class Theme(contentDescription: String) : SpyfallIcon(
        imageVector = Icons.Default.Palette,
        contentDescription = contentDescription
    )

    class Chat(contentDescription: String) : SpyfallIcon(
        imageVector = Icons.Default.ChatBubble,
        contentDescription = contentDescription
    )

    class Android(contentDescription: String) : SpyfallIcon(
        imageVector = Icons.Default.Android,
        contentDescription = contentDescription
    )
}

private val allIcons = listOf(
    SpyfallIcon.Add(""),
    SpyfallIcon.ArrowBack(""),
    SpyfallIcon.Bookmark(""),
    SpyfallIcon.BookmarkBorder(""),
    SpyfallIcon.Bookmarks(""),
    SpyfallIcon.BookmarkBorder(""),
    SpyfallIcon.BookmarksBorder(""),
    SpyfallIcon.Info(""),
    SpyfallIcon.Check(""),
    SpyfallIcon.Chat(""),
    SpyfallIcon.Close(""),
    SpyfallIcon.Grid3x3(""),
    SpyfallIcon.MoreVert(""),
    SpyfallIcon.Person(""),
    SpyfallIcon.Search(""),
    SpyfallIcon.Settings(""),
    SpyfallIcon.ShortText(""),
    SpyfallIcon.Upcoming(""),
    SpyfallIcon.UpcomingBorder(""),
    SpyfallIcon.ViewDay(""),
    SpyfallIcon.ChevronLeft(""),
    SpyfallIcon.ChevronRight(""),
    SpyfallIcon.Theme(""),
    SpyfallIcon.Android(""),
    )

@Preview(device = "spec:id=reference_phone,shape=Normal,width=1000,height=1200,unit=dp,dpi=200")
@Composable
@Suppress("MagicNumber")
private fun IconGridPreview() {
    PreviewContent(showBackground = true) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(allIcons) { icon ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(
                        imageVector = icon.imageVector,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = icon::class.java.simpleName)
                }
            }
        }
    }
}
