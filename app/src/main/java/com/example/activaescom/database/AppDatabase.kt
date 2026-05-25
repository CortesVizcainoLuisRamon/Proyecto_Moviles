package com.example.activaescom.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.activaescom.database.dao.ActividadDao
import com.example.activaescom.database.dao.UbicacionDao
import com.example.activaescom.database.dao.UsuarioDao
import com.example.activaescom.database.entities.ActividadEntity
import com.example.activaescom.database.entities.UbicacionEntity
import com.example.activaescom.database.entities.UsuarioEntity
import com.example.activaescom.database.entities.EntrenamientoEntity
import com.example.activaescom.database.dao.EntrenamientoDao
import com.example.activaescom.database.entities.CarreraDetalleEntity
import com.example.activaescom.database.dao.CarreraDetalleDao
import com.example.activaescom.database.entities.CarreraConfiguracionEntity
import com.example.activaescom.database.dao.CarreraConfiguracionDao
import com.example.activaescom.database.entities.BicicletaConfiguracionEntity
import com.example.activaescom.database.entities.BicicletaDetalleEntity
import com.example.activaescom.database.dao.BicicletaDao
import com.example.activaescom.database.entities.FuerzaConfiguracionEntity
import com.example.activaescom.database.entities.FuerzaDetalleEntity
import com.example.activaescom.database.dao.FuerzaDao
import com.example.activaescom.database.entities.CaminataConfiguracionEntity
import com.example.activaescom.database.entities.CaminataDetalleEntity
import com.example.activaescom.database.dao.CaminataDao
import com.example.activaescom.database.entities.NatacionConfiguracionEntity
import com.example.activaescom.database.entities.NatacionDetalleEntity
import com.example.activaescom.database.dao.NatacionDao
import com.example.activaescom.database.entities.YogaConfiguracionEntity
import com.example.activaescom.database.entities.YogaDetalleEntity
import com.example.activaescom.database.dao.YogaDao

@Database(
    entities = [
        UsuarioEntity::class,
        ActividadEntity::class,
        UbicacionEntity::class,
        EntrenamientoEntity::class,
        CarreraDetalleEntity::class,
        CarreraConfiguracionEntity::class,
        BicicletaConfiguracionEntity::class,
        BicicletaDetalleEntity::class,
        FuerzaConfiguracionEntity::class,
        FuerzaDetalleEntity::class,
        CaminataConfiguracionEntity::class,
        CaminataDetalleEntity::class,
        NatacionConfiguracionEntity::class,
        NatacionDetalleEntity::class,
        YogaConfiguracionEntity::class,
        YogaDetalleEntity::class
    ],
    version = 11
)

abstract class AppDatabase : RoomDatabase() {

    abstract fun yogaDao(): YogaDao
    abstract fun natacionDao(): NatacionDao
    abstract fun caminataDao(): CaminataDao
    abstract fun fuerzaDao(): FuerzaDao
    abstract fun bicicletaDao(): BicicletaDao
    abstract fun usuarioDao(): UsuarioDao

    abstract fun actividadDao(): ActividadDao

    abstract fun ubicacionDao(): UbicacionDao

    abstract fun entrenamientoDao(): EntrenamientoDao

    abstract fun carreraDetalleDao(): CarreraDetalleDao

    abstract fun carreraConfiguracionDao(): CarreraConfiguracionDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "activa_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()


                INSTANCE = instance

                instance
            }
        }
    }

}