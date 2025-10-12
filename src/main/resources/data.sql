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

-- users
INSERT INTO app_user (name, phone_number, email, info, password, created, last_modified, role_id)
VALUES
    ('John Wick',    '0612345678', 'johnwick@tickettrack.com',   '', '$2a$12$aIf/u0A4fLCytmiyOfxTwuNMoiouRQJWb/BHOlEsoMfByVgyRYP4a', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, (SELECT id FROM role WHERE name='ROLE_ADMIN')),
    ('Natasha Romanoff', '0612345678', 'natasharomanoff@tickettrack.com', '', '$2a$12$aIf/u0A4fLCytmiyOfxTwuNMoiouRQJWb/BHOlEsoMfByVgyRYP4a', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, (SELECT id FROM role WHERE name='ROLE_ADMIN')),
    ('Tony Stark',   '0612345679', 'tonystark@tickettrack.com',  '', '$2a$12$aIf/u0A4fLCytmiyOfxTwuNMoiouRQJWb/BHOlEsoMfByVgyRYP4a', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, (SELECT id FROM role WHERE name='ROLE_IT')),
    ('Shuri',        '0612345680', 'shuri@tickettrack.com',      '', '$2a$12$aIf/u0A4fLCytmiyOfxTwuNMoiouRQJWb/BHOlEsoMfByVgyRYP4a', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, (SELECT id FROM role WHERE name='ROLE_IT')),
    ('Peter Parker', '0612345681', 'peterparker@tickettrack.com','', '$2a$12$aIf/u0A4fLCytmiyOfxTwuNMoiouRQJWb/BHOlEsoMfByVgyRYP4a', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, (SELECT id FROM role WHERE name='ROLE_CUSTOMER')),
    ('Ethan Hunt',   '0612345682', 'ethanhunt@tickettrack.com',  '', '$2a$12$aIf/u0A4fLCytmiyOfxTwuNMoiouRQJWb/BHOlEsoMfByVgyRYP4a', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, (SELECT id FROM role WHERE name='ROLE_CUSTOMER'))
    ON CONFLICT (email) DO NOTHING;


