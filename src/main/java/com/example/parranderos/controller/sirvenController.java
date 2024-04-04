package com.example.parranderos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.parranderos.modelo.Bar;
import com.example.parranderos.modelo.Bebida;
import com.example.parranderos.modelo.Sirven;
import com.example.parranderos.modelo.SirvenPK;
import com.example.parranderos.repositorio.BarRepository;
import com.example.parranderos.repositorio.BebidaRepository;
import com.example.parranderos.repositorio.SirvenRepository;
import com.example.parranderos.servicios.SirvenServicio;

@Controller
public class SirvenController {

    @Autowired
    private SirvenRepository sirvenRepository;

    @Autowired
    private BarRepository barRepository;

    @Autowired
    private BebidaRepository bebidaRepository;

    @Autowired
    private SirvenServicio sirvenServicio;

    @GetMapping("/sirven/new")
    public String sirvenForm(Model model) {
        model.addAttribute("bares", barRepository.darBares());
        model.addAttribute("bebidas", bebidaRepository.darBebidas());
        return "sirvenNuevo";
    }

    @PostMapping("/sirven/new/save")
    public String sirvenGuardar(@ModelAttribute("id_bar") Long idBar,
            @ModelAttribute("id_bebida") Long idBebida, @ModelAttribute("horario") String horario) {

        Bar bar = barRepository.darBar(idBar);
        Bebida bebida = bebidaRepository.darBebida(idBebida);
        SirvenPK pk = new SirvenPK(bar, bebida, horario);
        Sirven sirven = new Sirven();
        sirven.setPk(pk);
        sirvenRepository.insertarSirven(sirven.getPk().getBar().getId(), sirven.getPk().getBebida().getId(),
                sirven.getPk().getHorario());
        return "redirect:/bares";
    }

    @GetMapping("sirven/transferir")
    public String transferirSirven(){
        sirvenServicio.transferir_bebida(2,1,"todos", 2);
        return "redirect:/bares";
    }

}
