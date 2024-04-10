package com.example.parranderos.servicios;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.example.parranderos.modelo.Bar;
import com.example.parranderos.repositorio.BarRepository;
import com.example.parranderos.repositorio.SirvenRepository;
import java.lang.Thread;
import java.util.List;  
import java.util.Collection;



@Service
public class BaresServicio {
    
    private BarRepository baresRepository;

    public BaresServicio(BarRepository baresRepository)
    {
        this.baresRepository = baresRepository;
    }

    // Transacción para transferir un bar a Bogotá con un nivel de aislamiento que permite lecturas comprometidas.
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void transferir_bar(int id_bar, String destino) {
        try {
            Bar barActual = baresRepository.darBar(id_bar); // Obtener el bar actual.
            Thread.sleep(10000); // Simulación de una operación de larga duración.
            baresRepository.eliminarBar(id_bar); // Eliminar el bar actual.
            // Insertar el bar con la nueva ubicación.
            baresRepository.insertarBar(barActual.getNombre(), destino, barActual.getPresupuesto(), barActual.getCant_sedes());
        } catch (Exception e) {
            System.out.println(e.getMessage()); // Manejo de excepciones.
        }
    }

    // Método para transferir un bar con simulación de error para probar el rollback.
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void transferir_bar_rollback(int id_bar, String destino) {
        try {
            Bar barActual = baresRepository.darBar(id_bar);
            Thread.sleep(10000); // Simula una operación de larga duración
            baresRepository.eliminarBar(id_bar);
            if (true) { // Forzar el error
                throw new Exception("Error forzado para probar el rollback");
            }
            baresRepository.insertarBar(barActual.getNombre(), destino, barActual.getPresupuesto(), barActual.getCant_sedes());
        } catch (Exception e) {
            System.out.println("Se ha producido un error: " + e.getMessage());
            // Lanzar RuntimeException para provocar rollback.
            throw new RuntimeException("Error al transferir el bar: " + e.getMessage(), e);
        }
    }

    // Método para consultar un bar y bloquear la tabla de bares usando el nivel de aislamiento SERIALIZABLE.
    @Transactional(isolation = Isolation.SERIALIZABLE, readOnly = true)
    public Collection<Bar> consultarBaresSinFantasma() throws InterruptedException {
        Collection<Bar> bares = baresRepository.darBares(); // Consultar bar.
        System.out.println(bares.size());
        Thread.sleep(10000); // Simular operación larga para mantener el bloqueo.
        bares = baresRepository.darBares(); // Consultar bar.
        return bares; // Devolver el bar consultado.
    }

    // Método para consultar un bar y bloquear la tabla de bares usando el nivel de aislamiento READ_COMMITTED.
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Collection<Bar> consultarBaresConFantasma() throws InterruptedException {
        Collection<Bar> bares = baresRepository.darBares(); // Consultar bar.
        System.out.println(bares.size());
        Thread.sleep(10000); // Simular operación larga para mantener el bloqueo.
        bares = baresRepository.darBares(); // Consultar bar.
        return bares; // Devolver el bar consultado.
    }

}
