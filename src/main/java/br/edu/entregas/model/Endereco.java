package br.edu.entregas.model;

public record Endereco(String logradouro, String complemento, String numero,
                       String cep, String cidade, String estado) {
    @Override
    public String toString() {
        return logradouro + ", " + numero +
               (complemento == null || complemento.isBlank() ? "" : " - " + complemento) +
               ", " + cidade + "/" + estado + " - CEP: " + cep;
    }
}
