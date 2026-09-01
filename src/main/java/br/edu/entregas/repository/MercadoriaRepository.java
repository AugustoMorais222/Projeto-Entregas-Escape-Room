package br.edu.entregas.repository;

import br.edu.entregas.model.Mercadoria;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MercadoriaRepository {
    private final List<Mercadoria> dados = new ArrayList<>();
    public void salvar(Mercadoria mercadoria) { dados.add(mercadoria); }
    public List<Mercadoria> listar() { return Collections.unmodifiableList(dados); }
}