-- assignment groups
    INSERT INTO assignment_group (name, email, created, last_modified, created_by_id) VALUES
    ('First Line Support',  'firstline@company.com',  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, (SELECT id FROM app_user WHERE name='John Wick')),
    ('Second Line Support', 'secondline@company.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, (SELECT id FROM app_user WHERE name='John Wick')),
    ('Network Team',        'network@company.com',    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, (SELECT id FROM app_user WHERE name='John Wick')),
    ('Security Team',       'security@company.com',   CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, (SELECT id FROM app_user WHERE name='Natasha Romanoff')),
    ('Cloud Team',          'cloud@company.com',      CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, (SELECT id FROM app_user WHERE name='Natasha Romanoff'))
    ON CONFLICT (name) DO NOTHING;

-- service offerings (FK via assignment_group.name)
INSERT INTO service_offering (name, default_sla_in_days, created, last_modified, assignment_group_id, created_by_id) VALUES
      ('Email Service',   14, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, (SELECT id FROM assignment_group WHERE name='First Line Support'), (SELECT id FROM app_user WHERE name='John Wick')),
      ('VPN Access',       7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, (SELECT id FROM assignment_group WHERE name='Second Line Support'), (SELECT id FROM app_user WHERE name='John Wick')),
      ('Network Storage', 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, (SELECT id FROM assignment_group WHERE name='Network Team'),  (SELECT id FROM app_user WHERE name='John Wick')),
      ('Security Audit',  21, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, (SELECT id FROM assignment_group WHERE name='Security Team'), (SELECT id FROM app_user WHERE name='Natasha Romanoff')),
      ('Cloud Backup',    30, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, (SELECT id FROM assignment_group WHERE name='Cloud Team'), (SELECT id FROM app_user WHERE name='Natasha Romanoff'))
    ON CONFLICT (name) DO NOTHING;

INSERT INTO interaction (number, created, closed, last_modified, short_description, description, category, state, channel, service_offering_id, assignment_group_id, opened_by_id, opened_for_id, closed_by_id, incident_id)
VALUES
    ('IMS0000001', CURRENT_TIMESTAMP - INTERVAL '10 days', NULL, CURRENT_TIMESTAMP,
     'E-mailprobleem', 'Gebruiker meldt terugkerende bounce-berichten.', 'INCIDENT', 'NEW', 'EMAIL',
     (SELECT id FROM service_offering WHERE name='Email Service'),
     (SELECT id FROM assignment_group  WHERE name='First Line Support'),
     (SELECT id FROM app_user WHERE email='tonystark@tickettrack.com'),
     (SELECT id FROM app_user WHERE email='peterparker@tickettrack.com'),
     NULL, NULL),

    ('IMS0000002', CURRENT_TIMESTAMP - INTERVAL '6 days', NULL, CURRENT_TIMESTAMP,
     'VPN-aanvraag', 'Gebruiker vraagt om een nieuw VPN-token.', 'REQUEST', 'NEW', 'PHONE',
     (SELECT id FROM service_offering WHERE name='VPN Access'),
     (SELECT id FROM assignment_group  WHERE name='Second Line Support'),
     (SELECT id FROM app_user WHERE email='shuri@tickettrack.com'),
     (SELECT id FROM app_user WHERE email='ethanhunt@tickettrack.com'),
     NULL, NULL),

    ('IMS0000003', CURRENT_TIMESTAMP - INTERVAL '3 days', NULL, CURRENT_TIMESTAMP,
     'Toegang tot gedeelde map', 'Gebruiker heeft schrijfrechten op projectshare nodig.', 'INCIDENT', 'NEW', 'CHAT',
     (SELECT id FROM service_offering WHERE name='Network Storage'),
     (SELECT id FROM assignment_group  WHERE name='Network Team'),
     (SELECT id FROM app_user WHERE email='tonystark@tickettrack.com'),
     (SELECT id FROM app_user WHERE email='peterparker@tickettrack.com'),
     NULL, NULL),

    ('IMS0000004', CURRENT_TIMESTAMP - INTERVAL '1 days', NULL, CURRENT_TIMESTAMP,
     'Veiligheidszorg', 'Verdachte inlogpogingen op gebruikersaccount gesignaleerd.', 'GENERAL_INQUIRY', 'NEW', 'EMAIL',
     (SELECT id FROM service_offering WHERE name='Security Audit'),
     (SELECT id FROM assignment_group  WHERE name='Security Team'),
     (SELECT id FROM app_user WHERE email='natasharomanoff@tickettrack.com'),
     (SELECT id FROM app_user WHERE email='peterparker@tickettrack.com'),
     NULL, NULL),

-- 3 CLOSED (zonder incident)
    ('IMS0000005', CURRENT_TIMESTAMP - INTERVAL '14 days', CURRENT_TIMESTAMP - INTERVAL '13 days', CURRENT_TIMESTAMP,
     'Wachtwoord reset', 'Gebruiker verzoekt reset van het accountwachtwoord.', 'PASSWORD_RESET', 'CLOSED', 'PHONE',
     (SELECT id FROM service_offering WHERE name='Email Service'),
     (SELECT id FROM assignment_group  WHERE name='First Line Support'),
     (SELECT id FROM app_user WHERE email='tonystark@tickettrack.com'),
     (SELECT id FROM app_user WHERE email='ethanhunt@tickettrack.com'),
     (SELECT id FROM app_user WHERE email='tonystark@tickettrack.com'), NULL),

    ('IMS0000006', CURRENT_TIMESTAMP - INTERVAL '9 days', CURRENT_TIMESTAMP - INTERVAL '8 days', CURRENT_TIMESTAMP,
     'Wi-Fi verbindingsprobleem', 'Gebruiker kan niet verbinden met het wifi-netwerk.', 'USER_ASSISTANCE', 'CLOSED', 'WALK_IN',
     (SELECT id FROM service_offering WHERE name='Network Storage'),
     (SELECT id FROM assignment_group  WHERE name='Network Team'),
     (SELECT id FROM app_user WHERE email='shuri@tickettrack.com'),
     (SELECT id FROM app_user WHERE email='peterparker@tickettrack.com'),
     (SELECT id FROM app_user WHERE email='shuri@tickettrack.com'), NULL),

    ('IMS0000007', CURRENT_TIMESTAMP - INTERVAL '7 days', CURRENT_TIMESTAMP - INTERVAL '6 days', CURRENT_TIMESTAMP,
     'Informatievraag', 'Gebruiker heeft een vraag over account- of service-informatie.', 'GENERAL_INQUIRY', 'CLOSED', 'EMAIL',
     (SELECT id FROM service_offering WHERE name='Cloud Backup'),
     (SELECT id FROM assignment_group  WHERE name='Cloud Team'),
     (SELECT id FROM app_user WHERE email='johnwick@tickettrack.com'),
     (SELECT id FROM app_user WHERE email='ethanhunt@tickettrack.com'),
     (SELECT id FROM app_user WHERE email='johnwick@tickettrack.com'), NULL),

-- 3 escaleren later naar incident
    ('IMS0000008', CURRENT_TIMESTAMP - INTERVAL '12 days', NULL, CURRENT_TIMESTAMP,
     'E-mailstoring', 'Uitgaande e-mail komt terug met bounce/MX-fouten.', 'INCIDENT', 'IN_PROGRESS', 'EMAIL',
     (SELECT id FROM service_offering WHERE name='Email Service'),
     (SELECT id FROM assignment_group  WHERE name='First Line Support'),
     (SELECT id FROM app_user WHERE email='tonystark@tickettrack.com'),
     (SELECT id FROM app_user WHERE email='peterparker@tickettrack.com'),
     NULL, NULL),

    ('IMS0000009', CURRENT_TIMESTAMP - INTERVAL '8 days', NULL, CURRENT_TIMESTAMP,
     'VPN-verbinding valt weg', 'Gebruiker ervaart verbroken VPN-verbindingen met tussenpozen.', 'INCIDENT', 'IN_PROGRESS', 'PHONE',
     (SELECT id FROM service_offering WHERE name='VPN Access'),
     (SELECT id FROM assignment_group  WHERE name='Second Line Support'),
     (SELECT id FROM app_user WHERE email='shuri@tickettrack.com'),
     (SELECT id FROM app_user WHERE email='ethanhunt@tickettrack.com'),
     NULL, NULL),

    ('IMS0000010', CURRENT_TIMESTAMP - INTERVAL '5 days', NULL, CURRENT_TIMESTAMP,
     'Schrijfrechten gedeelde map', 'Gebruiker kan niet schrijven naar de gedeelde map.', 'INCIDENT', 'ON_HOLD', 'SELF_SERVICE',
     (SELECT id FROM service_offering WHERE name='Network Storage'),
     (SELECT id FROM assignment_group  WHERE name='Network Team'),
     (SELECT id FROM app_user WHERE email='peterparker@tickettrack.com'),
     (SELECT id FROM app_user WHERE email='peterparker@tickettrack.com'),
     NULL, NULL),

    ('IMS0000011', CURRENT_TIMESTAMP - INTERVAL '2 days', NULL, CURRENT_TIMESTAMP,
     'Beveiligingsalerts', 'Meerdere mislukte inlogpogingen vanaf onbekende IP-adressen.', 'INCIDENT', 'IN_PROGRESS', 'EMAIL',
     (SELECT id FROM service_offering WHERE name='Security Audit'),
     (SELECT id FROM assignment_group  WHERE name='Security Team'),
     (SELECT id FROM app_user WHERE email='natasharomanoff@tickettrack.com'),
     (SELECT id FROM app_user WHERE email='peterparker@tickettrack.com'),
     NULL, NULL),

    ('IMS0000012', CURRENT_TIMESTAMP - INTERVAL '5 days', NULL, CURRENT_TIMESTAMP,
     'Back-upquota overschreden', 'Dagelijkse back-up mislukt door overschreden opslagquotum.', 'INCIDENT', 'IN_PROGRESS', 'SELF_SERVICE',
     (SELECT id FROM service_offering WHERE name='Cloud Backup'),
     (SELECT id FROM assignment_group  WHERE name='Cloud Team'),
     (SELECT id FROM app_user WHERE email='johnwick@tickettrack.com'),
     (SELECT id FROM app_user WHERE email='ethanhunt@tickettrack.com'),
     NULL, NULL);


INSERT INTO incident
(number, created, last_modified, resolved, resolved_by_id, resolved_reason, canceled, canceled_reason, canceled_by_id,
 on_hold_since, on_hold_reason, resolve_before,
 short_description, description, category, state, channel, priority,
 service_offering_id, assignment_group_id, opened_by_id, opened_for_id, escalated_from_id)
VALUES
-- 1) Van IMS0000008  -> Email Service (14), HIGH => 12 dagen
    ('INC0000001',
    CURRENT_TIMESTAMP - INTERVAL '12 days', CURRENT_TIMESTAMP,
    NULL, NULL,
     null,
     NULL, NULL, NULL,
    NULL, NULL,
    (CURRENT_TIMESTAMP - INTERVAL '12 days') + INTERVAL '12 days',
    'E-mailstoring – MX-fouten', 'Mailflow verstoord; berichten geweigerd door MX-records.', 'INCIDENT', 'IN_PROGRESS', 'EMAIL', 'HIGH',
    (SELECT id FROM service_offering WHERE name='Email Service'),
    (SELECT id FROM assignment_group  WHERE name='First Line Support'),
    (SELECT id FROM app_user WHERE email='tonystark@tickettrack.com'),
    (SELECT id FROM app_user WHERE email='peterparker@tickettrack.com'),
    (SELECT id FROM interaction WHERE number='IMS0000008')
    ),

