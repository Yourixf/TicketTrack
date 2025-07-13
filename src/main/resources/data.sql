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

-- Insert 5 Interactions
-- INSERT INTO interaction (
--     id, number, created, last_modified, short_description, description,
--     category, state, channel, service_offering_id, assignment_group_id
-- ) VALUES
--       (1, 'IMS0000001', '2025-07-09T10:00:00', '2025-07-09T10:00:00', 'Laptop werkt niet', 'Gebruiker kan zijn laptop niet opstarten', 'INCIDENT', 'NEW', 'PHONE', 1, 1),
--       (2, 'IMS0000002', '2025-07-09T10:05:00', '2025-07-09T10:05:00', 'Email problemen', 'Outlook blijft hangen bij verzenden', 'REQUEST', 'NEW', 'EMAIL', 2, 1),
--       (3, 'IMS0000003', '2025-07-09T10:10:00', '2025-07-09T10:10:00', 'VPN faalt', 'Verbinding valt elke 10 minuten weg', 'INCIDENT', 'NEW', 'CHAT', 3, 2),
--       (4, 'IMS0000004', '2025-07-09T10:15:00', '2025-07-09T10:15:00', 'Beeldscherm flikkert', 'Mogelijk kabelprobleem of hardwarefout', 'INCIDENT', 'NEW', 'WALK_IN', 4, 2),
--       (5, 'IMS0000005', '2025-07-09T10:20:00', '2025-07-09T10:20:00', 'Toegang geweigerd', 'Gebruiker kan niet inloggen op bedrijfsnetwerk', 'PASSWORD_RESET', 'NEW', 'SELF_SERVICE', 5, 3);
