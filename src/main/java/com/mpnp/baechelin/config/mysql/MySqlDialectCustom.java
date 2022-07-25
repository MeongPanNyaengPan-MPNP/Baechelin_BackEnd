package com.mpnp.baechelin.config.mysql;

import org.hibernate.dialect.MySQL57Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StandardBasicTypes;

public class MySqlDialectCustom extends MySQL57Dialect {

    public MySqlDialectCustom() {
        registerFunction(
                "match",
                new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "match(?1, ?2) against (?3 in boolean mode)")
        );
    }
}