-- 2) Van IMS0000009  -> VPN (7), NORMAL => 7 dagen
    ('INC0000002',
    CURRENT_TIMESTAMP - INTERVAL '8 days', CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP - INTERVAL '7 days', (SELECT id FROM app_user WHERE email='shuri@tickettrack.com'),
     'SERVICE_RESTORED',
    NULL, NULL, NULL,
    NULL, NULL,
    (CURRENT_TIMESTAMP - INTERVAL '8 days') + INTERVAL '7 days',
    'VPN-verbindingsproblemen', 'Client verliest verbinding met tussenpozen.', 'INCIDENT', 'RESOLVED', 'PHONE', 'NORMAL',
    (SELECT id FROM service_offering WHERE name='VPN Access'),
    (SELECT id FROM assignment_group  WHERE name='Second Line Support'),
    (SELECT id FROM app_user WHERE email='shuri@tickettrack.com'),
    (SELECT id FROM app_user WHERE email='ethanhunt@tickettrack.com'),
    (SELECT id FROM interaction WHERE number='IMS0000009')
    ),

-- 3) Van IMS0000010  -> Network Storage (10), LOW => 13 dagen
    ('INC0000003',
    CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP,
    NULL, NULL, NULL,
     NULL, NULL, NULL,
    CURRENT_TIMESTAMP - INTERVAL '4 days', 'AWAITING_THIRD_PARTY',
    (CURRENT_TIMESTAMP - INTERVAL '5 days') + INTERVAL '13 days',
    'Schrijfrechten op gedeelde map', 'Gebruiker kan niet schrijven naar projectshare; wijziging vereist.', 'INCIDENT', 'ON_HOLD', 'SELF_SERVICE', 'LOW',
    (SELECT id FROM service_offering WHERE name='Network Storage'),
    (SELECT id FROM assignment_group  WHERE name='Network Team'),
    (SELECT id FROM app_user WHERE email='peterparker@tickettrack.com'),
    (SELECT id FROM app_user WHERE email='peterparker@tickettrack.com'),
    (SELECT id FROM interaction WHERE number='IMS0000010')
    ),

