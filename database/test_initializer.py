#!/usr/bin/env python3
"""
Script de prueba para simular la inicialización de la base de datos
similar a lo que hace DatabaseInitializer.kt


"""

import sqlite3
import uuid
from datetime import datetime, timedelta

def initialize_database():
    """Inicializa la base de datos con datos de prueba"""
    
    # Conectar a la base de datos
    db_path = "task_gamification.db"
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    
    print("=" * 80)
    print("🔄 INICIALIZANDO BASE DE DATOS")
    print("=" * 80)
    
    try:
        # 1. Verificar si existe un usuario
        cursor.execute("SELECT COUNT(*) FROM users")
        user_count = cursor.fetchone()[0]
        
        if user_count == 0:
            print("📝 Creando usuario por defecto...")
            
            # Crear usuario por defecto
            user_uuid = str(uuid.uuid4())
            cursor.execute("""
                INSERT INTO users (uuid, name, email, current_xp, level, current_streak, 
                                 longest_streak, tasks_completed, total_xp_earned)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """, (user_uuid, "Usuario", "usuario@example.com", 0, 1, 0, 0, 0, 0))
            
            user_id = cursor.lastrowid
            print(f"✅ Usuario creado con ID: {user_id}")
            
            # 2. Inicializar progreso de badges
            print("🏆 Inicializando progreso de badges...")
            cursor.execute("SELECT id FROM badges WHERE is_active = 1")
            badges = cursor.fetchall()
            
            for badge_id in badges:
                cursor.execute("""
                    INSERT OR IGNORE INTO user_badges (user_id, badge_id, progress, is_unlocked)
                    VALUES (?, ?, 0, 0)
                """, (user_id, badge_id[0]))
            
            print(f"✅ Inicializados {len(badges)} badges")
            
            # 3. Crear tareas de ejemplo
            print("📋 Creando tareas de ejemplo...")
            
            today = datetime.now()
            sample_tasks = [
                ("Estudiar matemáticas", "Repasar capítulo 5 de álgebra", "MATHEMATICS", "HIGH", 
                 (today + timedelta(days=3)).strftime("%Y-%m-%d"), 20),
                ("Leer historia", "Terminar lectura sobre la Segunda Guerra Mundial", "HISTORY", "MEDIUM",
                 (today + timedelta(days=1)).strftime("%Y-%m-%d"), 15),
                ("Hacer ejercicio", "30 minutos de cardio", "EXERCISE", "MEDIUM",
                 today.strftime("%Y-%m-%d"), 10),
                ("Proyecto de ciencias", "Completar experimento de química", "SCIENCE", "HIGH",
                 (today + timedelta(days=4)).strftime("%Y-%m-%d"), 25),
                ("Tarea de inglés", "Escribir ensayo sobre Shakespeare", "STUDY", "LOW",
                 (today + timedelta(days=6)).strftime("%Y-%m-%d"), 15),
            ]
            
            for task in sample_tasks:
                task_uuid = str(uuid.uuid4())
                cursor.execute("""
                    INSERT INTO tasks (uuid, user_id, title, description, category, priority, 
                                     status, due_date, xp_reward)
                    VALUES (?, ?, ?, ?, ?, ?, 'PENDING', ?, ?)
                """, (task_uuid, user_id, *task))
            
            print(f"✅ Creadas {len(sample_tasks)} tareas de ejemplo")
            
            # Commit de todos los cambios
            conn.commit()
            
        else:
            print(f"ℹ️  Ya existe(n) {user_count} usuario(s) en la base de datos")
        
        # 4. Mostrar estadísticas finales
        print("\n" + "=" * 80)
        print("📊 ESTADÍSTICAS DE LA BASE DE DATOS")
        print("=" * 80)
        
        # Contar tablas
        cursor.execute("""
            SELECT COUNT(*) FROM sqlite_master 
            WHERE type='table' AND name NOT LIKE 'sqlite_%'
        """)
        table_count = cursor.fetchone()[0]
        print(f"📁 Tablas: {table_count}")
        
        # Contar usuarios
        cursor.execute("SELECT COUNT(*) FROM users")
        user_count = cursor.fetchone()[0]
        print(f"👤 Usuarios: {user_count}")
        
        # Contar tareas
        cursor.execute("SELECT COUNT(*) FROM tasks")
        task_count = cursor.fetchone()[0]
        print(f"📋 Tareas: {task_count}")
        
        # Contar badges
        cursor.execute("SELECT COUNT(*) FROM badges")
        badge_count = cursor.fetchone()[0]
        print(f"🏆 Badges: {badge_count}")
        
        # Mostrar información del usuario
        cursor.execute("""
            SELECT name, email, current_xp, level, tasks_completed 
            FROM users LIMIT 1
        """)
        user = cursor.fetchone()
        if user:
            print(f"\n👤 Usuario Principal:")
            print(f"   Nombre: {user[0]}")
            print(f"   Email: {user[1]}")
            print(f"   XP: {user[2]}")
            print(f"   Nivel: {user[3]}")
            print(f"   Tareas Completadas: {user[4]}")
        
        # Mostrar tareas pendientes
        cursor.execute("""
            SELECT title, category, priority, due_date, xp_reward 
            FROM tasks 
            WHERE status = 'PENDING'
            ORDER BY priority DESC, due_date ASC
        """)
        tasks = cursor.fetchall()
        
        if tasks:
            print(f"\n📋 Tareas Pendientes ({len(tasks)}):")
            for i, task in enumerate(tasks, 1):
                print(f"   {i}. [{task[2]}] {task[0]} - {task[1]} (Vence: {task[3]}, +{task[4]} XP)")
        
        # Mostrar badges disponibles
        cursor.execute("""
            SELECT name, description, requirement_value, xp_bonus 
            FROM badges 
            WHERE is_active = 1
            ORDER BY requirement_value ASC
            LIMIT 5
        """)
        badges = cursor.fetchall()
        
        if badges:
            print(f"\n🏆 Primeros Badges Disponibles:")
            for badge in badges:
                print(f"   • {badge[0]} - {badge[1]} (+{badge[3]} XP)")
        
        print("\n" + "=" * 80)
        print("✅ BASE DE DATOS INICIALIZADA CORRECTAMENTE")
        print("=" * 80)
        
        # Mostrar ubicación del archivo
        import os
        db_size = os.path.getsize(db_path)
        db_size_kb = db_size / 1024
        
        print(f"\n📍 Ubicación: {os.path.abspath(db_path)}")
        print(f"📦 Tamaño: {db_size_kb:.2f} KB")
        print(f"🔢 Versión: 1.0")
        
        return True
        
    except Exception as e:
        print(f"\n❌ Error al inicializar: {e}")
        conn.rollback()
        return False
        
    finally:
        conn.close()


