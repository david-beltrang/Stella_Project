package Domain.repositoriesInterfaces;

import Application.dtos.internal.IntentoInternal; // Se usa un DTO o entidad Intento para guardar
import Application.dtos.internal.RespuestaInternal; // Se usa un DTO o entidad Respuesta para guardar

// Se asume que Intento y Respuesta son DTOs o Entidades simples para guardar en BD.
public interface InterfazIntentoRepository {

    // Guarda el intento de prueba del usuario y devuelve el ID generado.

    int guardarIntento(IntentoInternal intento);

    // Guarda la respuesta de una pregunta dentro de un intento.
    void guardarRespuesta(RespuestaInternal respuesta);
}