-- 4) Van IMS0000011  -> Security Audit (21), HIGH => 19 dagen
    ('INC0000004',
    CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP,
    NULL, NULL,      NULL,
     NULL, NULL, NULL,
    NULL, NULL,
    (CURRENT_TIMESTAMP - INTERVAL '2 days') + INTERVAL '19 days',
    'Beveiligingsalerts', 'Meerdere mislukte inlogpogingen vanaf onbekende IP-adressen.', 'INCIDENT', 'NEW', 'EMAIL', 'HIGH',
    (SELECT id FROM service_offering WHERE name='Security Audit'),
    (SELECT id FROM assignment_group  WHERE name='Security Team'),
    (SELECT id FROM app_user WHERE email='natasharomanoff@tickettrack.com'),
    (SELECT id FROM app_user WHERE email='peterparker@tickettrack.com'),
    (SELECT id FROM interaction WHERE number='IMS0000011')
    ),

-- 5) Van IMS0000012  -> Cloud Backup (30), NORMAL => 30 dagen
    ('INC0000005',
    CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP,
    NULL, NULL,NULL,
     CURRENT_TIMESTAMP - INTERVAL '4 days', 'OUT_OF_SCOPE', (SELECT id FROM app_user WHERE email='johnwick@tickettrack.com'),
    NULL, NULL,
    (CURRENT_TIMESTAMP - INTERVAL '5 days') + INTERVAL '30 days',
    'Back-upquota overschreden', 'Dagelijkse back-up mislukt door overschreden opslagquotum.', 'INCIDENT', 'CANCELED', 'SELF_SERVICE', 'NORMAL',
    (SELECT id FROM service_offering WHERE name='Cloud Backup'),
    (SELECT id FROM assignment_group  WHERE name='Cloud Team'),
    (SELECT id FROM app_user WHERE email='johnwick@tickettrack.com'),
    (SELECT id FROM app_user WHERE email='ethanhunt@tickettrack.com'),
    (SELECT id FROM interaction WHERE number='IMS0000012')
    );


