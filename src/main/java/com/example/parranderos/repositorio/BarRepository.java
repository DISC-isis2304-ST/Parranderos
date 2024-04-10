package com.example.parranderos.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import com.example.parranderos.modelo.Bar;

import jakarta.persistence.LockModeType;

public interface BarRepository extends JpaRepository<Bar, Integer> {

        public interface RespuestaBaresPorCiudad {
                String getCiudad();

                int getNumeroBares();

                int getCantidadSedes();
        }


        @Transactional
        @Query("SELECT b FROM Bar b")
        @Lock(LockModeType.PESSIMISTIC_READ)
        Collection<Bar> darBares();

        @Query(value = "SELECT * FROM bares WHERE id = :id", nativeQuery = true)
        Bar darBar(@Param("id") long id);

        // Consulta avanzada
        @Query(value = "SELECT B.*\r\n" + //
                        "FROM bares B\r\n" + //
                        "INNER JOIN sirven SB ON B.ID = SB.ID_BAR\r\n" + //
                        "INNER JOIN bebidas BR ON SB.ID_BEBIDA = BR.ID\r\n" + //
                        "INNER JOIN tipos_bebida TB ON BR.tipo = TB.ID\r\n" + //
                        "WHERE B.CIUDAD = :ciudad AND TB.NOMBRE = :tipo", nativeQuery = true)
        Collection<Bar> darBaresPorCiudadYTipoBebida(@Param("ciudad") String ciudad, @Param("tipo") String tipo);

        // Consulta avanzada
        @Query(value = "SELECT COUNT(*)\r\n" + //
                        "FROM bares B\r\n" + //
                        "INNER JOIN sirven SB ON B.ID = SB.ID_BAR\r\n" + //
                        "INNER JOIN bebidas BR ON SB.ID_BEBIDA = BR.ID\r\n" + //
                        "WHERE BR.GRADO_ALCOHOL = (SELECT MAX(GRADO_ALCOHOL) FROM bebidas)", nativeQuery = true)
        int darNumeroDeBaresQueSirvenBebidasConMayorGradoAlcohol();


        // Consulta avanzada
        @Query(value = "SELECT COUNT(*)\r\n" + //
                        "FROM bares B\r\n" + //
                        "INNER JOIN sirven SB ON B.ID = SB.ID_BAR\r\n" + //
                        "INNER JOIN bebidas BR ON SB.ID_BEBIDA = BR.ID\r\n" + //
                        "WHERE BR.GRADO_ALCOHOL = (SELECT MIN(GRADO_ALCOHOL) FROM bebidas)", nativeQuery = true)
        int darNumeroDeBaresQueSirvenBebidasConMenorGradoAlcohol();


        @Modifying
        @Transactional
        @Query(value = "DELETE FROM bares WHERE id = :id", nativeQuery = true)
        void eliminarBar(@Param("id") long id);

        @Modifying
        @Transactional
        @Query(value = "UPDATE bares SET nombre = :nombre, ciudad = :ciudad, presupuesto = :presupuesto, cant_sedes = :cant_sedes WHERE id = :id", nativeQuery = true)
        void actualizarBar(@Param("id") long id, @Param("nombre") String nombre, @Param("ciudad") String ciudad,
                        @Param("presupuesto") String presupuesto, @Param("cant_sedes") Integer cant_sedes);

        @Modifying
        @Transactional
        @Query(value = "INSERT INTO bares (id, nombre, ciudad, presupuesto, cant_sedes) VALUES ( parranderos_sequence.nextval , :nombre, :ciudad, :presupuesto, :cant_sedes)", nativeQuery = true)
        void insertarBar(@Param("nombre") String nombre, @Param("ciudad") String ciudad,
                        @Param("presupuesto") String presupuesto, @Param("cant_sedes") Integer cant_sedes);

}
