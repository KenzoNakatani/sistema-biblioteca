package com.example.demo.service;

import com.example.demo.exception.RegraNegocioException;
import com.example.demo.model.Emprestimo;
import com.example.demo.model.Livro;
import com.example.demo.model.StatusEmprestimo;
import com.example.demo.model.Usuario;
import com.example.demo.repository.EmprestimoRepository;
import com.example.demo.repository.LivroRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class EmprestimoService {

    private static final int LIMITE_EMPRESTIMOS = 3;
    private static final BigDecimal MULTA_POR_DIA = new BigDecimal("2.00");

    private final EmprestimoRepository emprestimoRepository;
    private final LivroRepository livroRepository;

    public EmprestimoService(EmprestimoRepository emprestimoRepository, LivroRepository livroRepository) {
        this.emprestimoRepository = emprestimoRepository;
        this.livroRepository = livroRepository;
    }

    public Emprestimo realizarEmprestimo(Usuario usuario, Long livroId) {
        long emprestimosAtivos = emprestimoRepository
                .countByUsuarioIdAndStatus(usuario.getId(), StatusEmprestimo.ATIVO);

        if (emprestimosAtivos >= LIMITE_EMPRESTIMOS) {
            throw new RegraNegocioException("Limite de " + LIMITE_EMPRESTIMOS + " empréstimos atingido.");
        }

        Livro livro = livroRepository.findById(livroId)
                .orElseThrow(() -> new RegraNegocioException("Livro não encontrado."));

        if (livro.getQuantidadeDisponivel() <= 0) {
            throw new RegraNegocioException("Não há exemplares disponíveis.");
        }

        livro.setQuantidadeDisponivel(livro.getQuantidadeDisponivel() - 1);
        livroRepository.save(livro);

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setUsuario(usuario);
        emprestimo.setLivro(livro);
        emprestimo.setDataEmprestimo(LocalDate.now());
        emprestimo.setDataPrevistaDevolucao(LocalDate.now().plusDays(14));
        emprestimo.setStatus(StatusEmprestimo.ATIVO);

        return emprestimoRepository.save(emprestimo);
    }

    public Emprestimo devolverLivro(Long emprestimoId) {
        Emprestimo emprestimo = emprestimoRepository.findById(emprestimoId)
                .orElseThrow(() -> new RegraNegocioException("Empréstimo não encontrado."));

        emprestimo.setDataDevolucao(LocalDate.now());

        long diasAtraso = ChronoUnit.DAYS.between(
                emprestimo.getDataPrevistaDevolucao(), LocalDate.now());

        if (diasAtraso > 0) {
            BigDecimal multa = MULTA_POR_DIA.multiply(BigDecimal.valueOf(diasAtraso));
            emprestimo.setMulta(multa);
            emprestimo.setStatus(StatusEmprestimo.ATRASADO);
        } else {
            emprestimo.setStatus(StatusEmprestimo.DEVOLVIDO);
        }

        Livro livro = emprestimo.getLivro();
        livro.setQuantidadeDisponivel(livro.getQuantidadeDisponivel() + 1);
        livroRepository.save(livro);

        return emprestimoRepository.save(emprestimo);
    }
}