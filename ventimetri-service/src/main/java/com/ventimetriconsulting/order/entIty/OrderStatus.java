package com.ventimetriconsulting.order.entIty;

import java.io.Serializable;

public enum OrderStatus implements Serializable {

    BOZZA,
    INVIATO,
    PRONTO_A_PARTIRE,
    CONSEGNATO,
    ARCHIVIATO,
    CANCELLATO
}
