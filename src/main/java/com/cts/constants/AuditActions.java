package com.cts.constants;

public final class AuditActions {

    private AuditActions() {}

    // User 
    public static final String REGISTER_USER      = "REGISTER_USER";
    public static final String LOGIN_USER         = "LOGIN_USER";

    // Property 
    public static final String CREATE_PROPERTY    = "CREATE_PROPERTY";
    public static final String UPDATE_PROPERTY    = "UPDATE_PROPERTY";
    public static final String DELETE_PROPERTY    = "DELETE_PROPERTY";

    // Unit 
    public static final String CREATE_UNIT        = "CREATE_UNIT";
    public static final String UPDATE_UNIT        = "UPDATE_UNIT";
    public static final String DELETE_UNIT        = "DELETE_UNIT";

    // Amenity 
    public static final String CREATE_AMENITY     = "CREATE_AMENITY";
    public static final String UPDATE_AMENITY     = "UPDATE_AMENITY";
    public static final String DELETE_AMENITY     = "DELETE_AMENITY";

    // Application 
    public static final String CREATE_APPLICATION = "CREATE_APPLICATION";
    public static final String UPDATE_APPLICATION = "UPDATE_APPLICATION";

    // Lease 
    public static final String CREATE_LEASE       = "CREATE_LEASE";
    public static final String UPDATE_LEASE       = "UPDATE_LEASE";
    public static final String TERMINATE_LEASE    = "TERMINATE_LEASE";

    // Tenant Profile 
    public static final String CREATE_TENANT_PROFILE = "CREATE_TENANT_PROFILE";
    public static final String UPDATE_TENANT_PROFILE = "UPDATE_TENANT_PROFILE";

    // Invoice 
    public static final String CREATE_INVOICE     = "CREATE_INVOICE";
    public static final String UPDATE_INVOICE     = "UPDATE_INVOICE";

    // Ledger Entry 
    public static final String CREATE_LEDGER_ENTRY = "CREATE_LEDGER_ENTRY";

    // Work Order / Maintenance 
    public static final String CREATE_WORK_ORDER        = "CREATE_WORK_ORDER";
    public static final String UPDATE_WORK_ORDER        = "UPDATE_WORK_ORDER";
    public static final String CREATE_MAINTENANCE_LOG   = "CREATE_MAINTENANCE_LOG";
    public static final String CREATE_MAINTENANCE_SCHEDULE = "CREATE_MAINTENANCE_SCHEDULE";
    public static final String UPDATE_MAINTENANCE_SCHEDULE = "UPDATE_MAINTENANCE_SCHEDULE";

    // Technician 
    public static final String CREATE_TECHNICIAN  = "CREATE_TECHNICIAN";
    public static final String UPDATE_TECHNICIAN  = "UPDATE_TECHNICIAN";
    public static final String DELETE_TECHNICIAN  = "DELETE_TECHNICIAN";

    // Account Officer 
    public static final String CREATE_ACCOUNT_OFFICER = "CREATE_ACCOUNT_OFFICER";
    public static final String UPDATE_ACCOUNT_OFFICER = "UPDATE_ACCOUNT_OFFICER";

    // Property Photo 
    public static final String UPLOAD_PROPERTY_PHOTO  = "UPLOAD_PROPERTY_PHOTO";
    public static final String DELETE_PROPERTY_PHOTO  = "DELETE_PROPERTY_PHOTO";
}
