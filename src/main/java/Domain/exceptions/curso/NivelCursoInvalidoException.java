package Domain.exceptions.curso;

// Excepción lanzada cuando el valor del NivelCurso no es uno de los permitidos
public class NivelCursoInvalidoException extends RuntimeException {
  public NivelCursoInvalidoException(String mensaje) {
    super("Nivel de Curso Inválido: " + mensaje);
  }
}