UPDATE interaction
SET state = 'CLOSED',
    closed = CURRENT_TIMESTAMP - INTERVAL '11 days',
    last_modified = CURRENT_TIMESTAMP,
    closed_by_id = (SELECT id FROM app_user WHERE email='tonystark@tickettrack.com'),
    incident_id  = (SELECT id FROM incident WHERE number='INC0000001')
WHERE number = 'IMS0000008';

UPDATE interaction
SET state = 'CLOSED',
    closed = CURRENT_TIMESTAMP - INTERVAL '7 days',
    last_modified = CURRENT_TIMESTAMP,
    closed_by_id = (SELECT id FROM app_user WHERE email='shuri@tickettrack.com'),
    incident_id  = (SELECT id FROM incident WHERE number='INC0000002')
WHERE number = 'IMS0000009';

UPDATE interaction
SET state = 'CLOSED',
    closed = CURRENT_TIMESTAMP - INTERVAL '4 days',
    last_modified = CURRENT_TIMESTAMP,
    closed_by_id = (SELECT id FROM app_user WHERE email='tonystark@tickettrack.com'),
    incident_id  = (SELECT id FROM incident WHERE number='INC0000003')
WHERE number = 'IMS0000010';

UPDATE interaction
SET state = 'CLOSED',
    closed = CURRENT_TIMESTAMP - INTERVAL '1 days',
    last_modified = CURRENT_TIMESTAMP,
    closed_by_id = (SELECT id FROM app_user WHERE email='natasharomanoff@tickettrack.com'),
    incident_id  = (SELECT id FROM incident WHERE number='INC0000004')
WHERE number = 'IMS0000011';

UPDATE interaction
SET state = 'CLOSED',
    closed = CURRENT_TIMESTAMP - INTERVAL '4 days',
    last_modified = CURRENT_TIMESTAMP,
    closed_by_id = (SELECT id FROM app_user WHERE email='johnwick@tickettrack.com'),
    incident_id  = (SELECT id FROM incident WHERE number='INC0000005')
WHERE number = 'IMS0000012';

-- INTERACTION NOTES

