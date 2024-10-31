package com.dicoding.jetheroes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.dicoding.jetheroes.model.HeroesData
import com.dicoding.jetheroes.ui.theme.JetHeroesTheme
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dicoding.jetheroes.data.HeroesRepository

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun JetHeroesApp(
    modifier: Modifier = Modifier,
    viewModel: JetHeroesViewModel = viewModel(factory = ViewModelFactory(HeroesRepository()))
) {
    Box(modifier = modifier) {
        val groupHeroes by viewModel.groupedHeroes.collectAsState()
        val scope = rememberCoroutineScope()
        val listState = rememberLazyListState()
        val showButton: Boolean by remember {
            derivedStateOf { listState.firstVisibleItemIndex > 0 }
        }
        val query by viewModel.query
        LazyColumn(state = listState, contentPadding = PaddingValues(bottom = 80.dp)) {
            item {
                SearchBarHeroes(
                    query = query,
                    modifier = Modifier.background(MaterialTheme.colorScheme.primary)
                ) {
                    viewModel.search(it)
                }
            }
            groupHeroes.forEach { (c, heroes) ->
                stickyHeader {
                    StickyHeader(char = c)
                }
                items(heroes, key = { it.id }) {
                    HeroItemList(
                        name = it.name,
                        photoUrl = it.photoUrl,
                        modifier = Modifier.fillMaxWidth().animateItemPlacement(tween(100))
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = showButton,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically(),
            modifier = Modifier
                .padding(bottom = 30.dp)
                .align(Alignment.BottomCenter)
        ) {
            ScrollToTop {
                scope.launch {
                    listState.animateScrollToItem(0)
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun JetHeroesAppPreview() {
    JetHeroesTheme {
        JetHeroesApp()
    }
}

@Composable
fun HeroItemList(
    name: String,
    photoUrl: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.clickable { },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = photoUrl,
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(8.dp)
                .size(60.dp)
                .clip(CircleShape)
        )
        Text(
            text = name,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
                .padding(start = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HeroItemListPreview() {
    JetHeroesTheme {
        HeroItemList(name = "H.O.S. Cokroaminoto", photoUrl = "")
    }
}

@Composable
fun ScrollToTop(modifier: Modifier = Modifier, onClick: () -> Unit) {
    FilledIconButton(onClick = { onClick.invoke() }, modifier = modifier) {
        Icon(imageVector = Icons.Filled.KeyboardArrowUp, contentDescription = "")
    }
}

@Composable
fun StickyHeader(modifier: Modifier = Modifier, char: Char) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    ) {
        Text(
            text = char.toString(),
            fontWeight = FontWeight.Black,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarHeroes(modifier: Modifier = Modifier, query: String, onQueryChange: (String) -> Unit) {
    SearchBar(
        query = query,
        onQueryChange = onQueryChange,
        onSearch = {},
        active = false,
        onActiveChange = {},
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        placeholder = {
            Text(text = stringResource(id = R.string.search_hero))
        },
        shape = MaterialTheme.shapes.large,
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .heightIn(48.dp)
    ) {

    }
}