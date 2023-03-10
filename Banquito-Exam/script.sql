create database products;

create table
    INTEREST_RATE (
        ID_INTEREST_RATE SERIAL not null,
        NAME VARCHAR(50) not null,
        INTEREST_RATE NUMERIC(2, 2) not null,
        STATE VARCHAR(3) not null constraint CKC_STATE_INTEREST check (STATE in ('ACT', 'INA')),
        START_DATE DATE not null,
        END_DATE DATE null,
        constraint PK_INTEREST_RATE primary key (ID_INTEREST_RATE)
    );

create table
    PRODUCT_ACCOUNT (
        ID_PRODUCT_ACCOUNT VARCHAR(16) not null,
        NAME VARCHAR(50) not null,
        DESCRIPTION VARCHAR(500) not null,
        MINIMUN_BALANCE NUMERIC(10, 2) not null,
        PAY_INTEREST VARCHAR(1) not null constraint CKC_PAY_INTEREST_PRODUCT_ check (PAY_INTEREST in ('Y', 'N')),
        ACCEPTS_CHECKS VARCHAR(1) not null constraint CKC_ACCEPTS_CHECKS_PRODUCT_ check (ACCEPTS_CHECKS in ('Y', 'N')),
        STATE VARCHAR(3) not null constraint CKC_STATE_PRODUCT_ check (STATE in ('DRA', 'ACT', 'INA')),
        CREATION_DATE DATE not null,
        constraint PK_PRODUCT_ACCOUNT primary key (ID_PRODUCT_ACCOUNT)
    );

insert into interest_rate
values (
        default,
        'PASIVA CORRIENTE',
        0.0,
        'ACT',
        NOW(),
        null
    );

insert into product_account
values (
        'CORR2023',
        'Cuenta Corriente 2022',
        'Cuenta corriente completamente autoegestionada y digital; no maneja cheques ni paga interes.',
        0.0,
        'N',
        'N',
        'ACT',
        NOW()
    );