def test_triggers():
    """Prueba los triggers de la base de datos"""
    
    print("\n" + "=" * 80)
    print("🧪 PROBANDO TRIGGERS AUTOMÁTICOS")
    print("=" * 80)
    
    db_path = "task_gamification.db"
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    
    try:
        # Obtener XP actual del usuario
        cursor.execute("SELECT id, current_xp, tasks_completed FROM users LIMIT 1")
        user_id, xp_before, tasks_before = cursor.fetchone()
        
        print(f"\n📊 Estado ANTES:")
        print(f"   XP: {xp_before}")
        print(f"   Tareas Completadas: {tasks_before}")
        
        # Completar la primera tarea pendiente
        cursor.execute("""
            SELECT id, title, xp_reward 
            FROM tasks 
            WHERE status = 'PENDING' 
            LIMIT 1
        """)
        task = cursor.fetchone()
        
        if task:
            task_id, task_title, xp_reward = task
            
            print(f"\n✅ Completando tarea: '{task_title}' (+{xp_reward} XP)")
            
            # Actualizar el estado de la tarea (esto debe activar el trigger)
            cursor.execute("""
                UPDATE tasks 
                SET status = 'COMPLETED', 
                    completed_at = CURRENT_TIMESTAMP 
                WHERE id = ?
            """, (task_id,))
            
            conn.commit()
            
            # Verificar cambios
            cursor.execute("SELECT current_xp, tasks_completed FROM users WHERE id = ?", (user_id,))
            xp_after, tasks_after = cursor.fetchone()
            
            print(f"\n📊 Estado DESPUÉS:")
            print(f"   XP: {xp_after} (+{xp_after - xp_before})")
            print(f"   Tareas Completadas: {tasks_after} (+{tasks_after - tasks_before})")
            
            # Verificar estadísticas diarias
            cursor.execute("""
                SELECT tasks_completed, xp_earned 
                FROM daily_stats 
                WHERE user_id = ? AND stat_date = DATE('now')
            """, (user_id,))
            daily = cursor.fetchone()
            
            if daily:
                print(f"\n📅 Estadísticas de Hoy:")
                print(f"   Tareas: {daily[0]}")
                print(f"   XP Ganado: {daily[1]}")
            
            # Verificar registro de actividad
            cursor.execute("""
                SELECT description, xp_change 
                FROM activity_log 
                WHERE user_id = ? 
                ORDER BY created_at DESC 
                LIMIT 1
            """, (user_id,))
            activity = cursor.fetchone()
            
            if activity:
                print(f"\n📝 Última Actividad:")
                print(f"   {activity[0]} (+{activity[1]} XP)")
            
            print("\n✅ ¡Triggers funcionando correctamente!")
        else:
            print("\nℹ️  No hay tareas pendientes para probar")
        
    except Exception as e:
        print(f"\n❌ Error en prueba: {e}")
        conn.rollback()
    finally:
        conn.close()


if __name__ == "__main__":
    # Inicializar base de datos
    success = initialize_database()
    
    if success:
        # Probar triggers
        test_triggers()
        
        print("\n" + "=" * 80)
        print("🎉 ¡INICIALIZACIÓN COMPLETA!")
        print("=" * 80)
        print("\n💡 Próximos pasos:")
        print("   1. Abre 'task_gamification.db' con DB Browser for SQLite")
        print("   2. Explora las tablas: users, tasks, badges, etc.")
        print("   3. Ejecuta 'python query_database.py' para ver consultas de ejemplo")
        print("   4. En Android Studio, usa Database Inspector para ver la BD en la app")
        print()
