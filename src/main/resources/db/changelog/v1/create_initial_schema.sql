-- liquibase formatted sql
-- foreign key naming notation : fk_<source_table>_<referenced_table>_<column>

-- changeset mgamal:create-user-table splitStatements:true endDelimiter:;
CREATE TABLE IF NOT EXISTS user
(
    id                  varchar(36)             not null primary key,
    birth_date          date                    null,
    email_address       varchar(255)            null,
    gender              varchar(20)             null CHECK (gender in ('FEMALE', 'MALE')),
    is_account_verified bit                     null,
    name                varchar(255)            null,
    password            varchar(255)            null,
    phone_number        varchar(255)            null,
    profile_image_path  varchar(255)            null,
    role                varchar(20)             null CHECK (role in ('ADMIN', 'USER'))
);

-- changeset mgamal:create-otp-table splitStatements:true endDelimiter:;
CREATE TABLE IF NOT EXISTS otp
(
    id              varchar(36)                               not null primary key,
    code            varchar(255)                              null,
    expiration_time datetime(6)                               null,
    otp_type        varchar(20)                               null CHECK (otp_type in ('RESET_PASSWORD', 'VERIFY_ACCOUNT')),
    is_used         bit                                       null,
    user_id         varchar(36)                               not null,
    CONSTRAINT fk_user_otp_user_id FOREIGN KEY (user_id) REFERENCES user (id)
);

-- changeset mgamal:create-user-password-history-table splitStatements:true endDelimiter:;
CREATE TABLE IF NOT EXISTS user_password_history
(
    id                  bigint auto_increment primary key,
    old_password_hash   varchar(255) null,
    password_changed_at datetime(6)  null,
    user_id             varchar(36)  null,
    CONSTRAINT fk_user_user_password_history_user_id FOREIGN KEY (user_id) REFERENCES user (id)
);
