package com.example.activaescom.database.dao

import androidx.room.Dao
import androidx.room.Insert
import com.example.activaescom.database.entities.NatacionConfiguracionEntity
import com.example.activaescom.database.entities.NatacionDetalleEntity
import androidx.room.Query
import androidx.room.Transaction
import com.example.activaescom.database.relations.NatacionCompleta


@Dao
interface NatacionDao {

    @Insert
    suspend fun insertarConfiguracion(
        configuracion:
        NatacionConfiguracionEntity
    )

    @Insert
    suspend fun insertarDetalle(
        detalle:
        NatacionDetalleEntity
    )

    @Transaction
    @Query("""

SELECT * FROM entrenamientos

WHERE usuarioId = :usuarioId

ORDER BY fechaEntrenamiento DESC

""")
    suspend fun obtenerNatacionCompleta(
        usuarioId: Int
    ): List<NatacionCompleta>
}