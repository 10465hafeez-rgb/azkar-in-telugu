package com.example

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.AzkarData
import com.example.data.ChapterModel
import com.example.data.ZikrItem
import com.example.ui.theme.MyApplicationTheme

sealed interface Screen {
    object Dashboard : Screen
    data class AzkarList(val chapter: ChapterModel) : Screen
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(dynamicColor = false) {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Dashboard) }
                var textScale by remember { mutableFloatStateOf(1.0f) }

                BackHandler(enabled = currentScreen is Screen.AzkarList) {
                    currentScreen = Screen.Dashboard
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFF3F4F6)) // light grey background
                            .padding(innerPadding)
                    ) {
                        when (val screen = currentScreen) {
                            is Screen.Dashboard -> {
                                DashboardScreen(
                                    onChapterSelected = { chapter ->
                                        if (chapter.azkarList.isNotEmpty()) {
                                            currentScreen = Screen.AzkarList(chapter)
                                        } else {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "ఈ అధ్యాయంలో ఇంకా డేటా జోడించబడలేదు (Coming Soon)",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                )
                            }
                            is Screen.AzkarList -> {
                                AzkarListScreen(
                                    chapter = screen.chapter,
                                    textScale = textScale,
                                    onBack = { currentScreen = Screen.Dashboard },
                                    onUpdateTextScale = { textScale = it }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// DASHBOARD SCREEN
// ==========================================
@Composable
fun DashboardScreen(
    onChapterSelected: (ChapterModel) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // App Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                AsyncImage(
                    model = "https://azkarintelugu.epizy.com/popup.jpeg",
                    contentDescription = "Azkar Header Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = android.R.drawable.ic_menu_gallery),
                    placeholder = painterResource(id = android.R.drawable.ic_menu_gallery)
                )
                // Dark gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.1f),
                                    Color.Black.copy(alpha = 0.4f),
                                    Color.Black.copy(alpha = 0.85f)
                                )
                            )
                        )
                )
                // Content Overlay
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 30.dp),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Azkar In Telugu",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp,
                        style = TextStyle(
                            shadow = Shadow(
                                color = Color.Black,
                                offset = Offset(0f, 3f),
                                blurRadius = 10f
                            )
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = Color(0xFF064E3B).copy(alpha = 0.8f),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.5.dp, Color(0xFFFDE68A))
                    ) {
                        Text(
                            text = "ఖుర్ఆన్ మరియు హదీస్ వెలుగులో",
                            color = Color(0xFFFDE68A),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Quran and Hadith cards
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Quran Verse Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(2.dp, Color(0xFFDCFCE7)), // Green.50
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيم",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF064E3B)
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "يَٰٓأَيُّهَا ٱلَّذِينَ ءَامَنُوا۟ ٱذْكُرُوا۟ ٱللَّهَ ذِكْرًۭا كَثِيرًۭا وَسَبِّحُوهُ بُكْرَةًۭ وَأَصِيلًا",
                            textAlign = TextAlign.Center,
                            style = TextStyle(
                                fontSize = 22.sp,
                                lineHeight = 36.sp,
                                color = Color(0xFF064E3B),
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "ఓ విశ్వాసులారా! అల్లాహ్ను అత్యధికంగా స్మరించండి.\nఉదయం, సాయంకాలం ఆయన పవిత్రతను కొనియాడండి.\n[ఖుర్ఆన్ 33 : 41,42]",
                            textAlign = TextAlign.Center,
                            style = TextStyle(
                                fontSize = 15.sp,
                                lineHeight = 22.sp,
                                color = Color.Black.copy(alpha = 0.8f),
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                // Hadith Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)), // Light Amber
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(2.dp, Color(0xFFFDE68A)), // Amber line
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "عَنْ أَبِي هُرَيْرَةَ، أَنَّ رَسُولَ اللهِ ﷺ قَالَ:\nسَبَقَ الْمُفَرِّدُونَ، قَالُوا: وَمَا الْمُفَرِّدُونَ يَا رَسُولَ اللهِ؟ قَالَ: الذَّاكِرُونَ اللهَ كَثِيراً وَالذَّاكِرَاتُ\n(رواه مسلم)",
                            textAlign = TextAlign.Center,
                            style = TextStyle(
                                fontSize = 20.sp,
                                lineHeight = 32.sp,
                                color = Color(0xFF92400E),
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "అబూహురైరా (రజియల్లాహు అన్హు ) \"ఉల్లేఖించారు _ దైవప్రవక్త (సల్లల్లాహు అలైహి వ సల్లం ) వారు ఒకసారి \" ముఫర్రిదూన్\" ముందంజలో ఉన్నారు అని తెలిపారు, ఆమాట విని సహచరులు \" ' ముఫర్రిదూన్ ' అంటే ఎవరు దైవప్రవక్త (ﷺ) అని అడిగారు , అందుకు సమాధానమిస్తూ ,అల్లాహ్ ను అత్యధికంగా స్మరించే స్త్రీ పురుషులు అని చెప్పారు . ( సహీహ్ ముస్లిం)",
                            textAlign = TextAlign.Center,
                            style = TextStyle(
                                fontSize = 14.sp,
                                lineHeight = 21.sp,
                                color = Color.Black.copy(alpha = 0.8f),
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
        }

        // Chapters List Grid (using manual layout inside LazyColumn for seamless scrolling)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val chapters = AzkarData.appChapters
                for (i in chapters.indices step 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            ChapterCard(chapter = chapters[i], onClick = { onChapterSelected(chapters[i]) })
                        }
                        if (i + 1 < chapters.size) {
                            Box(modifier = Modifier.weight(1f)) {
                                ChapterCard(chapter = chapters[i + 1], onClick = { onChapterSelected(chapters[i + 1]) })
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

// ==========================================
// CHAPTER CARD COMPONENT
// ==========================================
@Composable
fun ChapterCard(
    chapter: ChapterModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.9f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.5.dp, chapter.color.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .background(chapter.color.copy(alpha = 0.15f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = chapter.icon,
                    contentDescription = null,
                    tint = chapter.color,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = chapter.titleArabic,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = chapter.color
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = chapter.titleTelugu,
                style = TextStyle(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black.copy(alpha = 0.75f)
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

// ==========================================
// AZKAR LIST SCREEN (With adjustable font size)
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AzkarListScreen(
    chapter: ChapterModel,
    textScale: Float,
    onBack: () -> Unit,
    onUpdateTextScale: (Float) -> Unit
) {
    var showFontDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = chapter.titleTelugu,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showFontDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.FormatSize,
                            contentDescription = "Adjust Text Size"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = chapter.color,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF3F4F6))
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header Arabic Supplication Category
                item {
                    Text(
                        text = chapter.titleArabic,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        style = TextStyle(
                            fontSize = (28 * textScale).sp,
                            fontWeight = FontWeight.Bold,
                            color = chapter.color,
                            textAlign = TextAlign.Center
                        )
                    )
                }

                // Supplication Cards
                items(chapter.azkarList) { item ->
                    ProZikrCard(
                        item = item,
                        textScale = textScale,
                        onCopy = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clipContent = buildString {
                                append(item.arabic).append("\n\n")
                                if (item.telugu.isNotEmpty()) {
                                    append(item.telugu).append("\n\n")
                                }
                                if (item.meaning.isNotEmpty()) {
                                    append(item.meaning).append("\n\n")
                                }
                                if (item.benefit.isNotEmpty()) {
                                    append(item.benefit)
                                }
                            }
                            val clip = ClipData.newPlainText("Zikr Supplication", clipContent)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        onShare = {
                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                type = "text/plain"
                                val shareContent = buildString {
                                    append(chapter.titleTelugu).append(" - ").append(chapter.titleArabic).append("\n\n")
                                    append(item.arabic).append("\n\n")
                                    if (item.telugu.isNotEmpty()) {
                                        append(item.telugu).append("\n\n")
                                    }
                                    if (item.meaning.isNotEmpty()) {
                                        append(item.meaning).append("\n\n")
                                    }
                                    if (item.benefit.isNotEmpty()) {
                                        append(item.benefit)
                                    }
                                }
                                putExtra(Intent.EXTRA_TEXT, shareContent)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Supplication via"))
                        }
                    )
                }
            }

            // Adjust Font Size Alert Dialog
            if (showFontDialog) {
                AlertDialog(
                    onDismissRequest = { showFontDialog = false },
                    title = {
                        Text(
                            text = "అక్షరాల పరిమాణం మార్చుకోండి\n(Adjust Text Size)",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF064E3B),
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    text = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("A-", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Slider(
                                    value = textScale,
                                    onValueChange = onUpdateTextScale,
                                    valueRange = 0.8f..2.0f,
                                    steps = 12,
                                    colors = SliderDefaults.colors(
                                        activeTrackColor = Color(0xFF064E3B),
                                        inactiveTrackColor = Color(0xFFDCFCE7),
                                        thumbColor = Color(0xFF064E3B)
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 8.dp)
                                )
                                Text("A+", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Size: ${(textScale * 100).toInt()}%",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showFontDialog = false }) {
                            Text(
                                text = "OK",
                                style = TextStyle(
                                    color = Color(0xFF064E3B),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                )
            }
        }
    }
}

// ==========================================
// ZIKR CARD COMPONENT
// ==========================================
@Composable
fun ProZikrCard(
    item: ZikrItem,
    textScale: Float,
    onCopy: () -> Unit,
    onShare: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Arabic Text Box (RTL layout)
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF0FDF4))
                        .border(1.dp, Color(0xFFD1FAE5), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = item.arabic,
                        style = TextStyle(
                            fontSize = (24 * textScale).sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = (38 * textScale).sp,
                            color = Color.Black.copy(alpha = 0.85f),
                            textAlign = TextAlign.Right
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Telugu Transliteration
            if (item.telugu.isNotEmpty()) {
                Text(
                    text = item.telugu,
                    style = TextStyle(
                        fontSize = (16 * textScale).sp,
                        lineHeight = (24 * textScale).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Divider and Translation/Meaning
            if (item.meaning.isNotEmpty()) {
                HorizontalDivider(color = Color.Black.copy(alpha = 0.08f))
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = item.meaning,
                    style = TextStyle(
                        fontSize = (15 * textScale).sp,
                        lineHeight = (22 * textScale).sp,
                        color = Color(0xFF4B5563)
                    )
                )
            }

            // Benefit yellow box
            if (item.benefit.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFEF3C7))
                        .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = item.benefit,
                        style = TextStyle(
                            fontSize = (14 * textScale).sp,
                            lineHeight = (20 * textScale).sp,
                            color = Color(0xFF92400E),
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Play Audio button (Placeholder, non-functional)
                OutlinedButton(
                    onClick = { /* Non-functional */ },
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, Color(0xFF064E3B).copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF064E3B)),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PlayCircle,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Play Audio", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // Copy and Share Actions
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onCopy,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CopyAll,
                            contentDescription = "Copy to clipboard",
                            tint = Color.Gray,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    IconButton(
                        onClick = onShare,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share supplication",
                            tint = Color.Gray,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}
