package com.gabrielflorencco.hotel_impact.service;

import com.gabrielflorencco.hotel_impact.dto.CategoriaForm;
import com.gabrielflorencco.hotel_impact.model.CategoriaQuarto;
import com.gabrielflorencco.hotel_impact.exception.NegocioException;
import com.gabrielflorencco.hotel_impact.exception.RegistroNaoEncontradoException;
import com.gabrielflorencco.hotel_impact.repository.CategoriaQuartoRepository;
import com.gabrielflorencco.hotel_impact.repository.QuartoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaQuartoService {

    private final CategoriaQuartoRepository categorias;
    private final QuartoRepository quartos;

    public CategoriaQuartoService(CategoriaQuartoRepository categorias, QuartoRepository quartos) {
        this.categorias = categorias;
        this.quartos = quartos;
    }

    @Transactional(readOnly = true)
    public List<CategoriaQuarto> listar() {
        return categorias.findAllByOrderByNomeAsc();
    }

    @Transactional(readOnly = true)
    public CategoriaQuarto buscarPorId(Long id) {
        return categorias.findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Categoria não encontrada."));
    }

    @Transactional
    public CategoriaQuarto salvar(CategoriaForm form) {
        validarNomeUnico(form.getNome(), form.getId());

        CategoriaQuarto categoria = form.getId() == null
                ? new CategoriaQuarto()
                : buscarPorId(form.getId());
        categoria.setNome(form.getNome().trim());
        categoria.setDescricao(form.getDescricao() == null ? null : form.getDescricao().trim());
        categoria.setCapacidadeMaxima(form.getCapacidadeMaxima());
        categoria.setQuantidadeCamas(form.getQuantidadeCamas());
        categoria.setPrecoBase(form.getPrecoBase());
        categoria.setAtivo(form.getAtivo() != null && form.getAtivo());
        return categorias.save(categoria);
    }

    @Transactional
    public boolean alternarAtivo(Long id) {
        CategoriaQuarto categoria = buscarPorId(id);
        categoria.alternarAtivo();
        return categorias.save(categoria).getAtivo();
    }

    @Transactional
    public void excluir(Long id) {
        CategoriaQuarto categoria = buscarPorId(id);
        long quartosVinculados = quartos.countByCategoriaId(id);
        if (quartosVinculados > 0) {
            throw new NegocioException(
                    "Não é possível excluir a categoria: existem %d quarto(s) utilizando-a. Desative-a para impedir novos vínculos."
                            .formatted(quartosVinculados));
        }
        categorias.delete(categoria);
    }

    private void validarNomeUnico(String nome, Long id) {
        boolean duplicado = id == null
                ? categorias.existsByNomeIgnoreCase(nome.trim())
                : categorias.existsByNomeIgnoreCaseAndIdNot(nome.trim(), id);
        if (duplicado) {
            throw new NegocioException("Já existe uma categoria com o nome informado.");
        }
    }
}