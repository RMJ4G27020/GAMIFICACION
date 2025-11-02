"""
Script para insertar datos de ejemplo en la base de datos
"""

import sqlite3
import os
from datetime import datetime, timedelta
import uuid

def insert_sample_data():
    """Inserta datos de ejemplo en la base de datos"""
    
    db_file = os.path.join(os.path.dirname(__file__), 'task_gamification.db')
    
    if not os.path.exists(db_file):
        print("❌ Error: No existe task_gamification.db")
        print("   Ejecuta primero: python create_database.py")
        return False
    
    try:
        conn = sqlite3.connect(db_file)
        cursor = conn.cursor()
        
        # Habilitar foreign keys
        cursor.execute("PRAGMA foreign_keys = ON;")
        
        print("📝 Insertando datos de ejemplo...\n")
        
        # 1. Crear usuario de ejemplo
        user_uuid = str(uuid.uuid4())
        cursor.execute("""
            INSERT INTO users (uuid, name, email, current_xp, level, current_streak, tasks_completed)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """, (user_uuid, "Ricardo Jiménez", "ricardo@example.com", 150, 2, 3, 8))
        user_id = cursor.lastrowid
        print(f"✅ Usuario creado: Ricardo Jiménez (ID: {user_id})")
        
        # 2. Crear tareas de ejemplo
        today = datetime.now()
        tasks = [
            ("Estudiar matemáticas", "Repasar capítulo 5 de álgebra lineal", "MATHEMATICS", "HIGH", 
             (today + timedelta(days=2)).strftime("%Y-%m-%d"), 20, "PENDING"),
            ("Leer historia", "Terminar lectura sobre la Segunda Guerra Mundial", "HISTORY", "MEDIUM",
             (today + timedelta(days=1)).strftime("%Y-%m-%d"), 15, "PENDING"),
            ("Hacer ejercicio", "30 minutos de cardio en el gimnasio", "EXERCISE", "MEDIUM",
             today.strftime("%Y-%m-%d"), 10, "PENDING"),
            ("Proyecto de ciencias", "Completar experimento de química", "SCIENCE", "HIGH",
             (today + timedelta(days=3)).strftime("%Y-%m-%d"), 25, "PENDING"),
            ("Tarea de inglés", "Escribir ensayo sobre Shakespeare", "STUDY", "LOW",
             (today + timedelta(days=5)).strftime("%Y-%m-%d"), 15, "PENDING"),
            ("Estudiar programación", "Completar curso de Python", "STUDY", "HIGH",
             (today - timedelta(days=1)).strftime("%Y-%m-%d"), 20, "COMPLETED"),
            ("Reunión grupal", "Reunión de proyecto final", "SOCIAL", "MEDIUM",
             (today - timedelta(days=2)).strftime("%Y-%m-%d"), 10, "COMPLETED"),
            ("Ejercicio matutino", "Correr 5km", "EXERCISE", "LOW",
             (today - timedelta(days=3)).strftime("%Y-%m-%d"), 10, "COMPLETED"),
        ]
        
        task_count = 0
        for title, description, category, priority, due_date, xp, status in tasks:
            task_uuid = str(uuid.uuid4())
            completed_at = datetime.now().strftime("%Y-%m-%d %H:%M:%S") if status == "COMPLETED" else None
            
            cursor.execute("""
                INSERT INTO tasks (uuid, user_id, title, description, category, priority, 
                                 status, due_date, xp_reward, completed_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """, (task_uuid, user_id, title, description, category, priority, 
                  status, due_date, xp, completed_at))
            task_count += 1
        
        print(f"✅ {task_count} tareas creadas")
        
        # 3. Inicializar progreso de badges
        cursor.execute("SELECT id FROM badges")
        badge_ids = cursor.fetchall()
        
        for badge_id in badge_ids:
            cursor.execute("""
                INSERT INTO user_badges (user_id, badge_id, progress, is_unlocked)
                VALUES (?, ?, ?, ?)
            """, (user_id, badge_id[0], 0, 0))
        
        print(f"✅ Progreso de badges inicializado ({len(badge_ids)} badges)")
        
        # 4. Crear sesiones de estudio
        sessions = [
            ("Matemáticas", "Repaso de cálculo diferencial", 
             (today + timedelta(days=1, hours=10)).strftime("%Y-%m-%d %H:%M:%S"), 60, "SCHEDULED"),
            ("Historia", "Análisis de documentos históricos",
             (today + timedelta(days=2, hours=14)).strftime("%Y-%m-%d %H:%M:%S"), 90, "SCHEDULED"),
            ("Programación", "Práctica de algoritmos",
             (today - timedelta(days=1)).strftime("%Y-%m-%d %H:%M:%S"), 120, "COMPLETED"),
        ]
        
        for subject, description, scheduled_date, duration, status in sessions:
            session_uuid = str(uuid.uuid4())
            xp = duration if status == "COMPLETED" else 0
            completed_at = datetime.now().strftime("%Y-%m-%d %H:%M:%S") if status == "COMPLETED" else None
            
            cursor.execute("""
                INSERT INTO study_sessions (uuid, user_id, subject, description, 
                                          scheduled_date, duration_minutes, status, 
                                          xp_earned, completed_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """, (session_uuid, user_id, subject, description, scheduled_date, 
                  duration, status, xp, completed_at))
        
        print(f"✅ {len(sessions)} sesiones de estudio creadas")
        
        # 5. Crear estadísticas diarias
        for i in range(7):
            date = (today - timedelta(days=i)).strftime("%Y-%m-%d")
            tasks_completed = 1 if i < 3 else 0
            xp_earned = 15 if i < 3 else 0
            
            cursor.execute("""
                INSERT INTO daily_stats (user_id, stat_date, tasks_completed, 
                                       xp_earned, study_minutes, streak_active)
                VALUES (?, ?, ?, ?, ?, ?)
            """, (user_id, date, tasks_completed, xp_earned, 
                  60 if i < 3 else 0, 1 if i < 3 else 0))
        
        print(f"✅ Estadísticas de últimos 7 días creadas")
        
        # 6. Crear actividades recientes
        activities = [
            ("TASK_COMPLETED", "task", 6, "Completaste: Estudiar programación", 20),
            ("TASK_COMPLETED", "task", 7, "Completaste: Reunión grupal", 10),
            ("LEVEL_UP", "user", user_id, "¡Subiste al nivel 2!", 0),
            ("TASK_COMPLETED", "task", 8, "Completaste: Ejercicio matutino", 10),
        ]
        
        for activity_type, entity_type, entity_id, description, xp_change in activities:
            cursor.execute("""
                INSERT INTO activity_log (user_id, activity_type, entity_type, 
                                        entity_id, description, xp_change)
                VALUES (?, ?, ?, ?, ?, ?)
            """, (user_id, activity_type, entity_type, entity_id, description, xp_change))
        
        print(f"✅ {len(activities)} actividades registradas")
        
        # Commit
        conn.commit()
        
        # Mostrar resumen
        print("\n" + "="*80)
        print("📊 RESUMEN DE LA BASE DE DATOS")
        print("="*80)
        
        cursor.execute("SELECT COUNT(*) FROM users")
        print(f"👤 Usuarios: {cursor.fetchone()[0]}")
        
        cursor.execute("SELECT COUNT(*) FROM tasks")
        print(f"📝 Tareas: {cursor.fetchone()[0]}")
        
        cursor.execute("SELECT COUNT(*) FROM tasks WHERE status = 'COMPLETED'")
        print(f"✅ Tareas completadas: {cursor.fetchone()[0]}")
        
        cursor.execute("SELECT COUNT(*) FROM tasks WHERE status = 'PENDING'")
        print(f"⏳ Tareas pendientes: {cursor.fetchone()[0]}")
        
        cursor.execute("SELECT COUNT(*) FROM badges")
        print(f"🏆 Badges disponibles: {cursor.fetchone()[0]}")
        
        cursor.execute("SELECT COUNT(*) FROM study_sessions")
        print(f"📚 Sesiones de estudio: {cursor.fetchone()[0]}")
        
        cursor.execute("SELECT COUNT(*) FROM daily_stats")
        print(f"📊 Días con estadísticas: {cursor.fetchone()[0]}")
        
        cursor.execute("SELECT COUNT(*) FROM activity_log")
        print(f"📋 Actividades registradas: {cursor.fetchone()[0]}")
        
        # Mostrar datos del usuario
        cursor.execute("""
            SELECT name, email, current_xp, level, current_streak, tasks_completed
            FROM users WHERE id = ?
        """, (user_id,))
        user_data = cursor.fetchone()
        
        print(f"\n👤 Usuario: {user_data[0]}")
        print(f"   📧 Email: {user_data[1]}")
        print(f"   ⭐ XP: {user_data[2]}")
        print(f"   🎯 Nivel: {user_data[3]}")
        print(f"   🔥 Racha: {user_data[4]} días")
        print(f"   ✅ Tareas completadas: {user_data[5]}")
        
        conn.close()
        
        print("\n" + "="*80)
        print("🎉 ¡DATOS DE EJEMPLO INSERTADOS CORRECTAMENTE!")
        print("="*80)
        print(f"\n📁 Base de datos: {db_file}")
        print(f"📦 Tamaño: {os.path.getsize(db_file) / 1024:.2f} KB")
        
        return True
        
    except sqlite3.Error as e:
        print(f"\n❌ Error de SQLite: {e}")
        conn.rollback()
        return False
    except Exception as e:
        print(f"\n❌ Error: {e}")
        return False

if __name__ == "__main__":
    print("\n" + "="*80)
    print("📝 INSERTAR DATOS DE EJEMPLO")
    print("   Gestor de Tareas Gamificado")
    print("="*80 + "\n")
    
    insert_sample_data()
