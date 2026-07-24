export type EstadoCarga =
  | 'PENDIENTE'
  | 'PROCESANDO'
  | 'COMPLETADA'
  | 'COMPLETADA_CON_ERRORES'
  | 'FALLIDA';

export interface CargaMasiva {
  id: number;
  nombreArchivo: string;
  estado: EstadoCarga;
  totalRegistros: number;
  registrosExitosos: number;
  registrosConError: number;
  fechaInicio: string;
  fechaFin: string | null;
}

export interface ErrorCarga {
  id: number;
  numeroFila: number;
  contenidoFila: string;
  mensajeError: string;
}