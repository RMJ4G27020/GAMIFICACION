"""
Explorador interactivo de la base de datos SQLite
Permite ejecutar queries y ver resultados
"""

import sqlite3
import os
from tabulate import tabulate

def connect_db():
    """Conecta a la base de datos"""
    db_file = os.path.join(os.path.dirname(__file__), 'task_gamification.db')
    if not os.path.exists(db_file):
        print("❌ Error: No existe task_gamification.db")
        return None
    return sqlite3.connect(db_file)

def execute_query(conn, query):
    """Ejecuta una query y muestra resultados"""
    try:
        cursor = conn.cursor()
        cursor.execute(query)
        
        if query.strip().upper().startswith("SELECT"):
            results = cursor.fetchall()
            if results:
                headers = [description[0] for description in cursor.description]
                print("\n" + tabulate(results, headers=headers, tablefmt="grid"))
                print(f"\n📊 Total: {len(results)} registros")
            else:
                print("\n⚠️  No se encontraron resultados")
        else:
            conn.commit()
            print(f"\n✅ Query ejecutado. Filas afectadas: {cursor.rowcount}")
        
        return True
    except sqlite3.Error as e:
        print(f"\n❌ Error: {e}")
        return False

def show_tables(conn):
    """Muestra todas las tablas"""
    cursor = conn.cursor()
    cursor.execute("SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%' ORDER BY name;")
    tables = cursor.fetchall()
    
    print("\n" + "="*80)
    print("📊 TABLAS EN LA BASE DE DATOS")
    print("="*80)
    
    for i, table in enumerate(tables, 1):
        cursor.execute(f"SELECT COUNT(*) FROM {table[0]}")
        count = cursor.fetchone()[0]
        print(f"{i:2}. {table[0]:20} ({count} registros)")

def show_schema(conn, table_name):
    """Muestra el schema de una tabla"""
    cursor = conn.cursor()
    cursor.execute(f"PRAGMA table_info({table_name})")
    columns = cursor.fetchall()
    
    print(f"\n📋 Estructura de: {table_name}")
    print("="*80)
    
    headers = ["#", "Columna", "Tipo", "NotNull", "Default", "PK"]
    data = [[col[0], col[1], col[2], "✓" if col[3] else "", col[4] or "", "✓" if col[5] else ""] 
            for col in columns]
    
    print(tabulate(data, headers=headers, tablefmt="grid"))

def quick_queries(conn):
    """Ejecuta queries predefinidas útiles"""
    queries = {
        "1": ("Ver todos los usuarios", "SELECT id, name, email, current_xp, level, current_streak, tasks_completed FROM users;"),
        "2": ("Ver tareas pendientes", "SELECT id, title, category, priority, due_date, xp_reward FROM tasks WHERE status = 'PENDING';"),
        "3": ("Ver tareas completadas", "SELECT id, title, category, completed_at, xp_reward FROM tasks WHERE status = 'COMPLETED';"),
        "4": ("Ver todos los badges", "SELECT badge_key, name, description, requirement_value, xp_bonus FROM badges;"),
        "5": ("Ver progreso de badges del usuario", "SELECT b.name, ub.progress, b.requirement_value, ub.is_unlocked FROM user_badges ub JOIN badges b ON ub.badge_id = b.id WHERE ub.user_id = 1;"),
        "6": ("Ver sesiones de estudio", "SELECT id, subject, scheduled_date, duration_minutes, status FROM study_sessions;"),
        "7": ("Ver estadísticas diarias", "SELECT stat_date, tasks_completed, xp_earned, study_minutes FROM daily_stats ORDER BY stat_date DESC;"),
        "8": ("Ver actividad reciente", "SELECT activity_type, description, xp_change, created_at FROM activity_log ORDER BY created_at DESC LIMIT 10;"),
        "9": ("Ver resumen del usuario", "SELECT * FROM v_user_summary;"),
        "10": ("Ver tareas enriquecidas", "SELECT title, category, status, due_date, is_overdue, days_until_due FROM v_tasks_enriched;"),
    }
    
    print("\n" + "="*80)
    print("🔍 QUERIES RÁPIDAS")
    print("="*80)
    
    for key, (description, _) in queries.items():
        print(f"{key:3}. {description}")
    
    print("\n  0. Volver")
    
    choice = input("\n👉 Selecciona una opción: ").strip()
    
    if choice in queries:
        description, query = queries[choice]
        print(f"\n📝 Ejecutando: {description}")
        print(f"SQL: {query}\n")
        execute_query(conn, query)
    elif choice != "0":
        print("\n⚠️  Opción inválida")

def main():
    """Función principal"""
    conn = connect_db()
    if not conn:
        return
    
    print("\n" + "="*80)
    print("🗄️  EXPLORADOR DE BASE DE DATOS SQLite")
    print("   task_gamification.db")
    print("="*80)
    
    while True:
        print("\n" + "-"*80)
        print("MENÚ PRINCIPAL")
        print("-"*80)
        print("1. Ver todas las tablas")
        print("2. Ver estructura de una tabla")
        print("3. Ejecutar query SQL personalizado")
        print("4. Queries rápidas predefinidas")
        print("5. Salir")
        
        choice = input("\n👉 Selecciona una opción: ").strip()
        
        if choice == "1":
            show_tables(conn)
        
        elif choice == "2":
            table_name = input("\n📋 Nombre de la tabla: ").strip()
            show_schema(conn, table_name)
        
        elif choice == "3":
            print("\n💡 Escribe tu query SQL (o 'cancel' para cancelar):")
            query = input("SQL> ").strip()
            if query.lower() != 'cancel':
                execute_query(conn, query)
        
        elif choice == "4":
            quick_queries(conn)
        
        elif choice == "5":
            print("\n👋 ¡Hasta luego!")
            break
        
        else:
            print("\n⚠️  Opción inválida")
    
    conn.close()

if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        print("\n\n👋 ¡Hasta luego!")
    except Exception as e:
        print(f"\n❌ Error: {e}")
