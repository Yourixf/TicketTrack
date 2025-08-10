-- Insert Roles
INSERT INTO role (id, name, created, last_modified)
VALUES 
    (1, 'ROLE_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'ROLE_IT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 'ROLE_CUSTOMER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO app_user (name, phone_number, email, info, password, created, last_modified, role_id)
VALUES
    ('John Wick', 0612345678, 'johnwick@tickettrack.com', '', '$2a$12$aIf/u0A4fLCytmiyOfxTwuNMoiouRQJWb/BHOlEsoMfByVgyRYP4a', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
    ( 'Tony Stark', 0612345679, 'tonystark@tickettrack.com', '', '$2a$12$aIf/u0A4fLCytmiyOfxTwuNMoiouRQJWb/BHOlEsoMfByVgyRYP4a', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2),
    ( 'Shuri', 0612345680, 'shuri@tickettrack.com', '', '$2a$12$aIf/u0A4fLCytmiyOfxTwuNMoiouRQJWb/BHOlEsoMfByVgyRYP4a', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2),
    ( 'Peter Parker', 0612345681, 'peterparker@tickettrack.com', '', '$2a$12$aIf/u0A4fLCytmiyOfxTwuNMoiouRQJWb/BHOlEsoMfByVgyRYP4a', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3),
    ( 'Ethan Hunt', 0612345682, 'ethanhunt@tickettrack.com', '', '$2a$12$aIf/u0A4fLCytmiyOfxTwuNMoiouRQJWb/BHOlEsoMfByVgyRYP4a', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3);


-- Insert AssignmentGroups
INSERT INTO assignment_group (id, name, email, created, last_modified)
VALUES
    (1, 'First Line Support', 'firstline@company.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'Second Line Support', 'secondline@company.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 'Network Team', 'network@company.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (4, 'Security Team', 'security@company.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (5, 'Cloud Team', 'cloud@company.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert ServiceOfferings (referencing existing assignment_group IDs)
INSERT INTO service_offering (id, name, default_sla_in_days, created, last_modified, assignment_group_id)
VALUES
    (1, 'Email Service', 14, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
    (2, 'VPN Access', 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2),
    (3, 'Network Storage', 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3),
    (4, 'Security Audit', 21, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 4),
    (5, 'Cloud Backup', 30, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5);
