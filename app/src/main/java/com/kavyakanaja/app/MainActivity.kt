package com.kavyakanaja.app

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.json.JSONArray
import java.time.LocalDate
import java.util.Locale

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)
        setContent {
            KavyaKanajaTheme {
                val poems = remember { loadPoems() }
                KavyaKanajaApp(
                    poems = poems,
                    onPlayAudio = { text -> recitePoem(text) }
                )
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.setLanguage(Locale("kn", "IN"))
        }
    }

    private fun recitePoem(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "PoemRecitation")
        Toast.makeText(this, "Reciting in Kannada...", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }

    private fun loadPoems(): List<Poem> {
        return try {
            val json = resources.openRawResource(R.raw.poetry_database).bufferedReader().use { it.readText() }
            val array = JSONArray(json)
            List(array.length()) { index ->
                val item = array.getJSONObject(index)
                Poem(
                    id = item.getString("id"),
                    title = item.getString("title"),
                    poet = item.getString("poet"),
                    verse = item.getString("verse"),
                    bhavartha = item.getString("bhavartha"),
                    audioUrl = item.optString("audio_url", ""),
                    era = item.getString("era"),
                    poetBio = item.getString("poet_bio"),
                    jnanpith = item.optBoolean("jnanpith", false),
                    hardWord = item.optString("hard_word", ""),
                    wordMeaning = item.optString("word_meaning", "")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}

data class Poem(
    val id: String, val title: String, val poet: String, val verse: String,
    val bhavartha: String, val audioUrl: String,
    val era: String, val poetBio: String, val jnanpith: Boolean,
    val hardWord: String, val wordMeaning: String
)

enum class AppTab(val label: String, val icon: ImageVector) {
    Home("Verse", Icons.Outlined.Home),
    Discovery("Poets", Icons.Outlined.AccountCircle),
    Archive("Archive", Icons.AutoMirrored.Outlined.List)
}

@Composable
fun KavyaKanajaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF1A237E), secondary = Color(0xFF5D4037),
            background = Color(0xFFFDFBF7), surface = Color(0xFFFFFFFF)
        ),
        typography = Typography(),
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KavyaKanajaApp(poems: List<Poem>, onPlayAudio: (String) -> Unit) {
    var currentTab by remember { mutableStateOf(AppTab.Home) }
    var poetFilter by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Kavya-Kanaja", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                navigationIcon = { 
                    // Remove menu icon from the first screen (Home)
                    if (currentTab != AppTab.Home) {
                        IconButton(onClick = {}) { Icon(Icons.Default.Menu, "Menu", tint = MaterialTheme.colorScheme.primary) } 
                    }
                },
                actions = { 
                    // Remove search icon from the first screen (Home)
                    if (currentTab != AppTab.Home) {
                        IconButton(onClick = {}) { Icon(Icons.Default.Search, "Search", tint = MaterialTheme.colorScheme.primary) } 
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFFFDFBF7))
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                AppTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = currentTab == tab,
                        onClick = { currentTab = tab; if (tab != AppTab.Archive) poetFilter = null },
                        icon = { Icon(tab.icon, tab.label) },
                        label = { Text(tab.label, fontSize = 12.sp) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            when (currentTab) {
                AppTab.Home -> HomeScreen(poems, innerPadding, onPlayAudio)
                AppTab.Discovery -> DiscoveryScreen(poems, innerPadding) { poet -> poetFilter = poet; currentTab = AppTab.Archive }
                AppTab.Archive -> ArchiveScreen(poems, poetFilter, innerPadding)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(poems: List<Poem>, padding: PaddingValues, onPlayAudio: (String) -> Unit) {
    if (poems.isEmpty()) return
    val dailyPoem = remember(poems) { poems[LocalDate.now().dayOfYear % poems.size] }
    var showMeaning by remember { mutableStateOf(false) }
    var showWordPopup by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            modifier = Modifier.fillMaxWidth().height(520.dp),
            shape = RoundedCornerShape(24.dp), elevation = CardDefaults.cardElevation(4.dp),
            border = BorderStroke(1.dp, Color(0xFFE8E4D8))
        ) {
            Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFFB2DFDB), Color(0xFFFDFBF7)))).padding(24.dp)) {
                Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("ದಿನದ ಕವಿತೆ (VERSE OF THE DAY)", fontSize = 11.sp, color = Color.Gray, letterSpacing = 1.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))
                    Text(dailyPoem.title, style = MaterialTheme.typography.headlineMedium.copy(textDecoration = TextDecoration.Underline), fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.primary)
                    Text(dailyPoem.poet, style = MaterialTheme.typography.bodyMedium, fontFamily = FontFamily.Serif, color = Color.DarkGray)
                    Spacer(Modifier.weight(1f))
                    
                    val annotatedVerse = buildAnnotatedString {
                        val parts = dailyPoem.verse.split(dailyPoem.hardWord)
                        if (parts.size > 1) {
                            append(parts[0])
                            pushStringAnnotation("HARD_WORD", dailyPoem.hardWord)
                            withStyle(SpanStyle(textDecoration = TextDecoration.Underline, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)) {
                                append(dailyPoem.hardWord)
                            }
                            pop()
                            append(parts[1])
                        } else {
                            append(dailyPoem.verse)
                        }
                    }

                    ClickableText(
                        text = annotatedVerse,
                        style = TextStyle(fontSize = 24.sp, lineHeight = 38.sp, textAlign = TextAlign.Center, fontFamily = FontFamily.Serif, color = Color(0xFF212121)),
                        onClick = { offset ->
                            annotatedVerse.getStringAnnotations("HARD_WORD", offset, offset).firstOrNull()?.let { showWordPopup = true }
                        }
                    )
                    Spacer(Modifier.weight(1f))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                        OutlinedButton(onClick = { showMeaning = true }, shape = RoundedCornerShape(12.dp)) { Text("ಭಾವಾರ್ಥ (MEANING)", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                        IconButton(onClick = { onPlayAudio(dailyPoem.verse) }, modifier = Modifier.size(56.dp).background(MaterialTheme.colorScheme.primary, CircleShape)) {
                            Icon(Icons.Default.PlayArrow, "Play", tint = Color.White, modifier = Modifier.size(32.dp))
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            items(listOf("ಸೌಂದರ್ಯ", "ಜೀವನ ದರ್ಶನ", "ನೀತಿ", "ಪ್ರಕೃತಿ")) { cat ->
                Surface(color = Color(0xFFFFEBEE), shape = RoundedCornerShape(16.dp)) { Text(cat, modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp), fontSize = 12.sp, color = Color(0xFF8D6663)) }
            }
        }
    }

    if (showWordPopup) {
        Dialog(onDismissRequest = { showWordPopup = false }) {
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))) {
                Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Word Meaning", fontWeight = FontWeight.Bold, color = Color.Blue)
                    Spacer(Modifier.height(8.dp))
                    Text("'${dailyPoem.hardWord}'", style = MaterialTheme.typography.headlineSmall, fontFamily = FontFamily.Serif)
                    Spacer(Modifier.height(8.dp))
                    Text(dailyPoem.wordMeaning, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(16.dp))
                    TextButton(onClick = { showWordPopup = false }) { Text("Got it!") }
                }
            }
        }
    }

    if (showMeaning) {
        ModalBottomSheet(onDismissRequest = { showMeaning = false }) {
            Column(Modifier.padding(24.dp).padding(bottom = 32.dp)) {
                Text("ಭಾವಾರ್ಥ (Philosophy)", style = MaterialTheme.typography.headlineSmall, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Text(dailyPoem.bhavartha, lineHeight = 28.sp, fontFamily = FontFamily.Serif, fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun DiscoveryScreen(poems: List<Poem>, padding: PaddingValues, onOpenArchive: (String) -> Unit) {
    val poets = remember(poems) { 
        val grouped = poems.groupBy { it.poet }.map { it.value.first() }
        val orderedNames = listOf("Kuvempu", "D. R. Bendre", "Akka Mahadevi", "Basavanna", "Kanakadasa", "Adikavi Pampa")
        orderedNames.mapNotNull { name -> grouped.find { it.poet == name } }.take(6)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Poet's Corner", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif, color = MaterialTheme.colorScheme.primary)
        Text("Select a poet to view their works", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Spacer(Modifier.height(16.dp))
        
        Column(modifier = Modifier.fillMaxSize()) {
            for (i in 0 until 3) {
                Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    for (j in 0 until 2) {
                        val index = i * 2 + j
                        if (index < poets.size) {
                            Box(modifier = Modifier.weight(1f).padding(4.dp)) {
                                CompactPoetCard(poets[index], onOpenArchive)
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CompactPoetCard(poet: Poem, onOpenArchive: (String) -> Unit) {
    val imageRes = when (poet.poet) {
        "Kuvempu" -> R.drawable.kuvempu
        "D. R. Bendre" -> R.drawable.bendre
        "Akka Mahadevi" -> R.drawable.akkamahadevi
        "Basavanna" -> R.drawable.basava
        "Kanakadasa" -> R.drawable.kanakadasa_art
        "Adikavi Pampa" -> R.drawable.pampa
        else -> R.drawable.kuvempu
    }

    Card(
        modifier = Modifier.fillMaxSize().clickable { onOpenArchive(poet.poet) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF9F6)),
        border = BorderStroke(1.dp, Color(0xFFE8E4D8))
    ) {
        Column(modifier = Modifier.padding(8.dp).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier.size(80.dp).clip(CircleShape).background(Color.LightGray),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(4.dp))
            Text(poet.poet, style = MaterialTheme.typography.titleSmall, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.primary)
            Text(poet.era, style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontSize = 9.sp)
        }
    }
}

@Composable
fun ArchiveScreen(poems: List<Poem>, poetFilter: String?, padding: PaddingValues) {
    var query by remember { mutableStateOf("") }
    val filtered = poems.filter { (poetFilter == null || it.poet == poetFilter) && (it.title.contains(query, true) || it.poet.contains(query, true) || it.verse.contains(query, true)) }
    Column(Modifier.fillMaxSize().padding(top = padding.calculateTopPadding()).padding(horizontal = 20.dp)) {
        Text(poetFilter ?: "Archive", modifier = Modifier.padding(top = 20.dp), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
        OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth().padding(top = 12.dp), placeholder = { Text("Search poems...") }, shape = RoundedCornerShape(12.dp))
        LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 160.dp), modifier = Modifier.weight(1f).padding(top = 16.dp), contentPadding = PaddingValues(bottom = 80.dp), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp) ) {
            items(filtered) { poem ->
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(Modifier.padding(14.dp)) { Text(poem.title, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                              Text(poem.poet, color = Color.Gray, fontSize = 12.sp) }
                }
            }
        }
    }
}
