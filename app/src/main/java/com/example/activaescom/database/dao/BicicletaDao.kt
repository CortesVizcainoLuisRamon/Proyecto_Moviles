package com.example.activaescom.database.dao

import androidx.room.Dao
import androidx.room.Insert
import com.example.activaescom.database.entities.BicicletaConfiguracionEntity
import com.example.activaescom.database.entities.BicicletaDetalleEntity
import androidx.room.Query
import androidx.room.Transaction
import com.example.activaescom.database.relations.BicicletaCompleta

@Dao
interface BicicletaDao {

    @Insert
    suspend fun insertarConfiguracion(
        configuracion:
        BicicletaConfiguracionEntity
    )

    @Insert
    suspend fun insertarDetalle(
        detalle:
        BicicletaDetalleEntity
    )

    @Transaction
    @Query("""

SELECT * FROM entrenamientos

WHERE usuarioId = :usuarioId

ORDER BY fechaEntrenamiento DESC

""")
    suspend fun obtenerBicicletaCompleta(
        usuarioId: Int
    ): List<BicicletaCompleta>
}