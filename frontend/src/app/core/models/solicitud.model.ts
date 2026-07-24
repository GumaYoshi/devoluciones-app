export type EstadoSolicitud =
  | 'BORRADOR'
  | 'EN_REVISION'
  | 'APROBADA'
  | 'RECHAZADA'
  | 'PAGADA'
  | 'ANULADA';

export type Moneda = 'CLP';

export type OrigenSolicitud = 'MANUAL' | 'CARGA_MASIVA';

export interface Solicitud {
  id: number;
  folio: string;
  rutCliente: string;
  nombreCliente: string;
  monto: number;
  moneda: Moneda;
  bancoDestino: string;
  cuentaDestino: string;
  referenciaBanco: string;
  origen: OrigenSolicitud;
  estado: EstadoSolicitud;
  cantidadReaperturas: number;
  creadaPor: string;
  fechaCreacion: string;
  fechaActualizacion: string | null;
}

export interface CrearSolicitudRequest {
  folio: string;
  rutCliente: string;
  nombreCliente: string;
  monto: number;
  moneda: Moneda;
  bancoDestino: string;
  cuentaDestino: string;
  referenciaBanco: string;
  origen: OrigenSolicitud;
  creadaPor: string;
}

export interface ActualizarSolicitudRequest {
  rutCliente: string;
  nombreCliente: string;
  monto: number;
  moneda: Moneda;
  bancoDestino: string;
  cuentaDestino: string;
}

export interface CambiarEstadoRequest {
  nuevoEstado: EstadoSolicitud;
  usuario: string;
  comentario: string | null;
}

export interface EventoSolicitud {
  id: number;
  estadoOrigen: EstadoSolicitud;
  estadoDestino: EstadoSolicitud;
  usuario: string;
  fecha: string;
  comentario: string | null;
}