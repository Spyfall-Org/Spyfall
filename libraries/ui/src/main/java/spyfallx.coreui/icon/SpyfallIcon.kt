package spyfallx.ui.icon

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
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
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
import spyfallx.ui.PreviewContent
import spyfallx.ui.components.text.Text


enum class SpyfallIcon(val imageVector: ImageVector) {
    Add(Icons.Rounded.Add),
    ArrowBack(Icons.Rounded.ArrowBack),
    Bookmark(Icons.Rounded.Bookmark),
    BookmarkBorder(Icons.Rounded.BookmarkBorder),
    Bookmarks(Icons.Rounded.Bookmarks),
    BookmarksBorder(Icons.Outlined.Bookmarks),
    Info(Icons.Default.Info),
    Check(Icons.Rounded.Check),
    Close(Icons.Rounded.Close),
    Grid3x3(Icons.Rounded.Grid3x3),
    MoreVert(Icons.Default.MoreVert),
    Person(Icons.Rounded.Person),
    Search(Icons.Rounded.Search),
    Settings(Icons.Rounded.Settings),
    ShortText(Icons.Rounded.ShortText),
    Upcoming(Icons.Rounded.Upcoming),
    UpcomingBorder(Icons.Outlined.Upcoming),
    ViewDay(Icons.Rounded.ViewDay),
    ChevronLeft(Icons.Default.ChevronLeft),
    ChevronRight(Icons.Default.ChevronRight);
}

@Preview(device = "spec:id=reference_phone,shape=Normal,width=800,height=600,unit=dp,dpi=200")
@Composable
@Suppress("MagicNumber")
private fun IconGridPreview() {
    PreviewContent(showBackground = true) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(SpyfallIcon.values()) { icon ->
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
                    Text(text = icon.name)
                }
            }
        }
    }
}
