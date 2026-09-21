package com.example.demo.repository;

import com.example.demo.model.Emprestimo;
import com.example.demo.model.StatusEmprestimo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {
    List<Emprestimo> findByUsuarioIdAndStatus(Long usuarioId, StatusEmprestimo status);
    long countByUsuarioIdAndStatus(Long usuarioId, StatusEmprestimo status);
}