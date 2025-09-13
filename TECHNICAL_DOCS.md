# 📚 Documentación Técnica - GAMIFICACIÓN

## 🏗️ Arquitectura Detallada

### 📊 Diagrama de Componentes

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                       │
├─────────────────────────────────────────────────────────────┤
│  MainActivity.kt (Navigation Host)                          │
│  ├── DashboardScreen.kt     ├── TaskListScreen.kt          │
│  ├── AddTaskScreen.kt       ├── ProfileScreen.kt           │
│  └── ReportsScreen.kt       └── ImageZoomActivity.kt       │
├─────────────────────────────────────────────────────────────┤
│                    BUSINESS LOGIC LAYER                     │
├─────────────────────────────────────────────────────────────┤
│  TaskManagerViewModel.kt (State Management)                 │
│  ├── Task Management        ├── XP Calculation             │
│  ├── User Profile          ├── Statistics                  │
│  └── Gamification Logic    └── Data Persistence            │
├─────────────────────────────────────────────────────────────┤
│                     DATA LAYER                              │
├─────────────────────────────────────────────────────────────┤
│  Models.kt (Data Classes)                                   │
│  ├── Task                  ├── UserProfile                 │
│  ├── TaskCategory          ├── Badge                       │
│  └── TaskStatus            └── enums                       │
└─────────────────────────────────────────────────────────────┘
```

### 🔄 Flujo de Datos

```
User Interaction → Compose UI → ViewModel → State Update → UI Recomposition
                              ↓
                    Local Storage (Memory) ← Data Models
```

## 🧩 Componentes Principales

### 📱 MainActivity.kt
```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskManagerApp() {
    // Navigation Controller para manejar navegación entre pantallas
    val navController = rememberNavController()
    
    // ViewModel compartido entre todas las pantallas
    val viewModel: TaskManagerViewModel = viewModel()
    
    // Scaffold con BottomNavigation y FloatingActionButton
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        floatingActionButton = { /* FAB para galería zoom */ }
    ) { /* Navigation Host */ }
}
```

**Responsabilidades:**
- Configuración del tema Material 3
- Navegación entre pantallas
- Integración con sistema de Activity
- FloatingActionButton para galería zoom

### 🧠 TaskManagerViewModel.kt
```kotlin
class TaskManagerViewModel : ViewModel() {
    // Estados observables
    private val _userProfile = mutableStateOf(UserProfile())
    val userProfile: State<UserProfile> = _userProfile
    
    private val _tasks = mutableStateListOf<Task>()
    val tasks: List<Task> = _tasks
    
    // Funciones principales
    fun addTask(task: Task) { /* Lógica agregar tarea */ }
    fun completeTask(taskId: String) { /* Completar + XP */ }
    fun calculateLevel(xp: Int): Int { /* Cálculo nivel */ }
    fun updateStreak() { /* Actualizar racha */ }
}
```

**Responsabilidades:**
- Gestión de estado global de la aplicación
- Lógica de negocio para tareas y gamificación
- Cálculos de XP, niveles y rachas
- Persistencia en memoria (expansible a DB)

### 🎨 Screens/ - Pantallas Compose

#### 🏠 DashboardScreen.kt
```kotlin
@Composable
fun DashboardScreen(
    viewModel: TaskManagerViewModel,
    onNavigateToTasks: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAddTask: () -> Unit
) {
    Column {
        UserStatsCard(userProfile = viewModel.userProfile.value)
        CategoriesGrid(onCategoryClick = { /* Navegar a tareas filtradas */ })
        RecentTasksList(tasks = viewModel.tasks)
    }
}
```

**Componentes:**
- `UserStatsCard`: XP, nivel, racha con animaciones
- `CategoriesGrid`: Grid 2x3 con categorías coloridas
- `RecentTasksList`: Lista de tareas recientes/próximas

#### ✅ AddTaskScreen.kt
```kotlin
@Composable
fun AddTaskScreen(
    viewModel: TaskManagerViewModel,
    onNavigateBack: () -> Unit
) {
    Column {
        TopAppBar(title = "Nueva Tarea", navigationIcon = BackButton)
        
        // Formulario
        OutlinedTextField(value = title, onValueChange = { title = it })
        OutlinedTextField(value = description, onValueChange = { description = it })
        
        // Dropdown para categoría
        CategoryDropdown(selectedCategory = category)
        
        // Slider para prioridad
        PrioritySlider(priority = priority)
        
        // Date picker para fecha límite
        DatePickerField(date = dueDate)
        
        // Botón guardar
        Button(onClick = { viewModel.addTask(newTask) }) {
            Text("Guardar Tarea")
        }
    }
}
```

### 📊 Data/Models.kt
```kotlin
data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val category: TaskCategory,
    val priority: TaskPriority,
    val status: TaskStatus = TaskStatus.PENDING,
    val dueDate: LocalDate?,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val completedAt: LocalDateTime? = null
)

