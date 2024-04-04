package com.example.parranderos.servicios;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.example.parranderos.modelo.Bar;
import com.example.parranderos.repositorio.BarRepository;
import com.example.parranderos.repositorio.SirvenRepository;
import java.lang.Thread;  



@Service
public class BaresServicio {
    
    private BarRepository baresRepository;

    public BaresServicio(BarRepository baresRepository)
    {
        this.baresRepository = baresRepository;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void transferir_bar_a_bogota(int id_bar, String destino)
    {
        try{
        Bar barActual = baresRepository.darBar(id_bar);
        Thread.sleep(20000);
        baresRepository.eliminarBar(id_bar);
        baresRepository.insertarBar( barActual.getNombre(), destino, barActual.getPresupuesto(), barActual.getCant_sedes());
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void transferir_bar_a_bogota_2(int id_bar, String destino)
    {
        Bar barActual = baresRepository.darBar(id_bar);
        baresRepository.eliminarBar(id_bar);
        baresRepository.insertarBar( barActual.getNombre(), destino, barActual.getPresupuesto(), barActual.getCant_sedes());
    }
}
