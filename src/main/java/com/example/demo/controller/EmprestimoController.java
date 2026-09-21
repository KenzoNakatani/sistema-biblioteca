package com.example.demo.controller;

import com.example.demo.model.Emprestimo;
import com.example.demo.model.Usuario;
import com.example.demo.service.EmprestimoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/emprestimos")
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    public EmprestimoController(EmprestimoService emprestimoService) {
        this.emprestimoService = emprestimoService;
    }

    @PostMapping
    public ResponseEntity<Emprestimo> emprestar(@AuthenticationPrincipal Usuario usuario,
                                                  @RequestParam Long livroId) {
        Emprestimo emprestimo = emprestimoService.realizarEmprestimo(usuario, livroId);
        return ResponseEntity.ok(emprestimo);
    }

    @PutMapping("/{id}/devolver")
    public ResponseEntity<Emprestimo> devolver(@PathVariable Long id) {
        Emprestimo emprestimo = emprestimoService.devolverLivro(id);
        return ResponseEntity.ok(emprestimo);
    }
}