data class UserProfile(
    val name: String = "Estudiante",
    val xp: Int = 0,
    val level: Int = 1,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val tasksCompleted: Int = 0,
    val badges: List<Badge> = emptyList()
)

enum class TaskCategory(val displayName: String, val color: Color, val icon: ImageVector) {
    STUDY("Estudios", StudyBlue, Icons.Default.MenuBook),
    EXERCISE("Ejercicio", ExerciseGreen, Icons.Default.FitnessCenter),
    FOOD("Comida/Salud", FoodOrange, Icons.Default.Restaurant),
    WORK("Trabajo", WorkPurple, Icons.Default.Work),
    ENTERTAINMENT("Entretenimiento", FunPink, Icons.Default.Whatshot),
    OTHER("Otros", Color.Gray, Icons.Default.Category)
}
```

## 🎮 Sistema de Gamificación

### 📈 Cálculo de XP y Niveles
```kotlin
companion object {
    const val XP_PER_TASK = 50
    const val BASE_XP_FOR_LEVEL = 100
    
    fun calculateLevel(xp: Int): Int {
        return (xp / BASE_XP_FOR_LEVEL) + 1
    }
    
    fun xpForNextLevel(currentXp: Int): Int {
        val currentLevel = calculateLevel(currentXp)
        return (currentLevel * BASE_XP_FOR_LEVEL) - currentXp
    }
    
    fun progressToNextLevel(currentXp: Int): Float {
        val currentLevel = calculateLevel(currentXp) - 1
        val xpInCurrentLevel = currentXp - (currentLevel * BASE_XP_FOR_LEVEL)
        return xpInCurrentLevel.toFloat() / BASE_XP_FOR_LEVEL
    }
}
```

### 🏆 Sistema de Badges
```kotlin
data class Badge(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val unlockedAt: LocalDateTime? = null
) {
    val isUnlocked: Boolean get() = unlockedAt != null
}

// Badges predefinidos
val FIRST_TASK_BADGE = Badge(
    id = "first_task",
    name = "Primer Paso",
    description = "Completa tu primera tarea",
    icon = Icons.Default.EmojiEvents
)

