-- ============================================
-- Base de Datos: Gestor de Tareas Gamificado
-- Sistema: Android App con SQLite
-- Versión: 1.0
-- ============================================

-- ============================================
-- TABLA: users
-- Información del perfil del usuario
-- ============================================
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    uuid TEXT NOT NULL UNIQUE,
    name TEXT NOT NULL,
    email TEXT UNIQUE,
    avatar_url TEXT,
    current_xp INTEGER DEFAULT 0 CHECK(current_xp >= 0),
    level INTEGER DEFAULT 1 CHECK(level >= 1),
    current_streak INTEGER DEFAULT 0 CHECK(current_streak >= 0),
    longest_streak INTEGER DEFAULT 0 CHECK(longest_streak >= 0),
    tasks_completed INTEGER DEFAULT 0 CHECK(tasks_completed >= 0),
    total_xp_earned INTEGER DEFAULT 0 CHECK(total_xp_earned >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    preferences_json TEXT, -- JSON para configuraciones personalizadas
    is_active INTEGER DEFAULT 1 CHECK(is_active IN (0, 1))
);

-- Índices para users
CREATE INDEX IF NOT EXISTS idx_users_uuid ON users(uuid);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_level ON users(level);

-- ============================================
-- TABLA: tasks
-- Tareas del usuario con toda la información
-- ============================================
CREATE TABLE IF NOT EXISTS tasks (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    uuid TEXT NOT NULL UNIQUE,
    user_id INTEGER NOT NULL,
    title TEXT NOT NULL,
    description TEXT,
    category TEXT NOT NULL CHECK(category IN (
        'STUDY', 'MATHEMATICS', 'HISTORY', 'SCIENCE', 
        'EXERCISE', 'SOCIAL', 'WORK', 'PERSONAL'
    )),
    priority TEXT NOT NULL DEFAULT 'MEDIUM' CHECK(priority IN ('LOW', 'MEDIUM', 'HIGH')),
    status TEXT NOT NULL DEFAULT 'PENDING' CHECK(status IN (
        'PENDING', 'IN_PROGRESS', 'COMPLETED', 'OVERDUE'
    )),
    due_date DATE NOT NULL,
    xp_reward INTEGER DEFAULT 10 CHECK(xp_reward >= 0),
    image_proof_path TEXT,
    calendar_event_id INTEGER, -- ID del evento en calendario nativo
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    
    -- Relación con usuario
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Índices para tasks
CREATE INDEX IF NOT EXISTS idx_tasks_uuid ON tasks(uuid);
CREATE INDEX IF NOT EXISTS idx_tasks_user_id ON tasks(user_id);
CREATE INDEX IF NOT EXISTS idx_tasks_status ON tasks(status);
CREATE INDEX IF NOT EXISTS idx_tasks_due_date ON tasks(due_date);
CREATE INDEX IF NOT EXISTS idx_tasks_category ON tasks(category);
CREATE INDEX IF NOT EXISTS idx_tasks_priority ON tasks(priority);
CREATE INDEX IF NOT EXISTS idx_tasks_completed ON tasks(user_id, status, completed_at);
CREATE INDEX IF NOT EXISTS idx_tasks_search ON tasks(user_id, category, status);

-- ============================================
-- TABLA: badges
-- Logros/insignias desbloqueables
-- ============================================
CREATE TABLE IF NOT EXISTS badges (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    badge_key TEXT NOT NULL UNIQUE, -- FIRST_TASK, STREAK_3, etc.
    name TEXT NOT NULL,
    description TEXT NOT NULL,
    icon_name TEXT NOT NULL,
    requirement_type TEXT NOT NULL CHECK(requirement_type IN (
        'TASK_COUNT', 'STREAK', 'XP_MILESTONE', 'CATEGORY_MASTER', 'SPECIAL'
    )),
    requirement_value INTEGER NOT NULL,
    xp_bonus INTEGER DEFAULT 0 CHECK(xp_bonus >= 0),
    is_active INTEGER DEFAULT 1 CHECK(is_active IN (0, 1)),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para badges
CREATE INDEX IF NOT EXISTS idx_badges_key ON badges(badge_key);
CREATE INDEX IF NOT EXISTS idx_badges_type ON badges(requirement_type);

-- ============================================
-- TABLA: user_badges
-- Relación muchos-a-muchos entre usuarios y badges
-- ============================================
CREATE TABLE IF NOT EXISTS user_badges (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    badge_id INTEGER NOT NULL,
    unlocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    progress INTEGER DEFAULT 0, -- Progreso hacia el badge
    is_unlocked INTEGER DEFAULT 0 CHECK(is_unlocked IN (0, 1)),
    
    -- Relaciones
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (badge_id) REFERENCES badges(badge_id) ON DELETE CASCADE,
    
    -- Un usuario no puede tener el mismo badge duplicado
    UNIQUE(user_id, badge_id)
);

-- Índices para user_badges
CREATE INDEX IF NOT EXISTS idx_user_badges_user ON user_badges(user_id);
CREATE INDEX IF NOT EXISTS idx_user_badges_unlocked ON user_badges(user_id, is_unlocked);

-- ============================================
-- TABLA: study_sessions
-- Sesiones de estudio programadas
-- ============================================
CREATE TABLE IF NOT EXISTS study_sessions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    uuid TEXT NOT NULL UNIQUE,
    user_id INTEGER NOT NULL,
    subject TEXT NOT NULL,
    description TEXT,
    scheduled_date TIMESTAMP NOT NULL,
    duration_minutes INTEGER NOT NULL CHECK(duration_minutes > 0),
    calendar_event_id INTEGER, -- ID del evento en calendario nativo
    status TEXT NOT NULL DEFAULT 'SCHEDULED' CHECK(status IN (
        'SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'MISSED'
    )),
    xp_earned INTEGER DEFAULT 0 CHECK(xp_earned >= 0),
    actual_duration_minutes INTEGER,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    
    -- Relación con usuario
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Índices para study_sessions
CREATE INDEX IF NOT EXISTS idx_sessions_uuid ON study_sessions(uuid);
CREATE INDEX IF NOT EXISTS idx_sessions_user ON study_sessions(user_id);
CREATE INDEX IF NOT EXISTS idx_sessions_date ON study_sessions(scheduled_date);
CREATE INDEX IF NOT EXISTS idx_sessions_status ON study_sessions(status);

-- ============================================
-- TABLA: daily_stats
-- Estadísticas diarias del usuario
-- ============================================
CREATE TABLE IF NOT EXISTS daily_stats (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    stat_date DATE NOT NULL,
    tasks_completed INTEGER DEFAULT 0 CHECK(tasks_completed >= 0),
    xp_earned INTEGER DEFAULT 0 CHECK(xp_earned >= 0),
    study_minutes INTEGER DEFAULT 0 CHECK(study_minutes >= 0),
    streak_active INTEGER DEFAULT 0 CHECK(streak_active IN (0, 1)),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Relación con usuario
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    -- Una entrada por día por usuario
    UNIQUE(user_id, stat_date)
);

-- Índices para daily_stats
CREATE INDEX IF NOT EXISTS idx_daily_stats_user ON daily_stats(user_id);
CREATE INDEX IF NOT EXISTS idx_daily_stats_date ON daily_stats(user_id, stat_date DESC);

-- ============================================
-- TABLA: activity_log
-- Registro de actividades del usuario
-- ============================================
CREATE TABLE IF NOT EXISTS activity_log (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    activity_type TEXT NOT NULL CHECK(activity_type IN (
        'TASK_CREATED', 'TASK_COMPLETED', 'TASK_DELETED',
        'BADGE_UNLOCKED', 'LEVEL_UP', 'SESSION_COMPLETED',
        'STREAK_MILESTONE', 'XP_EARNED'
    )),
    entity_type TEXT, -- 'task', 'badge', 'session', etc.
    entity_id INTEGER,
    description TEXT NOT NULL,
    xp_change INTEGER DEFAULT 0,
    metadata_json TEXT, -- JSON para información adicional
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Relación con usuario
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Índices para activity_log
CREATE INDEX IF NOT EXISTS idx_activity_user ON activity_log(user_id);
CREATE INDEX IF NOT EXISTS idx_activity_type ON activity_log(activity_type);
CREATE INDEX IF NOT EXISTS idx_activity_date ON activity_log(user_id, created_at DESC);

-- ============================================
-- TABLA: app_settings
-- Configuración global de la aplicación
-- ============================================
CREATE TABLE IF NOT EXISTS app_settings (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    setting_key TEXT NOT NULL UNIQUE,
    setting_value TEXT NOT NULL,
    setting_type TEXT NOT NULL CHECK(setting_type IN ('STRING', 'INTEGER', 'BOOLEAN', 'JSON')),
    description TEXT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índice para app_settings
CREATE INDEX IF NOT EXISTS idx_settings_key ON app_settings(setting_key);

-- ============================================
-- TABLA: sync_queue
-- Cola de sincronización para backup/cloud
-- ============================================
CREATE TABLE IF NOT EXISTS sync_queue (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    entity_type TEXT NOT NULL,
    entity_id INTEGER NOT NULL,
    operation TEXT NOT NULL CHECK(operation IN ('CREATE', 'UPDATE', 'DELETE')),
    data_json TEXT NOT NULL,
    sync_status TEXT NOT NULL DEFAULT 'PENDING' CHECK(sync_status IN (
        'PENDING', 'IN_PROGRESS', 'COMPLETED', 'FAILED'
    )),
    retry_count INTEGER DEFAULT 0,
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    synced_at TIMESTAMP,
    
    -- Relación con usuario
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Índices para sync_queue
CREATE INDEX IF NOT EXISTS idx_sync_user ON sync_queue(user_id);
CREATE INDEX IF NOT EXISTS idx_sync_status ON sync_queue(sync_status);
CREATE INDEX IF NOT EXISTS idx_sync_pending ON sync_queue(user_id, sync_status, created_at);

-- ============================================
-- TRIGGERS: Actualizar timestamps automáticamente
-- ============================================

-- Trigger para users
CREATE TRIGGER IF NOT EXISTS update_users_timestamp 
AFTER UPDATE ON users
FOR EACH ROW
BEGIN
    UPDATE users SET updated_at = CURRENT_TIMESTAMP WHERE id = OLD.id;
END;

-- Trigger para tasks
CREATE TRIGGER IF NOT EXISTS update_tasks_timestamp 
AFTER UPDATE ON tasks
FOR EACH ROW
BEGIN
    UPDATE tasks SET updated_at = CURRENT_TIMESTAMP WHERE id = OLD.id;
END;

-- Trigger para study_sessions
CREATE TRIGGER IF NOT EXISTS update_sessions_timestamp 
AFTER UPDATE ON study_sessions
FOR EACH ROW
BEGIN
    UPDATE study_sessions SET updated_at = CURRENT_TIMESTAMP WHERE id = OLD.id;
END;

-- ============================================
-- TRIGGERS: Lógica de negocio automática
-- ============================================

-- Trigger: Actualizar estadísticas cuando se completa una tarea
CREATE TRIGGER IF NOT EXISTS task_completed_stats
AFTER UPDATE ON tasks
FOR EACH ROW
WHEN NEW.status = 'COMPLETED' AND OLD.status != 'COMPLETED'
BEGIN
    -- Actualizar contador de usuario
    UPDATE users 
    SET 
        tasks_completed = tasks_completed + 1,
        current_xp = current_xp + NEW.xp_reward,
        total_xp_earned = total_xp_earned + NEW.xp_reward
    WHERE id = NEW.user_id;
    
    -- Registrar en estadísticas diarias
    INSERT INTO daily_stats (user_id, stat_date, tasks_completed, xp_earned)
    VALUES (NEW.user_id, DATE('now'), 1, NEW.xp_reward)
    ON CONFLICT(user_id, stat_date) DO UPDATE SET
        tasks_completed = tasks_completed + 1,
        xp_earned = xp_earned + NEW.xp_reward;
    
    -- Registrar actividad
    INSERT INTO activity_log (user_id, activity_type, entity_type, entity_id, description, xp_change)
    VALUES (
        NEW.user_id, 
        'TASK_COMPLETED', 
        'task', 
        NEW.id,
        'Completaste: ' || NEW.title,
        NEW.xp_reward
    );
END;

-- Trigger: Verificar nivel cuando cambia XP
CREATE TRIGGER IF NOT EXISTS check_level_up
AFTER UPDATE ON users
FOR EACH ROW
WHEN NEW.current_xp != OLD.current_xp
BEGIN
    -- Calcular nuevo nivel (100 XP por nivel)
    UPDATE users
    SET level = (NEW.current_xp / 100) + 1
    WHERE id = NEW.id AND (NEW.current_xp / 100) + 1 > OLD.level;
    
    -- Registrar level up
    INSERT INTO activity_log (user_id, activity_type, entity_type, description)
    SELECT 
        NEW.id,
        'LEVEL_UP',
        'user',
        '¡Subiste al nivel ' || ((NEW.current_xp / 100) + 1) || '!'
    WHERE (NEW.current_xp / 100) + 1 > OLD.level;
END;

-- ============================================
-- DATOS INICIALES: Badges predefinidos
-- ============================================

INSERT OR IGNORE INTO badges (badge_key, name, description, icon_name, requirement_type, requirement_value, xp_bonus) VALUES
('FIRST_TASK', 'Primer Paso', 'Completaste tu primera tarea', 'star', 'TASK_COUNT', 1, 50),
('TASK_10', 'Novato Productivo', 'Completaste 10 tareas', 'trophy', 'TASK_COUNT', 10, 100),
('TASK_50', 'Estudiante Dedicado', 'Completaste 50 tareas', 'medal', 'TASK_COUNT', 50, 250),
('TASK_100', 'Maestro de Tareas', 'Completaste 100 tareas', 'crown', 'TASK_COUNT', 100, 500),

('STREAK_3', 'Constancia', '3 días consecutivos completando tareas', 'fire', 'STREAK', 3, 75),
('STREAK_7', 'Semana Perfecta', '7 días consecutivos de productividad', 'fire', 'STREAK', 7, 150),
('STREAK_30', 'Mes de Oro', '30 días de racha ininterrumpida', 'fire', 'STREAK', 30, 500),

('XP_1000', 'Aprendiz', 'Alcanzaste 1,000 XP', 'star', 'XP_MILESTONE', 1000, 100),
('XP_5000', 'Experto', 'Alcanzaste 5,000 XP', 'star', 'XP_MILESTONE', 5000, 300),
('XP_10000', 'Leyenda', 'Alcanzaste 10,000 XP', 'star', 'XP_MILESTONE', 10000, 750),

('MATH_MASTER', 'Genio Matemático', 'Completaste 20 tareas de Matemáticas', 'calculator', 'CATEGORY_MASTER', 20, 200),
('SCIENCE_MASTER', 'Científico Brillante', 'Completaste 20 tareas de Ciencias', 'microscope', 'CATEGORY_MASTER', 20, 200),
('HISTORY_MASTER', 'Historiador Experto', 'Completaste 20 tareas de Historia', 'book', 'CATEGORY_MASTER', 20, 200),

('EARLY_BIRD', 'Madrugador', 'Completa tareas antes de las 8 AM', 'sunrise', 'SPECIAL', 5, 150),
('NIGHT_OWL', 'Búho Nocturno', 'Completa tareas después de las 10 PM', 'moon', 'SPECIAL', 5, 150);

-- ============================================
-- DATOS INICIALES: Configuración de la app
-- ============================================

INSERT OR IGNORE INTO app_settings (setting_key, setting_value, setting_type, description) VALUES
('db_version', '1.0', 'STRING', 'Versión del esquema de base de datos'),
('xp_per_level', '100', 'INTEGER', 'XP necesarios para subir de nivel'),
('default_task_xp', '10', 'INTEGER', 'XP por defecto para tareas nuevas'),
('streak_reset_hours', '24', 'INTEGER', 'Horas sin actividad antes de perder racha'),
('enable_notifications', 'true', 'BOOLEAN', 'Notificaciones push habilitadas'),
('enable_calendar_sync', 'true', 'BOOLEAN', 'Sincronización con calendario habilitada'),
('theme_mode', 'auto', 'STRING', 'Modo de tema: light, dark, auto'),
('last_backup', '', 'STRING', 'Última fecha de backup'),
('sync_enabled', 'false', 'BOOLEAN', 'Sincronización cloud habilitada');

-- ============================================
-- VIEWS: Consultas frecuentes optimizadas
-- ============================================

-- Vista: Resumen del usuario con estadísticas
CREATE VIEW IF NOT EXISTS v_user_summary AS
SELECT 
    u.id,
    u.uuid,
    u.name,
    u.email,
    u.current_xp,
    u.level,
    u.current_streak,
    u.longest_streak,
    u.tasks_completed,
    COUNT(DISTINCT ub.badge_id) as badges_unlocked,
    (SELECT COUNT(*) FROM tasks t WHERE t.user_id = u.id AND t.status = 'PENDING') as pending_tasks,
    (SELECT COUNT(*) FROM tasks t WHERE t.user_id = u.id AND t.status = 'OVERDUE') as overdue_tasks,
    (SELECT SUM(xp_earned) FROM daily_stats ds WHERE ds.user_id = u.id AND ds.stat_date >= DATE('now', '-7 days')) as xp_last_week,
    u.created_at,
    u.last_login
FROM users u
LEFT JOIN user_badges ub ON u.id = ub.user_id AND ub.is_unlocked = 1
GROUP BY u.id;

-- Vista: Tareas con información enriquecida
CREATE VIEW IF NOT EXISTS v_tasks_enriched AS
SELECT 
    t.*,
    u.name as user_name,
    CASE 
        WHEN t.due_date < DATE('now') AND t.status != 'COMPLETED' THEN 1
        ELSE 0
    END as is_overdue,
    CASE 
        WHEN t.due_date = DATE('now') THEN 1
        ELSE 0
    END as is_today,
    julianday(t.due_date) - julianday('now') as days_until_due
FROM tasks t
JOIN users u ON t.user_id = u.id;

-- Vista: Progreso de badges por usuario
CREATE VIEW IF NOT EXISTS v_badge_progress AS
SELECT 
    ub.user_id,
    b.badge_key,
    b.name,
    b.description,
    b.requirement_type,
    b.requirement_value,
    ub.progress,
    ub.is_unlocked,
    ub.unlocked_at,
    CAST(ub.progress AS REAL) / b.requirement_value * 100 as progress_percentage
FROM user_badges ub
JOIN badges b ON ub.badge_id = b.id;

-- Vista: Estadísticas semanales
CREATE VIEW IF NOT EXISTS v_weekly_stats AS
SELECT 
    user_id,
    DATE('now', 'weekday 0', '-7 days') as week_start,
    SUM(tasks_completed) as tasks_this_week,
    SUM(xp_earned) as xp_this_week,
    SUM(study_minutes) as study_minutes_this_week,
    AVG(tasks_completed) as avg_tasks_per_day,
    COUNT(CASE WHEN streak_active = 1 THEN 1 END) as days_active
FROM daily_stats
WHERE stat_date >= DATE('now', 'weekday 0', '-7 days')
GROUP BY user_id;

-- ============================================
-- FIN DEL SCHEMA
-- ============================================
