package com.example.parranderos.servicios;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.example.parranderos.modelo.Bar;
import com.example.parranderos.repositorio.BarRepository;
import com.example.parranderos.repositorio.SirvenRepository;

import jakarta.persistence.LockModeType;

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


    @Transactional(isolation = Isolation.SERIALIZABLE, readOnly = true)
    public Collection<Bar> darBares() throws InterruptedException {
        Collection<Bar> bares = baresRepository.darBares(); // Consultar bar.
        return bares;
    }

    // Transacción para transferir un bar a Bogotá con un nivel de aislamiento que permite lecturas comprometidas.
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void actualizar_bar_con_espera(int id_bar, String destino) {
        try {
            Bar barActual = baresRepository.darBar(id_bar); // Obtener el bar actual.
            Thread.sleep(10000); // Simulación de una operación de larga duración.
            // Insertar el bar con la nueva ubicación.
            baresRepository.actualizarBar(id_bar, barActual.getNombre(), destino, barActual.getPresupuesto(), barActual.getCant_sedes()+3);
        } catch (Exception e) {
            System.out.println(e.getMessage()); // Manejo de excepciones.
        }
    }
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public void actualizar_bar_sin_espera(int id_bar, String destino) {
        try {
            Bar barActual = baresRepository.darBar(id_bar); // Obtener el bar actual.
            // Insertar el bar con la nueva ubicación.
            baresRepository.actualizarBar(id_bar, barActual.getNombre(), destino, barActual.getPresupuesto(), barActual.getCant_sedes()+3);
        } catch (Exception e) {
            System.out.println(e.getMessage()); // Manejo de excepciones.
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