val WEEK_STREAK_BADGE = Badge(
    id = "week_streak", 
    name = "Constancia",
    description = "Mantén una racha de 7 días",
    icon = Icons.Default.Whatshot
)
```

## 🖼️ Implementación de Zoom

### 🔍 ZoomableImageView.kt
```kotlin
class ZoomableImageView @JvmOverloads constructor(
    context: Context, 
    attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {
    
    private val matrix = Matrix()
    private var scaleFactor = 1f
    private val minScale = 1f
    private val maxScale = 4f
    
    private val scaleDetector = ScaleGestureDetector(context, ScaleListener())
    private val gestureDetector = GestureDetector(context, GestureListener())
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)
        
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> { /* Iniciar drag */ }
            MotionEvent.ACTION_MOVE -> { /* Procesar drag */ }
            MotionEvent.ACTION_UP -> { /* Finalizar gesture */ }
        }
        
        imageMatrix = matrix
        return true
    }
    
    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val factor = detector.scaleFactor
            val newScale = scaleFactor * factor
            val clamped = max(minScale, min(newScale, maxScale))
            // Aplicar escala con límites
            matrix.postScale(scaleChange, scaleChange, detector.focusX, detector.focusY)
            return true
        }
    }
    
    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent): Boolean {
            // Toggle entre zoom normal y 2x
            val target = if (scaleFactor > minScale) minScale else 2f
            val scaleChange = target / scaleFactor
            scaleFactor = target
            matrix.postScale(scaleChange, scaleChange, e.x, e.y)
            imageMatrix = matrix
            return true
        }
    }
}
```

### 🎭 ImageZoomActivity.kt
```kotlin
class ImageZoomActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_zoom)
        
        // Cargar imágenes dinámicamente
        val imageNames = listOf("img_task1", "img_task2", "img_task3")
        val viewIds = listOf(R.id.zoomImage1, R.id.zoomImage2, R.id.zoomImage3)
        
        imageNames.forEachIndexed { index, name ->
            val resId = resources.getIdentifier(name, "drawable", packageName)
            val imageView = findViewById<ZoomableImageView>(viewIds[index])
            
            if (resId != 0) {
                imageView.setImageDrawable(ContextCompat.getDrawable(this, resId))
            } else {
                // Fallback a ícono del sistema
                imageView.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        }
        
        Toast.makeText(this, "Pellizca, arrastra y haz doble tap para zoom", Toast.LENGTH_LONG).show()
    }
}
```

## 🎨 Sistema de Temas

### 🌈 Color.kt
```kotlin
// Colores principales
val PrimaryBlue = Color(0xFF1976D2)
val PrimaryBlueVariant = Color(0xFF1565C0)
val AccentOrange = Color(0xFFFF9800)

// Colores de estado
val SuccessGreen = Color(0xFF4CAF50)
val WarningAmber = Color(0xFFFFC107)
val ErrorRed = Color(0xFFF44336)

// Colores por categoría
val StudyBlue = Color(0xFF2196F3)
val ExerciseGreen = Color(0xFF4CAF50)
val FoodOrange = Color(0xFFFF9800)
val WorkPurple = Color(0xFF9C27B0)
val FunPink = Color(0xFFE91E63)

