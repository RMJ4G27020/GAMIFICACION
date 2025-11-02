"""
Script para crear la base de datos SQLite desde schema.sql
Genera el archivo task_gamification.db listo para usar
"""

import sqlite3
import os
import sys

def create_database():
    """Crea la base de datos SQLite desde el archivo schema.sql"""
    
    # Rutas
    script_dir = os.path.dirname(os.path.abspath(__file__))
    schema_file = os.path.join(script_dir, 'schema.sql')
    db_file = os.path.join(script_dir, 'task_gamification.db')
    
    # Verificar que existe schema.sql
    if not os.path.exists(schema_file):
        print(f"❌ Error: No se encuentra {schema_file}")
        return False
    
    # Eliminar BD existente si existe
    if os.path.exists(db_file):
        print(f"⚠️  Eliminando base de datos existente...")
        os.remove(db_file)
    
    try:
        # Leer el schema
        print(f"📖 Leyendo schema desde: {schema_file}")
        with open(schema_file, 'r', encoding='utf-8') as f:
            schema_sql = f.read()
        
        # Crear conexión a la base de datos
        print(f"🔨 Creando base de datos: {db_file}")
        conn = sqlite3.connect(db_file)
        cursor = conn.cursor()
        
        # Habilitar foreign keys
        cursor.execute("PRAGMA foreign_keys = ON;")
        
        # Ejecutar el schema completo
        print(f"⚙️  Ejecutando schema SQL...")
        cursor.executescript(schema_sql)
        
        # Commit y cerrar
        conn.commit()
        
        # Verificar tablas creadas
        cursor.execute("SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%';")
        tables = cursor.fetchall()
        
        print(f"\n✅ Base de datos creada exitosamente!")
        print(f"📍 Ubicación: {db_file}")
        print(f"📦 Tamaño: {os.path.getsize(db_file) / 1024:.2f} KB")
        print(f"\n📊 Tablas creadas ({len(tables)}):")
        for table in tables:
            cursor.execute(f"SELECT COUNT(*) FROM {table[0]}")
            count = cursor.fetchone()[0]
            print(f"   • {table[0]}: {count} registros")
        
        # Verificar badges
        cursor.execute("SELECT COUNT(*) FROM badges")
        badge_count = cursor.fetchone()[0]
        print(f"\n🏆 Badges predefinidos: {badge_count}")
        
        # Verificar configuración
        cursor.execute("SELECT COUNT(*) FROM app_settings")
        settings_count = cursor.fetchone()[0]
        print(f"⚙️  Configuraciones: {settings_count}")
        
        # Verificar vistas
        cursor.execute("SELECT name FROM sqlite_master WHERE type='view';")
        views = cursor.fetchall()
        print(f"👁️  Vistas: {len(views)}")
        for view in views:
            print(f"   • {view[0]}")
        
        # Verificar triggers
        cursor.execute("SELECT name FROM sqlite_master WHERE type='trigger';")
        triggers = cursor.fetchall()
        print(f"⚡ Triggers: {len(triggers)}")
        for trigger in triggers:
            print(f"   • {trigger[0]}")
        
        conn.close()
        
        print("\n" + "="*80)
        print("🎉 ¡BASE DE DATOS LISTA PARA USAR!")
        print("="*80)
        print(f"\n💡 Puedes abrir el archivo con:")
        print(f"   • DB Browser for SQLite: https://sqlitebrowser.org/")
        print(f"   • SQLite Viewer (VS Code Extension)")
        print(f"   • Comando: sqlite3 {db_file}")
        print("\n📁 Archivo creado: task_gamification.db")
        
        return True
        
    except sqlite3.Error as e:
        print(f"\n❌ Error de SQLite: {e}")
        return False
    except Exception as e:
        print(f"\n❌ Error: {e}")
        return False

if __name__ == "__main__":
    print("\n" + "="*80)
    print("🗄️  GENERADOR DE BASE DE DATOS SQLite")
    print("    Gestor de Tareas Gamificado")
    print("="*80 + "\n")
    
    success = create_database()
    
    if success:
        sys.exit(0)
    else:
        sys.exit(1)
