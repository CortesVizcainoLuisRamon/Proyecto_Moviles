package com.example.activaescom.database.dao

import androidx.room.Dao
import androidx.room.Insert
import com.example.activaescom.database.entities.CaminataConfiguracionEntity
import com.example.activaescom.database.entities.CaminataDetalleEntity
import androidx.room.Query
import androidx.room.Transaction
import com.example.activaescom.database.relations.CaminataCompleta

@Dao
interface CaminataDao {

    @Insert
    suspend fun insertarConfiguracion(
        configuracion:
        CaminataConfiguracionEntity
    )

    @Insert
    suspend fun insertarDetalle(
        detalle:
        CaminataDetalleEntity
    )

    @Transaction
    @Query("""

SELECT * FROM entrenamientos

WHERE usuarioId = :usuarioId

ORDER BY fechaEntrenamiento DESC

""")
    suspend fun obtenerCaminataCompleta(
        usuarioId: Int
    ): List<CaminataCompleta>
}