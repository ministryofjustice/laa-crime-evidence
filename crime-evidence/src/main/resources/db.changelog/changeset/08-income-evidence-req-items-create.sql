--liquibase formatted sql
--changeset muthus:08-income-evidence-req-items-create

CREATE TABLE IF NOT EXISTS crime_evidence.INCOME_EVIDENCE_REQ_ITEMS
(
     ID INTEGER,
     IEVR_ID INTEGER,
     INEV_EVIDENCE VARCHAR(20),
     MANDATORY VARCHAR(1),
     DATE_CREATED TIMESTAMP NOT NULL,
     USER_CREATED VARCHAR(100) NOT NULL,
     DATE_MODIFIED TIMESTAMP,
     USER_MODIFIED VARCHAR(100),
     CONSTRAINT IERI_PK PRIMARY KEY (ID),
     CONSTRAINT IERI_IEVR_FK FOREIGN KEY (IEVR_ID)
         REFERENCES crime_evidence.INCOME_EVIDENCE_REQUIRED (ID)
);