// Colores de superficie
val SurfaceLight = Color(0xFFF8F9FA)
val SurfaceDark = Color(0xFF121212)
val TextPrimary = Color(0xFF212121)
val TextSecondary = Color(0xFF757575)
```

### 🎭 Theme.kt
```kotlin
@Composable
fun Ejercicio2Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> darkColorScheme(
            primary = PrimaryBlue,
            secondary = AccentOrange,
            tertiary = SuccessGreen
        )
        else -> lightColorScheme(
            primary = PrimaryBlue,
            secondary = AccentOrange,
            tertiary = SuccessGreen
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

## 🚀 Performance y Optimización

### 🏃‍♂️ Compose Performance
```kotlin
// Usar remember para evitar recomposiciones innecesarias
@Composable
fun TaskItem(task: Task, onTaskClick: (Task) -> Unit) {
    val backgroundColor by remember(task.category) { 
        mutableStateOf(task.category.color.copy(alpha = 0.1f))
    }
    
    // Stable parameters para evitar recomposiciones
    val onClickStable = remember { onTaskClick }
    
    Card(
        modifier = Modifier.clickable { onClickStable(task) },
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        // Contenido de la tarjeta
    }
}

// LazyColumn con keys para performance
@Composable
fun TasksList(tasks: List<Task>) {
    LazyColumn {
        items(tasks, key = { it.id }) { task ->
            TaskItem(task = task, onTaskClick = { /* Handle click */ })
        }
    }
}
```

### 💾 Memory Management
```kotlin
// ViewModel con limpieza automática
class TaskManagerViewModel : ViewModel() {
    private val _tasks = mutableStateListOf<Task>()
    
    // Cleanup cuando ViewModel se destruye
    override fun onCleared() {
        super.onCleared()
        _tasks.clear()
    }
    
    // Limitar número de tareas en memoria
    fun addTask(task: Task) {
        _tasks.add(0, task) // Agregar al inicio
        if (_tasks.size > MAX_TASKS_IN_MEMORY) {
            _tasks.removeAt(_tasks.lastIndex) // Remover la más antigua
        }
    }
    
    companion object {
        private const val MAX_TASKS_IN_MEMORY = 100
    }
}
```

## 🔧 Configuración de Build

### 📦 build.gradle.kts (App Level)
```kotlin
android {
    namespace = "com.example.ejercicio2"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.ejercicio2"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
}
```

### 🔒 ProGuard Rules
```proguard
# Mantener clases de datos
-keep class com.example.ejercicio2.data.** { *; }

# Reglas para Compose
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }

# Reglas para ViewModel
-keep class * extends androidx.lifecycle.ViewModel { *; }

# Reglas para Kotlin coroutines
-dontwarn kotlinx.coroutines.**
```

## 🧪 Testing Strategy

### ✅ Unit Tests
```kotlin
class TaskManagerViewModelTest {
    
    @Test
    fun `addTask should increase tasks count`() {
        val viewModel = TaskManagerViewModel()
        val initialCount = viewModel.tasks.size
        
        val task = Task(
            title = "Test Task",
            description = "Test Description",
            category = TaskCategory.STUDY
        )
        
        viewModel.addTask(task)
        
        assertEquals(initialCount + 1, viewModel.tasks.size)
        assertEquals(task, viewModel.tasks.first())
    }
    
    @Test
    fun `completeTask should award XP`() {
        val viewModel = TaskManagerViewModel()
        val task = Task(title = "Test", description = "", category = TaskCategory.STUDY)
        
        viewModel.addTask(task)
        val initialXP = viewModel.userProfile.value.xp
        
        viewModel.completeTask(task.id)
        
        assertEquals(initialXP + TaskManagerViewModel.XP_PER_TASK, viewModel.userProfile.value.xp)
    }
}
```

### 🎯 Compose UI Tests
```kotlin
@RunWith(AndroidJUnit4::class)
class DashboardScreenTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun dashboardShowsUserStats() {
        val mockViewModel = TaskManagerViewModel().apply {
            // Setup mock data
        }
        
        composeTestRule.setContent {
            Ejercicio2Theme {
                DashboardScreen(
                    viewModel = mockViewModel,
                    onNavigateToTasks = {},
                    onNavigateToProfile = {},
                    onNavigateToAddTask = {}
                )
            }
        }
        
        composeTestRule.onNodeWithText("Nivel 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("0 XP").assertIsDisplayed()
    }
}
```

## 🔐 Seguridad y Privacidad

### 🛡️ Datos Locales
```kotlin
// Actualmente la app usa almacenamiento en memoria
// Para persistencia segura, considerar:

// 1. Room Database con encriptación
@Database(
    entities = [TaskEntity::class, UserProfileEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}

// 2. SharedPreferences encriptadas
val encryptedPrefs = EncryptedSharedPreferences.create(
    "secure_prefs",
    MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
    context,
    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
)
```

### 🔒 Buenas Prácticas
- No almacenar datos sensibles en logs
- Validar inputs del usuario
- Usar ProGuard para ofuscar código en release
- Implementar backup/restore seguro
- Considerar autenticación biométrica para datos sensibles

## 📈 Métricas y Analytics

### 📊 KPIs Sugeridos
```kotlin
// Métricas de engagement
data class AppMetrics(
    val dailyActiveUsers: Int,
    val averageSessionDuration: Duration,
    val tasksCompletedPerDay: Float,
    val userRetentionRate: Float,
    val averageUserLevel: Float
)

// Eventos de gamificación
sealed class GamificationEvent {
    object TaskCompleted : GamificationEvent()
    object LevelUp : GamificationEvent()
    object StreakAchieved : GamificationEvent()
    object BadgeUnlocked : GamificationEvent()
}
```

---

**📝 Nota**: Esta documentación técnica debe mantenerse actualizada con cada release y nuevas funcionalidades implementadas.
