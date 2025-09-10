module common.simulation {
    requires static lombok;
    requires com.h2database;
    requires java.sql;
    exports dto;
    exports h2;
}