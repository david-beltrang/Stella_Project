package Domain.models.PruebaValueObjects;

import Domain.exceptions.Prueba.PuntajeInvalidoException;

public record Puntaje(double valorPuntaje) {
    public Puntaje{
        if (valorPuntaje < 0 || valorPuntaje > 100){
            throw new PuntajeInvalidoException(valorPuntaje);
        }
    }
}
