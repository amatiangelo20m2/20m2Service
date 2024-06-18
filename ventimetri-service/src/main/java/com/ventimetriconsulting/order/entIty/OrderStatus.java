package com.ventimetriconsulting.order.entIty;

import java.io.Serializable;

public enum OrderStatus implements Serializable {
    BOZZA,
    INVIATO,
    IN_LAVORAZIONE,
    PRONTO_A_PARTIRE,
    DA_CONFERMARE,
    CONSEGNATO,
    ARCHIVIATO,
    CANCELLATO
}
