package com.example.activaescom.database.dao

import androidx.room.Dao
import androidx.room.Insert
import com.example.activaescom.database.entities.YogaConfiguracionEntity
import com.example.activaescom.database.entities.YogaDetalleEntity
import androidx.room.Transaction
import androidx.room.Query
import com.example.activaescom.database.relations.YogaCompleta

@Dao
interface YogaDao {

    @Insert
    suspend fun insertarConfiguracion(
        configuracion:
        YogaConfiguracionEntity
    )

    @Insert
    suspend fun insertarDetalle(
        detalle:
        YogaDetalleEntity
    )

    @Transaction
    @Query("""

SELECT * FROM entrenamientos

WHERE usuarioId = :usuarioId

ORDER BY fechaEntrenamiento DESC

""")
    suspend fun obtenerYogaCompleto(
        usuarioId: Int
    ): List<YogaCompleta>
}