package aegina.lacamaronera.DB

import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory

class DB(context: Context, name: String, factory: CursorFactory?, version: Int) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase)
    {
        db.execSQL("create table Ventas(idVenta INTEGER primary key AUTOINCREMENT, fecha text, idEmpleado INTEGER, nombreEmpleado text)")
        db.execSQL("create table Platillos(idVenta INTEGER primary key, idPlatillo INTEGER, cantidad real, nombre text, precio real)")
        db.execSQL("create table Ingredientes(idVenta INTEGER primary key, idIngrediente INTEGER, cantidad real)")
        db.execSQL("create table PlatillosRespaldo(idPlatillo INTEGER primary key, nombre text, precio real, idFamilia INTEGER, descripcion text)")
        db.execSQL("create table IngredientesRespaldo(idPlatillo INTEGER, idIngrediente INTEGER, cantidad real)")
        db.execSQL("create table User(idEmpleado INTEGER primary key, user text, nombre text, admin INTEGER, password text, token text, url text, internet INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int)
    {

    }
}