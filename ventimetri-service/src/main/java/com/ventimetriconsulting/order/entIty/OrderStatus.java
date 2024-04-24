package com.ventimetriconsulting.order.entIty;

import java.io.Serializable;

public enum OrderStatus implements Serializable {

    DRAFT,
    CREATED,
    SENT,
    ACCEPTED,
    REFUSED,
    ARCHIVED
}
