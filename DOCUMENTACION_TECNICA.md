# 📚 Documentación Técnica - Gestor de Tareas Gamificado

## 🔧 Arquitectura del Proyecto

### Estructura de Archivos
```
app/src/main/java/com/example/ejercicio2/
├── MainActivity.kt                 # Actividad principal con Compose Navigation
├── ImageZoomActivity.kt           # Actividad de galería con zoom
├── ZoomableImageView.kt          # ImageView personalizado con gestos
├── data/
│   ├── Models.kt                 # Modelos de datos (Task, UserProfile, etc.)
│   └── TaskManagerViewModel.kt   # ViewModel con lógica de negocio
├── screens/                      # Pantallas Compose
│   ├── DashboardScreen.kt
│   ├── AddTaskScreen.kt
│   ├── TaskListScreen.kt
│   ├── ProfileScreen.kt
│   └── ReportsScreen.kt
└── ui/theme/                     # Tema y colores personalizados
    ├── Color.kt
    ├── Theme.kt
    └── Type.kt
```

## 📱 Respuestas a Preguntas Técnicas

### 1. Comunicación entre Fragmentos - Mejores Prácticas

#### 🎯 **ViewModel Compartido (Recomendado)**
```kotlin
// En Activity/Fragment padre
class SharedViewModel : ViewModel() {
    private val _selectedData = MutableLiveData<String>()
    val selectedData: LiveData<String> = _selectedData
    
    fun updateSelection(data: String) {
        _selectedData.value = data
    }
}

// En Fragment A (envía datos)
viewModel.updateSelection("nuevo dato")

// En Fragment B (recibe datos)
viewModel.selectedData.observe(this) { data ->
    // Actualizar UI
}
```

#### 🔄 **Interface Pattern**
```kotlin
interface FragmentCommunicationListener {
    fun onDataChanged(data: String)
}

class FragmentA : Fragment() {
    private lateinit var listener: FragmentCommunicationListener
    
    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as FragmentCommunicationListener
    }
}
```

#### ⚡ **Beneficios de Eficiencia**
- **Memoria**: ViewModel sobrevive a rotaciones de pantalla
- **Rendimiento**: Evita recreación innecesaria de objetos
- **Mantenibilidad**: Separación clara de responsabilidades

### 2. Optimización de Navigation Drawer

#### 🎨 **Implementación Optimizada**
```kotlin
class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Configuración lazy loading
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dashboard -> loadFragment(DashboardFragment(), false)
                R.id.nav_gallery -> loadFragment(GalleryFragment(), true)
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }
    
    private fun loadFragment(fragment: Fragment, addToBackStack: Boolean) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.slide_in_right,
            R.anim.slide_out_left,
            R.anim.slide_in_left,
            R.anim.slide_out_right
        )
        transaction.replace(R.id.fragment_container, fragment)
        if (addToBackStack) transaction.addToBackStack(null)
        transaction.commit()
    }
}
```

#### ♿ **Accesibilidad y UX**
```xml
<com.google.android.material.navigation.NavigationView
    android:layout_width="wrap_content"
    android:layout_height="match_parent"
    android:layout_gravity="start"
    android:contentDescription="@string/navigation_menu_description"
    app:headerLayout="@layout/nav_header"
    app:menu="@menu/drawer_menu"
    app:itemIconTint="@color/nav_item_color_selector"
    app:itemTextColor="@color/nav_item_color_selector" />
```

### 3. Personalización de App Bar

#### 🎨 **Toolbar Personalizado**
```kotlin
class CustomActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val toolbar = findViewById<Toolbar>(R.id.custom_toolbar)
        setSupportActionBar(toolbar)
        
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false) // Usar título personalizado
        }
        
        // Título personalizado con animación
        val titleView = findViewById<TextView>(R.id.toolbar_title)
        titleView.alpha = 0f
        titleView.animate().alpha(1f).duration = 500
    }
}
```

#### 📱 **Compatibilidad Multi-dispositivo**
```xml
<!-- values/styles.xml -->
<style name="CustomAppTheme" parent="Theme.Material3.DayNight.NoActionBar">
    <item name="colorPrimary">@color/primary_color</item>
    <item name="colorPrimaryVariant">@color/primary_variant</item>
    <item name="android:statusBarColor">@color/status_bar_color</item>
</style>

<!-- values-v21/styles.xml -->
<style name="CustomAppTheme" parent="Theme.Material3.DayNight.NoActionBar">
    <item name="android:statusBarColor">@android:color/transparent</item>
    <item name="android:windowDrawsSystemBarBackgrounds">true</item>
</style>
```

## 🚀 Implementación en el Proyecto Actual

### Aplicación en Gestor de Tareas

El proyecto actual utiliza **Jetpack Compose** con navegación moderna, pero los principios se aplican:

1. **ViewModel Compartido**: `TaskManagerViewModel` maneja estado global
2. **Navegación Consistente**: `NavController` con bottom navigation
3. **Experiencia Fluida**: Transiciones suaves entre pantallas

### Evolución Futura: Navigation Drawer

```kotlin
// Propuesta para migración híbrida
@Composable
fun HybridNavigationApp() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                items = listOf(
                    DrawerItem("Dashboard", Icons.Default.Dashboard),
                    DrawerItem("Galería", Icons.Default.Photo),
                    DrawerItem("Configuración", Icons.Default.Settings)
                )
            )
        }
    ) {
        // Contenido principal con Compose Navigation existente
        TaskManagerApp()
    }
}
```

## 📊 Métricas de Rendimiento

### Optimizaciones Implementadas

1. **Carga Lazy**: Imágenes se cargan solo cuando se necesitan
2. **Gestión de Memoria**: Matrix reutilizable en ZoomableImageView
3. **Smooth Animations**: 60 FPS en gestos de zoom
4. **Resource Efficiency**: Vector drawables escalables

### Mejoras Futuras

- **Fragment Caching** para navegación instantánea
- **Image Compression** para fotos capturadas
- **Background Processing** para operaciones pesadas
- **Database Optimization** con Room para persistencia

---

*Esta documentación refleja las mejores prácticas de desarrollo Android moderno, enfocándose en experiencia de usuario, rendimiento y mantenibilidad del código.*