-- I1: IMS0000001 (NEW; created 10d ago; geen closed)
INSERT INTO note (content, created, noteable_type, noteable_id, visibility, created_by_id)
VALUES
   ('Gebruiker meldt bounces en voorbeeld-mails bijgevoegd', CURRENT_TIMESTAMP - INTERVAL '9 days 20 hours',
    'Interaction', (SELECT id FROM interaction WHERE number='IMS0000001'), 'PUBLIC',
    (SELECT id FROM app_user WHERE email='peterparker@tickettrack.com')),
   ('Onderzoek gestart op mailflow en MX-logs', CURRENT_TIMESTAMP - INTERVAL '9 days',
    'Interaction', (SELECT id FROM interaction WHERE number='IMS0000001'), 'WORK',
    (SELECT id FROM app_user WHERE email='tonystark@tickettrack.com'));

-- I2: IMS0000006 (CLOSED; window: 9d ago → 8d ago)
INSERT INTO note (content, created, noteable_type, noteable_id, visibility, created_by_id)
VALUES
   ('Vraag over Wi-Fi beantwoord; verbinding werkt weer', CURRENT_TIMESTAMP - INTERVAL '8 days 20 hours',
    'Interaction', (SELECT id FROM interaction WHERE number='IMS0000006'), 'PUBLIC',
    (SELECT id FROM app_user WHERE email='peterparker@tickettrack.com')),
   ('Handleiding gestuurd en verificatie gedaan', CURRENT_TIMESTAMP - INTERVAL '8 days 12 hours',
    'Interaction', (SELECT id FROM interaction WHERE number='IMS0000006'), 'WORK',
    (SELECT id FROM app_user WHERE email='shuri@tickettrack.com'));

-- I3: IMS0000008 (CLOSED na escalatie; window: 12d ago → 11d ago)
INSERT INTO note (content, created, noteable_type, noteable_id, visibility, created_by_id)
VALUES
   ('Mails komen terug; impact op meerdere gebruikers', CURRENT_TIMESTAMP - INTERVAL '11 days 20 hours',
    'Interaction', (SELECT id FROM interaction WHERE number='IMS0000008'), 'PUBLIC',
    (SELECT id FROM app_user WHERE email='peterparker@tickettrack.com')),
   ('Ticket geëscaleerd naar incident voor verdere analyse', CURRENT_TIMESTAMP - INTERVAL '11 days 12 hours',
    'Interaction', (SELECT id FROM interaction WHERE number='IMS0000008'), 'WORK',
    (SELECT id FROM app_user WHERE email='tonystark@tickettrack.com'));

-- I4: IMS0000010 (CLOSED na escalatie; window: 5d ago → 4d ago)
INSERT INTO note (content, created, noteable_type, noteable_id, visibility, created_by_id)
VALUES
   ('Geen schrijfrechten op gedeelde map', CURRENT_TIMESTAMP - INTERVAL '4 days 20 hours',
    'Interaction', (SELECT id FROM interaction WHERE number='IMS0000010'), 'PUBLIC',
    (SELECT id FROM app_user WHERE email='peterparker@tickettrack.com')),
   ('Permissiecontrole uitgevoerd; wacht op netwerkteam', CURRENT_TIMESTAMP - INTERVAL '4 days 10 hours',
    'Interaction', (SELECT id FROM interaction WHERE number='IMS0000010'), 'WORK',
    (SELECT id FROM app_user WHERE email='tonystark@tickettrack.com'));

-- I5: IMS0000012 (CLOSED; window: 5d ago → 4d ago)
INSERT INTO note (content, created, noteable_type, noteable_id, visibility, created_by_id)
VALUES
    ('Back-up melding ontvangen bij dagelijkse run', CURRENT_TIMESTAMP - INTERVAL '4 days 23 hours',
    'Interaction', (SELECT id FROM interaction WHERE number='IMS0000012'), 'PUBLIC',
    (SELECT id FROM app_user WHERE email='ethanhunt@tickettrack.com')),
    ('Quota geverifieerd; voorstel gedaan voor opschoning', CURRENT_TIMESTAMP - INTERVAL '4 days 12 hours',
    'Interaction', (SELECT id FROM interaction WHERE number='IMS0000012'), 'WORK',
    (SELECT id FROM app_user WHERE email='johnwick@tickettrack.com'));


-- INCIDENTS NOTES

