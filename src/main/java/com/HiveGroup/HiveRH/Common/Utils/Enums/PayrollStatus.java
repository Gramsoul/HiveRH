package com.HiveGroup.HiveRH.Common.Utils.Enums;

public enum PayrollStatus {
    DRAFT, //liquidacion armada pero todavía no confirmada. (Borrador, editable)
    CONFIRMED, // Confirmada, ya validada por STAFF
    CANCELLED // Anulada
}
