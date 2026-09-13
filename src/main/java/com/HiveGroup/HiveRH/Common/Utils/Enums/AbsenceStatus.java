package com.HiveGroup.HiveRH.Common.Utils.Enums;

public enum AbsenceStatus {
    PENDING,    // Solicitud creada, pendiente de revisión
    APPROVED,   // Solicitud aprobada por STAFF
    REJECTED,   // Solicitud rechazada por STAFF
    CANCELLED   // Solicitud cancelada por el empleado antes de ser aprobada
}
