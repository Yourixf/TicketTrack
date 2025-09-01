-- uniques
CREATE UNIQUE INDEX IF NOT EXISTS ux_role_name               ON role(name);
CREATE UNIQUE INDEX IF NOT EXISTS ux_assignment_group_name   ON assignment_group(name);
CREATE UNIQUE INDEX IF NOT EXISTS ux_service_offering_name   ON service_offering(name);
CREATE UNIQUE INDEX IF NOT EXISTS ux_app_user_email          ON app_user(email);

-- roles
INSERT INTO role (id, name, created, last_modified) VALUES
(1, 'ROLE_ADMIN',    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2,'ROLE_IT',       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'ROLE_CUSTOMER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT (name) DO NOTHING;

-- assignment groups
    INSERT INTO assignment_group (name, email, created, last_modified) VALUES
    ('First Line Support',  'firstline@company.com',  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Second Line Support', 'secondline@company.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Network Team',        'network@company.com',    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Security Team',       'security@company.com',   CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Cloud Team',          'cloud@company.com',      CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT (name) DO NOTHING;

-- service offerings (FK via assignment_group.name)
INSERT INTO service_offering (name, default_sla_in_days, created, last_modified, assignment_group_id) VALUES
      ('Email Service',   14, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, (SELECT id FROM assignment_group WHERE name='First Line Support')),
      ('VPN Access',       7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, (SELECT id FROM assignment_group WHERE name='Second Line Support')),
      ('Network Storage', 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, (SELECT id FROM assignment_group WHERE name='Network Team')),
      ('Security Audit',  21, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, (SELECT id FROM assignment_group WHERE name='Security Team')),
      ('Cloud Backup',    30, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, (SELECT id FROM assignment_group WHERE name='Cloud Team'))
    ON CONFLICT (name) DO NOTHING;

-- users (FK via role.name; telefoon als text)
INSERT INTO app_user (name, phone_number, email, info, password, created, last_modified, role_id) VALUES
      ('John Wick',    '0612345678', 'johnwick@tickettrack.com',   '', '$2a$12$aIf/u0A4fLCytmiyOfxTwuNMoiouRQJWb/BHOlEsoMfByVgyRYP4a', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, (SELECT id FROM role WHERE name='ROLE_ADMIN')),
      ('Natasha Romanoff', '0612345678', 'natasharomanoff@tickettrack.com', '', '$2a$12$aIf/u0A4fLCytmiyOfxTwuNMoiouRQJWb/BHOlEsoMfByVgyRYP4a', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, (SELECT id FROM role WHERE name='ROLE_ADMIN')),
      ('Tony Stark',   '0612345679', 'tonystark@tickettrack.com',  '', '$2a$12$aIf/u0A4fLCytmiyOfxTwuNMoiouRQJWb/BHOlEsoMfByVgyRYP4a', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, (SELECT id FROM role WHERE name='ROLE_IT')),
      ('Shuri',        '0612345680', 'shuri@tickettrack.com',      '', '$2a$12$aIf/u0A4fLCytmiyOfxTwuNMoiouRQJWb/BHOlEsoMfByVgyRYP4a', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, (SELECT id FROM role WHERE name='ROLE_IT')),
      ('Peter Parker', '0612345681', 'peterparker@tickettrack.com','', '$2a$12$aIf/u0A4fLCytmiyOfxTwuNMoiouRQJWb/BHOlEsoMfByVgyRYP4a', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, (SELECT id FROM role WHERE name='ROLE_CUSTOMER')),
      ('Ethan Hunt',   '0612345682', 'ethanhunt@tickettrack.com',  '', '$2a$12$aIf/u0A4fLCytmiyOfxTwuNMoiouRQJWb/BHOlEsoMfByVgyRYP4a', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, (SELECT id FROM role WHERE name='ROLE_CUSTOMER'))
    ON CONFLICT (email) DO NOTHING;
