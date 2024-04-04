package com.example.parranderos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.parranderos.modelo.Bar;
import com.example.parranderos.repositorio.BarRepository;
import com.example.parranderos.repositorio.Tipo_bebidaRepository;
import com.example.parranderos.servicios.BaresServicio;

@Controller
public class BaresController {

    @Autowired
    private BarRepository barRepository;

    @Autowired
    private Tipo_bebidaRepository tipo_bebidaRepository;

    @Autowired
    private BaresServicio baresServicio;

    @GetMapping("/bares")
    public String bares(Model model, String ciudad, String tipo) {
        int NumeroDeBaresQueSirvenBebidasConMayorGradoAlcohol = barRepository
                .darNumeroDeBaresQueSirvenBebidasConMayorGradoAlcohol();
        int NumeroDeBaresQueSirvenBebidasConMenorGradoAlcohol = barRepository
                .darNumeroDeBaresQueSirvenBebidasConMenorGradoAlcohol();
        model.addAttribute("NumeroDeBaresQueSirvenBebidasConMayorGradoAlcohol",
                NumeroDeBaresQueSirvenBebidasConMayorGradoAlcohol);
        model.addAttribute("NumeroDeBaresQueSirvenBebidasConMenorGradoAlcohol",
                NumeroDeBaresQueSirvenBebidasConMenorGradoAlcohol);
        model.addAttribute("tipos", tipo_bebidaRepository.darTipos_bebida());

        if((ciudad == null || ciudad.equals("")) || (tipo ==null || tipo.equals("")))
        {
            System.out.println("default search");
            model.addAttribute("bares", barRepository.darBares());
        }
        else{
            System.out.println(barRepository.darBaresPorCiudadYTipoBebida(ciudad, tipo));
            model.addAttribute("bares", barRepository.darBaresPorCiudadYTipoBebida(ciudad, tipo));
        }

        return "bares";
    }

    @GetMapping("/bares/new")
    public String barForm(Model model) {
        model.addAttribute("bar", new Bar());
        return "barNuevo";
    }

    @PostMapping("/bares/new/save")
    public String barGuardar(@ModelAttribute Bar bar) {
        barRepository.insertarBar(bar.getNombre(), bar.getCiudad(), bar.getPresupuesto(), bar.getCant_sedes());
        return "redirect:/bares";
    }

    @GetMapping("/bares/{id}/edit")
    public String barEditarForm(@PathVariable("id") long id, Model model) {
        Bar bar = barRepository.darBar(id);
        if (bar != null) {
            model.addAttribute("bar", bar);
            return "barEditar";
        } else {
            return "redirect:/bares";
        }
    }

    @PostMapping("/bares/{id}/edit/save")
    public String barEditarGuardar(@PathVariable("id") long id, @ModelAttribute Bar bar) {
        barRepository.actualizarBar(((long) id), bar.getNombre(), bar.getCiudad(), bar.getPresupuesto(),
                bar.getCant_sedes());
        return "redirect:/bares";
    }

    @GetMapping("/bares/{id}/delete")
    public String barEliminar(@PathVariable("id") long id) {
        barRepository.eliminarBar(id);
        return "redirect:/bares";
    }

    @GetMapping("/bares/transferir_1")
    public String transferir_1() {
        baresServicio.transferir_bar_a_bogota(13, "Bogota");
        return "redirect:/bares";
    }

    @GetMapping("/bares/transferir_2")
    public String transferir_2() {
        baresServicio.transferir_bar_a_bogota_2(14, "Bogota");
        return "redirect:/bares";
    }

}