-- C1: INC0000001 (IN_PROGRESS; created 12d ago; geen end)
INSERT INTO note (content, created, noteable_type, noteable_id, visibility, created_by_id)
VALUES
   ('Storing merkbaar bij meerdere mailboxen', CURRENT_TIMESTAMP - INTERVAL '11 days 23 hours',
    'Incident', (SELECT id FROM incident WHERE number='INC0000001'), 'PUBLIC',
    (SELECT id FROM app_user WHERE email='peterparker@tickettrack.com')),
   ('MX-records en mailgateway worden doorgelicht', CURRENT_TIMESTAMP - INTERVAL '11 days 12 hours',
    'Incident', (SELECT id FROM incident WHERE number='INC0000001'), 'WORK',
    (SELECT id FROM app_user WHERE email='tonystark@tickettrack.com'));

-- C2: INC0000002 (RESOLVED 7d ago; window: 8d ago → 7d ago)
INSERT INTO note (content, created, noteable_type, noteable_id, visibility, created_by_id)
VALUES
   ('VPN werkt weer stabiel na update', CURRENT_TIMESTAMP - INTERVAL '7 days 23 hours',
    'Incident', (SELECT id FROM incident WHERE number='INC0000002'), 'PUBLIC',
    (SELECT id FROM app_user WHERE email='ethanhunt@tickettrack.com')),
   ('Configuratie aangepast; monitoring toegevoegd', CURRENT_TIMESTAMP - INTERVAL '7 days 12 hours',
    'Incident', (SELECT id FROM incident WHERE number='INC0000002'), 'WORK',
    (SELECT id FROM app_user WHERE email='shuri@tickettrack.com'));

-- C3: INC0000003 (ON_HOLD; created 5d ago; on_hold_since 4d ago)
INSERT INTO note (content, created, noteable_type, noteable_id, visibility, created_by_id)
VALUES
   ('Nog steeds geen schrijfrechten op de share', CURRENT_TIMESTAMP - INTERVAL '4 days 23 hours',
    'Incident', (SELECT id FROM incident WHERE number='INC0000003'), 'PUBLIC',
    (SELECT id FROM app_user WHERE email='peterparker@tickettrack.com')),
   ('Wachten op derde partij; change aangevraagd', CURRENT_TIMESTAMP - INTERVAL '3 days 20 hours',
    'Incident', (SELECT id FROM incident WHERE number='INC0000003'), 'WORK',
    (SELECT id FROM app_user WHERE email='tonystark@tickettrack.com'));

-- C4: INC0000004 (NEW; created 2d ago)
INSERT INTO note (content, created, noteable_type, noteable_id, visibility, created_by_id)
VALUES
   ('Verdachte inlogs blijven binnenkomen', CURRENT_TIMESTAMP - INTERVAL '1 days 20 hours',
    'Incident', (SELECT id FROM incident WHERE number='INC0000004'), 'PUBLIC',
    (SELECT id FROM app_user WHERE email='peterparker@tickettrack.com')),
   ('Regels aangescherpt; SIEM-alerts ingesteld', CURRENT_TIMESTAMP - INTERVAL '1 days 10 hours',
    'Incident', (SELECT id FROM incident WHERE number='INC0000004'), 'WORK',
    (SELECT id FROM app_user WHERE email='natasharomanoff@tickettrack.com'));

-- C5: INC0000005 (CANCELED 4d ago; window: 5d ago → 4d ago)
INSERT INTO note (content, created, noteable_type, noteable_id, visibility, created_by_id)
VALUES
   ('Back-up blijft falen door quotum', CURRENT_TIMESTAMP - INTERVAL '4 days 23 hours',
    'Incident', (SELECT id FROM incident WHERE number='INC0000005'), 'PUBLIC',
    (SELECT id FROM app_user WHERE email='ethanhunt@tickettrack.com')),
   ('Out of scope bevestigd; workflow afgesloten', CURRENT_TIMESTAMP - INTERVAL '4 days 12 hours',
    'Incident', (SELECT id FROM incident WHERE number='INC0000005'), 'WORK',
    (SELECT id FROM app_user WHERE email='johnwick@tickettrack.com'));