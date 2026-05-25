package com.example.activaescom.database.dao

import androidx.room.Dao
import androidx.room.Insert
import com.example.activaescom.database.entities.FuerzaConfiguracionEntity
import com.example.activaescom.database.entities.FuerzaDetalleEntity
import androidx.room.Query
import androidx.room.Transaction
import com.example.activaescom.database.relations.FuerzaCompleta

@Dao
interface FuerzaDao {

    @Insert
    suspend fun insertarConfiguracion(
        configuracion:
        FuerzaConfiguracionEntity
    )

    @Insert
    suspend fun insertarDetalle(
        detalle:
        FuerzaDetalleEntity
    )

    @Transaction
    @Query("""

SELECT * FROM entrenamientos

WHERE usuarioId = :usuarioId

ORDER BY fechaEntrenamiento DESC

""")
    suspend fun obtenerFuerzaCompleta(
        usuarioId: Int
    ): List<FuerzaCompleta>
}