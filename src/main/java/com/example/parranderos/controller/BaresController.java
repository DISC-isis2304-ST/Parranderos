package com.example.parranderos.controller;

import java.util.Collection;
import java.util.List;

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

//Import usado para que no falle en caso de rollback
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


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

        int retryCount = 0; // Contador de reintentos

        while (true) {
            try {
                if ((ciudad == null || ciudad.equals("")) || (tipo == null || tipo.equals(""))) {
                    System.out.println("default search");
                    model.addAttribute("bares", baresServicio.consultarBaresSinFantasma());
                } else {
                    System.out.println(barRepository.darBaresPorCiudadYTipoBebida(ciudad, tipo));
                    model.addAttribute("bares", barRepository.darBaresPorCiudadYTipoBebida(ciudad, tipo));
                }
                break; // Si se completa con éxito, salir del bucle
            } catch (Exception e) {
                System.out.println("Intento " + (retryCount + 1) + ": " + e);
            }
        }

        return "bares";
    }

    @GetMapping("/bares/fantasma")
    public String baresFantasma(Model model, String ciudad, String tipo) {
        int NumeroDeBaresQueSirvenBebidasConMayorGradoAlcohol = barRepository
                .darNumeroDeBaresQueSirvenBebidasConMayorGradoAlcohol();
        int NumeroDeBaresQueSirvenBebidasConMenorGradoAlcohol = barRepository
                .darNumeroDeBaresQueSirvenBebidasConMenorGradoAlcohol();
        model.addAttribute("NumeroDeBaresQueSirvenBebidasConMayorGradoAlcohol",
                NumeroDeBaresQueSirvenBebidasConMayorGradoAlcohol);
        model.addAttribute("NumeroDeBaresQueSirvenBebidasConMenorGradoAlcohol",
                NumeroDeBaresQueSirvenBebidasConMenorGradoAlcohol);
        model.addAttribute("tipos", tipo_bebidaRepository.darTipos_bebida());

        int retryCount = 0; // Contador de reintentos

        while (true) {
            try {
                if ((ciudad == null || ciudad.equals("")) || (tipo == null || tipo.equals(""))) {
                    System.out.println("default search");
                    model.addAttribute("bares", baresServicio.consultarBaresConFantasma());
                } else {
                    System.out.println(barRepository.darBaresPorCiudadYTipoBebida(ciudad, tipo));
                    model.addAttribute("bares", barRepository.darBaresPorCiudadYTipoBebida(ciudad, tipo));
                }
                break; // Si se completa con éxito, salir del bucle
            } catch (Exception e) {
                System.out.println("Intento " + (retryCount + 1) + ": " + e);
            }
        }

        return "redirect:/bares";
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

    // Método que inicia la transferencia de un bar específico a Bogotá.
    // Espera 20 segundos antes de completar la operación para simular una tarea de larga duración,
    // pero no implementa manejo de errores ni rollback.
    @GetMapping("/bares/transferir_bar")
    public String transferir_1() {
        Collection<Bar> bares = barRepository.darBares();
        if (!bares.isEmpty()) {
            Bar primerBar = bares.iterator().next(); // Obtiene el primer bar de la colección
            baresServicio.transferir_bar(primerBar.getId(), "Bogota");
            System.out.println("El bar transferido es: " + primerBar.getNombre());
            System.out.println("Su ID es: " + primerBar.getId());
        }
        return "redirect:/bares";
    }

    // Método que intenta transferir un bar a Bogotá pero está diseñado para forzar un error después de eliminar el bar.
    // Utiliza manejo de errores para capturar cualquier excepción, mostrando un mensaje al usuario si algo falla.
    // Proporciona una demostración de rollback: si falla, la transacción se revierte para mantener la integridad de los datos.
    @GetMapping("/bares/transferir_rollback")
    public String transferir_3(RedirectAttributes redirectAttributes) {
        Collection<Bar> bares = barRepository.darBares();
        if (!bares.isEmpty()) {
            try {
                Bar primerBar = bares.iterator().next(); // Obtiene el primer bar de la colección
                System.out.println("El bar transferido es: " + primerBar.getNombre());
                System.out.println("Su ID es: " + primerBar.getId());
                baresServicio.transferir_bar_rollback(primerBar.getId(), "Bogota");
            } catch (Exception e) {
                System.err.println("Error al transferir el bar: " + e.getMessage());
                redirectAttributes.addFlashAttribute("errorMessage", "No se pudo completar la transferencia del bar a Bogotá.");
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "No hay bares disponibles para transferir.");
        }
        return "redirect:/bares";
    }    

    // Método que consulta un bar específico y bloquea las lecturas de otros registros mientras se ejecuta,
    // simulando una operación larga (20 segundos) para probar el comportamiento de bloqueo en la base de datos.
    @GetMapping("/bares/bloqueo")
    public String consultarBaresSinFantasma(RedirectAttributes redirectAttributes) {
        try {
            // Indicar el ID del bar a consultar.
            baresServicio.consultarBaresSinFantasma();
        } catch (Exception e) {
            System.err.println("Error durante la consulta de bares: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "No se pudo consultar los bares correctamente.");
            return "redirect:/bares";
        }
        return "redirect:/bares";
    }


}
