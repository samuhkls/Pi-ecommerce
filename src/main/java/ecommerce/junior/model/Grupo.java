package ecommerce.junior.model;

public enum Grupo {
    ESTOQUISTA(0),
    ADMINISTRADOR(1);

    private final int valor;

    Grupo(int valor) {
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }

    public static Grupo fromValor(int valor) {
        for (Grupo tipo : Grupo.values()) {
            if (tipo.getValor() == valor) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Valor inválido: " + valor);
    }
}

