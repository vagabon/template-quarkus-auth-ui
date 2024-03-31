
insert into  public.profile (active, creation_date, name, roles) values
(TRUE, '023-04-01 16:30:34.783169+00', 'USER', 'USER'),
(TRUE, '023-04-01 16:30:34.783169+00', 'ADMIN', 'USER,ADMIN');

insert into public."user" (active, creation_date, email, password, username, email_activation) values
(TRUE, '2023-04-01 15:49:35.281065+00', 'vagabond.git@gmail.com', '$2a$10$u5NC71uBzi1bHdWoTd8L5OB/sfbUfqicaZXszes7CSni/Hg5SRD1a', 'admin', TRUE);

insert into public.user_profile(user_id, profile_id) values
(1, 2);
